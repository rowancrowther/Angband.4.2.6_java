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
import uk.co.jackoftradesltd.channel.messages.data.GameEventData;
import uk.co.jackoftradesltd.middle.enums.Stats;
import uk.co.jackoftradesltd.middle.game.event.EventHandlerInterface;
import uk.co.jackoftradesltd.middle.game.event.EventsHandler;
import uk.co.jackoftradesltd.middle.game.gameengine.GameEngine;
import uk.co.jackoftradesltd.middle.game.gameengine.GameState;
import uk.co.jackoftradesltd.middle.game.globals.GameConstants;
import uk.co.jackoftradesltd.middle.game.globals.data.CarryCapData;
import uk.co.jackoftradesltd.middle.game.globals.data.GameConstantsData;
import uk.co.jackoftradesltd.middle.game.globals.data.PlayerData;
import uk.co.jackoftradesltd.middle.player.enums.PlayerUpdateEnum;
import uk.co.jackoftradesltd.testsupport.CalcBonusesFixture;
import uk.co.jackoftradesltd.testsupport.SeededPlayerRegistry;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link PlayerBirth#recalculateStats(Map, int)}, the port of C's {@code recalculate_stats}
 * ({@code player-birth.c:685-707}).
 *
 * <p>The C is short:
 *
 * <pre>{@code
 * for (i = 0; i < STAT_MAX; i++) {
 *     player->stat_cur[i] = player->stat_max[i] =
 *             player->stat_birth[i] = stats_local_local[i];
 *     player->stat_map[i] = i;
 * }
 * player->au_birth = z_info->start_gold + (50 * points_left_local);
 * get_bonuses();
 * event_signal(EVENT_GOLD);
 * event_signal(EVENT_AC);
 * event_signal(EVENT_HP);
 * event_signal(EVENT_STATS);
 * }</pre>
 *
 * <p><b>Two things this suite exists to pin down, because both were wrong at some point during the
 * port.</b> First, the gold line was written as {@code start_gold + (50 + points_left)} - addition
 * where C multiplies - which only agreed with C when exactly one point was left; {@link Gold} checks
 * a spread of values including zero, where the wrong version would have added a spurious fifty.
 * Second, the stat loop iterated {@link Stats#values()} with no guard, and that enumeration carries
 * {@code STAT_NONE} and {@code STAT_MAX} alongside the five real stats C's {@code i < STAT_MAX} bound
 * excludes; unguarded, the loop threw looking up a sentinel the caller's map never populates.
 * {@link StatLoop} checks both that the five real stats are written and that the sentinels are not
 * touched, the same guard already used two hundred lines up in {@link PlayerBirth#getStats}.
 *
 * <p>{@code getBonuses} and the event bus are each covered on their own terms elsewhere
 * ({@link PlayerBirthGetBonusesTest}, {@link PlayerRedrawStuffTest}); what is checked here is only
 * that this method reaches them, via a state-identity swap for the former and a captured event order
 * for the latter.
 *
 * <p>The method reads the character through {@link GameState#getPlayer()} rather than taking one as
 * a parameter, so every test installs its own player there first and restores whatever was installed
 * before, the same way {@link GameEngine#getEventsBusHandler()} is swapped and restored around the
 * captured bus.
 *
 * <p>Class PlayerBirthRecalculateStatsTest coded on 260905, commented in full on 260905.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class PlayerBirthRecalculateStatsTest {

    /**
     * The shipped {@code player:start-gold} figure ({@code constants.txt:201}), used as the default
     * for every test that does not care about a different one.
     */
    private static final int START_GOLD = 600;

    /**
     * A plain, otherwise-unremarkable set of the five real stats, distinct enough from one another
     * that a value landing in the wrong slot would be caught.
     */
    private static final Map<Stats, Integer> ORDINARY_STATS = statsMap(17, 10, 10, 14, 16);

    /**
     * The character under test, installed as {@link GameState}'s current player.
     */
    private Player player;

    /**
     * Whatever {@link GameState} held as the current player before the test, restored afterwards.
     */
    private Player realPlayer;

    /**
     * The bus installed for the test, capturing every event signalled.
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
     * Five stat values, one per real stat, in the order the shipped enum declares them.
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
     * Reads the player's private {@code auBirth} field, which has no getter.
     *
     * @return the stored birth gold
     * @throws ReflectiveOperationException if the field cannot be reached
     */
    private long auBirth() throws ReflectiveOperationException {
        Field field = Player.class.getDeclaredField("auBirth");
        field.setAccessible(true);
        return field.getLong(player);
    }

    /**
     * Reads the player's private {@code statsBirth} field, which has no getter.
     *
     * @return the stored birth stat values
     * @throws ReflectiveOperationException if the field cannot be reached
     */
    @SuppressWarnings("unchecked")
    private Map<Stats, Integer> statsBirth() throws ReflectiveOperationException {
        Field field = Player.class.getDeclaredField("statsBirth");
        field.setAccessible(true);
        return (Map<Stats, Integer>) field.get(player);
    }

    /**
     * Reads the player's private {@code statMap} field, which has no getter.
     *
     * @return the stored scramble map
     * @throws ReflectiveOperationException if the field cannot be reached
     */
    @SuppressWarnings("unchecked")
    private Map<Stats, Stats> statMap() throws ReflectiveOperationException {
        Field field = Player.class.getDeclaredField("statMap");
        field.setAccessible(true);
        return (Map<Stats, Stats>) field.get(player);
    }

    /**
     * Writes {@link GameState}'s private {@code characterGenerated} field directly, since
     * {@link GameState} exposes no setter for it.
     *
     * @param value the value to force the field to
     * @throws ReflectiveOperationException if the field cannot be reached
     */
    private static void setCharacterGenerated(boolean value) throws ReflectiveOperationException {
        Field field = GameState.class.getDeclaredField("characterGenerated");
        field.setAccessible(true);
        field.set(null, value);
    }

    /**
     * A plain character partway through birth, installed as the current player, with the shipped
     * starting gold published and a capturing event bus in place.
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

        realCharacterGenerated = GameState.getCharacterGenerated();
        setCharacterGenerated(false);
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
        setCharacterGenerated(realCharacterGenerated);
        constantsField().set(null, savedConstants);
    }

    /**
     * Captures the events signalled during the test, in order.
     *
     * @author Rowan Crowther
     */
    private static class CapturingBus implements EventsHandler {

        /**
         * Every event type signalled since the bus was installed, in order.
         */
        private final List<GameEventType> events = new ArrayList<>();

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
        }
    }

    /**
     * The stat loop: C's {@code for (i = 0; i < STAT_MAX; i++)} writing {@code stat_cur},
     * {@code stat_max}, {@code stat_birth} and {@code stat_map} for the five real stats only.
     */
    @Nested
    @DisplayName("the stat loop")
    class StatLoop {

        /**
         * Each of the five real stats gets its supplied value written to all three totals, and the
         * identity scramble map, matching C's chained assignment and {@code stat_map[i] = i}.
         *
         * @throws ReflectiveOperationException if a private field cannot be reached
         */
        @Test
        @DisplayName("writes current, max and birth for each real stat, and the identity scramble map")
        void writesEachRealStat() throws ReflectiveOperationException {
            PlayerBirth.recalculateStats(ORDINARY_STATS, 2);

            for (Map.Entry<Stats, Integer> entry : ORDINARY_STATS.entrySet()) {
                Stats stat = entry.getKey();
                int value = entry.getValue();
                assertEquals(value, player.getCurStatValue(stat), stat + " current value");
                assertEquals(value, player.getStatMax(stat), stat + " max value");
                assertEquals(value, statsBirth().get(stat), stat + " birth value");
                assertEquals(stat, statMap().get(stat), stat + " scramble map entry");
            }
        }

        /**
         * {@link Stats#values()} enumerates {@code STAT_NONE} and {@code STAT_MAX} alongside the
         * five real stats; C's bound excludes both, and a caller's map - built the natural way, one
         * entry per real stat - has no entry for either. An unguarded loop threw looking one up.
         *
         * @throws ReflectiveOperationException if a private field cannot be reached
         */
        @Test
        @DisplayName("STAT_NONE and STAT_MAX are skipped rather than looked up in the caller's map")
        void skipsTheSentinels() throws ReflectiveOperationException {
            assertDoesNotThrow(() -> PlayerBirth.recalculateStats(ORDINARY_STATS, 2));

            assertFalse(statMap().containsKey(Stats.STAT_NONE),
                    "C's loop never reaches an index STAT_NONE would correspond to");
            assertFalse(statMap().containsKey(Stats.STAT_MAX),
                    "C's loop bound, i < STAT_MAX, excludes the sentinel itself");
        }

        /**
         * A map that does carry entries for the sentinels - as one built from
         * {@link Stats#values()} rather than the five real stats would - still must not have them
         * written back, since C's loop bound would never reach them either.
         *
         * @throws ReflectiveOperationException if a private field cannot be reached
         */
        @Test
        @DisplayName("sentinel entries in the map, if present, are still not written back")
        void ignoresSentinelEntriesEvenIfSupplied() throws ReflectiveOperationException {
            Map<Stats, Integer> stats = new HashMap<>(ORDINARY_STATS);
            stats.put(Stats.STAT_NONE, 999);
            stats.put(Stats.STAT_MAX, 999);

            PlayerBirth.recalculateStats(stats, 2);

            assertFalse(statMap().containsKey(Stats.STAT_NONE));
            assertFalse(statMap().containsKey(Stats.STAT_MAX));
        }
    }

    /**
     * The gold line: C's {@code z_info->start_gold + (50 * points_left_local)}, "gold is inversely
     * proportional to cost" in C's own comment.
     */
    @Nested
    @DisplayName("the birth gold formula")
    class Gold {

        /**
         * The ordinary path: three points left adds a hundred and fifty over the starting gold.
         *
         * @throws ReflectiveOperationException if the birth field cannot be reached
         */
        @Test
        @DisplayName("adds fifty gold per point left unspent")
        void multipliesPointsLeftByFifty() throws ReflectiveOperationException {
            PlayerBirth.recalculateStats(ORDINARY_STATS, 3);

            assertEquals(750, auBirth());
        }

        /**
         * The boundary a wrong {@code start_gold + (50 + points_left)} would get wrong: with nothing
         * left to spend, C adds nothing at all, where the addition-based version would still add a
         * flat fifty.
         *
         * @throws ReflectiveOperationException if the birth field cannot be reached
         */
        @Test
        @DisplayName("no points left adds no gold at all")
        void zeroPointsLeftAddsNothing() throws ReflectiveOperationException {
            PlayerBirth.recalculateStats(ORDINARY_STATS, 0);

            assertEquals(START_GOLD, auBirth());
        }

        /**
         * A second boundary distinguishing multiplication from addition: at exactly one point left
         * the two formulas agree, so a value on either side is needed to catch the wrong operator.
         *
         * @throws ReflectiveOperationException if the birth field cannot be reached
         */
        @Test
        @DisplayName("one point left is the case where addition and multiplication would agree")
        void onePointLeftIsTheAmbiguousCase() throws ReflectiveOperationException {
            PlayerBirth.recalculateStats(ORDINARY_STATS, 1);

            assertEquals(START_GOLD + 50, auBirth());
        }

        /**
         * The starting figure comes from the constants table, not a literal baked into the port.
         *
         * @throws ReflectiveOperationException if a fixture field cannot be reached
         */
        @Test
        @DisplayName("reads the starting figure from the constants table")
        void readsStartGoldFromConstants() throws ReflectiveOperationException {
            seedConstants(1000);

            PlayerBirth.recalculateStats(ORDINARY_STATS, 4);

            assertEquals(1200, auBirth());
        }
    }

    /**
     * The two calls after the stat and gold writes: {@code get_bonuses()} and the four
     * {@code event_signal} calls.
     */
    @Nested
    @DisplayName("the downstream calls")
    class DownstreamCalls {

        /**
         * {@code getBonuses} installs a freshly recalculated {@link PlayerState}
         * ({@code PlayerBirthGetBonusesTest.stateRecalculated}); seeing a different instance
         * afterwards is evidence this method actually reaches it rather than skipping the call.
         */
        @Test
        @DisplayName("calls getBonuses, which replaces the player's state")
        void callsGetBonuses() {
            PlayerState before = player.getPlayerState();

            PlayerBirth.recalculateStats(ORDINARY_STATS, 2);

            assertTrue(before != player.getPlayerState(),
                    "getBonuses should have installed a recalculated state");
        }

        /**
         * The four signals fire in C's order - gold, AC, hit points, stats - and nothing else, so a
         * UI listening for one does not have to guess when it arrives relative to the others.
         */
        @Test
        @DisplayName("signals gold, AC, HP and stats, in that order, and nothing else")
        void signalsFourEventsInOrder() {
            PlayerBirth.recalculateStats(ORDINARY_STATS, 2);

            assertEquals(List.of(GameEventType.EVENT_GOLD, GameEventType.EVENT_AC,
                    GameEventType.EVENT_HP, GameEventType.EVENT_STATS), bus.events);
        }
    }
}
