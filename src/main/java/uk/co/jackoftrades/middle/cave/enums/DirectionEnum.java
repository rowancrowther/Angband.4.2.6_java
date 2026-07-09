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

/**
 * The movement directions, each tied to its numeric-keypad key and its
 * {@code (x, y)} step offset. This unifies the C original's parallel
 * {@code ddx}/{@code ddy} offset arrays and keypad-direction mapping into one
 * enum: the {@code key} is the numpad digit (1–9) for that direction, and the
 * offsets give the change in column/row for a single step. Note {@code y}
 * increases <em>northward</em> here (N is {@code +1}), matching the offsets
 * encoded below.
 *
 * @author Rowan Crowther
 */
public enum DirectionEnum {
    /**
     * Unknown/invalid direction (no movement, keypad 0). @author Rowan Crowther
     */
    DIR_UNKNOWN(0, 0, 0),
    /**
     * North-west (keypad 7). @author Rowan Crowther
     */
    DIR_NW(7, -1, 1),
    /** North (keypad 8). @author Rowan Crowther */
    DIR_N(8, 0, 1),
    /** North-east (keypad 9). @author Rowan Crowther */
    DIR_NE(9, 1, 1),
    /** West (keypad 4). @author Rowan Crowther */
    DIR_W(4, -1, 0),
    /** "Target" pseudo-direction / centre (keypad 5). @author Rowan Crowther */
    DIR_TARGET(5, 0, 0),
    /** No direction / centre (keypad 5). @author Rowan Crowther */
    DIR_NONE(5, 0, 0),
    /** East (keypad 6). @author Rowan Crowther */
    DIR_E(6, 1, 0),
    /** South-west (keypad 1). @author Rowan Crowther */
    DIR_SW(1, -1, -1),
    /** South (keypad 2). @author Rowan Crowther */
    DIR_S(2, 0, -1),
    /** South-east (keypad 3). @author Rowan Crowther */
    DIR_SE(3, 1, -1),
    ;

    /**
     * The numeric-keypad key (1–9) that selects this direction.
     *
     * @author Rowan Crowther
     */
    private final int key;
    /**
     * Change in column for one step in this direction.
     *
     * @author Rowan Crowther
     */
    private final int xOffset;
    /**
     * Change in row for one step in this direction (north is positive).
     *
     * @author Rowan Crowther
     */
    private final int yOffset;

    /**
     * Bind a direction to its keypad key and step offsets.
     *
     * @param key     the numpad key
     * @param xOffset the column step
     * @param yOffset the row step
     * @author Rowan Crowther
     */
    DirectionEnum(int key, int xOffset, int yOffset) {
        this.key = key;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
    }

    /**
     * @return the column step for this direction (the C {@code ddx} value)
     * @author Rowan Crowther
     */
    public int ddx() {
        return xOffset;
    }

    /**
     * @return the row step for this direction (the C {@code ddy} value)
     * @author Rowan Crowther
     */
    public int ddy() {
        return yOffset;
    }

    /**
     * @return the step offset as a {@link Loc} (the C {@code ddgrid} value)
     * @author Rowan Crowther
     */
    public Loc ddgrid() {
        return new Loc(xOffset, yOffset);
    }

    /**
     * @return the numpad key associated with this direction
     * @author Rowan Crowther
     */
    public int getKey() {
        return key;
    }

    /**
     * Resolve a direction from its numpad key.
     *
     * @param key the numpad key (1–9)
     * @return the matching direction, or {@link #DIR_UNKNOWN} if none matches
     * @author Rowan Crowther
     */
    public static DirectionEnum fromKey(int key) {
        for (DirectionEnum d : DirectionEnum.values()) {
            if (d.getKey() == key) {
                return d;
            }
        }

        return DIR_UNKNOWN;
    }
}
