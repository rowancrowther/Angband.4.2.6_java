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

import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.middle.objects.ItemObject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link EventDataMissile}, the port of the {@code missile} member of
 * {@code game_event_data} ({@code game-event.h}) as filled by {@code event_signal_missile()}
 * ({@code game-event.c}): the four fields {@code obj}, {@code seen}, {@code y}, {@code x}.
 *
 * <p>The class only stores and returns, so what can go wrong is crossing {@code y} with
 * {@code x}, or dropping the object reference. Values are chosen so no permutation matches.
 *
 * @author Rowan Crowther
 */
class EventDataMissileTest {

    @Test
    void fieldsComeBackInSignalOrder() {
        ItemObject obj = new ItemObject();

        EventDataMissile data = new EventDataMissile(obj, true, 7, 41);

        assertSame(obj, data.getItemObject());
        assertTrue(data.isSeen());
        assertEquals(7, data.getY());
        assertEquals(41, data.getX());
    }

    @Test
    void unseenIsPreserved() {
        EventDataMissile data = new EventDataMissile(new ItemObject(), false, 2, 3);

        assertFalse(data.isSeen());
    }

    @Test
    void objectIsHeldByReferenceNotCopied() {
        ItemObject obj = new ItemObject();
        EventDataMissile data = new EventDataMissile(obj, true, 1, 2);

        assertSame(data.getItemObject(), data.getItemObject());
        assertSame(obj, data.getItemObject());
    }

    @Test
    void nullObjectIsAccepted() {
        EventDataMissile data = new EventDataMissile(null, true, 0, 0);

        assertNull(data.getItemObject());
        assertEquals(0, data.getY());
        assertEquals(0, data.getX());
    }

    @Test
    void negativeAndLargeCoordinatesPassThrough() {
        EventDataMissile data = new EventDataMissile(null, false, -1, Integer.MAX_VALUE);

        assertEquals(-1, data.getY());
        assertEquals(Integer.MAX_VALUE, data.getX());
    }
}
