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
 * Holds the "level-max" group of constants from {@code constants.txt}. Currently
 * just the per-level monster cap, but kept as a switch-driven loader (like its
 * sibling constant classes) so further per-level maxima can be added without
 * restructuring. Part of the Java port of the C constants loader.
 *
 * @author Rowan Crowther
 */
public class LevelMaxConstants {
    /**
     * The data-file group tag this class consumes ({@code level-max}).
     *
     * @author Rowan Crowther
     */
    private final static String constantsTag = "level-max";
    /**
     * Maximum number of monsters allowed on a single level.
     *
     * @author Rowan Crowther
     */
    private static int monsters;
    /**
     * Logger used to report malformed/invalid constants during parsing.
     *
     * @author Rowan Crowther
     */
    private static final Logger logger = LogManager.getLogger();

    /**
     * Private constructor - only has static variables on it
     */
    @Contract(pure = true)
    private LevelMaxConstants() {
    }

    /**
     * Get the maximum number of allowed monsters per level
     *
     * @return level-max:monsters as read from the constants.txt file.
     */
    @CheckReturnValue
    @Contract(pure = true)
    public static int getMaxMonstersPerLevel() {
        return monsters;
    }

    /**
     * Handle the input of a string with a name:value format.
     * This is the level maxima version of this, so there should only be one line
     *
     * @param value a string of the format name:value
     */
    public static void setValue(@NotNull String value) throws InvalidTokenFoundDuringParse {
        String[] results = value.split(":");
        int val = 0;

        // Check for correct string type
        if (results.length != 2) {
            String message = "Invalid number of arguments found in incoming value. Value was: " + constantsTag + ":" + value;
            InvalidTokenFoundDuringParse e = new InvalidTokenFoundDuringParse(message);
            logger.error(message, e);
            throw e;
        } else {
            try {
                val = Integer.parseInt(results[1]);
            } catch (NumberFormatException e) {
                String message = "Poorly formatted integer in incoming token. Token was " + constantsTag + ":" + value;
                logger.error(message, e);
                throw e;
            }
        }

        String name = results[0];

        /*
         * Left as switch statement to allow increase of inputs
         */
        switch (name) {
            case "monsters":
                setMonsters(val);
                return;

            default:
                String message = "Invalid switch found in constants.txt file. Input was " + constantsTag + ":" + name + ":" + val;
                InvalidTokenFoundDuringParse e = new InvalidTokenFoundDuringParse(message);
                logger.error(message, e);
                throw e;
        }
    }

    /**
     * Validate and store the per-level monster cap. Rejects values {@code <= 0},
     * since a level must be able to hold at least one monster.
     *
     * @param monsters the proposed maximum monsters per level
     * @throws InvalidTokenFoundDuringParse if {@code monsters <= 0}
     * @author Rowan Crowther
     */
    @Contract(pure = false)
    private static void setMonsters(int monsters) throws InvalidTokenFoundDuringParse {
        if (monsters <= 0) {
            String message = "Invalid value for max monsters on a given level. Incoming value is " + monsters
                    + ". Should be greater than 0";
            InvalidTokenFoundDuringParse e = new InvalidTokenFoundDuringParse(message);
            logger.error(message, e);
            throw e;
        }
        LevelMaxConstants.monsters = monsters;
    }
}