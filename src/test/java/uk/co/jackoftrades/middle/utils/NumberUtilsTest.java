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

package uk.co.jackoftrades.middle.utils;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.co.jackoftrades.middle.numerics.Rational;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for {@link NumberUtils}, the port of the arithmetic helpers in the C source's
 * {@code src/z-util.c}.
 *
 * <p>The "guard" methods are the interesting half. In C an overflowing addition wraps
 * silently and corrupts whatever game value it was computing; these ports saturate at the
 * type's limit instead. Saturation is only observable at the boundary, so the tests
 * concentrate on the four corners - overflow, underflow, and the largest values that must
 * still be computed exactly - rather than on the middle of the range where any
 * implementation looks correct. The 16-bit variants clamp to the C {@code int16_t} range
 * while still being handed and returning Java {@code int}s, so their limits have to be
 * asserted as the literals -32768 and 32767 rather than inferred from the Java type.
 *
 * <p>{@link NumberUtils#mean} and {@link NumberUtils#variance} return {@link Rational}s, so
 * they are checked against exact fractions: a mean of 5/2 is a legitimate answer here and
 * must not be rounded on the way out.
 *
 * @author Rowan Crowther
 */
class NumberUtilsTest {

    /**
     * Builds a mutable list of ints, since the methods under test take {@link ArrayList}.
     *
     * @param values the values to place in the list, in order
     * @return a new list holding those values
     */
    private static ArrayList<Integer> listOf(int... values) {
        ArrayList<Integer> result = new ArrayList<>();
        for (int value : values) {
            result.add(value);
        }
        return result;
    }

    /**
     * Asserts that a rational reduced to lowest terms is the given fraction.
     *
     * @param expectedNumerator   the expected numerator
     * @param expectedDenominator the expected denominator
     * @param actual              the rational under test
     */
    private static void assertFraction(int expectedNumerator, int expectedDenominator,
                                       Rational actual) {
        assertEquals(new Rational(expectedNumerator, expectedDenominator), actual,
                () -> "expected " + expectedNumerator + " / " + expectedDenominator
                        + " but was " + actual);
    }

    /**
     * Saturating 32-bit addition and subtraction.
     */
    @Nested
    class IntegerGuards {

        @Test
        void ordinaryArithmeticIsUnaffected() {
            assertEquals(7, NumberUtils.addGuardI(3, 4));
            assertEquals(-1, NumberUtils.addGuardI(3, -4));
            assertEquals(-1, NumberUtils.subGuardI(3, 4));
            assertEquals(7, NumberUtils.subGuardI(3, -4));
        }

        @Test
        void additionSaturatesAtTheTopOfTheRange() {
            assertEquals(Integer.MAX_VALUE, NumberUtils.addGuardI(Integer.MAX_VALUE, 1));
            assertEquals(Integer.MAX_VALUE,
                    NumberUtils.addGuardI(Integer.MAX_VALUE, Integer.MAX_VALUE));
        }

        @Test
        void additionSaturatesAtTheBottomOfTheRange() {
            assertEquals(Integer.MIN_VALUE, NumberUtils.addGuardI(Integer.MIN_VALUE, -1));
            assertEquals(Integer.MIN_VALUE,
                    NumberUtils.addGuardI(Integer.MIN_VALUE, Integer.MIN_VALUE));
        }

        @Test
        void theLargestExactAdditionsAreStillComputed() {
            // One short of overflow in each direction: the guard must not clamp early.
            assertEquals(Integer.MAX_VALUE, NumberUtils.addGuardI(Integer.MAX_VALUE - 1, 1));
            assertEquals(Integer.MIN_VALUE, NumberUtils.addGuardI(Integer.MIN_VALUE + 1, -1));
        }

        @Test
        void mixedSignAdditionCanNeverOverflowAndIsPassedStraightThrough() {
            assertEquals(-1, NumberUtils.addGuardI(Integer.MAX_VALUE, Integer.MIN_VALUE));
        }

        @Test
        void subtractionSaturatesAtTheTopOfTheRange() {
            assertEquals(Integer.MAX_VALUE, NumberUtils.subGuardI(Integer.MAX_VALUE, -1));
            assertEquals(Integer.MAX_VALUE,
                    NumberUtils.subGuardI(Integer.MAX_VALUE, Integer.MIN_VALUE));
        }

        @Test
        void subtractionSaturatesAtTheBottomOfTheRange() {
            assertEquals(Integer.MIN_VALUE, NumberUtils.subGuardI(Integer.MIN_VALUE, 1));
            assertEquals(Integer.MIN_VALUE,
                    NumberUtils.subGuardI(Integer.MIN_VALUE, Integer.MAX_VALUE));
        }

        @Test
        void theLargestExactSubtractionsAreStillComputed() {
            assertEquals(Integer.MAX_VALUE, NumberUtils.subGuardI(Integer.MAX_VALUE - 1, -1));
            assertEquals(Integer.MIN_VALUE, NumberUtils.subGuardI(Integer.MIN_VALUE + 1, 1));
        }

        @Test
        void zeroIsAnIdentityForBothGuards() {
            assertEquals(Integer.MAX_VALUE, NumberUtils.addGuardI(Integer.MAX_VALUE, 0));
            assertEquals(Integer.MIN_VALUE, NumberUtils.addGuardI(Integer.MIN_VALUE, 0));
            assertEquals(Integer.MAX_VALUE, NumberUtils.subGuardI(Integer.MAX_VALUE, 0));
            assertEquals(Integer.MIN_VALUE, NumberUtils.subGuardI(Integer.MIN_VALUE, 0));
        }
    }

    /**
     * Saturating arithmetic clamped to the C {@code int16_t} range.
     */
    @Nested
    class SixteenBitGuards {

        /**
         * The largest value a C {@code int16_t} can hold.
         */
        private static final int MAX_I16 = 32767;
        /**
         * The smallest value a C {@code int16_t} can hold.
         */
        private static final int MIN_I16 = -32768;

        @Test
        void ordinaryArithmeticIsUnaffected() {
            assertEquals(7, NumberUtils.addGuardI16(3, 4));
            assertEquals(-1, NumberUtils.subGuardI16(3, 4));
        }

        @Test
        void additionClampsToTheSixteenBitLimits() {
            assertEquals(MAX_I16, NumberUtils.addGuardI16(MAX_I16, 1));
            assertEquals(MAX_I16, NumberUtils.addGuardI16(30000, 30000));
            assertEquals(MIN_I16, NumberUtils.addGuardI16(MIN_I16, -1));
            assertEquals(MIN_I16, NumberUtils.addGuardI16(-30000, -30000));
        }

        @Test
        void subtractionClampsToTheSixteenBitLimits() {
            assertEquals(MAX_I16, NumberUtils.subGuardI16(MAX_I16, -1));
            assertEquals(MAX_I16, NumberUtils.subGuardI16(30000, -30000));
            assertEquals(MIN_I16, NumberUtils.subGuardI16(MIN_I16, 1));
            assertEquals(MIN_I16, NumberUtils.subGuardI16(-30000, 30000));
        }

        @Test
        void theLimitsThemselvesAreReachableExactly() {
            assertEquals(MAX_I16, NumberUtils.addGuardI16(MAX_I16 - 1, 1));
            assertEquals(MIN_I16, NumberUtils.addGuardI16(MIN_I16 + 1, -1));
            assertEquals(MAX_I16, NumberUtils.subGuardI16(MAX_I16 - 1, -1));
            assertEquals(MIN_I16, NumberUtils.subGuardI16(MIN_I16 + 1, 1));
        }

        @Test
        void mixedSignsPassStraightThroughWhenTheyStayInRange() {
            assertEquals(-1, NumberUtils.addGuardI16(MAX_I16, MIN_I16));
            assertEquals(MAX_I16, NumberUtils.subGuardI16(MAX_I16, MIN_I16 + 1));
        }
    }

    /**
     * The exact-arithmetic mean.
     */
    @Nested
    class Mean {

        @Test
        void meanOfAWholeList() {
            assertFraction(3, 1, NumberUtils.mean(listOf(1, 2, 3, 4, 5), 5));
        }

        @Test
        void aFractionalMeanIsKeptExact() {
            // 10/4 must survive as 5/2 rather than being rounded to 2 on the way out.
            assertFraction(5, 2, NumberUtils.mean(listOf(1, 2, 3, 4), 4));
        }

        @Test
        void onlyTheFirstSizeElementsAreUsed() {
            assertFraction(2, 1, NumberUtils.mean(listOf(1, 2, 3, 400, 500), 3));
        }

        @Test
        void anOversizedSizeIsTruncatedToTheListLength() {
            assertFraction(2, 1, NumberUtils.mean(listOf(1, 2, 3), 99));
        }

        @Test
        void aSizeOfZeroOrAnEmptyListGivesZero() {
            assertFraction(0, 1, NumberUtils.mean(listOf(1, 2, 3), 0));
            assertFraction(0, 1, NumberUtils.mean(new ArrayList<>(), 5));
        }

        @Test
        void negativeValuesAreAveragedNormally() {
            assertFraction(-1, 1, NumberUtils.mean(listOf(-3, -1, 1), 3));
        }

        @Test
        void theInputListIsNotModified() {
            ArrayList<Integer> numbers = listOf(1, 2, 3, 4);

            NumberUtils.mean(numbers, 2);

            assertEquals(List.of(1, 2, 3, 4), numbers);
        }
    }

    /**
     * The exact-arithmetic variance, in its biased, unbiased and "of the mean" forms.
     */
    @Nested
    class Variance {

        /**
         * A sample whose population variance is exactly 4, chosen so the biased result is a
         * whole number and any accidental rounding in the unbiased form (32/7) stands out.
         */
        private static final int[] SAMPLE = {2, 4, 4, 4, 5, 5, 7, 9};

        @Test
        void biasedVarianceDividesBySize() {
            assertFraction(4, 1,
                    NumberUtils.variance(listOf(SAMPLE), SAMPLE.length, false, false));
        }

        @Test
        void unbiasedVarianceDividesBySizeMinusOne() {
            assertFraction(32, 7,
                    NumberUtils.variance(listOf(SAMPLE), SAMPLE.length, true, false));
        }

        @Test
        void varianceOfTheMeanDividesByTheSizeAgain() {
            assertFraction(1, 2,
                    NumberUtils.variance(listOf(SAMPLE), SAMPLE.length, false, true));
            assertFraction(4, 7,
                    NumberUtils.variance(listOf(SAMPLE), SAMPLE.length, true, true));
        }

        @Test
        void aConstantSampleHasNoVariance() {
            assertFraction(0, 1, NumberUtils.variance(listOf(5, 5, 5, 5), 4, false, false));
        }

        @Test
        void aSizeOfOneOrLessGivesZeroRatherThanDividingByZero() {
            assertFraction(0, 1, NumberUtils.variance(listOf(1, 2, 3), 1, true, false));
            assertFraction(0, 1, NumberUtils.variance(listOf(1, 2, 3), 0, false, false));
            assertFraction(0, 1, NumberUtils.variance(listOf(1, 2, 3), -1, false, false));
        }

        @Test
        void onlyTheFirstSizeElementsAreUsed() {
            // The tail is wild enough that including it could not possibly still give 2/3.
            assertFraction(2, 3,
                    NumberUtils.variance(listOf(1, 2, 3, 1000, -1000), 3, false, false));
        }

        @Test
        void theInputListIsNotModified() {
            ArrayList<Integer> numbers = listOf(1, 2, 3, 4);

            NumberUtils.variance(numbers, 4, false, false);

            assertEquals(List.of(1, 2, 3, 4), numbers);
        }
    }
}
