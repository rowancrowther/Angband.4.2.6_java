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

package uk.co.jackoftradesltd.backend.parser.history;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftradesltd.backend.io.bespokeexceptions.DataFileParseException;
import uk.co.jackoftradesltd.backend.parser.ParseUtils;
import uk.co.jackoftradesltd.channel.parser.Assembler;
import uk.co.jackoftradesltd.middle.player.PlayerHistoryChart;
import uk.co.jackoftradesltd.middle.player.PlayerHistoryEntry;

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
 * <p><b>Every failure stops the load.</b> Each failure is logged at fatal level and thrown as a
 * {@link DataFileParseException} naming the file, and nothing is returned. Four things fail:
 * <ul>
 *   <li>a number that cannot be read (see {@link ParseUtils#parseIntString});</li>
 *   <li>a chart number written with a leading {@code -}, which C refuses as
 *       {@code PARSE_ERROR_NOT_NUMBER} because {@code chart} is a {@code uint} — {@code -0}
 *       counts, as C tests the text and not the value; a chart numbered {@code 0} is accepted, as in
 *       C;</li>
 *   <li>a record whose successor disagrees with the one its chart was first given;</li>
 *   <li>a non-zero successor that names a chart which does not exist, which is C's
 *       {@code finish_parse_history} returning {@code -1}.</li>
 * </ul>
 *
 * <p><b>Deviation from C: one successor per chart.</b> C stores the successor ({@code isucc}) on
 * every entry, so a chart may send different entries to different charts, and it accepts a repeat of
 * a chart number whatever successor it names. The port hoists the successor to the chart (see
 * {@link PlayerHistoryChart}) and so refuses a repeat that names a different one, where C would
 * load it. The shipped {@code history.txt} never varies the successor within a chart, so nothing
 * shipped is affected.
 *
 * <p>Two smaller differences leave no visible effect. The charts come back in first-appearance
 * order where C's list is newest first, and a chart number above {@link Integer#MAX_VALUE} is
 * clamped where C keeps the low bits of the {@code unsigned int}; the first because the one later
 * lookup goes by chart number, the second because ParseUtils records it as an accepted deviation.
 *
 * <p>Mirrors C's {@code parse_history_chart}, {@code findchart} and {@code finish_parse_history}
 * ({@code init.c}), where the successor is likewise stored as a number at load and resolved to a
 * reference only once every chart has been read. C pushes each entry onto the front of its chart
 * and reverses the list afterwards; the port appends, giving the same file order.
 *
 * <p>Class HistoryAssembler coded before 260915, commented in full on 261026.
 *
 * @author Rowan Crowther
 */
public class HistoryAssembler implements Assembler<HistoryParseRecord, List<PlayerHistoryChart>> {
    /**
     * Receives the fatal message before each {@link DataFileParseException} is thrown. Nothing is
     * logged on the success path.
     *
     * <p>Field logger coded before 260915, commented in full on 261026.
     */
    private static final Logger logger = LogManager.getLogger(HistoryAssembler.class);
    
    /**
     * Assembles the parsed records into the resolved chart graph, in two passes.
     *
     * <p><b>First pass.</b> For each record the chart number, successor number and roll are read
     * before any chart is touched, so a bad field throws before a half-built chart exists. A leading
     * {@code -} on the chart number throws (see the class comment); the following {@code < 0} test
     * can no longer fire once that is refused, and stays as a guard. The numbers are read as C's
     * {@code strtol} base 0 reads them, so {@code 010} is 8. The chart is then looked up by number,
     * which is C's {@code findchart}: a miss creates it and appends it to the result, and a hit
     * reuses it, throwing if this record's successor differs from the one the chart was created
     * with. The entry, a roll and the record's phrase, is appended in file order.
     *
     * <p><b>Second pass.</b> Each chart's successor number is resolved to the chart itself. A
     * successor of {@code 0} ends the chain and is skipped, leaving a {@code null} successor, as in
     * C's {@code finish_parse_history}. Any other number that names no chart throws, where C returns
     * {@code -1} from the finish function. The resolved chart is the same object held in the result,
     * so the returned graph is linked and no chart is copied.
     *
     * <p>Examples: {@code chart:1:2:10} and {@code chart:1:2:100} give one chart numbered 1 with two
     * entries and successor 2; {@code chart:5:0:100} gives one terminal chart; {@code chart:1:9:100}
     * with no chart 9 throws; {@code chart:-1:2:10} throws.
     *
     * <p>Function assemble commented in full before 260915, commented again in full on 261026.
     *
     * @param records  the raw {@link HistoryParseRecord}s in source order
     * @param errors   not used; every failure throws, and the parameter is kept to satisfy
     *                 {@link Assembler}
     * @param filename the file the records came from, named in each thrown message
     * @return the assembled {@link PlayerHistoryChart}s in first-appearance order, each linked to
     *         its successor
     * @throws DataFileParseException if a number cannot be read, a chart number is negative, a
     *                                chart is repeated with a different successor, or a successor
     *                                names no chart
     */
    @Override
    public List<PlayerHistoryChart> assemble(@NotNull List<HistoryParseRecord> records, @NotNull List<String> errors,
                                    String filename) {
        List<PlayerHistoryChart> charts = new ArrayList<>();
        Map<Integer, PlayerHistoryChart> entryMap = new HashMap<>();

        // First pass: fold the records into charts, one entry per record.
        for (HistoryParseRecord record : records) {
            int line = record.line();

            // Read every number before touching a chart, so a bad field throws before a
            // half-built chart exists.
            int chartNumber = 0;
            if (!ParseUtils.minusFound(record.currentChart())) {
                chartNumber = ParseUtils.parseIntString("History chart", record.currentChart(), line, filename, "chart number");
                if (chartNumber < 0) {
                    String msg = "History chart starting at line: " + line +" of file: " + filename + " has " +
                            "an illegal current chart value: " + record.currentChart();
                    logger.fatal(msg);
                    throw new DataFileParseException(msg);                    
                }
            } else {
                String msg = "History chart starting at line: " + line +" of file: " + filename + " has " +
                        "an illegal negative current chart value: " + record.currentChart();
                logger.fatal(msg);
                throw new DataFileParseException(msg);
            }
            int nextChartNumber = ParseUtils.parseIntString("History chart", record.nextChart(), line, filename, "next chart number");
            int roll = ParseUtils.parseIntString("History chart", record.percentage(), line, filename, "percent");

            // Find the chart this record belongs to, keyed by number, or start a new one.
            PlayerHistoryChart chart = entryMap.get(chartNumber);
            if (chart == null) {
                chart = new PlayerHistoryChart(chartNumber, nextChartNumber);
                charts.add(chart);
                entryMap.put(chartNumber, chart);
            } else if (chart.getSuccessorNumber() != nextChartNumber) {
                String msg = "History chart starting at line: " + line +" of file: " + filename + " has " +
                        "a successor chart value: " + record.nextChart() +
                        " that disagrees with " + chart.getSuccessorNumber();
                logger.fatal(msg);
                throw new DataFileParseException(msg);
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
                String msg = "History chart: " + currentChartNumber +" of file: " + filename + " has " +
                        "an unknown successor chart: " + nextChartNumber;
                logger.fatal(msg);
                throw new DataFileParseException(msg);
            }
            chart.setSuccessor(nextChart);
        }

        return charts;
    }
}
