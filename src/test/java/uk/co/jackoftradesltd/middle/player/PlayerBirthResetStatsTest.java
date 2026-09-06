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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.co.jackoftradesltd.channel.enums.GameEventType;
import uk.co.jackoftradesltd.channel.messages.data.EventDataBirthPoints;
import uk.co.jackoftradesltd.channel.messages.data.GameEventData;
import uk.co.jackoftradesltd.middle.enums.Stats;
import uk.co.jackoftradesltd.middle.game.GameWorld;
import uk.co.jackoftradesltd.middle.game.event.EventHandlerInterface;
import uk.co.jackoftradesltd.middle.game.event.EventsHandler;
import uk.co.jackoftradesltd.middle.game.gameengine.GameEngine;
import uk.co.jackoftradesltd.middle.game.gameengine.GameState;
import uk.co.jackoftradesltd.middle.game.globals.GameConstants;
import uk.co.jackoftradesltd.middle.game.globals.data.CarryCapData;
import uk.co.jackoftradesltd.middle.game.globals.data.GameConstantsData;
import uk.co.jackoftradesltd.middle.game.globals.data.PlayerData;
import uk.co.jackoftradesltd.testsupport.CalcBonusesFixture;
import uk.co.jackoftradesltd.testsupport.SeededPlayerRegistry;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link PlayerBirth#resetStats(Map, Map, Map, int, boolean)}, the port of C's
 * {@code reset_stats} ({@code player-birth.c:710-735}).
 *
 * <p>The C is short:
 *
 * <pre>{@code
 * *points_left_local = MAX_BIRTH_POINTS;
 * for (i = 0; i < STAT_MAX; i++) {
 *     stats_local[i] = 10;
 *     points_spent_local[i] = 0;
 *     points_inc_local[i] = birth_stat_costs[stats_local[i] + 1];
 * }
 * if (update_display) {
 *     recalculate_stats(stats_local, *points_left_local);
 *     event_signal_birthpoints(points_spent_local, points_inc_local, *points_left_local);
 * }
 * }</pre>
 *
 * <p><b>Two things this suite exists to pin down, because both were wrong at some point during the
 * port.</b> First, the cost lookup at one point read a neighbouring stat's value
 * ({@code Stats.values()[index + 1]}, one enum constant on from the stat actually being processed)
 * rather than the current stat's own freshly-set value; {@link StatReset} seeds every map with
 * distinct leftover values before the call, so a lookup drifting onto a neighbour - or off the end,
 * onto {@code STAT_MAX} - shows up as a wrong increment cost or a thrown exception rather than
 * passing by chance. Second, {@code points_left} is C's {@code int *points_left_local}
 * out-parameter; the port cannot take an {@code int} by reference, so it returns the reset total
 * instead. {@link UpdateDisplayGuard} checks that the birth gold {@link PlayerBirth#recalculateStats}
 * derives from it uses the freshly reset total and not whatever the caller happened to pass in.
 *
 * <p>{@link PlayerBirth#recalculateStats} and the event bus are each covered on their own terms
 * elsewhere ({@link PlayerBirthRecalculateStatsTest}); what is checked here is that this method
 * reaches them with the right arguments when {@code updateDisplay} is set, and reaches neither when
 * it is not.
 *
 * <p>The method reads the character through {@link GameState#getPlayer()} only via
 * {@link PlayerBirth#recalculateStats}, so every test installs its own player and bus the same way
 * {@link PlayerBirthRecalculateStatsTest} does, and restores whatever was installed before.
 *
 * <p>Class PlayerBirthResetStatsTest coded on 260906, commented in full on 260906.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class PlayerBirthResetStatsTest {

    /**
     * The shipped {@code player:start-gold} figure ({@code constants.txt:201}).
     */
    private static final int START_GOLD = 600;

    /**
     * C's {@code MAX_BIRTH_POINTS} ({@code player-birth.c:683}), {@code 3 * (1+1+1+1+1+1+2)}.
     */
    private static final int MAX_BIRTH_POINTS = 20;

    /**
     * The cost {@code birth_stat_costs} charges to raise a stat from 10 - {@code birth_stat_costs[11]}
     * ({@code player-birth.c:679}) - which every real stat's increment cost must equal once reset.
     */
    private static final int COST_AT_TEN = 1;

    /**
     * The port under test.
     */
    private final PlayerBirth playerBirth = new PlayerBirth();

    /**
     * The character under test, installed as {@link GameState}'s current player.
     */
    private Player player;

    /**
     * Whatever {@link GameState} held as the current player before the test, restored afterwards.
     */
    private Player realPlayer;

    /**
     * The bus installed for the test, capturing every event signalled and its payload.
     */
    private CapturingBus bus;

    /**
     * The bus that was installed before the test, put back afterwards.
     */
    private EventsHandler realBus;

    /**
     * Whether a character was generated before the test, put back afterwards.
     */
    private boolean realCharacterGenerated;

    /**
     * The constants table as it was before the test replaced it.
     */
    private Object savedConstants;

    /**
     * The constants holder, made accessible.
     *
     * @return the field
     * @throws ReflectiveOperationException if it cannot be reached
     */
    private static Field constantsField() throws ReflectiveOperationException {
        Field field = GameConstants.class.getDeclaredField("data");
        field.setAccessible(true);
        return field;
    }

    /**
     * Publishes a constants table carrying the given starting gold, plus the carry-cap figures a
     * {@link Player} needs to be constructed at all.
     *
     * @param startGold the {@code player:start-gold} figure to publish
     * @throws ReflectiveOperationException if the field cannot be reached
     */
    private static void seedConstants(int startGold) throws ReflectiveOperationException {
        constantsField().set(null, new GameConstantsData(
                null, null, null, null, null,
                new CarryCapData(23, 10, 40, 5, 16),
                null, null,
                new PlayerData(20, 20, startGold, 5000),
                null, null, null, null, null, null, null, null));
    }

    /**
     * Five distinct values, one per real stat, in the order the shipped enum declares them - a
     * caller's leftover map from partway through point-buy, never the round number the reset writes.
     *
     * @param str   the strength value
     * @param intel the intelligence value
     * @param wis   the wisdom value
     * @param dex   the dexterity value
     * @param con   the constitution value
     * @return the map
     */
    private static Map<Stats, Integer> statsMap(int str, int intel, int wis, int dex, int con) {
        Map<Stats, Integer> stats = new HashMap<>();
        stats.put(Stats.STAT_STR, str);
        stats.put(Stats.STAT_INT, intel);
        stats.put(Stats.STAT_WIS, wis);
        stats.put(Stats.STAT_DEX, dex);
        stats.put(Stats.STAT_CON, con);
        return stats;
    }

    /**
     * Asserts every one of the five real stats in a map equals an expected value; the sentinels are
     * never checked here, since the port's loop never writes them.
     *
     * @param map      the map to check
     * @param expected the value every real stat must carry
     * @param label    what the map represents, for the failure message
     */
    private static void assertAllRealStats(Map<Stats, Integer> map, int expected, String label) {
        for (Stats stat : Stats.values()) {
            if (stat == Stats.STAT_NONE || stat == Stats.STAT_MAX) continue;
            assertEquals(expected, map.get(stat), label + " for " + stat);
        }
    }

    /**
     * Reads the player's private {@code auBirth} field, which has no getter.
     *
     * @return the stored birth gold
     * @throws ReflectiveOperationException if the field cannot be reached
     */
    private int auBirth() throws ReflectiveOperationException {
        Field field = Player.class.getDeclaredField("auBirth");
        field.setAccessible(true);
        return field.getInt(player);
    }

    /**
     * A plain character, installed as the current player, with the shipped starting gold published
     * and a capturing event bus in place.
     *
     * @throws ReflectiveOperationException if a fixture field cannot be reached
     */
    @BeforeEach
    void newCharacter() throws ReflectiveOperationException {
        savedConstants = constantsField().get(null);
        seedConstants(START_GOLD);

        player = new Player();
        CalcBonusesFixture.plainCharacter(player);

        realPlayer = GameState.getPlayer();
        GameState.setPlayer(player);

        bus = new CapturingBus();
        realBus = GameEngine.getEventsBusHandler();
        GameEngine.setEventsBusHandler(bus);

        realCharacterGenerated = GameWorld.characterGenerated;
        GameWorld.characterGenerated = false;
    }

    /**
     * Puts every global this method touches back, so no other suite inherits them.
     *
     * @throws ReflectiveOperationException if the constants field cannot be reached
     */
    @AfterEach
    void restoreGlobals() throws ReflectiveOperationException {
        GameState.setPlayer(realPlayer);
        GameEngine.setEventsBusHandler(realBus);
        GameWorld.characterGenerated = realCharacterGenerated;
        constantsField().set(null, savedConstants);
    }

    /**
     * Captures the events signalled during the test, in order, together with each one's payload.
     *
     * @author Rowan Crowther
     */
    private static class CapturingBus implements EventsHandler {

        /**
         * Every event type signalled since the bus was installed, in order.
         */
        private final List<GameEventType> events = new ArrayList<>();

        /**
         * Every payload signalled since the bus was installed, in the same order as {@link #events}.
         */
        private final List<GameEventData> payloads = new ArrayList<>();

        @Override
        public void eventAddHandler(GameEventType eventType, EventHandlerInterface handler) {
        }

        @Override
        public void eventRemoveHandler(GameEventType eventType, EventHandlerInterface handler) {
        }

        @Override
        public void eventRemoveHandlerType(GameEventType eventType) {
        }

        @Override
        public void gameEventDispatch(GameEventType eventType, GameEventData eventData) {
            events.add(eventType);
            payloads.add(eventData);
        }
    }

    /**
     * The stat loop: C's {@code stats_local[i] = 10}, {@code points_spent_local[i] = 0} and
     * {@code points_inc_local[i] = birth_stat_costs[stats_local[i] + 1]}, for the five real stats
     * only.
     */
    @Nested
    @DisplayName("the stat loop")
    class StatReset {

        /**
         * Every real stat is written back to 10, nothing spent, and the increment cost of the next
         * point - regardless of whatever leftover values the caller's maps carried going in. A cost
         * lookup that drifted onto a neighbouring stat's value, rather than the current stat's own,
         * would read one of these distinct leftovers instead and very likely miss {@link #COST_AT_TEN}.
         *
         * @throws ReflectiveOperationException if a private field cannot be reached
         */
        @Test
        @DisplayName("overwrites every real stat to 10/0/1 regardless of what it held before")
        void overwritesRegardlessOfLeftoverValues() throws ReflectiveOperationException {
            Map<Stats, Integer> stats = statsMap(17, 13, 9, 18, 11);
            Map<Stats, Integer> spent = statsMap(5, 2, 0, 8, 3);
            Map<Stats, Integer> inc = statsMap(42, 42, 42, 42, 42);

            playerBirth.resetStats(stats, spent, inc, 0, false);

            assertAllRealStats(stats, 10, "reset stat value");
            assertAllRealStats(spent, 0, "reset points spent");
            assertAllRealStats(inc, COST_AT_TEN, "reset increment cost");
        }

        /**
         * {@link Stats#values()} enumerates {@code STAT_NONE} and {@code STAT_MAX} alongside the
         * five real stats; a caller's map built the natural way, one entry per real stat, has no
         * entry for either, and a loop that looked one up - directly or by an off-by-one drift onto
         * the next enum constant - would throw.
         */
        @Test
        @DisplayName("STAT_NONE and STAT_MAX are skipped rather than looked up in the caller's maps")
        void skipsTheSentinels() {
            Map<Stats, Integer> stats = statsMap(14, 14, 14, 14, 14);
            Map<Stats, Integer> spent = statsMap(0, 0, 0, 0, 0);
            Map<Stats, Integer> inc = statsMap(0, 0, 0, 0, 0);

            assertDoesNotThrow(() -> playerBirth.resetStats(stats, spent, inc, 0, false));
        }

        /**
         * Sentinel entries, if a caller's map does carry them, are still not written to - the loop's
         * bound excludes them the same way C's {@code i < STAT_MAX} does.
         */
        @Test
        @DisplayName("sentinel entries in the maps, if present, are left untouched")
        void leavesSentinelEntriesUntouchedIfPresent() {
            Map<Stats, Integer> stats = statsMap(14, 14, 14, 14, 14);
            Map<Stats, Integer> spent = statsMap(0, 0, 0, 0, 0);
            Map<Stats, Integer> inc = statsMap(0, 0, 0, 0, 0);
            for (Map<Stats, Integer> map : List.of(stats, spent, inc)) {
                map.put(Stats.STAT_NONE, 999);
                map.put(Stats.STAT_MAX, 999);
            }

            playerBirth.resetStats(stats, spent, inc, 0, false);

            for (Map<Stats, Integer> map : List.of(stats, spent, inc)) {
                assertEquals(999, map.get(Stats.STAT_NONE), "STAT_NONE must be left alone");
                assertEquals(999, map.get(Stats.STAT_MAX), "STAT_MAX must be left alone");
            }
        }
    }

    /**
     * The out-parameter substitution: C writes {@code MAX_BIRTH_POINTS} through
     * {@code *points_left_local}; the port returns it instead, regardless of the value the caller
     * passed in as {@code pointsLeftLocal}.
     */
    @Nested
    @DisplayName("the returned point total")
    class PointsLeftReturn {

        /**
         * A low incoming value is discarded in favour of the reset total.
         */
        @Test
        @DisplayName("returns MAX_BIRTH_POINTS even when the incoming value is lower")
        void ignoresALowIncomingValue() {
            Map<Stats, Integer> stats = statsMap(10, 10, 10, 10, 10);
            Map<Stats, Integer> spent = statsMap(0, 0, 0, 0, 0);
            Map<Stats, Integer> inc = statsMap(1, 1, 1, 1, 1);

            int result = playerBirth.resetStats(stats, spent, inc, 0, false);

            assertEquals(MAX_BIRTH_POINTS, result);
        }

        /**
         * A high incoming value - one C's own point-buy screen could never actually reach - is
         * discarded just the same, since C's assignment does not consult the old value either.
         */
        @Test
        @DisplayName("returns MAX_BIRTH_POINTS even when the incoming value is higher")
        void ignoresAHighIncomingValue() {
            Map<Stats, Integer> stats = statsMap(10, 10, 10, 10, 10);
            Map<Stats, Integer> spent = statsMap(0, 0, 0, 0, 0);
            Map<Stats, Integer> inc = statsMap(1, 1, 1, 1, 1);

            int result = playerBirth.resetStats(stats, spent, inc, 999, false);

            assertEquals(MAX_BIRTH_POINTS, result);
        }
    }

    /**
     * The {@code updateDisplay} guard: C's {@code if (update_display) { recalculate_stats(...);
     * event_signal_birthpoints(...); } }.
     */
    @Nested
    @DisplayName("the updateDisplay guard")
    class UpdateDisplayGuard {

        /**
         * With the guard set, {@link PlayerBirth#recalculateStats} and the birthpoints signal both
         * fire, in C's order, and both see the freshly reset total rather than whatever
         * {@code pointsLeftLocal} arrived carrying - the gold figure pins that down, since it is
         * derived from the point total {@link PlayerBirth#recalculateStats} is handed.
         *
         * @throws ReflectiveOperationException if a private field cannot be reached
         */
        @Test
        @DisplayName("true recalculates stats and signals birthpoints, using the reset total")
        void recalculatesAndSignalsUsingTheResetTotal() throws ReflectiveOperationException {
            Map<Stats, Integer> stats = statsMap(17, 13, 9, 18, 11);
            Map<Stats, Integer> spent = statsMap(5, 2, 0, 8, 3);
            Map<Stats, Integer> inc = statsMap(42, 42, 42, 42, 42);

            playerBirth.resetStats(stats, spent, inc, 999, true);

            assertEquals(List.of(GameEventType.EVENT_GOLD, GameEventType.EVENT_AC,
                    GameEventType.EVENT_HP, GameEventType.EVENT_STATS,
                    GameEventType.EVENT_BIRTHPOINTS), bus.events);
            assertEquals(START_GOLD + 50 * MAX_BIRTH_POINTS, auBirth(),
                    "gold must be derived from the reset total (20), not the incoming 999");

            EventDataBirthPoints birthPoints =
                    (EventDataBirthPoints) bus.payloads.get(bus.payloads.size() - 1);
            assertEquals(MAX_BIRTH_POINTS, birthPoints.getRemaining());
            assertAllRealStats(birthPoints.getPoints(), 0, "signalled points spent");
            assertAllRealStats(birthPoints.getIncPoints(), COST_AT_TEN, "signalled increment cost");
        }

        /**
         * With the guard clear, neither call happens: no event fires, and no gold is derived.
         *
         * @throws ReflectiveOperationException if a private field cannot be reached
         */
        @Test
        @DisplayName("false calls neither recalculateStats nor the birthpoints signal")
        void doesNeitherWhenClear() throws ReflectiveOperationException {
            int before = auBirth();
            Map<Stats, Integer> stats = statsMap(17, 13, 9, 18, 11);
            Map<Stats, Integer> spent = statsMap(5, 2, 0, 8, 3);
            Map<Stats, Integer> inc = statsMap(42, 42, 42, 42, 42);

            playerBirth.resetStats(stats, spent, inc, 999, false);

            assertTrue(bus.events.isEmpty(), "no event should fire when updateDisplay is false");
            assertEquals(before, auBirth(), "recalculateStats must not run when updateDisplay is false");
        }
    }
}
