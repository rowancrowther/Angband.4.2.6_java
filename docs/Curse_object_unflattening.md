# Curse object unflattening — give `Curse` a real `ItemObject`

Drafted 261005. Plan only; nothing in `src/main/**` has been changed.

## Why

C's `struct curse` (`object.h`) is small:

```c
struct curse {
	struct curse *next;
	char *name;
	bool *poss;
	struct object *obj;
	char *conflict;
	bitflag conflict_flags[OF_SIZE];
	char *desc;
};
```

Everything mechanical lives on `curse->obj`: the template object that holds the curse's weight, flags, modifiers,
element info, combat penalties, effect, effect message and time, and its `known` twin. C reads these through
`curse->obj->x` everywhere, and `player_know_object` runs on a curse object exactly as it does on a normal item.

The port has *flattened* `curse->obj` into `Curse` as 17 of its own fields. `Curse.itemObject`
exists, but it is an empty `ItemObject` the constructor creates and nothing reads or writes. Results:

- `Curse` carries a second copy of `ItemObject`'s field set (and a hand-rolled `known*` copy of
  `ItemObject.known`), so every piece of object logic has to be written twice — once for items, once for curses.
  `CurseSource` and part of `PlayerKnowledge` exist mainly to bridge that gap.
- `time` sits on the curse's `Effect` rather than on the object (see `Effect_time_migration.md`).
- The `msg:` line is kept on `Curse.message`, where C keeps it on `curse->obj->effect_msg`.
- `write_curse_kinds` (`obj-init.c`) has no port: the curse object gets no `<curse object>` kind, no sval, and its
  `known` object is not marked `OBJ_NOTICE_ASSESSED`.

## What moves and what stays

### Stays on `Curse` (C's `struct curse` members)

| `Curse` field               | C                                      |
|-----------------------------|----------------------------------------|
| `name`                      | `curse->name`                          |
| `objectBases`               | `curse->poss`                          |
| `conflictNames`, `conflict` | `curse->conflict` (raw string)         |
| `conflictFlags`             | `curse->conflict_flags`                |
| `description`               | `curse->desc`                          |
| `index`                     | position in `curses[]` (port addition) |
| `itemObject`                | `curse->obj` — **now populated**       |
| `logger`                    | —                                      |

### Moves onto `curse.getItemObject()` (C's `curse->obj`)

| `Curse` field (removed)   | `ItemObject` target         | Setter today                  |
|---------------------------|-----------------------------|-------------------------------|
| `weight`                  | `weight`                    | `setWeight`                   |
| `objectFlags`             | `flags`                     | `setFlagsTo`                  |
| `modifiers`               | `modifiers`                 | `setModifiers`                |
| `elInfo`                  | `elInfo`                    | `setElInfo`                   |
| `combatToHit`             | `toHit`                     | `setToHit`                    |
| `combatDam`               | `toDam`                     | `setToDam`                    |
| `combatAC`                | `toAC`                      | `setToAC`                     |
| `effect`                  | `effect` (a `List<Effect>`) | `setEffect`                   |
| `message`                 | `effectMessage`             | **none found** — needs adding |
| *(time, from the effect)* | `time`                      | `setTime`                     |
| —                         | `kind` = `<curse object>`   | `setKind`                     |
| —                         | `sValue` = that kind's sval | `setsValue`                   |

### Moves onto `curse.getItemObject().getKnown()` (C's `curse->obj->known`)

| `Curse` field (removed)                                     | Known-object target                                                            |
|-------------------------------------------------------------|--------------------------------------------------------------------------------|
| `knownCombatToHit` / `knownCombatToDam` / `knownCombatToAC` | `toHit` / `toDam` / `toAC`                                                     |
| `knownModifiers`                                            | `modifiers`                                                                    |
| `knownElInfo`                                               | `elInfo`                                                                       |
| `knownObjectFlags`                                          | `flags`                                                                        |
| `knownEffect`                                               | `effect`                                                                       |
| —                                                           | `kind`, `sValue`, `notice` += `OBJ_NOTICE_ASSESSED` (from `write_curse_kinds`) |

## Interaction with the Effect time plan

`Effect_time_migration.md` asks whether `Curse` gets `time` by constructor or by setter. If this plan goes first, that
question goes away: the curse's time lands on `curse.getItemObject().setTime(...)`, as C's `parse_curse_time` writes
`curse->obj->time`. **Recommendation: do this plan first**, then run the time migration with `Curse` dropped from its
scope.

## Decisions for Rowan before starting

1. **Delegators or direct reads?**
    - **A (recommended end state):** callers read `curse.getItemObject().getX()`, as C reads
      `curse->obj->x`. `Curse` loses the 17 accessors outright.
    - **B (migration aid):** keep `Curse.getWeight()` and the rest as one-line delegators for a phase, migrate callers,
      then delete. Fewer simultaneous breakages; more passes.
2. **Where does `write_curse_kinds` live?** Curses load *before* object kinds (`GameConstants.java`: `loadCurses()` then
   `loadItemObjects()`, because `object.txt` names curses), so the `<curse object>` kind does not exist when
   `CurseAssembler` runs. C has the same ordering and solves it with a post-pass. The port needs one too — e.g. a step
   in `ObjectDataLoader` or
   `GameConstants` after `loadItemObjects()` that sets `kind`, `sValue` and the known object on every curse.
3. **Does `knowObject(Player, Curse)` survive?** C calls the same `player_know_object` for items and curse objects. Once
   the curse has a real object, `PlayerKnowledge.knowObject(Player, Curse)` may reduce to
   `knowObject(player, curse.getItemObject())`. Worth a stage 1 comparison before deciding; out of scope for the
   mechanical move.
4. **Does `CurseSource` survive?** It is the `BonusSource` adapter over the flattened fields. With a real object it
   might become an `ItemSource` over `curse.getItemObject()`. Its Javadoc says the known half is constant-zero because
   "nothing in `obj-knowledge.c` fills it in", which needs re-checking against the `known*` writes in `PlayerKnowledge`
   before anything is changed.

## Files that change

### Domain (`src/main/java/uk/co/jackoftradesltd/middle/`)

| File                                              | Change                                                                                                                                                                                                                                       | Call sites (approx.) |
|---------------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|----------------------|
| `objects/Curse.java`                              | remove 17 flattened fields + their getters/setters; constructor slims to the `struct curse` members plus a populated `ItemObject`; rework `modifyWeightForCurse`, `hasStandardToH`, `isFullyKnown`, `getTime`, `toString` to read the object | —                    |
| `objects/ItemObject.java`                         | callers switch to `curse.getItemObject()` (mostly `getModifiers`, `getCombatAC`, `getCombatToHit`/`Dam`); add an `effectMessage` setter if none exists                                                                                       | ~39                  |
| `player/PlayerKnowledge.java`                     | `knowObject(Player, Curse)` and related: the `setKnown*`/`putKnownElementInfo` writes go to `curse.getItemObject().getKnown()`                                                                                                               | ~23                  |
| `objects/CurseSource.java`                        | accessors read the curse object (or the class is replaced; see decision 4)                                                                                                                                                                   | ~7                   |
| `game/globals/registry/UIEntryValueRegistry.java` | flag/element/modifier reads, including `getKnownObjectFlags`                                                                                                                                                                                 | ~7                   |
| `objects/ObjectUtils.java`                        | `getTime`, `isFullyKnown`, `getKnownObjectFlags`, `getKnownElInfo` reads                                                                                                                                                                     | ~6                   |
| `game/GameWorld.java`                             | curse-timeout loop: `curse.getEffect().getTime()` → the curse object's `getTime()`                                                                                                                                                           | 1                    |

Call-site counts are a grep for `curse.<accessor>` patterns. A few `ItemObject.java` hits may turn out to be the item's
own accessor on a curse-named variable; confirm with the IDE's find-usages before editing.

### Loading (`src/main/java/uk/co/jackoftradesltd/`)

| File                                                                                            | Change                                                                                                                                                         |
|-------------------------------------------------------------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `backend/parser/curse/CurseAssembler.java`                                                      | build and populate the `ItemObject` (weight, flags, modifiers, element info, combat, effect list, message, time) instead of passing 10 loose values to `Curse` |
| `middle/game/globals/loaders/ObjectDataLoader.java` or `middle/game/globals/GameConstants.java` | new `write_curse_kinds` post-pass after `loadItemObjects()` (decision 2)                                                                                       |

No grammar or parse-record change is needed: `CurseParseRecord` already carries every line, and only the assembler's
destination moves. (The time migration plan changes `CurseParseRecord` and
`CurseGrammar.g4` separately.)

### Tests (`src/test/**` — Claude's side)

| Scope                                                                                                                                                                                       | Count | Change                                                                                                                 |
|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-------|------------------------------------------------------------------------------------------------------------------------|
| Files calling `new Curse(...)`                                                                                                                                                              | 38    | slimmed constructor; mechanical properties set on the curse object (a shared fixture helper is the obvious first step) |
| Files calling curse accessors                                                                                                                                                               | 8     | read through `getItemObject()` (overlaps the 38)                                                                       |
| `CurseGetTimeTest`, `CurseIsFullyKnownTest`, `CurseKnownStateTest`, `CurseModifyWeightTest`, `CurseWeightBoundariesTest`, `CurseNullMapsTest`, `CurseDefinitionTest`, `CurseSource`'s tests | —     | rewrite around the object; `CurseNullMapsTest` may become obsolete, since `ItemObject` never holds null maps           |
| `backend/parser/CurseReaderTest.java`                                                                                                                                                       | —     | assertions move to the curse object                                                                                    |
| New                                                                                                                                                                                         | —     | the `write_curse_kinds` post-pass: kind is `<curse object>`, known object exists and is `ASSESSED`                     |

List the 38 with `grep -rln "new Curse(" src/test`.

### Javadoc (after the code lands)

- `Curse.java` — the class block (the flattening explanation and the `known*` paragraph) and every surviving method.
- `CurseSource.java` — class block, especially the "the port has no such object" paragraph.
- `PlayerKnowledge.java`, `BonusSource.java`, `ItemObject.java` — any block that says the curse object is flattened or
  empty. (`BonusSource.java` also carries an `obj-init.c` line-number citation to drop when rewritten.)

## Suggested order

1. **Populate without removing.** `CurseAssembler` fills `curse.getItemObject()` *as well as* the flattened fields. Add
   the `write_curse_kinds` post-pass. Tests for both. Build stays green.
2. **Switch readers**, one caller file at a time (`GameWorld` → `ObjectUtils` →
   `UIEntryValueRegistry` → `CurseSource` → `ItemObject` → `PlayerKnowledge`), running the suite after each.
3. **Switch the known writes** in `PlayerKnowledge` to the known object; `isFullyKnown` reads it.
4. **Remove** the flattened fields, accessors and the constructor's loose parameters; update the 38 test constructions.
5. Javadoc pass, then `./gradlew test`.

## Done when

- `Curse` has only `struct curse`'s members plus `index` and a populated `itemObject`.
- For each of the 10 `curse.txt` curses, `getItemObject()` matches the data file (weight, flags, modifiers, elements,
  combat, effect, message, time), its kind is `<curse object>`, and its known object exists and is `ASSESSED`.
- `grep -rn "getCombatToHit\|getKnownObjectFlags\|setKnownModifiers" src/main` finds nothing on `Curse`.
- Suite green.
