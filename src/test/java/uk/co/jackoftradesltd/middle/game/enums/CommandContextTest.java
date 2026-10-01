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
 * Tests for {@link CommandContext}, the port of {@code enum cmd_context} in {@code cmd-core.h},
 * where {@code CTX_INIT} is 0 and the rest follow in sequence.
 *
 * @author Rowan Crowther
 */
class CommandContextTest {
    @Test
    @DisplayName("constants appear in the same order as the C enum")
    void orderMatchesC() {
        CommandContext[] expected = {
                CommandContext.CTX_INIT,
                CommandContext.CTX_BIRTH,
                CommandContext.CTX_GAME,
                CommandContext.CTX_STORE,
                CommandContext.CTX_DEATH
        };
        assertArrayEquals(expected, CommandContext.values());
    }

    @Test
    @DisplayName("ordinals equal C's integer values, CTX_INIT = 0 through CTX_DEATH = 4")
    void ordinalsMatchCValues() {
        assertEquals(0, CommandContext.CTX_INIT.ordinal());
        assertEquals(1, CommandContext.CTX_BIRTH.ordinal());
        assertEquals(2, CommandContext.CTX_GAME.ordinal());
        assertEquals(3, CommandContext.CTX_STORE.ordinal());
        assertEquals(4, CommandContext.CTX_DEATH.ordinal());
    }

    @Test
    @DisplayName("there are exactly five contexts")
    void count() {
        assertEquals(5, CommandContext.values().length);
    }
}
