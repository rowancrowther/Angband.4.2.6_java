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

package uk.co.jackoftradesltd.middle.player;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

/**
 * Tests {@link PlayerBirth}'s {@code birthStatCosts} field, the port of C's
 * {@code birth_stat_costs} ({@code player-birth.c:679}).
 *
 * <p>The field is a bare data array with no accessor, so the only thing to check is that the
 * nineteen entries match C's exactly — the values are hand-derived from {@code player-birth.c:679}
 * rather than copied out of the port, so a transcription slip in either place would show up as a
 * mismatch.
 *
 * <p>Class PlayerBirthBirthStatCostsTest coded on 260905, commented in full on 260905.
 *
 * @author Rowan Crowther
 */
class PlayerBirthBirthStatCostsTest {

    /**
     * Costs 0-19, index {@code i} pricing the point that raises a stat from {@code i - 1} to
     * {@code i} — {@code player-birth.c:679}.
     */
    private static final int[] EXPECTED =
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 2, 4};

    /**
     * Reads the private field off a fresh instance.
     *
     * @return the field's current value
     * @throws Exception if the field cannot be reached
     */
    private static int[] birthStatCosts() throws Exception {
        Field field = PlayerBirth.class.getDeclaredField("birthStatCosts");
        field.setAccessible(true);
        return (int[]) field.get(new PlayerBirth());
    }

    /**
     * C prices 10 through 17 at one point per point, 18 at two, and 19 at four — a stat can never
     * exceed 18 at birth by any other route, so 19's cost only matters if a future caller reads
     * one entry past where {@code buy_stat} would ever ask.
     *
     * @throws Exception if the field cannot be reached
     */
    @Test
    @DisplayName("all nineteen entries match player-birth.c's birth_stat_costs")
    void matchesTheCArray() throws Exception {
        assertArrayEquals(EXPECTED, birthStatCosts(),
                "the port must charge exactly what C charges at every index");
    }
}
