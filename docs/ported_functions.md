# Ported functions

Every C function of Angband 4.2.6 that this port implements **in full**. Stubs, partial ports, and Java methods that
merely *call* a C counterpart rather than reproduce it are deliberately absent.

Each function listed here also carries a `/* Ported to Java 2026-08-30 */` comment immediately above its definition in
the reference C source at `/home/rowan/Desktop/Angband-4.2.6/src`.

**274 C functions across 41 files**, listed 2026-08-30.

Twelve C functions are split across more than one Java method — `player_knows_brand`/`_slay`/`_curse`
each answer from both `Player` and `KnownObject`, `update_stuff` and `update_bonuses` straddle `Player`
and its upkeep/state, and `splashscreen_note`'s two branches became three methods. Every Java site is listed against its
C function. Annotations are stripped from the signatures; the return type has its own column.

## Contents

- [`cave-map.c`](#cave-mapc) — 2
- [`cave-square.c`](#cave-squarec) — 5
- [`cave-view.c`](#cave-viewc) — 10
- [`cave.c`](#cavec) — 2
- [`cmd-core.c`](#cmd-corec) — 38
- [`game-event.c`](#game-eventc) — 2
- [`game-input.c`](#game-inputc) — 2
- [`game-world.c`](#game-worldc) — 14
- [`generate.c`](#generatec) — 3
- [`main-win.c`](#main-winc) — 1
- [`mon-make.c`](#mon-makec) — 2
- [`mon-predicate.c`](#mon-predicatec) — 1
- [`mon-timed.c`](#mon-timedc) — 2
- [`mon-util.c`](#mon-utilc) — 2
- [`obj-curse.c`](#obj-cursec) — 3
- [`obj-desc.c`](#obj-descc) — 2
- [`obj-gear.c`](#obj-gearc) — 14
- [`obj-ignore.c`](#obj-ignorec) — 10
- [`obj-knowledge.c`](#obj-knowledgec) — 32
- [`obj-pile.c`](#obj-pilec) — 8
- [`obj-power.c`](#obj-powerc) — 24
- [`obj-properties.c`](#obj-propertiesc) — 2
- [`obj-slays.c`](#obj-slaysc) — 1
- [`obj-tval.c`](#obj-tvalc) — 4
- [`obj-util.c`](#obj-utilc) — 9
- [`player-birth.c`](#player-birthc) — 1
- [`player-calcs.c`](#player-calcsc) — 18
- [`player-quest.c`](#player-questc) — 1
- [`player-spell.c`](#player-spellc) — 3
- [`player-timed.c`](#player-timedc) — 4
- [`player-util.c`](#player-utilc) — 6
- [`player.c`](#playerc) — 4
- [`ui-birth.c`](#ui-birthc) — 2
- [`ui-display.c`](#ui-displayc) — 8
- [`ui-entry-combiner.c`](#ui-entry-combinerc) — 14
- [`ui-term.c`](#ui-termc) — 4
- [`z-bitflag.c`](#z-bitflagc) — 2
- [`z-color.c`](#z-colorc) — 3
- [`z-rand.c`](#z-randc) — 1
- [`z-type.c`](#z-typec) — 6
- [`z-util.c`](#z-utilc) — 2

## `cave-map.c`

| C function          | Java class | Java signature                       | Returns |
|---------------------|------------|--------------------------------------|---------|
| `map_info`          | `GridData` | `mapInfo(Loc grid) throws Exception` | `void`  |
| `square_light_spot` | `Chunk`    | `squareLightSpot(Loc grid)`          | `void`  |

## `cave-square.c`

| C function            | Java class | Java signature                   | Returns   |
|-----------------------|------------|----------------------------------|-----------|
| `square_forget`       | `Chunk`    | `squareForget(Loc grid)`         | `void`    |
| `square_holds_object` | `Square`   | `holdsObject(ItemObject object)` | `boolean` |
| `square_isfeel`       | `Chunk`    | `squareIsFeel(Loc grid)`         | `boolean` |
| `square_isglow`       | `Square`   | `isGlow()`                       | `boolean` |
| `square_islit`        | `Square`   | `isLit()`                        | `boolean` |

## `cave-view.c`

| C function              | Java class | Java signature                                                    | Returns   |
|-------------------------|------------|-------------------------------------------------------------------|-----------|
| `add_light`             | `Chunk`    | `addLight(Player player, Loc sourceGrid, int radius, int inten)`  | `void`    |
| `become_viewable`       | `Chunk`    | `becomeViewable(Loc grid, Player player, boolean close)`          | `void`    |
| `calc_lighting`         | `Chunk`    | `calcLighting(Player player)`                                     | `void`    |
| `glow_can_light_wall`   | `Chunk`    | `glowCanLightWall(Player player, Loc wallGrid)`                   | `boolean` |
| `mark_wasseen`          | `Chunk`    | `markWasSeen()`                                                   | `void`    |
| `no_light`              | `Chunk`    | `noLight(Player player)`                                          | `boolean` |
| `source_can_light_wall` | `Chunk`    | `sourceCanLightWall(Player player, Loc sourceGrid, Loc wallGrid)` | `boolean` |
| `update_one`            | `Chunk`    | `updateOne(Loc grid, Player player)`                              | `void`    |
| `update_view`           | `Chunk`    | `updateView(Player player)`                                       | `void`    |
| `update_view_one`       | `Chunk`    | `updateViewOne(Loc grid, Player player)`                          | `void`    |

## `cave.c`

| C function   | Java class | Java signature                      | Returns         |
|--------------|------------|-------------------------------------|-----------------|
| `motion_dir` | `Loc`      | `motionDir(Loc finish)`             | `DirectionEnum` |
| `next_grid`  | `Loc`      | `nextGrid(DirectionEnum direction)` | `Loc`           |

## `cmd-core.c`

| C function                      | Java class         | Java signature                                                                                                                                              | Returns                   |
|---------------------------------|--------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------|---------------------------|
| `cmd_cancel_repeat`             | `CommandQueue`     | `cancelRepeat()`                                                                                                                                            | `void`                    |
| `cmd_copy`                      | `Command`          | `clone()`                                                                                                                                                   | `Command`                 |
| `cmd_disable_repeat`            | `CommandQueue`     | `disableRepeat()`                                                                                                                                           | `void`                    |
| `cmd_disable_repeat_floor_item` | `CommandQueue`     | `disableRepeatFloorItem()`                                                                                                                                  | `void`                    |
| `cmd_get_arg_choice`            | `Command`          | `getArgChoice(String argName)`                                                                                                                              | `Optional<Integer>`       |
| `cmd_get_arg_direction`         | `Command`          | `getArgDirection(String argName)`                                                                                                                           | `Optional<DirectionEnum>` |
| `cmd_get_arg_item`              | `Command`          | `getArgItem(String argName)`                                                                                                                                | `Optional<ItemObject>`    |
| `cmd_get_arg_number`            | `Command`          | `getArgNumber(String argName)`                                                                                                                              | `Optional<Integer>`       |
| `cmd_get_arg_point`             | `Command`          | `getArgPoint(String argName)`                                                                                                                               | `Optional<Loc>`           |
| `cmd_get_arg_string`            | `Command`          | `getArgString(String argName)`                                                                                                                              | `Optional<String>`        |
| `cmd_get_arg_target`            | `Command`          | `getArgTarget(String argName)`                                                                                                                              | `Optional<DirectionEnum>` |
| `cmd_get_direction`             | `Command`          | `getDirection(String argName, boolean allow5)`                                                                                                              | `Optional<DirectionEnum>` |
| `cmd_get_effect_from_list`      | `Command`          | `getEffectFromList(String argName, String prompt, List<Effect> effects, int count, boolean allowRandom)`                                                    | `EffectChoice`            |
| `cmd_get_item`                  | `Command`          | `getItem(String argName, String prompt, String reject, Predicate<ItemObject> filter, Flag<GetItemFlags> mode)`                                              | `Optional<ItemObject>`    |
| `cmd_get_nrepeats`              | `CommandQueue`     | `getNrepeats()`                                                                                                                                             | `int`                     |
| `cmd_get_quantity`              | `Command`          | `getQuantity(String argName, int max)`                                                                                                                      | `Optional<Integer>`       |
| `cmd_get_spell`                 | `Command`          | `getSpell(String argName, String verb, Predicate<ItemObject> bookFilter, String bookError, BiPredicate<Player, MagicSpell> spellFilter, String spellError)` | `Optional<MagicSpell>`    |
| `cmd_get_string`                | `Command`          | `getString(String argName, String initial, String title, String prompt)`                                                                                    | `Optional<String>`        |
| `cmd_get_target`                | `Command`          | `getTarget(String argName)`                                                                                                                                 | `Optional<DirectionEnum>` |
| `cmd_set_arg`                   | `Command`          | `setArg(String argName, CommandArgumentType argumentType, CommandArgumentData value)`                                                                       | `void`                    |
| `cmd_set_arg_choice`            | `Command`          | `setArgChoice(String argName, int value)`                                                                                                                   | `void`                    |
| `cmd_set_arg_direction`         | `Command`          | `setArgDirection(String argName, DirectionEnum value)`                                                                                                      | `void`                    |
| `cmd_set_arg_item`              | `Command`          | `setArgItem(String argName, ItemObject value)`                                                                                                              | `void`                    |
| `cmd_set_arg_number`            | `Command`          | `setArgNumber(String argName, int value)`                                                                                                                   | `void`                    |
| `cmd_set_arg_point`             | `Command`          | `setArgPoint(String argName, Loc value)`                                                                                                                    | `void`                    |
| `cmd_set_arg_string`            | `Command`          | `setArgString(String argName, String value)`                                                                                                                | `void`                    |
| `cmd_set_arg_target`            | `Command`          | `setArgTarget(String argName, DirectionEnum value)`                                                                                                         | `void`                    |
| `cmd_set_repeat`                | `CommandQueue`     | `setRepeat(int numberOfRepeats)`                                                                                                                            | `void`                    |
| `cmd_verb`                      | `CommandProcessor` | `getCommandVerb(CommandCode code)`                                                                                                                          | `String`                  |
| `cmdq_execute`                  | `CommandQueue`     | `execute(CommandContext context)`                                                                                                                           | `void`                    |
| `cmdq_flush`                    | `CommandQueue`     | `flush()`                                                                                                                                                   | `void`                    |
| `cmdq_peek`                     | `CommandQueue`     | `commandQueuePeek()`                                                                                                                                        | `Command`                 |
| `cmdq_pop`                      | `CommandQueue`     | `getNextCommand()`                                                                                                                                          | `Command`                 |
| `cmdq_pop`                      | `CommandQueue`     | `commandPop(CommandContext commandContext)`                                                                                                                 | `boolean`                 |
| `cmdq_push`                     | `CommandQueue`     | `push(CommandCode cmd)`                                                                                                                                     | `boolean`                 |
| `cmdq_push_copy`                | `CommandQueue`     | `push(Command cmd)`                                                                                                                                         | `boolean`                 |
| `cmdq_push_repeat`              | `CommandQueue`     | `push(CommandCode command, int numberOfRepeats)`                                                                                                            | `boolean`                 |
| `cmdq_release`                  | `CommandQueue`     | `release()`                                                                                                                                                 | `void`                    |
| `process_command`               | `CommandProcessor` | `processCommand(CommandContext context, Command command, CommandQueue queue)`                                                                               | `void`                    |

## `game-event.c`

| C function                 | Java class      | Java signature                                                                        | Returns |
|----------------------------|-----------------|---------------------------------------------------------------------------------------|---------|
| `event_add_handler_set`    | `EventsHandler` | `eventAddHandlerSet(List<GameEventType> eventTypes, EventHandlerInterface record)`    | `void`  |
| `event_remove_handler_set` | `EventsHandler` | `eventRemoveHandlerSet(List<GameEventType> eventTypes, EventHandlerInterface record)` | `void`  |

## `game-input.c`

| C function            | Java class  | Java signature                                                                                                                  | Returns                |
|-----------------------|-------------|---------------------------------------------------------------------------------------------------------------------------------|------------------------|
| `confirm_debug`       | `GameInput` | `confirmDebug()`                                                                                                                | `boolean`              |
| `get_spell_from_book` | `Command`   | `getSpellFromBook(Player player, String verb, ItemObject book, String spellError, BiPredicate<Player, MagicSpell> spellFilter)` | `Optional<MagicSpell>` |

## `game-world.c`

| C function           | Java class      | Java signature                                  | Returns           |
|----------------------|-----------------|-------------------------------------------------|-------------------|
| `decrease_timeouts`  | `GameWorld`     | `decreaseTimeouts()`                            | `void`            |
| `is_daytime`         | `GameWorld`     | `isDaytime()`                                   | `boolean`         |
| `level_by_depth`     | `WorldRegistry` | `getLevelByDepth(int depth)`                    | `Optional<World>` |
| `level_by_name`      | `WorldRegistry` | `getLevelByName(String name)`                   | `Optional<World>` |
| `make_noise`         | `Chunk`         | `resetNoise()`                                  | `void`            |
| `make_noise`         | `GameWorld`     | `makeNoise()`                                   | `void`            |
| `on_leave_level`     | `GameWorld`     | `onLeaveLevel()`                                | `void`            |
| `on_new_level`       | `GameWorld`     | `onNewLevel()`                                  | `void`            |
| `play_ambient_sound` | `GameWorld`     | `playAmbientSound()`                            | `void`            |
| `process_player`     | `GameWorld`     | `processPlayer()`                               | `void`            |
| `recharge_objects`   | `GameWorld`     | `rechargeObjects()`                             | `void`            |
| `recharged_notice`   | `GameWorld`     | `rechargedNotice(ItemObject item, boolean all)` | `void`            |
| `run_game_loop`      | `GameWorld`     | `runGameLoop()`                                 | `void`            |
| `turn_energy`        | `GameWorld`     | `turnEnergy(int speed)`                         | `int`             |
| `update_scent`       | `Chunk`         | `updateScent()`                                 | `void`            |
| `update_scent`       | `GameWorld`     | `updateScent()`                                 | `void`            |

## `generate.c`

| C function                         | Java class | Java signature                  | Returns  |
|------------------------------------|------------|---------------------------------|----------|
| `get_room_builder_count`           | `RoomType` | `getRoomBuilderCount()`         | `int`    |
| `get_room_builder_index_from_name` | `RoomType` | `getIndexFromName(String name)` | `int`    |
| `get_room_builder_name_from_index` | `RoomType` | `getNameFromIndex(int index)`   | `String` |

## `main-win.c`

| C function       | Java class | Java signature            | Returns |
|------------------|------------|---------------------------|---------|
| `term_data_link` | `TermData` | `termDataLink(Term term)` | `void`  |

## `mon-make.c`

| C function         | Java class | Java signature                      | Returns |
|--------------------|------------|-------------------------------------|---------|
| `compact_monsters` | `Chunk`    | `compactMonsters(int numToCompact)` | `void`  |
| `delete_monster`   | `Chunk`    | `deleteMonster(Loc grid)`           | `void`  |

## `mon-predicate.c`

| C function               | Java class | Java signature           | Returns   |
|--------------------------|------------|--------------------------|-----------|
| `monster_is_camouflaged` | `Monster`  | `monsterIsCamouflaged()` | `boolean` |

## `mon-timed.c`

| C function        | Java class | Java signature                                                        | Returns   |
|-------------------|------------|-----------------------------------------------------------------------|-----------|
| `mon_clear_timed` | `Monster`  | `clearTimed(MonTimed timed, Flag<MonTimedFlags> flag)`                | `boolean` |
| `mon_dec_timed`   | `Monster`  | `decrementTimed(MonTimed timed, int timer, Flag<MonTimedFlags> flag)` | `boolean` |

## `mon-util.c`

| C function              | Java class        | Java signature                   | Returns       |
|-------------------------|-------------------|----------------------------------|---------------|
| `get_commanded_monster` | `MonsterUtils`    | `getCommandMonster()`            | `Monster`     |
| `lookup_monster`        | `MonsterRegistry` | `lookupMonsterRace(String name)` | `MonsterRace` |

## `obj-curse.c`

| C function                | Java class   | Java signature                              | Returns   |
|---------------------------|--------------|---------------------------------------------|-----------|
| `apply_curse_attributes`  | `ItemObject` | `applyCurseAttributes(Curse curseToIgnore)` | `void`    |
| `curses_are_equal`        | `CurseData`  | `equals(Object o)`                          | `boolean` |
| `modify_weight_for_curse` | `Curse`      | `modifyWeightForCurse(int weight)`          | `int`     |

## `obj-desc.c`

| C function             | Java class   | Java signature                                                          | Returns  |
|------------------------|--------------|-------------------------------------------------------------------------|----------|
| `obj_desc_name_format` | `ItemObject` | `objDescNameFormat(String string, String modString, boolean pluralise)` | `String` |
| `object_kind_name`     | `ItemObject` | `objectKindName(ObjectKind kind, boolean easyKnow)`                     | `String` |

## `obj-gear.c`

| C function                   | Java class   | Java signature                                                                                                                 | Returns                     |
|------------------------------|--------------|--------------------------------------------------------------------------------------------------------------------------------|-----------------------------|
| `combine_pack`               | `Player`     | `combinePack()`                                                                                                                | `void`                      |
| `equipped_item_by_slot_name` | `PlayerBody` | `equippedItemBySlotName(String name)`                                                                                          | `ItemObject`                |
| `gear_insert_end`            | `Player`     | `gearInsertEnd(ItemObject itemObject)`                                                                                         | `void`                      |
| `gear_to_label`              | `Player`     | `gearToLabel(ItemObject item)`                                                                                                 | `char`                      |
| `inven_can_stack_partial`    | `Player`     | `invenCanStackPartial(ItemObject item1, ItemObject item2, Flag<ObjectStackEnum> stackMode1, Flag<ObjectStackEnum> stackMode2)` | `boolean`                   |
| `object_is_carried`          | `Player`     | `isCarried(ItemObject item)`                                                                                                   | `boolean`                   |
| `object_is_equipped`         | `PlayerBody` | `itemIsEquipped(ItemObject item)`                                                                                              | `boolean`                   |
| `object_is_in_quiver`        | `ItemObject` | `isInQuiver(Player player)`                                                                                                    | `boolean`                   |
| `pack_slots_used`            | `Player`     | `packSlotsUsed()`                                                                                                              | `int`                       |
| `preferred_quiver_slot`      | `Player`     | `preferredQuiverSlot(ItemObject item)`                                                                                         | `int`                       |
| `quiver_absorb_num`          | `Player`     | `quiverAbsorbNum(ItemObject item, SplitBetweenPackAndQuiver splitIn)`                                                          | `SplitBetweenPackAndQuiver` |
| `slot_by_name`               | `Player`     | `slotByName(String name)`                                                                                                      | `int`                       |
| `slot_by_type`               | `Player`     | `slotByType(EquipmentSlotsEnum type, boolean full)`                                                                            | `int`                       |
| `wield_slot`                 | `ItemObject` | `wieldSlot()`                                                                                                                  | `int`                       |

## `obj-ignore.c`

| C function                | Java class   | Java signature                               | Returns            |
|---------------------------|--------------|----------------------------------------------|--------------------|
| `cmp_object_trait`        | `ItemObject` | `compareObjectTrait(int bonus, Random base)` | `int`              |
| `ego_is_ignored`          | `ItemObject` | `egoIsIgnored(IgnoreType type)`              | `boolean`          |
| `ignore_drop`             | `Player`     | `ignoreDrop()`                               | `void`             |
| `ignore_item_ok`          | `Player`     | `ignoreItemOK(ItemObject item)`              | `boolean`          |
| `ignore_level_of`         | `ItemObject` | `ignoreLevelOf()`                            | `QualityValueEnum` |
| `ignore_type_of`          | `ItemObject` | `getIgnoreTypeOf()`                          | `IgnoreType`       |
| `is_object_good`          | `ItemObject` | `isGood()`                                   | `int`              |
| `kind_ignore_when_aware`  | `ObjectKind` | `setIgnoredAware(boolean ignoredAware)`      | `void`             |
| `kind_is_ignored_unaware` | `ObjectKind` | `isIgnoredUnaware()`                         | `boolean`          |
| `object_is_ignored`       | `Player`     | `isIgnored(ItemObject item)`                 | `boolean`          |

## `obj-knowledge.c`

| C function                     | Java class       | Java signature                                                     | Returns   |
|--------------------------------|------------------|--------------------------------------------------------------------|-----------|
| `easy_know`                    | `ItemObject`     | `easyKnow()`                                                       | `boolean` |
| `equip_learn_flag`             | `Player`         | `equipLearnFlag(ObjectFlag flag)`                                  | `void`    |
| `equip_learn_on_defend`        | `Player`         | `equipLearnOnDefend()`                                             | `void`    |
| `equip_learn_on_melee_attack`  | `Player`         | `equipLearnOnMeleeAttack()`                                        | `void`    |
| `equip_learn_on_ranged_attack` | `Player`         | `equipLearnOnRangedAttack()`                                       | `void`    |
| `init_rune`                    | `Rune`           | `initRunes()`                                                      | `void`    |
| `max_runes`                    | `ObjectRegistry` | `getMaxRunes()`                                                    | `int`     |
| `object_curses_find_flags`     | `Player`         | `cursesFindFlags(ItemObject item, FlagView<ObjectFlag> testFlags)` | `boolean` |
| `object_curses_find_to_a`      | `Player`         | `cursesFindToA(ItemObject item)`                                   | `void`    |
| `object_curses_find_to_d`      | `Player`         | `cursesFindToD(ItemObject item)`                                   | `void`    |
| `object_curses_find_to_h`      | `Player`         | `cursesFindToH(ItemObject item)`                                   | `void`    |
| `object_flavor_aware`          | `Player`         | `flavourAware(ItemObject item)`                                    | `void`    |
| `object_flavor_is_aware`       | `ItemObject`     | `flavourIsAware()`                                                 | `boolean` |
| `object_flavor_is_aware`       | `ItemObject`     | `objectFlavourIsAware()`                                           | `boolean` |
| `object_fully_known`           | `ItemObject`     | `isFullyKnown()`                                                   | `boolean` |
| `object_has_standard_to_h`     | `ItemObject`     | `hasStandardToH()`                                                 | `boolean` |
| `object_non_curse_runes_known` | `Player`         | `nonCurseRunesKnown(ItemObject item)`                              | `boolean` |
| `object_set_base_known`        | `Player`         | `setBaseKnown(ItemObject item)`                                    | `void`    |
| `player_know_object`           | `Player`         | `knowObject(ItemObject item)`                                      | `void`    |
| `player_knows_brand`           | `KnownObject`    | `brandIsKnown(Brand brand)`                                        | `boolean` |
| `player_knows_brand`           | `Player`         | `knowsBrand(Brand brand)`                                          | `boolean` |
| `player_knows_curse`           | `KnownObject`    | `curseIsKnown(Curse curse)`                                        | `boolean` |
| `player_knows_curse`           | `Player`         | `knowsCurse(Curse curse)`                                          | `boolean` |
| `player_knows_ego`             | `Player`         | `knowsEgo(ItemObject item)`                                        | `boolean` |
| `player_knows_rune`            | `Player`         | `knowsRune(Rune rune)`                                             | `boolean` |
| `player_knows_slay`            | `KnownObject`    | `slayIsKnown(Slay slay)`                                           | `boolean` |
| `player_knows_slay`            | `Player`         | `knowsSlay(Slay slay)`                                             | `boolean` |
| `player_learn_all_runes`       | `Player`         | `learnAllRunes()`                                                  | `void`    |
| `player_learn_brand`           | `Player`         | `learnBrand(Brand brand)`                                          | `void`    |
| `player_learn_curse`           | `Player`         | `learnCurse(Curse curse)`                                          | `void`    |
| `player_learn_flag`            | `Player`         | `learnFlag(ObjectFlag flag)`                                       | `void`    |
| `player_learn_innate`          | `Player`         | `learnInnate()`                                                    | `void`    |
| `player_learn_rune`            | `Player`         | `learnRune(Rune rune, boolean printMessage)`                       | `void`    |
| `player_learn_slay`            | `Player`         | `learnSlay(Slay slay)`                                             | `void`    |
| `rune_index`                   | `Rune`           | `runeIndex(CombatRunes key)`                                       | `Rune`    |
| `rune_set_note`                | `Rune`           | `setNote(String note)`                                             | `void`    |

## `obj-pile.c`

| C function              | Java class   | Java signature                                                                                              | Returns      |
|-------------------------|--------------|-------------------------------------------------------------------------------------------------------------|--------------|
| `object_absorb`         | `ItemObject` | `objectAbsorb(ItemObject toAbsorb)`                                                                         | `void`       |
| `object_absorb_merge`   | `ItemObject` | `objectAbsorbMerge(ItemObject toAbsorb, Player player, boolean combineChargesTimeouts)`                     | `void`       |
| `object_absorb_partial` | `ItemObject` | `objectAbsorbPartial(ItemObject item2, Flag<ObjectStackEnum> stackMode1, Flag<ObjectStackEnum> stackMode2)` | `void`       |
| `object_copy`           | `ItemObject` | `copy(boolean includingKnown)`                                                                              | `ItemObject` |
| `object_mergeable`      | `ItemObject` | `mergeable(ItemObject toMerge, Flag<ObjectStackEnum> stackModes)`                                           | `boolean`    |
| `object_similar`        | `ItemObject` | `similar(ItemObject itm2, Flag<ObjectStackEnum> mode)`                                                      | `boolean`    |
| `object_split`          | `ItemObject` | `objectSplit(int amount)`                                                                                   | `ItemObject` |
| `object_stackable`      | `ItemObject` | `objectStackable(ItemObject toMerge, Flag<ObjectStackEnum> stackModes)`                                     | `boolean`    |

## `obj-power.c`

| C function                   | Java class   | Java signature                                               | Returns        |
|------------------------------|--------------|--------------------------------------------------------------|----------------|
| `ac_power`                   | `ItemObject` | `acPower(int power)`                                         | `int`          |
| `ammo_damage_power`          | `ItemObject` | `ammoDamagePower(int power)`                                 | `int`          |
| `bow_multiplier`             | `ItemObject` | `bowMulitplier()`                                            | `int`          |
| `curse_power`                | `ItemObject` | `cursePower(int power, boolean verbose, String logFileName)` | `int`          |
| `damage_dice_power`          | `ItemObject` | `damageDicePower()`                                          | `int`          |
| `effects_power`              | `ItemObject` | `effectsPower(int power)`                                    | `int`          |
| `element_power`              | `ItemObject` | `elementPower(int power)`                                    | `int`          |
| `extra_blows_power`          | `ItemObject` | `extraBlowsPower(int power)`                                 | `int`          |
| `extra_might_power`          | `ItemObject` | `extraMightPower(PowerAndMult incoming)`                     | `PowerAndMult` |
| `extra_shots_power`          | `ItemObject` | `extraShotsPower(int power)`                                 | `int`          |
| `flags_power`                | `ItemObject` | `flagsPower(int power)`                                      | `int`          |
| `jewelry_power`              | `ItemObject` | `jewelleryPower(int power)`                                  | `int`          |
| `launcher_ammo_damage_power` | `ItemObject` | `launcherAmmoDamagePower(int power)`                         | `int`          |
| `modifier_power`             | `ItemObject` | `modifierPower(int power)`                                   | `int`          |
| `nonstandard_weight_power`   | `ItemObject` | `nonStandardWeightPower(int power)`                          | `int`          |
| `object_power`               | `ItemObject` | `objectPower(boolean verbose, String logFileName)`           | `int`          |
| `object_value`               | `ItemObject` | `objectValue(int quantity)`                                  | `int`          |
| `object_value_base`          | `ItemObject` | `objectValueBase()`                                          | `int`          |
| `object_value_real`          | `ItemObject` | `objectValueReal(int quantity)`                              | `int`          |
| `rescale_bow_power`          | `ItemObject` | `rescaleBowPower(int power)`                                 | `int`          |
| `slay_power`                 | `ItemObject` | `slayPower(int power, boolean verbose, int dicePower)`       | `int`          |
| `to_ac_power`                | `ItemObject` | `toAcPower(int power)`                                       | `int`          |
| `to_damage_power`            | `ItemObject` | `toDamagePower()`                                            | `int`          |
| `to_hit_power`               | `ItemObject` | `toHitPower(int power)`                                      | `int`          |

## `obj-properties.c`

| C function            | Java class       | Java signature                                                                  | Returns          |
|-----------------------|------------------|---------------------------------------------------------------------------------|------------------|
| `flag_message`        | `ItemObject`     | `flagMessage(ObjectFlag flag, String name)`                                     | `void`           |
| `lookup_obj_property` | `ObjectRegistry` | `lookupObjectProperty(ObjPropertyType type, ObjectPropertyTypeWrapper payload)` | `ObjectProperty` |

## `obj-slays.c`

| C function            | Java class | Java signature                 | Returns   |
|-----------------------|------------|--------------------------------|-----------|
| `same_monsters_slain` | `Slay`     | `sameMonsterSlain(Slay other)` | `boolean` |

## `obj-tval.c`

| C function        | Java class   | Java signature               | Returns         |
|-------------------|--------------|------------------------------|-----------------|
| `tval_find_idx`   | `TValue`     | `findIndex(String tvalName)` | `int`           |
| `tval_find_idx`   | `TValue`     | `fromName(String name)`      | `TValue`        |
| `tval_find_idx`   | `TValue`     | `fromName(int i)`            | `TValue`        |
| `tval_is_digger`  | `ItemSource` | `isDigger()`                 | `boolean`       |
| `tval_sval_count` | `TValue`     | `tValSValCount(String name)` | `int`           |
| `tval_sval_list`  | `TValue`     | `tvalSvalList(String name)`  | `List<Integer>` |

## `obj-util.c`

| C function             | Java class   | Java signature                                                                         | Returns            |
|------------------------|--------------|----------------------------------------------------------------------------------------|--------------------|
| `check_for_inscrip`    | `ItemObject` | `checkForInscription(String s)`                                                        | `int`              |
| `number_charging`      | `ItemObject` | `numberCharging()`                                                                     | `int`              |
| `obj_can_browse`       | `ItemObject` | `canBrowse()`                                                                          | `boolean`          |
| `obj_can_browse`       | `ObjectKind` | `canBrowse()`                                                                          | `boolean`          |
| `object_flags`         | `ItemObject` | `objectFlags(Flag<ObjectFlag> flag)`                                                   | `void`             |
| `object_flags_known`   | `ItemObject` | `flagsKnown()`                                                                         | `Flag<ObjectFlag>` |
| `object_weight_one`    | `ItemObject` | `weightOne()`                                                                          | `int`              |
| `print_custom_message` | `ItemObject` | `printCustomMessage(String string, MessageType msgT, Player player, boolean noObject)` | `void`             |
| `recharge_timeout`     | `ItemObject` | `rechargeTimeout()`                                                                    | `boolean`          |
| `verify_object`        | `ItemObject` | `verifyObject(String prompt, Player player)`                                           | `boolean`          |

## `player-birth.c`

| C function      | Java class   | Java signature | Returns      |
|-----------------|--------------|----------------|--------------|
| `player_embody` | `PlayerBody` | `copy()`       | `PlayerBody` |

## `player-calcs.c`

| C function           | Java class     | Java signature                                                                                                      | Returns       |
|----------------------|----------------|---------------------------------------------------------------------------------------------------------------------|---------------|
| `adjust_skill_scale` | `Player`       | `adjustSkillScale(int value, int numerator, int denominator, int minValue)`                                         | `int`         |
| `average_spell_stat` | `Player`       | `averageSpellStat(PlayerState state)`                                                                               | `int`         |
| `calc_blows`         | `Player`       | `calcBlows(ItemObject item, PlayerState state, int extraBlows)`                                                     | `int`         |
| `calc_bonuses`       | `Player`       | `calcBonuses(PlayerState state, boolean knownOnly, boolean update)`                                                 | `void`        |
| `calc_hitpoints`     | `Player`       | `calcHitpoints()`                                                                                                   | `void`        |
| `calc_inventory`     | `Player`       | `calcInventory()`                                                                                                   | `void`        |
| `calc_light`         | `Player`       | `calcLight(PlayerState state, boolean update)`                                                                      | `void`        |
| `calc_mana`          | `Player`       | `calcMana(PlayerState state, boolean update)`                                                                       | `void`        |
| `calc_shapechange`   | `Player`       | `calcShapechange(PlayerState state, Map<ElementEnum, Boolean> vulnerabilities, PlayerShape shape, Extras incoming)` | `Extras`      |
| `earlier_object`     | `ItemObject`   | `earlierObject(ItemObject origObj, ItemObject newObj, boolean store)`                                               | `boolean`     |
| `equipped_item_slot` | `PlayerBody`   | `equippedItemSlot(ItemObject item)`                                                                                 | `int`         |
| `handle_stuff`       | `Player`       | `handleStuff()`                                                                                                     | `void`        |
| `health_track`       | `PlayerUpkeep` | `healthTrack(Monster monster)`                                                                                      | `void`        |
| `notice_stuff`       | `Player`       | `noticeStuff()`                                                                                                     | `void`        |
| `redraw_stuff`       | `Player`       | `redrawStuff()`                                                                                                     | `void`        |
| `update_bonuses`     | `Player`       | `updateBonuses()`                                                                                                   | `void`        |
| `update_bonuses`     | `PlayerState`  | `copy()`                                                                                                            | `PlayerState` |
| `update_stuff`       | `Player`       | `updateStuff()`                                                                                                     | `void`        |
| `update_stuff`       | `PlayerUpkeep` | `updateOff(PlayerUpdateEnum flag)`                                                                                  | `boolean`     |
| `weight_limit`       | `PlayerState`  | `weightLimit()`                                                                                                     | `int`         |

## `player-quest.c`

| C function | Java class | Java signature       | Returns   |
|------------|------------|----------------------|-----------|
| `is_quest` | `Player`   | `isQuest(int level)` | `boolean` |

## `player-spell.c`

| C function              | Java class    | Java signature                                       | Returns           |
|-------------------------|---------------|------------------------------------------------------|-------------------|
| `class_magic_realms`    | `PlayerClass` | `magicRealm()`                                       | `Set<MagicRealm>` |
| `player_object_to_book` | `Command`     | `playerObjectToBook(Player player, ItemObject item)` | `MagicBook`       |
| `spell_by_index`        | `ClassMagic`  | `spellByIndex(int spellIndex)`                       | `MagicSpell`      |

## `player-timed.c`

| C function                | Java class | Java signature                                                                      | Returns   |
|---------------------------|------------|-------------------------------------------------------------------------------------|-----------|
| `player_dec_timed`        | `Player`   | `decTimed(TimedEffect timedEffect, int amount, boolean notify, boolean canDisturb)` | `boolean` |
| `player_of_has_not_timed` | `Player`   | `playerOfHasNotTimed(ObjectFlag objectFlag)`                                        | `boolean` |
| `player_set_timed`        | `Player`   | `setTimed(TimedEffect timedEffect, int amount, boolean notify, boolean canDisturb)` | `boolean` |
| `player_timed_grade_eq`   | `Player`   | `timedGradeEq(TimedEffect index, String match)`                                     | `boolean` |

## `player-util.c`

| C function               | Java class    | Java signature                                     | Returns   |
|--------------------------|---------------|----------------------------------------------------|-----------|
| `dungeon_change_level`   | `PlayerUtils` | `dungeonChangeLevel(int dungeonLevel)`             | `void`    |
| `dungeon_get_next_level` | `PlayerUtils` | `dungeonGetNextLevel(int dungeonLevel, int added)` | `int`     |
| `modify_stat_value`      | `PlayerUtils` | `modifyStatValue(int value, int amount)`           | `int`     |
| `player_is_immune`       | `Player`      | `playerIsImmune(ElementEnum element)`              | `boolean` |
| `player_is_shapechanged` | `Player`      | `isShapeChanged()`                                 | `boolean` |
| `player_resting_count`   | `Player`      | `playerRestingCount()`                             | `int`     |

## `player.c`

| C function           | Java class     | Java signature                                           | Returns |
|----------------------|----------------|----------------------------------------------------------|---------|
| `init_player`        | `PlayerUpkeep` | `PlayerUpkeep()`                                         | `—`     |
| `player_exp_lose`    | `Player`       | `expLose(int amount, boolean permanent)`                 | `void`  |
| `player_flags`       | `Player`       | `playerFlags(PlayerState state, Flag<ObjectFlag> flags)` | `void`  |
| `player_flags_timed` | `Player`       | `flagsTimed(Flag<ObjectFlag> flags)`                     | `void`  |

## `ui-birth.c`

| C function             | Java class    | Java signature | Returns |
|------------------------|---------------|----------------|---------|
| `ui_enter_birthscreen` | `BirthEvents` | `enterBirth()` | `void`  |
| `ui_leave_birthscreen` | `BirthEvents` | `leaveBirth()` | `void`  |

## `ui-display.c`

| C function          | Java class     | Java signature                                                  | Returns |
|---------------------|----------------|-----------------------------------------------------------------|---------|
| `init_angband_aux`  | `UILoop`       | `initAngbandAux(String why)`                                    | `void`  |
| `splashscreen_note` | `InitHandlers` | `splashScreenNote(GameEventType eventType, GameEventData data)` | `void`  |
| `splashscreen_note` | `SplashScreen` | `splashScreenNote(String message)`                              | `void`  |
| `splashscreen_note` | `SplashScreen` | `splashScreenBirthNote(String message)`                         | `void`  |
| `ui_enter_game`     | `MainEvents`   | `enterGame()`                                                   | `void`  |
| `ui_enter_init`     | `InitHandlers` | `enterInit(GameEventType eventType, GameEventData data)`        | `void`  |
| `ui_enter_world`    | `MainEvents`   | `enterWorld()`                                                  | `void`  |
| `ui_leave_game`     | `MainEvents`   | `leaveGame()`                                                   | `void`  |
| `ui_leave_init`     | `MainEvents`   | `leaveInit()`                                                   | `void`  |
| `ui_leave_world`    | `MainEvents`   | `leaveWorld()`                                                  | `void`  |

## `ui-entry-combiner.c`

| C function                              | Java class                    | Java signature                                         | Returns                |
|-----------------------------------------|-------------------------------|--------------------------------------------------------|------------------------|
| `add_vec`                               | `AddCombiner`                 | `vec(int n, List<Integer> values, List<Integer> auxs)` | `UIEntryCombinerState` |
| `bitwise_or_vec`                        | `BitwiseOrCombiner`           | `vec(int n, List<Integer> values, List<Integer> auxs)` | `UIEntryCombinerState` |
| `dummy_combine_finish`                  | `SmallestCombiner`            | `finish()`                                             | `UIEntryCombinerState` |
| `first_vec`                             | `FirstCombiner`               | `vec(int n, List<Integer> values, List<Integer> auxs)` | `UIEntryCombinerState` |
| `largest_vec`                           | `LargestCombiner`             | `vec(int n, List<Integer> values, List<Integer> auxs)` | `UIEntryCombinerState` |
| `last_combine_accum`                    | `LastCombiner`                | `accum(int v, int a)`                                  | `void`                 |
| `last_vec`                              | `LastCombiner`                | `vec(int n, List<Integer> values, List<Integer> auxs)` | `UIEntryCombinerState` |
| `logical_combine_init`                  | `LogicalOrCombiner`           | `init(int v, int a)`                                   | `void`                 |
| `logical_or_vec`                        | `LogicalOrCombiner`           | `vec(int n, List<Integer> values, List<Integer> auxs)` | `UIEntryCombinerState` |
| `logical_or_with_cancel_combine_finish` | `LogicalOrWithCancelCombiner` | `finish()`                                             | `UIEntryCombinerState` |
| `logical_or_with_cancel_vec`            | `LogicalOrWithCancelCombiner` | `vec(int n, List<Integer> values, List<Integer> auxs)` | `UIEntryCombinerState` |
| `resist_0_vec`                          | `Resist0Combiner`             | `vec(int n, List<Integer> values, List<Integer> auxs)` | `UIEntryCombinerState` |
| `simple_combine_init`                   | `SmallestCombiner`            | `init(int v, int a)`                                   | `void`                 |
| `smallest_vec`                          | `SmallestCombiner`            | `vec(int n, List<Integer> values, List<Integer> auxs)` | `UIEntryCombinerState` |

## `ui-term.c`

| C function    | Java class | Java signature                                       | Returns |
|---------------|------------|------------------------------------------------------|---------|
| `Term_clear`  | `Window`   | `clear()`                                            | `void`  |
| `Term_putch`  | `SwingUI`  | `put(int row, int col, char c, ColourEnum colour)`   | `void`  |
| `Term_putstr` | `SwingUI`  | `put(int row, int col, String s, ColourEnum colour)` | `void`  |
| `term_init`   | `Term`     | `termInit(int width, int height, int keys)`          | `void`  |

## `z-bitflag.c`

| C function  | Java class | Java signature               | Returns   |
|-------------|------------|------------------------------|-----------|
| `flag_copy` | `Flag`     | `copyFrom(FlagView<E> flag)` | `void`    |
| `flag_off`  | `Square`   | `sqInfoOff(SquareEnum flag)` | `boolean` |

## `z-color.c`

| C function           | Java class   | Java signature                         | Returns      |
|----------------------|--------------|----------------------------------------|--------------|
| `attr_to_text`       | `ColourEnum` | `attributeToString(ColourEnum colour)` | `String`     |
| `color_char_to_attr` | `ColourEnum` | `fromCode(String code)`                | `ColourEnum` |
| `color_text_to_attr` | `ColourEnum` | `fromCode(String code)`                | `ColourEnum` |

## `z-rand.c`

| C function | Java class         | Java signature     | Returns |
|------------|--------------------|--------------------|---------|
| `Rand_div` | `RandomValueUtils` | `randDiv(int max)` | `int`   |

## `z-type.c`

| C function    | Java class | Java signature                   | Returns   |
|---------------|------------|----------------------------------|-----------|
| `loc_diff`    | `Loc`      | `diff(Loc other)`                | `Loc`     |
| `loc_eq`      | `Loc`      | `equals(Object obj)`             | `boolean` |
| `loc_is_zero` | `Loc`      | `isZero()`                       | `boolean` |
| `loc_offset`  | `Loc`      | `offset(int dx, int dy)`         | `Loc`     |
| `loc_sum`     | `Loc`      | `sum(Loc other)`                 | `Loc`     |
| `rand_loc`    | `Loc`      | `rand(int xSpread, int ySpread)` | `Loc`     |

## `z-util.c`

| C function   | Java class | Java signature            | Returns |
|--------------|------------|---------------------------|---------|
| `add_guardi` | `Guards`   | `addGuardI(int a, int b)` | `int`   |
| `sub_guardi` | `Guards`   | `subGuardI(int a, int b)` | `int`   |

