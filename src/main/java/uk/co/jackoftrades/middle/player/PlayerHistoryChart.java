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
 *    Java code copyright (c) Rowan Crowther 2026
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
 * <p>Ports the C {@code struct history_chart}. Character creation starts at a chart, rolls one
 * of its entries (see {@link PlayerHistoryEntry}), follows that entry's successor chart, and
 * repeats until an entry with no successor is reached — assembling the biography along the way.
 *
 * @author ClaudeCode
 */
public class PlayerHistoryChart {
    /**
     * Logger for this type.
     */
    private static final Logger logger = LogManager.getLogger();

    /** This chart's identifying number, referenced by entries' {@code nextChartNumber}. */
    private final int chartNumber;
    /** The candidate entries that may be rolled when this chart is reached. */
    private final List<PlayerHistoryEntry> charts = new ArrayList<>();

    /**
     * Creates an empty chart with the given number.
     *
     * @param chartNumber this chart's identifier
     */
    public PlayerHistoryChart(int chartNumber) {
        this.chartNumber = chartNumber;
    }

    /**
     * Adds a candidate entry to this chart.
     *
     * @param chart the {@link PlayerHistoryEntry} to add (a single entry, despite the field name)
     */
    public void addEntry(PlayerHistoryEntry chart) {
        charts.add(chart);
    }

    /**
     * @return this chart's identifying number
     */
    public int getChartNumber() {
        return chartNumber;
    }
}
