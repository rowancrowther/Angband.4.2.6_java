# Chapter 3 roadmap — A character exists (pre-map inventory)

C: `player-birth.c` (1479 lines, 38 functions), `player-calcs.c` (2733, 27), `player.c` (511, 16),
`player-util.c` (1733, 57), `player-history.c` (294, 14), `player-timed.c` (1131, 30 — but 25 of those are its parser,
already ported in Chapter 1). Scaffold re-audited against the code 2026-08-18.

**This is not the map.** The method says Chapter 3 gets its fine detail when the map is written, and the map is your
first deliverable. What this file records is the *pre-existing state* plus the scoping decisions already taken, so the
map can be written knowing what is there — a stub with agreed semantics is a different porting job from a blank page.

## What "a character exists" bounds

The chapter ends with **a fully-formed `Player` whose derived numbers are correct, displayed on screen**. Nothing
consumes those numbers yet: no cave (Chapter 4), no turn loop (Chapter 5), no monsters (Chapter 6). That sentence
settles most of the scope questions by itself — anything that needs a world, a turn or a monster is out.

### Three different things are all called "creating the player"

Naming these apart is what unlocks the map; they are separate jobs with separate porting costs.

| C                                                                                | What it is                                                                                                            | Port status                                 |
|----------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------|---------------------------------------------|
| `init_player` (`[C] player.c:476`)                                               | Allocates the one global `player` struct at startup                                                                   | **Done** — `GameEngine.loadGameConstants()` |
| `player_generate` (`[C] player-birth.c:979`)                                     | Fills that struct from a race/class/body choice; a pure re-derivation, called afresh every time a menu choice changes | **Missing**                                 |
| `do_cmd_birth_init` … `do_cmd_accept_character` (`[C] player-birth.c:1049–1332`) | The command-driven birth *flow*: 13 handlers, roller, point-buy, quickstart, prev-stats                               | **Missing** (channel plumbing only)         |

The middle row is the one that surprises: `player_generate` is not "the birth screen". The flow calls it many times
before anything is final.

### "Player state & timed effects" is two subsystems under one label

- **`struct player_state`** — the *derived* block: every number computed from race + class + equipment + shape + timed
  effects. `PlayerState.java` is that shape with accessors and no arithmetic behind it.
  `calc_bonuses` (`[C] player-calcs.c:1877`, ~460 lines) is what fills it. C keeps **two** per player — `state` and
  `known_state`, the second computed with `known_only = true` so the UI can show what the character believes rather than
  what is true. `Player` already carries both.
- **The `player-timed.c` runtime** — the TMD_* counters (poisoned, afraid, hasted, blind) with grades, failure
  conditions, on-begin/on-end effects and messages. Five real functions: `player_set_timed`
  (`[C] player-timed.c:787`), `player_inc_check` (`:923`), `player_inc_timed` (`:1050`), `player_dec_timed` (`:1093`),
  `player_clear_timed` (`:1123`).

They share a heading because timed effects are an **input to** `calc_bonuses`: change a counter and the state block is
stale.

### Birth options are consumed, not built

Already ported as data — `PlayerOptionEnum.java:100–118` carries the `OP_birth_*` set typed
`PlayerOptionTypes.BIRTH`, and `PlayerOptions.java:100–104` has `has()` / `initDefaults()`. Chapter 3 builds no option
system and no option menu (`[C] option.c` plus its UI). It only *reads* four of them:
`birth_start_kit` (`[C] player-birth.c:612`), and `birth_know_runes` / `birth_randarts` / `birth_know_flavors`
(`[C] player-birth.c:1261, 1282, 1293`) inside `do_cmd_accept_character`. Not a task — an input you already hold.

## Scoping decisions taken (260818)

**1. Stub everything in `player-calcs.c` that can be stubbed.** Provisional — reviewable once you are inside the code,
but it stands for now. It reshapes the chapter: calcs stops being "the big one" and becomes a thin floor plus a large
deferred block. Clearly stubbable, because nothing in this chapter can exercise them: `calc_inventory`
(`[C] player-calcs.c:1023`), `calc_spells` (`:1268`), `calc_mana` (`:1480`), `calc_light` (`:1598`),
`calc_shapechange` (`:1798`), `calc_blows` (`:1703`), `calc_digging_chances` (`:1651`), `calc_unlocking_chance`
(`:1676`), `weight_limit` / `weight_remaining` (`:1741, :1756`), `earlier_object` (`:939`), and the five tracking
functions (`health_track` … `tracked_object_is`, `:2470–2521`).

**2. Birth's outfitting is mainly stubs for now.** `player_outfit` (`[C] player-birth.c:584`) and `wield_all`
(`[C] player-birth.c:462`) are where Chapter 3 would otherwise reach into Chapter 7's object lifecycle. The character is
born without usable gear; the `birth_start_kit` option branch exists but does nothing yet. Revisit in Chapter 7, where
the objects to outfit with actually exist.

**3. The `calc_bonuses` floor — write it whole.** The question decision 1 left open, settled 260818. Unlike its siblings
`calc_bonuses` (`[C] player-calcs.c:1877–2335`) is not stubbable as a unit: one 460-line block that either produces the
state block or leaves the character with no numbers. Three cuts were available; all three were declined.

- **All 17 `adj_*` tables get ported**, not the 11 the floor strictly needs (`adj_dex_ta`, `adj_str_td`, `adj_dex_th`,
  `adj_str_th` at `[C] player-calcs.c:2233–2236`; `adj_dex_dis`, `adj_int_dis`, `adj_int_dev`, `adj_wis_sav`,
  `adj_str_dig` at `:2240–2244`; `adj_str_hold` at `:2252`; `adj_con_mhp` for `calc_hitpoints` at `:1568`). They are
  static integer arrays with no logic, and a half-populated `StatTables` is a trap — the missing six surface in Chapter
  6 with no record of which were skipped deliberately.
- **The timed block is ported**, not short-circuited — `[C] player-calcs.c:2094–2214`, 120 lines of food effects plus
  stun/fear/haste/blind, calling `player_timed_grade_eq` and `player_flags_timed` directly. It is not dead code for a
  new character: the food branch at `:2095` tests `!player_timed_grade_eq(p, TMD_FOOD, "Fed")` and a born character has
  `TMD_FOOD` set, so it fires at birth. This is the coupling that fixes the ordering below — timed runtime before
  `player_generate`.
- **The equipment loop is written**, not omitted — `[C] player-calcs.c:1922–2026`, ~105 lines accumulating modifiers,
  resists and combat bonuses off worn gear. Under decision 2 the character is born naked, so it iterates over nothing
  and cannot be exercised this chapter. Accepted anyway: the alternative reopens a 460-line function twice, and
  mid-function surgery in Chapters 6 and 7 is where divergence from C creeps in. The content is transcription, not
  design.

Not in question either way: the stat-index loop at `[C] player-calcs.c:2054–2093`, which computes `stat_ind` — every
table lookup downstream is indexed by it. It pulls in one small cross-file dependency, `modify_stat_value`
(`[C] player-util.c:339`), which `[C] player-birth.c:272` needs independently.

## Already in place (before the chapter starts)

### Data classes — real, populated by Chapter 1's loaders

`PlayerRace`, `PlayerClass`, `PlayerBody`, `PlayerShape`, `PlayerHistoryChart` / `PlayerHistoryEntry`,
`PlayerProperty`, `PlayerTimedEffect` / `TimedGrade` / `TimedFailure`, `StartItem`, `StartOptionExclusion`, `Quest`,
`PlayerAbility`, `PlayerBlow`, `EquipSlot` — all built by `PlayerDataLoader` at boot. The *data* for birth and timed
effects is loaded; what is missing is the code that acts on it.

### `Player` — 2546 lines, live at boot

`Player.java` ports `struct player`: created in `GameEngine.loadGameConstants()`, carries `upkeep`, `obj_k`
(`Player.java:324`), options, both `PlayerState`s, the timed-effect table. Chapter 2's rune-knowledge half is real
(`knowsRune`, `learnRune`, `knowObject`, `updateObjectKnowledge`). **12 methods carry a stub marker**
(`grep -n "Stub class TODO\|Stub function TODO" Player.java`), and they are almost exactly Chapter 3's shopping list —
landing sites already exist with agreed signatures:

- the update cycle — `noticeStuff` (`Player.java:1258`), `handleStuff` (`:1269`), `updateStuff` (`:1279`),
  `redrawStuff` (`:1289`)
- the timed runtime — `clearTimed` (`:1247`), `timedGradeEqual` (`:1312`), `decTimed` (`:1424`), `incTimed` (`:1449`),
  `setTimed` (`:1468`)
- `player.c` odds — `statDec` (`:1367`), `expLose` (`:1380`), `adjustLevel` (`:1399`)

### Supporting classes — mixed

- `PlayerUpkeep` (577 lines) — real fields, partially exposed; Javadoc says fields surface as callers arrive.
- `PlayerState` (169 lines) — the `struct player_state` *data* shape with accessors; **no calculation behind it**.
- `PlayerUtils` (219 lines) — 12 of `player-util.c`'s 57 functions have landing sites; **3 are real**
  (`applyDamageReduction`, `dungeonChangeLevel`, `dungeonGetNextLevel`), 9 are stubs (`takeHit`, `regenHP`,
  `regenMana`, `updateLight`, `overExert`, `takeTerrainDamage`, `disturb`, `search`, `restingCompleteSpecial`) — and
  every one of those 9 needs a world or a turn, so they stay stubs through this chapter.
- `PlayerHistory` — stub, logger only.
- `StatTables` — **1 of C's 18 `adj_*` tables** ported (`adjConFix`); the other 17 (`adj_str_td`, `adj_dex_th`,
  `adj_con_mhp`, `adj_mag_mana`, …, `[C] player-calcs.c:45–819`) are absent.
- Enums — `PlayerFlag`, `PlayerNotice`, `PlayerRedraw`, `PlayerSkill`, `PlayerUpdateEnum`, `TimedEffect`,
  `PlayerOptionEnum` etc. all exist (the `PF_*`/`PN_*`/`PR_*`/`PU_*`/`TMD_*` sets).

### Birth — boundary plumbing only

`UIBirth` (core side) and `BirthEvents` (UI side) handle entering/leaving the birth screen over the channel, with
`EventDataBirthStage` / `EventDataBirthPoints` message records already defined. None of `player-birth.c`'s flow exists:
no `do_cmd_birth_*` handlers, no roller, no point-buy, no `player_generate`, no `wield_all`.

### Tests

**None for `middle/player`.** The only player-adjacent suites are Chapter 1's parser tests (`PlayerRaceReaderTest`,
`PlayerClassReaderTest`, `PlayerTimedReaderTest`, `PlayerPropertyReaderTest`). Tests are Claude's half as functions
land; the 17 `adj_*` tables and `calc_bonuses` are table-driven and C-comparable, the best test territory in the
chapter. Its equipment loop is the exception — written under decision 3 but unexercisable until Chapter 7 puts gear on
the character, so it goes untested this chapter by construction.

## How the ROADMAP boxes map onto the work

The three ROADMAP boxes are **not three independent tasks** — they are a chain, with a fourth job hiding between them:

```
 birth flow ──calls──▶ player_generate ──needs──▶ calc_bonuses ◀──reads── timed runtime
                                                       │
                                              the update cycle
                                    (notice/update/redraw/handle_stuff)
```

`ROADMAP.md` stays the checklist you tick; its boxes span the stages below rather than matching them one-to-one. The
ticking is not sequential — no ROADMAP box completes until several stages have.

| ROADMAP box                                   | Work-order stages |
|-----------------------------------------------|-------------------|
| Map: the player subsystem                     | 0                 |
| Derived stats (`player-calcs`)                | A, C              |
| Player state & timed effects (`player-timed`) | B, E              |
| Character creation (birth)                    | F, G, H           |
| *(no box — should be added)* The update cycle | D                 |

Two consequences worth noting before the map is written:

- **Timed splits in two, and its read side comes first.** Decision 3 keeps `calc_bonuses`' timed block
  (`[C] player-calcs.c:2094–2214`), which calls `player_timed_grade_eq` and `player_flags_timed`. So the read side
  (stage B) must land *before* derived stats, while the write side (stage E) needs the update cycle to raise flags into,
  and lands after it. The old note that timed was "orderable either side of calcs" no longer holds.
- **The update cycle has no ROADMAP box and needs one.** `notice_stuff` / `update_stuff` / `redraw_stuff` /
  `handle_stuff` (`[C] player-calcs.c:2536–2728`) sit between every other item on the list, hold four `Player` stubs,
  and carry the chapter's design set piece.

## The design set piece — settled 260818

**The `PR_*` drain: how does a raised redraw flag become a channel message?**

The candidate this file used to carry — "keep C's flag-polling or lean on the channel/event system?" — was the wrong
question, on three counts, and is withdrawn.

1. **C already does both.** `redraw_stuff` (`[C] player-calcs.c:2676`) draws nothing. Its body is a loop over
   `redraw_events[]` (`[C] player-calcs.c:2645`), a table mapping 25 `PR_*` flags one-to-one onto `game_event_type`s,
   calling `event_signal` for each raised flag. C's redraw half *is* an event system; the bitmask is a coalescing buffer
   in front of it, not an alternative to it.
2. **The buffer does three jobs the channel cannot.** *Coalescing* — take forty hits in a turn, set `PR_HP` forty times,
   send one message rather than forty across a thread boundary. *Ordering* — `update_stuff` polls
   `PU_INVEN` → `PU_BONUS` → `PU_HP` → `PU_MANA` in sequence (`[C] player-calcs.c:2571–2596`) because bonuses read the
   inventory and `calc_hitpoints` reads `state.stat_ind[STAT_CON]` (`:1568`); publication order gives no such guarantee.
   *Drain-time suppression* — `redraw_stuff` gates on `!character_generated` (`:2683`), masks to
   `PR_SUBWINDOW` when the map is hidden (`:2686`), and throttles all but message and map on 99 turns in 100 while
   resting or running (`:2690`). All three use state the raising code has no business knowing.
3. **The port already made the call.** `PlayerUpkeep.java:115–125` holds the three sets as `Flag<PlayerNotice>`,
   `Flag<PlayerUpkeepEnum>`, `Flag<PlayerRedraw>`, with `redrawFlagOn`/`Off` (`:301–317`), `setUpdateFlagOn`/`FlagsOn`
   (`:446–467`) and `noticeFlagOn` (`:475`). The flags stay.

**What is genuinely undecided** is what C never had to decide. `event_signal(EVENT_HP)` carries **no payload** — the UI
reaches into the global `player` and reads `chp` itself. Architecture Principle 1 forbids exactly that, so every
`PR_*` flag that C turns into a bare signal must become a `CoreMessage` *carrying its data*. The options:

- **One message type per flag** — ~25 records (`EventDataHP(cur, max)`, `EventDataStats(…)`), dispatched from an
  `EnumMap<PlayerRedraw, …>` that keeps `redraw_events[]`'s table-driven shape. Precise, verbose.
- **One snapshot message** carrying everything currently dirty. Trivial to drain; the UI repaints panels that did not
  change, and the message grows without bound as the port does.
- **Group by panel region** — fewer types than flags, more precision than a snapshot, but the grouping is a UI concern
  leaking into a core message set, which is what Principle 1 exists to prevent.
- **Notify-only, then ask** — the message says "HP changed", the UI requests the value over `coreQueue`. Legal under the
  two-queue model, but turns one send into a round trip.

**Starting position for the proposal:** one message per flag with an `EnumMap` dispatch table — it preserves C's shape
and can be collapsed later if traffic proves it needs collapsing.

**One asymmetry the proposal must state:** `PN_*` and `PU_*` never cross the boundary. Notice actions and recalculations
are entirely core-internal, so two-thirds of the flag machinery is not a boundary question at all — it is a work queue
inside the core. Only `PR_*` reaches the UI, and only through `redraw_stuff`.

## Work order

Each stage's dependencies are complete before it starts; everything before stage G is testable without a UI. The ROADMAP
boxes span these stages rather than matching them one-to-one.

### 0 — The map *(before any porting)*

- [x] **Map: the player subsystem** — you write it; review against the C first. Given the scaffold, it should account
  for: how the update cycle decides *when* recalculation fires; how birth commands travel the channel (the existing
  `UIBirth` / `BirthEvents` plumbing already answers part of it); which stubs this chapter fills versus leaves to
  Chapters 5–7; and whether decisions 1–3 survive contact with the C.

### A — Stat tables *(no dependencies)*

- [x] The 17 missing `adj_*` tables into `StatTables` (`[C] player-calcs.c:45–819`):
  - [x] `adj_int_dev`
  - [x] `adj_wis_sav`
  - [x] `adj_dex_dis`
  - [x] `adj_int_dis`
  - [x] `adj_dex_ta`
  - [x] `adj_str_td`
  - [x] `adj_dex_th`
  - [x] `adj_str_th`
  - [x] `adj_str_wgt`
  - [x] `adj_str_hold`
  - [x] `adj_str_dig`
  - [x] `adj_str_blow`
  - [x] `adj_dex_blow`
  - [x] `adj_dex_safe`
  - [x] `adj_con_mhp`
  - [x] `adj_mag_study`
  - [x] `adj_mag_mana`
  - [x] (`adj_con_fix` is already there.)

### B — Timed, read side *(unblocks C; small)*

- [x] `player_timed_grade_eq` (`[C] player-timed.c:734`) → the `timedGradeEqual` stub, `Player.java:1312`
- [x] `player_flags_timed` (`[C] player.c:310`) and:
- [x] `player_flags` (`[C] player.c:290`) — both called from inside
  `calc_bonuses` (`[C] player-calcs.c:2135, 1921`)

### C — Derived stats *(needs A, B)*

- [x] `modify_stat_value` (`[C] player-util.c:339`) — needed by both `calc_bonuses` and birth
- [x] `calc_bonuses` (`[C] player-calcs.c:1877–2335`), whole, per decision 3 — including the equipment loop
  (`:1922–2026`, unexercisable until Chapter 7) and the timed block (`:2094–2214`)
- [x] `calc_hitpoints` (`[C] player-calcs.c:1562`)
- [x] `update_bonuses` (`[C] player-calcs.c:2336`) — the wrapper that recomputes both `state` and `known_state`
- [x] Stubs, per decision 1: `calc_inventory`, `calc_spells`, `calc_mana`, `calc_light`, `calc_shapechange`,
  `calc_blows`, `calc_digging_chances`, `calc_unlocking_chance`, `weight_limit`/`weight_remaining`, `earlier_object`,
  and the five tracking functions (`[C] player-calcs.c:2470–2521`)

### D — The update cycle + the design set piece *(needs C)*

- [X] Write the set-piece proposal above; review before building
- [X] `notice_stuff` (`[C] player-calcs.c:2536`) → `Player.java:1258`
- [X] `update_stuff` (`[C] player-calcs.c:2565`) → `Player.java:1279` — the fixed poll order is the point
- [X] `redraw_stuff` (`[C] player-calcs.c:2676`) → `Player.java:1289` — where the proposal lands
- [X] `handle_stuff` (`[C] player-calcs.c:2728`) → `Player.java:1269`

### E — Timed, write side *(needs D, to raise flags)*

- [X] `player_set_timed` (`[C] player-timed.c:787`) → `Player.java:1468`
- [X] `player_inc_check` (`[C] player-timed.c:923`)
- [X] `player_inc_timed` (`[C] player-timed.c:1050`) → `Player.java:1449`
- [X] `player_dec_timed` (`[C] player-timed.c:1093`) → `Player.java:1424`
- [X] `player_clear_timed` (`[C] player-timed.c:1123`) → `Player.java:1247`

### F — `player.c` odds birth depends on *(needs C)*

- [X] `player_stat_inc`
- [X] `player_stat_dec` (`[C] player.c:145, 171`) → `statDec` stub at `Player.java:1367`
- [X] `adjust_level` (`[C] player.c:208`) → `Player.java:1399`;
- [X] `player_exp_gain`
- [X] `player_exp_lose` (`[C] player.c:269, 277`) → `expLose` at `Player.java:1380`
- [ ] `stat_name_to_idx`
- [ ] `stat_idx_to_name`
- [ ] `lookup_realm` (`[C] player.c:111, 122, 130`)
- [ ] `player_random_name`
- [ ] `player_safe_name` (`[C] player.c:375, 389`) — birth needs both

### G — `player_generate` and its helpers *(needs C, F)*

- [ ] `player_embody` (`[C] player-birth.c:369`), `get_ahw` (`:353`), `get_history` (`:330`), `get_money` (`:390`)
- [ ] `roll_hp` (`[C] player-birth.c:279`), `get_bonuses` (`:311`), `get_stats` (`:231`)
- [ ] `player_generate` (`[C] player-birth.c:979`)
- [ ] `player_init` (`[C] player-birth.c:395`) — note this is *not* `player.c`'s `init_player`, already ported
- [ ] `player_make_simple` (`[C] player-birth.c:522`)
- [ ] `player_outfit` (`[C] player-birth.c:584`) and `wield_all` (`:462`) — **stubs**, per decision 2
- [ ] **Remove the `LitPlayer` test double** in `src/test/.../cave/ChunkMarkWasSeenTest.java` once this stage gives a
  player a real `PlayerState`, and go back to `new Player()`. `Player()` leaves `state` null, but
  `Chunk.updateView` → `calcLighting` reads `Player.getStateLight()` (`Player.java:762`, `state.getCurLight()`), so
  every test in that class was dying on an NPE before its first assertion. The double overrides that one accessor to
  return a light radius of 1. C has no such case — its `p->state` is a struct, zeroed from allocation. Added 260828.

### H — The birth flow *(needs G; the only stage that needs the UI)*

- [ ] Point-buy: `recalculate_stats` (`[C] player-birth.c:681`), `reset_stats` (`:705`), `buy_stat` (`:732`),
  `sell_stat` (`:770`), `generate_stats` (`:816`), and the `birth_stat_costs[]` table (`:676`)
- [ ] Quickstart: `save_roller_data` / `load_roller_data` (`[C] player-birth.c:146, 179`), `do_birth_reset` (`:1034`)
- [ ] The 13 command handlers, `do_cmd_birth_init` … `do_cmd_accept_character` (`[C] player-birth.c:1049–1332`), over
  the existing `UIBirth` / `BirthEvents` plumbing
- [ ] Roman-numeral suffixes: `find_roman_suffix_start`, `int_to_roman`, `roman_to_int`
  (`[C] player-birth.c:1334, 1366, 1426`)
- [ ] `PlayerHistory` — only enough for birth's opening entry; the rest is Chapter 8

## Not Chapter 3

`player-util.c`'s movement/resting/spell gates (Chapter 5), `player-attack.c` (Chapter 6), `player-spell.c`
(Chapter 6+), `player-quest.c` (Chapters 4–8), `ui-player.c` (UI side), `option.c` and the options menu (UI side),
`player-history.c` beyond the struct (mostly meaningful once artifacts and death exist — Chapter 8; this chapter needs
only enough for birth's opening entry), and — by decision 2 — the object lifecycle behind `player_outfit` /
`wield_all` (Chapter 7).
