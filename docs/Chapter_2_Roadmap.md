# Chapter 2 roadmap — Object knowledge (the rune system)

C: `obj-knowledge.c`. Audited against the code (not the ticks) 2026-08-13; full suite at that date:
1526 tests, 0 failures. Fine detail for the chapter in progress lives here; `ROADMAP.md` keeps the one-line summary.

## Done — verified in code

- [x] `init_rune` → `Rune.initRunes()` (`Rune.java:109`) — all six variety loops ported (combat, mod, resist, brand,
  slay, curse, flag), sentinel skipping, brand grouping by name, slay grouping by `sameMonsterSlain`, flag subtype
  exclusions (`OFT_LIGHT/DIG/THROW/CURSE_ONLY`). Stores via
  `ObjectRegistry.setRunes`. Verified against C: 99 runes, every per-variety count matches.
- [x] `struct rune` redesign — `sealed interface RuneVariety` with seven record keys (`CombatKey`, `ModKey`,
  `ResistKey`, `BrandKey`, `SlayKey`, `CurseKey`, `FlagKey`), each carrying its resolved definition and answering
  `group()`. The design set piece for this subsystem; verdict: paid off, killed the off-by-one class entirely.
- [x] `rune_note` / `rune_set_note` / `rune_variety` → `getNote()` / `setNote()` / `getVariety()`
  as instance accessors (`Rune.java:225–246`). List-indexed entry points wait on `getRunes` below.
- [x] `c_rune[]`, `rune_group_text[]`, `enum rune_variety` → `CombatRunes`, `RuneGroup`.
- [x] `cleanup_rune` — N/A, garbage collected.
- [x] **The `lookupObjectProperty` stat/mod bug is fixed.** The settled design was the *minimal*
  fix, not the sealed `ObjectPropertySubject` interface `ROADMAP.md` used to describe (that was superseded the same day
  it was proposed, 2026-08-10; deferred, not rejected). The fix: the tag comparison in
  `ObjectPropertyTypeWrapper.equals` is retired (commented with rationale,
  `ObjectPropertyTypeWrapper.java:223–229`), so the wrapper compares subjects only and
  `lookupObjectProperty` (`ObjectRegistry.java:758`) compares the tag exactly once — which is what lets it fold
  `OBJ_PROPERTY_STAT` into `OBJ_PROPERTY_MOD` lookups, as C does. The 20 tests that were red on this now pass; the four
  ACID properties stay distinct. *Note: the full write-up this once pointed to is no longer in `docs/Issues log.md` —
  that file was emptied in the architecture migration. The rationale now lives in the code comment itself.*

## To do — the list half

Ordered roughly by dependency; the first item unblocks the next three.

- [ ] `ObjectRegistry.getRunes()` + `max_runes` (C: `obj-knowledge.c:230`). `setRunes` exists but there is no getter —
  `RuneInitTest` currently reads the list back by reflection and says so at
  `RuneInitTest.java:74`. List order is load-bearing (knowledge menu order; C's savefile identifies runes by index).
- [ ] `rune_index` (C: `obj-knowledge.c:194`) → map-based lookup by `RuneVariety`.
- [ ] `rune_name` / `rune_desc` (C: `obj-knowledge.c:321, 342`) → `displayName()` / `description()`
  on `RuneVariety`. Nothing of this exists yet; the per-variety data it needs is already on the record keys.
- [ ] Canonical-representative lookup — the scan inside `player_learn_brand` / `player_learn_slay`
  (C: `obj-knowledge.c:1407, 1384`) that maps any brand/slay to the one its group's rune was built from.

## Deferred — the knowledge-application half

- [ ] `player_knows_rune` (C: `obj-knowledge.c:249`) and `player_learn_rune` (C: `:1260`) plus the
  `player_knows_brand/slay/curse/ego` family. `Player` now exists (`Player.java`, with the `obj_k`
  port at `Player.java:324`), so the hard dependency has landed early — but the roadmap defers this half to Chapter 5+,
  and nothing forces it sooner. Decide when the list half is done.

## Not Chapter 2

The rest of `obj-knowledge.c` — the `object_*` functions (`object_has_rune`, `object_runes_known`,
`object_fully_known`, `object_sense/see/touch/grab`, `player_know_object`,
`update_player_object_knowledge`, `player_learn_flag/innate`) — is the rune- *learning* pipeline and belongs to Chapter
7 ("Rune learning end-to-end"), where objects exist to learn from.
