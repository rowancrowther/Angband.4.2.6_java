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

package uk.co.jackoftrades.middle.game.event;

/**
 * {@link GameEventData} payload carrying a single string — used by events that
 * report a name or label (e.g. a generation profile or room type).
 *
 * @author ClaudeCode
 */
public class EventDataString implements GameEventData {
    /**
     * The string value carried by this event.
     *
     * @author ClaudeCode
     */
    private String string;

    /**
     * Build a string payload.
     *
     * @param string the string value
     * @author ClaudeCode
     */
    public EventDataString(String string) {
        this.string = string;
    }

    /**
     * @return the string value
     * @author ClaudeCode
     */
    public String getString() {
        return string;
    }
}
