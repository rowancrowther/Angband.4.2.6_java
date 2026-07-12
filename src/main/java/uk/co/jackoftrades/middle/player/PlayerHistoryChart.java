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
 * A numbered chart of background-history options — one node in the history-generation graph,
 * holding the {@link PlayerHistoryEntry} rows that may be rolled at this step.
 *
 * <p>Ports the C {@code struct history_chart}. Character creation starts at a chart, rolls one of
 * its entries (see {@link PlayerHistoryEntry}), follows this chart's {@link #getSuccessor()
 * successor}, and repeats until a chart with no successor is reached — assembling the biography
 * along the way.
 *
 * <p><b>Successor is per-chart, not per-entry.</b> C's {@code history_entry} carries its own
 * {@code succ}, but every chart in {@code history.txt} gives the same successor on all of its
 * lines, so the port hoists that single edge up to the chart. The reader enforces as a rule the
 * uniformity the file merely assumes.
 *
 * @author Rowan Crowther
 */
public class PlayerHistoryChart {
    /**
     * Logger for this type.
     *
     * @author Rowan Crowther
     */
    private static final Logger logger = LogManager.getLogger();

    /**
     * This chart's identifying number, referenced by other charts' {@link #successorNumber}.
     */
    private final int chartNumber;

    /**
     * The chart to move on to after this one, resolved from {@link #successorNumber} during the
     * assembler's second pass. {@code null} for a terminal chart (successor number {@code 0}) or
     * before resolution has run.
     */
    private PlayerHistoryChart successor;

    /**
     * The raw successor-chart number as read from the file: {@code 0} means end-of-chain, any
     * other value is resolved into {@link #successor} once every chart has been read.
     */
    private final int successorNumber;

    /** The candidate entries that may be rolled when this chart is reached. */
    private final List<PlayerHistoryEntry> entries = new ArrayList<>();

    /**
     * Creates a chart with no entries and an as-yet-unresolved successor.
     *
     * @param chartNumber     this chart's identifier
     * @param successorNumber the number of the chart to roll on next, or {@code 0} for end-of-chain;
     *                        resolved into a {@link #successor} reference after all charts are read
     * @author Rowan Crowther
     */
    public PlayerHistoryChart(int chartNumber, int successorNumber) {
        this.chartNumber = chartNumber;
        this.successor = null;
        this.successorNumber = successorNumber;
    }

    /**
     * Adds one candidate entry to this chart.
     *
     * @param entry the {@link PlayerHistoryEntry} to add
     * @author Rowan Crowther
     */
    public void addEntry(PlayerHistoryEntry entry) {
        entries.add(entry);
    }

    /**
     * Links this chart to its resolved successor. Called during the assembler's second pass, once
     * the target chart is known to exist.
     *
     * @param chart the successor {@link PlayerHistoryChart} this chart chains to
     * @author Rowan Crowther
     */
    public void setSuccessor(PlayerHistoryChart chart) {
        this.successor = chart;
    }

    /**
     * @return this chart's identifying number
     * @author Rowan Crowther
     */
    public int getChartNumber() {
        return chartNumber;
    }

    /**
     * @return the raw successor-chart number from the file ({@code 0} = end-of-chain)
     * @author Rowan Crowther
     */
    public int getSuccessorNumber() {
        return successorNumber;
    }

    /**
     * @return the resolved successor chart, or {@code null} if this chart ends the chain
     * @author Rowan Crowther
     */
    public PlayerHistoryChart getSuccessor() {
        return successor;
    }
}
