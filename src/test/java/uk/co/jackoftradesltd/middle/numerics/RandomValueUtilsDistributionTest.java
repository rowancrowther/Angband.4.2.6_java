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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.middle.enums.DamageAspect;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the parts of {@link RandomValueUtils} that {@link RandomValueUtilsTest} leaves out: the
 * edges of the uniform draws, and the normal-distribution family ({@code normal}, {@code sample},
 * {@code mBonus}, {@code mBonusCalc}) that the port builds on Box-Muller where C uses a table.
 *
 * <p>The port cannot reproduce C's draws, so none of these compare a sequence against C. They
 * compare <em>distributions</em>, using figures worked out from {@code z-rand.c} rather than read
 * back from the Java:
 * <ul>
 *   <li>{@code Rand_normal}'s table gives a draw within one standard deviation of the mean for
 *       22245 of 32768 rolls (index 63), within two for 31249 (index 127) and within three for
 *       32677 (index 191), that is 67.9%, 95.4% and 99.7%.</li>
 *   <li>Its offset is {@code stand * index / 64} with the index below 256, so the largest offset is
 *       under {@code 4 * stand}, and a {@code stand} below 1 returns the mean.</li>
 *   <li>The comment above {@code m_bonus} tabulates its outcomes for a maximum of 10: a level of 0
 *       gives 0 on 66.37% of rolls, a level of 64 gives 5 on 31.57%, and a level of 128 gives 10
 *       on 64.07%.</li>
 *   <li>{@code Rand_sample} scales a {@code normal(0, 1000)} draw by
 *       {@code (upper - mean) / (100 * stand_u)} above the mean and
 *       {@code (mean - lower) / (100 * stand_l)} below it.</li>
 *   <li>{@code simulate_division} rounds up with probability {@code remainder / divisor}.</li>
 * </ul>
 *
 * <p>Every test seeds the generator, so the figures are reproducible. Each tolerance is several
 * standard errors wide for the draw count used, so a failure means the distribution is wrong, not
 * that the seed was unlucky. Where the Box-Muller figure differs slightly from C's, because of the
 * continuous draw and the truncation toward zero, the tolerance spans both.
 *
 * <p>Class RandomValueUtilsDistributionTest coded on 261005, commented in full on 261005.
 *
 * @author Rowan Crowther
 */
class RandomValueUtilsDistributionTest {

    /**
     * Draws per distribution check; the standard error of a fraction is about 0.0035 at this size.
     */
    private static final int MANY = 20000;

    /**
     * A fixed seed, so a failing figure is reproducible rather than a flake.
     */
    private static final long SEED = 20261005L;

    /**
     * Asserts that {@code fraction} lies in {@code [low, high]}, with the figure in the message.
     */
    private static void assertFractionBetween(double low, double high, double fraction, String what) {
        assertTrue(fraction >= low && fraction <= high,
                () -> what + ": " + fraction + " is outside " + low + ".." + high);
    }

    @BeforeEach
    void seedTheGenerator() {
        RandomValueUtils.stateInit(SEED);
    }

    /**
     * The edges of the uniform draws that {@link RandomValueUtilsTest} does not reach.
     */
    @Nested
    class UniformEdges {

        @Test
        void theLargestBoundCIsWillingToTakeStillDraws() {
            // C asserts m <= 0x10000000, so 0x10000000 itself is legal.
            for (int draw = 0; draw < 100; draw++) {
                int rolled = RandomValueUtils.randDiv(0x10000000);

                assertTrue(rolled >= 0 && rolled < 0x10000000, () -> "out of range: " + rolled);
            }
        }

        @Test
        void aBoundAboveCsAssertLimitIsRejected() {
            assertThrows(IllegalArgumentException.class, () -> RandomValueUtils.randDiv(0x10000001));
            assertThrows(IllegalArgumentException.class, () -> RandomValueUtils.randDiv(Integer.MAX_VALUE));
            assertThrows(IllegalArgumentException.class, () -> RandomValueUtils.randInt0(0x10000001));
            assertThrows(IllegalArgumentException.class, () -> RandomValueUtils.randInt1(0x10000001));
        }

        @Test
        void aZeroBoundGivesZeroOrOneAccordingToTheDrawAboveIt() {
            // randint0(0) is Rand_div(0) = 0, and randint1(0) is that plus one.
            assertEquals(0, RandomValueUtils.randInt0(0));
            assertEquals(1, RandomValueUtils.randInt1(0));
        }

        @Test
        void oneInZeroAndOneInOneAlwaysFire() {
            // one_in_(x) is !randint0(x), and randint0 gives 0 for a bound of 0 or 1.
            for (int draw = 0; draw < 100; draw++) {
                assertTrue(RandomValueUtils.oneIn(0));
                assertTrue(RandomValueUtils.oneIn(1));
            }
        }

        @Test
        void oneInTwoIsACoinFlip() {
            int fired = 0;
            for (int draw = 0; draw < MANY; draw++) {
                if (RandomValueUtils.oneIn(2)) {
                    fired++;
                }
            }

            assertFractionBetween(0.47, 0.53, (double) fired / MANY, "one_in_(2)");
        }

        @Test
        void oneInANegativeNumberIsRejected() {
            assertThrows(IllegalArgumentException.class, () -> RandomValueUtils.oneIn(-1));
        }

        @Test
        void aSpreadOfOneReachesExactlyThreeValues() {
            Set<Integer> seen = new HashSet<>();
            for (int draw = 0; draw < 500; draw++) {
                seen.add(RandomValueUtils.randSpread(10, 1));
            }

            assertEquals(Set.of(9, 10, 11), seen);
        }

        @Test
        void aNegativeSpreadIsRejected() {
            // rand_spread(A, D) hands randint0 a bound of 1 + 2D, which is negative for D < 0.
            assertThrows(IllegalArgumentException.class, () -> RandomValueUtils.randSpread(10, -1));
            assertThrows(IllegalArgumentException.class, () -> RandomValueUtils.randSpread(10, -5));
        }

        @Test
        void aSpreadIsTheSameDrawAsTheEquivalentRange() {
            // z-rand.h: "rand_spread(A, D) == rand_range(A - D, A + D)", so both cover 11 values.
            Set<Integer> spread = new HashSet<>();
            Set<Integer> range = new HashSet<>();
            for (int draw = 0; draw < MANY; draw++) {
                spread.add(RandomValueUtils.randSpread(0, 5));
                range.add(RandomValueUtils.randRange(-5, 5));
            }

            assertEquals(range, spread);
            assertEquals(11, spread.size());
        }

        @Test
        void aRangeAcrossZeroReachesEveryValueIncludingBothEnds() {
            Set<Integer> seen = new HashSet<>();
            for (int draw = 0; draw < 2000; draw++) {
                seen.add(RandomValueUtils.randRange(-3, 3));
            }

            assertEquals(7, seen.size(), "-3..3 holds seven values");
            assertTrue(seen.contains(-3) && seen.contains(3));
        }

        @Test
        void aRangeOfTwoValuesReachesBoth() {
            Set<Integer> seen = new HashSet<>();
            for (int draw = 0; draw < 200; draw++) {
                seen.add(RandomValueUtils.randRange(0, 1));
            }

            assertEquals(Set.of(0, 1), seen);
        }

        @Test
        void aRangeZeroToNMinusOneIsTheSameDrawAsRandInt0() {
            // z-rand.c: 'Note that "rand_range(0, N-1)" == "randint0(N)"'.
            Set<Integer> range = new HashSet<>();
            Set<Integer> uniform = new HashSet<>();
            for (int draw = 0; draw < 2000; draw++) {
                range.add(RandomValueUtils.randRange(0, 5));
                uniform.add(RandomValueUtils.randInt0(6));
            }

            assertEquals(uniform, range);
        }

        @Test
        void damagingWithOneSidedDiceCountsTheDice() {
            // Each die of a one-sided set rolls its only face, 1.
            assertEquals(5, RandomValueUtils.damRoll(5, 1));
            assertEquals(1, RandomValueUtils.damRoll(1, 1));
        }

        @Test
        void aDamageAspectWithNoDiceIsZeroExceptForTheAverage() {
            // damcalc: MAXIMISE is num * sides, MINIMISE is num, AVERAGE is num * (sides + 1) / 2.
            assertEquals(0, RandomValueUtils.damCalc(0, 6, DamageAspect.MAXIMIZE));
            assertEquals(0, RandomValueUtils.damCalc(0, 6, DamageAspect.MINIMIZE));
            assertEquals(0, RandomValueUtils.damCalc(0, 6, DamageAspect.AVERAGE));
            assertEquals(0, RandomValueUtils.damCalc(0, 6, DamageAspect.RANDOMIZE));
        }

        @Test
        void averagingTruncatesAnOddTotal() {
            // 1d4: 1 * 5 / 2 = 2 in integer arithmetic; 2d6: 2 * 7 / 2 = 7.
            assertEquals(2, RandomValueUtils.damCalc(1, 4, DamageAspect.AVERAGE));
            assertEquals(7, RandomValueUtils.damCalc(2, 6, DamageAspect.AVERAGE));
            assertEquals(1, RandomValueUtils.damCalc(1, 1, DamageAspect.AVERAGE));
        }
    }

    /**
     * Probabilistic division.
     */
    @Nested
    class SimulatedDivision {

        @Test
        void aHalfRemainderRoundsUpHalfTheTime() {
            // 10 / 4 is 2 remainder 2, so it rounds up with probability 2/4.
            int up = 0;
            for (int draw = 0; draw < MANY; draw++) {
                int result = RandomValueUtils.simulateDivision(10, 4);
                assertTrue(result == 2 || result == 3, () -> "unexpected quotient: " + result);
                if (result == 3) {
                    up++;
                }
            }

            assertFractionBetween(0.47, 0.53, (double) up / MANY, "10/4 rounding up");
        }

        @Test
        void aQuarterRemainderRoundsUpAQuarterOfTheTime() {
            // 1 / 4 is 0 remainder 1, so it rounds up with probability 1/4.
            int up = 0;
            for (int draw = 0; draw < MANY; draw++) {
                if (RandomValueUtils.simulateDivision(1, 4) == 1) {
                    up++;
                }
            }

            assertFractionBetween(0.22, 0.28, (double) up / MANY, "1/4 rounding up");
        }

        @Test
        void aZeroDividendIsZero() {
            for (int draw = 0; draw < 100; draw++) {
                assertEquals(0, RandomValueUtils.simulateDivision(0, 5));
            }
        }

        @Test
        void aNegativeDividendNeverRoundsUp() {
            // C's % truncates toward zero, so -7 / 2 is -3 remainder -1, and randint0(2) < -1 is
            // never true: the helper only rounds away from zero for a positive remainder.
            for (int draw = 0; draw < 500; draw++) {
                assertEquals(-3, RandomValueUtils.simulateDivision(-7, 2));
                assertEquals(-4, RandomValueUtils.simulateDivision(-8, 2));
            }
        }
    }

    /**
     * The normal draw, the port of {@code Rand_normal}.
     */
    @Nested
    class Normal {

        @Test
        void aSpreadBelowOneReturnsTheMeanUntouched() {
            // Rand_normal: 'if (stand < 1) return (mean);'. C's stand is an int, so only 0 and the
            // negatives can reach it there; the fractional 0.5 is a port-only input (the parameter
            // is a float) that the same guard covers.
            for (int draw = 0; draw < 100; draw++) {
                assertEquals(7, RandomValueUtils.normal(7, 0));
                assertEquals(7, RandomValueUtils.normal(7, 0.5f));
                assertEquals(7, RandomValueUtils.normal(7, -3));
                assertEquals(-40, RandomValueUtils.normal(-40, -1000));
            }
        }

        @Test
        void aWideDrawGoesBeyondTheNarrowCapNormalOnceHad() {
            // Guards the spread being the standard deviation asked for rather than a small fixed
            // one: normal(100, 50) must regularly move more than 100 from the mean.
            int far = 0;
            for (int draw = 0; draw < MANY; draw++) {
                if (Math.abs(RandomValueUtils.normal(100, 50) - 100) > 100) {
                    far++;
                }
            }

            // Past two standard deviations is about 4.6% of draws.
            assertFractionBetween(0.03, 0.065, (double) far / MANY, "draws beyond two standard deviations");
        }

        @Test
        void neverStraysFourStandardDeviationsFromTheMean() {
            // C's largest offset is under 4 * stand; the port clamps at exactly 4 * std.
            for (int draw = 0; draw < 200000; draw++) {
                int one = RandomValueUtils.normal(0, 1);
                assertTrue(Math.abs(one) <= 4, () -> "std 1 gave " + one);
            }
            for (int draw = 0; draw < MANY; draw++) {
                int wide = RandomValueUtils.normal(100, 50);
                assertTrue(wide >= -100 && wide <= 300, () -> "std 50 gave " + wide);
            }
            for (int draw = 0; draw < MANY; draw++) {
                int huge = RandomValueUtils.normal(0, 1000);
                assertTrue(Math.abs(huge) <= 4000, () -> "std 1000 gave " + huge);
            }
        }

        @Test
        void isCentredOnTheMean() {
            long sum = 0;
            for (int draw = 0; draw < MANY; draw++) {
                sum += RandomValueUtils.normal(100, 10);
            }
            assertEquals(100.0, (double) sum / MANY, 0.5);

            sum = 0;
            for (int draw = 0; draw < MANY; draw++) {
                sum += RandomValueUtils.normal(-50, 10);
            }
            assertEquals(-50.0, (double) sum / MANY, 0.5);
        }

        @Test
        void putsAboutTwoThirdsWithinOneStandardDeviation() {
            // C's table: 22245 / 32768 = 67.9% within one, 31249 / 32768 = 95.4% within two,
            // 32677 / 32768 = 99.7% within three.
            int one = 0;
            int two = 0;
            int three = 0;
            for (int draw = 0; draw < MANY; draw++) {
                int offset = Math.abs(RandomValueUtils.normal(0, 100));
                if (offset <= 100) one++;
                if (offset <= 200) two++;
                if (offset <= 300) three++;
            }

            assertFractionBetween(0.65, 0.71, (double) one / MANY, "within one standard deviation");
            assertFractionBetween(0.94, 0.97, (double) two / MANY, "within two standard deviations");
            assertFractionBetween(0.992, 1.0, (double) three / MANY, "within three standard deviations");
        }

        @Test
        void aStandardDeviationOfOneIsMostlyTheMean() {
            // C: with stand 1 the offset is index / 64, which is 0 for an index below 64, that is
            // 22245 / 32768 = 67.9% of rolls.
            int onTheMean = 0;
            for (int draw = 0; draw < MANY; draw++) {
                if (RandomValueUtils.normal(0, 1) == 0) {
                    onTheMean++;
                }
            }

            assertFractionBetween(0.64, 0.72, (double) onTheMean / MANY, "std 1 landing on the mean");
        }

        @Test
        void fallsOnBothSidesOfTheMeanEvenly() {
            // 'One half should be negative' / 'One half should be positive'.
            int above = 0;
            int below = 0;
            for (int draw = 0; draw < MANY; draw++) {
                int offset = RandomValueUtils.normal(0, 100);
                if (offset > 0) above++;
                if (offset < 0) below++;
            }

            assertFractionBetween(0.47, 0.53, (double) above / (above + below), "share above the mean");
        }

        @Test
        void isRepeatableFromASeed() {
            RandomValueUtils.stateInit(99L);
            int[] first = new int[50];
            for (int index = 0; index < first.length; index++) {
                first[index] = RandomValueUtils.normal(0, 100);
            }

            RandomValueUtils.stateInit(99L);
            for (int index = 0; index < first.length; index++) {
                assertEquals(first[index], RandomValueUtils.normal(0, 100), "diverged at draw " + index);
            }
        }
    }

    /**
     * The two-halved normal, the port of {@code Rand_sample}.
     */
    @Nested
    class Sample {

        @Test
        void staysWithinFourStandardDeviationsOfEachHalf() {
            // mean 100, upper 140 (stand_u 20), lower 60 (stand_l 20): the largest pick of 4000
            // scales to 4000 * 40 / (100 * 20) = 80 either way.
            for (int draw = 0; draw < MANY; draw++) {
                int result = RandomValueUtils.sample(100, 140, 60, 20, 20);

                assertTrue(result >= 20 && result <= 180, () -> "out of range: " + result);
            }
        }

        @Test
        void reachesTheUpperBoundAtTwoStandardDeviations() {
            // stand_u of 20 puts the upper bound two standard deviations out: pick >= 2000 scales
            // to 40, and a normal(0, 1000) draw reaches that 2.3% of the time.
            int atOrAbove = 0;
            for (int draw = 0; draw < MANY; draw++) {
                if (RandomValueUtils.sample(100, 140, 60, 20, 20) >= 140) {
                    atOrAbove++;
                }
            }

            assertFractionBetween(0.015, 0.03, (double) atOrAbove / MANY, "at or above the upper bound");
        }

        @Test
        void scalesTheLowerHalfByItsOwnDeviation() {
            // stand_l of 10 puts the lower bound one standard deviation out: pick <= -1000 scales
            // to -40, and that is 15.9% of draws. A different upper figure must not disturb it.
            int atOrBelow = 0;
            for (int draw = 0; draw < MANY; draw++) {
                if (RandomValueUtils.sample(100, 140, 60, 20, 10) <= 60) {
                    atOrBelow++;
                }
            }

            assertFractionBetween(0.14, 0.18, (double) atOrBelow / MANY, "at or below the lower bound");
        }

        @Test
        void isCentredOnTheMean() {
            long sum = 0;
            for (int draw = 0; draw < MANY; draw++) {
                sum += RandomValueUtils.sample(100, 140, 60, 20, 20);
            }

            assertEquals(100.0, (double) sum / MANY, 0.5);
        }

        @Test
        void aLopsidedDistributionStretchesTheWiderHalf() {
            // upper 200, lower 90, same stand: the upper half reaches 10 times as far. The largest
            // pick of 4000 scales to 4000 * 100 / 2000 = 200 up and 4000 * 10 / 2000 = 20 down.
            int highest = Integer.MIN_VALUE;
            int lowest = Integer.MAX_VALUE;
            for (int draw = 0; draw < MANY; draw++) {
                int result = RandomValueUtils.sample(100, 200, 90, 20, 20);
                highest = Math.max(highest, result);
                lowest = Math.min(lowest, result);
            }

            assertTrue(highest > 150 && highest <= 300, "the wide upper half is off: " + highest);
            assertTrue(lowest >= 80 && lowest < 100, "the narrow lower half is off: " + lowest);
        }
    }

    /**
     * The enchantment bonus, the port of {@code m_bonus}.
     */
    @Nested
    class MBonus {

        @Test
        void staysBetweenZeroAndTheMaximum() {
            int[] maxima = {0, 1, 2, 3, 5, 7, 9, 10, 20, 40};
            int[] levels = {0, 1, 50, 127, 128, 500};
            for (int max : maxima) {
                for (int level : levels) {
                    for (int draw = 0; draw < 300; draw++) {
                        int bonus = RandomValueUtils.mBonus(max, level);

                        assertTrue(bonus >= 0 && bonus <= max,
                                () -> "mBonus(" + max + ", " + level + ") gave " + bonus);
                    }
                }
            }
        }

        @Test
        void aMaximumOfZeroGivesZero() {
            for (int draw = 0; draw < 100; draw++) {
                assertEquals(0, RandomValueUtils.mBonus(0, 50));
            }
        }

        @Test
        void atLevelZeroMostRollsAreZero() {
            // The comment above m_bonus: 66.37% of m_bonus(10, 0) are 0.
            int zero = 0;
            for (int draw = 0; draw < MANY; draw++) {
                if (RandomValueUtils.mBonus(10, 0) == 0) {
                    zero++;
                }
            }

            assertFractionBetween(0.60, 0.72, (double) zero / MANY, "m_bonus(10, 0) giving 0");
        }

        @Test
        void atMidLevelTheMeanBonusIsHalfTheMaximum() {
            // The comment above m_bonus: 31.57% of m_bonus(10, 64) are 5, the centre.
            int five = 0;
            long sum = 0;
            for (int draw = 0; draw < MANY; draw++) {
                int bonus = RandomValueUtils.mBonus(10, 64);
                if (bonus == 5) {
                    five++;
                }
                sum += bonus;
            }

            assertFractionBetween(0.27, 0.37, (double) five / MANY, "m_bonus(10, 64) giving 5");
            assertEquals(5.0, (double) sum / MANY, 0.3);
        }

        @Test
        void atTheDepthCapMostlyTheMaximum() {
            // The comment above m_bonus: 64.07% of m_bonus(10, 128) are 10.
            int ten = 0;
            for (int draw = 0; draw < MANY; draw++) {
                if (RandomValueUtils.mBonus(10, 128) == 10) {
                    ten++;
                }
            }

            assertFractionBetween(0.57, 0.70, (double) ten / MANY, "m_bonus(10, 128) giving 10");
        }

        @Test
        void aLevelPastTheCapBehavesAsTheLevelJustBelowIt() {
            // 'if (level >= MAX_RAND_DEPTH) level = MAX_RAND_DEPTH - 1;'
            int ten = 0;
            for (int draw = 0; draw < MANY; draw++) {
                if (RandomValueUtils.mBonus(10, 5000) == 10) {
                    ten++;
                }
            }

            assertFractionBetween(0.57, 0.70, (double) ten / MANY, "m_bonus(10, 5000) giving 10");
        }

        @Test
        void aLargeMaximumUsesItsFullSpread() {
            // m_bonus(20, ...) has a standard deviation of 5, not the 4 an earlier clamp gave it:
            // at level 64 the mean is 10, so values within 5 of it should reach 15 or more.
            int high = 0;
            for (int draw = 0; draw < MANY; draw++) {
                if (RandomValueUtils.mBonus(20, 64) >= 15) {
                    high++;
                }
            }

            // P(trunc(5z) >= 5) = P(z >= 1) = 15.9%.
            assertFractionBetween(0.13, 0.19, (double) high / MANY, "m_bonus(20, 64) at or above 15");
        }

        @Test
        void isRepeatableFromASeed() {
            RandomValueUtils.stateInit(7L);
            int[] first = new int[50];
            for (int index = 0; index < first.length; index++) {
                first[index] = RandomValueUtils.mBonus(10, 64);
            }

            RandomValueUtils.stateInit(7L);
            for (int index = 0; index < first.length; index++) {
                assertEquals(first[index], RandomValueUtils.mBonus(10, 64), "diverged at draw " + index);
            }
        }
    }

    /**
     * The aspect-selected bonus, the port of {@code m_bonus_calc}.
     */
    @Nested
    class MBonusCalc {

        @Test
        void maximisingAndExtremifyingGiveTheMaximum() {
            assertEquals(10, RandomValueUtils.mBonusCalc(10, 64, DamageAspect.MAXIMIZE));
            assertEquals(10, RandomValueUtils.mBonusCalc(10, 64, DamageAspect.EXTREMIFY));
            assertEquals(10, RandomValueUtils.mBonusCalc(10, 0, DamageAspect.MAXIMIZE));
        }

        @Test
        void minimisingGivesZero() {
            assertEquals(0, RandomValueUtils.mBonusCalc(10, 64, DamageAspect.MINIMIZE));
            assertEquals(0, RandomValueUtils.mBonusCalc(10, 127, DamageAspect.MINIMIZE));
        }

        @Test
        void averagingIsMaxTimesLevelOverTheDepthCapInIntegerArithmetic() {
            assertEquals(5, RandomValueUtils.mBonusCalc(10, 64, DamageAspect.AVERAGE));
            assertEquals(9, RandomValueUtils.mBonusCalc(10, 127, DamageAspect.AVERAGE));
            assertEquals(10, RandomValueUtils.mBonusCalc(10, 128, DamageAspect.AVERAGE));
            assertEquals(0, RandomValueUtils.mBonusCalc(7, 1, DamageAspect.AVERAGE));
            assertEquals(0, RandomValueUtils.mBonusCalc(5, 0, DamageAspect.AVERAGE));
        }

        @Test
        void averagingDoesNotCapTheLevelTheWayRandomisingDoes() {
            // m_bonus_calc: 'case AVERAGE: return max * level / MAX_RAND_DEPTH;' with no cap, so a
            // level of 200 gives 15 for a maximum of 10, more than the maximum itself.
            assertEquals(15, RandomValueUtils.mBonusCalc(10, 200, DamageAspect.AVERAGE));
        }

        @Test
        void randomisingRollsAnMBonus() {
            for (int draw = 0; draw < 500; draw++) {
                int bonus = RandomValueUtils.mBonusCalc(10, 64, DamageAspect.RANDOMIZE);

                assertTrue(bonus >= 0 && bonus <= 10, () -> "out of range: " + bonus);
            }
        }
    }
}
