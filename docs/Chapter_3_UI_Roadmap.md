# Chapter 3 roadmap — the UI side of birth

C: `ui-birth.c` (1810 lines, 25 functions), with `ui-player.c` (1327, 30) and `ui-menu.c` behind it. Scaffold audited
against the code 2026-09-05.

The companion to `Chapter_3_Roadmap.md`, which is the **core** side of birth — `player-birth.c`'s
`player_generate`, the point-buy arithmetic, the 13 command handlers. This file is the other half:
the screens, the stage machine, and the keystrokes that push those commands.

**Only the display half is Chapter 3.** The scoping decision below splits this file in two, and the work order is in two
parts to match: Part 1 is what Chapter 3 builds, Part 2 is what waits for Chapter 5's keyboard. Everything above the
work order describes the whole subsystem, because the display half cannot be scoped sensibly without knowing what the
input half will ask of it.

**This is not the map.** The core roadmap's stage 0 map covers the player subsystem. What this records is the
pre-existing state of the front end plus the C shape it has to reproduce, so that a UI map — if one is written — starts
from an inventory rather than a blank page.

## What the UI side bounds

C's `ui-birth.c` has exactly one entry point into the game loop: `textui_do_birth`
(`[C] ui-birth.c:1617`), called when the command getter is asked for a command in `CTX_BIRTH`. It does not return until
the character is accepted. Everything in this file is reached from inside that call, or from the two event handlers
registered alongside it.

That gives a clean boundary. **In scope:** the stage machine, three menus, two stat screens, three prompts, and the
input handling for each. **Out of scope:** anything the birth screen merely calls — the birth options menu
(`do_cmd_options_birth`, `[C] ui-options.c`), the help browser (`do_cmd_help`), and the savefile-name machinery.

## Scoping decision taken (260905)

**Chapter 3 takes the display half of `ui-birth.c` only; the input half defers to Chapter 5.**

The line falls where `menu_select` and `inkey` do. Chapter 3 builds everything that paints — the screen primitives, the
character sheet, the menus as drawn objects, the three point-buy event handlers. Chapter 5 builds everything that reads
a key — the stage machine, `menu_question`,
`roller_command`, `point_based_command`, the three prompts, quickstart, and the random finish.

Three reasons it falls there rather than anywhere else:

1. **Keyboard input was already Chapter 5's.** `UILoop`'s own Javadoc says so (`UILoop.java:72`), and the
   `inputfromuser` package is named for a job it does not yet do. Pulling it forward to satisfy birth would move the
   largest piece of Chapter 5 into Chapter 3 to serve one screen.
2. **The display half is driven entirely by events.** Every screen below is repainted in response to something the core
   sent — `EVENT_ENTER_BIRTH`, `EVENT_BIRTHPOINTS`, `EVENT_STATS`,
   `EVENT_GOLD`. That makes the whole of Part 1 exercisable by sending messages, with no input, no stage machine and no
   live game. It is the best-tested UI work available this chapter.
3. **The design set piece goes with the keys.** "Who owns the birth loop under two threads" is a question about a loop
   that reads input and pushes commands. Nothing in the display half asks it, so Chapter 3 does not have to answer it.

**What this costs, stated plainly.** Chapter 3 ends with a character whose numbers are correct and screens that can draw
that character — but nothing that walks a player through the stages, because nothing can press a key. The chapter's
stated goal, "roll a character; see their stats", is met by the sheet, not by the birth flow. This is the same shape as
the core roadmap's decision 3, where
`calc_bonuses`' equipment loop is written but unexercisable until Chapter 7: the content is transcription, and splitting
it later is worse than writing it now.

**How Part 1 gets exercised.** Tests that send the events and assert on the resulting grid are the primary answer, and
they cover everything except the sheet's layout. A throwaway harness that paints one screen and holds it is the
secondary one — worth building, worth not keeping.

## The stage machine

`enum birth_stage` (`[C] ui-birth.c:60`) has eleven values, and their **numeric order is load-bearing** —
`menu_question` advances with `current + 1` and `current + 2`
(`[C] ui-birth.c:813, 826, 831`), and `textui_do_birth` steps back with `current_stage - 1`
(`[C] ui-birth.c:1680`). A Java enum in the same order preserves both; an enum reordered for readability silently breaks
the roller fork.

All of it is Part 2. It is described here because Part 1's screens are the things it moves between, and because which
screen paints when is decided by `prev`, which lives in this loop.

| #  | Stage                  | Reached from                       | Screen                                   |
|----|------------------------|------------------------------------|------------------------------------------|
| -1 | `BIRTH_BACK`           | any stage's ESC                    | *(not a stage — a signal)*               |
| 0  | `BIRTH_RESET`          | start; `'S'` at final confirm      | *(none — pushes a command and moves on)* |
| 1  | `BIRTH_QUICKSTART`     | reset, if allowed                  | previous character, prompt               |
| 2  | `BIRTH_RACE_CHOICE`    | reset; quickstart `'N'`            | race menu                                |
| 3  | `BIRTH_CLASS_CHOICE`   | race chosen                        | race + class menus                       |
| 4  | `BIRTH_ROLLER_CHOICE`  | class chosen                       | race + class + roller menus              |
| 5  | `BIRTH_POINTBASED`     | roller choice, cursor 0            | stat sheet + cost column                 |
| 6  | `BIRTH_ROLLER`         | roller choice, cursor 1            | full character sheet                     |
| 7  | `BIRTH_NAME_CHOICE`    | either roller accepted             | character sheet + name prompt            |
| 8  | `BIRTH_HISTORY_CHOICE` | name accepted                      | character sheet + history editor         |
| 9  | `BIRTH_FINAL_CONFIRM`  | history accepted; `'@'` anywhere   | character sheet + prompt                 |
| 10 | `BIRTH_COMPLETE`       | confirm accepted; quickstart `'Y'` | *(loop ends)*                            |

```
                    ┌──────────────┐
   ┌───────────────▶│ BIRTH_RESET  │◀──────────── 'S' at final confirm
   │                └──────┬───────┘
   │        quickstart_allowed?
   │         ┌─────yes─────┴─────no─────┐
   │         ▼                          │
   │  ┌─────────────┐  'N'              │
   │  │ QUICKSTART  │──────────────────▶│
   │  └──┬───────┬──┘                   ▼
   │  'Y'│    'C'│              ┌────────────────┐
   │     │       │              │  RACE_CHOICE   │
   │     │       │              └───────┬────────┘
   │     │       │                 ESC ↑│↓ select
   │     │       │              ┌───────┴────────┐
   │     │       │              │  CLASS_CHOICE  │
   │     │       │              └───────┬────────┘
   │     │       │                 ESC ↑│↓ select
   │     │       │              ┌───────┴────────┐
   │     │       │              │ ROLLER_CHOICE  │
   │     │       │              └──┬──────────┬──┘
   │     │       │        cursor 0 │          │ cursor 1
   │     │       │       (+1)      ▼          ▼   (+2)
   │     │       │        ┌─────────────┐  ┌──────────┐
   │     │       │        │ POINTBASED  │  │  ROLLER  │
   │     │       │        └──────┬──────┘  └────┬─────┘
   │     │       │          Enter │              │ Enter
   │     │       │               └──────┬───────┘
   │     │       │                      ▼
   │     │       └──────────────▶┌─────────────┐
   │     │                       │ NAME_CHOICE │◀── ESC returns to `roller`
   │     │                       └──────┬──────┘    (whichever was used)
   │     │                              ▼
   │     │                       ┌──────────────┐
   │     │                       │   HISTORY    │
   │     │                       └──────┬───────┘
   │     │                              ▼
   │     │                       ┌───────────────┐    '@' from any menu
   │     │                       │ FINAL_CONFIRM │◀───── jumps here
   │     │                       └──────┬────────┘
   │     ▼                              ▼
   │  ┌────────────────────────────────────┐
   └──│           BIRTH_COMPLETE           │
      └────────────────────────────────────┘
```

Three things the diagram cannot show, and each is a bug waiting to happen:

- **`roller` is a fourth variable, not a stage.** `textui_do_birth` keeps `current_stage`, `prev`,
  `next` *and* `roller` (`[C] ui-birth.c:1619–1622`). `roller` remembers which of the two stat screens was used, purely
  so that ESC at the name prompt returns to the right one (`[C] ui-birth.c:1731`). Nothing else reads it.
- **`prev` decides whether a screen is redrawn.** Each of the last three stages repaints the character sheet only
  `if (prev < CURRENT)` (`[C] ui-birth.c:1726, 1738, 1750`) — that is, only when arriving forwards. Coming back from a
  later stage leaves the sheet already on screen.
  `BIRTH_POINTBASED` inverts the test (`prev > BIRTH_POINTBASED`, `:1693`) because the point-buy screen *is* rebuilt
  when re-entered from the name prompt.
- **The `+1` step passes through `BIRTH_QUICKSTART`.** Stepping back from `BIRTH_RACE_CHOICE` gives
  `BIRTH_QUICKSTART`, which is wrong unless the character is reset first — hence the guard at
  `[C] ui-birth.c:1683` rewriting that to `BIRTH_RESET`.

## The two paths through the stats

Both branch off `BIRTH_ROLLER_CHOICE`, and the fork is made in `menu_question`
(`[C] ui-birth.c:809–827`) rather than in the main loop, which is the one place the roller menu is treated differently
from the race and class menus.

The **key tables below are Part 2**; the **screens they act on are Part 1**. That is the split in miniature: Chapter 3
draws the cost column, Chapter 5 makes `+` change it.

### Point-based (`roller_menu` cursor 0)

The screen is a live cost sheet: the stats down the left, a **Cost** column at `COSTS_COL`, and a running
`Total Cost: n/m` line (`[C] ui-birth.c:1066–1083`). Arrow keys move a cursor between stats; left/right (or `-`/`+`)
sell and buy.

| Key     | Command pushed                         | C                          |
|---------|----------------------------------------|----------------------------|
| ↑ / ↓   | *(none — moves the stat cursor)*       | `[C] ui-birth.c:1182–1192` |
| → / `+` | `CMD_BUY_STAT` + choice = stat         | `:1260`                    |
| ← / `-` | `CMD_SELL_STAT` + choice = stat        | `:1254`                    |
| `r`     | `CMD_RESET_STATS` + choice = **false** | `:1271`                    |
| Enter   | *(none)* → `BIRTH_NAME_CHOICE`         | `:1276`                    |
| ESC     | *(none)* → `BIRTH_ROLLER_CHOICE`       | `:1266`                    |

Two details that are easy to lose:

- **Entering the stage pushes `CMD_RESET_STATS` with choice = `true`** (`[C] ui-birth.c:824`), and the comment there
  calls it a hack in as many words. The `true` means "this is the initial buy" — it forces a re-buy so the point totals
  are current before the screen is first painted. Re-entering from the name prompt pushes `CMD_REFRESH_STATS` instead
  (`:1699`), which redraws without resetting.
- **`buysell[]` is UI state derived from the core's payload.** `point_based_points`
  (`[C] ui-birth.c:1069–1076`) reads `EVENT_BIRTHPOINTS` and records, per stat, whether it can be bought (cost ≤
  remaining) or sold (already spent). Only the mouse context menu reads it (`:1221–1230`) — the keyboard path pushes the
  command and lets the core refuse. Since mouse handling is out of scope entirely, **nothing in either part reads
  `buysell[]`**; it is listed in Part 1 as a decision, not as work.

### Standard roller (`roller_menu` cursor 1)

No cursor, no cost column: the whole character sheet, and a three-key prompt.

| Key         | Command pushed                                     | C                    |
|-------------|----------------------------------------------------|----------------------|
| `r` / space | `CMD_ROLL_STATS`, sets `prev_roll = true`          | `[C] ui-birth.c:985` |
| `p`         | `CMD_PREV_STATS` *(only offered once `prev_roll`)* | `:991`               |
| Enter       | *(none)* → `BIRTH_NAME_CHOICE`                     | `:996`               |
| ESC         | *(none)* → `BIRTH_ROLLER_CHOICE`                   | `:980`               |

`prev_roll` is a `static` local (`[C] ui-birth.c:887`) reset by the `first_call` argument, which the main loop computes
as `prev < BIRTH_ROLLER` (`:1717`). It exists only to decide whether `'p'` is live and whether the prompt mentions it —
the previous roll itself lives core-side, saved by
`save_roller_data`. A first entry to the roller pushes `CMD_ROLL_STATS` from `menu_question`
(`:812`), so the screen never paints an unrolled character.

**The roller has no display half of its own.** Its whole screen is `display_player(0)` plus a one-line prompt
(`[C] ui-birth.c:890, 902`), both of which Part 1 builds for other stages. Part 2 adds only the key handling — which is
why there is no D-stage for it below.

### Quickstart

Only offered when `EVENT_ENTER_BIRTH` arrives with its flag set — C stashes it in the
`quickstart_allowed` file-static (`[C] ui-birth.c:1788`). Four keys: `Y` accepts as-is (`CMD_ACCEPT_CHARACTER`, straight
to complete), `N` rerolls from scratch (`CMD_BIRTH_RESET`, to race choice), `C` jumps to the name prompt keeping the
stats, `=` opens birth options.

The flag's journey across the channel is Part 1 (D1); everything it gates is Part 2.

### `'@'` — finish with random choices

Available from any of the three menus, and it is not a stage transition so much as a batch. C's
`finish_with_random_choices` (`[C] ui-birth.c:659`) builds up to four commands — race, class, name, history — for
whichever choices are not yet made, then **pushes them in reverse order** (`:761`)
because the queue executes last-pushed-first. It jumps straight to `BIRTH_FINAL_CONFIRM`, skipping both stat screens
entirely: the character keeps whatever the default point buy gave it.

## What the core sends back

Five events reach the birth screen, and only two of them are permanent subscriptions. **All five are Part 1** —
receiving an event and painting from it is the display half's entire job.

| Event               | Payload                     | Handler                                     | Lifetime                 |
|---------------------|-----------------------------|---------------------------------------------|--------------------------|
| `EVENT_ENTER_BIRTH` | `flag` — quickstart allowed | `ui_enter_birthscreen` → `setup_menus`      | registered at start-up   |
| `EVENT_LEAVE_BIRTH` | —                           | `ui_leave_birthscreen` → `free_birth_menus` | registered at start-up   |
| `EVENT_BIRTHPOINTS` | spent / inc / remaining     | `point_based_points`                        | **point-buy stage only** |
| `EVENT_STATS`       | —                           | `point_based_stats` → repaint stats         | **point-buy stage only** |
| `EVENT_GOLD`        | —                           | `point_based_misc` → repaint extras         | **point-buy stage only** |

The bottom three are added by `point_based_start` (`[C] ui-birth.c:1106–1108`) and removed by
`point_based_stop` (`:1113–1115`). That scoping is deliberate and worth keeping: outside the point-buy screen,
`EVENT_STATS` means something the sidebar handles, and leaving the birth handler subscribed would repaint a birth screen
over the game.

**The subscribe/unsubscribe pair straddles the split, and Part 1 owns it.** C's `point_based_start`
and `point_based_stop` are called from the stage machine (`[C] ui-birth.c:1694, 1709`), which is Part 2 — but what they
do is manage subscriptions and paint, which is Part 1. Build them in Part 1 with no caller; Part 2 supplies one.

The core also signals `EVENT_AC` and `EVENT_HP` from the same places (`[C] player-birth.c:705–706, 1183–1184`), which
the birth screen ignores — it repaints the whole sheet off `EVENT_STATS` instead. Not a gap to fill.

**`EventDataBirthStage` ports a dead struct.** The `birthstage` member of C's `game_event_data`
union is declared at `[C] game-event.h:130` and **never written or read anywhere in 4.2.6** — a grep over the whole of
`src/` finds that one line and nothing else. `EventDataBirthStage.java`
faithfully reproduces it. Nothing in this roadmap needs it, and no stage below signals it; recording it here so its
absence from the work order is not read as an oversight.

## Already in place (verified 2026-09-05)

### The front end — a character grid and nothing above it

`frontend/` is 2,366 lines across 25 files. `Term`, `TermData`, `TermWin`, `Window` and `SwingUI`
give a grid of coloured characters with `put(row, col, char/String, ColourEnum)`
(`SwingUI.java:512, 531`) and `Window.display(AngbandDisplayCharacter[][])` (`Window.java:114`).
`UILoop.loop()` (`UILoop.java:177`) drains the inbox and dispatches on message type.

That is the whole of it. **There is no port of `ui-menu.c`, `ui-player.c`, `ui-input.c` or
`ui-output.c`** — no menu, no `display_player`, no `inkey`, no `text_out`/textblock. Every screen in this roadmap is
built on primitives that do not exist yet, which is why Part 1 is mostly infrastructure: three of its six stages would
be needed by any screen the game ever draws.

### No keyboard input at all

A grep for `KeyListener`, `KeyEvent`, `keyPressed`, `KeyAdapter` and `InputMap` across `frontend/`
returns nothing. `UILoop`'s own Javadoc says so explicitly — the `inputfromuser` package was named for a job it does not
yet do, and its comment at `UILoop.java:72` puts keypresses in Chapter 5. **This is the fact the scoping decision above
is built on.**

### `TextUIHook` — the input boundary, all stubs

`TextUIHook.java` (166 lines) is the port of C's `get_*_hook` indirection: seven methods, every one returning
`Optional.empty()` with a `TODO`. `getString` (`:144`) is the one birth needs for the name prompt. None of them is wired
to anything that could answer. All of it is Part 2.

### The two birth handlers — logging stubs on both sides

`UIBirth.java` (core side) registers `uiEnterBirthscreen` / `uiLeaveBirthscreen` on the event bus
(`UIBirth.java:96–97`); each logs and sends a bare `SimpleCoreMessage`. `BirthEvents.java` (UI side)
receives them from `UILoop` (`UILoop.java:247–248`); `enterBirth()` and `leaveBirth()` log and return. The
`quickstart_allowed` flag C carries on the payload is **not** carried — `UIBirth` sends
`SimpleCoreMessage`, which has no payload slot, and `BirthEvents.enterBirth` takes no argument.

These two are Part 1's landing sites, and the only ones that already exist.

### Commands — the codes exist, the getter does not

All 13 birth command codes are in `CommandCode.java:56–68`, and `CommandContext.CTX_BIRTH` exists.
`CommandGetter` is the interface C's `cmd_get_hook` becomes; the only implementation is
`DefaultCommandGetter` (`DefaultCommandGetter.java:40`), which returns 1 — "no command". **Nothing implements
`CommandGetter` for the UI side**, so there is no Java counterpart to `textui_get_cmd`, and therefore nowhere
`textui_do_birth` would be called from. All Part 2: the display half pushes no commands.

### Channel messages — three records, none of them event-plus-payload

`CoreMessage` is sealed over `SimpleCoreMessage`, `TextCoreMessage` and `LifecycleCoreMessage`
(`CoreMessage.java:47–82`). The `PR_*` drain proposal (`docs/implementation/260820_redraw_drain.md:157`) identified a
fourth member pairing a
`GameEventType` with a `GameEventData` as necessary and it has not landed. Birth's display half needs it too:
`EVENT_ENTER_BIRTH` carries a boolean and `EVENT_BIRTHPOINTS` carries three arrays. Without it the cost column has
nothing to paint, which is why it is D1 rather than a later stage.

`EventDataBirthPoints.java` already holds the point-buy payload as
`HashMap<Stats, Integer>` spent / inc plus an `int remaining` — a map where C has an array, which removes the `STAT_MAX`
-indexing that `buysell[]` depends on.

### Tests

`BirthEventsTest` exists. Nothing else on the UI side of birth.

---

# Part 1 — the display half *(Chapter 3)*

Six stages, none of which reads a key. Every one is exercisable by sending events at it.

## D1 — The channel's missing message shape *(no dependencies)*

- [X] The fourth `CoreMessage` record pairing a `GameEventType` with a `GameEventData` — shared with the `PR_*` drain,
  which identified the same gap (`docs/implementation/260820_redraw_drain.md:157`)
- [X] `UIBirth.uiEnterBirthscreen` (`UIBirth.java:106`) to send the quickstart flag rather than a bare
  `SimpleCoreMessage`
- [X] `BirthEvents.enterBirth` (`BirthEvents.java:71`) to take it and hold it — nothing reads it until Part 2, and that
  is fine; it is one boolean
- [X] Decide whether `EventDataBirthStage` is kept, given it ports an unused C struct

## D2 — Screen primitives *(no dependencies)*

The `ui-output.c` calls the birth screens actually make, and no more. Everything here outlives this chapter — any screen
the game draws needs the same handful.

- [X] `Term_clear`
- [X] `Term_erase`
- [ ] `Term_gotoxy`
- [ ] `put_str`
- [ ] `prt`
- [ ] `clear_from`
- [ ] `text_out`
- [ ] `text_out_e` with
- [ ] `text_out_indent` — the colour-markup writer the race and class help panels are
  written in (`[C] ui-birth.c:263, 647`)

## D3 — The character sheet *(needs D2)*

The largest single item in this roadmap and arguably its own chapter: `ui-player.c` is 1,327 lines.

- [ ] `display_player(0)` (`[C] ui-player.c`) — a **reduced form** showing the header, stats and gold is enough to
  unblock everything below, and the scoping of the rest is a decision to take when you are inside the file
- [ ] `display_player_stat_info` (`[C] ui-birth.c:1036` calls it) — the stat block on its own
- [ ] `display_player_xtra_info` (`:1047` calls it) — the extras block on its own

## D4 — The menus, as drawn objects *(needs D2)*

Only the painting half of `ui-menu.c`. `menu_select` is Part 2, and **`context_hook` is not needed at all** — it is the
mouse path, and mouse input is out of scope for both parts.

- [ ] `menu_init` / `menu_layout` / `menu_refresh` with a scroll skin (`MN_SKIN_SCROLL`) and a per-item display hook
- [ ] `birthmenu_display` (`[C] ui-birth.c:201`) — the one-line renderer, cursor colour and all
- [ ] `init_birth_menu` (`:495`) minus its `context_hook` line, and `setup_menus` (`:540`) — race, class and roller
  menus with their hints
- [ ] `free_birth_menus` (`:601`) — or its Java equivalent, which is likely to be "nothing"
- [ ] `clear_question` (`:612`) and `print_menu_instructions` (`:634`)
- [ ] `race_help` (`:240`), `class_help` (`:303`), `skill_help` (`:217`) — the side panels, which are the `browse_hook`
  's output. Painting one for a given index is Part 1; the menu calling it as the cursor moves is Part 2.

## D5 — The point-buy screen *(needs D1, D3, D4)*

Built with no caller — Part 2's `textui_do_birth` supplies one.

- [ ] `point_based_start` (`[C] ui-birth.c:1086`) — the clear, both `display_player_*` calls, the prompt line, and the
  subscribe of the three events
- [ ] `point_based_stop` (`:1111`) — the matching unsubscribe
- [ ] `point_based_points` (`:1056`) — the cost column and the `Total Cost:` line, off
  `EventDataBirthPoints`
- [ ] `point_based_stats` (`:1033`) and `point_based_misc` (`:1044`) — two one-line handlers
- [ ] Decide `buysell[]`'s fate: its only reader in C is the mouse context menu, so it can be omitted until a mouse
  exists. Record the decision either way — a silently missing array is the kind of gap Part 2 inherits without noticing.

## D6 — Enter and leave, for real *(needs D4)*

- [ ] `ui_enter_birthscreen` (`[C] ui-birth.c:1784`) → `setup_menus`, replacing the log line at
  `UIBirth.java:107`
- [ ] `ui_leave_birthscreen` (`:1794`) → `free_birth_menus`, and the split `BirthEvents.java:81`
  already flags: naming the savefile is core work C happens to do from a UI file

## D7 — Tests *(alongside D1–D6)*

- [ ] Event-driven tests for D5's three handlers — send an `EventDataBirthPoints` and assert on the grid; this is where
  the display half proves itself
- [ ] Menu-layout tests for D4 against C's region constants (`[C] ui-birth.c:153–172`)
- [ ] A throwaway harness that paints one birth screen and holds it — build it, look at it, do not keep it

---

# Part 2 — the input half *(deferred to Chapter 5)*

Listed so the split is legible and so Part 1 knows what it is being asked to support. **None of it is Chapter 3 work.**
The ordering here is provisional and should be revisited when Chapter 5's map is written, since it shares its first
stage with the rest of Chapter 5.

## I0 — The design set piece

**Who owns the birth loop under two threads?**

`textui_do_birth` is a `while (!done)` loop that pushes commands and then calls
`cmdq_execute(CTX_BIRTH)` (`[C] ui-birth.c:1773`) — it *drives the game* from inside a UI file, on the game's own
thread, and returns only when the character is accepted. The port has two threads and a channel between them, and the
core is not allowed to call the front end at all.

Every message the port sends today is a one-way report: the core says what happened and carries on (`UILoop`'s arms are
all paint-and-return). Birth is the first exchange where the core must *wait*
for an answer, and it is not one answer but a conversation of a dozen or more, each of which changes core state that the
next screen displays. `UIBirth`'s Javadoc flags this at `UIBirth.java:60`, and
`BirthEvents` at `:38`.

- **The loop stays UI-side** (closest to C). `BirthEvents` owns the stage machine and sends each command over
  `coreQueue`; the core's `CommandGetter` blocks on that queue in `CTX_BIRTH`. Preserves C's structure exactly,
  including `prev` and `roller`. Costs: the core is blocked-waiting for a UI decision, which is the arrangement the
  two-channel model was written to avoid.
- **The loop moves core-side.** The core drives the stages and the UI becomes a pure painter that answers "what did they
  press?". Fits the channel model, but relocates the one C file this roadmap is porting, and the stage machine is
  *entirely* presentation — `prev`, `roller` and the `+1`/`+2`
  arithmetic have no meaning to `player-birth.c`.
- **The loop stays UI-side, requests are async.** No blocking either way. Truest to the architecture, and the hardest,
  because the redraw decisions (`if (prev < CURRENT) display_player`)
  assume the core's state is current when the stage begins.

**Starting position for the proposal:** the loop stays UI-side. It is what C does, it keeps the stage machine next to
the screens it paints, and the "core blocks in `CTX_BIRTH`" objection is weaker than it looks — C's game loop blocks on
the same thing, and no other core work exists to do while a character is being rolled.

- [ ] Write the proposal — problem / C's way / proposed way / gains / risks — and have it challenged before building

## I1 — Keyboard input *(the whole of Chapter 5's front end, not just birth's)*

- [ ] A key listener on the Swing component, producing events on the EDT
- [ ] EDT → `uiChannel` delivery, the same route `WindowCloseRequested` already takes (`UILoop.java:214`)
- [ ] A `UiEventType`-shaped event record — `EVT_KBRD` and `EVT_ESCAPE` already exist in the enum
- [ ] `inkey` / `inkey_ex` (`[C] ui-input.c`) — the blocking read the stage machine is written around. Keyboard half
  only; `EVT_MOUSE` is not needed by anything in this file.
- [ ] Fill in `TextUIHook.getString` (`TextUIHook.java:144`) on top of it

## I2 — The menus, made navigable *(needs I1, D4)*

- [ ] `menu_select` with `MN_DBL_TAP`, `cmd_keys` and the `browse_hook` fired as the cursor moves

## I3 — The stage machine *(needs I0, I1, I2, and all of Part 1)*

- [ ] The `birth_stage` enum, **in C's order**, and `birth_rollers`
- [ ] The `CommandGetter` implementation the UI installs — C's `textui_get_cmd`'s `CTX_BIRTH` arm
- [ ] `textui_do_birth` (`[C] ui-birth.c:1617`), including `prev`, `roller`, and the two guards at
  `:1683` and `:1726`

## I4 — The menu questions *(needs I3)*

- [ ] `menu_question` (`[C] ui-birth.c:783`) — the fork at `:809` is the whole of the roller branch

## I5 — Point-buy input *(needs I4, D5; pairs with core stage H's `buy_stat`/`sell_stat`/`reset_stats`)*

- [ ] `point_based_command` (`[C] ui-birth.c:1118`), keyboard path only
- [ ] `buysell[]`, if D5 deferred it and a mouse has since arrived

## I6 — Roller input *(needs I4, D3; pairs with core stage H's quickstart save/load)*

- [ ] `roller_command` (`[C] ui-birth.c:868`), keyboard path only, including `prev_roll`

## I7 — Name, history, confirm *(needs I3, D3)*

- [ ] `get_name_command` (`[C] ui-birth.c:1298`) — the savefile-overwrite check is the part to decide about, not
  transcribe
- [ ] `get_history_command` (`:1525`)
- [ ] `edit_text` (`:1352`) and `get_screen_loc` (`:1331`) — the multi-line history editor. C's is UTF-8 byte arithmetic
  over a `char*`; Java's `String` makes most of it disappear, which makes this a transcription trap rather than a
  transcription job.
- [ ] `get_confirm_command` (`:1567`)

## I8 — Quickstart and the random finish *(needs I7)*

- [ ] `textui_birth_quickstart` (`[C] ui-birth.c:103`), reading D1's flag
- [ ] `finish_with_random_choices` (`:659`) — including the reverse push order at `:761`

---

## Not in this roadmap, either part

`do_cmd_options_birth` and the options menu (`[C] ui-options.c`), `do_cmd_help`
(`[C] ui-help.c`), the savefile-name machinery (`savefile_set_name`,
`savefile_name_already_used`), mouse input and every `context_hook` path (`use_context_menu_birth`,
`[C] ui-birth.c:409`, and the context menus inside `roller_command` and
`point_based_command`), `arg_force_name` and the `-n` command-line path, and the full
`display_player` character sheet beyond the reduced form D3 calls for — `ui-player.c` is 1,327 lines and deserves its
own scoping decision.

Also out: `ui-history.c` (152 lines — `history_display`, `dump_history`, static `print_history_header`), the
display/dump side of `player-history.c`'s artifact-known/lost/unmask entries. `Chapter_3_Roadmap.md`'s "Not Chapter 3"
note already defers that core file to Chapter 8 beyond the struct — birth's opening entry via `history_add` is the only
piece this chapter needs — and `ui-history.c` is that same deferral's UI half, since it consumes `history_get_list`,
which does not exist yet either.
