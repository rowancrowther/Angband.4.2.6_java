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

package uk.co.jackoftrades.middle.player;

import uk.co.jackoftrades.middle.objects.enums.TValue;

/**
 * One entry in a class's (or race's) starting-equipment list — a kind of item the
 * character is granted at birth, with a randomised quantity and optional constraints.
 *
 * <p>Ports the C {@code struct start_item} ({@code player.h}), populated from the
 * {@code start-item:} lines in {@code class.txt}. Each entry names an item kind
 * (tval + sval), a quantity range, and an optional set of ego/exclusion options that
 * narrow exactly which item is produced.
 *
 * <p><b>Why a quantity range rather than a fixed count:</b> birth gear is rolled, not
 * fixed (e.g. "3–5 torches"), so {@link #min}/{@link #max} bound a random draw made during
 * character creation.
 *
 * @author Rowan Crowther
 */
public class StartItem {
    /**
     * The item's base type (C: {@code start_item.tval}).
     */
    private TValue tValue;
    /** The item's subtype, held by name (C: {@code start_item.sval}). */
    private String sValue;
    /** Minimum quantity granted at birth (inclusive). */
    private int min;
    /** Maximum quantity granted at birth (inclusive). */
    private int max;
    /** Optional ego/exclusion options constraining which exact item is produced (C: {@code start_item.eopts}). */
    private String eOpts;

    /**
     * Creates a starting-item specification.
     *
     * @param tValue the item base type
     * @param sValue the item subtype name
     * @param min    minimum quantity (inclusive)
     * @param max    maximum quantity (inclusive)
     * @param eOpts  ego/exclusion option string, or empty if unconstrained
     */
    public StartItem(TValue tValue, String sValue, int min, int max, String eOpts) {
        this.tValue = tValue;
        this.sValue = sValue;
        this.min = min;
        this.max = max;
        this.eOpts = eOpts;
    }
}
