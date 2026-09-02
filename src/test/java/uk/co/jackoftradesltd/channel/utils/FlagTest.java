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

package uk.co.jackoftradesltd.channel.utils;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link Flag}, the port of the C source's flag-array machinery
 * ({@code src/z-bitflag.c}) onto an {@link java.util.EnumSet}.
 *
 * <p>The C original stores flags as a packed array of bytes and exposes a family of
 * operations - {@code flag_on}, {@code flag_union}, {@code flag_inter}, {@code flag_diff}
 * and so on - most of which return a boolean saying whether the call actually changed
 * anything. That return value is not decoration: ported call sites use it to decide whether
 * to recompute derived state or redraw, so a method that performed the right mutation but
 * reported the wrong "changed" answer would be a live bug that a set-contents-only
 * assertion would miss entirely. Every mutator here is therefore checked twice - once for
 * the resulting set, once for what it claimed.
 *
 * <p>Two names read backwards from their behaviour and are pinned deliberately:
 * {@link Flag#isSubset} asks whether the <em>argument</em> is contained in the receiver,
 * and {@link Flag#test} is an any-of test while {@link Flag#testAll} is the all-of one.
 *
 * <p>The tests use a local enum as the flag domain; nothing depends on its meaning, only on
 * its having a stable set of constants, which a game enum under active porting would not.
 *
 * @author Rowan Crowther
 */
class FlagTest {

    /**
     * The flag set under test, empty at the start of every test.
     */
    private Flag<TestFlag> flags;

    /**
     * Builds a populated flag set in one expression.
     *
     * @param members the flags to switch on
     * @return a new flag set holding exactly those flags
     */
    private static Flag<TestFlag> flagsOf(TestFlag... members) {
        Flag<TestFlag> result = new Flag<>(TestFlag.class);
        result.set(members);
        return result;
    }

    /**
     * Collects a flag set's contents in iteration order.
     *
     * @param flag the set to drain
     * @return its members, in the order the iterator yields them
     */
    private static List<TestFlag> contentsOf(Flag<TestFlag> flag) {
        List<TestFlag> result = new ArrayList<>();
        for (TestFlag member : flag) {
            result.add(member);
        }
        return result;
    }

    @BeforeEach
    void setUp() {
        flags = new Flag<>(TestFlag.class);
    }

    /**
     * A small stand-in enum for a flag domain, kept local so the tests do not break when a
     * game enum gains or loses constants.
     */
    private enum TestFlag {
        /**
         * First test constant.
         */
        ALPHA,
        /**
         * Second test constant.
         */
        BETA,
        /**
         * Third test constant.
         */
        GAMMA,
        /**
         * Fourth and (by natural ordering) last test constant.
         */
        DELTA
    }

    /**
     * The empty/full/count predicates.
     */
    @Nested
    class Population {

        @Test
        void aFreshFlagSetIsEmpty() {
            assertTrue(flags.isEmpty());
            assertFalse(flags.isFull());
            assertEquals(0, flags.count());
        }

        @Test
        void countTracksTheNumberOfFlagsSwitchedOn() {
            flags.on(TestFlag.ALPHA);
            assertEquals(1, flags.count());

            flags.on(TestFlag.BETA);
            assertEquals(2, flags.count());

            flags.off(TestFlag.ALPHA);
            assertEquals(1, flags.count());
        }

        @Test
        void aPartiallyPopulatedSetIsNeitherEmptyNorFull() {
            flags.on(TestFlag.ALPHA);

            assertFalse(flags.isEmpty());
            assertFalse(flags.isFull());
        }

        @Test
        void setAllFillsTheSetAndWipeEmptiesIt() {
            flags.setAll();

            assertTrue(flags.isFull());
            assertEquals(TestFlag.values().length, flags.count());

            flags.wipe();

            assertTrue(flags.isEmpty());
            assertEquals(0, flags.count());
        }
    }

    /**
     * Switching individual flags on and off, and the "did this change anything" contract.
     */
    @Nested
    class SingleFlagMutation {

        @Test
        void switchingAFlagOnReportsTheChangeOnlyTheFirstTime() {
            assertTrue(flags.on(TestFlag.ALPHA));
            assertTrue(flags.has(TestFlag.ALPHA));

            assertFalse(flags.on(TestFlag.ALPHA));
            assertEquals(1, flags.count());
        }

        @Test
        void switchingAFlagOffReportsTheChangeOnlyWhenItWasSet() {
            flags.on(TestFlag.ALPHA);

            assertTrue(flags.off(TestFlag.ALPHA));
            assertFalse(flags.has(TestFlag.ALPHA));

            assertFalse(flags.off(TestFlag.ALPHA));
        }

        @Test
        void hasIsFalseForFlagsThatWereNeverSet() {
            flags.on(TestFlag.ALPHA);

            assertFalse(flags.has(TestFlag.BETA));
        }

        @Test
        void negateSwapsEveryFlagForItsOpposite() {
            flags.set(TestFlag.ALPHA, TestFlag.GAMMA);

            flags.negate();

            assertFalse(flags.has(TestFlag.ALPHA));
            assertTrue(flags.has(TestFlag.BETA));
            assertFalse(flags.has(TestFlag.GAMMA));
            assertTrue(flags.has(TestFlag.DELTA));
        }

        @Test
        void negatingTwiceRestoresTheOriginal() {
            flags.set(TestFlag.ALPHA, TestFlag.GAMMA);

            flags.negate();
            flags.negate();

            assertTrue(flags.isEqual(flagsOf(TestFlag.ALPHA, TestFlag.GAMMA)));
        }

        @Test
        void negatingAnEmptySetFillsIt() {
            flags.negate();

            assertTrue(flags.isFull());
        }
    }

    /**
     * Bulk mutation from varargs and from lists - both overloads of each method exist and
     * both are used by ported code, so both are exercised.
     */
    @Nested
    class BulkMutation {

        @Test
        void setSwitchesSeveralFlagsOnAndReportsWhetherAnythingChanged() {
            assertTrue(flags.set(TestFlag.ALPHA, TestFlag.BETA));
            assertEquals(2, flags.count());

            assertFalse(flags.set(TestFlag.ALPHA, TestFlag.BETA));
        }

        @Test
        void setReportsAChangeWhenEvenOneFlagIsNew() {
            flags.set(TestFlag.ALPHA);

            assertTrue(flags.set(TestFlag.ALPHA, TestFlag.BETA));
        }

        @Test
        void theListOverloadOfSetBehavesLikeTheVarargsOne() {
            assertTrue(flags.set(List.of(TestFlag.ALPHA, TestFlag.BETA)));

            assertTrue(flags.isEqual(flagsOf(TestFlag.ALPHA, TestFlag.BETA)));
        }

        @Test
        void clearSwitchesSeveralFlagsOffAndReportsWhetherAnythingChanged() {
            flags.set(TestFlag.ALPHA, TestFlag.BETA, TestFlag.GAMMA);

            assertTrue(flags.clear(TestFlag.ALPHA, TestFlag.BETA));
            assertTrue(flags.isEqual(flagsOf(TestFlag.GAMMA)));

            assertFalse(flags.clear(TestFlag.ALPHA, TestFlag.BETA));
        }

        @Test
        void theListOverloadOfClearBehavesLikeTheVarargsOne() {
            flags.set(TestFlag.ALPHA, TestFlag.BETA);

            assertTrue(flags.clear(List.of(TestFlag.ALPHA)));
            assertTrue(flags.isEqual(flagsOf(TestFlag.BETA)));
        }

        @Test
        void initReplacesTheContentsRatherThanAddingToThem() {
            flags.set(TestFlag.ALPHA, TestFlag.BETA);

            flags.init(TestFlag.GAMMA);

            assertTrue(flags.isEqual(flagsOf(TestFlag.GAMMA)));
        }

        @Test
        void theListOverloadOfInitAlsoReplaces() {
            flags.set(TestFlag.ALPHA);

            flags.init(List.of(TestFlag.BETA, TestFlag.DELTA));

            assertTrue(flags.isEqual(flagsOf(TestFlag.BETA, TestFlag.DELTA)));
        }

        @Test
        void initWithNothingEmptiesTheSet() {
            flags.setAll();

            flags.init(List.of());

            assertTrue(flags.isEmpty());
        }
    }

    /**
     * Set algebra between two flag sets.
     */
    @Nested
    class SetOperations {

        @Test
        void unionAddsTheOtherSetsFlagsAndReportsWhetherAnythingChanged() {
            flags.set(TestFlag.ALPHA);

            assertTrue(flags.union(flagsOf(TestFlag.BETA)));
            assertTrue(flags.isEqual(flagsOf(TestFlag.ALPHA, TestFlag.BETA)));

            assertFalse(flags.union(flagsOf(TestFlag.ALPHA)));
        }

        @Test
        void unionLeavesTheOtherSetAlone() {
            Flag<TestFlag> other = flagsOf(TestFlag.BETA);
            flags.set(TestFlag.ALPHA);

            flags.union(other);

            assertTrue(other.isEqual(flagsOf(TestFlag.BETA)));
        }

        @Test
        void interKeepsOnlyTheFlagsPresentInBoth() {
            flags.set(TestFlag.ALPHA, TestFlag.BETA);

            assertTrue(flags.inter(flagsOf(TestFlag.BETA, TestFlag.GAMMA)));
            assertTrue(flags.isEqual(flagsOf(TestFlag.BETA)));
        }

        @Test
        void interWithASupersetChangesNothing() {
            flags.set(TestFlag.ALPHA);

            assertFalse(flags.inter(flagsOf(TestFlag.ALPHA, TestFlag.BETA)));
            assertTrue(flags.isEqual(flagsOf(TestFlag.ALPHA)));
        }

        @Test
        void interWithADisjointSetEmptiesTheReceiver() {
            flags.set(TestFlag.ALPHA);

            assertTrue(flags.inter(flagsOf(TestFlag.BETA)));
            assertTrue(flags.isEmpty());
        }

        @Test
        void diffRemovesTheOtherSetsFlagsAndReportsWhetherAnythingChanged() {
            flags.set(TestFlag.ALPHA, TestFlag.BETA);

            assertTrue(flags.diff(flagsOf(TestFlag.BETA, TestFlag.GAMMA)));
            assertTrue(flags.isEqual(flagsOf(TestFlag.ALPHA)));

            assertFalse(flags.diff(flagsOf(TestFlag.GAMMA)));
        }

        @Test
        void maskKeepsOnlyTheListedFlags() {
            flags.set(TestFlag.ALPHA, TestFlag.BETA, TestFlag.GAMMA);

            assertTrue(flags.mask(TestFlag.BETA, TestFlag.DELTA));
            assertTrue(flags.isEqual(flagsOf(TestFlag.BETA)));
        }

        @Test
        void theListOverloadOfMaskBehavesLikeTheVarargsOne() {
            flags.set(TestFlag.ALPHA, TestFlag.BETA);

            assertTrue(flags.mask(List.of(TestFlag.ALPHA)));
            assertTrue(flags.isEqual(flagsOf(TestFlag.ALPHA)));
        }

        @Test
        void copyFromReplacesTheContentsWithTheOtherSets() {
            flags.set(TestFlag.ALPHA, TestFlag.GAMMA);

            flags.copyFrom(flagsOf(TestFlag.BETA));

            assertTrue(flags.isEqual(flagsOf(TestFlag.BETA)));
        }

        @Test
        void copyFromProducesAnIndependentCopy() {
            Flag<TestFlag> source = flagsOf(TestFlag.BETA);

            flags.copyFrom(source);
            flags.on(TestFlag.DELTA);

            assertFalse(source.has(TestFlag.DELTA));
        }
    }

    /**
     * The four mutators whose argument was widened from {@link Flag} to {@link FlagView} on
     * 260818, exercised through a {@code FlagView}-typed reference.
     *
     * <p>{@link SetOperations} already covers what these methods do; this covers what they will
     * now <em>accept</em>. The distinction matters because every test above passes a {@link Flag},
     * and a {@code Flag} satisfies both signatures — so those tests would pass whether or not the
     * widening had happened. Only a reference declared as the interface pins it.
     *
     * <p>{@link Flag#inter} additionally gets its own rewrite covered here: it was the one method
     * that could not simply be re-typed, because its {@code retainAll} needed a
     * {@link java.util.Collection} and a view is only an {@link Iterable}. Its replacement must
     * still answer the narrow "was a flag actually cleared" question that its Javadoc promises and
     * that C's {@code flag_inter} does not.
     *
     * <p>Class ReadOnlyArguments coded on 260818, commented in full on 260818.
     */
    @Nested
    class ReadOnlyArguments {

        @Test
        void unionAcceptsAViewTypedArgument() {
            FlagView<TestFlag> source = flagsOf(TestFlag.BETA);
            flags.set(TestFlag.ALPHA);

            assertTrue(flags.union(source));
            assertTrue(flags.isEqual(flagsOf(TestFlag.ALPHA, TestFlag.BETA)));
        }

        @Test
        void interAcceptsAViewTypedArgument() {
            FlagView<TestFlag> source = flagsOf(TestFlag.BETA, TestFlag.GAMMA);
            flags.set(TestFlag.ALPHA, TestFlag.BETA);

            assertTrue(flags.inter(source));
            assertTrue(flags.isEqual(flagsOf(TestFlag.BETA)));
        }

        @Test
        void diffAcceptsAViewTypedArgument() {
            FlagView<TestFlag> source = flagsOf(TestFlag.BETA);
            flags.set(TestFlag.ALPHA, TestFlag.BETA);

            assertTrue(flags.diff(source));
            assertTrue(flags.isEqual(flagsOf(TestFlag.ALPHA)));
        }

        @Test
        void copyFromAcceptsAViewTypedArgument() {
            FlagView<TestFlag> source = flagsOf(TestFlag.BETA);
            flags.set(TestFlag.ALPHA, TestFlag.GAMMA);

            flags.copyFrom(source);

            assertTrue(flags.isEqual(flagsOf(TestFlag.BETA)));
        }

        @Test
        void noneOfThemWritesToTheArgument() {
            Flag<TestFlag> source = flagsOf(TestFlag.BETA);
            FlagView<TestFlag> view = source;

            flags.set(TestFlag.ALPHA, TestFlag.BETA);
            flags.union(view);
            flags.inter(view);
            flags.diff(view);
            flags.copyFrom(view);

            assertTrue(source.isEqual(flagsOf(TestFlag.BETA)));
        }

        @Test
        void interReportsFalseWhenTheIntersectionClearsNothing() {
            flags.set(TestFlag.ALPHA);

            assertFalse(flags.inter(flagsOf(TestFlag.ALPHA, TestFlag.BETA)));
            assertTrue(flags.isEqual(flagsOf(TestFlag.ALPHA)));
        }

        @Test
        void interWithItselfIsASafeNoOp() {
            flags.set(TestFlag.ALPHA, TestFlag.GAMMA);

            assertFalse(flags.inter(flags));
            assertTrue(flags.isEqual(flagsOf(TestFlag.ALPHA, TestFlag.GAMMA)));
        }

        @Test
        void interWithAnEmptyViewEmptiesTheReceiverAndSaysSo() {
            flags.set(TestFlag.ALPHA);

            assertTrue(flags.inter(new Flag<>(TestFlag.class)));
            assertTrue(flags.isEmpty());
        }

        @Test
        void copyFromReplacesRatherThanMerging() {
            FlagView<TestFlag> source = flagsOf(TestFlag.BETA);
            flags.set(TestFlag.ALPHA);

            flags.copyFrom(source);

            assertFalse(flags.has(TestFlag.ALPHA));
            assertTrue(flags.has(TestFlag.BETA));
        }
    }

    /**
     * The comparison predicates.
     */
    @Nested
    class Predicates {

        @Test
        void isInterIsTrueWhenTheSetsShareAtLeastOneFlag() {
            flags.set(TestFlag.ALPHA, TestFlag.BETA);

            assertTrue(flags.isInter(flagsOf(TestFlag.BETA, TestFlag.GAMMA)));
            assertFalse(flags.isInter(flagsOf(TestFlag.GAMMA, TestFlag.DELTA)));
        }

        @Test
        void isInterIsFalseAgainstAnEmptySet() {
            flags.setAll();

            assertFalse(flags.isInter(new Flag<>(TestFlag.class)));
        }

        @Test
        void isSubsetAsksWhetherTheArgumentIsContainedInTheReceiver() {
            // Note the direction: this reads as "other is a subset of me", not the other
            // way round, which is the opposite of what the method name suggests.
            flags.set(TestFlag.ALPHA, TestFlag.BETA);

            assertTrue(flags.isSubset(flagsOf(TestFlag.ALPHA)));
            assertFalse(flagsOf(TestFlag.ALPHA).isSubset(flags));
        }

        @Test
        void everySetContainsTheEmptySet() {
            assertTrue(flags.isSubset(new Flag<>(TestFlag.class)));
        }

        @Test
        void isEqualIsContainmentInBothDirections() {
            flags.set(TestFlag.ALPHA, TestFlag.BETA);

            assertTrue(flags.isEqual(flagsOf(TestFlag.BETA, TestFlag.ALPHA)));
            assertFalse(flags.isEqual(flagsOf(TestFlag.ALPHA)));
            assertFalse(flags.isEqual(flagsOf(TestFlag.ALPHA, TestFlag.BETA, TestFlag.GAMMA)));
        }

        @Test
        void twoEmptySetsAreEqual() {
            assertTrue(flags.isEqual(new Flag<>(TestFlag.class)));
        }

        @Test
        void testIsAnAnyOfCheck() {
            flags.set(TestFlag.ALPHA);

            assertTrue(flags.test(TestFlag.ALPHA, TestFlag.BETA));
            assertFalse(flags.test(TestFlag.BETA, TestFlag.GAMMA));
        }

        @Test
        void testOfNothingIsFalse() {
            flags.setAll();

            assertFalse(flags.test(List.of()));
        }

        @Test
        void testAllIsAnAllOfCheck() {
            flags.set(TestFlag.ALPHA, TestFlag.BETA);

            assertTrue(flags.testAll(List.of(TestFlag.ALPHA, TestFlag.BETA)));
            assertFalse(flags.testAll(List.of(TestFlag.ALPHA, TestFlag.GAMMA)));
        }

        @Test
        void testAllOfNothingIsVacuouslyTrue() {
            assertTrue(flags.testAll(List.of()));
        }

        @Test
        void theListOverloadOfTestBehavesLikeTheVarargsOne() {
            flags.set(TestFlag.ALPHA);

            assertTrue(flags.test(List.of(TestFlag.ALPHA, TestFlag.BETA)));
            assertFalse(flags.test(List.of(TestFlag.BETA)));
        }
    }

    /**
     * Iteration, which is how ported code walks a flag set.
     */
    @Nested
    class Iteration {

        @Test
        void iterationYieldsTheSetFlagsInEnumDeclarationOrder() {
            flags.set(TestFlag.DELTA, TestFlag.ALPHA);

            assertEquals(List.of(TestFlag.ALPHA, TestFlag.DELTA), contentsOf(flags));
        }

        @Test
        void anEmptySetYieldsNothing() {
            assertEquals(List.of(), contentsOf(flags));
        }

        @Test
        void theIteratorDoesNotExposeTheUnderlyingSetForModification() {
            flags.set(TestFlag.ALPHA);
            Iterator<TestFlag> iterator = flags.iterator();
            iterator.next();

            assertThrows(UnsupportedOperationException.class, iterator::remove);
            assertTrue(flags.has(TestFlag.ALPHA));
        }
    }
}
