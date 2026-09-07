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
 * Tests {@link PlayerBirth#doCmdSellStat}, the port of C's {@code do_cmd_sell_stat} ({@code
 * player-birth.c:1146-1155}).
 *
 * <p>The C:
 *
 * <pre>{@code
 * void do_cmd_sell_stat(struct command *cmd)
 * {
 *         if (!rolled_stats) {
 *                 int choice;
 *                 cmd_get_arg_choice(cmd, "choice", &choice);
 *                 sell_stat(choice, stats, points_spent, points_inc, &points_left, true);
 *         }
 * }
 * }</pre>
 *
 * <p><b>What is actually under test.</b> {@link PlayerBirth#sellStat} has its own suite ({@link
 * PlayerBirthSellStatTest}); what is checked here is the wiring - the {@code rolledStats} guard, the
 * argument read, and the registry round trip - plus the one place the wiring has to do more work
 * than C's does. C hands its raw {@code int choice} straight to {@code sell_stat}, whose own bounds
 * check ({@code choice >= STAT_MAX || choice < 0}) absorbs every out-of-range value as a silent
 * no-op. This method instead converts the arg to a {@link Stats} with {@link Stats#getStats} first,
 * and that lookup only recognises the two sentinels - any other out-of-range index comes back
 * {@code null}, which {@link PlayerBirth#sellStat}'s own sentinel check does not catch. {@link
 * #choiceFarOutOfRangeDoesNothingRatherThanThrowing} pins down the explicit {@code null} guard added
 * to stand in for the missing half of C's range test.
 *
 * <p>Class PlayerBirthDoCmdSellStatTest coded on 260907, commented in full on 260907.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class PlayerBirthDoCmdSellStatTest {

    /**
     * The shipped {@code player:start-gold} figure ({@code constants.txt:201}).
     */
    private static final int START_GOLD = 600;

    /**
     * C's {@code MAX_BIRTH_POINTS} ({@code player-birth.c:683}).
     */
    private static final int MAX_BIRTH_POINTS = 20;

    /**
     * {@code birth_stat_costs[11]} - the flat cost of the point between base 10 and 11, in both
     * directions.
     */
    private static final int COST_FLAT = 1;

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

    /**
     * Five stats at base 10, except {@code STAT_STR} which starts at 11 - one flat-cost point
     * already bought, so there is something to sell back without first buying it in-test.
     *
     * @return the map
     */
    private static Map<Stats, Integer> baseStatsWithOneStrPointBought() {
        Map<Stats, Integer> stats = new HashMap<>();
        for (Stats stat : List.of(Stats.STAT_STR, Stats.STAT_INT, Stats.STAT_WIS, Stats.STAT_DEX, Stats.STAT_CON)) {
            stats.put(stat, 10);
        }
        stats.put(Stats.STAT_STR, 11);
        return stats;
    }

    /**
     * Matches {@link #baseStatsWithOneStrPointBought()}: every stat unspent except {@code STAT_STR},
     * which carries the {@link #COST_FLAT} already paid for its eleventh point.
     *
     * @return the map
     */
    private static Map<Stats, Integer> spendWithOneStrPointBought() {
        Map<Stats, Integer> spend = new HashMap<>();
        for (Stats stat : List.of(Stats.STAT_STR, Stats.STAT_INT, Stats.STAT_WIS, Stats.STAT_DEX, Stats.STAT_CON)) {
            spend.put(stat, 0);
        }
        spend.put(Stats.STAT_STR, COST_FLAT);
        return spend;
    }

    private static Map<Stats, Integer> flatCosts() {
        Map<Stats, Integer> inc = new HashMap<>();
        for (Stats stat : List.of(Stats.STAT_STR, Stats.STAT_INT, Stats.STAT_WIS, Stats.STAT_DEX, Stats.STAT_CON)) {
            inc.put(stat, COST_FLAT);
        }
        return inc;
    }

    /**
     * Builds the {@code CMD_SELL_STAT} command C's {@code cmd_get_arg_choice} would read from - the
     * port of {@code cmd_set_arg_choice(cmdq_peek(), "choice", stat)}, which the birth-screen points
     * menu calls before the command ever runs ({@code ui-birth.c:1255-1257}).
     *
     * @param choice the raw choice index to store, or {@code null} to leave the arg unset
     * @return the command
     */
    private static Command sellStatCommand(Integer choice) {
        Command cmd = new Command(CommandContext.CTX_BIRTH, CommandCode.CMD_SELL_STAT, 0, 0, new ArrayList<>());
        if (choice != null) cmd.setArgChoice("choice", choice);
        return cmd;
    }

    @BeforeEach
    void seedFixture() throws ReflectiveOperationException {
        savedConstants = constantsField().get(null);
        seedConstants();

        player = new Player();
        CalcBonusesFixture.plainCharacter(player);

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

        PlayerBirthStateRegistry.setStats(baseStatsWithOneStrPointBought());
        PlayerBirthStateRegistry.setPointsSpent(spendWithOneStrPointBought());
        PlayerBirthStateRegistry.setPointsInc(flatCosts());
        PlayerBirthStateRegistry.setPointsLeft(MAX_BIRTH_POINTS - COST_FLAT);
        PlayerBirthStateRegistry.setRolledStats(false);
    }

    @AfterEach
    void restoreFixture() throws ReflectiveOperationException {
        GameState.setPlayer(realPlayer);
        GameEngine.setEventsBusHandler(realBus);
        setCharacterGenerated(realCharacterGenerated);
        constantsField().set(null, savedConstants);

        PlayerBirthStateRegistry.setStats(savedRegistryStats);
        PlayerBirthStateRegistry.setPointsSpent(savedRegistryPointsSpent);
        PlayerBirthStateRegistry.setPointsInc(savedRegistryPointsInc);
        PlayerBirthStateRegistry.setPointsLeft(savedRegistryPointsLeft);
        PlayerBirthStateRegistry.setRolledStats(savedRolledStats);
    }

    /**
     * The ordinary path: not rolled, a valid choice, a stat above the floor. {@code sell_stat} is
     * always called with {@code update_display} true here, unlike {@code reset_stats}'s conditional
     * call elsewhere in the file, so the birthpoints signal and {@link PlayerBirth#recalculateStats}'s
     * own four events must both fire.
     */
    @Test
    @DisplayName("lowers the chosen stat, refunds the cost, and signals the UI")
    void ordinaryChoiceSellsTheStatAndSignals() {
        PlayerBirth.doCmdSellStat(sellStatCommand(Stats.STAT_STR.getValue()));

        assertEquals(10, PlayerBirthStateRegistry.getStats().get(Stats.STAT_STR));
        assertEquals(0, PlayerBirthStateRegistry.getPointsSpent().get(Stats.STAT_STR));
        assertEquals(MAX_BIRTH_POINTS, PlayerBirthStateRegistry.getPointsLeft());
        assertEquals(List.of(GameEventType.EVENT_BIRTHPOINTS, GameEventType.EVENT_GOLD,
                GameEventType.EVENT_AC, GameEventType.EVENT_HP, GameEventType.EVENT_STATS), bus.events);
    }

    /**
     * The floor guard found in stage 1: C's {@code stats_local[choice] > 10} test refuses to sell a
     * stat already sitting at the point-buy base of 10, so {@code STAT_CON} - untouched at 10 by the
     * fixture - must come back unchanged rather than dropping to 9.
     */
    @Test
    @DisplayName("a stat already at the base of 10 is not sold below it")
    void statAtFloorIsNotSoldBelowIt() {
        PlayerBirth.doCmdSellStat(sellStatCommand(Stats.STAT_CON.getValue()));

        assertEquals(10, PlayerBirthStateRegistry.getStats().get(Stats.STAT_CON));
        assertEquals(0, PlayerBirthStateRegistry.getPointsSpent().get(Stats.STAT_CON));
        assertEquals(MAX_BIRTH_POINTS - COST_FLAT, PlayerBirthStateRegistry.getPointsLeft());
        assertTrue(bus.events.isEmpty());
    }

    /**
     * C's {@code if (!rolled_stats)} guard: with stats currently rolled, the whole body is skipped -
     * the arg is never even read, so no sale happens and nothing is signalled.
     */
    @Test
    @DisplayName("rolledStats true skips the sale entirely")
    void rolledStatsSkipsTheSale() {
        PlayerBirthStateRegistry.setRolledStats(true);

        PlayerBirth.doCmdSellStat(sellStatCommand(Stats.STAT_STR.getValue()));

        assertEquals(11, PlayerBirthStateRegistry.getStats().get(Stats.STAT_STR));
        assertEquals(MAX_BIRTH_POINTS - COST_FLAT, PlayerBirthStateRegistry.getPointsLeft());
        assertTrue(bus.events.isEmpty());
    }

    /**
     * The port's deliberate divergence from C, shared with {@link PlayerBirth#doCmdBuyStat}: with the
     * {@code "choice"} arg unset, the method returns before touching the registry, unlike C, which
     * would fall through and pass whatever garbage {@code choice} held straight to {@code sell_stat}.
     * The one producer of {@code CMD_SELL_STAT} in the C tree sets the arg before the command runs, so
     * this path is otherwise unreachable in practice.
     */
    @Test
    @DisplayName("with the choice arg unset, does nothing rather than following C's fallthrough")
    void argMissingDoesNothing() {
        PlayerBirth.doCmdSellStat(sellStatCommand(null));

        assertEquals(11, PlayerBirthStateRegistry.getStats().get(Stats.STAT_STR));
        assertEquals(MAX_BIRTH_POINTS - COST_FLAT, PlayerBirthStateRegistry.getPointsLeft());
        assertTrue(bus.events.isEmpty());
    }

    /**
     * The regression this pass exists to fix: a {@code choice} outside {@code -1..5} converts to a
     * {@code null} {@link Stats} that {@link PlayerBirth#sellStat}'s own sentinel check does not
     * catch. Before the guard was added, this threw a {@code NullPointerException} from the map
     * lookup inside {@code sellStat}; C's own complete bounds check would have absorbed the same value
     * as a silent no-op, so that is what this must do too.
     */
    @Test
    @DisplayName("a choice far out of range does nothing rather than throwing")
    void choiceFarOutOfRangeDoesNothingRatherThanThrowing() {
        assertDoesNotThrow(() -> PlayerBirth.doCmdSellStat(sellStatCommand(42)));

        assertEquals(11, PlayerBirthStateRegistry.getStats().get(Stats.STAT_STR));
        assertEquals(MAX_BIRTH_POINTS - COST_FLAT, PlayerBirthStateRegistry.getPointsLeft());
        assertTrue(bus.events.isEmpty(), "an out-of-range choice must not signal the UI");
    }

    /**
     * The two sentinels {@link Stats#getStats} still resolves to a real constant - {@code choice}
     * values C's own {@code choice >= STAT_MAX || choice < 0} test excludes. These reach {@link
     * PlayerBirth#sellStat} rather than being caught by the {@code null} guard, and its own sentinel
     * check absorbs them the same way C's does.
     */
    @Test
    @DisplayName("the STAT_MAX sentinel reaches sellStat and is rejected there, not thrown")
    void sentinelChoiceIsRejectedBySellStatNotByTheGuard() {
        assertDoesNotThrow(() -> PlayerBirth.doCmdSellStat(sellStatCommand(Stats.STAT_MAX.getValue())));

        assertEquals(11, PlayerBirthStateRegistry.getStats().get(Stats.STAT_STR));
        assertEquals(MAX_BIRTH_POINTS - COST_FLAT, PlayerBirthStateRegistry.getPointsLeft());
        assertTrue(bus.events.isEmpty());
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
