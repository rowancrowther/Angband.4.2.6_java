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

package uk.co.jackoftradesltd.middle.player;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.backend.parser.HistoryReader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests {@link PlayerHistoryChart}, the port of C's {@code struct history_chart} ({@code player.h})
 * as filled by {@code parse_history_chart} and {@code finish_parse_history} ({@code init.c}).
 *
 * <p>The struct has no behaviour of its own, so the tests pin what the port promises about it:
 * <ul>
 *   <li><b>The plain fields.</b> The chart number ({@code idx}) and raw successor number
 *       ({@code isucc}) come back as given; the resolved successor is {@code null} until
 *       {@code setSuccessor} runs, and stays {@code null} for a terminal chart.</li>
 *   <li><b>Entry order.</b> C pushes entries onto the front of a list and reverses it in
 *       {@code finish_parse_history}, ending in file order; the port appends, so the entries must
 *       come back in the order they were added.</li>
 *   <li><b>The shipped data.</b> The expected values are read straight from the {@code chart:} and
 *       {@code phrase:} lines of {@code lib/gamedata/history.txt} by a small line reader in this
 *       test, not from the port's own reader, and compared with what {@link HistoryReader} built:
 *       chart number, successor number, entry order, rolls and texts. The file also has to satisfy
 *       what {@code get_history} assumes (rolls ascend, each chart ends at 100) and what the port's
 *       hoist assumes (one successor per chart).</li>
 * </ul>
 *
 * <p>Class PlayerHistoryChartTest coded on 261011, commented in full on 261011.
 *
 * @author Rowan Crowther
 */
class PlayerHistoryChartTest {

    private static final String REAL_FILE = "lib/gamedata/history.txt";

    /** One {@code chart:} line and the {@code phrase:} line under it, as the C parser sees them. */
    private record RawRecord(int chart, int next, int roll, String phrase) {
    }

    /**
     * Reads {@code history.txt} the way {@code parse_history_chart} / {@code parse_history_phrase}
     * do: a {@code chart:idx:next:roll} line opens a record and the following {@code phrase:} line
     * supplies its text. Records are grouped by chart number in file order.
     */
    private static Map<Integer, List<RawRecord>> readRawFile() throws IOException {
        Map<Integer, List<RawRecord>> byChart = new LinkedHashMap<>();
        int chart = 0, next = 0, roll = 0;
        boolean open = false;
        for (String line : Files.readAllLines(Path.of(REAL_FILE))) {
            if (line.startsWith("chart:")) {
                String[] f = line.split(":");
                chart = Integer.parseInt(f[1]);
                next = Integer.parseInt(f[2]);
                roll = Integer.parseInt(f[3]);
                open = true;
            } else if (line.startsWith("phrase:") && open) {
                byChart.computeIfAbsent(chart, k -> new ArrayList<>())
                        .add(new RawRecord(chart, next, roll, line.substring("phrase:".length())));
                open = false;
            }
        }
        return byChart;
    }

    private static PlayerHistoryChart byNumber(List<PlayerHistoryChart> charts, int number) {
        return charts.stream().filter(c -> c.getChartNumber() == number).findFirst().orElse(null);
    }

    @Nested
    @DisplayName("Construction and plain accessors")
    class Construction {

        @Test
        @DisplayName("chart number and successor number come back as given")
        void numbersRoundTrip() {
            PlayerHistoryChart chart = new PlayerHistoryChart(18, 57);

            assertEquals(18, chart.getChartNumber());
            assertEquals(57, chart.getSuccessorNumber());
        }

        @Test
        @DisplayName("a new chart has no entries and no resolved successor")
        void startsEmptyAndUnresolved() {
            PlayerHistoryChart chart = new PlayerHistoryChart(1, 2);

            assertTrue(chart.getEntries().isEmpty());
            assertNull(chart.getSuccessor());
        }

        @Test
        @DisplayName("successor number 0 is the end-of-chain marker and the successor stays null")
        void terminalChart() {
            PlayerHistoryChart chart = new PlayerHistoryChart(53, 0);

            assertEquals(0, chart.getSuccessorNumber());
            assertNull(chart.getSuccessor());
        }
    }

    @Nested
    @DisplayName("Successor resolution")
    class Successor {

        @Test
        @DisplayName("setSuccessor makes getSuccessor return that very chart, number untouched")
        void resolvedSuccessor() {
            PlayerHistoryChart one = new PlayerHistoryChart(1, 2);
            PlayerHistoryChart two = new PlayerHistoryChart(2, 3);

            one.setSuccessor(two);

            assertSame(two, one.getSuccessor());
            assertEquals(2, one.getSuccessorNumber());
            assertEquals(2, one.getSuccessor().getChartNumber());
        }

        @Test
        @DisplayName("an unresolved non-zero successor is null but its number still says so")
        void unresolvedNonZeroSuccessor() {
            // The C loader would fail here; the port leaves the link null. The raw number is the
            // only thing that tells this case from a genuine end of chain.
            PlayerHistoryChart chart = new PlayerHistoryChart(9, 54);

            assertNull(chart.getSuccessor());
            assertEquals(54, chart.getSuccessorNumber());
        }

        @Test
        @DisplayName("charts chain through their successors")
        void chainWalk() {
            PlayerHistoryChart a = new PlayerHistoryChart(1, 2);
            PlayerHistoryChart b = new PlayerHistoryChart(2, 3);
            PlayerHistoryChart c = new PlayerHistoryChart(3, 0);
            a.setSuccessor(b);
            b.setSuccessor(c);

            List<Integer> visited = new ArrayList<>();
            for (PlayerHistoryChart at = a; at != null; at = at.getSuccessor()) {
                visited.add(at.getChartNumber());
            }

            assertEquals(List.of(1, 2, 3), visited);
        }
    }

    @Nested
    @DisplayName("Entries")
    class Entries {

        @Test
        @DisplayName("addEntry appends, so entries come back in the order they were added")
        void fileOrderIsKept() {
            PlayerHistoryChart chart = new PlayerHistoryChart(1, 2);
            PlayerHistoryEntry first = new PlayerHistoryEntry(10, "a");
            PlayerHistoryEntry second = new PlayerHistoryEntry(20, "b");
            PlayerHistoryEntry third = new PlayerHistoryEntry(100, "c");

            chart.addEntry(first);
            chart.addEntry(second);
            chart.addEntry(third);

            assertEquals(3, chart.getEntries().size());
            assertSame(first, chart.getEntries().get(0));
            assertSame(second, chart.getEntries().get(1));
            assertSame(third, chart.getEntries().get(2));
        }

        @Test
        @DisplayName("nothing sorts the entries: a descending add stays descending")
        void noSorting() {
            // The ascending order the roll needs comes from the file, not from the chart.
            PlayerHistoryChart chart = new PlayerHistoryChart(1, 0);
            chart.addEntry(new PlayerHistoryEntry(100, "late"));
            chart.addEntry(new PlayerHistoryEntry(10, "early"));

            assertEquals(100, chart.getEntries().get(0).getRoll());
            assertEquals(10, chart.getEntries().get(1).getRoll());
        }

        @Test
        @DisplayName("getEntries is the chart's own live list, not a copy")
        void liveList() {
            PlayerHistoryChart chart = new PlayerHistoryChart(1, 0);
            List<PlayerHistoryEntry> view = chart.getEntries();

            chart.addEntry(new PlayerHistoryEntry(100, "x"));

            assertSame(view, chart.getEntries());
            assertEquals(1, view.size());
        }
    }

    @Nested
    @DisplayName("Against the shipped history.txt")
    class ShippedData {

        @Test
        @DisplayName("the chart set is exactly the chart numbers in the file")
        void sameCharts() throws IOException {
            Map<Integer, List<RawRecord>> raw = readRawFile();
            List<PlayerHistoryChart> charts = new HistoryReader().parseWithResults(REAL_FILE).items();

            assertEquals(44, raw.size());
            assertEquals(raw.size(), charts.size());
            for (int number : raw.keySet()) {
                assertNotNull(byNumber(charts, number), "chart " + number + " missing");
            }
        }

        @Test
        @DisplayName("every chart has the file's successor number, entry order, rolls and texts")
        void everyChartMatchesTheFile() throws IOException {
            Map<Integer, List<RawRecord>> raw = readRawFile();
            List<PlayerHistoryChart> charts = new HistoryReader().parseWithResults(REAL_FILE).items();

            for (Map.Entry<Integer, List<RawRecord>> e : raw.entrySet()) {
                PlayerHistoryChart chart = byNumber(charts, e.getKey());
                List<RawRecord> expected = e.getValue();

                assertEquals(expected.get(0).next(), chart.getSuccessorNumber(),
                        "successor number of chart " + e.getKey());
                assertEquals(expected.size(), chart.getEntries().size(),
                        "entry count of chart " + e.getKey());
                for (int i = 0; i < expected.size(); i++) {
                    PlayerHistoryEntry entry = chart.getEntries().get(i);
                    assertEquals(expected.get(i).roll(), entry.getRoll(),
                            "roll " + i + " of chart " + e.getKey());
                    assertEquals(expected.get(i).phrase(), entry.getText(),
                            "text " + i + " of chart " + e.getKey());
                }
            }
        }

        @Test
        @DisplayName("every chart's lines name one successor — the assumption behind the hoist")
        void successorIsUniformPerChart() throws IOException {
            for (Map.Entry<Integer, List<RawRecord>> e : readRawFile().entrySet()) {
                int first = e.getValue().get(0).next();
                for (RawRecord r : e.getValue()) {
                    assertEquals(first, r.next(), "chart " + e.getKey() + " varies its successor");
                }
            }
        }

        @Test
        @DisplayName("rolls ascend strictly and every chart ends at 100, as get_history needs")
        void rollsAreCumulativeThresholds() throws IOException {
            List<PlayerHistoryChart> charts = new HistoryReader().parseWithResults(REAL_FILE).items();

            for (PlayerHistoryChart chart : charts) {
                List<PlayerHistoryEntry> entries = chart.getEntries();
                for (int i = 1; i < entries.size(); i++) {
                    assertTrue(entries.get(i).getRoll() > entries.get(i - 1).getRoll(),
                            "chart " + chart.getChartNumber() + " does not ascend at entry " + i);
                }
                assertEquals(100, entries.get(entries.size() - 1).getRoll(),
                        "chart " + chart.getChartNumber() + " does not end at 100");
            }
        }

        @Test
        @DisplayName("every non-zero successor number resolves to the chart with that number")
        void successorsResolve() throws IOException {
            List<PlayerHistoryChart> charts = new HistoryReader().parseWithResults(REAL_FILE).items();

            for (PlayerHistoryChart chart : charts) {
                if (chart.getSuccessorNumber() == 0) {
                    assertNull(chart.getSuccessor(), "terminal chart " + chart.getChartNumber());
                } else {
                    assertNotNull(chart.getSuccessor(), "chart " + chart.getChartNumber());
                    assertEquals(chart.getSuccessorNumber(), chart.getSuccessor().getChartNumber());
                    assertSame(byNumber(charts, chart.getSuccessorNumber()), chart.getSuccessor());
                }
            }
        }

        @Test
        @DisplayName("the Human chain 1, 2, 3, 50, 51, 52, 53 ends at the terminal chart 53")
        void humanChain() throws IOException {
            List<PlayerHistoryChart> charts = new HistoryReader().parseWithResults(REAL_FILE).items();

            List<Integer> visited = new ArrayList<>();
            for (PlayerHistoryChart at = byNumber(charts, 1); at != null; at = at.getSuccessor()) {
                visited.add(at.getChartNumber());
            }

            assertEquals(List.of(1, 2, 3, 50, 51, 52, 53), visited);
        }

        @Test
        @DisplayName("chart 1's rolls are 10, 20, 95, 100 in that order")
        void chartOneRolls() throws IOException {
            List<PlayerHistoryChart> charts = new HistoryReader().parseWithResults(REAL_FILE).items();

            List<Integer> rolls = new ArrayList<>();
            for (PlayerHistoryEntry entry : byNumber(charts, 1).getEntries()) {
                rolls.add(entry.getRoll());
            }

            assertEquals(List.of(10, 20, 95, 100), rolls);
        }
    }
}
