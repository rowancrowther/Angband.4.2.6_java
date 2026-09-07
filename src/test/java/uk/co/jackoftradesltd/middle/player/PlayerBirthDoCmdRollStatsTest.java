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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.co.jackoftradesltd.channel.enums.GameEventType;
import uk.co.jackoftradesltd.channel.messages.data.GameEventData;
import uk.co.jackoftradesltd.middle.enums.Stats;
import uk.co.jackoftradesltd.middle.game.event.EventHandlerInterface;
import uk.co.jackoftradesltd.middle.game.event.EventsHandler;
import uk.co.jackoftradesltd.middle.game.gameengine.Command;
import uk.co.jackoftradesltd.middle.game.gameengine.GameEngine;
import uk.co.jackoftradesltd.middle.game.gameengine.GameState;
import uk.co.jackoftradesltd.middle.game.globals.GameConstants;
import uk.co.jackoftradesltd.middle.game.globals.data.CarryCapData;
import uk.co.jackoftradesltd.middle.game.globals.data.GameConstantsData;
import uk.co.jackoftradesltd.middle.game.globals.data.PlayerData;
import uk.co.jackoftradesltd.middle.game.enums.CommandCode;
import uk.co.jackoftradesltd.middle.game.enums.CommandContext;
import uk.co.jackoftradesltd.testsupport.CalcBonusesFixture;
import uk.co.jackoftradesltd.testsupport.SeededPlayerRegistry;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link PlayerBirth#doCmdRollStats}, the port of C's {@code do_cmd_roll_stats}
 * ({@code player-birth.c:1181-1215}).
 *
 * <p>The C:
 *
 * <pre>{@code
 * void do_cmd_roll_stats(struct command *cmd)
 * {
 *         int i;
 *
 *         save_roller_data(&prev);
 *
 *         get_stats(stats);
 *         get_bonuses();
 *         get_ahw(player);
 *         if (player->history)
 *                 string_free(player->history);
 *         player->history = get_history(player->race->history);
 *
 *         event_signal(EVENT_GOLD);
 *         event_signal(EVENT_AC);
 *         event_signal(EVENT_HP);
 *         event_signal(EVENT_STATS);
 *
 *         points_left = 0;
 *         for (i = 0; i < STAT_MAX; i++) {
 *                 points_spent[i] = 0;
 *                 points_inc[i] = 0;
 *         }
 *
 *         event_signal_birthpoints(points_spent, points_inc, points_left);
 *
 *         rolled_stats = true;
 * }
 * }</pre>
 *
 * <p><b>What is actually under test.</b> {@link PlayerBirth#saveRollerData}, {@link
 * PlayerBirth#getStats}, {@link PlayerBirth#getBonuses}, {@link PlayerBirth#getAHW} and {@link
 * PlayerBirth#getHistory} each have their own suite already; what is left here is the wiring - that
 * the snapshot is taken of the character <em>before</em> anything rolls, that the four bonus events
 * fire before the birthpoints reset rather than after, that the dummy points-left bookkeeping really
 * does land on every real stat, and that {@code rolled_stats} ends up {@code true} regardless of
 * what it held going in.
 *
 * <p>Two of {@link PlayerBirth#getStats}, {@link PlayerBirth#getAHW} and {@link
 * PlayerBirth#getHistory} roll dice, so this class does not pin exact figures for them - it pins the
 * properties C guarantees (age in the race's 15-20 window, history overwritten from the race's
 * chart) the same way {@code PlayerBirthGetStatsTest} and {@code PlayerBirthGetAHWTest} already do
 * for the methods themselves.
 *
 * <p>Class PlayerBirthDoCmdRollStatsTest coded on 260907, commented in full on 260907.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class PlayerBirthDoCmdRollStatsTest {

    /**
     * A placeholder starting gold; no test here reads it back.
     */
    private static final int START_GOLD = 600;

    /**
     * A sentinel age no roll can produce - {@code PlayerRace}'s base age is 14 and its modifier 6,
     * so {@code get_ahw} can only ever land in 15 to 20.
     */
    private static final int OLD_AGE = 999;

    /**
     * A sentinel gold total distinct from {@link #START_GOLD}, so a snapshot that accidentally read
     * the constant rather than the player's own field would be caught.
     */
    private static final long OLD_AU = 12345L;

    /**
     * A sentinel background string, distinct from the empty one a null-chart race always produces.
     */
    private static final String OLD_HISTORY = "a history from before this roll";

    /**
     * The character under test, installed as {@link GameState}'s current player.
     */
    private Player player;

    private Player realPlayer;
    private CapturingBus bus;
    private EventsHandler realBus;

    private Map<Stats, Integer> savedRegistryStats;
    private Map<Stats, Integer> savedRegistryPointsSpent;
    private Map<Stats, Integer> savedRegistryPointsInc;
    private int savedRegistryPointsLeft;
    private boolean savedRolledStats;
    private Birther savedPrev;
    private Object savedConstants;

    private static Field constantsField() throws ReflectiveOperationException {
        Field field = GameConstants.class.getDeclaredField("data");
        field.setAccessible(true);
        return field;
    }

    private static void seedConstants() throws ReflectiveOperationException {
        constantsField().set(null, new GameConstantsData(
                null, null, null, null, null,
                new CarryCapData(23, 10, 40, 5, 16),
                null, null,
                new PlayerData(20, 20, START_GOLD, 5000),
                null, null, null, null, null, null, null, null));
    }

    /**
     * Builds a {@code CMD_ROLL_STATS} command - {@code do_cmd_roll_stats} never reads {@code cmd},
     * so this carries no args, matching the one real producer in the C tree
     * ({@code ui-birth.c:1130}).
     *
     * @return the command
     */
    private static Command rollStatsCommand() {
        return new Command(CommandContext.CTX_BIRTH, CommandCode.CMD_ROLL_STATS, 0, 0,
                new ArrayList<>());
    }

    @BeforeEach
    void seedFixture() throws Exception {
        savedConstants = constantsField().get(null);
        seedConstants();

        player = new Player();
        CalcBonusesFixture.plainCharacter(player);

        // A distinguishable "old" character, so the pre-roll snapshot can be told apart from
        // whatever getStats/getAHW/getHistory roll afterwards.
        player.setAge(OLD_AGE);
        player.setHeightBirth(OLD_AGE);
        player.setWeightBirth(OLD_AGE);
        player.setAUBirth(OLD_AU);
        player.setHistoryBirth(OLD_HISTORY);
        player.setFullName("Old Character");
        for (Stats stat : Stats.values()) {
            if (stat == Stats.STAT_NONE || stat == Stats.STAT_MAX) continue;
            player.setStatBirth(stat, 3);
        }

        realPlayer = GameState.getPlayer();
        GameState.setPlayer(player);

        bus = new CapturingBus();
        realBus = GameEngine.getEventsBusHandler();
        GameEngine.setEventsBusHandler(bus);

        savedRegistryStats = PlayerBirthStateRegistry.getStats();
        savedRegistryPointsSpent = PlayerBirthStateRegistry.getPointsSpent();
        savedRegistryPointsInc = PlayerBirthStateRegistry.getPointsInc();
        savedRegistryPointsLeft = PlayerBirthStateRegistry.getPointsLeft();
        savedRolledStats = PlayerBirthStateRegistry.isRolledStats();
        savedPrev = PlayerBirthStateRegistry.getPrev();

        // Leftover point-buy bookkeeping from a hypothetical earlier session, so the reset is
        // visible.
        Map<Stats, Integer> leftoverSpent = new HashMap<>();
        Map<Stats, Integer> leftoverInc = new HashMap<>();
        for (Stats stat : Stats.values()) {
            if (stat == Stats.STAT_NONE || stat == Stats.STAT_MAX) continue;
            leftoverSpent.put(stat, 8);
            leftoverInc.put(stat, 42);
        }
        PlayerBirthStateRegistry.setPointsSpent(leftoverSpent);
        PlayerBirthStateRegistry.setPointsInc(leftoverInc);
        PlayerBirthStateRegistry.setPointsLeft(3);
        PlayerBirthStateRegistry.setRolledStats(false);
        PlayerBirthStateRegistry.setPrev(new Birther());
    }

    @AfterEach
    void restoreFixture() throws Exception {
        GameState.setPlayer(realPlayer);
        GameEngine.setEventsBusHandler(realBus);
        constantsField().set(null, savedConstants);

        PlayerBirthStateRegistry.setStats(savedRegistryStats);
        PlayerBirthStateRegistry.setPointsSpent(savedRegistryPointsSpent);
        PlayerBirthStateRegistry.setPointsInc(savedRegistryPointsInc);
        PlayerBirthStateRegistry.setPointsLeft(savedRegistryPointsLeft);
        PlayerBirthStateRegistry.setRolledStats(savedRolledStats);
        PlayerBirthStateRegistry.setPrev(savedPrev);
    }

    /**
     * {@code save_roller_data(&prev)} is the very first statement, so the snapshot it leaves in
     * {@code prev} must be the character exactly as it stood before any of {@code get_stats}/
     * {@code get_bonuses}/{@code get_ahw}/{@code get_history} touched it. Asserting against the
     * sentinel values the fixture seeded, rather than whatever the roll happens to produce, is what
     * pins the ordering down: a port that rolled first and snapshotted after would show the new
     * figures here instead.
     */
    @Test
    @DisplayName("snapshots the character as it stood before the roll, not after")
    void snapshotsThePreRollCharacter() {
        PlayerBirth.doCmdRollStats(rollStatsCommand());

        Birther prev = PlayerBirthStateRegistry.getPrev();
        assertEquals(OLD_AGE, prev.getAge());
        assertEquals(OLD_AU, prev.getAu());
        assertEquals(OLD_HISTORY, prev.getHistory());
        assertEquals("Old Character", prev.getName());
        for (Stats stat : Stats.values()) {
            if (stat == Stats.STAT_NONE || stat == Stats.STAT_MAX) continue;
            assertEquals(3, prev.getStat().get(stat), stat.name());
        }
    }

    /**
     * The dummy points-left bookkeeping - {@code points_left = 0} and the loop zeroing every real
     * stat's {@code points_spent}/{@code points_inc} - overwrites whatever leftover figures a
     * previous point-buy session left behind, "to give the UI some dummy info about the points
     * situation" as C's own comment puts it.
     */
    @Test
    @DisplayName("zeroes the points-left bookkeeping for every real stat")
    void zeroesPointsBookkeeping() {
        PlayerBirth.doCmdRollStats(rollStatsCommand());

        assertEquals(0, PlayerBirthStateRegistry.getPointsLeft());
        for (Stats stat : Stats.values()) {
            if (stat == Stats.STAT_NONE || stat == Stats.STAT_MAX) continue;
            assertEquals(0, PlayerBirthStateRegistry.getPointsSpent(stat), stat.name() + " spent");
            assertEquals(0, PlayerBirthStateRegistry.getPointsInc(stat), stat.name() + " inc");
        }
    }

    /**
     * {@code rolled_stats = true;} is the trailing statement and runs unconditionally - it must land
     * even though the fixture starts it {@code false}, locking {@link PlayerBirth#doCmdBuyStat} and
     * {@link PlayerBirth#doCmdSellStat} out until the next reset.
     */
    @Test
    @DisplayName("locks out buying and selling by marking rolledStats true")
    void marksRolledStatsTrue() {
        PlayerBirth.doCmdRollStats(rollStatsCommand());

        assertTrue(PlayerBirthStateRegistry.isRolledStats());
    }

    /**
     * The four bonus-update signals fire before the birthpoints reset's own signal, in C's exact
     * order: {@code EVENT_GOLD}, {@code EVENT_AC}, {@code EVENT_HP}, {@code EVENT_STATS}, then
     * {@code EVENT_BIRTHPOINTS} last, after the points-left loop has run.
     */
    @Test
    @DisplayName("signals GOLD, AC, HP, STATS, then BIRTHPOINTS, in that order")
    void signalsEventsInCOrder() {
        PlayerBirth.doCmdRollStats(rollStatsCommand());

        assertEquals(List.of(GameEventType.EVENT_GOLD, GameEventType.EVENT_AC,
                GameEventType.EVENT_HP, GameEventType.EVENT_STATS,
                GameEventType.EVENT_BIRTHPOINTS), bus.events);
    }

    /**
     * {@code get_ahw(player)} rolls the age as {@code base_age + randint1(mod_age)}; the fixture's
     * race gives 14 and 6, so a genuine roll must land in 15 to 20 - nowhere near the sentinel 999
     * the fixture seeded beforehand.
     */
    @Test
    @DisplayName("rolls a fresh age in the race's 15-20 window")
    void rollsAFreshAge() {
        PlayerBirth.doCmdRollStats(rollStatsCommand());

        int age = player.getAge();
        assertTrue(age >= 15 && age <= 20, "age was " + age + ", outside the race's 15-20 window");
    }

    /**
     * {@code player->history = get_history(player->race->history);} always overwrites the previous
     * background, guarded only by a {@code string_free} the port has nothing to do (see
     * {@link PlayerBirth#saveRollerData}'s own history handling). The fixture's race carries a
     * {@code null} chart, so {@link PlayerBirth#getHistory} answers the empty string - still a
     * genuine overwrite of the sentinel history seeded beforehand, not a leftover.
     */
    @Test
    @DisplayName("overwrites the character's history from the race's chart")
    void overwritesHistoryFromTheRace() {
        PlayerBirth.doCmdRollStats(rollStatsCommand());

        assertEquals("", player.getHistoryBirth());
        assertNotEquals(OLD_HISTORY, player.getHistoryBirth());
    }

    /**
     * Every real stat is rolled to {@code 5 + 1d3 + 1d4 + 1d5}, so 8 to 17 before race and class -
     * {@code PlayerBirthGetStatsTest} already pins the dice mechanics down; this only confirms
     * {@code do_cmd_roll_stats} actually calls through to it rather than leaving the registry's
     * stats untouched.
     */
    @Test
    @DisplayName("rolls fresh stats into the registry, each in the 8-17 range")
    void rollsFreshStatsIntoTheRegistry() {
        PlayerBirth.doCmdRollStats(rollStatsCommand());

        for (Stats stat : Stats.values()) {
            if (stat == Stats.STAT_NONE || stat == Stats.STAT_MAX) continue;
            int rolled = PlayerBirthStateRegistry.getStats().get(stat);
            assertTrue(rolled >= 8 && rolled <= 17, stat.name() + " was rolled at " + rolled);
        }
    }

    /**
     * Captures the events signalled during the test, in order.
     *
     * @author Rowan Crowther
     */
    private static class CapturingBus implements EventsHandler {
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
}
