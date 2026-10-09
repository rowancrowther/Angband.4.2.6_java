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

package uk.co.jackoftradesltd.middle.player;

import uk.co.jackoftradesltd.middle.objects.enums.TValue;

import java.util.List;

/**
 * One entry in a class's starting-equipment list — a kind of item the character is granted at
 * birth, with a randomized quantity and optional birth-option exclusions.
 *
 * <p>Ports the C {@code struct start_item} ({@code player.h}), populated from the
 * {@code equip:} lines in {@code class.txt} (C: {@code parse_class_equip()} in {@code init.c}).
 * Each entry names an item kind (tval + sval), a quantity range, and a list of birth-option
 * tests that can withhold the item from the character.
 *
 * <p><b>Why a quantity range rather than a fixed count:</b> birth gear is rolled, not fixed
 * (e.g. 1–3 wooden torches), so {@link #min}/{@link #max} bound the {@code rand_range()} draw
 * made in {@code player_outfit()} ({@code player-birth.c}). The C parser rejects either bound
 * above 99 ({@code PARSE_ERROR_INVALID_ITEM_NUMBER}).
 *
 * <p><b>Divergences from C:</b>
 * <ul>
 *   <li>The C {@code next} pointer is gone — membership in the owning {@code PlayerClass}'s
 *       {@code List<StartItem>} replaces the linked list.</li>
 *   <li>{@code sval} is held as the item's name and resolved against the object kinds at use,
 *       not as an index resolved at parse time.</li>
 *   <li>{@code eopts} is a list of {@link StartOptionExclusion}, empty when unconstrained,
 *       rather than a zero-terminated {@code int} array that is {@code NULL} when unconstrained.</li>
 * </ul>
 *
 * <p>Class StartItem coded before 261009, commented in full on 261009.
 *
 * @author Rowan Crowther
 */
public class StartItem {
    /**
     * The item's base type (C: {@code start_item.tval}).
     *
     * <p>Field tValue coded before 261009, commented in full on 261009.
     */
    private TValue tValue;
    /**
     * The item's subtype, held by name and unresolved until the item is created (C:
     * {@code start_item.sval}, an index there).
     *
     * <p>Field sValue coded before 261009, commented in full on 261009.
     */
    private String sValue;
    /**
     * Minimum quantity granted at birth (inclusive; C: {@code start_item.min}).
     *
     * <p>Field min coded before 261009, commented in full on 261009.
     */
    private int min;
    /**
     * Maximum quantity granted at birth (inclusive; C: {@code start_item.max}).
     *
     * <p>Field max coded before 261009, commented in full on 261009.
     */
    private int max;
    /**
     * Birth-option tests that can exclude this item (C: {@code start_item.eopts}). The item is
     * withheld if <em>any</em> test fires, so the list is an OR of exclusions; an empty list means
     * the item is never excluded on option grounds.
     *
     * <p>Field eOpts coded before 261009, commented in full on 261009.
     */
    private List<StartOptionExclusion> eOpts;

    /**
     * Creates a starting-item specification. The list is stored as given, not copied.
     *
     * <p>Constructor StartItem coded before 261009, commented in full on 261009.
     *
     * @param tValue the item base type
     * @param sValue the item subtype name
     * @param min    minimum quantity (inclusive)
     * @param max    maximum quantity (inclusive)
     * @param eOpts  the birth-option exclusions, or empty if the item is unconstrained
     */
    public StartItem(TValue tValue, String sValue, int min, int max,
                     List<StartOptionExclusion> eOpts) {
        this.tValue = tValue;
        this.sValue = sValue;
        this.min = min;
        this.max = max;
        this.eOpts = eOpts;
    }

    /**
     * Reads the item's base type (C: {@code start_item.tval}).
     *
     * <p>Function gettValue coded before 261009, commented in full on 261009.
     *
     * @return the item's base type (tval)
     */
    public TValue gettValue() {
        return tValue;
    }

    /**
     * Reads the item's subtype name (C: {@code start_item.sval}). The caller resolves it to a
     * kind with the tval, as {@code lookup_kind()} does in C.
     *
     * <p>Function getsValue coded before 261009, commented in full on 261009.
     *
     * @return the item's subtype name (sval, held unresolved)
     */
    public String getsValue() {
        return sValue;
    }

    /**
     * Reads the lower bound of the quantity roll (C: {@code start_item.min}).
     *
     * <p>Function getMin coded before 261009, commented in full on 261009.
     *
     * @return the minimum quantity granted at birth (inclusive)
     */
    public int getMin() {
        return min;
    }

    /**
     * Reads the upper bound of the quantity roll (C: {@code start_item.max}).
     *
     * <p>Function getMax coded before 261009, commented in full on 261009.
     *
     * @return the maximum quantity granted at birth (inclusive)
     */
    public int getMax() {
        return max;
    }

    /**
     * Reads the birth-option exclusions (C: {@code start_item.eopts}). Any one firing withholds
     * the item: a plain entry fires when its option is set, a {@code NOT-} entry when it is
     * clear. Exposes the live list, not a copy.
     *
     * <p>Function geteOpts coded before 261009, commented in full on 261009.
     *
     * @return the birth-option exclusions; empty when the item is unconstrained
     */
    public List<StartOptionExclusion> geteOpts() {
        return eOpts;
    }
}
