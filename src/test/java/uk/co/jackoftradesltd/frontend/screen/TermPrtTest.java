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
 * {@link Term#cPrt} and {@link Term#prt}, checked against C's {@code c_prt} and {@code prt}
 * ({@code [C] src/ui-output.c}): both clear the rest of the line with {@code Term_erase} and
 * then dump the string with {@code Term_addstr} directly - unlike {@code c_put_str}, there is
 * no {@code Term_gotoxy} step, so an out-of-range {@code row}/{@code col} is never rejected up
 * front. It is only clipped, the way every write through {@code Region} is clipped
 * ({@code Region.put}/{@code Region.erase}), which is why these tests check what gets drawn
 * rather than a returned status code the way {@code TermPutStrTest} does for {@code cPutStr}.
 *
 * @author Rowan Crowther
 */
@Timeout(value = 30, unit = TimeUnit.SECONDS)
class TermPrtTest {

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
     * An ordinary in-range write: the string lands in the given colour starting at
     * {@code (row, col)}, and the rest of the line to its right is blanked in white by the
     * {@code Term_erase(col, row, 255)} step that runs first.
     */
    @Test
    void anOrdinaryWriteErasesToEndOfLineThenPlacesTheString() {
        term.cPrt(ColourEnum.COLOUR_RED, "Hi", 5, 10);

        assertEquals('H', cell(5, 10).getCharacter());
        assertEquals(ColourEnum.COLOUR_RED, cell(5, 10).getAttributeColour());
        assertEquals('i', cell(5, 11).getCharacter());
        assertEquals(ColourEnum.COLOUR_RED, cell(5, 11).getAttributeColour());

        assertEquals(' ', cell(5, 12).getCharacter());
        assertEquals(ColourEnum.COLOUR_WHITE, cell(5, 12).getAttributeColour());
        assertEquals(' ', cell(5, WIDTH - 1).getCharacter());
        assertEquals(ColourEnum.COLOUR_WHITE, cell(5, WIDTH - 1).getAttributeColour());
    }

    /**
     * A cell to the left of the write is untouched - only {@code col} onward is erased.
     */
    @Test
    void aCellBeforeTheWriteIsUntouched() {
        term.cPrt(ColourEnum.COLOUR_RED, "Hi", 5, 10);

        assertNull(cell(5, 9));
    }

    /**
     * {@code prt} is {@code c_prt} fixed to white - the same write, with no colour argument.
     */
    @Test
    void prtWritesInWhite() {
        term.prt("Hi", 5, 10);

        assertEquals('H', cell(5, 10).getCharacter());
        assertEquals(ColourEnum.COLOUR_WHITE, cell(5, 10).getAttributeColour());
    }

    /**
     * The top-left corner, {@code (0, 0)}, is in range.
     */
    @Test
    void theFirstCellIsInRange() {
        term.cPrt(ColourEnum.COLOUR_WHITE, "A", 0, 0);

        assertEquals('A', cell(0, 0).getCharacter());
    }

    /**
     * The bottom-right corner, {@code (width - 1, height - 1)}, is in range.
     */
    @Test
    void theLastCellIsInRange() {
        term.cPrt(ColourEnum.COLOUR_WHITE, "A", HEIGHT - 1, WIDTH - 1);

        assertEquals('A', cell(HEIGHT - 1, WIDTH - 1).getCharacter());
    }

    /**
     * A negative row draws nothing at all - {@code Region.put}/{@code Region.erase} clip a bad
     * row rather than wrapping or throwing, and there is no {@code gotoXY} step here to reject
     * the call up front instead.
     */
    @Test
    void aNegativeRowDrawsNothing() {
        term.cPrt(ColourEnum.COLOUR_WHITE, "Hi", -1, 10);

        assertNull(cell(0, 10));
    }

    /**
     * A row equal to the height is one past the last valid row and draws nothing.
     */
    @Test
    void aRowEqualToTheHeightDrawsNothing() {
        term.cPrt(ColourEnum.COLOUR_WHITE, "Hi", HEIGHT, 10);

        assertNull(cell(HEIGHT - 1, 10));
    }

    /**
     * A column equal to the width is one past the last valid column and draws nothing -
     * {@code Region.put}'s {@code col >= cols} check, reached directly since there is no
     * {@code gotoXY} to reject it first.
     */
    @Test
    void aColumnEqualToTheWidthDrawsNothing() {
        term.cPrt(ColourEnum.COLOUR_WHITE, "Hi", 5, WIDTH);

        assertNull(cell(5, WIDTH - 1));
    }

    /**
     * A negative column drops its overhanging characters rather than discarding the whole
     * string - {@code Region.put(int, int, String, ColourEnum)}'s documented clip contract.
     * Starting "Hi" one column before the edge of the grid drops the "H" and draws only the
     * "i", at column 0.
     */
    @Test
    void aNegativeColumnDropsItsOverhangingCharacters() {
        term.cPrt(ColourEnum.COLOUR_RED, "Hi", 5, -1);

        assertEquals('i', cell(5, 0).getCharacter());
        assertEquals(ColourEnum.COLOUR_RED, cell(5, 0).getAttributeColour());
    }
}
