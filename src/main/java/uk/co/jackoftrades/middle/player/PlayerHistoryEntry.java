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

/**
 * One option in the player's background-history generation — a single weighted line of
 * backstory that may chain to a further chart.
 *
 * <p>Ports the C {@code struct history_entry}, built from {@code history.txt}. A character's
 * background is produced by walking a series of numbered charts: at each chart a random roll
 * selects one entry, whose {@link #text} is appended to the biography and whose
 * {@link #nextChartNumber} (when non-zero) names the next chart to roll on. {@link #roll} is
 * the cumulative probability threshold at which this entry is chosen.
 *
 * <p><b>Why chart-chaining rather than a flat table:</b> backstories branch — race leads to
 * parentage options, which lead to social-standing options — so the data is a directed graph
 * of small weighted choices, and each entry carries the edge to its successor chart.
 *
 * @author Rowan Crowther
 */
public class PlayerHistoryEntry {
    /**
     * Logger for this type.
     */
    private static final Logger logger = LogManager.getLogger();

    /** The chart this entry belongs to. */
    private int chartNumber;
    /** The chart to roll on next after choosing this entry, or {@code 0} to stop. */
    private int nextChartNumber;
    /** Cumulative probability threshold at which this entry is selected during the roll. */
    private int roll;
    /** The biography text contributed by this entry. */
    private String text;

    /**
     * Creates a history entry.
     *
     * @param chartNumber     the owning chart's number
     * @param nextChartNumber the successor chart to roll on next ({@code 0} = end of chain)
     * @param roll            the cumulative selection threshold
     * @param text            the backstory text this entry adds
     */
    public PlayerHistoryEntry(int chartNumber, int nextChartNumber, int roll, String text) {
        this.chartNumber = chartNumber;
        this.nextChartNumber = nextChartNumber;
        this.roll = roll;
        this.text = text;
    }

    /**
     * @return the chart this entry belongs to
     */
    public int getChartNumber() {
        return chartNumber;
    }
}
