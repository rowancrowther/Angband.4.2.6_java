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

import uk.co.jackoftradesltd.channel.strings.AngbandDisplayCharacter;

import java.util.Arrays;

/**
 * The screen's data — a plain grid of coloured cells, with no Swing and no drawing logic of its own.
 * One of three deliberately separated concerns recorded in {@code docs/UIPanelArchitecture.md}: this
 * class is the data, {@code Region} is the operations built on it, and a Swing panel is the
 * presentation, which only ever reads a copy.
 *
 * <p>Addressing is {@code [row][col]}, matching C's {@code term_win} convention that "the attr/char
 * pair at (x,y) is a[y][x]/c[y][x]" ({@code ui-term.h}). This class is not a port of {@code term_win}
 * though — it carries no attribute/character split, no cursor state, no dirty-row tracking, and no
 * diffing against a previous frame. The design doc records that departure as deliberate: Swing
 * repaints a whole panel cheaply, and the diffing machinery is where much of {@code ui-term.c}'s
 * complexity lives.
 *
 * <p>{@link #set} is the only primitive that writes a cell; everything else — drawing a string,
 * erasing a run, clearing a box — is built on it one boundary up, in {@code Region}.
 *
 * <p>Class CellGrid coded on 260909, commented in full on 260909.
 *
 * @author Rowan Crowther
 */
public class CellGrid {
    /**
     * The backing store, addressed {@code [row][col]}. Every cell is {@code null} until written by
     * {@link #set}.
     */
    private final AngbandDisplayCharacter[][] cells;

    /**
     * A new grid of the given size, every cell left {@code null}.
     *
     * <p>Cells are not pre-filled with a blank space the way {@code JPanelArea}'s constructor fills
     * its backing array ({@code SwingUI.java}) — a grid built here starts empty and stays that way
     * until something calls {@link #set}.
     *
     * <p><b>Outstanding:</b> the design doc flags the lack of pre-fill as an open gap against the
     * panel this class is migrating away from — a visible difference on first paint (blank cells
     * fall back to a dark colour rather than showing as white space) unless the caller fills the grid
     * before it is first shown.
     *
     * <p>Constructor CellGrid coded on 260909, commented in full on 260909.
     *
     * @param rows number of rows in the grid
     * @param cols number of columns in the grid
     */
    public CellGrid(int rows, int cols) {
        cells = new AngbandDisplayCharacter[rows][cols];
    }

    /**
     * The number of rows in this grid, read off the backing array's own length rather than a stored
     * field.
     *
     * <p>Method rows coded on 260909, commented in full on 260909.
     *
     * @return the row count
     */
    public int rows() {
        return cells.length;
    }

    /**
     * The number of columns in this grid, read off the first row's length rather than a stored field.
     *
     * <p>No guard against a zero-row grid: the grid is always constructed at a fixed size and is
     * never expected to be empty, so a guard here would turn a real bug — a zero-row grid — into a
     * silent {@code 0} instead of the {@link ArrayIndexOutOfBoundsException} that would expose it.
     *
     * <p>Method cols coded on 260909, commented in full on 260909.
     *
     * @return the column count
     */
    public int cols() {
        return cells[0].length;
    }

    /**
     * The cell at {@code (row, col)}, or {@code null} if that coordinate falls outside the grid.
     *
     * <p>Out of range is not exceptional here the way it is for {@link #set} — a read past the edge
     * answers "nothing there" rather than signalling a defect, which is what lets a caller such as
     * {@code Region} probe a coordinate without a bounds check of its own.
     *
     * <p>Method get coded on 260909, commented in full on 260909.
     *
     * @param row the row to read
     * @param col the column to read
     * @return the cell at that coordinate, or {@code null} if out of range
     */
    public AngbandDisplayCharacter get(int row, int col) {
        if (row < 0 || row >= cells.length || col < 0 || col >= cells[0].length)
            return null;

        return cells[row][col];
    }

    /**
     * Writes a cell at {@code (row, col)}.
     *
     * <p>Throws rather than clips. Clipping is a promise the public drawing API ({@code Region})
     * makes to its callers — the game prints overrunning strings constantly, so silently dropping the
     * overrun is the common case there, not an error. By the time a call reaches this method the
     * region arithmetic has already run, so an out-of-range coordinate here is a defect in that
     * arithmetic rather than a normal case; clipping it too would trade a loud failure for a blank
     * screen with nothing to point at.
     *
     * <p>Method set coded on 260909, commented in full on 260909.
     *
     * @param row       the row to write
     * @param col       the column to write
     * @param character the cell to store
     * @throws IndexOutOfBoundsException if {@code row} or {@code col} falls outside the grid
     */
    public void set(int row, int col, AngbandDisplayCharacter character) {
        if (row < 0 || row >= rows() || col < 0 || col >= cols())
            throw new IndexOutOfBoundsException();

        cells[row][col] = character;
    }

    /**
     * An independent copy of this grid, safe to hand to another thread while this one keeps mutating
     * its own.
     *
     * <p>Structural, not deep: a new outer array and a new array per row, but the same
     * {@link AngbandDisplayCharacter} references inside them. That is enough because
     * {@code AngbandDisplayCharacter} is itself immutable — both its fields are {@code final} with no
     * setters — so sharing an instance between two grids can never let a mutation on one reach the
     * other. Cloning the cells themselves would only add cost for no extra safety.
     *
     * <p>This is the guarantee {@code Screen.frame()} rests on: publishing a copy of the live grid to
     * the EDT, rather than the live grid itself, is what keeps the EDT from ever painting a screen the
     * UI thread is still mid-way through writing.
     *
     * <p>Method copy coded on 260909, commented in full on 260909.
     *
     * @return a structurally independent copy of this grid
     */
    public CellGrid copy() {
        CellGrid copy = new CellGrid(cells.length, cells[0].length);
        for (int row = 0; row < cells.length; row++) {
            copy.cells[row] = Arrays.copyOf(cells[row], cells[row].length);
        }

        return copy;
    }

    /**
     * This grid's cells as a plain array, for a caller on the far side of the Swing boundary
     * that needs a raw {@code [row][col]} array rather than a {@code CellGrid} — {@code Window}
     * is the one caller today, handing the result straight to
     * {@code SwingUI.JPanelArea#setChars}.
     *
     * <p>Shallower than {@link #copy}. {@link Object#clone()} on a two-dimensional array
     * duplicates only the outer array — the rows in the returned array are the same row-array
     * instances this grid holds internally, not independent copies the way {@link #copy}
     * produces. That is safe today only because the one caller, {@code Window#show}, always
     * reads it off a {@link Frame}'s grid, and a {@code Frame}'s grid is built by
     * {@code Screen#frame()} with {@link #copy} and never written to again once published — so
     * the rows this method hands out are never live rows some other thread might still be
     * writing through {@link #set}. That safety is the caller's discipline, not a guarantee this
     * method makes itself.
     *
     * <p><b>Outstanding:</b> nothing here stops a future caller from calling this on a grid still
     * being written to. Because the rows are shared, a later {@link #set} on this grid would
     * write into the very array the caller is holding, mid-repaint — the exact hazard {@link
     * #copy} and {@link Frame}'s defensive-copy contract exist to rule out.
     *
     * <p>Method getCells coded on 260909, commented in full on 260910.
     *
     * @return this grid's cells as a plain array; the outer array is independent of this grid's
     * own, but each row is shared, not copied
     */
    public AngbandDisplayCharacter[][] getCells() {
        return cells.clone();
    }
}
