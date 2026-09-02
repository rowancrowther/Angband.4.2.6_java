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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.objects.Artifact;
import uk.co.jackoftradesltd.middle.player.enums.PlayerHistoryType;

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
     * Adds a history entry of a single type, the port of C's {@code history_add}
     * ({@code player-history.c}). This is the wrapper the ordinary event loggers use - gaining a
     * level, slaying a unique, a player's own note - where the entry carries one type and relates
     * to no artifact.
     *
     * <p>C wipes a local bitflag array and switches the one type on; constructing a {@link Flag}
     * from a single constant does both in one step.</p>
     *
     * <p>Function historyAdd coded on 260831, commented in full on 260831.</p>
     *
     * @param player the player whose ledger the entry is appended to
     * @param buf    the text of the entry
     * @param flag   the single history type the entry carries
     * @return {@code true} always, following C
     */
    static boolean historyAdd(Player player, String buf, PlayerHistoryType flag) {
        Flag<PlayerHistoryType> flags = new Flag<>(PlayerHistoryType.class, flag);

        return historyAddWithFlags(player, buf, flags, null);
    }

    /**
     * Adds a history entry stamped with the player's present circumstances, the port of C's
     * {@code history_add_with_flags} ({@code player-history.c}). The caller supplies the text, the
     * types and the artifact; the depth, character level and turn are read from the player here.
     * That is the boundary between this method and {@link #historyAddFull}: callers logging
     * something as it happens come through here, while callers that already know the circumstances
     * an entry belongs to - the savefile loader replaying a stored ledger - go straight to
     * {@code historyAddFull}.
     *
     * <p>The turn recorded is the cumulative energy divided by one hundred, C's
     * {@code p->total_energy / 100}, so a history turn is a player-turn rather than a game turn.</p>
     *
     * <p>Function historyAddWithFlags coded on 260831, commented in full on 260831.</p>
     *
     * @param player   the player whose depth, level and turn stamp the entry, and whose ledger it
     *                 is appended to
     * @param buf      the text of the entry
     * @param flags    the history types the entry carries
     * @param artifact the artifact the entry relates to, or {@code null} if it relates to none
     * @return {@code true} always, following C
     */
    private static boolean historyAddWithFlags(Player player, String buf, Flag<PlayerHistoryType> flags, Artifact artifact) {
        return historyAddFull(player, flags, artifact, player.getDepth(), player.getLevel(), player.getTotalEnergy() / 100, buf);
    }

    /**
     * Appends an entry to the player's history ledger, the port of C's {@code history_add_full}
     * ({@code player-history.c}). Every field of the entry arrives as an argument rather than being
     * read from the player, which is what lets a caller record an entry against circumstances other
     * than the present ones.
     *
     * <p>The flag set is copied rather than stored by reference, matching C's {@code hist_copy}:
     * the entry keeps its own record of its types, so a caller that later reuses or clears the flag
     * set it passed in cannot rewrite history.</p>
     *
     * <p>C truncates the entry text to the eighty characters its {@code event} field holds, but
     * every C caller has already formatted into an eighty-character buffer before arriving, so the
     * text is stored whole.</p>
     *
     * <p>The {@code boolean} return is C's, which reports success unconditionally.</p>
     *
     * <p>Function historyAddFull coded on 260831, commented in full on 260831. Allocation note
     * dropped on 260901.</p>
     *
     * @param player   the player whose ledger the entry is appended to
     * @param flags    the history types the entry carries
     * @param artifact the artifact the entry relates to, or {@code null} for C's artifact index 0
     * @param dLev     the dungeon level to record against the entry
     * @param cLev     the character level to record against the entry
     * @param turnNo   the turn to record against the entry
     * @param buf      the text of the entry
     * @return {@code true} always, following C
     */
    private static boolean historyAddFull(Player player, Flag<PlayerHistoryType> flags, Artifact artifact, int dLev, int cLev,
                                          int turnNo, String buf) {
        Flag<PlayerHistoryType> copyFlags = new Flag<>(PlayerHistoryType.class);
        copyFlags.copyFrom(flags);

        // Add entry
        HistoryInfo newEntry = new HistoryInfo(copyFlags, dLev, cLev, artifact, turnNo, buf);
        player.getPlayerHistory().addEntry(newEntry);
        return true;
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
     * happens where the entry is built, in {@link #historyAddFull}, so that an entry is
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
