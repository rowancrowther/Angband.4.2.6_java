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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests {@link PlayerHistoryEntry}, the port of C's {@code struct history_entry} ({@code player.h})
 * as filled by {@code parse_history_chart} and {@code parse_history_phrase} ({@code init.c}) and
 * read by {@code get_history} ({@code player-birth.c}).
 *
 * <p>The struct has no behaviour of its own, so the tests pin what the port promises about it:
 * <ul>
 *   <li><b>The two fields.</b> {@code roll} and {@code text} come back exactly as given, with no
 *       trimming and no range check, and one entry's values do not leak into another's.</li>
 *   <li><b>The threshold rule.</b> {@code get_history} takes the first entry whose {@code roll} is
 *       at least the draw. The expected pick for every draw from 1 to 100 is worked out by hand from
 *       the four {@code chart:1} lines of {@code history.txt} (thresholds 10, 20, 95, 100), not
 *       derived from the port, and compared with what a first-match walk over the loaded entries
 *       gives.</li>
 *   <li><b>The shipped data.</b> Every loaded entry has a threshold in the 1-100 range of the
 *       draw, and its text is the {@code phrase:} line unchanged. Counted against the file with a
 *       plain line reader: 165 entries, 149 whose text ends in a space and 16 that do not.</li>
 * </ul>
 *
 * <p>Class PlayerHistoryEntryTest coded on 261012, commented in full on 261012.
 *
 * @author Rowan Crowther
 */
class PlayerHistoryEntryTest {

    private static final String REAL_FILE = "lib/gamedata/history.txt";

    /** Index of the entry C picks for a draw, by the {@code roll <= entry->roll} rule. */
    private static int pick(List<PlayerHistoryEntry> entries, int draw) {
        for (int i = 0; i < entries.size(); i++) {
            if (draw <= entries.get(i).getRoll()) {
                return i;
            }
        }
        return -1;
    }

    private static List<PlayerHistoryChart> loadShipped() throws IOException {
        return new HistoryReader().parseWithResults(REAL_FILE).items();
    }

    @Nested
    @DisplayName("Construction and accessors")
    class Construction {

        @Test
        @DisplayName("roll and text come back as given")
        void roundTrip() {
            PlayerHistoryEntry entry = new PlayerHistoryEntry(40, "of a Serf.  ");

            assertEquals(40, entry.getRoll());
            assertEquals("of a Serf.  ", entry.getText());
        }

        @Test
        @DisplayName("text is kept exactly: trailing space, leading space and inner spacing")
        void textIsNotTrimmed() {
            // history.txt: "Note that the 'spacing' in the 'description' lines is very important!"
            assertEquals("You are the first child ",
                    new PlayerHistoryEntry(100, "You are the first child ").getText());
            assertEquals(" and so on", new PlayerHistoryEntry(1, " and so on").getText());
            assertEquals("a  b", new PlayerHistoryEntry(1, "a  b").getText());
            assertEquals("", new PlayerHistoryEntry(1, "").getText());
        }

        @Test
        @DisplayName("the boundary rolls 1 and 100 are stored as they are")
        void boundaryRolls() {
            assertEquals(1, new PlayerHistoryEntry(1, "x").getRoll());
            assertEquals(100, new PlayerHistoryEntry(100, "x").getRoll());
        }

        @Test
        @DisplayName("nothing is validated: 0, negative and over-100 thresholds are stored unchanged")
        void noRangeCheck() {
            // C's struct is a bare int with no check either; a bad value is a data-file fault that
            // get_history trips over later, not something the entry polices.
            assertEquals(0, new PlayerHistoryEntry(0, "x").getRoll());
            assertEquals(-5, new PlayerHistoryEntry(-5, "x").getRoll());
            assertEquals(101, new PlayerHistoryEntry(101, "x").getRoll());
        }

        @Test
        @DisplayName("two entries hold independent values")
        void independentInstances() {
            PlayerHistoryEntry a = new PlayerHistoryEntry(10, "first ");
            PlayerHistoryEntry b = new PlayerHistoryEntry(20, "second ");

            assertEquals(10, a.getRoll());
            assertEquals("first ", a.getText());
            assertEquals(20, b.getRoll());
            assertEquals("second ", b.getText());
        }
    }

    @Nested
    @DisplayName("The threshold rule get_history applies")
    class ThresholdRule {

        @Test
        @DisplayName("chart 1 of history.txt: draws 1-10, 11-20, 21-95 and 96-100 pick entries 0 to 3")
        void chartOneBands() throws IOException {
            PlayerHistoryChart chartOne = loadShipped().stream()
                    .filter(c -> c.getChartNumber() == 1).findFirst().orElseThrow();
            List<PlayerHistoryEntry> entries = chartOne.getEntries();

            assertEquals(4, entries.size());
            assertEquals(10, entries.get(0).getRoll());
            assertEquals(20, entries.get(1).getRoll());
            assertEquals(95, entries.get(2).getRoll());
            assertEquals(100, entries.get(3).getRoll());

            for (int draw = 1; draw <= 100; draw++) {
                int expected = draw <= 10 ? 0 : draw <= 20 ? 1 : draw <= 95 ? 2 : 3;
                assertEquals(expected, pick(entries, draw), "draw " + draw);
            }
        }

        @Test
        @DisplayName("a draw equal to a threshold picks that entry, not the next (<= not <)")
        void drawOnThresholdPicksThatEntry() {
            List<PlayerHistoryEntry> entries = List.of(
                    new PlayerHistoryEntry(10, "a"),
                    new PlayerHistoryEntry(20, "b"),
                    new PlayerHistoryEntry(100, "c"));

            assertEquals(0, pick(entries, 10));
            assertEquals(1, pick(entries, 11));
            assertEquals(1, pick(entries, 20));
            assertEquals(2, pick(entries, 21));
            assertEquals(2, pick(entries, 100));
        }

        @Test
        @DisplayName("every chart in the file is covered for every draw, so some entry is always picked")
        void everyDrawPicksSomething() throws IOException {
            for (PlayerHistoryChart chart : loadShipped()) {
                for (int draw = 1; draw <= 100; draw++) {
                    assertNotEquals(-1, pick(chart.getEntries(), draw),
                            "chart " + chart.getChartNumber() + " draw " + draw);
                }
            }
        }
    }

    @Nested
    @DisplayName("Against the shipped history.txt")
    class ShippedData {

        @Test
        @DisplayName("every loaded threshold is in the 1-100 range of the draw")
        void rollsInRange() throws IOException {
            int count = 0;
            for (PlayerHistoryChart chart : loadShipped()) {
                for (PlayerHistoryEntry entry : chart.getEntries()) {
                    assertTrue(entry.getRoll() >= 1 && entry.getRoll() <= 100,
                            "chart " + chart.getChartNumber() + " roll " + entry.getRoll());
                    count++;
                }
            }
            assertEquals(165, count);
        }

        @Test
        @DisplayName("149 entries end in a space and 16 do not, as the phrase lines of the file do")
        void trailingSpaceCounts() throws IOException {
            int fileSpaced = 0, fileUnspaced = 0;
            for (String line : Files.readAllLines(Path.of(REAL_FILE))) {
                if (line.startsWith("phrase:")) {
                    if (line.endsWith(" ")) fileSpaced++;
                    else fileUnspaced++;
                }
            }
            assertEquals(149, fileSpaced);
            assertEquals(16, fileUnspaced);

            int spaced = 0, unspaced = 0;
            for (PlayerHistoryChart chart : loadShipped()) {
                for (PlayerHistoryEntry entry : chart.getEntries()) {
                    if (entry.getText().endsWith(" ")) spaced++;
                    else unspaced++;
                }
            }
            assertEquals(fileSpaced, spaced);
            assertEquals(fileUnspaced, unspaced);
        }

        @Test
        @DisplayName("the unspaced phrases are the ends of chains: their charts have successor 0")
        void unspacedPhrasesEndChains() throws IOException {
            // A phrase with no trailing space can only be followed by nothing, or the joined
            // biography would run two words together.
            for (PlayerHistoryChart chart : loadShipped()) {
                for (PlayerHistoryEntry entry : chart.getEntries()) {
                    if (!entry.getText().endsWith(" ")) {
                        assertEquals(0, chart.getSuccessorNumber(),
                                "chart " + chart.getChartNumber() + " text '" + entry.getText() + "'");
                    }
                }
            }
        }
    }
}
