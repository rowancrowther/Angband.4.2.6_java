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

package uk.co.jackoftradesltd.middle.utils;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.middle.numerics.Rational;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link NumberUtils} against the values the compiled C of {@code z-util.c} returned, rather than against values worked
 * out from the Java. {@link NumberUtilsTest} checks the behaviour; this class checks that behaviour is C's.
 *
 * <p>The expected figures come from a small C driver linked against Angband 4.2.6's own {@code z-util.c}. For the four
 * guards it printed the result of every pair drawn from the values at and around {@code INT_MIN}, {@code INT_MAX},
 * -32768 and 32767. For {@code mean} and {@code variance} it printed the integer and the {@code struct my_rational}
 * that C hands back through {@code frac}. The Java returns one {@link Rational}, so each expected value is folded back
 * together as {@code integer + n/d}.
 *
 * <p>Three places where the Java does not give C's answer are recorded rather than hidden. A variance between
 * {@code INT_MAX} and {@code INT_MAX + 1} is the accepted deviation in {@code docs/Accepted_Deviations.md}; no integer
 * input found reaches it (two entries cannot, and a search over three found none), so there is no C figure to pin and
 * no test. A fraction past {@code UINT_MAX} is exact in the Java and approximate in C, which is the {@link Rational}
 * accepted deviation. A fraction past 63 bits makes the Java throw, which is not decided; see
 * {@code LargeLists.aFractionTooBigForALongThrowsWhereCApproximates}.
 *
 * <p>Class NumberUtilsCParityTest coded on 261014, commented in full on 261014.
 *
 * @author Rowan Crowther
 */
class NumberUtilsCParityTest {
    private static final int MAX_I = Integer.MAX_VALUE;
    private static final int MIN_I = Integer.MIN_VALUE;

    /**
     * Every pair of {@code {a, b, add_guardi(a, b), sub_guardi(a, b)}} that the C driver printed, taken from the
     * values {@code INT_MIN}, {@code INT_MIN + 1}, -100000, -32769, -32768, -32767, -1, 0, 1, 32766, 32767, 32768,
     * 100000, {@code INT_MAX - 1} and {@code INT_MAX}.
     */
    private static final int[][] GUARD_32 = {
            {MIN_I, MIN_I, MIN_I, 0}, {MIN_I, -2147483647, MIN_I, -1}, {MIN_I, -100000, MIN_I, -2147383648},
            {MIN_I, -32769, MIN_I, -2147450879}, {MIN_I, -32768, MIN_I, -2147450880}, {MIN_I, -32767, MIN_I, -2147450881},
            {MIN_I, -1, MIN_I, -2147483647}, {MIN_I, 0, MIN_I, MIN_I}, {MIN_I, 1, -2147483647, MIN_I},
            {MIN_I, 32766, -2147450882, MIN_I}, {MIN_I, 32767, -2147450881, MIN_I}, {MIN_I, 32768, -2147450880, MIN_I},
            {MIN_I, 100000, -2147383648, MIN_I}, {MIN_I, 2147483646, -2, MIN_I}, {MIN_I, MAX_I, -1, MIN_I},
            {-2147483647, MIN_I, MIN_I, 1}, {-2147483647, -2147483647, MIN_I, 0}, {-2147483647, -100000, MIN_I, -2147383647},
            {-2147483647, -32769, MIN_I, -2147450878}, {-2147483647, -32768, MIN_I, -2147450879}, {-2147483647, -32767, MIN_I, -2147450880},
            {-2147483647, -1, MIN_I, -2147483646}, {-2147483647, 0, -2147483647, -2147483647}, {-2147483647, 1, -2147483646, MIN_I},
            {-2147483647, 32766, -2147450881, MIN_I}, {-2147483647, 32767, -2147450880, MIN_I}, {-2147483647, 32768, -2147450879, MIN_I},
            {-2147483647, 100000, -2147383647, MIN_I}, {-2147483647, 2147483646, -1, MIN_I}, {-2147483647, MAX_I, 0, MIN_I},
            {-100000, MIN_I, MIN_I, 2147383648}, {-100000, -2147483647, MIN_I, 2147383647}, {-100000, -100000, -200000, 0},
            {-100000, -32769, -132769, -67231}, {-100000, -32768, -132768, -67232}, {-100000, -32767, -132767, -67233},
            {-100000, -1, -100001, -99999}, {-100000, 0, -100000, -100000}, {-100000, 1, -99999, -100001},
            {-100000, 32766, -67234, -132766}, {-100000, 32767, -67233, -132767}, {-100000, 32768, -67232, -132768},
            {-100000, 100000, 0, -200000}, {-100000, 2147483646, 2147383646, MIN_I}, {-100000, MAX_I, 2147383647, MIN_I},
            {-32769, MIN_I, MIN_I, 2147450879}, {-32769, -2147483647, MIN_I, 2147450878}, {-32769, -100000, -132769, 67231},
            {-32769, -32769, -65538, 0}, {-32769, -32768, -65537, -1}, {-32769, -32767, -65536, -2},
            {-32769, -1, -32770, -32768}, {-32769, 0, -32769, -32769}, {-32769, 1, -32768, -32770},
            {-32769, 32766, -3, -65535}, {-32769, 32767, -2, -65536}, {-32769, 32768, -1, -65537},
            {-32769, 100000, 67231, -132769}, {-32769, 2147483646, 2147450877, MIN_I}, {-32769, MAX_I, 2147450878, MIN_I},
            {-32768, MIN_I, MIN_I, 2147450880}, {-32768, -2147483647, MIN_I, 2147450879}, {-32768, -100000, -132768, 67232},
            {-32768, -32769, -65537, 1}, {-32768, -32768, -65536, 0}, {-32768, -32767, -65535, -1},
            {-32768, -1, -32769, -32767}, {-32768, 0, -32768, -32768}, {-32768, 1, -32767, -32769},
            {-32768, 32766, -2, -65534}, {-32768, 32767, -1, -65535}, {-32768, 32768, 0, -65536},
            {-32768, 100000, 67232, -132768}, {-32768, 2147483646, 2147450878, MIN_I}, {-32768, MAX_I, 2147450879, MIN_I},
            {-32767, MIN_I, MIN_I, 2147450881}, {-32767, -2147483647, MIN_I, 2147450880}, {-32767, -100000, -132767, 67233},
            {-32767, -32769, -65536, 2}, {-32767, -32768, -65535, 1}, {-32767, -32767, -65534, 0},
            {-32767, -1, -32768, -32766}, {-32767, 0, -32767, -32767}, {-32767, 1, -32766, -32768},
            {-32767, 32766, -1, -65533}, {-32767, 32767, 0, -65534}, {-32767, 32768, 1, -65535},
            {-32767, 100000, 67233, -132767}, {-32767, 2147483646, 2147450879, MIN_I}, {-32767, MAX_I, 2147450880, MIN_I},
            {-1, MIN_I, MIN_I, MAX_I}, {-1, -2147483647, MIN_I, 2147483646}, {-1, -100000, -100001, 99999},
            {-1, -32769, -32770, 32768}, {-1, -32768, -32769, 32767}, {-1, -32767, -32768, 32766},
            {-1, -1, -2, 0}, {-1, 0, -1, -1}, {-1, 1, 0, -2},
            {-1, 32766, 32765, -32767}, {-1, 32767, 32766, -32768}, {-1, 32768, 32767, -32769},
            {-1, 100000, 99999, -100001}, {-1, 2147483646, 2147483645, -2147483647}, {-1, MAX_I, 2147483646, MIN_I},
            {0, MIN_I, MIN_I, MAX_I}, {0, -2147483647, -2147483647, MAX_I}, {0, -100000, -100000, 100000},
            {0, -32769, -32769, 32769}, {0, -32768, -32768, 32768}, {0, -32767, -32767, 32767},
            {0, -1, -1, 1}, {0, 0, 0, 0}, {0, 1, 1, -1},
            {0, 32766, 32766, -32766}, {0, 32767, 32767, -32767}, {0, 32768, 32768, -32768},
            {0, 100000, 100000, -100000}, {0, 2147483646, 2147483646, -2147483646}, {0, MAX_I, MAX_I, -2147483647},
            {1, MIN_I, -2147483647, MAX_I}, {1, -2147483647, -2147483646, MAX_I}, {1, -100000, -99999, 100001},
            {1, -32769, -32768, 32770}, {1, -32768, -32767, 32769}, {1, -32767, -32766, 32768},
            {1, -1, 0, 2}, {1, 0, 1, 1}, {1, 1, 2, 0},
            {1, 32766, 32767, -32765}, {1, 32767, 32768, -32766}, {1, 32768, 32769, -32767},
            {1, 100000, 100001, -99999}, {1, 2147483646, MAX_I, -2147483645}, {1, MAX_I, MAX_I, -2147483646},
            {32766, MIN_I, -2147450882, MAX_I}, {32766, -2147483647, -2147450881, MAX_I}, {32766, -100000, -67234, 132766},
            {32766, -32769, -3, 65535}, {32766, -32768, -2, 65534}, {32766, -32767, -1, 65533},
            {32766, -1, 32765, 32767}, {32766, 0, 32766, 32766}, {32766, 1, 32767, 32765},
            {32766, 32766, 65532, 0}, {32766, 32767, 65533, -1}, {32766, 32768, 65534, -2},
            {32766, 100000, 132766, -67234}, {32766, 2147483646, MAX_I, -2147450880}, {32766, MAX_I, MAX_I, -2147450881},
            {32767, MIN_I, -2147450881, MAX_I}, {32767, -2147483647, -2147450880, MAX_I}, {32767, -100000, -67233, 132767},
            {32767, -32769, -2, 65536}, {32767, -32768, -1, 65535}, {32767, -32767, 0, 65534},
            {32767, -1, 32766, 32768}, {32767, 0, 32767, 32767}, {32767, 1, 32768, 32766},
            {32767, 32766, 65533, 1}, {32767, 32767, 65534, 0}, {32767, 32768, 65535, -1},
            {32767, 100000, 132767, -67233}, {32767, 2147483646, MAX_I, -2147450879}, {32767, MAX_I, MAX_I, -2147450880},
            {32768, MIN_I, -2147450880, MAX_I}, {32768, -2147483647, -2147450879, MAX_I}, {32768, -100000, -67232, 132768},
            {32768, -32769, -1, 65537}, {32768, -32768, 0, 65536}, {32768, -32767, 1, 65535},
            {32768, -1, 32767, 32769}, {32768, 0, 32768, 32768}, {32768, 1, 32769, 32767},
            {32768, 32766, 65534, 2}, {32768, 32767, 65535, 1}, {32768, 32768, 65536, 0},
            {32768, 100000, 132768, -67232}, {32768, 2147483646, MAX_I, -2147450878}, {32768, MAX_I, MAX_I, -2147450879},
            {100000, MIN_I, -2147383648, MAX_I}, {100000, -2147483647, -2147383647, MAX_I}, {100000, -100000, 0, 200000},
            {100000, -32769, 67231, 132769}, {100000, -32768, 67232, 132768}, {100000, -32767, 67233, 132767},
            {100000, -1, 99999, 100001}, {100000, 0, 100000, 100000}, {100000, 1, 100001, 99999},
            {100000, 32766, 132766, 67234}, {100000, 32767, 132767, 67233}, {100000, 32768, 132768, 67232},
            {100000, 100000, 200000, 0}, {100000, 2147483646, MAX_I, -2147383646}, {100000, MAX_I, MAX_I, -2147383647},
            {2147483646, MIN_I, -2, MAX_I}, {2147483646, -2147483647, -1, MAX_I}, {2147483646, -100000, 2147383646, MAX_I},
            {2147483646, -32769, 2147450877, MAX_I}, {2147483646, -32768, 2147450878, MAX_I}, {2147483646, -32767, 2147450879, MAX_I},
            {2147483646, -1, 2147483645, MAX_I}, {2147483646, 0, 2147483646, 2147483646}, {2147483646, 1, MAX_I, 2147483645},
            {2147483646, 32766, MAX_I, 2147450880}, {2147483646, 32767, MAX_I, 2147450879}, {2147483646, 32768, MAX_I, 2147450878},
            {2147483646, 100000, MAX_I, 2147383646}, {2147483646, 2147483646, MAX_I, 0}, {2147483646, MAX_I, MAX_I, -1},
            {MAX_I, MIN_I, -1, MAX_I}, {MAX_I, -2147483647, 0, MAX_I}, {MAX_I, -100000, 2147383647, MAX_I},
            {MAX_I, -32769, 2147450878, MAX_I}, {MAX_I, -32768, 2147450879, MAX_I}, {MAX_I, -32767, 2147450880, MAX_I},
            {MAX_I, -1, 2147483646, MAX_I}, {MAX_I, 0, MAX_I, MAX_I}, {MAX_I, 1, MAX_I, 2147483646},
            {MAX_I, 32766, MAX_I, 2147450881}, {MAX_I, 32767, MAX_I, 2147450880}, {MAX_I, 32768, MAX_I, 2147450879},
            {MAX_I, 100000, MAX_I, 2147383647}, {MAX_I, 2147483646, MAX_I, 1}, {MAX_I, MAX_I, MAX_I, 0},
    };

    /**
     * Every pair of {@code {a, b, add_guardi16(a, b), sub_guardi16(a, b)}} that the C driver printed for arguments that
     * fit in an {@code int16_t}.
     */
    private static final int[][] GUARD_16 = {
            {-32768, -32768, -32768, 0}, {-32768, -32767, -32768, -1}, {-32768, -1, -32768, -32767},
            {-32768, 0, -32768, -32768}, {-32768, 1, -32767, -32768}, {-32768, 32766, -2, -32768},
            {-32768, 32767, -1, -32768}, {-32767, -32768, -32768, 1}, {-32767, -32767, -32768, 0},
            {-32767, -1, -32768, -32766}, {-32767, 0, -32767, -32767}, {-32767, 1, -32766, -32768},
            {-32767, 32766, -1, -32768}, {-32767, 32767, 0, -32768}, {-1, -32768, -32768, 32767},
            {-1, -32767, -32768, 32766}, {-1, -1, -2, 0}, {-1, 0, -1, -1},
            {-1, 1, 0, -2}, {-1, 32766, 32765, -32767}, {-1, 32767, 32766, -32768},
            {0, -32768, -32768, 32767}, {0, -32767, -32767, 32767}, {0, -1, -1, 1},
            {0, 0, 0, 0}, {0, 1, 1, -1}, {0, 32766, 32766, -32766},
            {0, 32767, 32767, -32767}, {1, -32768, -32767, 32767}, {1, -32767, -32766, 32767},
            {1, -1, 0, 2}, {1, 0, 1, 1}, {1, 1, 2, 0},
            {1, 32766, 32767, -32765}, {1, 32767, 32767, -32766}, {32766, -32768, -2, 32767},
            {32766, -32767, -1, 32767}, {32766, -1, 32765, 32767}, {32766, 0, 32766, 32766},
            {32766, 1, 32767, 32765}, {32766, 32766, 32767, 0}, {32766, 32767, 32767, -1},
            {32767, -32768, -1, 32767}, {32767, -32767, 0, 32767}, {32767, -1, 32766, 32767},
            {32767, 0, 32767, 32767}, {32767, 1, 32767, 32766}, {32767, 32766, 32767, 1},
            {32767, 32767, 32767, 0},
    };

    /**
     * Builds a mutable list of ints from the array, since the methods under test take {@link ArrayList}.
     *
     * @param values the values to place in the list
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
     * Folds C's integer result and its {@code my_rational} fraction into the one value the Java returns.
     *
     * @param integer  the {@code int} C returned
     * @param fracN    the numerator C stored through {@code frac}
     * @param fracD    the denominator C stored through {@code frac}
     * @return {@code integer + fracN / fracD}
     */
    private static Rational cValue(int integer, long fracN, long fracD) {
        return new Rational((long) integer * fracD + fracN, fracD);
    }

    /**
     * The 32-bit guards against C over every printed pair.
     */
    @Nested
    class Guards32 {
        @Test
        void addGuardIMatchesCOverTheBoundaryGrid() {
            for (int[] row : GUARD_32) {
                assertEquals(row[2], NumberUtils.addGuardI(row[0], row[1]), "add_guardi(" + row[0] + ", " + row[1] + ")");
            }
        }

        @Test
        void subGuardIMatchesCOverTheBoundaryGrid() {
            for (int[] row : GUARD_32) {
                assertEquals(row[3], NumberUtils.subGuardI(row[0], row[1]), "sub_guardi(" + row[0] + ", " + row[1] + ")");
            }
        }
    }

    /**
     * The 16-bit guards against C over every printed pair.
     */
    @Nested
    class Guards16 {
        @Test
        void addGuardI16MatchesCOverTheBoundaryGrid() {
            for (int[] row : GUARD_16) {
                assertEquals(row[2], NumberUtils.addGuardI16(row[0], row[1]),
                        "add_guardi16(" + row[0] + ", " + row[1] + ")");
            }
        }

        @Test
        void subGuardI16MatchesCOverTheBoundaryGrid() {
            for (int[] row : GUARD_16) {
                assertEquals(row[3], NumberUtils.subGuardI16(row[0], row[1]),
                        "sub_guardi16(" + row[0] + ", " + row[1] + ")");
            }
        }

        /**
         * C's parameters are {@code int16_t}, so it narrows its arguments; the Java takes {@code int} and does not.
         * Pinned so the difference is visible: an out-of-range argument is passed through, not clamped.
         */
        @Test
        void anArgumentOutsideSixteenBitsIsNotNarrowedAsCWouldNarrowIt() {
            assertEquals(40000, NumberUtils.addGuardI16(40000, 0));
            assertEquals(-40000, NumberUtils.subGuardI16(-40000, 0));
        }
    }

    /**
     * {@code mean} against the integer and fraction C returned with a non-null {@code frac}.
     */
    @Nested
    class Mean {
        @Test
        void aWholeMean() {
            // C: 3, 0/1
            assertEquals(cValue(3, 0, 1), NumberUtils.mean(listOf(1, 2, 3, 4, 5), 5));
        }

        @Test
        void aMeanWithAFractionIsTheFlooredIntegerPlusTheFraction() {
            // C: 2, 1/2
            assertEquals(cValue(2, 1, 2), NumberUtils.mean(listOf(1, 2, 3, 4), 4));
        }

        @Test
        void aNegativeFractionalMeanIsFlooredAndTheFractionStaysPositive() {
            // C: -2, 1/2 for {-1, -2}, and -2, 2/3 for {-1, -1, -2}
            assertEquals(cValue(-2, 1, 2), NumberUtils.mean(listOf(-1, -2), 2));
            assertEquals(cValue(-2, 2, 3), NumberUtils.mean(listOf(-1, -1, -2), 3));
        }

        @Test
        void aZeroMeanFromMixedSigns() {
            // C: -1, 0/1
            assertEquals(cValue(-1, 0, 1), NumberUtils.mean(listOf(-3, -1, 1), 3));
        }

        @Test
        void aFractionBelowOneWholeNumber() {
            // C: 0, 1/3
            assertEquals(cValue(0, 1, 3), NumberUtils.mean(listOf(0, 0, 1), 3));
        }

        @Test
        void theExtremesOfTheIntRangeSumWithoutOverflow() {
            // C: INT_MAX, 0/1 ; INT_MIN, 0/1 ; -1, 1/2 ; INT_MIN, 1/2 ; INT_MAX - 1, 0/1
            assertEquals(cValue(MAX_I, 0, 1), NumberUtils.mean(listOf(MAX_I, MAX_I), 2));
            assertEquals(cValue(MIN_I, 0, 1), NumberUtils.mean(listOf(MIN_I, MIN_I), 2));
            assertEquals(cValue(-1, 1, 2), NumberUtils.mean(listOf(MAX_I, MIN_I), 2));
            assertEquals(cValue(MIN_I, 1, 2), NumberUtils.mean(listOf(MIN_I, MIN_I + 1), 2));
            assertEquals(cValue(MAX_I - 1, 0, 1), NumberUtils.mean(listOf(MAX_I, MAX_I - 1, MAX_I - 2), 3));
        }

        @Test
        void onlyTheFirstSizeEntriesAreUsed() {
            // C: 2, 0/1 for the first three of {1, 2, 3, 1000, -1000}
            assertEquals(cValue(2, 0, 1), NumberUtils.mean(listOf(1, 2, 3, 1000, -1000), 3));
        }

        @Test
        void aSizeOfZeroOrLessIsZeroAsInC() {
            // C: 0, 0/1
            assertEquals(cValue(0, 0, 1), NumberUtils.mean(listOf(1, 2, 3), 0));
            assertEquals(cValue(0, 0, 1), NumberUtils.mean(listOf(1, 2, 3), -4));
        }
    }

    /**
     * {@code variance} against the integer and fraction C returned with a non-null {@code frac}, one row per
     * combination of {@code unbiased} and {@code of_mean}.
     */
    @Nested
    class Variance {
        @Test
        void oneToFiveInEveryForm() {
            ArrayList<Integer> data = listOf(1, 2, 3, 4, 5);
            // C, in order (biased, unbiased) x (plain, of mean): 2 + 0/1, 0 + 2/5, 2 + 1/2, 0 + 1/2
            assertEquals(cValue(2, 0, 1), NumberUtils.variance(data, 5, false, false));
            assertEquals(cValue(0, 2, 5), NumberUtils.variance(data, 5, false, true));
            assertEquals(cValue(2, 1, 2), NumberUtils.variance(data, 5, true, false));
            assertEquals(cValue(0, 1, 2), NumberUtils.variance(data, 5, true, true));
        }

        @Test
        void oneToFourInEveryForm() {
            ArrayList<Integer> data = listOf(1, 2, 3, 4);
            // C: 1 + 1/4, 0 + 5/16, 1 + 2/3, 0 + 5/12
            assertEquals(cValue(1, 1, 4), NumberUtils.variance(data, 4, false, false));
            assertEquals(cValue(0, 5, 16), NumberUtils.variance(data, 4, false, true));
            assertEquals(cValue(1, 2, 3), NumberUtils.variance(data, 4, true, false));
            assertEquals(cValue(0, 5, 12), NumberUtils.variance(data, 4, true, true));
        }

        @Test
        void theTextbookSampleInEveryForm() {
            ArrayList<Integer> data = listOf(2, 4, 4, 4, 5, 5, 7, 9);
            // C: 4 + 0/1, 0 + 1/2, 4 + 4/7, 0 + 4/7
            assertEquals(cValue(4, 0, 1), NumberUtils.variance(data, 8, false, false));
            assertEquals(cValue(0, 1, 2), NumberUtils.variance(data, 8, false, true));
            assertEquals(cValue(4, 4, 7), NumberUtils.variance(data, 8, true, false));
            assertEquals(cValue(0, 4, 7), NumberUtils.variance(data, 8, true, true));
        }

        @Test
        void aNegativeSampleInEveryForm() {
            ArrayList<Integer> data = listOf(-1, -1, -2);
            // C: 0 + 2/9, 0 + 2/27, 0 + 1/3, 0 + 1/9
            assertEquals(cValue(0, 2, 9), NumberUtils.variance(data, 3, false, false));
            assertEquals(cValue(0, 2, 27), NumberUtils.variance(data, 3, false, true));
            assertEquals(cValue(0, 1, 3), NumberUtils.variance(data, 3, true, false));
            assertEquals(cValue(0, 1, 9), NumberUtils.variance(data, 3, true, true));
        }

        @Test
        void aMixedSignSampleInEveryForm() {
            ArrayList<Integer> data = listOf(-3, -1, 1);
            // C: 2 + 2/3, 0 + 8/9, 4 + 0/1, 1 + 1/3
            assertEquals(cValue(2, 2, 3), NumberUtils.variance(data, 3, false, false));
            assertEquals(cValue(0, 8, 9), NumberUtils.variance(data, 3, false, true));
            assertEquals(cValue(4, 0, 1), NumberUtils.variance(data, 3, true, false));
            assertEquals(cValue(1, 1, 3), NumberUtils.variance(data, 3, true, true));
        }

        @Test
        void aConstantSampleHasNoVarianceWhateverItsSize() {
            // C: 0 + 0/1 in all four forms, for 7, 7, 7, 7 and for INT_MAX twice and INT_MIN twice
            for (ArrayList<Integer> data : List.of(listOf(7, 7, 7, 7), listOf(MAX_I, MAX_I), listOf(MIN_I, MIN_I))) {
                assertEquals(Rational.zero, NumberUtils.variance(data, data.size(), false, false));
                assertEquals(Rational.zero, NumberUtils.variance(data, data.size(), false, true));
                assertEquals(Rational.zero, NumberUtils.variance(data, data.size(), true, false));
                assertEquals(Rational.zero, NumberUtils.variance(data, data.size(), true, true));
            }
        }

        @Test
        void valuesNextToTheIntLimitsStillGiveExactSmallVariances() {
            // C: INT_MIN, INT_MIN + 1 -> 0 + 1/4, 0 + 1/8, 0 + 1/2, 0 + 1/4
            ArrayList<Integer> low = listOf(MIN_I, MIN_I + 1);
            assertEquals(cValue(0, 1, 4), NumberUtils.variance(low, 2, false, false));
            assertEquals(cValue(0, 1, 8), NumberUtils.variance(low, 2, false, true));
            assertEquals(cValue(0, 1, 2), NumberUtils.variance(low, 2, true, false));
            assertEquals(cValue(0, 1, 4), NumberUtils.variance(low, 2, true, true));

            // C: INT_MAX, INT_MAX - 1, INT_MAX - 2 -> 0 + 2/3, 0 + 2/9, 1 + 0/1, 0 + 1/3
            ArrayList<Integer> high = listOf(MAX_I, MAX_I - 1, MAX_I - 2);
            assertEquals(cValue(0, 2, 3), NumberUtils.variance(high, 3, false, false));
            assertEquals(cValue(0, 2, 9), NumberUtils.variance(high, 3, false, true));
            assertEquals(cValue(1, 0, 1), NumberUtils.variance(high, 3, true, false));
            assertEquals(cValue(0, 1, 3), NumberUtils.variance(high, 3, true, true));
        }

        @Test
        void aWholeNumberVarianceJustBelowTheCapIsKept() {
            // C: {0, 65536} -> 1073741824, 536870912, then INT_MAX (unbiased, capped), 1073741824
            ArrayList<Integer> data = listOf(0, 65536);
            assertEquals(cValue(1073741824, 0, 1), NumberUtils.variance(data, 2, false, false));
            assertEquals(cValue(536870912, 0, 1), NumberUtils.variance(data, 2, false, true));
            assertEquals(cValue(MAX_I, 0, 1), NumberUtils.variance(data, 2, true, false));
            assertEquals(cValue(1073741824, 0, 1), NumberUtils.variance(data, 2, true, true));
        }

        @Test
        void aVarianceAboveIntMaxIsCappedWithNoFraction() {
            // C: INT_MAX + 0/1 for {INT_MAX, INT_MIN} in all four forms, and for {0, 100000} in three of them
            ArrayList<Integer> extremes = listOf(MAX_I, MIN_I);
            assertEquals(cValue(MAX_I, 0, 1), NumberUtils.variance(extremes, 2, false, false));
            assertEquals(cValue(MAX_I, 0, 1), NumberUtils.variance(extremes, 2, false, true));
            assertEquals(cValue(MAX_I, 0, 1), NumberUtils.variance(extremes, 2, true, false));
            assertEquals(cValue(MAX_I, 0, 1), NumberUtils.variance(extremes, 2, true, true));

            ArrayList<Integer> wide = listOf(0, 100000);
            assertEquals(cValue(MAX_I, 0, 1), NumberUtils.variance(wide, 2, false, false));
            assertEquals(cValue(1250000000, 0, 1), NumberUtils.variance(wide, 2, false, true));
            assertEquals(cValue(MAX_I, 0, 1), NumberUtils.variance(wide, 2, true, false));
            assertEquals(cValue(MAX_I, 0, 1), NumberUtils.variance(wide, 2, true, true));
        }

        @Test
        void onlyTheFirstSizeEntriesAreUsed() {
            // C: first three of {1, 2, 3, 1000, -1000} -> 0 + 2/3, 0 + 2/9, 1 + 0/1, 0 + 1/3
            ArrayList<Integer> data = listOf(1, 2, 3, 1000, -1000);
            assertEquals(cValue(0, 2, 3), NumberUtils.variance(data, 3, false, false));
            assertEquals(cValue(0, 2, 9), NumberUtils.variance(data, 3, false, true));
            assertEquals(cValue(1, 0, 1), NumberUtils.variance(data, 3, true, false));
            assertEquals(cValue(0, 1, 3), NumberUtils.variance(data, 3, true, true));
        }

        @Test
        void aSizeOfOneOrLessIsZeroAsInC() {
            // C: 0 + 0/1
            assertEquals(cValue(0, 0, 1), NumberUtils.variance(listOf(1, 2, 3), 1, true, true));
            assertEquals(cValue(0, 0, 1), NumberUtils.variance(listOf(1, 2, 3), 0, false, false));
            assertEquals(cValue(0, 0, 1), NumberUtils.variance(listOf(1, 2, 3), -1, false, false));
        }
    }

    /**
     * Lists long enough that C's exact-or-approximate arithmetic and the Java's exact arithmetic can part.
     */
    @Nested
    class LargeLists {
        /**
         * Builds the list the C driver used: entry {@code x} is {@code (x * 2654435761) % (2 * spread + 1) - spread}.
         *
         * @param count  how many entries
         * @param spread the largest magnitude
         * @return the list
         */
        private ArrayList<Integer> lcgList(int count, int spread) {
            ArrayList<Integer> list = new ArrayList<>();
            for (int x = 0; x < count; x++) {
                list.add((int) (((long) x * 2654435761L) % (2L * spread + 1)) - spread);
            }
            return list;
        }

        @Test
        void aTwoThousandEntryMeanAndVarianceAgreeWithC() {
            ArrayList<Integer> data = lcgList(2000, 3500000);
            // C: mean 104 + 137/250 ; variance of the mean, biased 2042660708 + 78557731/125000000,
            // unbiased 2043682549 + 112870231/124937500 ; the other two forms are capped at INT_MAX
            assertEquals(cValue(104, 137, 250), NumberUtils.mean(data, 2000));
            assertEquals(cValue(MAX_I, 0, 1), NumberUtils.variance(data, 2000, false, false));
            assertEquals(cValue(2042660708, 78557731, 125000000), NumberUtils.variance(data, 2000, false, true));
            assertEquals(cValue(MAX_I, 0, 1), NumberUtils.variance(data, 2000, true, false));
            assertEquals(cValue(2043682549, 112870231, 124937500), NumberUtils.variance(data, 2000, true, true));
        }

        @Test
        void aThreeThousandEntryMeanAgreesWithC() {
            // C: mean -3768 + 559/1500
            assertEquals(cValue(-3768, 559, 1500), NumberUtils.mean(lcgList(3000, 3000000), 3000));
        }

        /**
         * C works the variance of the mean for 3000 entries out to {@code 1000410913 + 3200625548/4294967295} (biased)
         * and {@code 1000744495 + 1046332399/4294967295} (unbiased). The denominator is {@code UINT_MAX}, so C has
         * approximated. The Java fraction is exact ({@code 6752773667780125019/6750000000} and
         * {@code 6752773667780125019/6747750000}), so it differs from C by a few parts in 10^10. This is the
         * {@link Rational} accepted deviation showing through {@code variance}.
         */
        @Test
        void aFractionPastUintMaxIsExactWhereCApproximates() {
            ArrayList<Integer> data = lcgList(3000, 3000000);
            Rational biased = NumberUtils.variance(data, 3000, false, true);
            Rational unbiased = NumberUtils.variance(data, 3000, true, true);

            assertEquals(new Rational(6752773667780125019L, 6750000000L), biased);
            assertEquals(new Rational(6752773667780125019L, 6747750000L), unbiased);
            assertNotEquals(cValue(1000410913, 3200625548L, 4294967295L), biased);
            assertNotEquals(cValue(1000744495, 1046332399L, 4294967295L), unbiased);
        }

        /**
         * C approximates a fraction that will not fit and carries on. The Java builds the exact fraction and converts
         * it with {@code longValueExact}, so a reduced numerator or denominator past 63 bits throws. It needs a long
         * list and large values together, and depends on the data: 5000 entries drawn by {@code Random(1)} from
         * +/-3,000,000, or 10000 from +/-1,000,000, throw for the variance of the mean, while 3000 from
         * +/-3,000,000 does not. Not yet decided.
         */
        @Test
        void aFractionTooBigForALongThrowsWhereCApproximates() {
            Random random = new Random(1);
            ArrayList<Integer> data = new ArrayList<>();
            for (int x = 0; x < 5000; x++) {
                data.add(random.nextInt(6000001) - 3000000);
            }

            assertThrows(ArithmeticException.class, () -> NumberUtils.variance(data, 5000, false, true));
            assertThrows(ArithmeticException.class, () -> NumberUtils.variance(data, 5000, true, true));
        }
    }

    /**
     * {@code cmp} against the {@code CMP} macro of {@code h-basic.h}.
     */
    @Nested
    class Cmp {
        @Test
        void givesExactlyMinusOneZeroOrOne() {
            assertEquals(-1, NumberUtils.cmp(1, 2));
            assertEquals(0, NumberUtils.cmp(2, 2));
            assertEquals(1, NumberUtils.cmp(3, 2));
        }

        @Test
        void theExtremesDoNotOverflowIntoTheWrongSign() {
            assertEquals(-1, NumberUtils.cmp(MIN_I, MAX_I));
            assertEquals(1, NumberUtils.cmp(MAX_I, MIN_I));
            assertEquals(0, NumberUtils.cmp(MIN_I, MIN_I));
            assertEquals(-1, NumberUtils.cmp(-5, 0));
        }
    }
}
