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

package uk.co.jackoftradesltd.frontend.screen;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import uk.co.jackoftradesltd.channel.colour.ColourEnum;
import uk.co.jackoftradesltd.channel.strings.AngbandDisplayCharacter;
import uk.co.jackoftradesltd.frontend.screen.grid.CellGrid;
import uk.co.jackoftradesltd.frontend.screen.grid.Screen;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * {@link Term#cPutStr} and {@link Term#putStr}, checked against C's {@code c_put_str} and
 * {@code put_str} ({@code [C] src/ui-output.c}): both delegate entirely to
 * {@code Term_putstr} - move first, then dump the string - and {@code put_str} is just
 * {@code c_put_str} fixed to {@code COLOUR_WHITE}.
 *
 * <p>The Java split keeps that same two-step shape across {@link Term#gotoXY} and the
 * installed {@code outputHook}, so an out-of-range {@code row}/{@code col} must reject
 * before the hook ever writes a cell - matching {@code Term_putstr} returning early,
 * without reaching {@code Term_addstr}, when {@code Term_gotoxy} fails.
 *
 * @author Rowan Crowther
 */
@Timeout(value = 30, unit = TimeUnit.SECONDS)
class TermPutStrTest {

    private static final int WIDTH = 80;
    private static final int HEIGHT = 24;

    private Term term;
    private CellGrid grid;

    @BeforeEach
    void buildAnEightyByTwentyFourTerm() {
        term = new Term();
        grid = new CellGrid(HEIGHT, WIDTH);
        term.termInit(WIDTH, HEIGHT, 1024, null, new Screen(grid, new ArrayList<>()));
    }

    private AngbandDisplayCharacter cell(int row, int col) {
        return grid.get(row, col);
    }

    /**
     * An ordinary in-range write: succeeds, and every character of the string lands in
     * the given colour starting at {@code (row, col)}.
     */
    @Test
    void anOrdinaryWriteSucceedsAndPlacesEveryCharacter() {
        assertEquals(0, term.cPutStr(ColourEnum.COLOUR_RED, "Hi", 5, 10));

        assertEquals('H', cell(5, 10).getCharacter());
        assertEquals(ColourEnum.COLOUR_RED, cell(5, 10).getAttributeColour());
        assertEquals('i', cell(5, 11).getCharacter());
        assertEquals(ColourEnum.COLOUR_RED, cell(5, 11).getAttributeColour());
    }

    /**
     * {@code put_str} is {@code c_put_str} fixed to white - the same write, with no
     * colour argument.
     */
    @Test
    void putStrWritesInWhite() {
        assertEquals(0, term.putStr("Hi", 5, 10));

        assertEquals('H', cell(5, 10).getCharacter());
        assertEquals(ColourEnum.COLOUR_WHITE, cell(5, 10).getAttributeColour());
    }

    /**
     * The top-left corner, {@code (0, 0)}, is in range - C's bound is {@code x >= w},
     * not {@code x > w}.
     */
    @Test
    void theFirstCellIsInRange() {
        assertEquals(0, term.cPutStr(ColourEnum.COLOUR_WHITE, "A", 0, 0));
        assertEquals('A', cell(0, 0).getCharacter());
    }

    /**
     * The bottom-right corner, {@code (width - 1, height - 1)}, is in range.
     */
    @Test
    void theLastCellIsInRange() {
        assertEquals(0, term.cPutStr(ColourEnum.COLOUR_WHITE, "A", HEIGHT - 1, WIDTH - 1));
        assertEquals('A', cell(HEIGHT - 1, WIDTH - 1).getCharacter());
    }

    /**
     * A negative column is rejected before any write - {@code Term_gotoxy} fails, so
     * {@code Term_addstr} is never reached.
     */
    @Test
    void aNegativeColumnIsRejectedAndNothingIsWritten() {
        assertEquals(-1, term.cPutStr(ColourEnum.COLOUR_WHITE, "Hi", 5, -1));
        assertNull(cell(5, 0));
    }

    /**
     * A negative row is rejected before any write.
     */
    @Test
    void aNegativeRowIsRejectedAndNothingIsWritten() {
        assertEquals(-1, term.cPutStr(ColourEnum.COLOUR_WHITE, "Hi", -1, 10));
        assertNull(cell(0, 10));
    }

    /**
     * A column equal to the width is one past the last valid column - C's {@code x >= w}
     * - and is rejected before any write.
     */
    @Test
    void aColumnEqualToTheWidthIsRejectedAndNothingIsWritten() {
        assertEquals(-1, term.cPutStr(ColourEnum.COLOUR_WHITE, "Hi", 5, WIDTH));
        assertNull(cell(5, WIDTH - 1));
    }

    /**
     * A row equal to the height is one past the last valid row - C's {@code y >= h} -
     * and is rejected before any write.
     */
    @Test
    void aRowEqualToTheHeightIsRejectedAndNothingIsWritten() {
        assertEquals(-1, term.cPutStr(ColourEnum.COLOUR_WHITE, "Hi", HEIGHT, 10));
        assertNull(cell(HEIGHT - 1, 10));
    }

    /**
     * {@code putStr} rejects out of range the same way {@code cPutStr} does, since it is
     * only {@code cPutStr} with a fixed colour.
     */
    @Test
    void putStrRejectsOutOfRangeTheSameWay() {
        assertEquals(-1, term.putStr("Hi", -1, 10));
        assertNull(cell(0, 10));
    }
}
