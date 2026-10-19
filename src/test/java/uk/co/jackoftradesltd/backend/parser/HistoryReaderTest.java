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

package uk.co.jackoftradesltd.backend.parser;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import uk.co.jackoftradesltd.channel.parser.ParseResult;
import uk.co.jackoftradesltd.middle.player.PlayerHistoryChart;
import uk.co.jackoftradesltd.middle.player.PlayerHistoryEntry;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-end throughput tests for {@link HistoryReader}: file text -> {@code HistoryLexer}/
 * {@code HistoryGrammar} -> {@link uk.co.jackoftradesltd.backend.parser.history.HistoryParseRecord}
 * -> {@link uk.co.jackoftradesltd.backend.parser.history.HistoryAssembler} -> {@link PlayerHistoryChart}.
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
        // The phrase keeps its trailing space, as in C's history.txt; get_history joins phrases raw.
        assertEquals("You are the illegitimate and unacknowledged child ", first.getText());
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

    // ---- Against the upstream file and C's parser ------------------------

    /**
     * The 44 charts of the upstream {@code history.txt} as {@code chart:next:entries:rolls}, in the
     * order each chart first appears. Worked out from the upstream file with a line-by-line split,
     * not from the reader.
     */
    private static final String UPSTREAM_CHARTS =
            "1:2:4:10,20,95,100|2:3:7:40,65,80,90,96,99,100|3:50:3:20,80,100|4:1:8:15,30,50,70,85,95,98,100|5:6:2:60,100|6:9:3:40,70,100|7:8:2:60,100|8:9:3:75,95,100|9:54:6:40,70,87,95,99,100|10:11:2:85,100|11:3:8:20,30,40,50,80,95,99,100|13:14:2:85,100|14:3:5:20,50,75,95,100|16:17:2:25,100|17:18:6:10,25,75,90,99,100|18:57:3:15,85,100|19:20:2:25,100|20:2:2:20,100|21:22:6:30,60,75,90,95,100|22:62:4:5,95,99,100|23:24:3:20,80,100|24:25:6:15,40,70,85,95,100|25:26:5:25,50,75,95,100|26:67:3:20,95,100|50:51:6:20,60,70,80,90,100|51:52:3:70,90,100|52:53:5:30,70,80,90,100|53:0:5:10,30,80,90,100|54:55:3:85,95,100|55:56:2:75,100|56:0:4:75,85,95,100|57:58:2:99,100|58:59:2:90,100|59:60:2:75,100|60:61:4:25,60,90,100|61:0:1:100|62:63:4:60,85,99,100|63:64:3:33,66,100|64:65:3:33,66,100|65:66:4:25,50,75,100|66:0:3:33,66,100|67:68:5:10,40,80,99,100|68:69:4:40,60,95,100|69:0:3:10,90,100";

    @Test
    void everyShippedChartAgreesWithTheUpstreamTableInFirstAppearanceOrder() throws IOException {
        List<PlayerHistoryChart> charts = new HistoryReader().parseWithResults(REAL_FILE).items();

        StringBuilder actual = new StringBuilder();
        for (PlayerHistoryChart chart : charts) {
            if (actual.length() > 0) actual.append('|');
            actual.append(chart.getChartNumber()).append(':').append(chart.getSuccessorNumber()).append(':')
                    .append(entriesOf(chart).size()).append(':')
                    .append(entriesOf(chart).stream().map(e -> String.valueOf(e.getRoll()))
                            .collect(java.util.stream.Collectors.joining(",")));
        }
        assertEquals(UPSTREAM_CHARTS, actual.toString());
    }

    @Test
    void everyShippedPhraseComesBackWhole() throws IOException {
        // Split the file by hand, chart line then phrase line, and compare each chart's entries in
        // order. C's str field takes everything after 'phrase:', so trailing spaces are kept.
        List<PlayerHistoryChart> charts = new HistoryReader().parseWithResults(REAL_FILE).items();
        java.util.Map<Integer, List<String>> expected = new java.util.LinkedHashMap<>();
        int chart = -1;
        for (String line : Files.readAllLines(Path.of(REAL_FILE))) {
            if (line.startsWith("chart:")) {
                chart = Integer.parseInt(line.split(":")[1]);
            } else if (line.startsWith("phrase:")) {
                expected.computeIfAbsent(chart, k -> new java.util.ArrayList<>()).add(line.substring(7));
            }
        }

        assertEquals(REAL_CHART_COUNT, expected.size());
        for (PlayerHistoryChart c : charts) {
            List<String> texts = entriesOf(c).stream().map(PlayerHistoryEntry::getText).toList();
            assertEquals(expected.get(c.getChartNumber()), texts, "chart " + c.getChartNumber());
        }
    }

    @Test
    void chartsComeBackInFirstAppearanceOrderWhereCListsThemNewestFirst() throws IOException {
        // C pushes each new chart on the front of 'histories'; the port keeps them in a List.
        ParseResult<PlayerHistoryChart> result = load("order.txt", withHeader(3,
                record(7, 0, 100, "a") + record(3, 0, 100, "b") + record(5, 0, 100, "c")));

        assertEquals(List.of(7, 3, 5),
                result.items().stream().map(PlayerHistoryChart::getChartNumber).toList());
    }

    @Test
    void aMissingFileIsAnIOExceptionWhereCQuits() {
        assertThrows(IOException.class,
                () -> new HistoryReader().parseWithResults(tempDir.resolve("nothing-here.txt").toString()));
    }

    @Test
    void aPhraseKeepsColonsAndLeadingSpaces() throws IOException {
        // C's 'str text' field consumes the rest of the line, so nothing in it is a separator.
        ParseResult<PlayerHistoryChart> result = load("raw.txt", withHeader(1,
                record(1, 0, 100, "  a: b:c ")));

        assertFalse(result.hasErrors(), result.errors()::toString);
        assertEquals("  a: b:c ", entriesOf(byNumber(result.items(), 1)).get(0).getText());
    }

    @Test
    void commentsAndBlankLinesBetweenTheLinesAreSkipped() throws IOException {
        ParseResult<PlayerHistoryChart> result = load("comments.txt",
                "# header\n\nrecord-count:1\n\n# note\nchart:1:0:100\n\n# between\nphrase:kept\n\n");

        assertFalse(result.hasErrors(), result.errors()::toString);
        assertEquals("kept", entriesOf(byNumber(result.items(), 1)).get(0).getText());
    }

    @Test
    void aNegativeNextAndRollAreAcceptedAsInC() throws IOException {
        // 'next' and 'roll' are plain ints in C, so a minus sign is fine.
        ParseResult<PlayerHistoryChart> result = load("negative-roll.txt", withHeader(1,
                record(1, 0, -5, "x")));

        assertFalse(result.hasErrors(), result.errors()::toString);
        assertEquals(-5, entriesOf(byNumber(result.items(), 1)).get(0).getRoll());
    }

    // ---- Where the grammar differs from C --------------------------------

    @Test
    void aChartWithNoPhraseBetweenTwoRecordsIsAHardErrorHereWhereCAcceptsIt() throws IOException {
        // C makes an entry with NULL text; the grammar needs a phrase after every chart line.
        ParseResult<PlayerHistoryChart> result = load("no-phrase-mid.txt", withHeader(2,
                "chart:1:0:50\n" + record(1, 0, 100, "x")));

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    @Test
    void aSecondPhraseLineIsAHardErrorHereWhereCAppendsItToTheEntry() throws IOException {
        // parse_history_phrase string_appends onto the newest entry, so C reads 'onetwo'.
        ParseResult<PlayerHistoryChart> result = load("two-phrases.txt", withHeader(1,
                "chart:1:0:100\nphrase:one\nphrase:two\n"));

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    @Test
    void anEmptyPhraseIsAHardErrorAsInC() throws IOException {
        // C: strtok on an empty remainder gives NULL, so PARSE_ERROR_MISSING_FIELD. Both refuse it.
        ParseResult<PlayerHistoryChart> result = load("empty-phrase.txt", withHeader(1,
                "chart:1:0:100\nphrase:\n"));

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    @Test
    void aNegativeChartNumberLoadsHereWhereCRejectsIt() throws IOException {
        // 'chart' is a uint in init_parse_history and parser_parse refuses a leading '-'.
        ParseResult<PlayerHistoryChart> result = load("negative-chart.txt", withHeader(1,
                record(-1, 0, 100, "x")));

        assertFalse(result.hasErrors(), result.errors()::toString);
        assertEquals(-1, result.items().get(0).getChartNumber());
    }

    @Test
    void textAfterTheRollIsAHardErrorHereWhereCIgnoresIt() throws IOException {
        // The last int field is the final strtok token on ':' and the rest is dropped, so C reads
        // roll 10 from both 'chart:1:0:10:extra' and 'chart:1:0:10abc'.
        for (String tail : new String[]{":extra", "abc"}) {
            ParseResult<PlayerHistoryChart> result = load("tail.txt", withHeader(1,
                    "chart:1:0:10" + tail + "\nphrase:x\n"));

            assertTrue(result.items().isEmpty(), tail);
            assertTrue(result.hasErrors(), tail);
        }
    }

    @Test
    void aHexRollIsAHardErrorHereWhereCReadsItInBaseSixteen() throws IOException {
        // strtol base 0 gives 16 for 0x10.
        ParseResult<PlayerHistoryChart> result = load("hex.txt", withHeader(1,
                "chart:1:0:0x10\nphrase:x\n"));

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    @Test
    void aZeroPaddedChartIsReadInDecimalWhereCReadsItInOctal() throws IOException {
        // strtoul base 0 reads 010 as 8.
        ParseResult<PlayerHistoryChart> result = load("octal.txt", withHeader(1,
                "chart:010:0:100\nphrase:x\n"));

        assertFalse(result.hasErrors(), result.errors()::toString);
        assertEquals(10, result.items().get(0).getChartNumber());
    }

    @Test
    void anIndentedLineIsAHardErrorHereWhereCSkipsLeadingWhitespace() throws IOException {
        // parser_parse skips leading whitespace, so C reads an indented directive.
        ParseResult<PlayerHistoryChart> result = load("indented.txt", withHeader(1,
                "  chart:1:0:100\nphrase:x\n"));

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    @Test
    void aHeaderWithNoRecordsIsAHardErrorHereWhereCAcceptsAnEmptyFile() throws IOException {
        ParseResult<PlayerHistoryChart> result = load("empty.txt", "record-count:0\n");

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }
}
