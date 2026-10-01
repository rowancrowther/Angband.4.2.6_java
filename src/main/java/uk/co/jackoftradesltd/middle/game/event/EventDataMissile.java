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

package uk.co.jackoftradesltd.middle.game.event;

import uk.co.jackoftradesltd.channel.messages.data.GameEventData;
import uk.co.jackoftradesltd.middle.objects.ItemObject;

/**
 * {@link GameEventData} payload for {@code EVENT_MISSILE}: a thrown or fired
 * object in flight, whether the player can see it, and the grid it currently
 * occupies. Port of the {@code missile} member of the {@code game_event_data}
 * union in {@code game-event.h}, as filled by {@code event_signal_missile()} in
 * {@code game-event.c} and read by {@code display_missile()} in
 * {@code ui-display.c}, which only draws the missile when {@code seen} is set.
 *
 * <p>Like the C struct it carries the object by reference, not a copy. Outstanding:
 * the {@link ItemObject} needs flattening before this can move into
 * {@code channel.messages.data}.
 *
 * <p>coded on 260929 / commented in full on 261001
 *
 * @author Rowan Crowther
 */
public class EventDataMissile implements GameEventData {
    /**
     * The missile object in flight ({@code struct object *obj} in C).
     *
     * <p>coded on 260929 / commented in full on 261001
     */
    private ItemObject itemObject;
    /**
     * Whether the player can see the missile ({@code bool seen} in C). The display
     * handler skips all drawing and delay when this is false.
     *
     * <p>coded on 260929 / commented in full on 261001
     */
    private boolean seen;
    /**
     * Row of the missile's current grid ({@code int y} in C).
     *
     * <p>coded on 260929 / commented in full on 261001
     */
    private int y;
    /**
     * Column of the missile's current grid ({@code int x} in C).
     *
     * <p>coded on 260929 / commented in full on 261001
     */
    private int x;

    /**
     * Builds a missile payload; argument order matches {@code event_signal_missile()}
     * in {@code game-event.c} (object, seen, y, x). Nothing is validated or copied.
     *
     * <p>coded on 260929 / commented in full on 261001
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
     * Reads {@code data->missile.obj}.
     *
     * <p>coded on 260929 / commented in full on 261001
     *
     * @return the missile object, by reference
     */
    public ItemObject getItemObject() {
        return itemObject;
    }

    /**
     * Reads {@code data->missile.seen}.
     *
     * <p>coded on 260929 / commented in full on 261001
     *
     * @return whether the player sees the missile
     */
    public boolean isSeen() {
        return seen;
    }

    /**
     * Reads {@code data->missile.y}.
     *
     * <p>coded on 260929 / commented in full on 261001
     *
     * @return the current row
     */
    public int getY() {
        return y;
    }

    /**
     * Reads {@code data->missile.x}.
     *
     * <p>coded on 260929 / commented in full on 261001
     *
     * @return the current column
     */
    public int getX() {
        return x;
    }
}
