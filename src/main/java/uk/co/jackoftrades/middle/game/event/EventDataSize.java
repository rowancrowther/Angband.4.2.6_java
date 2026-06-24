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
 * {@link GameEventData} payload carrying a width/height pair — used by events
 * that report a chosen size (e.g. a room's dimensions during generation).
 *
 * @author ClaudeCode
 */
public class EventDataSize implements GameEventData {
    /**
     * The width component.
     *
     * @author ClaudeCode
     */
    private int width;
    /**
     * The height component.
     *
     * @author ClaudeCode
     */
    private int height;

    /**
     * Build a size payload.
     *
     * @param height the height
     * @param width  the width
     * @author ClaudeCode
     */
    public EventDataSize(int height, int width) {
        this.width = width;
        this.height = height;
    }

    /**
     * @return the width
     * @author ClaudeCode
     */
    public int getWidth() {
        return width;
    }

    /**
     * @return the height
     * @author ClaudeCode
     */
    public int getHeight() {
        return height;
    }
}
