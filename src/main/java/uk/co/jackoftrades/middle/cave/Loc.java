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

package uk.co.jackoftrades.middle.cave;

import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftrades.backend.numerics.RandomValueUtils;
import uk.co.jackoftrades.middle.cave.enums.DirectionEnum;

/**
 * An immutable-ish grid coordinate (column {@code x}, row {@code y}) in the
 * dungeon. This is the Java port of the C original's {@code struct loc}
 * ({@code src/loc.h}); the helper methods ({@link #sum}, {@link #diff},
 * {@link #nextGrid}, {@link #randLoc}, {@link #locOffset}) reproduce the
 * {@code loc_*} coordinate arithmetic used throughout level generation and
 * movement.
 *
 * @author ClaudeCode
 */
public class Loc {
    /**
     * Column (horizontal) coordinate.
     *
     * @author ClaudeCode
     */
    private int x;
    /**
     * Row (vertical) coordinate.
     *
     * @author ClaudeCode
     */
    private int y;

    /**
     * Quick pointer to a location which is the origin (0, 0)
     */
    public static Loc zero = new Loc(0, 0);

    /**
     * Constructor
     *
     * @param x the x coordinate of this location
     * @param y the y coordinate of this location
     */
    public Loc(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Getter for y
     *
     * @return the current value of y
     */
    public int getY() {
        return y;
    }

    /**
     * Getter for x
     *
     * @return the current value of x
     */
    public int getX() {
        return x;
    }

    /**
     * Returns a grid which is the result of moving one grid in the given direction
     *
     * @param direction The direction enum of the direction to move in
     * @return a new grid one step away from this grid in the given direction
     */
    @CheckReturnValue
    @Contract(pure = true)
    public Loc nextGrid(DirectionEnum direction) {
        return new Loc(this.x + direction.ddx(), this.y + direction.ddy());
    }

    /**
     * Returns true if the incoming location is equivalent to this one, false otherwise
     *
     * @param other the incoming location to compare with this one
     * @return true if these two locations are equivalent, false otherwise
     */
    public boolean equals(@NotNull Loc other) {
        return this.x == other.x && this.y == other.y;
    }

    /**
     * Determines if this location is the origin location (0, 0)
     *
     * @return true if this is equivalent to the origin location, false otherwise
     */
    public boolean isZero() {
        return equals(zero);
    }

    /**
     * Adds two locations together and return the result
     *
     * @param other The location to sum with this one
     * @return A location which consists of (x1 + x2, y1 + y2)
     */
    public Loc sum(@NotNull Loc other) {
        return new Loc(x + other.x, y + other.y);
    }

    /**
     * Create a location which is the difference between the two points this (x1, y1) and other (x2, y2)
     *
     * @param other the other point to work out the difference from this point
     * @return A new location (x1 - x2, y1 - y2).
     */
    public Loc diff(@NotNull Loc other) {
        return new Loc(this.x - other.x, this.y - other.y);
    }

    /**
     * Create a random location with the given spread variables on x and y
     *
     * @param xSpread The x spread value - new value should be between this.x - xSpread and this.x + xSpread
     * @param ySpread The y spread value - new value should be between this.y - ySpread and this.y + ySpread
     * @return A random location where new.x is between this.x - xSpread and this.x + xSpread
     * and new.y is between this.y - ySpread and this.y + ySpread
     */
    public Loc randLoc(int xSpread, int ySpread) {
        return new Loc(RandomValueUtils.randSpread(x, xSpread), RandomValueUtils.randSpread(y, ySpread));
    }

    /**
     * Returns a new location offset from this location by dx and dy
     *
     * @param dx The amount that the x coordinate is offset
     * @param dy The amount that the y coordinate is offset
     * @return A new location of the form (x + dx, y + dy)
     */
    public Loc locOffset(int dx, int dy) {
        return new Loc(x + dx, y + dy);
    }
}