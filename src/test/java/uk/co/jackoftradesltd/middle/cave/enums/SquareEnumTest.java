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
 * Unit tests for {@link SquareEnum}, the port of {@code list-square-flags.h} and the
 * {@code SQUARE_*} enum in {@code cave.h}.
 *
 * <p>Expected values come from the C header: the {@code SQUARE()} lines in order, from
 * {@code NONE} (0) to {@code CLOSE_PLAYER} (21), then {@code SQUARE_MAX} (22). The descriptions
 * are the header's second field, written out here rather than read from the enum.
 *
 * <p>Class SquareEnumTest coded on 260930, commented in full on 260930.
 */
class SquareEnumTest {

    /**
     * The header's flag names and descriptions, in header order.
     */
    private static final String[][] HEADER = {
            {"SQUARE_NONE", ""},
            {"SQUARE_MARK", "memorized feature"},
            {"SQUARE_GLOW", "self-illuminating"},
            {"SQUARE_VAULT", "part of a vault"},
            {"SQUARE_ROOM", "part of a room"},
            {"SQUARE_SEEN", "seen flag"},
            {"SQUARE_VIEW", "view flag"},
            {"SQUARE_WASSEEN", "previously seen (during update)"},
            {"SQUARE_FEEL", "hidden points to trigger feelings"},
            {"SQUARE_TRAP", "square containing a known trap"},
            {"SQUARE_INVIS", "square containing an unknown trap"},
            {"SQUARE_WALL_INNER", "inner wall generation flag"},
            {"SQUARE_WALL_OUTER", "outer wall generation flag"},
            {"SQUARE_WALL_SOLID", "solid wall generation flag"},
            {"SQUARE_MON_RESTRICT", "no random monster flag"},
            {"SQUARE_NO_TELEPORT", "player can't teleport from this square"},
            {"SQUARE_NO_MAP", "square can't be magically mapped"},
            {"SQUARE_NO_ESP", "telepathy doesn't work on this square"},
            {"SQUARE_PROJECT", "marked for projection processing"},
            {"SQUARE_DTRAP", "trap detected square"},
            {"SQUARE_NO_STAIRS", "square is not suitable for placing stairs"},
            {"SQUARE_CLOSE_PLAYER",
                    "square is seen and in player's light radius or UNLIGHT detection radius"},
    };

    @Test
    void ordinalsMatchCEnumValues() {
        assertEquals(0, SquareEnum.SQUARE_NONE.ordinal());
        assertEquals(7, SquareEnum.SQUARE_WASSEEN.ordinal());
        assertEquals(21, SquareEnum.SQUARE_CLOSE_PLAYER.ordinal());
        assertEquals(22, SquareEnum.SQUARE_MAX.ordinal());
    }

    @Test
    void maxIsTheSizeOfTheFlagSet() {
        assertEquals(HEADER.length, SquareEnum.SQUARE_MAX.ordinal());
        assertEquals(HEADER.length + 1, SquareEnum.values().length);
    }

    @Test
    void everyFlagHasTheHeadersNameAndDescriptionAtTheHeadersIndex() {
        for (int i = 0; i < HEADER.length; i++) {
            SquareEnum flag = SquareEnum.values()[i];
            assertEquals(HEADER[i][0], flag.name(), "name at index " + i);
            assertEquals(HEADER[i][1], flag.getDescription(), "description of " + HEADER[i][0]);
        }
    }

    @Test
    void wasSeenDescriptionHasNoStrayPunctuation() {
        assertEquals("previously seen (during update)", SquareEnum.SQUARE_WASSEEN.getDescription());
    }

    @Test
    void sentinelsHaveEmptyDescriptions() {
        assertEquals("", SquareEnum.SQUARE_NONE.getDescription());
        assertEquals("", SquareEnum.SQUARE_MAX.getDescription());
    }

    @Test
    void namesResolveByValueOf() {
        assertEquals(SquareEnum.SQUARE_NO_STAIRS, SquareEnum.valueOf("SQUARE_NO_STAIRS"));
        assertThrows(IllegalArgumentException.class, () -> SquareEnum.valueOf("SQUARE_NO_STAIR"));
    }
}
