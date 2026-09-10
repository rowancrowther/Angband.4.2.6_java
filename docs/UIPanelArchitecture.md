# UI panel architecture — the drawing layer

Design discussion held 260909. Worked from the **problem space** rather than from either the existing Java classes or
the C original: the question asked was "how should this be architected", not "how does
`ui-term.c` do it". C is referenced below only where it is instructive, and in one place the design deliberately departs
from it.

**Status: agreed, not yet built.** Decisions marked *settled* were taken during the discussion. Open items are listed at
the foot.

## The problem as posed

A `CoreChannel` delivers events that need drawing on screen. The screen is a `JPanel` — acknowledged as a solution
rather than a problem, but a fixed constraint for now. The screen must be amendable by many different events, each of
which needs helper functions to draw *portions* of it and then display the result appropriately. In future, events
originating **on** the screen must travel back the other way.

## Three things worth separating

The design rests on splitting three concerns that are easy to conflate:

1. **The data** — a grid of coloured cells. Plain, dumb, no Swing, no threading.
2. **The operations** — draw a string, erase a run, clear a box. The "helper functions".
3. **The presentation** — a `JPanel` that paints.

The `JPanel` is a poor home for the operations. They are the part that gets called thousands of times, wants testing
without a display, and wants composing off the EDT. The rule that keeps this honest:

> **The panel only ever reads. Nothing that mutates screen content lives on a Swing type.**

## The types

The data, with `[row][col]` addressing:

```java
public final class CellGrid {
    private final AngbandDisplayCharacter[][] cells;     // [row][col]

    public int rows();

    public int cols();

    public AngbandDisplayCharacter get(int row, int col);

    public void set(int row, int col, AngbandDisplayCharacter cell);   // throws out of range

    public CellGrid copy();
}
```

`set` is the only primitive. Everything else is built on it.

The operations hang off a **region** — a rectangle into a grid that translates and clips coordinates:

```java
public final class Region {
    private final CellGrid grid;
    private final int top, left, rows, cols;

    public void put(int row, int col, char glyph, ColourEnum colour);

    public void put(int row, int col, String s, ColourEnum colour);

    public void erase(int row, int col, int n);

    public void clear();
}
```

The point of the region is that "draw a portion of the screen" becomes "hand an event a region and let it draw at 0,0".
The event never learns where on screen it sits, and cannot scribble outside its box.

The owner object, which holds the live grid and is the only route out:

```java
public final class Screen {
    private final CellGrid live;
    private final List<Hotspot> hotspots = new ArrayList<>();

    public Region root();

    public Frame frame();          // new Frame(live.copy(), List.copyOf(hotspots))
}
```

What crosses to the EDT, carrying both what to paint and what the painted thing means:

```java
public record Hotspot(int top, int left, int rows, int cols, Object event) {
}

public record Frame(CellGrid grid, List<Hotspot> hotspots) {
}
```

And the panel, whose entire mutating surface is one method taking a whole frame:

```java
final class ScreenPanel extends JPanel {
    private Frame frame;                                  // EDT-confined

    void show(Frame frame) {
        this.frame = frame;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g);
}
```

## Who owns the grid — the fork

This was the one decision everything else hung off.

**Option A — the EDT owns it.** The other half sends one message per draw operation; a consumer on the EDT applies each
to the grid. Simpler to start, and needs no notion of a flush. But the EDT can paint a half-drawn screen, and the
composing side can never read the screen back.

**Option B — the composing thread owns it, the EDT gets immutable snapshots.** Helpers are called freely on that thread,
and a copy is published when the screen is ready to show.

**Settled: Option B.** It buys atomicity for free — a snapshot is never mid-update — keeps the helpers as plain
single-threaded code testable with no Swing at all, and makes the channel carry frames rather than per-cell chatter. The
cost is one 24×80 array copy per flush (1,920 references, microseconds) plus the discipline of an explicit "display it
now" point in the protocol.

## Which thread is "the composing thread"

An ambiguity worth recording, because it was got wrong once in the discussion. "Core" was used loosely to mean
"whichever side isn't the EDT", and in this repo that phrase points at a third party.

There are **three**, per `Channels.java:39-44`:

| Party         | Holds         | Rights                                    |
|---------------|---------------|-------------------------------------------|
| Game thread   | `CoreChannel` | sends `CoreMessage`, receives `UIMessage` |
| **UI thread** | `UIChannel`   | sends and receives                        |
| EDT           | `EDTChannel`  | **send-only**, deliberately               |

The grid belongs to the **UI thread** — the `UIChannel` holder in `frontend`. Not
`middle.game.gameengine.Core`, and not a new class named `Core` in the front end.

**Why the middle end must not own it.** `Core.java:36-42` records the boundary established in stage 4:
"the front end holds nothing of the middle end at all", and what `Core` knows of the other half is "a queue it reads and
a queue it writes". Giving `Core` a `CellGrid` would drag rows, columns and colours into the middle end and undo exactly
that. `CoreMessage` therefore stays **semantic** — "the player moved", "here is the birth prompt" — and never carries
cells.

The resulting flow:

```
Core ──CoreMessage(semantic)──▶ UI thread
                                  │  owns CellGrid, calls Region helpers
                                  │  builds an immutable Frame
                                  ▼
                            invokeLater(frame)  ──▶  EDT paints it
```

The last hop is `invokeLater`, not a channel: the EDT deliberately has no receiver (`Channels.java:41-44`), because
handing it one would let a listener block the event dispatch thread on
`receive()`.

## The invariant Option B rests on

> **Once a frame is handed to the EDT, the UI thread must never touch that grid again.**

Otherwise the EDT paints a torn screen — a bug that only shows under load. The cheap guarantee is to publish a *copy*
and let the UI thread keep mutating its own live grid. `Screen.frame()` is the only way out and copies both halves, so
the invariant is enforced in one place rather than by a rule to remember at each call site.

Note `ScreenPanel.frame` needs no `volatile`: it is written by `show` (arriving via `invokeLater`) and read by
`paintComponent`, both on the EDT.

## The flush point

Something must say "the screen is consistent now, show it". **One flush at the end of handling each message from the
core.** That yields atomicity per event automatically — a message either lands on screen whole, or not yet, never half.

```java
SwingUtilities.invokeLater(() ->panel.

show(screen.frame()));   // WRONG
```

`frame()` must be called on the UI thread, not inside the lambda — otherwise the live object has been handed to the EDT
and the whole invariant is lost. Take the frame first, then post it.

**Departure from C, deliberate.** C's `Term_fresh` diffs `scr` against `old` row by row and pushes only changed runs
through the hooks. This design does not diff. Swing repaints the whole panel anyway, 1,920 cells is nothing, and the
diffing machinery is where much of `ui-term.c`'s complexity lives. Revisit only if profiling demands it.

## The return path

A bare character grid cannot generate events: a click at (12, 40) has no meaning without knowing what was drawn there.
Hence `Hotspot`, published *with* the frame — the frame that was painted is the frame that gets hit-tested, so there is
no window in which the user clicks one screen and the core interprets it against another.

The road is already built. `EDTChannel` is the send-only path onto the UI thread's inbox, and the pattern is
established: `Channels.java:69-71` records that a window close request "arrives UI-side first and is translated into a
`LifecycleUIMessage` there, rather than being sent to the core raw". A hotspot click takes the same journey — the EDT
posts the raw hit, the UI thread turns it into meaning, and only then does a `UIMessage` reach the core.

**Send meaning, never coordinates.**

## Decisions taken

**Settled: `Region` clips silently, and there is no nesting.** A region is a flat rectangle into the grid; no `sub()`
method on day one. The game prints overrunning strings constantly, so clipping is the common case rather than an error,
and a flat region list is easy to nest later if it earns its keep.

The clip contract, in full — this is the specification the unit tests are written against:

- A bad `row` — negative, or at/past `rows` — draws **nothing at all**. There is no wrapping to the next line.
- A string starting at negative `col` drops its first `-col` characters and draws the remainder from column 0. It is
  **not** discarded whole.
- A string overrunning the right edge draws its first `cols - col` characters.
- Both rules can apply to the same call.
- `erase` clamps `col` up to 0 and `n` down to `cols - col`; if `n` lands at zero or below it does nothing.

## Strict underneath

Clipping is a promise the **public** API makes. `CellGrid.set` does not repeat it — it throws.

If `set` also clipped, a coordinate bug in `Region` would silently draw nothing instead of failing, and the hunt would
be for a blank screen with no exception to point at. By the time `set` is reached the arithmetic is already done, so an
out-of-range call there is a defect, not a normal case.

## Where the classes live

**`uk.co.jackoftradesltd.frontend.screen.grid`** — `CellGrid`, `Region`, `Frame`, `Screen`, `Hotspot`.

The rule that earns it a package of its own: **nothing in it imports `javax.swing`**. That is greppable in one line, and
it is the property that keeps the drawing layer testable with no display. Putting these into `frontend.screen` would
blur it, since `Window` there is a `JFrame` and the package already mixes Swing in.

`ScreenPanel` is a `JPanel` and belongs with the Swing types. The dependency runs one way only:
`ScreenPanel` imports `Frame`, and nothing in `grid` imports the panel.

`frontend/inputfromuser/UILoop.java:76` is the UI thread's body and already holds the `UIChannel`
(`UILoop.java:92`), which makes it the natural owner of the `Screen` instance and the place the flush goes.

## `AngbandDisplayCharacter` is already immutable

Checked 260909. No new `Cell` record is needed — `CellGrid` is built on
`channel/strings/AngbandDisplayCharacter` directly.

- Both fields are `final`: `character` at `AngbandDisplayCharacter.java:35`, `attributeColour` at `:39`.
- No setters. The constructor Javadoc at `:42` says as much: "the only way to set field values".
- Both field types are immutable in themselves — `char` is a primitive, `ColourEnum` is an enum.
- `equals` and `hashCode` (`:109`, `:126`) are value-based over exactly those two fields.

**Safe publication holds.** Final fields carry the JMM freeze guarantee, so a correctly constructed instance is visible
to another thread without synchronisation. Handing one to the EDT inside a frame needs no locking.

**The one gap:** the class is not `final` (`:30`), so a subclass could add a mutable field and slip through the frame's
guarantee unnoticed. One word closes it, and nothing suggests subclassing was intended — `equals` at `:110` rejects
subclasses outright with `getClass() != o.getClass()`.

**Leave it as a class, not a record.** A record would give implicit `final` and generated
`equals`/`hashCode`, but it renames the accessors and churns every call site for no behavioural gain.

**Consequence for `copy()`:** because elements cannot be mutated behind your back, `copy()` only duplicates the array
structure. A per-row `Arrays.copyOf` is correct; there is no deep copy to write.

## Build order

1. `CellGrid` — plain data, no Swing import, fully testable headless.
2. `Region` — the drawing helpers and the clip contract above. Still no Swing.
3. `Frame` and the panel repaint path.
4. The flush, wired into the UI thread's message loop.
5. `Hotspot` and hit-testing, last — when there is something worth clicking.

Steps 1 and 2 are testable with no display at all, which is the payoff for keeping the package free of Swing.

## Open items

- `AngbandDisplayCharacter` is not `final`; the annotation `@Contract(mutates = "this")` on its constructor at `:48`
  claims the opposite of what the class does, and `logger` at `:31` appears unused.
- Where this design leaves the existing `Term` / `TermWin` / `TermData` / `Window` / `JPanelArea` group is undecided.
  The grid layer displaces some of what they do, and the overlap has not been mapped.
- `Rect` already exists at `frontend/screen/Rect.java` with `long` left/top/right/bottom — a candidate for `Hotspot`'s
  bounds rather than four loose `int` fields, when hotspots are built.
- Nesting and frame diffing are both deferred, not rejected.

## `CellGrid.rows()` / `cols()` and `copy()` (260909)

**`rows()`/`cols()` are plain accessors over the array's own dimensions** — no stored field, no computation:

```java
public int rows() {
    return cells.length;
}

public int cols() {
    return cells[0].length;
}
```

Matches the convention already in `JPanelArea.getDisplayWidth()`/`getDisplayHeight()`, which read off
`display.length`/`display[0].length` rather than caching a size. No zero-guard on `cols()` — the grid is always
constructed at a fixed size (24×80) and is never empty, so a guard would hide a real bug (a
`0`-row grid) behind a `0` instead of an `ArrayIndexOutOfBoundsException`.

**`copy()` is not a deep copy in the usual sense.** Because `AngbandDisplayCharacter` is immutable, the individual cells
never need cloning — only the **array structure** does, so a mutation on the copy's rows can never reach the original's.
Three levels, the middle one being the trap:

- **Shallow** — `cells.clone()`. Copies only the outer array of row-references; the row arrays stay shared, so `set()`
  on the copy would still mutate the original's row content. Wrong — breaks the frame-publishing invariant.
- **Structural (correct)** — new outer array, new row array per row, same `AngbandDisplayCharacter`
  references inside:

```java
public CellGrid copy() {
    AngbandDisplayCharacter[][] copy = new AngbandDisplayCharacter[cells.length][];
    for (int i = 0; i < cells.length; i++) {
        copy[i] = Arrays.copyOf(cells[i], cells[i].length);
    }
    return new CellGrid(copy);
}
```

- **Deep** — the above, plus cloning every `AngbandDisplayCharacter`. Unnecessary: sharing the same immutable instance
  across both grids is safe by construction.

## Is `ScreenPanel` actually `SwingUI.JPanelArea`? (260909)

**Yes — same panel, not a second one.** "`ScreenPanel`" in the design above was a placeholder name for
"the `JPanel` side of this", not a mandate for a new class. `JPanelArea` (`SwingUI.java:503`) is already the one
`JPanel` painting the screen; a competing panel would leave two of them fighting over the same window.

For `JPanelArea` to become that panel, its role has to shrink to match the three-way split this design rests on. It
currently does all three at once:

- Holds the grid (`display` field, `SwingUI.java:522`) — becomes `CellGrid`'s job.
- Mutates it (`setChars`, `put`, `erase`) — becomes `Region`'s job, called on the UI thread, never on the panel.
- Paints it (`paintComponent`) — the only part that stays.

`setChars`/`put`/`erase` come off `JPanelArea`, `display` is replaced by a held `Frame`, and
`paintComponent` reads `frame.grid()` instead of the local array.

**This is a migration, not a rename.** `Window.erase`/`Window.clear`/`Window.display`
(`Window.java:145,171,117`) call `area.erase`/`area.setChars` directly today, and
`SplashScreen.java:239,318,391` reach `JPanelArea` the same way. Cutting those methods breaks every one of those call
sites — they would need to go through `Screen`/`Region` and a flushed `Frame` instead. Tracked as its own step rather
than assumed to fall out of building the `grid` package.

## Migration hazards for the `display`/`erase`/`put`/`setChars` cutover (260909)

Discussion held before writing any of the migration itself. Full call-site inventory, confirmed by grep against
`src/main` — nothing else calls these four methods:

- `Window.java:119-120` (`display`) — `area.setChars(display); area.repaint();`
- `Window.java:153-154` (`clear`) — builds a blank 24×80 array inline, then `setChars`/`repaint`.
- `Window.java:172-173` (`erase`) — `area.erase(x, y, n); area.repaint();`
- `SplashScreen.java:239-240` — `panel.setChars(display); panel.repaint();`
- `SplashScreen.java:319-320` — `activeWindow.getArea().setChars(display); ...repaint();`
- `SplashScreen.java:391,395` — `panel.put(birthLine, 0, message, COLOUR_WHITE); ...repaint();`

**1. `erase`'s argument order transposes.** `JPanelArea.erase(x, y, n)` is (col, row, n)
(`SwingUI.java:590`). `Region.erase(row, col, n)` is (row, col, n) (`Region.java:186`).
`Window.erase` forwards `x, y` straight through today (`Window.java:171-172`). A mechanical swap of
`area.erase(x, y, n)` → `region.erase(x, y, n)` silently transposes row and column. `put`'s order already agrees between
the two, so this trap is specific to `erase`.

**2. `setChars` has no `Region`/`Screen` equivalent.** `Region`/`Screen` are built around cell-by-cell
`put`/`clear` on a grid `Screen` already owns — there's no "install this whole externally-built grid"
primitive. All three `setChars` call sites hand in a grid they built themselves (`Window.clear`'s inline blank array,
`SplashScreen`'s parsed news/birth grids). Either `Screen` gets a way to adopt an external `CellGrid` wholesale (`live`
is currently `final`, `Screen.java:23`), or `SplashScreen` gets rewritten to draw through `Region.put` cell-by-cell
instead of building its own array. Decide which before moving code.

**3. The EDT-hop and the flush-ordering invariant are currently fused, and shouldn't be.** All three
`Window` methods build the `invokeLater` lambda *and* do the mutation inside it (`Window.java:117-121,
145-154, 171-174`). §"The flush point" above requires `Screen.frame()` to be called on the UI thread *before* the
lambda, never inside it. A same-shape port — swap `area.foo()` for `region.foo()` but leave it inside
`invokeLater(() -> {...})` — reproduces exactly that mistake, because the frame gets built on the EDT instead of the UI
thread. Needs restructuring, not a rename: take the frame first, then post it.

**4. `SplashScreen` calling from the game thread is a documented bug today, and Option B turns it into a correctness
violation.** `Window.java:41-42` already records: "`clear()` is currently reached from the game thread through
`SplashScreen`, which is a bug in the caller rather than here." Today that's a Swing-thread-safety bug. Once `Screen`
owns a live `CellGrid` under the single-owner assumption §"The invariant Option B rests on" states, the same call from
the wrong thread stops being merely risky and becomes the exact scenario the invariant exists to prevent. In the blast
radius of this refactor, not a follow-on.

**5. `CellGrid` doesn't pre-fill blank cells; `JPanelArea` does.** `JPanelArea`'s constructor fills every cell with a
space before anything paints (`SwingUI.java:558-565`). `CellGrid`'s constructor just allocates the array — every cell is
`null` until written (`CellGrid.java:27-29`). `paintComponent`'s existing null-tolerance
(`SwingUI.java:632-634, 663-664`) falls back to `COLOUR_DARK`, not the white-space `JPanelArea` starts with — a visible
difference on first paint unless `Screen`/`CellGrid`
gets the same "blank on construction" treatment.

**6. `JPanelArea.put`'s Javadoc and its code already disagree — don't port the Javadoc's claim forward.** The Javadoc
says an out-of-range `put` "throws a raw `ArrayIndexOutOfBoundsException`"
(`SwingUI.java:689-693`), but the code is a silent bounds-check-and-return (`SwingUI.java:700-701`). The real behaviour
is closer to a subset of `Region`'s clip contract — drop the whole call rather than clip a string — not a throw.

**7. `getArea()` is a second public escape hatch that needs its own decision.** `Window.getArea()`
(`Window.java:129`) and `SplashScreen`'s two `activeWindow.getArea()` calls (`SplashScreen.java:234,
390`) reach past `Window`'s own methods straight to the panel. If `Screen`/`Region` become the only mutation path,
`getArea()` either disappears or is replaced with an equivalent `getScreen()`/`root()`
accessor — otherwise `SplashScreen` keeps a live route around the new architecture.

**8. `Screen` ownership belongs on the UI thread, not on `Window`.** Per §"Where the classes live",
`UILoop.java:76` (holding `UIChannel`) is the intended owner of the `Screen` instance and the flush point, not `Window`.
Not just "swap the method bodies inside `Window`" — `Window`'s role likely shrinks to holding the panel and receiving a
`Frame` to show, while the grid-building logic `Window.clear()`
does inline today (`Window.java:146-151`) moves up to wherever `Screen` lives.

**Out of scope for this cutover, flagged so it isn't assumed in:** `Term.cPutStr`/`Term.putStr` are still empty stubs
(`Term.java:459-465`), and `Term`/`TermWin` carry their own diff-shaped buffers (`old`/`scr`, per-row `x1`/`x2` dirty
bounds) that this design deliberately does not replicate (see
"Departure from C, deliberate" above). None of `Term`'s hooks currently call `put`/`erase`/`setChars`, so wiring `Term`
through `Region` is a separate, harder question this pass doesn't answer.
