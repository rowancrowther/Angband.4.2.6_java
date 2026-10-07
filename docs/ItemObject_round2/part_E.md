# Part E: damage power (Stage 1, round 2)

Run 2026-10-07. Read-only. Every Java declaration and C function named below was read in full this turn:
`ItemObject.java` (both overloads of all nine methods and the `PowerAndMult` record), `ObjectRegistry.java` (constants
and the `archery` field), `GameConstants.java` (the archery rows), `Archery.java`, `TValue.java` (`isAmmo`,
`isMeleeWeapon`),
`Slay.java`, `Brand.java`, `Curse.java`, `CurseAssembler.java`, and in `obj-power.c` the nine C functions,
`object_power`
and `curse_power`, plus `obj-power.h`, `obj-gear.c` (`wield_slot`, `slot_by_type`, `slot_by_name`), `obj-tval.c`,
`obj-slays.c` and the curse parser in `obj-init.c`.

## What matches

- `ItemObject.java:6384` `toDamagePower()` against C `to_damage_power`: first lot `toDam * DAMAGE_POWER / 2`, then the
  second lot `toDam * DAMAGE_POWER` only when `wieldSlot() != slotByName(player, "shooting")` and the type is neither a
  melee weapon nor ammunition. Same order of evaluation (slot lookup first), same truncation. Walk: `to_d` 5 on a sword
  gives 12 both ways; `to_d` 3 on a ring gives 7 + 15 = 22 both ways; `to_d` -3 on a ring gives -7 + -15 = -22 both ways
  (truncates toward zero in both languages); a bow with `to_d` 4 gets only the first lot, 10, both ways.

- `ItemObject.java:6416` `toDamagePower(Curse)`: C reaches the second-lot branch unconditionally for a curse object
  (tval `TV_NONE`, so `wield_slot` is -1, and it is neither melee nor ammo). Java adds both lots unconditionally. Reads
  `curse.getItemObject().getToDam()`, which `CurseAssembler.java:340` sets from the `combat:` line, as
  `parse_curse_combat` sets `obj->to_d`. Walk: `to_d` -5 gives -12 + -25 = -37 both ways.

- `ItemObject.java:6317` `damageDicePower()` against C `damage_dice_power`: melee or ammo gives
  `(dd * (ds + 1) * DAMAGE_POWER) / 4`; otherwise, only when the slot is not shooting, a non-empty brand set, non-empty
  slay set, or blows/shots/might above zero gives `WEAP_DAMAGE * DAMAGE_POWER` (60). Walk: 3d4 sword gives 3 *5*5/4 = 18
  both ways; 1d1 gives 1 *2*5/4 = 2; a ring with shots 1 gives 60; a ring with blows -1 and nothing else gives 0; a bow
  gives 0 (neither branch). The C test `obj->brands || obj->slays` is a pointer test, the Java test is non-empty. They
  agree for every state the C code can reach, because C allocates the array only when copying a source that has one.

- `ItemObject.java:6352` `damageDicePower(Curse)`: only the modifiers branch of C applies to a curse object, because
  `curse.txt` has no brand or slay directive (the curse parser in `obj-init.c` registers none). Reads
  `curse.getItemObject().getModifierValue(...)` for blows, shots and might, all `> 0`, as C does. Walk: shots 1 gives
  60, might 0 and blows -2 gives 0.

- `ItemObject.java:6251` `ammoDamagePower(int)` against C `ammo_damage_power`: launcher only when the slot is shooting;
  kind flags tested in the order shots, arrows, bolts; result `ammo_dam * DAMAGE_POWER / 2`. Walk: sling 10 *5/2 = 25,
  bow 12*5/2 = 30, crossbow 14*5/2 = 35, both ways. A bow with none of the three kind flags gives 0 both ways. It
  returns an increment, and `ItemObject.java:4252` adds it, as `object_power` does.

- `ItemObject.java:6290` `ammoDamagePower(Curse, int)` returns 0, as C does for a curse object (slot -1, never
  shooting).

- `ItemObject.java:6159` `launcherAmmoDamagePower(int)` against C `launcher_ammo_damage_power`: ammo only (`TV_SHOT`,
  `TV_ARROW`, `TV_BOLT`, the same three as `tval_is_ammo`); ego ammo adds `launch_dam * DAMAGE_POWER / 2` (22 on every
  row) before the multiply; then `power * launch_mult / (2 * MAX_BLOWS)`. The Java keys the row by tval where C indexes
  by 0, 1, 2. Walk: shot, no ego, power 100 gives 100 *4/10 = 40; ego arrow, power 100 gives 122*5/10 = 61; ego bolt,
  power 100 gives 122*7/10 = 85; shot, power -15 gives -60/10 = -6, all identical in C and Java.

- `ItemObject.java:6185` `launcherAmmoDamagePower(Curse, int)` returns its input; C does nothing for a non-ammo tval.

- The archery rows at `GameConstants.java:460-466` are (shot 10, 9, 4), (arrow 12, 9, 5), (bolt 14, 9, 7), identical to
  C's `archery[]`. `Archery.java` getters return the four fields unswapped.

- `ItemObject.java:6203` `bowMulitplier()` against C `bow_multiplier`: 1 for anything that is not `TV_BOW`, otherwise
  the
  `pval`. `ItemObject.java:6226` `bowMulitplier(Curse)` returns 1, as C does for tval `TV_NONE`.

- `ItemObject.java:6082` `extraBlowsPower(int)` against C `extra_blows_power`: zero returns the input; `>= 3` adds
  `INHIBIT_POWER` and returns; otherwise `power * (MAX_BLOWS + b) / MAX_BLOWS` and then
  `+ NONWEAP_DAMAGE * b * DAMAGE_POWER / 2`, in the same evaluation order. Boundaries: blows 2 is priced, blows 3 is
  inhibited. Walk with power 100: blows 1 gives 120 + 37 = 157; blows 2 gives 140 + 75 = 215; blows 3 gives 20100; blows
  -1 gives 80 + (-75/2 = -37) = 43. All identical both ways.

- `ItemObject.java:6116` `extraBlowsPower(Curse, int)`: same steps against `curse.getItemObject()`. The curse's modifier
  map comes from `CurseAssembler.java:338`.

- `ItemObject.java:6009` `extraShotsPower(int)` against C `extra_shots_power`: zero returns; `>= 21` inhibits; `> 0`
  gives `power * (10 + q)` then `/ 10`, as two statements in both; negative leaves the input. Boundaries: shots 20 is
  priced (power 100 gives 300), shots 21 is inhibited (20100), shots -5 gives 100. Identical both ways.

- `ItemObject.java:6041` `extraShotsPower(Curse, int)`: same steps. The extra `curse == null` guard has no C counterpart
  and is unreachable for an assembled curse.

- `ItemObject.java:5934` `extraMightPower(PowerAndMult)` against C `extra_might_power`: no zero test, `>= 4` adds
  `INHIBIT_POWER` and returns, otherwise `mult += might` and `power *= mult`. Walk: bow pval 3, might 1, power 100 gives
  mult 4 and 400; might 3 gives mult 6 and 600; might 4 gives 20100; non-bow (mult 1), might -1 gives mult 0 and power
  0, both ways. Returning a record rather than the power alone is documented at `ItemObject.java:7380` and is harmless,
  because `ItemObject.java:4260-4262` writes the multiplier back to a local nothing reads, as `object_power` discards it
  in C.

- `ItemObject.java:5968` `extraMightPower(Curse, PowerAndMult)`: same steps from `curse.getItemObject()`, starting from
  the caller's multiplier, which is 1 (`bowMulitplier(Curse)`), as `bow_multiplier` of a curse object is 1.

- `ItemObject.java:5817` `slayPower(int, boolean, int)` against C `slay_power`: `best_power` starts at 1; brands and
  slays each take the maximum power; slays with multiplier `<= 3` count as slays, above as kills; early return when all
  three counts are zero; then `(dice * dice * (best - 100)) / 2500` and the five bonus terms with divisor
  `DAMAGE_POWER * 5`, in the same order, plus the flat 10, 20 and 20 at exactly 8 slays, 5 brands and 3 kills. Same
  strict `>` / `==` tests. Walk with dice 60: one brand at power 161 gives 60 *60*61/2500 = 87; two brands (161, 107)
  add 2 *4*60/25 = 19; one slay (101) plus one brand (113) add 1 *1*60/25 = 2; two kills add 3 *4*60/25 = 28; negative
  case best_power 1 gives 60 *60*(-99)/2500 = -142 (356400/2500 = 142.56, truncated toward zero, both languages). The C
  loops start at index 1 because index 0 of `brands[]` and `slays[]` is a dummy (`obj-init.c` fills from 1), so no real
  entry is skipped and the Java set walk visits the same entries. Not mutating, as C: nothing is freed or written.

- `ItemObject.java:5910` `slayPower(Curse, ...)` returns its input; C counts zero for a curse object because
  `curse.txt` has no brand or slay directive.

- Constants: `ObjectRegistry.java:55` NONWEAP_DAMAGE 15, `:67` WEAP_DAMAGE 12, `:81` DAMAGE_POWER 5, `:99` MAX_BLOWS 5,
  `:126` INHIBIT_POWER 20000, `:130` INHIBIT_BLOWS 3, `:134` INHIBIT_MIGHT 4, `:138` INHIBIT_SHOTS 21. All equal to
  `obj-power.h`.

- Order of steps in both `objectPower` overloads (`ItemObject.java:4248-4264` and `:4321-4337`, read as the callers
  only): to-damage, dice, ammo, bow multiplier, launcher-ammo, blows, early return, shots, early return, might, early
  return, slay. Same as C `object_power`, including the strict `> INHIBIT_POWER` tests. An initial negative total plus
  20000 stays below the test in both.

- Shooting-slot lookup and the live player: `wieldSlot()` (`ItemObject.java:6457`) refreshes `player` from
  `GameState.getPlayer()` on each call, and the three Part E readers refresh it again first. This writes the instance
  field `player` only; no other shared state is written. `ObjectUtils.slotByName` returns the index of the named slot by
  identity (`ObjectUtils.numberFromSlot`), `slotByType` reproduces `slot_by_type`'s empty-then-fallback result, and
  `wieldSlot()` has the same switch and default -1 as C `wield_slot`.

- Curse data: the Curse overloads read `curse.getItemObject()` (`Curse.java:198`), which `CurseAssembler.java:338-346`
  fills with modifiers, to-damage and the rest. That is C's `curse->obj`. The unflattening has landed for these
  overloads: none of them reads a field that `Curse` has dropped.

- `ItemObject.java:7380` `PowerAndMult` is a plain `(int power, int mult)` record with no logic.

- `Slay` now has `equals` and `hashCode` (`Slay.java:318`, `Slay.java:336`), so `Set<Slay>` collapses value-equal slays
  and `slayPower` counts a slay once, as C's one-bool-per-index array does. This supersedes item 5 of
  `docs/ItemObject_stage1_mismatches.md` for the purposes of this part.

## What does not match

- No behavioural mismatch with C found in any of the 19 declarations. The accepted divergences of batch 6
  (`ammoDamagePower` zero on a null kind, the null-player `NullPointerException`, the "non-combat bonuses" log wording)
  were not re-raised. Two comment-only drifts, neither changing behaviour, for the Stage 2 Javadoc pass:

- Comment drift, `ItemObject.java:6409` (Javadoc of `toDamagePower(Curse)`): says "The curse's figure is
  `getCombatDam()`". The code at `ItemObject.java:6417` and `:6421` reads `curse.getItemObject().getToDam()`, and
  `Curse.getCombatDam` no longer exists in `src/main`. Stale since the unflattening.

- Comment drift, `ItemObject.java:5797-5798` (Javadoc of `slayPower(int, boolean, int)`): says "The search starts from 1
  rather than 0, as C's does, so a set whose every member scores below 100 still takes the full penalty of 99 below it."
  What starts at 1 is `best_power`, not the search. The penalty is `best - 100`, so a set whose best member scored 50
  would take -50, not -99; only a best of 1 gives -99. With the shipped data (lowest power 101, `brand.txt` and
  `slay.txt`) the start value is never the result of a non-empty set.

## Out-of-scope observations

- `Archery.java:24`, `ObjectRegistry.java:188`, `:198`, `:205` and `:328` cite C line numbers in Javadoc
  (`obj-power.c:47-57`, `:71`, `:93`, `:112`, `:132`). The project rule is to name the C file only. The `archery`
  Javadoc at `ObjectRegistry.java:181` is clean.

- `ItemObject.java:6355` has the comment "Add damage from dice for any wearable weapon or ammo" above code in
  `damageDicePower(Curse)` that does neither; it was copied from the plain overload.

- `ItemObject.java:3106` `addBrand` and `ItemObject.java:3160` `addSlay` add to the set without C's `append_brand` /
  `append_slay` replace-if-stronger rule (the dedupe by name or monsters slain lives in `ObjectUtils.copyBrands` and
  `ObjectUtils.copySlays`). `slayPower` counts set members, so two brands of one element added through `addBrand` would
  count twice where C would hold one. Callers of `addBrand`/`addSlay` outside `ItemObject.java` are the two in
  `PlayerKnowledge.java:286` and `:301`, which fill a known object, not a priced one, so this is not reachable from the
  power calculation today. Part A owns the methods.

- `ItemObject.java:5725` (Javadoc in the Part D area) still names `Curse#getCombatToHit()`, which no longer exists, and
  `PlayerKnowledge.java:1725` and `:1760` name `Curse#getCombatDam` and `Curse#getCombatToHit`. Same unflattening
  staleness as the first drift above.

- The Curse overloads in `extraBlowsPower`, `extraShotsPower`, `extraMightPower` and `damageDicePower` guard with
  `getModifiers() != null`, which cannot be false because `ItemObject.getModifiers()` answers `Map.of()` for a null
  field (`ItemObject.java:2778`). Harmless dead guards.

## Next

- Part E has no code finding. Fix the two Javadoc items (`ItemObject.java:6409`, `ItemObject.java:5797`) in the Stage 2
  pass, and decide whether the C-line-number Javadoc in `Archery.java` and `ObjectRegistry.java` is yours or Stage 2's.

clean
