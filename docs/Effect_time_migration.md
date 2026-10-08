# Effect time migration — move `time` off `Effect` onto its owner

Drafted 261005. Plan only; nothing in `src/main/**` has been changed by this plan.

**Amended 261008 for the curse unflattening** (`Curse_object_unflattening.md`). That work landed first, as its
"Interaction" section recommended, so `Curse` is no longer a flattened class with its own mechanical fields: C's
`curse->obj` is a real `ItemObject`, reached through `curse.getItemObject()`. `Curse` drops out of this plan's owner
changes. The curse's time goes straight onto `curse.getItemObject().setTime(...)`, as C's `parse_curse_time` writes
`curse->obj->time`. Everything below that concerns `ObjectKind`, `Effect`, `EffectBlock.g4` and the other grammars is
unchanged.

## Why

- C's `struct effect` (`object.h`) has no `time` member. `time` lives on the owner: `object_kind`,
  `curse->obj`, `ego_item`, `artifact`, and is copied onto `obj->time` by `obj-make.c`.
- The Java port attaches `time:` to an `Effect` because the shared `EffectBlock.g4` rule
  `effectBlock` swallows a trailing `time:` line. With several effects on one kind, the time lands on the **last**
  effect only; earlier effects get `null`.
- Result today for kinds: `ItemObjectAssembler` passes `""` as `ObjectKind`'s time, so `ObjectKind.time` is
  `null` for every loaded kind, and `ObjectUtils.objectPrep` copies that (as zero) onto every item. The 43 `time:`
  values in `object.txt` are parsed but never reach an item.
- Result today for curses: the readers are already moved. `GameWorld` and `ObjectUtils` read
  `curse.getItemObject().getTime()`, but `CurseAssembler` never writes it, so every curse object holds a zero
  `Random`. Ten curses with a `time:` line disagree with the data file, and
  `CurseDataFileConformanceTest.everyCurseMatchesTheDataFile()` fails for exactly those ten on `obj time`.
- `ArtifactGrammar.g4` and `EgoItemsGrammar.g4` already keep `time` on the owner via their own
  `time` rule. This plan brings kinds and curses into line with them.

## Data affected

Only three data files carry `time:` lines (`lib/gamedata/`):

| File           | `time:` lines | Parsed via      | Affected?                               |
|----------------|---------------|-----------------|-----------------------------------------|
| `object.txt`   | 43            | `effectBlock`   | Yes — moves to `ObjectKind`             |
| `curse.txt`    | 10            | `effectBlock`   | Yes — moves to the curse's `ItemObject` |
| `artifact.txt` | 67            | own `time` rule | No                                      |

All 53 `effectBlock`-captured lines sit after the record's last `effect:`; no record has `time:`
without an effect. Every other grammar that uses `effectBlock` (activations, chest traps, traps, monster spells, shapes,
player classes) has no `time:` data, so dropping it from them loses nothing.

## Target design

- **`EffectBlock.g4`**: keep the standalone `time` rule (so importing grammars can call it), but remove the optional
  `(time { ... })?` clause from `effectBlock` and the `timeDiceString` return.
- **`ItemObjectGrammar.g4` / `CurseGrammar.g4`**: add `| time { timeInit = $time.timeStr; }` as an alternative in the
  record's `(...)+` loop, as `ArtifactGrammar.g4` does. `time:` then belongs to the record, wherever it appears.
- **`ItemObjectParseRecord` / `CurseParseRecord`**: gain a `String time` component.
- **`ObjectKind`**: the constructor's `String time` parameter becomes `Random time`, never `null`
  (zero `Random` when the line is absent, matching C's zeroed `random_value`).
- **`Curse`**: no change. It has no `time` field and no `getTime()`; the dice live on `curse.getItemObject()`, which
  already has `setTime`/`getTime` and never holds `null`. `CurseAssembler` calls `setTime` on the object it builds.
- **`Effect`**: loses the field, the constructor parameter, `getTime()`, and the copy in `copy()`.

## Decision for Rowan before starting

None outstanding. The old question, how `Curse` receives its time (constructor parameter or setter), went away with the
unflattening: the time is set on the curse's `ItemObject` before the `Curse` is built, as the other mechanical
properties already are, and `Curse`'s constructor does not change.

## Files that change

### Owners and their accessors (`src/main/**`)

| File                             | Change                                                                                                               |
|----------------------------------|----------------------------------------------------------------------------------------------------------------------|
| `middle/objects/ObjectKind.java` | 45-arg constructor: `String time` → `Random time`, stored as given (or zero if `null`); drop `Random.parseStr(time)` |
| `middle/objects/Curse.java`      | **none** — see the amendment note                                                                                    |
| `middle/game/GameWorld.java`     | **none** — the curse-timeout loop already reads `curse.getItemObject().getTime()`                                    |
| `middle/effect/Effect.java`      | remove `time` field, constructor param, `getTime()`, the time line in `copy()`                                       |

### Assemblers (`src/main/**`)

| File                                                      | Change                                                                                                                                     |
|-----------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------|
| `backend/parser/grammars/EffectAssembler.java`            | drop `Random.parseStr(record.timeDiceString())` and the `time` constructor argument                                                        |
| `backend/parser/itemobject/ItemObjectAssembler.java`      | parse `record.time()` (zero if empty, error if unparseable) and pass it in place of `""`                                                   |
| `backend/parser/curse/CurseAssembler.java`                | parse `record.time()` the same way and `itemObject.setTime(...)` on the object it builds; also drop the class Javadoc's "Outstanding" note |
| `backend/parser/playerclass/ClassSpellBookAssembler.java` | pass a zero `Random` instead of `""` for time (and it still needs the `power` argument added)                                              |

### Parse records (`src/main/**`)

| File                                                   | Change                                             |
|--------------------------------------------------------|----------------------------------------------------|
| `backend/parser/grammars/EffectParseRecord.java`       | remove `timeDiceString` component and its `@param` |
| `backend/parser/itemobject/ItemObjectParseRecord.java` | add `String time` component                        |
| `backend/parser/curse/CurseParseRecord.java`           | add `String time` component                        |

### Grammars (`backend/parser/grammars/`)

| File                     | Change                                                                                                                |
|--------------------------|-----------------------------------------------------------------------------------------------------------------------|
| `imports/EffectBlock.g4` | remove `(time ...)?` from `effectBlock`, remove `timeDiceString` from its `returns` and `@init`; keep the `time` rule |
| `ItemObjectGrammar.g4`   | add `time` alternative + `timeInit`; pass it to `ItemObjectParseRecord`; drop `timeDiceString` arg                    |
| `CurseGrammar.g4`        | add `time` alternative + `timeInit`; pass it to `CurseParseRecord`; drop `timeDiceString` arg                         |
| `ActivationsGrammar.g4`  | drop `$effectBlock.timeDiceString` arg                                                                                |
| `ChestTrapGrammar.g4`    | drop `$effectBlock.timeDiceString` arg                                                                                |
| `MonsterSpellGrammar.g4` | drop `$effectBlock.timeDiceString` arg                                                                                |
| `PlayerClassGrammar.g4`  | drop `$effectBlock.timeDiceString` arg                                                                                |
| `ShapeGrammar.g4`        | drop `$effectBlock.timeDiceString` arg                                                                                |
| `TrapGrammar.g4`         | drop the arg at both `new EffectParseRecord` sites (`effectBlock` and `effectXtraBlock`, where it is a literal `""`)  |
| `PlayerTimedGrammar.g4`  | drop one `""` at both `new EffectParseRecord` sites (`onBeginEffect`, `onEndEffectBlock`)                             |

### Generated parsers — regenerate, don't edit

Generation is IDE-driven from `.idea/misc.xml`. All nine grammars above have an entry. Regenerate:

- `activations/ActivationsGrammar.java`
- `chesttrap/ChestTrapGrammar.java`
- `curse/CurseGrammar.java`
- `itemobject/ItemObjectGrammar.java`
- `monsterspell/MonsterSpellGrammar.java`
- `playerclass/PlayerClassGrammar.java`
- `playertimed/PlayerTimedGrammar.java`
- `shape/ShapeGrammar.java`
- `trap/TrapGrammar.java`
- `imports/effectblock/EffectBlock.java` — **note:** `.idea/misc.xml` has an entry for
  `EffectBlockLexer.g4` only, not for `EffectBlock.g4`; check how this file was produced before relying on a regenerate.

A stale generated parser will keep passing `timeDiceString` and fail to compile against the new
`EffectParseRecord` — that is the check that regeneration happened.

### Tests (`src/test/**` — Claude's side)

| File                                                               | Change                                                                                                             |
|--------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------|
| `backend/parser/grammars/imports/effectblock/EffectBlockTest.java` | `complexDiceWithTwoExprsAndTime` and the null-time assertion: rewrite, since `effectBlock` no longer captures time |
| `middle/effect/EffectAccessorsTest.java`                           | drop the time argument; remove the `getTime` copy assertions                                                       |
| `middle/objects/CurseObjectTimeTest.java`                          | its Javadoc says time is set by hand until this lands; add a case that loads through the assembler                 |
| `middle/objects/CurseDataFileConformanceTest.java`                 | no change — it already asserts `obj time` for all 27 curses and is the check that this plan worked                 |
| `middle/objects/CurseIsFullyKnownTest.java`                        | drop the `Effect` time argument                                                                                    |
| `middle/objects/ObjectUtilsCopyCursesTest.java`                    | drop the `Effect` time argument (its `effectWithTime` helper); the curse's time is already set on its object       |
| `middle/objects/ObjectUtilsObjectPrepTest.java`                    | drop the `Effect` time argument                                                                                    |
| `middle/objects/ItemObjectMergeHelpersTest.java`                   | drop the `Effect` time argument                                                                                    |
| `middle/objects/ObjectKindTest.java`                               | 45-arg `ObjectKind` call: `String` → `Random` for time                                                             |
| `backend/parser/ItemObjectReaderTest.java`                         | add: a kind's `time:` reaches `ObjectKind.getTime()`, absent → zero                                                |
| `backend/parser/CurseReaderTest.java`                              | add: a curse's `time:` reaches `getItemObject().getTime()`; absent → zero                                          |

The 13 test files that call `new Curse(...)` need nothing from this plan: the constructor does not change.

### Javadoc (after the code lands)

- `Curse.java` — none from this plan; the unflattening already rewrote it.
- `ObjectUtils.java` — the `copyCurses` block links `{@link Curse#getTime()}`, which no longer exists; point it at the
  curse object's `getTime()`.
- `Effect.java` — remove the `time` field note; the `copy()` block lists the time dice as copied.
- `ObjectKind.java` — 45-arg constructor block (also already outstanding: wrong "dice string" and
  "copying the brand/slay" wording, missing `@param power`).
- `EffectParseRecord.java`, `ItemObjectParseRecord.java`, `CurseParseRecord.java` — `@param`s.

## Suggested order (keeps the build compiling between steps)

1. **Add first.** `time` on `ItemObjectParseRecord`, `CurseParseRecord`; `time` alternatives in
   `ItemObjectGrammar.g4` and `CurseGrammar.g4`; regenerate those two. (`effectBlock` still grabs a trailing `time:` at
   this point, so the new alternative won't fire yet — that's expected.)
2. **Owners.** `ObjectKind` takes `Random time`; the assemblers fill the kind and the curse object from their records
   (`ItemObjectAssembler` into `ObjectKind`, `CurseAssembler` via `itemObject.setTime`). `Curse` and `GameWorld` need no
   change. The curse `time` is still empty here, because `effectBlock` swallows the line until step 3.
3. **Remove.** `(time ...)?` out of `effectBlock`; `timeDiceString` out of `EffectParseRecord`, the nine grammars, and
   `EffectAssembler`; `time` out of `Effect`. Regenerate all ten parsers. The ten `obj time` failures in
   `CurseDataFileConformanceTest` clear at this step, once the new `time` alternatives see the line.
4. **Tests**, then the Javadoc pass, then `./gradlew test`.

## Done when

- `ObjectKind.getTime()` for a rod in `object.txt` matches its `time:` line; a kind without one returns a zero `Random`,
  never `null`.
- `curse.getItemObject().getTime()` for each of the 10 `curse.txt` curses with a `time:` line matches it, and the others
  hold a zero `Random`. `CurseDataFileConformanceTest` passes for all 27 curses.
- `grep -rn "timeDiceString\|Effect.*getTime" src/main` finds nothing outside generated history.
- Suite green. This also closes the `time` item in `Curse_object_unflattening.md`'s "Done when".
