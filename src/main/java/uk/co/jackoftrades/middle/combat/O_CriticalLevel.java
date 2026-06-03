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

package uk.co.jackoftrades.middle.combat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;
import uk.co.jackoftrades.backend.io.bespokeexceptions.InvalidTokenFoundDuringParse;
import uk.co.jackoftrades.middle.enums.MessageEnum;

public class O_CriticalLevel {
    private final static String tag = "o-melee-critical-level";
    private int chance;
    private int addedDice;
    private MessageEnum msgt;
    private final static Logger logger = LogManager.getLogger();

    /**
     * Constructs a new critical level for non-vanilla angbands
     *
     * @param chance    One over this is the chance this level occurs when none of the previous levels have been selected.
     *                  The last level will always be used if none of the prior levels has been selected. This must be
     *                  positive
     * @param addedDice The number of dice to add to the damage for the critical. It must be non-negative.
     * @param msgt      The message to use (from the list-messages.h), stored in a HitType enum to ensure that it is pulled
     *                  in correctly.
     */
    @CheckReturnValue
    public O_CriticalLevel(int chance, int addedDice, MessageEnum msgt) throws InvalidTokenFoundDuringParse {
        if (chance <= 0) {
            String message = "Invalid value of chance in parsing of a constants.txt line. Token was: "
                    + tag + ":" + chance + ":" + addedDice + ":" + msgt.name();
            InvalidTokenFoundDuringParse e = new InvalidTokenFoundDuringParse(message);
            logger.error(message, e);
            throw new InvalidTokenFoundDuringParse(message);
        }
        this.chance = chance;

        if (addedDice < 0) {
            String message = "Invalid value of added dice in parsing of a constants.txt line. Token was: "
                    + tag + ":" + chance + ":" + addedDice + ":" + msgt.name();
            InvalidTokenFoundDuringParse e = new InvalidTokenFoundDuringParse(message);
            logger.error(message, e);
            throw e;
        }
        this.addedDice = addedDice;

        // No need to change - forced to be correct by input.
        this.msgt = msgt;
    }

    /**
     * Gets the 1/chance that this level occurs when none of the previous levels have been selected. This will always
     * be positive
     *
     * @return the 1/chance that this level occurs when none of the previous levels have been selected.
     */
    @Contract(pure = true)
    public int getChance() {
        return chance;
    }

    /**
     * Get the number of dice to add for the critical. This will always be non-negative.
     *
     * @return the number of dice to add for the critical.
     */
    @Contract(pure = true)
    public int getAddedDice() {
        return addedDice;
    }

    /**
     * The hit type of this critical
     *
     * @return hit type of this critical
     */
    @Contract(pure = true)
    public MessageEnum getMsgt() {
        return msgt;
    }
}