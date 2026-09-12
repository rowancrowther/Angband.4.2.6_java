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
 * ({@code ui-output.h:35-41}).
 *
 * <p>The struct's four fields carry the same conditional meanings here that they do in C, but this
 * class only stores them; it does no interpreting. {@link #width} of {@code 1} means "use the system
 * default" and a non-positive value means "relative to the right of the screen"; a non-positive
 * {@link #pageRows} means "relative to the bottom of the screen". Resolving those relative values to
 * absolute ones - what C's own comment on {@code region_calculate} describes - is a boundary this
 * class does not own.
 *
 * <p>Class Region coded on 260911, commented in full on 260911.
 *
 * @author Rowan Crowther
 */
public class Region {
    public static final Region ScreenRegion = new Region(0, 0, 0, 0);

    /**
     * The x-coordinate of the region's corner - the port of C's {@code col} ({@code
     * ui-output.h:36}).
     *
     * <p>Field col coded on 260911, commented in full on 260911.
     */
    private int col;

    /**
     * The y-coordinate of the region's corner - the port of C's {@code row} ({@code
     * ui-output.h:37}).
     *
     * <p>Field row coded on 260911, commented in full on 260911.
     */
    private int row;

    /**
     * The width of the display area - the port of C's {@code width} ({@code ui-output.h:38}). A
     * value of {@code 1} means "use the system default"; a non-positive value is relative to the
     * right of the screen rather than an absolute width.
     *
     * <p>Field width coded on 260911, commented in full on 260911.
     */
    private int width;

    /**
     * The number of rows in the region's page - the port of C's {@code page_rows} ({@code
     * ui-output.h:39-40}). A non-positive value is relative to the bottom of the screen rather than
     * an absolute row count.
     *
     * <p>Field pageRows coded on 260911, commented in full on 260911.
     */
    private int pageRows;

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
}
