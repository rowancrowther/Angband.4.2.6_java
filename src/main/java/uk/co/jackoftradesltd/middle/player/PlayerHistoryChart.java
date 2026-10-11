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

import java.util.ArrayList;
import java.util.List;

/**
 * A numbered chart of background-history options — one node in the history-generation graph,
 * holding the {@link PlayerHistoryEntry} rows that may be rolled at this step.
 *
 * <p>Ports the C {@code struct history_chart} in {@code player.h}, built from {@code history.txt}
 * by {@code parse_history_chart} and {@code finish_parse_history} in {@code init.c}. Character
 * creation starts at a chart (each player race names one), rolls one of its entries (see
 * {@link PlayerHistoryEntry}), follows this chart's {@link #getSuccessor() successor}, and repeats
 * until a chart with no successor is reached — assembling the biography along the way. The walk
 * itself is {@code get_history} in {@code player-birth.c}, ported as
 * {@link PlayerBirth#getHistory(PlayerHistoryChart)}.
 *
 * <p><b>Fields against C.</b> C's three fields become four here:
 * <pre>{@code
 * struct history_chart {
 *     struct history_chart *next;      // gone: the loader's List holds the charts
 *     struct history_entry *entries;   // entries
 *     unsigned int idx;                // chartNumber
 * };
 * // and, hoisted up from struct history_entry:
 * //     int isucc;                    // successorNumber
 * //     struct history_chart *succ;   // successor
 * }</pre>
 *
 * <p><b>Successor is per-chart, not per-entry.</b> C's {@code history_entry} carries its own
 * {@code isucc}/{@code succ}, but every chart in {@code history.txt} gives the same successor on
 * all of its lines (checked against the shipped file: all 44 charts), so the port hoists that
 * single edge up to the chart. The assembler enforces as a rule the uniformity the file merely
 * assumes, rejecting a record whose successor disagrees with the one its chart was first given. A
 * data file that did vary its successors by entry would load differently here from C.
 *
 * <p><b>No linked list of charts.</b> C pushes each new chart onto the front of a global
 * {@code histories} list and finds a chart by walking it ({@code findchart}). The port keeps
 * charts in a {@link List} in first-appearance order and the assembler finds them through a map.
 * The one later lookup, {@code PlayerRegistry.lookupPlayerHistoryChart}, goes by chart number, so
 * the reversed C order has no visible effect.
 *
 * <p>Class PlayerHistoryChart coded on 260902, commented in full on 261011.
 *
 * @author Rowan Crowther
 */
public class PlayerHistoryChart {
    /**
     * Logger for this type. Nothing in this class logs; it is kept so the class matches its
     * neighbours.
     *
     * <p>Field logger commented in full on 261011.
     */
    private static final Logger logger = LogManager.getLogger();

    /**
     * This chart's identifying number — C's {@code idx}, an {@code unsigned int} there. It is the
     * number the chart carries in {@code history.txt} and the one other charts name as their
     * {@link #successorNumber}. The shipped file uses 1 to 69 with gaps (44 charts); {@code 0} is
     * never a chart number because it is the end-of-chain marker.
     *
     * <p>Field chartNumber commented in full on 261011.
     */
    private final int chartNumber;

    /**
     * The chart to move on to after this one, resolved from {@link #successorNumber} during the
     * assembler's second pass — C's {@code succ}, held per chart here and per entry there.
     * {@code null} for a terminal chart (successor number {@code 0}) or before resolution has run.
     *
     * <p>A {@code null} here does not by itself mean end-of-chain. If the assembler cannot find the
     * chart a non-zero {@link #successorNumber} names, it records an error and leaves this
     * {@code null}, where C's {@code finish_parse_history} fails the whole load; only a successor
     * number of {@code 0} makes the null genuine.
     *
     * <p>Field successor commented in full on 261011.
     */
    private PlayerHistoryChart successor;

    /**
     * The raw successor-chart number as read from the file — C's {@code isucc}: {@code 0} means
     * end-of-chain, any other value is resolved into {@link #successor} once every chart has been
     * read (the successor may be a chart the file has not reached yet, which is why C and the port
     * both resolve in a second pass). Fixed at construction; the assembler compares it against each
     * later record for the same chart to enforce the per-chart rule.
     *
     * <p>Field successorNumber commented in full on 261011.
     */
    private final int successorNumber;

    /**
     * The candidate entries that may be rolled when this chart is reached — C's {@code entries}
     * list. Kept in file order, which is the ascending-threshold order the roll depends on (see
     * {@link #getEntries()}).
     *
     * <p>Field entries commented in full on 261011.
     */
    private final List<PlayerHistoryEntry> entries = new ArrayList<>();

    /**
     * Creates a chart with no entries and an as-yet-unresolved successor. The part of C's
     * {@code parse_history_chart} that makes a new chart ({@code c->idx = idx}) and records the
     * successor number, which C stores on each entry instead.
     *
     * <p>Constructor PlayerHistoryChart coded on 260902, commented in full on 261011.
     *
     * @param chartNumber     this chart's identifier
     * @param successorNumber the number of the chart to roll on next, or {@code 0} for end-of-chain;
     *                        resolved into a {@link #successor} reference after all charts are read
     */
    public PlayerHistoryChart(int chartNumber, int successorNumber) {
        this.chartNumber = chartNumber;
        this.successor = null;
        this.successorNumber = successorNumber;
    }

    /**
     * Adds one candidate entry to this chart, after those already added. This is the part of
     * {@code parse_history_chart} that links a new entry onto the chart. C pushes it onto the
     * <em>front</em> of the chart's list and has {@code finish_parse_history} reverse it later;
     * appending here gives the same final order in one step. Entries must be added in the order they
     * appear in the file, because nothing here sorts them.
     *
     * <p>Method addEntry coded on 260902, commented in full on 261011.
     *
     * @param entry the {@link PlayerHistoryEntry} to add
     */
    public void addEntry(PlayerHistoryEntry entry) {
        entries.add(entry);
    }

    /**
     * Links this chart to its resolved successor. Called during the assembler's second pass, once
     * the target chart is known to exist — the port's counterpart of the {@code findchart} lookup
     * and {@code e->succ} assignment in {@code finish_parse_history}. Not called for a chart whose
     * {@link #getSuccessorNumber() successor number} is {@code 0}, so such a chart keeps a
     * {@code null} successor.
     *
     * <p>Method setSuccessor coded on 260902, commented in full on 261011.
     *
     * @param chart the successor {@link PlayerHistoryChart} this chart chains to
     */
    public void setSuccessor(PlayerHistoryChart chart) {
        this.successor = chart;
    }

    /**
     * The number this chart goes by in {@code history.txt} (C's {@code idx}).
     *
     * <p>Method getChartNumber coded on 260902, commented in full on 261011.
     *
     * @return this chart's identifying number
     */
    public int getChartNumber() {
        return chartNumber;
    }

    /**
     * The successor number exactly as the file gave it (C's {@code isucc}). Unlike
     * {@link #getSuccessor()}, it still tells a terminal chart ({@code 0}) apart from one whose
     * successor failed to resolve.
     *
     * <p>Method getSuccessorNumber coded on 260902, commented in full on 261011.
     *
     * @return the raw successor-chart number from the file ({@code 0} = end-of-chain)
     */
    public int getSuccessorNumber() {
        return successorNumber;
    }

    /**
     * The chart {@link PlayerBirth#getHistory(PlayerHistoryChart)} moves to after rolling this one
     * — where C reads {@code entry->succ} off the chosen entry.
     *
     * <p>Method getSuccessor coded on 260902, commented in full on 261011.
     *
     * @return the resolved successor chart, or {@code null} if this chart ends the chain (or its
     *         successor could not be resolved, see {@link #successor})
     */
    public PlayerHistoryChart getSuccessor() {
        return successor;
    }

    /**
     * The candidate entries for this chart, in the order the roll must walk them.
     *
     * <p><b>The order is load-bearing.</b> {@link PlayerHistoryEntry#getRoll()} is a cumulative
     * threshold, not a weight, so selection is "the first entry whose roll is at least the number
     * rolled" — which is only correct while the entries ascend. That is the order they appear in
     * {@code history.txt}, and the order the assembler adds them in. C has to work for the same
     * order the hard way: its parser pushes each entry onto the front of a linked list, so
     * {@code finish_parse_history} in {@code init.c} reverses every chart's list before the game
     * ever reads one. The port needs no such repair, because a {@link List} appends.
     *
     * <p>The returned list is this chart's own, not a copy; nothing outside the assembler has cause
     * to change it.
     *
     * <p>Function getEntries coded on 260902, commented in full on 261011.
     *
     * @return this chart's entries, in ascending order of roll threshold
     */
    public List<PlayerHistoryEntry> getEntries() {
        return entries;
    }
}
