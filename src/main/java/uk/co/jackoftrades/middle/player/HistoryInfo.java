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

import uk.co.jackoftrades.channel.utils.Flag;
import uk.co.jackoftrades.middle.objects.Artifact;
import uk.co.jackoftrades.middle.player.enums.PlayerHistoryType;

/**
 * One event in the player's history ledger — the port of C's {@code struct history_info}
 * ({@code player-history.h}). An entry records something notable that happened to the character
 * (their birth, a level gained, a unique slain, an artifact found or missed, a note they typed)
 * together with the circumstances it happened in: how deep they were, what level they were, and
 * which turn it was. Entries are appended to a {@link PlayerHistory} and are never afterwards
 * reordered or removed, so the ledger reads as a life story in the order it was lived.
 *
 * <p>An entry is not quite immutable, because C rewrites the flags of an already-logged entry in
 * place: {@code history_mark_artifact_known}, {@code history_mark_artifact_lost} and
 * {@code history_unmask_unknown} all scan the ledger and turn type flags on or off on an entry
 * that is already in it. Nothing else about a logged entry ever changes.
 *
 * <p>Two of C's fields changed shape in the port, and both changes remove a limit rather than add
 * one. C identifies an artifact by {@code a_idx}, a {@code uint8_t} index into the artifact table
 * with zero standing for "no artifact"; the port holds the {@link Artifact} itself and uses
 * {@code null} for none, so identity is the object rather than a number and there is no ceiling at
 * 255. C's {@code event} is an eighty-character buffer that {@code history_add_full} truncates
 * into; a {@link String} has no such length, and every C caller has already formatted into an
 * eighty-character buffer before it arrives, so nothing is lost by not truncating.
 *
 * <p>The remaining fields widen harmlessly: C's {@code int16_t} depth and character level and its
 * {@code int32_t} turn all fit what is used here.
 *
 * <p>Class HistoryInfo coded on 260831, commented in full on 260831.
 *
 * @author Rowan Crowther
 */
public class HistoryInfo {
    /**
     * What kind of event this is — C's {@code type} bitflags. A set rather than a single value
     * because C's artifact entries genuinely carry more than one at once:
     * {@code history_lose_artifact} logs a missed artifact as both
     * {@link PlayerHistoryType#HIST_ARTIFACT_UNKNOWN} and
     * {@link PlayerHistoryType#HIST_ARTIFACT_LOST} together.
     */
    Flag<PlayerHistoryType> type;
    /**
     * The dungeon level the event happened on — C's {@code dlev}, taken from {@code p->depth}.
     */
    int dLevel;
    /**
     * The character level the player was at — C's {@code clev}, taken from {@code p->lev}.
     */
    int cLevel;
    /**
     * The artifact the event concerns, or {@code null} where it concerns none — C's {@code a_idx},
     * whose zero this {@code null} stands in for. It is what the artifact scans in
     * {@code history_is_artifact_known} and its two marking functions match on.
     */
    Artifact artifact;
    /**
     * The turn the event happened on — C's {@code turn}, which
     * {@code history_add_with_flags} fills from {@code p->total_energy / 100}, making it a
     * player-turn count rather than a game-turn count.
     */
    long turn;
    /**
     * The line of text describing the event, as it will be shown to the player — C's
     * {@code event}. The text is already formatted by the time it reaches here; nothing downstream
     * composes it.
     */
    String historyText;

    /**
     * A logged event, built from the circumstances the caller has already gathered. This is the
     * slot-filling half of C's {@code history_add_full}, which writes each field into
     * {@code h->entries[h->next]} before advancing the cursor; here the entry is built first and
     * handed to {@link PlayerHistory#addEntry} afterwards.
     *
     * <p>The flag set is stored as given rather than copied. C's {@code hist_copy} does the copying
     * inside {@code history_add_full}, and the port keeps it there, in
     * {@code Player.historyAddFull}, so an entry arrives here already owning its flags.
     *
     * <p>Constructor HistoryInfo coded on 260831, commented in full on 260831.
     *
     * @param type        the history types this event carries
     * @param dLevel      the dungeon level the event happened on
     * @param cLevel      the character level the player was at
     * @param artifact    the artifact the event concerns, or {@code null} for C's index zero
     * @param turn        the turn the event happened on
     * @param historyText the text describing the event
     */
    public HistoryInfo(Flag<PlayerHistoryType> type, int dLevel, int cLevel,
                       Artifact artifact, long turn, String historyText) {
        this.type = type;
        this.dLevel = dLevel;
        this.cLevel = cLevel;
        this.artifact = artifact;
        this.turn = turn;
        this.historyText = historyText;
    }
}
