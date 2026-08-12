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

package uk.co.jackoftrades.middle.numerics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import uk.co.jackoftrades.middle.enums.DamageAspect;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link RandomValueUtils}, the port of the C source's random-number helpers
 * ({@code src/z-rand.c}).
 *
 * <p>Testing randomness needs care about what is actually being asserted. Three kinds of
 * claim appear below, and they are kept apart deliberately:
 *
 * <ul>
 *   <li><b>Bounds.</b> A roll must land inside its documented range - every time, not on
 *       average. These are asserted over a few hundred draws from a fixed seed, so a
 *       failure is reproducible rather than a flake somebody re-runs and forgets.</li>
 *   <li><b>Coverage.</b> A roll must also be able to <em>reach</em> both ends of its range.
 *       This is what catches an off-by-one that narrows a die without breaking its bounds -
 *       the failure mode a bounds check alone would sail straight past.</li>
 *   <li><b>Exact values.</b> The degenerate cases and the non-random damage aspects have
 *       one right answer and are asserted directly.</li>
 * </ul>
 *
 * <p>Every test seeds the generator first, so the whole class is deterministic. What is
 * <em>not</em> asserted is any particular sequence of draws: that would pin the tests to
 * {@link java.util.Random}'s algorithm rather than to this class's behaviour.
 *
 * <p>{@code mBonus} and {@code mBonusCalc} are absent because they read the world registry
 * and game constants, which puts them outside the reach of a unit test that seeds nothing
 * else; they belong with the registry-backed suites.
 *
 * @author Rowan Crowther
 */
class RandomValueUtilsTest {

    /**
     * How many draws each bounds check makes. Large enough that an off-by-one at either end
     * of a small range is near-certain to show, small enough to stay instant.
     */
    private static final int DRAWS = 500;

    /**
     * A fixed seed, so a failing bounds assertion is reproducible rather than a flake.
     */
    private static final long SEED = 20260810L;

    @BeforeEach
    void seedTheGenerator() {
        RandomValueUtils.stateInit(SEED);
    }

    /**
     * The uniform integer draws.
     */
    @Nested
    class UniformDraws {

        @Test
        void randDivStaysBelowItsBound() {
            for (int draw = 0; draw < DRAWS; draw++) {
                int rolled = RandomValueUtils.randDiv(10);

                assertTrue(rolled >= 0 && rolled < 10, () -> "out of range: " + rolled);
            }
        }

        @Test
        void randDivReachesBothEndsOfItsRange() {
            Set<Integer> seen = new HashSet<>();
            for (int draw = 0; draw < DRAWS; draw++) {
                seen.add(RandomValueUtils.randDiv(10));
            }

            assertTrue(seen.contains(0), "never rolled the low end");
            assertTrue(seen.contains(9), "never rolled the high end");
            assertEquals(10, seen.size(), "did not cover the whole range");
        }

        @Test
        void aDegenerateBoundAlwaysGivesZero() {
            // Guarding rather than throwing: randDiv(0) and randDiv(1) both have exactly one
            // possible answer, and callers pass them freely from data-driven values. This half
            // of the guard is C's own -- 'if (m <= 1) return (0)' (z-rand.c:176).
            assertEquals(0, RandomValueUtils.randDiv(1));
            assertEquals(0, RandomValueUtils.randDiv(0));
        }

        @Test
        void aNegativeBoundIsRejected() {
            // This case used to assert randDiv(-5) == 0, lumped in with the degenerate bounds
            // above. It is not the same thing: 0 and 1 are values a data file can legitimately
            // produce, whereas a negative bound is arithmetic that has already gone wrong at the
            // call site. C never has to decide, because Rand_div takes a uint32_t -- a negative
            // argument wraps past 0x10000000 and trips its assert. The int parameter here makes
            // the bug representable, so returning 0 would hand the caller a plausible-looking
            // roll instead of a failure.
            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
                    () -> RandomValueUtils.randDiv(-5));

            assertTrue(thrown.getMessage().toLowerCase().contains("max"), thrown.getMessage());
        }

        @Test
        void theRejectionReachesTheDelegatingDraws() {
            // randInt0 and randInt1 are bare wrappers, so the guard has to arrive through them
            // rather than being re-implemented; these fail if either grows its own bounds check.
            assertThrows(IllegalArgumentException.class, () -> RandomValueUtils.randInt0(-1));
            assertThrows(IllegalArgumentException.class, () -> RandomValueUtils.randInt1(-1));
        }

        @Test
        void minValueIsRejectedRatherThanWrapping() {
            // The edge worth pinning: Integer.MIN_VALUE has no positive counterpart, so any guard
            // written as a negation or an abs() rather than a comparison silently lets it through.
            assertThrows(IllegalArgumentException.class,
                    () -> RandomValueUtils.randDiv(Integer.MIN_VALUE));
        }

        @Test
        void theSmallestLegalBoundStillDraws() {
            // Immediately above the rejected range: 2 is the smallest bound with a real choice in
            // it, so it catches a guard written as 'max < 2' that would swallow a usable draw.
            Set<Integer> seen = new HashSet<>();
            for (int draw = 0; draw < DRAWS; draw++) {
                int rolled = RandomValueUtils.randDiv(2);
                assertTrue(rolled == 0 || rolled == 1, () -> "out of range: " + rolled);
                seen.add(rolled);
            }

            assertEquals(Set.of(0, 1), seen, "a two-sided draw must produce both outcomes");
        }

        @Test
        void randInt0IsRandDivUnderAnotherName() {
            for (int draw = 0; draw < DRAWS; draw++) {
                int rolled = RandomValueUtils.randInt0(6);

                assertTrue(rolled >= 0 && rolled < 6, () -> "out of range: " + rolled);
            }
        }

        @Test
        void randInt1RollsFromOneUpToItsBoundInclusive() {
            Set<Integer> seen = new HashSet<>();
            for (int draw = 0; draw < DRAWS; draw++) {
                int rolled = RandomValueUtils.randInt1(6);
                assertTrue(rolled >= 1 && rolled <= 6, () -> "out of range: " + rolled);
                seen.add(rolled);
            }

            assertEquals(6, seen.size(), "a six-sided die must roll all six faces");
        }

        @Test
        void aOneSidedDieAlwaysRollsOne() {
            assertEquals(1, RandomValueUtils.randInt1(1));
        }
    }

    /**
     * Spreads around a centre point.
     */
    @Nested
    class Spreads {

        @Test
        void aSpreadStaysWithinItsRadiusOfTheCentre() {
            for (int draw = 0; draw < DRAWS; draw++) {
                int rolled = RandomValueUtils.randSpread(100, 5);

                assertTrue(rolled >= 95 && rolled <= 105, () -> "out of spread: " + rolled);
            }
        }

        @Test
        void aSpreadReachesBothExtremesAndTheCentre() {
            Set<Integer> seen = new HashSet<>();
            for (int draw = 0; draw < DRAWS; draw++) {
                seen.add(RandomValueUtils.randSpread(100, 5));
            }

            assertTrue(seen.contains(95), "never reached the low extreme");
            assertTrue(seen.contains(100), "never reached the centre");
            assertTrue(seen.contains(105), "never reached the high extreme");
            assertEquals(11, seen.size(), "a spread of 5 covers 11 values");
        }

        @Test
        void aZeroSpreadAlwaysGivesTheCentre() {
            assertEquals(100, RandomValueUtils.randSpread(100, 0));
        }

        @Test
        void spreadsWorkAroundANegativeCentre() {
            for (int draw = 0; draw < DRAWS; draw++) {
                int rolled = RandomValueUtils.randSpread(-100, 3);

                assertTrue(rolled >= -103 && rolled <= -97, () -> "out of spread: " + rolled);
            }
        }
    }

    /**
     * The one-in-N probability test.
     */
    @Nested
    class OneIn {

        @Test
        void oneInOneAlwaysFires() {
            for (int draw = 0; draw < DRAWS; draw++) {
                assertTrue(RandomValueUtils.oneIn(1));
            }
        }

        @Test
        void oneInNSometimesFiresAndSometimesDoesNot() {
            int fired = 0;
            for (int draw = 0; draw < DRAWS; draw++) {
                if (RandomValueUtils.oneIn(4)) {
                    fired++;
                }
            }

            // Only that both outcomes occur - the exact frequency is not this class's
            // contract, and asserting it would make the test a distribution test.
            assertTrue(fired > 0, "one-in-four never fired");
            assertTrue(fired < DRAWS, "one-in-four always fired");
        }
    }

    /**
     * Dice rolls and the damage aspects that select how a roll is resolved.
     */
    @Nested
    class Damage {

        @Test
        void aDamageRollStaysBetweenItsMinimumAndMaximum() {
            for (int draw = 0; draw < DRAWS; draw++) {
                int rolled = RandomValueUtils.damRoll(3, 6);

                assertTrue(rolled >= 3 && rolled <= 18, () -> "out of range: " + rolled);
            }
        }

        @Test
        void aDamageRollReachesBothItsMinimumAndItsMaximum() {
            Set<Integer> seen = new HashSet<>();
            for (int draw = 0; draw < 2000; draw++) {
                seen.add(RandomValueUtils.damRoll(2, 4));
            }

            assertTrue(seen.contains(2), "never rolled snake eyes");
            assertTrue(seen.contains(8), "never rolled the maximum");
        }

        @Test
        void rollingNoDiceOrDiceWithNoSidesGivesNoDamage() {
            assertEquals(0, RandomValueUtils.damRoll(0, 6));
            assertEquals(0, RandomValueUtils.damRoll(3, 0));
            assertEquals(0, RandomValueUtils.damRoll(-1, 6));
            assertEquals(0, RandomValueUtils.damRoll(3, -1));
        }

        @Test
        void maximisingAndExtremifyingTakeEveryDieAtItsHighestFace() {
            assertEquals(18, RandomValueUtils.damCalc(3, 6, DamageAspect.MAXIMIZE));
            assertEquals(18, RandomValueUtils.damCalc(3, 6, DamageAspect.EXTREMIFY));
        }

        @Test
        void minimisingTakesEveryDieAtOne() {
            assertEquals(3, RandomValueUtils.damCalc(3, 6, DamageAspect.MINIMIZE));
        }

        @Test
        void averagingTakesTheMeanFaceAndTruncates() {
            // 3d6 averages 10.5, which truncates to 10 - the C original does the same
            // integer division rather than rounding.
            assertEquals(10, RandomValueUtils.damCalc(3, 6, DamageAspect.AVERAGE));
            assertEquals(5, RandomValueUtils.damCalc(2, 4, DamageAspect.AVERAGE));
        }

        @Test
        void randomisingRollsWithinTheDiceRange() {
            for (int draw = 0; draw < DRAWS; draw++) {
                int rolled = RandomValueUtils.damCalc(3, 6, DamageAspect.RANDOMIZE);

                assertTrue(rolled >= 3 && rolled <= 18, () -> "out of range: " + rolled);
            }
        }

        @ParameterizedTest
        @EnumSource(DamageAspect.class)
        void everyAspectResolvesBetweenTheMinimumAndMaximumRoll(DamageAspect aspect) {
            // Whatever an aspect means, it must produce something a 3d6 could actually roll.
            int result = RandomValueUtils.damCalc(3, 6, aspect);

            assertTrue(result >= 3 && result <= 18, () -> aspect + " gave " + result);
        }
    }

    /**
     * Ranged draws and probabilistic division.
     */
    @Nested
    class RangesAndDivision {

        @Test
        void aRangeWithNoWidthGivesItsOnlyValue() {
            assertEquals(7, RandomValueUtils.randRange(7, 7));
            assertEquals(-3, RandomValueUtils.randRange(-3, -3));
        }

        @Test
        void anInvertedRangeIsRejected() {
            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
                    () -> RandomValueUtils.randRange(9, 4));

            assertTrue(thrown.getMessage().contains("9"), thrown.getMessage());
            assertTrue(thrown.getMessage().contains("4"), thrown.getMessage());
        }

        @Test
        void aRangeDrawsInclusivelyBetweenItsEnds() {
            // Was disabled while randRange used randInt1, which shifted every ranged value one
            // too high and made the requested lower bound unreachable. It now matches C's
            // 'A + Rand_div(1 + B - A)' (z-rand.c:399), so both ends are drawable again.
            Set<Integer> seen = new HashSet<>();
            for (int draw = 0; draw < DRAWS; draw++) {
                int rolled = RandomValueUtils.randRange(4, 9);
                assertTrue(rolled >= 4 && rolled <= 9, () -> "out of range: " + rolled);
                seen.add(rolled);
            }

            assertTrue(seen.contains(4), "never reached the low end");
            assertTrue(seen.contains(9), "never reached the high end");
        }

        @Test
        void anExactDivisionIsAlwaysExact() {
            // No remainder means nothing to round, so the answer cannot vary between draws.
            for (int draw = 0; draw < DRAWS; draw++) {
                assertEquals(5, RandomValueUtils.simulateDivision(10, 2));
            }
        }

        @Test
        void anInexactDivisionRoundsToOneOfTheTwoNeighbouringIntegers() {
            for (int draw = 0; draw < DRAWS; draw++) {
                int result = RandomValueUtils.simulateDivision(7, 2);

                assertTrue(result == 3 || result == 4, () -> "unexpected quotient: " + result);
            }
        }

        @Test
        void anInexactDivisionRoundsBothWaysOverManyDraws() {
            // The point of the helper: 7/2 must come out as 4 sometimes, or the fractional
            // half is being discarded and every derived value drifts low.
            Set<Integer> seen = new HashSet<>();
            for (int draw = 0; draw < DRAWS; draw++) {
                seen.add(RandomValueUtils.simulateDivision(7, 2));
            }

            assertTrue(seen.contains(3), "never rounded down");
            assertTrue(seen.contains(4), "never rounded up");
        }

        @Test
        void dividingByZeroIsAnArithmeticError() {
            assertThrows(ArithmeticException.class,
                    () -> RandomValueUtils.simulateDivision(7, 0));
        }
    }

    /**
     * Seeding, which is what makes a level reproducible from a saved game.
     */
    @Nested
    class Seeding {

        @Test
        void theSameSeedReproducesTheSameSequence() {
            RandomValueUtils.stateInit(12345L);
            int[] first = new int[20];
            for (int index = 0; index < first.length; index++) {
                first[index] = RandomValueUtils.randDiv(1000);
            }

            RandomValueUtils.stateInit(12345L);
            for (int index = 0; index < first.length; index++) {
                assertEquals(first[index], RandomValueUtils.randDiv(1000),
                        "sequences diverged at draw " + index);
            }
        }

        @Test
        void differentSeedsGiveDifferentSequences() {
            RandomValueUtils.stateInit(1L);
            int[] first = new int[20];
            for (int index = 0; index < first.length; index++) {
                first[index] = RandomValueUtils.randDiv(1000);
            }

            RandomValueUtils.stateInit(2L);
            boolean diverged = false;
            for (int value : first) {
                if (value != RandomValueUtils.randDiv(1000)) {
                    diverged = true;
                }
            }

            assertTrue(diverged, "two different seeds produced identical draws");
        }

        @Test
        void theArgumentlessSeedingDoesNotThrow() {
            // It derives a seed from the clock and the process id; nothing about the value
            // can be asserted, only that the derivation works.
            RandomValueUtils.stateInit();

            int rolled = RandomValueUtils.randDiv(10);
            assertTrue(rolled >= 0 && rolled < 10);
        }
    }
}
