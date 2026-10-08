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
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.testsupport.CurseFixture;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * The two corners of {@link Curse} that the other {@code Curse*Test} classes leave open: the
 * rounding step sitting right under the {@code 32767} ceiling of C's
 * {@code modify_weight_for_curse} ({@code obj-curse.c}), and the index being replaceable through
 * {@link Curse#setIndex(int)}, which {@code CurseAssembler} relies on after it reverses its list
 * into C's order.
 *
 * <p>The weight cases were chosen by hand from C's arithmetic: with {@code scaled = weight * factor},
 * C takes {@code q = scaled / 100} and, only when {@code q < 32767}, adds one if
 * {@code scaled % 100 >= 50}. So {@code q = 32766} is the last quotient that can round, and it can
 * round up to exactly 32767 without ever taking the saturating branch.
 *
 * <p>Class CurseIndexAndRoundingTest coded on 261008, commented in full on 261008.
 *
 * @author Rowan Crowther
 */
class CurseIndexAndRoundingTest {

    /**
     * A multiplying curse with the given percentage.
     *
     * @param percent the curse's weight field, read as a percentage
     * @return the curse
     */
    private static Curse multiplier(int percent) {
        Flag<ObjectFlag> flags = new Flag<>(ObjectFlag.class);
        flags.set(List.of(ObjectFlag.OF_MULTIPLY_WEIGHT));
        return CurseFixture.curse("heavy", List.of(), percent, null, flags,
                Map.of(), Map.of(), 0, 0, 0, List.of(), new Flag<>(ObjectFlag.class), "", "", 0);
    }

    /**
     * 25205 * 130 is 3276650: a quotient of 32766 with a remainder of exactly 50. C's
     * {@code q < 32767} is true, so it rounds up to 32767 by the increment rather than by the
     * ceiling. 30061 * 109 is 3276649: the same quotient with a remainder of 49, which stays
     * at 32766.
     */
    @Test
    @DisplayName("the last quotient below the ceiling still rounds at the 50 boundary")
    void roundsUnderTheCeiling() {
        assertAll(
                () -> assertEquals(32767, multiplier(130).modifyWeightForCurse(25205)),
                () -> assertEquals(32766, multiplier(109).modifyWeightForCurse(30061)));
    }

    /**
     * The index the constructor was given is the one reported until {@code setIndex} replaces it,
     * and the replacement is exactly the value passed (no offset, no validation).
     */
    @Test
    @DisplayName("setIndex replaces the index the constructor gave")
    void setIndexReplacesIndex() {
        Curse curse = CurseFixture.curse("vulnerability", List.of(), 0, null,
                new Flag<>(ObjectFlag.class), Map.of(), Map.of(), 0, 0, 0, List.of(),
                new Flag<>(ObjectFlag.class), "", "", 7);
        assertEquals(7, curse.getIndex());

        curse.setIndex(0);
        assertEquals(0, curse.getIndex());

        curse.setIndex(253);
        assertEquals(253, curse.getIndex());
    }
}
