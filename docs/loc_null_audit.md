# Loc null-versus-`Loc.zero` audit

Audit of 2026-10-12. Paths are relative to `src/main/java/uk/co/jackoftradesltd/` unless they start
with `src/test`. Every file named here was read on the day of the audit, so check a line number
before relying on it. `ArgumentPointTest` was the only test run.

## Scope and result

- Nothing in `src/main` holds a `Loc` in a `Map`, `Optional<Loc>` field, array or `@Nullable` slot. The only `Optional`s are `Command.getArgPoint` and `GameInput.getPanel`.
- Only two of the 17 real Loc holders already say "no location" with `Loc.zero`. `Chunk.decoy` always holds `Loc.zero` or a value, and `PlayerUpkeep.pathDestination` is the same.
- Four holders can be null: `ItemObject.location`, `Target.grid`, `Monster.grid` and `ArgumentPoint.value`. Two more, `Trap.grid` and `Connector.grid`, are null and never read.
- Two of these are bugs in `Command.java`, not convention questions (see "Bugs found on the way").
- Only two double guards exist: `ItemObject.java:4060` and `Target.java:226`.

## Entry points: constructors, setters, field initialisers

- `Command.java:311-315` (`setArgPoint`): the guard is inverted. `if (value != null) value = Loc.zero;` overwrites every real point with zero and lets null through. C's `cmd_set_arg_point` (`cmd-core.c`) takes a `struct loc` by value, so it has no null case. Fix: change `!=` to `==`. The guard is then the only null defence and cannot go.
- `ArgumentPoint.java:49` (record component `value`): a null gets in through `new ArgumentPoint(null)`, and the class Javadoc says it is not rejected. `copy()` at `ArgumentPoint.java:79` is the first thing to fail. C's `union cmd_arg_data.point` is a by-value `struct loc`. Fix: add a compact constructor that turns null into `Loc.zero`. Then `Command.java:312` can go, and the `ArgumentPoint.java:79` NPE and its Javadoc line disappear.
- `ItemObject.java:323` (field `location`): it has no initialiser, and the no-arg constructor never sets it. The C counterpart is `object.h` `struct loc grid; /* position on map, or (0, 0) */`. C tests it with `loc_is_zero` at several sites, including `cave.c`, `obj-list.c`, `obj-pile.c`, `obj-knowledge.c` and `mon-make.c`. Fix: `private Loc location = Loc.zero;`.
- `ItemObject.java:1051` (constructor, parameter `location`): null can be passed in. Fix: `this.location = location == null ? Loc.zero : location;`.
- `ItemObject.java:1246-1247` (`setGrid`): it stores null on purpose, per its own Javadoc. Fix: `location = grid == null ? Loc.zero : grid;`.
- `ItemObject.java:6630` (`wipe`): `location = null`. C zeroes the struct, so the target is `Loc.zero`. Fix: `location = Loc.zero;`.
- `Monster.java:195` (constructor, parameter `grid`; field at `Monster.java:79`): null can be passed in, and 10 test sites do. C's `mon->grid` (`monster.h`) is zeroed by `mem_zalloc` until the monster is placed. Fix: `this.grid = grid == null ? Loc.zero : grid;`. These callers dereference it with no guard: `Chunk.java:2122`, `Chunk.java:2186`, `Chunk.java:3110`, `GameWorld.java:821` and `Target.java:101`.
- `Target.java:52` (static field `grid`): it has no initialiser, so it is null until the first `setTargetMonster` or reset. The `Target.java:112` reset already uses `Loc.zero`. C's file-scope `target` (`target.c`) starts zeroed. Fix: `private static Loc grid = Loc.zero;`.
- `Target.java:173` (`setGrid`): null can be passed in. The only caller is `Target.java:221`, with a monster's grid. That is safe once the `Monster.java:195` fix is in. No change.
- `Trap.java:58` (field `grid`): it has no initialiser, setter or getter, and `new Trap()` (`Square.java:1610`) is the only constructor. So it is always null and nothing reads it. C is `trap.h` `struct loc grid`, and C never tests it with `loc_is_zero`. Fix: `private Loc grid = Loc.zero;`. No guard exists to drop.
- `Connector.java:32` (field `grid`): same as `Trap.java:58`, with no accessors or writers. C is `cave.h` `struct connector`. Fix: `private Loc grid = Loc.zero;`.
- `Player.java:126` and `Player.java:132` (`grid`, `oldGrid`): both are set to `Loc.zero` at `Player.java:514`, `Player.java:518`, `Player.java:589` and `Player.java:593`. There is no setter, so they can't go null. C is `player.h` `p->grid` and `p->old_grid`. No change.
- `PlayerUpkeep.java:314` (`pathDestination`): `PlayerUpkeep.java:368` sets it to `Loc.zero`, and there is no setter. C is `player.h` `path_dest`. No change.
- `Chunk.java:179` (`decoy`): `Chunk.java:331` sets `Loc.zero` in the only constructor, and there is no setter. C's `cave_find_decoy` returns `c->decoy` (`cave.h`). The Java readers only call `equals(decoy)` (`ChunkUtils.java:430`, `ChunkUtils.java:496`, `ChunkUtils.java:555`), which is false for either null or zero. C's `project.c` also compares with `loc_eq`, not `loc_is_zero`. No change.
- `EventDataExplosion.java:121` (`centre`, `blastGrid`): the constructor stores both unchecked. C's `game-event.h` has `struct loc centre` and `struct loc *blast_grid`, and `centre` is a real position, so zero is not "none". `EventsHandler.java:283` (`eventSignalPoint`) dereferences `point` unguarded. No change.
- `GameInput.java:89` (`Rectangle` record): an absent panel is `Optional.empty()` at `DefaultGameInput.java`, and zero is a valid corner. No change.
- `PointSet.java:69` (`add`): it accepts null into the list. Zero is a legitimate member, so coercing would corrupt the set. No change.
- `Heatmap.java:85` and `Heatmap.java:95`: they dereference with no sentinel meaning. No change.
- `roombuilders/*.build(Chunk, Loc centre, int)` (21 stubs, for example `roombuilders/SimpleRoom.java:40`): all return `false` and never read `centre`. C's `gen-room.c` never tests it with `loc_is_zero`. No change.
- `Chunk.java:391-1715` (about 80 methods taking `@NotNull Loc grid`) and `Loc.sum` and `Loc.diff`: annotation only, with no guard. No change.

## Guards that become droppable

- `ItemObject.java:4060`: `known.getGrid() != null && !known.getGrid().isZero()` is a double guard. The `!= null` half is already dead, because `getGrid()` (`ItemObject.java:1227-1228`) never returns null. The line can be `!known.getGrid().isZero()` today.
- `Target.java:226`: `getGrid() != null && getGrid().getY() != 0 && getGrid().getX() != 0`. The `!= null` goes once `Target.java:52` has an initialiser. C's test is `target.grid.x && target.grid.y`, which is not `loc_is_zero`. A grid on row 0 or column 0 is unusable in C, so don't turn this into `!isZero()`.
- `ItemObject.java:1228`: the `location == null ? Loc.zero : location` ternary goes after `ItemObject.java:323`, `ItemObject.java:1051`, `ItemObject.java:1246` and `ItemObject.java:6630` all hold zero.
- `ItemObject.java:6159-6162`: `copy()`'s `if (this.location == null) copy.location = null;` branch goes for the same reason.
- `Command.java:312`: the guard goes once `ArgumentPoint` normalises null.
- `CommandQueue.java:397`: `(obj == null) ? Loc.zero : obj.getGrid()` guards the item, not the `Loc`, so it stays. The `Loc` side is already safe through `getGrid()`.

## Bugs found on the way

- `Command.java:498` (`getArgPoint`): an unset name returns `Optional.of(Loc.zero)`. Its Javadoc says empty, and C's `cmd_get_arg_point` returns `CMD_ARG_NOT_FOUND` and leaves `*grid` untouched. That makes "no point" read as "point at (0, 0)", which is the same sentinel mix-up this audit is about.
- `ArgumentPointTest` had 5 of 17 tests failing at the time of the audit, all from `Command.java:312` and `Command.java:498`. Four round-trip or overwrite tests fail on the zeroed point. `getArgPoint is empty when the name is unset…` fails on the `Optional.of(Loc.zero)` return.

## Tests that pin null and would need rewriting

All paths are under `src/test/java/uk/co/jackoftradesltd/`.

- `middle/objects/ItemObjectWipeTest.java:223`: `assertNull(read(item, "location"))`.
- `middle/objects/ItemObjectConstructionTest.java:166`: `assertNull(read(item, "location"))`.
- `middle/objects/ItemObjectCopyTest.java:360-374`: `diceAndGridStayNull` sets `location` to null and asserts the copy's is null.
- `middle/combat/TargetTest.java:470-474`: `okayGridNull` feeds `rawTarget(true, 0, null)`. It would NPE without the guard, so it needs rewriting or deleting.
- `middle/game/gameengine/argumentdata/ArgumentPointTest.java:131-134`: `new ArgumentPoint(null).copy()` is expected to throw NPE.
- `middle/objects/ItemObjectAccessorsTest.java:335-343` and `middle/objects/ItemObjectPartHRound2Test.java:86-102`: these call `setGrid(null)` and assert `getGrid().isZero()`. They stay green under the change.
- `middle/objects/ItemObjectFieldSurfaceTest.java:308`: `assertSame(Loc.zero, loaded().getGrid())` also stays green.
- The ten `new Monster(…, null, …)` sites need no rewrite if the constructor coerces. They need `Loc.zero` if it rejects null. The files are `PlayerUpkeepStructTest`, `PlayerIncCheckTest`, `SourceTest`, `ChunkMemberAccessTest`, `MonsterUpdateSmartLearnTest`, `MonsterIsSmartTest`, `ChunkTrapsAndStubsTest`, `MonsterPredicatesTest`, `MonsterIsStupidTest` and `MonsterIsCamoflagedTest`.
- `middle/cave/ChunkFindDecoyTest.java` and `middle/cave/ChunkUtilsProjectionPathTest.java` write `decoy` by reflection and never with null, so they are unaffected.

## Next step

- Start with `Command.java:312` and `Command.java:498`, since they were broken at the time of the audit and account for the 5 red tests.
- Then choose one convention for `ItemObject`, `Monster`, `Target` and `ArgumentPoint`: normalise null to `Loc.zero` at the entry points listed above, or reject it. Both pass the tests above, but they differ for the 10 `Monster` test sites.
