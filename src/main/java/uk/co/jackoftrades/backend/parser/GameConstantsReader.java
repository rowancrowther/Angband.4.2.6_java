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

package uk.co.jackoftrades.backend.parser;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.co.jackoftrades.backend.parser.gameconstants.GameConstantsParseRecord;
import uk.co.jackoftrades.backend.parser.grammars.gameconstants.GameConstantsGrammar;
import uk.co.jackoftrades.backend.parser.grammars.gameconstants.GameConstantsLexer;
import uk.co.jackoftrades.middle.game.globals.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Class to read in <code>constants.txt</code> file and pass back to the <code>GameConstants</code> object.
 *
 * @author Rowan Crowther
 */
public class GameConstantsReader {
    /**
     * Logger used to report file-loading failures
     */
    private static final Logger logger = LogManager.getLogger();

    public GameConstantsParseResult parse(@NotNull String filename) throws IOException {
        return parseWithResults(filename);
    }

    /**
     * Dispatch a record to the builder passing through the current list of errors
     * @param filename The name of the file to parse
     *
     * @author Rowan Crowther
     */
    @CheckReturnValue
    public GameConstantsParseResult parseWithResults(@NotNull String filename) throws IOException {
        GameConstantsData data = null;
        ParseErrors errorCatcher = null;
        List<String> errors = new ArrayList<>();

        try {
            CharStream input = CharStreams.fromFileName(filename);
            GameConstantsLexer lexer = new GameConstantsLexer(input);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            GameConstantsGrammar parser = new GameConstantsGrammar(tokens);

            errorCatcher = ParseErrors.install(lexer, parser, filename);

            GameConstantsGrammar.FileContext output = parser.file();

            errorCatcher.throwIfAny();

            List<GameConstantsParseRecord> records = output.results;

            GameConstantsData.GameConstantsBuilder b = new GameConstantsData.GameConstantsBuilder();

            for (GameConstantsParseRecord record : records) {
                dispatch(record, b, errors);
            }
            data = b.build(errors);

            errorCatcher.throwIfAny();
        } catch (IOException e) {
            logger.error("Error while loading file {}", filename, e);
            throw e;
        } catch (ParseCancellationException e) {
            logger.error("Error while parsing file {}", filename, e);
            return new GameConstantsParseResult(null, errorCatcher.getErrors());
        }

        return new GameConstantsParseResult(data, errors);
    }

    /**
     * Dispatch a record to the builder passing through the current list of errors
     *
     * @param rec    The record from the constants.txt file
     * @param b      The builder to build the records from
     * @param errors The list of current errors
     * @author Rowan Crowther
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

        ORangedCriticalLevelData record = new ORangedCriticalLevelData(chance, dice, messageType);
        b.addORangedCriticalLevel(record);
    }

    /**
     * Parse an entry from a list of 'o-ranged-critical' into the builder
     *
     * @param rec    The entry from the list
     * @param b      The builder responsible for building the game data
     * @param errors The list of current errors
     * @author Rowan Crowther
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
                if (!b.setORangedCriticalDebuffToh(value)) {
                    errors.add("Line: " + line + ": duplicate o-ranged-critical:debuff-toh");
                }
            }
            case "power-launched-toh-scale-numerator" -> {
                if (!b.setORangedCriticalPowerLaunchedTohScaleNumerator(value)) {
                    errors.add("Line: " + line + ": duplicate o-ranged-critical:power-launched-toh-scale-numerator");
                }
            }
            case "power-launched-toh-scale-denominator" -> {
                if (!b.setORangedCriticalPowerLaunchedTohScaleDenominator(value)) {
                    errors.add("Line: " + line + ": duplicate o-ranged-critical:power-launched-toh-scale-denominator");
                }
            }
            case "power-thrown-toh-scale-numerator" -> {
                if (!b.setORangedCriticalPowerThrownTohScaleNumerator(value)) {
                    errors.add("Line: " + line + ": duplicate o-ranged-critical:power-thrown-toh-scale-numerator");
                }
            }
            case "power-thrown-toh-scale-denominator" -> {
                if (!b.setORangedCriticalPowerThrownTohScaleDenominator(value)) {
                    errors.add("Line: " + line + ": duplicate o-ranged-critical:power-thrown-toh-scale-denominator");
                }
            }
            case "chance-power-scale-numerator" -> {
                if (!b.setORangedCriticalChancePowerScaleNumerator(value)) {
                    errors.add("Line: " + line + ": duplicate o-ranged-critical:chance-power-scale-numerator");
                }
            }
            case "chance-power-scale-denominator" -> {
                if (!b.setORangedCriticalChancePowerScaleDenominator(value)) {
                    errors.add("Line: " + line + ": duplicate o-ranged-critical:chance-power-scale-denominator");
                }
            }
            case "chance-add-denominator" -> {
                if (!b.setORangedCriticalChanceAddDenominator(value)) {
                    errors.add("Line: " + line + ": duplicate o-ranged-critical:chance-add-denominator");
                }
            }
            default -> errors.add("Line: " + line + " unknown o-ranged-critical constant");
        }
    }

    /**
     * Parse a record from a list of 'o-melee-critical-level' into the builder
     *
     * @param rec    The entry from the list
     * @param b      The builder responsible for building the game data
     * @param errors The list of current errors
     * @author Rowan Crowther
     */
    private void oMeleeCriticalLevel(@NotNull GameConstantsParseRecord rec, @NotNull GameConstantsData.GameConstantsBuilder b,
                                     @NotNull List<String> errors) {
        List<String> f = rec.getFields();
        int line = rec.getLineNumber();

        if (f.size() != 3) {
            errors.add("Line: " + line + ": o-melee-critical expects value:value:value, got " +
                    f.size() + " fields");
            return;
        }

        Integer chance = coerceInt(f.get(0), line, errors);
        Integer dice = coerceInt(f.get(1), line, errors);
        String messageType = f.get(2);

        if (chance == null || dice == null) {
            return;
        }

        OMeleeCriticalLevelData record = new OMeleeCriticalLevelData(chance, dice, messageType);
        b.addOMeleeCriticalLevelData(record);
    }

    /**
     * Parse an entry from a list of 'o-melee-critical' into the builder
     *
     * @param rec    The entry from the list
     * @param b      The builder responsible for building the game data
     * @param errors The list of current errors
     * @author Rowan Crowther
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
                if (!b.setOMeleeCriticalDebuffToh(value))
                    errors.add("Line: " + line + ": duplicate constant o-melee-critical:debuff-toh");
            }
            case "power-toh-scale-numerator" -> {
                if (!b.setOMeleeCriticalPowerTohScaleNumerator(value)) {
                    errors.add("Line: " + line + ": duplicate constant o-melee-critical:power-toh-scale-numerator");
                }
            }
            case "power-toh-scale-denominator" -> {
                if (!b.setOMeleeCriticalPowerTohScaleDenominator(value)) {
                    errors.add("Line: " + line + ": duplicate constant o-melee-critical:power-toh-scale-denominator");
                }
            }
            case "chance-power-scale-numerator" -> {
                if (!b.setOMeleeCriticalChancePowerScaleNumerator(value)) {
                    errors.add("Line: " + line + ": duplicate constant o-melee-critical:chance-power-scale-numerator");
                }
            }
            case "chance-power-scale-denominator" -> {
                if (!b.setOMeleeCriticalChancePowerScaleDenominator(value)) {
                    errors.add("Line: " + line + ": duplicate constant o-melee-critical:chance-power-scale-denominator");
                }
            }
            case "chance-add-denominator" -> {
                if (!b.setOMeleeCriticalChanceAddDenominator(value)) {
                    errors.add("Line: " + line + ": duplicate constant o-melee-critical:chance-add-denominator");
                }
            }
            default -> errors.add("Line: " + line + ": unknown o-melee-critical constant");
        }
    }

    /**
     * Parse a record from a list of 'ranged-critical-level' into the builder
     *
     * @param rec    The entry from the list
     * @param b      The builder responsible for building the game data
     * @param errors The list of current errors
     * @author Rowan Crowther
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

        b.addRangedCriticalLevelData(new RangedCriticalLevelData(cutoff, damageMultiplier, damageAdded, messageType));
    }

    /**
     * Parse an entry from a list of 'ranged-critical' into the builder
     *
     * @param rec    The entry from the list
     * @param b      The builder responsible for building the game data
     * @param errors The list of current errors
     * @author Rowan Crowther
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
                if (!b.setRangedCriticalDebuffToh(value)) {
                    errors.add("Line: " + line + ": duplicate ranged-critical:debuff-toh");
                }
            }
            case "chance-weight-scale" -> {
                if (!b.setRangedCriticalChanceWeightScale(value)) {
                    errors.add("Line: " + line + ": duplicate ranged-critical:chance-weight-scale");
                }
            }
            case "chance-toh-scale" -> {
                if (!b.setRangedCriticalChanceTohScale(value)) {
                    errors.add("Line: " + line + ": duplicate ranged-critical:chance-toh-scale");
                }
            }
            case "chance-level-scale" -> {
                if (!b.setRangedCriticalChanceLevelScale(value)) {
                    errors.add("Line: " + line + ": duplicate ranged-critical:chance-level-scale");
                }
            }
            case "chance-launched-toh-skill-scale" -> {
                if (!b.setRangedCriticalChanceLaunchedTohSkillScale(value)) {
                    errors.add("Line: " + line + ": duplicate ranged-critical:chance-launched-toh-skill-scale");
                }
            }
            case "chance-thrown-toh-skill-scale" -> {
                if (!b.setRangedCriticalChanceThrownTohSkillScale(value)) {
                    errors.add("Line: " + line + ": duplicate ranged-critical:chance-thrown-toh-skill-scale");
                }
            }
            case "chance-offset" -> {
                if (!b.setRangedCriticalChanceOffset(value)) {
                    errors.add("Line: " + line + ": duplicate ranged-critical:chance-offset");
                }
            }
            case "chance-range" -> {
                if (!b.setRangedCriticalChanceRange(value)) {
                    errors.add("Line: " + line + ": duplicate ranged-critical:chance-range");
                }
            }
            case "power-weight-scale" -> {
                if (!b.setRangedCriticalPowerWeightScale(value)) {
                    errors.add("Line: " + line + ": duplicate ranged-critical:power-weight-scale");
                }
            }
            case "power-random" -> {
                if (!b.setRangedCriticalPowerRandom(value)) {
                    errors.add("Line: " + line + ": duplicate ranged-critical:power-random");
                }
            }
            default -> errors.add("Line: " + line + ": unknown ranged-critical constant");
        }
    }


    /**
     * Parse a record from a list of 'melee-critical-level' into the builder
     *
     * @param rec    The entry from the list
     * @param b      The builder responsible for building the game data
     * @param errors The list of current errors
     * @author Rowan Crowther
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

        MeleeCriticalLevelData meleeCriticalLevelData = new MeleeCriticalLevelData(cutoff, damageMultiplier,
                damageAddition, messageType);

        b.addMeleeCriticalLevelData(meleeCriticalLevelData);
    }

    /**
     * Parse an entry from a list of 'melee-critical' into the builder
     *
     * @param rec    The entry from the list
     * @param b      The builder responsible for building the game data
     * @param errors The list of current errors
     * @author Rowan Crowther
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
                if (!b.setMeleeCriticalDebuffToh(value)) {
                    errors.add("Line: " + line + ": duplicate melee-critical:debuff-toh");
                }
            }
            case "chance-weight-scale" -> {
                if (!b.setMeleeCriticalChanceWeightScale(value)) {
                    errors.add("Line: " + line + ": duplicate melee-critical:chance-weight-scale");
                }
            }
            case "chance-toh-scale" -> {
                if (!b.setMeleeCriticalChanceTohScale(value)) {
                    errors.add("Line: " + line + ": duplicate melee-critical:chance-toh-scale");
                }
            }
            case "chance-level-scale" -> {
                if (!b.setMeleeCriticalChanceLevelScale(value)) {
                    errors.add("Line: " + line + ": duplicate melee-critical:chance-level-scale");
                }
            }
            case "chance-toh-skill-scale" -> {
                if (!b.setMeleeCriticalChanceTohSkillScale(value)) {
                    errors.add("Line: " + line + ": duplicate melee-critical:chance-toh-skill-scale");
                }
            }
            case "chance-offset" -> {
                if (!b.setMeleeCriticalChanceOffset(value)) {
                    errors.add("Line: " + line + ": duplicate melee-critical:chance-offset");
                }
            }
            case "chance-range" -> {
                if (!b.setMeleeCriticalChanceRange(value)) {
                    errors.add("Line: " + line + ": duplicate melee-critical:chance-range");
                }
            }
            case "power-weight-scale" -> {
                if (!b.setMeleeCriticalPowerWeightScale(value)) {
                    errors.add("Line: " + line + ": duplicate melee-critical:power-weight-scale");
                }
            }
            case "power-random" -> {
                if (!b.setMeleeCriticalPowerRandom(value)) {
                    errors.add("Line: " + line + ": duplicate melee-critical:power-random");
                }
            }
            default -> errors.add("Line: " + line + ": unknown melee-critical constant");
        }
    }

    /**
     * Parse an entry from a list of 'Player constants' into the builder
     *
     * @param rec    The entry from the list
     * @param b      The builder responsible for building the game data
     * @param errors The list of current errors
     * @author Rowan Crowther
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
                if (!b.setPlayerMaxSight(value)) {
                    errors.add("Line: " + line + ": duplicate player:max-sight");
                }
            }
            case "max-range" -> {
                if (!b.setPlayerMaxRange(value)) {
                    errors.add("Line: " + line + ": duplicate player:max-range");
                }
            }
            case "start-gold" -> {
                if (!b.setPlayerStartGold(value)) {
                    errors.add("Line: " + line + ": duplicate player:start-gold");
                }
            }
            case "food-value" -> {
                if (!b.setPlayerFoodValue(value)) {
                    errors.add("Line: " + line + ": duplicate player:food-value");
                }
            }
            default -> errors.add("Line: " + line + ": unknown player constant");
        }
    }

    /**
     * Parse an entry from a list of 'Object Generation' into the builder
     *
     * @param rec    The entry from the list
     * @param b      The builder responsible for building the game data
     * @param errors The list of current errors
     * @author Rowan Crowther
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
                if (!b.setObjMakeMaxDepth(value)) {
                    errors.add("Line: " + line + ": duplicate obj-make:max-depth");
                }
            }
            case "great-obj" -> {
                if (!b.setObjGreatObj(value)) {
                    errors.add("Line: " + line + ": duplicate obj-make:great-obj");
                }
            }
            case "great-ego" -> {
                if (!b.setObjGreatEgo(value)) {
                    errors.add("Line: " + line + ": duplicate obj-make:great-ego");
                }
            }
            case "fuel-torch" -> {
                if (!b.setObjFuelTorch(value)) {
                    errors.add("Line: " + line + ": duplicate obj-make:fuel-torch");
                }
            }
            case "fuel-lamp" -> {
                if (!b.setObjFuelLamp(value)) {
                    errors.add("Line: " + line + ": duplicate obj-make:fuel-lamp");
                }
            }
            case "default-lamp" -> {
                if (!b.setObjDefaultLamp(value)) {
                    errors.add("Line: " + line + ": duplicate obj-make:default-lamp");
                }
            }
            default -> errors.add("Line: " + line + ": unknown obj-make constant");
        }
    }

    /**
     * Parse an entry from a list of 'Store Parameters' into the builder
     *
     * @param rec    The entry from the list
     * @param b      The builder responsible for building the game data
     * @param errors The list of current errors
     * @author Rowan Crowther
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
                if (!b.setStoreInvenMax(value))
                    errors.add("Line: " + line + ": duplicate store:inven-max");
            }
            case "turns" -> {
                if (!b.setStoreTurns(value))
                    errors.add("Line: " + line + ": duplicate store:turns");
            }
            case "shuffle" -> {
                if (!b.setStoreShuffle(value))
                    errors.add("Line: " + line + ": duplicate store:shuffle");
            }
            case "magic-level" -> {
                if (!b.setStoreMagicLevel(value))
                    errors.add("Line: " + line + ": duplicate store:magic-level");
            }
            default -> errors.add("Line: " + line + ": unknown store constant");
        }
    }

    /**
     * Parse an entry from a list of 'Carrying Capacity' into the builder
     *
     * @param rec    The entry from the list
     * @param b      The builder responsible for building the game data
     * @param errors The list of current errors
     * @author Rowan Crowther
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
                if (!b.setCarryCapPackSize(value)) {
                    errors.add("Line: " + line + ": duplicate carry-cap:pack-size");
                }
            }
            case "quiver-size" -> {
                if (!b.setCarryCapQuiverSize(value)) {
                    errors.add("Line: " + line + ": duplicate carry-cap:quiver-size");
                }
            }
            case "quiver-slot-size" -> {
                if (!b.setCarryCapQuiverSlotSize(value)) {
                    errors.add("Line: " + line + ": duplicate carry-cap:quiver-slot-size");
                }
            }
            case "thrown-quiver-mult" -> {
                if (!b.setCarryCapThrownQuiverMult(value)) {
                    errors.add("Line: " + line + ": duplicate carry-cap:thrown-quiver-mult");
                }
            }
            case "floor-size" -> {
                if (!b.setCarryCapFloorSize(value)) {
                    errors.add("Line: " + line + ": duplicate carry-cap:floor-size");
                }
            }
            default -> {
                errors.add("Line: " + line + ": unknown carry-cap constant");
            }
        }
    }

    /**
     * Parse an entry from a list of 'World' into the builder
     *
     * @param rec    The entry from the list
     * @param b      The builder responsible for building the game data
     * @param errors The list of current errors
     * @author Rowan Crowther
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
                if (!b.setWorldMaxDepth(value)) {
                    errors.add("Line: " + line + ": duplicate world:max-depth");
                }
            }
            case "day-length" -> {
                if (!b.setWorldDayLength(value)) {
                    errors.add("Line: " + line + ": duplicate world:day-length");
                }
            }
            case "dungeon-hgt" -> {
                if (!b.setWorldDungeonHgt(value)) {
                    errors.add("Line: " + line + ": duplicate world:dungeon-hgt");
                }
            }
            case "dungeon-wid" -> {
                if (!b.setWorldDungeonWid(value)) {
                    errors.add("Line: " + line + ": duplicate world:dungeon-wid");
                }
            }
            case "town-hgt" -> {
                if (!b.setWorldTownHgt(value)) {
                    errors.add("Line: " + line + ": duplicate world:town-hgt");
                }
            }
            case "town-wid" -> {
                if (!b.setWorldTownWid(value)) {
                    errors.add("Line: " + line + ": duplicate world:town-wid");
                }
            }
            case "feeling-total" -> {
                if (!b.setWorldFeelingTotal(value)) {
                    errors.add("Line: " + line + ": duplicate world:feeling-total");
                }
            }
            case "feeling-need" -> {
                if (!b.setWorldFeelingNeed(value)) {
                    errors.add("Line: " + line + ": duplicate world:feeling-need");
                }
            }
            case "stair-skip" -> {
                if (!b.setWorldStairSkip(value)) {
                    errors.add("Line: " + line + ": duplicate world:stair-skip");
                }
            }
            case "move-energy" -> {
                if (!b.setWorldMoveEnergy(value)) {
                    errors.add("Line: " + line + ": duplicate world:move-energy");
                }
            }
            default -> errors.add("Line: " + line + ": unknown world constant");
        }
    }

    /**
     * Parse an entry from a list of 'Dungeon Generation' into the builder
     *
     * @param rec    The entry from the list
     * @param b      The builder responsible for building the game data
     * @param errors The list of current errors
     * @author Rowan Crowther
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
                if (!b.setDunGenCentMax(value))
                    errors.add("Line: " + line + ": duplicate dun-gen:cent-max");
            }
            case "door-max" -> {
                if (!b.setDunGenDoorMax(value))
                    errors.add("Line: " + line + ": duplicate dun-gen:door-max");
            }
            case "wall-max" -> {
                if (!b.setDunGenWallMax(value))
                    errors.add("Line: " + line + ": duplicate dun-gen:wall-max");
            }
            case "tunn-max" -> {
                if (!b.setDunGenTunnMax(value))
                    errors.add("Line: " + line + ": duplicate dun-gen:tunn-max");
            }
            case "amt-room" -> {
                if (!b.setDunGenAmtRoom(value))
                    errors.add("Line: " + line + ": duplicate dun-gen:amt-room");
            }
            case "amt-item" -> {
                if (!b.setDunGenAmtItem(value))
                    errors.add("Line: " + line + ": duplicate dun-gen:amt-item");
            }
            case "amt-gold" -> {
                if (!b.setDunGenAmtGold(value))
                    errors.add("Line: " + line + ": duplicate dun-gen:amt-gold");
            }
            case "pit-max" -> {
                if (!b.setDunGenPitMax(value))
                    errors.add("Line: " + line + ": duplicate dun-gen:pit-max");
            }
            default -> {
                errors.add("Line: " + line + ": unknown dun-gen constant " + label);
            }
        }
    }

    /**
     * Parse an entry from a list of 'Monster Gameplay' into the builder
     *
     * @param rec    The entry from the list
     * @param b      The builder responsible for building the game data
     * @param errors The list of current errors
     * @author Rowan Crowther
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
                if (!b.setMonPlayBreakGlyph(value))
                    errors.add("Line: " + line + ": duplicate mon-play:break-glyph");
            }
            case "mult-rate" -> {
                if (!b.setMonPlayMultRate(value))
                    errors.add("Line: " + line + ": duplicate mon-play:mult-rate");
            }
            case "life-drain" -> {
                if (!b.setMonPlayLifeDrain(value))
                    errors.add("Line: " + line + ": duplicate mon-play:life-drain");
            }
            case "flee-range" -> {
                if (!b.setMonPlayFleeRange(value))
                    errors.add("Line: " + line + ": duplicate mon-play:flee-range");
            }
            case "turn-range" -> {
                if (!b.setMonPlayTurnRange(value))
                    errors.add("Line: " + line + ": duplicate mon-play:turn-range");
            }
            default -> {
                errors.add("Line: " + line + ": unknown mon-play constant " + label);
            }
        }
    }

    /**
     * Parse an entry from a list of 'Monster Generation' into the builder
     *
     * @param rec    The entry from the list
     * @param b      The builder responsible for building the game data
     * @param errors The list of current errors
     * @author Rowan Crowther
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
                if (!b.setMonGenChance(value))
                    errors.add("Line: " + line + ": duplicate mon-gen:chance");
            }
            case "level-min" -> {
                if (!b.setMonGenLevelMin(value))
                    errors.add("Line: " + line + ": duplicate mon-gen:level-min");
            }
            case "town-day" -> {
                if (!b.setMonGenTownDay(value))
                    errors.add("Line: " + line + ": duplicate mon-gen:town-day");
            }
            case "town-night" -> {
                if (!b.setMonGenTownNight(value))
                    errors.add("Line: " + line + ": duplicate mon-gen:town-night");
            }
            case "repro-max" -> {
                if (!b.setMonGenReproMax(value))
                    errors.add("Line: " + line + ": duplicate mon-gen:repro-max");
            }
            case "ood-chance" -> {
                if (!b.setMonGenOodChance(value))
                    errors.add("Line: " + line + ": duplicate mon-gen:ood-chance");
            }
            case "ood-amount" -> {
                if (!b.setMonGenOodAmount(value))
                    errors.add("Line: " + line + ": duplicate mon-gen:ood-amount");
            }
            case "group-max" -> {
                if (!b.setMonGenGroupMax(value))
                    errors.add("Line: " + line + ": duplicate mon-gen:group-max");
            }
            case "group-dist" -> {
                if (!b.setMonGenGroupDist(value))
                    errors.add("Line: " + line + ": duplicate mon-gen:group-dist");
            }
            default -> {
                errors.add("Line: " + line + ": unknown mon-gen constant '" + label + "'");
            }
        }
    }

    /**
     * Parse an entry from a list of 'level maxima' into the builder
     *
     * @param rec    The entry from the list
     * @param b      The builder responsible for building the game data
     * @param errors The list of current errors
     * @author Rowan Crowther
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
            if (!b.setLevelMaxMonsters(value))
                errors.add("Line: " + line + ": duplicate level-max:monsters");
        } else {
            errors.add("Line: " + line + ": unknown level-max constant '" + label + "'");
        }
    }

    /**
     * Coerces a string expression of an integer into an Integer. Logs an error if the coercion fails
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