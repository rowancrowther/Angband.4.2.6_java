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
 * Holds the "world" group of tunable constants loaded from {@code constants.txt}
 * — dungeon/town geometry, day length, level-feeling tuning and movement energy.
 * Part of the Java port of the C constants loader: each value is a {@code static}
 * global and {@link #setValue(String)} is the parser callback that decodes one
 * {@code name:value} line and routes it to a validating setter.
 *
 * @author ClaudeCode
 */
public class GameWorldConstants {
    /**
     * The data-file group tag this class consumes ({@code world}).
     *
     * @author ClaudeCode
     */
    private final static String tag = "world";
    /**
     * Maximum dungeon level; must be at least 100 (below 128 can suppress some objects).
     *
     * @author ClaudeCode
     */
    private static int maxDepth;
    /**
     * Length of a full day (dawn to dawn) measured in game turns.
     *
     * @author ClaudeCode
     */
    private static int dayLength;
    /**
     * Dungeon level height in grid rows.
     *
     * @author ClaudeCode
     */
    private static int dungeonHgt;
    /**
     * Dungeon level width in grid columns.
     *
     * @author ClaudeCode
     */
    private static int dungeonWid;
    /**
     * Town map height in grid rows.
     *
     * @author ClaudeCode
     */
    private static int townHgt;
    /**
     * Town map width in grid columns.
     *
     * @author ClaudeCode
     */
    private static int townWid;
    /**
     * Total number of "feeling" squares per level used to gauge level danger/treasure.
     *
     * @author ClaudeCode
     */
    private static int feelingTotal;
    /**
     * Number of squares the player must explore before the first level feeling appears.
     *
     * @author ClaudeCode
     */
    private static int feelingNeed;
    /**
     * Number of dungeon levels skipped per staircase descent.
     *
     * @author ClaudeCode
     */
    private static int stairSkip;
    /**
     * Energy required for a standard move by the player or a monster.
     *
     * @author ClaudeCode
     */
    private static int moveEnergy;
    /**
     * Logger used to report malformed/invalid constants during parsing.
     *
     * @author ClaudeCode
     */
    private final static Logger logger = LogManager.getLogger();

    /**
     * Parse and store a single {@code world} constant from the data file.
     * The value is expected as {@code name:integer}; the name selects the target
     * constant and each is validated before storage.
     *
     * @param value the raw {@code name:value} token from {@code constants.txt}
     * @throws InvalidTokenFoundDuringParse if the token is malformed, the integer
     *                                      cannot be parsed, or the name is unrecognised
     * @author ClaudeCode
     */
    public static void setValue(@NotNull String value) throws InvalidTokenFoundDuringParse {
        String[] results = value.split(":");
        int val;

        // Check for correct string type
        if (results.length != 2) {
            String message = "Invalid number of tokens in Constants.txt file. Token was " + tag + ":" + value;
            InvalidTokenFoundDuringParse e = new InvalidTokenFoundDuringParse(message);
            logger.error(message, e);
            throw e;
        }

        try {
            val = Integer.parseInt(results[1]);
        } catch (NumberFormatException e) {
            String message = "Poorly formatted integer in incoming token. Token was " + tag + ":" + value;
            logger.error(message, e);
            throw e;
        }

        String name = results[0];

        switch (name) {
            case "max-depth":
                setMaxDepth(val, name);
                break;

            case "day-length":
                setDayLength(val, name);
                break;

            case "dungeon-hgt":
                setDungeonHgt(val, name);
                break;

            case "dungeon-wid":
                setDungeonWid(val, name);
                break;

            case "town-hgt":
                setTownHgt(val, name);
                break;

            case "town-wid":
                setTownWid(val, name);
                break;

            case "feeling-total":
                setFeelingTotal(val, name);
                break;

            case "feeling-need":
                setFeelingNeed(val, name);
                break;

            case "stair-skip":
                setStairSkip(val, name);
                break;

            case "move-energy":
                setMoveEnergy(val, name);
                break;

            default:
                String message = "Invalid switch found in constants.txt file. Input was " + tag + ":" + value;
                InvalidTokenFoundDuringParse e = new InvalidTokenFoundDuringParse(message);
                logger.error(message, e);
                throw e;
        }
    }

    /**
     * Get the maximum dungeon level
     * This must be at least 100 and setting it below 128 may prevent the creation of some objects
     *
     * @return the maximum dungeon level
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getMaxDepth() {
        return maxDepth;
    }

    /**
     * Validate and store the maximum dungeon depth. Rejects values below 100
     * (the game's hard minimum) and warns below 128, where some objects can no
     * longer be generated.
     *
     * @param maxDepth the proposed maximum depth
     * @param name     the constant name, used only for error reporting
     * @throws InvalidTokenFoundDuringParse if {@code maxDepth < 100}
     * @author ClaudeCode
     */
    private static void setMaxDepth(int maxDepth, String name) throws InvalidTokenFoundDuringParse {
        if (maxDepth < 100) {
            String message = "Dungeon depth set in constants.txt to be less than 100. This value should be at least 100."
                    + " Value was " + tag + ":" + name + ":" + maxDepth;
            InvalidTokenFoundDuringParse e = new InvalidTokenFoundDuringParse(message);
            logger.error(message, e);
            throw e;
        } else if (maxDepth < 128) {
            String message = "Dungeon depth set in constants.txt to be less than 128."
                    + "This value may cause some objects not to be created. Value was "
                    + tag + ":" + name + ":" + maxDepth;
            logger.warn(message);
        }

        GameWorldConstants.maxDepth = maxDepth;
    }

    /**
     * Get the length of the day (dawn to dawn) in turns
     *
     * @return the length of the day in turns
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getDayLength() {
        return dayLength;
    }

    /**
     * Store the day length. Warns (but does not reject) when the value is odd,
     * since an even number of turns avoids rounding issues in day/night logic.
     *
     * @param dayLength the proposed day length in turns
     * @param name      the constant name, used only for error reporting
     * @author ClaudeCode
     */
    private static void setDayLength(int dayLength, String name) {
        if (dayLength % 2 != 0) {
            String message = "Number of turns for a day is not even. This may cause issues in the code. Value was: "
                    + tag + ":" + name + ":" + dayLength;
            logger.warn(message);
        }

        GameWorldConstants.dayLength = dayLength;
    }

    /**
     * Get the number of vertical lines (x) in the grid for each dungeon level
     *
     * @return the number of vertical lines (x) in the grid for each dungeon level
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getDungeonHgt() {
        return dungeonHgt;
    }

    /**
     * Validate and store the dungeon height. Rejects values below 1.
     *
     * @param dungeonHgt the proposed dungeon height in rows
     * @param name       the constant name, used only for error reporting
     * @throws InvalidTokenFoundDuringParse if {@code dungeonHgt < 1}
     * @author ClaudeCode
     */
    private static void setDungeonHgt(int dungeonHgt, String name) throws InvalidTokenFoundDuringParse {
        if (dungeonHgt < 1) {
            String message = "Invalid maximum dungeon height. Token string was: "
                    + tag + ":" + name + ":" + dungeonHgt;
            InvalidTokenFoundDuringParse e = new InvalidTokenFoundDuringParse(message);
            logger.error(message, e);
            throw e;
        }

        GameWorldConstants.dungeonHgt = dungeonHgt;
    }

    /**
     * Get the number of horizontal lines (y) in the grid for each dungeon level
     *
     * @return the number of horizontal lines (y) in the grid for each dungeon level
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getDungeonWid() {
        return dungeonWid;
    }

    /**
     * Validate and store the dungeon width. Rejects values below 1.
     *
     * @param dungeonWid the proposed dungeon width in columns
     * @param name       the constant name, used only for error reporting
     * @throws InvalidTokenFoundDuringParse if {@code dungeonWid < 1}
     * @author ClaudeCode
     */
    private static void setDungeonWid(int dungeonWid, String name) throws InvalidTokenFoundDuringParse {
        if (dungeonWid < 1) {
            String message = "Invalid maximum dungeon width. Token string was: "
                    + tag + ":" + name + ":" + dungeonWid;
            InvalidTokenFoundDuringParse e = new InvalidTokenFoundDuringParse(message);
            logger.error(message, e);
            throw e;
        }

        GameWorldConstants.dungeonWid = dungeonWid;
    }

    /**
     * Get the number of vertical lines (x) in the grid for the town
     *
     * @return the number of vertical lines (x) in the grid for the town
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getTownHgt() {
        return townHgt;
    }

    /**
     * Validate and store the town height. Rejects values below 1.
     *
     * @param townHgt the proposed town height in rows
     * @param name    the constant name, used only for error reporting
     * @throws InvalidTokenFoundDuringParse if {@code townHgt < 1}
     * @author ClaudeCode
     */
    private static void setTownHgt(int townHgt, String name) throws InvalidTokenFoundDuringParse {
        if (townHgt < 1) {
            String message = "Invalid maximum town height. Token string was: "
                    + tag + ":" + name + ":" + townHgt;
            logger.error(message);
            throw new InvalidTokenFoundDuringParse(message);
        }

        GameWorldConstants.townHgt = townHgt;
    }

    /**
     * Get the number of horizontal lines (y) in the grid for the town
     *
     * @return the number of horizontal lines (y) in the grid for the town
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getTownWid() {
        return townWid;
    }

    /**
     * Validate and store the town width. Rejects values below 1.
     *
     * @param townWid the proposed town width in columns
     * @param name    the constant name, used only for error reporting
     * @throws InvalidTokenFoundDuringParse if {@code townWid < 1}
     * @author ClaudeCode
     */
    private static void setTownWid(int townWid, String name) throws InvalidTokenFoundDuringParse {
        if (townWid < 1) {
            String message = "Invalid maximum town width. Token string was: "
                    + tag + ":" + name + ":" + townWid;
            logger.error(message);
            throw new InvalidTokenFoundDuringParse(message);
        }

        GameWorldConstants.townWid = townWid;
    }

    /**
     * Get the total number of feeling squares per level
     *
     * @return the number of feeling squares per level
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getFeelingTotal() {
        return feelingTotal;
    }

    /**
     * Validate and store the total feeling-square count. Rejects values below 1.
     *
     * @param feelingTotal the proposed number of feeling squares per level
     * @param name         the constant name, used only for error reporting
     * @throws InvalidTokenFoundDuringParse if {@code feelingTotal < 1}
     * @author ClaudeCode
     */
    private static void setFeelingTotal(int feelingTotal, String name) throws InvalidTokenFoundDuringParse {
        if (feelingTotal < 1) {
            String message = "Invalid total feeling squares per level. Token string was: "
                    + tag + ":" + name + ":" + feelingTotal;
            logger.error(message);
            throw new InvalidTokenFoundDuringParse(message);
        }

        GameWorldConstants.feelingTotal = feelingTotal;
    }

    /**
     * Get the number of squares the player has to move before the sees the first feeling
     *
     * @return the number of squares the player has to move before the sees the first feeling
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getFeelingNeed() {
        return feelingNeed;
    }

    /**
     * Validate and store the squares-needed-before-feeling value. Rejects values below 1.
     *
     * @param feelingNeed the proposed number of squares to explore first
     * @param name        the constant name, used only for error reporting
     * @throws InvalidTokenFoundDuringParse if {@code feelingNeed < 1}
     * @author ClaudeCode
     */
    private static void setFeelingNeed(int feelingNeed, String name) throws InvalidTokenFoundDuringParse {
        if (feelingNeed < 1) {
            String message = "Invalid squares needed to visit before feeling. Token string was: "
                    + tag + ":" + name + ":" + feelingNeed;
            logger.error(message);

            throw new InvalidTokenFoundDuringParse(message);
        }

        GameWorldConstants.feelingNeed = feelingNeed;
    }

    /**
     * Gets the number of levels each staircase takes the player down
     *
     * @return the number of levels each staircase takes the player down
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getStairSkip() {
        return stairSkip;
    }

    /**
     * Validate and store the stair-skip depth.
     * <p>
     * Note: the guard here checks {@code feelingNeed} rather than
     * {@code stairSkip}, which appears to be a copy-paste carry-over from
     * {@link #setFeelingNeed(int, String)} in the original code.
     *
     * @param stairSkip the proposed number of levels skipped per staircase
     * @param name      the constant name, used only for error reporting
     * @throws InvalidTokenFoundDuringParse if the (feelingNeed) guard fails
     * @author ClaudeCode
     */
    private static void setStairSkip(int stairSkip, String name) throws InvalidTokenFoundDuringParse {
        if (feelingNeed < 1) {
            String message = "Invalid squares needed to visit before feeling. Token string was: "
                    + tag + ":" + name + ":" + stairSkip;
            logger.error(message);
            throw new InvalidTokenFoundDuringParse(message);
        }
        GameWorldConstants.stairSkip = stairSkip;
    }

    /**
     * Gets the energy needed by the player and the monsters to move
     *
     * @return the energy needed by the player and the monsters to move
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getMoveEnergy() {
        return moveEnergy;
    }

    /**
     * Validate and store the per-move energy cost.
     * <p>
     * Note: as with {@link #setStairSkip(int, String)}, the guard checks
     * {@code feelingNeed} rather than {@code moveEnergy} — likely a copy-paste
     * carry-over in the original code.
     *
     * @param moveEnergy the proposed energy required for a standard move
     * @param name       the constant name, used only for error reporting
     * @throws InvalidTokenFoundDuringParse if the (feelingNeed) guard fails
     * @author ClaudeCode
     */
    private static void setMoveEnergy(int moveEnergy, String name) throws InvalidTokenFoundDuringParse {
        if (feelingNeed < 1) {
            String message = "Invalid energy needed to move by player or monster. Token string was: "
                    + tag + ":" + name + ":" + moveEnergy;
            logger.error(message);
            throw new InvalidTokenFoundDuringParse(message);
        }

        GameWorldConstants.moveEnergy = moveEnergy;
    }
}