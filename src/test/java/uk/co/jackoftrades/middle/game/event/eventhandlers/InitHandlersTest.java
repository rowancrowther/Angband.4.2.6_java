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

package uk.co.jackoftrades.middle.game.event.eventhandlers;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.jackoftrades.channel.enums.GameEventType;
import uk.co.jackoftrades.middle.game.event.EventsBusHandler;
import uk.co.jackoftrades.middle.game.event.statusdisplay.StatusDisplay;
import uk.co.jackoftrades.middle.game.event.statusdisplay.StatusDisplayHolder;
import uk.co.jackoftrades.middle.game.gameengine.GameEngine;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the start-up chain {@link InitHandlers} owns: bus signal, to handler, to the
 * {@link StatusDisplay} boundary. Replaces the {@code InitHandlersTest} named in
 * {@code InitHandlers}' Javadoc, which the stage 0/1 package moves lost.
 *
 * <p>The property under test is the one that is invisible at a breakpoint in the bus:
 * {@code EVENT_INITSTATUS} has no handler until {@code EVENT_ENTER_INIT} has been dispatched,
 * because {@link InitHandlers#enterInit} is what subscribes it. Signalling the two in the wrong
 * order dispatches to an empty list and looks exactly like an event that was never signalled.
 *
 * @author Rowan Crowther
 */
class InitHandlersTest {

    private EventsBusHandler bus;
    private RecordingDisplay display;

    @BeforeEach
    void setUp() {
        bus = new EventsBusHandler();
        GameEngine.setEventsBusHandler(bus);
        display = new RecordingDisplay();
        StatusDisplayHolder.setInstance(display);
        InitHandlers.initHandlers();
    }

    @AfterEach
    void tearDown() {
        StatusDisplayHolder.resetInstance();
    }

    /**
     * The order {@code GameConstants.init()} actually uses: enter-init first, then the notes.
     * Both must reach the display.
     */
    @Test
    void enterInitThenNotesReachTheDisplay() {
        bus.eventSignalString(GameEventType.EVENT_ENTER_INIT, "Entering Init");
        bus.eventSignalString(GameEventType.EVENT_INITSTATUS, "Initialising game constants...");
        bus.eventSignalString(GameEventType.EVENT_INITSTATUS, "Initialising world...");

        assertIterableEquals(
                List.of("showSplashScreen",
                        "note:Initialising game constants...",
                        "note:Initialising world..."),
                display.calls);
    }

    /**
     * A note signalled before enter-init has no subscriber, and is silently lost.
     */
    @Test
    void noteBeforeEnterInitIsLost() {
        bus.eventSignalString(GameEventType.EVENT_INITSTATUS, "too early");

        assertTrue(display.calls.isEmpty(), "expected no handler for EVENT_INITSTATUS yet");
    }

    /**
     * A bare {@code eventSignal} carries no payload, so {@code enterInit}'s guard rejects it.
     */
    @Test
    void bareEnterInitSignalShowsNothing() {
        bus.eventSignal(GameEventType.EVENT_ENTER_INIT);

        assertTrue(display.calls.isEmpty(), "enterInit requires an EventDataString payload");
        bus.eventSignalString(GameEventType.EVENT_INITSTATUS, "note");
        assertEquals(0, display.calls.size(), "no subscription happened either");
    }

    /**
     * A recording stand-in for the front end, so the boundary calls can be asserted.
     */
    private static final class RecordingDisplay implements StatusDisplay {
        private final List<String> calls = new ArrayList<>();

        @Override
        public void showSplashScreen() {
            calls.add("showSplashScreen");
        }

        @Override
        public void splashScreenNote(String message) {
            calls.add("note:" + message);
        }
    }
}
