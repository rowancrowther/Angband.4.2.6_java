# Migrating to the two-channel architecture

The route from the current wiring to the design in `Architecture.md`, in stages that each end with a game that still
runs and something you can see working. Written to be walked one checkbox at a time, port-loop style: you write, Claude
verifies and tests, primers on demand.

---

## 1. Where we are, where we're going

**Now** (per `Java_map.md`): `main()` → EDT → `Frontend` → creates `GameRunner` → starts the game thread. The core
reaches the UI through three static holders (`StatusDisplayHolder`,
`CommandGetterHolder`, `GameInputHolder`); the UI reaches the core through the `GameRunner` it holds (`start()` /
`requestStop()`). Shutdown is `Frontend.closeDown()` calling `System.exit(0)`
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
      ├─▶ core thread   ──── displayChannel ───▶ UI thread ──invokeLater──▶ EDT
      │                 ◀─── commandChannel ────     ▲                       │
      │                                              └── raw Swing events ──┘
```

The display channel is the UI thread's single inbox: the core's messages and the EDT's raw events both arrive on it, and
only the UI thread sends on the command channel (see review note 1).

**Vocabulary decision, made now so nothing has to be renamed twice.** The transport queues are called **channels** —
`commandChannel` (UI→core) and `displayChannel` (core→UI). "Channel" is Hoare's own CSP term, and it keeps clear of
`CommandQueue`, which already exists as the port of C's
`cmdq` (`cmd-core.c`) and is a *core-internal* structure that stays exactly where it is. If the two ever sit in one
sentence: the *channel* carries messages between threads; the *queue* holds game commands awaiting dispatch inside the
core.

## 2. Review notes on Architecture.md (read before stage 1)

Points where the document and reality need to shake hands. None of them break the design.

1. **Three threads, not two.** Swing owns the EDT regardless. The workable shape: the "UI thread"
   of the document blocks on `displayChannel.take()`, and the display channel is its *single inbox* — core messages and
   raw Swing events both land on it. Swing listeners (which run on the EDT) never touch the command channel: they wrap
   the raw event as a message, put it on the display channel, and the UI thread parses it and sends any resulting
   `CoreCommand` down the command channel itself. *(Corrected 2026-08-10 — the first draft had EDT listeners sending
   commands directly, which Rowan rejected.)* Three reasons the corrected routing is right, in rising order of weight:
   interpretation ("which command does this event mean?") is stateful UI logic — keymaps, whatever prompt is up — that
   belongs in one loop, not scattered across listeners; a thread can only block on one queue, so merging both sources
   into one inbox is what makes the document's "waits for a) events and also b) items — either or both" implementable at
   all; and every command-channel send now happens on one thread, so command ordering is deterministic. The EDT stays a
   private implementation detail of the UI half; the core never knows it exists. *Consequence for Architecture.md's
   Principles:* "the UI should never send on the core-to-UI queue" is now violated by design — the EDT is a UI-side
   producer onto the display channel. The invariant that survives is about *receiving*, not sending: only the UI thread
   receives on the display channel, only the core receives on the command channel, and the core sends only
   `DisplayMessage`s — all three enforceable by stage 1's types.
2. **"JWT"** in the document is a typo, and means AWT/Swing window events.
3. **Birth is interactive, and that's the hidden hard part.** "Receives and processes birth related commands" implies
   the core *asking* the UI things and waiting for answers — C's synchronous
   `get_command`, currently stubbed as `CommandGetterHolder` / `GameInputHolder`. Under CSP that becomes a
   request/response protocol over the two channels. Nothing calls those seams yet, so this migration doesn't need to
   solve it — but it is the natural **design set piece for Chapter 3**, and stage 5 leaves a marker for it rather than
   pretending it's done.
4. **Step 4 of Startup ("waits for both threads") is what makes save-on-quit possible.** Today
   `closeDown()` requests a stop and immediately `System.exit(0)`s — the core dies mid-stride. In the new shape the core
   finishes its `saveAndStop` work, *then* sends `stopped`, *then* exits, and `main()` outlives it. This is a
   correctness gain, not just tidiness.
5. **Unbounded channels, for now.** `LinkedBlockingQueue` without a capacity. Backpressure (a core producing display
   messages faster than the UI drains them) is a real topic but not a today problem; a bounded channel would add a way
   for the core to block on the UI, which is exactly the coupling being removed. Revisit if the map redraw traffic in
   Chapter 5 ever measures slow.
6. **An unexpected gain: the event-ordering constraint dissolves.** Today `InitHandlers` must be registered before
   `loadGameConstants()` or `EVENT_ENTER_INIT` is lost — the Javadoc calls the timing load-bearing, and
   `EnterInitWiringTest` pins it. Channels *buffer*: a message sent before the UI is ready simply waits. The whole class
   of "was anyone listening yet?" bugs goes away for cross-thread traffic. (The bus keeps the constraint for its
   remaining *core-internal*
   listeners, but the UI is no longer one of them.)

## 3. What stays untouched

Worth saying out loud, because it's most of the codebase: every parser, grammar, reader, registry and loader;
`GameConstants.init()`; the rune system; `CommandQueue`/`CommandProcessor`; and the **event bus** (`EventsBusHandler`).
The bus is the port of `game-event.c` and becomes a purely core-internal broadcast mechanism — exactly what it is in C.
Only the *last hop* changes: where a handler today calls through `StatusDisplayHolder` into Swing, it will put a message
on the display channel instead.

---

## 4. The route

Five stages. Each is a chapter-sized chunk of checkbox items at roughly function granularity; each ends with the game
running and something observable. Claude's half throughout: tests for every new class, verification that the visible
behaviour (splash screen, progress notes, clean shutdown)
survives each stage.

### Stage 1 — The messages and the channels *(no behaviour change)*

Define the protocol before any wiring moves. New package, suggested name
`uk.co.jackoftrades.channel` — owned by neither half; both may import it (alongside `backend`, which is already the
shared layer — `Frontend` legitimately imports `backend.colour` and
`backend.strings` today).

- [ ] `UiInboxMessage` — the display channel's element type, a sealed interface with exactly two branches, matching the
  two producers (review note 1):
  - `DisplayMessage` — sealed; one record per thing the core can tell the UI. Today's traffic needs exactly four:
  `ShowSplashScreen()`, `LoadNote(String text)`, `BirthNote(String
        text)`, `Stopped()`. (These mirror the three `StatusDisplay` methods plus the shutdown handshake — no invention
  required.)
  - `UiEvent` — sealed; one record per raw Swing event the EDT forwards to the UI thread. One member for now:
  `WindowCloseRequested()`. (Keystrokes join it in Chapter 5.)
- [ ] `CoreCommand` — a sealed interface; one record per thing the UI can tell the core:
  `Start()`, `SaveAndStop()`.
- [ ] `Channels` — one small class/record holding the two typed `LinkedBlockingQueue`s (`UiInboxMessage` and
  `CoreCommand`), created in one place and handed to both halves. Consider thin wrapper views instead of raw queues so
  the receive invariants are compiler-enforced: the core gets a *send-only, `DisplayMessage`-only* view of the display
  channel and a *receive-only* view of the command channel — then the core cannot forge a
  `UiEvent`, receive on the wrong channel, or send a command, by type alone. A worthwhile design conversation, not a
  requirement.
- [ ] Claude: tests — message equality, channel round-trip across two real threads.

*Primer candidates:* sealed interfaces + records as a message protocol (you've met sealed with
`RuneVariety`; the new bit is records-as-messages); `BlockingQueue` semantics (`put`/`take`/
`offer`/`poll`, and why `take` blocking is a feature).

**Done when:** it compiles, tests pass, and nothing else has changed.

### Stage 2 — Core→UI traffic crosses the display channel

The pivot of the whole migration, and it's small because `StatusDisplay` is already the perfect seam: swap *which
implementation* is registered, and the core doesn't notice.

- [ ] `ChannelStatusDisplay implements StatusDisplay` (core side): each method wraps its arguments in the matching
  `DisplayMessage` and `put`s it on the display channel. Three one-line methods.
- [ ] `UiLoop` (UI side, a `Runnable`): loop on `displayChannel.take()`, switch over the sealed
  `UiInboxMessage` — for each `DisplayMessage`, `invokeLater` the painting the old
  `SplashScreen` methods do now (`SplashScreen` itself keeps its painting code; it just stops being the thing the *core*
  calls); `UiEvent` handling arrives in stage 3.
- [ ] `Frontend.init`: build the channels (temporarily — they move to `main()` in stage 4), register
  `ChannelStatusDisplay` in the holder instead of `SplashScreen`, start the consumer on its own thread
  (`new Thread(consumer, "angband-display")`).
- [ ] Claude: tests — a fake channel proves each `StatusDisplay` call becomes the right message; consumer tests prove
  each message reaches the right painting call.

*Primer candidates:* producer–consumer as a pattern; the EDT and `invokeLater` (why painting must hop, why `take()` must
not run on the EDT). Also fixes in passing: `SplashScreen`'s own Javadoc admits it touches Swing from the game thread
today — after this stage every touch arrives via the consumer's `invokeLater`, closing that hole.

**Done when:** the splash screen and the "Initializing arrays…" notes still appear — but a breakpoint (or a logging
wrapper) shows every one of them crossed the display channel.

### Stage 3 — UI→Core traffic crosses the command channel; clean shutdown

The `sleep(5)` placeholder in `GameRunner.gameLoop()` becomes a real receive loop, and the kill-the-thread shutdown
becomes the document's handshake.

- [ ] `gameLoop()`: after `loadGameConstants()`, loop on `commandChannel.take()`; switch over
  `CoreCommand`. `Start` → log it (birth lands here in Chapter 3). `SaveAndStop` → send
  `Stopped()` on the display channel and fall out of the loop; the thread ends. (Nothing to save yet — the save half
  arrives with Chapter 8.)
- [ ] `Frontend.init`: once the window is up, `put` a `Start()` — the document's step 1.5.
- [ ] `windowClosing`: put a `WindowCloseRequested()` on the *display* channel — the EDT forwards the raw event, nothing
  more (review note 1). No `requestStop`, no `System.exit`.
- [ ] `UiLoop`, on `WindowCloseRequested()`: send `SaveAndStop()` on the command channel — the one place raw events are
  turned into core commands.
- [ ] `UiLoop`, on `Stopped()`: `invokeLater` the window disposal (the old `closeDown`
  body minus `exit` and minus `requestStop`), then fall out of its own loop; the thread ends.
- [ ] Delete the `requestStop()`/interrupt path — the `RuntimeException`-on-interrupt wart in
  `gameLoop` (its own Javadoc already flags it) goes with it.
- [ ] Claude: tests — the handshake in both orders; verify the process exits with no
  `System.exit` anywhere on the path (all windows disposed → EDT ends → JVM ends).

*Primer candidates:* thread lifecycle (why the JVM exits when the last non-daemon thread does, and what keeps the EDT
alive); the poison-pill / sentinel-message pattern (`Stopped` is one).

**Done when:** the game starts, the splash shows, and closing the window shuts everything down cleanly — verifiable with
a thread dump (`jstack`) before and after, and by the exit code.

### Stage 4 — Ownership moves to `main()`

The structure finally matches the document's Startup section. Mostly *moving* code, not writing it.

- [ ] `Main.main()`: create the `Channels`; create and start the core thread and the UI thread;
  `join()` both; return. (The `-l`/usage windows stay exactly as they are — they run before either thread exists, as
  `Java_map.md` already notes.)
- [ ] Core side: `GameRunner` absorbs its last responsibilities and becomes the core's `Runnable`
  (suggested rename: `Core`) — constructed with the channels and the startup options, no longer constructed *by the UI*.
- [ ] UI side: a `Runnable` (inside `Frontend` or wrapping it) that does the Swing bootstrap on the EDT (`invokeLater`
  from the UI thread) and then runs the `UiLoop` as its own body — one thread, matching the document's UI sequencing.
- [ ] `Frontend` loses its `gameRunner` field entirely. After this stage the UI's compile-time view of the core is:
  nothing. The channel package is the whole interface.
- [ ] `StartupOptions`: both halves get a copy (document's steps 2–3); the static field in `Main`
  and its happens-before Javadoc essay can go — arguments become plain constructor parameters.
- [ ] Claude: verification — full startup/shutdown pass; thread dump naming check (`angband-core`, `angband-ui`, EDT);
  confirm `main` really does outlive both.

*Primer candidate:* `Thread.join()` and structured teardown — why "main waits" is what makes
"save before exit" a guarantee rather than a race.

**Done when:** `Java_map.md`'s startup narrative can be rewritten to match `Architecture.md`'s Startup section line for
line — because they now describe the same program.

### Stage 5 — Seal the boundary and take stock

- [ ] Retire `StatusDisplayHolder` + `DefaultStatusDisplay`: with the channel in place,
  `InitHandlers`' bus handlers can put messages on the display channel directly (handed the channel, not reaching a
  static). The `StatusDisplay` interface either retires with it or survives UI-side as the consumer's painting seam —
  Rowan's call; either is defensible.
- [ ] **The boundary test** (Claude writes, both maintain): a test that walks `src/main` imports and fails if
  `frontend.**` imports anything from `middle.**`, or `middle.**` anything from
  `frontend.**`; `backend.**` and `channel.**` are the shared allowlist. This turns
  `Java_map.md`'s "you can check by reading the import statements" from a habit into a regression test.
- [ ] Docs pass: `Java_map.md` rewritten; `big_map.md`'s ending checked (it already points this direction); a verdict
  paragraph recorded here, set-piece style — what it cost, what it paid.
- [ ] **Marker for Chapter 3:** the input seams (`CommandGetterHolder`, `GameInputHolder`,
  `DefaultCommandGetter`) are still holder-shaped and still uncalled. Their CSP form — the core sends a *request*
  display message and blocks on the command channel for the reply — is the birth chapter's design set piece. Do not
  migrate them speculatively now; migrate them when birth gives them their first real caller.

**Done when:** the boundary test is green and would have been red at every stage before 4.

---

## 5. Naming table (for consistency as you go)

| Concept                              | Name                                                     | Not to be confused with                                    |
|--------------------------------------|----------------------------------------------------------|------------------------------------------------------------|
| UI→core transport                    | `commandChannel`                                         | `CommandQueue` (core-internal `cmdq` port — unchanged)     |
| UI thread's inbox (core→UI + EDT→UI) | `displayChannel`                                         | the event bus (core-internal — unchanged)                  |
| Display channel element type         | `UiInboxMessage` (sealed: `DisplayMessage` \| `UiEvent`) | —                                                          |
| Core→UI message type                 | `DisplayMessage` (sealed)                                | `GameEventData` (bus payloads — unchanged)                 |
| EDT→UI-thread raw event              | `UiEvent` (sealed)                                       | AWT's own `WindowEvent`/`KeyEvent` (never cross a channel) |
| UI→core message type                 | `CoreCommand` (sealed)                                   | `Command`/`CommandCode` (game commands in the `cmdq`)      |
| Core thread's runnable               | `Core` (was `GameRunner`)                                | `GameEngine` (unchanged)                                   |
| UI thread's runnable                 | `UiLoop`                                                 | the EDT (Swing's own thread, never blocks on a channel)    |

## 6. Primer menu

Page-length, on demand, tied to the code in front of us at the time — ask when a stage reaches the topic, not before:

1. **CSP in one page** — processes, channels, why "share by communicating" beats "communicate by sharing", and where
   this design is CSP-flavoured rather than CSP-strict (stage 1).
2. **Records + sealed interfaces as a wire protocol** — exhaustive `switch`, why the compiler becomes the protocol
   checker (stage 1).
3. **`BlockingQueue` mechanics** — `put`/`take` vs `offer`/`poll`, interruption, memory-visibility guarantees you get
   for free (stage 1–2).
4. **The EDT, properly** — what runs on it, `invokeLater` vs `invokeAndWait`, why `take()` on the EDT freezes the app
   (stage 2).
5. **Thread lifecycle & shutdown** — non-daemon threads, `join`, why `System.exit` was hiding a race, poison pills
   (stage 3–4).

---

*Verdict log — filled in as stages complete, one honest paragraph each:*

- *(stage 1: …)*
