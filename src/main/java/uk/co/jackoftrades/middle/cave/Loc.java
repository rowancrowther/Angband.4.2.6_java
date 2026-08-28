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

package uk.co.jackoftrades.middle.cave;

import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftrades.middle.numerics.RandomValueUtils;
import uk.co.jackoftrades.middle.cave.enums.DirectionEnum;

/**
 * An immutable grid coordinate (column {@code x}, row {@code y}) in the
 * dungeon. This is the Java port of the C original's {@code struct loc}
 * ({@code src/z-type.h}); the helper methods ({@link #sum}, {@link #diff},
 * {@link #nextGrid}, {@link #rand}, {@link #offset}) reproduce the
 * {@code loc_*} coordinate arithmetic used throughout level generation and
 * movement.
 * <p>
 * Both coordinates are final and every operation returns a new instance rather than
 * mutating in place, so a {@code Loc} is safe to use as a key in a hashed collection:
 * it cannot change identity after being stored.
 *
 * @author Rowan Crowther
 */
public class Loc {
    /**
     * Column (horizontal) coordinate.
     */
    private final int x;
    /**
     * Row (vertical) coordinate.
     */
    private final int y;

    /**
     * The origin (0, 0), shared rather than reallocated at each use. Safe to share
     * because {@link Loc} is immutable.
     * <p>
     * Note that much of the C original overloads (0, 0) to mean "no location" as well as
     * the literal top-left grid — see {@link #isZero()} and the {@code loc_is_zero} checks
     * it ports. Treat a bare {@code zero} as a sentinel with care.
     */
    public static final Loc zero = new Loc(0, 0);

    /**
     * Private because instances are obtained through the fluent {@link #row(int)} idiom or
     * derived from an existing location, which keeps the (x, y) argument order from being
     * transposed at the call site — a standing hazard in the C original, where
     * {@code loc(x, y)} takes column first but the grids it indexes are row-major.
     *
     * @param x the x coordinate (column) of this location
     * @param y the y coordinate (row) of this location
     */
    private Loc(int x, int y) {
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
     * Returns a grid which is the result of moving one grid in the given direction.
     * Ports {@code next_grid} ({@code src/cave.h}), with the C {@code int dir} replaced by
     * {@link DirectionEnum} so an out-of-range direction cannot be passed.
     * <p>
     * Performs no bounds checking — the result may lie outside the dungeon, so callers
     * stepping towards an edge must test it before indexing a grid array.
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
     * Value equality: two locations are equal exactly when both coordinates match.
     * <p>
     * This is the port of {@code loc_eq} ({@code src/z-type.c}). Overriding it — rather
     * than merely overloading {@code equals(Loc)} — is what makes {@code Loc} usable in
     * collections at all, because {@code List.contains}, {@code HashSet} and
     * {@code HashMap} all dispatch through {@code equals(Object)}. An overload is
     * invisible to them, and they would silently fall back to reference identity.
     *
     * @param obj the object to compare against this location; may be null
     * @return true if {@code obj} is a {@link Loc} with the same {@code x} and {@code y};
     * false otherwise, including when {@code obj} is null or of an unrelated type
     * @see #hashCode()
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Loc other) {
            return this.x == other.x && this.y == other.y;
        }
        return false;
    }

    /**
     * Determines if this location is the origin location (0, 0). Ports
     * {@code loc_is_zero} ({@code src/z-type.c}), which likewise defers to an equality
     * check against the origin rather than testing the fields directly.
     *
     * @return true if this is equivalent to the origin location, false otherwise
     */
    public boolean isZero() {
        return equals(zero);
    }

    /**
     * Adds two locations together and returns the result. Ports {@code loc_sum}
     * ({@code src/z-type.c}).
     * <p>
     * The second operand is usually an offset rather than a position — this is how the C
     * applies the {@code ddgrid} direction deltas when walking a grid's eight neighbours.
     *
     * @param other The location to sum with this one
     * @return A location which consists of (x1 + x2, y1 + y2)
     */
    public Loc sum(@NotNull Loc other) {
        return new Loc(x + other.x, y + other.y);
    }

    /**
     * Create a location which is the difference between the two points this (x1, y1) and other (x2, y2).
     * Ports {@code loc_diff} ({@code src/z-type.c}).
     * <p>
     * The result is a displacement, not a position, and its coordinates are routinely
     * negative — one of the few ways a {@link Loc} legitimately leaves the dungeon bounds.
     *
     * @param other the other point to work out the difference from this point
     * @return A new location (x1 - x2, y1 - y2).
     */
    public Loc diff(@NotNull Loc other) {
        return new Loc(this.x - other.x, this.y - other.y);
    }

    /**
     * Create a random location with the given spread variables on x and y. Ports
     * {@code rand_loc} ({@code src/z-type.c}).
     * <p>
     * Draws each axis independently, so the result is uniform over the enclosing
     * rectangle, not over a circle around this point.
     *
     * @param xSpread The x spread value - new value should be between this.x - xSpread and this.x + xSpread
     * @param ySpread The y spread value - new value should be between this.y - ySpread and this.y + ySpread
     * @return A random location where new.x is between this.x - xSpread and this.x + xSpread
     * and new.y is between this.y - ySpread and this.y + ySpread
     */
    public Loc rand(int xSpread, int ySpread) {
        return new Loc(RandomValueUtils.randSpread(x, xSpread), RandomValueUtils.randSpread(y, ySpread));
    }

    /**
     * Entry point for the fluent {@code Loc.row(y).col(x)} construction idiom, fixing the row
     * (y-coordinate) first and returning a builder that then takes the column.
     *
     * @param y the row (y-coordinate)
     * @return a {@link RowBuilder} awaiting the column to complete the {@link Loc}
     */
    public static RowBuilder row(int y) {
        RowBuilder rowHolder = new RowBuilder();
        rowHolder.y = y;
        return rowHolder;
    }

    /**
     * Returns a new location offset from this location by dx and dy. Ports
     * {@code loc_offset} ({@code src/z-type.c}); equivalent to {@link #sum} with the
     * offset supplied as loose coordinates rather than as a {@link Loc}.
     *
     * @param dx The amount that the x coordinate is offset
     * @param dy The amount that the y coordinate is offset
     * @return A new location of the form (x + dx, y + dy)
     */
    public Loc offset(int dx, int dy) {
        return new Loc(x + dx, y + dy);
    }

    /**
     * Returns an independent copy of this location.
     *
     * <p>{@link Loc} is immutable in practice - nothing mutates its coordinates - so a copy is
     * rarely needed. It exists for the callers that copy a whole object graph and want no shared
     * references anywhere in it, rather than because sharing one would be unsafe.
     *
     * <p>Function copy commented in full on 260827.
     *
     * @return a new location with the same coordinates
     */
    public Loc copy() {
        return new Loc(x, y);
    }

    /**
     * Returns the direction of a single step from this location towards {@code finish}. Ports
     * {@code motion_dir} ({@code src/cave.c}).
     *
     * <p>Only the sign of each coordinate difference matters, never its size, so the result is a
     * bearing rather than a route: a caller that wants to travel the whole way re-asks from each
     * new grid. Diagonals are preferred wherever both axes differ, which is what makes the
     * repeated single steps trace the C original's path rather than an L-shaped one.
     *
     * <p>Rows are numbered from the top downwards, as in C, so a {@code finish} with the larger
     * y is to the <em>south</em>.
     *
     * <p>Two locations that are equal yield {@link DirectionEnum#DIR_NONE}, meaning no motion is
     * needed. Note that C gives {@code DIR_NONE} and {@code DIR_TARGET} the same value 5 and so
     * cannot tell them apart; the enum here keeps them as distinct constants, and this method
     * returns only {@code DIR_NONE}.
     *
     * <p>Function motionDir coded before 260828, commented in full on 260828.
     *
     * @param finish the location being moved towards
     * @return the direction of the first step towards {@code finish}, or
     * {@link DirectionEnum#DIR_NONE} if this location is already {@code finish}
     */
    public DirectionEnum motionDir(Loc finish) {
        if (this.equals(finish)) return DirectionEnum.DIR_NONE;

        if (this.getX() == finish.getX()) return this.getY() < finish.getY() ? DirectionEnum.DIR_S
                : DirectionEnum.DIR_N;

        if (this.getY() == finish.getY()) return this.getX() < finish.getX() ? DirectionEnum.DIR_E
                : DirectionEnum.DIR_W;

        if (this.getY() < finish.getY()) return this.getX() < finish.getX() ? DirectionEnum.DIR_SE
                : DirectionEnum.DIR_SW;

        if (this.getY() > finish.getY()) return this.getX() < finish.getX() ? DirectionEnum.DIR_NE
                : DirectionEnum.DIR_NW;

        return DirectionEnum.DIR_NONE;
    }

    /**
     * Returns the approximate distance between this location and {@code grid}. Ports
     * {@code distance} ({@code src/cave-view.c}).
     *
     * <p>The result is a cheap integer approximation to the true Euclidean distance, computed as
     * {@code max(dy, dx) + min(dy, dx) / 2} on the absolute coordinate differences. It is almost
     * exact when one component dwarfs the other, and otherwise over-estimates by roughly one grid
     * in every fifteen. The C original uses it everywhere a distance is compared against a radius
     * or a range, so reproducing the approximation — rather than improving on it with a real
     * hypotenuse — is what keeps the ported ranges matching the game.
     *
     * <p>Halving is done with an arithmetic right shift, exactly as in C. Both operands are
     * absolute values and so never negative, which is what makes the shift and integer division
     * agree; a signed right shift rounds towards negative infinity, not towards zero.
     *
     * <p>The relation is symmetric: {@code a.distance(b)} equals {@code b.distance(a)}, because
     * only the magnitudes of the differences are used. A location's distance from itself is zero.
     *
     * <p>Function distance coded on 260828, commented in full on 260828.
     *
     * @param grid the other location to measure to
     * @return the approximate number of grids between the two locations, never negative
     */
    public int distance(Loc grid) {
        int ay = Math.abs(this.y - grid.y);
        int ax = Math.abs(this.x - grid.x);

        return ay > ax ? ay + (ax >> 1) : ax + (ay >> 1);
    }

    /**
     * Intermediate builder for the fluent {@code Loc.row(y).col(x)} idiom, holding the row until the
     * column is supplied.
     *
     * @author Rowan Crowther
     */
    public static class RowBuilder {
        /**
         * The row (y-coordinate) fixed by {@link Loc#row(int)}.
         */
        private int y;

        /**
         * Completes the location by supplying the column.
         *
         * @param x the column (x-coordinate)
         * @return the {@link Loc} at the previously-fixed row and this column
         */
        public Loc col(int x) {
            return new Loc(x, y);
        }
    }

    /**
     * Hashes the coordinate pair by multiplying {@code x} by the 32-bit golden-ratio
     * constant (Knuth's multiplicative hashing constant) and adding {@code y}.
     * <p>
     * The constant is odd, so multiplying by it is a bijection modulo 2<sup>32</sup> and
     * no information about {@code x} is lost; its evenly spread bits scatter adjacent
     * columns far apart. That matters because the natural key set here is a dense
     * rectangular block of small coordinates, which is the worst case for the
     * conventional {@code 31 * x + y}: that formula gives {@code (1, 0)} and
     * {@code (0, 31)} the same hash, and dungeon rows run well past 31.
     * <p>
     * Deliberately avoids {@code Objects.hash(x, y)}, which allocates a varargs array and
     * boxes both coordinates on every call. This method allocates nothing, which matters
     * in the level-generation and pathfinding loops that hash grids heavily.
     * <p>
     * Note that {@code 0x9E3779B1} is a negative {@code int} — hex literals may set the
     * sign bit, unlike decimal ones. That is harmless here: the arithmetic wraps modulo
     * 2<sup>32</sup> regardless of sign.
     *
     * @return a hash code consistent with {@link #equals(Object)}
     * @see #equals(Object)
     */
    @Override
    public int hashCode() {
        return x * 0x9E3779B1 + y;
    }
}