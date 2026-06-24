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
import uk.co.jackoftrades.backend.io.bespokeexceptions.InvalidTokenFoundDuringParse;

/**
 * Holds the "player" group of constants from {@code constants.txt} — core player
 * limits: sight radius, missile/spell range, starting wealth and food value.
 * Part of the Java port of the C constants loader; {@link #setValue(String)}
 * decodes one {@code name:value} line and routes it to a validating setter.
 *
 * @author ClaudeCode
 */
public class PlayerConstants {
    /**
     * The data-file group tag this class consumes ({@code player}).
     *
     * @author ClaudeCode
     */
    private final static String tag = "player";

    /**
     * Maximum visual range of the player, in grids.
     *
     * @author ClaudeCode
     */
    private static int maxSight = 0;
    /**
     * Maximum missile and spell range of the player, in grids.
     *
     * @author ClaudeCode
     */
    private static int maxRange = 0;
    /**
     * Starting gold (or equivalent equipment value) for a new player.
     *
     * @author ClaudeCode
     */
    private static int startGold = 0;
    /**
     * Number of turns that 1% of the player's food capacity sustains them.
     *
     * @author ClaudeCode
     */
    private static int foodValue = 0;

    /**
     * Logger used to report malformed/invalid constants during parsing.
     *
     * @author ClaudeCode
     */
    private final static Logger logger = LogManager.getLogger();

    /**
     * Parse and store a single {@code player} constant from the data file
     * ({@code name:integer}), routing it to the matching setter.
     *
     * @param value the raw {@code name:value} token from {@code constants.txt}
     * @throws InvalidTokenFoundDuringParse if the token is malformed, the integer
     *                                      cannot be parsed, or the name is unrecognised
     * @author ClaudeCode
     */
    public static void setValue(String value) throws InvalidTokenFoundDuringParse {
        String[] values = value.split(":");

        if (values.length != 2) {
            String message = "Invalid number of tokens incoming from constants.txt. Value was: "
                    + tag + ":" + value;
            InvalidTokenFoundDuringParse e = new InvalidTokenFoundDuringParse(message);
            logger.error(message, e);
            throw e;
        }

        String name = values[0];
        int val;

        try {
            val = Integer.parseInt(values[1]);
        } catch (NumberFormatException e) {
            String message = "Invalid number format found in input string from constants.txt. Token was: "
                    + tag + ":" + value + "\n\n" + e.getMessage();
            logger.error(message, e);
            throw e;
        }

        switch (name) {
            case "max-sight":
                setMaxSight(val, name);
                break;

            case "max-range":
                setMaxRange(val, name);
                break;

            case "start-gold":
                setStartGold(val, name);
                break;

            case "food-value":
                setFoodValue(val, name);
                break;

            default:
                String message = "Unknown value imported from constants.txt. Token was: "
                        + tag + ":" + value;
                InvalidTokenFoundDuringParse e = new InvalidTokenFoundDuringParse(message);
                logger.error(message, e);
                throw e;
        }
    }

    /**
     * Get the maximum visual range of the player
     *
     * @return the maximum visual range of the player
     */
    @CheckReturnValue
    @Contract(pure = true)
    public static int getMaxSight() {
        return maxSight;
    }

    /**
     * Validate and store the player sight range. Rejects values {@code <= 0}.
     *
     * @param maxSight the proposed sight range
     * @param name     the constant name, used only for error reporting
     * @throws InvalidTokenFoundDuringParse if {@code maxSight <= 0}
     * @author ClaudeCode
     */
    private static void setMaxSight(int maxSight, String name) throws InvalidTokenFoundDuringParse {
        if (maxSight <= 0) {
            String message = "Invalid maximum player sight range. Token was: " + tag + ":" + name + ":" + maxSight;
            InvalidTokenFoundDuringParse e = new InvalidTokenFoundDuringParse(message);
            logger.error(message, e);
            throw e;
        }

        PlayerConstants.maxSight = maxSight;
    }

    /**
     * Get the maximum missile and spell range of the player
     *
     * @return the maximum missile and spell range of the player
     */
    @CheckReturnValue
    @Contract(pure = true)
    public static int getMaxRange() {
        return maxRange;
    }

    /**
     * Validate and store the player missile/spell range. Rejects values {@code <= 0}.
     *
     * @param maxRange the proposed range
     * @param name     the constant name, used only for error reporting
     * @throws InvalidTokenFoundDuringParse if {@code maxRange <= 0}
     * @author ClaudeCode
     */
    private static void setMaxRange(int maxRange, String name) throws InvalidTokenFoundDuringParse {
        if (maxRange <= 0) {
            String message = "Invalid maximum player missile/spell range. Token was: " + tag + ":" + name + ":" + maxRange;
            InvalidTokenFoundDuringParse e = new InvalidTokenFoundDuringParse(message);
            logger.error(message, e);
            throw e;
        }

        PlayerConstants.maxRange = maxRange;
    }

    /**
     * Get the starting amount of gold or value of equipment that the player has
     *
     * @return the starting amount of gold that the player has
     */
    @CheckReturnValue
    @Contract(pure = true)
    public static int getStartGold() {
        return startGold;
    }

    /**
     * Validate and store the starting gold. Rejects values {@code <= 0}.
     *
     * @param startGold the proposed starting gold
     * @param name      the constant name, used only for error reporting
     * @throws InvalidTokenFoundDuringParse if {@code startGold <= 0}
     * @author ClaudeCode
     */
    private static void setStartGold(int startGold, String name) throws InvalidTokenFoundDuringParse {
        if (startGold <= 0) {
            String message = "Invalid player starting gold/equipment amount. Token was: " + tag + ":" + name + ":"
                    + startGold;
            InvalidTokenFoundDuringParse e = new InvalidTokenFoundDuringParse(message);
            logger.error(message, e);
            throw e;
        }

        PlayerConstants.startGold = startGold;
    }

    /**
     * Get the number of turns that 1% of player food capacity feeds them for
     *
     * @return the player food capacity duration
     */
    @CheckReturnValue
    @Contract(pure = true)
    public static int getFoodValue() {
        return foodValue;
    }

    /**
     * Validate and store the food value. Rejects values {@code <= 0}.
     *
     * @param foodValue the proposed food value
     * @param name      the constant name, used only for error reporting
     * @throws InvalidTokenFoundDuringParse if {@code foodValue <= 0}
     * @author ClaudeCode
     */
    private static void setFoodValue(int foodValue, String name) throws InvalidTokenFoundDuringParse {
        if (foodValue <= 0) {
            String message = "Invalid player food turns. Token was: " + tag + ":" + name + ":" + foodValue;
            logger.error(message);
            throw new InvalidTokenFoundDuringParse(message);
        }

        PlayerConstants.foodValue = foodValue;
    }
}