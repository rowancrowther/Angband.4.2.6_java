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
import uk.co.jackoftradesltd.backend.io.bespokeexceptions.DataFileParseException;
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
 * covers the failures that stop the load — a negative chart number, a chart repeated with a
 * different successor (the one invariant the assembler enforces beyond C) and a non-zero successor
 * that names no chart — each thrown as a {@link DataFileParseException}, plus the hard fail-closed
 * channel {@code GrammarDriver} threads through {@link ParseResult} for grammar errors and the soft
 * {@code record-count:} mismatch.
 *
 * <p>Two notes on the fixtures:
 * <ul>
 *   <li>No registry seeding is required — History resolves entirely against its own file, with no
 *       cross-file references, so nothing needs {@code GameConstants} loaded first.</li>
 *   <li>The {@code HistoryLexer} guarantees {@code INTEGER} is an optional {@code -} and a run of
 *       digits, so a <em>non-numeric</em> chart/next/percent value is a hard lexer error. The only
 *       way to make the number reader work hard is integer <em>overflow</em>, which it clamps
 *       ({@link #integerOverflowInPercentIsClampedWhereCWrapsIt()}).</li>
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

    // ---- Failures that stop the load -------------------------------------

    /**
     * A bad record ends the load, as every parse error does in C ({@code print_error} in
     * {@code datafile.c}): the assembler throws a {@link DataFileParseException} naming the file and
     * nothing is returned.
     */
    private DataFileParseException assertStopsTheLoad(String fileName, String content, String message) {
        DataFileParseException thrown = assertThrows(DataFileParseException.class,
                () -> load(fileName, content));
        assertTrue(thrown.getMessage().contains(message), thrown::getMessage);
        assertTrue(thrown.getMessage().contains(fileName), thrown::getMessage);
        return thrown;
    }

    @Test
    void aChartRepeatedWithADifferentSuccessorStopsTheLoadHereWhereCAcceptsIt() {
        // C keeps isucc on every entry, so entries of chart 5 may name 0 and 7. The port holds one
        // successor per chart (PlayerHistoryChart), so the second record is refused. The message
        // names the line of the offending chart line and the successor the chart began with.
        DataFileParseException thrown = assertStopsTheLoad("mismatch.txt", withHeader(2,
                        record(5, 0, 50, "alpha") + record(5, 7, 60, "beta")),
                "a successor chart value: 7 that disagrees with 0");

        assertTrue(thrown.getMessage().contains("line: 4"), thrown::getMessage);
    }

    @Test
    void aChartRepeatedWithTheSameSuccessorKeepsEveryEntryInFileOrder() throws IOException {
        // findchart reuses chart 5, and finish_parse_history puts the entries back in file order.
        ParseResult<PlayerHistoryChart> result = load("repeat.txt", withHeader(3,
                record(5, 0, 10, "a") + record(6, 0, 100, "z") + record(5, 0, 100, "b")));

        assertFalse(result.hasErrors(), result.errors()::toString);
        assertEquals(2, result.items().size());
        List<PlayerHistoryEntry> entries = entriesOf(byNumber(result.items(), 5));
        assertEquals(List.of("a", "b"), entries.stream().map(PlayerHistoryEntry::getText).toList());
        assertEquals(List.of(10, 100), entries.stream().map(PlayerHistoryEntry::getRoll).toList());
    }

    @Test
    void danglingSuccessorStopsTheLoadAsInC() {
        // finish_parse_history returns -1 when findchart misses a non-zero isucc.
        assertStopsTheLoad("dangling.txt", withHeader(1, record(1, 999, 50, "lonely")),
                "has an unknown successor chart: 999");
    }

    @Test
    void aNegativeSuccessorIsNonZeroSoItIsLookedUpAndStopsTheLoadAsInC() {
        // isucc is an int, -1 is non-zero, and findchart(-1) matches no chart.
        assertStopsTheLoad("negative-next.txt", withHeader(1, record(1, -1, 50, "x")),
                "has an unknown successor chart: -1");
    }

    @Test
    void aSuccessorOfTheChartItselfLinksToItself() throws IOException {
        // findchart finds chart 4 for isucc 4, so the loop is allowed; the port links the same object.
        ParseResult<PlayerHistoryChart> result = load("self.txt", withHeader(1, record(4, 4, 100, "x")));

        PlayerHistoryChart chart = byNumber(result.items(), 4);
        assertSame(chart, chart.getSuccessor());
    }

    @Test
    void aSuccessorMayNameAChartThatAppearsLaterInTheFile() throws IOException {
        // The reason C resolves in finish_parse_history and not at load.
        ParseResult<PlayerHistoryChart> result = load("forward.txt", withHeader(2,
                record(1, 2, 100, "a") + record(2, 0, 100, "b")));

        assertSame(byNumber(result.items(), 2), byNumber(result.items(), 1).getSuccessor());
    }

    @Test
    void integerOverflowInPercentIsClampedWhereCWrapsIt() throws IOException {
        // strtol returns a long and C keeps the low 32 bits in the int field; ParseUtils clamps
        // instead (accepted deviation 2026-10-22). The record loads either way.
        ParseResult<PlayerHistoryChart> result = load("overflow.txt", withHeader(1,
                "chart:1:0:99999999999999999\nphrase:too big\n"));

        assertFalse(result.hasErrors(), result.errors()::toString);
        assertEquals(Integer.MAX_VALUE, entriesOf(byNumber(result.items(), 1)).get(0).getRoll());
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
    void aNegativeChartNumberStopsTheLoadAsInC() {
        // 'chart' is a uint in init_parse_history and parser_parse refuses a leading '-'.
        DataFileParseException thrown = assertStopsTheLoad("negative-chart.txt", withHeader(1,
                record(-1, 0, 100, "x")), "an illegal negative current chart value: -1");

        assertTrue(thrown.getMessage().contains("line: 2"), thrown::getMessage);
    }

    @Test
    void minusZeroIsRefusedAsInCBecauseTheTextIsTestedNotTheValue() {
        // parser_parse tests *tok == '-', so -0 is NOT_NUMBER even though its value is 0.
        assertStopsTheLoad("minus-zero.txt", withHeader(1, "chart:-0:0:100\nphrase:x\n"),
                "an illegal negative current chart value: -0");
    }

    @Test
    void chartZeroLoadsAsInC() throws IOException {
        // strtoul accepts 0 and findchart(0) misses, so C builds a chart whose idx is 0.
        ParseResult<PlayerHistoryChart> result = load("chart-zero.txt", withHeader(1,
                record(0, 0, 100, "x")));

        assertFalse(result.hasErrors(), result.errors()::toString);
        assertEquals(1, result.items().size());
        assertEquals(0, result.items().get(0).getChartNumber());
    }

    @Test
    void aChartZeroCanNotBeNamedAsASuccessorBecauseZeroEndsTheChain() throws IOException {
        // isucc 0 is skipped in finish_parse_history, so chart 1 ends the chain even though a
        // chart 0 exists.
        ParseResult<PlayerHistoryChart> result = load("zero-target.txt", withHeader(2,
                record(0, 0, 100, "zero") + record(1, 0, 100, "one")));

        assertNull(byNumber(result.items(), 1).getSuccessor());
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
    void zeroPaddedNumbersAreReadInOctalAsInC() throws IOException {
        // strtoul and strtol base 0 read 010 as 8 for the chart, the next and the roll alike.
        ParseResult<PlayerHistoryChart> result = load("octal.txt", withHeader(2,
                "chart:010:010:010\nphrase:x\n" + record(8, 8, 100, "y")));

        assertFalse(result.hasErrors(), result.errors()::toString);
        PlayerHistoryChart chart = byNumber(result.items(), 8);
        assertEquals(2, entriesOf(chart).size());
        assertEquals(8, chart.getSuccessorNumber());
        assertEquals(8, entriesOf(chart).get(0).getRoll());
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
