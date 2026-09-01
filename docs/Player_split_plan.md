# Splitting `Player.java` along C's file boundaries

Written 2026-09-01. `Player.java` is 6241 lines: 2424 of code, 3225 of Javadoc, 593 blank.

## Why now rather than later

- The class has absorbed six C translation units. `player.c` is only 515 lines of the 14,923 in the `player-*` /
  `obj-knowledge` group, so the merge is already lopsided.
- Still unported and destined for the same class if nothing changes: `player-attack.c` (1446),
  `player-path.c` (2069), `player-birth.c` (1480), `player-spell.c` (723).
- Call-site churn is currently near zero. Counting only `main/` and excluding Javadoc `{@link}`s, most of these methods
  have **0-3 real callers** outside `Player.java` (`slotByType` 11 and
  `timedGradeEq` 8 are the outliers). Nearly all the references are in tests, which are already named one-per-method and
  move mechanically. That cost only rises.

## The principle

One new Java class per C source file, named after it. A method's home is decided by
`grep -n "^[a-zA-Z_].*[ *]<c_name>(" src/*.c` in `/home/rowan/Desktop/Angband-4.2.6`, not by what the Java signature
happens to touch. Where C has a `static` helper, the Java stays package-private in the class that owns its translation
unit — that is what preserves the choke points (see Risks).

## What stays on `Player`

`Player.java` becomes the port of `struct player` plus `player.c` — the 515-line file, which really does contain these:

| Java                                                                                                    | C                              | span      |
|---------------------------------------------------------------------------------------------------------|--------------------------------|-----------|
| fields, constructor                                                                                     | `struct player`, `player_init` | 83-439    |
| `statDec`                                                                                               | `player_stat_dec`              | 2714-2784 |
| `adjustLevel`                                                                                           | `adjust_level`                 | 2785-2892 |
| `playerStatInc`                                                                                         | `player_stat_inc`              | 6057-6123 |
| `playerExpGain`                                                                                         | `player_exp_gain`              | 6124-6162 |
| `playerExpLose`                                                                                         | `player_exp_lose`              | 6163-6202 |
| `playerFlags`                                                                                           | `player_flags`                 | 4670-4677 |
| `flagsTimed`                                                                                            | `player_flags_timed`           | 4607-4669 |
| accessors, `opt`, `hasPlayerFlag`, `hasObjectFlag`, `getTimedEffect`, `getStateLight`, `isShapeChanged` | field access                   | scattered |

Estimated residue: ~1200 lines, most of it the field block's Javadoc.

## New files

All in `uk.co.jackoftrades.middle.player` unless noted. Line spans are current `Player.java`
line numbers, Javadoc included.

### 1. `PlayerKnowledge.java` — port of `obj-knowledge.c` (2357 C lines)

~1760 lines. Touches 14 fields, chiefly `itemKnowledge`, `gear`, `shape`, `race`.

| method                                                                                                       | span      | Done |
|--------------------------------------------------------------------------------------------------------------|-----------|------|
| `knowObject`                                                                                                 | 456-697   | Y    |
| `nonCurseRunesKnown`                                                                                         | 801-878   | Y    |
| `setBaseKnown`                                                                                               | 879-947   | Y    |
| `flavourAware`                                                                                               | 1019-1099 | Y    |
| `knowsEgo`                                                                                                   | 1100-1243 | Y    |
| `learnCurse`                                                                                                 | 3541-3563 | Y    |
| `learnBrand`, `learnSlay`, `knowsBrand`                                                                      | 3592-3657 | Y    |
| `learnFlag`, `knowsRune`, `knowsSlay`, `knowsCurse`, `learnRune` (3839)                                      | 3658-3880 | Y    |
| `updateObjectKnowledge`, `learnInnate`, `learnAllRunes`                                                      | 3881-4031 | Y    |
| `equipLearnOnDefend`/`OnRangedAttack`/`OnMeleeAttack`, `equipLearnFlag`, `cursesFindToA`/`ToD`/`ToH`/`Flags` | 4032-4503 | Y    |
| `equipLearnElement`, `objectCursesFindElement`                                                               | 5751-5964 | Y    |

### 2. `PlayerCalcs.java` — port of `player-calcs.c` (2751 C lines)

~1655 lines. Touches 27 fields — the widest reach of any cluster, and the reason it moves as one piece rather than being
subdivided.

| method                                                          | span                                     | Done |
|-----------------------------------------------------------------|------------------------------------------|------|
| `calcBonuses`                                                   | 1244-1727                                | Y    |
| `noticeStuff`                                                   | 1834-1883                                | Y    |
| `handleStuff`, `updateStuff`, `redrawStuff`                     | 2378-2648 (less `updateMonsters`, below) | Y    |
| `calcMana`, `averageSpellStat`                                  | 4678-4798                                | Y    |
| `calcBlows`, `calcShapechange`, `calcLight`, `adjustSkillScale` | 4818-5056                                | Y    |
| `calcHitpoints`, `updateBonuses`                                | 5057-5276                                | Y    |
| `calcInventory`, `calcSpells`                                   | 5277-5546                                | Y    |
| record `Extras`                                                 | 6203-6221                                | Y    |

### 3. `PlayerTimed.java` — port of `player-timed.c` (1131 C lines)

~590 lines once `flagsTimed` and `playerFlags` are left behind on `Player` (both are `player.c`).

| method                                                        | span      | Done |
|---------------------------------------------------------------|-----------|------|
| `clearTimed`                                                  | 1818-1833 | Y    |
| `decTimed`, `incTimed`, `incCheck`, `setTimed`                | 2991-3360 | Y    |
| `timedGradeEq`                                                | 4504-4606 | Y    |
| `playerIncTimed` (5939), `playerDecTimed`, `playerClearTimed` | 5965-6056 | Y    |

### 4. `PlayerHistory` — port of `player-history.c` (294 C lines)

`historyAdd` / `historyAddWithFlags` / `historyAddFull`, 2893-2975, ~83 lines. A
`PlayerHistory.java` already exists as the ledger data model; these three are the writers and belong on it, not in a new
file. Done

### 5. Moves out of the player package

These are not player code at all and go to existing homes in `middle.objects`:

| method                                                                                                                     | span                                                  | C file           | destination                 | done |
|----------------------------------------------------------------------------------------------------------------------------|-------------------------------------------------------|------------------|-----------------------------|------|
| `combinePack`, `invenCanStackPartial` (2023), `quiverAbsorbNum`, `preferredQuiverSlot`, `packSlotsUsed`                    | 1884-2299                                             | `obj-gear.c`     | `ObjectUtils`               | Y    |
| `gearInsertEnd`                                                                                                            | 5516-5532                                             | `obj-gear.c`     | `ObjectUtils`               | Y    |
| `slotByType`, `slotByName`, `slotByNumber`, `numberFromSlot`                                                               | 5613-5736                                             | `obj-gear.c`     | `ObjectUtils`               | Y    |
| `gearToLabel`, `isCarried`                                                                                                 | 948-1018                                              | `obj-gear.c`     | `ObjectUtils`               | Y    
| `ignoreKnownItemOk`, `ignoreDrop`, `ignoreItemOK`, `isIgnored`                                                             | 1728-1739, 2300-2377, 5547-5612                       | `obj-ignore.c`   | new `ObjectIgnore.java`     | Y    |
| record `SplitBetweenPackAndQuiver`                                                                                         | 6222-6242                                             | —                | with `quiverAbsorbNum`      | Y    |
| `updateMonsters`                                                                                                           | 2515-2552                                             | `mon-util.c`     | `middle.monsters` utils     | Y    |
| `embody`                                                                                                                   | 4799-4817                                             | `player-birth.c` | future `PlayerBirth.java`   | Y    |
| `playerOfHasNotTimed`, `playerIsImmune`, `attackRandomMonster`, `playerRestingCount`, `restingIsSpecial`, `setRecallDepth` | 1763-1787, 2649-2666, 3361-3424, 3440-3454, 3485-3494 | `player-util.c`  | existing `PlayerUtils.java` | Y    | 
| `isQuest`                                                                                                                  | 3509-3526                                             | `player-quest.c` | future `PlayerQuest.java`   | Y    |

`updateMaxLevel` (3571-3578) and `updateDungeonDepth` (3579-3591) have no C original found; leave them on `Player` until
their provenance is established.

## Mechanics

**Form.** Static methods taking the player as first argument — `PlayerKnowledge.learnBrand(player,
brand)` — matching `ObjectUtils.equipLearnFlag(Player, ObjectFlag)`, which is already written that way. This keeps the
Java argument list identical to C's, so the port stays readable line-for-line against the original.

**Do not copy `PlayerUtils`'s cached-static player.** `PlayerUtils` holds
`player = GameState.getPlayer()` in a static initialiser with a documented load-order hazard. New files take the player
as a parameter.

**Field access is the real cost.** All 63 fields are `private`, and Java gives a same-package class no access to them.
Each move needs one of:

- package-private accessors on `Player` (recommended — public API unchanged, and the `middle.player`
  package boundary then does the job C's `static` does);
- widening the fields themselves to package-private (fewer lines, but loses the Javadoc anchor the accessors would
  carry).

There is currently no accessor at all for `itemKnowledge`, `gearKnown`, `race`, `shape`, `statCur`,
`statMax`, `food`, `exp` (setter), `level` (setter) — those are what the first two moves need.

**Package placement for `PlayerKnowledge`.** The Javadoc above `cursesFindToA` (from 260815)
argues the family lives on `Player` because "C's file boundary is this port's package boundary", which keeps `learnRune`
package-private. That argument survives the split only if the whole
`obj-knowledge.c` cluster lands in one package. Two readings:

- `middle.player.PlayerKnowledge` — needs only package-private accessors on `Player`, but the file is named for a player
  concept while C names it for objects;
- `middle.objects.ObjectKnowledge` — exact match to C's file, and `learnRune` stays package-private next to
  `KnownObject`, but every field it touches needs a *public* getter on `Player`.

Recommendation: `middle.player.PlayerKnowledge`. Public getters on `Player` for `itemKnowledge` and
`shape` are a worse trade than a filename that reads player-first, and that comment needs an amendment either way.

## Suggested order

1. **`PlayerHistory` writers** (~83 lines, 5 fields, one test file). Smallest possible rehearsal of the accessor
   question.
2. **`PlayerTimed`** (~590 lines, 12 fields). Self-contained: `timed` plus upkeep redraw flags. Nine test files move
   with it.
3. **The `obj-gear.c` / `obj-ignore.c` evictions** (~710 lines). No new Java concepts, and it deletes the largest block
   of non-player code.
4. **`PlayerKnowledge`** (~1760 lines). The big one; do it after the accessor pattern is settled.
5. **`PlayerCalcs`** (~1655 lines). Last, because it reaches 27 fields and needs the most accessors.

After 1-5, `Player.java` is roughly 1200 lines and every remaining method is `player.c`.

## Risks

- **The `learnRune` choke point.** `learnRune` (3839) is package-private because C keeps it
  `static`, and it is the single guarded entry that fires the second update and the group fan-out. Every `learnX`
  wrapper must move in the *same* commit as `learnRune`, into the *same* package. Splitting them across two moves would
  silently make `learnRune` unreachable and force it public.
- **A duplicate already exists.** `ObjectUtils.equipLearnFlag(Player, ObjectFlag)` is a stub of the same function
  `Player.equipLearnFlag` (4198-4503) implements. The knowledge move must delete the stub, not add a third copy.
- **`calcBonuses` must not be subdivided.** At 484 lines it is the single largest method, and it reads nearly every
  field. It moves whole or not at all.
- **Field-usage counts above are approximate** — they were derived by name matching, so parameters named `state` or
  `level` inflate them. Confirm per method before adding an accessor.
- **Javadoc cross-references.** Roughly 3225 comment lines carry `{@link #method}` forms that become
  `{@link PlayerCalcs#method}` after a move. The IDE's Move Members refactor rewrites these; a hand-move will not.
