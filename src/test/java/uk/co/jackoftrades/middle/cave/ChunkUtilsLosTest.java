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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.co.jackoftrades.channel.utils.Flag;
import uk.co.jackoftrades.middle.cave.enums.TerrainFeatureFlags;
import uk.co.jackoftrades.middle.monsters.enums.MonsterRaceFlag;
import uk.co.jackoftrades.middle.player.Player;
import uk.co.jackoftrades.testsupport.SeededPlayerRegistry;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link ChunkUtils#los}, the port of C's {@code los} ({@code cave-view.c}).
 *
 * <p>The expected answers here were derived by hand-executing the C, not by reading the Java. That
 * matters more than usual for this method: the fixed-point walk visits a specific, slightly
 * counter-intuitive set of grids, and a port that traced a merely plausible line — a Bresenham
 * line, say, or a rounded one — would agree with C on most open levels and disagree exactly where
 * a wall sits on the boundary between two candidate rays. So most tests below name a single grid,
 * wall it, and assert what C would answer; and several pair a grid the C walk does consult with
 * the neighbouring grid it does not, asserting opposite answers for the two.
 *
 * <p>The three walks whose grid sequences the tests were built from, all traced through the C:
 * <ul>
 *   <li>Slope 1, {@code (2,2)} to {@code (6,6)}: the pre-step for {@code qy == f2} fires, and the
 *       walk visits {@code (3,3)}, {@code (4,4)}, {@code (5,5)} — the pure diagonal.</li>
 *   <li>Slope 1/3, {@code (0,0)} to {@code (6,2)}: visits {@code (1,0)}, {@code (2,1)},
 *       {@code (3,1)}, {@code (4,1)}, {@code (5,2)}. The fraction lands exactly on {@code f2}
 *       twice, so the shorter-axis step is taken with no second check — {@code (1,1)} is brushed
 *       but never entered.</li>
 *   <li>Slope 2/5, {@code (0,0)} to {@code (5,2)}: visits {@code (1,0)}, {@code (1,1)},
 *       {@code (2,1)}, {@code (3,1)}, {@code (4,1)}, {@code (4,2)}. Here the fraction overshoots
 *       {@code f2}, so the ray genuinely enters the grid above and the second check does run —
 *       {@code (1,1)} now blocks. The contrast between this and the case above is the whole point
 *       of the corner-brush branch.</li>
 * </ul>
 *
 * <p>One branch of the method is unreachable and is therefore not tested: the vertical walk's
 * {@code quotientX == factor2} pre-step needs {@code absX * absX == absX * absY}, that is
 * {@code absX == absY}, but the vertical walk is only entered when {@code absX < absY}. It is dead
 * in the C for the same reason, and is kept in the port because the port follows the C.
 *
 * <p>Every grid is given a terrain feature during setup. A freshly built {@link Chunk} fills itself
 * with squares carrying a null feature; {@link Square#featIsProjectable} reads that feature, and
 * an unfurnished level would answer "not projectable" everywhere rather than exercising anything.
 * No case places a grid outside the level, so C's assertion on bounds and the port's defensive
 * {@code inBounds} test never come apart here.
 *
 * <p>Class ChunkUtilsLosTest coded on 260828, commented in full on 260828.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class ChunkUtilsLosTest {

    /**
     * The level's width, in grids. Different from {@link #HEIGHT} so that a transposed index shows.
     */
    private static final int WIDTH = 13;

    /**
     * The level's height, in grids. Different from {@link #WIDTH} so that a transposed index shows.
     */
    private static final int HEIGHT = 11;

    /**
     * The level under test.
     */
    private Chunk level;

    /**
     * A terrain feature carrying the given flags and nothing else. Only {@code TF_PROJECT} is read
     * here, through {@link Square#featIsProjectable}, so the rest of the feature is left empty
     * rather than built from the registry.
     *
     * @param flags the terrain flags the feature carries
     * @return the constructed feature
     */
    private static Feature feature(TerrainFeatureFlags... flags) {
        Flag<TerrainFeatureFlags> set = new Flag<>(TerrainFeatureFlags.class);
        for (TerrainFeatureFlags flag : flags)
            set.on(flag);
        return new Feature(null, "test", "", null, 0, 0, set, null, "", "", "", "", "", "", "",
                new Flag<>(MonsterRaceFlag.class));
    }

    /**
     * Shorthand for a grid, in the column/row order the cases are written in.
     *
     * @param x the column
     * @param y the row
     * @return the grid
     */
    private static Loc at(int x, int y) {
        return Loc.row(y).col(x);
    }

    /**
     * A level whose every grid is projectable. Tests wall the individual grids they are about.
     */
    @BeforeEach
    void newLevel() {
        level = new Chunk("test level", 0, 0, 0, 0, 0, false,
                HEIGHT, WIDTH, 0, 4, 3, 0, 0, 0, new Player());

        for (int y = 0; y < HEIGHT; y++)
            for (int x = 0; x < WIDTH; x++)
                open(Loc.row(y).col(x));
    }

    /**
     * Makes a grid projectable, so that a line of sight through it is not blocked.
     *
     * @param grid the grid to open up
     */
    private void open(Loc grid) {
        level.getSquare(grid).setFeature(feature(TerrainFeatureFlags.TF_PROJECT));
    }

    /**
     * Makes a grid unprojectable, so that a line of sight through it is blocked.
     *
     * @param grid the grid to wall off
     */
    private void wall(Loc grid) {
        level.getSquare(grid).setFeature(feature());
    }

    /**
     * Asks the method under test whether sight runs between two grids.
     *
     * @param x1 the column sight is traced from
     * @param y1 the row sight is traced from
     * @param x2 the column sight is traced to
     * @param y2 the row sight is traced to
     * @return the method's answer
     */
    private boolean los(int x1, int y1, int x2, int y2) {
        return ChunkUtils.los(level, at(x1, y1), at(x2, y2));
    }

    /**
     * The cases C settles before it looks at any grid at all.
     */
    @Nested
    @DisplayName("degenerate cases")
    class Degenerate {

        /**
         * A grid always sees itself, and C reaches that answer from the absolute offsets alone,
         * without consulting the grid. Walling it must not change the answer.
         */
        @Test
        @DisplayName("a grid sees itself even when it is a wall")
        void identical() {
            wall(at(5, 5));
            assertTrue(los(5, 5, 5, 5));
        }

        /**
         * All eight neighbours are in sight unconditionally: the offsets are both under two, so C
         * returns before the wall checks begin. Every grid on the level is a wall here to make the
         * point that nothing is examined.
         */
        @Test
        @DisplayName("all eight neighbours are in sight through solid rock")
        void adjacent() {
            for (int y = 0; y < HEIGHT; y++)
                for (int x = 0; x < WIDTH; x++)
                    wall(at(x, y));

            for (int dy = -1; dy <= 1; dy++)
                for (int dx = -1; dx <= 1; dx++)
                    assertTrue(los(5, 5, 5 + dx, 5 + dy),
                            "offset " + dx + "," + dy + " should be in sight");
        }
    }

    /**
     * The two axis-aligned walks, which C handles with a plain exclusive loop rather than the
     * fixed-point trace.
     */
    @Nested
    @DisplayName("straight lines")
    class Straight {

        /**
         * Looking south down a clear column. The bounds are exclusive at both ends, so walling both
         * endpoints leaves sight intact — the endpoints are what is being looked at, not through.
         */
        @Test
        @DisplayName("south: clear column, walled endpoints, still in sight")
        void southClear() {
            wall(at(5, 2));
            wall(at(5, 8));
            assertTrue(los(5, 2, 5, 8));
        }

        /**
         * A wall anywhere strictly between the endpoints blocks the southward look.
         */
        @Test
        @DisplayName("south: a wall between the endpoints blocks")
        void southBlocked() {
            wall(at(5, 5));
            assertFalse(los(5, 2, 5, 8));
        }

        /**
         * The northward branch is a separate loop in C, counting down, and has to block on the same
         * wall. Reversing the endpoints of the case above is what catches a mixed-up sign.
         */
        @Test
        @DisplayName("north: the same wall blocks the reversed look")
        void northBlocked() {
            wall(at(5, 5));
            assertFalse(los(5, 8, 5, 2));
        }

        /**
         * The northward branch with nothing in the way.
         */
        @Test
        @DisplayName("north: clear column is in sight")
        void northClear() {
            assertTrue(los(5, 8, 5, 2));
        }

        /**
         * Looking east along a clear row, endpoints walled.
         */
        @Test
        @DisplayName("east: clear row, walled endpoints, still in sight")
        void eastClear() {
            wall(at(2, 5));
            wall(at(10, 5));
            assertTrue(los(2, 5, 10, 5));
        }

        /**
         * A wall strictly between the endpoints blocks the eastward look.
         */
        @Test
        @DisplayName("east: a wall between the endpoints blocks")
        void eastBlocked() {
            wall(at(6, 5));
            assertFalse(los(2, 5, 10, 5));
        }

        /**
         * And the westward loop, counting down, blocks on the same wall.
         */
        @Test
        @DisplayName("west: the same wall blocks the reversed look")
        void westBlocked() {
            wall(at(6, 5));
            assertFalse(los(10, 5, 2, 5));
        }
    }

    /**
     * The knight's-move special cases, which C grants more generously than the true ray would, and
     * which are the sole place the function is not reflexive.
     */
    @Nested
    @DisplayName("knight's moves")
    class Knights {

        /**
         * Offset one by two. C consults only the grid orthogonally below the origin, so sight holds
         * even though the diagonal neighbour the true ray clips is a wall.
         */
        @Test
        @DisplayName("tall knight: the orthogonal neighbour alone decides")
        void tallKnightGranted() {
            wall(at(6, 5));
            assertTrue(los(5, 4, 6, 6));
        }

        /**
         * With that orthogonal neighbour walled the special case does not fire, and the vertical
         * fixed-point walk then checks the same grid and blocks.
         */
        @Test
        @DisplayName("tall knight: walling the orthogonal neighbour blocks")
        void tallKnightBlocked() {
            wall(at(5, 5));
            assertFalse(los(5, 4, 6, 6));
        }

        /**
         * Offset two by one, the horizontal twin: C consults the grid orthogonally beside the
         * origin, and the diagonal neighbour is irrelevant.
         */
        @Test
        @DisplayName("wide knight: the orthogonal neighbour alone decides")
        void wideKnightGranted() {
            wall(at(5, 6));
            assertTrue(los(4, 5, 6, 6));
        }

        /**
         * And walling that neighbour drops through to the horizontal walk, which blocks on it.
         */
        @Test
        @DisplayName("wide knight: walling the orthogonal neighbour blocks")
        void wideKnightBlocked() {
            wall(at(5, 5));
            assertFalse(los(4, 5, 6, 6));
        }

        /**
         * The asymmetry C documents. The grid consulted is chosen relative to the first argument,
         * so with a wall at {@code (6,5)} the look from {@code (5,4)} consults the open
         * {@code (5,5)} and succeeds, while the look back consults the wall and fails — then falls
         * into the vertical walk, which checks {@code (6,5)} again and confirms the block.
         */
        @Test
        @DisplayName("the knight's move is the one case that is not reflexive")
        void notReflexive() {
            wall(at(6, 5));
            assertTrue(los(5, 4, 6, 6));
            assertFalse(los(6, 6, 5, 4));
        }
    }

    /**
     * The fixed-point walk, checked grid by grid against the sequences traced through the C.
     */
    @Nested
    @DisplayName("the fixed-point walk")
    class Walk {

        /**
         * Slope 1. The C walk pre-steps and then visits the pure diagonal, so a wall on it blocks.
         */
        @Test
        @DisplayName("slope 1 visits the diagonal")
        void diagonalBlocked() {
            wall(at(4, 4));
            assertFalse(los(2, 2, 6, 6));
        }

        /**
         * The grids either side of that diagonal are not on the walk at all, so walling both leaves
         * sight intact. This is what separates the C's ray from a thicker one.
         */
        @Test
        @DisplayName("slope 1 does not visit the grids flanking the diagonal")
        void diagonalFlanksIgnored() {
            wall(at(3, 4));
            wall(at(4, 3));
            wall(at(5, 4));
            wall(at(4, 5));
            assertTrue(los(2, 2, 6, 6));
        }

        /**
         * Endpoints are looked at, not through, on the diagonal as much as on a straight line.
         */
        @Test
        @DisplayName("slope 1 ignores walls on the endpoints")
        void diagonalEndpointsIgnored() {
            wall(at(2, 2));
            wall(at(6, 6));
            assertTrue(los(2, 2, 6, 6));
        }

        /**
         * Slope 1/3, walking horizontally. Each grid of the traced sequence blocks when walled.
         */
        @Test
        @DisplayName("slope 1/3: every grid on the traced path blocks")
        void shallowPathBlocks() {
            int[][] path = {{1, 0}, {2, 1}, {3, 1}, {4, 1}, {5, 2}};
            for (int[] grid : path) {
                newLevel();
                wall(at(grid[0], grid[1]));
                assertFalse(los(0, 0, 6, 2),
                        "wall at " + grid[0] + "," + grid[1] + " should block");
            }
        }

        /**
         * The corner brush. On this slope the fraction lands exactly on the scale factor, so the
         * ray meets the corner of {@code (1,1)} without entering it and C takes the shorter-axis
         * step with no second check. Walling it must not block.
         */
        @Test
        @DisplayName("slope 1/3: a grid the ray only brushes does not block")
        void shallowCornerBrushed() {
            wall(at(1, 1));
            assertTrue(los(0, 0, 6, 2));
        }

        /**
         * Slope 2/5, the contrast case. Here the fraction overshoots the scale factor, the ray
         * genuinely enters {@code (1,1)}, and C's second check runs — the same grid that was
         * harmless above now blocks. A port that dropped the overshoot branch's extra check would
         * pass the brush test and fail this one.
         */
        @Test
        @DisplayName("slope 2/5: the grid the ray enters does block")
        void steeperCornerEntered() {
            wall(at(1, 1));
            assertFalse(los(0, 0, 5, 2));
        }

        /**
         * The rest of the slope 2/5 sequence, for completeness.
         */
        @Test
        @DisplayName("slope 2/5: every grid on the traced path blocks")
        void steeperPathBlocks() {
            int[][] path = {{1, 0}, {1, 1}, {2, 1}, {3, 1}, {4, 1}, {4, 2}};
            for (int[] grid : path) {
                newLevel();
                wall(at(grid[0], grid[1]));
                assertFalse(los(0, 0, 5, 2),
                        "wall at " + grid[0] + "," + grid[1] + " should block");
            }
        }

        /**
         * The vertical walk is the horizontal one with the axes swapped, and C writes it out
         * separately, so it gets its own transposed sequence rather than being taken on trust.
         */
        @Test
        @DisplayName("vertical walk: every grid on the transposed path blocks")
        void verticalPathBlocks() {
            int[][] path = {{0, 1}, {1, 1}, {1, 2}, {1, 3}, {1, 4}, {2, 4}};
            for (int[] grid : path) {
                newLevel();
                wall(at(grid[0], grid[1]));
                assertFalse(los(0, 0, 2, 5),
                        "wall at " + grid[0] + "," + grid[1] + " should block");
            }
        }

        /**
         * And the vertical walk on a clear level is in sight, so the sequence above is blocking on
         * the wall rather than on something the fixture got wrong.
         */
        @Test
        @DisplayName("vertical walk: a clear path is in sight")
        void verticalClear() {
            assertTrue(los(0, 0, 2, 5));
        }

        /**
         * Away from the knight's moves the function is reflexive, in all four quadrants. A sign
         * error in the deltas or in the walk shows here as a disagreement between the two
         * directions.
         */
        @Test
        @DisplayName("reflexive in all four quadrants")
        void reflexive() {
            wall(at(6, 5));
            wall(at(7, 4));
            int[][] pairs = {{3, 3, 9, 7}, {9, 7, 3, 3}, {3, 7, 9, 3}, {9, 3, 3, 7}};
            for (int[] p : pairs)
                assertTrue(los(p[0], p[1], p[2], p[3]) == los(p[2], p[3], p[0], p[1]),
                        "asymmetric for " + p[0] + "," + p[1] + " to " + p[2] + "," + p[3]);
        }
    }
}
