# Part C: value and the power driver (Stage 1, round 2)

Run 2026-10-07. Read-only. Every Java declaration below was read in full this turn in `ItemObject.java` (the ten Part C
declarations and the fields, helpers and callees they touch). Every C function was read in full this turn:
`object_value`, `object_value_base`, `object_value_real`, `object_power`, `nonstandard_weight_power` and `curse_power`
in `obj-power.c`; `apply_curse_attributes` and `modify_weight_for_curse` in `obj-curse.c`; `object_weight_one` in
`obj-util.c`; `add_guardi`, `sub_guardi` and `add_guardi16` in `z-util.c`; the `tval_*` predicates in `obj-tval.c`; the
constants in `obj-power.h`. `lib/gamedata/constants.txt` holds none of the power constants.

Accepted divergences from `docs/ItemObject_port_batches.md` batches 3 and 4 are not re-raised: kindless
`objectValueReal`
returns zero; `objectValueBase` on a null type throws; `logFileName` carried but unused; base armour of a curse object
omitted in `applyCurseAttributes`; asserts become thrown `RuntimeException`s. The batch 4 divergence on curse visiting
order is a special case, covered below.

## What matches

- `ItemObject.java:4027` `objectValue` against `object_value`: same three routes in the same order. Variable power with
  a known half prices `known` through `objectValueReal(quantity)`; a flavoured type whose flavour is known prices the
  object itself; everything else is `objectValueBase() * quantity`. Inputs walked: a sword with `known` set goes to the
  first route; a sword with `known == null` skips to the third (it cannot be flavoured); an aware potion goes to the
  second; an unaware potion goes to the third and prices 20 per unit. Both versions agree on each.

- `ItemObject.java:4027` `objectValue`, tval predicates: `TValue.hasVariablePower` (20 types), `canHaveFlavour` (8
  types),
  `canHaveCharges` (staff, wand), `isLight` and `isAmmo` match `tval_has_variable_power`, `tval_can_have_flavor_k`,
  `tval_can_have_charges`, `tval_is_light` and `tval_is_ammo` case for case.

- `ItemObject.java:4064` `objectValueBase` against `object_value_base`: aware returns the kind's cost; otherwise 5
  (food, mushroom), 20 (potion, scroll), 45 (ring, amulet), 50 (wand), 70 (staff), 90 (rod), 0 for anything else. Same
  table, same fall-through to zero.

- `ItemObject.java:4124` `objectValueReal` against `object_value_real`, variable-power route: `a = 1`, `b = 5`, the
  three-way split on the sign of power, and all four overflow guards are the same expressions. Values walked: power 1
  gives 6 in both; power -1 gives `-1 * (-1 - 5) = -6` in both; power 46335 gives 2,147,163,900 in both (no clamp);
  power 46341 clamps to `Integer.MAX_VALUE` in both; power -46336 clamps to `Integer.MIN_VALUE` in both; power 0 gives
  0, lifted to 1 in both.

- `ItemObject.java:4124` `objectValueReal`, expendable rescale: `isLight() && OF_BURNS_OUT && ego == null`, or
  `isAmmo()`, divides by `ObjectRegistry.AMMO_RESCALER` (20, matches `AMMO_RESCALER` in `obj-power.h`) with truncation
  toward zero. The flag read is the raw `flags` field through `getFlags()`, as C reads `obj->flags`, not `obj_has_flag`.
  Walked: value 36 on a plain torch gives 1; value -50 on ammo gives -2 in both; a zero result after the divide is
  lifted to 1 in both, and the negative -2 is left alone in both.

- `ItemObject.java:4124` `objectValueReal`, total: `value * quantity` floored at zero, in both. A cursed stack (-6 * 3)
  returns 0 in both.

- `ItemObject.java:4124` `objectValueReal`, fixed-price route: zero cost returns 0 at once; staff and wand add
  `value * charges / 20` where `charges = pValue * quantity / number`, plus one when the remainder is non-zero; total
  floored at zero. Walked: a wand of cost 400, `pValue` 10, `number` 3, quantity 1 gives charges 4 and total 400 + 80 =
  480 in both. Quantity 0 gives 0 in both. `number == 0` divides by zero in both (an
  `ArithmeticException` here, a trap in C), so nothing separates them.

- `ItemObject.java:4246` `objectPower(boolean, String)` against `object_power`: the step order is identical, from
  `toDamagePower` through `nonStandardWeightPower`. The `INHIBIT_POWER` (20000, matches `obj-power.h`) early returns are
  in the same three places (after blows, after shots, after might), and each returns the running total, not a capped
  figure. `power += ammoDamagePower(power)` and `bowMulitplier()` are in C's order, and `mult` is assigned from
  `extraMightPower` and never read again, as in C where `extra_might_power` takes it by value.

- `ItemObject.java:4319` `objectPower(Curse, boolean, String)`: the same sequence on the curse overloads, the same three
  early returns, with `cursePower(curse, ...)` and `nonStandardWeightPower(curse, ...)` last. The two identity overloads
  are right against C: `ItemObject.java:4971` `cursePower(Curse, ...)` returns its input because C's `curse_power` has
  its whole body behind `if (obj->curses)` and a curse object's `curses` is never allocated (`parse_curse_name`
  zero-allocates the object); `ItemObject.java:4497` `nonStandardWeightPower(Curse, ...)` returns its input because the
  curse object has no curses, so `object_weight_one` gives `MAX(weight, 0)`, equal to the standard weight, and C's first
  test returns at once.

- `ItemObject.java:4395` `nonStandardWeightPower(int)` against `nonstandard_weight_power`: standard weight is
  `max(weight, 0)`; the negative-weight assert becomes a throw; equal weights return at once; flags are the object's own
  union the flags of every curse with non-zero power; the base-armour test is `baseAC == 0`; the no-base-AC term is
  `(std - nonstd) / 50` with C's sign-split clamp and a numerator of 1; the throwing term is `nonstd/12 - std/12` (each
  divided before the subtract) times 15 with C's clamp; both terms join through `Guards.addGuardI`, and so does the add
  to power; nothing is added when the combined adjustment is 0. Values walked, standard 100: nonstandard 50 with no base
  AC gives +1 in both; nonstandard 140 gives (100-140)/50 = 0 (truncation toward zero) in both; nonstandard 130 with
  THROWING gives (10 - 8) * 15 = +30 in both; nonstandard 30 with THROWING and no base AC gives 1 + (2 - 8) * 15 = -89
  in both; the same weights on an object with base AC skip the first term in both.

- `ItemObject.java:4540` `cursePower(int, boolean, String)` against `curse_power`, first pass: a power-0 entry is
  skipped; a `MULTIPLY_WEIGHT` curse is weight-affecting unless its weight is exactly 100; any other curse is
  weight-affecting unless its weight is 0; non-affecting curses price as `objectPower(curse)` minus `power / 10`
  (truncating) and sum into
  `q`. Walked: curse object power 50, curse power on the item 90 gives 41; curse power 5 gives 50; both versions agree.

- `ItemObject.java:4540` `cursePower(int, boolean, String)`, second pass: the scratch copy is `copy(true)`, all curses
  are applied with `null` (C's -1), curses are cleared before the copy is priced, brands and slays are freed after; the
  per-curse copy applies every curse but one; the same two skip tests; `powerCurse` is `subGuardI(all, allButC)`; a
  negative result is scaled by `clamp(power, 20, 100)` with C's `INT_MIN / resistance` guard and divided by 100
  (truncating toward zero); a non-negative result is used as is; contributions join through `addGuardI`. Walked: all =
  100, allButC = 130, so -30: curse power 60 gives -1800 / 100 = -18; curse power 5 clamps to 20 and gives -600 / 100 =
  -6; curse power 150 clamps to 100 and gives -30. A non-negative gap of +40 stays +40. Same in both.

- `ItemObject.java:4540` `cursePower(int, boolean, String)`, tail: `power += q` only when `q != 0`. Matches.

- Visiting order of curses, which batch 4 recorded as a divergence, no longer diverges in the code.
  `ItemObject.java:823`
  `curses` is a `TreeMap` ordered by `ItemObject.java:149` `CURSE_ORDER` (index, then name), `ItemObject.java:1052`
  `cursesFactory` builds every map, and `copy` at `ItemObject.java:6572` rebuilds through it. `CurseAssembler.java`
  reverses the parsed list and numbers it from 0, so ascending Java index is ascending C index (C's `finish_parse_curse`
  also numbers the prepend-built list from the last curse in the file). `cursePower` therefore walks in C's order. C
  starts at 1 because slot 0 is a placeholder; Java has no placeholder, so it skips nothing.

- `ItemObject.java:4832` `applyCurseAttributes` against `apply_curse_attributes`: early return when the object has no
  curses; curses visited in ascending index (the loop at `ItemObject.java:4845` sorts the registry's indices); the
  ignored curse and any curse that is absent or at power 0 are skipped; weight goes through `Curse.modifyWeightForCurse`
  (`Curse.java:352`, read this turn and checked against `modify_weight_for_curse`: the multiply branch, the
  round-to-nearest, the `> 100` lift to 1, the 32767 ceiling and the negative-addend clamp all agree); `toAC`, `toHit`,
  `toDam` join through `Guards.addGuardI16`, which equals `add_guardi16`; flags are unioned (`ItemObject.java:3048`
  `setFlags` calls `Flag.union`, confirmed in `Flag.java`, and does not go through the copying `getFlags`); modifiers
  add with saturation, with an absent entry taken from the curse (equivalent to C adding 0 to the array slot).

- `ItemObject.java:4832` `applyCurseAttributes`, element merge: the test order is the same as C's, `>= 3` (skip),
  `== 1`,
  `== VULN_AND_RES` (`ItemObject.java:380`, `Short.MIN_VALUE`, equal to C's -32768), `< 0`, else. Every cell of the
  table agrees: resistant + immune gives 3; resistant + vulnerable gives the sentinel; resistant + resistant or silent
  stays 1; sentinel + immune gives 3, otherwise stays; vulnerable + immune gives 3; vulnerable + resistant gives the
  sentinel; vulnerable + vulnerable or silent stays; unaffected + anything takes the curse's level. An element the
  object does not list is created from the curse when the curse lists it (`new ElementInfo()`, level only, flags empty,
  as C copies only `res_level`). The closing pass turns any surviving sentinel into 0, as C does. `ELEM_NONE` and
  `ELEM_MAX`
  are skipped (intentional sentinels).

- Shared-state side effects of `applyCurseAttributes` are contained: `ItemObject.java:6572` `copy` deep-copies
  `modifiers`
  into a new `HashMap`, `elInfo` entry by entry through `ElementInfo.copy()`, and the curse map with fresh `CurseData`.
  The in-place `setResLevel` and `put` calls in `applyCurseAttributes` therefore never reach the original or the
  registry, and the maps are mutable because `copy` always builds `HashMap`s. `ItemObject.java:6855` `freeCurses`,
  `ItemObject.java:4687`
  `freeBrands` and `ItemObject.java:4676` `freeSlays` only replace fields on the scratch copy.

- Curse data now comes from the curse's own object. `applyCurseAttributes` and `cursePower` read weight, flags, to-hit,
  to-dam, to-AC, modifiers and element info through `curse.getItemObject()`, and `CurseAssembler.java` (read this turn)
  sets exactly those on it. This is C's `curses[i].obj->x`. No read of a removed flattened field remains in the Part C
  declarations.

- Constants: `ObjectRegistry.java` `INHIBIT_POWER` 20000, `AMMO_RESCALER` 20, `WGT_POWER_NUM_NOBASEAC` 1,
  `WGT_POWER_DEN_NOBASEAC` 50, `WGT_POWER_NUM_THROW` 15, `WGT_POWER_DEN_THROW` 12 all equal `obj-power.h`.

## What does not match

No behavioural mismatch in code was found in any Part C declaration. The items below are statements in Javadoc and
comments inside Part C declarations that the code, read this turn, contradicts. None changes a result; all are Stage 2
work. Part A and Part F items are listed under out-of-scope.

- `ItemObject.java:4529` to `ItemObject.java:4531`, `cursePower(int, boolean, String)` Javadoc: says "C visits the
  curses in registry order and the port in the order they were added to the object. That cannot change the first
  pass ... and could change the second only when a sum reaches the int limits." This is now false. The map is a
  `TreeMap` under
  `CURSE_ORDER` (`ItemObject.java:823`, `ItemObject.java:149`), so the port walks in index order as C does, and the
  batch 4 accepted divergence has been closed by the code. The paragraph should go or be rewritten. The same Javadoc is
  also wrong that the sum order mattered only at the limits in the abstract, which no longer arises.

- `ItemObject.java:4291` to `ItemObject.java:4296`, `objectPower(Curse, boolean, String)` Javadoc, and
  `ItemObject.java:76` to `ItemObject.java:79`, class Javadoc: both say the port's `Curse` is a "flattened record" and
  not an object. `Curse.java:116` now holds a real `ItemObject` (`getItemObject()` at `Curse.java:198`) and
  `CurseAssembler.java` fills it. The identity-overload arguments in those blocks still hold; the premise they open with
  does not.

- `ItemObject.java:4854`, comment in `applyCurseAttributes`: "We have a flattened curse data - so don't look at an
  object, look directly at the curse". The lines under it read `curse.getItemObject()`, which is looking at an object.

- `ItemObject.java:4011` to `ItemObject.java:4014`, `objectValue` Javadoc: says a variable-power object with no `known`
  half "lands on the base route, which prices an unlisted type at zero: an object the player has never seen is worth
  nothing here." The code matches C, but the sentence misdescribes it. An unflavoured kind is made aware at start-up
  (`ObjectUtils.java:1485`, as C's `flavor_init` does), so `objectValueBase` returns the kind's cost for a sword with no
  known half, not zero. Zero is reached only for an unaware, unlisted type (for example a special artifact kind before
  it is learned).

- `ItemObject.java:4050` to `ItemObject.java:4052`, `objectValueBase` Javadoc: "Types not listed are worth nothing
  unidentified, which includes every wearable that is not jewellery." Same cause: a wearable's kind is aware from
  start-up, so the aware branch at `ItemObject.java:4065` answers first. The zero return applies only to an unaware kind
  of an unlisted type.

## Out-of-scope observations

- `ItemObject.java:4849` `applyCurseAttributes` compares the held-back curse by identity (`curse == curseToIgnore`),
  while the lookups next to it (`containsKey`, `get`) go through `CURSE_ORDER` (index, then name), and the weight, flags
  and modifiers come from the registry's instance while `cursePower` at `ItemObject.java:4559` reads the map key's. If a
  curse map ever held a `Curse` that equals a registry entry under `CURSE_ORDER` but is a different instance (a test
  fixture, a reloaded registry), `applyCurseAttributes(c)` would not ignore it, the "all but c" copy would equal the
  "all curses" copy, and `powerCurse` would be 0 silently. `ObjectUtils.copyCurses` draws from the registry, so
  production does not reach it. Reachability judged by grep: `applyCurseAttributes` is private with one caller,
  `cursePower`.

- Now that `Curse.getItemObject()` is a real `ItemObject` (`Curse.java:198`), the whole second family of `Curse`
  overloads (`objectPower(Curse ...)`, `nonStandardWeightPower(Curse ...)`, `cursePower(Curse ...)` and the Part D and E
  ones) is a hand-reduced stand-in for calling `objectPower` on that object, which is what C does. Their
  identity-overload justifications are correct today, but each is a place to drift if `curse.txt` ever gains a base
  armour, a brand or a kind with power. Design decision for you, not a mismatch.

- `ItemObject.java:4027` `objectValue` is `private`; C's `object_value` is public and is also called by the store and
  wizard-mode code. Only `earlierObject` calls it today (`ItemObject.java:1149` to `ItemObject.java:1153`). It will need
  widening when Chapter 8 lands.

- `ItemObject.java:1758` (`setCurses` Javadoc) and `ItemObject.java:1860` (`clearAndPutCurses` Javadoc) say the field is
  assigned a new `LinkedHashMap`; `ItemObject.java:1773` builds it with `cursesFactory()`, which is a `TreeMap`. Part A.

- `ItemObject.java:3347` (`objectWeightOne` Javadoc) says "The map the curses live in keeps insertion order, which is
  why the indices are sorted first", and `ItemObject.java:7082` (`initCurses` Javadoc) says the map "keeps insertion
  order". Both are stale against the `TreeMap`. The extra sort in `objectWeightOne` (`ItemObject.java:3353`) is now
  redundant, though harmless. Part F and Part A.

- `ItemObject.java:3353` `objectWeightOne` and `ItemObject.java:4832` `applyCurseAttributes` both re-sort the curse
  indices and then do a nested search for the matching curse. With the `TreeMap` and an index-sorted registry list, a
  direct loop would be identical. Efficiency only.

- `ItemObject.java:4832` `applyCurseAttributes`: if a curse's modifier value is outside the 16-bit range and the object
  has no entry for that modifier, Java stores the value unclamped (`ItemObject.java:4876`), where C's
  `add_guardi16(0, value)` would clamp it. Not reachable: C stores curse modifiers in `int16_t`, and no value in
  `lib/gamedata/curse.txt` is anywhere near the limit.

- `objectValueReal` and `objectPower` also serve `PlayerBirth.java:882` (`objectValueReal`), the only non-`ItemObject`
  caller found by grep. `objectPower` itself has no caller outside `ItemObject.java`.

## Call to action

- Nothing in Part C needs a code change. Take the five Javadoc corrections above (`ItemObject.java:4529` to
  `ItemObject.java:4531`, `ItemObject.java:4291` to `ItemObject.java:4296` plus `ItemObject.java:76` to
  `ItemObject.java:79`, `ItemObject.java:4854`, `ItemObject.java:4011` to `ItemObject.java:4014`, `ItemObject.java:4050`
  to
  `ItemObject.java:4052`) into Stage 2, and decide whether the identity comparison at `ItemObject.java:4849` should use
  `CURSE_ORDER` like its neighbours.

clean
