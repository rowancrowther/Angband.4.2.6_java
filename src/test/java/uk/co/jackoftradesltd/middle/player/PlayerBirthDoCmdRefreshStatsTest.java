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
import uk.co.jackoftradesltd.channel.enums.GameEventType;
import uk.co.jackoftradesltd.channel.messages.data.EventDataBirthPoints;
import uk.co.jackoftradesltd.channel.messages.data.GameEventData;
import uk.co.jackoftradesltd.middle.enums.Stats;
import uk.co.jackoftradesltd.middle.game.enums.CommandCode;
import uk.co.jackoftradesltd.middle.game.enums.CommandContext;
import uk.co.jackoftradesltd.middle.game.event.EventHandlerInterface;
import uk.co.jackoftradesltd.middle.game.event.EventsHandler;
import uk.co.jackoftradesltd.middle.game.gameengine.Command;
import uk.co.jackoftradesltd.middle.game.gameengine.GameEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link PlayerBirth#doCmdRefreshStats}, the port of C's {@code do_cmd_refresh_stats}
 * ({@code player-birth.c:1173-1178}).
 *
 * <p>The C:
 *
 * <pre>{@code
 * void do_cmd_refresh_stats(struct command *cmd)
 * {
 *         /* Refreshing is a no-op when using rolled stats. *\/
 *         if (rolled_stats) return;
 *         event_signal_birthpoints(points_spent, points_inc, points_left);
 * }
 * }</pre>
 *
 * <p><b>What is actually under test.</b> There is no computation here to verify beyond the
 * two-branch shape itself: with {@code rolled_stats} true, the method must return without
 * touching the event bus at all; with it false, the method must re-signal {@code
 * EVENT_BIRTHPOINTS} carrying exactly {@link PlayerBirthStateRegistry}'s current {@code
 * pointsSpent}/{@code pointsInc}/{@code pointsLeft} - the same three values, not recomputed
 * copies. The {@code cmd} argument is never read in C, so one case here passes a command with a
 * {@code choice} arg set anyway, to pin down that it changes nothing.
 *
 * <p>Class PlayerBirthDoCmdRefreshStatsTest coded on 260907, commented in full on 260907.
 *
 * @author Rowan Crowther
 */
class PlayerBirthDoCmdRefreshStatsTest {

    private CapturingBus bus;
    private EventsHandler realBus;

    private Map<Stats, Integer> savedRegistryPointsSpent;
    private Map<Stats, Integer> savedRegistryPointsInc;
    private int savedRegistryPointsLeft;
    private boolean savedRolledStats;

    /**
     * Builds the {@code CMD_REFRESH_STATS} command, optionally carrying a {@code choice} arg -
     * which C's {@code do_cmd_refresh_stats} never reads, so its presence should not matter.
     *
     * @param choice a {@code choice} arg to set, or {@code null} to leave the command bare
     * @return the command
     */
    private static Command refreshStatsCommand(Integer choice) {
        Command cmd = new Command(CommandContext.CTX_BIRTH, CommandCode.CMD_REFRESH_STATS, 0, 0,
                new ArrayList<>());
        if (choice != null) cmd.setArgChoice("choice", choice);
        return cmd;
    }

    @BeforeEach
    void seedFixture() {
        bus = new CapturingBus();
        realBus = GameEngine.getEventsBusHandler();
        GameEngine.setEventsBusHandler(bus);

        savedRegistryPointsSpent = PlayerBirthStateRegistry.getPointsSpent();
        savedRegistryPointsInc = PlayerBirthStateRegistry.getPointsInc();
        savedRegistryPointsLeft = PlayerBirthStateRegistry.getPointsLeft();
        savedRolledStats = PlayerBirthStateRegistry.isRolledStats();
    }

    @AfterEach
    void restoreFixture() {
        GameEngine.setEventsBusHandler(realBus);

        PlayerBirthStateRegistry.setPointsSpent(savedRegistryPointsSpent);
        PlayerBirthStateRegistry.setPointsInc(savedRegistryPointsInc);
        PlayerBirthStateRegistry.setPointsLeft(savedRegistryPointsLeft);
        PlayerBirthStateRegistry.setRolledStats(savedRolledStats);
    }

    /**
     * The ordinary path: with {@code rolled_stats} false, the method must re-signal
     * {@code EVENT_BIRTHPOINTS} carrying exactly the registry's current three values - the same
     * map instances, not copies, matching C passing {@code points_spent}/{@code points_inc}/
     * {@code points_left} straight through without recalculating anything.
     */
    @Test
    @DisplayName("with point-buy stats active, re-signals EVENT_BIRTHPOINTS with the current totals")
    void pointBuyActiveResignalsCurrentTotals() {
        Map<Stats, Integer> pointsSpent = new HashMap<>();
        Map<Stats, Integer> pointsInc = new HashMap<>();
        pointsSpent.put(Stats.STAT_STR, 8);
        pointsInc.put(Stats.STAT_STR, 3);
        PlayerBirthStateRegistry.setPointsSpent(pointsSpent);
        PlayerBirthStateRegistry.setPointsInc(pointsInc);
        PlayerBirthStateRegistry.setPointsLeft(4);
        PlayerBirthStateRegistry.setRolledStats(false);

        PlayerBirth.doCmdRefreshStats(refreshStatsCommand(null));

        assertEquals(1, bus.events.size(), "exactly one event should have been signalled");
        assertEquals(GameEventType.EVENT_BIRTHPOINTS, bus.events.get(0));
        EventDataBirthPoints payload = bus.payloads.get(0);
        assertSame(pointsSpent, payload.getPoints(), "C passes points_spent through, not a copy");
        assertSame(pointsInc, payload.getIncPoints(), "C passes points_inc through, not a copy");
        assertEquals(4, payload.getRemaining());
    }

    /**
     * C's {@code if (rolled_stats) return;} is an unconditional early return - nothing runs
     * after it, so the event bus must see nothing at all.
     */
    @Test
    @DisplayName("with rolled stats active, is a no-op and never touches the event bus")
    void rolledStatsActiveIsNoOp() {
        PlayerBirthStateRegistry.setPointsLeft(4);
        PlayerBirthStateRegistry.setRolledStats(true);

        PlayerBirth.doCmdRefreshStats(refreshStatsCommand(null));

        assertTrue(bus.events.isEmpty(), "rolled_stats=true must return before signalling anything");
    }

    /**
     * C's {@code do_cmd_refresh_stats} never reads {@code cmd} at all - not even to check whether
     * a {@code choice} arg is present. Passing one anyway must not change the outcome.
     */
    @Test
    @DisplayName("an unused choice arg on the command changes nothing")
    void unusedChoiceArgIsIgnored() {
        Map<Stats, Integer> pointsSpent = new HashMap<>();
        Map<Stats, Integer> pointsInc = new HashMap<>();
        PlayerBirthStateRegistry.setPointsSpent(pointsSpent);
        PlayerBirthStateRegistry.setPointsInc(pointsInc);
        PlayerBirthStateRegistry.setPointsLeft(0);
        PlayerBirthStateRegistry.setRolledStats(false);

        PlayerBirth.doCmdRefreshStats(refreshStatsCommand(1));

        assertEquals(List.of(GameEventType.EVENT_BIRTHPOINTS), bus.events);
        assertEquals(0, bus.payloads.get(0).getRemaining());
    }

    /**
     * Captures the events and birthpoints payloads signalled during the test, in order.
     *
     * @author Rowan Crowther
     */
    private static class CapturingBus implements EventsHandler {
        private final List<GameEventType> events = new ArrayList<>();
        private final List<EventDataBirthPoints> payloads = new ArrayList<>();

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
            payloads.add((EventDataBirthPoints) eventData);
        }
    }
}
