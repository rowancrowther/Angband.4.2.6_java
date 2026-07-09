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

package uk.co.jackoftrades.middle.combat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.jackoftrades.middle.enums.MessageType;

/**
 * This is a STEM class - TODO: Extend it
 */

public class CriticalLevel {
    /**
     * Power cut-off: this level applies when the hit's power is below this value.
     *
     * @author Rowan Crowther
     */
    private int cutOff;
    /**
     * Damage multiplier applied at this critical level.
     *
     * @author Rowan Crowther
     */
    private int mult;
    /**
     * Flat damage added at this critical level.
     *
     * @author Rowan Crowther
     */
    private int add;
    /**
     * The message shown to the player for this critical level.
     *
     * @author Rowan Crowther
     */
    private MessageType msgt;
    /**
     * Logger (reserved for diagnostics).
     *
     * @author Rowan Crowther
     */
    private final Logger logger = LogManager.getLogger();

    /**
     * Build a (Vanilla) critical level from its cut-off, multiplier, bonus and message.
     *
     * @param cutOff power cut-off for this level
     * @param mult   damage multiplier
     * @param add    flat damage bonus
     * @param msgt   message to display
     * @author Rowan Crowther
     */
    public CriticalLevel(int cutOff, int mult, int add, MessageType msgt) {
        this.cutOff = cutOff;
        this.mult = mult;
        this.add = add;
        this.msgt = msgt;
    }

    /**
     * @return this level's power cut-off
     * @author Rowan Crowther
     */
    public int getCutOff() {
        return cutOff;
    }

    /**
     * @return this level's damage multiplier
     * @author Rowan Crowther
     */
    public int getMult() {
        return mult;
    }

    /**
     * @return this level's flat damage bonus
     * @author Rowan Crowther
     */
    public int getAdd() {
        return add;
    }

    /**
     * @return this level's display message
     * @author Rowan Crowther
     */
    public MessageType getMsgt() {
        return msgt;
    }
}
