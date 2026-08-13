# Chapter 3 roadmap — A character exists (pre-map inventory)

C: `player-birth.c` (38 functions), `player-calcs.c` (27), `player.c` (16), `player-util.c` (57),
`player-history.c` (14), `player-timed.c` (30). Audited against the code 2026-08-13.

**This is not the map.** The method says Chapter 3 gets its fine detail when the map is written, and the map is Rowan's
first deliverable. What this file records is the *pre-existing state*: the architecture migration and the game-loop work
already dropped a substantial scaffold into
`middle/player`, and the map should be written knowing what's there — a stub with agreed semantics is a different
porting job from a blank page.

## Already in place (before the chapter starts)

### Data classes — real, populated by Chapter 1's loaders

`PlayerRace`, `PlayerClass`, `PlayerBody`, `PlayerShape`, `PlayerHistoryChart` /
`PlayerHistoryEntry`, `PlayerProperty`, `PlayerTimedEffect` / `TimedGrade` / `TimedFailure`,
`StartItem`, `StartOptionExclusion`, `Quest`, `PlayerAbility`, `PlayerBlow`, `EquipSlot` — all built by
`PlayerDataLoader` at boot. The *data* for birth and timed effects is loaded; what's missing is the code that acts on
it.

### `Player` — a 991-line skeleton, live at boot

`Player.java` ports `struct player`: created in `GameEngine.loadGameConstants()` (the
`init_player` port), carries `upkeep`, `obj_k` (`Player.java:324`), options, both `PlayerState`s, the timed-effect
table. Real accessors throughout, but **~17 methods are deliberate, documented stubs** (`grep -n Stub Player.java`) —
`clearTimed`, `noticeStuff` / `updateStuff` /
`redrawStuff` / `handleStuff`, `statDec`, `expLose`, `knowObject`, `timedGradeEqual` among them — each annotated with
what subsystem it awaits (timed runtime, player-calcs, display, object knowledge).

### Supporting classes — mixed

- `PlayerUpkeep` (452 lines) — real fields, partially exposed; Javadoc says fields surface as callers arrive.
- `PlayerState` (172 lines) — the `struct player_state` *data* shape with accessors; **no calculation code behind it**.
- `PlayerUtils` (232 lines) — 13 of `player-util.c`'s 57 functions have landing sites; **3 are real**
  (`applyDamageReduction`, `dungeonChangeLevel`, `dungeonGetNextLevel`), 10 are stubs (`takeHit`, `regenHP`,
  `regenMana`, `updateLight`, `overExert`, `takeTerrainDamage`, `disturb`,
  `search`, `restingCompleteSpecial`).
- `PlayerHistory` — stub, logger only.
- `StatTables` — **1 of C's 18 `adj_*` tables** ported (`adjConFix`); the other 17 (`adj_str_td`, `adj_dex_th`,
  `adj_con_mhp`, `adj_mag_mana`, …, `player-calcs.c:45–819`) are absent and are the raw material of derived stats.
- Enums — `PlayerFlag`, `PlayerNotice`, `PlayerRedraw`, `PlayerSkill`, `PlayerUpkeepEnum`,
  `TimedEffect`, `PlayerOptionEnum` etc. all exist (the `PF_*`/`PN_*`/`PR_*`/`PU_*`/`TMD_*` sets).

### Birth — boundary plumbing only

`UIBirth` (core side) and `BirthEvents` (UI side) handle entering/leaving the birth screen over the channel, with
`EventDataBirthStage` / `EventDataBirthPoints` message records already defined. None of `player-birth.c`'s actual flow
exists: no `do_cmd_birth_*` command handlers, no roller, no point-buy, no `player_generate`, no `wield_all`.

### Tests

**None for `middle/player`.** The only player-adjacent suites are the Chapter 1 parser tests (`PlayerRaceReaderTest`,
`PlayerClassReaderTest`, `PlayerTimedReaderTest`,
`PlayerPropertyReaderTest`). As `player.c`/`player-calcs.c` functions are ported, suites are Claude's half —
`player-calcs` in particular is table-driven and C-comparable, good test territory.

## The chapter itself (boxes stay ROADMAP's; detail comes after the map)

- [ ] **Map: the player subsystem** — Rowan writes it; review against the C before any porting. Things the map should
  account for, given the scaffold: which existing stubs Chapter 3 fills vs. leaves for Chapters 5–7; where
  `player_state` recalculation hooks into the
  `noticeStuff`/`updateStuff`/`redrawStuff` cycle; how birth commands travel the channel (the plumbing already fixes
  part of this answer).
- [ ] **Character creation (birth)** — `player-birth.c`. Data and screen plumbing exist; the flow, rollers, point-buy
  and `player_generate` do not.
- [ ] **Derived stats (`player-calcs` — the big one)** — 27 functions, 17 missing `adj_*` tables,
  `calc_bonuses` at the centre; fills the empty half of `PlayerState` and four `Player` stubs.
- [ ] **Player state & timed effects (`player-timed`)** — the runtime (`player_set_timed`, grades, on-end/on-increase
  messages) over the already-loaded `PlayerTimedEffect` data; fills
  `clearTimed` and the timed stubs.

Candidate for the chapter's one design set piece (decide at map time, not now): C scatters recalculation through
flag-polling (`PN_*`/`PU_*`/`PR_*` bitmasks polled each turn); whether the port keeps that or leans on the channel/event
system is a real, writable proposal.

## Not Chapter 3

`player-util.c`'s movement/resting/spell gates (Chapter 5), `player-attack.c` (Chapter 6),
`player-spell.c` (Chapter 6+), `player-quest.c` (Chapters 4–8), `ui-player.c` (UI side),
`player-history.c` beyond the struct (mostly meaningful once artifacts/death exist — Chapter 8 territory; the chapter
needs only enough for birth's opening entry).
