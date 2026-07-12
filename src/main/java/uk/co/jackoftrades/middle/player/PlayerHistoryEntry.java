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
 * One option in the player's background-history generation — a single weighted line of backstory
 * rolled at a {@link PlayerHistoryChart}.
 *
 * <p>Ports the C {@code struct history_entry}, built from {@code history.txt}. A character's
 * background is produced by walking a series of numbered charts: at each chart a random roll
 * selects one entry, whose {@link #text} is appended to the biography, and generation then follows
 * the chart's successor and repeats. {@link #roll} is the cumulative probability threshold at which
 * this entry is chosen.
 *
 * <p>Unlike the C struct, an entry holds <em>no</em> successor of its own — the edge to the next
 * chart is carried by the owning {@link PlayerHistoryChart}, because it is uniform across a chart's
 * entries in the data (see {@link PlayerHistoryChart} for that decision). An entry is therefore
 * just a weight and a phrase.
 *
 * @author Rowan Crowther
 */
public class PlayerHistoryEntry {
    /**
     * Logger for this type.
     *
     * @author Rowan Crowther
     */
    private static final Logger logger = LogManager.getLogger();

    /** Cumulative probability threshold at which this entry is selected during the roll. */
    private int roll;

    /** The biography text contributed by this entry. */
    private String text;

    /**
     * Creates a history entry.
     *
     * @param roll the cumulative selection threshold
     * @param text the backstory text this entry adds
     * @author Rowan Crowther
     */
    public PlayerHistoryEntry(int roll, String text) {
        this.roll = roll;
        this.text = text;
    }

    /**
     * @return the backstory text this entry contributes to the biography
     * @author Rowan Crowther
     */
    public String getText() {
        return text;
    }

    /**
     * @return the cumulative probability threshold at which this entry is selected
     * @author Rowan Crowther
     */
    public int getRoll() {
        return roll;
    }
}
