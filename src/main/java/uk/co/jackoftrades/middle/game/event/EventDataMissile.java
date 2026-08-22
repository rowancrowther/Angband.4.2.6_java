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

import uk.co.jackoftrades.channel.messages.data.GameEventData;
import uk.co.jackoftrades.middle.objects.ItemObject;

/**
 * {@link GameEventData} payload describing a thrown/fired missile object in
 * flight — the {@link ItemObject} being thrown, whether the player sees it, and
 * its current grid. Consumed by the display layer to animate the missile.
 *
 * <p>Needs to have it's ItemObject flattened so it can move into 
 * channel.messages.data
 *
 * @author Rowan Crowther
 */
public class EventDataMissile implements GameEventData {
    /**
     * The missile object in flight.
     */
    private ItemObject itemObject;
    /**
     * Whether the player can see the missile.
     */
    private boolean seen;
    /**
     * Current row.
     */
    private int y;
    /**
     * Current column.
     */
    private int x;

    /**
     * Build a missile payload.
     *
     * @param itemObject the missile object
     * @param seen       whether the player sees it
     * @param y          current row
     * @param x          current column
     */
    public EventDataMissile(ItemObject itemObject, boolean seen, int y, int x) {
        this.itemObject = itemObject;
        this.seen = seen;
        this.y = y;
        this.x = x;
    }

    /**
     * @return the missile object
     */
    public ItemObject getItemObject() {
        return itemObject;
    }

    /**
     * @return whether the player sees the missile
     */
    public boolean isSeen() {
        return seen;
    }

    /**
     * @return the current row
     */
    public int getY() {
        return y;
    }

    /**
     * @return the current column
     */
    public int getX() {
        return x;
    }
}
