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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * The player's history ledger — the port of C's {@code struct player_history} and the
 * {@code p->hist} it is held in ({@code player-history.h}). This is the running log of notable
 * events in a character's life: their birth, each level gained, each unique slain, each artifact
 * found or missed, and any note the player types for themselves. Each event is one
 * {@link HistoryInfo}, stamped with the depth, character level and turn it happened at.
 *
 * <p>Not to be confused with the character's background: that is rolled at birth from the
 * {@link PlayerHistoryChart} / {@link PlayerHistoryEntry} graph into a block of text, C's
 * {@code p->history}, and is a different thing entirely from this ledger.
 *
 * <p>C's struct carries three fields — the entries, {@code next} (how many are in use) and
 * {@code length} (how many are allocated) — because it manages its own memory:
 * {@code history_init} takes twenty slots, {@code history_realloc} adds twenty more whenever
 * {@code next} reaches {@code length}, and {@code history_clear} frees the lot. A {@link List}
 * does all of that by itself, so only the entries survive the port; C's {@code next} is the list's
 * size, and its {@code length} has no counterpart because capacity is no longer anyone's business
 * here.
 *
 * <p>Class PlayerHistory coded on 260831, commented in full on 260831.
 *
 * @author Rowan Crowther
 */
public class PlayerHistory {
    /**
     * Logger for this type.
     */
    private static final Logger logger = LogManager.getLogger();

    /**
     * The events logged so far, oldest first — C's {@code h->entries} read up to {@code h->next}.
     *
     * <p>C hands this array straight out to the UI in {@code history_get_list} rather than copying
     * it, so the openness here is C's too.
     */
    public List<HistoryInfo> entries;

    /**
     * A new, empty ledger, the port of C's {@code history_init}. C allocates its first twenty
     * zeroed slots and sets {@code next} to zero; an empty list is the same state, since a slot
     * past {@code next} is never read.
     *
     * <p>C allocates lazily — {@code history_add_full} calls {@code history_init} only when it
     * finds no array — because {@code history_clear} can leave the struct holding none. Building
     * the list here instead means the ledger is never absent, so no caller has to test for it.
     *
     * <p>Constructor PlayerHistory coded on 260831, commented in full on 260831.
     */
    public PlayerHistory() {
        entries = new ArrayList<>();
    }

    /**
     * Appends an event to the end of the ledger — the write half of C's {@code history_add_full},
     * which stores at {@code h->entries[h->next]} and then advances {@code h->next}. Appending is
     * the same operation, and it is the only one: entries are never inserted, replaced or removed,
     * so the list stays in the order the events happened. Later readers rely on that,
     * {@code history_is_artifact_known} and {@code history_mark_artifact_known} both scanning
     * backwards from the end to find the most recent mention of an artifact.
     *
     * <p>The entry is stored as given rather than copied; the copying C's {@code hist_copy} does
     * happens where the entry is built, in {@code Player.historyAddFull}, so that an entry is
     * already the ledger's own by the time it arrives here.
     *
     * <p>Method addEntry coded on 260831, commented in full on 260831.
     *
     * @param entry the event to log
     */
    public void addEntry(HistoryInfo entry) {
        entries.add(entry);
    }
}
