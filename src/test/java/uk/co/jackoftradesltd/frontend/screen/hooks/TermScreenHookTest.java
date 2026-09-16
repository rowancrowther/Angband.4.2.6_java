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

package uk.co.jackoftradesltd.frontend.screen.hooks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import uk.co.jackoftradesltd.channel.colour.ColourEnum;
import uk.co.jackoftradesltd.frontend.screen.grid.CellGrid;
import uk.co.jackoftradesltd.frontend.screen.grid.Screen;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * {@link TermScreenHook}, checked directly rather than through {@link uk.co.jackoftradesltd.frontend.screen.Term}.
 * Every live caller in {@code Term} pre-clips {@code n} to a non-negative value before
 * calling {@link TermTextHook#putStr} - {@code Term.addstr} always computes
 * {@code n = min(k, str.length())} first - so the hook's own {@code n < 0} "unlimited"
 * branch, and its reliance on {@link uk.co.jackoftradesltd.frontend.screen.grid.Region}'s
 * own clipping rather than clipping itself, is never exercised by any {@code Term}-level
 * test. These tests call the hook directly to cover that branch, and the
 * {@code (col, row)} to {@code (row, col)} parameter swap in {@link TermScreenHook#erase},
 * on their own terms.
 *
 * @author Rowan Crowther
 */
@Timeout(value = 30, unit = TimeUnit.SECONDS)
class TermScreenHookTest {

    private static final int WIDTH = 10;
    private static final int HEIGHT = 5;

    private TermScreenHook hook;
    private CellGrid grid;
    private Screen screen;

    @BeforeEach
    void buildATenByFiveHookOverAnEmptyGrid() {
        grid = new CellGrid(HEIGHT, WIDTH);
        screen = new Screen(grid, new ArrayList<>());
        hook = new TermScreenHook(screen);
    }

    /**
     * A negative {@code n} draws the whole string unclipped by the hook itself - no
     * {@code substring} call at all - matching the C original's use of {@code n < 0} to
     * mean "as many as fit", which by this point in the call chain is entrusted entirely
     * to the painting surface.
     */
    @Test
    void negativeNDrawsTheWholeStringWithNoTruncation() {
        hook.putStr(2, 1, -1, ColourEnum.COLOUR_RED, "Hi");

        assertEquals('H', grid.get(1, 2).getCharacter());
        assertEquals(ColourEnum.COLOUR_RED, grid.get(1, 2).getAttributeColour());
        assertEquals('i', grid.get(1, 3).getCharacter());
    }

    /**
     * A negative {@code n} that would overrun the region's right edge is still not
     * clipped by the hook - it is handed to {@link uk.co.jackoftradesltd.frontend.screen.grid.Region#put}
     * exactly as given, which does the actual clipping. The write must not throw despite
     * the string running past the grid's width.
     */
    @Test
    void negativeNReliesOnTheRegionsOwnRightEdgeClipping() {
        hook.putStr(8, 0, -1, ColourEnum.COLOUR_RED, "Hello");

        assertEquals('H', grid.get(0, 8).getCharacter());
        assertEquals('e', grid.get(0, 9).getCharacter());
    }

    /**
     * An {@code n} of {@code 0} truncates to the empty string and draws nothing, rather
     * than being treated the same as "unlimited".
     */
    @Test
    void nOfZeroDrawsNothing() {
        hook.putStr(0, 0, 0, ColourEnum.COLOUR_RED, "Hi");

        assertNull(grid.get(0, 0));
    }

    /**
     * An explicit {@code n} shorter than the string truncates to exactly the first
     * {@code n} characters.
     */
    @Test
    void explicitNTruncatesToExactlyNCharacters() {
        hook.putStr(0, 0, 1, ColourEnum.COLOUR_RED, "Hi");

        assertEquals('H', grid.get(0, 0).getCharacter());
        assertNull(grid.get(0, 1));
    }

    /**
     * {@code erase}'s own parameter order is {@code (col, row, n)}, but it must call
     * {@link uk.co.jackoftradesltd.frontend.screen.grid.Region#erase} with {@code (row, col, n)} -
     * this proves the swap happens, rather than the two coordinates being passed straight
     * through. A sentinel character is placed at the transposed cell first; if the swap
     * were missing, that sentinel would be the one erased instead of the run at
     * {@code (row, col)}.
     */
    @Test
    void eraseSwapsColRowIntoRegionsRowColOrder() {
        screen.root().put(1, 3, 'A', ColourEnum.COLOUR_RED);
        screen.root().put(1, 4, 'B', ColourEnum.COLOUR_RED);
        screen.root().put(3, 1, 'S', ColourEnum.COLOUR_RED);

        hook.erase(3, 1, 2);

        assertEquals(' ', grid.get(1, 3).getCharacter());
        assertEquals(' ', grid.get(1, 4).getCharacter());
        assertEquals('S', grid.get(3, 1).getCharacter(), "a swapped erase would have hit this cell instead");
    }

    /**
     * An oversized {@code n} - the {@code 255} that {@code Term.clearFrom} and
     * {@code Term.cPrt} both pass, deliberately larger than any real row width - blanks
     * only the columns actually remaining, rather than throwing or writing past the row.
     */
    @Test
    void oversizedNClipsToTheRowsRemainingColumns() {
        hook.putStr(0, 0, -1, ColourEnum.COLOUR_RED, "0123456789");

        hook.erase(8, 0, 255);

        assertEquals('7', grid.get(0, 7).getCharacter());
        assertEquals(' ', grid.get(0, 8).getCharacter());
        assertEquals(' ', grid.get(0, 9).getCharacter());
    }
}
