# ItemObject.java port-check: batches

`ItemObject.java` is about 5,600 lines and 178 methods, so the `/port` pass runs over it in batches. Each batch is Stage
1 (verify against C), and only if clean, Stages 2 to 5. A batch that fails Stage 1 stops there until the Java is changed
and re-verified.

Started 2026-10-02. Method names are as they stood on that date.

## Batch 1 - pack ordering and stacking checks (Stage 1 FAILED, waiting on Rowan)

Methods: `earlierObject`, `similar`, `checkElementStacking`, `cursesAreEqual`, `originCombine`, `distributeCharges`,
`isFullyKnown`, `runesKnown`, `effectIsKnown`.

C sources: `earlier_object` (`player-calcs.c`), `object_similar` (`obj-pile.c`), `curses_are_equal` (`obj-curse.c`),
`object_origin_combine` and `distribute_charges` (`obj-util.c` / `obj-pile.c`), `object_fully_known`,
`object_runes_known`, `object_effect_is_known` (`obj-knowledge.c`).

Open findings:

- `cursesAreEqual` treats a curse stored at power zero as different from the same curse at power zero, and from an
  absent curse; C treats all three as equal.
- `similar` compares modifiers by key, so an explicit zero differs from an absent entry; C compares every slot and calls
  them equal.
- `earlierObject` and `similar` read the static `player`, which is whatever `GameState.getPlayer()` returned when an
  `ItemObject` was last constructed, where C reads its `player` global at the moment of the call.

## Batch 2 - stackability, absorb, split, copy (done 2026-10-02)

Methods: `mergeable`, `objectStackable`, `objectAbsorb`, `objectAbsorbMerge`, `nullKnown`, `objectSplit`, `copy`,
`objectIsInQuiver`, `objectWeightOne`, `checkForInscription`, `verifyObject`.

C sources: `object_mergeable`, `object_stackable`, `object_absorb`, `object_absorb_merge`, `object_split`,
`object_copy` / `object_copy_amt` (`obj-pile.c`), `object_weight_one`, `check_for_inscrip`, `verify_object`
(`obj-util.c`), `object_is_in_quiver` (`obj-gear.c`). There is no Java counterpart to `object_copy_amt`.

Matches C: `objectStackable`, `objectIsInQuiver`, `objectWeightOne`, `checkForInscription`, `nullKnown`, and
`verifyObject`'s flags.

Fixed: `objectAbsorb` now refreshes `player`; `objectSplit` aligns the known count first; `mergeable` tests `this` for
ammo.

Accepted divergences: `objectAbsorbMerge` ignores an empty absorbed note, which C does not test for (no empty notes
exist); `copy(false)` leaves `known` null where C's `object_copy` copies the pointer (`includingKnown` stays).

Deferred: `object_copy_amt` is used only by the store code, so it waits for Chapter 8. `verifyObject` prints a
placeholder name until `description` is ported in Chapter 7.

Stage 3: new `ItemObjectCopyTest`; new absorb, split and charge-pooling cases in `ItemObjectAbsorbTest`; an ordering
case in `ItemObjectWeightTest`. `ItemObjectStackingTest` now seeds the registry's curse list, which `similar` reads.

## Batch 3 - value (done 2026-10-02)

Methods: `objectValue`, `objectValueBase`, `objectValueReal`.

C sources: `object_value`, `object_value_base`, `object_value_real` (`obj-power.c`).

Matches C: all three, clause for clause. The overflow clamps, the `AMMO_RESCALER` division, the lift of zero to one and
the round-up of charges all agree.

Accepted divergences: `objectValueReal` returns zero for a kindless object where C would dereference null;
`objectValueBase`'s switch on a null type would throw where C's falls through to zero.

Stage 3: new `ItemObjectValueTest`. The overflow clamps are not tested, because the largest power reachable through a
short path is about 20000, well short of the 46,000 where they apply.

Already stamped in C: all three functions carried `/* Ported to Java 2026-08-30 */` before this pass, so Stage 5 added
nothing.

## Batch 4 - object power: driver and curse plumbing (done 2026-10-02)

Methods: both `objectPower` overloads, both `nonStandardWeightPower` overloads, both `cursePower` overloads,
`freeSlays`, `freeBrands`, `freeCurses`, `applyCurseAttributes`.

C sources: `object_power`, `nonstandard_weight_power` and `curse_power` (`obj-power.c`), `apply_curse_attributes`
(`obj-curse.c`). The `free` methods stand in for the `mem_free` calls inside `curse_power`.

Matches C: all of them, clause for clause. The step order, the three `INHIBIT_POWER` returns, the truncating divisions,
the 20 to 100 clamp on a curse's strength, the throwing term's divide-before-subtract and the resistance-combining table
all agree.

Accepted divergences: `logFileName` is carried but no file is written, because the trace goes to the logger;
`applyCurseAttributes` omits C's addition of the curse object's base armour, which `curse.txt` cannot set; the
impossible-state `assert`s become thrown `RuntimeException`s; `cursePower` visits curses in the order they were added to
the object, where C uses registry order, which could matter only if the saturating adds reach the `int` limits.

Stage 2: the C line numbers were removed, the `applyCurseAttributes` block no longer says flags add, and the class,
`logger`, `VULN_AND_RES` and the eleven fields these methods touch were expanded.

Stage 3: new `ItemObjectPowerCursesTest`, 58 cases. The full suite is green.

Already stamped in C: `object_power`, `nonstandard_weight_power`, `curse_power` and `apply_curse_attributes` all carried
`/* Ported to Java 2026-08-30 */`, so Stage 5 added nothing.

## Batch 5 - object power: the property components

Methods, each with its plain and `Curse` overload: `effectsPower`, `elementPower`, `flagsPower`, `modifierPower`,
`jewelleryPower`, `toAcPower`, `acPower`, `toHitPower`, `rescaleBowPower`.

C sources: `effects_power`, `element_power`, `flags_power`, `modifier_power`, `jewelry_power`, `to_ac_power`,
`ac_power`, `to_hit_power`, `rescale_bow_power` (`obj-power.c`).

## Batch 6 - object power: damage

Methods, each with its plain and `Curse` overload where it has one: `slayPower`, `extraMightPower`, `extraShotsPower`,
`extraBlowsPower`, `launcherAmmoDamagePower`, `bowMulitplier`, `ammoDamagePower`, `damageDicePower`, `toDamagePower`.

C sources: `slay_power`, `extra_might_power`, `extra_shots_power`, `extra_blows_power`, `launcher_ammo_damage_power`,
`bow_multiplier`, `ammo_damage_power`, `damage_dice_power`, `to_damage_power` (`obj-power.c`).

## Batch 7 - knowledge, ignoring and flags

Methods: `isKnown`, `hasStandardToH`, `hasFlag`, `flagMessage`, `objectFlags`, `flagsKnown`, `easyKnow`,
`flavourIsAware`, `objectFlavourIsAware`, `getIgnoreTypeOf`, `isEgo`, `egoIsIgnored`, `ignoreLevelOf`, `isGood`,
`compareObjectTrait`, `rechargeTimeout`, `numberCharging`, `getTime`.

C sources: `obj-knowledge.c`, `obj-ignore.c`, `obj-util.c` (`number_charging`, `recharge_timeout`).

## Batch 8 - description, messages, slots and glyphs

Methods: `description`, `printCustomMessage`, `objectKindName`, `objDescNameFormat`, `wieldSlot`, `canBrowse`,
`objectKindChar`, `objectKindAttr`, `useFlavourGlyph`, `getItemObjectADC`, `wipe`, `initCurses`.

C sources: `obj-desc.c`, `obj-util.c` (`print_custom_message`, `wield_slot`, `obj_can_browse`), `obj-pile.c`
(`object_wipe`).

Carry-over from the 260929 precis: the `printCustomMessage` Javadoc still says the Java version cannot truncate, and the
method passes its built text to `Message.messageType` as the pattern where C passes it as a `"%s"` argument.

## Batch 9 - fields, accessors and mutators (Javadoc coverage)

Methods: the constructors, every plain getter and setter, the curse, brand and slay editing methods, and the
`PowerAndMult` record. These have no C counterpart beyond the struct field, so Stage 1 is mostly a field-by-field check
against `struct object` in `object.h`, and the weight falls on Stage 2: class, field and method Javadoc.
