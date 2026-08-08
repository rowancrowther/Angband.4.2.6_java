# Port roadmap

A port of Angband 4.2.6 (C) to Java, as a learning exercise. Chapters are playable milestones — each one ends with
something you can *see working*. Boxes inside are C subsystems (`src/*.c`), ticked as their functions are ported and
tested. Fine detail only exists for the chapter in progress; each new chapter gets its detail when its **map** is
written, not before.

**The method** (full version in Claude's memory, `working-method.md`):

1. **Map first** — before a new subsystem, Rowan writes a half-page map of the C (files, globals, call flow); Claude
   reviews it. No porting until the map survives review.
2. **Port loop** — function at a time. Rowan writes, Claude verifies against the C and the data files, tests are
   Claude's half, box gets ticked.
3. **One design set piece per subsystem** (at most) — a written proposal for a deliberate deviation from C, challenged
   before it's built, verdict recorded after.
4. **Primers on demand** — one-screen concept notes tied to the code at hand.

---

## Chapter 0 — The big map ⬅ *next*

The whole-game view everything else hangs off. How does C Angband actually run?

- [ ] **The big map**: Rowan writes it — init chain (`main` → `init_angband` → the parsers), the main loop
  (`play_game` / `run_game_loop` in `game-world.c`), the event system (`game-event.c`), how UI and core talk
  (`cmd-core.c`, `game-input.c`), where the port's registries sit relative to C's globals. Claude reviews against the C.
- [ ] Revise the map after review; commit it as `docs/big-map.md`
- [ ] Java-side counterpart: one page on how *this* port boots (`Angband.java`, loaders, registries) and where it
  diverges

## Chapter 1 — The game loads its data *(mostly done)*

Every data file in `lib/gamedata` parses into a registry. C: `parser.c`, `init.c`, `datafile.c`,
`*-init.c`.

- [x] Parser infrastructure (ANTLR grammars + readers + assemblers, ~49 readers)
- [x] Object data: object_property, object_base, object, ego_item, artifact, curse, brand, slay, activation
- [x] Monster data: monster_base, monster, pain, summon, blow effects
- [x] Player data: race, class, history, shapes, timed effects, properties
- [x] World data: world, projection, quest, terrain, trap, dungeon_profile, room_template, vault
- [x] UI data: ui_entry, ui_entry_base, ui_entry_renderer
- [ ] Remaining loaders wired into one boot sequence (C: `init_angband`'s module list, in order)
- [ ] Prune this list against reality — boxes above are Claude's survey, not gospel

## Chapter 2 — Object knowledge (the rune system) *(in progress)*

C: `obj-knowledge.c`. The current stretch.

- [x] `init_rune` → `Rune.initRunes()` — verified against C; 99 runes, all counts match
- [x] `struct rune` redesign (`RuneVariety` sealed interface) — the design set piece for this subsystem, verdict: paid
  off, killed the off-by-one class entirely
- [x] `rune_note` / `rune_set_note` / `rune_variety` (as instance accessors; list-indexed entry points wait on a
  `getRunes`)
- [x] `c_rune[]`, `rune_group_text[]`, `enum rune_variety` (tables → `CombatRunes`, `RuneGroup`)
- [x] `cleanup_rune` — N/A, garbage collected
- [ ] **Fix `lookupObjectProperty` stat/mod bug** (Option B, agreed) — blocks everything below; 20 red tests point at it
  by name
- [ ] `ObjectRegistry.getRunes()` + `max_runes`
- [ ] `rune_index` → map-based lookup by `RuneVariety`
- [ ] `rune_name` / `rune_desc` → `displayName()` / `description()` on `RuneVariety`
- [ ] Canonical-representative lookup (`player_learn_brand`/`_slay`'s scan)
- [ ] `player_knows_rune` and the knowledge-application half (needs player object — may defer to Chapter 5)

## Chapter 3 — A character exists

Roll a character; see their stats. C: `player-birth.c`, `player-calcs.c`, `player.c`,
`player-util.c`, `player-history.c`. *(Map before detail.)*

- [ ] Map: the player subsystem
- [ ] Character creation (birth)
- [ ] Derived stats (`player-calcs` — the big one)
- [ ] Player state & timed effects (`player-timed`)

## Chapter 4 — A level exists

Generate a dungeon level and print it. C: `generate.c`, `gen-cave.c`, `gen-room.c`,
`gen-chunk.c`, `gen-util.c`, `gen-monster.c`, `cave.c`, `cave-*.c`. *(Map before detail — the parsing half is already
done in Chapter 1.)*

- [ ] Map: level generation
- [ ] Cave/square data structures
- [ ] Room + cave generation
- [ ] Feature placement, monster/object seeding

## Chapter 5 — You can walk around

The game loop runs; a `@` moves; you can see. C: `game-world.c`, `cmd-core.c`, `cmd-cave.c`,
`game-input.c`, `cave-view.c` (FOV), `player-path.c`, `target.c`.

- [ ] Map: the command/event loop (builds on the big map)
- [ ] Game loop + turn structure
- [ ] Movement commands
- [ ] Field of view / light

## Chapter 6 — Monsters live and combat works

Monsters spawn, move, attack; you attack back. C: `mon-make.c`, `mon-move.c`, `mon-attack.c`,
`mon-blows.c`, `mon-timed.c`, `player-attack.c`, `project*.c`, `effects.c`.

- [ ] Map: the monster + combat subsystems
- [ ] Monster creation & AI
- [ ] Melee both directions
- [ ] Projections & effects (the `projection.txt` data finally fires)

## Chapter 7 — Objects in hand

Pick up, wield, use; runes get *learned*. C: `obj-make.c`, `obj-pile.c`, `obj-gear.c`,
`obj-desc.c`, `obj-power.c`, `obj-util.c`, `obj-ignore.c`; the rest of `obj-knowledge.c`.

- [ ] Map: object lifecycle
- [ ] Object generation & piles
- [ ] Inventory/equipment
- [ ] Rune learning end-to-end (closes Chapter 2's deferred half)

## Chapter 8 — The town and persistence

Stores, save, load, die, score. C: `store.c`, `save.c`, `load.c`, `savefile.c`, `score.c`.

- [ ] Map: persistence (a design set piece candidate — the savefile format is a real decision:
  C-compatible or own format?)
- [ ] Stores
- [ ] Save/load
- [ ] Death & scores

## Not being ported

- `main-*.c` front ends, `z-*.c` utilities (Java stdlib covers them), `snd-sdl.c`, `grafmode.c`, wizard/spoiler/stats
  tooling (`wiz-*.c`, `*-spoil.c`, `main-stats.c`) — revisit wizards later if wanted for debugging.

---

*Look-back log — one line per finished stretch, newest first:*

- 2026-08-08: `init_rune` ported and verified (99 runes, every count matches C); rune types designed (sealed
  `RuneVariety`); 66 tests written; found real bug in
  `lookupObjectProperty` (stat/mod); comment pass over 14 files.
- 2026-08-07 and earlier: data parsing layer — 49 readers, 61 test suites, registries, loaders (see git log).
