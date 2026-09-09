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

package uk.co.jackoftradesltd.frontend;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import uk.co.jackoftradesltd.channel.colour.ColourEnum;
import uk.co.jackoftradesltd.channel.strings.AngbandDisplayCharacter;

import java.awt.GraphicsEnvironment;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

/**
 * {@link SwingUI.JPanelArea#erase}, checked against the C original's boundary handling: the port
 * of {@code Term_erase} ({@code [C] src/ui-term.c}), which places the cursor with
 * {@code Term_gotoxy} before erasing and does nothing at all if that placement is out of range.
 *
 * <p><b>Constructing a {@code JPanelArea} means constructing a {@code SwingUI}</b>, because it is
 * a non-static inner class - the same reason {@code SwingUITest} needs a display, this does too.
 *
 * <p><b>How the grid is inspected without a getter.</b> {@link SwingUI.JPanelArea#setChars} keeps
 * the array it is given by reference rather than copying it, so a grid built here and handed to
 * {@code setChars} is the same object {@code erase} goes on to mutate - reading it back after the
 * call needs no reflection.
 *
 * @author Rowan Crowther
 */
@Timeout(value = 30, unit = TimeUnit.SECONDS)
class JPanelAreaEraseTest {

    private static final int ROWS = 24;
    private static final int COLS = 80;

    /**
     * The marker every cell starts as, chosen to be nothing {@link SwingUI.JPanelArea#erase}
     * would ever write, so a marker cell surviving a call is unambiguous evidence it was not
     * touched.
     */
    private static final AngbandDisplayCharacter MARKER =
            new AngbandDisplayCharacter('#', ColourEnum.COLOUR_RED);

    /**
     * What an erased cell becomes, per C's {@code scr_aa[x] = COLOUR_WHITE; scr_cc[x] = ' ';}.
     */
    private static final AngbandDisplayCharacter BLANK =
            new AngbandDisplayCharacter(' ', ColourEnum.COLOUR_WHITE);

    private SwingUI.JPanelArea area;
    private AngbandDisplayCharacter[][] grid;

    @BeforeEach
    void buildAFullyMarkedGrid() {
        assumeFalse(GraphicsEnvironment.isHeadless(),
                "needs a display: constructing a JPanelArea constructs the SwingUI enclosing it");

        SwingUI swingUi = new SwingUI(null, null, null);
        area = swingUi.new JPanelArea();

        grid = new AngbandDisplayCharacter[ROWS][COLS];
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                grid[row][col] = MARKER;
            }
        }
        area.setChars(grid);
    }

    /**
     * Asserts that row {@code row} is blank from {@code erasedLo} to {@code erasedHi} inclusive,
     * and still marked everywhere else in that row.
     *
     * @param row      the row to check
     * @param erasedLo the first column expected to have been blanked
     * @param erasedHi the last column expected to have been blanked, inclusive; pass a value
     *                 less than {@code erasedLo} to assert the whole row is untouched
     */
    private void assertRowErasedOnly(int row, int erasedLo, int erasedHi) {
        for (int col = 0; col < COLS; col++) {
            AngbandDisplayCharacter expected = (col >= erasedLo && col <= erasedHi) ? BLANK : MARKER;
            assertEquals(expected, grid[row][col],
                    "row " + row + ", column " + col + " should be "
                            + (expected == BLANK ? "blanked" : "untouched"));
        }
    }

    private void assertRowUntouched(int row) {
        assertRowErasedOnly(row, 0, -1);
    }

    /**
     * The ordinary path: a run in the middle of a row is blanked, and nothing either side of it -
     * in that row or any other - is touched.
     */
    @Test
    void erasesOnlyTheRequestedRun() {
        area.erase(10, 5, 4);

        assertRowErasedOnly(5, 10, 13);
        assertRowUntouched(4);
        assertRowUntouched(6);
    }

    /**
     * A run that would overrun the row's last column is shortened, not rejected - C's
     * {@code if (x + n > w) n = w - x;}. Erasing from column 75 for 10 cells in an 80-wide row can
     * only reach columns 75-79.
     */
    @Test
    void aRunPastTheEdgeIsClampedNotRejected() {
        area.erase(75, 3, 10);

        assertRowErasedOnly(3, 75, 79);
    }

    /**
     * The last column is reachable and erasable on its own - the clamp in the test above must
     * land exactly on it, not one short.
     */
    @Test
    void theLastColumnCanBeErasedAlone() {
        area.erase(79, 7, 1);

        assertRowErasedOnly(7, 79, 79);
    }

    /**
     * A run spanning the whole row blanks every column in it - the clamp must not cut a run that
     * was never going to overrun in the first place.
     */
    @Test
    void aFullRowIsErasedEntirely() {
        area.erase(0, 12, COLS);

        assertRowErasedOnly(12, 0, COLS - 1);
    }

    /**
     * {@code n == 0} erases nothing, the same as C's {@code for (i = 0; i < n; ...)} not
     * executing when {@code n} is zero.
     */
    @Test
    void zeroCellsErasesNothing() {
        area.erase(10, 8, 0);

        assertRowUntouched(8);
    }

    /**
     * A negative {@code n} also erases nothing: C's clamp only ever shortens an overrunning run,
     * so a negative width is never lengthened to zero, and the loop condition {@code i < n} is
     * false from the first check exactly as it is for {@code n == 0}.
     */
    @Test
    void negativeCellCountErasesNothing() {
        area.erase(10, 9, -5);

        assertRowUntouched(9);
    }

    /**
     * A negative column is out of range for {@code Term_gotoxy}, which rejects it and returns
     * before {@code Term_erase} touches a single cell - not "erase whatever is reachable".
     */
    @Test
    void negativeColumnIsANoOp() {
        area.erase(-1, 6, 5);

        assertRowUntouched(6);
    }

    /**
     * A negative row is rejected the same way, and by the same C function - {@code Term_gotoxy}
     * does not distinguish which coordinate was out of range.
     */
    @Test
    void negativeRowIsANoOp() {
        area.erase(5, -1, 5);

        for (int row = 0; row < ROWS; row++) {
            assertRowUntouched(row);
        }
    }

    /**
     * A column equal to the row's width is one past the last valid index -
     * {@code Term_gotoxy}'s {@code x >= w} guard, not {@code x > w}.
     */
    @Test
    void columnAtTheWidthIsANoOp() {
        area.erase(COLS, 2, 5);

        assertRowUntouched(2);
    }

    /**
     * A row equal to the grid's height is one past the last valid index -
     * {@code Term_gotoxy}'s {@code y >= h} guard, not {@code y > h}.
     */
    @Test
    void rowAtTheHeightIsANoOp() {
        area.erase(5, ROWS, 5);

        for (int row = 0; row < ROWS; row++) {
            assertRowUntouched(row);
        }
    }
}
