# Migrating to the two-channel architecture

The route from the current wiring to the design in `Architecture.md`, in stages that each end with a game that still
runs and something you can see working. Written to be walked one checkbox at a time, port-loop style: you write, Claude
verifies and tests, primers on demand.

---

## 1. Where we are, where we're going

**Now** (per `Old_java_map.md`): `main()` → EDT → `SwingUI` → creates `Core` → starts the game thread. The core
reaches the UI through three static holders (`StatusDisplayHolder`,
`CommandGetterHolder`, `GameInputHolder`); the UI reaches the core through the `Core` it holds (`start()` /
`requestStop()`). Shutdown is `SwingUI.closeDown()` calling `System.exit(0)`
with the game thread shot wherever it happens to be.

```
   EDT ──creates & starts──▶ game thread
    ▲                            │
    └── StatusDisplayHolder ─────┘   (static slot, core calls UI methods directly)
```

**Target** (per `Architecture.md`): `main()` creates two thread-safe queues, starts a core thread and a UI thread, and
waits for both to exit. Core and UI know nothing of each other; everything crosses in messages.

```
   main() ── creates channels, starts both, joins both
      │
      ├─▶ core thread   ────── uiQueue ────────▶ UI thread ──invokeLater──▶ EDT
      │                 ◀───── coreQueue ───────     ▲                       │
      │                                              └── raw Swing events ──┘
```

Each queue is named for **whose inbox it is**: `uiQueue` is the UI thread's single inbox — the core's messages and the
EDT's forwarded events both arrive on it — and `coreQueue` is the core's, which only the UI thread writes to (see review
note 1).

**Vocabulary decision, made now so nothing has to be renamed twice.** The transport is called a **channel**. "Channel"
is Hoare's own CSP term, and it keeps clear of `CommandQueue`, which already exists as the port of C's
`cmdq` (`cmd-core.c`) and is a *core-internal* structure that stays exactly where it is. If the two ever sit in one
sentence: the *channel* carries messages between threads; the *queue* holds game commands awaiting dispatch inside the
core.

**A channel is a half's pair of ends, not a direction.** *(Corrected 2026-08-13 against the code, superseding the first
draft — see the note below.)* `CoreChannel` is what the core is handed: `(CoreReceiver, CoreSender)`, its inbox and its
one route out. `UIChannel` is the same for the UI thread, and `EDTChannel` is the EDT's, which holds a sender and
nothing else. So a channel is named for the half that **owns** it, and each one spans *both* directions. The two
underlying queues are named for whose inbox they are — `coreQueue`, typed `UIMessage`, and `uiQueue`, typed
`ChannelMessage` — and neither is exposed directly; a half only ever sees its own two ends.

> ⚠️ **The first draft said the opposite, and it is an easy trap.** It had channels named for the half that *sends* on
> them, so `CoreChannel` meant the core→UI queue. Under the convention the code actually settled on, `CoreChannel` is
> the core's pair — and the queue that carries core→UI traffic is `uiQueue`, because it is the UI's inbox. The two
> readings differ by exactly a swap, and a sentence written under one against names from the other is wrong while
> sounding right. That happened once already, in `big_map.md`. If a note describes `CoreChannel` as going *from* the
> core, it predates 2026-08-13.

**The messages are named for their sender**, and that half is unchanged. Everything that crosses a channel is an
`XMessage`: a **`UIMessage`** is something the UI sends, a **`CoreMessage`** is something the core sends. *(Decision
taken 2026-08-10, replacing the first draft's `DisplayMessage`/`UiEvent` pair.)* The old names
were wrong in two ways. "UI event" is ambiguous — it reads equally as *an AWT event* and as *a core-to-UI notification*,
which are opposite directions; and it collides outright with `frontend.events.Event` /
`UiEventType`, the existing port of C's `ui_event` (`src/ui-event.h`), which is a UI-internal type that never crosses a
channel. Naming by sender leaves no room for either confusion: if the UI made it, it is a `UIMessage`.

One place the sender-naming is not exclusive, recorded so it isn't mistaken for a slip: the EDT is UI-side, so the raw
events it forwards to the UI thread are `UIMessage`s — but they land in the *UI thread's* inbox rather than the core's,
because that is the only queue anyone on this side is waiting on (review note 1). So `coreQueue` carries only
`UIMessage`s, while `uiQueue` carries `CoreMessage`s *and* the EDT's `UIMessage`s — which is why the two queues are
typed `UIMessage` and `ChannelMessage` respectively.

## 2. Review notes on Architecture.md (read before stage 1)

Points where the document and reality need to shake hands. None of them break the design.

*Status marked 2026-08-13, at the end of stage 5.* Four of the six are settled and are kept as the record of why the
code looks as it does; one is a standing decision with a trigger to revisit; one is still genuinely open. A seventh
note — that `Architecture.md` said "JWT" where it meant AWT — was deleted rather than dated, the typo having been fixed
in that document, and the notes renumbered.

1. **Three threads, not two.** *(Resolved 2026-08-13 — landed across stages 1–4; `angband-core`, `angband-ui` and the
   EDT, verified by thread dump at stage 4.)* Swing owns the EDT regardless. The workable shape: the "UI thread"
   of the document blocks on `uiChannel.uiReceiver().receive()`, and `uiQueue` is its *single inbox* — `CoreMessage`s
   and
   the EDT's forwarded `UIMessage`s both land on it. Swing listeners (which run on the EDT) never reach the core: they
   wrap the raw AWT event as a `UIMessage`, put it on `uiQueue` — the UI thread's own inbox — and the UI thread parses
   it and sends any resulting `UIMessage` on to the core itself. *(Corrected 2026-08-10 — the first draft had EDT
   listeners sending
   commands directly, which Rowan rejected.)* Three reasons the corrected routing is right, in rising order of weight:
   interpretation ("which command does this event mean?") is stateful UI logic — keymaps, whatever prompt is up — that
   belongs in one loop, not scattered across listeners; a thread can only block on one queue, so merging both sources
   into one inbox is what makes the document's "waits for a) events and also b) items — either or both" implementable at
   all; and every send to the core now happens on one thread, so command ordering is deterministic. The EDT stays a
   private implementation detail of the UI half; the core never knows it exists. *Consequence for Architecture.md's
   Principles:* "the UI should never send on the core-to-UI queue" is now violated by design — the EDT is a UI-side
   producer onto `uiQueue`. The invariant that survives is about *receiving*, not sending: only the UI thread receives
   from `uiQueue`, only the core receives from `coreQueue`, and the core sends only `CoreMessage`s — all three
   enforceable by stage 1's types, since each half is handed nothing but its own pair of ends.
2. **Birth is interactive, and that's the hidden hard part.** *(Still open at 2026-08-13 — the one note this migration
   deliberately does not close; stage 5's Chapter 3 markers carry it forward.)* "Receives and processes birth related
   commands" implies
   the core *asking* the UI things and waiting for answers — C's synchronous
   `get_command`, currently stubbed as `CommandGetterHolder` / `GameInputHolder`. Under CSP that becomes a
   request/response protocol over the two channels. Nothing calls those boundaries yet, so this migration doesn't need
   to
   solve it — but it is the natural **design set piece for Chapter 3**, and stage 5 leaves a marker for it rather than
   pretending it's done.
3. **Step 4 of Startup ("waits for both threads") is what makes save-on-quit possible.** *(Resolved 2026-08-13 — landed
   at stages 3–4; `main()` joins both threads and no `System.exit` survives on the shutdown path. The save itself is
   still Chapter 8's, so what is guaranteed today is the ordering, not yet the saving.)* Today
   `closeDown()` requests a stop and immediately `System.exit(0)`s — the core dies mid-stride. In the new shape the core
   finishes its `saveAndStop` work, *then* sends `stopped`, *then* exits, and `main()` outlives it. This is a
   correctness gain, not just tidiness.
4. **Unbounded channels, for now.** *(Still in force at 2026-08-13, with its trigger unchanged — revisit in Chapter 5 if
   map redraw traffic measures slow. A decision standing, not a question outstanding.)* `LinkedBlockingQueue` without a
   capacity. Backpressure (a core producing
   `CoreMessage`s faster than the UI drains them) is a real topic but not a today problem; a bounded channel would add a
   way
   for the core to block on the UI, which is exactly the coupling being removed. Revisit if the map redraw traffic in
   Chapter 5 ever measures slow.
5. **An unexpected gain: the event-ordering constraint dissolves.** *(Resolved 2026-08-13, with the scope in the
   parenthetical below intact: the "was the UI listening yet?" class is gone, because the UI is no longer a bus
   subscriber at all. The bus's own core-internal ordering survives — `Core.gameLoop()` still registers `InitHandlers`
   before `loadGameConstants()`, and `EnterInitWiringTest` still pins it.)* Today `InitHandlers` must be registered
   before
   `loadGameConstants()` or `EVENT_ENTER_INIT` is lost — the Javadoc calls the timing load-bearing, and
   `EnterInitWiringTest` pins it. Channels *buffer*: a message sent before the UI is ready simply waits. The whole class
   of "was anyone listening yet?" bugs goes away for cross-thread traffic. (The bus keeps the constraint for its
   remaining *core-internal*
   listeners, but the UI is no longer one of them.)

6. **`backend` is the IO layer, not the shared layer.** *(Decision taken 2026-08-10, superseding the first draft, which
   treated `backend` as legitimately shared by both halves. Resolved 2026-08-13 — stage 0 carried it out, and
   `backend` now holds exactly `io/`, `parser/` and `AngbandModule`.)* `backend` is for liaising with IO — reading,
   writing and parsing files — and nothing else. Everything now in `backend` that `frontend` needs moves into `channel`;
   the
   non-IO
   remainder moves into `middle`. This is stage 0, and it comes first because it's a pure move and because it turns
   stage 5's boundary test from an allowlist with an exception in it into a flat rule: **`frontend` imports
   `channel`,
   and nothing else of ours.**

## 3. What stays untouched

Worth saying out loud, because it's most of the codebase: every parser, grammar, reader, registry and loader;
`GameConstants.init()`; the rune system; `CommandQueue`/`CommandProcessor`; and the **event bus** (`EventsBusHandler`).
The bus is the port of `game-event.c` and stays a core-internal broadcast mechanism — exactly what it is in C.

Its *handlers*, though, are a different matter. *(Amended 2026-08-10 by stage 1's design note; the first draft said the
whole bus was untouched.)* Two things change around the bus, neither of them inside it:

- the **event vocabulary** — `GameEventType` and the `GameEventData` shapes — moves into `channel` in stage 1, because
  it is what `CoreMessage`s are written in;
- the handlers that *render* stop being core code. Today `InitHandlers` subscribes core-side and calls through
  `StatusDisplayHolder` into Swing. From stage 5 the core's subscriber does one thing — put the event on the core
  channel as a `CoreMessage` — and the rendering handler lives in the UI half, which is where C keeps it (`ui-*.c`).

The bus's own mechanics, and every core-internal subscription, are untouched.

(Stage 0 rewrites a great many `import` lines across these files, but not a line of their logic. "Untouched" here means
untouched in behaviour.)

---

## 4. The route

Stage 0 plus five stages. Each is a chapter-sized chunk of checkbox items at roughly function granularity; each ends
with the game
running and something observable. Claude's half throughout: tests for every new class, verification that the visible
behaviour (splash screen, progress notes, clean shutdown)
survives each stage.

### Stage 0 — Empty `backend` of everything that isn't IO *(no behaviour change)*

A pure move: IDE "Move Class" plus import fixes, no logic touched, verified by compiling. It goes first because every
later stage is easier to state once `channel` is the only thing `frontend` is allowed to see.

**What `frontend` actually uses from `backend` today** — all of it, five types across ten files:

| Type                                   | Used by                                                                                |
|----------------------------------------|----------------------------------------------------------------------------------------|
| `colour.ColourEnum`                    | `SwingUI`, `TermWin`, `Window`, `TextOutHook`, `Colour`, `FlickerTable`, `ColourCycle` |
| `strings.AngbandDisplayCharacter`      | `SwingUI`, `TermWin`, `Window`, `SplashScreen`                                         |
| `utils.Flag`                           | `Event`, `TextUIHook`                                                                  |
| `utils.Combiner` + `utils.combiners.*` | `CombinerName`                                                                         |

The closure is small but it is *not* just those five: `AngbandDisplayCharacter` needs `ColourEnum`; the nine combiners
need `Combiner` and `UIEntryCombinerState`; and `ColourEnum` needs `ColourTranslation`, which is the subtle one. A
same-package dependency needs no `import`, so it is invisible to an import survey — `ColourEnum` holds a translation
table indexed by `ColourTranslation` and exposes `forTranslation(ColourTranslation)` and
`translateColour(…, ColourTranslation, …)`, making the enum part of its public API. It moves with it or `ColourEnum`
doesn't compile. `backend.colour` therefore empties completely.

The other two split packages were checked for the same trap and are clean: nothing moving out of `backend.strings` or
`backend.utils` touches the neighbours left behind, and none of those neighbours touches a mover.

- [X] Move to `channel`, in sub-packages so the protocol types stay visible at the top level:
    - `channel.colour` — `ColourEnum` **and `ColourTranslation`** (the whole of `backend.colour`)
    - `channel.strings` — `AngbandDisplayCharacter`
    - `channel.utils` — `Flag`, `Combiner`, `UIEntryCombinerState`, and `combiners/*` (nine of them)
- [X] Claude: move the matching tests to mirror the new packages —
  `src/test/…/backend/colour/ColourEnumTest` is the one that exists today.
- [X] Move the non-IO remainder to `middle`. Nothing in `frontend` touches any of it, so this half is invisible to
  the
  boundary:
    - `backend.numerics` (`Random`, `Dice`, `Rational`, `RandomChance`, `RandomValueUtils`) → `middle.numerics`.
      Fourteen
      `middle` files and nineteen `backend` files import these; `backend` importing `middle` is already the normal
      direction (92 `backend` files do it today), so this costs nothing.
    - `backend.enums.DamageAspect` → `middle`, alongside its two users there.
    - `backend.strings.Quark`, `backend.strings.TextBlock`, `backend.utils.NumberUtils`, `backend.utils.StringUtils`,
      `backend.utils.ControlUtils` → `middle`. All five are ported-but-not-yet-called (nothing references them). Move
      them to where their eventual callers will live rather than leave them sitting in `backend` defining it wrongly.
    - `backend.utils.quit` — three `backend` files use it, but quitting is a game-lifecycle concern, not IO. Under the
      target architecture it becomes the `SAVE_AND_STOP`/`STOPPED` handshake, so this one is better **deleted in stage
      3**
      than moved now. Leave it where it is and let stage 3 take it.
- [X] After the move, `backend` contains exactly `io/`, `parser/` and `AngbandModule` — and `utils/quit`, on borrowed
  time. *(Stage 3 collected on that: `utils/quit` is gone, replaced by the `SAVE_AND_STOP`/`STOPPED` handshake as
  planned, so `backend` is now exactly the first three.)*
- [X] Claude: verification — full compile, full test run, and a grep proving `frontend` no longer names `backend`
  anywhere. *(2026-08-10: `clean build` green; 123 test classes / 1303 tests, 0 failures, 0 errors, 0 skipped;
  `grep -rn "backend" src/main/java/uk/co/jackoftrades/swingUI` returns nothing — not just the qualified name, the bare
  word. Five stray tests moved to mirror the `middle` move: `RationalTest`, `RandomValueUtilsTest` →
  `middle.numerics`, `QuarkTest` → `middle.strings`, `NumberUtilsTest`, `StringUtilsTest` → `middle.utils`. Game
  launches, splash screen and progress notes render.)*

**Done when:** `grep -r "jackoftrades\.backend" src/main/java/uk/co/jackoftrades/swingUI` returns nothing, and the game
still starts and shows the splash screen.

*A note on what this does to `channel`'s meaning, recorded so it isn't rediscovered as a surprise.* Before this decision
`channel` was to be the wire protocol and nothing more. It is now also the **shared vocabulary** — the types both halves
must agree on to be able to talk about colours and characters at all. That is a coherent thing for it to be: these are
precisely the words the messages are written in, and a `CoreMessage` carrying a coloured string carries a
`ColourEnum` by definition. It does mean the package name reads narrower than its contents. If that ever grates, the
alternative shape is `uk.co.jackoftrades.shared` holding `shared.channel` for the transport beside `shared.colour` and
friends; the file moves would be identical and the rename is cheap at any time. Not proposed — just recorded.

### Stage 1 — The messages and the channels *(no behaviour change)*

Define the protocol before any wiring moves. It lands in `uk.co.jackoftrades.channel`, the package stage 0 has just made
the *only* thing both halves share.

- [x] `ChannelMessage` — the root of the protocol, a sealed interface with exactly two branches, one per sender:
    - `CoreMessage` — sealed; **one record per payload shape, not one per occasion** (see "What the core actually
      sends", below). Two groups: *game events*, each carrying a `GameEventType` and whatever data that event needs; and
      *lifecycle*, which are protocol rather than gameplay. Today's traffic needs `Simple(GameEventType)` for
      `EVENT_ENTER_INIT`, `Text(GameEventType, String)` for `EVENT_INITSTATUS` and the birth notes, and
      `Lifecycle(CoreLifecycle)` — one constant, `STOPPED` — for the shutdown handshake.
    - `UIMessage` — sealed; **same rule, one record per payload shape** (see "What the UI actually sends", below). Two
      populations, on different axes: *raw input* from the EDT, which is the port of C's `ui_event`; and *intent* from
      the UI thread, which is the port of C's `struct command`. Today's traffic needs neither in full — only
      `Lifecycle(UiLifecycle)`, carrying `START` or `SAVE_AND_STOP`, *to the core on `coreQueue`*, and
      `WindowCloseRequested()` *to the UI thread on `uiQueue`* (the inbox, because that is the only queue the UI
      thread is blocked on). Note what the EDT is doing: it *receives* an AWT `WindowEvent` and *sends* a
      `WindowCloseRequested`. The AWT type stays inside the EDT; the record is the message that leaves it.
    - Both `Lifecycle` records are payload-free, so by the rule they are **one record each with the meaning in an enum
      field**, not one record per occasion. *(Rowan, 2026-08-10.)* Until Chapter 5 this makes `coreQueue` a
      single-record channel — a *fact about today's traffic*, not an invariant: population B joins it as soon as there
      are game commands to send.
- [x] Move the **vocabularies** into `channel` — the enums of meaning and the payload shapes they travel in, but not the
  machinery that dispatches them (see both design notes):
    - `GameEventType` + the `GameEventData` shapes — the eleven `EventData*`, tiered in "Which shapes can actually move"
      below. `EventsBusHandler`, `EventsHandler` and `EventHandlerInterface` stay in `middle`: they are the machinery,
      the port of C's `event_signal_*` family.
  - `UiEventType` + the keypress/mouseclick shapes, out of `frontend.events`. *(Only when population A lands — Chapter
        5. Listed here because the reasoning belongs with the other two.)*
    - `CommandCode` + the `cmd_arg` shapes. `CommandQueue`/`CommandProcessor` stay in `middle`. *(Chapter 5.)*
- [x] `Grid(int y, int x)` in `channel` — the flattened form of `middle.cave.Loc` for messages that carry positions.
- [x] `Channels` — one small class/record holding the two typed `LinkedBlockingQueue`s — `uiQueue` of
  `ChannelMessage` (the UI thread's inbox, both senders) and `coreQueue` of `UIMessage` — created in one place and
  handed to both halves. Consider thin wrapper views instead of raw queues so the receive invariants are
  compiler-enforced: the core gets a *send-only, `CoreMessage`-only* view of the core channel and a *receive-only* view
  of the UI channel — then the core cannot forge a `UIMessage`, receive on the wrong channel, or send on the UI channel,
  by type alone. A worthwhile design conversation, not a requirement.
- [x] **Open point for that conversation.** Naming by sender puts the EDT's `WindowCloseRequested` and the UI thread's
  `Lifecycle` in one sealed type, so the compiler alone no longer stops a `Lifecycle(START)` being put on the core
  channel or a `WindowCloseRequested()` on the UI channel. If that matters, the fix is two sealed sub-interfaces
  *inside* `UIMessage` (one per UI-side sender) rather than a return to direction-named types — the `XMessage`
  vocabulary stays either way.

  Note that this split is arriving anyway, for unrelated reasons: populations A and B *are* the two UI-side senders. So
  there may be nothing to decide at stage 1 — with one record on each side there is very little for the sub-interfaces
  to separate, and Chapter 5 brings both the need and the members. Deferring is the cheaper bet; the cost of being wrong
  is that the wrapper views get written twice.
- [x] Claude: tests — message equality, channel round-trip across two real threads.

*Primer candidates:* sealed interfaces + records as a message protocol (you've met sealed with
`RuneVariety`; the new bit is records-as-messages); `BlockingQueue` semantics (`put`/`take`/
`offer`/`poll`, and why `take` blocking is a feature).

**Done when:** it compiles, tests pass, and nothing else has changed.

#### Design note: what the core actually sends *(decided 2026-08-10)*

The first draft said "one record per thing the core can tell the UI", with four members. Rowan's objection: that set
grows without bound. One for the splash screen, one for a progress line, one for redrawing a character when a monster
moves, one for switching which window is being drawn on, one for asking the UI a question — and the game has barely
started. The objection is right, and the fix has two parts: pick the axis, then split on payload.

**The axis: the channel carries meaning, not drawing.** The tempting set — *redraw everything, draw a line, redraw one
character, change the active window* — is very nearly C's `term` hook set (`wipe_hook`, `text_hook`, `pict_hook`,
`curs_hook`, `Term_activate`; about ten in `ui-term.h`). That set really is closed: it describes what a display can
*do*, and it has not grown as Angband has. But in C those hooks are called by `ui-*.c`, not by the game. The core
signals `EVENT_HP`; a handler in the front end decides the health bar sits at row 5 in red and calls `Term_putstr`.
Putting the primitives on the channel would put that decision core-side and hand the core a screen layout — precisely
the coupling this migration exists to remove. `frontend` already owns `TermWin`, `Window` and `SwingUI`: **term
primitives stay UI-internal and never cross a channel.** What crosses is the event.

**The split: by payload shape.** With that axis chosen the set is not open-ended, because C has already enumerated it
and the port has already ported it:

- `GameEventType` — 65 constants, fixed by C's `game-event.h`, already in `middle.game.enums`;
- `GameEventData` — the port of C's `game_event_data` **union**, which has about ten shapes (`point`, `string`,
  `message`, `birthstage`, `explosion`, `bolt`, `size`, …), already an interface with `EventData*` implementations in
  `middle.game.event`.

So `CoreMessage` gets roughly ten records — one per payload shape, each carrying the `GameEventType` that says what it
means — plus the lifecycle handful. `EVENT_HP`, `EVENT_GOLD` and `EVENT_AC` all ride the same payload-free record. The
record count is driven by payload variety, which is stable; not by game content, which is not.

```java
sealed interface CoreMessage extends ChannelMessage {
    // game events — the payload shapes of C's game_event_data union
    record Simple(GameEventType type) implements CoreMessage {
    }   // the payload-free majority

    record Point(GameEventType type, int x, int y) implements CoreMessage {
    }

    record Text(GameEventType type, String text) implements CoreMessage {
    }

    record Msg(GameEventType type, String text, MessageType msgType) implements CoreMessage {
    }
    // …explosion, bolt, birthstage as their events acquire real callers

    // lifecycle — protocol, not gameplay; its own vocabulary, not GameEventType
    record Lifecycle(CoreLifecycle what) implements CoreMessage {   // STOPPED, for now
    }
}
```

**The trap to avoid**, since it is the obvious reading of "flat payload": *not*
`record CoreMessage(GameEventType type, Object payload)`. The enum makes the `switch` exhaustive, so it compiles — but
nothing then checks that `EVENT_MESSAGE` arrives carrying message-shaped data. You would be casting, and the
compiler-as-protocol-checker, which is the entire reason for using sealed records, is gone. Keep the fields typed.

**Three of today's four messages were never new.** `ShowSplashScreen`, `LoadNote` and `BirthNote` are
`EVENT_ENTER_INIT`, `EVENT_INITSTATUS` and the birth notes wearing different hats — they already travel the bus, and
`InitHandlers` already turns them into `StatusDisplay` calls. Only the stop signal is genuinely new, which is why the
lifecycle group exists at all.

**Lifecycle gets a record, not a record per signal** *(Rowan, 2026-08-10)*. `STOPPED` carries nothing, so it rides
`Lifecycle(CoreLifecycle)` exactly as the payload-free game events ride `Simple(GameEventType)`. The compiler still
checks you handled every case: a `switch` *expression* over an enum must cover all constants or carry a default. This is
not the `Object payload` trap above — there is no payload here to arrive in the wrong shape. It also keeps both ends of
the handshake modelled alike, since the UI's `Lifecycle` is the same construction (see the next note); a bare
`Stopped()` facing a `Lifecycle(START | SAVE_AND_STOP)` would have been two shapes for one conversation.

**What this costs elsewhere in this document,** recorded here so the change isn't rediscovered as a contradiction: §3
listed the bus as untouched and wholly core-internal, and stage 5 planned to *retire* `StatusDisplayHolder`. Under this
shape the bus is still core-internal and still the port of `game-event.c` — but the **event vocabulary** moves to
`channel` (stage 1), and the handlers that render move to the UI half (stage 5) rather than being deleted. That is the C
arrangement, where the handlers live in `ui-*.c`. Both sections below are amended to match.

#### Design note: what the UI actually sends *(decided 2026-08-10)*

The same question, asked of the other direction, and the same rule answers it — but `UIMessage` is not the mirror image
of `CoreMessage`, because the UI has **two senders with two unrelated vocabularies**. Splitting them is the first move;
everything else follows.

**Population A — the EDT, sending raw input to the UI thread.** This is the port of C's `ui_event`, whose union has
exactly three shapes against eight event types:

```c
typedef union {
    ui_event_type type;        /* bare: RESIZE, BUTTON, ESCAPE, SELECT, MOVE, SWITCH */
    struct mouseclick mouse;   /* x, y, button, mods */
    struct keypress key;       /* code, mods */
} ui_event;
```

So three records — `Keystroke(int code, int mods)`, `MouseClick(int x, int y, int button, int mods)`, and a bare
`Input(UiEventType type)` carrying the rest. At this size "split by device" and "split by payload shape" happen to give
the same answer, so the rule from the `CoreMessage` note costs nothing here.

*Except for one.* `WindowCloseRequested` has no C counterpart — `ui_event_type` has no close or quit constant, because C
leaves by a different road entirely. It is a genuine AWT-driven addition, so when you write it, say so in the Javadoc:
"port of C's X" is true of its three neighbours and false of this one.

**Population B — the UI thread, sending intent to the core.** Not `ui_event`; this is C's `struct command` — a
`cmd_code` plus up to four tagged `cmd_arg`s (`CMD_MAX_ARGS`, `cmd-core.h`), against about ninety command codes. One
record per code would be the explosion in its worst form, so the rule applies unchanged: carry `CommandCode` in a field,
split by argument shape.

Alongside it sits **lifecycle**, which is protocol rather than gameplay: starting the core, and asking it to save and
stop. Both carry nothing, so by the rule they are one record — `Lifecycle(UiLifecycle)`, with `START` and
`SAVE_AND_STOP` as constants — not a record each. *(Rowan, 2026-08-10.)* Three consequences worth having written down:

- **`coreQueue` carries exactly one record type until Chapter 5.** That is a fact about today's traffic, not a property
  of the design. Population B lands on the same channel the moment there is a keymap producing commands, so nothing
  should be built that assumes a single-record channel — least of all the wrapper views.
- **The core's lifecycle collapses the same way**, which is why `Stopped()` became `Lifecycle(CoreLifecycle)` in the
  note above. Two enums rather than one, because the two halves say different things: the UI has `START` and
  `SAVE_AND_STOP`, the core has `STOPPED`. Sharing one enum would let each end name a signal it can never send.
- **Expect `SAVE_AND_STOP` to split back out.** It is payload-free today, but C's exit paths distinguish saving from not
  (`CMD_QUIT` beside the retire path), so a "save or discard" flag is a plausible Chapter 8 arrival. When it comes it is
  a different payload shape and therefore its own record — the rule handles it, and the collapse being undone later is
  not evidence it was wrong now.

**On the name.** `Lifecycle`, not `Control`. The document already calls this group lifecycle on both sides, and
`Control` collides twice over: `ControlUtils` exists in `backend.utils` (stage 0 moves it to `middle`), and
`KC_MOD_CONTROL` is keyboard-modifier vocabulary arriving with population A. `UIMessage.Control(START)` would send a
reader to check whether it meant the Ctrl key.

**Only the lifecycle pair and `WindowCloseRequested` are stage 1's business.** Population A arrives with keystrokes and
population B when there is a keymap to interpret them against — both Chapter 5. Writing either now would be porting
against no caller.

**The symmetry that actually holds** is not between the record sets — it is that each direction has an *enum of
meanings* and a small closed set of *payload shapes*, and in all three cases the record count tracks the shapes:

| Direction        | Meaning (enum)       | Payload shapes             | Machinery that stays put          |
|------------------|----------------------|----------------------------|-----------------------------------|
| core → UI        | `GameEventType` (65) | `game_event_data`, ~10     | the bus (`EventsBusHandler`)      |
| EDT → UI thread  | `UiEventType` (8)    | `ui_event`, 3              | AWT listeners                     |
| UI thread → core | `CommandCode` (~90)  | `cmd_arg` ×4, tagged union | `CommandQueue`/`CommandProcessor` |

All three enums and all three shape sets move into `channel`; none of the machinery does.

**A correction to §5's table,** made rather than left to be tripped over: an earlier draft listed
`frontend.events.Event` / `UiEventType` as "UI-internal, never crosses a channel", and used that to justify the
`UiEvent` → `UIMessage` rename. That does not survive population A — those records *are* the port of `ui_event`, and
`channel` cannot import `frontend`, so the vocabulary has to move or a keystroke ends up with two representations.
The
rename still stands, on better grounds: `UIMessage` is the envelope, `UiEventType` is the vocabulary written inside it —
the same relation `CoreMessage` has to `GameEventType`.

#### Design note: which shapes can actually move *(decided 2026-08-10)*

"Move the payload shapes into `channel`" is easy to write and turns out to be five different jobs. The eleven
`EventData*` classes were surveyed; five of them name a `middle` type, and those five are where the real decisions are.

**The rule, tighter than "move the shapes":** a `middle` type moves into `channel` **only if it is genuinely a word the
messages are written in** — something the UI must understand to render at all. Otherwise the *shape* flattens to
primitives plus what `channel` already holds (`ColourEnum`, `AngbandDisplayCharacter`, `Grid`), and the `middle` type
stays where it is. *(Rowan's challenge, 2026-08-10: the first draft moved `Loc` and `Stats` too, and neither passes.)*

| Shape                                         | Names                       | Verdict                                                                                         |
|-----------------------------------------------|-----------------------------|-------------------------------------------------------------------------------------------------|
| `String`, `Boolean`, `Size`, `Tunnel`, `Bolt` | nothing but primitives      | move as they are                                                                                |
| `Message`                                     | `middle.enums.MessageType`  | **move `MessageType` too** — C's `MSG_*` list is shared, the front end colours and sounds by it |
| `Point`, `Explosion`                          | `middle.cave.Loc`           | **flatten to `Grid`**                                                                           |
| `BirthPoints`                                 | `middle.enums.Stats`        | **`Stats` stays** — flatten to indexed ints                                                     |
| `BirthStage`                                  | `Object xtra`               | **blocked** — untyped, needs a real type                                                        |
| `Missile`                                     | `middle.objects.ItemObject` | **blocked** — flatten to appearance                                                             |

**Why `Loc` flattens rather than moves.** C's `struct loc` is `{int x, y;}`, a POD; `loc_sum`, `next_grid`,
`rand_loc` and friends are free functions in `cave.c`. The Java port fused the struct and its helpers into one 268-line
class, so moving it would drag `nextGrid(DirectionEnum)`, `rand(xSpread, ySpread)`, the arithmetic and the
`RowBuilder` DSL into the shared package — dungeon movement and RNG, in the package the UI imports. Flattening restores
C's own separation. A record rather than bare ints because `EventDataExplosion` holds an `ArrayList<Loc> blastGrid`;
`Grid` is Angband's own word for a map square and does not collide with `Loc`, which stays in `middle.cave`.

> ⚠️ **The transposition trap.** `Loc`'s constructor is `Loc(int x, int y)` — x first — and `Grid` is written y first,
> matching how most of Angband's older code passes coordinates. Two int fields means a swapped pair compiles silently
> and surfaces as a mirrored dungeon, a long way from the conversion that caused it. Whichever order wins, the
> `Loc` ↔ `Grid` conversion needs a test that would fail on a transposition — not a round-trip, which passes when both
> directions are wrong the same way.

**Why `Stats` does not move.** C's `birthpoints` payload is `const int *points`, `const int *inc_points`,
`int remaining` — indexed arrays carrying no stat identity whatever. The labels live entirely front-end side:
`stat_names[]` and `stat_names_reduced[]` are *defined* in `ui-display.c` and used from `ui-player.c` and `ui-birth.c`.
The core never sends a stat's name. So `EventDataBirthPoints` carries indexed ints — its `HashMap<Stats, Integer>` is
already a divergence from C's array — and the UI owns its own label table.

There is a sting worth recording: `Stats.getStatString()` returns `"STR"`, `"INT"`, `"WIS"` — that *is* C's
`stat_names_reduced`, currently living in `middle.enums`. A UI table in the core. Moving `Stats` to `channel` would
cement that inversion into the shared package; leaving it keeps the mistake contained, and Chapter 5 can lift the labels
out to where C has them.

**And `ElementEnum` does not move either** *(asked and answered 2026-08-13, during stage 5's boundary test)*. This one
arrives from outside the payload shapes — it is a *field* on `UIEntry`, `frontend`'s only crossing of rule 1 that is not
the input boundary — but the rule above answers it unchanged, and C is unusually emphatic. `struct ui_entry` holds a
plain `int param_index` (`[C] src/ui-entry.c:109`); the element identity never enters it as a typed value. The parameter
kind is resolved by *string* through `name_parameters[]`, whose `"element"` row calls `get_element_name`, which reads an
`element_names[]` defined `static` at `[C] src/ui-entry.c:1577` — front-end-local, exactly as `stat_names[]` is. C
carries four independent `element_names[]`-ish tables (`init.c`, `obj-init.c`, `datafile.c`, `ui-entry.c`), each private
to its consumer: the front end deliberately does not share the core's element vocabulary. So this is the `Stats` verdict
a second time — the port's typed field flattens, `ElementEnum` stays in `middle.objects.enums`.

*Not yet, though, and for the reason this document keeps giving.* `getParameter()` has no caller anywhere in
`src/main` — the field is written by the assembler and read only by tests — and the thing that would first read it is
the second character screen (`ui-player.c`), which is not ported. The right flattened shape (an index, as C has it, or
the resolved label) is a guess until something renders it, and worse, it interacts with the larger question above: if
the `ui_entry` parsing moves front-end-side where C keeps it, `ElementEnum` stops being reachable from `frontend` for a
different reason and no flattening is needed at all. Designing the flattening now would mean designing it around keeping
`backend` able to build a `UIEntry` — which is probably the arrangement that is wrong. Baselined instead.

**The two blocked shapes.** `EventDataMissile` holds an `ItemObject`, and moving that would expose the object system to
`frontend` — the coupling this migration exists to remove. C shows the way out: `game_event_data.missile` carries
`struct object *obj`, and `ui-*.c` immediately reduces it to appearance via `object_kind_char`/`object_kind_attr`. The
UI never wants the object, only its glyph and colour — `AngbandDisplayCharacter` + `ColourEnum`, both already in
`channel`, resolved core-side before the message is sent. `EventDataBirthStage` has the milder version, an
`Object xtra` mirroring C's `const void *xtra`; an untyped escape hatch cannot cross a typed channel.

**Neither is urgent, and that is the point.** Every one of the eleven is referenced only by `EventsHandler`, and only
`EventDataString` has a second caller (`InitHandlers`). Nothing in the game signals a missile or a birth stage yet, so
leave both where they are with a comment: the flattened form is better designed against a real caller than guessed at
now. This is the same caution recorded against `cmd_arg` in the previous note, arriving early on the event side.

*One to know before it bites:* `EventDataBolt.projType` and `EventDataExplosion.projType` are both `int`, each with a
"probably going to be replaced by an Enum" comment. When that enum arrives it is channel vocabulary, not `middle` —
worth knowing before choosing where to put it.

### Stage 2 — Core→UI traffic crosses the core channel

The pivot of the whole migration, and it's small because `StatusDisplay` is already the perfect boundary: swap *which
implementation* is registered, and the core doesn't notice.

- [x] `ChannelStatusDisplay implements StatusDisplay` (core side): each method wraps its arguments in the matching
  `CoreMessage` and `put`s it on the core channel. Two one-line methods —
  `showSplashScreen()` → `SimpleCoreMessage(EVENT_ENTER_INIT)`, `splashScreenNote(text)` →
  `TextCoreMessage(EVENT_INITSTATUS, text)`.
- [x] `StatusDisplay` loses `splashScreenBirthNote`; the birth note is parked for Chapter 3 (see below). Drop the
  `@Override` on `SplashScreen`'s implementation rather than deleting it — the painting code and its Javadoc are right,
  they simply have no caller yet — and delete the empty stub from `DefaultStatusDisplay`.
- [x] `UiLoop` (UI side, a `Runnable`): loop on `uiChannel.uiReceiver().receive()`, switch over the sealed
  `ChannelMessage` — for each `CoreMessage`, `invokeLater` the painting the old
  `SplashScreen` methods do now (`SplashScreen` itself keeps its painting code; it just stops being the thing the *core*
  calls); `UIMessage` handling arrives in stage 3.
- [x] `SwingUI.init`: build the channels (temporarily — they move to `main()` in stage 4), register
  `ChannelStatusDisplay` in the holder instead of `SplashScreen`, start the consumer on its own thread
  (`new Thread(consumer, "angband-display")`).
- [x] Claude: tests — a fake channel proves each `StatusDisplay` call becomes the right message; consumer tests prove
  each message reaches the right painting call.

**Why the birth note is parked, in full — it looks like a dropped method otherwise.** C has *one* callback,
`splashscreen_note` (`[C] src/ui-display.c:2403`), which branches at run time on
`data->message.type == MSG_BIRTH`: birth notes stack down from row 2 and pause for a keypress, load notes rewrite row 23
centred and bracketed. The port split that run-time branch into two interface methods so the choice is made at the call
site and checked by the compiler — a good decision, taken one chapter early. Nothing distinguishes the two on the wire:
both would send `TextCoreMessage(EVENT_INITSTATUS, text)`, byte-identical, because no
`MSG_BIRTH` equivalent has been ported yet. Two methods whose whole point is that they behave oppositely, collapsing to
the same message, is the tell. Implementing it now means inventing the discriminator — a second `GameEventType`? a
`MessageType` field on the record? — against no caller, and `InitHandlers` confirms it: `splashScreenNote` logs rather
than forwarding, precisely because the payload cannot yet say which kind of note it holds. Same call already recorded
here for `EventDataMissile` and `cmd_arg`, and for the same reason: better designed against a real caller than guessed
at now. Removing the method from the interface rather than leaving it unimplemented is the honest form — a method on the
boundary that looks routable and isn't is worse than no method.

*Primer candidates:* producer–consumer as a pattern; the EDT and `invokeLater` (why painting must hop, why `take()` must
not run on the EDT) — both now written, in `docs/primers/`. Also fixes in passing: `SplashScreen`'s own Javadoc admits
it touches Swing from the game thread today — after this stage every touch arrives via the consumer's `invokeLater`,
closing that hole.

**Done when:** the splash screen and the "Initializing arrays…" notes still appear — but a breakpoint (or a logging
wrapper) shows every one of them crossed the core channel.

### Stage 3 — UI→Core traffic crosses the UI channel; clean shutdown

The `sleep(5)` placeholder in `GameRunner.gameLoop()` becomes a real receive loop, and the kill-the-thread shutdown
becomes the document's handshake.

- [x] `gameLoop()`: after `loadGameConstants()`, loop on `coreChannel.coreReceiver().receive()`; switch over
  `UIMessage`, then over the
  `UiLifecycle` inside it. `START` → log it (birth lands here in Chapter 3). `SAVE_AND_STOP` → send
  `Lifecycle(STOPPED)` on the core channel and fall out of the loop; the thread ends. (Nothing to save yet — the save
  half arrives with Chapter 8.)
- [x] `SwingUI.init`: once the window is up, `put` a `Lifecycle(START)` — the document's step 1.5.
- [x] `windowClosing`: put a `WindowCloseRequested()` on the *core* channel — the EDT forwards the raw AWT event as a
  `UIMessage` and nothing more (review note 1). No `requestStop`, no `System.exit`.
- [x] `UiLoop`, on `WindowCloseRequested()`: send `Lifecycle(SAVE_AND_STOP)` on the UI channel — the one place raw AWT
  events are turned into messages for the core.
- [x] `UiLoop`, on `Lifecycle(STOPPED)`: `invokeLater` the window disposal (the old `closeDown`
  body minus `exit` and minus `requestStop`), then fall out of its own loop; the thread ends.
- [x] Delete the `requestStop()`/interrupt path — the `RuntimeException`-on-interrupt wart in
  `gameLoop` (its own Javadoc already flags it) goes with it.
- [x] Claude: tests — the handshake in both orders; verify the process exits with no
  `System.exit` anywhere on the path (all windows disposed → EDT ends → JVM ends).

*Primer candidates:* thread lifecycle (why the JVM exits when the last non-daemon thread does, and what keeps the EDT
alive); the poison-pill / sentinel-message pattern (`Lifecycle(STOPPED)` is one).

**Done when:** the game starts, the splash shows, and closing the window shuts everything down cleanly — verifiable with
a thread dump (`jstack`) before and after, and by the exit code.

### Stage 4 — Ownership moves to `main()`

The structure finally matches the document's Startup section. Mostly *moving* code, not writing it.

- [x] `Main.main()`: create the `Channels`; create and start the core thread and the UI thread;
  `join()` both; return. (The `-l`/usage windows stay exactly as they are — they run before either thread exists, as
  `Old_java_map.md` already notes.)
- [x] Core side: `GameRunner` absorbs its last responsibilities and becomes the core's `Runnable`
  (suggested rename: `Core`) — constructed with the channels and the startup options, no longer constructed *by the UI*.
- [x] UI side: a `Runnable` (inside `SwingUI` or wrapping it) that does the Swing bootstrap on the EDT (`invokeLater`
  from the UI thread) and then runs the `UiLoop` as its own body — one thread, matching the document's UI sequencing.
- [x] `SwingUI` loses its `core` field entirely. After this stage the UI's compile-time view of the core is:
  nothing. The channel package is the whole interface.
- [x] `StartupOptions`: both halves get a copy (document's steps 2–3); the static field in `Main`
  and its happens-before Javadoc essay can go — arguments become plain constructor parameters.
- [x] Claude: verification — full startup/shutdown pass; thread dump naming check (`angband-core`, `angband-ui`, EDT);
  confirm `main` really does outlive both.

*Primer candidate:* `Thread.join()` and structured teardown — why "main waits" is what makes
"save before exit" a guarantee rather than a race.

**Done when:** `Old_java_map.md`'s startup narrative can be rewritten to match `Architecture.md`'s Startup section line
for
line — because they now describe the same program.

### Stage 5 — Seal the boundary and take stock

- [x] Retire `StatusDisplayHolder` + `DefaultStatusDisplay`: with the channel in place, `InitHandlers`' bus handlers can
  put `CoreMessage`s on the core channel directly (handed the channel, not reaching a static). The `StatusDisplay`
  interface either retires with it or survives UI-side as the consumer's painting boundary — Rowan's call; either is
  defensible.
- [x] **Split the handlers across the boundary** *(added 2026-08-10 by stage 1's design note)*. `InitHandlers` currently
  does two jobs in one place: it subscribes to the bus, and it decides what the screen should look like. Those separate
  here.
    - Core side keeps the subscription and does one thing with it: wrap the event as the matching `CoreMessage` and put
      it on the core channel. No Swing, no layout, no colour.
    - UI side gains the rendering half — the `switch` over `GameEventType` that decides *where* and *how*, calling
      `TermWin`/`Window` directly. This is C's arrangement: `game-event.c` broadcasts, `ui-*.c` draws.
  - The test that this landed: `grep` for `GameEventType` in `frontend` returns hits, and `grep` for anything Swing
    in
      the core's handler returns none.
- [x] **The boundary test** (Claude writes, both maintain): a test that walks `src/main` imports and enforces three
  rules, which stage 0 is what makes them this simple —
    - `frontend.**` may name `channel.**` and nothing else of ours (not `middle`, not `backend`);
    - `middle.**` may not name `frontend.**`;
    - `backend.**` may not name `frontend.**`.

  This turns `Old_java_map.md`'s "you can check by reading the import statements" from a habit into a regression test.
  The
  first rule would have been red before stage 0 and green after; the other two catch backsliding.

  **What was actually built** *(`BoundaryTest`, 2026-08-13)*, in two places where it went wider than the sentence above:

    - **It reads names, not imports.** A fully qualified name in a method body couples exactly as tightly as an import
      and appears in no import survey, so the scan looks for `uk.co.jackoftrades.…` anywhere in the file — which
      subsumes the import list. Comments and string literals are blanked first, since a Javadoc `{@link}` across the
      boundary is a cross-reference and not a dependency. Ten of the class's tests exercise the scanner rather than the
      rules: a boundary test that scanned nothing would pass all three, so the machinery has to prove it has teeth
      before its negatives mean anything.
    - **Each rule carries a baseline, and the baseline is a ratchet.** Every crossing that remains is blocked on a
      chapter that has not been written, so three plain negatives would have meant a red build for months — which
      teaches a person to stop reading the failure. Instead each rule asserts *no crossing outside its baseline* **and**
      *no baseline entry without a crossing*. The second half is what keeps it from becoming a suppression list: fix a
      crossing and the build goes red until the line is deleted, so the count can only fall and the baseline is always a
      census rather than a history. When a baseline empties, deleting the constant leaves the plain negative this
      checkbox originally described.

  Starting counts, each entry annotated in the test with the chapter that removes it: **6** for rule 1 (five are
  `TextUIHook`, the input boundary this stage parks by name below; one is `UIEntry`'s element, waiting on the character
  screen), **13** for rule 2 and **23** for rule 3 — those two being one job seen at two layers, the `ui_entry`/
  `visuals` data being loaded core-side. So the test is green, but green with 42 crossings named and dated, and the
  stage is done in the sense that the rule is now enforced rather than in the sense that the boundary is clean.

  *A finding worth keeping, since it explains 36 of those 42:* C does not have this problem because **`ui_entry.txt` is
  parsed inside `ui-entry.c`**. All `init.c` holds is `{ "ui entries", &ui_entry_parser }` — an opaque
  `struct file_parser` of function pointers — and `struct ui_entry` is an incomplete type in `ui-entry.h`, so nothing
  outside the front end can see its fields even in principle. The front end parses its own data files. Moving the
  readers, assemblers and registries to match is the fix for both baselines, and it is chapter-sized.
- [x] Docs pass: `Old_java_map.md` rewritten; `big_map.md`'s ending checked (it already points this direction); a
  verdict paragraph recorded here, set-piece style — what it cost, what it paid.
    - This has been a learning experience. I have implemented threads, both via thread.start and via invoke later. I
      have learned about LinkedBlockingQueues, and have implemented queues with two ends and ones with 'one' end which
      file on a pre-existing queue.
    - **What it cost**: A lot of time and thinking power. I am still having some brain fog issues due to the amount of
      sheer brain power required. However it was, I think, worth it.
    - **What it paid**: It paid off in the fact that I have a saner, cleaner build to use going forward. A tool to check
      it stays sane, and a better understanding of how the base of the game works.
- [ ] **Marker for Chapter 3:** the input boundaries (`CommandGetterHolder`, `GameInputHolder`,
  `DefaultCommandGetter`) are still holder-shaped and still uncalled. Their CSP form — the core sends a request
  `CoreMessage` and blocks on the UI channel for the reply — is the birth chapter's design set piece. Do not
  migrate them speculatively now; migrate them when birth gives them their first real caller.
- [ ] **Second marker for Chapter 3 — the birth note.** Stage 2 removed `splashScreenBirthNote` from `StatusDisplay`
  (reasoning there). Chapter 3 is what gives it a caller, and restoring it needs three things, none of which should be
  guessed at before then: a way for the wire to say "this is a birth note" (C's `MSG_BIRTH`, so probably
  `MessageType` on the message rather than a second `GameEventType` — it is a property of the note, not a different
  event); the method back on `StatusDisplay` and the `@Override` back on `SplashScreen`, whose painting code is already
  correct and waiting; and the pause. That last one is the interesting half: C calls `pause_line(Term)` after each note
  so the player reads them one at a time, and a pause is a *read* — so it needs the input boundary above, and the two
  markers are really one piece of work. Also settle the wrap divergence then (`SplashScreen`'s Javadoc records it: C
  wraps at 24 and reuses row 23, the port wraps at 23 and loses a row).

**Done when:** the boundary test is green and would have been red at every stage before 4.

---

## 5. Naming table (for consistency as you go)

Everything crossing a channel is an `XMessage` named for its **sender**; each channel is named for the half that
**owns** it, and holds that half's two ends. The two queues underneath are named for whose inbox they are.

| Concept                             | Name                                                                                                                                                                    | Not to be confused with                                             |
|-------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------|---------------------------------------------------------------------|
| The core's pair of ends             | `CoreChannel(CoreReceiver, CoreSender)` — handed to the core, spans both directions                                                                                     | the core→UI *queue*, which is `uiQueue` (the UI's inbox)            |
| The UI thread's pair of ends        | `UIChannel(UIReceiver, UISender)` — handed to `UILoop`                                                                                                                  | the UI→core *queue*, which is `coreQueue` (the core's inbox)        |
| The EDT's end                       | `EDTChannel(EDTSender)` — a sender and deliberately no receiver                                                                                                         | a full channel; the EDT may never block on a queue                  |
| UI→core transport                   | `coreQueue` (the core's inbox, typed `UIMessage`)                                                                                                                       | `CommandQueue` (core-internal `cmdq` port — unchanged)              |
| Core→UI + EDT→UI transport          | `uiQueue` (the UI thread's inbox, typed `ChannelMessage`)                                                                                                               | the event bus (core-internal — unchanged)                           |
| Root of the protocol                | `ChannelMessage` (sealed: `CoreMessage` \| `UIMessage`)                                                                                                                 | `middle.Message` — C's `msg`/`msgt` log, core-internal, not renamed |
| Anything the core sends             | `CoreMessage` (sealed, ~one record per payload shape)                                                                                                                   | `GameEventData` — the shapes it carries, moved to `channel`         |
| Anything the UI sends               | `UIMessage` (sealed, ~one record per payload shape)                                                                                                                     | `UiEventType`/`CommandCode` — the vocabularies it carries           |
| What a message *means*              | a `GameEventType`/`UiEventType`/`CommandCode` field                                                                                                                     | the record type, which says only what *shape* the payload is        |
| A raw AWT event                     | never crosses a channel                                                                                                                                                 | the `UIMessage` the EDT wraps it in and forwards                    |
| A `term` drawing primitive          | never crosses a channel — UI-internal                                                                                                                                   | the `CoreMessage` whose handler calls it, UI-side                   |
| Core thread's runnable              | `Core` (was `GameRunner`)                                                                                                                                               | `GameEngine` (unchanged)                                            |
| UI thread's runnable                | `UiLoop`                                                                                                                                                                | the EDT (Swing's own thread, never blocks on a channel)             |
| The only package both halves import | `channel` (transport + shared vocabulary)                                                                                                                               | `backend` — IO only from stage 0 on, and UI-invisible               |
| Shared vocabulary types             | `channel.colour`, `channel.strings`, `channel.utils`, `channel.directories`, `channel.globals`, `StartupOptions`, plus the three meaning-enums and their payload shapes | `middle.numerics` etc. (core-only, moved out of `backend`)          |
| A map position on the wire          | `Grid(int y, int x)` in `channel`                                                                                                                                       | `middle.cave.Loc` — same idea plus movement and RNG, stays put      |

**The test for whether a `middle` type belongs in `channel`:** is it a word the messages are *written in* — something
the UI must understand to render at all? `ColourEnum` and `MessageType` pass it. `Loc` and `Stats` do not, and their
shapes flatten instead. See "Which shapes can actually move".

**But that test is not the only door, and stage 5 found the second one** *(2026-08-13)*. `AngbandDirs` and `Angband`
moved into `channel` during the stage 5 cleanup, and neither is a word any message is written in — no `CoreMessage` will
ever carry a directory or a version string. They qualify under the *other* half of what stage 0 made `channel` mean:
the shared vocabulary both halves must agree on. C agrees on both counts. `ANGBAND_DIR_*` are declared in `init.h` and
read from both sides — `ui-display.c` builds `ANGBAND_DIR_SCREENS/news.txt` for the splash and `sound-core.c` builds
`ANGBAND_DIR_SOUNDS/…`, while `main.c` writes them from `-d`; the front end is not reaching for something it should not
have. Two alternatives were weighed and rejected: passing the two paths the front end needs at construction (rejected
because `Main` enumerates the whole table for both the `-d` validity check and the usage message, and C keeps one table
for the same reason), and splitting the enum by half (the constants do divide suspiciously cleanly, but C's
`change_path_values[]` is one table whose third column is a setgid privilege flag, not a UI/core split — so the tidiness
is coincidence).

*Recorded because it is a counter:* these are the second and third types admitted on shared-vocabulary grounds rather
than message-vocabulary grounds, and the package name reads narrower than its contents by exactly that much. Stage 0's
note already sketched the alternative — `uk.co.jackoftrades.shared`, holding `shared.channel` for the transport beside
`shared.colour`, `shared.directories` and friends. Still not proposed. But if a fourth arrives, that is the signal to
stop deferring it.

*Retired names, so a stale note is recognisable:* `commandChannel` → `coreQueue`; `displayChannel` → `uiQueue`; the
direction-naming of channels (`uiChannel` meaning UI→core, `CoreChannel` meaning core→UI) → **ownership**-naming, so
`CoreChannel` is now the core's *pair* and the direction lives on the queue names;
`UiInboxMessage` → `ChannelMessage`; `DisplayMessage` → `CoreMessage`; `UiEvent` and `CoreCommand` → `UIMessage`;
`swingUI` and `startSwingUI` → `frontend` (the package; the *class* `SwingUI` keeps its name, which is why notes from
stages 0–4 read as though the two were the same word). Two greps in stage 0's verification note still spell the old
package: they quote commands actually run on 2026-08-10 and are left verbatim, since rewriting them would make the
record false. (The
root is `ChannelMessage`, not `Message`, because `middle.Message` — C's `message.c` — has the better claim on that name
and five callers already.)

## 6. Primer menu

Page-length, on demand, tied to the code in front of us at the time — ask when a stage reaches the topic, not before.
Written ones live in `docs/primers/` and are marked ✅ with their filename. **Cite a primer by its filename, never by a
number:** the list is ordered by the stage that reaches each topic, so inserting one renumbers the rest, and a numbered
citation elsewhere silently starts pointing at the wrong page. *(The numbers were removed on 2026-08-13, having done
exactly that once already.)*

- **CSP in one page** — processes, channels, why "share by communicating" beats "communicate by sharing", and where this
  design is CSP-flavoured rather than CSP-strict (stage 1).
- **Records + sealed interfaces as a wire protocol** — exhaustive `switch`, why the compiler becomes the protocol
  checker (stage 1).
- **`BlockingQueue` mechanics** — `put`/`take` vs `offer`/`poll`, interruption, memory-visibility guarantees you get for
  free (stage 1–2). ✅ `blocking-queue.md`
- **Producer–consumer as a pattern** — the shape rather than the mechanism: what you buy by having neither thread name
  the other, the consumer loop's four decisions, why there is no back-pressure here (stage 2). ✅
  `producer-consumer.md`
- **The EDT, properly** — what runs on it, `invokeLater` vs `invokeAndWait`, why `take()` on the EDT freezes the app
  (stage 2). ✅ `edt-and-invokelater.md`
- **Thread lifecycle & shutdown** — non-daemon threads, `join`, why `System.exit` was hiding a race, poison pills (stage
  3–4). ✅ `thread-lifecycle-and-shutdown.md` — also covers `UncaughtExceptionHandler` and the crash path the handshake
  does not reach, found while verifying stage 4.
- **Request–response over a one-way pair of channels** — why `get_check` is not an `event_signal`, the shared-inbox
  matching problem, and four ways out (correlation id, dedicated reply channel, rendezvous-in-the-message, re-issued
  command) (Chapter 3, the `GameInput` boundary). ✅ `request-response.md`
- **How C's front end is actually layered** — `game-event.c` broadcasting upward vs `ui-term.h`'s hooks drawing
  downward, why `ui-*.c` sits between them, and what that says about which of the two a channel should carry (stage 1,
  and again at stage 5 when the handlers split).

---

*Verdict log — filled in as stages complete, one honest paragraph each:*

- *(stage 0)* Lots of architecture work, but a lot of understanding came out of it. Problem was that after I had done
  all the work, I felt drained and not energised, which is normally how I feel coming out of a coding session.
  Worthwhile but worth spreading out between coding runs.

- *(stage 1)* A lot of confusion and turns dealing with that confusion, but a good outcome. More understanding built,
  and this time some coding was included, so a mixture of drained and energised. Slight (oh, nothing happens when you
  run it) disappointment in that I have gone from a running model through to a model where the guts are correct, but not
  wired in. Need to handle the lack of success and dopamine hit with a "next stage will be better" (and I hope it will
  be.)
