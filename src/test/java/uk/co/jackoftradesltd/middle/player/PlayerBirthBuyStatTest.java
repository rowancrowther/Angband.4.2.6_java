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
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link PlayerBirth#buyStat(Stats, Map, Map, Map, int, boolean)}, the port of C's
 * {@code buy_stat} ({@code player-birth.c:738-773}).
 *
 * <p>The C:
 *
 * <pre>{@code
 * if (!(choice >= STAT_MAX || choice < 0) && (stats_local[choice] < 18)) {
 *     int stat_cost = birth_stat_costs[stats_local[choice] + 1];
 *     assert(stat_cost == points_inc_local[choice]);
 *     if (stat_cost <= *points_left_local) {
 *         stats_local[choice]++;
 *         points_spent_local[choice] += stat_cost;
 *         points_inc_local[choice] = birth_stat_costs[stats_local[choice] + 1];
 *         *points_left_local -= stat_cost;
 *         if (update_display) {
 *             event_signal_birthpoints(points_spent_local, points_inc_local, *points_left_local);
 *             recalculate_stats(stats_local, *points_left_local);
 *         }
 *         return true;
 *     }
 * }
 * return false;
 * }</pre>
 *
 * <p>C's {@code choice} is a raw index that {@code stat_local[]} subscripts directly, so
 * {@code buy_stat} itself has to bounds-check it. The port's {@code choice} is the {@link Stats}
 * constant itself rather than that raw index, so the enum's own type rules out the
 * out-of-{@code STAT_MAX} values C's guard exists to catch - the only two inputs {@link
 * InvalidChoice} can still hand it are the sentinels, {@code STAT_NONE} and {@code STAT_MAX}.
 *
 * <p><b>Two other things this suite exists to pin down, because both were wrong at some point
 * during the port.</b> First, {@link Boundary18} pins down the one place the port deliberately
 * does <em>not</em> match C: raising a stat to 18 has C read one entry past the end of
 * {@code birth_stat_costs}, which the port guards against rather than reproduces. Second,
 * {@link UpdateDisplayGuard} checks the port calls the birthpoints signal and
 * {@link PlayerBirth#recalculateStats} in C's order - birthpoints first, then the recalculation -
 * which is the <em>opposite</em> order {@link PlayerBirthResetStatsTest} pins down for
 * {@link PlayerBirth#resetStats}, since C's own two functions do it differently.
 *
 * <p>{@link PlayerBirth#recalculateStats} and the event bus are each covered on their own terms
 * elsewhere ({@link PlayerBirthRecalculateStatsTest}); what is checked here is that this method
 * reaches them with the right arguments when {@code updateDisplay} is set, and reaches neither when
 * it is not - the same split {@link PlayerBirthResetStatsTest} draws.
 *
 * <p>{@link PlayerBirth#buyStat}'s return type is a private record, so every test reaches its two
 * components through reflection rather than by naming the type.
 *
 * <p>Class PlayerBirthBuyStatTest coded on 260906, commented in full on 260906.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class PlayerBirthBuyStatTest {

    /**
     * The shipped {@code player:start-gold} figure ({@code constants.txt:201}).
     */
    private static final int START_GOLD = 600;

    /**
     * C's {@code MAX_BIRTH_POINTS} ({@code player-birth.c:683}), {@code 3 * (1+1+1+1+1+1+2)}.
     */
    private static final int MAX_BIRTH_POINTS = 20;

    /**
     * {@code birth_stat_costs[11]} ({@code player-birth.c:679}) - the cost of the point that raises
     * a stat from 10 to 11, and every point up to 16.
     */
    private static final int COST_FLAT = 1;

    /**
     * {@code birth_stat_costs[17]} - the cost of the point that raises a stat from 16 to 17.
     */
    private static final int COST_AT_SEVENTEEN = 2;

    /**
     * {@code birth_stat_costs[18]} - the cost of the point that raises a stat from 17 to 18, the
     * highest a stat can reach through point-buy.
     */
    private static final int COST_AT_EIGHTEEN = 4;

    /**
     * The {@code choice} that selects strength.
     */
    private static final Stats CHOICE_STR = Stats.STAT_STR;

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
     * Five distinct values, one per real stat, in the order the shipped enum declares them.
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
     * Reads the private record's {@code value} component (the returned points-left total) by
     * reflection.
     *
     * @param result the object {@link #call} returned
     * @return the points-left figure it carries
     * @throws ReflectiveOperationException if the accessor cannot be reached
     */
    private static int pointsLeftOf(Object result) throws ReflectiveOperationException {
        Method method = result.getClass().getDeclaredMethod("value");
        method.setAccessible(true);
        return (int) method.invoke(result);
    }

    /**
     * Reads the private record's {@code bool} component (whether the purchase succeeded) by
     * reflection.
     *
     * @param result the object {@link #call} returned
     * @return the success flag it carries
     * @throws ReflectiveOperationException if the accessor cannot be reached
     */
    private static boolean succeededOf(Object result) throws ReflectiveOperationException {
        Method method = result.getClass().getDeclaredMethod("bool");
        method.setAccessible(true);
        return (boolean) method.invoke(result);
    }

    /**
     * Calls {@link PlayerBirth#buyStat}, catching the private {@code IntAndBoolean} result as a
     * bare {@code Object} since the record's name is not accessible from this class.
     *
     * @param choice           the stat to pass through
     * @param statsLocal       the stat values map
     * @param pointsSpentLocal the points-spent map
     * @param pointsIncLocal   the increment-cost map
     * @param pointsLeftLocal  the incoming point total
     * @param updateDisplay    whether to signal the UI
     * @return the private result object
     */
    private Object call(Stats choice, Map<Stats, Integer> statsLocal, Map<Stats, Integer> pointsSpentLocal,
                        Map<Stats, Integer> pointsIncLocal, int pointsLeftLocal, boolean updateDisplay) {
        return playerBirth.buyStat(choice, statsLocal, pointsSpentLocal, pointsIncLocal, pointsLeftLocal,
                updateDisplay);
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
     * The ordinary path: a valid stat below 18, enough points left, {@code updateDisplay} false.
     */
    @Nested
    @DisplayName("an ordinary purchase")
    class OrdinaryPurchase {

        /**
         * C's {@code stats_local[choice]++}, {@code points_spent_local[choice] += stat_cost},
         * {@code points_inc_local[choice] = birth_stat_costs[stats_local[choice] + 1]} and
         * {@code *points_left_local -= stat_cost}, none of which touch any other stat's entry.
         *
         * @throws ReflectiveOperationException if the private result cannot be read
         */
        @Test
        @DisplayName("raises the chosen stat, charges the cost, and leaves other stats untouched")
        void raisesTheChosenStat() throws ReflectiveOperationException {
            Map<Stats, Integer> stats = statsMap(10, 13, 9, 12, 11);
            Map<Stats, Integer> spent = statsMap(0, 2, 0, 1, 3);
            Map<Stats, Integer> inc = statsMap(COST_FLAT, 42, 42, 42, 42);

            Object result = call(CHOICE_STR, stats, spent, inc, MAX_BIRTH_POINTS, false);

            assertTrue(succeededOf(result), "buying the first point on a fresh stat must succeed");
            assertEquals(MAX_BIRTH_POINTS - COST_FLAT, pointsLeftOf(result));
            assertEquals(11, stats.get(Stats.STAT_STR));
            assertEquals(COST_FLAT, spent.get(Stats.STAT_STR));
            assertEquals(COST_FLAT, inc.get(Stats.STAT_STR), "cost of the next point, 11 to 12, is still flat");

            assertEquals(13, stats.get(Stats.STAT_INT), "untouched stat must be unchanged");
            assertEquals(2, spent.get(Stats.STAT_INT), "untouched stat's spend must be unchanged");
            assertEquals(42, inc.get(Stats.STAT_INT), "untouched stat's increment cost must be unchanged");
        }

        /**
         * The same purchase, chosen by the last real stat rather than the first, to confirm the
         * {@code choice + 1} shift lands on {@code STAT_CON} and not some neighbour.
         */
        @Test
        @DisplayName("choice 4 (STAT_CON) raises constitution, not a neighbouring stat")
        void choiceFourRaisesConstitution() {
            Map<Stats, Integer> stats = statsMap(10, 10, 10, 10, 16);
            Map<Stats, Integer> spent = statsMap(0, 0, 0, 0, 0);
            Map<Stats, Integer> inc = statsMap(COST_FLAT, COST_FLAT, COST_FLAT, COST_FLAT, COST_AT_SEVENTEEN);

            call(Stats.STAT_CON, stats, spent, inc, MAX_BIRTH_POINTS, false);

            assertEquals(17, stats.get(Stats.STAT_CON));
            assertEquals(10, stats.get(Stats.STAT_DEX), "STAT_DEX (the neighbour one index down) must be untouched");
        }
    }

    /**
     * The two sentinels are the only {@code choice} values the enum's own type still lets through -
     * every raw index C's {@code !(choice >= STAT_MAX || choice < 0)} would have rejected is now
     * ruled out at compile time instead, since {@code choice} is a {@link Stats} constant rather
     * than C's raw array subscript. Both sentinels must still answer {@code false} without
     * throwing, matching what C's guard would have done had it been asked about them.
     */
    @Nested
    @DisplayName("a sentinel choice")
    class InvalidChoice {

        /**
         * Asserts a {@code choice} value is rejected without an exception, leaving every map
         * untouched and returning the incoming points-left figure unchanged.
         *
         * @param choice the sentinel to try
         * @throws ReflectiveOperationException if the private result cannot be read
         */
        private void assertRejectedCleanly(Stats choice) throws ReflectiveOperationException {
            Map<Stats, Integer> stats = statsMap(14, 14, 14, 14, 14);
            Map<Stats, Integer> spent = statsMap(3, 3, 3, 3, 3);
            Map<Stats, Integer> inc = statsMap(9, 9, 9, 9, 9);

            Object[] result = new Object[1];
            assertDoesNotThrow(() -> result[0] = call(choice, stats, spent, inc, 17, true));

            assertFalse(succeededOf(result[0]), "a sentinel choice must never succeed");
            assertEquals(17, pointsLeftOf(result[0]), "a sentinel choice must not spend anything");
            for (Stats stat : List.of(Stats.STAT_STR, Stats.STAT_INT, Stats.STAT_WIS, Stats.STAT_DEX,
                    Stats.STAT_CON)) {
                assertEquals(14, stats.get(stat), "stat map must be untouched");
                assertEquals(3, spent.get(stat), "spend map must be untouched");
                assertEquals(9, inc.get(stat), "increment map must be untouched");
            }
            assertTrue(bus.events.isEmpty(), "a sentinel choice must not signal the UI even when requested");
        }

        /**
         * The same sentinel C's own {@code choice < 0} test excludes.
         *
         * @throws ReflectiveOperationException if the private result cannot be read
         */
        @Test
        @DisplayName("STAT_NONE is rejected, not thrown")
        void rejectsStatNone() throws ReflectiveOperationException {
            assertRejectedCleanly(Stats.STAT_NONE);
        }

        /**
         * The same sentinel C's own {@code choice >= STAT_MAX} test excludes.
         *
         * @throws ReflectiveOperationException if the private result cannot be read
         */
        @Test
        @DisplayName("STAT_MAX is rejected, not thrown")
        void rejectsStatMax() throws ReflectiveOperationException {
            assertRejectedCleanly(Stats.STAT_MAX);
        }
    }

    /**
     * C's {@code stats_local[choice] < 18} guard: a stat already at the point-buy cap is left
     * alone, whatever its recorded cost or spend.
     */
    @Nested
    @DisplayName("a stat already at the point-buy cap")
    class StatAlreadyCapped {

        /**
         * Every field the call might otherwise touch is seeded with an obviously-wrong sentinel, so
         * a guard that fired late (after doing some of the work) would be caught.
         *
         * @throws ReflectiveOperationException if the private result cannot be read
         */
        @Test
        @DisplayName("a stat at 18 is never raised further, regardless of points available")
        void refusesToRaisePast18() throws ReflectiveOperationException {
            Map<Stats, Integer> stats = statsMap(18, 10, 10, 10, 10);
            Map<Stats, Integer> spent = statsMap(999, 0, 0, 0, 0);
            Map<Stats, Integer> inc = statsMap(999, COST_FLAT, COST_FLAT, COST_FLAT, COST_FLAT);

            Object result = call(CHOICE_STR, stats, spent, inc, MAX_BIRTH_POINTS, false);

            assertFalse(succeededOf(result));
            assertEquals(MAX_BIRTH_POINTS, pointsLeftOf(result));
            assertEquals(18, stats.get(Stats.STAT_STR));
            assertEquals(999, spent.get(Stats.STAT_STR), "spend map must be untouched when the cap guard fires");
            assertEquals(999, inc.get(Stats.STAT_STR), "increment map must be untouched when the cap guard fires");
        }
    }

    /**
     * C's {@code assert(stat_cost == points_inc_local[choice])}, ported as a thrown
     * {@code RuntimeException} rather than an assertion that a release build would compile out -
     * the same substitution documented on {@link PlayerBirth#getHistory}.
     */
    @Nested
    @DisplayName("a mismatched increment cost")
    class CostMismatch {

        /**
         * The caller's own {@code pointsIncLocal} entry disagrees with what
         * {@link PlayerBirth}'s own {@code birthStatCosts} says the point should cost.
         */
        @Test
        @DisplayName("throws when the caller's recorded cost disagrees with birthStatCosts")
        void throwsOnMismatch() {
            Map<Stats, Integer> stats = statsMap(10, 10, 10, 10, 10);
            Map<Stats, Integer> spent = statsMap(0, 0, 0, 0, 0);
            Map<Stats, Integer> inc = statsMap(999, COST_FLAT, COST_FLAT, COST_FLAT, COST_FLAT);

            RuntimeException thrown = assertThrows(RuntimeException.class,
                    () -> call(CHOICE_STR, stats, spent, inc, MAX_BIRTH_POINTS, false));
            assertEquals("Invalid point buy cost", thrown.getMessage());
        }
    }

    /**
     * C's {@code stat_cost <= *points_left_local} guard: too few points left refuses the purchase
     * without spending any.
     */
    @Nested
    @DisplayName("too few points left")
    class InsufficientPoints {

        /**
         * The cost of the next point (4, to reach 18) is one more than the points on offer.
         *
         * @throws ReflectiveOperationException if the private result cannot be read
         */
        @Test
        @DisplayName("refuses the purchase and leaves the stat and spend maps unchanged")
        void refusesWhenPointsAreShort() throws ReflectiveOperationException {
            Map<Stats, Integer> stats = statsMap(17, 10, 10, 10, 10);
            Map<Stats, Integer> spent = statsMap(8, 0, 0, 0, 0);
            Map<Stats, Integer> inc = statsMap(COST_AT_EIGHTEEN, COST_FLAT, COST_FLAT, COST_FLAT, COST_FLAT);

            Object result = call(CHOICE_STR, stats, spent, inc, COST_AT_EIGHTEEN - 1, false);

            assertFalse(succeededOf(result));
            assertEquals(COST_AT_EIGHTEEN - 1, pointsLeftOf(result));
            assertEquals(17, stats.get(Stats.STAT_STR));
            assertEquals(8, spent.get(Stats.STAT_STR));
        }
    }

    /**
     * Raising a stat from 17 to 18: the one place the port deliberately does not follow C. C's next
     * line reads {@code birth_stat_costs[19]} - one past both that array's and
     * {@link PlayerBirth}'s own {@code birthStatCosts}'s nineteen entries - undefined behaviour that
     * is reachable within {@link #MAX_BIRTH_POINTS} (reaching 18 from base 10 costs 12 of the 20
     * points available). The port's guard leaves {@code pointsIncLocal} exactly as the caller had it
     * rather than following C past the array.
     */
    @Nested
    @DisplayName("raising a stat to 18")
    class Boundary18 {

        /**
         * The purchase itself still succeeds and still charges the right amount; only the
         * increment-cost refresh is skipped.
         *
         * @throws ReflectiveOperationException if the private result cannot be read
         */
        @Test
        @DisplayName("succeeds and charges COST_AT_EIGHTEEN, but leaves pointsIncLocal as the caller had it")
        void skipsTheIncrementCostRefresh() throws ReflectiveOperationException {
            Map<Stats, Integer> stats = statsMap(17, 10, 10, 10, 10);
            Map<Stats, Integer> spent = statsMap(8, 0, 0, 0, 0);
            Map<Stats, Integer> inc = statsMap(COST_AT_EIGHTEEN, COST_FLAT, COST_FLAT, COST_FLAT, COST_FLAT);
            int pointsLeft = MAX_BIRTH_POINTS - 8;

            Object result = call(CHOICE_STR, stats, spent, inc, pointsLeft, false);

            assertTrue(succeededOf(result));
            assertEquals(pointsLeft - COST_AT_EIGHTEEN, pointsLeftOf(result));
            assertEquals(18, stats.get(Stats.STAT_STR));
            assertEquals(8 + COST_AT_EIGHTEEN, spent.get(Stats.STAT_STR));
            assertEquals(COST_AT_EIGHTEEN, inc.get(Stats.STAT_STR),
                    "birthStatCosts[19] is out of bounds, so the port leaves the caller's own value in place");
        }
    }

    /**
     * The {@code updateDisplay} guard: C's {@code if (update_display) { event_signal_birthpoints(...);
     * recalculate_stats(...); } } - birthpoints first, then the recalculation, the opposite order
     * from {@code reset_stats}.
     */
    @Nested
    @DisplayName("the updateDisplay guard")
    class UpdateDisplayGuard {

        /**
         * With the guard set, the birthpoints signal fires before
         * {@link PlayerBirth#recalculateStats}'s own four events, and the gold figure that
         * recalculation derives is built from the points-left total <em>after</em> this purchase,
         * not before it.
         *
         * @throws ReflectiveOperationException if a private field cannot be reached
         */
        @Test
        @DisplayName("true signals birthpoints, then recalculates, using the post-purchase total")
        void signalsThenRecalculatesUsingThePostPurchaseTotal() throws ReflectiveOperationException {
            Map<Stats, Integer> stats = statsMap(10, 13, 9, 12, 11);
            Map<Stats, Integer> spent = statsMap(0, 2, 0, 1, 3);
            Map<Stats, Integer> inc = statsMap(COST_FLAT, 42, 42, 42, 42);

            call(CHOICE_STR, stats, spent, inc, MAX_BIRTH_POINTS, true);

            assertEquals(List.of(GameEventType.EVENT_BIRTHPOINTS, GameEventType.EVENT_GOLD,
                    GameEventType.EVENT_AC, GameEventType.EVENT_HP, GameEventType.EVENT_STATS), bus.events);
            assertEquals(START_GOLD + 50 * (MAX_BIRTH_POINTS - COST_FLAT), auBirth(),
                    "gold must be derived from the points-left total after this purchase");
        }

        /**
         * With the guard clear, neither call happens: no event fires, and no gold is derived.
         *
         * @throws ReflectiveOperationException if a private field cannot be reached
         */
        @Test
        @DisplayName("false calls neither the birthpoints signal nor recalculateStats")
        void doesNeitherWhenClear() throws ReflectiveOperationException {
            long before = auBirth();
            Map<Stats, Integer> stats = statsMap(10, 13, 9, 12, 11);
            Map<Stats, Integer> spent = statsMap(0, 2, 0, 1, 3);
            Map<Stats, Integer> inc = statsMap(COST_FLAT, 42, 42, 42, 42);

            call(CHOICE_STR, stats, spent, inc, MAX_BIRTH_POINTS, false);

            assertTrue(bus.events.isEmpty(), "no event should fire when updateDisplay is false");
            assertEquals(before, auBirth(), "recalculateStats must not run when updateDisplay is false");
        }
    }
}
