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

package uk.co.jackoftrades.middle.cave;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import uk.co.jackoftrades.middle.numerics.RandomValueUtils;
import uk.co.jackoftrades.middle.cave.enums.DirectionEnum;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link Loc}, the port of the C source's grid coordinate type
 * ({@code src/z-type.c}).
 *
 * <p>The C original is a bare two-field struct with free functions over it, and its
 * standing hazard is argument order: {@code loc(x, y)} takes the column first while every
 * grid array it indexes is row-major, so a transposed call compiles and then quietly
 * addresses the wrong square. The Java port closes that off by making the constructor
 * private behind the fluent {@code Loc.row(y).col(x)} idiom, which is why the tests below
 * are careful to assert x and y separately rather than comparing whole locations - a
 * transposition is exactly the bug a {@code Loc}-to-{@code Loc} assertion would hide when
 * both coordinates happen to be equal.
 *
 * <p>{@link Loc#equals} and {@link Loc#hashCode} carry more weight than usual: locations are
 * used as {@link java.util.HashMap} keys and {@link java.util.HashSet} members throughout
 * level generation and pathfinding, and the natural key set there is a dense rectangular
 * block of small coordinates - the worst case for a naive hash. Both are tested against
 * that shape rather than against a handful of scattered pairs.
 *
 * @author Rowan Crowther
 */
class LocTest {

    /**
     * Asserts a location's coordinates individually, so a transposition cannot pass.
     *
     * @param expectedX the expected column
     * @param expectedY the expected row
     * @param actual    the location under test
     */
    private static void assertAt(int expectedX, int expectedY, Loc actual) {
        assertEquals(expectedX, actual.getX(), "x (column)");
        assertEquals(expectedY, actual.getY(), "y (row)");
    }

    /**
     * The fluent construction idiom.
     */
    @Nested
    class Construction {

        @Test
        void rowThenColumnFixesTheCoordinatesInThatOrder() {
            assertAt(5, 3, Loc.row(3).col(5));
        }

        @Test
        void negativeCoordinatesAreAllowedBecauseOffsetsUseTheSameType() {
            assertAt(-2, -7, Loc.row(-7).col(-2));
        }

        @Test
        void theSharedOriginIsAtZeroZero() {
            assertAt(0, 0, Loc.zero);
        }

        @Test
        void aRowBuilderCanBeCompletedIndependentlyOfOtherBuilders() {
            Loc.RowBuilder atRowThree = Loc.row(3);

            assertAt(1, 3, atRowThree.col(1));
            assertAt(9, 3, atRowThree.col(9));
        }
    }

    /**
     * Arithmetic over locations - sums, differences and offsets.
     */
    @Nested
    class Arithmetic {

        @Test
        void summingAddsBothCoordinates() {
            Loc result = Loc.row(3).col(5).sum(Loc.row(10).col(20));

            assertAt(25, 13, result);
        }

        @Test
        void summingLeavesBothOperandsUntouched() {
            Loc left = Loc.row(3).col(5);
            Loc right = Loc.row(10).col(20);

            left.sum(right);

            assertAt(5, 3, left);
            assertAt(20, 10, right);
        }

        @Test
        void differenceSubtractsTheArgumentFromTheReceiver() {
            Loc result = Loc.row(10).col(20).diff(Loc.row(3).col(5));

            assertAt(15, 7, result);
        }

        @Test
        void aDifferenceIsADisplacementAndMayBeNegative() {
            // The result is an offset, not a position, so it is allowed outside the dungeon.
            assertAt(-15, -7, Loc.row(3).col(5).diff(Loc.row(10).col(20)));
        }

        @Test
        void offsetIsSumWithLooseCoordinates() {
            Loc base = Loc.row(3).col(5);

            assertEquals(base.sum(Loc.row(2).col(1)), base.offset(1, 2));
            assertAt(6, 5, base.offset(1, 2));
        }

        @Test
        void offsettingByNothingGivesAnEqualLocation() {
            Loc base = Loc.row(3).col(5);

            assertEquals(base, base.offset(0, 0));
        }

        @Test
        void summingWithTheOriginIsTheIdentity() {
            Loc base = Loc.row(3).col(5);

            assertEquals(base, base.sum(Loc.zero));
        }

        @Test
        void aLocationDifferencedWithItselfIsTheOrigin() {
            Loc base = Loc.row(3).col(5);

            assertTrue(base.diff(base).isZero());
        }
    }

    /**
     * Stepping to a neighbouring grid.
     */
    @Nested
    class Stepping {

        @Test
        void steppingAppliesTheDirectionsDeltas() {
            // Rows are numbered from the top of the map downwards, as in C, so stepping
            // north *decreases* y and stepping south increases it.
            Loc start = Loc.row(10).col(10);

            assertAt(10, 9, start.nextGrid(DirectionEnum.DIR_N));
            assertAt(10, 11, start.nextGrid(DirectionEnum.DIR_S));
            assertAt(11, 10, start.nextGrid(DirectionEnum.DIR_E));
            assertAt(9, 10, start.nextGrid(DirectionEnum.DIR_W));
        }

        @Test
        void diagonalStepsMoveOnBothAxes() {
            Loc start = Loc.row(10).col(10);

            assertAt(11, 9, start.nextGrid(DirectionEnum.DIR_NE));
            assertAt(9, 9, start.nextGrid(DirectionEnum.DIR_NW));
            assertAt(11, 11, start.nextGrid(DirectionEnum.DIR_SE));
            assertAt(9, 11, start.nextGrid(DirectionEnum.DIR_SW));
        }

        @Test
        void theNonDirectionsDoNotMove() {
            Loc start = Loc.row(10).col(10);

            assertEquals(start, start.nextGrid(DirectionEnum.DIR_UNKNOWN));
            assertEquals(start, start.nextGrid(DirectionEnum.DIR_TARGET));
            assertEquals(start, start.nextGrid(DirectionEnum.DIR_NONE));
        }

        @ParameterizedTest
        @EnumSource(DirectionEnum.class)
        void steppingIsTheSameAsSummingTheDirectionsGrid(DirectionEnum direction) {
            // nextGrid and ddgrid are two spellings of the same delta; ported code uses
            // both, so they must not drift apart.
            Loc start = Loc.row(4).col(7);

            assertEquals(start.sum(direction.ddgrid()), start.nextGrid(direction));
        }

        @Test
        void steppingPerformsNoBoundsChecking() {
            // Documented behaviour: the caller is responsible for the dungeon edge, so a
            // step off it produces a negative coordinate rather than throwing or clamping.
            assertAt(-1, 0, Loc.zero.nextGrid(DirectionEnum.DIR_W));
        }

        @Test
        void eightStandardStepsFromOneGridReachEightDistinctNeighbours() {
            Loc centre = Loc.row(5).col(5);
            Set<Loc> neighbours = new HashSet<>();

            for (DirectionEnum direction : DirectionEnum.values()) {
                if (direction.isStandard()) {
                    neighbours.add(centre.nextGrid(direction));
                }
            }

            assertEquals(8, neighbours.size());
            assertFalse(neighbours.contains(centre));
        }
    }

    /**
     * Value equality and hashing, which is what makes locations usable as collection keys.
     */
    @Nested
    class EqualityAndHashing {

        @Test
        void locationsWithTheSameCoordinatesAreEqual() {
            assertEquals(Loc.row(3).col(5), Loc.row(3).col(5));
        }

        @Test
        void transposedCoordinatesAreNotEqual() {
            assertNotEquals(Loc.row(3).col(5), Loc.row(5).col(3));
        }

        @Test
        void differingOnEitherAxisIsEnoughToBeUnequal() {
            assertNotEquals(Loc.row(3).col(5), Loc.row(3).col(6));
            assertNotEquals(Loc.row(3).col(5), Loc.row(4).col(5));
        }

        @Test
        void nullAndForeignTypesAreNotEqual() {
            Loc location = Loc.row(3).col(5);

            assertNotEquals(null, location);
            assertFalse(location.equals("3,5"));
        }

        @Test
        void equalLocationsHashAlike() {
            assertEquals(Loc.row(3).col(5).hashCode(), Loc.row(3).col(5).hashCode());
        }

        @Test
        void aDenseBlockOfGridsHashesWithoutCollision() {
            // The natural key set in level generation is a rectangular block of small
            // coordinates - the case the golden-ratio multiplier exists to handle, and the
            // one where the conventional 31 * x + y collides badly.
            Map<Integer, Loc> byHash = new HashMap<>();
            for (int y = 0; y < 66; y++) {
                for (int x = 0; x < 198; x++) {
                    Loc grid = Loc.row(y).col(x);
                    Loc clash = byHash.put(grid.hashCode(), grid);
                    assertNull(clash, () -> "hash collision at ("
                            + grid.getX() + ", " + grid.getY() + ")");
                }
            }
        }

        @Test
        void locationsWorkAsHashSetMembers() {
            Set<Loc> visited = new HashSet<>();
            visited.add(Loc.row(3).col(5));

            assertTrue(visited.contains(Loc.row(3).col(5)));
            assertFalse(visited.contains(Loc.row(5).col(3)));
        }

        @Test
        void locationsWorkAsHashMapKeys() {
            Map<Loc, String> squares = new HashMap<>();
            squares.put(Loc.row(3).col(5), "granite");

            assertEquals("granite", squares.get(Loc.row(3).col(5)));
        }
    }

    /**
     * The origin sentinel.
     */
    @Nested
    class Origin {

        @Test
        void onlyTheOriginIsZero() {
            assertTrue(Loc.zero.isZero());
            assertTrue(Loc.row(0).col(0).isZero());
            assertFalse(Loc.row(0).col(1).isZero());
            assertFalse(Loc.row(1).col(0).isZero());
        }

        @Test
        void arithmeticOnTheSharedOriginReturnsAFreshLocationRatherThanAliasingIt() {
            // Loc.zero is shared across the whole game, so any operation that handed the
            // same instance back would let one caller's result be seen by every other.
            Loc sum = Loc.zero.sum(Loc.zero);

            assertEquals(Loc.zero, sum);
            assertNotSame(Loc.zero, sum);
            assertNotSame(Loc.zero, Loc.row(0).col(0));
        }
    }

    /**
     * Random displacement, which draws each axis independently.
     */
    @Nested
    class RandomDisplacement {

        @Test
        void aRandomLocationStaysWithinTheGivenSpreadOnBothAxes() {
            RandomValueUtils.stateInit(20260810L);
            Loc centre = Loc.row(50).col(100);

            for (int attempt = 0; attempt < 500; attempt++) {
                Loc drawn = centre.rand(3, 7);

                assertTrue(drawn.getX() >= 97 && drawn.getX() <= 103,
                        () -> "x out of spread: " + drawn.getX());
                assertTrue(drawn.getY() >= 43 && drawn.getY() <= 57,
                        () -> "y out of spread: " + drawn.getY());
            }
        }

        @Test
        void aZeroSpreadGivesTheCentreBack() {
            RandomValueUtils.stateInit(20260810L);
            Loc centre = Loc.row(50).col(100);

            assertEquals(centre, centre.rand(0, 0));
        }

        @Test
        void theDrawIsRectangularNotCircular() {
            // Each axis is drawn independently, so the corners of the enclosing rectangle
            // are reachable - a fact level generation relies on for room placement.
            RandomValueUtils.stateInit(20260810L);
            Loc centre = Loc.row(50).col(100);
            boolean reachedACorner = false;

            for (int attempt = 0; attempt < 2000 && !reachedACorner; attempt++) {
                Loc drawn = centre.rand(2, 2);
                reachedACorner = Math.abs(drawn.getX() - 100) == 2
                        && Math.abs(drawn.getY() - 50) == 2;
            }

            assertTrue(reachedACorner, "no corner of the spread rectangle was ever drawn");
        }
    }
}
