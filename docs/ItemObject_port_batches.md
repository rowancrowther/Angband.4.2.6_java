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

## Batch 2 - stackability, absorb, split, copy

Methods: `mergeable`, `objectStackable`, `objectAbsorb`, `objectAbsorbMerge`, `nullKnown`, `objectSplit`, `copy`,
`objectIsInQuiver`, `objectWeightOne`, `checkForInscription`, `verifyObject`.

C sources: `object_mergeable`, `object_stackable`, `object_absorb`, `object_absorb_merge`, `object_split`,
`object_copy` / `object_copy_amt` (`obj-pile.c`), `object_weight_one`, `check_for_inscrip` (`obj-util.c`),
`object_is_in_quiver` (`obj-gear.c`), `verify_object` (`ui-object.c`).

## Batch 3 - value

Methods: `objectValue`, `objectValueBase`, `objectValueReal`.

C sources: `object_value`, `object_value_base`, `object_value_real` (`obj-util.c`).

## Batch 4 - object power: driver and curse plumbing

Methods: both `objectPower` overloads, both `nonStandardWeightPower` overloads, both `cursePower` overloads,
`freeSlays`, `freeBrands`, `freeCurses`, `applyCurseAttributes`.

C sources: `object_power`, `object_power_nonstandard_weight`, `curse_power`, `apply_curse_attributes`-style helpers in
`obj-power.c`.

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
