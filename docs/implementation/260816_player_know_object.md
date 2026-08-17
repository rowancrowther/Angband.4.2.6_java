# 260816 — `player_know_object` port: file-by-file correctness

Audit of the working tree as read on 2026-08-16, against
`/home/rowan/Desktop/Angband-4.2.6/src/obj-knowledge.c` (`player_know_object`, `obj-knowledge.c:1018`)
and its helpers.

## Build and suite state

The compile blocker (`ObjectKind.ignore` retyped to `Flag<IgnoreFlag>`, two stale call sites) and the inverted early
return at `Player.knowObject:589` were both fixed on 260816. The tree builds.

`./gradlew test` — **1994 tests, 1 failure**:

```
PlayerRuneLearningTest > equipLearnFlag > an item without the flag has the flag ruled out on its
    known counterpart FAILED
```

That failure is finding 3 below arriving under its own steam, and is the strongest evidence in the suite for it.
`ItemObject.getKnownFlags()` routes through `getObjectFlags()`, which builds a new
`Flag` and copies into it, so `equipLearnFlag`'s write to the known set is made to a temporary and discarded. The
method's own Javadoc still says "Live, not a copy, because the point of it is to be written to" — the documentation is
right and the code no longer matches it. `getFlags()` already returns the live set, so `known.getFlags()` is the shape
wanted.

Ten further failures in `ItemObjectSimilarTest` were a stale test fixture rather than a code fault —
`ItemObject.modifiers` was retyped from `Map<…, String>` to `Map<…, Integer>`, which is right (C's
`obj->modifiers[i]` is an `int16_t` holding the rolled value, and the dice stay on the kind), but the fixture writes the
field reflectively so nothing failed at compile time. Fixed on 260816.

## File status

| File                                   | Status         | Note                                                                                                                                                                                                                                                                                                                          |
|----------------------------------------|----------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `middle/objects/enums/IgnoreFlag.java` | **correct**    | New two-value enum, matches C's `IGNORE_IF_AWARE` / `IGNORE_IF_UNAWARE`.                                                                                                                                                                                                                                                      |
| `middle/objects/EgoItem.java`          | **correct**    | Accessors only (`isEverSeen`, `getFlags`, `getElInfo`, `getBrands`, `getSlays`, `getCurses`); all plain reads and all present in `struct ego_item`.                                                                                                                                                                           |
| `middle/player/PlayerUpkeep.java`      | **correct**    | `orNoticeFlag`, `getQuiver`, `getInventory` — direct ports of `p->upkeep->notice                                                                                                                                                                                                                                              |= …`, `->quiver`, `->inven`. |
| `middle/cave/Square.java`              | **needs work** | `holdsObject` is correct (`square_holds_object`). `lightSpot()` is an empty stub, so the display refresh at the end of `flavourAware` does nothing.                                                                                                                                                                           |
| `middle/cave/Chunk.java`               | **correct**    | `getSquare` / `getWidth` / `getHeight` are what the new code uses, and they are right. (Chunk's older stubs at 1535–1705 are outside this work.)                                                                                                                                                                              |
| `middle/player/PlayerBody.java`        | **needs work** | `itemIsEquipped` correct. `equippedItemSlot` dereferences `slot.getItem()` without the null guard `itemIsEquipped` has — NPEs on the first empty slot.                                                                                                                                                                        |
| `middle/objects/ObjectKind.java`       | **needs work** | Fields and accessors are fine in themselves, but there is no `getDamageDice()` / `getDamageSides()` exposing `damageDice` / `damageSides`, which is what `setBaseKnown` actually needs (see below). The `ignore` retype to `Flag<IgnoreFlag>` is correct and its call sites are fixed.                                        |
| `middle/objects/KnownObject.java`      | **needs work** | Twelve-field `obj_k` stand-in is sound, but `toA` has no `getToA()` getter — which is why `knowObject` reaches for `getAc()` in its place.                                                                                                                                                                                    |
| `middle/objects/ItemObject.java`       | **needs work** | `getKnownFlags()` returns a *copy* and reads `known.known`; `getCurses()` returns `Collections.unmodifiableMap`. Both are used as write targets by `knowObject`. `description()` (1186) is still a stub, so the "You have …" / "On the ground:" messages cannot render.                                                       |
| `middle/player/Player.java`            | **needs work** | The main body. See `260816_functions_implemented.md` for the function-level breakdown. The inverted early return at 589 is fixed, so the rest of `knowObject` is now reachable — and the per-block findings below now matter in play rather than in theory. `flavourAware` and `isCarried` are correct, commented and tested. |

## Reading order for the fix pass

1. ~~The compile blocker (two call sites).~~ Done 260816.
2. ~~`Player.knowObject:589` — the inverted `if (itemKind != null) return;`.~~ Done 260816.
3. The two accessor shapes that block writes: `ItemObject.getKnownFlags` (copy — and this is what the one remaining
   suite failure is) and `ItemObject.getCurses` (unmodifiable).
4. The missing accessors: `KnownObject.getToA`, `ObjectKind.getDamageDice` / `getDamageSides`.
5. The remaining per-block issues in `knowObject`, `setBaseKnown`, `knowsEgo`,
   `nonCurseRunesKnown`, `gearToLabel`.

## Commented and tested on 260816

The functions the audit found correct now carry house-style Javadoc and have suites behind them. 43 new tests, all
passing.

| Suite                                     | Covers                                                                                                                      |
|-------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------|
| `middle/cave/SquareHoldsObjectTest`       | `Square.holdsObject` — identity rather than equality, deep piles, excision                                                  |
| `middle/player/PlayerBodyEquippedTest`    | `PlayerBody.itemIsEquipped` — the empty-slot skip C does not need, in each position                                         |
| `middle/player/PlayerUpkeepGearViewsTest` | `orNoticeFlag` idempotence and non-displacement; `getQuiver` / `getInventory` as live views                                 |
| `middle/objects/EgoItemAccessorsTest`     | the seven ego accessors; pins the `null`-for-untouched-modifier divergence from C                                           |
| `middle/player/PlayerFlavourAwareTest`    | `flavourAware` — awareness on the kind not the object, the effect, the ignore transition, the re-run guard; and `isCarried` |

Not covered, and why: `flavourAware`'s gear refresh calls `setBaseKnown`, which is on the needs-work list, so a fixture
carrying objects would be testing that instead; and its floor sweep needs
`Square.lightSpot`, which is an empty stub. Both branches are reached and survived in the suite, not asserted on.

The shape of the rest. Three findings are structural rather than local, and  
each needs an accessor that doesn't exist yet:

- ItemObject.getKnownFlags () returns a copy and reads known.known —          
  knowObject:585 uses it as a write target twice over.
- ItemObject.getCurses () returns Collections.unmodifiableMap, so the whole   
  curses block throws once reached.
- KnownObject.getToA () and ObjectKind.getDamageDice ()/getDamageSides () are   
  missing, which is why knowObject:555 substitutes getAc () and setBaseKnown:905 reads getToD ().getDice ().

Three inverted conditions beyond 589, all confirmed against C: knowsEgo:841  
(isSubset has the same argument order as C's flag_is_subset, so the test     
rejects an ego exactly when the player does know its flags),                 
nonCurseRunesKnown:829 (returns the negation of the answer), and             
setBaseKnown:904/906/908 (C fills the dice only when the known value is      
zero).

One that destroys state: knowObject:643 calls entry.curseData ().setPower (0)  
on the item's own curse map, not the known copy.