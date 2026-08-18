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

package uk.co.jackoftrades.middle.player;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link PlayerUtils#modifyStatValue}, the port of C's {@code modify_stat_value}
 * ({@code player-util.c:339}).
 *
 * <p>The method applies a bonus or penalty to a stat held in the game's two-speed scale: 3 to 18
 * one point at a time, then in tens, so that 18/10 and 18/20 are stored as 28 and 38. A point of
 * bonus is therefore worth one below 18 and ten above it, and the interesting cases are all at or
 * across that boundary.
 *
 * <p><b>The boundary is tested from both sides and from within.</b> Going up, the step changes
 * after 18 rather than at it; coming down, values in 19–27 — which the scale should never hold —
 * snap back to a clean 18, the branch C comments "Prevent weirdness". Those two are the cases a
 * plausible rewrite gets wrong.
 *
 * <p><b>The point-at-a-time loop is load-bearing</b>, not an optimisation the port could take out.
 * A bonus that crosses 18 is worth different amounts in its two halves, so a closed-form
 * {@code value + amount} or {@code value + amount * step} disagrees with the game. Cases below
 * cross the boundary in both directions with amounts greater than one, which is what pins it.
 *
 * <p><b>The asymmetry between gain and loss is deliberate.</b> Gains have no cap here, while losses
 * stop dead at 3. A group below shows the pair is therefore not reversible, which is the game's
 * behaviour and not a porting slip.
 *
 * <p>{@link #cReference} restates the C body, and a sweep compares the two across the whole
 * plausible range of stats and modifiers. The hand-written cases stay because they say what the
 * behaviour is meant to be; the sweep only says the two agree.
 *
 * <p>The method is an instance method on a class whose every other member is static, so the tests
 * hold an instance. Constructing one runs the class initialiser, which reads the game's player and
 * is content with there being none.
 *
 * <p>Class PlayerUtilsModifyStatValueTest coded on 260818, commented in full on 260818.
 *
 * @author Rowan Crowther
 */
class PlayerUtilsModifyStatValueTest {

    /**
     * The instance the tests call through.
     */
    private PlayerUtils utils;

    /**
     * A transliteration of C's {@code modify_stat_value}, kept deliberately close to the original
     * — same loop, same literals, same branch order — so that the sweep compares against the C and
     * not against a second reading of the Java.
     *
     * @param value  the stat value to modify
     * @param amount the bonus or penalty in points
     * @return the modified stat value
     */
    private static int cReference(int value, int amount) {
        if (amount > 0) {
            for (int i = 0; i < amount; i++) {
                if (value < 18) value++;
                else value += 10;
            }
        } else if (amount < 0) {
            for (int i = 0; i < (0 - amount); i++) {
                if (value >= 18 + 10) value -= 10;
                else if (value > 18) value = 18;
                else if (value > 3) value--;
            }
        }
        return value;
    }

    @BeforeEach
    void setUp() {
        utils = new PlayerUtils();
    }

    /**
     * Bonuses below the boundary, where a point is worth a point.
     */
    @Nested
    class GainsBelowEighteen {

        @ParameterizedTest
        @CsvSource({"3, 1, 4", "10, 1, 11", "17, 1, 18", "3, 5, 8", "10, 7, 17"})
        void aPointIsWorthAPoint(int value, int amount, int expected) {
            assertEquals(expected, utils.modifyStatValue(value, amount));
        }

        @Test
        void aBonusReachingEighteenStopsThereWithoutJumping() {
            assertEquals(18, utils.modifyStatValue(15, 3));
        }
    }

    /**
     * Bonuses at and above the boundary, where a point is worth ten.
     */
    @Nested
    class GainsAtAndAboveEighteen {

        @Test
        void thePointAfterEighteenIsWorthTen() {
            assertEquals(28, utils.modifyStatValue(18, 1));
        }

        @ParameterizedTest
        @CsvSource({"28, 1, 38", "38, 1, 48", "28, 3, 58", "88, 2, 108"})
        void eachFurtherPointAddsAnotherTen(int value, int amount, int expected) {
            assertEquals(expected, utils.modifyStatValue(value, amount));
        }

        /**
         * The step changes after 18, not at it: the point that arrives at 18 is worth one, and only
         * the next is worth ten.
         */
        @Test
        void aBonusCrossingTheBoundaryChangesStepPartWayThrough() {
            assertEquals(28, utils.modifyStatValue(17, 2));
            assertEquals(38, utils.modifyStatValue(17, 3));
        }

        /**
         * The loop is not equivalent to any single multiplication, which this pins: 16 with four
         * points is neither 20 nor 56.
         */
        @Test
        void theResultIsNotAClosedFormOfValueAndAmount() {
            assertEquals(38, utils.modifyStatValue(16, 4));
        }

        @Test
        void gainsAreNotCappedByThisMethod() {
            assertEquals(1018, utils.modifyStatValue(18, 100));
        }
    }

    /**
     * Penalties, which are bounded twice and asymmetrically.
     */
    @Nested
    class Losses {

        @ParameterizedTest
        @CsvSource({"4, -1, 3", "10, -1, 9", "18, -1, 17", "17, -5, 12"})
        void belowTheBoundaryAPointCostsAPoint(int value, int amount, int expected) {
            assertEquals(expected, utils.modifyStatValue(value, amount));
        }

        @ParameterizedTest
        @CsvSource({"28, -1, 18", "38, -1, 28", "58, -3, 28", "38, -2, 18"})
        void atOrAboveTwentyEightAPointCostsTen(int value, int amount, int expected) {
            assertEquals(expected, utils.modifyStatValue(value, amount));
        }

        /**
         * Coming down from 28 lands on 18 rather than continuing in tens, so the boundary is
         * symmetric in value even though the branches are not.
         */
        @Test
        void aLossCrossingTheBoundaryChangesStepPartWayThrough() {
            assertEquals(17, utils.modifyStatValue(28, -2));
        }

        @Test
        void lossesStopAtThree() {
            assertEquals(3, utils.modifyStatValue(3, -1));
            assertEquals(3, utils.modifyStatValue(5, -10));
            assertEquals(3, utils.modifyStatValue(108, -100));
        }

        /**
         * A value already below the floor is left alone rather than being pulled up to it.
         */
        @Test
        void aValueBelowTheFloorIsNotRaisedToIt() {
            assertEquals(1, utils.modifyStatValue(1, -5));
        }

        /**
         * The pair does not undo itself once the floor has been hit — the game's behaviour, and the
         * reason drained stats are worth restoring promptly.
         */
        @Test
        void aLossToTheFloorIsNotReversedByTheSameGain() {
            int drained = utils.modifyStatValue(5, -8);
            assertEquals(3, drained);
            assertEquals(11, utils.modifyStatValue(drained, 8));
        }
    }

    /**
     * Values in 19 to 27, which the scale should never hold — C's "Prevent weirdness" branch.
     */
    @Nested
    class ValuesBetweenTheSteps {

        @ParameterizedTest
        @ValueSource(ints = {19, 20, 24, 27})
        void aSinglePointOfLossSnapsBackToEighteen(int value) {
            assertEquals(18, utils.modifyStatValue(value, -1));
        }

        /**
         * The snap costs a whole point of the penalty, so the rest is spent from 18 downwards.
         */
        @Test
        void theRemainingPenaltyIsSpentFromEighteen() {
            assertEquals(16, utils.modifyStatValue(25, -3));
        }

        /**
         * 28 is the first value that steps in tens rather than snapping, and 27 the last that
         * snaps.
         */
        @Test
        void theSnapBoundarySitsBetweenTwentySevenAndTwentyEight() {
            assertEquals(18, utils.modifyStatValue(27, -1));
            assertEquals(18, utils.modifyStatValue(28, -1));
            assertEquals(28, utils.modifyStatValue(38, -1));
        }

        /**
         * Gains treat such a value as simply above the boundary, with no snapping.
         */
        @Test
        void aGainFromBetweenTheStepsAddsTenWithoutSnapping() {
            assertEquals(30, utils.modifyStatValue(20, 1));
        }
    }

    /**
     * The no-change case, and the sweep against the C.
     */
    @Nested
    class ZeroAndTheSweep {

        @ParameterizedTest
        @ValueSource(ints = {3, 17, 18, 19, 28, 118})
        void anAmountOfZeroReturnsTheValueUnchanged(int value) {
            assertEquals(value, utils.modifyStatValue(value, 0));
        }

        /**
         * Every stat value the game can hold, against every modifier a character could plausibly
         * carry, compared with the transliterated C.
         */
        @Test
        void theSweepAgreesWithTheCReference() {
            int compared = 0;
            for (int value = 0; value <= 220; value++) {
                for (int amount = -40; amount <= 40; amount++) {
                    assertEquals(cReference(value, amount), utils.modifyStatValue(value, amount),
                            "value " + value + ", amount " + amount);
                    compared++;
                }
            }
            assertTrue(compared > 17000, "the sweep should cover the whole range");
        }
    }
}
