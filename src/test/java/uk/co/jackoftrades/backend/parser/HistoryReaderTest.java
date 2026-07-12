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

package uk.co.jackoftrades.backend.parser;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import uk.co.jackoftrades.middle.player.PlayerHistoryChart;
import uk.co.jackoftrades.middle.player.PlayerHistoryEntry;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-end throughput tests for {@link HistoryReader}: file text -> {@code HistoryLexer}/
 * {@code HistoryGrammar} -> {@link uk.co.jackoftrades.backend.parser.history.HistoryParseRecord}
 * -> {@link uk.co.jackoftrades.backend.parser.history.HistoryAssembler} -> {@link PlayerHistoryChart}.
 *
 * <p>These pin the assembler's job, which is the interesting half of History: the flat stream of
 * {@code chart:}/{@code phrase:} parse-records is <em>collapsed</em> into the chart graph — records
 * sharing a chart number fold into one {@link PlayerHistoryChart} carrying many
 * {@link PlayerHistoryEntry} rows, and each chart's {@code next} number is resolved in a second pass
 * into a direct successor reference (or {@code null} for the terminal {@code 0}). The suite also
 * covers the two invariants the assembler enforces beyond C — a chart's successor must be
 * <em>uniform</em> across its records, and a non-zero successor must <em>resolve</em> — plus both
 * error channels {@code GrammarDriver} threads through {@link ParseResult}: the soft
 * skip-and-continue {@code errors} list and the hard fail-closed channel.
 *
 * <p>Two notes on the fixtures:
 * <ul>
 *   <li>No registry seeding is required — History resolves entirely against its own file, with no
 *       cross-file references, so nothing needs {@code GameConstants} loaded first.</li>
 *   <li>The {@code HistoryLexer} guarantees {@code INTEGER} is a run of digits, so a
 *       <em>non-numeric</em> chart/next/percent value is a hard lexer error, not a soft
 *       {@code NumberFormatException}. The only route into the assembler's numeric {@code catch}
 *       blocks is integer <em>overflow</em>, which is how {@link #integerOverflowInPercentIsReportedAndRecordSkipped()}
 *       exercises that path (mirroring {@code WorldReader}).</li>
 * </ul>
 *
 * <p>{@link PlayerHistoryChart} exposes no accessor for its entry list, so {@link #entriesOf} reads
 * the private field reflectively (as {@code EgoItemReaderTest} does for {@code EgoItem}).
 *
 * @author Rowan Crowther
 */
class HistoryReaderTest {

    private static final String REAL_FILE = "lib/gamedata/history.txt";

    /**
     * The real file's declared record-count header: one per chart+phrase pair.
     */
    private static final int REAL_RECORD_COUNT = 165;
    /**
     * Distinct chart numbers in the real file — the number of assembled charts.
     */
    private static final int REAL_CHART_COUNT = 44;

    @TempDir
    Path tempDir;

    // ---- fixture helpers -------------------------------------------------

    private static String withHeader(int count, String body) {
        return "record-count:" + count + "\n" + body;
    }

    /**
     * One parse-record: a chart line and its phrase line.
     */
    private static String record(int chart, int next, int percent, String phrase) {
        return "chart:" + chart + ":" + next + ":" + percent + "\n"
                + "phrase:" + phrase + "\n";
    }

    private ParseResult<PlayerHistoryChart> load(String name, String content) throws IOException {
        Path file = tempDir.resolve(name);
        Files.writeString(file, content);
        return new HistoryReader().parseWithResults(file.toString());
    }

    private static PlayerHistoryChart byNumber(List<PlayerHistoryChart> charts, int number) {
        return charts.stream().filter(c -> c.getChartNumber() == number).findFirst().orElse(null);
    }

    /**
     * Reads {@link PlayerHistoryChart}'s private entry list — no getter is exposed.
     */
    @SuppressWarnings("unchecked")
    private static List<PlayerHistoryEntry> entriesOf(PlayerHistoryChart chart) {
        try {
            Field field = PlayerHistoryChart.class.getDeclaredField("entries");
            field.setAccessible(true);
            return (List<PlayerHistoryEntry>) field.get(chart);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("PlayerHistoryChart entry field not found", e);
        }
    }

    // ---- Happy path: the real file ---------------------------------------

    @Test
    void realFileLoadsAll44ChartsWithNoErrors() throws IOException {
        ParseResult<PlayerHistoryChart> result = new HistoryReader().parseWithResults(REAL_FILE);

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(REAL_CHART_COUNT, result.items().size());
    }

    @Test
    void everyPhraseIsPreservedAsAnEntry() throws IOException {
        // The 165 chart/phrase records collapse into 44 charts, but no phrase is lost:
        // the entries summed across all charts must equal the parse-record count.
        List<PlayerHistoryChart> charts = new HistoryReader().parseWithResults(REAL_FILE).items();

        int totalEntries = charts.stream().mapToInt(c -> entriesOf(c).size()).sum();
        assertEquals(REAL_RECORD_COUNT, totalEntries);
    }

    @Test
    void entriesAreGroupedByChartNumber() throws IOException {
        // Chart 1 has four chart:1 lines in the file, so its one PlayerHistoryChart holds four entries.
        List<PlayerHistoryChart> charts = new HistoryReader().parseWithResults(REAL_FILE).items();
        PlayerHistoryChart chartOne = byNumber(charts, 1);

        assertNotNull(chartOne);
        assertEquals(4, entriesOf(chartOne).size());
    }

    @Test
    void firstEntryOfChartOneHasExpectedRollAndText() throws IOException {
        // Entries keep file order, so entry 0 of chart 1 is 'chart:1:2:10' + its phrase.
        List<PlayerHistoryChart> charts = new HistoryReader().parseWithResults(REAL_FILE).items();
        PlayerHistoryEntry first = entriesOf(byNumber(charts, 1)).get(0);

        assertEquals(10, first.getRoll());
        assertEquals("You are the illegitimate and unacknowledged child", first.getText());
    }

    @Test
    void successorReferencesAreResolvedToTheNextChart() throws IOException {
        // chart:1:2 -> successor is the chart-2 object; chart:2:3 -> the chart-3 object.
        List<PlayerHistoryChart> charts = new HistoryReader().parseWithResults(REAL_FILE).items();
        PlayerHistoryChart chartOne = byNumber(charts, 1);

        assertNotNull(chartOne.getSuccessor());
        assertEquals(2, chartOne.getSuccessor().getChartNumber());
        assertSame(byNumber(charts, 2), chartOne.getSuccessor());
        assertEquals(3, byNumber(charts, 2).getSuccessor().getChartNumber());
    }

    @Test
    void terminalChartHasNullSuccessor() throws IOException {
        // chart:53:0 is an end-of-chain node: successor number 0 resolves to no reference.
        List<PlayerHistoryChart> charts = new HistoryReader().parseWithResults(REAL_FILE).items();
        PlayerHistoryChart terminal = byNumber(charts, 53);

        assertEquals(0, terminal.getSuccessorNumber());
        assertNull(terminal.getSuccessor());
    }

    // ---- Soft errors: skip-and-continue, partial results survive ---------

    @Test
    void mismatchedSuccessorWithinChartIsReportedAndEntrySkipped() throws IOException {
        // Same chart number, two different successors: the model can hold only one successor per
        // chart, so the contradicting record is rejected (its phrase is dropped) but the chart loads.
        ParseResult<PlayerHistoryChart> result = load("mismatch.txt", withHeader(2,
                record(5, 0, 50, "alpha")
                        + record(5, 7, 60, "beta")));

        assertEquals(1, result.items().size());
        PlayerHistoryChart chart = byNumber(result.items(), 5);
        assertEquals(1, entriesOf(chart).size());          // only 'alpha' survived
        assertEquals("alpha", entriesOf(chart).get(0).getText());
        assertTrue(result.errors().stream().anyMatch(e -> e.contains("successor")),
                result.errors()::toString);
    }

    @Test
    void danglingSuccessorIsReportedButChartStillLoads() throws IOException {
        // A non-zero successor naming a chart that does not exist is a soft error; the chart itself
        // still loads (partial-results contract) with no successor linked.
        ParseResult<PlayerHistoryChart> result = load("dangling.txt", withHeader(1,
                record(1, 999, 50, "lonely")));

        assertEquals(1, result.items().size());
        assertNull(byNumber(result.items(), 1).getSuccessor());
        assertTrue(result.errors().stream().anyMatch(e -> e.contains("unknown successor")),
                result.errors()::toString);
    }

    @Test
    void integerOverflowInPercentIsReportedAndRecordSkipped() throws IOException {
        // INTEGER is lexed as digits, so the only non-numeric route into the assembler's numeric
        // catch is overflow. An overflowing percent drops the whole record before the chart is built.
        ParseResult<PlayerHistoryChart> result = load("overflow.txt", withHeader(1,
                "chart:1:0:99999999999999999\nphrase:too big\n"));

        assertTrue(result.items().isEmpty());
        assertTrue(result.errors().stream().anyMatch(e -> e.contains("malformed percent")),
                result.errors()::toString);
    }

    @Test
    void recordCountMismatchIsReportedButValidChartStillLoads() throws IOException {
        // A wrong record-count header is soft: the records that parsed still load.
        ParseResult<PlayerHistoryChart> result = load("bad-count.txt", withHeader(5,
                record(1, 0, 50, "solo")));

        assertEquals(1, result.items().size());
        assertTrue(result.hasErrors());
    }

    // ---- Hard errors: fail closed, nothing loads -------------------------

    @Test
    void missingRecordCountHeaderIsAHardError() throws IOException {
        // The grammar requires the record-count header; without it the file fails closed.
        ParseResult<PlayerHistoryChart> result = load("no-header.txt", record(1, 0, 50, "x"));

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    @Test
    void phraseWithoutAChartIsAHardError() throws IOException {
        // A record is 'chart' then 'phrase'; a bare phrase has no chart to attach to.
        ParseResult<PlayerHistoryChart> result = load("orphan-phrase.txt", withHeader(1,
                "phrase:orphaned\n"));

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    @Test
    void chartWithoutAPhraseIsAHardError() throws IOException {
        // Every chart line must be followed by its phrase line; a trailing chart is a parse error.
        ParseResult<PlayerHistoryChart> result = load("chart-no-phrase.txt", withHeader(1,
                "chart:1:0:50\n"));

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }
}
