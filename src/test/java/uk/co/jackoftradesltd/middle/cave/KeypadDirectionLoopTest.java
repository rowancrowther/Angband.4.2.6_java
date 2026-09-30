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
 * Tests {@link KeypadDirectionLoop} against C's {@code ddgrid_ddd[]} ({@code cave.c}).
 *
 * <p>The expected values are the literals in the C array, written out here as {x, y} pairs:
 * {@code {0,1} {0,-1} {1,0} {-1,0} {1,1} {-1,1} {1,-1} {-1,-1} {0,0}}. C indexes that array with a
 * caller-owned index, so each {@code new KeypadDirectionLoop()} must own its own cursor, start at
 * south, and be unaffected by any other loop.
 */
class KeypadDirectionLoopTest {
    /**
     * C's {@code ddgrid_ddd[]} as {x, y}.
     */
    private static final int[][] DDGRID_DDD = {
            {0, 1}, {0, -1}, {1, 0}, {-1, 0}, {1, 1}, {-1, 1}, {1, -1}, {-1, -1}, {0, 0}};

    private KeypadDirectionLoop loop;

    @BeforeEach
    void newLoop() {
        loop = new KeypadDirectionLoop();
    }

    @Test
    @DisplayName("south is the first direction, as ddgrid_ddd[0]")
    void startsAtSouth() {
        assertEquals(0, loop.getXOffset());
        assertEquals(1, loop.getYOffset());
    }

    @Test
    @DisplayName("one full lap visits the nine entries in ddgrid_ddd[] order")
    void fullLapMatchesC() {
        for (int[] expected : DDGRID_DDD) {
            assertEquals(expected[0], loop.getXOffset());
            assertEquals(expected[1], loop.getYOffset());
            loop.moveNext();
        }
    }

    @Test
    @DisplayName("getGrid agrees with the offsets at every position in a lap")
    void getGridMatchesTableEverywhere() {
        for (int[] expected : DDGRID_DDD) {
            Loc grid = loop.getGrid();
            assertEquals(expected[0], grid.getX());
            assertEquals(expected[1], grid.getY());
            loop.moveNext();
        }
    }

    @Test
    @DisplayName("the centre entry wraps back to south")
    void centreWrapsToSouth() {
        for (int i = 0; i < 8; i++) {
            loop.moveNext();
        }
        assertEquals(0, loop.getXOffset());
        assertEquals(0, loop.getYOffset());
        loop.moveNext();
        assertEquals(0, loop.getXOffset());
        assertEquals(1, loop.getYOffset());
    }

    @Test
    @DisplayName("a second lap repeats the first")
    void secondLapRepeats() {
        for (int i = 0; i < 9; i++) {
            loop.moveNext();
        }
        for (int[] expected : DDGRID_DDD) {
            assertEquals(expected[0], loop.getXOffset());
            assertEquals(expected[1], loop.getYOffset());
            loop.moveNext();
        }
    }

    @Test
    @DisplayName("two loops advance independently")
    void loopsAreIndependent() {
        KeypadDirectionLoop other = new KeypadDirectionLoop();
        loop.moveNext();
        loop.moveNext();
        // loop is at E {1,0}; other has not moved and is still at S {0,1}
        assertEquals(1, loop.getXOffset());
        assertEquals(0, loop.getYOffset());
        assertEquals(0, other.getXOffset());
        assertEquals(1, other.getYOffset());
    }

    @Test
    @DisplayName("constructing a new loop does not reset an existing one")
    void newLoopDoesNotResetOthers() {
        loop.moveNext();
        new KeypadDirectionLoop();
        // still N {0,-1}
        assertEquals(0, loop.getXOffset());
        assertEquals(-1, loop.getYOffset());
    }

    @Test
    @DisplayName("getGrid puts x and y in the right places")
    void getGridPlacesXAndY() {
        // ddgrid_ddd[2] = {1, 0}: E is +x; a swapped x/y would give (0, 1)
        loop.moveNext();
        loop.moveNext();
        Loc grid = loop.getGrid();
        assertEquals(1, grid.getX());
        assertEquals(0, grid.getY());
        // ddgrid_ddd[5] = {-1, 1}: SW is -x, +y
        loop.moveNext();
        loop.moveNext();
        loop.moveNext();
        grid = loop.getGrid();
        assertEquals(-1, grid.getX());
        assertEquals(1, grid.getY());
    }

    @Test
    @DisplayName("getGrid returns a new Loc each call")
    void getGridReturnsFreshLoc() {
        assertNotSame(loop.getGrid(), loop.getGrid());
    }

    @Test
    @DisplayName("the first eight entries are adjacent grids and only the ninth is the origin")
    void onlyTheNinthIsTheOrigin() {
        for (int i = 0; i < 8; i++) {
            int x = loop.getXOffset();
            int y = loop.getYOffset();
            assertEquals(1, Math.max(Math.abs(x), Math.abs(y)));
            loop.moveNext();
        }
        assertEquals(0, loop.getXOffset());
        assertEquals(0, loop.getYOffset());
    }
}
