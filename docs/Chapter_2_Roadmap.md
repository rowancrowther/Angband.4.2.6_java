# Chapter 2 roadmap — Object knowledge (the rune system)

C: `obj-knowledge.c`. Re-audited against the code (not the ticks) 2026-08-17: **every item in this chapter is
implemented.** Fine detail for the chapter lives here; `ROADMAP.md` keeps the one-line summary.

## Done — verified in code

### The rune data itself

- [x] `init_rune` → `Rune.initRunes()` (`Rune.java:109`) — all six variety loops ported (combat, mod, resist, brand,
  slay, curse, flag), sentinel skipping, brand grouping by name, slay grouping by `sameMonsterSlain`, flag subtype
  exclusions (`OFT_LIGHT/DIG/THROW/CURSE_ONLY`). Stores via
  `ObjectRegistry.setRunes`. Verified against C: 99 runes, every per-variety count matches.
- [x] `struct rune` redesign — `sealed interface RuneVariety` with seven record keys (`CombatKey`, `ModKey`,
  `ResistKey`, `BrandKey`, `SlayKey`, `CurseKey`, `FlagKey`), each carrying its resolved definition and answering
  `group()`. The design set piece for this subsystem; verdict: paid off, killed the off-by-one class entirely.
- [x] `rune_note` / `rune_set_note` / `rune_variety` → `getNote()` / `setNote()` / `getVariety()`
  as instance accessors (`Rune.java:225–246`).
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

### The list half — completed 2026-08-16/17

- [x] `ObjectRegistry.getRunes()` + `max_runes` (C: `obj-knowledge.c:230`) → `ObjectRegistry.getRunes()`
  (`ObjectRegistry.java:206`), which hands back the stored immutable list itself rather than copying, and
  `ObjectRegistry.getMaxRunes()` (`ObjectRegistry.java:238`), derived from the list size so C's separate `rune_max`
  counter cannot drift. List order is load-bearing (knowledge menu order; C's savefile identifies runes by index) and
  `RuneInitTest.java:594–614` asserts order, count and immutability. `RuneInitTest` still reads the list through its own
  reflective `loadedRunes()` (`RuneInitTest.java:75`) — now a deliberate choice, so the tests check what
  `initRunes` *stored* rather than what the getter chooses to show, not a workaround for a missing getter.
- [x] `rune_index` (C: `obj-knowledge.c:194`) → seven `Rune.runeIndex` overloads, one per variety (`Rune.java:268`
  onward), each scanning `getRunes()` for the matching `RuneVariety` key. Overloading rather than one map keeps the
  compiler, not a cast, responsible for picking the right subject type.
- [x] `rune_name` / `rune_desc` (C: `obj-knowledge.c:321, 342`) → `RuneVariety.runeName()` and
  `RuneVariety.runeDesc()` (`RuneVariety.java:77, 105`), implemented on each of the seven record keys. **Named for their
  C originals, not the `displayName()` / `description()` this file once proposed.** Derived per call from the subject
  the key already holds, so C's second lookup back into `curses[]` for the description is unnecessary. Covered by
  `RuneVarietyTest`.
- [x] Canonical-representative lookup — the scan inside `player_learn_brand` / `player_learn_slay`
  (C: `obj-knowledge.c:1407, 1384`). Not a separate function here: it is absorbed into `Rune.runeIndex(Brand)`
  (`Rune.java:337`) and `Rune.runeIndex(Slay)` (`Rune.java:361`), which answer with the group's rune, so
  `Player.learnBrand` (`Player.java:1662`) and `Player.learnSlay` (`Player.java:1687`) are a guard plus a call.

### The knowledge-application half — landed early, ahead of this roadmap's plan

This was deferred to Chapter 5+; `Player` arriving early meant it was written on 2026-08-16/17 instead.

- [x] `player_knows_rune` → `Player.knowsRune` (`Player.java:1764`); `player_learn_rune` → `Player.learnRune`
  (`Player.java:1888`, package-private — the choke point every wrapper goes through, see the `rune-learning-wrappers`
  note).
- [x] The `player_knows_brand/slay/curse/ego` family → `Player.knowsBrand` (`:1702`), `knowsSlay` (`:1798`),
  `knowsCurse` (`:1824`), `knowsEgo` (`:1020`).

## Not Chapter 2 — but also already written

The rest of `obj-knowledge.c` — the `object_*` functions and `player_know_object` /
`update_player_object_knowledge` / `player_learn_flag` / `player_learn_innate` — belongs to Chapter 7 ("Rune learning
end-to-end"). It too has been ported ahead of schedule: `Player.knowObject` (`Player.java:645`),
`Player.updateObjectKnowledge` (`Player.java:1978`), `Player.learnFlag` (`Player.java:1728`),
`Player.learnInnate` (`Player.java:2039`). Chapter 7 owns the audit of those; they are listed here only so this file
does not read as though they are missing.

## Genuinely outstanding

Nothing rune-specific. The stubs those Chapter 7 functions lean on — `Player.java:1248–1500`, the player-calc,
timed-effects and display placeholders, each flagged in its own Javadoc — are other chapters' work and do not block
anything here.
