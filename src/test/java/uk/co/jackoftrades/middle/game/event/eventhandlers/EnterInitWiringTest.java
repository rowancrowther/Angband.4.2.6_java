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
import uk.co.jackoftrades.channel.Channels;
import uk.co.jackoftrades.channel.enums.GameEventType;
import uk.co.jackoftrades.channel.messages.ChannelMessage;
import uk.co.jackoftrades.channel.messages.CoreMessage;
import uk.co.jackoftrades.middle.game.event.EventsBusHandler;
import uk.co.jackoftrades.middle.game.event.statusdisplay.ChannelStatusDisplay;
import uk.co.jackoftrades.middle.game.event.statusdisplay.StatusDisplayHolder;
import uk.co.jackoftrades.middle.game.gameengine.GameEngine;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The whole core-side path of stage 2, asserted end to end on the real transport: a bus signal
 * from where {@code GameConstants.init()} raises it, through {@link InitHandlers}, through
 * {@link StatusDisplayHolder}, through {@link ChannelStatusDisplay}, onto a real
 * {@link Channels} queue - and read back off the UI half's inbox exactly as {@code UILoop} would.
 *
 * <p>Everything but the painting, in other words. That last hop is what {@code UILoop} does and is
 * not covered here; see the note in this test's companion about why it cannot be, today.
 *
 * <p>Worth having as a separate test from {@code InitHandlersTest} because it fails for different
 * reasons: that one breaks if the handler registration changes, this one breaks if the wire format
 * does. It also pins the ordering constraint the migration document calls load-bearing - the
 * splash-screen message must reach the channel before any note, because the UI has nowhere to put
 * a note until it has a splash screen.
 *
 * @author Rowan Crowther
 */
class EnterInitWiringTest {

    private EventsBusHandler bus;
    private Channels channels;

    @BeforeEach
    void setUp() {
        bus = new EventsBusHandler();
        GameEngine.setEventsBusHandler(bus);
        channels = Channels.create();
        StatusDisplayHolder.setInstance(new ChannelStatusDisplay(channels.coreChannel().coreSender()));
        InitHandlers.initHandlers();
    }

    @AfterEach
    void tearDown() {
        StatusDisplayHolder.resetInstance();
    }

    /**
     * The real start-up sequence: {@code EVENT_ENTER_INIT} carrying its string, then the progress
     * notes. Each must arrive on the UI inbox as the matching record, in the order signalled.
     */
    @Test
    void initSignalsArriveOnTheUiInboxAsMessages() {
        bus.eventSignalString(GameEventType.EVENT_ENTER_INIT, "Entering Init");
        bus.eventSignalString(GameEventType.EVENT_INITSTATUS, "Initialising game constants...");
        bus.eventSignalString(GameEventType.EVENT_INITSTATUS, "Initialising world...");

        assertIterableEquals(
                List.of(new CoreMessage.SimpleCoreMessage(GameEventType.EVENT_ENTER_INIT),
                        new CoreMessage.TextCoreMessage(GameEventType.EVENT_INITSTATUS,
                                "Initialising game constants..."),
                        new CoreMessage.TextCoreMessage(GameEventType.EVENT_INITSTATUS,
                                "Initialising world...")),
                drain(3));
    }

    /**
     * The core-internal traffic stays core-internal. {@code EVENT_REFRESH} and friends are
     * signalled on the same bus and must not reach the channel - nothing subscribes them to it,
     * and this is the test that would fail if a future forwarding handler were registered against
     * every event type rather than a named list.
     */
    @Test
    void unsubscribedEventsDoNotReachTheChannel() {
        bus.eventSignalString(GameEventType.EVENT_ENTER_INIT, "Entering Init");
        drain(1);

        bus.eventSignal(GameEventType.EVENT_REFRESH);
        bus.eventSignal(GameEventType.EVENT_MESSAGE_FLUSH);
        bus.eventSignal(GameEventType.EVENT_CHECK_INTERRUPT);

        assertTrue(drainAvailable().isEmpty(), "core-internal events must not cross the channel");
    }

    /**
     * Take exactly {@code count} messages off the UI inbox, failing rather than hanging if the
     * core never sent them.
     */
    private List<ChannelMessage> drain(int count) {
        return assertTimeoutPreemptively(java.time.Duration.ofSeconds(2), () -> {
            List<ChannelMessage> received = new ArrayList<>();
            for (int index = 0; index < count; index++) {
                received.add(channels.uiChannel().uiReceiver().receive());
            }
            return received;
        });
    }

    /**
     * Whatever is on the inbox right now, without blocking - used to assert that nothing more
     * arrived. Gives the sender a moment first, since a false pass here would be silent.
     */
    private List<ChannelMessage> drainAvailable() {
        List<ChannelMessage> received = new ArrayList<>();
        Thread reader = new Thread(() -> {
            try {
                received.add(channels.uiChannel().uiReceiver().receive());
            } catch (InterruptedException expected) {
                // nothing arrived, which is the assertion
            }
        });
        reader.start();
        try {
            reader.join(250);
            reader.interrupt();
            reader.join(250);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return received;
    }
}
