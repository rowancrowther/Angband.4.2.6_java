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
import uk.co.jackoftradesltd.middle.numerics.RandomValueUtils;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link PlayerBirth#getHistory(PlayerHistoryChart)}, the port of C's {@code get_history}
 * ({@code player-birth.c:330-345}):
 *
 * <pre>{@code
 * while (chart) {
 *     int roll = randint1(100);
 *     for (entry = chart->entries; entry; entry = entry->next)
 *         if (roll <= entry->roll)
 *             break;
 *     assert(entry);
 *     res = string_append(res, entry->text);
 *     chart = entry->succ;
 * }
 * }</pre>
 *
 * <p>Three things are worth pinning, and the expected values for all three come from the C above
 * and from {@code lib/gamedata/history.txt}, not from the port.
 *
 * <p><b>Selection is a threshold walk.</b> {@code roll <= entry->roll} takes the <em>first</em>
 * entry the roll does not exceed, so an entry's number is a cumulative cut-off rather than a
 * weight, and the comparison is inclusive. Rather than assert a distribution and hope, these tests
 * compute the roll for a seed by drawing {@code randint1(100)} themselves, reseed, and then assert
 * the phrase the C rule says that exact roll must produce — which pins the inclusive boundary at
 * every cut-off the sweep happens to land on, and would fail a port that used {@code <}.
 *
 * <p><b>Assembly is bare concatenation.</b> {@code string_append} inserts nothing, which is why the
 * shipped phrases carry their own leading capitals and trailing double spaces. A chain of charts
 * with one certain entry each therefore has a single exactly-predictable answer, whatever the seed.
 *
 * <p><b>The chain ends at a chart with no successor</b>, and its length is the number of rolls
 * taken — the Human's {@code 1 -> 2 -> 3 -> 50 -> ...} is one roll per link.
 *
 * <p>The port's two deliberate divergences from C are covered too: the successor is read off the
 * chart rather than the chosen entry, and C's {@code assert(entry)} — a crash, and only in a build
 * with asserts on — becomes a thrown exception when a chart's entries stop short of 100.
 *
 * <p>Class PlayerBirthGetHistoryTest coded on 260902, commented in full on 260902.
 *
 * @author Rowan Crowther
 */
class PlayerBirthGetHistoryTest {

    /**
     * Builds a chart and fills it, in the ascending-threshold order the file uses.
     *
     * @param chartNumber     the chart's number
     * @param successorNumber the number of the chart rolled next, {@code 0} to end the chain
     * @param rollsAndTexts   alternating cut-off and phrase, e.g. {@code 10, "a ", 100, "b "}
     * @return the chart
     */
    private static PlayerHistoryChart chart(int chartNumber, int successorNumber,
                                            Object... rollsAndTexts) {
        PlayerHistoryChart chart = new PlayerHistoryChart(chartNumber, successorNumber);
        for (int i = 0; i < rollsAndTexts.length; i += 2) {
            chart.addEntry(new PlayerHistoryEntry((Integer) rollsAndTexts[i],
                    (String) rollsAndTexts[i + 1]));
        }
        return chart;
    }

    /**
     * Chart 1 of {@code history.txt}, verbatim — cut-offs {@code 10/20/95/100}, so its four phrases
     * carry 10%, 10%, 75% and 5% of the rolls.
     *
     * @return the chart, with no successor
     */
    private static PlayerHistoryChart shippedChartOne() {
        return chart(1, 0,
                10, "You are the illegitimate and unacknowledged child ",
                20, "You are the illegitimate but acknowledged child ",
                95, "You are one of several children ",
                100, "You are the first child ");
    }

    /**
     * Chart 2 of {@code history.txt}, verbatim — the parentage fragment that follows chart 1.
     *
     * @return the chart, with no successor
     */
    private static PlayerHistoryChart shippedChartTwo() {
        return chart(2, 0,
                40, "of a Serf.  ",
                65, "of a Yeoman.  ",
                80, "of a Townsman.  ",
                90, "of a Guildsman.  ",
                96, "of a Landed Knight.  ",
                99, "of a Titled Noble.  ",
                100, "of a Royal Blood Line.  ");
    }

    /**
     * The roll {@code get_history} will take next from the seeded stream. Drawing it here and then
     * reseeding lets a test know the roll without the method telling it.
     *
     * @param seed the seed to set
     * @return the value {@code randint1(100)} yields first from that seed
     */
    private static int rollFor(long seed) {
        RandomValueUtils.stateInit(seed);
        return RandomValueUtils.randInt1(100);
    }

    /**
     * Applies C's selection rule directly: the first entry whose cut-off the roll does not exceed.
     *
     * @param chart the chart being rolled on
     * @param roll  the roll taken
     * @return that entry's phrase
     */
    private static String expectedPhrase(PlayerHistoryChart chart, int roll) {
        for (PlayerHistoryEntry entry : chart.getEntries()) {
            if (roll <= entry.getRoll()) return entry.getText();
        }
        throw new AssertionError("roll " + roll + " ran off the end of chart "
                + chart.getChartNumber());
    }

    /**
     * The single-chart case and the threshold rule.
     */
    @Nested
    @DisplayName("selecting an entry")
    class Selection {

        /**
         * A chart with one certain entry gives that entry's phrase and nothing else, on every seed.
         */
        @Test
        @DisplayName("a chart with a single 100 entry is a certainty")
        void singleCertainEntry() {
            for (long seed = 0; seed < 50; seed++) {
                RandomValueUtils.stateInit(seed);
                assertEquals("of a Serf.  ",
                        PlayerBirth.getHistory(chart(2, 0, 100, "of a Serf.  ")),
                        "seed " + seed);
            }
        }

        /**
         * For each seed the roll is drawn first, then the method is run from the same seed, and the
         * result is checked against the phrase C's rule assigns that roll. Any seed landing exactly
         * on 10, 20, 95 or 100 exercises the inclusive comparison.
         */
        @Test
        @DisplayName("takes the first entry the roll does not exceed")
        void thresholdWalk() {
            for (long seed = 0; seed < 400; seed++) {
                int roll = rollFor(seed);
                String expected = expectedPhrase(shippedChartOne(), roll);

                RandomValueUtils.stateInit(seed);
                assertEquals(expected, PlayerBirth.getHistory(shippedChartOne()),
                        "seed " + seed + " rolled " + roll);
            }
        }

        /**
         * The inclusive boundary again, but stated as a case rather than left to the sweep: a seed
         * whose roll is exactly a cut-off must take that entry, not the one after it. The seeds are
         * found by search so the test says which roll it is testing.
         */
        @Test
        @DisplayName("a roll landing exactly on a cut-off takes that entry")
        void inclusiveAtEachCutoff() {
            int[] cutoffs = {10, 20, 95, 100};
            String[] phrases = {
                    "You are the illegitimate and unacknowledged child ",
                    "You are the illegitimate but acknowledged child ",
                    "You are one of several children ",
                    "You are the first child "};

            for (int i = 0; i < cutoffs.length; i++) {
                Long seed = seedRolling(cutoffs[i]);
                assertNotNull(seed, "no seed found rolling " + cutoffs[i]);

                RandomValueUtils.stateInit(seed);
                assertEquals(phrases[i], PlayerBirth.getHistory(shippedChartOne()),
                        "roll of exactly " + cutoffs[i]);
            }
        }

        /**
         * Every entry of a shipped chart is reachable — a walk that stopped one entry early, or
         * never reached the last, would still pass a range check.
         */
        @Test
        @DisplayName("every entry of a shipped chart can be rolled")
        void allEntriesReachable() {
            List<String> seen = new ArrayList<>();
            for (long seed = 0; seed < 2000; seed++) {
                RandomValueUtils.stateInit(seed);
                String phrase = PlayerBirth.getHistory(shippedChartTwo());
                if (!seen.contains(phrase)) seen.add(phrase);
            }

            for (PlayerHistoryEntry entry : shippedChartTwo().getEntries()) {
                assertTrue(seen.contains(entry.getText()),
                        "never rolled: " + entry.getText());
            }
        }

        /**
         * Finds a seed whose first {@code randint1(100)} is the wanted roll.
         *
         * @param wanted the roll to look for
         * @return that seed, or {@code null} if none was found in the search
         */
        private Long seedRolling(int wanted) {
            for (long seed = 0; seed < 100_000; seed++) {
                if (rollFor(seed) == wanted) return seed;
            }
            return null;
        }
    }

    /**
     * Walking the chain and joining the fragments.
     */
    @Nested
    @DisplayName("walking the chain")
    class Chain {

        /**
         * Chart 1 to chart 2, both certain, appended with nothing between them. The shipped phrases
         * supply their own trailing spaces, so the join is bare concatenation.
         */
        @Test
        @DisplayName("appends fragments with no separator")
        void concatenatesInOrder() {
            PlayerHistoryChart two = chart(2, 0, 100, "of a Serf.  ");
            PlayerHistoryChart one = chart(1, 2, 100, "You are the first child ");
            one.setSuccessor(two);

            RandomValueUtils.stateInit(7);
            assertEquals("You are the first child of a Serf.  ", PlayerBirth.getHistory(one));
        }

        /**
         * A seven-chart chain, the length of the Human's {@code 1 -> 2 -> 3 -> 50 -> 51 -> 52 -> 53}
         * ({@code history.txt}), takes one roll per link and stops at the chart with no successor.
         * The roll count is read back off the stream: after the walk, the next value drawn must be
         * the eighth of that seed's sequence.
         */
        @Test
        @DisplayName("takes one roll per chart and stops at the end of the chain")
        void oneRollPerChart() {
            PlayerHistoryChart[] charts = new PlayerHistoryChart[7];
            StringBuilder expected = new StringBuilder();
            for (int i = 6; i >= 0; i--) {
                charts[i] = chart(i + 1, i == 6 ? 0 : i + 2, 100, "part" + i + " ");
                if (i < 6) charts[i].setSuccessor(charts[i + 1]);
            }
            for (int i = 0; i < 7; i++) expected.append("part").append(i).append(' ');

            RandomValueUtils.stateInit(11);
            assertEquals(expected.toString(), PlayerBirth.getHistory(charts[0]));
            int afterWalk = RandomValueUtils.randInt1(100);

            RandomValueUtils.stateInit(11);
            for (int i = 0; i < 7; i++) RandomValueUtils.randInt1(100);
            assertEquals(RandomValueUtils.randInt1(100), afterWalk,
                    "the walk did not draw exactly seven rolls");
        }

        /**
         * The successor is the chart's, not the chosen entry's — the port's one structural
         * divergence from C. Whichever of chart 1's four entries is rolled, the walk continues to
         * the same chart 2.
         */
        @Test
        @DisplayName("follows the chart's successor whichever entry is rolled")
        void successorIsPerChart() {
            for (long seed = 0; seed < 200; seed++) {
                PlayerHistoryChart one = shippedChartOne();
                PlayerHistoryChart two = chart(2, 0, 100, "of a Serf.  ");
                one.setSuccessor(two);

                int roll = rollFor(seed);
                String expected = expectedPhrase(shippedChartOne(), roll) + "of a Serf.  ";

                RandomValueUtils.stateInit(seed);
                assertEquals(expected, PlayerBirth.getHistory(one), "seed " + seed);
            }
        }

        /**
         * A chart with no successor ends the walk, and a chart resolved to none is the same thing:
         * the loop's condition is the chart being null, exactly as C's {@code while (chart)}.
         */
        @Test
        @DisplayName("a terminal chart contributes one fragment")
        void terminalChart() {
            RandomValueUtils.stateInit(3);
            String result = PlayerBirth.getHistory(chart(53, 0, 100, "the end."));
            assertEquals("the end.", result);
        }
    }

    /**
     * The edges C leaves to an assert or to a null pointer.
     */
    @Nested
    @DisplayName("edge cases")
    class Edges {

        /**
         * C's callers always pass a race's chart, which is never null, so its {@code NULL} return
         * for a null chart is unreachable. The port returns the empty string instead, which is the
         * same "nothing was assembled" without a null to hand back.
         */
        @Test
        @DisplayName("a null chart gives the empty string")
        void nullChart() {
            assertEquals("", PlayerBirth.getHistory(null));
        }

        /**
         * A chart whose entries stop short of 100 can be rolled past. C reaches {@code assert(entry)}
         * with a null; the port throws. A seed rolling exactly 100 makes it certain rather than
         * probable.
         */
        @Test
        @DisplayName("throws when the entries do not cover the roll")
        void rollRunsOffTheEnd() {
            Long seed = null;
            for (long candidate = 0; candidate < 100_000 && seed == null; candidate++) {
                if (rollFor(candidate) == 100) seed = candidate;
            }
            assertNotNull(seed, "no seed found rolling 100");

            PlayerHistoryChart short99 = chart(1, 0, 50, "low ", 99, "high ");
            RandomValueUtils.stateInit(seed);
            assertThrows(RuntimeException.class, () -> PlayerBirth.getHistory(short99));
        }

        /**
         * A chart with no entries at all cannot answer any roll, so it fails the same way.
         */
        @Test
        @DisplayName("throws on an empty chart")
        void emptyChart() {
            RandomValueUtils.stateInit(0);
            assertThrows(RuntimeException.class,
                    () -> PlayerBirth.getHistory(chart(1, 0)));
        }
    }
}
