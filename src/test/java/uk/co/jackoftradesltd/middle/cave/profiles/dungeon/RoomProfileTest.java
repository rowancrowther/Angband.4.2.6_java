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

package uk.co.jackoftradesltd.middle.cave.profiles.dungeon;

import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.middle.cave.roombuilders.RoomType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for {@link RoomProfile}, the port of {@code struct room_profile} in the C original's
 * {@code generate.h}. The type is a plain data holder, so the contract is that each constructor
 * argument comes back from its own getter and no two fields alias.
 *
 * <p>Expected values are taken from {@code room:} lines of the {@code classic} record in
 * {@code lib/gamedata/dungeon_profile.txt}, whose field order is
 * {@code name:rating:height:width:level:pit:rarity:cutoff}.
 *
 * <p>Class RoomProfileTest coded on 260930, commented in full on 260930.
 */
class RoomProfileTest {

    /**
     * {@code room:monster pit:0:11:33:5:1:2:8}. Every numeric field is distinct, so a getter wired
     * to the wrong field fails, and the pit flag is true.
     */
    @Test
    void monsterPitRoundTripsEveryField() {
        RoomProfile p = new RoomProfile("monster pit", RoomType.getRoomTypeFromName("monster pit"),
                0, 11, 33, 5, true, 2, 8);
        assertEquals("monster pit", p.getName());
        assertEquals(0, p.getRating());
        assertEquals(11, p.getHeight());
        assertEquals(33, p.getWidth());
        assertEquals(5, p.getLevel());
        assertTrue(p.isPit());
        assertEquals(2, p.getRarity());
        assertEquals(8, p.getCutoff());
    }

    /**
     * {@code room:room template:3:11:33:30:0:1:100}: the template rating survives, the pit flag is
     * false, and the cutoff of 100 is kept as written rather than clamped to 99.
     */
    @Test
    void roomTemplateKeepsRatingAndFullCutoff() {
        RoomProfile p = new RoomProfile("room template", null, 3, 11, 33, 30, false, 1, 100);
        assertEquals(3, p.getRating());
        assertEquals(30, p.getLevel());
        assertFalse(p.isPit());
        assertEquals(100, p.getCutoff());
    }

    /**
     * {@code room:staircase room:0:3:3:1:0:99:0}: rarity 99 and cutoff 0 are the extremes in the
     * data file, the room being reachable by no roll.
     */
    @Test
    void staircaseRoomKeepsExtremeRarityAndZeroCutoff() {
        RoomProfile p = new RoomProfile("staircase room", RoomType.STAIRCASE, 0, 3, 3, 1, false, 99, 0);
        assertEquals(99, p.getRarity());
        assertEquals(0, p.getCutoff());
        assertEquals(3, p.getHeight());
        assertEquals(3, p.getWidth());
    }

    /**
     * The resolved type is returned as the same instance, and an unresolved name leaves it null
     * while the name itself is kept.
     */
    @Test
    void roomTypeIsKeptByIdentityAndMayBeNull() {
        RoomProfile resolved = new RoomProfile("simple room", RoomType.SIMPLE, 0, 11, 33, 1, false, 0, 100);
        assertSame(RoomType.SIMPLE, resolved.getRoomType());

        RoomProfile unresolved = new RoomProfile("no such room", null, 0, 1, 1, 1, false, 0, 0);
        assertNull(unresolved.getRoomType());
        assertEquals("no such room", unresolved.getName());
    }
}
