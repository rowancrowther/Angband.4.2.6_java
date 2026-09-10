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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.channel.colour.ColourEnum;
import uk.co.jackoftradesltd.channel.strings.AngbandDisplayCharacter;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests {@link Region} against the clip contract set out in
 * {@code docs/UIPanelArchitecture.md} - there is no C original to check against, since this is
 * new architecture, not a port.
 *
 * <p>Every grid built here is 8 rows by 12 columns, and every region under test occupies rows
 * 2-5, columns 3-7 of it - offset from the grid's own origin on purpose, so a check that only
 * ever exercised {@code top == 0, left == 0} could not hide a translation bug the way earlier
 * revisions of {@link Region} did (a coordinate check compared against the region's own size
 * while the corresponding write skipped the {@code top}/{@code left} offset, or vice versa).
 * Every test therefore verifies the whole grid, not just the cells a call was meant to touch -
 * a leak outside the region's rectangle is exactly the bug this class exists to prevent.
 *
 * <p>No display is needed anywhere below: the whole point of keeping {@code grid} free of
 * {@code javax.swing} imports is that it, and the operations built on it, are testable headless.
 *
 * <p>Class RegionTest coded on 260909, commented in full on 260909.
 *
 * @author Rowan Crowther
 */
class RegionTest {

    private static final int GRID_ROWS = 8;
    private static final int GRID_COLS = 12;

    private static final int TOP = 2;
    private static final int LEFT = 3;
    private static final int REGION_ROWS = 4;
    private static final int REGION_COLS = 5;

    /**
     * The value every cell starts as, chosen to be nothing any method under test would ever
     * write, so a marker cell surviving a call is unambiguous evidence it was not touched.
     */
    private static final AngbandDisplayCharacter MARKER =
            new AngbandDisplayCharacter('#', ColourEnum.COLOUR_RED);

    /**
     * What an erased or cleared cell becomes, per {@link Region#erase} and {@link Region#clear}
     * both writing a space in {@link ColourEnum#COLOUR_WHITE}.
     */
    private static final AngbandDisplayCharacter BLANK =
            new AngbandDisplayCharacter(' ', ColourEnum.COLOUR_WHITE);

    private CellGrid grid;
    private Region region;

    @BeforeEach
    void buildAFullyMarkedGridAndAnOffsetRegion() {
        grid = new CellGrid(GRID_ROWS, GRID_COLS);
        for (int row = 0; row < GRID_ROWS; row++) {
            for (int col = 0; col < GRID_COLS; col++) {
                grid.set(row, col, MARKER);
            }
        }

        region = new Region(grid, TOP, LEFT, REGION_ROWS, REGION_COLS);
    }

    /**
     * Asserts every cell in {@link #grid} matches {@code changes}, defaulting to {@link #MARKER}
     * wherever {@code changes} says nothing - so a write that landed one cell away from where it
     * was expected fails here, rather than being missed by only checking the intended cell.
     *
     * @param changes the grid coordinates expected to differ from {@link #MARKER}, and what
     *                each should hold instead
     */
    private void assertGridMatches(Map<Coord, AngbandDisplayCharacter> changes) {
        for (int row = 0; row < GRID_ROWS; row++) {
            for (int col = 0; col < GRID_COLS; col++) {
                AngbandDisplayCharacter expected = changes.getOrDefault(new Coord(row, col), MARKER);
                assertEquals(expected, grid.get(row, col), "cell (" + row + ", " + col + ")");
            }
        }
    }

    /**
     * Asserts every cell within the rectangle ({@code top}, {@code left}) to
     * ({@code top + rows}, {@code left + cols}) is {@link #BLANK}, and every cell outside it is
     * still {@link #MARKER}.
     */
    private void assertRectangleBlankedRestMarked(int top, int left, int rows, int cols) {
        for (int row = 0; row < GRID_ROWS; row++) {
            for (int col = 0; col < GRID_COLS; col++) {
                boolean inRectangle = row >= top && row < top + rows && col >= left && col < left + cols;
                AngbandDisplayCharacter expected = inRectangle ? BLANK : MARKER;
                assertEquals(expected, grid.get(row, col), "cell (" + row + ", " + col + ")");
            }
        }
    }

    /**
     * A grid coordinate, used purely as a {@code Map} key for the cells a test expects to have
     * changed - everywhere else in the grid is expected to still hold {@link #MARKER}.
     */
    private record Coord(int row, int col) {
    }

    @Nested
    @DisplayName("put(row, col, char, colour)")
    class PutCharacter {

        /**
         * The ordinary path: a region-local coordinate is translated by ({@code top},
         * {@code left}) before it reaches the grid.
         */
        @Test
        @DisplayName("writes to the translated cell, and nowhere else")
        void writesToTheTranslatedCell() {
            region.put(1, 2, 'X', ColourEnum.COLOUR_GREEN);

            assertGridMatches(Map.of(
                    new Coord(TOP + 1, LEFT + 2), new AngbandDisplayCharacter('X', ColourEnum.COLOUR_GREEN)));
        }

        /**
         * The last region-local row and column are still in range - the bounds check must be
         * {@code >= rows}/{@code >= cols}, not one short of it.
         */
        @Test
        @DisplayName("the last valid cell in the region is writable")
        void lastValidCellIsWritable() {
            region.put(REGION_ROWS - 1, REGION_COLS - 1, 'X', ColourEnum.COLOUR_GREEN);

            assertGridMatches(Map.of(
                    new Coord(TOP + REGION_ROWS - 1, LEFT + REGION_COLS - 1),
                    new AngbandDisplayCharacter('X', ColourEnum.COLOUR_GREEN)));
        }

        @Test
        @DisplayName("a negative row is a no-op")
        void negativeRowIsANoOp() {
            region.put(-1, 0, 'X', ColourEnum.COLOUR_GREEN);

            assertGridMatches(Map.of());
        }

        @Test
        @DisplayName("a row at the region's height is a no-op")
        void rowAtRegionHeightIsANoOp() {
            region.put(REGION_ROWS, 0, 'X', ColourEnum.COLOUR_GREEN);

            assertGridMatches(Map.of());
        }

        @Test
        @DisplayName("a negative column is a no-op")
        void negativeColumnIsANoOp() {
            region.put(0, -1, 'X', ColourEnum.COLOUR_GREEN);

            assertGridMatches(Map.of());
        }

        @Test
        @DisplayName("a column at the region's width is a no-op")
        void columnAtRegionWidthIsANoOp() {
            region.put(0, REGION_COLS, 'X', ColourEnum.COLOUR_GREEN);

            assertGridMatches(Map.of());
        }
    }

    @Nested
    @DisplayName("put(row, col, String, colour)")
    class PutString {

        @Test
        @DisplayName("writes a string starting at the given column")
        void writesStringStartingAtGivenColumn() {
            region.put(0, 1, "Hi", ColourEnum.COLOUR_GREEN);

            assertGridMatches(Map.of(
                    new Coord(TOP, LEFT + 1), new AngbandDisplayCharacter('H', ColourEnum.COLOUR_GREEN),
                    new Coord(TOP, LEFT + 2), new AngbandDisplayCharacter('i', ColourEnum.COLOUR_GREEN)));
        }

        @Test
        @DisplayName("a negative row draws nothing")
        void negativeRowDrawsNothing() {
            region.put(-1, 0, "Hi", ColourEnum.COLOUR_GREEN);

            assertGridMatches(Map.of());
        }

        @Test
        @DisplayName("a row at the region's height draws nothing")
        void rowAtRegionHeightDrawsNothing() {
            region.put(REGION_ROWS, 0, "Hi", ColourEnum.COLOUR_GREEN);

            assertGridMatches(Map.of());
        }

        /**
         * The string is not discarded whole: its first {@code -col} characters are dropped, and
         * the remainder is drawn from column 0.
         */
        @Test
        @DisplayName("a negative column drops its leading characters and draws the remainder from column 0")
        void negativeColumnDropsLeadingCharacters() {
            region.put(0, -2, "Hello", ColourEnum.COLOUR_GREEN);

            assertGridMatches(Map.of(
                    new Coord(TOP, LEFT), new AngbandDisplayCharacter('l', ColourEnum.COLOUR_GREEN),
                    new Coord(TOP, LEFT + 1), new AngbandDisplayCharacter('l', ColourEnum.COLOUR_GREEN),
                    new Coord(TOP, LEFT + 2), new AngbandDisplayCharacter('o', ColourEnum.COLOUR_GREEN)));
        }

        @Test
        @DisplayName("a negative column that drops the whole string draws nothing")
        void negativeColumnDroppingWholeStringDrawsNothing() {
            region.put(0, -10, "Hi", ColourEnum.COLOUR_GREEN);

            assertGridMatches(Map.of());
        }

        @Test
        @DisplayName("a column already at the region's width draws nothing")
        void columnAtRegionWidthDrawsNothing() {
            region.put(0, REGION_COLS, "Hi", ColourEnum.COLOUR_GREEN);

            assertGridMatches(Map.of());
        }

        /**
         * A run overrunning the right edge draws only its first {@code cols - col} characters -
         * {@code REGION_COLS - 3 == 2} here.
         */
        @Test
        @DisplayName("a string overrunning the right edge is truncated, not rejected")
        void overrunningRightEdgeIsTruncated() {
            region.put(0, 3, "Hello", ColourEnum.COLOUR_GREEN);

            assertGridMatches(Map.of(
                    new Coord(TOP, LEFT + 3), new AngbandDisplayCharacter('H', ColourEnum.COLOUR_GREEN),
                    new Coord(TOP, LEFT + 4), new AngbandDisplayCharacter('e', ColourEnum.COLOUR_GREEN)));
        }

        /**
         * The negative-column rule and the right-edge rule apply to the same call in sequence:
         * dropping the first 3 characters of an 11-character string still leaves 8, which still
         * overruns a 5-wide region and is truncated a second time down to 5.
         */
        @Test
        @DisplayName("the negative-column rule and the right-edge rule can both apply to the same call")
        void bothClipRulesCanApplyToSameCall() {
            region.put(0, -3, "Hello World", ColourEnum.COLOUR_GREEN);

            assertGridMatches(Map.of(
                    new Coord(TOP, LEFT), new AngbandDisplayCharacter('l', ColourEnum.COLOUR_GREEN),
                    new Coord(TOP, LEFT + 1), new AngbandDisplayCharacter('o', ColourEnum.COLOUR_GREEN),
                    new Coord(TOP, LEFT + 2), new AngbandDisplayCharacter(' ', ColourEnum.COLOUR_GREEN),
                    new Coord(TOP, LEFT + 3), new AngbandDisplayCharacter('W', ColourEnum.COLOUR_GREEN),
                    new Coord(TOP, LEFT + 4), new AngbandDisplayCharacter('o', ColourEnum.COLOUR_GREEN)));
        }
    }

    @Nested
    @DisplayName("erase(row, col, n)")
    class Erase {

        @Test
        @DisplayName("erases only the requested run")
        void erasesOnlyTheRequestedRun() {
            region.erase(1, 1, 3);

            assertGridMatches(Map.of(
                    new Coord(TOP + 1, LEFT + 1), BLANK,
                    new Coord(TOP + 1, LEFT + 2), BLANK,
                    new Coord(TOP + 1, LEFT + 3), BLANK));
        }

        /**
         * A run overrunning the region's right edge is shortened to what fits -
         * {@code REGION_COLS - 3 == 2} cells here - not rejected outright.
         */
        @Test
        @DisplayName("a run past the right edge is clamped, not rejected")
        void aRunPastTheRightEdgeIsClampedNotRejected() {
            region.erase(0, 3, 10);

            assertGridMatches(Map.of(
                    new Coord(TOP, LEFT + 3), BLANK,
                    new Coord(TOP, LEFT + 4), BLANK));
        }

        @Test
        @DisplayName("the last column can be erased alone")
        void theLastColumnCanBeErasedAlone() {
            region.erase(2, REGION_COLS - 1, 1);

            assertGridMatches(Map.of(new Coord(TOP + 2, LEFT + REGION_COLS - 1), BLANK));
        }

        @Test
        @DisplayName("a full-width run erases the entire row of the region")
        void aFullWidthRunErasesTheEntireRegionRow() {
            region.erase(3, 0, REGION_COLS);

            assertRectangleBlankedRestMarked(TOP + 3, LEFT, 1, REGION_COLS);
        }

        @Test
        @DisplayName("n == 0 erases nothing")
        void zeroCellsErasesNothing() {
            region.erase(0, 0, 0);

            assertGridMatches(Map.of());
        }

        @Test
        @DisplayName("a negative n erases nothing")
        void negativeCellCountErasesNothing() {
            region.erase(0, 0, -5);

            assertGridMatches(Map.of());
        }

        /**
         * Deliberately different from the C-derived {@code JPanelArea.erase}/{@code Term_erase},
         * which reject a negative column outright: {@link Region#erase} clips it instead, the
         * same as {@link Region#put(int, int, String, ColourEnum)} does for a negative column -
         * the overhanging cells are dropped, and the remainder, if any, is still erased.
         */
        @Test
        @DisplayName("a negative column drops the overhanging cells instead of being rejected")
        void negativeColumnDropsTheOverhangInsteadOfBeingRejected() {
            region.erase(1, -2, 5);

            assertGridMatches(Map.of(
                    new Coord(TOP + 1, LEFT), BLANK,
                    new Coord(TOP + 1, LEFT + 1), BLANK,
                    new Coord(TOP + 1, LEFT + 2), BLANK));
        }

        @Test
        @DisplayName("a column entirely off the left of the region erases nothing")
        void columnEntirelyOffTheLeftErasesNothing() {
            region.erase(1, -10, 3);

            assertGridMatches(Map.of());
        }

        @Test
        @DisplayName("a negative row erases nothing")
        void negativeRowErasesNothing() {
            region.erase(-1, 0, 3);

            assertGridMatches(Map.of());
        }

        @Test
        @DisplayName("a row at the region's height erases nothing")
        void rowAtRegionHeightErasesNothing() {
            region.erase(REGION_ROWS, 0, 3);

            assertGridMatches(Map.of());
        }

        @Test
        @DisplayName("a column at the region's width erases nothing")
        void columnAtRegionWidthErasesNothing() {
            region.erase(0, REGION_COLS, 3);

            assertGridMatches(Map.of());
        }
    }

    @Nested
    @DisplayName("clear()")
    class Clear {

        /**
         * Every cell in the region's rectangle is blanked, and - since the rectangle here is
         * offset from the grid's own origin - nothing outside it is touched.
         */
        @Test
        @DisplayName("blanks every cell in the region, and no cell outside it")
        void blanksEveryCellInTheRegionOnly() {
            region.clear();

            assertRectangleBlankedRestMarked(TOP, LEFT, REGION_ROWS, REGION_COLS);
        }
    }
}
