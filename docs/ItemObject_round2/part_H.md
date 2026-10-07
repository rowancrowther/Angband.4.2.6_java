# Part H: accessors and mutators (ItemObject.java), round 2 Stage 1

Run 2026-10-07. Read-only. All 65 Part H declarations in
`src/main/java/uk/co/jackoftradesltd/middle/objects/ItemObject.java` were read in full this turn, plus the field
declarations (`ItemObject.java:118-1037`), `src/main/java/uk/co/jackoftradesltd/channel/utils/Flag.java`
(`copyFrom`, `union`, `on`, `off`, `set`, `iterator`), and `struct object` in `object.h`. Every line number below is
from that read. C references name files and functions only.

## What matches

- All 65 declarations read the right `struct object` field. None reads or writes a neighbouring field, and none has a
  swapped pair (`getToHit`/`toHit`, `getToDam`/`toDam`, `getToAC`/`toAC`, `getBaseAC`/`baseAC` for C's `ac`,
  `getDamageDice`/`getDamageSides` for `dd`/`ds`, `getpValue` for `pval`, `getsValue` for `sval`, `gettValue` for
  `tval`).

- The declaration count is 65: position, kind and counts 16; combat and value 16; links and effect 13 (including
  `setEffectMessage`, `setHeldMIndex`, `setOrigin`); maps 9; flags and notices 10 (including `getObjectFlags`);
  `isArtifact` 1.

- Struct coverage: every field of `struct object` has a Java field except `oidx`, which the port deliberately does not
  carry (documented at `ItemObject.java:3889`, `Chunk.java:184` and `Chunk.java:1873`), and `prev`/`next`, which
  `ItemObject.java:911` (`owningPile`) stands for. `baseDamage` (`ItemObject.java:418`) has no C field, and says so.

- `getTime` (`ItemObject.java:2468`) returns `time.copy()` and `setTime` (`ItemObject.java:2490`) stores
  `time.copy()`, so both agree with C's `random_value` struct copy. The field is never null: both constructors
  (`ItemObject.java:864`, `ItemObject.java:1023-1025`), `setTime` and `copy` land on `Random.Zero()`. `Random.copy()`
  (`Random.java`, end of file) carries the four terms C's struct holds and drops only the expression text C never had.

- `getFlags` (`ItemObject.java:2377`) and `getNotice` (`ItemObject.java:2442`) each allocate a new `Flag` and call
  `copyFrom` on the live field. I re-read `Flag.copyFrom` (`Flag.java:478-483`): it snapshots the source into a scratch
  `Flag`, wipes, then unions the snapshot back. For a non-null source the result is an independent copy, so the
  2026-10-07 self-copy change does not alter either accessor, and neither passes `this` as its own source.

- `setFlags` (`ItemObject.java:3048`) is `flags.union(mask)`, C's `of_union` on `obj->flags`; passing the live set is a
  no-op returning false, as in C. `setFlag` (`ItemObject.java:3066`) is `flags.set(flag)`, C's `of_on`; `Flag.set(E...)`
  returns true only for a newly raised flag. `orNotice`, `setNoticeOn`, `setNoticeOff` and `getNoticeHas`
  (`ItemObject.java:1201`, `2516`, `2573`, `2985`) are `on`, `on`, `off` and `has` on the live notice set, which is C's
  `|=`, `&= ~` and `&` on the one-byte `notice`. The four `ObjectNotice` constants are in C's order (`OBJ_NOTICE_WORN`,
  `ASSESSED`, `IGNORE`, `IMAGINED`).

- `setFlagsTo` (`ItemObject.java:3085`) wipes then copies in, and every production caller passes a fresh `Flag`
  (`ObjectUtils.java:1128`, `PlayerKnowledge.java:276`, `PlayerKnowledge.java:400`, `PlayerKnowledge.java:1298`,
  `PlayerKnowledge.java:1313`, `CurseAssembler.java:336`). That is C's `of_wipe` then `of_copy`
  (`player_know_object` in `obj-knowledge.c` does exactly this onto `obj->known->flags`).

- `getModifierValue(Stats)` (`ItemObject.java:3308`) maps `STAT_x` to `OM_x` by name. `Stats` and `ObjectModifier`
  agree on the five real stats and on both sentinels (`STAT_NONE` to `OM_NONE`, `STAT_MAX` to `OM_MAX`), so the
  sentinels answer zero and do not throw, as the Javadoc says. `getModifierValue(ObjectModifier)`
  (`ItemObject.java:3328`) reads through `getModifiers()`, so an absent entry and a null map both read zero, which is
  C's zeroed `modifiers[]` slot.

- `getModifiers` (`ItemObject.java:2778`) and `getElInfo` (`ItemObject.java:2816`) hand back the live map, or an
  immutable `Map.of()` for a null field. C's `obj->modifiers[i] = x` and `obj->el_info[i].res_level = x` write through
  the struct, so a live view is the right shape. Their only in-class writer, `applyCurseAttributes`
  (`ItemObject.java:4873`, `4876`), works on a scratch copy whose map `copy()` always builds
  (`ItemObject.java:6608-6618`), so the immutable empty map cannot reach it.

- `setModifiers` (`ItemObject.java:2799`), `setElInfo` (`ItemObject.java:2835`) and `setEffect` (`ItemObject.java:2958`)
  store the argument by reference without clearing first, as the batch 9 fix left them. `setEffect` matches C's
  `obj->effect = kind->effect` pointer share, and its null-to-empty-list rule is an addition C does not need. Callers
  checked this turn all pass either a freshly built map or the kind's shared effect list:
  `ObjectUtils.java:1119`, `ObjectUtils.java:1138`, `ObjectUtils.java:1187`, `PlayerKnowledge.java:248`,
  `PlayerKnowledge.java:270`, `CurseAssembler.java:335-338`. No caller hands a kind's own modifier or element map to an
  item.

- `putModifier` (`ItemObject.java:2924`), `putElInfo` (`ItemObject.java:2903`) and `setElInfoResLevel`
  (`ItemObject.java:2871`) write one entry, creating the map on demand. `setElInfoResLevel` changes only `res_level` and
  leaves the flags, as C's field assignment does. A fresh `ElementInfo` starts at level 0 with empty flags, which is C's
  zero fill.

- `setGrid` (`ItemObject.java:1186`) stores the `Loc`. `Loc` has final coordinates (`Loc.java:66`, `Loc.java:72`), so
  sharing is as safe as C's `struct loc` assignment. `setKind`, `setEgo`, `setKnown`, `setOwningPile`, `setNote`,
  `setEffectMessage` store the argument as C's pointer or string assignment does.

- `getKnown` and `setKnown` (`ItemObject.java:2424`, `ItemObject.java:7125`) are C's `obj->known` pointer read and
  write; the two items are not linked back, and the Javadoc says so.

- `setOrigin` (`ItemObject.java:7106`) matches `obj->origin = ...`. `ObjectOriginEnum` starts at `ORIGIN_NONE`, C's
  zero.

- `tValue` defaults to `TV_NONE` in the no-argument constructor (`ItemObject.java:863`), C's tval 0 (`list-tvals.h`
  begins with `TV(NULL, ...)`).

- `getObjectFlags` (`ItemObject.java:6837`) returns the live field and says so; its two production callers
  (`ObjectUtils.java:1692`, `ObjectUtils.java:1733`) only call `has` on it.

- Accepted divergences from batch 9 hold and are not re-raised: the `int` fields do not narrow to `uint8_t` and
  `int16_t`.

## What does not match

Field-to-accessor mapping is clean. The two items below are null-handling differences from C's zeroed memory, both low
severity. Neither is on batch 9's accepted list.

- **1. Null `flags` or `notice` throws where C holds an empty array.**

    - Where: `ItemObject.java:2377-2380` (`getFlags`), `ItemObject.java:2442-2445` (`getNotice`), `ItemObject.java:2985`
      (`getNoticeHas`), `ItemObject.java:1201-1203` (`orNotice`), `ItemObject.java:2516` and `ItemObject.java:2573`
      (`setNoticeOn`, `setNoticeOff`), `ItemObject.java:3048`, `3066` and `3085` (`setFlags`, `setFlag`, `setFlagsTo`).
      `getObjectFlags` (`ItemObject.java:6837`) hands the null back. `hasFlag` (`ItemObject.java:2268`, Part F) fails
      the same way.

    - C struct field: `flags[OF_SIZE]` and `notice`, inline arrays that `object_new` zero-fills, so they are never
      absent.

    - Input that diverges: the 36-argument constructor (`ItemObject.java:976`) given `flags == null` or
      `notice == null`. It stores both by reference (`ItemObject.java:1013`, `ItemObject.java:1028`).

    - Java result: `getFlags` calls `Flag.copyFrom(null)`, which calls `union(null)`, which iterates null
      (`Flag.java:506`) and throws `NullPointerException`. The other accessors throw on the first call.

    - C result: an empty flag set and no failure.

    - Contrast: the map, set and curse getters absorb a null field (`ItemObject.java:2778`, `2816`), and the constructor
      Javadoc at `ItemObject.java:923-925` says the getters absorb it, naming `getEffect()` as the only exception. Flags
      and notice are two more exceptions, and the Javadoc does not list them.

    - Reach: no production code calls the 36-argument constructor (`ItemObject.java:916-917`); only tests do. The test
      builders I read (`ItemObjectClassificationTest.java:72`, `PlayerOfHasNotTimedTest.java:158`) pass a non-null
      notice set, and I found no test that passes a null flags set (grepped for `itemWithFlags(null`).

- **2. `getGrid` answers null where C reads the grid (0, 0).**

    - Where: `ItemObject.java:1171-1173`.

    - C struct field: `struct loc grid`, "position on map, or (0, 0)".

    - Input that diverges: `new ItemObject()` (the constructor leaves `location` null, `ItemObject.java:851-865`), a
      wiped item (`wipe` assigns `location = null` at `ItemObject.java:7035`), or any item whose `setGrid(null)` was
      called.

    - Java result: `getGrid()` is null, so `getGrid().isZero()` throws. C result: `loc_is_zero(obj->grid)` is true.

    - It is documented at `ItemObject.java:1163-1165` and `ItemObject.java:840-841`, and batch 8 accepted it for `wipe`
      only. The constructor case is not on any accepted list.

    - Callers already guard: `CommandQueue.java:397-398` and `ItemObject.java:3814`. Other callers write
      `Loc.zero` (`ObjectGear.java:141-142`, `Chunk.java:1849`), so "not on the floor" has two spellings, null and
      `Loc.zero`, and every reader has to test for both.

- **3. Documentation only: `setFlagsTo` Javadoc gives a reason the code does not follow.**

    - `ItemObject.java:3077` says "`Flag#copyFrom` wipes before it unions, so the wipe does not need saying twice", but
      `ItemObject.java:3086` does say it twice (`this.flags.wipe()` ahead of `copyFrom`).

    - Effect: with the 2026-10-07 `copyFrom`, which leaves a self-copy untouched, the explicit wipe is the only thing
      that would make `item.setFlagsTo(item.getObjectFlags())` empty the item's flags. C's `of_wipe` then `of_copy` on
      the same array also ends empty, so the behaviour agrees with C; only the sentence is wrong. No caller passes a
      live set (all six production callers listed above pass fresh sets).

## Out-of-scope observations

- Part A, constructor Javadoc: `ItemObject.java:919-921` lists `curses` among the arguments stored by reference, but
  `ItemObject.java:1018-1019` copies the entries into a new map from `cursesFactory()`. `ItemObject.java:925` also
  describes a null curse map as staying null, where the constructor reads it as empty.

- Part A, `copy`: `ItemObject.java:6608-6618` build `modifiers` and `elInfo` as `HashMap`, where `wipe` and the
  constructors build `LinkedHashMap` (`ItemObject.java:857`, `859`). Iteration order of a copy can therefore differ from
  its source.

- Part G/H boundary, Stage 2 gap: `setEffectMessage` (`ItemObject.java:7358`) has no Javadoc and no provenance line. It
  is the only Part H declaration in that state.

- `effectMessage` is write-only inside the class: its readers are `copy` (`ItemObject.java:6635`) and nothing else, and
  its one production writer is `CurseAssembler.java:342`. `heldMIndex` is the same: read only by `copy`
  (`ItemObject.java:6646`), written by `ObjectGear.java:140`, `Chunk.java:1850` and `Chunk.java:2199`.

- `ObjectUtils.doCurseEffect` (`ObjectUtils.java:233`) is a stub returning false. C's version reads
  `curse->obj->effect_msg`, so its port will be the first real reader of an effect message.

- `ObjectMake.makeFakeArtifact` (`ObjectMake.java:24`) is a stub taking an `ItemObject` and an `Artifact`. C's version
  assigns `obj->artifact`, which `ItemObject` has no setter for.

- `Pile.hasArtifact` (`Pile.java:252-257`) is used only by `Square.hasObjectArtifact` (`Square.java:410`) and from there
  `Chunk.squareChangeable` (`Chunk.java:716`), which matches `square_changeable` in `cave-square.c` (plain
  `obj->artifact`).

## Plan 'Look for' answers

### (1) Batch 9 'Not ported' list

- Re-checked against `ItemObject.java` this turn, every item on the list is still absent: no `getArtifact`, no
  `getEffectMessage`, no `getActivation`, no `getOriginDepth`, no `getOriginRace`, no `getHeldMIndex`, no `setBrands`.
  `ItemObject.java:7166` (the `getMimickingMIndex` Javadoc) and `ItemObject.java:7098` (`setOrigin`) agree.

- The list is narrower than the gap. Also absent: `getOrigin` (the field has only `setOrigin`, and a grep of
  `src/main/java` found no caller of a getter); setters for `artifact`, `activation`,
  `originDepth` and `originRace`; and a way to set the artifact or activation on a known half. The field types differ
  from C's too: `activation` is a `List<Activation>` (`ItemObject.java:661`) where C holds one pointer.

- Callers that have worked around a missing getter: none. I grepped `src/main/java` for every name on the list and for
  comments saying "no getter" or "workaround". No caller reads these fields through another route.

- Callers that have run into a missing setter: none yet, because the C writers are all unported. The production
  `ItemObject` writers that exist are `setOrigin` (`PlayerBirth.java:871`), `setHeldMIndex` and
  `setEffectMessage`. The C code that will need the rest is `gen-util.c` and `mon-make.c` (origin depth and race),
  `obj-make.c` (artifact, ego and kind activation), `obj-knowledge.c` `object_touch` (known half's artifact),
  `obj-knowledge.c` `object_see` (reads `held_m_idx`), `obj-info.c` and `obj-chest.c` (read origin depth and race), and
  `save.c`/`load.c`.

### (2) `object_to_hit`, `object_to_dam`, `object_to_ac`, `object_effect`

- Confirmed: `getToHit`, `getToDam`, `getToAC` (`ItemObject.java:2124`, `2141`, `2154`) and `getEffect`
  (`ItemObject.java:2942`) return the bare fields, and no Java counterpart to the four C functions exists anywhere in
  `src/main/java` (grepped for `objectToHit`, `objectEffect`, `objectNeedsAim` and similar).

- Every current caller wants the bare field, because the C site it ports reads `obj->to_a`, `obj->to_h`, `obj->to_d` or
  `obj->effect` directly:
    - `PlayerKnowledge.java:232-235` and `PlayerKnowledge.java:446-448` match `player_know_object` and
      `object_non_curse_runes_known` in `obj-knowledge.c` (`obj->known->to_a != obj->to_a`).
    - `PlayerKnowledge.java:1449`, `1560` match the `if (obj->to_a)` and `if (obj->to_d)` tests in
      `obj-knowledge.c`'s equip-learning code.
    - `ObjectKnowledge.java:68` and `ObjectKnowledge.java:72` match `object_has_rune` in `obj-knowledge.c`.
    - `ItemSource.java:117-160` feeds `calcBonuses`; `calc_bonuses` in `player-calcs.c` adds `obj->to_a`, `obj->to_h`,
      `obj->to_d` directly and applies curses separately, which `CurseSource.java:193-277` does.
    - `PlayerKnowledge.java:360`, `563`, `569`, `622` and `PlayerBirth.java:878` copy `obj->effect` onto the known half,
      as `object_set_base_known` does; `ObjectUtils.java:1119` is `object_prep`'s `obj->effect = kind->effect`.

- Where the C behaviour will be needed, none of it in Java yet:
    - `obj-desc.c` `object_desc` (the combat details), behind the stub `ItemObject.description`
      (`ItemObject.java:2298`).
    - `player-attack.c` (melee and ranged to-hit and damage), `obj-info.c`, `ui-player.c` and `cmd-wizard.c`, none of
      which has a Java port; the grep for melee and ranged attack helpers found nothing.
    - `object_effect`: `cmd-obj.c` (`use_aux`), `obj-util.c` (`obj_needs_aim`, `obj_has_effect`-style tests) and
      `obj-info.c`.

- Note for the future port: `object_to_hit` and friends loop curse indices from 1 and add `curses[i].obj->to_h` for each
  curse with non-zero power. On the known half the same call reads `obj->known->curses`, so the Java version has to work
  for a known item too.

### (3) `isArtifact` and the known half

- Confirmed: `isArtifact` (`ItemObject.java:1216-1218`) is `artifact != null`.

- C's `object_is_known_artifact` (`obj-knowledge.c`) is `obj->known != NULL && obj->known->artifact != NULL`, so it does
  not look at the real object's artifact at all. C's `obj_is_known_artifact` (`obj-util.c`) is
  `obj->artifact && obj->known && obj->known->artifact`. `isArtifact` equals neither.

- Reachability today: none. Every production caller maps to a C site that tests plain `obj->artifact`:
    - `PlayerKnowledge.java:347` and `obj-knowledge.c` (`seen = obj->artifact ? true : kind->everseen`).
    - `GameWorld.java:628` and `recharged_notice` in `game-world.c`.
    - `Pile.java:254`, through `Square.hasObjectArtifact` to `Chunk.java:716`, and `square_changeable` in
      `cave-square.c`.
    - `ObjectIgnore.java:245`, `ObjectIgnore.java:259` and `object_is_ignored` in `obj-ignore.c`.
    - `ItemObject.java:3609` and `ItemObject.java:3612` (Part F's `ignoreLevelOf`) and `obj-ignore.c`
      (`ignore_level_of`).

- The C sites that use the known-artifact tests have no Java counterpart: `effect-handler-attack.c` (destruction),
  `mon-make.c` (two sites), `generate.c` (level change), `obj-desc.c`, `obj-list.c`, `ui-knowledge.c`, `obj-util.c`
  (`compare_items`) and `obj-gear.c` (`gear_object_for_use`). Grepping `src/main/java` for known-artifact tests, birth
  option `birth_lose_arts`, `history_lose_artifact` or `markArtifactCreated` shows only the registry helper at
  `ObjectUtils.java:961`, whose sole caller is `PlayerBirth.java:608`.

- When one of those sites is ported it cannot be built from `isArtifact` alone, and there is currently no way to make
  the known half an artifact: `ItemObject` has no artifact setter, so `known.isArtifact()` is false for every known half
  built by production code (`PlayerBirth.java:874`, `ObjectDataLoader.java:393`). C sets it in `object_touch`.

## Next

- Decide on findings 1 and 2: accept both as documented divergences (and add them to the batch 9 accepted list), or
  change the accessors to read a null as C's zero. Finding 3 is a Stage 2 Javadoc fix. All three are yours to call.

- Then say "try that" and I will re-read `ItemObject.java` and re-run this part from scratch.

stopped on mismatch
