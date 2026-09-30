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

package uk.co.jackoftradesltd.middle.cave.enums;

import uk.co.jackoftradesltd.middle.cave.Loc;

import java.util.stream.Stream;

/**
 * The movement directions, each tied to its numeric-keypad key and its
 * {@code (x, y)} step offset. This unifies the C original's parallel
 * {@code ddx}/{@code ddy}/{@code ddgrid} arrays (all in {@code cave.c}, indexed by keypad
 * digit) and the {@code DIR_*} constants of {@code cave.h} into one enum: the {@code key} is the
 * numpad digit for that direction, and the offsets give the change in column/row for a single
 * step. Note {@code y} increases <em>southward</em> here (N is {@code -1}), because rows are
 * numbered from the top of the map downwards, exactly as in C.
 *
 * <p>Two boundaries differ from C. {@code DIR_TARGET} and {@code DIR_NONE} are the same value 5
 * in C but are separate constants here, so {@link #fromKey} cannot tell them apart and returns
 * {@code DIR_TARGET}. And C's {@code ddd} loop order (S, N, E, W, SE, SW, NE, NW, then centre)
 * is carried by {@link #surroundingDirections()} rather than by the constants' declaration order.
 *
 * <p>Class DirectionEnum coded before 260828, commented in full on 260930.
 *
 * @author Rowan Crowther
 */
public enum DirectionEnum {
    /**
     * Unknown/invalid direction (no movement, keypad 0): C's {@code DIR_UNKNOWN}, index 0 of
     * {@code ddx}/{@code ddy}. Not a standard direction.
     *
     * <p>Constant DIR_UNKNOWN coded before 260828, commented in full on 260930.
     */
    DIR_UNKNOWN(0, 0, 0, false),
    /**
     * North-west (keypad 7, offsets {@code (-1, -1)}).
     *
     * <p>Constant DIR_NW coded before 260828, commented in full on 260930.
     */
    DIR_NW(7, -1, -1, true),
    /**
     * North (keypad 8, offsets {@code (0, -1)}).
     *
     * <p>Constant DIR_N coded before 260828, commented in full on 260930.
     */
    DIR_N(8, 0, -1, true),
    /**
     * North-east (keypad 9, offsets {@code (1, -1)}).
     *
     * <p>Constant DIR_NE coded before 260828, commented in full on 260930.
     */
    DIR_NE(9, 1, -1, true),
    /**
     * West (keypad 4, offsets {@code (-1, 0)}).
     *
     * <p>Constant DIR_W coded before 260828, commented in full on 260930.
     */
    DIR_W(4, -1, 0, true),
    /**
     * "Target" pseudo-direction (keypad 5, no movement): C's {@code DIR_TARGET}, meaning "use the
     * current target". It shares the value 5 with {@link #DIR_NONE} in C, and is declared first so
     * that {@link #fromKey(int)} resolves 5 to it. Not a standard direction.
     *
     * <p>Constant DIR_TARGET coded before 260828, commented in full on 260930.
     */
    DIR_TARGET(5, 0, 0, false),
    /**
     * No direction / centre (keypad 5, no movement): C's {@code DIR_NONE}, also the centre entry
     * of the {@code ddgrid_ddd} table. Not a standard direction.
     *
     * <p>Constant DIR_NONE coded before 260828, commented in full on 260930.
     */
    DIR_NONE(5, 0, 0, false),
    /**
     * East (keypad 6, offsets {@code (1, 0)}).
     *
     * <p>Constant DIR_E coded before 260828, commented in full on 260930.
     */
    DIR_E(6, 1, 0, true),
    /**
     * South-west (keypad 1, offsets {@code (-1, 1)}).
     *
     * <p>Constant DIR_SW coded before 260828, commented in full on 260930.
     */
    DIR_SW(1, -1, 1, true),
    /**
     * South (keypad 2, offsets {@code (0, 1)}).
     *
     * <p>Constant DIR_S coded before 260828, commented in full on 260930.
     */
    DIR_S(2, 0, 1, true),
    /**
     * South-east (keypad 3, offsets {@code (1, 1)}).
     *
     * <p>Constant DIR_SE coded before 260828, commented in full on 260930.
     */
    DIR_SE(3, 1, 1, true),
    ;

    /**
     * The numeric-keypad key (0-9) that selects this direction; the index into C's
     * {@code ddx}/{@code ddy}/{@code ddgrid}.
     *
     * <p>Field key coded before 260828, commented in full on 260930.
     */
    private final int key;
    /**
     * Change in column for one step in this direction (C's {@code ddx[key]}).
     *
     * <p>Field xOffset coded before 260828, commented in full on 260930.
     */
    private final int xOffset;
    /**
     * Change in row for one step in this direction, south positive (C's {@code ddy[key]}).
     *
     * <p>Field yOffset coded before 260828, commented in full on 260930.
     */
    private final int yOffset;

    /**
     * Whether this is one of the eight real one-step neighbour directions. It is Java's stand-in
     * for C's {@code ddd[]} loops stopping before the centre entry, and is false for
     * {@link #DIR_UNKNOWN}, {@link #DIR_TARGET} and {@link #DIR_NONE}.
     *
     * <p>Field standard coded before 260828, commented in full on 260930.
     */
    private final boolean standard;

    /**
     * Bind a direction to its keypad key and step offsets.
     *
     * <p>Constructor DirectionEnum coded before 260828, commented in full on 260930.
     *
     * @param key      the numpad key
     * @param xOffset  the column step
     * @param yOffset  the row step
     * @param standard true if this is one of the eight real neighbour steps
     */
    DirectionEnum(int key, int xOffset, int yOffset, boolean standard) {
        this.key = key;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.standard = standard;
    }

    /**
     * Resolve a direction from its numpad key, the reverse of C's integer direction values. C has
     * no such function because it never leaves the integer; this exists for callers that hold a
     * raw key. Key 5 is ambiguous ({@link #DIR_TARGET} and {@link #DIR_NONE} share it) and
     * resolves to {@link #DIR_TARGET}, the first declared. Key 0 and any out-of-range key give
     * {@link #DIR_UNKNOWN}.
     *
     * <p>Method fromKey coded before 260828, commented in full on 260930.
     *
     * @param key the numpad key (0–9)
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
     * The eight one-step neighbour directions in C's {@code ddd} order (S, N, E, W, SE, SW, NE,
     * NW), which {@code ddgrid_ddd} follows offset for offset. C's tables carry a ninth centre
     * entry (key 5, offset {@code (0, 0)}) that its loops stop before with {@code i < 8}; here
     * the centre entries ({@link #DIR_TARGET}, {@link #DIR_NONE}) and {@link #DIR_UNKNOWN} are
     * left out. Callers that want to sweep the ring
     * around a grid iterate this rather than {@link #values()}, so they need no
     * {@link #isStandard()} guard of their own.
     *
     * <p>A fresh array is built on each call, so a caller may shuffle or otherwise rearrange the
     * result without disturbing anyone else — C's table is a shared {@code const} and cannot be
     * treated that way.
     *
     * <p>Method surroundingDirections coded before 260828, commented in full on 260930.
     *
     * @return a new array of the eight standard neighbour directions
     */
    public static DirectionEnum[] surroundingDirections() {
        return Stream.of(
                DIR_S, DIR_N, DIR_E, DIR_W,
                DIR_SE, DIR_SW, DIR_NE, DIR_NW).toArray(DirectionEnum[]::new);
    }

    /**
     * The column step for this direction: C's {@code ddx[dir]}.
     *
     * <p>Method ddx coded before 260828, commented in full on 260930.
     *
     * @return the column step for this direction
     */
    public int ddx() {
        return xOffset;
    }

    /**
     * The row step for this direction: C's {@code ddy[dir]}, south positive.
     *
     * <p>Method ddy coded before 260828, commented in full on 260930.
     *
     * @return the row step for this direction
     */
    public int ddy() {
        return yOffset;
    }

    /**
     * The step offset as a {@link Loc}: C's {@code ddgrid[dir]}. A fresh {@link Loc} is built on
     * each call.
     *
     * <p>Method ddgrid coded before 260828, commented in full on 260930.
     *
     * @return the step offset as a {@link Loc}
     */
    public Loc ddgrid() {
        return Loc.row(yOffset).col(xOffset);
    }

    /**
     * The numpad key for this direction, which is C's integer direction value. Both
     * {@link #DIR_TARGET} and {@link #DIR_NONE} answer 5.
     *
     * <p>Method getKey coded before 260828, commented in full on 260930.
     *
     * @return the numpad key associated with this direction
     */
    public int getKey() {
        return key;
    }

    /**
     * Reports whether this is one of the eight "standard" grid directions — the four cardinals
     * and four diagonals that make up C's {@code ddgrid_ddd} neighbour set. Non-standard entries
     * (e.g. {@link #DIR_UNKNOWN} or a no-move centre) are excluded, letting callers iterate
     * {@link #values()} and skip anything that is not a real one-step neighbour offset.
     *
     * <p>Method isStandard coded before 260828, commented in full on 260930.
     *
     * @return true if this direction is a standard eight-way neighbour step
     */
    public boolean isStandard() {
        return standard;
    }
}
