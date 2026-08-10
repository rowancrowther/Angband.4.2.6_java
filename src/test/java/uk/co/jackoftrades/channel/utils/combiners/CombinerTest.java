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

package uk.co.jackoftrades.channel.utils.combiners;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import uk.co.jackoftrades.channel.utils.Combiner;
import uk.co.jackoftrades.channel.utils.UIEntryCombinerState;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Unit tests for the {@link Combiner} family, the port of the C source's character-sheet
 * value combiners ({@code src/ui-entry-combiner.c}).
 *
 * <p>A combiner folds the several contributions to one character-sheet line - one per
 * equipped item, say - into the single number the player sees. Each strategy answers a
 * different question ("what is the total?", "what is the best?", "does anything grant
 * this?"), and each is reachable through two independent code paths that the C original
 * keeps deliberately in step: an incremental
 * {@code init}/{@code accum}/{@code finish} sequence, and a one-shot {@code vec} over a
 * whole list. Both are exercised here, because the two implementations share no code and a
 * fix applied to one can silently miss the other.
 *
 * <p>What makes these worth testing beyond their arithmetic is the sentinel protocol. Three
 * values at the top of the {@code int} range are not values at all:
 * {@code UI_ENTRY_VALUE_NOT_PRESENT} means "this source has nothing to say", so it must be
 * skipped without disturbing the accumulator; {@code UI_ENTRY_UNKNOWN_VALUE} means "there
 * is something here but the player has not learned it", so it may occupy an empty
 * accumulator but must never displace a real value; and {@code UI_ENTRY_RESIST0_RES_VUL}
 * marks the specific case of resistance and vulnerability to the same element. Ordinary
 * arithmetic on those numbers would produce garbage, so every strategy filters them first -
 * and the ordering of those guards is the whole substance of the port.
 *
 * @author Rowan Crowther
 */
class CombinerTest {

    /**
     * "This source contributes nothing" - must leave an accumulator untouched.
     */
    private static final int NOT_PRESENT = Combiner.UI_ENTRY_VALUE_NOT_PRESENT;
    /**
     * "Something is here but the player has not learned what" - may fill an empty
     * accumulator, never displaces a real value.
     */
    private static final int UNKNOWN = Combiner.UI_ENTRY_UNKNOWN_VALUE;
    /**
     * "Both resistant and vulnerable to this element" - only meaningful to
     * {@link Resist0Combiner}.
     */
    private static final int RES_VUL = Combiner.UI_ENTRY_RESIST0_RES_VUL;

    /**
     * Runs the incremental path: {@code init} with the first pair, {@code accum} for each
     * subsequent pair, then {@code finish}.
     *
     * @param combiner the strategy under test
     * @param values   the primary values, at least one
     * @param auxs     the auxiliary values, the same length as {@code values}
     * @return the finished state
     */
    private static UIEntryCombinerState fold(Combiner combiner, List<Integer> values,
                                             List<Integer> auxs) {
        combiner.init(values.get(0), auxs.get(0));
        for (int index = 1; index < values.size(); index++) {
            combiner.accum(values.get(index), auxs.get(index));
        }
        return combiner.finish();
    }

    /**
     * Asserts that both the incremental and the one-shot path reduce the same input to the
     * same primary and auxiliary result.
     *
     * @param combiner    a fresh strategy instance
     * @param values      the primary values
     * @param auxs        the auxiliary values
     * @param expected    the primary result both paths must produce
     * @param expectedAux the auxiliary result both paths must produce
     */
    private static void assertBothPathsGive(Combiner combiner, List<Integer> values,
                                            List<Integer> auxs, int expected, int expectedAux) {
        UIEntryCombinerState incremental = fold(combiner.clone(), values, auxs);
        assertEquals(expected, incremental.getAccum(), "incremental accum");
        assertEquals(expectedAux, incremental.getAccumAux(), "incremental accumAux");

        UIEntryCombinerState oneShot = combiner.clone().vec(values.size(), values, auxs);
        assertEquals(expected, oneShot.getAccum(), "vec accum");
        assertEquals(expectedAux, oneShot.getAccumAux(), "vec accumAux");
    }

    /**
     * Every strategy, for the contract tests that apply to all of them.
     *
     * @return one fresh instance of each combiner
     */
    private static Stream<Combiner> allCombiners() {
        return Stream.of(new AddCombiner(), new BitwiseOrCombiner(), new FirstCombiner(),
                new LargestCombiner(), new LastCombiner(), new LogicalOrCombiner(),
                new LogicalOrWithCancelCombiner(), new Resist0Combiner(),
                new SmallestCombiner());
    }

    @Test
    void theSharedContractCoversEveryStrategyInThePackage() {
        // The contract tests are only as good as allCombiners(). If a tenth strategy is
        // added and not listed there it would silently escape all of them, so the count is
        // pinned here where the failure names the reason.
        assertEquals(9, allCombiners().count());
    }

    /**
     * Behaviour every strategy shares, regardless of how it folds values.
     */
    @Nested
    class SharedContract {

        @Test
        void theThreeSentinelsAreDistinctAndSitAtTheTopOfTheRange() {
            // Ordinary values must never collide with them, which is only true because they
            // are the three largest ints - the reason the accumulators saturate at MAX - 2.
            assertEquals(Integer.MAX_VALUE, UNKNOWN);
            assertEquals(Integer.MAX_VALUE - 1, NOT_PRESENT);
            assertEquals(Integer.MAX_VALUE - 2, RES_VUL);
        }

        @ParameterizedTest
        @MethodSource("uk.co.jackoftrades.channel.utils.combiners.CombinerTest#allCombiners")
        void vecRejectsAListShorterThanTheRequestedCount(Combiner combiner) {
            // Guarding rather than throwing: the C original indexes raw arrays here, so a
            // short list is a caller bug that must be caught before it reads off the end.
            assertNull(combiner.vec(5, List.of(1, 2), List.of(1, 2)));
            assertNull(combiner.vec(3, List.of(1, 2, 3), List.of(1)));
            assertNull(combiner.vec(1, List.of(), List.of()));
        }

        @ParameterizedTest
        @MethodSource("uk.co.jackoftrades.channel.utils.combiners.CombinerTest#allCombiners")
        void vecOfNothingIsNotPresent(Combiner combiner) {
            UIEntryCombinerState result = combiner.vec(0, List.of(), List.of());

            assertEquals(NOT_PRESENT, result.getAccum());
            assertEquals(NOT_PRESENT, result.getAccumAux());
        }

        @ParameterizedTest
        @MethodSource("uk.co.jackoftrades.channel.utils.combiners.CombinerTest#allCombiners")
        void vecOfASingleValueIsThatValue(Combiner combiner) {
            // Whatever the strategy, folding one contribution cannot change it - except for
            // the two that normalise ("is it non-zero?"), so a plain 1 is used here.
            UIEntryCombinerState result = combiner.vec(1, List.of(1), List.of(1));

            assertEquals(1, result.getAccum(), combiner.getClass().getSimpleName());
            assertEquals(1, result.getAccumAux(), combiner.getClass().getSimpleName());
        }

        @ParameterizedTest
        @MethodSource("uk.co.jackoftrades.channel.utils.combiners.CombinerTest#allCombiners")
        void cloningAnUninitialisedCombinerGivesAFreshOneOfTheSameKind(Combiner combiner) {
            Combiner copy = combiner.clone();

            assertNotSame(combiner, copy);
            assertEquals(combiner.getClass(), copy.getClass());
        }

        @ParameterizedTest
        @MethodSource("uk.co.jackoftrades.channel.utils.combiners.CombinerTest#allCombiners")
        void aCloneCarriesTheStateButAccumulatesIndependently(Combiner combiner) {
            combiner.init(1, 1);

            Combiner copy = combiner.clone();
            copy.accum(1, 1);

            // The original must be unaffected by work done on the copy - this is what lets
            // a partially folded combiner be reused across several character-sheet rows.
            assertEquals(1, combiner.finish().getAccum());
        }

        @ParameterizedTest
        @MethodSource("uk.co.jackoftrades.channel.utils.combiners.CombinerTest#allCombiners")
        void finishReturnsADetachedStateRatherThanTheLiveOne(Combiner combiner) {
            combiner.init(1, 1);

            UIEntryCombinerState first = combiner.finish();
            UIEntryCombinerState second = combiner.finish();

            assertNotSame(first, second);
        }

        @ParameterizedTest
        @MethodSource("uk.co.jackoftrades.channel.utils.combiners.CombinerTest#allCombiners")
        void anAbsentContributionIsSkippedEntirely(Combiner combiner) {
            // NOT_PRESENT is the "no opinion" sentinel: folding it in must give the same
            // answer as not folding it at all, for every strategy.
            UIEntryCombinerState withGap =
                    combiner.clone().vec(3, List.of(1, NOT_PRESENT, 1), List.of(1, NOT_PRESENT, 1));
            UIEntryCombinerState withoutGap =
                    combiner.clone().vec(2, List.of(1, 1), List.of(1, 1));

            assertEquals(withoutGap.getAccum(), withGap.getAccum(),
                    combiner.getClass().getSimpleName());
        }
    }

    /**
     * {@link AddCombiner} - totals, as used for numeric bonuses that stack.
     */
    @Nested
    class Adding {

        @Test
        void valuesAndAuxiliariesAreSummedSeparately() {
            assertBothPathsGive(new AddCombiner(), List.of(1, 2, 3), List.of(10, 20, 30),
                    6, 60);
        }

        @Test
        void negativeContributionsSubtract() {
            assertBothPathsGive(new AddCombiner(), List.of(5, -3), List.of(0, 0), 2, 0);
        }

        @Test
        void anUnknownContributionOccupiesAnEmptyAccumulatorButNotAFullOne() {
            assertBothPathsGive(new AddCombiner(), List.of(NOT_PRESENT, UNKNOWN),
                    List.of(NOT_PRESENT, UNKNOWN), UNKNOWN, UNKNOWN);
            assertBothPathsGive(new AddCombiner(), List.of(5, UNKNOWN), List.of(0, 0), 5, 0);
        }

        @Test
        void aRealValueDisplacesAnUnknownAccumulator() {
            assertBothPathsGive(new AddCombiner(), List.of(UNKNOWN, 5), List.of(0, 0), 5, 0);
        }

        @Test
        void additionSaturatesJustBelowTheSentinelRange() {
            // The cap is MAX - 2 rather than MAX so a saturated total can never be mistaken
            // for one of the three sentinels.
            AddCombiner combiner = new AddCombiner();
            combiner.init(10, 0);
            combiner.accum(Integer.MAX_VALUE - 3, 0);

            assertEquals(Integer.MAX_VALUE - 2, combiner.finish().getAccum());
        }

        @Test
        void additionSaturatesAtTheBottomOfTheRangeToo() {
            AddCombiner combiner = new AddCombiner();
            combiner.init(-10, 0);
            combiner.accum(Integer.MIN_VALUE + 5, 0);

            assertEquals(Integer.MIN_VALUE, combiner.finish().getAccum());
        }
    }

    /**
     * {@link BitwiseOrCombiner} - accumulating a set of bits.
     */
    @Nested
    class BitwiseOr {

        @Test
        void bitsFromEveryContributionAreUnioned() {
            assertBothPathsGive(new BitwiseOrCombiner(), List.of(0b0101, 0b0011),
                    List.of(0b1000, 0b0001), 0b0111, 0b1001);
        }

        @Test
        void orIsIdempotent() {
            assertBothPathsGive(new BitwiseOrCombiner(), List.of(0b0110, 0b0110),
                    List.of(0, 0), 0b0110, 0);
        }

        @Test
        void aRealValueDisplacesAnUnknownAccumulatorRatherThanBeingOredIntoIt() {
            // OR-ing into UNKNOWN (all bits set) would swallow the value entirely, so the
            // sentinel has to be replaced outright.
            assertBothPathsGive(new BitwiseOrCombiner(), List.of(UNKNOWN, 0b0100),
                    List.of(0, 0), 0b0100, 0);
        }
    }

    /**
     * {@link FirstCombiner} and {@link LastCombiner} - positional picks.
     */
    @Nested
    class Positional {

        @Test
        void firstKeepsTheOpeningContributionAndIgnoresTheRest() {
            assertBothPathsGive(new FirstCombiner(), List.of(7, 8, 9), List.of(70, 80, 90),
                    7, 70);
        }

        @Test
        void lastKeepsTheClosingContribution() {
            assertBothPathsGive(new LastCombiner(), List.of(7, 8, 9), List.of(70, 80, 90),
                    9, 90);
        }

        @Test
        void positionalPicksDoNotFilterSentinels() {
            // Unlike every other strategy these take the value at a position verbatim, so a
            // sentinel in that slot survives to the caller.
            UIEntryCombinerState first =
                    new FirstCombiner().vec(2, List.of(UNKNOWN, 3), List.of(0, 0));
            assertEquals(UNKNOWN, first.getAccum());

            UIEntryCombinerState last =
                    new LastCombiner().vec(2, List.of(3, UNKNOWN), List.of(0, 0));
            assertEquals(UNKNOWN, last.getAccum());
        }
    }

    /**
     * {@link LargestCombiner} and {@link SmallestCombiner} - extremes.
     */
    @Nested
    class Extremes {

        @Test
        void largestPicksTheMaximumOfEachChannel() {
            assertBothPathsGive(new LargestCombiner(), List.of(1, 9, 5), List.of(30, 10, 20),
                    9, 30);
        }

        @Test
        void smallestPicksTheMinimumOfEachChannel() {
            assertBothPathsGive(new SmallestCombiner(), List.of(9, 1, 5), List.of(30, 10, 20),
                    1, 10);
        }

        @Test
        void extremesHandleNegativesByValueNotMagnitude() {
            assertBothPathsGive(new LargestCombiner(), List.of(-5, -1), List.of(0, 0), -1, 0);
            assertBothPathsGive(new SmallestCombiner(), List.of(-5, -1), List.of(0, 0), -5, 0);
        }

        @Test
        void theUnknownSentinelIsNotTreatedAsAHugeNumber() {
            // UNKNOWN is Integer.MAX_VALUE; a naive max() would return it and report the
            // player's best resistance as an unlearned rune.
            assertBothPathsGive(new LargestCombiner(), List.of(5, UNKNOWN), List.of(0, 0),
                    5, 0);
        }

        @Test
        void theNotPresentSentinelIsNotTreatedAsAHugeNumberEither() {
            assertBothPathsGive(new LargestCombiner(), List.of(5, NOT_PRESENT), List.of(0, 0),
                    5, 0);
            assertBothPathsGive(new SmallestCombiner(), List.of(5, NOT_PRESENT), List.of(0, 0),
                    5, 0);
        }
    }

    /**
     * {@link LogicalOrCombiner} - "does anything grant this at all?".
     */
    @Nested
    class LogicalOr {

        @Test
        void anyNonZeroContributionNormalisesTheResultToOne() {
            assertBothPathsGive(new LogicalOrCombiner(), List.of(0, 7), List.of(0, 0), 1, 0);
            assertBothPathsGive(new LogicalOrCombiner(), List.of(5, 5), List.of(0, 0), 1, 0);
        }

        @Test
        void allZeroContributionsStayZero() {
            assertBothPathsGive(new LogicalOrCombiner(), List.of(0, 0), List.of(0, 0), 0, 0);
        }

        @Test
        void aNegativeContributionCountsAsPresent() {
            // Plain logical-or has no notion of cancelling; -1 is simply non-zero.
            assertBothPathsGive(new LogicalOrCombiner(), List.of(0, -1), List.of(0, 0), 1, 0);
        }

        @Test
        void theValueAndAuxiliaryChannelsAreIndependent() {
            assertBothPathsGive(new LogicalOrCombiner(), List.of(0, 0), List.of(0, 3), 0, 1);
        }
    }

    /**
     * {@link LogicalOrWithCancelCombiner} - as above, but a negative contribution cancels a
     * positive one, which is how a cursed item's penalty overrides an ordinary grant.
     */
    @Nested
    class LogicalOrWithCancel {

        @Test
        void positiveContributionsAloneGiveOne() {
            assertBothPathsGive(new LogicalOrWithCancelCombiner(), List.of(0, 5),
                    List.of(0, 0), 1, 0);
        }

        @Test
        void aNegativeContributionCancelsAPositiveOne() {
            // Both bits end up set; finish() collapses that to -1, the "cancelled" answer.
            assertBothPathsGive(new LogicalOrWithCancelCombiner(), List.of(3, -2),
                    List.of(0, 0), -1, 0);
        }

        @Test
        void orderOfContributionsDoesNotMatter() {
            assertBothPathsGive(new LogicalOrWithCancelCombiner(), List.of(-2, 3),
                    List.of(0, 0), -1, 0);
        }

        @Test
        void negativeContributionsAloneAlsoGiveMinusOne() {
            assertBothPathsGive(new LogicalOrWithCancelCombiner(), List.of(-1, -4),
                    List.of(0, 0), -1, 0);
        }

        @Test
        void allZeroContributionsStayZero() {
            assertBothPathsGive(new LogicalOrWithCancelCombiner(), List.of(0, 0),
                    List.of(0, 0), 0, 0);
        }

        @Test
        void theIntermediateBitPairNeverEscapesThroughFinish() {
            // The accumulator holds 1 for "some positive", 2 for "some negative" and 3 for
            // both; only finish() maps that back to the -1/0/1 the UI understands, so a
            // bare 2 or 3 reaching a caller would be a leak of the internal encoding.
            LogicalOrWithCancelCombiner combiner = new LogicalOrWithCancelCombiner();
            combiner.init(-5, 0);

            assertEquals(-1, combiner.finish().getAccum());
        }
    }

    /**
     * {@link Resist0Combiner} - element resistance, which tracks resistance and
     * vulnerability separately before collapsing them.
     */
    @Nested
    class Resist0 {

        @Test
        void resistanceAloneIsReportedAsItsLevel() {
            assertBothPathsGive(new Resist0Combiner(), List.of(2), List.of(0), 2, 0);
        }

        @Test
        void theStrongestResistanceWins() {
            assertBothPathsGive(new Resist0Combiner(), List.of(1, 2), List.of(0, 0), 2, 0);
        }

        @Test
        void vulnerabilityAloneIsReportedAsMinusOne() {
            assertBothPathsGive(new Resist0Combiner(), List.of(-1), List.of(0), -1, 0);
        }

        @Test
        void resistanceAndVulnerabilityTogetherCollapseToTheResVulSentinel() {
            // The player is both resistant and vulnerable; neither number alone would be
            // honest, so the UI is handed the sentinel that says exactly that.
            assertBothPathsGive(new Resist0Combiner(), List.of(2, -1), List.of(0, 0),
                    RES_VUL, 0);
        }

        @Test
        void theResVulSentinelSurvivesBeingFedBackIn() {
            assertBothPathsGive(new Resist0Combiner(), List.of(RES_VUL), List.of(0),
                    RES_VUL, 0);
        }

        @Test
        void immunityOutranksVulnerabilityInsteadOfCollapsing() {
            // Level 3 is immunity: it is not merely the strongest resistance, it is past the
            // threshold where a vulnerability can still qualify the answer.
            assertBothPathsGive(new Resist0Combiner(), List.of(3, -1), List.of(0, 0), 3, 0);
        }

        @Test
        void finishClearsTheNegativeAccumulatorItUsedAsScratchSpace() {
            Resist0Combiner combiner = new Resist0Combiner();
            combiner.init(-1, -1);

            UIEntryCombinerState finished = combiner.finish();

            assertEquals(0, finished.getNegAccum());
            assertEquals(0, finished.getNegAccumAux());
        }
    }

    /**
     * The mutable state object the combiners fold into.
     */
    @Nested
    class State {

        @Test
        void aFreshStateIsAllZeroes() {
            UIEntryCombinerState state = new UIEntryCombinerState();

            assertEquals(0, state.getAccum());
            assertEquals(0, state.getAccumAux());
            assertEquals(0, state.getNegAccum());
            assertEquals(0, state.getNegAccumAux());
        }

        @Test
        void eachChannelIsStoredAndReadBackIndependently() {
            UIEntryCombinerState state = new UIEntryCombinerState();

            state.setAccum(1);
            state.setAccumAux(2);
            state.setNegAccum(3);
            state.setNegAccumAux(4);

            assertEquals(1, state.getAccum());
            assertEquals(2, state.getAccumAux());
            assertEquals(3, state.getNegAccum());
            assertEquals(4, state.getNegAccumAux());
        }
    }
}
