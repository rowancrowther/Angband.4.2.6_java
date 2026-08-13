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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.jackoftrades.channel.Sender;
import uk.co.jackoftrades.channel.enums.GameEventType;
import uk.co.jackoftrades.channel.messages.CoreMessage;
import uk.co.jackoftrades.middle.game.event.EventsBusHandler;
import uk.co.jackoftrades.middle.game.gameengine.GameEngine;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the start-up chain {@link InitHandlers} owns: bus signal, to handler, to the sending end of
 * the core channel.
 *
 * <p>The property under test is the one that is invisible at a breakpoint in the bus:
 * {@code EVENT_INITSTATUS} has no handler until {@code EVENT_ENTER_INIT} has been dispatched,
 * because {@code enterInit} is what subscribes it. Signalling the two in the wrong order dispatches
 * to an empty list and looks exactly like an event that was never signalled.
 *
 * <p><b>A fake {@link Sender}, not a real channel.</b> That is the whole reason this test and
 * {@code EnterInitWiringTest} both exist: this one asks what the handlers do and can see the answer
 * synchronously, with no queue and no second thread; that one asks whether the real transport
 * carries it. They fail for different reasons - this one when the registration changes, that one
 * when the wire format does.
 *
 * <p>Since stage 5 there is nothing global to restore: the handlers are handed their sender at
 * construction rather than reaching a process-wide holder, so each test gets its own instance and
 * the only shared state left is the bus, which {@code setUp} replaces outright.
 *
 * @author Rowan Crowther
 */
class InitHandlersTest {

    private EventsBusHandler bus;
    private RecordingSender sender;

    @BeforeEach
    void setUp() {
        bus = new EventsBusHandler();
        GameEngine.setEventsBusHandler(bus);
        sender = new RecordingSender();
        new InitHandlers(sender).initHandlers();
    }

    /**
     * The order {@code GameConstants.init()} actually uses: enter-init first, then the notes. Each
     * must reach the sender as the record matching its payload shape - nothing for the splash
     * screen, text for a note.
     */
    @Test
    void enterInitThenNotesReachTheSender() {
        bus.eventSignalString(GameEventType.EVENT_ENTER_INIT, "Entering Init");
        bus.eventSignalString(GameEventType.EVENT_INITSTATUS, "Initialising game constants...");
        bus.eventSignalString(GameEventType.EVENT_INITSTATUS, "Initialising world...");

        assertIterableEquals(
                List.of(new CoreMessage.SimpleCoreMessage(GameEventType.EVENT_ENTER_INIT),
                        new CoreMessage.TextCoreMessage(GameEventType.EVENT_INITSTATUS,
                                "Initialising game constants..."),
                        new CoreMessage.TextCoreMessage(GameEventType.EVENT_INITSTATUS,
                                "Initialising world...")),
                sender.sent);
    }

    /**
     * The type on the message is the event that was signalled, not the payload's text and not a
     * constant baked into the handler. Pins the {@code eventType} forwarding both handlers do.
     */
    @Test
    void messagesCarryTheEventThatWasSignalled() {
        bus.eventSignalString(GameEventType.EVENT_ENTER_INIT, "Entering Init");
        bus.eventSignalString(GameEventType.EVENT_INITSTATUS, "a note");

        CoreMessage.SimpleCoreMessage splash =
                assertInstanceOf(CoreMessage.SimpleCoreMessage.class, sender.sent.get(0));
        CoreMessage.TextCoreMessage note =
                assertInstanceOf(CoreMessage.TextCoreMessage.class, sender.sent.get(1));

        assertEquals(GameEventType.EVENT_ENTER_INIT, splash.gameEventType());
        assertEquals(GameEventType.EVENT_INITSTATUS, note.gameEventType());
        assertEquals("a note", note.message());
    }

    /**
     * A note signalled before enter-init has no subscriber, and is silently lost.
     */
    @Test
    void noteBeforeEnterInitIsLost() {
        bus.eventSignalString(GameEventType.EVENT_INITSTATUS, "too early");

        assertTrue(sender.sent.isEmpty(), "expected no handler for EVENT_INITSTATUS yet");
    }

    /**
     * A bare {@code eventSignal} carries no payload, so {@code enterInit}'s guard rejects it - and
     * rejects it before the subscription, so the notes that follow are lost too.
     */
    @Test
    void bareEnterInitSignalSendsNothing() {
        bus.eventSignal(GameEventType.EVENT_ENTER_INIT);

        assertTrue(sender.sent.isEmpty(), "enterInit requires an EventDataString payload");
        bus.eventSignalString(GameEventType.EVENT_INITSTATUS, "note");
        assertEquals(0, sender.sent.size(), "no subscription happened either");
    }

    /**
     * A note carrying no string payload is dropped, the same way {@code enterInit} drops a bare
     * signal - the guard is on both handlers and this is the half the other tests do not reach.
     */
    @Test
    void bareNoteSignalSendsNothing() {
        bus.eventSignalString(GameEventType.EVENT_ENTER_INIT, "Entering Init");
        sender.sent.clear();

        bus.eventSignal(GameEventType.EVENT_INITSTATUS);

        assertTrue(sender.sent.isEmpty(), "splashScreenNote requires an EventDataString payload");
    }

    /**
     * The phase handlers that only log still send nothing at all. Cheap to assert and it is the
     * test that would fail first if one of them grew a message without one being intended.
     */
    @Test
    void loggingOnlyHandlersSendNothing() {
        bus.eventSignal(GameEventType.EVENT_LEAVE_INIT);
        bus.eventSignal(GameEventType.EVENT_ENTER_GAME);
        bus.eventSignal(GameEventType.EVENT_LEAVE_GAME);
        bus.eventSignal(GameEventType.EVENT_ENTER_WORLD);
        bus.eventSignal(GameEventType.EVENT_LEAVE_WORLD);
        bus.eventSignal(GameEventType.EVENT_ENTER_BIRTH);
        bus.eventSignal(GameEventType.EVENT_LEAVE_BIRTH);

        assertTrue(sender.sent.isEmpty(), "these handlers only log so far");
    }

    /**
     * A recording stand-in for the core's end of the channel.
     *
     * <p>Implementing {@link Sender} rather than subclassing {@code CoreSender} is what the field's
     * interface type buys: no queue to own, no messages to drain, and the assertions read against a
     * plain list.
     */
    private static final class RecordingSender implements Sender<CoreMessage> {
        private final List<CoreMessage> sent = new ArrayList<>();

        @Override
        public void send(CoreMessage message) {
            sent.add(message);
        }
    }
}
