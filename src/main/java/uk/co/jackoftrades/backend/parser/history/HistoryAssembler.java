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

package uk.co.jackoftrades.backend.parser.history;

import org.jetbrains.annotations.NotNull;
import uk.co.jackoftrades.backend.parser.Assembler;
import uk.co.jackoftrades.middle.player.PlayerHistoryChart;
import uk.co.jackoftrades.middle.player.PlayerHistoryEntry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Turns the flat stream of {@link HistoryParseRecord}s parsed from {@code history.txt} into the
 * {@link PlayerHistoryChart} graph the game walks when generating a character's background.
 *
 * <p>Each parse record is one {@code chart:}/{@code phrase:} pair; records that share a chart
 * number belong to the same chart. Assembly runs in two passes:
 * <ol>
 *   <li><b>Group and build.</b> Records are grouped by chart number into {@link PlayerHistoryChart}s,
 *       each gaining one {@link PlayerHistoryEntry} (a {@code roll} weight plus its {@code phrase})
 *       per record.</li>
 *   <li><b>Resolve successors.</b> Each chart's raw successor <em>number</em> is resolved to the
 *       actual successor {@link PlayerHistoryChart}; the number {@code 0} marks the end of a chain
 *       and resolves to no successor.</li>
 * </ol>
 *
 * <p>Bad input is handled softly — the offending record is reported and skipped while the rest load
 * (the partial-results contract). Three things are rejected this way: a numeric field that will not
 * parse; a record whose successor disagrees with the one its chart was first given (the successor is
 * a single per-chart value, see {@link PlayerHistoryChart}); and a non-zero successor that names a
 * chart which does not exist.
 *
 * <p>Mirrors C's {@code parse_history_chart} / {@code finish_parse_history} ({@code init.c}), where
 * the successor is likewise stored as an index at load and resolved to a reference only once every
 * chart has been read.
 *
 * @author Rowan Crowther
 */
public class HistoryAssembler implements Assembler<HistoryParseRecord, List<PlayerHistoryChart>> {
    /**
     * Assembles the parsed records into the resolved chart graph.
     *
     * @param records the raw {@link HistoryParseRecord}s in source order
     * @param errors  the soft-error sink; malformed numbers, non-uniform successors and dangling
     *                successor references are appended here rather than aborting the load
     * @return the assembled {@link PlayerHistoryChart}s, each linked to its successor
     * @author Rowan Crowther
     */
    @Override
    public List<PlayerHistoryChart> assemble(@NotNull List<HistoryParseRecord> records, @NotNull List<String> errors) {
        List<PlayerHistoryChart> charts = new ArrayList<>();
        Map<Integer, PlayerHistoryChart> entryMap = new HashMap<>();

        // First pass: fold the records into charts, one entry per record.
        for (HistoryParseRecord record : records) {
            int line = record.line();

            // Parse every number before touching a chart, so a malformed field discards the whole
            // record instead of leaving a half-built chart behind.
            int chartNumber = -1;
            try {
                chartNumber = Integer.parseInt(record.currentChart());
            } catch (NumberFormatException e) {
                errors.add("History entry starting line: " + line + " has " +
                        "a malformed chart integer: " + record.currentChart());
                continue;
            }
            int nextChartNumber = -1;
            try {
                nextChartNumber = Integer.parseInt(record.nextChart());
            } catch (NumberFormatException e) {
                errors.add("History entry starting line: " + line + " has " +
                        "a malformed next chart integer: " + record.nextChart());
                continue;
            }
            int roll = 0;
            try {
                roll = Integer.parseInt(record.percentage());
            } catch (NumberFormatException e) {
                errors.add("History entry starting line: " + line + " has " +
                        "a malformed percent integer: " + record.percentage());
                continue;
            }

            // Find the chart this record belongs to, keyed by number, or start a new one.
            PlayerHistoryChart chart = entryMap.get(chartNumber);
            if (chart == null) {
                chart = new PlayerHistoryChart(chartNumber, nextChartNumber);
                charts.add(chart);
                entryMap.put(chartNumber, chart);
            } else {
                // A chart holds a single successor, so every record for it must name the same one.
                if (chart.getSuccessorNumber() != nextChartNumber) {
                    errors.add("History entry starting at line: " + line + " has " +
                            "a successor chart number which is different from a preceding " +
                            "chart.");
                    continue;
                }
            }
            String phrase = record.phrase();
            PlayerHistoryEntry historyEntry = new PlayerHistoryEntry(roll, phrase);
            chart.addEntry(historyEntry);
        }

        // Second pass: resolve each chart's successor number to the actual chart (0 = end of chain).
        for (PlayerHistoryChart chart : charts) {
            int nextChartNumber = chart.getSuccessorNumber();
            if (nextChartNumber == 0) continue;

            int currentChartNumber = chart.getChartNumber();
            PlayerHistoryChart nextChart = entryMap.get(nextChartNumber);

            if (nextChart == null) {
                errors.add("Chart: " + currentChartNumber + " points to " +
                        "an unknown successor chart: " + nextChartNumber);
                continue;
            }
            chart.setSuccessor(nextChart);
        }

        return charts;
    }
}
