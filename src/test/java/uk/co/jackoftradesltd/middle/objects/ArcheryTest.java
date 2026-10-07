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

package uk.co.jackoftradesltd.middle.objects;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import uk.co.jackoftradesltd.middle.game.globals.registry.ObjectRegistry;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for {@link Archery}, the port of {@code struct archery} and its table in {@code obj-power.c}.
 *
 * <p>The expected figures are the three initialiser rows in C, {@code {TV_SHOT, 10, 9, 4}},
 * {@code {TV_ARROW, 12, 9, 5}} and {@code {TV_BOLT, 14, 9, 7}}, and the launcher-pricing arithmetic
 * in {@code launcher_ammo_damage_power()}. None is read back from the port.
 *
 * <p>Class ArcheryTest coded on 261007, commented in full on 261007.
 *
 * @author Rowan Crowther
 */
class ArcheryTest {

    /**
     * Each of the four constructor arguments comes back from its own getter, for each C row.
     *
     * @param tval       the ammunition tval
     * @param ammoDam    C's {@code ammo_dam}
     * @param launchDam  C's {@code launch_dam}
     * @param launchMult C's {@code launch_mult}
     */
    @ParameterizedTest
    @CsvSource({"TV_SHOT,10,9,4", "TV_ARROW,12,9,5", "TV_BOLT,14,9,7"})
    void rowRoundTripsEveryField(TValue tval, int ammoDam, int launchDam, int launchMult) {
        Archery row = new Archery(tval, ammoDam, launchDam, launchMult);
        assertEquals(tval, row.getAmmoType());
        assertEquals(ammoDam, row.getAmmoDamage());
        assertEquals(launchDam, row.getLaunchDamage());
        assertEquals(launchMult, row.getLaunchMult());
    }

    /**
     * Distinct values per field catch a constructor that assigns two arguments to the wrong
     * fields, which the real rows (9 on every launch damage) could hide.
     */
    @Test
    void distinctValuesLandInTheirOwnFields() {
        Archery row = new Archery(TValue.TV_ARROW, 1, 2, 3);
        assertEquals(1, row.getAmmoDamage());
        assertEquals(2, row.getLaunchDamage());
        assertEquals(3, row.getLaunchMult());
    }

    /**
     * The doubled multiplier divided by {@code 2 * MAX_BLOWS} (C: {@code p * launch_mult / (2 *
     * MAX_BLOWS)}) with {@code MAX_BLOWS = 5} ({@code obj-power.h}) and a power of 503: a shot row gives
     * 503 * 4 / 10 = 201, an arrow row 503 * 5 / 10 = 251 and a bolt row 503 * 7 / 10 = 352, each
     * truncating.
     *
     * @param tval       the ammunition tval
     * @param launchMult C's {@code launch_mult}
     * @param expected   the truncated result
     */
    @ParameterizedTest
    @CsvSource({"TV_SHOT,4,201", "TV_ARROW,5,251", "TV_BOLT,7,352"})
    void multiplierDividesBackOutAsInC(TValue tval, int launchMult, int expected) {
        Archery row = new Archery(tval, 0, 9, launchMult);
        assertEquals(expected, 503 * row.getLaunchMult() / (2 * ObjectRegistry.MAX_BLOWS));
    }
}
