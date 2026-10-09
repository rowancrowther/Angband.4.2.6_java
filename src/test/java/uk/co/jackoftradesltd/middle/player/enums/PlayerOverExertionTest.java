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

package uk.co.jackoftradesltd.middle.player.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.channel.utils.Flag;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link PlayerOverExertion} - the port of the {@code PY_EXERT_*} enum in
 * {@code player-util.h}.
 *
 * <p><b>Every expectation comes from the C source, not from the port.</b> C declares nine
 * constants: {@code PY_EXERT_NONE = 0x00}, then the eight single-bit values {@code 0x01} (CON) to
 * {@code 0x80} (HP), in the order CON, FAINT, SCRAMBLE, CUT, CONF, HALLU, SLOW, HP. The port
 * replaces the bit values with an {@link java.util.EnumSet}-backed {@link Flag}, so what has to
 * hold is the set of names and the ability to combine them, as {@code game-world.c} does with
 * {@code PY_EXERT_HP | PY_EXERT_CUT | PY_EXERT_SLOW}.
 *
 * @author Rowan Crowther
 */
@DisplayName("PlayerOverExertion")
class PlayerOverExertionTest {

    private static final String[] C_ORDER = {
            "PY_EXERT_NONE", "PY_EXERT_CON", "PY_EXERT_FAINT", "PY_EXERT_SCRAMBLE",
            "PY_EXERT_CUT", "PY_EXERT_CONF", "PY_EXERT_HALLU", "PY_EXERT_SLOW", "PY_EXERT_HP"
    };

    @Test
    @DisplayName("has exactly C's nine constants, in C's declaration order")
    void matchesCDeclarationOrder() {
        PlayerOverExertion[] values = PlayerOverExertion.values();
        assertEquals(C_ORDER.length, values.length);
        for (int i = 0; i < C_ORDER.length; i++) {
            assertEquals(C_ORDER[i], values[i].name(), "constant at position " + i);
        }
    }

    @Test
    @DisplayName("each real constant sits at the position of its C bit: ordinal n is 1 << (n - 1)")
    void ordinalTracksCBitPosition() {
        int[] cValues = {0x01, 0x02, 0x04, 0x08, 0x10, 0x20, 0x40, 0x80};
        PlayerOverExertion[] values = PlayerOverExertion.values();
        assertEquals(0, values[0].ordinal(), "PY_EXERT_NONE is 0x00 in C");
        for (int n = 1; n < values.length; n++) {
            assertEquals(cValues[n - 1], 1 << (values[n].ordinal() - 1), values[n].name());
        }
    }

    @Test
    @DisplayName("an empty Flag requests no penalty, the equivalent of C's PY_EXERT_NONE mask")
    void emptyFlagIsNone() {
        Flag<PlayerOverExertion> none = new Flag<>(PlayerOverExertion.class);
        for (PlayerOverExertion p : PlayerOverExertion.values()) {
            assertFalse(none.has(p), p.name());
        }
    }

    @Test
    @DisplayName("the bloodlust combination HP | CUT | SLOW holds those three and nothing else")
    void bloodlustCombination() {
        Flag<PlayerOverExertion> flag = new Flag<>(PlayerOverExertion.class);
        flag.set(PlayerOverExertion.PY_EXERT_HP, PlayerOverExertion.PY_EXERT_CUT,
                PlayerOverExertion.PY_EXERT_SLOW);

        assertTrue(flag.has(PlayerOverExertion.PY_EXERT_HP));
        assertTrue(flag.has(PlayerOverExertion.PY_EXERT_CUT));
        assertTrue(flag.has(PlayerOverExertion.PY_EXERT_SLOW));
        assertFalse(flag.has(PlayerOverExertion.PY_EXERT_CON));
        assertFalse(flag.has(PlayerOverExertion.PY_EXERT_FAINT));
        assertFalse(flag.has(PlayerOverExertion.PY_EXERT_SCRAMBLE));
        assertFalse(flag.has(PlayerOverExertion.PY_EXERT_CONF));
        assertFalse(flag.has(PlayerOverExertion.PY_EXERT_HALLU));
        assertFalse(flag.has(PlayerOverExertion.PY_EXERT_NONE));
    }

    @Test
    @DisplayName("a single-constant Flag, as the spell and attack callers pass, tests only that one")
    void singleConstant() {
        Flag<PlayerOverExertion> faint =
                new Flag<>(PlayerOverExertion.class, PlayerOverExertion.PY_EXERT_FAINT);
        assertTrue(faint.has(PlayerOverExertion.PY_EXERT_FAINT));
        assertFalse(faint.has(PlayerOverExertion.PY_EXERT_CON));
    }
}
