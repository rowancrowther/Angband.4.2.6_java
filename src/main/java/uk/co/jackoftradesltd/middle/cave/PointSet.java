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

package uk.co.jackoftradesltd.middle.cave;

import java.util.ArrayList;

/**
 * An ordered collection of grid locations, collected so that a set of changes can be
 * applied to them afterwards (targeting uses it to gather candidate grids).
 * Java port of {@code struct point_set} and its utility functions in {@code z-type.h}
 * and {@code z-type.c}.
 * <p>
 * Despite the name this is a bag, not a set: like the C original it admits duplicates,
 * and it keeps insertion order. Membership is by value, through {@link Loc#equals}, as
 * C's {@code loc_eq} does.
 * <p>
 * The C growth bookkeeping ({@code n}, {@code allocated}, {@code pts}, the
 * {@code initial_size} argument and {@code point_set_dispose}) has no Java equivalent:
 * {@link ArrayList} grows itself and the garbage collector frees it.
 * <p>
 * Class PointSet coded before 260930, commented in full on 260930.
 *
 * @author Rowan Crowther
 */
public class PointSet {
    /**
     * The locations held in this set, in insertion order. Replaces the C fields
     * {@code pts}, {@code n} and {@code allocated}, which an {@link ArrayList} tracks
     * for itself.
     * <p>
     * Field points coded before 260930, commented in full on 260930.
     */
    private final ArrayList<Loc> points;

    /**
     * Creates an empty point set. Ports {@code point_set_new}; C's {@code initial_size}
     * parameter is dropped because the list resizes itself.
     * <p>
     * Function PointSet coded before 260930, commented in full on 260930.
     */
    public PointSet() {
        points = new ArrayList<>();
    }

    /**
     * Appends a location to the set. Ports {@code add_to_point_set}. No duplicate check is
     * made, exactly as in C, and the location is stored by reference, so it should not be
     * mutated afterwards.
     * <p>
     * Function add coded before 260930, commented in full on 260930.
     *
     * @param location the location to add
     */
    public void add(Loc location) {
        points.add(location);
    }

    /**
     * Tests whether an equal location is already in the set. Ports {@code point_set_contains},
     * which scans the array in order and compares each entry with {@code loc_eq}; here the
     * comparison is {@link Loc#equals}. C returns {@code int} 1 or 0, Java a boolean.
     * <p>
     * Function contains coded before 260930, commented in full on 260930.
     *
     * @param location the location to look for
     * @return true if some stored location has the same x and y, false otherwise
     */
    public boolean contains(Loc location) {
        return points.contains(location);
    }

    /**
     * Gets the number of locations held, duplicates counted. Ports {@code point_set_size}.
     * <p>
     * Function size coded before 260930, commented in full on 260930.
     *
     * @return the number of locations added so far
     */
    public int size() {
        return points.size();
    }
}
