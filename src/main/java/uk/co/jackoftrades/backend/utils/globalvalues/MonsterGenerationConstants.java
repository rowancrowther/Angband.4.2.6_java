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
 *    Java code copyright (c) Rowan Crowther 2026
 */

package uk.co.jackoftrades.backend.utils.globalvalues;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftrades.backend.io.bespokeexceptions.InvalidTokenFoundDuringParse;

/**
 * Holds the "mon-gen" group of constants from {@code constants.txt} — the
 * monster-generation tuning: spawn chance, per-level minimum, town day/night
 * populations, breeding cap, out-of-depth (OOD) chance/amount and group
 * size/spacing. Several setters validate against
 * {@link LevelMaxConstants#getMaxMonstersPerLevel()}, so that data is expected to
 * be loaded first. Part of the Java port of the C constants loader.
 *
 * @author ClaudeCode
 */
public class MonsterGenerationConstants {
    /**
     * The data-file group tag this class consumes ({@code mon-gen}).
     *
     * @author ClaudeCode
     */
    private final static String constantsTag = "mon-gen";
    /**
     * 1-in-{@code chance} probability that a new monster is generated.
     *
     * @author ClaudeCode
     */
    private static int chance;
    /**
     * Minimum number of monsters generated on a level.
     *
     * @author ClaudeCode
     */
    private static int levelMin;
    /**
     * Number of townsfolk generated during the day.
     *
     * @author ClaudeCode
     */
    private static int townDay;
    /**
     * Number of townsfolk generated during the night.
     *
     * @author ClaudeCode
     */
    private static int townNight;
    /**
     * Maximum number of breeding monsters allowed on a level.
     *
     * @author ClaudeCode
     */
    private static int reproMax;
    /**
     * 1-in-{@code oodChance} probability that a monster is out of its normal depth.
     *
     * @author ClaudeCode
     */
    private static int oodChance;
    /**
     * Maximum number of levels out of depth a monster may be generated.
     *
     * @author ClaudeCode
     */
    private static int oodAmount;
    /**
     * Maximum number of monsters in a single group.
     *
     * @author ClaudeCode
     */
    private static int groupMax;
    /**
     * Maximum distance a group may generate from a related group.
     *
     * @author ClaudeCode
     */
    private static int groupDist;
    /**
     * Logger used to report malformed/invalid constants during parsing.
     *
     * @author ClaudeCode
     */
    private static final Logger logger = LogManager.getLogger();

    /**
     * Returns the 1 in chance of a new monster being generated
     *
     * @return The chance a new monster is generated
     */
    @CheckReturnValue
    @Contract(pure = true)
    public static int getAllocMonsterChance() {
        return chance;
    }

    /**
     * Validate and store the spawn chance. Rejects values {@code <= 0}.
     *
     * @param chanceVal the proposed 1-in-N chance
     * @param tag       the constant name, used only for error reporting
     * @throws IllegalArgumentException if {@code chanceVal <= 0}
     * @author ClaudeCode
     */
    private static void setChance(int chanceVal, @NotNull String tag) throws IllegalArgumentException {
        if (chanceVal <= 0) {
            String message = "Invalid switch value found in constants.txt file. Input was " + constantsTag + ":" + tag + ":" + chanceVal;
            InvalidTokenFoundDuringParse e = new InvalidTokenFoundDuringParse(message);
            logger.error(message, e);
            throw e;
        }

        chance = chanceVal;
    }

    /**
     * Receive a text:value pair in a string, and store in the correct field
     *
     * @param value the text to parse down into a string and value, and store the value in the correct field as denoted
     *              by the string
     */
    public static void setValue(@NotNull String value) throws InvalidTokenFoundDuringParse {
        String[] results = value.split(":");

        // Check we have a valid input
        if (results.length != 2) {
            String message = "Invalid number of tokens found. Token was: " + constantsTag + ":" + value;
            logger.error(message);
            throw new InvalidTokenFoundDuringParse(message);
        }

        String name = results[0];
        int val;

        try {
            val = Integer.parseInt(results[1]);
        } catch (NumberFormatException e) {
            String message = "Invalid integer tokens found. Token was: " + constantsTag + ":" + value
                    + "\n\n" + e.getMessage();
            logger.error(message);
            throw new InvalidTokenFoundDuringParse(message);
        }

        /*
         * Switch statement for monster generation
         */
        switch (name) {
            case "chance":
                setChance(val, name);
                break;

            case "level-min":
                setLevelMin(val, name);
                break;

            case "town-day":
                setTownDay(val, name);
                break;

            case "town-night":
                setTownNight(val, name);
                break;

            case "repro-max":
                setReproMax(val, name);
                break;

            case "ood-chance":
                setOodChance(val, name);
                break;

            case "ood-amount":
                setOodAmount(val, name);
                break;

            case "group-max":
                setGroupMax(val, name);
                break;

            case "group-dist":
                setGroupDist(val, name);
                break;

            default:
                String msg = "Invalid token found in constants.txt file. Input was " + constantsTag + ":" + value;
                logger.error(msg);
                throw new InvalidTokenFoundDuringParse(msg);
        }
    }

    /**
     * Validate and store the per-level monster minimum. Rejects values {@code <= 0}.
     *
     * @param value the proposed minimum
     * @param tag   the constant name, used only for error reporting
     * @throws InvalidTokenFoundDuringParse if {@code value <= 0}
     * @author ClaudeCode
     */
    private static void setLevelMin(int value, @NotNull String tag) throws InvalidTokenFoundDuringParse {
        if (value <= 0) {
            String message = "Invalid switch value found in constants.txt file. Input was " + constantsTag + ":" + tag + ":" + value;
            logger.error(message);
            throw new InvalidTokenFoundDuringParse(message);
        }

        levelMin = value;
    }

    /**
     * Get the minimum number of monsters generated on a level
     *
     * @return the minimum number of monsters generated on a level as determined by the constants.txt file
     */
    @CheckReturnValue
    @Contract(pure = true)
    public static int getLevelMonsterMin() {
        return levelMin;
    }

    /**
     * Validate and store the daytime town population. Must be between 0 and the
     * per-level monster maximum.
     *
     * @param value the proposed count
     * @param tag   the constant name, used only for error reporting
     * @throws InvalidTokenFoundDuringParse if out of the allowed range
     * @author ClaudeCode
     */
    private static void setTownDay(int value, @NotNull String tag) throws InvalidTokenFoundDuringParse {
        if (value < 0 || value > LevelMaxConstants.getMaxMonstersPerLevel()) {
            String message = "Invalid integer value found in constants.txt file, should have been between 1 and "
                    + LevelMaxConstants.getMaxMonstersPerLevel()
                    + ". Input was " + constantsTag + ":" + tag + ":" + value;
            logger.error(message);
            throw new InvalidTokenFoundDuringParse(message);
        }

        townDay = value;
    }

    /**
     * Return the value of mon-gen:town-day in the constants.txt file
     *
     * @return The number of townsfolk generated in the day
     */
    @Contract(pure = true)
    public static int getTownMonstersDay() {
        return townDay;
    }

    /**
     * Validate and store the night-time town population. Must be between 0 and
     * the per-level monster maximum.
     *
     * @param value the proposed count
     * @param tag   the constant name, used only for error reporting
     * @throws InvalidTokenFoundDuringParse if out of the allowed range
     * @author ClaudeCode
     */
    private static void setTownNight(int value, @NotNull String tag) throws InvalidTokenFoundDuringParse {
        if (value < 0 || value > LevelMaxConstants.getMaxMonstersPerLevel()) {
            String message = "Invalid switch value found in constants.txt file. Value was " + value
                    + " should have been between 1 and "
                    + LevelMaxConstants.getMaxMonstersPerLevel()
                    + ". Input was " + constantsTag + ":" + tag + ":" + value;
            logger.error(message);
            throw new InvalidTokenFoundDuringParse(message);
        }

        townNight = value;
    }

    /**
     * Return the value of mon-gen:town-night in the constants.txt file
     *
     * @return The number of townsfolk generated in the night
     */
    @CheckReturnValue
    @Contract(pure = true)
    public static int getTownMonstersNight() {
        return townNight;
    }

    /**
     * Validate and store the breeding-monster cap. Must be between 0 and the
     * per-level monster maximum.
     *
     * @param value the proposed cap
     * @param tag   the constant name, used only for error reporting
     * @throws InvalidTokenFoundDuringParse if out of the allowed range
     * @author ClaudeCode
     */
    private static void setReproMax(int value, @NotNull String tag) throws InvalidTokenFoundDuringParse {
        if (value < 0 || value > LevelMaxConstants.getMaxMonstersPerLevel()) {
            String message = "Invalid switch value found in constants.txt file. Value was " + value
                    + " should have been between 1 and "
                    + LevelMaxConstants.getMaxMonstersPerLevel()
                    + ". Input was " + constantsTag + ":" + tag + ":" + value;
            logger.error(message);
            throw new InvalidTokenFoundDuringParse(message);
        }

        reproMax = value;
    }

    /**
     * Get the maximum number of breeding monsters allowed on a level
     *
     * @return the maximum number of breeding monsters allowed on a level
     */
    @CheckReturnValue
    @Contract(pure = true)
    public static int getReproMax() {
        return reproMax;
    }

    /**
     * Validate and store the out-of-depth chance. Rejects values {@code <= 0}.
     *
     * @param value the proposed 1-in-N chance
     * @param tag   the constant name, used only for error reporting
     * @throws InvalidTokenFoundDuringParse if {@code value <= 0}
     * @author ClaudeCode
     */
    private static void setOodChance(int value, @NotNull String tag) throws InvalidTokenFoundDuringParse {
        if (value <= 0) {
            String message = "Invalid switch value found in constants.txt file. Value was " + value
                    + " should have been greater or equal to 0. Input was " + constantsTag + ":" + tag + ":" + value;
            logger.error(message);
            throw new InvalidTokenFoundDuringParse(message);
        }

        oodChance = value;
    }

    /**
     * Get the chance that a monster will be found out of its normal depth
     *
     * @return The 1-in-value that a monster will be found out of its normal depth
     */
    @CheckReturnValue
    @Contract(pure = true)
    public static int getOodChance() {
        return oodChance;
    }

    /**
     * Validate and store the out-of-depth amount. Rejects values {@code <= 0}.
     *
     * @param value the proposed maximum levels out of depth
     * @param tag   the constant name, used only for error reporting
     * @throws InvalidTokenFoundDuringParse if {@code value <= 0}
     * @author ClaudeCode
     */
    private static void setOodAmount(int value, @NotNull String tag) throws InvalidTokenFoundDuringParse {
        if (value <= 0) {
            String message = "Invalid switch value found in constants.txt file. Value was " + value
                    + " should have been greater or equal to 0. Input was " + constantsTag + ":" + tag + ":" + value;
            logger.error(message);
            throw new InvalidTokenFoundDuringParse(message);
        }

        oodAmount = value;
    }

    /**
     * Get the maximum out-of-depth amount for monster generation
     *
     * @return The maximum out-of-depth a monster can be generated
     */
    @CheckReturnValue
    @Contract(pure = true)
    public static int getOodAmount() {
        return oodAmount;
    }

    /**
     * Validate and store the maximum group size. Must be between 1 and the
     * per-level monster maximum.
     *
     * @param value the proposed group size
     * @param tag   the constant name, used only for error reporting
     * @throws InvalidTokenFoundDuringParse if out of the allowed range
     * @author ClaudeCode
     */
    private static void setGroupMax(int value, @NotNull String tag) throws InvalidTokenFoundDuringParse {
        if (value <= 0 || value > LevelMaxConstants.getMaxMonstersPerLevel()) {
            String message = "Invalid switch value found in constants.txt file. Value was " + value
                    + " should have been between 1 and "
                    + LevelMaxConstants.getMaxMonstersPerLevel()
                    + ". Input was " + constantsTag + ":" + tag + ":" + value;
            logger.error(message);
            throw new InvalidTokenFoundDuringParse(message);
        }

        groupMax = value;
    }

    /**
     * Get the maximum number of monsters in a group
     *
     * @return the maximum allowable monsters in a group
     */
    @CheckReturnValue
    @Contract(pure = true)
    public static int getGroupMax() {
        return groupMax;
    }

    /**
     * Validate and store the maximum inter-group distance. Rejects values {@code <= 0}.
     *
     * @param value the proposed distance
     * @param tag   the constant name, used only for error reporting
     * @throws IllegalArgumentException if {@code value <= 0}
     * @author ClaudeCode
     */
    private static void setGroupDist(int value, @NotNull String tag) {
        if (value <= 0) {
            String message = "Invalid switch value found in constants.txt file. Value was " + value
                    + " should have been greater or equal to 0. Input was " + constantsTag + ":" + tag + ":" + value;
            logger.error(message);
            throw new IllegalArgumentException(message);
        }

        groupDist = value;
    }

    /**
     * Gets the maximum distance a group of monsters can generate from a related group of monsters
     *
     * @return the maximum distance a group of monsters can generate from a related group of monsters
     */
    @CheckReturnValue
    @Contract(pure = true)
    public static int getGroupDist() {
        return groupDist;
    }
}