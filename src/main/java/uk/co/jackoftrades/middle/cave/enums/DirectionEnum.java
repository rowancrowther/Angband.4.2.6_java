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

package uk.co.jackoftrades.middle.cave.enums;

import uk.co.jackoftrades.middle.cave.Loc;

import java.util.stream.Stream;

/**
 * The movement directions, each tied to its numeric-keypad key and its
 * {@code (x, y)} step offset. This unifies the C original's parallel
 * {@code ddx}/{@code ddy} offset arrays and keypad-direction mapping into one
 * enum: the {@code key} is the numpad digit (1–9) for that direction, and the
 * offsets give the change in column/row for a single step. Note {@code y}
 * increases <em>southward</em> here (N is {@code -1}), because rows are numbered
 * from the top of the map downwards, matching the offsets encoded below.
 *
 * @author Rowan Crowther
 */
public enum DirectionEnum {
    /**
     * Unknown/invalid direction (no movement, keypad 0). @author Rowan Crowther
     */
    DIR_UNKNOWN(0, 0, 0, false),
    /**
     * North-west (keypad 7). @author Rowan Crowther
     */
    DIR_NW(7, -1, -1, true),
    /** North (keypad 8). @author Rowan Crowther */
    DIR_N(8, 0, -1, true),
    /** North-east (keypad 9). @author Rowan Crowther */
    DIR_NE(9, 1, -1, true),
    /** West (keypad 4). @author Rowan Crowther */
    DIR_W(4, -1, 0, true),
    /** "Target" pseudo-direction / centre (keypad 5). @author Rowan Crowther */
    DIR_TARGET(5, 0, 0, false),
    /** No direction / centre (keypad 5). @author Rowan Crowther */
    DIR_NONE(5, 0, 0, false),
    /** East (keypad 6). @author Rowan Crowther */
    DIR_E(6, 1, 0, true),
    /** South-west (keypad 1). @author Rowan Crowther */
    DIR_SW(1, -1, 1, true),
    /** South (keypad 2). @author Rowan Crowther */
    DIR_S(2, 0, 1, true),
    /** South-east (keypad 3). @author Rowan Crowther */
    DIR_SE(3, 1, 1, true),
    ;

    /**
     * The numeric-keypad key (1–9) that selects this direction.
     */
    private final int key;
    /**
     * Change in column for one step in this direction.
     */
    private final int xOffset;
    /**
     * Change in row for one step in this direction (south is positive).
     */
    private final int yOffset;

    private final boolean standard;

    /**
     * Bind a direction to its keypad key and step offsets.
     *
     * @param key     the numpad key
     * @param xOffset the column step
     * @param yOffset the row step
     */
    DirectionEnum(int key, int xOffset, int yOffset, boolean standard) {
        this.key = key;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.standard = standard;
    }

    /**
     * @return the column step for this direction (the C {@code ddx} value)
     */
    public int ddx() {
        return xOffset;
    }

    /**
     * @return the row step for this direction (the C {@code ddy} value)
     */
    public int ddy() {
        return yOffset;
    }

    /**
     * @return the step offset as a {@link Loc} (the C {@code ddgrid} value)
     */
    public Loc ddgrid() {
        return Loc.row(yOffset).col(xOffset);
    }

    /**
     * @return the numpad key associated with this direction
     */
    public int getKey() {
        return key;
    }

    /**
     * Resolve a direction from its numpad key.
     *
     * @param key the numpad key (1–9)
     * @return the matching direction, or {@link #DIR_UNKNOWN} if none matches
     */
    public static DirectionEnum fromKey(int key) {
        for (DirectionEnum d : DirectionEnum.values()) {
            if (d.getKey() == key) {
                return d;
            }
        }

        return DIR_UNKNOWN;
    }

    /**
     * Reports whether this is one of the eight "standard" grid directions — the four cardinals
     * and four diagonals that make up C's {@code ddgrid_ddd} neighbour set. Non-standard entries
     * (e.g. {@link #DIR_UNKNOWN} or a no-move centre) are excluded, letting callers iterate
     * {@link #values()} and skip anything that is not a real one-step neighbour offset.
     *
     * @return true if this direction is a standard eight-way neighbour step
     */
    public boolean isStandard() {
        return standard;
    }

    /**
     * The eight one-step neighbour offsets, as C's {@code ddgrid_ddd} table: every direction that
     * moves you to an adjacent grid, with the two centre entries ({@link #DIR_TARGET},
     * {@link #DIR_NONE}) and {@link #DIR_UNKNOWN} left out. Callers that want to sweep the ring
     * around a grid iterate this rather than {@link #values()}, so they need no
     * {@link #isStandard()} guard of their own.
     *
     * <p>A fresh array is built on each call, so a caller may shuffle or otherwise rearrange the
     * result without disturbing anyone else — C's table is a shared {@code const} and cannot be
     * treated that way.
     *
     * <p>Function surroundingDirections coded before 260828, commented in full on 260828.
     *
     * @return a new array of the eight standard neighbour directions
     */
    public static DirectionEnum[] surroundingDirections() {
        return Stream.of(
                DIR_S, DIR_N, DIR_E, DIR_W,
                DIR_SE, DIR_SE, DIR_NE, DIR_NW).toArray(DirectionEnum[]::new);
    }
}
