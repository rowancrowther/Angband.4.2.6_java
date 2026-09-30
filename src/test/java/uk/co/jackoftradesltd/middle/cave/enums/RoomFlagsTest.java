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

package uk.co.jackoftradesltd.middle.cave.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for {@link RoomFlags}, the port of {@code list-room-flags.h} and the
 * {@code room_flags[]} name table in {@code generate.c}.
 *
 * <p>Expected values come from the C source: the table is {@code "NONE"} followed by the
 * {@code ROOMF()} lines in header order ({@code FEW_ENTRANCES}, then {@code MAX}), so the
 * indices are 0, 1 and 2, and the help strings are the header's second field.
 */
class RoomFlagsTest {

    @Test
    void ordinalsMatchCNameTableIndices() {
        assertEquals(0, RoomFlags.NONE.ordinal());
        assertEquals(1, RoomFlags.FEW_ENTRANCES.ordinal());
        assertEquals(2, RoomFlags.MAX.ordinal());
    }

    @Test
    void maxIsTheSizeOfTheFlagSet() {
        assertEquals(3, RoomFlags.values().length);
        assertEquals(RoomFlags.values().length - 1, RoomFlags.MAX.ordinal());
    }

    @Test
    void helpStringsMatchHeader() {
        assertEquals("", RoomFlags.NONE.getHelpString());
        assertEquals("select alternate tunneling for a room since it can only be entered from a few "
                + "directions or the entrances involve digging", RoomFlags.FEW_ENTRANCES.getHelpString());
        assertEquals("", RoomFlags.MAX.getHelpString());
    }

    @Test
    void namesResolveAsTheParsersLookThemUp() {
        assertEquals(RoomFlags.FEW_ENTRANCES, RoomFlags.valueOf("FEW_ENTRANCES"));
        assertEquals(RoomFlags.NONE, RoomFlags.valueOf("NONE"));
        assertThrows(IllegalArgumentException.class, () -> RoomFlags.valueOf("FEW_ENTRANCE"));
    }
}
