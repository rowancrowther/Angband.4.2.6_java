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

Fixed: `cursesAreEqual` treats a curse at power zero as equal to an absent one, as C does; `similar` treats an absent
modifier as zero and compares by value when both are present; `earlierObject` and `similar` refresh `player`
from `GameState.getPlayer()` on each call, where C reads its `player` global at the moment of the call.
`checkElementStacking` now reads a missing element as level 0 with no flags, so it matches C's full-array comparison,
and `similar` calls it once. (The three findings were listed as open until 2026-10-03; they were fixed on 2026-10-02.)

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

Matches C: `objectAbsorbMerge` tests only that the absorbed note is not null, as C does, so an empty absorbed note
replaces a real one. (An earlier version of this doc and of the Javadoc said it ignored empty notes; the code never did,
and both were corrected on 2026-10-03.)

Accepted divergences: `copy(false)` leaves `known` null where C's `object_copy` copies the pointer (`includingKnown`
stays).

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

## Batch 5 - object power: the property components (done 2026-10-02)

Methods, each with its plain and `Curse` overload: `effectsPower`, `elementPower`, `flagsPower`, `modifierPower`,
`jewelleryPower`, `toAcPower`, `acPower`, `toHitPower`, `rescaleBowPower`.

C sources: `effects_power`, `element_power`, `flags_power`, `modifier_power`, `jewelry_power`, `to_ac_power`,
`ac_power`, `to_hit_power`, `rescale_bow_power` (`obj-power.c`).

Matches C: all nine, clause for clause. The three tables, the band thresholds, the ability-table boundaries, the two
truncating divisions in `acPower` and the type multiplier all agree.

Fixed: `rescaleBowPower` reads the live player; `effectsPower` tolerates a null activation list; two log typos.

Accepted divergences: an item with no kind prices at zero in `effectsPower` where C would dereference null; unknown flag
and modifier properties throw where C asserts; the element table's rows name their element where C relies on position.

Deferred: `wieldSlot` still reads the construction-time `player`, so the bow test is only as live as that method. It
belongs to Batch 8.

Stage 2: the nine field blocks these methods read and all eighteen method blocks were rewritten, without C line numbers
and without the unsupported claim that base armour halves acid damage.

Stage 3: new `ItemObjectPropertyPowerTest`, 97 cases. The full suite is green.

Already stamped in C: all nine functions carried `/* Ported to Java 2026-08-30 */`, so Stage 5 added nothing.

## Batch 6 - object power: damage (done 2026-10-02)

Methods, each with its plain and `Curse` overload where it has one: `slayPower`, `extraMightPower`, `extraShotsPower`,
`extraBlowsPower`, `launcherAmmoDamagePower`, `bowMulitplier`, `ammoDamagePower`, `damageDicePower`, `toDamagePower`.

C sources: `slay_power`, `extra_might_power`, `extra_shots_power`, `extra_blows_power`, `launcher_ammo_damage_power`,
`bow_multiplier`, `ammo_damage_power`, `damage_dice_power`, `to_damage_power` (`obj-power.c`).

Matches C: all nine, clause for clause, on every value walked through. The `archery` rows, the five constants, the three
inhibit thresholds, the truncating divisions and the order of steps in both `objectPower` overloads all agree.

Fixed: `damageDicePower` and `toDamagePower` now refresh `player` before the shooting-slot lookup, as `ammoDamagePower`
already did, so an item built before a character exists prices against the live player. A null modifier map in a `Curse`
no longer throws in `extraBlowsPower(Curse)` or `extraShotsPower(Curse)`, because `Curse.getModifiers()` now answers an
empty map.

Accepted divergences: `ammoDamagePower` answers zero for an item with no kind where C would dereference null; with no
character at all the slot lookup throws a `NullPointerException` where C would crash on its null global; the Java
`damageDicePower(Curse)` logs "non-combat bonuses" where C says "non-weapon combat bonuses".

Deferred: `wieldSlot` still reads the construction-time `player`, so the shooting-slot tests are only as live as the
refresh each caller does first. It belongs to Batch 8.

Stage 2: the C line numbers were removed from all eighteen method blocks and from the `archery` block in
`ObjectRegistry`; the eleven field blocks these methods read were extended, including `player`, whose note no longer
says only `wieldSlot` reaches it; the unsupported claims that a ring's doubled to-damage and the floor of 1 in
`slayPower` are deliberate were dropped.

Stage 3: new `ItemObjectDamagePowerTest`, 152 cases. The full suite is green.

Already stamped in C: all nine functions carried `/* Ported to Java 2026-08-30 */`, so Stage 5 added nothing.

## Batch 7 - knowledge, ignoring and flags (done 2026-10-02)

Methods: `isKnown`, `hasStandardToH`, `hasFlag`, `flagMessage`, `objectFlags`, `flagsKnown`, `easyKnow`,
`flavourIsAware`, `objectFlavourIsAware`, `getIgnoreTypeOf`, `isEgo`, `egoIsIgnored`, `ignoreLevelOf`, `isGood`,
`compareObjectTrait`, `rechargeTimeout`, `numberCharging`, `getTime`.

C sources: `object_has_standard_to_h`, `easy_know` and `object_flavor_is_aware` (`obj-knowledge.c`), `flag_message`
(`obj-properties.c`), `object_flags` and `object_flags_known` (`obj-util.c`), `ignore_type_of`, `ego_is_ignored`,
`ignore_level_of`, `is_object_good` and `cmp_object_trait` (`obj-ignore.c`), `number_charging` and `recharge_timeout`
(`obj-util.c`). `isKnown`, `hasFlag`, `isEgo` and `getTime` read a struct field in C and have no function to compare.

Matches C: all of them, clause for clause. The quality table agrees with C row for row and in order, the weights are
four, two and one, the kind's minimum roll is clamped to at most zero, `flagsKnown` intersects, then adds the kind's
flags, then adds and removes the ego's, and the charging count rounds up and caps at the stack size.

Accepted divergences: `flagsKnown` answers an empty set for an item with no known half where C dereferences null;
`easyKnow` and `flavourIsAware` answer false for a kindless item where C asserts; `objectFlavourIsAware` is a second,
strict port of the same C function that throws instead; `egoIsIgnored` guards a null ego where C's caller has already
tested the known one; `flagMessage` substitutes only the exact `{name}` tag, where C drops other tags, accepts any tag
starting `name` and truncates at 1,024 characters, and it logs its two data errors where C prints a "Bug:" line. None of
those is reachable with the shipped data.

Stage 2: the C line numbers were removed from eleven blocks, the `isKnown` block no longer says every object carries a
known counterpart from creation (C makes one when the item is first sensed, seen or grabbed) or that C only asserts the
test (`ignore_level_of` branches on it), the `objectFlavourIsAware` block names the right C file, and the class,
`logger` and thirteen field blocks these methods read were extended.

Stage 3: new `ItemObjectKnowledgeIgnoreTest`, 88 cases, and a dice-interval class in `ItemObjectRechargeTest`, 18 cases.
The full suite is green.

Already stamped in C: all thirteen functions carried `/* Ported to Java 2026-08-30 */`, so Stage 5 added nothing.

## Batch 8 - description, messages, slots and glyphs (done 2026-10-02)

Methods: `description`, `printCustomMessage`, `objectKindName`, `objDescNameFormat`, `wieldSlot`, `canBrowse`,
`objectKindChar`, `objectKindAttr`, `useFlavourGlyph`, `getItemObjectADC`, `wipe`, `initCurses`.

C sources: `obj-desc.c`, `obj-util.c` (`print_custom_message`, `obj_can_browse`), `obj-gear.c` (`wield_slot`),
`obj-pile.c` (`object_wipe`), `ui-object.c` (`object_kind_char`, `object_kind_attr`, `use_flavor_glyph`).

Carry-over from the 260929 precis: `printCustomMessage` now passes its built text to `Message.messageType` as a `"%s"`
argument, so that half is closed. Its Javadoc still says the Java version cannot truncate, but `Message.messageType`
cuts at 1023 characters; that is a Stage 2 fix.

Matches C: `printCustomMessage`, `objectKindName`, `objDescNameFormat`, `canBrowse`, `objectKindChar`,
`objectKindAttr`, `useFlavourGlyph`, `getItemObjectADC`, `initCurses`, and `wieldSlot` and `wipe` after the fixes below.

Fixed: `wieldSlot` refreshes `player` from `GameState.getPlayer()`, which closes the Batch 5 and 6 deferral; `wipe`
builds `LinkedHashMap`s for `modifiers` and `elInfo`, as the constructors do.

Not verifiable: `description` is a stub returning `{DESCRIPTION_TAG}`; `object_desc` and its helpers wait for Chapter 7.

Accepted divergences: C's `print_custom_message` hands `object_desc` the start of its buffer, so text ahead of
`{name}` is overwritten there, where Java appends in place (every `{name}` message in `activation.txt` and
`artifact.txt` leads with the tag); a tag's letters are tested with `Character.isAlphabetic` where C's `isalpha` is
ASCII only; `objDescNameFormat` treats a second `~` straight after the first as having nothing before it, where C writes
a bare `s`; the glyph methods read the parsed data where C reads the pref-remappable `kind_x_char` and `flavor_x_char`
tables; `wipe` leaves `location` as `null` where C's zero is a grid of (0, 0); `time` is reset to a zero `Random`, which
is C's four zero dice.

Stage 2: the C line numbers were removed from `wipe` and `initCurses`; the `printCustomMessage` block no longer says
Java cannot truncate (`Message.messageType` cuts at 1023) or that the messages live in `object.txt`; the
`objectKindName` block now names the real C callers; the `initCurses` block no longer says `copyCurses` guards its call;
the `wipe`, `wieldSlot` and `canBrowse` blocks were rewritten, and the class, `player` and every field `wipe`
writes were extended. `rescaleBowPower`'s block was brought into line with the `wieldSlot` fix.

Stage 3: new `ItemObjectWieldSlotTest`, 8 cases; four new cases in `ItemObjectWipeTest` and two in
`ItemObjectCustomMessageTest`. The full suite is green (0 failures, 18 skipped).

Stage 5: `object_wipe` was stamped 2026-10-02. The other eight functions already carried a stamp. `obj_kind_can_browse`
in `obj-util.c` has none, though `ObjectKind.canBrowse` is its port; that file is outside this batch.

## Batch 9 - fields, accessors and mutators (done 2026-10-02)

Methods: the constructors, every plain getter and setter, the curse, brand and slay editing methods, and the
`PowerAndMult` record. These have no C counterpart beyond the struct field, so Stage 1 was a field-by-field check
against `struct object` in `object.h`, and the weight fell on Stage 2: class, field and method Javadoc.

C sources: `struct object` (`object.h`), `object_new` (`obj-pile.c`) and `copy_curses` (`obj-curse.c`).

Matches C: every getter and setter that exists agrees with the `struct object` field it stands for, including
`setTime`'s copy (C's struct assign), `getFlags` and `getNotice` handing back copies, and the add, remove and clear trio
for brands, slays and curses. The full constructor stores its arguments by reference, as the `ItemObject` fields do.

Fixed: the map setters no longer clear before assigning; `setCurses` and `clearAndPutCurses` copy the incoming map
before assigning, so the same power with a new timeout is kept, as `copy_curses` rewrites it; `ItemObject()` sets
`tValue` to `TV_NONE`; `getModifierValue(ObjectModifier)` reads through the null-safe getter.

Accepted divergences: the `int` fields do not narrow to C's `uint8_t` and `int16_t`; `PowerAndMult` hands back a
multiplier that `objectPower` assigns and never reads, which C does not need.

Not ported: `ItemObject` has no getters for the artifact, effect message, activation, origin depth, origin race or
holding monster, and no `setBrands`; nothing calls them yet.

Stage 2: the class, the `tValue`, `activation` and `curses` fields, both constructors and every accessor and editor were
given blocks with the provenance line. The full-constructor block no longer says it copies the curse map, the
`removeCurse` block no longer says nothing is stored at power zero, the `getModifierValue(Stats)` block no longer says
the sentinels throw, and the C line numbers were removed from the blocks rewritten.

Stage 3: new `ItemObjectFieldSurfaceTest`, 35 cases. The full suite is green (0 failures, 18 skipped).

Stage 5: `object_new` was stamped 2026-10-02. `copy_curses` is not stamped; `ObjectUtils.copyCurses` is its port and is
outside this batch.
