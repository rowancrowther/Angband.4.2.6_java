# Design set piece — the `PR_*` drain

**Chapter 3, stage D. Status: proposal complete, all four decisions settled; verdict pending review.**

How does a raised `PR_*` redraw flag become a `CoreMessage` on the UI channel?

Format follows the agreed set-piece shape: problem / C's way / proposed way / gains / risks. Sections 1–6 are the
proposal, to be challenged before anything is built. Section 7 is filled in afterwards, once `redrawStuff` exists and
has been run — a set piece's verdict is recorded after the build, not after the review (`docs/ROADMAP.md:14`).

---

## 1. Problem

- Each raised `PR_*` flag has to be sent through the `CoreChannel` to the UI.

- That send is two things: the `GameEventType` (`EVENT_HP`, say) and a `GameEventData` object — a flat object carrying
  exactly the data the UI needs to fulfil the redraw request.

- `GameEventData` is already the marker interface for those payloads (`channel/messages/data/GameEventData.java`), the
  port of C's `game_event_data` union, with eleven implementations in place.

- Architecture Principle 1 is that the front end knows nothing of the core, and the core nothing of the front end
  (`docs/Architecture.md:3`).

- Where C reaches for the actual object to find whatever data it requires, the port has to bundle that data into the
  `GameEventData` and pass it across in an agnostic form.

- C's `prt_hp` (`[C] ui-display.c:315`) is the illustration: it reads `player->chp` and
  `player->mhp` straight off the global. The port has to send those values instead.

- Scope limit: of C's three flag words — `notice`, `update`, `redraw` — only `PR_*` produces a
  `GameEventData` payload. All three are ported: `PlayerNotice`, `PlayerUpdateEnum`,
  `PlayerRedraw` (`middle/player/enums/`).

- The three are a pipeline, not three peers. `handle_stuff` (`[C] player-calcs.c:2728`) runs
  `update_stuff` and then `redraw_stuff`, and that order is load-bearing. `notice_stuff` is not called from
  `handle_stuff` at all — callers invoke it separately.

- `PU_*` reaches the UI *indirectly*: its handlers raise `PR_*` flags as their output.
  `calc_mana` sets `PR_MANA` (`[C] player-calcs.c:1552`), `calc_hitpoints` sets `PR_HP` (`:1588`),
  `update_bonuses` sets `PR_STATS`, `PR_SPEED`, `PR_ARMOR` and `PR_INVEN` (`:2361`, `:2393`,
  `:2398`, `:2408`). The core recomputes first, then announces what changed, in the same drain.

- That is what bounds this design set piece: 25 payloads to settle, not 38 — and fewer shapes than that, as section 3
  shows.

- Two exceptions keep the claim honest, and neither carries data. `PU_PANEL` clears itself and calls
  `event_signal(EVENT_PLAYERMOVED)` straight from `update_stuff`
  (`[C] player-calcs.c:2626`) — a non-`PR_*` flag crossing the boundary, argument-free (`EVENT_PLAYERMOVED` is already
  in `channel/enums/GameEventType.java:117`). `PN_MON_MESSAGE`
  calls `show_monster_messages` (`:2554`), which reaches the player down the message path rather than as an event
  payload.

---

## 2. C's way

Facts established 260820, cross-referenced against `/home/rowan/Desktop/Angband-4.2.6`.

- `redraw_stuff` (`[C] player-calcs.c:2678`) draws nothing. Its body is a loop over
  `redraw_events[]` (`:2645`), a table mapping 25 `PR_*` flags one-to-one onto `game_event_type`s, calling
  `event_signal` for each raised flag.

- Three gates run before the loop: `!character_generated` returns early (`:2686`); a hidden map masks to
  `redraw &= PR_SUBWINDOW` (`:2689`); resting or running throttles everything except
  `PR_MESSAGE | PR_MAP` to one drain in 100 (`:2692`).

- `PR_MAP` is handled *after* the loop, by `event_signal_point(EVENT_MAP, -1, -1)` (`:2709`), because the table's
  signature has no room for arguments. `(-1, -1)` is the whole-map sentinel.

- The flag set is cleared with `p->upkeep->redraw &= ~redraw` (`:2712`), and `EVENT_END` is signalled last unless the
  map is hidden (`:2720`).

- `EVENT_MAP` has a second producer that never touches the flags: `square_light_spot`
  (`[C] cave-map.c:258`) signals it per grid, immediately, with real coordinates.

- Why the bitmask exists at all: `redraw_stuff` is a *drain*. Game code raises bits all through a turn and draws
  nothing; the drain is the single point at which the accumulated set is emptied out to the UI — read the bits, signal
  one event per bit, clear them. `handle_stuff` calls it after `update_stuff` (`[C] player-calcs.c:2728`), so the
  recompute pass has already raised its own `PR_*` bits before the drain reads them.

- **Coalescing.** Ten things in one turn can set `PR_HP`; a bit can only be set once, so the UI is told once. This is
  the main reason the flag word exists instead of an `event_signal` at each mutation site.

- **Deferral to a consistent state.** Bits are raised mid-computation, when `player->chp` and
  `player->mhp` may disagree. The drain runs at a quiet point, so `prt_hp` reading the globals sees a settled player.

- **Drain-time suppression.** "Do not draw this now" is decided once, centrally, from the state at drain time rather
  than at every call site — the three gates at `:2686`, `:2689` and `:2692`.

- **Ordering.** `redraw_events[]` fixes emission order independent of the order the bits were raised in, with `PR_MAP`
  after the loop and `EVENT_END` last, so the front end can tell when a batch is closed.

- The drain works on a *snapshot*: `uint32_t redraw = p->upkeep->redraw` at entry (`:2681`), and the clear at `:2712` is
  `&= ~redraw` — it clears only what this drain took. A flag raised *during* the loop, by a handler reacting to an
  event, is not in the snapshot, is not cleared, and drains next time.

- The same mechanism carries the two suppressions. The hidden-map mask removes the non-subwindow bits from the local
  copy, so `~redraw` leaves them standing in `upkeep->redraw` and they fire when the map returns. The resting/running
  throttle returns *before* the clear (`:2694`), so its flags accumulate and go out on the drain that passes the `% 100`
  test. Both defer; neither discards.

---

## 3. Proposed way

- Already built: C's table is *data* because C has no way to hang a field off a `#define`. The port's flags are objects,
  so each one carries its own `GameEventType` as a constructor argument and `redraw_events[]` disappears into the enum:

```java
PR_HP(true, false, false, GameEventType.EVENT_HP),
PR_MANA(true, false, false, GameEventType.EVENT_MANA),
PR_MONLIST(false, false, true, GameEventType.EVENT_MONSTERLIST),
```

- The lookup C does with a linear scan of 25 table rows is `flag.getEventType()`. No `EnumMap`, no parallel table to
  keep in step with the enum, and no way for the two to drift apart — a new constant cannot be declared without naming
  its event.

- The three display-group booleans arrive by the same route (`PlayerRedraw.java:126`), so one constant declaration
  states everything C spreads across `player-calcs.h:51-96` and
  `player-calcs.c:2645`.

- `PR_MAP` is the one flag with no row in C's table, because `event_signal_point` needs arguments the table cannot
  carry. The port declares it with `EVENT_MAP` like any other, and `redrawStuff`
  special-cases it after the loop — see 4.3.

- The proposal proper: each raised flag becomes exactly one `CoreMessage` — its
  `GameEventType` from `getEventType()`, and a `GameEventData` payload carrying the values C's handler would have read
  off the `player` global.

- **The enums do not build their payloads.** A `PlayerRedraw` constant knows *which* event it is, not *how* to fill one
  in; that would put a dependency on `Player` — and through it on most of the core — onto an enum whose whole job is to
  be a flag. Construction belongs to `redrawStuff`. This settles 4.1 in favour of the first option.

- `redrawStuff` does not build them inline either. It runs the drain — snapshot, gates, loop, clear — and delegates the
  filling-in to one small function per payload shape. The loop stays readable at the shape C gives it, and the knowledge
  of what `EVENT_HP` needs sits in one named place.

- **Per shape, not per flag.** The 25 flags do not need 25 records, because most of them do not carry anything a shape
  does not already describe: several want nothing at all and reuse
  `SimpleCoreMessage`, and others share a single value. `EVENT_MAP` needs a point (`EventDataGrid`) that no other redraw
  event carries, and the current/other pair below is the one shape the drain adds; everything else already exists. That
  is the same rule `CoreMessage` already states — one record per payload shape, not one per occasion
  (`channel/messages/CoreMessage.java:21`).

- The gain is where the change lands. Adding a redraw item means a new constant and, at most, a new builder function;
  the drain itself is untouched. A `switch` in `redrawStuff` over 25 cases would have to be edited for every one of
  them.

- Once built, the messages go onto the `CoreChannel` through `CoreSender.send` like every other event, and are delivered
  in the order the core produced them. Nothing is drawn at construction time and nothing jumps the queue: the drain's
  output takes its place in the single ordered stream the front end already consumes, which is what keeps C's sequence —
  the table's events, then `EVENT_MAP`, then `EVENT_END` — meaningful on the far side of the boundary.

- One thing the current `CoreMessage` cannot yet express: none of its three records pairs a
  `GameEventType` with a `GameEventData`. The proposal needs a fourth member of the sealed interface for that pairing,
  which is exactly the break-the-build-everywhere moment its Javadoc describes as the intended way to find the places
  that must learn about it.

- The shapes are not new. `channel/messages/data/` already holds eleven `GameEventData`
  implementations, most built for the birth and level-generation events, and the drain reuses them rather than adding
  twenty-five of its own:

```java
public record EventDataGrid(int row, int col) implements GameEventData { }

public record EventDataMessage(MessageType type, String message) implements GameEventData { }

public record EventDataBoolean(boolean value) implements GameEventData { }
```

- Read them against the flags and the reuse is immediate. `PR_MAP` wants the grid — the whole-map case aside, see 4.3.
  `PR_MESSAGE` wants the message. `PR_DTRAP` is an indicator that is on or off, which is what `EventDataBoolean` is for,
  and its Javadoc already states the rule the drain depends on: *"the payload deliberately says nothing about which
  question was asked, because the `GameEventType` accompanying it already does"*
  (`channel/messages/data/EventDataBoolean.java:31`).

- That is the pattern the whole drain follows. A payload is named for its *shape* and says nothing about its occasion,
  so one record serves every flag of that shape and the event type carries the meaning. `EventDataString` already serves
  three unrelated occasions on the same grounds.

- The declarations are also the argument for records over the older payload classes. Three of the eleven are classes
  with private fields and getters; these three are one line each, immutable by construction, and equal by value — which
  matters for something handed to another thread and compared in a test.

- The drain adds one shape of its own, for the paired numbers that make up most of the sidebar
  (`channel/messages/data/EventDataStat.java:20`):

```java
public record EventDataStat(int current, int other) implements GameEventData { }
```

- It serves `PR_HP` (C's `chp` and `mhp`) and `PR_MANA` (`csp`, `msp`), and `PR_LEV` fits the same shape — `prt_level`
  (`[C] ui-display.c`) prints `lev` and needs `max_lev` only to decide whether the label is drawn drained or full, which
  is a display decision made from the pair.

- `other` rather than `max`: the second number is whatever the event pairs with the first, and the record is not in a
  position to say more than that. Naming it for one reading of it would mislead the first flag that pairs the current
  value with something else.

- Note what the record does *not* do: it sends both numbers and no colour. `prt_hp` picks its colour from the ratio and
  `prt_level` from the comparison, and both of those are the front end's business. The core sends the pair; the display
  decides what it looks like.

- `EventDataSize(int height, int width)` is the same two `int`s and was deliberately not reused — it is named for map
  extents and documents itself as C's `h, w`. That is the boundary of the shape rule: reuse a shape when the *meaning*
  of the fields matches, not merely their types.

- The flags survive the port, and the reasons are the port's own. Section 2's four are C's, and re-deriving them here
  does not simply reproduce them: three carry over, one strengthens, and one has to be struck out.

- **The raise site must not know about the channel.** `calcHitpoints` raising `PR_HP` sets a field on upkeep. Without
  the flag word it would have to *send*, and sending needs a `CoreSender` — so every core class that touches player
  state acquires a channel dependency, threaded through every constructor between it and `Channels`. That is precisely
  the coupling the two-channel design exists to prevent, and it costs far more here than in C, where `event_signal` is a
  free function over a global and costs its caller nothing. This reason is native to the port: it would justify
  inventing the mechanism if C had never had it.

- **Deferral to a consistent state matters more here, and for a different reason.** In C the handler reads `player->chp`
  at draw time, so an ill-timed signal paints a torn value and the next signal corrects it — the global stays the source
  of truth throughout. In the port the payload is *built* at send time and then frozen: a mid-computation send captures
  the wrong number into an immutable record, hands it to another thread, and that thread cannot re-read anything. The
  bad value becomes the truth. So the split the flags create — raise cheaply now, construct at a quiet point later — is
  load-bearing in a way it is not in C.

- **Suppression needs somewhere to stand.** The hidden-map mask and the resting/running throttle are decisions about a
  *set* of pending redraws; with no accumulated set there is no set to decide about and no single place to decide. The
  throttle also earns more: a 200-turn rest without it puts tens of thousands of messages on a queue the UI has to
  drain.

- **Coalescing is worth more per duplicate.** In C a repeated signal costs a `prt()` to a terminal. Here it costs an
  allocation, a queue hand-off, a thread wake and Swing work. `updateBonuses`
  alone raises `PR_STATS`, `PR_SPEED`, `PR_ARMOR` and `PR_INVEN`, and can run more than once a turn.

- **Ordering is struck out.** C's emission order is not semantically meaningful — the six status events all reach
  `update_statusline` (`[C] ui-display.c:1316`), which repaints the whole row and ignores the event that woke it. "The
  flags give a defined order" is therefore not a reason to keep them, and this document does not offer it as one. The
  ordering that *is* load-bearing — table events, then `EVENT_MAP`, then `EVENT_END` — comes from `redrawStuff`'s
  structure, not from the flag word. See 4.2.

- Diffability is a secondary reason and is labelled as one: 26 constants, one per `#define`, means a missed redraw can
  be found by comparing the two sources — worth having in a port that will be checked against the original for years,
  but a reason to keep the *shape*, not evidence that the mechanism is right.

---

## 4. Decisions this proposal must settle

### 4.1 Payload construction site

- `redrawStuff` builds every record, or each `PlayerRedraw` constant knows how to build its own.

- The first keeps `PlayerRedraw` free of any dependency on `Player`.

- **Verdict:** `redrawStuff` builds them, delegating to one small function per payload shape. A `PlayerRedraw` constant
  knows *which* event it is, not how to fill one in. The alternative puts a dependency on `Player` — and through it on
  most of the core — onto an enum whose whole job is to be a flag, and it would have to be passed a `Player` at every
  call to a method the enum exposes. Splitting the construction by shape rather than by flag also keeps the drain itself
  untouched when a redraw item is added.

### 4.2 Emission order

- `PlayerRedraw`'s declaration order follows `player-calcs.h:51–76` (the bit order).

- C's `redraw_events[]` is in a *different* order — `PR_STATE`, `PR_STATUS`, `PR_STUDY`,
  `PR_DTRAP`, `PR_FEELING`, `PR_LIGHT` all appear before `PR_INVEN` in the table, but after it in the header.

- Iterating the snapshot therefore will not reproduce C's emission sequence: `Flag`'s iterator runs in enum declaration
  order, which is the header's order and not the table's (`channel/utils/Flag.java:122`).

- **Verdict:** the difference does not matter. Keep `PlayerRedraw` in header order and iterate the snapshot directly.
  Emission order could only matter if two events wrote the same screen region, and in
  `ui-display.c` they do not: the thirteen sidebar events reach `update_sidebar` (`:844`), which repaints only the rows
  matching the event it was given, at fixed positions; and the six status events — `EVENT_STUDYSTATUS`, `EVENT_STATUS`,
  `EVENT_DETECTIONSTATUS`, `EVENT_STATE`,
  `EVENT_FEELING`, `EVENT_LIGHT` — all reach `update_statusline` (`:1316`), which **ignores the event type entirely**
  and repaints the whole row from `status_handlers[]` (`:1301`). Six signals in any order produce one identical row. The
  remainder write disjoint regions: the message line, the four subwindows, the map.

- The clustering of those six in `redraw_events[]` is therefore a grouping convenience in the table's own history, not a
  sequence the display depends on.

- The ordering that *is* load-bearing is already outside the table and is preserved by
  `redrawStuff`'s structure: the table's events, then `EVENT_MAP` (`[C] player-calcs.c:2709`), then
  `EVENT_END` (`:2720`).

### 4.3 `PR_MAP`'s payload

- A sentinel pair, C-faithful, or a distinct whole-map record.

- The flag path can only ever mean "repaint everything" — a bit stores no coordinates.

- `square_light_spot`'s per-grid sends bypass the flags entirely, so one event type has two payload shapes and two
  routes.

- **Verdict:** `EVENT_MAP` carries an `EventDataGrid`, on both routes, with `(-1, -1)` as C's whole-map sentinel.

- **`Loc` never crosses the boundary.** It is a core type, carrying the movement and randomisation helpers the game
  needs and the display never asks for, and sending it would hand the front end a piece of the core's model. What
  crosses is numbers:
  `EventDataGrid(int row, int col)` and nothing else. `EventDataGrid`'s own Javadoc already states this — it is the
  coordinate pair *without* the arithmetic (`channel/messages/data/EventDataGrid.java:19`).

- Translating `Loc` into `EventDataGrid` is the core's job, done in the builder function, and it crosses the naming
  order on the way: `Loc`'s constructor takes `(x, y)` and the record is
  `(row, col)`. That conversion wants a test that would fail on a transposition, which a round-trip will not.

- Keeping the sentinel rather than adding a whole-map record is what lets the two routes share one shape.
  `square_light_spot` sends real coordinates per grid, the drain sends `(-1, -1)`, and the front end distinguishes them
  by reading the numbers — one event type, one payload type, one
  `case` arm.

### 4.4 The throttle predicate

- The resting/running skip exempts `PR_MESSAGE | PR_MAP`.

- That pairing is not one of the three display groups, so `isBasic` / `isExtra` / `isSubwindow`
  cannot express it.

- Options: a fourth boolean on the enum, or a two-constant test inside `redrawStuff`.

- **Verdict:** the two constants go straight into the gate, as arguments to `Flag.test`. C's
  `redraw & (PR_MESSAGE | PR_MAP)` asks whether the pending set intersects a fixed pair, and
  `test` is that question already — "true if any one of the flags in the list is set"
  (`channel/utils/Flag.java:418`):

```java
if ((upkeep.getRestingCounter() % 100 != 0 || upkeep.getRunningCounter() % 100 != 0)
        && !redraw.test(PlayerRedraw.PR_MESSAGE, PlayerRedraw.PR_MAP)) {
    return;
}
```

- No constant set is needed at all: `test` takes varargs, so the pair is named at the one place that asks about it and
  nowhere else. C's mask reads as C's mask, negated because C tests for presence and this gate skips on absence.

- `getRunningCounter` does not exist yet — `runningCounter` is declared but unexposed
  (`middle/player/PlayerUpkeep.java:162`), where `getRestingCounter` is (`:530`). The gate needs it.

- `% 100 != 0` stays on `int`, deliberately. The special rests store negative constants —
  `REST_COMPLETE` (-2), `REST_ALL_POINTS` (-1), `REST_SOME_POINTS` (-3)
  (`[C] player-util.h:53-55`) — which never decrement, because `player_resting_step_turn` counts down only while
  `resting > 0` (`[C] player-util.c:1475`). C's `%` leaves those negative and therefore truthy, so a rest-until-healed
  skips every drain that is not a message or a map. That is the original's behaviour, and Java's `%` reproduces it.
  `Math.floorMod` would not.

- The gate returns before the clear, as C's does, so the skipped flags stay raised in the live set and go out on the
  drain that passes.

- **Why not the fourth boolean.** It costs 26 declarations to express a two-element set, and 24 of them say no. It would
  also sit alongside `isBasic` / `isExtra` / `isSubwindow`, which name display *regions*; this pair is not a region but
  "the player would notice if this were late" — the same field shape carrying an unrelated kind of meaning. Naming the
  two constants in the gate keeps the exemption where its reason is, and costs nothing on the other twenty-four.

- **The hidden-map gate is expressed differently, on purpose.** `redraw &= PR_SUBWINDOW`
  (`[C] player-calcs.c:2689`) masks by a real display group, so it goes through `Flag.mask`
  (`channel/utils/Flag.java:596`), which clears everything not listed — C's `&=` exactly — over a list derived from the
  boolean that already exists:

```java
private static final List<PlayerRedraw> SUBWINDOW = Arrays.stream(PlayerRedraw.values())
        .filter(PlayerRedraw::isSubwindow)
        .toList();

redraw.mask(SUBWINDOW);
```

- The asymmetry is the point: one gate masks by a group the enum genuinely knows about, the other by an ad-hoc pair it
  should not have to.

---

## 5. Gains

- **The mapping cannot be incomplete.** C's `redraw_events[]` is hand-maintained and can silently omit a flag; `PR_MAP`
  's absence is deliberate, and an accidental omission would look exactly the same — a redraw that never happens, with
  nothing to point at. In the port the event type is a constructor argument, so a flag that names no event does not
  compile. The failure mode C is vulnerable to is removed rather than guarded against.

- **The drain becomes testable without a display.** `redrawStuff` is a function of the player and the raised flags,
  producing a list of messages. A test raises flags, runs the drain against a recording `Sender<CoreMessage>`, and
  asserts on what came out — the gates, the clear, the snapshot behaviour and each payload's contents, none of it
  needing a window. C's `redraw_stuff`
  cannot be exercised at all without a `term`, which is why its behaviour has to be read out of the source rather than
  demonstrated.

- **The message volume is bounded, and by the same two mechanisms C uses.** A 9999-turn rest produces on the order of a
  hundred drains rather than ten thousand, and within each drain a flag raised repeatedly produces one message. That
  matters more here than in C: every message is an allocation, a queue hand-off and Swing work on the far side, where C
  paid only a `prt()`.

- **The boundary is checkable rather than remembered.** Every payload the drain sends is a record of primitives,
  `EventDataGrid`s and channel-side enums, so `channel/messages/data/` needs no import from `middle`. That is a property
  a test can assert over the whole package, which is a stronger guarantee than each new payload being reviewed for it.

- **Adding a redraw item is a small, local change.** A new constant with its event type, and at most a new builder
  function if its shape is new. The drain itself, its gates and its clear are untouched — where a `switch` over 25 cases
  inside `redrawStuff` would have to be edited every time.

- **The front end's dispatch stays small.** Payloads are named for shape and reused, so the switch grows an arm per
  *shape*, not per event — and the sealed hierarchy means a new shape breaks the build at every switch that must learn
  about it (see section 6 for what that guarantee does and does not cover).

- **The core never waits for the display.** `CoreSender.send` is an `offer` onto an unbounded queue, so a slow repaint
  cannot slow the game down. C's handlers run synchronously inside
  `event_signal`, which is why `display_missile` has to call `redraw_stuff` twice from inside itself
  (`[C] ui-display.c:1711`, `:1717`) to keep the screen current mid-animation. The port has no equivalent need.

- **The port stays diffable against the original.** `redrawStuff` keeps `redraw_stuff`'s shape — snapshot, three gates,
  loop, `PR_MAP`, clear, `EVENT_END` — so a divergence in behaviour can be found by reading the two side by side. For a
  port that will be checked against C for years, that is worth more than a tidier structure would be.

---

## 6. Risks

### C is accidentally self-healing; the port is not

- This is the risk the whole design turns on. C's handlers read `player` at draw time, so a field that changed without
  its flag being raised is still corrected the moment *anything* else causes that region to repaint. `update_statusline`
  redrawing the whole row for an unrelated event (`[C] ui-display.c:1316`) fixes six indicators at once, whether or not
  their flags were set.

- The port has no such recovery. A payload is built at drain time and frozen; the front end cannot re-read anything. A
  mutation that fails to raise its flag is not merely late, it is invisible until something raises that exact flag —
  possibly for the rest of the session.

- So missing `PR_*` raises, which in C are a cosmetic flicker at worst, are a persistent wrong number here. Every port
  of a function that mutates player state has to carry its flag raises across, and the C is the only place that records
  which they are.

- Mitigation is coverage, not cleverness: the update handlers that raise `PR_*` as their output (`calcMana`,
  `calcHitpoints`, `updateBonuses`) are the dense cases and are worth a test each asserting the exact flag set they
  leave behind.

### The snapshot and the clear are already provided, and must not be worked around

- `PlayerUpkeep` has both halves of C's take-and-clear built in, and their Javadoc says so.
  `getRedrawFlags()` returns a copy the caller owns, not the live set (`middle/player/PlayerUpkeep.java:314`), and
  `clearRedrawFlags(FlagView)` takes the snapshot back and calls `diff` — C's `&= ~redraw` (`:344`).

- So `redrawStuff` must clear by handing its *narrowed* snapshot to `clearRedrawFlags`, not by wiping the live set.
  `Flag.wipe()` (`channel/utils/Flag.java:255`) is the wrong door and is one keystroke away from looking right: it
  discards anything raised while the drain was running, and anything the hidden-map mask deliberately left standing.

- The mask matters to the clear for that reason. `mask` mutates the snapshot, and the snapshot is what gets diffed out,
  so narrowing before clearing is what leaves the non-subwindow flags pending — the behaviour C gets from `~redraw` on
  the masked local.

- This is worth a test in its own right: raise a flag during emission, drain, and assert it is still raised afterwards;
  and drain with the map hidden, then assert the basic flags survived.

### The `PU_*` → `PR_*` order is load-bearing and silent when wrong

- `handle_stuff` runs `update_stuff` and *then* `redraw_stuff` (`[C] player-calcs.c:2728`) because the recompute is what
  raises several of the flags the drain is about to read. Reverse them and the drain sends the values from before the
  recompute.

- Nothing fails. The display is simply one turn stale, in exactly the fields that just changed, which is the hardest
  kind of bug to notice and the easiest to blame on something else.

### The throttle makes the front end look hung

- A special rest freezes the display for its whole duration (4.4), which in C is a static terminal and in a windowed
  port is an application that appears to have stopped. The exemptions — messages and the map — are the only signs of
  life.

- C gets away with it because the rest ends quickly in real time. Whether that holds here depends on the port's turn
  rate, and if it does not, the fix is a port-side decision about the throttle rather than a bug in this design.

### `EVENT_MAP`'s other producer is the volume risk, not the drain

- `square_light_spot` (`[C] cave-map.c:258`) signals per grid, immediately, bypassing the flags. Lighting a large room
  becomes one message per square on the same queue the drain uses, and no coalescing applies to any of it.

- The drain's own output is bounded (section 5); this route is not. It is out of scope here, but it shares the channel,
  and a queue that backs up does so for both.

### The event-to-payload pairing is unchecked

- Sealing `CoreMessage` checks the set of payload *shapes*, not which shape goes with which event.
  `GameEventData` is a marker interface declaring nothing (`channel/messages/data/GameEventData.java:31`), so a message
  pairing the two accepts any combination:

```java
record DataCoreMessage(GameEventType gameEventType, GameEventData data) implements CoreMessage { }

new DataCoreMessage(GameEventType.EVENT_HP, new EventDataBolt(...));   // compiles
```

- With 25 flags each building their own payload, a builder wired to the wrong record is a plausible slip, and nothing
  catches it. It surfaces in the front end as a `ClassCastException` on the downcast — a thread away from the code that
  got it wrong, and only for the events a session happens to exercise.

- Sealing makes this worse than it looks, by suggesting the protocol is compiler-checked throughout. It is checked at
  the shape boundary and nowhere past it.

- This is C's position too: `game_event_data` is an untagged union, and the choice between
  `event_signal_point` and `event_signal_message` is the only thing keeping the pairing straight. The port is not worse.
  It is simply no better, and the drain is where the number of pairings jumps from a handful to 25.

- **Mitigation — a class token on the event type.** Generics cannot express it: a Java enum takes no type parameter, so
  `GameEventType<T extends GameEventData>` is unavailable. Each constant can carry the payload class instead, exactly as
  `PlayerRedraw` already carries its `GameEventType`:

```java
EVENT_HP(EventDataStat.class),
EVENT_MAP(EventDataGrid.class),
```

- A compact constructor on the message then rejects a mismatched pair at the *send* site, in the core, on the line that
  built it — turning a UI-thread cast failure into an immediate one next to
  `redrawStuff`'s builder. It is a runtime check, not a compile-time one; that is the ceiling Java allows here.

- It is also testable in bulk: one test walking all 25 flags, building each payload and sending it, catches every
  mismatch in a single run rather than waiting for a session to raise the flag.

### Rejected alternatives

- **One snapshot message per turn.** Send the whole player state and let the front end diff it against what it drew
  last: no flags to miss, but the UI has to hold a model of the player to diff against, which is the coupling Principle
  1 forbids — and it sends everything every turn to avoid sending the wrong thing occasionally.

- **Grouping by panel region.** One message per stale region — sidebar, status line, subwindow — rather than per flag:
  fewer messages, but the region layout is the front end's business, so choosing the grouping in the core hands it a
  screen layout. C's own regions are a `ui-display.c`
  detail, not a game concept.

- **Notify-only, then ask over `coreQueue`.** Send a bare "HP changed" and let the UI request the values back: keeps
  payloads empty, but makes every redraw a round trip on a channel the core must then service mid-turn, and
  re-introduces the torn-read problem the drain exists to avoid.

- **De-duplicating on the channel instead of accumulating flags.** Let every mutation site send, and coalesce by
  scanning the queue: buys coalescing alone, gives nothing on deferral, throttling or raise-site decoupling, and
  de-duplicating a queue means reordering it.

- **A payload class per event rather than per shape.** Twenty-five records, each named for its occasion: reads well at
  the send site and grows without bound, which `CoreMessage`'s Javadoc already rejects for the protocol as a whole
  (`channel/messages/CoreMessage.java:22`).

---

## 7. Verdict

*Fill in once the drain is built and has been played, not before. One honest paragraph per heading; delete a heading
rather than pad it. Precedents: `Chapter_2_Roadmap.md:16` (one clause, naming the class of bug the deviation killed) and
`Architecture_migration.md:779` (a paragraph each, including what the work felt like).*

### Did the deviations pay?

*The proposal is mostly faithful, so name the actual departures and judge them one at a time: the payload travelling on
the message rather than being read off a global; `redraw_events[]` folded into the enum; the throttle exemption written
into the gate rather than carried as a group; header order rather than table order. Which earned their place, which
turned out not to matter either way, and which cost more than they returned.*

### What it killed

*Chapter 2's verdict is quotable because it names a class of bug, not a feature. The candidates here are the mapping
that cannot be incomplete (section 5) and the transposition the
`Loc` → `EventDataGrid` test catches (4.3) — but only claim them if the build bore them out.*

### What it cost

*How many builder functions it actually took. Whether the per-shape rule held at 25 flags or drifted towards a record
each. What had to change in `PlayerUpkeep`, `Flag` and `CoreMessage` to support it, and whether any of that was work
this document did not foresee.*

### Which section 4 verdicts survived contact

*The most useful thing this section can record. 4.4 already predicts one change — `getRunningCounter`
does not exist. If 4.1's per-shape split, 4.2's header order or 4.3's sentinel got reversed in the writing, that
reversal is the verdict, and the reason for it belongs here rather than in an edit to section 4.*

### Were the section 6 risks real?

*Especially the first one: did a missed `PR_*` raise actually show up as a number that stayed wrong in play, and how was
it found? Also whether the `wipe()` trap or the event-to-payload mismatch caught anyone, and whether the class-token
mitigation was worth building.*

### The honest paragraph

*What the work was like to do — drained or energised, confusing or clear, and what would be worth doing differently on
the next set piece.*
