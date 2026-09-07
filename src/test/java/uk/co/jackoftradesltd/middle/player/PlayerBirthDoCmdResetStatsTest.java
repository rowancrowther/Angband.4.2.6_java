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
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.enums.Stats;
import uk.co.jackoftradesltd.middle.game.enums.CommandCode;
import uk.co.jackoftradesltd.middle.game.enums.CommandContext;
import uk.co.jackoftradesltd.middle.game.event.EventHandlerInterface;
import uk.co.jackoftradesltd.middle.game.event.EventsHandler;
import uk.co.jackoftradesltd.middle.game.gameengine.Command;
import uk.co.jackoftradesltd.middle.game.gameengine.GameEngine;
import uk.co.jackoftradesltd.middle.game.gameengine.GameState;
import uk.co.jackoftradesltd.middle.game.globals.GameConstants;
import uk.co.jackoftradesltd.middle.game.globals.data.CarryCapData;
import uk.co.jackoftradesltd.middle.game.globals.data.GameConstantsData;
import uk.co.jackoftradesltd.middle.game.globals.data.PlayerData;
import uk.co.jackoftradesltd.middle.magic.ClassMagic;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.player.enums.PlayerFlag;
import uk.co.jackoftradesltd.middle.player.enums.PlayerSkill;
import uk.co.jackoftradesltd.testsupport.CalcBonusesFixture;
import uk.co.jackoftradesltd.testsupport.SeededPlayerRegistry;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link PlayerBirth#doCmdResetStats}, the port of C's {@code do_cmd_reset_stats}
 * ({@code player-birth.c:1157-1169}).
 *
 * <p>The C:
 *
 * <pre>{@code
 * void do_cmd_reset_stats(struct command *cmd)
 * {
 *         int choice;
 *
 *         reset_stats(stats, points_spent, points_inc, &points_left, true);
 *
 *         cmd_get_arg_choice(cmd, "choice", &choice);
 *         if (choice)
 *                 generate_stats(stats, points_spent, points_inc, &points_left);
 *
 *         rolled_stats = false;
 * }
 * }</pre>
 *
 * <p><b>What is actually under test.</b> {@link PlayerBirth#resetStats} and the private
 * {@code generateStats} each have their own suite; what is left here is the wiring - that
 * {@code reset_stats} always runs with {@code update_display} true (unlike {@link
 * PlayerBirth#doCmdBirthReset}'s {@code false}), that {@code generate_stats} is gated on the
 * {@code choice} arg's truthiness rather than its presence, and the one place this wiring is not a
 * copy of {@link PlayerBirth#doCmdBuyStat}/{@link PlayerBirth#doCmdSellStat}/{@link
 * PlayerBirth#doCmdChooseClass}'s shape.
 *
 * <p><b>The shape that sets this method apart.</b> Every sibling handler that reads a {@code
 * choice} arg returns early when it is absent, skipping everything else in the method. C's own
 * {@code do_cmd_reset_stats} cannot be read that way: its trailing {@code rolled_stats = false;}
 * sits <em>outside and after</em> the {@code if (choice)} block, so it runs unconditionally, no
 * matter what {@code cmd_get_arg_choice} put in {@code choice} - even a caller that never set the
 * arg at all still clears the flag in C. {@link #argMissingStillResetsAndClearsRolledStats} pins
 * this down: with the arg unset, {@code generateStats} must not run, but {@code resetStats} and the
 * trailing {@code setRolledStats(false)} must both still happen, matching C's unconditional tail
 * rather than the early-return shape used elsewhere in this class.
 *
 * <p>The fixture reuses {@code PlayerBirthDoCmdChooseClassTest}'s non-caster, non-warrior-favoured
 * class ({@code maxAttacks} 6, flat point-buy costs) so the ordinary-path figures are the same known
 * ones {@code PlayerBirthGenerateStatsTest.WarriorFullRun} already proved: a 20-point reset from
 * base 10 buys {@code STR} to 17, {@code DEX} to 16 and {@code CON} to 16, leaves {@code WIS}/{@code
 * INT} untouched, and spends every point.
 *
 * <p>Class PlayerBirthDoCmdResetStatsTest coded on 260907, commented in full on 260907.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class PlayerBirthDoCmdResetStatsTest {

    /**
     * A placeholder starting gold; no test here reads it back.
     */
    private static final int START_GOLD = 600;

    /**
     * {@code birth_stat_costs[11]} - the flat cost of every point-buy point from base 10 up to 16,
     * which is every point {@code generateStats} spends in this fixture.
     */
    private static final int COST_FLAT = 1;

    /**
     * C's {@code MAX_BIRTH_POINTS} ({@code player-birth.c:683}), what {@code resetStats} always
     * resets the running total to before {@code generateStats} (if it runs at all) spends any of it.
     */
    private static final int MAX_BIRTH_POINTS = 20;

    /**
     * The character under test, installed as {@link GameState}'s current player.
     */
    private Player player;

    private Player realPlayer;
    private CapturingBus bus;
    private EventsHandler realBus;
    private boolean realCharacterGenerated;
    private Object savedConstants;

    private Map<Stats, Integer> savedRegistryStats;
    private Map<Stats, Integer> savedRegistryPointsSpent;
    private Map<Stats, Integer> savedRegistryPointsInc;
    private int savedRegistryPointsLeft;
    private boolean savedRolledStats;

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

    private static void setCharacterGenerated(boolean value) throws ReflectiveOperationException {
        Field field = GameState.class.getDeclaredField("characterGenerated");
        field.setAccessible(true);
        field.set(null, value);
    }

    private static void writeInstance(Object target, String name, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(name);
        field.setAccessible(true);
        field.set(target, value);
    }

    /**
     * A non-caster, non-warrior-favoured class with flat point-buy costs - the same shape
     * {@code PlayerBirthDoCmdChooseClassTest}'s {@code CHOSEN_CLASS_NAME} and
     * {@code PlayerBirthGenerateStatsTest.WarriorFullRun} both use, so their known figures apply
     * here too.
     *
     * @return the class
     */
    private static PlayerClass testClass() {
        Map<Stats, Integer> stats = new HashMap<>();
        Map<PlayerSkill, Integer> skills = new HashMap<>();
        for (Stats stat : Stats.values()) {
            if (stat == Stats.STAT_NONE || stat == Stats.STAT_MAX) continue;
            stats.put(stat, 0);
        }
        for (PlayerSkill skill : PlayerSkill.values()) {
            if (skill == PlayerSkill.SKILL_NONE || skill == PlayerSkill.SKILL_MAX) continue;
            skills.put(skill, 0);
        }
        return new PlayerClass("Test Warrior", List.of(), stats, skills, new HashMap<>(skills), 0, 0,
                new Flag<>(ObjectFlag.class), new Flag<>(PlayerFlag.class),
                6, 100, 1, List.of(), ClassMagic.NONE);
    }

    /**
     * Builds the {@code CMD_RESET_STATS} command C's {@code cmd_get_arg_choice} would read from -
     * the port of {@code cmd_set_arg_choice(cmdq_peek(), "choice", ...)}, which both real producers
     * of this command call before it is ever executed ({@code ui-birth.c:824},
     * {@code ui-birth.c:1270}).
     *
     * @param choice whether to auto-generate stats, or {@code null} to leave the arg unset
     * @return the command
     */
    private static Command resetStatsCommand(Integer choice) {
        Command cmd = new Command(CommandContext.CTX_BIRTH, CommandCode.CMD_RESET_STATS, 0, 0,
                new ArrayList<>());
        if (choice != null) cmd.setArgChoice("choice", choice);
        return cmd;
    }

    /**
     * Asserts every one of the five real stats in a map equals an expected value.
     */
    private static void assertAllRealStats(Map<Stats, Integer> map, int expected, String label) {
        for (Stats stat : Stats.values()) {
            if (stat == Stats.STAT_NONE || stat == Stats.STAT_MAX) continue;
            assertEquals(expected, map.get(stat), label + " for " + stat);
        }
    }

    @BeforeEach
    void seedFixture() throws Exception {
        savedConstants = constantsField().get(null);
        seedConstants();

        player = new Player();
        CalcBonusesFixture.plainCharacter(player);
        writeInstance(player, "playerClass", testClass());

        realPlayer = GameState.getPlayer();
        GameState.setPlayer(player);

        bus = new CapturingBus();
        realBus = GameEngine.getEventsBusHandler();
        GameEngine.setEventsBusHandler(bus);

        realCharacterGenerated = GameState.getCharacterGenerated();
        setCharacterGenerated(false);

        savedRegistryStats = PlayerBirthStateRegistry.getStats();
        savedRegistryPointsSpent = PlayerBirthStateRegistry.getPointsSpent();
        savedRegistryPointsInc = PlayerBirthStateRegistry.getPointsInc();
        savedRegistryPointsLeft = PlayerBirthStateRegistry.getPointsLeft();
        savedRolledStats = PlayerBirthStateRegistry.isRolledStats();

        // Leftover point-buy state from a hypothetical earlier session, so a reset is visible.
        Map<Stats, Integer> leftoverStats = new HashMap<>();
        Map<Stats, Integer> leftoverSpent = new HashMap<>();
        Map<Stats, Integer> leftoverInc = new HashMap<>();
        for (Stats stat : Stats.values()) {
            if (stat == Stats.STAT_NONE || stat == Stats.STAT_MAX) continue;
            leftoverStats.put(stat, 17);
            leftoverSpent.put(stat, 8);
            leftoverInc.put(stat, 42);
        }
        PlayerBirthStateRegistry.setStats(leftoverStats);
        PlayerBirthStateRegistry.setPointsSpent(leftoverSpent);
        PlayerBirthStateRegistry.setPointsInc(leftoverInc);
        PlayerBirthStateRegistry.setPointsLeft(3);
        PlayerBirthStateRegistry.setRolledStats(true);
    }

    @AfterEach
    void restoreFixture() throws Exception {
        GameState.setPlayer(realPlayer);
        GameEngine.setEventsBusHandler(realBus);
        constantsField().set(null, savedConstants);
        setCharacterGenerated(realCharacterGenerated);

        PlayerBirthStateRegistry.setStats(savedRegistryStats);
        PlayerBirthStateRegistry.setPointsSpent(savedRegistryPointsSpent);
        PlayerBirthStateRegistry.setPointsInc(savedRegistryPointsInc);
        PlayerBirthStateRegistry.setPointsLeft(savedRegistryPointsLeft);
        PlayerBirthStateRegistry.setRolledStats(savedRolledStats);
    }

    /**
     * The ordinary path: a truthy {@code choice} resets the stats, then immediately runs
     * {@code generateStats} on top - exactly {@code PlayerBirthGenerateStatsTest.WarriorFullRun}'s
     * fixture, so the same figures apply.
     */
    @Test
    @DisplayName("a truthy choice resets the stats, then auto-generates on top")
    void truthyChoiceResetsThenAutoGenerates() {
        PlayerBirth.doCmdResetStats(resetStatsCommand(1));

        assertEquals(17, PlayerBirthStateRegistry.getStats().get(Stats.STAT_STR));
        assertEquals(16, PlayerBirthStateRegistry.getStats().get(Stats.STAT_DEX));
        assertEquals(16, PlayerBirthStateRegistry.getStats().get(Stats.STAT_CON));
        assertEquals(10, PlayerBirthStateRegistry.getStats().get(Stats.STAT_WIS));
        assertEquals(10, PlayerBirthStateRegistry.getStats().get(Stats.STAT_INT));
        assertEquals(0, PlayerBirthStateRegistry.getPointsLeft(),
                "every point of the 20-point reset should end up spent by generateStats");
        assertFalse(PlayerBirthStateRegistry.isRolledStats());
    }

    /**
     * C's {@code if (choice)} is a truthiness test on the raw stack {@code int}, not a presence
     * check - a {@code choice} of literal {@code 0} is falsy and skips {@code generate_stats} just
     * as surely as a missing arg does, but {@code reset_stats} still ran first and {@code
     * rolled_stats} still clears at the end.
     */
    @Test
    @DisplayName("a zero choice resets the stats but skips auto-generate")
    void zeroChoiceResetsButSkipsAutoGenerate() {
        PlayerBirth.doCmdResetStats(resetStatsCommand(0));

        assertAllRealStats(PlayerBirthStateRegistry.getStats(), 10, "reset stat value");
        assertAllRealStats(PlayerBirthStateRegistry.getPointsSpent(), 0, "reset points spent");
        assertAllRealStats(PlayerBirthStateRegistry.getPointsInc(), COST_FLAT, "reset increment cost");
        assertEquals(MAX_BIRTH_POINTS, PlayerBirthStateRegistry.getPointsLeft());
        assertFalse(PlayerBirthStateRegistry.isRolledStats());
    }

    /**
     * The shape that sets this method apart from {@link PlayerBirth#doCmdBuyStat}, {@link
     * PlayerBirth#doCmdSellStat} and {@link PlayerBirth#doCmdChooseClass}: those three return
     * early - skipping everything - when the {@code choice} arg is absent. C's own {@code
     * do_cmd_reset_stats} cannot be read that way, since its trailing {@code rolled_stats = false;}
     * sits outside and after the {@code if (choice)} block and so runs unconditionally. With the arg
     * unset here, {@code generateStats} must not run (the stats stay at their post-reset 10), but
     * {@code resetStats} and the trailing {@code setRolledStats(false)} must both still happen -
     * an earlier draft returned early on the missing arg, which would have left {@code
     * rolledStats} stuck at {@code true} and the leftover stat map untouched.
     */
    @Test
    @DisplayName("with the choice arg unset, still resets and clears rolledStats, but skips auto-generate")
    void argMissingStillResetsAndClearsRolledStats() {
        PlayerBirth.doCmdResetStats(resetStatsCommand(null));

        assertAllRealStats(PlayerBirthStateRegistry.getStats(), 10, "reset stat value");
        assertEquals(MAX_BIRTH_POINTS, PlayerBirthStateRegistry.getPointsLeft());
        assertFalse(PlayerBirthStateRegistry.isRolledStats(),
                "rolled_stats = false is unconditional in C, not gated on the choice arg");
    }

    /**
     * {@code reset_stats} is always called with {@code update_display} {@code true} here, unlike
     * {@link PlayerBirth#doCmdBirthReset}'s {@code false} - so even the zero-choice path, where
     * {@code generateStats} never runs, must still signal the birthpoints event from {@code
     * resetStats}'s own call.
     */
    @Test
    @DisplayName("resetStats always signals the UI, even when auto-generate is skipped")
    void resetStatsAlwaysUpdatesDisplayEvenWithoutAutoGenerate() {
        PlayerBirth.doCmdResetStats(resetStatsCommand(0));

        assertTrue(bus.events.contains(GameEventType.EVENT_BIRTHPOINTS),
                "resetStats's own update_display=true call must have signalled the UI");
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
