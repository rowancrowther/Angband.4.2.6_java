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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlagType;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * {@link FlagSet}, the port of C's {@code struct flag_set} and its {@code flag_sets[]} table in
 * {@code obj-power.c}, function {@code flags_power()}.
 *
 * <p>The expected increments below are worked by hand from C's rule, not read back from the Java:
 * once {@code count > 1} the row adds {@code factor * count * count}, and when
 * {@code count == size} it adds {@code bonus} on top. The two tests are independent in C (two
 * separate {@code if}s), so a full set of size 1 would collect both.
 *
 * <p>Test class FlagSetTest coded on 261009 / commented in full on 261009.
 */
@DisplayName("FlagSet (obj-power.c flag_sets[])")
class FlagSetTest {

    /**
     * The increment {@code flags_power()} adds for a row, written out from C's two {@code if}s
     * rather than taken from any Java code. Reads the row only through its accessors, so a
     * mis-assigned constructor argument shows up as a wrong total.
     */
    private static int cIncrement(FlagSet row) {
        int q = 0;
        if (row.getCount() > 1) {
            q += row.getFactor() * row.getCount() * row.getCount();
        }
        if (row.getCount() == row.getSize()) {
            q += row.getBonus();
        }
        return q;
    }

    private static FlagSet row(String name) {
        return switch (name) {
            // { OFT_SUST, 1, 10, 5, 0, "sustains" }
            case "sustains" -> new FlagSet(ObjectFlagType.OFT_SUST, 1, 10, 5, 0, "sustains");
            // { OFT_PROT, 3, 15, 4, 0, "protections" }
            case "protections" -> new FlagSet(ObjectFlagType.OFT_PROT, 3, 15, 4, 0, "protections");
            // { OFT_MISC, 1, 25, 8, 0, "misc abilities" }
            case "misc abilities" -> new FlagSet(ObjectFlagType.OFT_MISC, 1, 25, 8, 0, "misc abilities");
            default -> throw new IllegalArgumentException(name);
        };
    }

    /**
     * Each line: row, count, increment. Hand-derived. The interesting counts are 0 and 1 (nothing),
     * 2 (first quadratic term), size - 1, size (bonus lands) and size + 1 (bonus gone again).
     */
    @ParameterizedTest(name = "{0} holding {1} adds {2}")
    @CsvSource({
            "sustains,       0,  0",
            "sustains,       1,  0",
            "sustains,       2,  4",
            "sustains,       4,  16",
            "sustains,       5,  35",
            "sustains,       6,  36",
            "protections,    0,  0",
            "protections,    1,  0",
            "protections,    2,  12",
            "protections,    3,  27",
            "protections,    4,  63",
            "protections,    5,  75",
            "misc abilities, 0,  0",
            "misc abilities, 1,  0",
            "misc abilities, 2,  4",
            "misc abilities, 7,  49",
            "misc abilities, 8,  89",
            "misc abilities, 9,  81",
    })
    @DisplayName("the row's figures give C's increment at each count")
    void incrementFollowsC(String name, int count, int expected) {
        FlagSet row = row(name);
        row.setCount(count);

        assertEquals(expected, cIncrement(row));
    }

    /**
     * C's table, verbatim, as the accessors report it.
     */
    @Test
    @DisplayName("the three rows hold C's table figures")
    void tableFigures() {
        FlagSet sust = row("sustains");
        assertEquals(ObjectFlagType.OFT_SUST, sust.getType());
        assertEquals(1, sust.getFactor());
        assertEquals(10, sust.getBonus());
        assertEquals(5, sust.getSize());
        assertEquals(0, sust.getCount());
        assertEquals("sustains", sust.getDescription());

        FlagSet prot = row("protections");
        assertEquals(ObjectFlagType.OFT_PROT, prot.getType());
        assertEquals(3, prot.getFactor());
        assertEquals(15, prot.getBonus());
        assertEquals(4, prot.getSize());
        assertEquals(0, prot.getCount());
        assertEquals("protections", prot.getDescription());

        FlagSet misc = row("misc abilities");
        assertEquals(ObjectFlagType.OFT_MISC, misc.getType());
        assertEquals(1, misc.getFactor());
        assertEquals(25, misc.getBonus());
        assertEquals(8, misc.getSize());
        assertEquals(0, misc.getCount());
        assertEquals("misc abilities", misc.getDescription());
    }

    /**
     * C zeroes {@code count} on its static table before each object; the Java caller does the
     * same through {@code setCount(0)}, and a zeroed row must price as nothing whatever it held.
     */
    @Test
    @DisplayName("zeroing the count between objects discards the previous total")
    void zeroingDiscardsPreviousObject() {
        FlagSet row = row("protections");
        row.setCount(4);
        assertEquals(63, cIncrement(row));

        row.setCount(0);
        assertEquals(0, cIncrement(row));
    }
}
