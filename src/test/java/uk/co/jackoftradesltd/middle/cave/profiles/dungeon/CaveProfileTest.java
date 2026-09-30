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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import uk.co.jackoftradesltd.middle.cave.roombuilders.RoomType;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Tests for {@link CaveProfile}, the port of {@code struct cave_profile} in the C original's
 * {@code generate.h}. The type is a plain data holder, so the contract is that each constructor
 * argument comes back from its own getter, no two fields alias, and the values C's parser would
 * have produced from {@code dungeon_profile.txt} survive unchanged.
 *
 * <p>Expected values are taken from the {@code classic} and {@code town} records in
 * {@code lib/gamedata/dungeon_profile.txt}, not from the Java code.
 *
 * <p>Class CaveProfileTest coded on 260930, commented in full on 260930.
 */
class CaveProfileTest {

    /**
     * The {@code classic} record: {@code params:11:50:200:2}, {@code tunnel:10:30:15:25:50},
     * {@code streamer:5:2:3:90:2:40}, {@code alloc:90}. Every value is distinct, so a getter
     * wired to the wrong field fails.
     */
    @Test
    void classicRecordRoundTripsEveryField() {
        TunnelProfile tun = new TunnelProfile(10, 30, 15, 25, 50);
        StreamerProfile str = new StreamerProfile(5, 2, 3, 90, 2, 40);
        List<RoomProfile> rooms = List.of(
                new RoomProfile("Greater vault", RoomType.STAIRCASE, 0, 44, 66, 35, false, 0, 100));

        CaveProfile p = new CaveProfile("classic", 11, 50, 200, 2, tun, str, rooms, 0, 90);

        assertEquals("classic", p.getName());
        assertEquals(11, p.getBlockSize());
        assertEquals(50, p.getDunRooms());
        assertEquals(200, p.getDunUnusual());
        assertEquals(2, p.getMaxRarity());
        assertSame(tun, p.getTun());
        assertSame(str, p.getStr());
        assertSame(rooms, p.getRoomProfiles());
        assertEquals(0, p.getMinLevel());
        assertEquals(90, p.getAlloc());
    }

    /**
     * The {@code town} record has {@code params:1:0:200:0}, a streamer line and no tunnel line, so
     * C leaves {@code tun} zeroed. Java holds {@code null} for that case, and an empty room list.
     */
    @Test
    void townRecordWithNoTunnelLineKeepsNullTun() {
        StreamerProfile str = new StreamerProfile(1, 1, 0, 0, 0, 0);

        CaveProfile p = new CaveProfile("town", 1, 0, 200, 0, null, str, List.of(), 0, -1);

        assertNull(p.getTun());
        assertSame(str, p.getStr());
        assertEquals(0, p.getDunRooms());
        assertEquals(0, p.getMaxRarity());
        assertEquals(List.of(), p.getRoomProfiles());
    }

    /**
     * A profile with no streamer line keeps a null {@code str}, the mirror of the tunnel case.
     */
    @Test
    void missingStreamerLineKeepsNullStr() {
        TunnelProfile tun = new TunnelProfile(1, 2, 3, 4, 5);

        CaveProfile p = new CaveProfile("x", 1, 1, 1, 1, tun, null, List.of(), 1, 1);

        assertNull(p.getStr());
        assertSame(tun, p.getTun());
    }

    /**
     * {@code alloc} is stored raw: 0 and values below -1 disable a profile, -1 defers to the
     * hard-coded checks in {@code generate.c}. None may be clamped or normalised.
     */
    @ParameterizedTest
    @ValueSource(ints = {-2, -1, 0, 1, 90, Integer.MAX_VALUE})
    void allocIsReturnedRaw(int alloc) {
        CaveProfile p = new CaveProfile("x", 1, 1, 1, 1, null, null, List.of(), 0, alloc);

        assertEquals(alloc, p.getAlloc());
    }

    /**
     * {@code min-level} is stored as given, including the boundary values 0 and the deepest level.
     */
    @ParameterizedTest
    @ValueSource(ints = {0, 1, 10, 127})
    void minLevelIsReturnedRaw(int minLevel) {
        CaveProfile p = new CaveProfile("x", 1, 1, 1, 1, null, null, List.of(), minLevel, 1);

        assertEquals(minLevel, p.getMinLevel());
    }

    /**
     * The room list is the caller's list, in the caller's order: the cutoff scan takes the first
     * room whose cutoff beats the roll, so reordering would change which room is chosen.
     */
    @Test
    void roomListKeepsFileOrder() {
        RoomProfile first = new RoomProfile("a", RoomType.STAIRCASE, 0, 1, 1, 0, false, 0, 10);
        RoomProfile second = new RoomProfile("b", RoomType.STAIRCASE, 0, 1, 1, 0, false, 0, 20);

        CaveProfile p = new CaveProfile("x", 1, 1, 1, 1, null, null, List.of(first, second), 0, 1);

        assertSame(first, p.getRoomProfiles().get(0));
        assertSame(second, p.getRoomProfiles().get(1));
    }
}
