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
 * {@link GameEventData} payload carrying a message and its type code — used by
 * message/notification events.
 *
 * @author ClaudeCode
 */
public class EventDataMessage implements GameEventData {
    /**
     * The message type code.
     *
     * @author ClaudeCode
     */
    private int type;
    /**
     * The message text.
     *
     * @author ClaudeCode
     */
    private String message;

    /**
     * Build a message payload.
     *
     * @param type    the message type code
     * @param message the message text
     * @author ClaudeCode
     */
    public EventDataMessage(int type, String message) {
        this.type = type;
        this.message = message;
    }

    /**
     * @return the message type code
     * @author ClaudeCode
     */
    public int getType() {
        return type;
    }

    /**
     * @return the message text
     * @author ClaudeCode
     */
    public String getMessage() {
        return message;
    }
}
