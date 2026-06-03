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
import uk.co.jackoftrades.middle.combat.CriticalLevel;
import uk.co.jackoftrades.middle.enums.MessageEnum;

import java.util.ArrayList;

public class CriticalLevelConstants {
    private static final String meleeTag = "melee-critical-level";
    private static final String rangedTag = "ranged-critical-level";
    private static final ArrayList<CriticalLevel> meleeLevels = new ArrayList<>();
    private static final ArrayList<CriticalLevel> rangedLevels = new ArrayList<>();
    private static final Logger logger = LogManager.getLogger();

    @Contract(pure = true)
    private CriticalLevelConstants() {
    }

    /**
     * Get a string value, CriticalType pair and pull the values from the incoming value parameter and create a new
     * CriticalLevel instance and put it in the correct ArrayList based on the CriticalType
     *
     * @param value The string containing the tokens for the critical value
     * @param type  whether this is a mêlée or ranged hit
     * @throws InvalidTokenFoundDuringParse A token was invalid or found incorrectly
     */
    public static void setValue(@NotNull String value, CriticalType type) throws InvalidTokenFoundDuringParse {
        String tag = type == CriticalType.MELEE ? meleeTag : rangedTag;

        String[] values = value.split(":");

        if (values.length != 4) {
            String message = "Invalid number of tokens found during a parse. Should be 1 tag and 4 sub-tokens. "
                    + values.length + " sub-tokens found. Token was: "
                    + tag + ":" + value;
            InvalidTokenFoundDuringParse e = new InvalidTokenFoundDuringParse(message);
            logger.error(message, e);
            throw e;
        }

        // Get the three integer values, remember for one of the Critical Level the first integer is negative.
        // Apart from that they should come in increasing values - to ease we will take them in any order, and fix them
        // in the ArrayList.
        int currentCutoff;
        int damageMult;
        int amountAdded;
        String hitTypeString = values[3];
        MessageEnum hitType;

        try {
            hitType = MessageEnum.valueOf("MSG_" + hitTypeString);
        } catch (IllegalArgumentException e) {
            String message = "Unknown Hit Type message in token. Token was: " + tag + ":" + value;
            logger.error(message, e);
            throw e;
        }

        try {
            currentCutoff = Integer.parseInt(values[0]);
        } catch (NumberFormatException e) {
            String message = "Invalid cut off value, could not parse string. Token was: " + tag + ":" + value;
            logger.error(message, e);
            throw e;
        }

        try {
            damageMult = Integer.parseInt(values[1]);
        } catch (NumberFormatException e) {
            String message = "Invalid damage multiplier value, could not parse string. Token was: " + tag + ":" + value;
            logger.error(message, e);
            throw e;
        }

        try {
            amountAdded = Integer.parseInt(values[2]);
        } catch (NumberFormatException e) {
            String message = "Invalid damage amount added value, could not parse string. Token was: " + tag + ":" + value;
            logger.error(message, e);
            throw e;
        }

        CriticalLevel criticalLevel = new CriticalLevel(currentCutoff, damageMult, amountAdded, hitType);

        if (type == CriticalType.MELEE)
            meleeLevels.add(criticalLevel);
        else
            rangedLevels.add(criticalLevel);
    }

    /**
     * Get the relevant critical level based on the type of combat, and the power of this hit.<br><br>
     * <strong>Note:</strong> this assumes that the values in the two array lists are read in, in the correct order from
     * constants.txt. If they are out of order in that file, then errors will occur. These errors are not checked for
     * here.
     *
     * @param power The power of this hit
     * @param type  The type of combat we are dealing with
     * @return The critical level for this type of combat where the previous level's power was less than the power of
     * the hit, and this level's power is greater than or equal to it.
     */
    @Contract(pure = true)
    public static CriticalLevel getCriticalLevel(int power, CriticalType type) {
        CriticalLevel last = null;

        if (type == CriticalType.MELEE) {
            for (CriticalLevel level : meleeLevels) {
                if (level.getCutOff() > power)
                    return level;

                last = level;
            }
        } else {
            for (CriticalLevel level : rangedLevels) {
                if (level.getCutOff() > power)
                    return level;

                last = level;
            }
        }

        return last;
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