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

import uk.co.jackoftrades.middle.cave.Loc;

/**
 * {@link GameEventData} payload carrying a single grid location — used by events
 * that concern one point on the map.
 *
 * @author ClaudeCode
 */
public class EventDataPoint implements GameEventData {
    /**
     * The grid location this event refers to.
     *
     * @author ClaudeCode
     */
    private Loc loc;

    /**
     * Wrap an existing location.
     *
     * @param loc the location
     * @author ClaudeCode
     */
    public EventDataPoint(Loc loc) {
        this.loc = loc;
    }

    /**
     * Build the payload from raw coordinates.
     *
     * @param x column
     * @param y row
     * @author ClaudeCode
     */
    public EventDataPoint(int x, int y) {
        loc = new Loc(x, y);
    }

    /**
     * @return the grid location
     * @author ClaudeCode
     */
    public Loc getLoc() {
        return loc;
    }
}
