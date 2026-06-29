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

package uk.co.jackoftrades.middle.game.globals;

import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Record storing the various data groups which should be read in from the constants.txt file
 *
 * @param levelMax             The various maximums per level
 * @param monGen               Tne monster generation information
 * @param monPlay              The monster gameplay information
 * @param dunGen               Dungeon generation constants
 * @param world                Game world constants
 * @param carryCap             Constants relating to inventory
 * @param store                Constants relating to store generation
 * @param objMake              Constants relating to object generation
 * @param player               Constants relating to player
 * @param meleeCritical        Constants related to standard mêlée critical hits
 * @param meleeCriticalLevel   List of data for levels of standard mêlée critical
 *                             hits
 * @param rangedCritical       Constants related to ranged critical hits
 * @param rangedCriticalLevel  Each of the levels for ranged critical hits.
 *                             If none are defined then no extra damage will
 *                             occur for critical hits
 * @param oMeleeCritical       Constants related to non-standard (O) mêlée
 *                             critical hits
 * @param oMeleeCriticalLevel  List of data for levels of non-standard mêlée
 *                             criticals
 * @param oRangedCritical      Constants related to non-standard (O) ranged
 *                             critical hits
 * @param oRangedCriticalLevel List of levels of non-standard ranged criticals
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
     * Game constants builder
     *
     * <p>Consists of methods to log each constant in constants.txt, and store
     * it in the relevant area in GameConstantsData.
     *
     * @author Rowan Crowther
     */
    public static final class GameConstantsBuilder {
        // List of game constants - set here to ensure they are all accounted for during parsing
        private Integer levelMaxMonsters;

        private Integer monGenChance;
        private Integer monGenLevelMin;
        private Integer monGenTownDay;
        private Integer monGenTownNight;
        private Integer monGenReproMax;
        private Integer monGenOodChance;
        private Integer monGenOodAmount;
        private Integer monGenGroupMax;
        private Integer monGenGroupDist;

        private Integer monPlayBreakGlyph;
        private Integer monPlayMultRate;
        private Integer monPlayLifeDrain;
        private Integer monPlayFleeRange;
        private Integer monPlayTurnRange;

        private Integer dunGenCentMax;
        private Integer dunGenDoorMax;
        private Integer dunGenWallMax;
        private Integer dunGenTunnMax;
        private Integer dunGenAmtRoom;
        private Integer dunGenAmtItem;
        private Integer dunGenAmtGold;
        private Integer dunGenPitMax;

        private Integer worldMaxDepth;
        private Integer worldDayLength;
        private Integer worldDungeonHgt;
        private Integer worldDungeonWid;
        private Integer worldTownHgt;
        private Integer worldTownWid;
        private Integer worldFeelingTotal;
        private Integer worldFeelingNeed;
        private Integer worldStairSkip;
        private Integer worldMoveEnergy;

        private Integer carryCapPackSize;
        private Integer carryCapQuiverSize;
        private Integer carryCapQuiverSlotSize;
        private Integer carryCapThrownQuiverMult;
        private Integer carryCapFloorSize;

        private Integer storeInvenMax;
        private Integer storeTurns;
        private Integer storeShuffle;
        private Integer storeMagicLevel;

        private Integer objMakeMaxDepth;
        private Integer objMakeGreatObj;
        private Integer objMakeGreatEgo;
        private Integer objMakeFuelTorch;
        private Integer objMakeFuelLamp;
        private Integer objMakeDefaultLamp;

        private Integer playerMaxSight;
        private Integer playerMaxRange;
        private Integer playerStartGold;
        private Integer playerFoodValue;

        private Integer meleeCriticalDebuffToh;
        private Integer meleeCriticalChanceWeightScale;
        private Integer meleeCriticalChanceTohScale;
        private Integer meleeCriticalChanceLevelScale;
        private Integer meleeCriticalChanceTohSkillScale;
        private Integer meleeCriticalChanceOffset;
        private Integer meleeCriticalChanceRange;
        private Integer meleeCriticalPowerWeightScale;
        private Integer meleeCriticalPowerRandom;

        private final List<MeleeCriticalLevelData> meleeCriticalLevelDataList = new ArrayList<>();

        private Integer rangedCriticalDebuffToh;
        private Integer rangedCriticalChanceWeightScale;
        private Integer rangedCriticalChanceTohScale;
        private Integer rangedCriticalChanceLevelScale;
        private Integer rangedCriticalChanceLaunchedTohSkillScale;
        private Integer rangedCriticalChanceThrownTohSkillScale;
        private Integer rangedCriticalChanceOffset;
        private Integer rangedCriticalChanceRange;
        private Integer rangedCriticalPowerWeightScale;
        private Integer rangedCriticalPowerRandom;

        private final List<RangedCriticalLevelData> rangedCriticalLevelDataList = new ArrayList<>();

        private Integer oMeleeCriticalDebuffToh;
        private Integer oMeleeCriticalPowerTohScaleNumerator;
        private Integer oMeleeCriticalPowerTohScaleDenominator;
        private Integer oMeleeCriticalChancePowerScaleNumerator;
        private Integer oMeleeCriticalChancePowerScaleDenominator;
        private Integer oMeleeCriticalChanceAddDenominator;

        private final List<OMeleeCriticalLevelData> oMeleeCriticalLevelDataList = new ArrayList<>();

        private Integer oRangedCriticalDebuffToh;
        private Integer oRangedCriticalPowerLaunchedTohScaleNumerator;
        private Integer oRangedCriticalPowerLaunchedTohScaleDenominator;
        private Integer oRangedCriticalPowerThrownTohScaleNumerator;
        private Integer oRangedCriticalPowerThrownTohScaleDenominator;
        private Integer oRangedCriticalChancePowerScaleNumerator;
        private Integer oRangedCriticalChancePowerScaleDenominator;
        private Integer oRangedCriticalChanceAddDenominator;

        private final List<ORangedCriticalLevelData> oRangedCriticalLevelDataList = new ArrayList<>();

        /**
         * Add a new o-ranged-critical-level record to the list
         *
         * @param oRangedCriticalLevelData the record to add
         */
        @Contract(mutates = "this")
        public void addORangedCriticalLevel(ORangedCriticalLevelData oRangedCriticalLevelData) {
            oRangedCriticalLevelDataList.add(oRangedCriticalLevelData);
        }

        /**
         * Set the o-ranged-critical:chance-power-scale-denominator value
         *
         * @param value denominator scale factor for the critical power chance
         * @return false if this value has already been set (duplicate values are not allowed),
         * true otherwise
         * @author Rowan Crowther
         */
        @CheckReturnValue
        @Contract(mutates = "this")
        public boolean setORangedCriticalChanceAddDenominator(Integer value) {
            if (oRangedCriticalChanceAddDenominator != null) return false;
            oRangedCriticalChanceAddDenominator = value;
            return true;
        }

        /**
         * Set the o-ranged-critical:chance-power-scale-denominator value
         *
         * @param value denominator scale factor for the critical power chance
         * @return false if this value has already been set (duplicate values are not allowed),
         * true otherwise
         * @author Rowan Crowther
         */
        @CheckReturnValue
        @Contract(mutates = "this")
        public boolean setORangedCriticalChancePowerScaleDenominator(Integer value) {
            if (oRangedCriticalChancePowerScaleDenominator != null) return false;
            oRangedCriticalChancePowerScaleDenominator = value;
            return true;
        }

        /**
         * Set the o-ranged-critical:chance-power-scale-numerator value
         *
         * @param value numerator scale factor for the critical power chance
         * @return false if this value has already been set (duplicate values are not allowed),
         * true otherwise
         * @author Rowan Crowther
         */
        @CheckReturnValue
        @Contract(mutates = "this")
        public boolean setORangedCriticalChancePowerScaleNumerator(Integer value) {
            if (oRangedCriticalChancePowerScaleNumerator != null) return false;
            oRangedCriticalChancePowerScaleNumerator = value;
            return true;
        }

        /**
         * Set the o-ranged-critical:power-thrown-toh-scale-denominator value
         *
         * @param value scale factor denominator for to-hit value used to get power of thrown attack
         * @return false if this value has already been set (duplicate values are not allowed),
         * true otherwise
         * @author Rowan Crowther
         */
        @CheckReturnValue
        @Contract(mutates = "this")
        public boolean setORangedCriticalPowerThrownTohScaleDenominator(Integer value) {
            if (oRangedCriticalPowerThrownTohScaleDenominator != null) return false;
            oRangedCriticalPowerThrownTohScaleDenominator = value;
            return true;
        }

        /**
         * Set the o-ranged-critical:power-thrown-toh-scale-numerator value
         *
         * @param value scale factor numerator for to-hit value used to get power of thrown attack
         * @return false if this value has already been set (duplicate values are not allowed),
         * true otherwise
         * @author Rowan Crowther
         */
        @CheckReturnValue
        @Contract(mutates = "this")
        public boolean setORangedCriticalPowerThrownTohScaleNumerator(Integer value) {
            if (oRangedCriticalPowerThrownTohScaleNumerator != null) return false;
            oRangedCriticalPowerThrownTohScaleNumerator = value;
            return true;
        }

        /**
         * Set the o-ranged-critical:power-launched-toh-scale-denominator value
         *
         * @param value scale factor denominator for to-hit value used to get power of launched ranged attacks
         * @return false if this value has already been set (duplicate values are not allowed),
         * true otherwise
         * @author Rowan Crowther
         */
        @CheckReturnValue
        @Contract(mutates = "this")
        public boolean setORangedCriticalPowerLaunchedTohScaleDenominator(Integer value) {
            if (oRangedCriticalPowerLaunchedTohScaleDenominator != null) return false;
            oRangedCriticalPowerLaunchedTohScaleDenominator = value;
            return true;
        }

        /**
         * Set the o-ranged-critical:power-launched-toh-scale-numerator value
         *
         * @param value scale factor numerator for to-hit value used to get power of launched ranged attack
         * @return false if this value has already been set (duplicate values are not allowed),
         * true otherwise
         * @author Rowan Crowther
         */
        @CheckReturnValue
        @Contract(mutates = "this")
        public boolean setORangedCriticalPowerLaunchedTohScaleNumerator(Integer value) {
            if (oRangedCriticalPowerLaunchedTohScaleNumerator != null) return false;
            oRangedCriticalPowerLaunchedTohScaleNumerator = value;
            return true;
        }

        /**
         * Set the o-ranged-critical:debuff-toh value
         *
         * @param value added to to-hit value for debuffed target
         * @return false if this value has already been set (duplicate values are not allowed),
         * true otherwise
         * @author Rowan Crowther
         */
        @CheckReturnValue
        @Contract(mutates = "this")
        public boolean setORangedCriticalDebuffToh(Integer value) {
            if (oRangedCriticalDebuffToh != null) return false;
            oRangedCriticalDebuffToh = value;
            return true;
        }

        /**
         * Add a new o-melee-critical-level record to the list
         *
         * @param oMeleeCriticalLevelData the record to add
         */
        @Contract(mutates = "this")
        public void addOMeleeCriticalLevelData(OMeleeCriticalLevelData oMeleeCriticalLevelData) {
            oMeleeCriticalLevelDataList.add(oMeleeCriticalLevelData);
        }

        /**
         * Set the o-melee-critical:chance-add-denominator value
         *
         * @param value value of added term in denominator for critical chance
         * @return false if this value has already been set (duplicate values are not allowed),
         * true otherwise
         * @author Rowan Crowther
         */
        @CheckReturnValue
        @Contract(mutates = "this")
        public boolean setOMeleeCriticalChanceAddDenominator(Integer value) {
            if (oMeleeCriticalChanceAddDenominator != null) return false;
            oMeleeCriticalChanceAddDenominator = value;
            return true;
        }

        /**
         * Set the o-melee-critical:power-toh-scale-numerator value
         *
         * @param value numerator of scale factor applied to combined to-hit value
         * @return false if this value has already been set (duplicate values are not allowed),
         * true otherwise
         * @author Rowan Crowther
         */
        @CheckReturnValue
        @Contract(mutates = "this")
        public boolean setOMeleeCriticalChancePowerScaleDenominator(Integer value) {
            if (oMeleeCriticalChancePowerScaleDenominator != null) return false;
            oMeleeCriticalChancePowerScaleDenominator = value;
            return true;
        }

        /**
         * Set the o-melee-critical:chance-power-scale-numerator value
         *
         * @param value numerator of scale factor applied to critical's power
         * @return false if this value has already been set (duplicate values are not allowed),
         * true otherwise
         * @author Rowan Crowther
         */
        @CheckReturnValue
        @Contract(mutates = "this")
        public boolean setOMeleeCriticalChancePowerScaleNumerator(Integer value) {
            if (oMeleeCriticalChancePowerScaleNumerator != null) return false;
            oMeleeCriticalChancePowerScaleNumerator = value;
            return true;
        }

        /**
         * Set the o-melee-critical:power-toh-scale-denominator value
         *
         * @param value denominator of scale factor applied to combined to-hit value
         * @return false if this value has already been set (duplicate values are not allowed),
         * true otherwise
         * @author Rowan Crowther
         */
        @CheckReturnValue
        @Contract(mutates = "this")
        public boolean setOMeleeCriticalPowerTohScaleDenominator(Integer value) {
            if (oMeleeCriticalPowerTohScaleDenominator != null) return false;
            oMeleeCriticalPowerTohScaleDenominator = value;
            return true;
        }

        /**
         * Set the o-melee-critical:power-toh-scale-numerator value
         *
         * @param value numerator of scale factor applied to combined to-hit value
         * @return false if this value has already been set (duplicate values are not allowed),
         * true otherwise
         * @author Rowan Crowther
         */
        @CheckReturnValue
        @Contract(mutates = "this")
        public boolean setOMeleeCriticalPowerTohScaleNumerator(Integer value) {
            if (oMeleeCriticalPowerTohScaleNumerator != null) return false;
            oMeleeCriticalPowerTohScaleNumerator = value;
            return true;
        }

        /**
         * Set the o-melee-critical:debuff-toh value
         *
         * @param value amount added to to-hit if target is debuffed
         * @return false if this value has already been set (duplicate values are not allowed),
         * true otherwise
         * @author Rowan Crowther
         */
        @CheckReturnValue
        @Contract(mutates = "this")
        public boolean setOMeleeCriticalDebuffToh(Integer value) {
            if (oMeleeCriticalDebuffToh != null) return false;
            oMeleeCriticalDebuffToh = value;
            return true;
        }

        /**
         * Add a RangedCriticalData item to the rangedCriticalDataList
         *
         * @param value added to the rangedCriticalDataList
         */
        public void addRangedCriticalLevelData(RangedCriticalLevelData value) {
            rangedCriticalLevelDataList.add(value);
        }

        /**
         * Set the ranged-critical:power-random value
         *
         * @param value minimum of random part of power
         * @return false if this value has already been set (duplicate values are not allowed),
         * true otherwise
         * @author Rowan Crowther
         */
        @CheckReturnValue
        @Contract(mutates = "this")
        public boolean setRangedCriticalPowerRandom(Integer value) {
            if (rangedCriticalPowerRandom != null) return false;
            rangedCriticalPowerRandom = value;
            return true;
        }

        /**
         * Set the ranged-critical:power-weight-value
         *
         * @param value scale factor for missile weight
         * @return false if this value has already been set (duplicate values are not allowed),
         * true otherwise
         * @author Rowan Crowther
         */
        @CheckReturnValue
        @Contract(mutates = "this")
        public boolean setRangedCriticalPowerWeightScale(Integer value) {
            if (rangedCriticalPowerWeightScale != null) return false;
            rangedCriticalPowerWeightScale = value;
            return true;
        }

        /**
         * Set the ranged-critical:chance-range value
         *
         * @param value maximum range for a ranged critical chance
         * @return false if this value has already been set (duplicate values are not allowed),
         * true otherwise
         * @author Rowan Crowther
         */
        @CheckReturnValue
        @Contract(mutates = "this")
        public boolean setRangedCriticalChanceRange(Integer value) {
            if (rangedCriticalChanceRange != null) return false;
            rangedCriticalChanceRange = value;
            return true;
        }

        /**
         * Set the ranged-critical:chance-offset value
         *
         * @param value added to chance for a ranged critical
         * @return false if this value has already been set (duplicate values are not allowed),
         * true otherwise
         * @author Rowan Crowther
         */
        @CheckReturnValue
        @Contract(mutates = "this")
        public boolean setRangedCriticalChanceOffset(Integer value) {
            if (rangedCriticalChanceOffset != null) return false;
            rangedCriticalChanceOffset = value;
            return true;
        }

        /**
         * Set the ranged-critical:chance-thrown-toh-skill-scale value
         *
         * @param value scale factor for the thrown to-hit skill
         * @return false if this value has already been set (duplicate values are not allowed),
         * true otherwise
         * @author Rowan Crowther
         */
        @CheckReturnValue
        @Contract(mutates = "this")
        public boolean setRangedCriticalChanceThrownTohSkillScale(Integer value) {
            if (rangedCriticalChanceThrownTohSkillScale != null) return false;
            rangedCriticalChanceThrownTohSkillScale = value;
            return true;
        }

        /**
         * Set the ranged-critical:chance-launched-toh-skill-scale
         *
         * @param value scale factor for the launched to-hit skill
         * @return false if this value has already been set (duplicate values are not allowed),
         * true otherwise
         * @author Rowan Crowther
         */
        @CheckReturnValue
        @Contract(mutates = "this")
        public boolean setRangedCriticalChanceLaunchedTohSkillScale(Integer value) {
            if (rangedCriticalChanceLaunchedTohSkillScale != null) return false;
            rangedCriticalChanceLaunchedTohSkillScale = value;
            return true;
        }

        /**
         * Set the ranged-critical:chance-level-scale value
         *
         * @param value scale factor for player level
         * @return false if this value has already been set (duplicate values are not allowed),
         * true otherwise
         * @author Rowan Crowther
         */
        @CheckReturnValue
        @Contract(mutates = "this")
        public boolean setRangedCriticalChanceLevelScale(Integer value) {
            if (rangedCriticalChanceLevelScale != null) return false;
            rangedCriticalChanceLevelScale = value;
            return true;
        }

        /**
         * Set the ranged-critical:chance-toh-scale value
         *
         * @param value scale-factor for overall ranged to-hit value
         * @return false if this value has already been set (duplicate values are not allowed),
         * true otherwise
         * @author Rowan Crowther
         */
        @CheckReturnValue
        @Contract(mutates = "this")
        public boolean setRangedCriticalChanceTohScale(Integer value) {
            if (rangedCriticalChanceTohScale != null) return false;
            rangedCriticalChanceTohScale = value;
            return true;
        }

        /**
         * Set the ranged-critical:chance-weight-scale value
         *
         * @param value scale factor for the missile's weight
         * @return false if this value has already been set (duplicate values are not allowed),
         * true otherwise
         * @author Rowan Crowther
         */
        @CheckReturnValue
        @Contract(mutates = "this")
        public boolean setRangedCriticalChanceWeightScale(Integer value) {
            if (rangedCriticalChanceWeightScale != null) return false;
            rangedCriticalChanceWeightScale = value;
            return true;
        }

        /**
         * Set the ranged-critical:debuff-toh value
         *
         * @param value added to the to-hit value for calculating the chance of a ranged critical
         *              against a debuffed target
         * @return false if this value has already been set (duplicate values are not allowed),
         * true otherwise
         * @author Rowan Crowther
         */
        @CheckReturnValue
        @Contract(mutates = "this")
        public boolean setRangedCriticalDebuffToh(Integer value) {
            if (rangedCriticalDebuffToh != null) return false;
            rangedCriticalDebuffToh = value;
            return true;
        }

        /**
         * Add a MeleeCriticalData item to the meleeCriticalDataList
         *
         * @param value added to the meleeCriticalDataList
         */
        public void addMeleeCriticalLevelData(MeleeCriticalLevelData value) {
            meleeCriticalLevelDataList.add(value);
        }

        /**
         * Set the melee-critical:debuff-toh value
         *
         * @param value added to the to-hit value when the target is debuffed
         * @return false if this value has already been set (duplicate values are not allowed),
         * true otherwise
         * @author Rowan Crowther
         */
        @CheckReturnValue
        @Contract(mutates = "this")
        public boolean setMeleeCriticalPowerRandom(Integer value) {
            if (meleeCriticalPowerRandom != null) return false;
            meleeCriticalPowerRandom = value;
            return true;
        }

        /**
         * Set the melee-critical:power-weight-scale value
         *
         * @param value scale factor for the weapon weight in the power of a mêlée critical
         * @return false if this value has already been set (duplicate values are not allowed),
         * true otherwise
         * @author Rowan Crowther
         */
        @CheckReturnValue
        @Contract(mutates = "this")
        public boolean setMeleeCriticalPowerWeightScale(Integer value) {
            if (meleeCriticalPowerWeightScale != null) return false;
            meleeCriticalPowerWeightScale = value;
            return true;
        }

        /**
         * Set the melee-critical:chance-range value
         *
         * @param value maximum range for mêlée critical chance
         * @return false if this value has already been set (duplicate values are not allowed),
         * true otherwise
         * @author Rowan Crowther
         */
        @CheckReturnValue
        @Contract(mutates = "this")
        public boolean setMeleeCriticalChanceRange(Integer value) {
            if (meleeCriticalChanceRange != null) return false;
            meleeCriticalChanceRange = value;
            return true;
        }

        /**
         * Set the melee-critical:chance-offset value
         *
         * @param value value added to chance for calculating mêlée critical
         * @return false if this value has already been set (duplicate values are not allowed),
         * true otherwise
         * @author Rowan Crowther
         */
        @CheckReturnValue
        @Contract(mutates = "this")
        public boolean setMeleeCriticalChanceOffset(Integer value) {
            if (meleeCriticalChanceOffset != null) return false;
            meleeCriticalChanceOffset = value;
            return true;
        }

        /**
         * Set the melee-critical:chance-toh-skill-scale value
         *
         * @param value scale factor for the to-hit skill
         * @return false if this value has already been set (duplicate values are not allowed),
         * true otherwise
         * @author Rowan Crowther
         */
        @CheckReturnValue
        @Contract(mutates = "this")
        public boolean setMeleeCriticalChanceTohSkillScale(Integer value) {
            if (meleeCriticalChanceTohSkillScale != null) return false;
            meleeCriticalChanceTohSkillScale = value;
            return true;
        }

        /**
         * Set the melee-critical:chance-level-scale value
         *
         * @param value scale factor for player level
         * @return false if this value has already been set (duplicate values are not allowed),
         * true otherwise
         * @author Rowan Crowther
         */
        @CheckReturnValue
        @Contract(mutates = "this")
        public boolean setMeleeCriticalChanceLevelScale(Integer value) {
            if (meleeCriticalChanceLevelScale != null) return false;
            meleeCriticalChanceLevelScale = value;
            return true;
        }

        /**
         * Set the melee-critical:chance-toh-scale value
         *
         * @param value scale factor for overall to-hit
         * @return false if this value has already been set (duplicate values are not allowed),
         * true otherwise
         * @author Rowan Crowther
         */
        @CheckReturnValue
        @Contract(mutates = "this")
        public boolean setMeleeCriticalChanceTohScale(Integer value) {
            if (meleeCriticalChanceTohScale != null) return false;
            meleeCriticalChanceTohScale = value;
            return true;
        }

        /**
         * Set the melee-critical:chance-weight-scale value
         *
         * @param value Scale factor for the weapon's weight
         * @return false if this value has already been set (duplicate values are not allowed),
         * true otherwise
         * @author Rowan Crowther
         */
        @CheckReturnValue
        @Contract(mutates = "this")
        public boolean setMeleeCriticalChanceWeightScale(Integer value) {
            if (meleeCriticalChanceWeightScale != null) return false;
            meleeCriticalChanceWeightScale = value;
            return true;
        }

        /**
         * Set the melee-critical:debuff-toh value
         *
         * @param value added to the to-hit value when the target is debuffed
         * @return false if this value has already been set (duplicate values are not allowed),
         * true otherwise
         * @author Rowan Crowther
         */
        @CheckReturnValue
        @Contract(mutates = "this")
        public boolean setMeleeCriticalDebuffToh(Integer value) {
            if (meleeCriticalDebuffToh != null) return false;
            meleeCriticalDebuffToh = value;
            return true;
        }

        /**
         * Set the player:food-value value
         *
         * @param value the number of turns that 1% of the player food capacity feeds them for
         * @return false if this value has already been set (duplicate values are not allowed),
         * true otherwise
         * @author Rowan Crowther
         */
        @CheckReturnValue
        @Contract(mutates = "this")
        public boolean setPlayerFoodValue(Integer value) {
            if (playerFoodValue != null) return false;
            playerFoodValue = value;
            return true;
        }

        /**
         * Set the player:start-gold value
         *
         * @param value the starting gold of the player
         * @return false if this value has already been set (duplicate values are not allowed),
         * true otherwise
         * @author Rowan Crowther
         */
        @CheckReturnValue
        @Contract(mutates = "this")
        public boolean setPlayerStartGold(Integer value) {
            if (playerStartGold != null) return false;
            playerStartGold = value;
            return true;
        }

        /**
         * Set the player:max-range value
         *
         * @param value the maximum missile and spell range
         * @return false if this value has already been set (duplicate values are not allowed),
         * true otherwise
         * @author Rowan Crowther
         */
        @CheckReturnValue
        @Contract(mutates = "this")
        public boolean setPlayerMaxRange(Integer value) {
            if (playerMaxRange != null) return false;
            playerMaxRange = value;
            return true;
        }

        /**
         * Set the player:max-sight value
         *
         * @param value the maximum visual range of the player
         * @return false if this value has already been set (duplicate values are not allowed),
         * true otherwise
         * @author Rowan Crowther
         */
        @CheckReturnValue
        @Contract(mutates = "this")
        public boolean setPlayerMaxSight(Integer value) {
            if (playerMaxSight != null) return false;
            playerMaxSight = value;
            return true;
        }

        /**
         * Set the obj-make:default-lamp value
         *
         * @param value normal amount of fuel in a lamp
         * @return false if this value has already been set (duplicate values are not allowed),
         * true otherwise
         * @author Rowan Crowther
         */
        @CheckReturnValue
        @Contract(mutates = "this")
        public boolean setObjDefaultLamp(Integer value) {
            if (objMakeDefaultLamp != null) return false;
            objMakeDefaultLamp = value;
            return true;
        }

        /**
         * Set the obj-make:fuel-lamp value
         *
         * @param value maximum amount of fuel in a lantern
         * @return false if this value has already been set (duplicate values are not allowed),
         * true otherwise
         * @author Rowan Crowther
         */
        @CheckReturnValue
        @Contract(mutates = "this")
        public boolean setObjFuelLamp(Integer value) {
            if (objMakeFuelLamp != null) return false;
            objMakeFuelLamp = value;
            return true;
        }

        /**
         * Set the obj-make:fuel-torch value
         *
         * @param value maximum amount of fuel in a torch
         * @return false if this value has already been set (duplicate values are not allowed),
         * true otherwise
         * @author Rowan Crowther
         */
        @CheckReturnValue
        @Contract(mutates = "this")
        public boolean setObjFuelTorch(Integer value) {
            if (objMakeFuelTorch != null) return false;
            objMakeFuelTorch = value;
            return true;
        }

        /**
         * Set the obj-make:great-ego value
         *
         * @param value 1/chance of inflating the requested ego item level
         * @return false if this value has already been set (duplicate values are not allowed),
         * true otherwise
         * @author Rowan Crowther
         */
        @CheckReturnValue
        @Contract(mutates = "this")
        public boolean setObjGreatEgo(Integer value) {
            if (objMakeGreatEgo != null) return false;
            objMakeGreatEgo = value;
            return true;
        }

        /**
         * Set the obj-make:great-obj value
         *
         * @param value 1/chance of inflating the requested object's level
         * @return false if this value has already been set (duplicate values are not allowed),
         * true otherwise
         * @author Rowan Crowther
         */
        @CheckReturnValue
        @Contract(mutates = "this")
        public boolean setObjGreatObj(Integer value) {
            if (objMakeGreatObj != null) return false;
            objMakeGreatObj = value;
            return true;
        }

        /**
         * Set the obj-make:max-depth value
         *
         * @param value maximum depth to be used in object allocation
         * @return false if this value has already been set (duplicate values are not allowed),
         * true otherwise
         * @author Rowan Crowther
         */
        @CheckReturnValue
        @Contract(mutates = "this")
        public boolean setObjMakeMaxDepth(Integer value) {
            if (objMakeMaxDepth != null) return false;
            objMakeMaxDepth = value;
            return true;
        }

        /**
         * Set the store:magic-level value
         *
         * @param value the level to apply magic to objects for normal stores
         * @return false if this value has already been set (duplicate values are not allowed),
         * true otherwise
         * @author Rowan Crowther
         */
        @CheckReturnValue
        @Contract(mutates = "this")
        public boolean setStoreMagicLevel(Integer value) {
            if (storeMagicLevel != null) return false;
            storeMagicLevel = value;
            return true;
        }

        /**
         * Set the store:shuffle value
         *
         * @param value the 1/chance per day of a shop owner changing
         * @return false if this value has already been set (duplicate values are not allowed),
         * true otherwise
         * @author Rowan Crowther
         */
        @CheckReturnValue
        @Contract(mutates = "this")
        public boolean setStoreShuffle(Integer value) {
            if (storeShuffle != null) return false;
            storeShuffle = value;
            return true;
        }

        /**
         * Set the store:turns value
         *
         * @param value the number of turns between turnovers
         * @return false if this value has already been set (duplicate values are not allowed),
         * true otherwise
         * @author Rowan Crowther
         */
        @CheckReturnValue
        @Contract(mutates = "this")
        public boolean setStoreTurns(Integer value) {
            if (storeTurns != null) return false;
            storeTurns = value;
            return true;
        }

        /**
         * Set the store:inven-max value
         *
         * @param value the maximum number of items per shop
         * @return false if this value has already been set (duplicate values are not allowed),
         * true otherwise
         * @author Rowan Crowther
         */
        @CheckReturnValue
        @Contract(mutates = "this")
        public boolean setStoreInvenMax(Integer value) {
            if (storeInvenMax != null) return false;
            storeInvenMax = value;
            return true;
        }

        /**
         * Set the carry-cap:floor-size value
         *
         * @param value the maximum number of items per square
         * @return false if this value has already been set (duplicate values are not allowed),
         * true otherwise
         * @author Rowan Crowther
         */
        @CheckReturnValue
        @Contract(mutates = "this")
        public boolean setCarryCapFloorSize(Integer value) {
            if (carryCapFloorSize != null) return false;
            carryCapFloorSize = value;
            return true;
        }

        /**
         * Set the carry-cap:thrown-quiver-mult value
         *
         * @param value the multiplier for non-ammo thrown items
         * @return false if this value has already been set (duplicate values are not allowed),
         * true otherwise
         * @author Rowan Crowther
         */
        @CheckReturnValue
        @Contract(mutates = "this")
        public boolean setCarryCapThrownQuiverMult(Integer value) {
            if (carryCapThrownQuiverMult != null) return false;
            carryCapThrownQuiverMult = value;
            return true;
        }

        /**
         * Set the carry-cap:quiver-slot-size value
         *
         * @param value the number of missiles per quiver slot
         * @return false if this value has already been set (duplicate values are not allowed),
         * true otherwise
         * @author Rowan Crowther
         */
        @CheckReturnValue
        @Contract(mutates = "this")
        public boolean setCarryCapQuiverSlotSize(Integer value) {
            if (carryCapQuiverSlotSize != null) return false;
            carryCapQuiverSlotSize = value;
            return true;
        }

        /**
         * Set the carry-cap:quiver-size value
         *
         * @param value the number of quiver slots
         * @return false if this value has already been set (duplicate values are not allowed),
         * true otherwise
         * @author Rowan Crowther
         */
        @CheckReturnValue
        @Contract(mutates = "this")
        public boolean setCarryCapQuiverSize(Integer value) {
            if (carryCapQuiverSize != null) return false;
            carryCapQuiverSize = value;
            return true;
        }

        /**
         * Set the carry-cap:pack-size value
         *
         * @param value the number of item slots in the inventory
         * @return false if this value has already been set (duplicate values are not allowed),
         * true otherwise
         * @author Rowan Crowther
         */
        @CheckReturnValue
        @Contract(mutates = "this")
        public boolean setCarryCapPackSize(Integer value) {
            if (carryCapPackSize != null) return false;
            carryCapPackSize = value;
            return true;
        }

        /**
         * Set the world:move-energy value
         *
         * @param value the amount of energy for a monster or player to move
         * @return false if this value has already been set (duplicate values are not allowed),
         * true otherwise
         * @author Rowan Crowther
         */
        @CheckReturnValue
        @Contract(mutates = "this")
        public boolean setWorldMoveEnergy(Integer value) {
            if (this.worldMoveEnergy != null) return false;
            this.worldMoveEnergy = value;
            return true;
        }

        /**
         * Set the world:stair-skip value
         *
         * @param value the number of levels for each stair
         * @return false if this value has already been set (duplicate values are not allowed),
         * true otherwise
         * @author Rowan Crowther
         */
        @CheckReturnValue
        @Contract(mutates = "this")
        public boolean setWorldStairSkip(Integer value) {
            if (worldStairSkip != null) return false;
            worldStairSkip = value;
            return true;
        }

        /**
         * Set the world:feeling-need value
         *
         * @param value the minimum number of squares to vidit to get first feeling
         * @return false if this value has already been set (duplicate values are not allowed),
         * true otherwise
         * @author Rowan Crowther
         */
        @CheckReturnValue
        @Contract(mutates = "this")
        public boolean setWorldFeelingNeed(Integer value) {
            if (worldFeelingNeed != null) return false;
            worldFeelingNeed = value;
            return true;
        }

        /**
         * Set the world:feeling-total value
         *
         * @param value the number of feeling squares per level
         * @return false if this value has already been set (duplicate values are not allowed),
         * true otherwise
         * @author Rowan Crowther
         */
        @CheckReturnValue
        @Contract(mutates = "this")
        public boolean setWorldFeelingTotal(Integer value) {
            if (worldFeelingTotal != null) return false;
            worldFeelingTotal = value;
            return true;
        }

        /**
         * Set the world:town-wid value
         *
         * @param value the maximum number of horizontal grids in a town level
         * @return false if this value has already been set (duplicate values are not allowed),
         * true otherwise
         * @author Rowan Crowther
         */
        @CheckReturnValue
        @Contract(mutates = "this")
        public boolean setWorldTownWid(Integer value) {
            if (worldTownWid != null) return false;
            worldTownWid = value;
            return true;
        }

        /**
         * Set the world:town-hgt value
         *
         * @param value the maximum number of vertical grids in a town level
         * @return false if this value has already been set (duplicate values are not allowed),
         * true otherwise
         * @author Rowan Crowther
         */
        @CheckReturnValue
        @Contract(mutates = "this")
        public boolean setWorldTownHgt(Integer value) {
            if (worldTownHgt != null) return false;
            worldTownHgt = value;
            return true;
        }

        /**
         * Set the world:dungeon-wid value
         *
         * @param value the maximum number of horizontal grids in a dungeon level
         * @return false if this value has already been set (duplicate values are not allowed),
         * true otherwise
         * @author Rowan Crowther
         */
        @CheckReturnValue
        @Contract(mutates = "this")
        public boolean setWorldDungeonWid(Integer value) {
            if (worldDungeonWid != null) return false;
            worldDungeonWid = value;
            return true;
        }

        /**
         * Set the world:dungeon-hgt value
         *
         * @param value the maximum number of vertical grids in a dungeon level
         * @return false if this value has already been set (duplicate values are not allowed),
         * true otherwise
         * @author Rowan Crowther
         */
        @CheckReturnValue
        @Contract(mutates = "this")
        public boolean setWorldDungeonHgt(Integer value) {
            if (worldDungeonHgt != null) return false;
            worldDungeonHgt = value;
            return true;
        }

        /**
         * Set the world:day-length value
         *
         * @param value the number of turns from dawn to dawn
         * @return false if this value has already been set (duplicate values are not allowed),
         * true otherwise
         * @author Rowan Crowther
         */
        @CheckReturnValue
        @Contract(mutates = "this")
        public boolean setWorldDayLength(Integer value) {
            if (worldDayLength != null) return false;
            worldDayLength = value;
            return true;
        }

        /**
         * Set the world:max-depth value
         *
         * @param value the maximum dungeon level
         * @return false if this value has already been set (duplicate values are not allowed),
         * true otherwise
         * @author Rowan Crowther
         */
        @CheckReturnValue
        @Contract(mutates = "this")
        public boolean setWorldMaxDepth(Integer value) {
            if (worldMaxDepth != null) return false;
            worldMaxDepth = value;
            return true;
        }

        /**
         * Set the dun-gen:pit-max value
         *
         * @param value the maximum number of pits or nests allowed on a level
         * @return false if this value has already been set (duplicate values are not allowed),
         * true otherwise
         * @author Rowan Crowther
         */
        @CheckReturnValue
        @Contract(mutates = "this")
        public boolean setDunGenPitMax(Integer value) {
            if (dunGenPitMax != null) return false;
            dunGenPitMax = value;
            return true;
        }

        /**
         * Set the dun-gen:amt-gold value
         *
         * @param value the average number of treasure to place in rooms/corridors
         * @return false if this value has already been set (duplicate values are not allowed),
         * true otherwise
         * @author Rowan Crowther
         */
        @CheckReturnValue
        @Contract(mutates = "this")
        public boolean setDunGenAmtGold(Integer value) {
            if (dunGenAmtGold != null) return false;
            dunGenAmtGold = value;
            return true;
        }

        /**
         * Set the dun-gen:amt-item value
         *
         * @param value the average number of items to place in rooms/corridors
         * @return false if this value has already been set (duplicate values are not allowed),
         * true otherwise
         * @author Rowan Crowther
         */
        @CheckReturnValue
        @Contract(mutates = "this")
        public boolean setDunGenAmtItem(Integer value) {
            if (dunGenAmtItem != null) return false;
            dunGenAmtItem = value;
            return true;
        }

        /**
         * Set the dun-gen:amt-room value
         *
         * @param value the average number of objects to place in rooms
         * @return false if this value has already been set (duplicate values are not allowed),
         * true otherwise
         * @author Rowan Crowther
         */
        @CheckReturnValue
        @Contract(mutates = "this")
        public boolean setDunGenAmtRoom(Integer value) {
            if (dunGenAmtRoom != null) return false;
            dunGenAmtRoom = value;
            return true;
        }

        /**
         * Set the dun-gen:tunn-max value
         *
         * @param value the maximum number of tunnel grids
         * @return false if this value has already been set (duplicate values are not allowed),
         * true otherwise
         * @author Rowan Crowther
         */
        @CheckReturnValue
        @Contract(mutates = "this")
        public boolean setDunGenTunnMax(Integer value) {
            if (dunGenTunnMax != null) return false;
            dunGenTunnMax = value;
            return true;
        }


        /**
         * Set the dun-gen:wall-max value
         *
         * @param value the maximum number of places to potentially pierce room walls with tunnels on a level
         * @return false if this value has already been set (duplicate values are not allowed),
         * true otherwise
         * @author Rowan Crowther
         */
        @CheckReturnValue
        @Contract(mutates = "this")
        public boolean setDunGenWallMax(Integer value) {
            if (dunGenWallMax != null) return false;
            dunGenWallMax = value;
            return true;
        }

        /**
         * Set the dun-gen:door-max value
         *
         * @param value the maximum number of potential door locations on a level
         * @return false if this value has already been set (duplicate values are not allowed),
         * true otherwise
         * @author Rowan Crowther
         */
        @CheckReturnValue
        @Contract(mutates = "this")
        public boolean setDunGenDoorMax(Integer value) {
            if (dunGenDoorMax != null) return false;
            dunGenDoorMax = value;
            return true;
        }

        /**
         * Set the dun-gen:cent-max value
         *
         * @param value the maximum number of room centres on a level
         * @return false if this value has already been set (duplicate values are not allowed),
         * true otherwise
         * @author Rowan Crowther
         */
        @CheckReturnValue
        @Contract(mutates = "this")
        public boolean setDunGenCentMax(Integer value) {
            if (dunGenCentMax != null) return false;
            dunGenCentMax = value;
            return true;
        }

        /**
         * Set the mon-play:turn-range value
         *
         * @param value how close a slow scared monster must be to turn and fight
         * @return false if this value has already been set (duplicate values are not allowed),
         * true otherwise
         * @author Rowan Crowther
         */
        @CheckReturnValue
        @Contract(mutates = "this")
        public boolean setMonPlayTurnRange(Integer value) {
            if (monPlayTurnRange != null) return false;
            monPlayTurnRange = value;
            return true;
        }

        /**
         * Set the mon-play:flee-range value
         *
         * @param value number of grids out of sight a monster will flee
         * @return false if this value has already been set (duplicate values are not allowed),
         * true otherwise
         * @author Rowan Crowther
         */
        @CheckReturnValue
        @Contract(mutates = "this")
        public boolean setMonPlayFleeRange(Integer value) {
            if (monPlayFleeRange != null) return false;
            monPlayFleeRange = value;
            return true;
        }

        /**
         * Set the mon-play:life-drain value
         *
         * @param value the % of player exp drained per hit
         * @return false if this value has already been set (duplicate values are not allowed),
         * true otherwise
         * @author Rowan Crowther
         */
        @CheckReturnValue
        @Contract(mutates = "this")
        public boolean setMonPlayLifeDrain(Integer value) {
            if (monPlayLifeDrain != null) return false;
            monPlayLifeDrain = value;
            return true;
        }

        /**
         * Set the mon-play:mult-rate value
         *
         * @param value a value inversely related to the speed of monster multiplication
         * @return false if this value has already been set (duplicate values are not allowed),
         * true otherwise
         * @author Rowan Crowther
         */
        @CheckReturnValue
        @Contract(mutates = "this")
        public boolean setMonPlayMultRate(Integer value) {
            if (monPlayMultRate != null) return false;
            monPlayMultRate = value;
            return true;
        }

        /**
         * Set the mon-play:break-glyph
         *
         * @param value the rune of protection's resistance to monster breaking
         * @return false if this value has already been set (duplicate values are not allowed),
         * true otherwise
         * @author Rowan Crowther
         */
        @CheckReturnValue
        @Contract(mutates = "this")
        public boolean setMonPlayBreakGlyph(Integer value) {
            if (monPlayBreakGlyph != null) return false;
            monPlayBreakGlyph = value;
            return true;
        }

        /**
         * Set the mon-gen:group-dist value
         *
         * @param value the maximum distance of a group of monsters from a related group
         * @return false if this value has already been set (duplicate values are not allowed),
         * true otherwise
         * @author Rowan Crowther
         */
        @CheckReturnValue
        @Contract(mutates = "this")
        public boolean setMonGenGroupDist(Integer value) {
            if (monGenGroupDist != null) return false;
            monGenGroupDist = value;
            return true;
        }

        /**
         * Set the mon-gen:group-max value
         *
         * @param value the maximum number of monsters in a group
         * @return false if this value has already been set (duplicate values are not allowed),
         * true otherwise
         * @author Rowan Crowther
         */
        @CheckReturnValue
        @Contract(mutates = "this")
        public boolean setMonGenGroupMax(Integer value) {
            if (monGenGroupMax != null) return false;
            monGenGroupMax = value;
            return true;
        }

        /**
         * Set the mon-gen:ood-amount value
         *
         * @param value the maximum out of depth amount for a monster spawn
         * @return false if this value has already been set (duplicate values are not allowed),
         * true otherwise
         * @author Rowan Crowther
         */
        @CheckReturnValue
        @Contract(mutates = "this")
        public boolean setMonGenOodAmount(Integer value) {
            if (monGenOodAmount != null) return false;
            monGenOodAmount = value;
            return true;
        }

        /**
         * Set the mon-gen:ood-chance value
         *
         * @param value the chance of a generated monster's level being inflated
         * @return false if this value has already been set (duplicate values are not allowed),
         * true otherwise
         * @author Rowan Crowther
         */
        @CheckReturnValue
        @Contract(mutates = "this")
        public boolean setMonGenOodChance(Integer value) {
            if (monGenOodChance != null) return false;
            monGenOodChance = value;
            return true;
        }

        /**
         * Set the mon-gen:repro-max value
         *
         * @param value the maximum number of breeding monsters allowed on a level
         * @return false if this value has already been set (duplicate values are not allowed),
         * true otherwise
         * @author Rowan Crowther
         */
        @CheckReturnValue
        @Contract(mutates = "this")
        public boolean setMonGenReproMax(Integer value) {
            if (monGenReproMax != null) return false;
            monGenReproMax = value;
            return true;
        }

        /**
         * Set the mon-gen:town-night value
         *
         * @param value the value of the number of townsfolk spawned during the night
         * @return false if this value has already been set (duplicate values are not allowed),
         * true otherwise
         * @author Rowan Crowther
         */
        @CheckReturnValue
        @Contract(mutates = "this")
        public boolean setMonGenTownNight(Integer value) {
            if (monGenTownNight != null) return false;
            monGenTownNight = value;
            return true;
        }

        /**
         * Set the mon-gen:town-day value
         *
         * @param value the value of the number of townsfolk spawned during the day
         * @return false if this value has already been set (duplicate values are not allowed),
         * true otherwise
         * @author Rowan Crowther
         */
        @CheckReturnValue
        @Contract(mutates = "this")
        public boolean setMonGenTownDay(Integer value) {
            if (monGenTownDay != null) return false;
            monGenTownDay = value;
            return true;
        }

        /**
         * Set the mon-gen:level-min value
         *
         * @param value the value of the minimum number of monsters spawned per level
         * @return false if this value has already been set (duplicate values are not allowed),
         * true otherwise
         * @author Rowan Crowther
         */
        @CheckReturnValue
        @Contract(mutates = "this")
        public boolean setMonGenLevelMin(Integer value) {
            if (monGenLevelMin != null) return false;
            monGenLevelMin = value;
            return true;
        }

        /**
         * Set the level-max:monsters value
         *
         * @param value the value of max monsters on a given level from constants.txt
         * @return false if this value has already been set (duplicate values are not allowed),
         * true otherwise
         * @author Rowan Crowther
         */
        @CheckReturnValue
        @Contract(mutates = "this")
        public boolean setLevelMaxMonsters(Integer value) {
            if (levelMaxMonsters != null) return false;
            levelMaxMonsters = value;
            return true;
        }

        /**
         * Sets the mon-gen:chance value
         *
         * @param value The 1/turn chance of new monster generation
         * @return false if this value has already been set (duplicate values are not allowed),
         * true otherwise
         * @author Rowan Crowther
         */
        @CheckReturnValue
        @Contract(mutates = "this")
        public boolean setMonGenChance(Integer value) {
            if (monGenChance != null) return false;
            monGenChance = value;
            return true;
        }

        /**
         * Check that all the constants have been read in (their validity will be checked in
         * GameConstantsReader) and record an error if one is missing. Otherwise return the
         * GameConstantsData record fully filled in.
         *
         * @param errors The list of errors at this point in time
         * @return The GameConstantsData record, fully filled with its values, or null if
         * an error occurred
         * @author Rowan Crowther
         */
        @Nullable
        @CheckReturnValue
        public GameConstantsData build(@NotNull List<String> errors) {
            // Level maxima
            if (levelMaxMonsters == null) {
                errors.add("Missing required constant level-max:monsters");
            }

            // Monster generation
            if (monGenChance == null) {
                errors.add("Missing required constant mon-gen:chance");
            }
            if (monGenLevelMin == null) {
                errors.add("Missing required constant mon-gen:level-min");
            }
            if (monGenTownDay == null) {
                errors.add("Missing required constant mon-gen:town-day");
            }
            if (monGenTownNight == null) {
                errors.add("Missing required constant mon-gen:town-night");
            }
            if (monGenReproMax == null) {
                errors.add("Missing required constant mon-gen:repro-max");
            }
            if (monGenOodChance == null) {
                errors.add("Missing required constant mon-gen:ood-chance");
            }
            if (monGenOodAmount == null) {
                errors.add("Missing required constant mon-gen:ood-amount");
            }
            if (monGenGroupMax == null) {
                errors.add("Missing required constant mon-gen:group-max");
            }
            if (monGenGroupDist == null) {
                errors.add("Missing required constant mon-gen:group-dist");
            }

            // Monster Gameplay
            if (monPlayBreakGlyph == null) {
                errors.add("Missing required constant mon-play:break-glyph");
            }
            if (monPlayMultRate == null) {
                errors.add("Missing required constant mon-play:mult-rate");
            }
            if (monPlayLifeDrain == null) {
                errors.add("Missing required constant mon-play:life-drain");
            }
            if (monPlayFleeRange == null) {
                errors.add("Missing required constant mon-play:flee-range");
            }
            if (monPlayTurnRange == null) {
                errors.add("Missing required constant mon-play:turn-range");
            }

            // Dungeon Generation
            if (dunGenCentMax == null) {
                errors.add("Missing required constant dun-gen:cent-max");
            }
            if (dunGenDoorMax == null) {
                errors.add("Missing required constant dun-gen:door-max");
            }
            if (dunGenWallMax == null) {
                errors.add("Missing required constant dun-gen:wall-max");
            }
            if (dunGenTunnMax == null) {
                errors.add("Missing required constant dun-gen:tunn-max");
            }
            if (dunGenAmtRoom == null) {
                errors.add("Missing required constant dun-gen:amt-room");
            }
            if (dunGenAmtItem == null) {
                errors.add("Missing required constant dun-gen:amt-item");
            }
            if (dunGenAmtGold == null) {
                errors.add("Missing required constant dun-gen:amt-gold");
            }
            if (dunGenPitMax == null) {
                errors.add("Missing required constant dun-gen:pit-max");
            }

            // Game World
            if (worldMaxDepth == null) {
                errors.add("Missing required constant world:max-depth");
            }
            if (worldDayLength == null) {
                errors.add("Missing required constant world:day-length");
            }
            if (worldDungeonHgt == null) {
                errors.add("Missing required constant world:dungeon-hgt");
            }
            if (worldDungeonWid == null) {
                errors.add("Missing required constant world:dungeon-wid");
            }
            if (worldTownHgt == null) {
                errors.add("Missing required constant world:town-hgt");
            }
            if (worldTownWid == null) {
                errors.add("Missing required constant world:town-wid");
            }
            if (worldFeelingTotal == null) {
                errors.add("Missing required constant world:feeling-total");
            }
            if (worldFeelingNeed == null) {
                errors.add("Missing required constant world:feeling-need");
            }
            if (worldStairSkip == null) {
                errors.add("Missing required constant world:stair-skip");
            }
            if (worldMoveEnergy == null) {
                errors.add("Missing required constant world:move-energy");
            }

            // Carry Capacity
            if (carryCapPackSize == null) {
                errors.add("Missing required constant carry-cap:pack-size");
            }
            if (carryCapQuiverSize == null) {
                errors.add("Missing required constant carry-cap:quiver-size");
            }
            if (carryCapQuiverSlotSize == null) {
                errors.add("Missing required constant carry-cap:quiver-slot-size");
            }
            if (carryCapThrownQuiverMult == null) {
                errors.add("Missing required constant carry-cap:thrown-quiver-mult");
            }
            if (carryCapFloorSize == null) {
                errors.add("Missing required constant carry-cap:floor-size");
            }

            // Store Parameters
            if (storeInvenMax == null) {
                errors.add("Missing required constant store:inven-max");
            }
            if (storeTurns == null) {
                errors.add("Missing required constant store:turns");
            }
            if (storeShuffle == null) {
                errors.add("Missing required constant store:shuffle");
            }
            if (storeMagicLevel == null) {
                errors.add("Missing required constant store:magic-level");
            }

            // Object Generation
            if (objMakeMaxDepth == null) {
                errors.add("Missing required constant obj-make:max-depth");
            }
            if (objMakeGreatObj == null) {
                errors.add("Missing required constant obj-make:great-obj");
            }
            if (objMakeGreatEgo == null) {
                errors.add("Missing required constant obj-make:great-ego");
            }
            if (objMakeFuelTorch == null) {
                errors.add("Missing required constant obj-make:fuel-torch");
            }
            if (objMakeFuelLamp == null) {
                errors.add("Missing required constant obj-make:fuel-lamp");
            }
            if (objMakeDefaultLamp == null) {
                errors.add("Missing required constant obj-make:default-lamp");
            }

            // Player Constants
            if (playerMaxSight == null) {
                errors.add("Missing required constant player:max-sight");
            }
            if (playerMaxRange == null) {
                errors.add("Missing required constant player:max-range");
            }
            if (playerStartGold == null) {
                errors.add("Missing required constant player:start-gold");
            }
            if (playerFoodValue == null) {
                errors.add("Missing required constant player:food-value");
            }

            // Non-O critical melee calculations
            if (meleeCriticalDebuffToh == null) {
                errors.add("Missing required constant melee-critical:debuff-toh");
            }
            if (meleeCriticalChanceWeightScale == null) {
                errors.add("Missing required constant melee-critical:chance-weight-scale");
            }
            if (meleeCriticalChanceTohScale == null) {
                errors.add("Missing required constant melee-critical:chance-toh-scale");
            }
            if (meleeCriticalChanceLevelScale == null) {
                errors.add("Missing required constant melee-critical:chance-level-scale");
            }
            if (meleeCriticalChanceTohSkillScale == null) {
                errors.add("Missing required constant melee-critical:chance-toh-skill-scale");
            }
            if (meleeCriticalChanceOffset == null) {
                errors.add("Missing required constant melee-critical:chance-offset");
            }
            if (meleeCriticalChanceRange == null) {
                errors.add("Missing required constant melee-critical:chance-range");
            }
            if (meleeCriticalPowerWeightScale == null) {
                errors.add("Missing required constant melee-critical:power-weight-scale");
            }
            if (meleeCriticalPowerRandom == null) {
                errors.add("Missing required constant melee-critical:power-random");
            }

            // Non-O critical ranged calculations
            if (rangedCriticalDebuffToh == null) {
                errors.add("Missing required constant ranged-critical:debuff-toh");
            }
            if (rangedCriticalChanceWeightScale == null) {
                errors.add("Missing required constant ranged-critical:chance-weight-scale");
            }
            if (rangedCriticalChanceTohScale == null) {
                errors.add("Missing required constant ranged-critical:chance-toh-scale");
            }
            if (rangedCriticalChanceLevelScale == null) {
                errors.add("Missing required constant ranged-critical:chance-level-scale");
            }
            if (rangedCriticalChanceLaunchedTohSkillScale == null) {
                errors.add("Missing required constant ranged-critical:chance-launched-toh-skill-scale");
            }
            if (rangedCriticalChanceThrownTohSkillScale == null) {
                errors.add("Missing required constant ranged-critical:chance-thrown-toh-skill-scale");
            }
            if (rangedCriticalChanceOffset == null) {
                errors.add("Missing required constant ranged-critical:chance-offset");
            }
            if (rangedCriticalChanceRange == null) {
                errors.add("Missing required constant ranged-critical:chance-range");
            }
            if (rangedCriticalPowerWeightScale == null) {
                errors.add("Missing required constant ranged-critical:power-weight-scale");
            }
            if (rangedCriticalPowerRandom == null) {
                errors.add("Missing required constant ranged-critical:power-random");
            }

            // O Critical Calculations
            if (oMeleeCriticalDebuffToh == null) {
                errors.add("Missing required constant o-melee-critical:debuff-toh");
            }
            if (oMeleeCriticalPowerTohScaleNumerator == null) {
                errors.add("Missing required constant o-melee-critical:power-toh-scale-numerator");
            }
            if (oMeleeCriticalPowerTohScaleDenominator == null) {
                errors.add("Missing required constant o-melee-critical:power-toh-scale-denominator");
            }
            if (oMeleeCriticalChancePowerScaleNumerator == null) {
                errors.add("Missing required constant o-melee-critical:chance-power-scale-numerator");
            }
            if (oMeleeCriticalChancePowerScaleDenominator == null) {
                errors.add("Missing required constant o-melee-critical:chance-power-scale-denominator");
            }
            if (oMeleeCriticalChanceAddDenominator == null) {
                errors.add("Missing required constant o-melee-critical:chance-add-denominator");
            }

            // o-ranged criticals
            if (oRangedCriticalDebuffToh == null) {
                errors.add("Missing required constant o-ranged-critical:debuff-toh");
            }
            if (oRangedCriticalPowerLaunchedTohScaleNumerator == null) {
                errors.add("Missing required constant o-ranged-critical:power-launched-toh-scale-numerator");
            }
            if (oRangedCriticalPowerLaunchedTohScaleDenominator == null) {
                errors.add("Missing required constant o-ranged-critical:power-launched-toh-scale-denominator");
            }
            if (oRangedCriticalPowerThrownTohScaleNumerator == null) {
                errors.add("Missing required constant o-ranged-critical:power-thrown-toh-scale-numerator");
            }
            if (oRangedCriticalPowerThrownTohScaleDenominator == null) {
                errors.add("Missing required constant o-ranged-critical:power-thrown-toh-scale-denominator");
            }
            if (oRangedCriticalChancePowerScaleNumerator == null) {
                errors.add("Missing required constant o-ranged-critical:chance-power-scale-numerator");
            }
            if (oRangedCriticalChancePowerScaleDenominator == null) {
                errors.add("Missing required constant o-ranged-critical:chance-power-scale-denominator");
            }
            if (oRangedCriticalChanceAddDenominator == null) {
                errors.add("Missing required constant o-ranged-critical:chance-add-denominator");
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