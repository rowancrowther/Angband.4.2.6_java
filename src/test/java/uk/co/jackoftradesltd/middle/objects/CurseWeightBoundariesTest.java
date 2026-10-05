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
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.testsupport.CurseFixture;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * The boundary values of {@link Curse#modifyWeightForCurse(int)} that {@link CurseModifyWeightTest}
 * does not reach, each worked out by hand from C's {@code modify_weight_for_curse} in
 * {@code obj-curse.c} rather than read off the port.
 *
 * <p>The ordinary path, the rounding direction, the weightless coercion, the negative multiplier and
 * one saturation case are already covered there. What is left is where C's comparisons sit exactly:
 * the strict {@code > 100} that decides whether a weightless item is coerced, the strict
 * {@code < 32767} on the multiplied quotient, the strict {@code <} in the additive ceiling, the
 * {@code >= 50} remainder test on either side of 50, and an incoming weight that is already negative,
 * which C coerces to zero (or, for a multiplier above 100%, to one) before using it.
 *
 * <p>Class CurseWeightBoundariesTest coded on 261005, commented in full on 261005.
 *
 * @author Rowan Crowther
 */
class CurseWeightBoundariesTest {

    /**
     * A curse whose only interesting properties are its weight and whether it multiplies.
     *
     * @param weight   the curse's weight field
     * @param multiply whether it carries {@link ObjectFlag#OF_MULTIPLY_WEIGHT}
     * @return the curse
     */
    private static Curse curse(int weight, boolean multiply) {
        Flag<ObjectFlag> flags = new Flag<>(ObjectFlag.class);
        if (multiply) flags.set(List.of(ObjectFlag.OF_MULTIPLY_WEIGHT));
        return CurseFixture.curse("weighty", List.of(), weight, null, flags,
                Map.of(), Map.of(), 0, 0, 0, List.of(), new Flag<>(ObjectFlag.class), "", "", 0);
    }

    /**
     * The multiplicative branch's comparisons.
     */
    @Nested
    @DisplayName("with OF_MULTIPLY_WEIGHT")
    class Multiplicative {

        /**
         * C tests {@code curse_obj->weight > 100}, strictly. At exactly 100 a weightless item is not
         * coerced up to one, so it stays weightless; at 101 it is, and 1 * 101 / 100 is 1 with a
         * remainder of 1, which does not round up.
         */
        @Test
        @DisplayName("the coercion needs a factor strictly above 100%")
        void coercionThreshold() {
            assertAll(
                    () -> assertEquals(0, curse(100, true).modifyWeightForCurse(0)),
                    () -> assertEquals(1, curse(101, true).modifyWeightForCurse(0)));
        }

        /**
         * C rounds up when {@code scaled % 100 >= 50}. One at 149% is 149, remainder 49, which
         * stays at 1; one at 150% is 150, remainder 50, which becomes 2.
         */
        @Test
        @DisplayName("a remainder of 49 rounds down and 50 rounds up")
        void roundingEitherSideOfFifty() {
            assertAll(
                    () -> assertEquals(1, curse(149, true).modifyWeightForCurse(1)),
                    () -> assertEquals(2, curse(150, true).modifyWeightForCurse(1)));
        }

        /**
         * A zero percentage is legal ({@code assert(curse_obj->weight >= 0)} passes) and removes all
         * the weight.
         */
        @Test
        @DisplayName("a factor of zero leaves nothing")
        void zeroFactor() {
            assertEquals(0, curse(0, true).modifyWeightForCurse(50));
        }

        /**
         * C coerces the incoming weight with {@code MAX(weight, 1)} for a factor above 100%, so a
         * negative item weight is treated as one: 1 * 200 / 100 is 2. At or below 100% the coercion
         * is to zero, so the result is zero.
         */
        @Test
        @DisplayName("a negative incoming weight is coerced before it is scaled")
        void negativeIncomingWeight() {
            assertAll(
                    () -> assertEquals(2, curse(200, true).modifyWeightForCurse(-5)),
                    () -> assertEquals(0, curse(50, true).modifyWeightForCurse(-5)));
        }

        /**
         * C saturates when the quotient reaches 32767, not when it exceeds it, and does so without
         * the rounding step. 32767 at 100% has a quotient of exactly 32767 and answers 32767; 32766
         * at 100% has a quotient of 32766 and answers 32766; and 32767 at 10000% has a quotient
         * of 3276700 and answers 32767.
         */
        @Test
        @DisplayName("a quotient of 32767 or more saturates")
        void saturation() {
            assertAll(
                    () -> assertEquals(32767, curse(100, true).modifyWeightForCurse(32767)),
                    () -> assertEquals(32766, curse(100, true).modifyWeightForCurse(32766)),
                    () -> assertEquals(32767, curse(10000, true).modifyWeightForCurse(32767)));
        }

        /**
         * A multiplier below 100% on a heavy item, to confirm the quotient is not clamped to the
         * incoming weight: 32767 * 99 = 3243933, quotient 32439, remainder 33, so 32439.
         */
        @Test
        @DisplayName("a factor under 100% on a heavy item is not clamped")
        void heavyItemUnderFactor() {
            assertEquals(32439, curse(99, true).modifyWeightForCurse(32767));
        }
    }

    /**
     * The additive branch's comparisons.
     */
    @Nested
    @DisplayName("without OF_MULTIPLY_WEIGHT")
    class Additive {

        /**
         * C's ceiling test is {@code weight < 32767 - curse_obj->weight}, strictly. 32759 + 7 passes
         * it and answers 32766; 32760 + 7 fails it and answers 32767, which is also the sum, so the
         * saturation is exercised from the failing side.
         */
        @Test
        @DisplayName("the ceiling comparison is strict")
        void ceilingComparison() {
            assertAll(
                    () -> assertEquals(32766, curse(7, false).modifyWeightForCurse(32759)),
                    () -> assertEquals(32767, curse(7, false).modifyWeightForCurse(32760)),
                    () -> assertEquals(32767, curse(8, false).modifyWeightForCurse(32760)));
        }

        /**
         * A zero addend changes nothing, including at the very top where the strict comparison
         * {@code 32767 < 32767} fails and C answers the ceiling, which is the same number.
         */
        @Test
        @DisplayName("a zero addend leaves the weight alone")
        void zeroAddend() {
            assertAll(
                    () -> assertEquals(100, curse(0, false).modifyWeightForCurse(100)),
                    () -> assertEquals(32767, curse(0, false).modifyWeightForCurse(32767)));
        }

        /**
         * A reduction of exactly the item's weight leaves nothing; one more is clamped, because C
         * tests {@code result < 0} after the subtraction.
         */
        @Test
        @DisplayName("a reduction of exactly the weight gives zero, and one more still gives zero")
        void exactReduction() {
            assertAll(
                    () -> assertEquals(0, curse(-10, false).modifyWeightForCurse(10)),
                    () -> assertEquals(0, curse(-11, false).modifyWeightForCurse(10)));
        }

        /**
         * C replaces the incoming weight with {@code MAX(0, weight)} before adding, so a negative
         * item weight starts from zero: a +3 curse gives 3, and a -3 curse gives zero rather than
         * -3 or -8.
         */
        @Test
        @DisplayName("a negative incoming weight starts from zero")
        void negativeIncomingWeight() {
            assertAll(
                    () -> assertEquals(3, curse(3, false).modifyWeightForCurse(-5)),
                    () -> assertEquals(0, curse(-3, false).modifyWeightForCurse(-5)));
        }
    }
}
