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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * Tests {@link PlayerBirth#doCmdPrevStats}, the port of C's {@code do_cmd_prev_stats}
 * ({@code player-birth.c:1218-1231}).
 *
 * <p>The C:
 *
 * <pre>{@code
 * void do_cmd_prev_stats(struct command *cmd)
 * {
 *         if (prev.age) {
 *                 load_roller_data(&prev, &prev);
 *                 get_bonuses();
 *         }
 *
 *         event_signal(EVENT_GOLD);
 *         event_signal(EVENT_AC);
 *         event_signal(EVENT_HP);
 *         event_signal(EVENT_STATS);
 * }
 * }</pre>
 *
 * <p><b>What is actually under test.</b> {@link PlayerBirth#LoadRollerData} and
 * {@link PlayerBirth#getBonuses} each have their own suite already, including the aliased
 * {@code load(&prev, &prev)} case this method's one call reproduces
 * ({@code PlayerBirthLoadRollerDataTest.SavedAndPrevPlayerAreTheSameObject}). What is left here is
 * the wiring: that a zero-age {@code prev} skips both the swap and the bonus recalculation entirely,
 * that a non-zero age runs both, and that the four bonus-refresh events fire in C's order regardless
 * of which branch ran. {@link PlayerBirth#getBonuses}'s effect is observed indirectly, through the
 * "fully healed and fully rested" side effect it always performs
 * ({@code player->chp = player->mhp}, {@code player->csp = player->msp}) - a sentinel current HP/SP
 * below the maximum is left untouched when the guard fails, and pulled up to the maximum when it
 * runs.
 *
 * <p>Class PlayerBirthDoCmdPrevStatsTest coded on 260907, commented in full on 260907.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class PlayerBirthDoCmdPrevStatsTest {

    /**
     * A placeholder starting gold; no test here reads it back.
     */
    private static final int START_GOLD = 600;

    /**
     * A sentinel current-HP/SP value below the maximum {@link CalcBonusesFixture#plainCharacter}
     * produces, so a call to {@link PlayerBirth#getBonuses} - which always sets both to their
     * maxima - is distinguishable from one that never happened.
     */
    private static final int SENTINEL_CURRENT = 1;

    /**
     * The character under test, installed as {@link GameState}'s current player.
     */
    private Player player;

    private Player realPlayer;
    private CapturingBus bus;
    private EventsHandler realBus;
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
     * A {@link Birther} with one distinct value per field, so a test can tell whether a read came
     * from it after the swap.
     */
    private static Birther distinctBirther(int age) {
        Birther birther = new Birther();
        birther.setRace(SeededPlayerRegistry.plainRace(SeededPlayerRegistry.humanoidBody()));
        birther.setPlayerClass(CalcBonusesFixture.plainClass());
        birther.setAge(age);
        birther.setWeight(140);
        birther.setHeight(65);
        birther.setAu(900L);
        for (Stats stat : Stats.values()) {
            if (stat == Stats.STAT_NONE || stat == Stats.STAT_MAX) continue;
            birther.setStat(stat, 16);
        }
        birther.setHistoryBirth("A previously rolled background.");
        birther.setName("Previous Character");
        return birther;
    }

    /**
     * Builds a {@code CMD_PREV_STATS} command, optionally carrying a {@code choice} arg - which C's
     * {@code do_cmd_prev_stats} never reads, so its presence should not matter.
     *
     * @param choice a {@code choice} arg to set, or {@code null} to leave the command bare
     * @return the command
     */
    private static Command prevStatsCommand(Integer choice) {
        Command cmd = new Command(CommandContext.CTX_BIRTH, CommandCode.CMD_PREV_STATS, 0, 0,
                new ArrayList<>());
        if (choice != null) cmd.setArgChoice("choice", choice);
        return cmd;
    }

    @BeforeEach
    void seedFixture() throws Exception {
        savedConstants = constantsField().get(null);
        seedConstants();

        player = new Player();
        CalcBonusesFixture.plainCharacter(player);
        player.setCurrentHP(SENTINEL_CURRENT);
        player.setCurSp(SENTINEL_CURRENT);

        realPlayer = GameState.getPlayer();
        GameState.setPlayer(player);

        bus = new CapturingBus();
        realBus = GameEngine.getEventsBusHandler();
        GameEngine.setEventsBusHandler(bus);

        savedPrev = PlayerBirthStateRegistry.getPrev();
    }

    @AfterEach
    void restoreFixture() throws Exception {
        GameState.setPlayer(realPlayer);
        GameEngine.setEventsBusHandler(realBus);
        constantsField().set(null, savedConstants);

        PlayerBirthStateRegistry.setPrev(savedPrev);
    }

    /**
     * C's {@code if (prev.age)} is false for a never-set {@code prev} (a zeroed struct), so the swap
     * and the bonus recalculation must both be skipped - the player is left exactly as it was.
     */
    @Test
    @DisplayName("with no previous roll stored (age zero), skips the swap and the bonus recalculation")
    void zeroAgeSkipsSwapAndBonuses() {
        PlayerBirthStateRegistry.setPrev(new Birther());

        PlayerBirth.doCmdPrevStats(prevStatsCommand(null));

        assertEquals(SENTINEL_CURRENT, player.getCurrentHP(),
                "getBonuses must not run when prev.age is zero");
        assertEquals(SENTINEL_CURRENT, player.getCurSp(),
                "getBonuses must not run when prev.age is zero");
        assertEquals(0, PlayerBirthStateRegistry.getPrev().getAge(),
                "the untouched prev must still read as zero afterwards");
    }

    /**
     * C's {@code if (prev.age)} is true once a previous roll is stored, so
     * {@code load_roller_data(&prev, &prev)} runs - swapping the stored snapshot onto the live
     * player and the player's outgoing state back into the registry's {@code prev} - followed by
     * {@code get_bonuses()}.
     */
    @Test
    @DisplayName("with a previous roll stored, swaps it onto the player and recalculates bonuses")
    void nonZeroAgeSwapsAndRecalculatesBonuses() {
        Birther previousRoll = distinctBirther(42);
        PlayerBirthStateRegistry.setPrev(previousRoll);
        int priorAge = 33;
        player.setAge(priorAge);

        PlayerBirth.doCmdPrevStats(prevStatsCommand(null));

        // The player now holds what the stored previous roll held going in.
        assertEquals(42, player.getAge());
        assertEquals(140, player.getWeight());
        assertEquals("Previous Character", player.getFullName());

        // The registry's prev now holds what the player held before the swap.
        Birther resultingPrev = PlayerBirthStateRegistry.getPrev();
        assertEquals(priorAge, resultingPrev.getAge());

        // get_bonuses() ran: fully healed and fully rested.
        assertEquals(player.getMaxHP(), player.getCurrentHP());
        assertEquals(player.getMaxSP(), player.getCurSp());
        assertNotEquals(SENTINEL_CURRENT, player.getCurrentHP());
    }

    /**
     * The four bonus-refresh signals fire unconditionally, in C's exact order, whether or not the
     * guard above ran.
     */
    @Test
    @DisplayName("signals GOLD, AC, HP, STATS in order, regardless of the guard")
    void signalsEventsInCOrderRegardlessOfGuard() {
        PlayerBirthStateRegistry.setPrev(distinctBirther(42));

        PlayerBirth.doCmdPrevStats(prevStatsCommand(null));

        assertEquals(List.of(GameEventType.EVENT_GOLD, GameEventType.EVENT_AC,
                GameEventType.EVENT_HP, GameEventType.EVENT_STATS), bus.events);
    }

    /**
     * Same four signals, same order, when the guard is skipped instead.
     */
    @Test
    @DisplayName("still signals GOLD, AC, HP, STATS in order when the guard is skipped")
    void signalsEventsInCOrderWhenGuardSkipped() {
        PlayerBirthStateRegistry.setPrev(new Birther());

        PlayerBirth.doCmdPrevStats(prevStatsCommand(null));

        assertEquals(List.of(GameEventType.EVENT_GOLD, GameEventType.EVENT_AC,
                GameEventType.EVENT_HP, GameEventType.EVENT_STATS), bus.events);
    }

    /**
     * C's {@code do_cmd_prev_stats} never reads {@code cmd} at all - not even to check whether a
     * {@code choice} arg is present. Passing one anyway must not change the outcome.
     */
    @Test
    @DisplayName("an unused choice arg on the command changes nothing")
    void unusedChoiceArgIsIgnored() {
        PlayerBirthStateRegistry.setPrev(distinctBirther(42));

        PlayerBirth.doCmdPrevStats(prevStatsCommand(1));

        assertEquals(42, player.getAge());
        assertEquals(List.of(GameEventType.EVENT_GOLD, GameEventType.EVENT_AC,
                GameEventType.EVENT_HP, GameEventType.EVENT_STATS), bus.events);
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
