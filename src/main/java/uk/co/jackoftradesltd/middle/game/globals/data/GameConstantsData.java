/*
 * Copyright (c) 1987-2022 Angband contributors.
 *
 * This work is free software; you can redistribute it and/or modify it
 * under the terms of either:
 *
 * a) the GNU General Public License as published by the Free Software
 *    Foundation, version 2, or
 *
 * b) the Angband licence:
 *    This software may be copied and distributed for educational, research,
 *    and not for profit purposes provided that this copyright and statement
 *    are included in all such copies.  Other copyrights may also apply.
 *
 *    Java code and ANTLR4 grammars copyright (c) Rowan Crowther 2026
 */

package uk.co.jackoftradesltd.middle.game.globals.data;

import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Every game constant read from {@code constants.txt}, grouped by the directive that sets it. This is
 * the Java counterpart of the {@code constants.txt} half of C's {@code struct angband_constants}
 * ({@code init.h}), which C fills in place through the {@code parse_constants_*()} handlers in
 * {@code init.c} and then publishes as the global {@code z_info}. The other half of that struct, the
 * array bounds such as {@code k_max} and {@code r_max}, is set by the other data-file parsers and is
 * not held here.
 *
 * <p>C keeps all of these as flat fields on one struct. The port groups them into one record per
 * {@code constants.txt} directive ({@link LevelMaxData}, {@link MonGenData} and so on), and replaces
 * C's four singly linked lists of critical levels ({@code m_crit_level_head}, {@code r_crit_level_head},
 * {@code o_m_crit_level_head}, {@code o_r_crit_level_head}) with {@link List}s in file order.
 *
 * <p>Differences from C worth knowing:
 * <ul>
 *   <li>C declares the non-critical constants as {@code uint16_t}, and {@code init.c} only rejects
 *       negative values, so a value above 65535 is silently truncated in C. The port holds every
 *       constant as an {@link Integer} and keeps such a value as written.</li>
 *   <li>{@link GameConstantsBuilder#checkCriticalLevelDataLists()} also rejects a {@code -1} cutoff
 *       in the middle of the melee or ranged level list, which C allows. This is a deliberate Java-only
 *       rule, confirmed by Rowan on 261002.</li>
 *   <li>The four level lists are the builder's own {@link ArrayList}s, handed over without a copy, so
 *       they stay mutable after {@link GameConstantsBuilder#build(List)} returns. C's lists are just as
 *       mutable, so this matches C rather than diverging from it.</li>
 * </ul>
 *
 * <p>Record GameConstantsData coded before 261002, commented in full on 261002.
 *
 * @param levelMax             The {@code level-max} constants: the most monsters allowed on one level
 *                             ({@code z_info->level_monster_max})
 * @param monGen               The {@code mon-gen} constants: how often, how many and how far out of
 *                             depth monsters are generated
 * @param monPlay              The {@code mon-play} constants: monster behaviour during play, such as
 *                             glyph breaking, breeding rate, life drain and fleeing
 * @param dunGen               The {@code dun-gen} constants: room, door, wall-piercing, tunnel and pit
 *                             limits, and the average amounts of items and gold placed on a level
 * @param world                The {@code world} constants: dungeon depth and size, town size, day
 *                             length, level feelings, stair skip and move energy
 * @param carryCap             The {@code carry-cap} constants: pack, quiver and floor capacities
 * @param store                The {@code store} constants: store inventory size, turnover, owner
 *                             shuffling and the magic level for stock
 * @param objMake              The {@code obj-make} constants: object allocation depth, the chances of
 *                             inflating object and ego levels, and light-source fuel amounts
 * @param player               The {@code player} constants: sight and missile range, starting gold and
 *                             food value
 * @param meleeCritical        The {@code melee-critical} constants: the scale factors for the chance
 *                             and power of a standard melee critical hit in {@code critical_melee()}
 * @param meleeCriticalLevel   The {@code melee-critical-level} rows in file order. Each row's cutoff
 *                             caps the power for that level, apart from the last row's, which is never
 *                             read. An empty list means a standard melee hit is never critical
 * @param rangedCritical       The {@code ranged-critical} constants: the scale factors for the chance
 *                             and power of a standard ranged critical hit in {@code critical_shot()}
 * @param rangedCriticalLevel  The {@code ranged-critical-level} rows in file order, laid out like
 *                             {@code meleeCriticalLevel}. An empty list means a standard ranged hit is
 *                             never critical
 * @param oMeleeCritical       The {@code o-melee-critical} constants for O-combat melee criticals in
 *                             {@code o_critical_melee()}
 * @param oMeleeCriticalLevel  The {@code o-melee-critical-level} rows in file order. Each level is taken
 *                             with a one-in-{@code chance} roll, otherwise play moves on to the next
 *                             level; the last level catches whatever remains. An empty list means an
 *                             O-combat melee hit is never critical
 * @param oRangedCritical      The {@code o-ranged-critical} constants for O-combat ranged criticals in
 *                             {@code o_critical_shot()}
 * @param oRangedCriticalLevel The {@code o-ranged-critical-level} rows in file order, laid out like
 *                             {@code oMeleeCriticalLevel}
 * @author Rowan Crowther
 */
public record GameConstantsData(LevelMaxData levelMax, MonGenData monGen, MonPlayData monPlay,
                                DunGenData dunGen, WorldData world, CarryCapData carryCap,
                                StoreData store, ObjMakeData objMake, PlayerData player,
                                MeleeCriticalData meleeCritical, List<MeleeCriticalLevelData> meleeCriticalLevel,
                                RangedCriticalData rangedCritical, List<RangedCriticalLevelData> rangedCriticalLevel,
                                OMeleeCriticalData oMeleeCritical, List<OMeleeCriticalLevelData> oMeleeCriticalLevel,
                                ORangedCriticalData oRangedCritical,
                                List<ORangedCriticalLevelData> oRangedCriticalLevel) {

    /**
     * Collects {@code constants.txt} values one at a time as {@code GameConstantsAssembler} dispatches
     * each parsed line, then assembles them into a {@link GameConstantsData}. This plays the part of the
     * zeroed {@code struct angband_constants} that {@code init_parse_constants()} in {@code init.c}
     * allocates and hands to the parser as its private data. Each {@code parse_constants_*()} handler
     * writes one field of it, and {@code finish_parse_constants()} checks the critical level lists.
     *
     * <p>The constants live in {@link Integer} fields that start as {@code null}, so it is visible
     * whether a line set them. {@link #build(List)} turns any constant still {@code null} into
     * {@code 0}, which is what C's {@code mem_zalloc} leaves in a field that no line sets.
     *
     * <p>The setters store whatever they are given. Label matching, number parsing and message
     * lookup happen in {@code GameConstantsAssembler} before a setter is called. The builder does
     * its own checking only on the O-combat level rows ({@link #addOMeleeCriticalLevelData} and
     * {@link #addORangedCriticalLevel}) and in {@link #checkCriticalLevelDataLists()}.
     *
     * <p>Class GameConstantsBuilder coded before 261002, commented in full on 261002.
     *
     * @author Rowan Crowther
     */
    public static final class GameConstantsBuilder {
        // List of game constants - set here to ensure they are all accounted for during parsing
        /**
         * The {@code melee-critical-level} rows in file order. Port of C's
         * {@code z_info->m_crit_level_head} linked list. {@code critical_melee()} walks it and stops at
         * the first level whose cutoff is above the critical's power, or at the last level.
         *
         * <p>Field meleeCriticalLevelDataList coded before 261002, commented in full on 261002.
         */
        private final List<MeleeCriticalLevelData> meleeCriticalLevelDataList = new ArrayList<>();
        /**
         * The {@code ranged-critical-level} rows in file order. Port of C's
         * {@code z_info->r_crit_level_head} linked list, walked by {@code critical_shot()} the same way
         * {@link #meleeCriticalLevelDataList} is walked for melee.
         *
         * <p>Field rangedCriticalLevelDataList coded before 261002, commented in full on 261002.
         */
        private final List<RangedCriticalLevelData> rangedCriticalLevelDataList = new ArrayList<>();
        /**
         * The {@code o-melee-critical-level} rows in file order. Port of C's
         * {@code z_info->o_m_crit_level_head} linked list. {@code o_critical_melee()} walks it,
         * stopping at a level when a one-in-{@code chance} roll succeeds or when it reaches the last
         * level.
         *
         * <p>Field oMeleeCriticalLevelDataList coded before 261002, commented in full on 261002.
         */
        private final List<OMeleeCriticalLevelData> oMeleeCriticalLevelDataList = new ArrayList<>();
        /**
         * The {@code o-ranged-critical-level} rows in file order. Port of C's
         * {@code z_info->o_r_crit_level_head} linked list, walked by {@code o_critical_shot()} the same
         * way {@link #oMeleeCriticalLevelDataList} is walked for melee.
         *
         * <p>Field oRangedCriticalLevelDataList coded before 261002, commented in full on 261002.
         */
        private final List<ORangedCriticalLevelData> oRangedCriticalLevelDataList = new ArrayList<>();
        /**
         * The most monsters allowed on a single level, from {@code level-max:monsters}. Port of
         * {@code z_info->level_monster_max}.
         *
         * <p>Field levelMaxMonsters coded before 261002, commented in full on 261002.
         */
        private Integer levelMaxMonsters;
        /**
         * The one-in-N chance per game turn that a new monster is generated, from
         * {@code mon-gen:chance}. Port of {@code z_info->alloc_monster_chance}.
         *
         * <p>Field monGenChance coded before 261002, commented in full on 261002.
         */
        private Integer monGenChance;
        /**
         * The minimum number of monsters generated when a level is built, from
         * {@code mon-gen:level-min}. Port of {@code z_info->level_monster_min}.
         *
         * <p>Field monGenLevelMin coded before 261002, commented in full on 261002.
         */
        private Integer monGenLevelMin;
        /**
         * The number of townsfolk generated in the town by day, from {@code mon-gen:town-day}. Port of
         * {@code z_info->town_monsters_day}.
         *
         * <p>Field monGenTownDay coded before 261002, commented in full on 261002.
         */
        private Integer monGenTownDay;
        /**
         * The number of townsfolk generated in the town by night, from {@code mon-gen:town-night}.
         * Port of {@code z_info->town_monsters_night}.
         *
         * <p>Field monGenTownNight coded before 261002, commented in full on 261002.
         */
        private Integer monGenTownNight;
        /**
         * The most breeding monsters allowed on one level, from {@code mon-gen:repro-max}. Port of
         * {@code z_info->repro_monster_max}.
         *
         * <p>Field monGenReproMax coded before 261002, commented in full on 261002.
         */
        private Integer monGenReproMax;
        /**
         * The one-in-N chance that a generated monster is out of depth, from
         * {@code mon-gen:ood-chance}. Port of {@code z_info->ood_monster_chance}.
         *
         * <p>Field monGenOodChance coded before 261002, commented in full on 261002.
         */
        private Integer monGenOodChance;
        /**
         * The most levels out of depth a generated monster can be, from {@code mon-gen:ood-amount}.
         * Port of {@code z_info->ood_monster_amount}.
         *
         * <p>Field monGenOodAmount coded before 261002, commented in full on 261002.
         */
        private Integer monGenOodAmount;
        /**
         * The largest size of a monster group, from {@code mon-gen:group-max}. Port of
         * {@code z_info->monster_group_max}.
         *
         * <p>Field monGenGroupMax coded before 261002, commented in full on 261002.
         */
        private Integer monGenGroupMax;
        /**
         * The furthest a monster group may be placed from a related group, from
         * {@code mon-gen:group-dist}. Port of {@code z_info->monster_group_dist}.
         *
         * <p>Field monGenGroupDist coded before 261002, commented in full on 261002.
         */
        private Integer monGenGroupDist;
        /**
         * How hard it is for a monster to break a glyph of warding, from {@code mon-play:break-glyph}.
         * Port of {@code z_info->glyph_hardness}.
         *
         * <p>Field monPlayBreakGlyph coded before 261002, commented in full on 261002.
         */
        private Integer monPlayBreakGlyph;
        /**
         * The monster reproduction rate, from {@code mon-play:mult-rate}; a larger value means slower
         * breeding. Port of {@code z_info->repro_monster_rate}.
         *
         * <p>Field monPlayMultRate coded before 261002, commented in full on 261002.
         */
        private Integer monPlayMultRate;
        /**
         * The percentage of the player's life drained by a life-draining hit, from
         * {@code mon-play:life-drain}. Port of {@code z_info->life_drain_percent}.
         *
         * <p>Field monPlayLifeDrain coded before 261002, commented in full on 261002.
         */
        private Integer monPlayLifeDrain;
        /**
         * How many grids out of the player's view a fleeing monster runs, from
         * {@code mon-play:flee-range}. Port of {@code z_info->flee_range}.
         *
         * <p>Field monPlayFleeRange coded before 261002, commented in full on 261002.
         */
        private Integer monPlayFleeRange;
        /**
         * The distance inside which a frightened monster turns to fight, from
         * {@code mon-play:turn-range}. Port of {@code z_info->turn_range}.
         *
         * <p>Field monPlayTurnRange coded before 261002, commented in full on 261002.
         */
        private Integer monPlayTurnRange;
        /**
         * The most rooms (room centres) on a level, from {@code dun-gen:cent-max}. Port of
         * {@code z_info->level_room_max}.
         *
         * <p>Field dunGenCentMax coded before 261002, commented in full on 261002.
         */
        private Integer dunGenCentMax;
        /**
         * The most potential door locations on a level, from {@code dun-gen:door-max}. Port of
         * {@code z_info->level_door_max}.
         *
         * <p>Field dunGenDoorMax coded before 261002, commented in full on 261002.
         */
        private Integer dunGenDoorMax;
        /**
         * The most places where a tunnel may pierce a room wall, from {@code dun-gen:wall-max}. Port
         * of {@code z_info->wall_pierce_max}.
         *
         * <p>Field dunGenWallMax coded before 261002, commented in full on 261002.
         */
        private Integer dunGenWallMax;
        /**
         * The most tunnel grids on a level, from {@code dun-gen:tunn-max}. Port of
         * {@code z_info->tunn_grid_max}.
         *
         * <p>Field dunGenTunnMax coded before 261002, commented in full on 261002.
         */
        private Integer dunGenTunnMax;
        /**
         * The average number of items placed in rooms, from {@code dun-gen:amt-room}. Port of
         * {@code z_info->room_item_av}.
         *
         * <p>Field dunGenAmtRoom coded before 261002, commented in full on 261002.
         */
        private Integer dunGenAmtRoom;
        /**
         * The average number of items placed in random places (rooms or corridors), from
         * {@code dun-gen:amt-item}. Port of {@code z_info->both_item_av}.
         *
         * <p>Field dunGenAmtItem coded before 261002, commented in full on 261002.
         */
        private Integer dunGenAmtItem;
        /**
         * The average number of gold items placed in random places, from {@code dun-gen:amt-gold}.
         * Port of {@code z_info->both_gold_av}.
         *
         * <p>Field dunGenAmtGold coded before 261002, commented in full on 261002.
         */
        private Integer dunGenAmtGold;
        /**
         * The most monster pits or nests on a level, from {@code dun-gen:pit-max}. Port of
         * {@code z_info->level_pit_max}.
         *
         * <p>Field dunGenPitMax coded before 261002, commented in full on 261002.
         */
        private Integer dunGenPitMax;
        /**
         * The deepest dungeon level, from {@code world:max-depth}. Port of {@code z_info->max_depth}.
         *
         * <p>Field worldMaxDepth coded before 261002, commented in full on 261002.
         */
        private Integer worldMaxDepth;
        /**
         * The number of game turns from dawn to dawn, from {@code world:day-length}. Port of
         * {@code z_info->day_length}.
         *
         * <p>Field worldDayLength coded before 261002, commented in full on 261002.
         */
        private Integer worldDayLength;
        /**
         * The most vertical grids on a dungeon level, from {@code world:dungeon-hgt}. Port of
         * {@code z_info->dungeon_hgt}.
         *
         * <p>Field worldDungeonHgt coded before 261002, commented in full on 261002.
         */
        private Integer worldDungeonHgt;
        /**
         * The most horizontal grids on a dungeon level, from {@code world:dungeon-wid}. Port of
         * {@code z_info->dungeon_wid}.
         *
         * <p>Field worldDungeonWid coded before 261002, commented in full on 261002.
         */
        private Integer worldDungeonWid;
        /**
         * The most vertical grids in the town, from {@code world:town-hgt}. Port of
         * {@code z_info->town_hgt}.
         *
         * <p>Field worldTownHgt coded before 261002, commented in full on 261002.
         */
        private Integer worldTownHgt;
        /**
         * The most horizontal grids in the town, from {@code world:town-wid}. Port of
         * {@code z_info->town_wid}.
         *
         * <p>Field worldTownWid coded before 261002, commented in full on 261002.
         */
        private Integer worldTownWid;
        /**
         * The total number of feeling squares on a level, from {@code world:feeling-total}. Port of
         * {@code z_info->feeling_total}.
         *
         * <p>Field worldFeelingTotal coded before 261002, commented in full on 261002.
         */
        private Integer worldFeelingTotal;
        /**
         * The number of feeling squares the player must see before getting the first level feeling,
         * from {@code world:feeling-need}. Port of {@code z_info->feeling_need}.
         *
         * <p>Field worldFeelingNeed coded before 261002, commented in full on 261002.
         */
        private Integer worldFeelingNeed;
        /**
         * The number of levels each down staircase skips, from {@code world:stair-skip}. Port of
         * {@code z_info->stair_skip}.
         *
         * <p>Field worldStairSkip coded before 261002, commented in full on 261002.
         */
        private Integer worldStairSkip;
        /**
         * The energy the player or a monster needs to move, from {@code world:move-energy}. Port of
         * {@code z_info->move_energy}.
         *
         * <p>Field worldMoveEnergy coded before 261002, commented in full on 261002.
         */
        private Integer worldMoveEnergy;
        /**
         * The number of pack (inventory) slots, from {@code carry-cap:pack-size}. Port of
         * {@code z_info->pack_size}.
         *
         * <p>Field carryCapPackSize coded before 261002, commented in full on 261002.
         */
        private Integer carryCapPackSize;
        /**
         * The number of quiver slots, from {@code carry-cap:quiver-size}. Port of
         * {@code z_info->quiver_size}.
         *
         * <p>Field carryCapQuiverSize coded before 261002, commented in full on 261002.
         */
        private Integer carryCapQuiverSize;
        /**
         * The most missiles in one quiver slot, from {@code carry-cap:quiver-slot-size}. Port of
         * {@code z_info->quiver_slot_size}.
         *
         * <p>Field carryCapQuiverSlotSize coded before 261002, commented in full on 261002.
         */
        private Integer carryCapQuiverSlotSize;
        /**
         * The size multiplier for a non-ammunition throwing item kept in the quiver, from
         * {@code carry-cap:thrown-quiver-mult}. Port of {@code z_info->thrown_quiver_mult}.
         *
         * <p>Field carryCapThrownQuiverMult coded before 261002, commented in full on 261002.
         */
        private Integer carryCapThrownQuiverMult;
        /**
         * The most items on one floor grid, from {@code carry-cap:floor-size}. Port of
         * {@code z_info->floor_size}.
         *
         * <p>Field carryCapFloorSize coded before 261002, commented in full on 261002.
         */
        private Integer carryCapFloorSize;
        /**
         * The most objects in a store's inventory, from {@code store:inven-max}. Port of
         * {@code z_info->store_inven_max}.
         *
         * <p>Field storeInvenMax coded before 261002, commented in full on 261002.
         */
        private Integer storeInvenMax;
        /**
         * The number of game turns between store turnovers, from {@code store:turns}. Port of
         * {@code z_info->store_turns}.
         *
         * <p>Field storeTurns coded before 261002, commented in full on 261002.
         */
        private Integer storeTurns;
        /**
         * The one-in-N chance per day that a store's owner changes, from {@code store:shuffle}. Port
         * of {@code z_info->store_shuffle}.
         *
         * <p>Field storeShuffle coded before 261002, commented in full on 261002.
         */
        private Integer storeShuffle;
        /**
         * The level passed to {@code apply_magic()} for stock in normal stores, from
         * {@code store:magic-level}. Port of {@code z_info->store_magic_level}.
         *
         * <p>Field storeMagicLevel coded before 261002, commented in full on 261002.
         */
        private Integer storeMagicLevel;
        /**
         * The deepest level used in object allocation, from {@code obj-make:max-depth}. Port of
         * {@code z_info->max_obj_depth}.
         *
         * <p>Field objMakeMaxDepth coded before 261002, commented in full on 261002.
         */
        private Integer objMakeMaxDepth;
        /**
         * The one-in-N chance of inflating the level of a requested object, from
         * {@code obj-make:great-obj}. Port of {@code z_info->great_obj}.
         *
         * <p>Field objMakeGreatObj coded before 261002, commented in full on 261002.
         */
        private Integer objMakeGreatObj;
        /**
         * The one-in-N chance of inflating the level of a requested ego item, from
         * {@code obj-make:great-ego}. Port of {@code z_info->great_ego}.
         *
         * <p>Field objMakeGreatEgo coded before 261002, commented in full on 261002.
         */
        private Integer objMakeGreatEgo;
        /**
         * The most fuel a torch can hold, from {@code obj-make:fuel-torch}. Port of
         * {@code z_info->fuel_torch}.
         *
         * <p>Field objMakeFuelTorch coded before 261002, commented in full on 261002.
         */
        private Integer objMakeFuelTorch;
        /**
         * The most fuel a lantern can hold, from {@code obj-make:fuel-lamp}. Port of
         * {@code z_info->fuel_lamp}.
         *
         * <p>Field objMakeFuelLamp coded before 261002, commented in full on 261002.
         */
        private Integer objMakeFuelLamp;
        /**
         * The fuel a newly made lantern starts with, from {@code obj-make:default-lamp}. Port of
         * {@code z_info->default_lamp}.
         *
         * <p>Field objMakeDefaultLamp coded before 261002, commented in full on 261002.
         */
        private Integer objMakeDefaultLamp;
        /**
         * The player's maximum visual range, from {@code player:max-sight}. Port of
         * {@code z_info->max_sight}.
         *
         * <p>Field playerMaxSight coded before 261002, commented in full on 261002.
         */
        private Integer playerMaxSight;
        /**
         * The maximum range of missiles and spells, from {@code player:max-range}. Port of
         * {@code z_info->max_range}.
         *
         * <p>Field playerMaxRange coded before 261002, commented in full on 261002.
         */
        private Integer playerMaxRange;
        /**
         * The gold a new character starts with, from {@code player:start-gold}. Port of
         * {@code z_info->start_gold}.
         *
         * <p>Field playerStartGold coded before 261002, commented in full on 261002.
         */
        private Integer playerStartGold;
        /**
         * The number of game turns that 1% of the player's food lasts, from
         * {@code player:food-value}. Port of {@code z_info->food_value}.
         *
         * <p>Field playerFoodValue coded before 261002, commented in full on 261002.
         */
        private Integer playerFoodValue;
        /**
         * Added to the to-hit used for a standard melee critical when the target is debuffed, from
         * {@code melee-critical:debuff-toh}. Port of {@code z_info->m_crit_debuff_toh}, read by
         * {@code critical_melee()} in {@code player-attack.c}.
         *
         * <p>Field meleeCriticalDebuffToh coded before 261002, commented in full on 261002.
         */
        private Integer meleeCriticalDebuffToh;
        /**
         * The weapon-weight term in the chance of a standard melee critical, from
         * {@code melee-critical:chance-weight-scale}. Port of {@code z_info->m_crit_chance_weight_scl}.
         *
         * <p>Field meleeCriticalChanceWeightScale coded before 261002, commented in full on 261002.
         */
        private Integer meleeCriticalChanceWeightScale;
        /**
         * The to-hit term (player to-hit plus the weapon's bonus) in the chance of a standard melee
         * critical, from {@code melee-critical:chance-toh-scale}. Port of
         * {@code z_info->m_crit_chance_toh_scl}.
         *
         * <p>Field meleeCriticalChanceTohScale coded before 261002, commented in full on 261002.
         */
        private Integer meleeCriticalChanceTohScale;
        /**
         * The player-level term in the chance of a standard melee critical, from
         * {@code melee-critical:chance-level-scale}. Port of {@code z_info->m_crit_chance_level_scl}.
         *
         * <p>Field meleeCriticalChanceLevelScale coded before 261002, commented in full on 261002.
         */
        private Integer meleeCriticalChanceLevelScale;
        /**
         * The melee to-hit skill term in the chance of a standard melee critical, from
         * {@code melee-critical:chance-toh-skill-scale}. Port of
         * {@code z_info->m_crit_chance_toh_skill_scl}.
         *
         * <p>Field meleeCriticalChanceTohSkillScale coded before 261002, commented in full on 261002.
         */
        private Integer meleeCriticalChanceTohSkillScale;
        /**
         * The constant added to the chance of a standard melee critical, from
         * {@code melee-critical:chance-offset}. Port of {@code z_info->m_crit_chance_offset}.
         *
         * <p>Field meleeCriticalChanceOffset coded before 261002, commented in full on 261002.
         */
        private Integer meleeCriticalChanceOffset;
        /**
         * The die size the melee critical chance is rolled against: {@code critical_melee()} misses
         * the critical when {@code randint1(range)} exceeds the chance. From
         * {@code melee-critical:chance-range}; port of {@code z_info->m_crit_chance_range}.
         *
         * <p>Field meleeCriticalChanceRange coded before 261002, commented in full on 261002.
         */
        private Integer meleeCriticalChanceRange;
        /**
         * The weapon-weight term in the power of a standard melee critical, from
         * {@code melee-critical:power-weight-scale}. Port of {@code z_info->m_crit_power_weight_scl}.
         *
         * <p>Field meleeCriticalPowerWeightScale coded before 261002, commented in full on 261002.
         */
        private Integer meleeCriticalPowerWeightScale;
        /**
         * The die size of the random part of a standard melee critical's power ({@code randint1} of
         * this is added), from {@code melee-critical:power-random}. Port of
         * {@code z_info->m_crit_power_random}.
         *
         * <p>Field meleeCriticalPowerRandom coded before 261002, commented in full on 261002.
         */
        private Integer meleeCriticalPowerRandom;
        /**
         * Added to the to-hit used for a standard ranged critical when the target is debuffed, from
         * {@code ranged-critical:debuff-toh}. Port of {@code z_info->r_crit_debuff_toh}, read by
         * {@code critical_shot()} in {@code player-attack.c}.
         *
         * <p>Field rangedCriticalDebuffToh coded before 261002, commented in full on 261002.
         */
        private Integer rangedCriticalDebuffToh;
        /**
         * The missile-weight term in the chance of a standard ranged critical, from
         * {@code ranged-critical:chance-weight-scale}. Port of {@code z_info->r_crit_chance_weight_scl}.
         *
         * <p>Field rangedCriticalChanceWeightScale coded before 261002, commented in full on 261002.
         */
        private Integer rangedCriticalChanceWeightScale;
        /**
         * The to-hit term in the chance of a standard ranged critical, from
         * {@code ranged-critical:chance-toh-scale}. Port of {@code z_info->r_crit_chance_toh_scl}.
         *
         * <p>Field rangedCriticalChanceTohScale coded before 261002, commented in full on 261002.
         */
        private Integer rangedCriticalChanceTohScale;
        /**
         * The player-level term in the chance of a standard ranged critical, from
         * {@code ranged-critical:chance-level-scale}. Port of {@code z_info->r_crit_chance_level_scl}.
         *
         * <p>Field rangedCriticalChanceLevelScale coded before 261002, commented in full on 261002.
         */
        private Integer rangedCriticalChanceLevelScale;
        /**
         * The bow skill term in the chance of a ranged critical from a launched missile, from
         * {@code ranged-critical:chance-launched-toh-skill-scale}. Port of
         * {@code z_info->r_crit_chance_launched_toh_skill_scl}.
         *
         * <p>Field rangedCriticalChanceLaunchedTohSkillScale coded before 261002, commented in full on
         * 261002.
         */
        private Integer rangedCriticalChanceLaunchedTohSkillScale;
        /**
         * The throwing skill term in the chance of a ranged critical from a thrown object, from
         * {@code ranged-critical:chance-thrown-toh-skill-scale}. Port of
         * {@code z_info->r_crit_chance_thrown_toh_skill_scl}.
         *
         * <p>Field rangedCriticalChanceThrownTohSkillScale coded before 261002, commented in full on
         * 261002.
         */
        private Integer rangedCriticalChanceThrownTohSkillScale;
        /**
         * The constant added to the chance of a standard ranged critical, from
         * {@code ranged-critical:chance-offset}. Port of {@code z_info->r_crit_chance_offset}.
         *
         * <p>Field rangedCriticalChanceOffset coded before 261002, commented in full on 261002.
         */
        private Integer rangedCriticalChanceOffset;
        /**
         * The die size the ranged critical chance is rolled against: {@code critical_shot()} misses
         * the critical when {@code randint1(range)} exceeds the chance. From
         * {@code ranged-critical:chance-range}; port of {@code z_info->r_crit_chance_range}.
         *
         * <p>Field rangedCriticalChanceRange coded before 261002, commented in full on 261002.
         */
        private Integer rangedCriticalChanceRange;
        /**
         * The missile-weight term in the power of a standard ranged critical, from
         * {@code ranged-critical:power-weight-scale}. Port of {@code z_info->r_crit_power_weight_scl}.
         *
         * <p>Field rangedCriticalPowerWeightScale coded before 261002, commented in full on 261002.
         */
        private Integer rangedCriticalPowerWeightScale;
        /**
         * The die size of the random part of a standard ranged critical's power ({@code randint1} of
         * this is added), from {@code ranged-critical:power-random}. Port of
         * {@code z_info->r_crit_power_random}.
         *
         * <p>Field rangedCriticalPowerRandom coded before 261002, commented in full on 261002.
         */
        private Integer rangedCriticalPowerRandom;
        /**
         * Added to the power of an O-combat melee critical when the target is debuffed, from
         * {@code o-melee-critical:debuff-toh}. Port of {@code z_info->o_m_crit_debuff_toh}, read by
         * {@code o_critical_melee()} in {@code player-attack.c}.
         *
         * <p>Field oMeleeCriticalDebuffToh coded before 261002, commented in full on 261002.
         */
        private Integer oMeleeCriticalDebuffToh;
        /**
         * Numerator of the rational scale factor applied to the base melee hit chance to get an
         * O-combat critical's power, from {@code o-melee-critical:power-toh-scale-numerator}. Port of
         * {@code z_info->o_m_crit_power_toh_scl_num}.
         *
         * <p>Field oMeleeCriticalPowerTohScaleNumerator coded before 261002, commented in full on 261002.
         */
        private Integer oMeleeCriticalPowerTohScaleNumerator;
        /**
         * Denominator of the rational scale factor applied to the base melee hit chance to get an
         * O-combat critical's power, from {@code o-melee-critical:power-toh-scale-denominator}. Port
         * of {@code z_info->o_m_crit_power_toh_scl_den}. C divides by it with integer division.
         *
         * <p>Field oMeleeCriticalPowerTohScaleDenominator coded before 261002, commented in full on
         * 261002.
         */
        private Integer oMeleeCriticalPowerTohScaleDenominator;
        /**
         * The {@code a} in the O-combat melee critical chance {@code a * power / (b * power + c)}, from
         * {@code o-melee-critical:chance-power-scale-numerator}. Port of
         * {@code z_info->o_m_crit_chance_power_scl_num}.
         *
         * <p>Field oMeleeCriticalChancePowerScaleNumerator coded before 261002, commented in full on
         * 261002.
         */
        private Integer oMeleeCriticalChancePowerScaleNumerator;
        /**
         * The {@code b} in the O-combat melee critical chance {@code a * power / (b * power + c)}, from
         * {@code o-melee-critical:chance-power-scale-denominator}. Port of
         * {@code z_info->o_m_crit_chance_power_scl_den}.
         *
         * <p>Field oMeleeCriticalChancePowerScaleDenominator coded before 261002, commented in full on
         * 261002.
         */
        private Integer oMeleeCriticalChancePowerScaleDenominator;
        /**
         * The {@code c} in the O-combat melee critical chance {@code a * power / (b * power + c)}, from
         * {@code o-melee-critical:chance-add-denominator}. Port of
         * {@code z_info->o_m_crit_chance_add_den}.
         *
         * <p>Field oMeleeCriticalChanceAddDenominator coded before 261002, commented in full on 261002.
         */
        private Integer oMeleeCriticalChanceAddDenominator;
        /**
         * Added to the power of an O-combat ranged critical when the target is debuffed, from
         * {@code o-ranged-critical:debuff-toh}. Port of {@code z_info->o_r_crit_debuff_toh}, read by
         * {@code o_critical_shot()} in {@code player-attack.c}.
         *
         * <p>Field oRangedCriticalDebuffToh coded before 261002, commented in full on 261002.
         */
        private Integer oRangedCriticalDebuffToh;
        /**
         * Numerator of the scale factor applied to the base missile hit chance to get an O-combat
         * critical's power when the missile is launched, from
         * {@code o-ranged-critical:power-launched-toh-scale-numerator}. Port of
         * {@code z_info->o_r_crit_power_launched_toh_scl_num}.
         *
         * <p>Field oRangedCriticalPowerLaunchedTohScaleNumerator coded before 261002, commented in full
         * on 261002.
         */
        private Integer oRangedCriticalPowerLaunchedTohScaleNumerator;
        /**
         * Denominator of the scale factor applied to the base missile hit chance to get an O-combat
         * critical's power when the missile is launched, from
         * {@code o-ranged-critical:power-launched-toh-scale-denominator}. Port of
         * {@code z_info->o_r_crit_power_launched_toh_scl_den}. C divides by it with integer division.
         *
         * <p>Field oRangedCriticalPowerLaunchedTohScaleDenominator coded before 261002, commented in
         * full on 261002.
         */
        private Integer oRangedCriticalPowerLaunchedTohScaleDenominator;
        /**
         * Numerator of the scale factor applied to the base missile hit chance to get an O-combat
         * critical's power when the object is thrown, from
         * {@code o-ranged-critical:power-thrown-toh-scale-numerator}. Port of
         * {@code z_info->o_r_crit_power_thrown_toh_scl_num}.
         *
         * <p>Field oRangedCriticalPowerThrownTohScaleNumerator coded before 261002, commented in full on
         * 261002.
         */
        private Integer oRangedCriticalPowerThrownTohScaleNumerator;
        /**
         * Denominator of the scale factor applied to the base missile hit chance to get an O-combat
         * critical's power when the object is thrown, from
         * {@code o-ranged-critical:power-thrown-toh-scale-denominator}. Port of
         * {@code z_info->o_r_crit_power_thrown_toh_scl_den}. C divides by it with integer division.
         *
         * <p>Field oRangedCriticalPowerThrownTohScaleDenominator coded before 261002, commented in full
         * on 261002.
         */
        private Integer oRangedCriticalPowerThrownTohScaleDenominator;
        /**
         * The {@code a} in the O-combat ranged critical chance {@code a * power / (b * power + c)},
         * from {@code o-ranged-critical:chance-power-scale-numerator}. Port of
         * {@code z_info->o_r_crit_chance_power_scl_num}.
         *
         * <p>Field oRangedCriticalChancePowerScaleNumerator coded before 261002, commented in full on
         * 261002.
         */
        private Integer oRangedCriticalChancePowerScaleNumerator;
        /**
         * The {@code b} in the O-combat ranged critical chance {@code a * power / (b * power + c)},
         * from {@code o-ranged-critical:chance-power-scale-denominator}. Port of
         * {@code z_info->o_r_crit_chance_power_scl_den}.
         *
         * <p>Field oRangedCriticalChancePowerScaleDenominator coded before 261002, commented in full on
         * 261002.
         */
        private Integer oRangedCriticalChancePowerScaleDenominator;
        /**
         * The {@code c} in the O-combat ranged critical chance {@code a * power / (b * power + c)},
         * from {@code o-ranged-critical:chance-add-denominator}. Port of
         * {@code z_info->o_r_crit_chance_add_den}.
         *
         * <p>Field oRangedCriticalChanceAddDenominator coded before 261002, commented in full on 261002.
         */
        private Integer oRangedCriticalChanceAddDenominator;

        /**
         * Checks one {@code o-ranged-critical-level} row and, if it passes, appends it to
         * {@link #oRangedCriticalLevelDataList}. Port of the value checks and list append in
         * {@code parse_constants_o_ranged_critical_level()} in {@code init.c}.
         *
         * <p>C rejects a {@code chance} of {@code 0} with {@code PARSE_ERROR_INVALID_VALUE}. It declares
         * both {@code chance} and {@code dice} as {@code uint}, so the parser has already refused a
         * negative value before the handler runs. Java holds them as signed ints, so this method rejects
         * {@code chance <= 0} and {@code dice < 0} to cover both cases. The chance is checked first. A
         * rejected row is not added. C's message lookup ({@code PARSE_ERROR_INVALID_MESSAGE}) is done in
         * {@code GameConstantsAssembler} before this method is called.
         *
         * <p>Function addORangedCriticalLevel coded before 261002, commented in full on 261002.
         *
         * @param value the parsed row, with its message type already resolved
         * @return an empty string if the row was added, otherwise the error message
         */
        @Contract(mutates = "this")
        public String addORangedCriticalLevel(ORangedCriticalLevelData value) {
            if (value.chance() <= 0) {
                return "Negative or zero chance found in oRanged critical level data";
            }
            if (value.dice() < 0) {
                return "Negative dice found in oRanged critical level data";
            }
            oRangedCriticalLevelDataList.add(value);
            return "";
        }

        /**
         * Stores {@code o-ranged-critical:chance-add-denominator} in
         * {@link #oRangedCriticalChanceAddDenominator}. Port of the {@code "chance-add-denominator"}
         * branch of {@code parse_constants_o_ranged_critical()} in {@code init.c}.
         *
         * <p>Function setORangedCriticalChanceAddDenominator coded before 261002, commented in full on
         * 261002.
         *
         * @param value the {@code c} term added to the denominator of the O-combat ranged critical
         *              chance
         */
        @Contract(mutates = "this")
        public void setORangedCriticalChanceAddDenominator(Integer value) {
            oRangedCriticalChanceAddDenominator = value;
        }

        /**
         * Stores {@code o-ranged-critical:chance-power-scale-denominator} in
         * {@link #oRangedCriticalChancePowerScaleDenominator}. Port of the
         * {@code "chance-power-scale-denominator"} branch of {@code parse_constants_o_ranged_critical()}
         * in {@code init.c}.
         *
         * <p>Function setORangedCriticalChancePowerScaleDenominator coded before 261002, commented in
         * full on 261002.
         *
         * @param value the {@code b} multiplier on power in the denominator of the O-combat ranged
         *              critical chance
         */
        @Contract(mutates = "this")
        public void setORangedCriticalChancePowerScaleDenominator(Integer value) {
            oRangedCriticalChancePowerScaleDenominator = value;
        }

        /**
         * Stores {@code o-ranged-critical:chance-power-scale-numerator} in
         * {@link #oRangedCriticalChancePowerScaleNumerator}. Port of the
         * {@code "chance-power-scale-numerator"} branch of {@code parse_constants_o_ranged_critical()}
         * in {@code init.c}.
         *
         * <p>Function setORangedCriticalChancePowerScaleNumerator coded before 261002, commented in full
         * on 261002.
         *
         * @param value the {@code a} multiplier on power in the numerator of the O-combat ranged critical
         *              chance
         */
        @Contract(mutates = "this")
        public void setORangedCriticalChancePowerScaleNumerator(Integer value) {
            oRangedCriticalChancePowerScaleNumerator = value;
        }

        /**
         * Stores {@code o-ranged-critical:power-thrown-toh-scale-denominator} in
         * {@link #oRangedCriticalPowerThrownTohScaleDenominator}. Port of the
         * {@code "power-thrown-toh-scale-denominator"} branch of
         * {@code parse_constants_o_ranged_critical()} in {@code init.c}.
         *
         * <p>Function setORangedCriticalPowerThrownTohScaleDenominator coded before 261002, commented in
         * full on 261002.
         *
         * @param value denominator of the scale factor from hit chance to critical power for thrown
         *              objects
         */
        @Contract(mutates = "this")
        public void setORangedCriticalPowerThrownTohScaleDenominator(Integer value) {
            oRangedCriticalPowerThrownTohScaleDenominator = value;
        }

        /**
         * Stores {@code o-ranged-critical:power-thrown-toh-scale-numerator} in
         * {@link #oRangedCriticalPowerThrownTohScaleNumerator}. Port of the
         * {@code "power-thrown-toh-scale-numerator"} branch of
         * {@code parse_constants_o_ranged_critical()} in {@code init.c}.
         *
         * <p>Function setORangedCriticalPowerThrownTohScaleNumerator coded before 261002, commented in
         * full on 261002.
         *
         * @param value numerator of the scale factor from hit chance to critical power for thrown
         *              objects
         */
        @Contract(mutates = "this")
        public void setORangedCriticalPowerThrownTohScaleNumerator(Integer value) {
            oRangedCriticalPowerThrownTohScaleNumerator = value;
        }

        /**
         * Stores {@code o-ranged-critical:power-launched-toh-scale-denominator} in
         * {@link #oRangedCriticalPowerLaunchedTohScaleDenominator}. Port of the
         * {@code "power-launched-toh-scale-denominator"} branch of
         * {@code parse_constants_o_ranged_critical()} in {@code init.c}.
         *
         * <p>Function setORangedCriticalPowerLaunchedTohScaleDenominator coded before 261002, commented
         * in full on 261002.
         *
         * @param value denominator of the scale factor from hit chance to critical power for launched
         *              missiles
         */
        @Contract(mutates = "this")
        public void setORangedCriticalPowerLaunchedTohScaleDenominator(Integer value) {
            oRangedCriticalPowerLaunchedTohScaleDenominator = value;
        }

        /**
         * Stores {@code o-ranged-critical:power-launched-toh-scale-numerator} in
         * {@link #oRangedCriticalPowerLaunchedTohScaleNumerator}. Port of the
         * {@code "power-launched-toh-scale-numerator"} branch of
         * {@code parse_constants_o_ranged_critical()} in {@code init.c}.
         *
         * <p>Function setORangedCriticalPowerLaunchedTohScaleNumerator coded before 261002, commented in
         * full on 261002.
         *
         * @param value numerator of the scale factor from hit chance to critical power for launched
         *              missiles
         */
        @Contract(mutates = "this")
        public void setORangedCriticalPowerLaunchedTohScaleNumerator(Integer value) {
            oRangedCriticalPowerLaunchedTohScaleNumerator = value;
        }

        /**
         * Stores {@code o-ranged-critical:debuff-toh} in {@link #oRangedCriticalDebuffToh}. Port of the
         * {@code "debuff-toh"} branch of {@code parse_constants_o_ranged_critical()} in {@code init.c}.
         *
         * <p>Function setORangedCriticalDebuffToh coded before 261002, commented in full on 261002.
         *
         * @param value the amount added to an O-combat ranged critical's power against a debuffed
         *              target
         */
        @Contract(mutates = "this")
        public void setORangedCriticalDebuffToh(Integer value) {
            oRangedCriticalDebuffToh = value;
        }

        /**
         * Checks one {@code o-melee-critical-level} row and, if it passes, appends it to
         * {@link #oMeleeCriticalLevelDataList}. Port of the value checks and list append in
         * {@code parse_constants_o_melee_critical_level()} in {@code init.c}.
         *
         * <p>C rejects a {@code chance} of {@code 0} with {@code PARSE_ERROR_INVALID_VALUE}. It declares
         * both {@code chance} and {@code dice} as {@code uint}, so the parser has already refused a
         * negative value before the handler runs. Java holds them as signed ints, so this method rejects
         * {@code chance <= 0} and {@code dice < 0} to cover both cases. The chance is checked first. A
         * rejected row is not added. C's message lookup ({@code PARSE_ERROR_INVALID_MESSAGE}) is done in
         * {@code GameConstantsAssembler} before this method is called.
         *
         * <p>Function addOMeleeCriticalLevelData coded before 261002, commented in full on 261002.
         *
         * @param value the parsed row, with its message type already resolved
         * @return an empty string if the row was added, otherwise the error message
         */
        @Contract(mutates = "this")
        public String addOMeleeCriticalLevelData(OMeleeCriticalLevelData value) {
            if (value.chance() <= 0) {
                return "Negative or zero chance found in oMelee critical level data";
            }
            if (value.dice() < 0) {
                return "Negative dice found in oMelee critical level data";
            }
            oMeleeCriticalLevelDataList.add(value);
            return "";
        }

        /**
         * Stores {@code o-melee-critical:chance-add-denominator} in
         * {@link #oMeleeCriticalChanceAddDenominator}. Port of the {@code "chance-add-denominator"}
         * branch of {@code parse_constants_o_melee_critical()} in {@code init.c}.
         *
         * <p>Function setOMeleeCriticalChanceAddDenominator coded before 261002, commented in full on
         * 261002.
         *
         * @param value the {@code c} term added to the denominator of the O-combat melee critical
         *              chance
         */
        @Contract(mutates = "this")
        public void setOMeleeCriticalChanceAddDenominator(Integer value) {
            oMeleeCriticalChanceAddDenominator = value;
        }

        /**
         * Stores {@code o-melee-critical:chance-power-scale-denominator} in
         * {@link #oMeleeCriticalChancePowerScaleDenominator}. Port of the
         * {@code "chance-power-scale-denominator"} branch of {@code parse_constants_o_melee_critical()}
         * in {@code init.c}.
         *
         * <p>Function setOMeleeCriticalChancePowerScaleDenominator coded before 261002, commented in full
         * on 261002.
         *
         * @param value the {@code b} multiplier on power in the denominator of the O-combat melee
         *              critical chance
         */
        @Contract(mutates = "this")
        public void setOMeleeCriticalChancePowerScaleDenominator(Integer value) {
            oMeleeCriticalChancePowerScaleDenominator = value;
        }

        /**
         * Stores {@code o-melee-critical:chance-power-scale-numerator} in
         * {@link #oMeleeCriticalChancePowerScaleNumerator}. Port of the
         * {@code "chance-power-scale-numerator"} branch of {@code parse_constants_o_melee_critical()} in
         * {@code init.c}.
         *
         * <p>Function setOMeleeCriticalChancePowerScaleNumerator coded before 261002, commented in full
         * on 261002.
         *
         * @param value the {@code a} multiplier on power in the numerator of the O-combat melee critical
         *              chance
         */
        @Contract(mutates = "this")
        public void setOMeleeCriticalChancePowerScaleNumerator(Integer value) {
            oMeleeCriticalChancePowerScaleNumerator = value;
        }

        /**
         * Stores {@code o-melee-critical:power-toh-scale-denominator} in
         * {@link #oMeleeCriticalPowerTohScaleDenominator}. Port of the
         * {@code "power-toh-scale-denominator"} branch of {@code parse_constants_o_melee_critical()} in
         * {@code init.c}.
         *
         * <p>Function setOMeleeCriticalPowerTohScaleDenominator coded before 261002, commented in full on
         * 261002.
         *
         * @param value denominator of the scale factor from melee hit chance to critical power
         */
        @Contract(mutates = "this")
        public void setOMeleeCriticalPowerTohScaleDenominator(Integer value) {
            oMeleeCriticalPowerTohScaleDenominator = value;
        }

        /**
         * Stores {@code o-melee-critical:power-toh-scale-numerator} in
         * {@link #oMeleeCriticalPowerTohScaleNumerator}. Port of the
         * {@code "power-toh-scale-numerator"} branch of {@code parse_constants_o_melee_critical()} in
         * {@code init.c}.
         *
         * <p>Function setOMeleeCriticalPowerTohScaleNumerator coded before 261002, commented in full on
         * 261002.
         *
         * @param value numerator of the scale factor from melee hit chance to critical power
         */
        @Contract(mutates = "this")
        public void setOMeleeCriticalPowerTohScaleNumerator(Integer value) {
            oMeleeCriticalPowerTohScaleNumerator = value;
        }

        /**
         * Stores {@code o-melee-critical:debuff-toh} in {@link #oMeleeCriticalDebuffToh}. Port of the
         * {@code "debuff-toh"} branch of {@code parse_constants_o_melee_critical()} in {@code init.c}.
         *
         * <p>Function setOMeleeCriticalDebuffToh coded before 261002, commented in full on 261002.
         *
         * @param value the amount added to an O-combat melee critical's power against a debuffed target
         */
        @Contract(mutates = "this")
        public void setOMeleeCriticalDebuffToh(Integer value) {
            oMeleeCriticalDebuffToh = value;
        }

        /**
         * Appends one {@code ranged-critical-level} row to {@link #rangedCriticalLevelDataList}, with no
         * checks. Port of the list append in {@code parse_constants_ranged_critical_level()} in
         * {@code init.c}. C checks only the message name, and {@code GameConstantsAssembler} does that
         * before calling here. The cutoff order is checked once every row is in, by
         * {@link #checkCriticalLevelDataLists()}.
         *
         * <p>Function addRangedCriticalLevelData coded before 261002, commented in full on 261002.
         *
         * @param value the parsed row, with its message type already resolved
         */
        public void addRangedCriticalLevelData(RangedCriticalLevelData value) {
            rangedCriticalLevelDataList.add(value);
        }

        /**
         * Stores {@code ranged-critical:power-random} in {@link #rangedCriticalPowerRandom}. Port of the
         * {@code "power-random"} branch of {@code parse_constants_ranged_critical()} in {@code init.c}.
         *
         * <p>Function setRangedCriticalPowerRandom coded before 261002, commented in full on 261002.
         *
         * @param value the die size of the random part of a ranged critical's power
         */
        @Contract(mutates = "this")
        public void setRangedCriticalPowerRandom(Integer value) {
            rangedCriticalPowerRandom = value;
        }

        /**
         * Stores {@code ranged-critical:power-weight-scale} in {@link #rangedCriticalPowerWeightScale}.
         * Port of the {@code "power-weight-scale"} branch of {@code parse_constants_ranged_critical()} in
         * {@code init.c}.
         *
         * <p>Function setRangedCriticalPowerWeightScale coded before 261002, commented in full on 261002.
         *
         * @param value the multiplier on missile weight in a ranged critical's power
         */
        @Contract(mutates = "this")
        public void setRangedCriticalPowerWeightScale(Integer value) {
            rangedCriticalPowerWeightScale = value;
        }

        /**
         * Stores {@code ranged-critical:chance-range} in {@link #rangedCriticalChanceRange}. Port of the
         * {@code "chance-range"} branch of {@code parse_constants_ranged_critical()} in {@code init.c}.
         *
         * <p>Function setRangedCriticalChanceRange coded before 261002, commented in full on 261002.
         *
         * @param value the die size the ranged critical chance is rolled against
         */
        @Contract(mutates = "this")
        public void setRangedCriticalChanceRange(Integer value) {
            rangedCriticalChanceRange = value;
        }

        /**
         * Stores {@code ranged-critical:chance-offset} in {@link #rangedCriticalChanceOffset}. Port of
         * the {@code "chance-offset"} branch of {@code parse_constants_ranged_critical()} in
         * {@code init.c}.
         *
         * <p>Function setRangedCriticalChanceOffset coded before 261002, commented in full on 261002.
         *
         * @param value the constant added to the chance of a ranged critical
         */
        @Contract(mutates = "this")
        public void setRangedCriticalChanceOffset(Integer value) {
            rangedCriticalChanceOffset = value;
        }

        /**
         * Stores {@code ranged-critical:chance-thrown-toh-skill-scale} in
         * {@link #rangedCriticalChanceThrownTohSkillScale}. Port of the
         * {@code "chance-thrown-toh-skill-scale"} branch of {@code parse_constants_ranged_critical()} in
         * {@code init.c}.
         *
         * <p>Function setRangedCriticalChanceThrownTohSkillScale coded before 261002, commented in full on
         * 261002.
         *
         * @param value the multiplier on the throwing skill in the chance of a thrown critical
         */
        @Contract(mutates = "this")
        public void setRangedCriticalChanceThrownTohSkillScale(Integer value) {
            rangedCriticalChanceThrownTohSkillScale = value;
        }

        /**
         * Stores {@code ranged-critical:chance-launched-toh-skill-scale} in
         * {@link #rangedCriticalChanceLaunchedTohSkillScale}. Port of the
         * {@code "chance-launched-toh-skill-scale"} branch of {@code parse_constants_ranged_critical()}
         * in {@code init.c}.
         *
         * <p>Function setRangedCriticalChanceLaunchedTohSkillScale coded before 261002, commented in full
         * on 261002.
         *
         * @param value the multiplier on the bow skill in the chance of a launched critical
         */
        @Contract(mutates = "this")
        public void setRangedCriticalChanceLaunchedTohSkillScale(Integer value) {
            rangedCriticalChanceLaunchedTohSkillScale = value;
        }

        /**
         * Stores {@code ranged-critical:chance-level-scale} in {@link #rangedCriticalChanceLevelScale}.
         * Port of the {@code "chance-level-scale"} branch of {@code parse_constants_ranged_critical()} in
         * {@code init.c}.
         *
         * <p>Function setRangedCriticalChanceLevelScale coded before 261002, commented in full on 261002.
         *
         * @param value the multiplier on player level in the chance of a ranged critical
         */
        @Contract(mutates = "this")
        public void setRangedCriticalChanceLevelScale(Integer value) {
            rangedCriticalChanceLevelScale = value;
        }

        /**
         * Stores {@code ranged-critical:chance-toh-scale} in {@link #rangedCriticalChanceTohScale}. Port
         * of the {@code "chance-toh-scale"} branch of {@code parse_constants_ranged_critical()} in
         * {@code init.c}.
         *
         * <p>Function setRangedCriticalChanceTohScale coded before 261002, commented in full on 261002.
         *
         * @param value the multiplier on to-hit in the chance of a ranged critical
         */
        @Contract(mutates = "this")
        public void setRangedCriticalChanceTohScale(Integer value) {
            rangedCriticalChanceTohScale = value;
        }

        /**
         * Stores {@code ranged-critical:chance-weight-scale} in {@link #rangedCriticalChanceWeightScale}.
         * Port of the {@code "chance-weight-scale"} branch of {@code parse_constants_ranged_critical()} in
         * {@code init.c}.
         *
         * <p>Function setRangedCriticalChanceWeightScale coded before 261002, commented in full on 261002.
         *
         * @param value the multiplier on missile weight in the chance of a ranged critical
         */
        @Contract(mutates = "this")
        public void setRangedCriticalChanceWeightScale(Integer value) {
            rangedCriticalChanceWeightScale = value;
        }

        /**
         * Stores {@code ranged-critical:debuff-toh} in {@link #rangedCriticalDebuffToh}. Port of the
         * {@code "debuff-toh"} branch of {@code parse_constants_ranged_critical()} in {@code init.c}.
         *
         * <p>Function setRangedCriticalDebuffToh coded before 261002, commented in full on 261002.
         *
         * @param value the amount added to to-hit when working out the chance of a ranged critical
         *              against a debuffed target
         */
        @Contract(mutates = "this")
        public void setRangedCriticalDebuffToh(Integer value) {
            rangedCriticalDebuffToh = value;
        }

        /**
         * Appends one {@code melee-critical-level} row to {@link #meleeCriticalLevelDataList}, with no
         * checks. Port of the list append in {@code parse_constants_melee_critical_level()} in
         * {@code init.c}. C checks only the message name, and {@code GameConstantsAssembler} does that
         * before calling here. The cutoff order is checked once every row is in, by
         * {@link #checkCriticalLevelDataLists()}.
         *
         * <p>Function addMeleeCriticalLevelData coded before 261002, commented in full on 261002.
         *
         * @param value the parsed row, with its message type already resolved
         */
        public void addMeleeCriticalLevelData(MeleeCriticalLevelData value) {
            meleeCriticalLevelDataList.add(value);
        }

        /**
         * Checks that the power cutoffs in the melee list, then the ranged list, strictly increase.
         * Port of {@code check_critical_levels()} in {@code init.c}, which
         * {@code finish_parse_constants()} runs on {@code m_crit_level_head} and then
         * {@code r_crit_level_head}, failing with {@code PARSE_ERROR_NON_SEQUENTIAL_RECORDS}.
         *
         * <p>As in C, the last row's cutoff is never compared, because {@code critical_melee()} and
         * {@code critical_shot()} stop on the last level without reading its cutoff. The first row's
         * cutoff is compared only as the "previous" value for the second row. So every cutoff from the
         * second row to the second-to-last must be strictly greater than the one before it, and a list
         * of zero, one or two rows always passes.
         *
         * <p>Intentional divergence from C, confirmed by Rowan on 261002: a cutoff of {@code -1} in one
         * of the compared positions (second row to second-to-last) is rejected with its own "Invalid
         * sentinel" message, and that check runs before the order check. C has no such rule. It rejects
         * a middle {@code -1} only when it fails the order check, and then with the general
         * "not strictly increasing" error. (C accepts {@code [-5, -1, 10, -1]}, for instance; Java
         * rejects it.) {@code constants.txt} uses {@code -1} only as the unused last cutoff.
         *
         * <p>Function checkCriticalLevelDataLists coded before 261002, commented in full on 261002.
         *
         * @return an empty string if both lists pass, otherwise the first error found (melee before
         *         ranged)
         */
        public String checkCriticalLevelDataLists() {
            int listSize = meleeCriticalLevelDataList.size();
            int prevCutOff;
            if (listSize != 0) {
                prevCutOff = meleeCriticalLevelDataList.getFirst().powerCutoff();
                for (int index = 1; index < listSize - 1; index++) {
                    if (meleeCriticalLevelDataList.get(index).powerCutoff() == -1) {
                        return "Invalid sentinel value -1 found in middle of melee critical level data";
                    }
                    if (meleeCriticalLevelDataList.get(index).powerCutoff() <= prevCutOff) {
                        return "Melee critical level data not strictly increasing.";
                    }
                    prevCutOff = meleeCriticalLevelDataList.get(index).powerCutoff();
                }
//                if (meleeCriticalLevelDataList.getLast().powerCutoff() != -1) {
//                    return "No -1 sentinal at end of melee critical level data";
//                }
            }

            listSize = rangedCriticalLevelDataList.size();
            if (listSize != 0) {
                prevCutOff = rangedCriticalLevelDataList.getFirst().powerCutoff();
                for (int index = 1; index < listSize - 1; index++) {
                    if (rangedCriticalLevelDataList.get(index).powerCutoff() == -1) {
                        return "Invalid sentinel value -1 found in middle of ranged critical level data";
                    }
                    if (rangedCriticalLevelDataList.get(index).powerCutoff() <= prevCutOff) {
                        return "Ranged critical level data not strictly increasing.";
                    }
                    prevCutOff = rangedCriticalLevelDataList.get(index).powerCutoff();
                }
//                if (rangedCriticalLevelDataList.getLast().powerCutoff() != -1) {
//                    return "No -1 sentinal at end of ranged critical level data";
//                }
            }

            return "";
        }

        /**
         * Stores {@code melee-critical:power-random} in {@link #meleeCriticalPowerRandom}. Port of the
         * {@code "power-random"} branch of {@code parse_constants_melee_critical()} in {@code init.c}.
         *
         * <p>Function setMeleeCriticalPowerRandom coded before 261002, commented in full on 261002.
         *
         * @param value the die size of the random part of a melee critical's power
         */
        @Contract(mutates = "this")
        public void setMeleeCriticalPowerRandom(Integer value) {
            meleeCriticalPowerRandom = value;
        }

        /**
         * Stores {@code melee-critical:power-weight-scale} in {@link #meleeCriticalPowerWeightScale}.
         * Port of the {@code "power-weight-scale"} branch of {@code parse_constants_melee_critical()} in
         * {@code init.c}.
         *
         * <p>Function setMeleeCriticalPowerWeightScale coded before 261002, commented in full on 261002.
         *
         * @param value the multiplier on weapon weight in a melee critical's power
         */
        @Contract(mutates = "this")
        public void setMeleeCriticalPowerWeightScale(Integer value) {
            meleeCriticalPowerWeightScale = value;
        }

        /**
         * Stores {@code melee-critical:chance-range} in {@link #meleeCriticalChanceRange}. Port of the
         * {@code "chance-range"} branch of {@code parse_constants_melee_critical()} in {@code init.c}.
         *
         * <p>Function setMeleeCriticalChanceRange coded before 261002, commented in full on 261002.
         *
         * @param value the die size the melee critical chance is rolled against
         */
        @Contract(mutates = "this")
        public void setMeleeCriticalChanceRange(Integer value) {
            meleeCriticalChanceRange = value;
        }

        /**
         * Stores {@code melee-critical:chance-offset} in {@link #meleeCriticalChanceOffset}. Port of the
         * {@code "chance-offset"} branch of {@code parse_constants_melee_critical()} in {@code init.c}.
         *
         * <p>Function setMeleeCriticalChanceOffset coded before 261002, commented in full on 261002.
         *
         * @param value the constant added to the chance of a melee critical
         */
        @Contract(mutates = "this")
        public void setMeleeCriticalChanceOffset(Integer value) {
            meleeCriticalChanceOffset = value;
        }

        /**
         * Stores {@code melee-critical:chance-toh-skill-scale} in
         * {@link #meleeCriticalChanceTohSkillScale}. Port of the {@code "chance-toh-skill-scale"} branch
         * of {@code parse_constants_melee_critical()} in {@code init.c}.
         *
         * <p>Function setMeleeCriticalChanceTohSkillScale coded before 261002, commented in full on
         * 261002.
         *
         * @param value the multiplier on the melee skill in the chance of a melee critical
         */
        @Contract(mutates = "this")
        public void setMeleeCriticalChanceTohSkillScale(Integer value) {
            meleeCriticalChanceTohSkillScale = value;
        }

        /**
         * Stores {@code melee-critical:chance-level-scale} in {@link #meleeCriticalChanceLevelScale}.
         * Port of the {@code "chance-level-scale"} branch of {@code parse_constants_melee_critical()} in
         * {@code init.c}.
         *
         * <p>Function setMeleeCriticalChanceLevelScale coded before 261002, commented in full on 261002.
         *
         * @param value the multiplier on player level in the chance of a melee critical
         */
        @Contract(mutates = "this")
        public void setMeleeCriticalChanceLevelScale(Integer value) {
            meleeCriticalChanceLevelScale = value;
        }

        /**
         * Stores {@code melee-critical:chance-toh-scale} in {@link #meleeCriticalChanceTohScale}. Port of
         * the {@code "chance-toh-scale"} branch of {@code parse_constants_melee_critical()} in
         * {@code init.c}.
         *
         * <p>Function setMeleeCriticalChanceTohScale coded before 261002, commented in full on 261002.
         *
         * @param value the multiplier on to-hit in the chance of a melee critical
         */
        @Contract(mutates = "this")
        public void setMeleeCriticalChanceTohScale(Integer value) {
            meleeCriticalChanceTohScale = value;
        }

        /**
         * Stores {@code melee-critical:chance-weight-scale} in {@link #meleeCriticalChanceWeightScale}.
         * Port of the {@code "chance-weight-scale"} branch of {@code parse_constants_melee_critical()} in
         * {@code init.c}.
         *
         * <p>Function setMeleeCriticalChanceWeightScale coded before 261002, commented in full on 261002.
         *
         * @param value the multiplier on weapon weight in the chance of a melee critical
         */
        @Contract(mutates = "this")
        public void setMeleeCriticalChanceWeightScale(Integer value) {
            meleeCriticalChanceWeightScale = value;
        }

        /**
         * Stores {@code melee-critical:debuff-toh} in {@link #meleeCriticalDebuffToh}. Port of the
         * {@code "debuff-toh"} branch of {@code parse_constants_melee_critical()} in {@code init.c}.
         *
         * <p>Function setMeleeCriticalDebuffToh coded before 261002, commented in full on 261002.
         *
         * @param value the amount added to to-hit when working out the chance of a melee critical
         *              against a debuffed target
         */
        @Contract(mutates = "this")
        public void setMeleeCriticalDebuffToh(Integer value) {
            meleeCriticalDebuffToh = value;
        }

        /**
         * Stores {@code player:food-value} in {@link #playerFoodValue}. Port of the
         * {@code "food-value"} branch of {@code parse_constants_player()} in {@code init.c}.
         *
         * <p>Function setPlayerFoodValue coded before 261002, commented in full on 261002.
         *
         * @param value the number of game turns that 1% of the player's food lasts
         */
        @Contract(mutates = "this")
        public void setPlayerFoodValue(Integer value) {
            playerFoodValue = value;
        }

        /**
         * Stores {@code player:start-gold} in {@link #playerStartGold}. Port of the
         * {@code "start-gold"} branch of {@code parse_constants_player()} in {@code init.c}.
         *
         * <p>Function setPlayerStartGold coded before 261002, commented in full on 261002.
         *
         * @param value the gold a new character starts with
         */
        @Contract(mutates = "this")
        public void setPlayerStartGold(Integer value) {
            playerStartGold = value;
        }

        /**
         * Stores {@code player:max-range} in {@link #playerMaxRange}. Port of the {@code "max-range"}
         * branch of {@code parse_constants_player()} in {@code init.c}.
         *
         * <p>Function setPlayerMaxRange coded before 261002, commented in full on 261002.
         *
         * @param value the maximum range of missiles and spells
         */
        @Contract(mutates = "this")
        public void setPlayerMaxRange(Integer value) {
            playerMaxRange = value;
        }

        /**
         * Stores {@code player:max-sight} in {@link #playerMaxSight}. Port of the {@code "max-sight"}
         * branch of {@code parse_constants_player()} in {@code init.c}.
         *
         * <p>Function setPlayerMaxSight coded before 261002, commented in full on 261002.
         *
         * @param value the player's maximum visual range
         */
        @Contract(mutates = "this")
        public void setPlayerMaxSight(Integer value) {
            playerMaxSight = value;
        }

        /**
         * Stores {@code obj-make:default-lamp} in {@link #objMakeDefaultLamp}. Port of the
         * {@code "default-lamp"} branch of {@code parse_constants_obj_make()} in {@code init.c}.
         *
         * <p>Function setObjDefaultLamp coded before 261002, commented in full on 261002.
         *
         * @param value the fuel a newly made lantern starts with
         */
        @Contract(mutates = "this")
        public void setObjDefaultLamp(Integer value) {
            objMakeDefaultLamp = value;
        }

        /**
         * Stores {@code obj-make:fuel-lamp} in {@link #objMakeFuelLamp}. Port of the {@code "fuel-lamp"}
         * branch of {@code parse_constants_obj_make()} in {@code init.c}.
         *
         * <p>Function setObjFuelLamp coded before 261002, commented in full on 261002.
         *
         * @param value the most fuel a lantern can hold
         */
        @Contract(mutates = "this")
        public void setObjFuelLamp(Integer value) {
            objMakeFuelLamp = value;
        }

        /**
         * Stores {@code obj-make:fuel-torch} in {@link #objMakeFuelTorch}. Port of the
         * {@code "fuel-torch"} branch of {@code parse_constants_obj_make()} in {@code init.c}.
         *
         * <p>Function setObjFuelTorch coded before 261002, commented in full on 261002.
         *
         * @param value the most fuel a torch can hold
         */
        @Contract(mutates = "this")
        public void setObjFuelTorch(Integer value) {
            objMakeFuelTorch = value;
        }

        /**
         * Stores {@code obj-make:great-ego} in {@link #objMakeGreatEgo}. Port of the {@code "great-ego"}
         * branch of {@code parse_constants_obj_make()} in {@code init.c}.
         *
         * <p>Function setObjGreatEgo coded before 261002, commented in full on 261002.
         *
         * @param value the one-in-N chance of inflating the level of a requested ego item
         */
        @Contract(mutates = "this")
        public void setObjGreatEgo(Integer value) {
            objMakeGreatEgo = value;
        }

        /**
         * Stores {@code obj-make:great-obj} in {@link #objMakeGreatObj}. Port of the {@code "great-obj"}
         * branch of {@code parse_constants_obj_make()} in {@code init.c}.
         *
         * <p>Function setObjGreatObj coded before 261002, commented in full on 261002.
         *
         * @param value the one-in-N chance of inflating the level of a requested object
         */
        @Contract(mutates = "this")
        public void setObjGreatObj(Integer value) {
            objMakeGreatObj = value;
        }

        /**
         * Stores {@code obj-make:max-depth} in {@link #objMakeMaxDepth}. Port of the {@code "max-depth"}
         * branch of {@code parse_constants_obj_make()} in {@code init.c}.
         *
         * <p>Function setObjMakeMaxDepth coded before 261002, commented in full on 261002.
         *
         * @param value the deepest level used in object allocation
         */
        @Contract(mutates = "this")
        public void setObjMakeMaxDepth(Integer value) {
            objMakeMaxDepth = value;
        }

        /**
         * Stores {@code store:magic-level} in {@link #storeMagicLevel}. Port of the
         * {@code "magic-level"} branch of {@code parse_constants_store()} in {@code init.c}.
         *
         * <p>Function setStoreMagicLevel coded before 261002, commented in full on 261002.
         *
         * @param value the level passed to {@code apply_magic()} for stock in normal stores
         */
        @Contract(mutates = "this")
        public void setStoreMagicLevel(Integer value) {
            storeMagicLevel = value;
        }

        /**
         * Stores {@code store:shuffle} in {@link #storeShuffle}. Port of the {@code "shuffle"} branch of
         * {@code parse_constants_store()} in {@code init.c}.
         *
         * <p>Function setStoreShuffle coded before 261002, commented in full on 261002.
         *
         * @param value the one-in-N chance per day that a store's owner changes
         */
        @Contract(mutates = "this")
        public void setStoreShuffle(Integer value) {
            storeShuffle = value;
        }

        /**
         * Stores {@code store:turns} in {@link #storeTurns}. Port of the {@code "turns"} branch of
         * {@code parse_constants_store()} in {@code init.c}.
         *
         * <p>Function setStoreTurns coded before 261002, commented in full on 261002.
         *
         * @param value the number of game turns between store turnovers
         */
        @Contract(mutates = "this")
        public void setStoreTurns(Integer value) {
            storeTurns = value;
        }

        /**
         * Stores {@code store:inven-max} in {@link #storeInvenMax}. Port of the {@code "inven-max"}
         * branch of {@code parse_constants_store()} in {@code init.c}.
         *
         * <p>Function setStoreInvenMax coded before 261002, commented in full on 261002.
         *
         * @param value the most objects in a store's inventory
         */
        @Contract(mutates = "this")
        public void setStoreInvenMax(Integer value) {
            storeInvenMax = value;
        }

        /**
         * Stores {@code carry-cap:floor-size} in {@link #carryCapFloorSize}. Port of the
         * {@code "floor-size"} branch of {@code parse_constants_carry_cap()} in {@code init.c}.
         *
         * <p>Function setCarryCapFloorSize coded before 261002, commented in full on 261002.
         *
         * @param value the most items on one floor grid
         */
        @Contract(mutates = "this")
        public void setCarryCapFloorSize(Integer value) {
            carryCapFloorSize = value;
        }

        /**
         * Stores {@code carry-cap:thrown-quiver-mult} in {@link #carryCapThrownQuiverMult}. Port of the
         * {@code "thrown-quiver-mult"} branch of {@code parse_constants_carry_cap()} in {@code init.c}.
         *
         * <p>Function setCarryCapThrownQuiverMult coded before 261002, commented in full on 261002.
         *
         * @param value the size multiplier for a non-ammunition throwing item in the quiver
         */
        @Contract(mutates = "this")
        public void setCarryCapThrownQuiverMult(Integer value) {
            carryCapThrownQuiverMult = value;
        }

        /**
         * Stores {@code carry-cap:quiver-slot-size} in {@link #carryCapQuiverSlotSize}. Port of the
         * {@code "quiver-slot-size"} branch of {@code parse_constants_carry_cap()} in {@code init.c}.
         *
         * <p>Function setCarryCapQuiverSlotSize coded before 261002, commented in full on 261002.
         *
         * @param value the most missiles in one quiver slot
         */
        @Contract(mutates = "this")
        public void setCarryCapQuiverSlotSize(Integer value) {
            carryCapQuiverSlotSize = value;
        }

        /**
         * Stores {@code carry-cap:quiver-size} in {@link #carryCapQuiverSize}. Port of the
         * {@code "quiver-size"} branch of {@code parse_constants_carry_cap()} in {@code init.c}.
         *
         * <p>Function setCarryCapQuiverSize coded before 261002, commented in full on 261002.
         *
         * @param value the number of quiver slots
         */
        @Contract(mutates = "this")
        public void setCarryCapQuiverSize(Integer value) {
            carryCapQuiverSize = value;
        }

        /**
         * Stores {@code carry-cap:pack-size} in {@link #carryCapPackSize}. Port of the
         * {@code "pack-size"} branch of {@code parse_constants_carry_cap()} in {@code init.c}.
         *
         * <p>Function setCarryCapPackSize coded before 261002, commented in full on 261002.
         *
         * @param value the number of pack (inventory) slots
         */
        @Contract(mutates = "this")
        public void setCarryCapPackSize(Integer value) {
            carryCapPackSize = value;
        }

        /**
         * Stores {@code world:move-energy} in {@link #worldMoveEnergy}. Port of the
         * {@code "move-energy"} branch of {@code parse_constants_world()} in {@code init.c}.
         *
         * <p>Function setWorldMoveEnergy coded before 261002, commented in full on 261002.
         *
         * @param value the energy the player or a monster needs to move
         */
        @Contract(mutates = "this")
        public void setWorldMoveEnergy(Integer value) {
            this.worldMoveEnergy = value;
        }

        /**
         * Stores {@code world:stair-skip} in {@link #worldStairSkip}. Port of the {@code "stair-skip"}
         * branch of {@code parse_constants_world()} in {@code init.c}.
         *
         * <p>Function setWorldStairSkip coded before 261002, commented in full on 261002.
         *
         * @param value the number of levels each down staircase skips
         */
        @Contract(mutates = "this")
        public void setWorldStairSkip(Integer value) {
            worldStairSkip = value;
        }

        /**
         * Stores {@code world:feeling-need} in {@link #worldFeelingNeed}. Port of the
         * {@code "feeling-need"} branch of {@code parse_constants_world()} in {@code init.c}.
         *
         * <p>Function setWorldFeelingNeed coded before 261002, commented in full on 261002.
         *
         * @param value the number of feeling squares the player must see before the first level feeling
         */
        @Contract(mutates = "this")
        public void setWorldFeelingNeed(Integer value) {
            worldFeelingNeed = value;
        }

        /**
         * Stores {@code world:feeling-total} in {@link #worldFeelingTotal}. Port of the
         * {@code "feeling-total"} branch of {@code parse_constants_world()} in {@code init.c}.
         *
         * <p>Function setWorldFeelingTotal coded before 261002, commented in full on 261002.
         *
         * @param value the total number of feeling squares on a level
         */
        @Contract(mutates = "this")
        public void setWorldFeelingTotal(Integer value) {
            worldFeelingTotal = value;
        }

        /**
         * Stores {@code world:town-wid} in {@link #worldTownWid}. Port of the {@code "town-wid"} branch
         * of {@code parse_constants_world()} in {@code init.c}.
         *
         * <p>Function setWorldTownWid coded before 261002, commented in full on 261002.
         *
         * @param value the most horizontal grids in the town
         */
        @Contract(mutates = "this")
        public void setWorldTownWid(Integer value) {
            worldTownWid = value;
        }

        /**
         * Stores {@code world:town-hgt} in {@link #worldTownHgt}. Port of the {@code "town-hgt"} branch
         * of {@code parse_constants_world()} in {@code init.c}.
         *
         * <p>Function setWorldTownHgt coded before 261002, commented in full on 261002.
         *
         * @param value the most vertical grids in the town
         */
        @Contract(mutates = "this")
        public void setWorldTownHgt(Integer value) {
            worldTownHgt = value;
        }

        /**
         * Stores {@code world:dungeon-wid} in {@link #worldDungeonWid}. Port of the
         * {@code "dungeon-wid"} branch of {@code parse_constants_world()} in {@code init.c}.
         *
         * <p>Function setWorldDungeonWid coded before 261002, commented in full on 261002.
         *
         * @param value the most horizontal grids on a dungeon level
         */
        @Contract(mutates = "this")
        public void setWorldDungeonWid(Integer value) {
            worldDungeonWid = value;
        }

        /**
         * Stores {@code world:dungeon-hgt} in {@link #worldDungeonHgt}. Port of the
         * {@code "dungeon-hgt"} branch of {@code parse_constants_world()} in {@code init.c}.
         *
         * <p>Function setWorldDungeonHgt coded before 261002, commented in full on 261002.
         *
         * @param value the most vertical grids on a dungeon level
         */
        @Contract(mutates = "this")
        public void setWorldDungeonHgt(Integer value) {
            worldDungeonHgt = value;
        }

        /**
         * Stores {@code world:day-length} in {@link #worldDayLength}. Port of the {@code "day-length"}
         * branch of {@code parse_constants_world()} in {@code init.c}.
         *
         * <p>Function setWorldDayLength coded before 261002, commented in full on 261002.
         *
         * @param value the number of game turns from dawn to dawn
         */
        @Contract(mutates = "this")
        public void setWorldDayLength(Integer value) {
            worldDayLength = value;
        }

        /**
         * Stores {@code world:max-depth} in {@link #worldMaxDepth}. Port of the {@code "max-depth"}
         * branch of {@code parse_constants_world()} in {@code init.c}.
         *
         * <p>Function setWorldMaxDepth coded before 261002, commented in full on 261002.
         *
         * @param value the deepest dungeon level
         */
        @Contract(mutates = "this")
        public void setWorldMaxDepth(Integer value) {
            worldMaxDepth = value;
        }

        /**
         * Stores {@code dun-gen:pit-max} in {@link #dunGenPitMax}. Port of the {@code "pit-max"} branch
         * of {@code parse_constants_dun_gen()} in {@code init.c}.
         *
         * <p>Function setDunGenPitMax coded before 261002, commented in full on 261002.
         *
         * @param value the most monster pits or nests on a level
         */
        @Contract(mutates = "this")
        public void setDunGenPitMax(Integer value) {
            dunGenPitMax = value;
        }

        /**
         * Stores {@code dun-gen:amt-gold} in {@link #dunGenAmtGold}. Port of the {@code "amt-gold"}
         * branch of {@code parse_constants_dun_gen()} in {@code init.c}.
         *
         * <p>Function setDunGenAmtGold coded before 261002, commented in full on 261002.
         *
         * @param value the average number of gold items placed in random places
         */
        @Contract(mutates = "this")
        public void setDunGenAmtGold(Integer value) {
            dunGenAmtGold = value;
        }

        /**
         * Stores {@code dun-gen:amt-item} in {@link #dunGenAmtItem}. Port of the {@code "amt-item"}
         * branch of {@code parse_constants_dun_gen()} in {@code init.c}.
         *
         * <p>Function setDunGenAmtItem coded before 261002, commented in full on 261002.
         *
         * @param value the average number of items placed in random places (rooms or corridors)
         */
        @Contract(mutates = "this")
        public void setDunGenAmtItem(Integer value) {
            dunGenAmtItem = value;
        }

        /**
         * Stores {@code dun-gen:amt-room} in {@link #dunGenAmtRoom}. Port of the {@code "amt-room"}
         * branch of {@code parse_constants_dun_gen()} in {@code init.c}.
         *
         * <p>Function setDunGenAmtRoom coded before 261002, commented in full on 261002.
         *
         * @param value the average number of items placed in rooms
         */
        @Contract(mutates = "this")
        public void setDunGenAmtRoom(Integer value) {
            dunGenAmtRoom = value;
        }

        /**
         * Stores {@code dun-gen:tunn-max} in {@link #dunGenTunnMax}. Port of the {@code "tunn-max"}
         * branch of {@code parse_constants_dun_gen()} in {@code init.c}.
         *
         * <p>Function setDunGenTunnMax coded before 261002, commented in full on 261002.
         *
         * @param value the most tunnel grids on a level
         */
        @Contract(mutates = "this")
        public void setDunGenTunnMax(Integer value) {
            dunGenTunnMax = value;
        }


        /**
         * Stores {@code dun-gen:wall-max} in {@link #dunGenWallMax}. Port of the {@code "wall-max"}
         * branch of {@code parse_constants_dun_gen()} in {@code init.c}.
         *
         * <p>Function setDunGenWallMax coded before 261002, commented in full on 261002.
         *
         * @param value the most places where a tunnel may pierce a room wall on a level
         */
        @Contract(mutates = "this")
        public void setDunGenWallMax(Integer value) {
            dunGenWallMax = value;
        }

        /**
         * Stores {@code dun-gen:door-max} in {@link #dunGenDoorMax}. Port of the {@code "door-max"}
         * branch of {@code parse_constants_dun_gen()} in {@code init.c}.
         *
         * <p>Function setDunGenDoorMax coded before 261002, commented in full on 261002.
         *
         * @param value the most potential door locations on a level
         */
        @Contract(mutates = "this")
        public void setDunGenDoorMax(Integer value) {
            dunGenDoorMax = value;
        }

        /**
         * Stores {@code dun-gen:cent-max} in {@link #dunGenCentMax}. Port of the {@code "cent-max"}
         * branch of {@code parse_constants_dun_gen()} in {@code init.c}.
         *
         * <p>Function setDunGenCentMax coded before 261002, commented in full on 261002.
         *
         * @param value the most rooms (room centres) on a level
         */
        @Contract(mutates = "this")
        public void setDunGenCentMax(Integer value) {
            dunGenCentMax = value;
        }

        /**
         * Stores {@code mon-play:turn-range} in {@link #monPlayTurnRange}. Port of the
         * {@code "turn-range"} branch of {@code parse_constants_mon_play()} in {@code init.c}.
         *
         * <p>Function setMonPlayTurnRange coded before 261002, commented in full on 261002.
         *
         * @param value the distance inside which a frightened monster turns to fight
         */
        @Contract(mutates = "this")
        public void setMonPlayTurnRange(Integer value) {
            monPlayTurnRange = value;
        }

        /**
         * Stores {@code mon-play:flee-range} in {@link #monPlayFleeRange}. Port of the
         * {@code "flee-range"} branch of {@code parse_constants_mon_play()} in {@code init.c}.
         *
         * <p>Function setMonPlayFleeRange coded before 261002, commented in full on 261002.
         *
         * @param value how many grids out of the player's view a fleeing monster runs
         */
        @Contract(mutates = "this")
        public void setMonPlayFleeRange(Integer value) {
            monPlayFleeRange = value;
        }

        /**
         * Stores {@code mon-play:life-drain} in {@link #monPlayLifeDrain}. Port of the
         * {@code "life-drain"} branch of {@code parse_constants_mon_play()} in {@code init.c}.
         *
         * <p>Function setMonPlayLifeDrain coded before 261002, commented in full on 261002.
         *
         * @param value the percentage of the player's life drained by a life-draining hit
         */
        @Contract(mutates = "this")
        public void setMonPlayLifeDrain(Integer value) {
            monPlayLifeDrain = value;
        }

        /**
         * Stores {@code mon-play:mult-rate} in {@link #monPlayMultRate}. Port of the {@code "mult-rate"}
         * branch of {@code parse_constants_mon_play()} in {@code init.c}.
         *
         * <p>Function setMonPlayMultRate coded before 261002, commented in full on 261002.
         *
         * @param value the monster reproduction rate; a larger value means slower breeding
         */
        @Contract(mutates = "this")
        public void setMonPlayMultRate(Integer value) {
            monPlayMultRate = value;
        }

        /**
         * Stores {@code mon-play:break-glyph} in {@link #monPlayBreakGlyph}. Port of the
         * {@code "break-glyph"} branch of {@code parse_constants_mon_play()} in {@code init.c}.
         *
         * <p>Function setMonPlayBreakGlyph coded before 261002, commented in full on 261002.
         *
         * @param value how hard it is for a monster to break a glyph of warding
         */
        @Contract(mutates = "this")
        public void setMonPlayBreakGlyph(Integer value) {
            monPlayBreakGlyph = value;
        }

        /**
         * Stores {@code mon-gen:group-dist} in {@link #monGenGroupDist}. Port of the
         * {@code "group-dist"} branch of {@code parse_constants_mon_gen()} in {@code init.c}.
         *
         * <p>Function setMonGenGroupDist coded before 261002, commented in full on 261002.
         *
         * @param value the furthest a monster group may be placed from a related group
         */
        @Contract(mutates = "this")
        public void setMonGenGroupDist(Integer value) {
            monGenGroupDist = value;
        }

        /**
         * Stores {@code mon-gen:group-max} in {@link #monGenGroupMax}. Port of the {@code "group-max"}
         * branch of {@code parse_constants_mon_gen()} in {@code init.c}.
         *
         * <p>Function setMonGenGroupMax coded before 261002, commented in full on 261002.
         *
         * @param value the largest size of a monster group
         */
        @Contract(mutates = "this")
        public void setMonGenGroupMax(Integer value) {
            monGenGroupMax = value;
        }

        /**
         * Stores {@code mon-gen:ood-amount} in {@link #monGenOodAmount}. Port of the
         * {@code "ood-amount"} branch of {@code parse_constants_mon_gen()} in {@code init.c}.
         *
         * <p>Function setMonGenOodAmount coded before 261002, commented in full on 261002.
         *
         * @param value the most levels out of depth a generated monster can be
         */
        @Contract(mutates = "this")
        public void setMonGenOodAmount(Integer value) {
            monGenOodAmount = value;
        }

        /**
         * Stores {@code mon-gen:ood-chance} in {@link #monGenOodChance}. Port of the
         * {@code "ood-chance"} branch of {@code parse_constants_mon_gen()} in {@code init.c}.
         *
         * <p>Function setMonGenOodChance coded before 261002, commented in full on 261002.
         *
         * @param value the one-in-N chance that a generated monster is out of depth
         */
        @Contract(mutates = "this")
        public void setMonGenOodChance(Integer value) {
            monGenOodChance = value;
        }

        /**
         * Stores {@code mon-gen:repro-max} in {@link #monGenReproMax}. Port of the {@code "repro-max"}
         * branch of {@code parse_constants_mon_gen()} in {@code init.c}.
         *
         * <p>Function setMonGenReproMax coded before 261002, commented in full on 261002.
         *
         * @param value the most breeding monsters allowed on one level
         */
        @Contract(mutates = "this")
        public void setMonGenReproMax(Integer value) {
            monGenReproMax = value;
        }

        /**
         * Stores {@code mon-gen:town-night} in {@link #monGenTownNight}. Port of the
         * {@code "town-night"} branch of {@code parse_constants_mon_gen()} in {@code init.c}.
         *
         * <p>Function setMonGenTownNight coded before 261002, commented in full on 261002.
         *
         * @param value the number of townsfolk generated in the town by night
         */
        @Contract(mutates = "this")
        public void setMonGenTownNight(Integer value) {
            monGenTownNight = value;
        }

        /**
         * Stores {@code mon-gen:town-day} in {@link #monGenTownDay}. Port of the {@code "town-day"}
         * branch of {@code parse_constants_mon_gen()} in {@code init.c}.
         *
         * <p>Function setMonGenTownDay coded before 261002, commented in full on 261002.
         *
         * @param value the number of townsfolk generated in the town by day
         */
        @Contract(mutates = "this")
        public void setMonGenTownDay(Integer value) {
            monGenTownDay = value;
        }

        /**
         * Stores {@code mon-gen:level-min} in {@link #monGenLevelMin}. Port of the {@code "level-min"}
         * branch of {@code parse_constants_mon_gen()} in {@code init.c}.
         *
         * <p>Function setMonGenLevelMin coded before 261002, commented in full on 261002.
         *
         * @param value the minimum number of monsters generated when a level is built
         */
        @Contract(mutates = "this")
        public void setMonGenLevelMin(Integer value) {
            monGenLevelMin = value;
        }

        /**
         * Stores {@code level-max:monsters} in {@link #levelMaxMonsters}. Port of the
         * {@code "monsters"} branch of {@code parse_constants_level_max()} in {@code init.c}.
         *
         * <p>Function setLevelMaxMonsters coded before 261002, commented in full on 261002.
         *
         * @param value the most monsters allowed on a single level
         */
        @Contract(mutates = "this")
        public void setLevelMaxMonsters(Integer value) {
            levelMaxMonsters = value;
        }

        /**
         * Stores {@code mon-gen:chance} in {@link #monGenChance}. Port of the {@code "chance"} branch of
         * {@code parse_constants_mon_gen()} in {@code init.c}.
         *
         * <p>Function setMonGenChance coded before 261002, commented in full on 261002.
         *
         * @param value the one-in-N chance per game turn that a new monster is generated
         */
        @Contract(mutates = "this")
        public void setMonGenChance(Integer value) {
            monGenChance = value;
        }

        /**
         * Assembles the collected constants into a {@link GameConstantsData}. This plays the part of the
         * moment in {@code finish_parse_constants()} ({@code init.c}) when C publishes the parser's
         * private struct as {@code z_info}.
         *
         * <p>Every constant still {@code null} (no {@code constants.txt} line set it) is first replaced
         * with {@code 0}. Missing constants are not reported as errors. This matches C, where
         * {@code init_parse_constants()} allocates the struct with {@code mem_zalloc} and an unset field
         * just stays zero. Note that the defaulting writes into the builder's own fields, so it happens
         * even when the method then returns {@code null}.
         *
         * <p>If {@code errors} already holds anything (from the assembler, or from
         * {@link #checkCriticalLevelDataLists()}), the method returns {@code null} instead of a record,
         * as C's {@code run_parser()} fails the whole file on any error. Otherwise each group record is
         * built and the four critical level lists are passed into the result as they are, not copied.
         *
         * <p>Function build coded before 261002, commented in full on 261002.
         *
         * @param errors the errors collected so far while assembling {@code constants.txt}; only read
         *               here, never added to
         * @return the filled-in {@link GameConstantsData}, or {@code null} if {@code errors} is not
         *         empty
         */
        @Nullable
        @CheckReturnValue
        public GameConstantsData build(@NotNull List<String> errors) {
            // Level maxima
            if (levelMaxMonsters == null) {
                levelMaxMonsters = 0;
            }

            // Monster generation
            if (monGenChance == null) {
                monGenChance = 0;
            }
            if (monGenLevelMin == null) {
                monGenLevelMin = 0;
            }
            if (monGenTownDay == null) {
                monGenTownDay = 0;
            }
            if (monGenTownNight == null) {
                monGenTownNight = 0;
            }
            if (monGenReproMax == null) {
                monGenReproMax = 0;
            }
            if (monGenOodChance == null) {
                monGenOodChance = 0;
            }
            if (monGenOodAmount == null) {
                monGenOodAmount = 0;
            }
            if (monGenGroupMax == null) {
                monGenGroupMax = 0;
            }
            if (monGenGroupDist == null) {
                monGenGroupDist = 0;
            }

            // Monster Gameplay
            if (monPlayBreakGlyph == null) {
                monPlayBreakGlyph = 0;
            }
            if (monPlayMultRate == null) {
                monPlayMultRate = 0;
            }
            if (monPlayLifeDrain == null) {
                monPlayLifeDrain = 0;
            }
            if (monPlayFleeRange == null) {
                monPlayFleeRange = 0;
            }
            if (monPlayTurnRange == null) {
                monPlayTurnRange = 0;
            }

            // Dungeon Generation
            if (dunGenCentMax == null) {
                dunGenCentMax = 0;
            }
            if (dunGenDoorMax == null) {
                dunGenDoorMax = 0;
            }
            if (dunGenWallMax == null) {
                dunGenWallMax = 0;
            }
            if (dunGenTunnMax == null) {
                dunGenTunnMax = 0;
            }
            if (dunGenAmtRoom == null) {
                dunGenAmtRoom = 0;
            }
            if (dunGenAmtItem == null) {
                dunGenAmtItem = 0;
            }
            if (dunGenAmtGold == null) {
                dunGenAmtGold = 0;
            }
            if (dunGenPitMax == null) {
                dunGenPitMax = 0;
            }

            // Game World
            if (worldMaxDepth == null) {
                worldMaxDepth = 0;
            }
            if (worldDayLength == null) {
                worldDayLength = 0;
            }
            if (worldDungeonHgt == null) {
                worldDungeonHgt = 0;
            }
            if (worldDungeonWid == null) {
                worldDungeonWid = 0;
            }
            if (worldTownHgt == null) {
                worldTownHgt = 0;
            }
            if (worldTownWid == null) {
                worldTownWid = 0;
            }
            if (worldFeelingTotal == null) {
                worldFeelingTotal = 0;
            }
            if (worldFeelingNeed == null) {
                worldFeelingNeed = 0;
            }
            if (worldStairSkip == null) {
                worldStairSkip = 0;
            }
            if (worldMoveEnergy == null) {
                worldMoveEnergy = 0;
            }

            // Carry Capacity
            if (carryCapPackSize == null) {
                carryCapPackSize = 0;
            }
            if (carryCapQuiverSize == null) {
                carryCapQuiverSize = 0;
            }
            if (carryCapQuiverSlotSize == null) {
                carryCapQuiverSlotSize = 0;
            }
            if (carryCapThrownQuiverMult == null) {
                carryCapThrownQuiverMult = 0;
            }
            if (carryCapFloorSize == null) {
                carryCapFloorSize = 0;
            }

            // Store Parameters
            if (storeInvenMax == null) {
                storeInvenMax = 0;
            }
            if (storeTurns == null) {
                storeTurns = 0;
            }
            if (storeShuffle == null) {
                storeShuffle = 0;
            }
            if (storeMagicLevel == null) {
                storeMagicLevel = 0;
            }

            // Object Generation
            if (objMakeMaxDepth == null) {
                objMakeMaxDepth = 0;
            }
            if (objMakeGreatObj == null) {
                objMakeGreatObj = 0;
            }
            if (objMakeGreatEgo == null) {
                objMakeGreatEgo = 0;
            }
            if (objMakeFuelTorch == null) {
                objMakeFuelTorch = 0;
            }
            if (objMakeFuelLamp == null) {
                objMakeFuelLamp = 0;
            }
            if (objMakeDefaultLamp == null) {
                objMakeDefaultLamp = 0;
            }

            // Player Constants
            if (playerMaxSight == null) {
                playerMaxSight = 0;
            }
            if (playerMaxRange == null) {
                playerMaxRange = 0;
            }
            if (playerStartGold == null) {
                playerStartGold = 0;
            }
            if (playerFoodValue == null) {
                playerFoodValue = 0;
            }

            // Non-O critical melee calculations
            if (meleeCriticalDebuffToh == null) {
                meleeCriticalDebuffToh = 0;
            }
            if (meleeCriticalChanceWeightScale == null) {
                meleeCriticalChanceWeightScale = 0;
            }
            if (meleeCriticalChanceTohScale == null) {
                meleeCriticalChanceTohScale = 0;
            }
            if (meleeCriticalChanceLevelScale == null) {
                meleeCriticalChanceLevelScale = 0;
            }
            if (meleeCriticalChanceTohSkillScale == null) {
                meleeCriticalChanceTohSkillScale = 0;
            }
            if (meleeCriticalChanceOffset == null) {
                meleeCriticalChanceOffset = 0;
            }
            if (meleeCriticalChanceRange == null) {
                meleeCriticalChanceRange = 0;
            }
            if (meleeCriticalPowerWeightScale == null) {
                meleeCriticalPowerWeightScale = 0;
            }
            if (meleeCriticalPowerRandom == null) {
                meleeCriticalPowerRandom = 0;
            }

            // Non-O critical ranged calculations
            if (rangedCriticalDebuffToh == null) {
                rangedCriticalDebuffToh = 0;
            }
            if (rangedCriticalChanceWeightScale == null) {
                rangedCriticalChanceWeightScale = 0;
            }
            if (rangedCriticalChanceTohScale == null) {
                rangedCriticalChanceTohScale = 0;
            }
            if (rangedCriticalChanceLevelScale == null) {
                rangedCriticalChanceLevelScale = 0;
            }
            if (rangedCriticalChanceLaunchedTohSkillScale == null) {
                rangedCriticalChanceLaunchedTohSkillScale = 0;
            }
            if (rangedCriticalChanceThrownTohSkillScale == null) {
                rangedCriticalChanceThrownTohSkillScale = 0;
            }
            if (rangedCriticalChanceOffset == null) {
                rangedCriticalChanceOffset = 0;
            }
            if (rangedCriticalChanceRange == null) {
                rangedCriticalChanceRange = 0;
            }
            if (rangedCriticalPowerWeightScale == null) {
                rangedCriticalPowerWeightScale = 0;
            }
            if (rangedCriticalPowerRandom == null) {
                rangedCriticalPowerRandom = 0;
            }

            // O Critical Calculations
            if (oMeleeCriticalDebuffToh == null) {
                oMeleeCriticalDebuffToh = 0;
            }
            if (oMeleeCriticalPowerTohScaleNumerator == null) {
                oMeleeCriticalPowerTohScaleNumerator = 0;
            }
            if (oMeleeCriticalPowerTohScaleDenominator == null) {
                oMeleeCriticalPowerTohScaleDenominator = 0;
            }
            if (oMeleeCriticalChancePowerScaleNumerator == null) {
                oMeleeCriticalChancePowerScaleNumerator = 0;
            }
            if (oMeleeCriticalChancePowerScaleDenominator == null) {
                oMeleeCriticalChancePowerScaleDenominator = 0;
            }
            if (oMeleeCriticalChanceAddDenominator == null) {
                oMeleeCriticalChanceAddDenominator = 0;
            }

            // o-ranged criticals
            if (oRangedCriticalDebuffToh == null) {
                oRangedCriticalDebuffToh = 0;
            }
            if (oRangedCriticalPowerLaunchedTohScaleNumerator == null) {
                oRangedCriticalPowerLaunchedTohScaleNumerator = 0;
            }
            if (oRangedCriticalPowerLaunchedTohScaleDenominator == null) {
                oRangedCriticalPowerLaunchedTohScaleDenominator = 0;
            }
            if (oRangedCriticalPowerThrownTohScaleNumerator == null) {
                oRangedCriticalPowerThrownTohScaleNumerator = 0;
            }
            if (oRangedCriticalPowerThrownTohScaleDenominator == null) {
                oRangedCriticalPowerThrownTohScaleDenominator = 0;
            }
            if (oRangedCriticalChancePowerScaleNumerator == null) {
                oRangedCriticalChancePowerScaleNumerator = 0;
            }
            if (oRangedCriticalChancePowerScaleDenominator == null) {
                oRangedCriticalChancePowerScaleDenominator = 0;
            }
            if (oRangedCriticalChanceAddDenominator == null) {
                oRangedCriticalChanceAddDenominator = 0;
            }

            if (!errors.isEmpty())
                return null;

            LevelMaxData levelMaxData = new LevelMaxData(levelMaxMonsters);

            MonGenData monGenData = new MonGenData(monGenChance, monGenLevelMin, monGenTownDay, monGenTownNight,
                    monGenReproMax, monGenOodChance, monGenOodAmount, monGenGroupMax, monGenGroupDist);

            MonPlayData monPlayData = new MonPlayData(monPlayBreakGlyph, monPlayMultRate, monPlayLifeDrain,
                    monPlayFleeRange, monPlayTurnRange);

            DunGenData dunGenData = new DunGenData(dunGenCentMax, dunGenDoorMax, dunGenWallMax, dunGenTunnMax,
                    dunGenAmtRoom, dunGenAmtItem, dunGenAmtGold, dunGenPitMax);

            WorldData worldData = new WorldData(worldMaxDepth, worldDayLength, worldDungeonHgt, worldDungeonWid,
                    worldTownHgt, worldTownWid, worldFeelingTotal, worldFeelingNeed, worldStairSkip, worldMoveEnergy);

            CarryCapData carryCapData = new CarryCapData(carryCapPackSize, carryCapQuiverSize, carryCapQuiverSlotSize,
                    carryCapThrownQuiverMult, carryCapFloorSize);

            StoreData storeData = new StoreData(storeInvenMax, storeTurns, storeShuffle, storeMagicLevel);

            ObjMakeData objMakeData = new ObjMakeData(objMakeMaxDepth, objMakeGreatObj, objMakeGreatEgo,
                    objMakeFuelTorch, objMakeFuelLamp, objMakeDefaultLamp);

            PlayerData playerData = new PlayerData(playerMaxSight, playerMaxRange, playerStartGold, playerFoodValue);

            MeleeCriticalData meleeCriticalData = new MeleeCriticalData(meleeCriticalDebuffToh, meleeCriticalChanceWeightScale,
                    meleeCriticalChanceTohScale, meleeCriticalChanceLevelScale, meleeCriticalChanceTohSkillScale,
                    meleeCriticalChanceOffset, meleeCriticalChanceRange, meleeCriticalPowerWeightScale,
                    meleeCriticalPowerRandom);

            RangedCriticalData rangedCriticalData = new RangedCriticalData(rangedCriticalDebuffToh, rangedCriticalChanceWeightScale,
                    rangedCriticalChanceTohScale, rangedCriticalChanceLevelScale, rangedCriticalChanceLaunchedTohSkillScale,
                    rangedCriticalChanceThrownTohSkillScale, rangedCriticalChanceOffset, rangedCriticalChanceRange,
                    rangedCriticalPowerWeightScale, rangedCriticalPowerRandom);

            OMeleeCriticalData oMeleeCriticalData = new OMeleeCriticalData(oMeleeCriticalDebuffToh, oMeleeCriticalPowerTohScaleNumerator,
                    oMeleeCriticalPowerTohScaleDenominator, oMeleeCriticalChancePowerScaleNumerator, oMeleeCriticalChancePowerScaleDenominator,
                    oMeleeCriticalChanceAddDenominator);

            ORangedCriticalData oRangedCriticalData = new ORangedCriticalData(oRangedCriticalDebuffToh,
                    oRangedCriticalPowerLaunchedTohScaleNumerator, oRangedCriticalPowerLaunchedTohScaleDenominator,
                    oRangedCriticalPowerThrownTohScaleNumerator, oRangedCriticalPowerThrownTohScaleDenominator,
                    oRangedCriticalChancePowerScaleNumerator, oRangedCriticalChancePowerScaleDenominator,
                    oRangedCriticalChanceAddDenominator);

            return new GameConstantsData(levelMaxData, monGenData, monPlayData, dunGenData, worldData, carryCapData,
                    storeData, objMakeData, playerData, meleeCriticalData, meleeCriticalLevelDataList, rangedCriticalData,
                    rangedCriticalLevelDataList, oMeleeCriticalData, oMeleeCriticalLevelDataList, oRangedCriticalData,
                    oRangedCriticalLevelDataList);
        }
    }
}
