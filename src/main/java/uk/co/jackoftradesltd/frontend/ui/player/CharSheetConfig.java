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
 * {@code struct char_sheet_config} ({@code [C] ui-player.c:132-141}), currently used only for that
 * one panel since C notes the rest of the sheet is "no longer hardwired"
 * ({@code [C] ui-player.c:124-127}).
 *
 * <p>Built once per {@code UIPlayer.configureCharSheet()} call and held by that class's own
 * cache field, reused across draws until that layout is found stale. Every field here is set by
 * that one method; none is mutated anywhere else.
 *
 * <p>Class CharSheetConfig coded on 260925, commented in full on 260925.
 *
 * @author Rowan Crowther
 */
public class CharSheetConfig {
    /**
     * The player's stat-modifier UI entries, capped at {@code ChannelRegistry.STAT_MAX} — the
     * Java form of C's {@code stat_mod_entries} ({@code [C] ui-player.c:133}). Allocated by
     * {@link #initStatModEntries(int)} and filled one slot at a time by
     * {@link #setStatModEntry(int, UIEntry)}.
     *
     * <p>Field stat_mod_entries coded on 260925, commented in full on 260925.
     */
    UIEntry[] stat_mod_entries;

    /**
     * The four resistance-panel regions' screen positions, one per category
     * ({@code "resistances"}, {@code "abilities"}, {@code "hindrances"}, {@code "modifiers"}) —
     * the Java form of C's {@code res_regions[4]} ({@code [C] ui-player.c:134}). Pre-populated
     * with zero-valued {@link Region}s by the constructor so every slot has a real object to
     * mutate before its column, row and width are set; C needs no such step, since
     * {@code res_regions} is a value-typed array embedded directly in {@code char_sheet_config}
     * rather than an array of pointers.
     *
     * <p>Field resRegions coded on 260925, commented in full on 260925.
     */
    Region[] resRegions = new Region[4];

    /**
     * The resistance entries within each of the four regions, keyed first by region index (0-3)
     * and then by that entry's position within the region — the Java form of C's
     * {@code resists_by_region[4]} ({@code [C] ui-player.c:135}), an array of pointers each to a
     * separately {@code mem_alloc}'d array of {@code n_resist_by_region[i]} structs
     * ({@code [C] ui-player.c:246}). A {@link Map} per region stands in for that
     * dynamically-sized C array, since the entry count differs per region and is only known once
     * the matching {@link UIEntry}s have been counted.
     *
     * <p>Field resistsByRegion coded on 260925, commented in full on 260925.
     */
    Map<Integer, CharSheetResist>[] resistsByRegion = new HashMap[]{new HashMap(), new HashMap(), new HashMap(), new HashMap()};

    /**
     * How many resistance entries fall in each of the four regions — the Java form of C's
     * {@code n_resist_by_region[4]} ({@code [C] ui-player.c:136}).
     *
     * <p>Field nResistsByRegion coded on 260925, commented in full on 260925.
     */
    int[] nResistsByRegion = new int[4];

    /**
     * How many stat-modifier entries {@link #stat_mod_entries} holds — the Java form of C's
     * {@code n_stat_mod_entries} ({@code [C] ui-player.c:137}).
     *
     * <p>Field nStatModEntries coded on 260925, commented in full on 260925.
     */
    int nStatModEntries;

    /**
     * The resistance panel's column count per region: {@link #resNLabel} plus one plus the
     * player's current body-part count — the Java form of C's {@code res_cols}
     * ({@code [C] ui-player.c:138, 223-225}). Re-derived and compared against this cached value
     * elsewhere to detect a stale layout after a body-shape change.
     *
     * <p>Field resCols coded on 260925, commented in full on 260925.
     */
    int resCols;

    /**
     * The tallest per-region resistance entry count seen across all four regions — the Java form
     * of C's {@code res_rows} ({@code [C] ui-player.c:139}). Used to give every region's
     * {@link Region#getPageRows()} the same value once all four have been measured.
     *
     * <p>Field resRows coded on 260925, commented in full on 260925.
     */
    int resRows;

    /**
     * The resistance panel's label width in characters, always set to {@code 6} — the Java form
     * of C's {@code res_nlabel} ({@code [C] ui-player.c:140}).
     *
     * <p>Field resNLabel coded on 260925, commented in full on 260925.
     */
    int resNLabel;

    /**
     * Builds an empty configuration with all four {@link #resRegions} slots holding a zero-valued
     * {@link Region} and all four {@link #resistsByRegion} slots holding an empty map, ready to
     * be populated. C needs no equivalent step, since its {@code res_regions} array is embedded
     * by value and its {@code resists_by_region} pointers are left null until each region's own
     * {@code mem_alloc} call.
     *
     * <p>Constructor CharSheetConfig coded on 260925, commented in full on 260925.
     */
    public CharSheetConfig() {
        for (int index = 0; index < 4; index++) {
            resRegions[index] = new Region(0, 0, 0, 0);
            resistsByRegion[index] = new HashMap<>();
        }
    }

    /**
     * Method getResistsByRegion coded on 260925, commented in full on 260925.
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
     * Method setResistsByRegion coded on 260925, commented in full on 260925.
     *
     * @param index      the region index, 0-3
     * @param otherIndex the entry's position within that region
     * @param value      the resist entry to store
     */
    public void setResistsByRegion(int index, int otherIndex, CharSheetResist value) {
        this.resistsByRegion[index].put(otherIndex, value);
    }

    /**
     * Method getResCols coded on 260925, commented in full on 260925.
     *
     * @return the resistance panel's column count per region
     */
    public int getResCols() {
        return resCols;
    }

    /**
     * Method setResCols coded on 260925, commented in full on 260925.
     *
     * @param i the resistance panel's column count per region
     */
    public void setResCols(int i) {
        this.resCols = i;
    }

    /**
     * Method getResRows coded on 260925, commented in full on 260925.
     *
     * @return the tallest per-region resistance entry count seen across all four regions
     */
    public int getResRows() {
        return resRows;
    }

    /**
     * Method setResRows coded on 260925, commented in full on 260925.
     *
     * @param i the tallest per-region resistance entry count seen so far
     */
    public void setResRows(int i) {
        this.resRows = i;
    }

    /**
     * Method getResNLabel coded on 260925, commented in full on 260925.
     *
     * @return the resistance panel's label width in characters
     */
    public int getResNLabel() {
        return resNLabel;
    }

    /**
     * Method setNStatModEntries coded on 260925, commented in full on 260925.
     *
     * @param num how many stat-modifier entries {@link #stat_mod_entries} holds
     */
    public void setNStatModEntries(int num) {
        nStatModEntries = num;
    }

    /**
     * Allocates {@link #stat_mod_entries} to hold {@code num} entries, all initially null — the
     * Java form of C's {@code mem_alloc(n * sizeof(*cached_config->stat_mod_entries))}
     * ({@code [C] ui-player.c:215-216}).
     *
     * <p>Method initStatModEntries coded on 260925, commented in full on 260925.
     *
     * @param num how many stat-modifier entries to make room for
     */
    public void initStatModEntries(int num) {
        stat_mod_entries = new UIEntry[num];
    }

    /**
     * Method setStatModEntry coded on 260925, commented in full on 260925.
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
     * Method setResNlabel coded on 260925, commented in full on 260925.
     *
     * @param i the resistance panel's label width in characters
     */
    public void setResNlabel(int i) {
        this.resNLabel = i;
    }

    /**
     * Method getResRegion coded on 260925, commented in full on 260925.
     *
     * @param index the region index, 0-3
     * @return that region's screen position
     */
    public Region getResRegion(int index) {
        return resRegions[index];
    }

    /**
     * Method setResRegion coded on 260925, commented in full on 260925.
     *
     * @param index  the region index, 0-3
     * @param region the screen position to store there
     */
    public void setResRegion(int index, Region region) {
        resRegions[index] = region;
    }

    /**
     * Method getnResistsByRegion coded on 260925, commented in full on 260925.
     *
     * @param index the region index, 0-3
     * @return how many resistance entries fall in that region
     */
    public int getnResistsByRegion(int index) {
        return nResistsByRegion[index];
    }

    /**
     * Method setnResistsByRegion coded on 260925, commented in full on 260925.
     *
     * @param index the region index, 0-3
     * @param value how many resistance entries fall in that region
     */
    public void setnResistsByRegion(int index, int value) {
        this.nResistsByRegion[index] = value;
    }
}