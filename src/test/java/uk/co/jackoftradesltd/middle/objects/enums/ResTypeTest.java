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

package uk.co.jackoftradesltd.middle.objects.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.channel.enums.ElementEnum;
import uk.co.jackoftradesltd.middle.objects.ElementPowers;
import uk.co.jackoftradesltd.middle.objects.ElementSet;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pins {@link ResType} against the anonymous {@code enum { T_LRES, T_HRES }} in {@code obj-power.c},
 * and against the way the {@code element_sets[]} and {@code el_powers[]} tables in that file use it.
 *
 * <p>Expected values are transcribed from C: the enum order (T_LRES = 0, T_HRES = 1) and the group
 * column of each {@code el_powers[]} row.
 *
 * <p>Class ResTypeTest coded on 261011, commented in full on 261011.
 */
class ResTypeTest {

    /**
     * The (name, type) columns of C's {@code el_powers[]}, in C order: the four basic elements are
     * {@code T_LRES} and the nine others {@code T_HRES}.
     */
    private static final Object[][] C_EL_POWER_TYPES = {
            {"acid", ResType.T_LRES},
            {"electricity", ResType.T_LRES},
            {"fire", ResType.T_LRES},
            {"cold", ResType.T_LRES},
            {"poison", ResType.T_HRES},
            {"light", ResType.T_HRES},
            {"dark", ResType.T_HRES},
            {"sound", ResType.T_HRES},
            {"shards", ResType.T_HRES},
            {"nexus", ResType.T_HRES},
            {"nether", ResType.T_HRES},
            {"chaos", ResType.T_HRES},
            {"disenchantment", ResType.T_HRES},
    };

    @Test
    @DisplayName("exactly two constants, T_LRES then T_HRES, as in C's enum")
    void ordinalsMatchCDeclarationOrder() {
        assertEquals(0, ResType.T_LRES.ordinal());
        assertEquals(1, ResType.T_HRES.ordinal());
        assertEquals(2, ResType.values().length);
    }

    @Test
    @DisplayName("constant names are C's identifiers")
    void namesMatchC() {
        assertEquals("T_LRES", ResType.T_LRES.name());
        assertEquals("T_HRES", ResType.T_HRES.name());
        assertSame(ResType.T_LRES, ResType.valueOf("T_LRES"));
        assertSame(ResType.T_HRES, ResType.valueOf("T_HRES"));
    }

    @Test
    @DisplayName("an unknown name is rejected, so no third group can sneak in")
    void unknownNameRejected() {
        assertThrows(IllegalArgumentException.class, () -> ResType.valueOf("T_NONE"));
        assertThrows(IllegalArgumentException.class, () -> ResType.valueOf("T_MRES"));
    }

    @Test
    @DisplayName("the two groups are distinct, which is all the join in elementPower relies on")
    void groupsAreDistinct() {
        assertNotSame(ResType.T_LRES, ResType.T_HRES);
        assertEquals(ResType.T_LRES, ResType.T_LRES);
        assertEquals(ResType.T_HRES, ResType.T_HRES);
    }

    @Test
    @DisplayName("an element row joins to a set row only when their types are equal")
    void setAndPowerRowsJoinOnType() {
        ElementSet lowResists = new ElementSet(ResType.T_LRES, 1, 1, 10, 4, 0, "low resists");
        ElementSet highResists = new ElementSet(ResType.T_HRES, 1, 2, 10, 9, 0, "high resists");
        ElementPowers fire = new ElementPowers(ElementEnum.ELEM_FIRE, "fire", ResType.T_LRES, 3, -6, 6, 40);
        ElementPowers poison = new ElementPowers(ElementEnum.ELEM_POIS, "poison", ResType.T_HRES, 0, 0, 28, 0);

        assertSame(lowResists.getType(), fire.getType());
        assertSame(highResists.getType(), poison.getType());
        assertNotSame(lowResists.getType(), poison.getType());
        assertNotSame(highResists.getType(), fire.getType());
    }

    @Test
    @DisplayName("C's el_powers[] groups: four low resists then nine high resists")
    void cElementPowerGroupsSplitFourAndNine() {
        long low = 0;
        long high = 0;
        for (Object[] row : C_EL_POWER_TYPES) {
            if (row[1] == ResType.T_LRES) {
                low++;
            } else {
                high++;
            }
        }
        // The sizes in element_sets[] are 4 (low resists) and 9 (high resists); the two must agree
        // with the el_powers[] group counts or a "full set" bonus could never be reached.
        assertEquals(4, low);
        assertEquals(9, high);
        assertEquals(13, C_EL_POWER_TYPES.length);
    }
}
