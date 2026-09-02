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

package uk.co.jackoftradesltd.middle.cave.enums;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import uk.co.jackoftradesltd.middle.cave.Loc;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link DirectionEnum}, the port of the C source's direction tables
 * ({@code ddx}/{@code ddy} in {@code src/cave.c}).
 *
 * <p>The C original is a pair of parallel arrays indexed by the numeric-keypad digit the
 * player pressed, which is why the keys here are 1-9 laid out as a keypad rather than 0-7
 * round the compass. Three of the entries are not movements at all: key 5 is the middle of
 * the keypad and means "here"/"at the target", and key 0 is the absence of a direction. The
 * {@code isStandard} flag is what separates the eight real steps from those three, and
 * ported loops over the neighbours of a grid depend on it.
 *
 * <p>Note that {@code DIR_TARGET} and {@code DIR_NONE} deliberately share key 5, so
 * {@link DirectionEnum#fromKey} cannot be a bijection - it resolves 5 to whichever is
 * declared first. That is pinned below, because it is the kind of detail a later
 * reordering of the constants would silently change.
 *
 * @author Rowan Crowther
 */
class DirectionEnumTest {

    /**
     * The keypad-digit lookup.
     */
    @Nested
    class KeyLookup {

        @Test
        void eachCompassDirectionIsFoundByItsKeypadDigit() {
            assertSame(DirectionEnum.DIR_SW, DirectionEnum.fromKey(1));
            assertSame(DirectionEnum.DIR_S, DirectionEnum.fromKey(2));
            assertSame(DirectionEnum.DIR_SE, DirectionEnum.fromKey(3));
            assertSame(DirectionEnum.DIR_W, DirectionEnum.fromKey(4));
            assertSame(DirectionEnum.DIR_E, DirectionEnum.fromKey(6));
            assertSame(DirectionEnum.DIR_NW, DirectionEnum.fromKey(7));
            assertSame(DirectionEnum.DIR_N, DirectionEnum.fromKey(8));
            assertSame(DirectionEnum.DIR_NE, DirectionEnum.fromKey(9));
        }

        /**
         * The digits sit on the keypad as
         *
         * <pre>
         *   7 8 9
         *   4 5 6
         *   1 2 3
         * </pre>
         * <p>
         * and the whole point of the C tables is that a direction's offsets are just that
         * digit's position on the pad, read as (column, row) about the centre 5. So the
         * expected offsets can be computed from the digit rather than listed: the column is
         * {@code ((key - 1) % 3) - 1}, giving -1/0/+1 left to right, and the row is
         * {@code 1 - ((key - 1) / 3)}, giving +1 on the bottom row down to -1 on the top -
         * rows count downwards because {@code ddy} grows southward.
         *
         * <p>Derived that way the nine expected pairs reproduce C's {@code ddx}/{@code ddy}
         * (src/cave.c) entry for entry, including the (0, 0) at key 5, so any single
         * direction whose offsets have drifted - a flipped sign on one diagonal, say - shows
         * up here rather than slipping past the row/column comparisons this test used to do.
         */
        @ParameterizedTest
        @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7, 8, 9})
        void theKeypadLayoutMatchesTheDigitsPositions(int key) {
            int expectedX = ((key - 1) % 3) - 1;
            int expectedY = 1 - ((key - 1) / 3);

            DirectionEnum direction = DirectionEnum.fromKey(key);

            assertEquals(expectedX, direction.ddx(),
                    "key " + key + " (" + direction.name() + ") is in the wrong keypad column");
            assertEquals(expectedY, direction.ddy(),
                    "key " + key + " (" + direction.name() + ") is in the wrong keypad row");
        }

        @Test
        void eachKeypadRowSharesARowOffsetAndEachColumnAColumnOffset() {
            // The same layout seen the other way round: whatever the offsets are, the three
            // digits in a row must agree on ddy and the three in a column on ddx.
            for (int row = 0; row < 3; row++) {
                int first = 1 + row * 3;

                assertEquals(DirectionEnum.fromKey(first).ddy(),
                        DirectionEnum.fromKey(first + 1).ddy(), "keypad row " + first);
                assertEquals(DirectionEnum.fromKey(first + 1).ddy(),
                        DirectionEnum.fromKey(first + 2).ddy(), "keypad row " + first);
            }
            for (int column = 1; column <= 3; column++) {
                assertEquals(DirectionEnum.fromKey(column).ddx(),
                        DirectionEnum.fromKey(column + 3).ddx(), "keypad column " + column);
                assertEquals(DirectionEnum.fromKey(column + 3).ddx(),
                        DirectionEnum.fromKey(column + 6).ddx(), "keypad column " + column);
            }
        }

        @Test
        void keyZeroIsTheUnknownDirection() {
            assertSame(DirectionEnum.DIR_UNKNOWN, DirectionEnum.fromKey(0));
        }

        @Test
        void theSharedMiddleKeyResolvesToWhicheverConstantIsDeclaredFirst() {
            // DIR_TARGET and DIR_NONE both carry key 5; the linear scan finds DIR_TARGET.
            assertSame(DirectionEnum.DIR_TARGET, DirectionEnum.fromKey(5));
        }

        @Test
        void anUnusedKeyFallsBackToUnknownRatherThanNull() {
            assertSame(DirectionEnum.DIR_UNKNOWN, DirectionEnum.fromKey(10));
            assertSame(DirectionEnum.DIR_UNKNOWN, DirectionEnum.fromKey(-1));
            assertSame(DirectionEnum.DIR_UNKNOWN, DirectionEnum.fromKey(99));
        }

        @ParameterizedTest
        @EnumSource(DirectionEnum.class)
        void everyDirectionIsReachableFromItsOwnKeyOrSharesThatKey(DirectionEnum direction) {
            DirectionEnum found = DirectionEnum.fromKey(direction.getKey());

            assertEquals(direction.getKey(), found.getKey());
        }
    }

    /**
     * The movement deltas.
     */
    @Nested
    class Deltas {

        @Test
        void theEightStandardDirectionsHaveDistinctNonZeroDeltas() {
            Set<Loc> deltas = new HashSet<>();

            for (DirectionEnum direction : DirectionEnum.values()) {
                if (direction.isStandard()) {
                    deltas.add(direction.ddgrid());
                }
            }

            assertEquals(8, deltas.size());
            assertFalse(deltas.contains(Loc.zero));
        }

        @Test
        void theNonDirectionsHaveNoDelta() {
            assertEquals(0, DirectionEnum.DIR_UNKNOWN.ddx());
            assertEquals(0, DirectionEnum.DIR_UNKNOWN.ddy());
            assertEquals(0, DirectionEnum.DIR_TARGET.ddx());
            assertEquals(0, DirectionEnum.DIR_TARGET.ddy());
            assertEquals(0, DirectionEnum.DIR_NONE.ddx());
            assertEquals(0, DirectionEnum.DIR_NONE.ddy());
        }

        @ParameterizedTest
        @EnumSource(DirectionEnum.class)
        void everyDeltaIsASingleStep(DirectionEnum direction) {
            // No direction may move more than one grid on either axis, or the neighbour
            // walks that use these would skip squares.
            assertTrue(Math.abs(direction.ddx()) <= 1, direction.name());
            assertTrue(Math.abs(direction.ddy()) <= 1, direction.name());
        }

        @ParameterizedTest
        @EnumSource(DirectionEnum.class)
        void ddgridPackagesTheSameDeltasAsDdxAndDdy(DirectionEnum direction) {
            Loc grid = direction.ddgrid();

            assertEquals(direction.ddx(), grid.getX(), direction.name());
            assertEquals(direction.ddy(), grid.getY(), direction.name());
        }

        @Test
        void oppositeDirectionsCancel() {
            assertEquals(Loc.zero,
                    DirectionEnum.DIR_N.ddgrid().sum(DirectionEnum.DIR_S.ddgrid()));
            assertEquals(Loc.zero,
                    DirectionEnum.DIR_E.ddgrid().sum(DirectionEnum.DIR_W.ddgrid()));
            assertEquals(Loc.zero,
                    DirectionEnum.DIR_NE.ddgrid().sum(DirectionEnum.DIR_SW.ddgrid()));
            assertEquals(Loc.zero,
                    DirectionEnum.DIR_NW.ddgrid().sum(DirectionEnum.DIR_SE.ddgrid()));
        }

        @Test
        void theFourDiagonalsMoveOnBothAxesAndTheFourOrthogonalsOnOne() {
            for (DirectionEnum direction : new DirectionEnum[]{DirectionEnum.DIR_NE,
                    DirectionEnum.DIR_NW, DirectionEnum.DIR_SE, DirectionEnum.DIR_SW}) {
                assertTrue(direction.ddx() != 0 && direction.ddy() != 0, direction.name());
            }
            for (DirectionEnum direction : new DirectionEnum[]{DirectionEnum.DIR_N,
                    DirectionEnum.DIR_S, DirectionEnum.DIR_E, DirectionEnum.DIR_W}) {
                assertTrue((direction.ddx() == 0) != (direction.ddy() == 0), direction.name());
            }
        }
    }

    /**
     * The standard/non-standard split.
     */
    @Nested
    class StandardDirections {

        @Test
        void exactlyEightDirectionsAreStandard() {
            long standard = 0;
            for (DirectionEnum direction : DirectionEnum.values()) {
                if (direction.isStandard()) {
                    standard++;
                }
            }

            assertEquals(8, standard);
        }

        @Test
        void theNonDirectionsAreNotStandard() {
            assertFalse(DirectionEnum.DIR_UNKNOWN.isStandard());
            assertFalse(DirectionEnum.DIR_TARGET.isStandard());
            assertFalse(DirectionEnum.DIR_NONE.isStandard());
        }

        @ParameterizedTest
        @EnumSource(DirectionEnum.class)
        void aDirectionIsStandardExactlyWhenItActuallyMoves(DirectionEnum direction) {
            boolean moves = direction.ddx() != 0 || direction.ddy() != 0;

            assertEquals(moves, direction.isStandard(), direction.name());
        }
    }
}
