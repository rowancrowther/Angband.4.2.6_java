# Part B: pack order, stacking, absorb, split (Stage 1, round 2)

Run 2026-10-07. Read-only. In this turn I read every Part B declaration in `ItemObject.java` in full and every C
function the plan lists for Part B, and compared them clause by clause. Line numbers are as read this turn.

## What matches

- `earlierObject` (`ItemObject.java:1099`) agrees with C's `earlier_object` (`player-calcs.c`) on every step and in the
  same order. The two null tests come first, so a null candidate answers false and a null incumbent answers true. The
  `!store` book test, the usable-ammo test, the decreasing-type test, the flavour test, the increasing-sval test, the
  unaware-flavour test, the light-fuel test and the value test then follow.

- `earlierObject` type order: it compares `TValue` ordinals where C compares `tval`. I compared the `TValue` declaration
  order with `list-tvals.h` this turn and they are the same order (TV_NONE, CHEST, SHOT, ARROW, BOLT, BOW, DIGGING,
  HAFTED, POLEARM, SWORD, BOOTS, ... GOLD), so every `>` and `<` gives C's answer.

- `earlierObject` ammo walk: with `ammo_tval` = arrow, orig = arrow and new = bolt gives false (C false); orig = bolt
  and new = arrow gives true (C true). With a null `getAmmoTval()` (no launcher) both ammo tests are false and the order
  falls to tval, as C's `ammo_tval` of 0 does.

- `earlierObject` value walk: for ammo the cheaper stack goes first (`ItemObject.java:1148-1150`, C's
  `object_value(orig,1) < object_value(new,1)` returns false). For anything else the dearer goes first
  (`ItemObject.java:1152-1153`). Equal values answer false (no preference), as in C.

- `earlierObject` player refresh (`ItemObject.java:1110`) sits after the `canBrowse()` block. That is harmless, because
  `ObjectKind.canBrowse()` reads `GameState.getPlayer()` itself (`ObjectKind.java:1042`).

- `similar` (`ItemObject.java:1318`) agrees with C's `object_similar` (`obj-pile.c`) in order and logic.
    - It tests equipped items, then mimics, then the two `OSTACK_LIST` unknown-kind tests, then self, then kind, then
      flags (`isEqual` is a two-way subset, which is `of_is_equal`).
    - It then tests elements, then artifacts, then the tval switch.
    - The tval predicates (`isChest`, `isEdible`, `isPotion`, `isScroll`, `isRod`, `canHaveCharges`, `isMoney`,
      `isWeapon`, `isArmour`, `isJewellery`, `isLight`) agree with `obj-tval.c` member for member.
    - The timeout test `(t1||t2) && !light` else-if `t1 != t2` is C's pair of tests collapsed. For the light case, two
      lights with timeouts 5 and 7 give false, as in C.
    - The `OSTACK_LIST` known-versus-unknown test is last (`ItemObject.java:1396`), as in C.

- `similar` modifier walk (`ItemObject.java:1370-1385`), with the Java map holding only some modifiers:
    - OM_STR 2 on both gives equal, so it continues (C equal).
    - OM_STR 2 on one side and absent on the other gives false (C compares 2 with 0, so false).
    - OM_STR 0 on one side and absent on the other gives continue (C compares 0 with 0, so equal).
    - Absent on both gives continue (C equal).

- `checkElementStacking` (`ItemObject.java:1430`) reads an absent element as level 0 with no flags and compares only the
  hates and ignores bits, as C masks with `EL_INFO_HATES | EL_INFO_IGNORE`.
    - Walk: resist-fire level 1 on one side and absent on the other gives false (C 1 against 0, so false).
    - Walk: level 0 explicit against absent gives true (C equal).
    - Walk: both at level 1 with one carrying `EL_INFO_HATES` gives false (C masked flags differ).
    - It is called once from `similar` (`ItemObject.java:1343`).

- `cursesAreEqual` (`ItemObject.java:1530`) agrees with C's `curses_are_equal` (`obj-curse.c`), with the empty map
  standing for C's null array.
    - Both empty gives true (C both null, true).
    - One empty and the other non-empty gives false even if the other holds only a power-0 entry (C one null, false).
    - Both non-empty with the same curse at the same power gives equal.
    - Both non-empty with the same curse at powers 50 and 60 gives false (C false).
    - One side with the curse at power 0 and the other without the key gives equal (C 0 against 0).
    - One side with the curse at power 50 and the other without the key gives false (C 50 against 0).
    - Timeouts are ignored, as C ignores them.

- `originCombine` (`ItemObject.java:1622`) agrees with C's `object_origin_combine` (`obj-pile.c`), including the
  unique-race branches and the mixed-origin fallback.
    - Race is compared by reference (`!=`), as C compares pointers.
    - The unique branches copy origin, depth and race, and the keep branch changes nothing.
    - Neither or both unique gives `ORIGIN_MIXED`.
    - Same race with a different origin or depth gives `ORIGIN_MIXED`. Same race with the same origin and depth leaves
      the origin alone.

- `distributeCharges` (`ItemObject.java:1668`) agrees with C's `distribute_charges` (`obj-util.c`).
    - The charge change is `pval * amt / number` in `int` division, as in C. A wand with pval 7, number 3 and amt 1
      gives change 2, and the source is left with 5.
    - A whole-stack move (`amt == number`) leaves the source pval and timeout alone.
    - The rod timeout with `destNew` is `min(source.timeout, chargeTime*amt)`. The `!destNew` branch caps at
      `chargeTime*(dest.number+amt)`, and nothing is moved when `dest.timeout` is already at or past the cap. Both match
      C.
    - Rod walk: charge time 10, number 3, timeout 25, amt 1, `destNew`. The destination gets 10 and the source keeps 15
      (C the same).

- `objectIsInQuiver` (`ItemObject.java:3694`) agrees with `object_is_in_quiver` (`obj-gear.c`). It walks the quiver
  array, which is sized to `carry-cap:quiver-size` (`PlayerUpkeep.java:254`), and matches by identity.

- `mergeable` (`ItemObject.java:3725`) agrees with `object_mergeable` (`obj-pile.c`).
    - The store test wraps both the max-stack and the quiver tests, as in C.
    - The ammo test reads `this`, as C's `obj1` does.
    - The thrown limit is `slot_size / thrown_mult` in `int` division, so 40/5 = 8.
    - Walk: arrows 30 + 15 = 45 in the pack with `max_stack` 40 gives false. In the quiver with 20 + 25 = 45 it gives
      false on the slot limit. A thrown weapon 5 + 4 = 9 in the quiver gives false (limit 8).

- `objectStackable` (`ItemObject.java:3766`) agrees with `object_stackable` (`obj-pile.c`).
    - C compares quarks with `==`. `quark_add("")` in `z-quark.c` returns a real non-zero quark, so an empty inscription
      is a real note in C. The Java `String` is therefore the right model: `null` is note 0 and `""` is a non-zero
      quark.
    - Walk: "" against "!d" gives false in both. "" against null gives true in both.

- `objectAbsorb` (`ItemObject.java:3802`) agrees with `object_absorb` (`obj-pile.c`).
    - It captures `known` first, caps the count at `max_stack`, then merges, then deletes the known half, then deletes
      the absorbed object.
    - The excise is skipped for a zero grid, and `Loc.isZero` compares by value (`equals(zero)`).
    - Walk: 30 + 25 with `max_stack` 40 leaves the survivor at 40, as in C.
    - I walked the delete order against `Chunk.objectDelete` and `Chunk.delistObject` (`Chunk.java:1832`,
      `Chunk.java:1883`). After the known half is delisted from the player's cave, the orphan test in `objectDelete` for
      the real object is false, so the object is really removed, as in C where `delist_object` has nulled the player's
      slot first.
    - Called on a known half (`ObjectUtils.java:313`), the known's own `known` is null, so the known branch is skipped.
      `objectDelete(playerCave, known)` then removes it from `playerCave.objects`. This matches C's
      `object_delete(cave, player->cave, &known_obj)`.

- `objectAbsorbMerge` (`ItemObject.java:3969`) agrees with `object_absorb_merge` (`obj-pile.c`).
    - The known-effect copy is guarded on both knowns existing and the absorbed known having an effect, then calls
      `knowObject`.
    - The absorbed note replaces this one when it is non-null. Walk: an empty "" note replaces a real one in Java, and a
      non-zero empty quark replaces it in C (same).
    - Rod timeouts add. Wand, staff and gold pvals add, capped at 32767 (`min` is equal to C's
      `>= MAX_PVAL ? MAX_PVAL : total`).
    - The origin combine is last.

- `objectSplit` (`ItemObject.java:3902`) agrees with `object_split` (`obj-pile.c`).
    - Java aligns the known count before the copy, where C aligns it after `object_copy(dest, src)` and before copying
      the known. The result is the same, because `dest->number` is overwritten later.
    - The legality check comes after the copy and the alignment in both. The charges are distributed on both halves with
      `destNew` true. The counts, note and known note are written in C's order.
    - `copy(true)` gives the destination a deep copy of the known half (`ItemObject.java:6579-6583`), so
      `destination.getKnown()` is non-null whenever `this.getKnown()` is.
    - Walk: 10 split by 3 gives the destination 3 and the source 7. Split by 10 (a whole-stack split) throws, where C
      asserts. Split by 0 gives a destination of 0, in both.
    - The `oidx` reset has no port (the field does not exist; `Chunk.java:184` documents the list).

- `nullKnown` (`ItemObject.java:3856`) is `known = null`, as C's `obj->known = NULL`.

## What does not match

- Nothing found against the C source in any of the 14 Part B declarations.

- Walk of the batch 1 heading's findings against the code read this turn (the heading says "Stage 1 FAILED, waiting on
  Rowan" and the body says the findings were fixed). The body is true and the heading is stale. Each finding in turn:
    - `cursesAreEqual` treats power zero as equal to absent: fixed (`ItemObject.java:1538-1545`).
    - `similar` treats an absent modifier as zero and compares by value when both are present: fixed
      (`ItemObject.java:1374-1385`).
    - `earlierObject` refreshes `player`: fixed (`ItemObject.java:1110`).
    - `similar` refreshes `player`: fixed (`ItemObject.java:1319`).
    - `checkElementStacking` reads a missing element as level 0 with no flags, and `similar` calls it once: fixed
      (`ItemObject.java:1431-1462` and `ItemObject.java:1343`).
    - The batch 2 fixes also hold: `objectAbsorb` refreshes `player` (`ItemObject.java:3809`), `objectSplit` aligns the
      known count first (`ItemObject.java:3906-3907`), and `mergeable` tests `this` for ammo (`ItemObject.java:3734`).

- `objectAbsorbPartial` (`ItemObject.java:4727`) has no baseline, so this is its complete Stage 1 against
  `object_absorb_partial` (`obj-pile.c`). It matches clause for clause.
    - `smallest` and `largest` are `min` and `max` of the two counts, as in C.
    - The `OSTACK_STORE` assert becomes a thrown `RuntimeException` for either mode, as C asserts on either (the same
      impossible-state pattern as batch 4's accepted divergence).
    - Mode1 in the quiver: limit = slot size, divided by thrown mult unless `this` is ammo. Walk: with both in the
      quiver, arrows 30 and 25, limit 40: difference 10, `this` 40, `item2` 15 (total 55, as in C). Thrown 6 and 7,
      limit 8: difference 1, `this` 8, `item2` 5 (total 13, as in C).
    - Mode1 in the quiver and mode2 not: `this` takes the limit and `item2` takes `largest + smallest - limit`. The
      `>= max_stack` throw is C's `assert(x < max_stack)` negated. Walk: arrows 30 in the quiver, 20 in the pack, limit
      40 gives 40 and 10. If `item2` ends at 40 it throws, where C asserts (40 is not below 40).
    - Mode2 in the quiver and mode1 not: the limit uses `item2`'s ammo test, `this` takes `largest + smallest - limit`,
      and `item2` takes the limit. Walk: `this` 20 and `item2` 35, limit 40, gives 15 and 40.
    - Neither in the quiver: `difference = max_stack - largest`, `this` = `largest + difference` = `max_stack`,
      `item2` = `smallest - difference`. Walk: 30 and 25, `max_stack` 40, gives 40 and 15.
    - The charge distribution comes before the counts change, from `item2.getNumber() - newItm2Size` with `destNew`
      false and `this` as the destination, as in C. Then both counts are set, then `objectAbsorbMerge` runs with
      `isMoney()` as the combine flag.
    - The extra `player` refresh (`ItemObject.java:4734`) has no C counterpart, and changes nothing C reads.
    - Behaviour shared with C and not a divergence: when the total is smaller than the limit in the mixed-quiver cases
      (for example 10 + 5 with limit 40), `newThisSize` is the limit and `newItm2Size` is negative. The caller
      (`inven_can_stack_partial` in C) must make sure this does not happen, and Java does the same arithmetic.

- Curse unflattening: the plan said it may postdate the baseline. It has landed in code, in that `Curse.getItemObject()`
  is read at `ItemObject.java:4417` and `ItemObject.java:4559-4560`. `similar` and `cursesAreEqual` do not touch it.
  `cursesAreEqual` reads only the objects' `getCurses()` maps and the powers (`ItemObject.java:1531-1545`) and walks
  `ObjectRegistry.getCurses()` (`ItemObject.java:1534`). That is the same as C's walk over `curses[]`, so the comparison
  is unaffected.

## Out-of-scope observations

- `ItemObject.java:3912-3913`: the `objectSplit` exception message reads "should have been more than" the stack size.
  The legal range is an `amount` below the stack size, so the message states the opposite. This is text only and does
  not change behaviour.

- `ItemObject.java:4711-4714` (Javadoc): the `objectAbsorbPartial` Javadoc says the size left in the pack "must fit the
  kind's `max_stack`". The check at `ItemObject.java:4754` and `ItemObject.java:4767` rejects a size equal to
  `max_stack`, which is C's `assert(x < max_stack)`. The wording should say strictly less than.

- `ItemObject.java:3849-3852` (Javadoc on `nullKnown`) says `ObjectUtils` calls it "on both halves".
  `ObjectUtils.java:318` calls `knownObject.nullKnown()` on the known half, whose own `known` is already null, and
  `ObjectUtils.java:321` calls `item1.nullKnown()`. C sets only `obj1->known = NULL`, in `combine_pack` (`obj-gear.c`).
  The call at `ObjectUtils.java:318` is a no-op in C terms.

- `docs/ItemObject_port_batches.md` (batch 1 heading, "Stage 1 FAILED, waiting on Rowan"): stale against the code, as
  above. In the same entry, the C sources line names `object_origin_combine` and `distribute_charges` under "
  `obj-util.c` / `obj-pile.c`". `object_origin_combine` is in `obj-pile.c` and `distribute_charges` is in `obj-util.c`.

- `ObjectKind.java:1222` (Javadoc) cites "`obj-knowledge.c:856`", a C line number, which the house rules say to remove.

- Not re-raised, as instructed: the `copy(false)` leaving `known` null (batch 2), and the Part F accepted divergences
  for `flavourIsAware` and `objectFlavourIsAware` on a kindless item (`earlierObject` reaches those two).

## Plan 'Look for' answers

- `objectAbsorbPartial` has no baseline: a complete Stage 1 of its own is under "What does not match" above. Every case
  matches, including the four quiver and pack split cases. It is reached from `ObjectUtils.java:340-341` (the real and
  known halves, with one mode flag each).

- `cursesAreEqual` and `similar` against the curse unflattening work: re-checked against the code. The unflattening has
  landed (`Curse.getItemObject()` is read at `ItemObject.java:4417` and `ItemObject.java:4559-4560`), but neither Part B
  method reads a curse's object, so neither is affected.

- Batch 1 heading: the body is true and the heading is stale. All five batch 1 findings and the three batch 2 fixes are
  present in the code read this turn.

Call to action: nothing in Part B needs a code change. Update the batch 1 heading in `docs/ItemObject_port_batches.md`
from "FAILED" to done. Decide whether to fix the `objectSplit` message wording and the two Javadoc points above when
Stage 2 reaches this part.

clean
