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

package uk.co.jackoftradesltd.frontend.screen.enums;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import uk.co.jackoftradesltd.channel.utils.Combiner;
import uk.co.jackoftradesltd.channel.utils.combiners.AddCombiner;
import uk.co.jackoftradesltd.channel.utils.combiners.BitwiseOrCombiner;
import uk.co.jackoftradesltd.channel.utils.combiners.FirstCombiner;
import uk.co.jackoftradesltd.channel.utils.combiners.LargestCombiner;
import uk.co.jackoftradesltd.channel.utils.combiners.LastCombiner;
import uk.co.jackoftradesltd.channel.utils.combiners.LogicalOrCombiner;
import uk.co.jackoftradesltd.channel.utils.combiners.LogicalOrWithCancelCombiner;
import uk.co.jackoftradesltd.channel.utils.combiners.Resist0Combiner;
import uk.co.jackoftradesltd.channel.utils.combiners.SmallestCombiner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for {@link CombinerName}, the port of the C original's name-indexed
 * {@code combiners[]} table ({@code ui-entry-combiner.c}) and the
 * {@code ui_entry_combiner_lookup}/{@code ui_entry_combiner_get_funcs} pair that
 * indexes it ({@code ui-entry-combiner.h}).
 *
 * <p>The one behaviour that belongs to this enum rather than to the individual
 * {@link Combiner} strategies (already covered by {@code CombinerTest}) is the
 * prototype-and-clone wiring: {@link CombinerName#init(int, int)} must resolve
 * each constant to the right strategy, hand back an independent instance rather
 * than a shared one, and refuse to do either for {@link CombinerName#NONE} - the
 * Java stand-in for a C {@code combiner_index} of zero, which the C original
 * never passes to a combining function at all ({@code assert(0)} at
 * {@code ui-entry.c:697} if it tries). The expected seeded values below come
 * from the C {@code *_init}/{@code *_finish} pairs in {@code ui-entry-combiner.c}
 * ({@code simple_combine_init}, {@code logical_combine_init},
 * {@code logical_or_with_cancel_combine_init}/{@code _finish},
 * {@code resist_0_combine_init}/{@code _finish}), not from the Java
 * implementations.
 *
 * @author Rowan Crowther
 */
class CombinerNameTest {

    @Test
    void everyConstantInTheCsTableIsPresentPlusTheJavaOnlyNone() {
        // Pins the count so a strategy added to the C table (or to this enum)
        // without a matching entry on the other side fails loudly here, rather
        // than silently falling out of step.
        assertEquals(10, CombinerName.values().length);
    }

    @Nested
    class NoneHasNoPrototype {

        @Test
        void initThrowsRatherThanSilentlyFoldingNothing() {
            // Mirrors the C original's precondition: a combiner_index of zero is
            // never handed to a combining function (ui-entry.c:697 asserts on
            // it). The port enforces the same "must not happen" contract with a
            // NullPointerException instead of an assert.
            assertThrows(NullPointerException.class, () -> CombinerName.NONE.init(0, 0));
        }
    }

    /**
     * Each real constant resolves to the C table row of the same name and seeds
     * a fresh instance of the matching {@link Combiner} implementation with a
     * single ordinary (non-sentinel, non-negative) contribution.
     */
    @Nested
    class InitResolvesAndSeedsTheRightStrategy {

        @Test
        void add() {
            assertSeeds(CombinerName.ADD, AddCombiner.class, 5, 7, 5, 7);
        }

        @Test
        void bitwiseOr() {
            assertSeeds(CombinerName.BITWISE_OR, BitwiseOrCombiner.class, 5, 7, 5, 7);
        }

        @Test
        void first() {
            assertSeeds(CombinerName.FIRST, FirstCombiner.class, 5, 7, 5, 7);
        }

        @Test
        void largest() {
            assertSeeds(CombinerName.LARGEST, LargestCombiner.class, 5, 7, 5, 7);
        }

        @Test
        void last() {
            assertSeeds(CombinerName.LAST, LastCombiner.class, 5, 7, 5, 7);
        }

        @Test
        void smallest() {
            assertSeeds(CombinerName.SMALLEST, SmallestCombiner.class, 5, 7, 5, 7);
        }

        @Test
        void logicalOrNormalisesANonZeroFirstContributionToOne() {
            // logical_combine_init: accum = (v != 0), independently for the aux
            // channel - a positive value collapses to 1, not the value itself.
            assertSeeds(CombinerName.LOGICAL_OR, LogicalOrCombiner.class, 5, 7, 1, 1);
        }

        @Test
        void logicalOrWithCancelAlsoNormalisesAPositiveFirstContributionToOne() {
            assertSeeds(CombinerName.LOGICAL_OR_WITH_CANCEL,
                    LogicalOrWithCancelCombiner.class, 5, 7, 1, 1);
        }

        @Test
        void logicalOrWithCancelEncodesANegativeFirstContributionAsMinusOne() {
            // logical_or_with_cancel_combine_init/_finish: a negative v sets the
            // "negative seen" bit, which finish() maps straight to -1.
            assertSeeds(CombinerName.LOGICAL_OR_WITH_CANCEL,
                    LogicalOrWithCancelCombiner.class, -3, 0, -1, 0);
        }

        @Test
        void resist0PassesAPositiveFirstContributionThroughUnchanged() {
            // resist_0_combine_init/_finish: a v > 0 is recorded as the positive
            // accumulator with no negative to reconcile against, so finish()
            // leaves it exactly as init() set it.
            assertSeeds(CombinerName.RESIST_0, Resist0Combiner.class, 5, 7, 5, 7);
        }

        @Test
        void resist0EncodesANegativeFirstContributionAsMinusOne() {
            // A v < 0 with no offsetting positive resolves the same way plain
            // vulnerability does elsewhere in the family: -1.
            assertSeeds(CombinerName.RESIST_0, Resist0Combiner.class, -3, 0, -1, 0);
        }

        private void assertSeeds(CombinerName name, Class<? extends Combiner> expectedType,
                                 int v, int a, int expectedAccum, int expectedAccumAux) {
            Combiner combiner = name.init(v, a);

            assertInstanceOf(expectedType, combiner);
            var finished = combiner.finish();
            assertEquals(expectedAccum, finished.getAccum(), name + " accum");
            assertEquals(expectedAccumAux, finished.getAccumAux(), name + " accumAux");
        }
    }

    @Nested
    class EachCallIsIndependent {

        @ParameterizedTest
        @EnumSource(value = CombinerName.class, names = "NONE", mode = EnumSource.Mode.EXCLUDE)
        void initHandsBackADistinctInstanceEveryTime(CombinerName name) {
            // The whole point of cloning the prototype: two UI entries bound to
            // the same combiner name must not share fold state.
            Combiner first = name.init(1, 1);
            Combiner second = name.init(1, 1);

            assertNotSame(first, second);
        }

        @ParameterizedTest
        @EnumSource(value = CombinerName.class, names = "NONE", mode = EnumSource.Mode.EXCLUDE)
        void furtherAccumulationOnOneInstanceDoesNotLeakIntoAnother(CombinerName name) {
            Combiner first = name.init(1, 1);
            Combiner second = name.init(1, 1);

            second.accum(1, 1);

            // Whatever second's fold now reads, first must still report exactly
            // what a single contribution of (1, 1) gives every strategy in the
            // family (established as the identity case by CombinerTest).
            assertEquals(1, first.finish().getAccum(), name.toString());
        }
    }
}
