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

package uk.co.jackoftradesltd.frontend.screen.grid;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.channel.colour.ColourEnum;
import uk.co.jackoftradesltd.channel.strings.AngbandDisplayCharacter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests {@link CellGrid} against the decisions recorded in {@code docs/UIPanelArchitecture.md} -
 * there is no C original to check against, since this is new architecture, not a port. C's nearest
 * structure, {@code term_win} ({@code ui-term.h}), is a raw, unchecked {@code int**}/{@code wchar_t**}
 * pair with no bounds-checked accessors, so it gives no expected values to derive tests from; the
 * design doc is the spec instead.
 *
 * <p>Class CellGridTest coded on 260909, commented in full on 260909.
 *
 * @author Rowan Crowther
 */
class CellGridTest {

    private static final int ROWS = 6;
    private static final int COLS = 9;

    private static final AngbandDisplayCharacter A =
            new AngbandDisplayCharacter('A', ColourEnum.COLOUR_GREEN);
    private static final AngbandDisplayCharacter B =
            new AngbandDisplayCharacter('B', ColourEnum.COLOUR_RED);

    @Nested
    @DisplayName("construction")
    class Construction {

        @Test
        @DisplayName("rows() and cols() report the constructed size")
        void reportsConstructedSize() {
            CellGrid grid = new CellGrid(ROWS, COLS);

            assertEquals(ROWS, grid.rows());
            assertEquals(COLS, grid.cols());
        }

        /**
         * Deliberately different from {@code JPanelArea}'s constructor, which fills every cell
         * with a blank space ({@code SwingUI.java}). {@link CellGrid} allocates and nothing more -
         * the design doc records this as an open gap, not an oversight, so it is worth pinning
         * down with a test rather than left to be "discovered" as a regression later.
         */
        @Test
        @DisplayName("every cell starts null - construction does not pre-fill blanks")
        void everyCellStartsNull() {
            CellGrid grid = new CellGrid(ROWS, COLS);

            for (int row = 0; row < ROWS; row++) {
                for (int col = 0; col < COLS; col++) {
                    assertNull(grid.get(row, col), "cell (" + row + ", " + col + ")");
                }
            }
        }

        /**
         * {@code cols()} reads {@code cells[0].length} with no guard, per the design doc: a
         * zero-row grid is not a case the class defends against, so it surfaces as the ordinary
         * Java exception rather than a swallowed {@code 0}.
         */
        @Test
        @DisplayName("cols() on a zero-row grid throws, rather than returning 0")
        void colsOnZeroRowGridThrows() {
            CellGrid grid = new CellGrid(0, COLS);

            assertThrows(ArrayIndexOutOfBoundsException.class, grid::cols);
        }
    }

    @Nested
    @DisplayName("get(row, col)")
    class Get {

        @Test
        @DisplayName("returns what set(row, col, ...) last wrote")
        void returnsWhatWasSet() {
            CellGrid grid = new CellGrid(ROWS, COLS);
            grid.set(2, 3, A);

            assertEquals(A, grid.get(2, 3));
        }

        @Test
        @DisplayName("the last valid row and column are readable")
        void lastValidCellIsReadable() {
            CellGrid grid = new CellGrid(ROWS, COLS);
            grid.set(ROWS - 1, COLS - 1, A);

            assertEquals(A, grid.get(ROWS - 1, COLS - 1));
        }

        @Test
        @DisplayName("a negative row returns null, not an exception")
        void negativeRowReturnsNull() {
            CellGrid grid = new CellGrid(ROWS, COLS);

            assertNull(grid.get(-1, 0));
        }

        @Test
        @DisplayName("a row at the grid's height returns null")
        void rowAtGridHeightReturnsNull() {
            CellGrid grid = new CellGrid(ROWS, COLS);

            assertNull(grid.get(ROWS, 0));
        }

        @Test
        @DisplayName("a negative column returns null")
        void negativeColumnReturnsNull() {
            CellGrid grid = new CellGrid(ROWS, COLS);

            assertNull(grid.get(0, -1));
        }

        @Test
        @DisplayName("a column at the grid's width returns null")
        void columnAtGridWidthReturnsNull() {
            CellGrid grid = new CellGrid(ROWS, COLS);

            assertNull(grid.get(0, COLS));
        }
    }

    @Nested
    @DisplayName("set(row, col, character)")
    class Set {

        @Test
        @DisplayName("a later set(...) overwrites an earlier one at the same cell")
        void laterSetOverwritesEarlierOne() {
            CellGrid grid = new CellGrid(ROWS, COLS);
            grid.set(1, 1, A);
            grid.set(1, 1, B);

            assertEquals(B, grid.get(1, 1));
        }

        @Test
        @DisplayName("the last valid row and column are writable")
        void lastValidCellIsWritable() {
            CellGrid grid = new CellGrid(ROWS, COLS);

            grid.set(ROWS - 1, COLS - 1, A);

            assertEquals(A, grid.get(ROWS - 1, COLS - 1));
        }

        @Test
        @DisplayName("a negative row throws, rather than clipping")
        void negativeRowThrows() {
            CellGrid grid = new CellGrid(ROWS, COLS);

            assertThrows(IndexOutOfBoundsException.class, () -> grid.set(-1, 0, A));
        }

        @Test
        @DisplayName("a row at the grid's height throws")
        void rowAtGridHeightThrows() {
            CellGrid grid = new CellGrid(ROWS, COLS);

            assertThrows(IndexOutOfBoundsException.class, () -> grid.set(ROWS, 0, A));
        }

        @Test
        @DisplayName("a negative column throws")
        void negativeColumnThrows() {
            CellGrid grid = new CellGrid(ROWS, COLS);

            assertThrows(IndexOutOfBoundsException.class, () -> grid.set(0, -1, A));
        }

        @Test
        @DisplayName("a column at the grid's width throws")
        void columnAtGridWidthThrows() {
            CellGrid grid = new CellGrid(ROWS, COLS);

            assertThrows(IndexOutOfBoundsException.class, () -> grid.set(0, COLS, A));
        }
    }

    @Nested
    @DisplayName("copy()")
    class Copy {

        @Test
        @DisplayName("the copy starts out equal to the original, cell for cell")
        void copyStartsEqualToOriginal() {
            CellGrid grid = new CellGrid(ROWS, COLS);
            grid.set(0, 0, A);
            grid.set(ROWS - 1, COLS - 1, B);

            CellGrid copy = grid.copy();

            assertEquals(grid.rows(), copy.rows());
            assertEquals(grid.cols(), copy.cols());
            for (int row = 0; row < ROWS; row++) {
                for (int col = 0; col < COLS; col++) {
                    assertEquals(grid.get(row, col), copy.get(row, col), "cell (" + row + ", " + col + ")");
                }
            }
        }

        /**
         * The structural-copy guarantee {@code Screen.frame()} rests on: the copy's row arrays
         * must be independent of the original's, not aliased. A shallow {@code cells.clone()}
         * would pass every other test here but fail this one, since the row arrays - and so the
         * cells reachable through them - would still be shared.
         */
        @Test
        @DisplayName("writing to the original after copying does not change the copy")
        void writingToOriginalAfterCopyingDoesNotChangeCopy() {
            CellGrid grid = new CellGrid(ROWS, COLS);
            grid.set(3, 4, A);

            CellGrid copy = grid.copy();
            grid.set(3, 4, B);

            assertEquals(A, copy.get(3, 4));
            assertEquals(B, grid.get(3, 4));
        }

        @Test
        @DisplayName("writing to the copy does not change the original")
        void writingToCopyDoesNotChangeOriginal() {
            CellGrid grid = new CellGrid(ROWS, COLS);
            grid.set(3, 4, A);

            CellGrid copy = grid.copy();
            copy.set(3, 4, B);

            assertEquals(A, grid.get(3, 4));
            assertEquals(B, copy.get(3, 4));
        }
    }
}
