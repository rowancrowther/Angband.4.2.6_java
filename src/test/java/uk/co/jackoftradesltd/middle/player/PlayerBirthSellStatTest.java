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
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link PlayerBirth#sellStat(Stats, Map, Map, Map, int, boolean)}, the port of C's
 * {@code sell_stat} ({@code player-birth.c:777-807}).
 *
 * <p>The C:
 *
 * <pre>{@code
 * if (!(choice >= STAT_MAX || choice < 0) && (stats_local[choice] > 10)) {
 *     int stat_cost = birth_stat_costs[stats_local[choice]];
 *     stats_local[choice]--;
 *     points_spent_local[choice] -= stat_cost;
 *     points_inc_local[choice] = birth_stat_costs[stats_local[choice] + 1];
 *     *points_left_local += stat_cost;
 *     if (update_display) {
 *         event_signal_birthpoints(points_spent_local, points_inc_local, *points_left_local);
 *         recalculate_stats(stats_local, *points_left_local);
 *     }
 *     return true;
 * }
 * return false;
 * }</pre>
 *
 * <p><b>Two things this suite exists to pin down, because both were wrong at some point during the
 * port.</b> First, the stat was never actually decremented - {@code stats_local[choice]--} had no
 * counterpart at all, so a "sale" refunded points without ever lowering the stat.
 * {@link OrdinaryPurchase} and {@link StatTargeting} both assert the chosen stat's map entry directly,
 * not just the points returned. Second, {@code pointsIncLocal} was refreshed from the stat's
 * pre-decrement value rather than its post-decrement one, which happened to read the same flat cost
 * in the middle of the range but read one entry past {@link PlayerBirth}'s own {@code birthStatCosts}
 * - {@code birthStatCosts[19]} - when selling a stat at 18, throwing
 * {@code ArrayIndexOutOfBoundsException} where C reads cleanly. {@link Boundary18} pins that boundary
 * down.
 *
 * <p>Unlike {@link PlayerBirthBuyStatTest}, {@code choice} here is the enum constant itself rather
 * than C's raw index, so there is no shift arithmetic to mistake and no out-of-{@link Stats#values()}
 * integer to construct; {@link InvalidChoice} covers only the two sentinels the enum can actually
 * hand this method.
 *
 * <p>{@link PlayerBirth#recalculateStats} and the event bus are each covered on their own terms
 * elsewhere ({@link PlayerBirthRecalculateStatsTest}); what is checked here is that this method
 * reaches them with the right arguments when {@code updateDisplay} is set, and reaches neither when
 * it is not - the same split {@link PlayerBirthBuyStatTest} and {@link PlayerBirthResetStatsTest}
 * draw, in {@code sell_stat}'s own order (birthpoints, then the recalculation), the same order
 * {@code buy_stat} uses and the opposite of {@code reset_stats}'s.
 *
 * <p>{@link PlayerBirth#sellStat}'s return type is a private record, so every test reaches its two
 * components through reflection rather than by naming the type.
 *
 * <p>Class PlayerBirthSellStatTest coded on 260906, commented in full on 260906.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class PlayerBirthSellStatTest {

    /**
     * The shipped {@code player:start-gold} figure ({@code constants.txt:201}).
     */
    private static final int START_GOLD = 600;

    /**
     * C's {@code MAX_BIRTH_POINTS} ({@code player-birth.c:683}), {@code 3 * (1+1+1+1+1+1+2)}.
     */
    private static final int MAX_BIRTH_POINTS = 20;

    /**
     * {@code birth_stat_costs[11..16]} ({@code player-birth.c:679}) - the cost of any point between
     * base 10 and 16.
     */
    private static final int COST_FLAT = 1;

    /**
     * {@code birth_stat_costs[17]} - the cost of the point that raised a stat from 16 to 17, and so
     * the refund for selling it back down.
     */
    private static final int COST_AT_SEVENTEEN = 2;

    /**
     * {@code birth_stat_costs[18]} - the cost of the point that raised a stat from 17 to 18, the
     * highest a stat can reach through point-buy, and so the refund for selling from the cap.
     */
    private static final int COST_AT_EIGHTEEN = 4;

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
     * Reads the private record's {@code bool} component (whether the sale succeeded) by reflection.
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
     * Calls {@link PlayerBirth#sellStat}, catching the private {@code IntAndBoolean} result as a
     * bare {@code Object} since the record's name is not accessible from this class.
     *
     * @param choice           the stat to sell
     * @param statsLocal       the stat values map
     * @param pointsSpentLocal the points-spent map
     * @param pointsIncLocal   the increment-cost map
     * @param pointsLeftLocal  the incoming point total
     * @param updateDisplay    whether to signal the UI
     * @return the private result object
     */
    private Object call(Stats choice, Map<Stats, Integer> statsLocal, Map<Stats, Integer> pointsSpentLocal,
                        Map<Stats, Integer> pointsIncLocal, int pointsLeftLocal, boolean updateDisplay) {
        return PlayerBirth.sellStat(choice, statsLocal, pointsSpentLocal, pointsIncLocal, pointsLeftLocal,
                updateDisplay);
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
     * The ordinary path: a valid stat above 10, {@code updateDisplay} false.
     */
    @Nested
    @DisplayName("an ordinary sale")
    class OrdinaryPurchase {

        /**
         * C's {@code stats_local[choice]--}, {@code points_spent_local[choice] -= stat_cost},
         * {@code points_inc_local[choice] = birth_stat_costs[stats_local[choice] + 1]} and
         * {@code *points_left_local += stat_cost}, none of which touch any other stat's entry.
         *
         * @throws ReflectiveOperationException if the private result cannot be read
         */
        @Test
        @DisplayName("lowers the chosen stat, refunds the cost, and leaves other stats untouched")
        void lowersTheChosenStat() throws ReflectiveOperationException {
            Map<Stats, Integer> stats = statsMap(12, 13, 9, 12, 11);
            Map<Stats, Integer> spent = statsMap(2, 2, 0, 1, 3);
            Map<Stats, Integer> inc = statsMap(42, 42, 42, 42, 42);

            Object result = call(Stats.STAT_STR, stats, spent, inc, 15, false);

            assertTrue(succeededOf(result), "selling a point off a stat above 10 must succeed");
            assertEquals(15 + COST_FLAT, pointsLeftOf(result));
            assertEquals(11, stats.get(Stats.STAT_STR));
            assertEquals(2 - COST_FLAT, spent.get(Stats.STAT_STR));
            assertEquals(COST_FLAT, inc.get(Stats.STAT_STR), "cost of buying 11 to 12 back is still flat");

            assertEquals(13, stats.get(Stats.STAT_INT), "untouched stat must be unchanged");
            assertEquals(2, spent.get(Stats.STAT_INT), "untouched stat's spend must be unchanged");
            assertEquals(42, inc.get(Stats.STAT_INT), "untouched stat's increment cost must be unchanged");
        }
    }

    /**
     * Confirms the map key used is the {@code choice} passed in, not a neighbouring stat.
     */
    @Nested
    @DisplayName("stat targeting")
    class StatTargeting {

        /**
         * Sells constitution from 17, the top of the flat-cost climb, and checks the neighbouring
         * stat (dexterity, one entry earlier in the enum) is left alone.
         */
        @Test
        @DisplayName("STAT_CON lowers constitution, not a neighbouring stat")
        void sellsConstitutionNotItsNeighbour() {
            Map<Stats, Integer> stats = statsMap(10, 10, 10, 10, 17);
            Map<Stats, Integer> spent = statsMap(0, 0, 0, 0, 8);
            Map<Stats, Integer> inc = statsMap(1, 1, 1, 1, 42);

            call(Stats.STAT_CON, stats, spent, inc, 12, false);

            assertEquals(16, stats.get(Stats.STAT_CON));
            assertEquals(8 - COST_AT_SEVENTEEN, spent.get(Stats.STAT_CON));
            assertEquals(COST_AT_SEVENTEEN, inc.get(Stats.STAT_CON));
            assertEquals(10, stats.get(Stats.STAT_DEX), "STAT_DEX (the neighbour one index down) must be untouched");
        }
    }

    /**
     * C's {@code !(choice >= STAT_MAX || choice < 0)} guard, tested at the two sentinel values the
     * enum can actually hand this method - there is no shifted or out-of-domain integer to
     * mis-construct, unlike {@link PlayerBirthBuyStatTest.InvalidChoice}.
     */
    @Nested
    @DisplayName("a sentinel choice")
    class InvalidChoice {

        /**
         * Asserts a sentinel {@code choice} is rejected cleanly, leaving every map untouched and
         * returning the incoming points-left figure unchanged.
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
            assertEquals(17, pointsLeftOf(result[0]), "a sentinel choice must not refund anything");
            for (Stats stat : List.of(Stats.STAT_STR, Stats.STAT_INT, Stats.STAT_WIS, Stats.STAT_DEX,
                    Stats.STAT_CON)) {
                assertEquals(14, stats.get(stat), "stat map must be untouched");
                assertEquals(3, spent.get(stat), "spend map must be untouched");
                assertEquals(9, inc.get(stat), "increment map must be untouched");
            }
            assertTrue(bus.events.isEmpty(), "a sentinel choice must not signal the UI even when requested");
        }

        /**
         * @throws ReflectiveOperationException if the private result cannot be read
         */
        @Test
        @DisplayName("STAT_NONE is rejected without touching the maps")
        void rejectsStatNone() throws ReflectiveOperationException {
            assertRejectedCleanly(Stats.STAT_NONE);
        }

        /**
         * @throws ReflectiveOperationException if the private result cannot be read
         */
        @Test
        @DisplayName("STAT_MAX is rejected without touching the maps")
        void rejectsStatMax() throws ReflectiveOperationException {
            assertRejectedCleanly(Stats.STAT_MAX);
        }
    }

    /**
     * C's {@code stats_local[choice] > 10} guard: a stat already at the point-buy floor cannot be
     * sold any lower, whatever its recorded cost or spend.
     */
    @Nested
    @DisplayName("a stat already at the point-buy floor")
    class StatAlreadyAtFloor {

        /**
         * Every field the call might otherwise touch is seeded with an obviously-wrong sentinel, so
         * a guard that fired late (after doing some of the work) would be caught.
         *
         * @throws ReflectiveOperationException if the private result cannot be read
         */
        @Test
        @DisplayName("a stat at 10 is never sold, regardless of the points on the table")
        void refusesToSellBelowTen() throws ReflectiveOperationException {
            Map<Stats, Integer> stats = statsMap(10, 14, 14, 14, 14);
            Map<Stats, Integer> spent = statsMap(999, 0, 0, 0, 0);
            Map<Stats, Integer> inc = statsMap(999, 1, 1, 1, 1);

            Object result = call(Stats.STAT_STR, stats, spent, inc, 5, false);

            assertFalse(succeededOf(result));
            assertEquals(5, pointsLeftOf(result));
            assertEquals(10, stats.get(Stats.STAT_STR));
            assertEquals(999, spent.get(Stats.STAT_STR), "spend map must be untouched when the floor guard fires");
            assertEquals(999, inc.get(Stats.STAT_STR), "increment map must be untouched when the floor guard fires");
        }
    }

    /**
     * Selling a stat down from 18: the boundary that once read {@code birthStatCosts[19]} - one past
     * both C's {@code birth_stat_costs} and the port's own {@link PlayerBirth#birthStatCosts} - because
     * {@code pointsIncLocal} was refreshed from the stat's value before it was decremented rather
     * than after. C reads {@code birth_stat_costs[17 + 1]} once the local copy has already dropped to
     * 17, landing back inside the array; this suite pins that same in-bounds read down.
     */
    @Nested
    @DisplayName("selling a stat down from 18")
    class Boundary18 {

        /**
         * @throws ReflectiveOperationException if the private result cannot be read
         */
        @Test
        @DisplayName("succeeds, refunds COST_AT_EIGHTEEN, and does not throw")
        void sellsFromTheCapWithoutThrowing() throws ReflectiveOperationException {
            Map<Stats, Integer> stats = statsMap(18, 10, 10, 10, 10);
            Map<Stats, Integer> spent = statsMap(12, 0, 0, 0, 0);
            Map<Stats, Integer> inc = statsMap(42, 1, 1, 1, 1);

            Object[] result = new Object[1];
            assertDoesNotThrow(() -> result[0] = call(Stats.STAT_STR, stats, spent, inc, 8, false));

            assertTrue(succeededOf(result[0]));
            assertEquals(8 + COST_AT_EIGHTEEN, pointsLeftOf(result[0]));
            assertEquals(17, stats.get(Stats.STAT_STR));
            assertEquals(12 - COST_AT_EIGHTEEN, spent.get(Stats.STAT_STR));
            assertEquals(COST_AT_EIGHTEEN, inc.get(Stats.STAT_STR),
                    "birth_stat_costs[18] read after the decrement, same figure as the refund");
        }
    }

    /**
     * The {@code updateDisplay} guard: C's {@code if (update_display) { event_signal_birthpoints(...);
     * recalculate_stats(...); } } - birthpoints first, then the recalculation, the same order
     * {@code buy_stat} uses and the opposite of {@code reset_stats}'s.
     */
    @Nested
    @DisplayName("the updateDisplay guard")
    class UpdateDisplayGuard {

        /**
         * With the guard set, the birthpoints signal fires before
         * {@link PlayerBirth#recalculateStats}'s own four events, and the gold figure that
         * recalculation derives is built from the points-left total <em>after</em> this sale, not
         * before it.
         *
         * @throws ReflectiveOperationException if a private field cannot be reached
         */
        @Test
        @DisplayName("true signals birthpoints, then recalculates, using the post-sale total")
        void signalsThenRecalculatesUsingThePostSaleTotal() throws ReflectiveOperationException {
            Map<Stats, Integer> stats = statsMap(12, 13, 9, 12, 11);
            Map<Stats, Integer> spent = statsMap(2, 2, 0, 1, 3);
            Map<Stats, Integer> inc = statsMap(42, 42, 42, 42, 42);

            call(Stats.STAT_STR, stats, spent, inc, 18, true);

            assertEquals(List.of(GameEventType.EVENT_BIRTHPOINTS, GameEventType.EVENT_GOLD,
                    GameEventType.EVENT_AC, GameEventType.EVENT_HP, GameEventType.EVENT_STATS), bus.events);
            assertEquals(START_GOLD + 50 * (18 + COST_FLAT), auBirth(),
                    "gold must be derived from the points-left total after this sale");
        }

        /**
         * With the guard clear, neither call happens: no event fires, and no gold is derived.
         *
         * @throws ReflectiveOperationException if a private field cannot be reached
         */
        @Test
        @DisplayName("false calls neither the birthpoints signal nor recalculateStats")
        void doesNeitherWhenClear() throws ReflectiveOperationException {
            int before = auBirth();
            Map<Stats, Integer> stats = statsMap(12, 13, 9, 12, 11);
            Map<Stats, Integer> spent = statsMap(2, 2, 0, 1, 3);
            Map<Stats, Integer> inc = statsMap(42, 42, 42, 42, 42);

            call(Stats.STAT_STR, stats, spent, inc, 18, false);

            assertTrue(bus.events.isEmpty(), "no event should fire when updateDisplay is false");
            assertEquals(before, auBirth(), "recalculateStats must not run when updateDisplay is false");
        }
    }
}
