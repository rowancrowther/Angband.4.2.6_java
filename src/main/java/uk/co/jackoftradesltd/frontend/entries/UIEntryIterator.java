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

package uk.co.jackoftradesltd.frontend.entries;

import java.util.ArrayList;
import java.util.List;

/**
 * Walks a subset of the registered {@link UIEntry} elements in priority order — the port of
 * C's {@code struct ui_entry_iterator} ({@code ui-entry.c:41-44}) together with its free
 * functions {@code initialize_ui_entry_iterator}, {@code reset_ui_entry_iterator},
 * {@code count_ui_entry_iterator}, and {@code advance_ui_entry_iterator}
 * ({@code ui-entry.c:461-524}).
 *
 * <p>C builds the iterator's array in one call, filtering the global entry table by a
 * predicate and sorting it by descending category priority. This class splits that in two:
 * it is the plain data holder (the filtered, sorted list plus a cursor), while the filtering
 * and sorting themselves live in {@link uk.co.jackoftradesltd.frontend.ui.UIEntryCode
 * UIEntryCode}, whose {@code initialiseUIEntryIterator} is this project's analogue of
 * {@code initialize_ui_entry_iterator}. There is no equivalent of
 * {@code release_ui_entry_iterator}; the JVM reclaims the backing list once the iterator is
 * unreachable.
 *
 * <p>Class UIEntryIterator coded on 260916, commented in full on 260916.
 */
public class UIEntryIterator {
    /**
     * The entries this iterator enumerates, in the order they will be returned by
     * {@link #advance()} — already filtered and priority-sorted by the caller. Equivalent to
     * C's {@code entries} array, with {@code entries.size()} standing in for C's separate
     * {@code n} field ({@code ui-entry.c:42-43}).
     */
    private List<UIEntry> entries;

    /**
     * The iterator's current position within {@link #entries}; the next call to
     * {@link #advance()} returns {@code entries.get(index)}. Equivalent to C's {@code i}
     * field ({@code ui-entry.c:43}).
     */
    private int index;

    /**
     * Builds an iterator over an already-populated, already-sorted entry list, starting at an
     * arbitrary position.
     *
     * <p>{@code num} is accepted for parity with C's {@code n} field but is not retained as a
     * separate field; {@link #getNum()} derives the remaining count from {@code entries.size()
     * - index} instead, so a caller passing a {@code num} that disagrees with
     * {@code entries.size()} will not see it reflected.
     *
     * <p>Constructor UIEntryIterator(List, int, int) coded on 260916, commented in full on
     * 260916.
     *
     * @param entries the entries to iterate, in enumeration order
     * @param num     accepted for parity with C's {@code n} field; not stored
     * @param index   the starting cursor position
     */
    public UIEntryIterator(List<UIEntry> entries, int num, int index) {
        this.entries = entries;
        this.index = index;
    }

    /**
     * Builds an iterator over an already-populated, already-sorted entry list, starting at
     * the first entry.
     *
     * <p>Constructor UIEntryIterator(List) coded on 260916, commented in full on 260916.
     *
     * @param entries the entries to iterate, in enumeration order
     */
    public UIEntryIterator(List<UIEntry> entries) {
        this.entries = entries;
        this.index = 0;
    }

    /**
     * Builds an empty iterator to be populated with {@link #addEntry(UIEntry)} before
     * iteration begins. This is the constructor {@link
     * uk.co.jackoftradesltd.frontend.ui.UIEntryCode#initialiseUIEntryIterator} uses, filling
     * the list and sorting it in place of the array-fill loop in
     * {@code initialize_ui_entry_iterator} ({@code ui-entry.c:470-476}).
     *
     * <p>Constructor UIEntryIterator() coded on 260916, commented in full on 260916.
     */
    public UIEntryIterator() {
        this.entries = new ArrayList<>();
        this.index = 0;
    }

    /**
     * Returns the backing list of entries this iterator enumerates.
     *
     * <p>Method getEntries coded on 260916, commented in full on 260916.
     *
     * @return the entry list, in enumeration order
     */
    public List<UIEntry> getEntries() {
        return entries;
    }

    /**
     * Reports how many entries remain to be returned by {@link #advance()} — the port of
     * {@code count_ui_entry_iterator} ({@code ui-entry.c:509-512}), which returns C's
     * {@code n - i}. This is a remaining count, not the original total: it falls as
     * {@link #advance()} is called and reaches zero once the iterator is exhausted.
     *
     * <p>Method getNum coded on 260916, commented in full on 260916.
     *
     * @return the number of entries not yet returned by {@link #advance()}
     */
    public int getNum() {
        return entries.size() - index;
    }

    /**
     * Returns the iterator's current cursor position — the port of C's {@code i} field
     * ({@code ui-entry.c:43}).
     *
     * <p>Method getIndex coded on 260916, commented in full on 260916.
     *
     * @return the index of the next entry {@link #advance()} will return
     */
    public int getIndex() {
        return index;
    }

    /**
     * Appends an entry to the backing list. Used during construction (see {@link
     * uk.co.jackoftradesltd.frontend.ui.UIEntryCode#initialiseUIEntryIterator}) to build up
     * the filtered set before iteration begins, standing in for the array-fill loop in
     * {@code initialize_ui_entry_iterator} ({@code ui-entry.c:470-476}).
     *
     * <p>Method addEntry coded on 260916, commented in full on 260916.
     *
     * @param entry the entry to append
     */
    public void addEntry(UIEntry entry) {
        entries.add(entry);
    }

    /**
     * Returns the entry currently pointed to by the iterator, then advances the cursor by
     * one — the port of {@code advance_ui_entry_iterator} ({@code ui-entry.c:519-524}), which
     * reads {@code entries[i]} before incrementing {@code i}.
     *
     * <p>Unlike the C original, this returns {@code null} once the iterator is exhausted
     * rather than reading past the end of the list. C has no equivalent bounds check; it
     * relies on the caller bounding the loop with {@link #getNum()}'s value first (see
     * {@code ui-player.c:206-220}), so this guard is a safety net that a caller following
     * that pattern will never trigger.
     *
     * <p>Method advance coded on 260916, commented in full on 260916.
     *
     * @return the entry at the current cursor position, or {@code null} if the iterator is
     * exhausted
     */
    public UIEntry advance() {
        if (index >= entries.size()) return null;
        UIEntry entry = entries.get(index);
        index++;
        return entry;
    }
}
