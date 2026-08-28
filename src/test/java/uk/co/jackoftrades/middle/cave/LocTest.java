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

    /**
     * The bearing of a single step from one grid towards another, as returned by
     * {@link Loc#motionDir}.
     *
     * <p>Class Bearing coded and commented in full on 260828.
     */
    @Nested
    class Bearing {

        @Test
        void aGridHasNoBearingTowardsItself() {
            Loc here = Loc.row(7).col(3);

            assertEquals(DirectionEnum.DIR_NONE, here.motionDir(here));
            assertEquals(DirectionEnum.DIR_NONE, here.motionDir(Loc.row(7).col(3)));
        }

        @Test
        void aSharedColumnGivesNorthOrSouth() {
            // Rows count downwards, so the larger y is to the south.
            Loc here = Loc.row(10).col(10);

            assertEquals(DirectionEnum.DIR_S, here.motionDir(Loc.row(11).col(10)));
            assertEquals(DirectionEnum.DIR_N, here.motionDir(Loc.row(9).col(10)));
        }

        @Test
        void aSharedRowGivesEastOrWest() {
            Loc here = Loc.row(10).col(10);

            assertEquals(DirectionEnum.DIR_E, here.motionDir(Loc.row(10).col(11)));
            assertEquals(DirectionEnum.DIR_W, here.motionDir(Loc.row(10).col(9)));
        }

        @Test
        void differingOnBothAxesGivesADiagonal() {
            Loc here = Loc.row(10).col(10);

            assertEquals(DirectionEnum.DIR_SE, here.motionDir(Loc.row(11).col(11)));
            assertEquals(DirectionEnum.DIR_SW, here.motionDir(Loc.row(11).col(9)));
            assertEquals(DirectionEnum.DIR_NE, here.motionDir(Loc.row(9).col(11)));
            assertEquals(DirectionEnum.DIR_NW, here.motionDir(Loc.row(9).col(9)));
        }

        @Test
        void onlyTheSignOfTheDifferenceMattersNotItsSize() {
            // A bearing, not a route: a target far away on one axis and one grid away on
            // the other is still a plain diagonal.
            Loc here = Loc.row(10).col(10);

            assertEquals(DirectionEnum.DIR_SE, here.motionDir(Loc.row(11).col(90)));
            assertEquals(DirectionEnum.DIR_SE, here.motionDir(Loc.row(90).col(11)));
            assertEquals(DirectionEnum.DIR_NW, here.motionDir(Loc.row(-40).col(-3)));
        }

        @Test
        void theBearingIsTakenFromTheCallerNotTheArgument() {
            // motionDir is not symmetric: reversing the two grids reverses the direction.
            Loc here = Loc.row(4).col(4);
            Loc there = Loc.row(6).col(7);

            assertEquals(DirectionEnum.DIR_SE, here.motionDir(there));
            assertEquals(DirectionEnum.DIR_NW, there.motionDir(here));
        }

        @Test
        void everyNeighbourOfAGridIsTheDirectionThatStepsToIt() {
            // motionDir and nextGrid are inverses over the eight adjacent grids, which is
            // the round trip the ported callers make: motionDir feeds straight into
            // nextGrid.
            Loc centre = Loc.row(5).col(5);

            for (DirectionEnum direction : DirectionEnum.values()) {
                if (direction.isStandard()) {
                    Loc neighbour = centre.nextGrid(direction);

                    assertEquals(direction, centre.motionDir(neighbour),
                            () -> "bearing towards the " + direction + " neighbour");
                }
            }
        }

        @Test
        void steppingOnTheBearingAlwaysClosesTheDistance() {
            // Repeated single steps must terminate, so each one has to reduce the
            // Chebyshev distance to the target rather than circle it.
            Loc start = Loc.row(3).col(20);

            for (int y = -5; y <= 12; y++) {
                for (int x = 0; x <= 40; x++) {
                    Loc target = Loc.row(y).col(x);
                    if (start.equals(target)) continue;

                    Loc stepped = start.nextGrid(start.motionDir(target));

                    assertTrue(chebyshev(stepped, target) < chebyshev(start, target),
                            () -> "step towards " + target.getX() + "," + target.getY()
                                    + " did not close the gap");
                }
            }
        }

        @Test
        void repeatedStepsArriveAtTheTarget() {
            Loc at = Loc.row(2).col(2);
            Loc target = Loc.row(9).col(30);
            int steps = 0;

            while (!at.equals(target) && steps < 100) {
                at = at.nextGrid(at.motionDir(target));
                steps++;
            }

            assertEquals(target, at);
            // Diagonal motion is preferred, so the walk costs the Chebyshev distance, not
            // the sum of the two axes.
            assertEquals(28, steps);
        }

        @Test
        void theBearingIsNeverANonDirection() {
            Loc centre = Loc.row(0).col(0);

            for (int y = -3; y <= 3; y++) {
                for (int x = -3; x <= 3; x++) {
                    Loc target = Loc.row(y).col(x);
                    DirectionEnum bearing = centre.motionDir(target);

                    if (centre.equals(target)) {
                        assertEquals(DirectionEnum.DIR_NONE, bearing);
                    } else {
                        assertTrue(bearing.isStandard(),
                                () -> "non-standard bearing towards "
                                        + target.getX() + "," + target.getY());
                    }
                }
            }
        }

        /**
         * The number of single steps between two grids when diagonals are free, which is
         * what one step per call to {@link Loc#motionDir} costs.
         *
         * @param a one grid
         * @param b the other grid
         * @return the Chebyshev distance between them
         */
        private int chebyshev(Loc a, Loc b) {
            return Math.max(Math.abs(a.getX() - b.getX()), Math.abs(a.getY() - b.getY()));
        }
    }

    /**
     * The approximate distance between two grids, as returned by {@link Loc#distance}.
     *
     * <p>Every expected value here is worked out from the C original's formula in
     * {@code distance} ({@code src/cave-view.c}),
     * {@code ay > ax ? ay + (ax >> 1) : ax + (ay >> 1)}, rather than from the Java.
     *
     * <p>Class Distance coded and commented in full on 260828.
     */
    @Nested
    class Distance {

        @Test
        void aGridIsNoDistanceFromItself() {
            Loc here = Loc.row(7).col(3);

            assertEquals(0, here.distance(here));
            assertEquals(0, here.distance(Loc.row(7).col(3)));
        }

        @Test
        void alongOneAxisTheDistanceIsExact() {
            Loc origin = Loc.row(0).col(0);

            // ay = 5, ax = 0: 5 > 0, so 5 + (0 >> 1) = 5.
            assertEquals(5, origin.distance(Loc.row(5).col(0)));
            // ay = 0, ax = 5: 0 > 5 is false, so 5 + (0 >> 1) = 5.
            assertEquals(5, origin.distance(Loc.row(0).col(5)));
        }

        @Test
        void aDiagonalCostsOneAndAHalfPerStep() {
            Loc origin = Loc.row(0).col(0);

            // Equal components take the else branch: ax + (ay >> 1).
            assertEquals(4, origin.distance(Loc.row(3).col(3)));   // 3 + (3 >> 1) = 3 + 1
            assertEquals(6, origin.distance(Loc.row(4).col(4)));   // 4 + (4 >> 1) = 4 + 2
            assertEquals(1, origin.distance(Loc.row(1).col(1)));   // 1 + (1 >> 1) = 1 + 0
        }

        @Test
        void theSmallerComponentIsHalvedTowardsZero() {
            Loc origin = Loc.row(0).col(0);

            // ay = 8 > ax = 3: 8 + (3 >> 1) = 8 + 1 = 9, not 8 + 1.5.
            assertEquals(9, origin.distance(Loc.row(8).col(3)));
            // ax = 8 > ay = 3 gives the mirrored result through the other branch.
            assertEquals(9, origin.distance(Loc.row(3).col(8)));
        }

        @Test
        void theBranchTurnsOnAStrictGreaterThan() {
            Loc origin = Loc.row(0).col(0);

            // ay = 6, ax = 5: ay > ax, so 6 + (5 >> 1) = 6 + 2 = 8.
            assertEquals(8, origin.distance(Loc.row(6).col(5)));
            // ay = 5, ax = 6: the else branch, 6 + (5 >> 1) = 8. Both branches agree
            // whenever the components are merely swapped, which is why the tie at
            // ay == ax can safely fall to the else.
            assertEquals(8, origin.distance(Loc.row(5).col(6)));
        }

        @Test
        void theApproximationOverEstimatesTheTrueDistance() {
            Loc origin = Loc.row(0).col(0);

            // A 6-8-10 triangle: the C gives 8 + (6 >> 1) = 11 for a true distance of 10.
            assertEquals(11, origin.distance(Loc.row(8).col(6)));
        }

        @Test
        void negativeDifferencesAreTakenAbsolutely() {
            Loc centre = Loc.row(10).col(10);

            // Each neighbour is 3 rows and 4 columns away in some combination of signs;
            // ax = 4 > ay = 3 gives 4 + (3 >> 1) = 5 every time.
            assertEquals(5, centre.distance(Loc.row(13).col(14)));
            assertEquals(5, centre.distance(Loc.row(13).col(6)));
            assertEquals(5, centre.distance(Loc.row(7).col(14)));
            assertEquals(5, centre.distance(Loc.row(7).col(6)));
        }

        @Test
        void theDistanceIsSymmetric() {
            for (int y = -4; y <= 4; y++) {
                for (int x = -4; x <= 4; x++) {
                    Loc a = Loc.row(0).col(0);
                    Loc b = Loc.row(y).col(x);

                    assertEquals(a.distance(b), b.distance(a),
                            "asymmetric distance to " + x + "," + y);
                }
            }
        }

        @Test
        void everyNearbyGridMatchesTheCFormula() {
            Loc origin = Loc.row(0).col(0);

            for (int y = -12; y <= 12; y++) {
                for (int x = -12; x <= 12; x++) {
                    int ay = Math.abs(y);
                    int ax = Math.abs(x);
                    int expected = ay > ax ? ay + (ax >> 1) : ax + (ay >> 1);

                    assertEquals(expected, origin.distance(Loc.row(y).col(x)),
                            "distance to " + x + "," + y);
                }
            }
        }
    }
}
