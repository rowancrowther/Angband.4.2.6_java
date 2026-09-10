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

import uk.co.jackoftradesltd.channel.colour.ColourEnum;
import uk.co.jackoftradesltd.channel.strings.AngbandDisplayCharacter;

/**
 * A rectangle into a {@link CellGrid} that translates and clips coordinates - the operations
 * layer of the drawing architecture set out in {@code docs/UIPanelArchitecture.md}. No C
 * original: this class is new architecture, not a port.
 *
 * <p>The point of a region is that "draw a portion of the screen" becomes "hand an event a
 * region and let it draw at 0,0" - the event never learns where on screen it sits, and cannot
 * scribble outside its box. Every public method here clips silently rather than throwing on an
 * out-of-range row or column, since the game prints overrunning strings constantly and clipping
 * is the common case, not an error. {@link CellGrid#set} is the layer that throws instead -
 * by the time a coordinate reaches it, the arithmetic is already done, so an out-of-range call
 * there is a defect in this class, not a normal case.
 *
 * <p>There is no {@code sub()} method and no nesting: a region is a flat rectangle into the
 * grid.
 *
 * <p>Class Region coded on 260909, commented in full on 260909.
 */
public final class Region {
    /**
     * The grid this region writes into. Coordinates passed to this class are region-local;
     * they are translated by {@link #top}/{@link #left} only at the point of writing here.
     *
     * <p>Field grid coded on 260909, commented in full on 260909.
     */
    private final CellGrid grid;
    /**
     * This region's placement and extent within {@link #grid}: {@code top}/{@code left} are
     * the offset of this region's (0,0) within the grid, and {@code rows}/{@code cols} are
     * this region's own size, which every clip check is measured against - not the grid's.
     *
     * <p>Field top, left, rows, cols coded on 260909, commented in full on 260909.
     */
    private final int top;
    private final int left;
    private final int rows;
    private final int cols;

    /**
     * Builds a region occupying the rectangle ({@code top}, {@code left}) to
     * ({@code top + rows}, {@code left + cols}) of {@code grid}. Nothing here validates that
     * rectangle against the grid's own size - that is the caller's responsibility, as with
     * {@link Screen#root()}, which sizes a region to exactly match its grid.
     *
     * <p>Constructor coded on 260909, commented in full on 260909.
     *
     * @param grid the grid this region writes into
     * @param top  this region's row offset within {@code grid}
     * @param left this region's column offset within {@code grid}
     * @param rows this region's height, in region-local rows
     * @param cols this region's width, in region-local columns
     */
    public Region(CellGrid grid, int top, int left, int rows, int cols) {
        this.grid = grid;
        this.top = top;
        this.left = left;
        this.rows = rows;
        this.cols = cols;
    }

    /**
     * The primitive write. Bounds-checks {@code row}/{@code col} in this region's own,
     * untranslated coordinates against {@link #rows}/{@link #cols}, and translates by
     * {@link #top}/{@link #left} only at the {@link CellGrid#set} call - never before, so the
     * check and the write agree on which rectangle they mean.
     *
     * <p>An out-of-range {@code row} or {@code col} draws nothing at all rather than throwing;
     * clipping is this class's whole job, so a caller handing in a coordinate outside this
     * region's rectangle is a normal case, not a bug.
     *
     * <p>Function put coded on 260909, commented in full on 260909.
     *
     * @param row       the row to write to, in this region's own coordinates
     * @param col       the column to write to, in this region's own coordinates
     * @param character the character to write
     */
    public void put(int row, int col, AngbandDisplayCharacter character) {
        if (row >= rows || row < 0 || col >= cols || col < 0) return;

        grid.set(row + top, col + left, character);
    }

    /**
     * Writes a single glyph in the given colour - a convenience wrapper that builds the
     * {@link AngbandDisplayCharacter} and delegates to {@link #put(int, int, AngbandDisplayCharacter)}
     * for the actual bounds-checked write.
     *
     * <p>Function put coded on 260909, commented in full on 260909.
     *
     * @param row    the row to write to, in this region's own coordinates
     * @param col    the column to write to, in this region's own coordinates
     * @param glyph  the character to display
     * @param colour the colour to display it in
     */
    public void put(int row, int col, char glyph, ColourEnum colour) {
        AngbandDisplayCharacter character = new AngbandDisplayCharacter(glyph, colour);
        put(row, col, character);
    }

    /**
     * Writes a string starting at ({@code row}, {@code col}), clipping it to this region's
     * rectangle rather than rejecting the call. The clip contract, in full:
     *
     * <ul>
     *     <li>A bad {@code row} - negative, or at or past {@link #rows} - draws nothing at all.
     *     There is no wrapping to the next line.</li>
     *     <li>A string starting at a negative {@code col} drops its first {@code -col}
     *     characters and draws the remainder from column 0; it is not discarded whole. If the
     *     whole string would be dropped, nothing is drawn.</li>
     *     <li>A {@code col} already at or past {@link #cols} draws nothing.</li>
     *     <li>A string overrunning the right edge draws only its first {@code cols - col}
     *     characters.</li>
     *     <li>The negative-{@code col} rule and the right-edge rule can both apply to the same
     *     call, in that order.</li>
     * </ul>
     *
     * <p>Function put coded on 260909, commented in full on 260909.
     *
     * @param row    the row to write to, in this region's own coordinates
     * @param col    the column the string starts at, in this region's own coordinates
     * @param s      the string to write
     * @param colour the colour to display it in
     */
    public void put(int row, int col, String s, ColourEnum colour) {
        String toDisplay = s;

        // Is it out of bounds horizontally
        if (row < 0 || row >= rows) return;

        // Is the string starting before the screen
        if (col < 0) {
            if (toDisplay.length() + col < 0) return;
            toDisplay = toDisplay.substring(-col);
            col = 0;
        }

        // Is the string starting after then end of the screen
        if (col >= cols) return;

        // Is the string too long for the screen or dropping off the end
        if (col + toDisplay.length() > cols) {
            toDisplay = toDisplay.substring(0, cols - col);
        }

        int stringIndex = 0;
        for (int i = col; i < cols && stringIndex < toDisplay.length(); i++) {
            put(row, i, new AngbandDisplayCharacter(toDisplay.charAt(stringIndex), colour));
            stringIndex++;
        }
    }

    /**
     * Blanks {@code n} cells starting at ({@code row}, {@code col}), by writing a string of
     * {@code n} spaces through {@link #put(int, int, String, ColourEnum)} - so it inherits that
     * method's clip contract for free: a negative {@code col} drops its overhanging cells, and
     * a run overrunning the right edge is clamped to {@code cols - col}.
     *
     * <p>{@code n} at or below zero draws nothing, checked here rather than left to
     * {@code " ".repeat}, which throws on a negative count.
     *
     * <p>Function erase coded on 260909, commented in full on 260909.
     *
     * @param row the row to erase, in this region's own coordinates
     * @param col the column the erased run starts at, in this region's own coordinates
     * @param n   how many cells to blank
     */
    public void erase(int row, int col, int n) {
        // check bounds
        if (n <= 0) return;

        put(row, col, " ".repeat(n), ColourEnum.COLOUR_WHITE);
    }

    /**
     * Blanks every cell in this region, by writing a space through every ({@code row},
     * {@code col}) pair in its rectangle.
     *
     * <p>Function clear coded on 260909, commented in full on 260909.
     */
    public void clear() {
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                put(row, col, ' ', ColourEnum.COLOUR_WHITE);
            }
        }
    }

    public int cols() {
        return cols;
    }

    public int rows() {
        return rows;
    }
}
