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

package uk.co.jackoftradesltd.backend.parser.gameconstants;

import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.co.jackoftradesltd.channel.parser.Assembler;
import uk.co.jackoftradesltd.middle.enums.MessageType;
import uk.co.jackoftradesltd.middle.game.globals.data.*;

import java.util.List;

/**
 * 'Assembles' the final data from the list of GameConstantsParseRecords, and returns a
 * GameConstantsData record to pass back to the calling GameConstants class.
 *
 * @author Rowan Crowther
 */
public class GameConstantsAssembler implements Assembler<GameConstantsParseRecord, GameConstantsData> {
    /**
     * Assembles the data from the list of records using a
     * {@link GameConstantsData.GameConstantsBuilder}, returning a {@link GameConstantsData}
     * or a null value if an error occurred.
     *
     * <p>Function assemble commented in full before 260915, provenance stamp added on 260915.
     *
     * @param records The list of records to create the {@link GameConstantsData} from
     * @param errors  A list of errors which is returned to the builder
     * @return the {@link GameConstantsData} containing the data - note, this can be null
     */
    @Override
    @Nullable
    @CheckReturnValue
    public GameConstantsData assemble(@NotNull List<GameConstantsParseRecord> records,
                                      @NotNull List<String> errors) {
        GameConstantsData.GameConstantsBuilder b = new GameConstantsData.GameConstantsBuilder();
        for (GameConstantsParseRecord record : records) {
            dispatch(record, b, errors);
        }
        String result = b.checkCriticalLevelDataLists();
        if (!result.isEmpty()) {
            errors.add(result);
        }
            
        return b.build(errors);
    }

    /**
     * Dispatch a record to the builder passing through the current list of errors
     *
     * <p>Function dispatch commented in full before 260915, provenance stamp added on 260915.
     *
     * @param rec    The record from the constants.txt file
     * @param b      The builder to build the records from
     * @param errors The list of current errors
     */
    private void dispatch(@NotNull GameConstantsParseRecord rec, @NotNull GameConstantsData.GameConstantsBuilder b,
                          @NotNull List<String> errors) {
        switch (rec.getCategory()) {
            case "level-max" -> levelMax(rec, b, errors);
            case "mon-gen" -> monGen(rec, b, errors);
            case "mon-play" -> monPlay(rec, b, errors);
            case "dun-gen" -> dunGen(rec, b, errors);
            case "world" -> world(rec, b, errors);
            case "carry-cap" -> carryCap(rec, b, errors);
            case "store" -> store(rec, b, errors);
            case "obj-make" -> objGen(rec, b, errors);
            case "player" -> player(rec, b, errors);
            case "melee-critical" -> meleeCritical(rec, b, errors);
            case "melee-critical-level" -> meleeCriticalLevel(rec, b, errors);
            case "ranged-critical" -> rangedCritical(rec, b, errors);
            case "ranged-critical-level" -> rangedCriticalLevel(rec, b, errors);
            case "o-melee-critical" -> oMeleeCritical(rec, b, errors);
            case "o-melee-critical-level" -> oMeleeCriticalLevel(rec, b, errors);
            case "o-ranged-critical" -> oRangedCritical(rec, b, errors);
            case "o-ranged-critical-level" -> oRangedCriticalLevel(rec, b, errors);
            default -> errors.add("Line: " + rec.getLineNumber() + " unknown category");
        }
    }

    /**
     * Parse a record from a list of 'o-ranged-critical-level' into the builder
     *
     * <p>The final value of the rec fields is resolved to a MessageTypeEnum here
     *
     * <p>Function oRangedCriticalLevel commented in full before 260915, provenance stamp added on
     * 260915.
     *
     * @param rec    The entry from the list
     * @param b      The builder responsible for building the game data
     * @param errors The list of current errors
     */
    private void oRangedCriticalLevel(@NotNull GameConstantsParseRecord rec, @NotNull GameConstantsData.GameConstantsBuilder b,
                                      @NotNull List<String> errors) {
        List<String> f = rec.getFields();
        int line = rec.getLineNumber();

        if (f.size() != 3) {
            errors.add("Line: " + line + ": o-ranged-critical-level expects value:value:value, got " +
                    f.size() + " fields");
            return;
        }

        Integer chance = coerceInt(f.get(0), line, errors);
        Integer dice = coerceInt(f.get(1), line, errors);
        if (chance == null || dice == null)
            return;

        String messageType = f.get(2);
        MessageType messageTypeEnum;

        try {
            messageTypeEnum = MessageType.valueOf("MSG_" + messageType);
        } catch (IllegalArgumentException e) {
            errors.add("Line: " + line + ": unknown message type: " + messageType);
            return;
        }

        ORangedCriticalLevelData record = new ORangedCriticalLevelData(chance, dice, messageTypeEnum);
        String result = b.addORangedCriticalLevel(record);
        if (!result.isEmpty()) {
            errors.add(result);
        }
    }

    /**
     * Parse an entry from a list of 'o-ranged-critical' into the builder
     *
     * <p>Function oRangedCritical commented in full before 260915, provenance stamp added on
     * 260915.
     *
     * @param rec    The entry from the list
     * @param b      The builder responsible for building the game data
     * @param errors The list of current errors
     */
    private void oRangedCritical(@NotNull GameConstantsParseRecord rec, @NotNull GameConstantsData.GameConstantsBuilder b,
                                 @NotNull List<String> errors) {
        List<String> f = rec.getFields();
        int line = rec.getLineNumber();

        if (f.size() != 2) {
            errors.add("Line: " + line + " o-ranged-critical expects label:value, got " +
                    f.size() + " fields");
            return;
        }

        String label = f.get(0);
        Integer value = coerceInt(f.get(1), line, errors);
        if (value == null) return;

        switch (label) {
            case "debuff-toh" -> {
                b.setORangedCriticalDebuffToh(value);
            }
            case "power-launched-toh-scale-numerator" -> {
                b.setORangedCriticalPowerLaunchedTohScaleNumerator(value);
            }
            case "power-launched-toh-scale-denominator" -> {
                b.setORangedCriticalPowerLaunchedTohScaleDenominator(value);
            }
            case "power-thrown-toh-scale-numerator" -> {
                b.setORangedCriticalPowerThrownTohScaleNumerator(value);
            }
            case "power-thrown-toh-scale-denominator" -> {
                b.setORangedCriticalPowerThrownTohScaleDenominator(value);
            }
            case "chance-power-scale-numerator" -> {
                b.setORangedCriticalChancePowerScaleNumerator(value);
            }
            case "chance-power-scale-denominator" -> {
                b.setORangedCriticalChancePowerScaleDenominator(value);
            }
            case "chance-add-denominator" -> {
                b.setORangedCriticalChanceAddDenominator(value);
            }
            default -> errors.add("Line: " + line + " unknown o-ranged-critical constant");
        }
    }

    /**
     * Parse a record from a list of 'o-melee-critical-level' into the builder
     *
     * <p>The final value of the rec fields is resolved to a MessageTypeEnum here
     *
     * <p>Function oMeleeCriticalLevel commented in full before 260915, provenance stamp added on
     * 260915.
     *
     * @param rec    The entry from the list
     * @param b      The builder responsible for building the game data
     * @param errors The list of current errors
     */
    private void oMeleeCriticalLevel(@NotNull GameConstantsParseRecord rec, @NotNull GameConstantsData.GameConstantsBuilder b,
                                     @NotNull List<String> errors) {
        List<String> f = rec.getFields();
        int line = rec.getLineNumber();

        if (f.size() != 3) {
            errors.add("Line: " + line + ": o-melee-critical-level expects value:value:value, got " +
                    f.size() + " fields");
            return;
        }

        Integer chance = coerceInt(f.get(0), line, errors);
        Integer dice = coerceInt(f.get(1), line, errors);
        String messageType = f.get(2);
        MessageType messageTypeEnum;

        if (chance == null || dice == null) {
            return;
        }

        try {
            messageTypeEnum = MessageType.valueOf("MSG_" + messageType);
        } catch (IllegalArgumentException e) {
            errors.add("Line: " + line + ": unknown message type " + messageType);
            return;
        }

        OMeleeCriticalLevelData record = new OMeleeCriticalLevelData(chance, dice, messageTypeEnum);
        String result = b.addOMeleeCriticalLevelData(record);
        if (!result.isEmpty()) {
            errors.add(result);
        }
    }

    /**
     * Parse an entry from a list of 'o-melee-critical' into the builder
     *
     * <p>Function oMeleeCritical commented in full before 260915, provenance stamp added on
     * 260915.
     *
     * @param rec    The entry from the list
     * @param b      The builder responsible for building the game data
     * @param errors The list of current errors
     */
    private void oMeleeCritical(@NotNull GameConstantsParseRecord rec, @NotNull GameConstantsData.GameConstantsBuilder b,
                                @NotNull List<String> errors) {
        List<String> f = rec.getFields();
        int line = rec.getLineNumber();

        if (f.size() != 2) {
            errors.add("Line: " + line + ": o-melee-critical requires label:value, " +
                    "got " + f.size() + " fields");
            return;
        }

        String label = f.get(0);
        Integer value = coerceInt(f.get(1), line, errors);
        if (value == null) return;

        switch (label) {
            case "debuff-toh" -> {
                b.setOMeleeCriticalDebuffToh(value);
            }
            case "power-toh-scale-numerator" -> {
                b.setOMeleeCriticalPowerTohScaleNumerator(value);
            }
            case "power-toh-scale-denominator" -> {
                b.setOMeleeCriticalPowerTohScaleDenominator(value);
            }
            case "chance-power-scale-numerator" -> {
                b.setOMeleeCriticalChancePowerScaleNumerator(value);
            }
            case "chance-power-scale-denominator" -> {
                b.setOMeleeCriticalChancePowerScaleDenominator(value);
            }
            case "chance-add-denominator" -> {
                b.setOMeleeCriticalChanceAddDenominator(value);
            }
            default -> errors.add("Line: " + line + ": unknown o-melee-critical constant");
        }
    }

    /**
     * Parse a record from a list of 'ranged-critical-level' into the builder
     *
     * <p>The final value of the rec fields is resolved to a MessageTypeEnum here
     *
     * <p>Function rangedCriticalLevel commented in full before 260915, provenance stamp added on
     * 260915.
     *
     * @param rec    The entry from the list
     * @param b      The builder responsible for building the game data
     * @param errors The list of current errors
     */
    private void rangedCriticalLevel(@NotNull GameConstantsParseRecord rec, @NotNull GameConstantsData.GameConstantsBuilder b,
                                     @NotNull List<String> errors) {
        List<String> f = rec.getFields();
        int line = rec.getLineNumber();

        if (f.size() != 4) {
            errors.add("Line: " + line + ": ranged-critical-level expects value:value:value:value, " +
                    "got " + f.size() + " fields");
            return;
        }

        Integer cutoff = coerceInt(f.get(0), line, errors);
        Integer damageMultiplier = coerceInt(f.get(1), line, errors);
        Integer damageAdded = coerceInt(f.get(2), line, errors);
        if (cutoff == null || damageMultiplier == null || damageAdded == null)
            return;

        String messageType = f.get(3);
        MessageType messageTypeEnum;

        try {
            messageTypeEnum = MessageType.valueOf("MSG_" + messageType);
        } catch (IllegalArgumentException e) {
            errors.add("Line: " + line + ": unknown message type: " + messageType);
            return;
        }

        b.addRangedCriticalLevelData(new RangedCriticalLevelData(cutoff, damageMultiplier, damageAdded, messageTypeEnum));
    }

    /**
     * Parse an entry from a list of 'ranged-critical' into the builder
     *
     * <p>Function rangedCritical commented in full before 260915, provenance stamp added on
     * 260915.
     *
     * @param rec    The entry from the list
     * @param b      The builder responsible for building the game data
     * @param errors The list of current errors
     */
    private void rangedCritical(@NotNull GameConstantsParseRecord rec, @NotNull GameConstantsData.GameConstantsBuilder b,
                                @NotNull List<String> errors) {
        List<String> f = rec.getFields();
        int line = rec.getLineNumber();

        if (f.size() != 2) {
            errors.add("Line: " + line + ": ranged-critical expects " +
                    "label:value, got " + f.size() + " fields");
            return;
        }

        String label = f.get(0);
        Integer value = coerceInt(f.get(1), line, errors);
        if (value == null) return;

        switch (label) {
            case "debuff-toh" -> {
                b.setRangedCriticalDebuffToh(value);
            }
            case "chance-weight-scale" -> {
                b.setRangedCriticalChanceWeightScale(value);
            }
            case "chance-toh-scale" -> {
                b.setRangedCriticalChanceTohScale(value);
            }
            case "chance-level-scale" -> {
                b.setRangedCriticalChanceLevelScale(value);
            }
            case "chance-launched-toh-skill-scale" -> {
                b.setRangedCriticalChanceLaunchedTohSkillScale(value);
            }
            case "chance-thrown-toh-skill-scale" -> {
                b.setRangedCriticalChanceThrownTohSkillScale(value);
            }
            case "chance-offset" -> {
                b.setRangedCriticalChanceOffset(value);
            }
            case "chance-range" -> {
                b.setRangedCriticalChanceRange(value);
            }
            case "power-weight-scale" -> {
                b.setRangedCriticalPowerWeightScale(value);
            }
            case "power-random" -> {
                b.setRangedCriticalPowerRandom(value);
            }
            default -> errors.add("Line: " + line + ": unknown ranged-critical constant");
        }
    }


    /**
     * Parse a record from a list of 'melee-critical-level' into the builder
     *
     * <p>The final value of the rec fields is resolved to a MessageTypeEnum here
     *
     * <p>Function meleeCriticalLevel commented in full before 260915, provenance stamp added on
     * 260915.
     *
     * @param rec    The entry from the list
     * @param b      The builder responsible for building the game data
     * @param errors The list of current errors
     */
    private void meleeCriticalLevel(@NotNull GameConstantsParseRecord rec, @NotNull GameConstantsData.GameConstantsBuilder b,
                                    @NotNull List<String> errors) {
        List<String> f = rec.getFields();
        int line = rec.getLineNumber();

        if (f.size() != 4) {
            errors.add("Line: " + rec.getLineNumber() + ": melee-critical-level expect " +
                    "value:value:value:value, got " + f.size() + " fields");
            return;
        }

        Integer cutoff = coerceInt(f.get(0), line, errors);
        Integer damageMultiplier = coerceInt(f.get(1), line, errors);
        Integer damageAddition = coerceInt(f.get(2), line, errors);
        if (cutoff == null || damageMultiplier == null || damageAddition == null) return;

        String messageType = f.get(3);
        MessageType messageTypeEnum;

        try {
            messageTypeEnum = MessageType.valueOf("MSG_" + messageType);
        } catch (IllegalArgumentException e) {
            errors.add("Line: " + rec.getLineNumber() + ": unknown message type: " + messageType);
            return;
        }

        MeleeCriticalLevelData meleeCriticalLevelData = new MeleeCriticalLevelData(cutoff, damageMultiplier,
                damageAddition, messageTypeEnum);

        b.addMeleeCriticalLevelData(meleeCriticalLevelData);
    }

    /**
     * Parse an entry from a list of 'melee-critical' into the builder
     *
     * <p>Function meleeCritical commented in full before 260915, provenance stamp added on
     * 260915.
     *
     * @param rec    The entry from the list
     * @param b      The builder responsible for building the game data
     * @param errors The list of current errors
     */
    private void meleeCritical(@NotNull GameConstantsParseRecord rec, @NotNull GameConstantsData.GameConstantsBuilder b,
                               @NotNull List<String> errors) {
        List<String> f = rec.getFields();
        int line = rec.getLineNumber();

        if (f.size() != 2) {
            errors.add("Line: " + line + ": melee-critical expects label:value, got "
                    + f.size() + " fields");
            return;
        }

        String label = f.get(0);
        Integer value = coerceInt(f.get(1), line, errors);
        if (value == null) return;

        switch (label) {
            case "debuff-toh" -> {
                b.setMeleeCriticalDebuffToh(value);
            }
            case "chance-weight-scale" -> {
                b.setMeleeCriticalChanceWeightScale(value);
            }
            case "chance-toh-scale" -> {
                b.setMeleeCriticalChanceTohScale(value);
            }
            case "chance-level-scale" -> {
                b.setMeleeCriticalChanceLevelScale(value);
            }
            case "chance-toh-skill-scale" -> {
                b.setMeleeCriticalChanceTohSkillScale(value);
            }
            case "chance-offset" -> {
                b.setMeleeCriticalChanceOffset(value);
            }
            case "chance-range" -> {
                b.setMeleeCriticalChanceRange(value);
            }
            case "power-weight-scale" -> {
                b.setMeleeCriticalPowerWeightScale(value);
            }
            case "power-random" -> {
                b.setMeleeCriticalPowerRandom(value);
            }
            default -> errors.add("Line: " + line + ": unknown melee-critical constant");
        }
    }

    /**
     * Parse an entry from a list of 'Player constants' into the builder
     *
     * <p>Function player commented in full before 260915, provenance stamp added on 260915.
     *
     * @param rec    The entry from the list
     * @param b      The builder responsible for building the game data
     * @param errors The list of current errors
     */
    private void player(@NotNull GameConstantsParseRecord rec, @NotNull GameConstantsData.GameConstantsBuilder b,
                        @NotNull List<String> errors) {
        List<String> f = rec.getFields();
        int line = rec.getLineNumber();

        if (f.size() != 2) {
            errors.add("Line: " + line + ": player expects label:value, got "
                    + f.size() + " fields");
            return;
        }

        String label = f.get(0);
        Integer value = coerceInt(f.get(1), line, errors);
        if (value == null) return;

        switch (label) {
            case "max-sight" -> {
                b.setPlayerMaxSight(value);
            }
            case "max-range" -> {
                b.setPlayerMaxRange(value);
            }
            case "start-gold" -> {
                b.setPlayerStartGold(value);
            }
            case "food-value" -> {
                b.setPlayerFoodValue(value);
            }
            default -> errors.add("Line: " + line + ": unknown player constant");
        }
    }

    /**
     * Parse an entry from a list of 'Object Generation' into the builder
     *
     * <p>Function objGen commented in full before 260915, provenance stamp added on 260915.
     *
     * @param rec    The entry from the list
     * @param b      The builder responsible for building the game data
     * @param errors The list of current errors
     */
    private void objGen(@NotNull GameConstantsParseRecord rec, @NotNull GameConstantsData.GameConstantsBuilder b,
                        @NotNull List<String> errors) {
        List<String> f = rec.getFields();
        int line = rec.getLineNumber();

        if (f.size() != 2) {
            errors.add("Line: " + line + ": obj-make expects label:value, got "
                    + f.size() + " fields");
            return;
        }

        String label = f.get(0);
        Integer value = coerceInt(f.get(1), line, errors);
        if (value == null) return;

        switch (label) {
            case "max-depth" -> {
                b.setObjMakeMaxDepth(value);
            }
            case "great-obj" -> {
                b.setObjGreatObj(value);
            }
            case "great-ego" -> {
                b.setObjGreatEgo(value);
            }
            case "fuel-torch" -> {
                b.setObjFuelTorch(value);
            }
            case "fuel-lamp" -> {
                b.setObjFuelLamp(value);
            }
            case "default-lamp" -> {
                b.setObjDefaultLamp(value);
            }
            default -> errors.add("Line: " + line + ": unknown obj-make constant");
        }
    }

    /**
     * Parse an entry from a list of 'Store Parameters' into the builder
     *
     * <p>Function store commented in full before 260915, provenance stamp added on 260915.
     *
     * @param rec    The entry from the list
     * @param b      The builder responsible for building the game data
     * @param errors The list of current errors
     */
    private void store(@NotNull GameConstantsParseRecord rec, @NotNull GameConstantsData.GameConstantsBuilder b,
                       @NotNull List<String> errors) {
        List<String> f = rec.getFields();
        int line = rec.getLineNumber();

        if (f.size() != 2) {
            errors.add("Line: " + line + ": store expects label:value, got "
                    + f.size() + " fields");
            return;
        }

        String label = f.get(0);
        Integer value = coerceInt(f.get(1), line, errors);
        if (value == null) return;

        switch (label) {
            case "inven-max" -> {
                b.setStoreInvenMax(value);
            }
            case "turns" -> {
                b.setStoreTurns(value);
            }
            case "shuffle" -> {
                b.setStoreShuffle(value);
            }
            case "magic-level" -> {
                b.setStoreMagicLevel(value);
            }
            default -> errors.add("Line: " + line + ": unknown store constant");
        }
    }

    /**
     * Parse an entry from a list of 'Carrying Capacity' into the builder
     *
     * <p>Function carryCap commented in full before 260915, provenance stamp added on 260915.
     *
     * @param rec    The entry from the list
     * @param b      The builder responsible for building the game data
     * @param errors The list of current errors
     */
    private void carryCap(@NotNull GameConstantsParseRecord rec, @NotNull GameConstantsData.GameConstantsBuilder b,
                          @NotNull List<String> errors) {
        List<String> f = rec.getFields();
        int line = rec.getLineNumber();

        if (f.size() != 2) {
            errors.add("Line: " + line + ": carry-cap expects label:value, got "
                    + f.size() + " fields");
            return;
        }

        String label = f.get(0);
        Integer value = coerceInt(f.get(1), line, errors);
        if (value == null) return;

        switch (label) {
            case "pack-size" -> {
                b.setCarryCapPackSize(value);
            }
            case "quiver-size" -> {
                b.setCarryCapQuiverSize(value);
            }
            case "quiver-slot-size" -> {
                b.setCarryCapQuiverSlotSize(value);
            }
            case "thrown-quiver-mult" -> {
                b.setCarryCapThrownQuiverMult(value);
            }
            case "floor-size" -> {
                b.setCarryCapFloorSize(value);
            }
            default -> {
                errors.add("Line: " + line + ": unknown carry-cap constant");
            }
        }
    }

    /**
     * Parse an entry from a list of 'World' into the builder
     *
     * <p>Function world commented in full before 260915, provenance stamp added on 260915.
     *
     * @param rec    The entry from the list
     * @param b      The builder responsible for building the game data
     * @param errors The list of current errors
     */
    private void world(@NotNull GameConstantsParseRecord rec, @NotNull GameConstantsData.GameConstantsBuilder b,
                       @NotNull List<String> errors) {
        List<String> f = rec.getFields();
        int line = rec.getLineNumber();

        if (f.size() != 2) {
            errors.add("Line: " + line + ": world expects label:value, got "
                    + f.size() + " fields");
            return;
        }

        String label = f.get(0);
        Integer value = coerceInt(f.get(1), line, errors);
        if (value == null) return;

        switch (label) {
            case "max-depth" -> {
                b.setWorldMaxDepth(value);
            }
            case "day-length" -> {
                b.setWorldDayLength(value);
            }
            case "dungeon-hgt" -> {
                b.setWorldDungeonHgt(value);
            }
            case "dungeon-wid" -> {
                b.setWorldDungeonWid(value);
            }
            case "town-hgt" -> {
                b.setWorldTownHgt(value);
            }
            case "town-wid" -> {
                b.setWorldTownWid(value);
            }
            case "feeling-total" -> {
                b.setWorldFeelingTotal(value);
            }
            case "feeling-need" -> {
                b.setWorldFeelingNeed(value);
            }
            case "stair-skip" -> {
                b.setWorldStairSkip(value);
            }
            case "move-energy" -> {
                b.setWorldMoveEnergy(value);
            }
            default -> errors.add("Line: " + line + ": unknown world constant");
        }
    }

    /**
     * Parse an entry from a list of 'Dungeon Generation' into the builder
     *
     * <p>Function dunGen commented in full before 260915, provenance stamp added on 260915.
     *
     * @param rec    The entry from the list
     * @param b      The builder responsible for building the game data
     * @param errors The list of current errors
     */
    private void dunGen(@NotNull GameConstantsParseRecord rec, @NotNull GameConstantsData.GameConstantsBuilder b,
                        @NotNull List<String> errors) {
        List<String> f = rec.getFields();
        int line = rec.getLineNumber();

        if (f.size() != 2) {
            errors.add("Line: " + line + ": dun-gen expects label:value, got "
                    + f.size() + " fields");
            return;
        }

        String label = f.get(0);
        Integer value = coerceInt(f.get(1), line, errors);
        if (value == null) return;

        switch (label) {
            case "cent-max" -> {
                b.setDunGenCentMax(value);
            }
            case "door-max" -> {
                b.setDunGenDoorMax(value);
            }
            case "wall-max" -> {
                b.setDunGenWallMax(value);
            }
            case "tunn-max" -> {
                b.setDunGenTunnMax(value);
            }
            case "amt-room" -> {
                b.setDunGenAmtRoom(value);
            }
            case "amt-item" -> {
                b.setDunGenAmtItem(value);
            }
            case "amt-gold" -> {
                b.setDunGenAmtGold(value);
            }
            case "pit-max" -> {
                b.setDunGenPitMax(value);
            }
            default -> {
                errors.add("Line: " + line + ": unknown dun-gen constant " + label);
            }
        }
    }

    /**
     * Parse an entry from a list of 'Monster Gameplay' into the builder
     *
     * <p>Function monPlay commented in full before 260915, provenance stamp added on 260915.
     *
     * @param rec    The entry from the list
     * @param b      The builder responsible for building the game data
     * @param errors The list of current errors
     */
    private void monPlay(@NotNull GameConstantsParseRecord rec, @NotNull GameConstantsData.GameConstantsBuilder b,
                         @NotNull List<String> errors) {
        List<String> f = rec.getFields();
        int line = rec.getLineNumber();

        if (f.size() != 2) {
            errors.add("Line: " + line + ": mon-play expects label:value, got "
                    + f.size() + " field(s)");
            return;
        }

        String label = f.get(0);
        Integer value = coerceInt(f.get(1), line, errors);
        if (value == null) return;

        switch (label) {
            case "break-glyph" -> {
                b.setMonPlayBreakGlyph(value);
            }
            case "mult-rate" -> {
                b.setMonPlayMultRate(value);
            }
            case "life-drain" -> {
                b.setMonPlayLifeDrain(value);
            }
            case "flee-range" -> {
                b.setMonPlayFleeRange(value);
            }
            case "turn-range" -> {
                b.setMonPlayTurnRange(value);
            }
            default -> {
                errors.add("Line: " + line + ": unknown mon-play constant " + label);
            }
        }
    }

    /**
     * Parse an entry from a list of 'Monster Generation' into the builder
     *
     * <p>Function monGen commented in full before 260915, provenance stamp added on 260915.
     *
     * @param rec    The entry from the list
     * @param b      The builder responsible for building the game data
     * @param errors The list of current errors
     */
    private void monGen(@NotNull GameConstantsParseRecord rec, @NotNull GameConstantsData.GameConstantsBuilder b,
                        @NotNull List<String> errors) {
        List<String> f = rec.getFields();
        int line = rec.getLineNumber();

        if (f.size() != 2) {
            errors.add("Line: " + line + ": mon-gen expects label:value, got "
                    + f.size() + " field(s)");
            return;
        }

        String label = f.get(0);
        Integer value = coerceInt(f.get(1), line, errors);
        if (value == null) return;

        switch (label) {
            case "chance" -> {
                b.setMonGenChance(value);
            }
            case "level-min" -> {
                b.setMonGenLevelMin(value);
            }
            case "town-day" -> {
                b.setMonGenTownDay(value);
            }
            case "town-night" -> {
                b.setMonGenTownNight(value);
            }
            case "repro-max" -> {
                b.setMonGenReproMax(value);
            }
            case "ood-chance" -> {
                b.setMonGenOodChance(value);
            }
            case "ood-amount" -> {
                b.setMonGenOodAmount(value);
            }
            case "group-max" -> {
                b.setMonGenGroupMax(value);
            }
            case "group-dist" -> {
                b.setMonGenGroupDist(value);
            }
            default -> {
                errors.add("Line: " + line + ": unknown mon-gen constant '" + label + "'");
            }
        }
    }

    /**
     * Parse an entry from a list of 'level maxima' into the builder
     *
     * <p>Function levelMax commented in full before 260915, provenance stamp added on 260915.
     *
     * @param rec    The entry from the list
     * @param b      The builder responsible for building the game data
     * @param errors The list of current errors
     */
    private void levelMax(@NotNull GameConstantsParseRecord rec, @NotNull GameConstantsData.GameConstantsBuilder b,
                          @NotNull List<String> errors) {
        List<String> f = rec.getFields();
        int line = rec.getLineNumber();

        if (f.size() != 2) {
            errors.add("Line: " + line + ": level-max expects label:value, got "
                    + f.size() + " field(s)");
            return;
        }

        String label = f.get(0);
        Integer value = coerceInt(f.get(1), line, errors);
        if (value == null) return;

        if (label.equals("monsters")) {
            b.setLevelMaxMonsters(value);
        } else {
            errors.add("Line: " + line + ": unknown level-max constant '" + label + "'");
        }
    }

    /**
     * Coerces a string expression of an integer into an Integer. Logs an error if the coercion fails
     *
     * <p>Function coerceInt commented in full before 260915, provenance stamp added on 260915.
     *
     * @param raw    The string expression of the integer
     * @param line   The line this string was found on in the datafile
     * @param errors The list of current errors
     * @return An Integer containing the int value of the incoming raw string, or null if an error
     * occurred
     */
    @Nullable
    @CheckReturnValue
    private Integer coerceInt(@NotNull String raw, int line, @NotNull List<String> errors) {
        try {
            return Integer.parseInt(raw);
        } catch (NumberFormatException e) {
            errors.add("Line: " + line + ": " + raw + " is not an integer");
            return null;
        }
    }
}
