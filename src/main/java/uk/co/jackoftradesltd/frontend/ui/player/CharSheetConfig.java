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

package uk.co.jackoftradesltd.frontend.ui.player;

import uk.co.jackoftradesltd.frontend.entries.UIEntry;
import uk.co.jackoftradesltd.frontend.ui.output.Region;

import java.util.HashMap;
import java.util.Map;

/**
 * Cached layout for the character sheet's resistance panel — the Java form of C's
 * {@code struct char_sheet_config} ({@code [C] ui-player.c}), currently used only for that
 * one panel since C notes the rest of the sheet is "no longer hardwired".
 *
 * <p>This is a plain data holder with no behaviour of its own; the layout arithmetic (column
 * offsets, row capping, entry counting) lives in {@code UIPlayer.configureCharSheet()}. C's
 * {@code release_char_sheet_config()} has no counterpart here: every array is garbage-collected
 * along with the object.
 *
 * <p>Built once per {@code UIPlayer.configureCharSheet()} call and held by that class's own
 * cache field, reused across draws until that layout is found stale. Every field here is set by
 * that one method; none is mutated anywhere else, and the only reader outside it is
 * {@code UIPlayer.haveValidCharSheetConfig()}.
 *
 * <p>Class CharSheetConfig coded on 260925, commented in full on 260929.
 *
 * @author Rowan Crowther
 */
public class CharSheetConfig {
    /**
     * The player's stat-modifier UI entries — the Java form of C's {@code stat_mod_entries}
     * ({@code [C] ui-player.c}, {@code struct char_sheet_config}). Null until
     * {@link #initStatModEntries(int)} allocates it, then filled one slot at a time by
     * {@link #setStatModEntry(int, UIEntry)}. The cap at {@code ChannelRegistry.STAT_MAX}
     * (C's {@code STAT_MAX}, linked to the hardcoded stats display) is applied by the caller
     * before allocation, not by this class.
     *
     * <p>Field stat_mod_entries coded on 260925, commented in full on 260929.
     */
    UIEntry[] stat_mod_entries;

    /**
     * The four resistance-panel regions' screen positions, one per category
     * ({@code "resistances"}, {@code "abilities"}, {@code "hindrances"}, {@code "modifiers"}) —
     * the Java form of C's {@code res_regions[4]} ({@code [C] ui-player.c},
     * {@code struct char_sheet_config}). Pre-populated
     * with zero-valued {@link Region}s by the constructor so every slot has a real object to
     * mutate before its column, row and width are set; C needs no such step, since
     * {@code res_regions} is a value-typed array embedded directly in {@code char_sheet_config}
     * rather than an array of pointers.
     *
     * <p>Field resRegions coded on 260925, commented in full on 260929.
     */
    Region[] resRegions = new Region[4];

    /**
     * The resistance entries within each of the four regions, keyed first by region index (0-3)
     * and then by that entry's position within the region — the Java form of C's
     * {@code resists_by_region[4]} ({@code [C] ui-player.c}, {@code struct char_sheet_config}),
     * an array of pointers each to a
     * separately {@code mem_alloc}'d array of {@code n_resist_by_region[i]} structs (allocated in
     * {@code configure_char_sheet}). A {@link Map} per region stands in for that
     * dynamically-sized C array, since the entry count differs per region and is only known once
     * the matching {@link UIEntry}s have been counted. A position that has not been stored reads
     * back as {@code null}, where C would hand back uninitialised memory.
     *
     * <p>Field resistsByRegion coded on 260925, commented in full on 260929.
     */
    Map<Integer, CharSheetResist>[] resistsByRegion = new HashMap[]{new HashMap(), new HashMap(), new HashMap(), new HashMap()};

    /**
     * How many resistance entries fall in each of the four regions — the Java form of C's
     * {@code n_resist_by_region[4]} ({@code [C] ui-player.c}, {@code struct char_sheet_config}).
     * This is the count after C's row-capping step, so it can be smaller than the number of
     * matching {@link UIEntry}s; it is the authoritative bound for iterating
     * {@link #resistsByRegion}.
     *
     * <p>Field nResistsByRegion coded on 260925, commented in full on 260929.
     */
    int[] nResistsByRegion = new int[4];

    /**
     * How many stat-modifier entries {@link #stat_mod_entries} holds — the Java form of C's
     * {@code n_stat_mod_entries} ({@code [C] ui-player.c}, {@code struct char_sheet_config}).
     * Kept as a separate field, as in C, although here it is set to the same value as the
     * array's length.
     *
     * <p>Field nStatModEntries coded on 260925, commented in full on 260929.
     */
    int nStatModEntries;

    /**
     * The resistance panel's column count per region: {@link #resNLabel} plus one plus the
     * player's current body-part count — the Java form of C's {@code res_cols}
     * ({@code [C] ui-player.c}, {@code struct char_sheet_config} and function
     * {@code configure_char_sheet}). Re-derived and compared against this cached value
     * by {@code have_valid_char_sheet_config} to detect a stale layout after a body-shape change.
     *
     * <p>Field resCols coded on 260925, commented in full on 260929.
     */
    int resCols;

    /**
     * The tallest per-region resistance entry count seen across all four regions — the Java form
     * of C's {@code res_rows} ({@code [C] ui-player.c}, {@code struct char_sheet_config}). Used
     * to give every region's {@link Region#getPageRows()} the same value ({@code resRows + 2})
     * once all four have been measured.
     *
     * <p>Field resRows coded on 260925, commented in full on 260929.
     */
    int resRows;

    /**
     * The resistance panel's label width in characters, always set to {@code 6} — the Java form
     * of C's {@code res_nlabel} ({@code [C] ui-player.c}, {@code struct char_sheet_config}).
     *
     * <p>Field resNLabel coded on 260925, commented in full on 260929.
     */
    int resNLabel;

    /**
     * Builds an empty configuration with all four {@link #resRegions} slots holding a zero-valued
     * {@link Region} and all four {@link #resistsByRegion} slots holding an empty map, ready to
     * be populated. C needs no equivalent step, since its {@code res_regions} array is embedded
     * by value and its {@code resists_by_region} pointers are left null until each region's own
     * {@code mem_alloc} call. The scalar fields start at zero; C's {@code mem_alloc} leaves them
     * uninitialised, but {@code configure_char_sheet} assigns every one before use, so the
     * difference is invisible.
     *
     * <p>Constructor CharSheetConfig coded on 260925, commented in full on 260929.
     */
    public CharSheetConfig() {
        for (int index = 0; index < 4; index++) {
            resRegions[index] = new Region(0, 0, 0, 0);
            resistsByRegion[index] = new HashMap<>();
        }
    }

    /**
     * Reads one resistance entry back out of a region — the Java form of C's
     * {@code config->resists_by_region[index][otherIndex]}. Callers should stay below
     * {@link #getnResistsByRegion(int)} for that region.
     *
     * <p>Method getResistsByRegion coded on 260925, commented in full on 260929.
     *
     * @param index      the region index, 0-3
     * @param otherIndex the entry's position within that region
     * @return the resist entry stored at that position, or {@code null} if none has been stored
     * there
     */
    public CharSheetResist getResistsByRegion(int index, int otherIndex) {
        return resistsByRegion[index].getOrDefault(otherIndex, null);
    }

    /**
     * Stores one resistance entry into a region — the Java form of assigning to
     * {@code config->resists_by_region[index][otherIndex]}. Unlike a C array, the map has no
     * fixed capacity, so nothing is checked against {@link #nResistsByRegion}; an existing entry
     * at the same position is replaced.
     *
     * <p>Method setResistsByRegion coded on 260925, commented in full on 260929.
     *
     * @param index      the region index, 0-3
     * @param otherIndex the entry's position within that region
     * @param value      the resist entry to store
     */
    public void setResistsByRegion(int index, int otherIndex, CharSheetResist value) {
        this.resistsByRegion[index].put(otherIndex, value);
    }

    /**
     * Reads {@link #resCols}, the resistance panel's column count per region.
     *
     * <p>Method getResCols coded on 260925, commented in full on 260929.
     *
     * @return the resistance panel's column count per region
     */
    public int getResCols() {
        return resCols;
    }

    /**
     * Sets {@link #resCols}. The caller is expected to pass {@link #getResNLabel()} plus one
     * plus the player's body-part count, as C does.
     *
     * <p>Method setResCols coded on 260925, commented in full on 260929.
     *
     * @param i the resistance panel's column count per region
     */
    public void setResCols(int i) {
        this.resCols = i;
    }

    /**
     * Reads {@link #resRows}, the running maximum of per-region entry counts.
     *
     * <p>Method getResRows coded on 260925, commented in full on 260929.
     *
     * @return the tallest per-region resistance entry count seen across all four regions
     */
    public int getResRows() {
        return resRows;
    }

    /**
     * Sets {@link #resRows}. The caller resets it to zero before measuring the regions and
     * raises it whenever a region's count exceeds it.
     *
     * <p>Method setResRows coded on 260925, commented in full on 260929.
     *
     * @param i the tallest per-region resistance entry count seen so far
     */
    public void setResRows(int i) {
        this.resRows = i;
    }

    /**
     * Reads {@link #resNLabel}, the label width used both for the column arithmetic and for
     * building each entry's label.
     *
     * <p>Method getResNLabel coded on 260925, commented in full on 260929.
     *
     * @return the resistance panel's label width in characters
     */
    public int getResNLabel() {
        return resNLabel;
    }

    /**
     * Sets {@link #nStatModEntries}. This does not resize {@link #stat_mod_entries}; call
     * {@link #initStatModEntries(int)} with the same count for that.
     *
     * <p>Method setNStatModEntries coded on 260925, commented in full on 260929.
     *
     * @param num how many stat-modifier entries {@link #stat_mod_entries} holds
     */
    public void setNStatModEntries(int num) {
        nStatModEntries = num;
    }

    /**
     * Allocates {@link #stat_mod_entries} to hold {@code num} entries, all initially null — the
     * Java form of C's {@code mem_alloc(n * sizeof(*cached_config->stat_mod_entries))}
     * ({@code [C] ui-player.c}, function {@code configure_char_sheet}). Any previous array is
     * discarded.
     *
     * <p>Method initStatModEntries coded on 260925, commented in full on 260929.
     *
     * @param num how many stat-modifier entries to make room for
     */
    public void initStatModEntries(int num) {
        stat_mod_entries = new UIEntry[num];
    }

    /**
     * Stores a stat-modifier entry into {@link #stat_mod_entries}. An index at or beyond the
     * array's length is silently ignored, where the equivalent C store would write out of
     * bounds; a negative index still throws. Must be called after
     * {@link #initStatModEntries(int)}, or the array is null and this throws.
     *
     * <p>Method setStatModEntry coded on 260925, commented in full on 260929.
     *
     * @param index the slot to fill, within the bounds set by {@link #initStatModEntries(int)}
     * @param advance the stat-modifier entry to store there; out-of-range indices are ignored
     */
    public void setStatModEntry(int index, UIEntry advance) {
        // fail politely
        if (index >= stat_mod_entries.length) return;
        stat_mod_entries[index] = advance;
    }

    /**
     * Sets {@link #resNLabel}. {@code configure_char_sheet} always passes {@code 6}. Note the
     * lower-case "l" in the name, unlike {@link #getResNLabel()}.
     *
     * <p>Method setResNlabel coded on 260925, commented in full on 260929.
     *
     * @param i the resistance panel's label width in characters
     */
    public void setResNlabel(int i) {
        this.resNLabel = i;
    }

    /**
     * Returns the live {@link Region} for one category, not a copy, so the caller can go on to
     * set its column, row, width and page rows in place — the Java form of taking
     * {@code &config->res_regions[index]}.
     *
     * <p>Method getResRegion coded on 260925, commented in full on 260929.
     *
     * @param index the region index, 0-3
     * @return that region's screen position
     */
    public Region getResRegion(int index) {
        return resRegions[index];
    }

    /**
     * Replaces one category's {@link Region} with the one supplied; the previous object is
     * dropped rather than copied into.
     *
     * <p>Method setResRegion coded on 260925, commented in full on 260929.
     *
     * @param index  the region index, 0-3
     * @param region the screen position to store there
     */
    public void setResRegion(int index, Region region) {
        resRegions[index] = region;
    }

    /**
     * Reads one element of {@link #nResistsByRegion}.
     *
     * <p>Method getnResistsByRegion coded on 260925, commented in full on 260929.
     *
     * @param index the region index, 0-3
     * @return how many resistance entries fall in that region
     */
    public int getnResistsByRegion(int index) {
        return nResistsByRegion[index];
    }

    /**
     * Sets one element of {@link #nResistsByRegion}, the region's entry count after C's
     * row-capping step.
     *
     * <p>Method setnResistsByRegion coded on 260925, commented in full on 260929.
     *
     * @param index the region index, 0-3
     * @param value how many resistance entries fall in that region
     */
    public void setnResistsByRegion(int index, int value) {
        this.nResistsByRegion[index] = value;
    }
}
