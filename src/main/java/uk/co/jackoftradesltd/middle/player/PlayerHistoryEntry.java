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

/**
 * One option in the player's background-history generation — a single weighted line of backstory
 * rolled at a {@link PlayerHistoryChart}.
 *
 * <p>Ports the C {@code struct history_entry} in {@code player.h}, built from {@code history.txt}
 * by {@code parse_history_chart} and {@code parse_history_phrase} in {@code init.c}. A character's
 * background is produced by walking a series of numbered charts: at each chart a random roll
 * selects one entry, whose {@link #text} is appended to the biography, and generation then follows
 * the chart's successor and repeats. The walk is {@code get_history} in {@code player-birth.c},
 * ported as {@link PlayerBirth#getHistory(PlayerHistoryChart)}. {@link #roll} is the cumulative
 * probability threshold at which this entry is chosen.
 *
 * <p><b>Fields against C.</b> C's five fields become two here:
 * <pre>{@code
 * struct history_entry {
 *     struct history_entry *next;      // gone: the owning chart's List holds the entries
 *     struct history_chart *succ;      // gone: hoisted to PlayerHistoryChart.successor
 *     int isucc;                       // gone: hoisted to PlayerHistoryChart.successorNumber
 *     int roll;                        // roll
 *     char *text;                      // text
 * };
 * }</pre>
 *
 * <p>Unlike the C struct, an entry holds <em>no</em> successor of its own — the edge to the next
 * chart is carried by the owning {@link PlayerHistoryChart}, because it is uniform across a chart's
 * entries in the data (see {@link PlayerHistoryChart} for that decision). An entry is therefore
 * just a weight and a phrase.
 *
 * <p><b>One phrase per entry.</b> C builds {@code text} with {@code string_append}, so a second
 * {@code phrase:} line after a {@code chart:} line would be added on to the same entry. The port's
 * grammar takes exactly one {@code phrase:} line per {@code chart:} line, so {@link #text} is that
 * single line, kept exactly as written. The spacing is part of the data: 149 of the 165 phrases in
 * Angband 4.2.6's own {@code history.txt} end in a space (the 16 that do not end a chain), which is
 * what lets {@link PlayerBirth#getHistory(PlayerHistoryChart)} join them with no separator, so a
 * copy of the file that loses its trailing spaces gives a biography with the words run together.
 *
 * <p>The class is a plain value holder. Nothing in it validates its arguments: the threshold is not
 * checked against the 1-100 range of the roll, and the entries of a chart are not checked for
 * ascending order. Both are properties of the data file, relied on by
 * {@link PlayerHistoryChart#getEntries()}.
 *
 * <p>Class PlayerHistoryEntry coded on 260902, commented in full on 261012.
 *
 * @author Rowan Crowther
 */
public class PlayerHistoryEntry {
    /**
     * Logger for this type. Nothing in this class logs; it is kept so the class matches its
     * neighbours.
     *
     * <p>Field logger commented in full on 261012.
     */
    private static final Logger logger = LogManager.getLogger();

    /**
     * Cumulative probability threshold at which this entry is selected during the roll — C's
     * {@code roll}, an {@code int} there too. It is the third number on the entry's
     * {@code chart:} line in {@code history.txt}. {@code get_history} draws {@code randint1(100)}
     * and takes the first entry of the chart whose {@code roll} is at least the draw, so an entry
     * with {@code roll} 40 following one with {@code roll} 10 is chosen by draws 11 to 40, a 30%
     * chance. The last entry of every chart in the shipped file is 100, which is what guarantees
     * that some entry is always chosen. Set at construction and never changed afterwards.
     *
     * <p>Field roll coded on 260902, commented in full on 261012.
     */
    private int roll;

    /**
     * The biography text contributed by this entry — C's {@code text}. It is the {@code phrase:}
     * line that follows the entry's {@code chart:} line, exactly as the file gives it, trailing
     * space included (see the class comment for why that space matters). C leaves {@code text} as
     * {@code NULL} for a {@code chart:} line with no phrase after it; the port's grammar rejects
     * such a record instead, so an entry built by the loader always has text. Set at construction and never changed afterwards.
     *
     * <p>Field text coded on 260902, commented in full on 261012.
     */
    private String text;

    /**
     * Creates a history entry. The part of C's {@code parse_history_chart} that fills in
     * {@code e->roll} and of {@code parse_history_phrase} that fills in {@code e->text}, done in
     * one call because the port's parse record already holds both. The {@code next}, {@code succ}
     * and {@code isucc} fields that C sets here or later have no counterpart; the owning
     * {@link PlayerHistoryChart} holds them, and {@link PlayerHistoryChart#addEntry} links the
     * entry into its list.
     *
     * <p>Constructor PlayerHistoryEntry coded on 260902, commented in full on 261012.
     *
     * @param roll the cumulative selection threshold, 1 to 100 in the shipped data
     * @param text the backstory text this entry adds, trailing space included
     */
    public PlayerHistoryEntry(int roll, String text) {
        this.roll = roll;
        this.text = text;
    }

    /**
     * The text {@link PlayerBirth#getHistory(PlayerHistoryChart)} appends to the biography when this
     * entry is chosen — where C reads {@code entry->text} for its {@code string_append}. Returned
     * as stored, with no trimming, because the trailing space is part of the sentence layout.
     *
     * <p>Method getText coded on 260902, commented in full on 261012.
     *
     * @return the backstory text this entry contributes to the biography
     */
    public String getText() {
        return text;
    }

    /**
     * The threshold {@link PlayerBirth#getHistory(PlayerHistoryChart)} compares the draw against —
     * where C reads {@code entry->roll} in {@code roll <= entry->roll}. It is cumulative, not a
     * weight: see {@link #roll}.
     *
     * <p>Method getRoll coded on 260902, commented in full on 261012.
     *
     * @return the cumulative probability threshold at which this entry is selected
     */
    public int getRoll() {
        return roll;
    }
}
