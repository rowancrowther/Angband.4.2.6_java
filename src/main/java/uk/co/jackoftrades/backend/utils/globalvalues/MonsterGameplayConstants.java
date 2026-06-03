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

public class MonsterGameplayConstants {
    private final static String constantsTag = "mon-play";
    private static int glyphHardness;
    private static int reproMonsterRate;
    private static int lifeDrainPercent;
    private static int fleeRange;
    private static int turnRange;
    private final static Logger logger = LogManager.getLogger();

    /**
     * Receive a text:value pair in a string, and store in the correct field
     *
     * @param value the text to parse down into a string and value, and store the value in the correct field as denoted
     *              by the string
     */
    public static void setValue(@NotNull String value) throws InvalidTokenFoundDuringParse {
        String[] results = value.split(":");

        if (results.length != 2) {
            String message = "Invalid number of tokens found. Token was: " + constantsTag + ":" + value;
            InvalidTokenFoundDuringParse e = new InvalidTokenFoundDuringParse(message);
            logger.error(message, e);
            throw e;
        }

        String name = results[0];
        int val;

        try {
            val = Integer.parseInt(results[1]);
        } catch (NumberFormatException e) {
            String message = "Invalid integer tokens found. Token was: " + constantsTag + ":" + value
                    + "\n\n" + e.getMessage();
            logger.error(message, e);
            throw e;
        }

        /*
         * Switch statement for monster generation
         */
        switch (name) {
            case "break-glyph":
                setGlyphHardness(val, name);
                break;

            case "mult-rate":
                setReproMonsterRate(val, name);
                break;

            case "life-drain":
                setLifeDrainPercent(val, name);
                break;

            case "flee-range":
                setFleeRange(val, name);
                break;

            case "turn-range":
                setTurnRange(val, name);
                break;

            default:
                String message = "Invalid switch found in constants.txt file. Input was " + constantsTag + ":" + value;
                InvalidTokenFoundDuringParse e = new InvalidTokenFoundDuringParse(message);
                logger.error(message, e);
                throw e;
        }
    }

    private static void setGlyphHardness(int value, String tag) throws InvalidTokenFoundDuringParse {
        if (value <= 0) {
            String message = "Invalid switch value found in constants.txt file. Input was "
                    + constantsTag + ":" + tag + ":" + value;
            InvalidTokenFoundDuringParse e = new InvalidTokenFoundDuringParse(message);
            logger.error(message, e);
            throw e;
        }

        glyphHardness = value;
    }

    /**
     * Get the resistance of a glyph of protection to being broken by a monster
     *
     * @return the resistance of a glyph of protection to being broken by a monster
     */
    @CheckReturnValue
    @Contract(pure = true)
    public static int getGlyphHardness() {
        return glyphHardness;
    }


    private static void setReproMonsterRate(int value, String tag) throws InvalidTokenFoundDuringParse {
        if (value <= 0) {
            String message = "Invalid switch value found in constants.txt file. Input was " + constantsTag
                    + ":" + tag + ":" + value;
            logger.error(message);
            throw new InvalidTokenFoundDuringParse(message);
        }

        reproMonsterRate = value;
    }

    /**
     * Get value relating to breeding - higher values mean slower breeding
     * @return value relating to breeding
     */
    @CheckReturnValue
    @Contract(pure = true)
    public static int getReproMonsterRate() {
        return reproMonsterRate;
    }

    private static void setLifeDrainPercent(int value, String tag) throws InvalidTokenFoundDuringParse {
        if (value <= 0) {
            String message = "Invalid switch value found in constants.txt file. Input was "
                    + constantsTag + ":" + tag + ":" + value;
            logger.error(message);
            throw new InvalidTokenFoundDuringParse(message);
        }

        lifeDrainPercent = value;
    }

    /**
     * Get percentage of player's experience drained per life drain hit
     * @return percentage of player's experience
     */
    @CheckReturnValue
    @Contract(pure = true)
    public static int getLifeDrainPercent() {
        return lifeDrainPercent;
    }

    private static void setFleeRange(int value, String tag) throws InvalidTokenFoundDuringParse {
        if (value <= 0) {
            String message = "Invalid switch value found in constants.txt file. Input was "
                    + constantsTag + ":" + tag + ":" + value;
            logger.error(message);
            throw new InvalidTokenFoundDuringParse(message);
        }

        fleeRange = value;
    }

    /**
     * Gets the distance out of sight of the player that terrified monsters will run
     * @return the distance out of sight of the player that terrified monsters will run
     */
    @CheckReturnValue
    @Contract(pure = true)
    public static int getFleeRange() {
        return fleeRange;
    }

    private static void setTurnRange(int value, String tag) throws InvalidTokenFoundDuringParse {
        if (value <= 0) {
            String message = "Invalid switch value found in constants.txt file. Input was "
                    + constantsTag + ":" + tag + ":" + value;
            logger.error(message);
            throw new InvalidTokenFoundDuringParse(message);
        }

        turnRange = value;
    }

    /**
     * Gets the distance from the player that a terrified monster will turn and fight if they are slower than the player
     * @return the distance from the player that a terrified monster will turn and fight if they are slower than the
     * player
     */
    @CheckReturnValue
    @Contract(pure = true)
    public static int getTurnRange() {
        return turnRange;
    }
}