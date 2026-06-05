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

public class PlayerHistoryEntry {
    private static final Logger logger = LogManager.getLogger();

    private int chartNumber;
    private int nextChartNumber;
    private int roll;
    private String text;

    public PlayerHistoryEntry(int chartNumber, int nextChartNumber, int roll, String text) {
        this.chartNumber = chartNumber;
        this.nextChartNumber = nextChartNumber;
        this.roll = roll;
        this.text = text;
    }

    public int getChartNumber() {
        return chartNumber;
    }
}
