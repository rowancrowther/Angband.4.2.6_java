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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Tests for {@link CommandReturnCodes}, the port of {@code enum cmd_return_codes} in
 * {@code cmd-core.h}, where {@code CMD_OK} is 0 and the three failures are -1, -2 and -3.
 *
 * @author Rowan Crowther
 */
class CommandReturnCodesTest {

    @Test
    @DisplayName("each constant carries the value C assigns")
    void valuesMatchC() {
        assertEquals(0, CommandReturnCodes.CMD_OK.getValue());
        assertEquals(-1, CommandReturnCodes.CMD_ARG_NOT_PRESENT.getValue());
        assertEquals(-2, CommandReturnCodes.CMD_ARG_WRONG_TYPE.getValue());
        assertEquals(-3, CommandReturnCodes.CMD_ARG_ABORTED.getValue());
    }

    @Test
    @DisplayName("the enum has exactly the four C constants")
    void count() {
        assertEquals(4, CommandReturnCodes.values().length);
    }

    @Test
    @DisplayName("fromValue inverts getValue for every constant")
    void roundTrip() {
        for (CommandReturnCodes c : CommandReturnCodes.values()) {
            assertEquals(c, CommandReturnCodes.fromValue(c.getValue()));
        }
    }

    @Test
    @DisplayName("fromValue maps the C integers to the right constants")
    void fromValueKnown() {
        assertEquals(CommandReturnCodes.CMD_OK, CommandReturnCodes.fromValue(0));
        assertEquals(CommandReturnCodes.CMD_ARG_NOT_PRESENT, CommandReturnCodes.fromValue(-1));
        assertEquals(CommandReturnCodes.CMD_ARG_WRONG_TYPE, CommandReturnCodes.fromValue(-2));
        assertEquals(CommandReturnCodes.CMD_ARG_ABORTED, CommandReturnCodes.fromValue(-3));
    }

    @Test
    @DisplayName("fromValue returns null just outside the C range")
    void fromValueOutOfRange() {
        assertNull(CommandReturnCodes.fromValue(1));
        assertNull(CommandReturnCodes.fromValue(-4));
        assertNull(CommandReturnCodes.fromValue(Integer.MIN_VALUE));
    }
}
