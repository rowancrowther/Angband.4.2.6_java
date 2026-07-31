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

/**
 * A grid of integer "distances"/intensities over the whole level, used for
 * flow-based monster pathfinding (how far each grid is from the player along
 * passable terrain). This is the Java port of the C original's {@code heatmap}
 * struct ({@code src/cave.h}); higher-level flow code fills and reads {@link
 * #grids}.
 *
 * @author Rowan Crowther
 */
public class Heatmap {
    /**
     * Per-grid scalar values, indexed {@code [y][x]}.
     *
     * @author Rowan Crowther
     */
    private int[][] grids;

    /**
     * The map width in grids, matching the level's width; {@code grids} has this many columns.
     */
    private int width;

    /**
     * The map height in grids, matching the level's height; {@code grids} has this many rows.
     */
    private int height;

    /**
     * Creates a heatmap of the given dimensions, with every grid initialised to {@code 0} (the
     * "no value" / silence baseline). The dimensions are passed in by the owning level, so a
     * heatmap always matches the chunk it belongs to.
     *
     * @param width  the map width in grids
     * @param height the map height in grids
     * @author Rowan Crowther
     */
    public Heatmap(int width, int height) {
        grids = new int[height][width];
    }

    /**
     * Reads the value at a grid given loose coordinates.
     *
     * @param y the row (y-coordinate)
     * @param x the column (x-coordinate)
     * @return the scalar stored at that grid
     * @author Rowan Crowther
     */
    public int getValue(int y, int x) {
        return grids[y][x];
    }

    /**
     * Writes the value at a grid given loose coordinates.
     *
     * @param y     the row (y-coordinate)
     * @param x     the column (x-coordinate)
     * @param value the scalar to store at that grid
     * @author Rowan Crowther
     */
    public void setValue(int y, int x, int value) {
        grids[y][x] = value;
    }

    /**
     * Reads the value at a grid addressed by a {@link Loc}.
     *
     * @param loc the grid to read
     * @return the scalar stored at that grid
     * @author Rowan Crowther
     */
    public int getValue(Loc loc) {
        return grids[loc.getY()][loc.getX()];
    }

    /**
     * Writes the value at a grid addressed by a {@link Loc}.
     *
     * @param loc   the grid to write
     * @param value the scalar to store at that grid
     * @author Rowan Crowther
     */
    public void setValue(Loc loc, int value) {
        grids[loc.getY()][loc.getX()] = value;
    }
}
