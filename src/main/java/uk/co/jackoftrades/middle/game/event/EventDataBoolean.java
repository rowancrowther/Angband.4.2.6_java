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

package uk.co.jackoftrades.middle.game.event;

import org.jetbrains.annotations.Contract;

/**
 * {@link GameEventData} payload carrying a single boolean flag — used by events
 * that report a yes/no outcome (e.g. generation success).
 *
 * @author Rowan Crowther
 */
public class EventDataBoolean implements GameEventData {
    /**
     * The boolean value carried by this event.
     *
     * @author Rowan Crowther
     */
    public boolean value;

    /**
     * Build the payload with an explicit value.
     *
     * @param value the boolean value
     * @author Rowan Crowther
     */
    public EventDataBoolean(boolean value) {
        this.value = value;
    }

    /**
     * Build the payload defaulting to {@code false}.
     *
     * @author Rowan Crowther
     */
    public EventDataBoolean() {
        this.value = false;
    }

    /**
     * @return the boolean value
     * @author Rowan Crowther
     */
    @Contract(pure = true)
    public boolean getValue() {
        return value;
    }
}
