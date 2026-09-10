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

/**
 * {@link Term#clearFrom}, checked against C's {@code clear_from}
 * ({@code [C] src/ui-input.c}): {@code for (y = row; y < Term->hgt; y++) Term_erase(0, y, 255);}
 * - every row from {@code row} up to but not including the terminal's height is blanked in
 * white, and rows above {@code row} are left exactly as they were.
 *
 * @author Rowan Crowther
 */
@Timeout(value = 30, unit = TimeUnit.SECONDS)
class TermClearFromTest {

    private static final int WIDTH = 80;
    private static final int HEIGHT = 24;

    private Term term;
    private CellGrid grid;

    @BeforeEach
    void buildAnEightyByTwentyFourTermWithEveryRowFilled() {
        term = new Term();
        grid = new CellGrid(HEIGHT, WIDTH);
        term.termInit(WIDTH, HEIGHT, 1024, null, new Screen(grid, new ArrayList<>()));

        for (int row = 0; row < HEIGHT; row++) {
            for (int col = 0; col < WIDTH; col++) {
                grid.set(row, col, new AngbandDisplayCharacter('X', ColourEnum.COLOUR_RED));
            }
        }
    }

    private AngbandDisplayCharacter cell(int row, int col) {
        return grid.get(row, col);
    }

    /**
     * Clearing from a middle row blanks that row and every row below it, in white, across
     * the whole width - C's {@code Term_erase(0, y, 255)} clamping {@code n} down to the
     * terminal's actual width.
     */
    @Test
    void clearingFromAMiddleRowBlanksItAndEveryRowBelow() {
        term.clearFrom(10);

        for (int row = 10; row < HEIGHT; row++) {
            assertEquals(' ', cell(row, 0).getCharacter());
            assertEquals(ColourEnum.COLOUR_WHITE, cell(row, 0).getAttributeColour());
            assertEquals(' ', cell(row, WIDTH - 1).getCharacter());
            assertEquals(ColourEnum.COLOUR_WHITE, cell(row, WIDTH - 1).getAttributeColour());
        }
    }

    /**
     * Rows above the requested row are untouched - only {@code row} onward is cleared.
     */
    @Test
    void rowsAboveTheRequestedRowAreUntouched() {
        term.clearFrom(10);

        assertEquals('X', cell(9, 0).getCharacter());
        assertEquals(ColourEnum.COLOUR_RED, cell(9, 0).getAttributeColour());
        assertEquals('X', cell(0, WIDTH - 1).getCharacter());
        assertEquals(ColourEnum.COLOUR_RED, cell(0, WIDTH - 1).getAttributeColour());
    }

    /**
     * {@code row} of {@code 0} clears the entire terminal.
     */
    @Test
    void aRowOfZeroClearsTheWholeTerminal() {
        term.clearFrom(0);

        for (int row = 0; row < HEIGHT; row++) {
            assertEquals(' ', cell(row, 0).getCharacter());
            assertEquals(' ', cell(row, WIDTH - 1).getCharacter());
        }
    }

    /**
     * A row equal to the height is one past the last valid row - C's loop bound
     * {@code y < Term->hgt} - and clears nothing.
     */
    @Test
    void aRowEqualToTheHeightClearsNothing() {
        term.clearFrom(HEIGHT);

        for (int row = 0; row < HEIGHT; row++) {
            assertEquals('X', cell(row, 0).getCharacter());
            assertEquals(ColourEnum.COLOUR_RED, cell(row, 0).getAttributeColour());
        }
    }

    /**
     * The last row, {@code HEIGHT - 1}, is in range on its own and is the only row cleared.
     */
    @Test
    void clearingFromTheLastRowClearsOnlyThatRow() {
        term.clearFrom(HEIGHT - 1);

        assertEquals(' ', cell(HEIGHT - 1, 0).getCharacter());
        assertEquals('X', cell(HEIGHT - 2, 0).getCharacter());
    }

    /**
     * A negative row runs the loop from a negative index, but each of those out-of-range
     * calls no-ops the way {@code Region.put} clips a negative row - so the net effect is
     * the same as clearing from {@code 0}, not a crash and not "clear nothing".
     */
    @Test
    void aNegativeRowStillClearsEveryValidRow() {
        term.clearFrom(-5);

        for (int row = 0; row < HEIGHT; row++) {
            assertEquals(' ', cell(row, 0).getCharacter());
            assertEquals(ColourEnum.COLOUR_WHITE, cell(row, 0).getAttributeColour());
        }
    }
}
