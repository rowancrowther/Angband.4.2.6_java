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

package uk.co.jackoftrades.backend.utils.globalvalues;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftrades.backend.io.bespokeexceptions.InvalidTokenFoundDuringParse;

/**
 * Holds the "mon-play" group of constants from {@code constants.txt} — runtime
 * monster behaviour tuning (glyph hardness, breeding rate, life-drain strength,
 * and the flee/turn distances that govern fear behaviour). Part of the Java port
 * of the C constants loader; {@link #setValue(String)} decodes one
 * {@code name:value} line and routes it to a validating setter.
 *
 * @author Rowan Crowther
 */
public class MonsterGameplayConstants {
    /**
     * The data-file group tag this class consumes ({@code mon-play}).
     *
     * @author Rowan Crowther
     */
    private final static String constantsTag = "mon-play";
    /**
     * Resistance of a glyph of protection to being broken by a monster.
     *
     * @author Rowan Crowther
     */
    private static int glyphHardness;
    /**
     * Breeding-rate control; higher values mean slower monster reproduction.
     *
     * @author Rowan Crowther
     */
    private static int reproMonsterRate;
    /**
     * Percentage of the player's experience drained per life-drain hit.
     *
     * @author Rowan Crowther
     */
    private static int lifeDrainPercent;
    /**
     * Distance out of the player's sight that terrified monsters will run.
     *
     * @author Rowan Crowther
     */
    private static int fleeRange;
    /**
     * Distance from the player at which a slower terrified monster turns to fight.
     *
     * @author Rowan Crowther
     */
    private static int turnRange;
    /**
     * Logger used to report malformed/invalid constants during parsing.
     *
     * @author Rowan Crowther
     */
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

    /**
     * Validate and store the glyph hardness. Rejects values {@code <= 0}.
     *
     * @param value the proposed hardness
     * @param tag   the constant name, used only for error reporting
     * @throws InvalidTokenFoundDuringParse if {@code value <= 0}
     * @author Rowan Crowther
     */
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


    /**
     * Validate and store the breeding-rate control. Rejects values {@code <= 0}.
     *
     * @param value the proposed rate
     * @param tag   the constant name, used only for error reporting
     * @throws InvalidTokenFoundDuringParse if {@code value <= 0}
     * @author Rowan Crowther
     */
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

    /**
     * Validate and store the life-drain percentage. Rejects values {@code <= 0}.
     *
     * @param value the proposed percentage
     * @param tag   the constant name, used only for error reporting
     * @throws InvalidTokenFoundDuringParse if {@code value <= 0}
     * @author Rowan Crowther
     */
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

    /**
     * Validate and store the flee range. Rejects values {@code <= 0}.
     *
     * @param value the proposed range
     * @param tag   the constant name, used only for error reporting
     * @throws InvalidTokenFoundDuringParse if {@code value <= 0}
     * @author Rowan Crowther
     */
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

    /**
     * Validate and store the turn-and-fight range. Rejects values {@code <= 0}.
     *
     * @param value the proposed range
     * @param tag   the constant name, used only for error reporting
     * @throws InvalidTokenFoundDuringParse if {@code value <= 0}
     * @author Rowan Crowther
     */
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