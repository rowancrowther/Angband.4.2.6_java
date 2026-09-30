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

package uk.co.jackoftradesltd.middle.cave.roombuilders;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Tests for {@link RoomType}, the port of the {@code room_builders[]} table in the C original's
 * {@code generate.c} and its three accessors.
 *
 * <p>The expected table is copied from {@code list-rooms.h}, one {@code ROOM(name, max_height,
 * max_width, builder)} row per entry, in file order. It is written out here rather than read back
 * from the enum so that a transposed row or a swapped cap fails.
 *
 * <p>Class RoomTypeTest coded on 260930, commented in full on 260930.
 */
class RoomTypeTest {

    /**
     * {@code list-rooms.h}, in order: name, max_height, max_width.
     */
    private static final Object[][] C_TABLE = {
            {"staircase room", 0, 0},
            {"simple room", 0, 0},
            {"moria room", 0, 0},
            {"large room", 0, 0},
            {"crossed room", 0, 0},
            {"circular room", 0, 0},
            {"overlap room", 0, 0},
            {"room template", 11, 33},
            {"Interesting room", 40, 50},
            {"monster pit", 0, 0},
            {"monster nest", 0, 0},
            {"huge room", 0, 0},
            {"room of chambers", 0, 0},
            {"Lesser vault", 22, 22},
            {"Medium vault", 22, 33},
            {"Greater vault", 44, 66},
            {"Lesser vault (new)", 22, 22},
            {"Medium vault (new)", 22, 33},
            {"Greater vault (new)", 44, 66},
    };

    /**
     * {@code get_room_builder_count} returns {@code N_ELEMENTS(room_builders)}, which is 19.
     */
    @Test
    void countIsNineteen() {
        assertEquals(19, RoomType.getRoomBuilderCount());
        assertEquals(C_TABLE.length, RoomType.values().length);
    }

    /**
     * Every row of the C table lands at the same index with the same name and both caps.
     */
    @Test
    void everyRowMatchesListRoomsH() {
        RoomType[] types = RoomType.values();
        for (int i = 0; i < C_TABLE.length; i++) {
            assertEquals(C_TABLE[i][0], types[i].getName(), "name at index " + i);
            assertEquals(C_TABLE[i][1], types[i].getMaxHeight(), "max_height at index " + i);
            assertEquals(C_TABLE[i][2], types[i].getMaxWidth(), "max_width at index " + i);
        }
    }

    /**
     * {@code get_room_builder_index_from_name} returns the table index of each exact name.
     */
    @Test
    void indexFromNameFindsEveryName() {
        for (int i = 0; i < C_TABLE.length; i++) {
            assertEquals(i, RoomType.getIndexFromName((String) C_TABLE[i][0]));
        }
    }

    /**
     * {@code streq} is case-sensitive, so a name in the wrong case is not found and the index
     * comes back as -1, for both a lower-case name and a capitalised one.
     */
    @Test
    void indexFromNameIsCaseSensitive() {
        assertEquals(-1, RoomType.getIndexFromName("Simple room"));
        assertEquals(-1, RoomType.getIndexFromName("SIMPLE ROOM"));
        assertEquals(-1, RoomType.getIndexFromName("lesser vault"));
        assertEquals(-1, RoomType.getIndexFromName("interesting room"));
        assertEquals(-1, RoomType.getIndexFromName("Monster pit"));
    }

    /**
     * Unknown, empty and near-miss names give -1. The vault names differ only by a
     * {@code " (new)"} suffix, so a prefix must not match the longer entry or the reverse.
     */
    @Test
    void indexFromNameRejectsUnknownNames() {
        assertEquals(-1, RoomType.getIndexFromName(""));
        assertEquals(-1, RoomType.getIndexFromName("vault"));
        assertEquals(-1, RoomType.getIndexFromName("Lesser vault "));
        assertEquals(-1, RoomType.getIndexFromName("Lesser vault (new"));
        assertEquals(13, RoomType.getIndexFromName("Lesser vault"));
        assertEquals(16, RoomType.getIndexFromName("Lesser vault (new)"));
    }

    /**
     * {@code get_room_builder_name_from_index} returns the name for every valid index.
     */
    @Test
    void nameFromIndexReturnsEveryName() {
        for (int i = 0; i < C_TABLE.length; i++) {
            assertEquals(C_TABLE[i][0], RoomType.getNameFromIndex(i));
        }
    }

    /**
     * C's bounds check is {@code i >= 0 && i < count}, so -1 and the count itself both give
     * {@code NULL}, as do far-out values. The first and last valid indices still work.
     */
    @Test
    void nameFromIndexIsNullOutOfRange() {
        assertNull(RoomType.getNameFromIndex(-1));
        assertNull(RoomType.getNameFromIndex(19));
        assertNull(RoomType.getNameFromIndex(Integer.MIN_VALUE));
        assertNull(RoomType.getNameFromIndex(Integer.MAX_VALUE));
        assertEquals("staircase room", RoomType.getNameFromIndex(0));
        assertEquals("Greater vault (new)", RoomType.getNameFromIndex(18));
    }

    /**
     * The constant lookup agrees with the index lookup for every name.
     */
    @Test
    void roomTypeFromNameAgreesWithIndex() {
        for (int i = 0; i < C_TABLE.length; i++) {
            assertSame(RoomType.values()[i], RoomType.getRoomTypeFromName((String) C_TABLE[i][0]));
        }
    }

    /**
     * An unmatched name gives {@code null}, where C raises {@code PARSE_ERROR_NO_ROOM_FOUND}, and
     * matching is case-sensitive as in C.
     */
    @Test
    void roomTypeFromNameRejectsWrongNamesAndCase() {
        assertNull(RoomType.getRoomTypeFromName("no such room"));
        assertNull(RoomType.getRoomTypeFromName(""));
        assertNull(RoomType.getRoomTypeFromName("Simple Room"));
        assertNull(RoomType.getRoomTypeFromName("greater vault"));
        assertNull(RoomType.getRoomTypeFromName("Room Template"));
    }

    /**
     * Only the template, the interesting room and the six vaults have caps; the other eleven
     * types are uncapped, which the parsers rely on never being consulted for them.
     */
    @Test
    void onlyEightTypesHaveCaps() {
        int capped = 0;
        for (RoomType type : RoomType.values()) {
            if (type.getMaxHeight() != 0 || type.getMaxWidth() != 0) {
                capped++;
            }
        }
        assertEquals(8, capped);
    }
}
