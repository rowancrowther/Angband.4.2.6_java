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
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftrades.backend.io.bespokeexceptions.InvalidTokenFoundDuringParse;
import uk.co.jackoftrades.middle.combat.O_CriticalLevel;
import uk.co.jackoftrades.middle.enums.MessageType;

import java.util.ArrayList;

/**
 * Holds the melee/ranged O-combat critical tables from {@code constants.txt}.
 * The "O" variant is the alternative (Oangband-derived) combat system in which a
 * critical is described by a 1/chance probability and a number of extra damage
 * dice, plus the message shown to the player — as opposed to the weight-based
 * Vanilla system in {@link CriticalLevelConstants}. Each parsed entry becomes an
 * {@link O_CriticalLevel} stored in the appropriate per-combat-type list.
 *
 * @author ClaudeCode
 */
public class O_CriticalLevelConstants {
    /**
     * Logger used to report malformed/invalid critical-level tokens.
     *
     * @author ClaudeCode
     */
    private static final Logger logger = LogManager.getLogger();
    /**
     * Data-file group tag for O-melee critical levels.
     *
     * @author ClaudeCode
     */
    private static final String meleeTag = "o-melee-critical-level";
    /**
     * Data-file group tag for O-ranged critical levels.
     *
     * @author ClaudeCode
     */
    private static final String rangedTag = "o-range-critical-level";
    /**
     * Parsed O-melee critical levels, in data-file order.
     *
     * @author ClaudeCode
     */
    private static final ArrayList<O_CriticalLevel> meleeCriticals = new ArrayList<>();
    /**
     * Parsed O-ranged critical levels, in data-file order.
     *
     * @author ClaudeCode
     */
    private static final ArrayList<O_CriticalLevel> rangedCriticals = new ArrayList<>();

    /**
     * Private constructor preventing instantiation; the class is a static table holder.
     *
     * @author ClaudeCode
     */
    @Contract(pure = true)
    private O_CriticalLevelConstants() {
    }

    /**
     * Create a o-critical-level constant and store it in the correct array list
     *
     * @param value The string containing the three tokens needed to create a o-critical-level
     * @param type  The type of this level, either MELEE or RANGED
     * @throws InvalidTokenFoundDuringParse Either there were the wrong number of tokens sent through, or
     *
     */
    public static void setValue(@NotNull String value, CriticalType type) throws InvalidTokenFoundDuringParse {
        String tag = type == CriticalType.MELEE ? meleeTag : rangedTag;
        // String token = tag + ":" + value;

        // check we have three tokens
        String[] tokens = value.split(":");

        if (tokens.length != 3) {
            String message = "Invalid number of tokens found during parsing. Token was: " + tag + ":" + value;
            InvalidTokenFoundDuringParse e = new InvalidTokenFoundDuringParse(message);
            logger.error(message, e);
            throw e;
        }

        int chance;
        try {
            chance = Integer.parseInt(tokens[0]);
        } catch (NumberFormatException e) {
            String message = "Invalid integer format in chance found while parsing constants.txt. Token was: " + tag + ":" + value
                    + "\n\n" + e.getMessage();
            logger.error(message, e);
            throw e;
        }

        if (chance <= 0) {
            String message = "Invalid value of chance found while parsing constants.txt; should be positive."
                    + " Token was: " + tag + ":" + value;
            InvalidTokenFoundDuringParse e = new InvalidTokenFoundDuringParse(message);
            logger.error(message, e);
            throw e;
        }

        int dice;
        try {
            dice = Integer.parseInt(tokens[1]);
        } catch (NumberFormatException e) {
            String message = "Invalid integer format in dice found while parsing constants.txt. Token was: "
                    + tag + ":" + value
                    + "\n\n" + e.getMessage();
            logger.error(message, e);
            throw e;
        }

        if (dice < 0) {
            String message = "Invalid number of dice found while parsing constants.txt. Token was: " + tag + ":" + value;
            InvalidTokenFoundDuringParse e = new InvalidTokenFoundDuringParse(message);
            logger.error(message, e);
            throw e;
        }

        MessageType msgt;
        try {
            msgt = MessageType.valueOf(tokens[2]);
        } catch (IllegalArgumentException e) {
            String message = "Invalid value of type found while parsing constants.txt. Token was: "
                    + tag + ":" + value
                    + "\n\n" + e.getMessage();
            logger.error(message, e);
            throw e;
        }

        O_CriticalLevel level = new O_CriticalLevel(dice, chance, msgt);

        if (type == CriticalType.MELEE)
            meleeCriticals.add(level);
        else
            rangedCriticals.add(level);
    }

    /**
     * Enum for which type of critical we are looking, melee or ranged.
     */
    public enum CriticalType {
        /**
         * Mêlée combat, combat with anything but magic & thrown/ranged
         */
        MELEE,

        /**
         * Ranged combat, combat with slings, bows, etc.
         */
        RANGED
    }
}