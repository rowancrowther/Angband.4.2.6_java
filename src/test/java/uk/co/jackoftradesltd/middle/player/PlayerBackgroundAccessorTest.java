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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.enums.Stats;
import uk.co.jackoftradesltd.middle.objects.ElementInfo;
import uk.co.jackoftradesltd.middle.objects.enums.ElementEnum;
import uk.co.jackoftradesltd.middle.objects.enums.EquipmentSlotsEnum;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.player.enums.PlayerFlag;
import uk.co.jackoftradesltd.middle.player.enums.PlayerSkill;
import uk.co.jackoftradesltd.testsupport.SeededPlayerRegistry;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Tests the two ends of a character's background: {@link PlayerRace#getHistory()}, the chart the
 * text is generated from ({@code p->race->history}, {@code player.h:200}), and
 * {@link Player#setPlayerHistory(String)}, the finished text ({@code p->history}). C joins them in
 * one line — {@code p->history = get_history(p->race->history)} ({@code player-birth.c:1027}) — and
 * that line is what these two accessors have to make possible.
 *
 * <p>They are tested together because separately each is a field read and a field write, and what
 * is actually worth asserting is the relationship: a chart in, a string out, with the chart
 * belonging to the race and the string belonging to the character. The chart fixtures are charts 1
 * and 2 of {@code lib/gamedata/history.txt} rather than invented ones, and are chained the way the
 * file chains them, so the phrase asserted is a phrase a real Human can be born with.
 *
 * <p>The generation in the middle is {@link PlayerBirth#getHistory}, which has its own suite; it is
 * used here only as the thing that carries a value from one accessor to the other, with a
 * single-entry chart so the roll cannot change the answer.
 *
 * <p>Class PlayerBackgroundAccessorTest coded on 260902, commented in full on 260902.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class PlayerBackgroundAccessorTest {

    /**
     * The character whose background is written, fresh for each test.
     */
    private Player player;

    /**
     * Builds a chart with the given entries.
     *
     * @param chartNumber     the chart's own number
     * @param successorNumber the number of the chart to move to next, or zero for none
     * @param rollsAndTexts   alternating cut-off and phrase
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
     * Builds a race carrying the given chart, with the remaining fields filled from the Human
     * record of {@code p_race.txt}.
     *
     * @param name  the race's name
     * @param chart the chart the race's backgrounds start from, or {@code null} for none
     * @return a race whose only interesting field is its history chart
     */
    private static PlayerRace race(String name, PlayerHistoryChart chart) {
        List<EquipSlot> slots = new ArrayList<>(List.of(
                new EquipSlot(EquipmentSlotsEnum.EQUIP_WEAPON, "weapon")));
        Map<ElementEnum, ElementInfo> resists = new EnumMap<>(ElementEnum.class);
        return new PlayerRace(name, 0, 10, 100, 14, 6, 69, 10, 165, 35, 0,
                new PlayerBody("Humanoid", slots),
                new EnumMap<>(Stats.class), new EnumMap<>(PlayerSkill.class),
                new Flag<>(ObjectFlag.class), new Flag<>(PlayerFlag.class),
                chart, resists);
    }

    /**
     * Builds a new character.
     */
    @BeforeEach
    void build() {
        player = new Player();
    }

    /**
     * Reads the private {@code history} field, which has a writer but no reader — C's
     * {@code p->history} is read by the character sheet, which the port has not reached.
     *
     * @return the character's background text
     * @throws Exception if the field cannot be reached
     */
    private String history() throws Exception {
        Field field = Player.class.getDeclaredField("history");
        field.setAccessible(true);
        return (String) field.get(player);
    }

    /**
     * The race's end: the chart generation starts from.
     */
    @Nested
    @DisplayName("the race's history chart")
    class RaceChart {

        /**
         * The accessor hands back the chart the race was built with, identity included. C assigns
         * the pointer, and generation walks from that object to its successors, so anything other
         * than the same chart would start the walk somewhere else.
         */
        @Test
        @DisplayName("is the chart the race was given")
        void returnsTheChart() {
            PlayerHistoryChart chart = chart(1, 2, 100, "You are the first child ");
            assertSame(chart, race("Human", chart).getHistory());
        }

        /**
         * The chart survives {@link PlayerRace#copy()}, which is the form {@code playerGenerate}
         * hands the player. A copy that dropped it would leave every character with an empty
         * background.
         */
        @Test
        @DisplayName("survives a copy")
        void survivesACopy() {
            PlayerHistoryChart chart = chart(1, 2, 100, "You are the first child ");
            assertSame(chart, race("Human", chart).copy().getHistory());
        }

        /**
         * A race need not have a chart. C's field is a pointer and {@code get_history} treats a
         * null one as an empty biography rather than an error, so the accessor has to be able to
         * report its absence.
         */
        @Test
        @DisplayName("may be absent")
        void mayBeAbsent() {
            assertNull(race("Human", null).getHistory());
        }
    }

    /**
     * The character's end: the finished text.
     */
    @Nested
    @DisplayName("the character's background text")
    class BackgroundText {

        /**
         * A fresh character has no background, so a test that finds text has found text that was
         * written.
         *
         * @throws Exception if the field cannot be reached
         */
        @Test
        @DisplayName("starts unset")
        void startsUnset() throws Exception {
            assertNull(history());
        }

        /**
         * The text is stored as given. Nothing is trimmed, capitalised or appended — C's
         * {@code p->history} is the string {@code get_history} built and the character sheet
         * prints.
         *
         * @throws Exception if the field cannot be reached
         */
        @Test
        @DisplayName("is stored verbatim")
        void storedVerbatim() throws Exception {
            player.setPlayerHistory("You are one of several children of a Serf.  ");
            assertEquals("You are one of several children of a Serf.  ", history());
        }

        /**
         * A second write replaces the first. C frees the old string before assigning
         * ({@code player-birth.c:1024}), which is a statement that the old value is gone rather
         * than kept alongside the new one — and {@code player_generate} does exactly this whenever
         * a different race is chosen on the birth screen.
         *
         * @throws Exception if the field cannot be reached
         */
        @Test
        @DisplayName("a second write replaces the first")
        void secondWriteReplaces() throws Exception {
            player.setPlayerHistory("of a Serf.  ");
            player.setPlayerHistory("of a Royal Blood Line.  ");
            assertEquals("of a Royal Blood Line.  ", history());
        }
    }

    /**
     * The two ends joined, which is the only form C ever uses them in.
     */
    @Nested
    @DisplayName("chart to text")
    class ChartToText {

        /**
         * The line C writes, run end to end. The chart is a chain of two single-entry charts, so
         * the roll has nothing to choose between and the expected string is the two phrases of
         * {@code history.txt} concatenated in chart order.
         *
         * @throws Exception if the field cannot be reached
         */
        @Test
        @DisplayName("generates the race's background into the character")
        void generatesIntoTheCharacter() throws Exception {
            PlayerHistoryChart first = chart(1, 2, 100, "You are the first child ");
            first.setSuccessor(chart(2, 0, 100, "of a Serf.  "));
            PlayerRace race = race("Human", first);

            player.setPlayerHistory(PlayerBirth.getHistory(race.getHistory()));

            assertEquals("You are the first child of a Serf.  ", history());
        }

        /**
         * A race with no chart produces an empty biography, not a null one and not a crash: the
         * walk in {@code get_history} never enters its loop.
         *
         * @throws Exception if the field cannot be reached
         */
        @Test
        @DisplayName("a race with no chart yields an empty background")
        void noChartYieldsEmpty() throws Exception {
            player.setPlayerHistory(PlayerBirth.getHistory(race("Human", null).getHistory()));

            assertNotNull(history());
            assertEquals("", history());
        }
    }
}
