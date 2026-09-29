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

package uk.co.jackoftradesltd.frontend.ui.output;

/**
 * A rectangle on the screen bound to a panel or subpanel - the port of C's {@code struct region}
 * in {@code ui-output.h}.
 *
 * <p>The struct's four fields carry the same conditional meanings here that they do in C, but this
 * class only stores them; it does no interpreting. {@link #width} of {@code 1} means "use the system
 * default" and a non-positive value means "relative to the right of the screen"; a non-positive
 * {@link #pageRows} means "relative to the bottom of the screen". Resolving those relative values to
 * absolute ones - what C's own comment on {@code region_calculate} describes - is a boundary this
 * class does not own.
 *
 * <p>The class is mutable, as an unqualified C {@code struct region} is, so callers may adjust a
 * region in place through the setters. The one C region that is {@code const}
 * is {@code SCREEN_REGION}, which is why {@link #screenRegion()} is a factory rather than a shared
 * instance.
 *
 * <p>Class Region coded on 260911, commented in full on 260929.
 *
 * @author Rowan Crowther
 */
public class Region {
    /**
     * The x-coordinate of the region's corner - the port of C's {@code col} in
     * {@code struct region}.
     *
     * <p>Field col coded on 260911, commented in full on 260929.
     */
    private int col;
    /**
     * The y-coordinate of the region's corner - the port of C's {@code row} in
     * {@code struct region}.
     *
     * <p>Field row coded on 260911, commented in full on 260929.
     */
    private int row;
    /**
     * The width of the display area - the port of C's {@code width} in {@code struct region}. A
     * value of {@code 1} means "use the system default"; a non-positive value is relative to the
     * right of the screen rather than an absolute width.
     *
     * <p>Field width coded on 260911, commented in full on 260929.
     */
    private int width;
    /**
     * The number of rows in the region's page - the port of C's {@code page_rows} in
     * {@code struct region}. A non-positive value is relative to the bottom of the screen rather
     * than an absolute row count.
     *
     * <p>Field pageRows coded on 260911, commented in full on 260929.
     */
    private int pageRows;

    /**
     * Returns a new region covering the full screen - the port of C's {@code SCREEN_REGION}
     * ({@code static const region SCREEN_REGION = {0, 0, 0, 0}} in {@code ui-output.h}). C's is
     * read-only, and each translation unit gets its own copy; a shared Java instance would be
     * writable through the setters, so every call builds a fresh {@code Region(0, 0, 0, 0)} that a
     * caller may alter without affecting anyone else. All four zeros are the "relative" values, so
     * the region resolves to the whole screen once {@code region_calculate} is applied.
     *
     * <p>Method screenRegion coded on 260929, commented in full on 260929.
     *
     * @return a new full-screen region
     */
    public static final Region screenRegion() {
        return new Region(0, 0, 0, 0);
    }

    /**
     * Builds a region from its four raw field values, stored exactly as given - the port of a C
     * {@code struct region} literal or field-by-field initialisation. No validation or resolution
     * of relative {@link #width}/{@link #pageRows} values happens here; see the class Javadoc.
     *
     * <p>Constructor Region coded on 260911, commented in full on 260911.
     *
     * @param col      the x-coordinate of the region's corner
     * @param row      the y-coordinate of the region's corner
     * @param width    the display width, {@code 1} for system default, non-positive for relative to
     *                 the right of the screen
     * @param pageRows the page row count, non-positive for relative to the bottom of the screen
     */
    public Region(int col, int row, int width, int pageRows) {
        this.col = col;
        this.row = row;
        this.width = width;
        this.pageRows = pageRows;
    }

    /**
     * Method getCol coded on 260911, commented in full on 260911.
     *
     * @return the x-coordinate of the region's corner
     */
    public int getCol() {
        return col;
    }

    /**
     * Method getRow coded on 260911, commented in full on 260911.
     *
     * @return the y-coordinate of the region's corner
     */
    public int getRow() {
        return row;
    }

    /**
     * Method getWidth coded on 260911, commented in full on 260911.
     *
     * @return the raw display width, as stored - {@code 1} for system default, non-positive for
     * relative to the right of the screen
     */
    public int getWidth() {
        return width;
    }

    /**
     * Method getPageRows coded on 260911, commented in full on 260911.
     *
     * @return the raw page row count, as stored - non-positive for relative to the bottom of the
     * screen
     */
    public int getPageRows() {
        return pageRows;
    }

    /**
     * Method setCol coded on 260929, commented in full on 260929.
     *
     * @param col the new x-coordinate of the region's corner, stored as given
     */
    public void setCol(int col) {
        this.col = col;
    }

    /**
     * Method setRow coded on 260929, commented in full on 260929.
     *
     * @param row the new y-coordinate of the region's corner, stored as given
     */
    public void setRow(int row) {
        this.row = row;
    }

    /**
     * Method setWidth coded on 260929, commented in full on 260929.
     *
     * @param width the new display width, stored as given - {@code 1} for system default,
     *              non-positive for relative to the right of the screen
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * Method setPageRows coded on 260929, commented in full on 260929.
     *
     * @param pageRows the new page row count, stored as given - non-positive for relative to the
     *                 bottom of the screen
     */
    public void setPageRows(int pageRows) {
        this.pageRows = pageRows;
    }
}
