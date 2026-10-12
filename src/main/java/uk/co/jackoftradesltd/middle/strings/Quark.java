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

package uk.co.jackoftradesltd.middle.strings;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftradesltd.backend.AngbandModule;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The "quark" table: a deduplicating store that maps small integer keys to
 * strings. This is the Java port of Angband's quark subsystem
 * ({@code src/z-quark.c}), which the C game uses to keep frequently repeated
 * strings (such as inscriptions) in one place and refer to them cheaply by an
 * integer handle rather than copying the text around.
 * <p>
 * Implemented as an {@link AngbandModule} so it participates in the game's
 * init/cleanup lifecycle. New keys are always allocated above the current
 * maximum so that removing an entry never causes a later {@link #add(String)}
 * to accidentally reuse a stale key.
 * <p>
 * As in C, {@link #add(String)} returns the existing key when the text is already stored, so two
 * equal strings never hold two keys and a caller may compare keys instead of text. Key 0 is never
 * issued: C reserves it to mean "no quark" (an object with no inscription), and
 * {@link #getQuark(int)} returns {@code null} for it.
 * <p>
 * Accepted deviation (decided 261009): C keeps a blank record in slot 0, so {@code nr_quarks} is
 * one more than the number of strings held. This table holds only real records, and
 * {@link #size()} is the plain count of them. {@link #merge(Quark)} and {@link #containsText(String)}
 * have no C counterpart.
 * <p>Class Quark coded before 261012, commented in full on 261012.
 *
 * @author Rowan Crowther
 */
public class Quark implements AngbandModule {
    /**
     * This module's display name, set during {@link #init()}.
     * <p>Field name coded before 261012, commented in full on 261012.
     */
    private String name;
    /**
     * The key-to-string table; insertion-ordered so iteration is stable. It is the Java counterpart of
     * the {@code quarks} array in {@code z-quark.c}, except that it holds no blank record for key 0 and
     * so has no separate {@code nr_quarks} or {@code alloc_quarks} counters: the map grows by itself and
     * its size is the record count.
     * {@code null} until {@link #init()} runs and again after {@link #cleanup()}.
     * <p>Field quarks coded before 261012, commented in full on 261012.
     */
    private Map<Integer, String> quarks;
    /**
     * Logger used to report misuse (e.g. operating on an uninitialized table).
     * <p>Field logger coded before 261012, commented in full on 261012.
     */
    private final Logger logger = LogManager.getLogger();

    /**
     * Add a string to this set of quarks and return its key, as {@code quark_add()} in {@code z-quark.c}
     * does. The existing entries are scanned first, and if one holds exactly the same text (case
     * sensitive, as {@code streq} is) its key is returned and the table is left untouched. Only when no
     * entry matches is a new one stored, under a key one higher than the highest in use.
     * <p>
     * Allocating above the highest key, rather than from the size, means a gap in the keys can never cause
     * a later add to reuse a stale key. Keys start at 1; 0 is never issued.
     * <p>Function add coded on 261012, commented in full on 261012.
     *
     * @param string The string to add
     * @return the key of the stored string: the existing key if the text was already present, otherwise a
     * new key 1 higher than the previous highest value in this map.
     * @throws NullPointerException if this collection of quarks is null
     */
    @CheckReturnValue
    @Contract(mutates = "this")
    public int add(@NotNull String string) throws NullPointerException {
        if (quarks == null) {
            String message = "Quark is null. String '" + string + "' cannot be added to a null quark.";
            NullPointerException n = new NullPointerException(message);
            logger.error(message, n);
            throw n;
        }
        
        for (int index : quarks.keySet()) {
            if (quarks.get(index).equals(string))
                return index;
        }

        int i = quarks.size();

        for (int index : quarks.keySet()) {
            if (index > i) i = index;
        }

        quarks.put(i + 1, string);
        return i + 1;
    }

    /**
     * Merge the strings of another Quark into this one. C has no equivalent, since there is only ever one
     * global table.
     * <p>
     * Each incoming string is stored under its own key plus an offset of this table's highest key plus 1,
     * so nothing already here is overwritten. That means the incoming key 1 lands at the highest key plus
     * 2, leaving the key at highest plus 1 unused. The merge does not deduplicate: text held by both tables
     * is stored twice, under two keys. A null or empty {@code other} changes nothing, and {@code other} itself is never modified.
     * <p>Function merge coded before 261012, commented in full on 261012.
     *
     * @param other the other Quark to merge this one with
     */
    @CheckReturnValue
    @Contract(mutates = "this")
    public void merge(Quark other) {
        // if the other is null or has no entries, just return
        if (other == null || other.size() == 0) return;

        // Work out the offset for quarks in this set
        int offset = Collections.max(quarks.keySet()) + 1;

        // Insert the new quarks using their existing keys plus the offset
        for (Map.Entry<Integer, String> entry : other.quarks.entrySet()) {
            quarks.put(entry.getKey() + offset, entry.getValue());
        }
    }

    /**
     * Gets the value of the quark with given quark key, as {@code quark_str()} in {@code z-quark.c} does.
     * Key 0 (C's blank slot, "no quark") and any key that was never issued give {@code null}, which is
     * what C returns for slot 0 and for a key at or past {@code nr_quarks}.
     * <p>Function getQuark coded before 261012, commented in full on 261012.
     *
     * @param key the key of the string we wish to obtain from the map
     * @return if the key is a valid key (contained in the key set), then return the appropriate string - otherwise
     * return null.
     * @throws NullPointerException when this quark hasn't been initiated yet
     */
    @CheckReturnValue
    @Contract(pure = true)
    public String getQuark(int key) throws NullPointerException {
        if (quarks == null) {
            String message = "Quark is null. Key '" + key + "' will not be present.";
            NullPointerException e = new NullPointerException(message);
            logger.error(message, e);
            throw e;
        }

        if (quarks.containsKey(key)) return quarks.get(key);
        return null;
    }

    /**
     * Get the number of quarks in the list. This is the count of real strings held, so it is 0 for a fresh
     * table where C's {@code nr_quarks} would be 1 (accepted deviation, decided 261009). Unlike
     * {@link #add(String)} and {@link #getQuark(int)} it does not check for an uninitialized table, and
     * throws a bare {@link NullPointerException} before {@link #init()} or after {@link #cleanup()}.
     * <p>Function size coded before 261012, commented in full on 261012.
     *
     * @return The number of quarks in this quark list
     */
    @CheckReturnValue
    @Contract(pure = true)
    public int size() {
        return quarks.size();
    }

    /**
     * The module name, which is {@code "Quark"} once {@link #init()} has run (C's is {@code "z-quark"}) and
     * {@code null} before that.
     * <p>Function getName coded before 261012, commented in full on 261012.
     *
     * @return the name of this module
     */
    @Override
    @CheckReturnValue
    @Contract(pure = true)
    public String getName() {
        return name;
    }

    /**
     * Initialize this module: gives it an empty table and its name, as {@code quarks_init()} in
     * {@code z-quark.c} does. Any earlier contents are discarded, so calling it again after
     * {@link #cleanup()} (or on a used table) starts keys from 1 once more.
     * <p>Function init coded before 261012, commented in full on 261012.
     */
    @Contract(mutates = "this")
    @Override
    public void init() {
        quarks = new LinkedHashMap<>();
        name = "Quark";
    }

    /**
     * Clean up this module by setting the table to {@code null}, which releases the strings for garbage
     * collection; this is the Java counterpart of {@code quarks_free()} in {@code z-quark.c}, which frees
     * each string and then the array. The module name is left as it was. Until {@link #init()} runs again,
     * {@link #add(String)} and {@link #getQuark(int)} throw.
     * <p>Function cleanup coded before 261012, commented in full on 261012.
     */
    @Contract(mutates = "this")
    @Override
    public void cleanup() {
        quarks = null;
    }

    /**
     * Whether any stored string equals {@code text} exactly (case sensitive, whole string, no substring
     * match). C has no equivalent; {@code quark_add()} does this scan internally. Like {@link #size()} it
     * throws a bare {@link NullPointerException} on an uninitialized table.
     * <p>Function containsText coded before 261012, commented in full on 261012.
     *
     * @param text the text to look for
     * @return {@code true} if a quark holds exactly that text
     */
    public boolean containsText(String text) {
        for (Integer key : quarks.keySet()) {
            if (quarks.get(key).equals(text)) return true;
        }

        return false;
    }
}