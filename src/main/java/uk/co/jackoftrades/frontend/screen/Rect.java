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
 *    Java code copyright (c) Rowan Crowther 2026
 */

package uk.co.jackoftrades.frontend.screen;

/**
 * An immutable axis-aligned rectangle described by its four edge coordinates,
 * used by the screen/terminal layer to express regions of the display. Mirrors
 * the platform {@code RECT} structure used by the original C front ends; edges
 * are stored as {@code long} to match those native coordinate types.
 *
 * @author ClaudeCode
 */
public class Rect {
    /**
     * X coordinate of the left edge.
     *
     * @author ClaudeCode
     */
    private long left;
    /**
     * Y coordinate of the top edge.
     *
     * @author ClaudeCode
     */
    private long top;
    /**
     * X coordinate of the right edge.
     *
     * @author ClaudeCode
     */
    private long right;
    /**
     * Y coordinate of the bottom edge.
     *
     * @author ClaudeCode
     */
    private long bottom;

    /**
     * Build a rectangle from its four edge coordinates.
     *
     * @param left   left edge X
     * @param top    top edge Y
     * @param right  right edge X
     * @param bottom bottom edge Y
     * @author ClaudeCode
     */
    public Rect(long left, long top, long right, long bottom) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }

    /**
     * @return the left edge X coordinate
     * @author ClaudeCode
     */
    public long getLeft() {
        return left;
    }

    /**
     * @return the top edge Y coordinate
     * @author ClaudeCode
     */
    public long getTop() {
        return top;
    }

    /**
     * @return the right edge X coordinate
     * @author ClaudeCode
     */
    public long getRight() {
        return right;
    }

    /**
     * @return the bottom edge Y coordinate
     * @author ClaudeCode
     */
    public long getBottom() {
        return bottom;
    }
}