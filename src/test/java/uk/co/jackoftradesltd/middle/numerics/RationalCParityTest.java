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

package uk.co.jackoftradesltd.middle.numerics;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Checks {@link Rational} against C's {@code struct my_rational} and its functions in {@code z-util.c}:
 * {@code my_rational_construct}, {@code my_rational_product}, {@code my_rational_sum} and
 * {@code my_rational_to_uint}.
 *
 * <p>Every expected value below comes from the C functions, not from the Java. The C functions were compiled from
 * the original {@code z-util.c} and run on the same inputs, and the values are recorded here as C returned them.
 * That covers the ordinary path, the zero cases, and {@code my_rational_to_uint} with its remainder.
 *
 * <p>The port holds signed 63-bit values where C holds unsigned 32-bit ones (accepted deviation, 2026-10-14, in
 * {@code docs/Accepted_Deviations.md}), so C's own saturation figures cannot be copied across. Where C saturated at
 * {@code UINT_MAX} the same rule is applied at {@link Long#MAX_VALUE}, and the inputs that C would saturate but Java
 * holds exactly are pinned in {@link WiderRange} with the exact values worked out by hand. The approximation C uses
 * when an exact result will not fit is checked against the exact value, which does not depend on either
 * implementation.
 *
 * <p>Class RationalCParityTest coded on 261014, commented in full on 261014.
 *
 * @author Rowan Crowther
 */
class RationalCParityTest {

    /**
     * Asserts that a rational is in exactly the given canonical form.
     *
     * @param expectedNumerator   the numerator the rational should be reduced to
     * @param expectedDenominator the denominator the rational should be reduced to
     * @param actual              the rational under test
     */
    private static void assertFraction(long expectedNumerator, long expectedDenominator, Rational actual) {
        assertEquals(expectedNumerator, actual.getNumerator(), () -> "numerator of " + actual);
        assertEquals(expectedDenominator, actual.getDenominator(), () -> "denominator of " + actual);
    }

    /**
     * Asserts that an approximated result is within {@code tolerance} of the exact fraction
     * {@code exactNumerator / exactDenominator}, worked out in arbitrary precision.
     *
     * @param exactNumerator   the numerator of the exact result
     * @param exactDenominator the denominator of the exact result
     * @param tolerance        the largest allowed absolute difference
     * @param actual           the rational under test
     */
    private static void assertNear(BigInteger exactNumerator, BigInteger exactDenominator, double tolerance,
                                   Rational actual) {
        MathContext precision = new MathContext(60);
        BigDecimal exact = new BigDecimal(exactNumerator).divide(new BigDecimal(exactDenominator), precision);
        BigDecimal got = new BigDecimal(actual.getNumerator())
                .divide(new BigDecimal(actual.getDenominator()), precision);
        BigDecimal difference = got.subtract(exact).abs();
        assertTrue(difference.compareTo(BigDecimal.valueOf(tolerance)) <= 0,
                () -> actual + " is " + difference + " from the exact value " + exact);
    }

    /**
     * {@code my_rational_construct}: the pair is reduced and zero is stored as {@code 0/1}.
     */
    @Nested
    class ConstructAgainstC {

        @ParameterizedTest(name = "{0}/{1} reduces to {2}/{3}")
        @CsvSource({"0, 5, 0, 1", "6, 4, 3, 2", "7, 2, 7, 2", "22, 7, 22, 7", "355, 113, 355, 113",
                "100, 100, 1, 1", "6, 9, 2, 3"})
        void reducesToLowestTerms(long n, long d, long expectedN, long expectedD) {
            assertFraction(expectedN, expectedD, new Rational(n, d));
        }
    }

    /**
     * {@code my_rational_product} where the exact product fits, which is where C and Java agree exactly.
     */
    @Nested
    class ProductAgainstC {

        @ParameterizedTest(name = "{0}/{1} * {2}/{3} = {4}/{5}")
        @CsvSource({"1, 2, 1, 3, 1, 6", "2, 3, 3, 4, 1, 2", "3, 4, 4, 3, 1, 1", "10, 3, 9, 5, 6, 1",
                "0, 1, 3, 4, 0, 1", "3, 4, 0, 1, 0, 1", "65535, 1, 65537, 1, 4294967295, 1"})
        void matchesTheReturnedPair(long an, long ad, long bn, long bd, long expectedN, long expectedD) {
            assertFraction(expectedN, expectedD, new Rational(an, ad).multi(new Rational(bn, bd)));
        }

        @Test
        void zeroTimesAnythingIsTheCanonicalZeroWhicheverSideItIsOn() {
            // C returns 0/1 either way round, and the Java returns the shared zero instance.
            assertFraction(0, 1, Rational.zero.multi(new Rational(3, 4)));
            assertFraction(0, 1, new Rational(3, 4).multi(Rational.zero));
        }
    }

    /**
     * {@code my_rational_sum} where the exact sum fits, which is where C and Java agree exactly.
     */
    @Nested
    class SumAgainstC {

        @ParameterizedTest(name = "{0}/{1} + {2}/{3} = {4}/{5}")
        @CsvSource({"1, 2, 1, 3, 5, 6", "1, 6, 1, 3, 1, 2", "1, 2, 1, 2, 1, 1", "7, 12, 5, 18, 31, 36",
                "0, 1, 3, 4, 3, 4", "1, 3, 1, 6, 1, 2"})
        void matchesTheReturnedPair(long an, long ad, long bn, long bd, long expectedN, long expectedD) {
            assertFraction(expectedN, expectedD, new Rational(an, ad).add(new Rational(bn, bd)));
        }
    }

    /**
     * {@code my_rational_to_uint}: the returned whole number and the {@code remainder} out-parameter, which Java gets
     * from {@code multi(new Rational(scale)).getRemainder()}. C reports the remainder as a numerator over the
     * denominator of {@code a}, so the expected remainder is built the same way and compared by value.
     */
    @Nested
    class ToUintAgainstC {

        @ParameterizedTest(name = "{0}/{1} scaled by {2} is {3} remainder {4}/{1}")
        @CsvSource({"7, 2, 100, 350, 0", "1, 3, 100, 33, 1", "1, 6, 3, 0, 3", "1, 3, 0, 0, 0",
                "22, 7, 100, 314, 2", "5, 8, 1000, 625, 0", "1, 7, 1000, 142, 6", "355, 113, 1000, 3141, 67",
                "100, 3, 7, 233, 1", "2, 3, 3, 2, 0", "0, 1, 50, 0, 0", "9, 4, 1, 2, 1", "17, 5, 1, 3, 2",
                "1, 3, 10, 3, 1", "2, 3, 100, 66, 2"})
        void returnsTheWholeNumberAndTheRemainder(long n, long d, int scale, long expectedWhole,
                                                  long expectedRemainderNumerator) {
            Rational value = new Rational(n, d);

            assertEquals(expectedWhole, value.toUint(scale));
            assertEquals(new Rational(expectedRemainderNumerator, value.getDenominator()),
                    value.multi(new Rational(scale)).getRemainder());
        }
    }

    /**
     * The inputs where C saturates or approximates at 2^32 but the port holds the exact value (accepted deviation,
     * 2026-10-14). The expected pairs are worked out by hand.
     */
    @Nested
    class WiderRange {

        @Test
        void aSumPastTheUnsignedMaximumIsExact() {
            // C returns 4294967295/1 for both of these.
            assertFraction(4294967296L, 1, new Rational(4294967295L).add(new Rational(1)));
            assertFraction(5000000000L, 1, new Rational(4000000000L).add(new Rational(1000000000L)));
        }

        @Test
        void aProductPastTheUnsignedMaximumIsExact() {
            // C returns 4294967295/1 for 100000 * 100000.
            assertFraction(10000000000L, 1, new Rational(100000).multi(new Rational(100000)));
            assertFraction(12000000000L, 1, new Rational(4000000000L).multi(new Rational(3)));
        }

        @Test
        void aProductWhoseNumeratorPassesTheUnsignedMaximumIsExact() {
            // C approximates this as 4285757143/9; the exact product is 10000100000/21.
            assertFraction(10000100000L, 21, new Rational(100000, 3).multi(new Rational(100001, 7)));
        }

        @Test
        void toUintDoesNotSaturateAtTheUnsignedMaximum() {
            // C returns 4294967295 with a remainder of 0.
            assertEquals(8589934590L, new Rational(4294967295L).toUint(2));
        }
    }

    /**
     * The approximation used when an exact result does not fit in a {@code long}. C does the same at 2^32; here the
     * result is checked against the exact value in arbitrary precision.
     */
    @Nested
    class Approximation {

        private static final long P = 4294967291L;
        private static final long Q = 4294967279L;

        @Test
        void aProductWithATinyFractionIsWithinOneStepOfTheExactValue() {
            // 15 / (P * Q): the denominator needs 64 bits, so the whole part is 0 and the widest denominator is used.
            Rational product = new Rational(3, P).multi(new Rational(5, Q));

            assertNear(BigInteger.valueOf(15), BigInteger.valueOf(P).multiply(BigInteger.valueOf(Q)),
                    2.0 / Long.MAX_VALUE, product);
        }

        @Test
        void aSumWithATinyFractionIsWithinOneStepOfTheExactValue() {
            Rational sum = new Rational(1, P).add(new Rational(1, Q));

            assertNear(BigInteger.valueOf(P + Q), BigInteger.valueOf(P).multiply(BigInteger.valueOf(Q)),
                    2.0 / Long.MAX_VALUE, sum);
        }

        @Test
        void aProductWithAWholePartKeepsAFractionOfTheRemainingPrecision() {
            // 4e9/3 * 4e9/7 = 1.6e19/21, whose numerator needs 64 bits. The whole part is about 7.6e17, which leaves
            // a denominator of about 12 for the fraction, so the result is within a twelfth of the exact value.
            Rational product = new Rational(4000000000L, 3).multi(new Rational(4000000000L, 7));

            assertNear(new BigInteger("16000000000000000000"), BigInteger.valueOf(21), 0.1, product);
        }

        @Test
        void negatingTheOperandsNegatesTheApproximation() {
            Rational positive = new Rational(3, P).multi(new Rational(5, Q));
            Rational negative = new Rational(-3, P).multi(new Rational(5, Q));

            assertFraction(-positive.getNumerator(), positive.getDenominator(), negative);
        }
    }

    /**
     * The saturation rules, which C applies at {@code UINT_MAX} and the port applies at {@link Long#MAX_VALUE}. C
     * returns {@code UINT_MAX/1} for {@code UINT_MAX + 1}, for {@code 4000000000 + 1000000000} and for
     * {@code 4000000000 * 3}.
     */
    @Nested
    class Saturation {

        private static final long MAX = Long.MAX_VALUE;

        @Test
        void aSumOverTheMaximumSaturates() {
            assertFraction(MAX, 1, new Rational(MAX).add(new Rational(1)));
            assertFraction(MAX, 1, new Rational(MAX).add(new Rational(MAX)));
        }

        @Test
        void aSumJustUnderTheMaximumIsExact() {
            // C: 4294967294 + 1 = 4294967295/1.
            assertFraction(MAX, 1, new Rational(MAX - 1).add(new Rational(1)));
        }

        @Test
        void twoHalvesOfTheMaximumMakeTheMaximum() {
            // C: 4294967295/2 + 4294967295/2 = 4294967295/1.
            assertFraction(MAX, 1, new Rational(MAX, 2).add(new Rational(MAX, 2)));
        }

        @Test
        void aProductOverTheMaximumSaturates() {
            assertFraction(MAX, 1, new Rational(MAX).multi(new Rational(2)));
            assertFraction(MAX, 1, new Rational(MAX).multi(new Rational(MAX)));
        }

        @Test
        void saturationKeepsTheSignOfTheResult() {
            // C is unsigned, so this is the port's own rule: the whole part saturates and the sign is put back.
            assertFraction(-MAX, 1, new Rational(-MAX).multi(new Rational(2)));
            assertFraction(-MAX, 1, new Rational(-MAX).add(new Rational(-1)));
            assertFraction(MAX, 1, new Rational(-MAX).multi(new Rational(-2)));
        }

        @Test
        void toUintSaturatesThroughTheProduct() {
            assertEquals(MAX, new Rational(MAX).toUint(2));
        }

        @Test
        void aProductWholePartOfExactlyTheMaximumWithARoundingBitCannotBeHeld() {
            // (2^32 - 1) * (2^32 + 1) / 2 is (2^64 - 1) / 2: whole part 2^63 - 1, remainder 1. The rounding step then
            // wants to add one. C's 32-bit equivalent, 7 * 1227133513/2, fails assert(rn < UINT_MAX); the port throws.
            Rational left = new Rational(4294967295L);
            Rational right = new Rational(4294967297L, 2);

            assertThrows(ArithmeticException.class, () -> left.multi(right));
        }
    }
}
