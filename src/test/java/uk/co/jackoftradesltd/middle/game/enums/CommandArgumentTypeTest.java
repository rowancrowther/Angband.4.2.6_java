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

package uk.co.jackoftradesltd.middle.game.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for {@link CommandArgumentType}, the port of {@code enum cmd_arg_type} in
 * {@code cmd-core.h}. The expected order is transcribed from the C enum, where {@code arg_NONE}
 * is 0, {@code arg_STRING} is 1, and the rest follow in sequence.
 *
 * @author Rowan Crowther
 */
class CommandArgumentTypeTest {
    @Test
    @DisplayName("constants appear in the same order as the C enum")
    void orderMatchesC() {
        CommandArgumentType[] expected = {
                CommandArgumentType.arg_NONE,
                CommandArgumentType.arg_STRING,
                CommandArgumentType.arg_CHOICE,
                CommandArgumentType.arg_ITEM,
                CommandArgumentType.arg_NUMBER,
                CommandArgumentType.arg_DIRECTION,
                CommandArgumentType.arg_TARGET,
                CommandArgumentType.arg_POINT
        };
        assertArrayEquals(expected, CommandArgumentType.values());
    }

    @Test
    @DisplayName("ordinals equal C's integer values, arg_NONE = 0 and arg_STRING = 1")
    void ordinalsMatchCValues() {
        assertEquals(0, CommandArgumentType.arg_NONE.ordinal());
        assertEquals(1, CommandArgumentType.arg_STRING.ordinal());
        assertEquals(2, CommandArgumentType.arg_CHOICE.ordinal());
        assertEquals(3, CommandArgumentType.arg_ITEM.ordinal());
        assertEquals(4, CommandArgumentType.arg_NUMBER.ordinal());
        assertEquals(5, CommandArgumentType.arg_DIRECTION.ordinal());
        assertEquals(6, CommandArgumentType.arg_TARGET.ordinal());
        assertEquals(7, CommandArgumentType.arg_POINT.ordinal());
    }

    @Test
    @DisplayName("there are exactly eight constants, with no Java-only sentinel")
    void countMatchesC() {
        assertEquals(8, CommandArgumentType.values().length);
    }

    @Test
    @DisplayName("names round-trip through valueOf")
    void namesRoundTrip() {
        for (CommandArgumentType type : CommandArgumentType.values()) {
            assertEquals(type, CommandArgumentType.valueOf(type.name()));
        }
    }
}
