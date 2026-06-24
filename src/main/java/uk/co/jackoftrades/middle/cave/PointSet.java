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

import java.util.ArrayList;

/**
 * A simple collection of dungeon grid locations, used during level generation to
 * track sets of points (e.g. candidate or already-processed grids). This is the
 * Java port of the C original's {@code point_set} ({@code src/generate.c}).
 *
 * @author ClaudeCode
 */
public class PointSet {
    /**
     * The locations held in this set, in insertion order.
     *
     * @author ClaudeCode
     */
    private final ArrayList<Loc> points;

    /**
     * Constructor
     */
    public PointSet() {
        points = new ArrayList<>();
    }

    /**
     * Add a particular location to the ArrayList of locations
     *
     * @param location the location to add
     */
    public void add(Loc location) {
        points.add(location);
    }

    /**
     * Returns true if and only if the points ArrayList contains one element p such that p.equals(location)
     *
     * @param location the location we are looking for in the ArrayList
     * @return true if the ArrayList contains the location, false otherwise
     */
    public boolean contains(Loc location) {
        return points.contains(location);
    }

    /**
     * Gets the current size of this point set
     *
     * @return The current size of this point set
     */
    public int size() {
        return points.size();
    }
}
