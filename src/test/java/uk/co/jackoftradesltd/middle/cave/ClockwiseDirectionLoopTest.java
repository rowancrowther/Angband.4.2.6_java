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

package uk.co.jackoftradesltd.middle.cave;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

/**
 * Tests {@link ClockwiseDirectionLoop} against C's {@code clockwise_grid[]} and
 * {@code clockwise_ddd[]} ({@code cave.c}).
 *
 * <p>The expected values are the literals in the C arrays, written out here as {x, y} pairs:
 * {@code {0,-1} {1,-1} {1,0} {1,1} {0,1} {-1,1} {-1,0} {-1,-1} {0,0}}. C indexes those arrays with a
 * caller-owned index, so each {@code new ClockwiseDirectionLoop()} must own its own cursor, start at
 * north, and be unaffected by any other loop.
 */
class ClockwiseDirectionLoopTest {
    /**
     * C's {@code clockwise_grid[]} as {x, y}.
     */
    private static final int[][] CLOCKWISE_GRID = {
            {0, -1}, {1, -1}, {1, 0}, {1, 1}, {0, 1}, {-1, 1}, {-1, 0}, {-1, -1}, {0, 0}};

    private ClockwiseDirectionLoop loop;

    @BeforeEach
    void newLoop() {
        loop = new ClockwiseDirectionLoop();
    }

    @Test
    @DisplayName("two loops advance independently")
    void loopsAreIndependent() {
        ClockwiseDirectionLoop other = new ClockwiseDirectionLoop();
        loop.moveNext();
        loop.moveNext();
        // loop is at E {1,0}; other has not moved and is still at N {0,-1}
        assertEquals(1, loop.getXOffset());
        assertEquals(0, loop.getYOffset());
        assertEquals(0, other.getXOffset());
        assertEquals(-1, other.getYOffset());
    }

    @Test
    @DisplayName("constructing a new loop does not reset an existing one")
    void newLoopDoesNotResetOthers() {
        loop.moveNext();
        new ClockwiseDirectionLoop();
        // still NE {1,-1}
        assertEquals(1, loop.getXOffset());
        assertEquals(-1, loop.getYOffset());
    }

    @Test
    @DisplayName("north is the first direction, as clockwise_grid[0]")
    void startsAtNorth() {
        assertEquals(0, loop.getXOffset());
        assertEquals(-1, loop.getYOffset());
    }

    @Test
    @DisplayName("one full lap visits the nine entries in clockwise_grid[] order")
    void fullLapMatchesC() {
        for (int[] expected : CLOCKWISE_GRID) {
            assertEquals(expected[0], loop.getXOffset());
            assertEquals(expected[1], loop.getYOffset());
            loop.moveNext();
        }
    }

    @Test
    @DisplayName("the centre entry wraps back to north")
    void centreWrapsToNorth() {
        for (int i = 0; i < 8; i++) {
            loop.moveNext();
        }
        assertEquals(0, loop.getXOffset());
        assertEquals(0, loop.getYOffset());
        loop.moveNext();
        assertEquals(0, loop.getXOffset());
        assertEquals(-1, loop.getYOffset());
    }

    @Test
    @DisplayName("getGrid gives a Loc with x and y in the right places")
    void getGridPlacesXAndY() {
        // clockwise_grid[1] = {1, -1}: NE is +x, -y; a swapped x/y would give (-1, 1)
        loop.moveNext();
        Loc grid = loop.getGrid();
        assertEquals(1, grid.getX());
        assertEquals(-1, grid.getY());
    }

    @Test
    @DisplayName("getGrid returns a new Loc each call")
    void getGridReturnsFreshLoc() {
        assertNotSame(loop.getGrid(), loop.getGrid());
    }

    @Test
    @DisplayName("every compass entry is one step from the origin, so headings are adjacent grids")
    void everyCompassEntryIsAdjacent() {
        for (int i = 0; i < 8; i++) {
            int x = loop.getXOffset();
            int y = loop.getYOffset();
            assertEquals(1, Math.max(Math.abs(x), Math.abs(y)));
            loop.moveNext();
        }
    }
}
