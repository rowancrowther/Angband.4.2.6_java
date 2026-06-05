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

public class PlayerHistoryChart {
    private static final Logger logger = LogManager.getLogger();

    private final int chartNumber;
    private final List<PlayerHistoryEntry> charts = new ArrayList<>();

    public PlayerHistoryChart(int chartNumber) {
        this.chartNumber = chartNumber;
    }

    public void addEntry(PlayerHistoryEntry chart) {
        charts.add(chart);
    }

    public int getChartNumber() {
        return chartNumber;
    }
}
