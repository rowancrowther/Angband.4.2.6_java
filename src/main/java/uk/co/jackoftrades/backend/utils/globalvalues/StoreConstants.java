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
 * Holds the "store" group of constants from {@code constants.txt} — shop tuning:
 * inventory size, restock interval, owner-shuffle chance and the depth at which
 * store goods start gaining magical bonuses. Part of the Java port of the C
 * constants loader; {@link #setValue(String)} decodes one {@code name:value}
 * line and routes it to a validating setter.
 *
 * @author ClaudeCode
 */
public class StoreConstants {
    /**
     * The data-file group tag this class consumes ({@code store}).
     *
     * @author ClaudeCode
     */
    private static final String tag = "store";

    /**
     * Maximum number of distinct items a store may hold.
     *
     * @author ClaudeCode
     */
    private static int invenMax;
    /**
     * Number of game turns between store inventory turnovers.
     *
     * @author ClaudeCode
     */
    private static int turns;
    /**
     * 1-in-{@code shuffle} chance per day that a store's owner changes.
     *
     * @author ClaudeCode
     */
    private static int shuffle;
    /**
     * Dungeon level after which normal-store goods can gain magical bonuses.
     *
     * @author ClaudeCode
     */
    private static int magicLevel;

    /**
     * Logger used to report malformed/invalid constants during parsing.
     *
     * @author ClaudeCode
     */
    private static final Logger logger = LogManager.getLogger();

    /**
     * Parse and store a single {@code store} constant from the data file
     * ({@code name:integer}), routing it to the matching setter.
     *
     * @param value the raw {@code name:value} token from {@code constants.txt}
     * @throws InvalidTokenFoundDuringParse if the token is malformed, the integer
     *                                      cannot be parsed, or the name is unrecognised
     * @author ClaudeCode
     */
    public static void setValue(@NotNull String value) throws InvalidTokenFoundDuringParse {
        String[] values = value.split(":");

        if (values.length != 2) {
            String message = "Invalid number of tokens in incoming value from constants.txt. Token was: "
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
            String message = "Invalid integer value found in constants.txt. Token was: "
                    + tag + ":" + value + "\n\n" + e.getMessage();
            logger.error(message, e);
            throw e;
        }

        switch (name) {
            case "inven-max":
                setInvenMax(val, name);
                break;

            case "turns":
                setTurns(val, name);
                break;

            case "shuffle":
                setShuffle(val, name);
                break;

            case "magic-level":
                setMagicLevel(val, name);
                break;

            default:
                String message = "Invalid token found when parsing constants.txt. Token was: "
                        + tag + ":" + name + ":" + val;
                InvalidTokenFoundDuringParse e = new InvalidTokenFoundDuringParse(message);
                logger.error(message, e);
                throw e;
        }
    }

    /**
     * Get the maximum number of discrete items in a store's inventlry
     *
     * @return the maximum number of discrete items in a store's inventlry
     */
    @CheckReturnValue
    @Contract(pure = true)
    public static int getInvenMax() {
        return invenMax;
    }

    /**
     * Validate and store the store inventory size. Rejects values below 1.
     *
     * @param invenMax the proposed maximum item count
     * @param name     the constant name, used only for error reporting
     * @throws InvalidTokenFoundDuringParse if {@code invenMax < 1}
     * @author ClaudeCode
     */
    private static void setInvenMax(int invenMax, String name) throws InvalidTokenFoundDuringParse {
        if (invenMax < 1) {
            String message = "Invalid number of store's inventory items from constants.txt. Token was: "
                    + tag + ":" + name + ":" + invenMax;
            InvalidTokenFoundDuringParse e = new InvalidTokenFoundDuringParse(message);
            logger.error(message, e);
            throw e;
        }
        StoreConstants.invenMax = invenMax;
    }

    /**
     * Gets the number of turns between store's inventory turnovers
     *
     * @return the number of turns between store's inventory turnovers
     */
    @CheckReturnValue
    @Contract(pure = true)
    public static int getTurns() {
        return turns;
    }

    /**
     * Validate and store the restock interval. Rejects values below 1.
     *
     * @param turns the proposed turns between turnovers
     * @param name  the constant name, used only for error reporting
     * @throws InvalidTokenFoundDuringParse if {@code turns < 1}
     * @author ClaudeCode
     */
    private static void setTurns(int turns, String name) throws InvalidTokenFoundDuringParse {
        if (turns < 1) {
            String message = "Invalid number of turns between store's turnovers. Token was: "
                    + tag + ":" + name + ":" + turns;
            InvalidTokenFoundDuringParse e = new InvalidTokenFoundDuringParse(message);
            logger.error(message, e);
            throw e;
        }

        StoreConstants.turns = turns;
    }

    /**
     * Get the 1/chance per day of an owner changing
     *
     * @return the 1/chance per day of an owner changing
     */
    @CheckReturnValue
    @Contract(pure = true)
    public static int getShuffle() {
        return shuffle;
    }

    /**
     * Validate and store the owner-shuffle chance. Rejects values below 1.
     *
     * @param shuffle the proposed 1-in-N daily chance
     * @param name    the constant name, used only for error reporting
     * @throws InvalidTokenFoundDuringParse if {@code shuffle < 1}
     * @author ClaudeCode
     */
    private static void setShuffle(int shuffle, String name) throws InvalidTokenFoundDuringParse {
        if (shuffle < 1) {
            String message = "Invalid number 1/chance of when reading constants.txt. Token was: "
                    + tag + ":" + name + ":" + shuffle;
            InvalidTokenFoundDuringParse e = new InvalidTokenFoundDuringParse(message);
            logger.error(message, e);
            throw e;
        }

        StoreConstants.shuffle = shuffle;
    }

    /**
     * Gets the dungeon level required before objects in normal stores gain magical bonuses
     *
     * @return the dungeon level required before objects in normal stores gain magical bonuses
     */
    @CheckReturnValue
    @Contract(pure = true)
    public static int getMagicLevel() {
        return magicLevel;
    }

    /**
     * Validate and store the magic-level threshold. Rejects values below 1
     * (dungeon levels are 1-based).
     *
     * @param magicLevel the proposed threshold level
     * @param name       the constant name, used only for error reporting
     * @throws InvalidTokenFoundDuringParse if {@code magicLevel < 1}
     * @author ClaudeCode
     */
    private static void setMagicLevel(int magicLevel, String name) throws InvalidTokenFoundDuringParse {
        // dungeon levels start at 1
        if (magicLevel < 1) {
            String message = "Invalid dungeon level required for magic in mormal stores. Token was: "
                    + tag + ":" + name + ":" + magicLevel;
            InvalidTokenFoundDuringParse e = new InvalidTokenFoundDuringParse(message);
            logger.error(message, e);
            throw e;
        }

        StoreConstants.magicLevel = magicLevel;
    }
}