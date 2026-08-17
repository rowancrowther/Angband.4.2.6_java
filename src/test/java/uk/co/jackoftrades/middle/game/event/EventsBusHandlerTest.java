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

package uk.co.jackoftrades.middle.game.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.jackoftrades.channel.enums.GameEventType;
import uk.co.jackoftrades.channel.messages.data.EventDataString;
import uk.co.jackoftrades.channel.messages.data.GameEventData;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link EventsBusHandler}, the port of C's game event bus
 * ({@code src/game-event.c}) — a registry of handlers keyed by event type, and a dispatch that
 * calls every handler registered for the type being signalled.
 *
 * <p>The bus stays core-internal through the channel migration, but its behaviour is about to
 * matter more rather than less: from stage 5 the core's subscriber for each display-bound event is
 * what puts a message on the channel, so a handler that is silently dropped or called twice
 * becomes a missing or duplicated frame on screen.
 *
 * <p>Two properties are worth stating because they are easy to lose and hard to notice.
 * <b>Registration is per event type</b>, so a handler added for one event must not hear another —
 * the failure mode is a handler that appears to work while also firing on unrelated traffic.
 * And <b>a null payload is legal</b>: {@code eventSignal} dispatches one, so every handler has to
 * cope, and the bus must not filter it out on the way.
 *
 * @author Rowan Crowther
 */
class EventsBusHandlerTest {

    private EventsBusHandler bus;

    /**
     * A fresh bus per test. The constructor calls {@code init()}, which clears every type, so
     * there is no leakage between tests even though handlers are registered freely below.
     */
    @BeforeEach
    void setUp() {
        bus = new EventsBusHandler();
    }

    /**
     * The base case: register, signal, get called once with the type and payload as sent.
     */
    @Test
    void aRegisteredHandlerReceivesItsEvent() {
        RecordingHandler handler = new RecordingHandler();
        EventDataString payload = new EventDataString("Initializing arrays...");

        bus.eventAddHandler(GameEventType.EVENT_INITSTATUS, handler);
        bus.gameEventDispatch(GameEventType.EVENT_INITSTATUS, payload);

        assertEquals(1, handler.calls());
        assertEquals(GameEventType.EVENT_INITSTATUS, handler.types.get(0));
        assertEquals(payload, handler.payloads.get(0));
    }

    /**
     * Registration is keyed by type. A handler listening for one event must stay silent when
     * another is signalled — otherwise every handler hears all traffic and the keying is
     * decorative.
     */
    @Test
    void aHandlerHearsOnlyTheEventItRegisteredFor() {
        RecordingHandler handler = new RecordingHandler();

        bus.eventAddHandler(GameEventType.EVENT_INITSTATUS, handler);
        bus.gameEventDispatch(GameEventType.EVENT_MESSAGE, new EventDataString("not for you"));

        assertEquals(0, handler.calls());
    }

    /**
     * Several handlers on one event all fire, and in the order they were added. C's bus appends,
     * and order matters wherever one handler's drawing must land before another's.
     */
    @Test
    void everyHandlerOnAnEventFiresInRegistrationOrder() {
        List<String> order = new ArrayList<>();
        EventHandlerInterface first = (type, data) -> order.add("first");
        EventHandlerInterface second = (type, data) -> order.add("second");

        bus.eventAddHandler(GameEventType.EVENT_MESSAGE, first);
        bus.eventAddHandler(GameEventType.EVENT_MESSAGE, second);
        bus.gameEventDispatch(GameEventType.EVENT_MESSAGE, null);

        assertIterableEquals(List.of("first", "second"), order);
    }

    /**
     * Signalling an event nobody listens to is a no-op, not a failure. Most of the 65 event types
     * have no handler at any given moment.
     */
    @Test
    void signallingAnEventWithNoHandlersDoesNothing() {
        bus.gameEventDispatch(GameEventType.EVENT_GOLD, new EventDataString("unheard"));
    }

    /**
     * A null payload reaches the handler as null. {@code eventSignal} sends one for every
     * payload-free event, so the bus swallowing it would break the majority of the traffic.
     */
    @Test
    void aNullPayloadIsDeliveredRatherThanFiltered() {
        RecordingHandler handler = new RecordingHandler();

        bus.eventAddHandler(GameEventType.EVENT_ENTER_INIT, handler);
        bus.gameEventDispatch(GameEventType.EVENT_ENTER_INIT, null);

        assertEquals(1, handler.calls());
        assertNull(handler.payloads.get(0));
    }

    /**
     * A removed handler stops hearing the event, and the others carry on.
     */
    @Test
    void aRemovedHandlerStopsReceivingItsEvent() {
        RecordingHandler removed = new RecordingHandler();
        RecordingHandler kept = new RecordingHandler();

        bus.eventAddHandler(GameEventType.EVENT_MESSAGE, removed);
        bus.eventAddHandler(GameEventType.EVENT_MESSAGE, kept);
        bus.eventRemoveHandler(GameEventType.EVENT_MESSAGE, removed);
        bus.gameEventDispatch(GameEventType.EVENT_MESSAGE, null);

        assertEquals(0, removed.calls());
        assertEquals(1, kept.calls());
    }

    /**
     * Clearing one type leaves the others registered — the difference between
     * {@code eventRemoveHandlerType} and {@code eventRemoveAllHandlers}.
     */
    @Test
    void clearingOneEventTypeLeavesTheOthersAlone() {
        RecordingHandler cleared = new RecordingHandler();
        RecordingHandler survivor = new RecordingHandler();

        bus.eventAddHandler(GameEventType.EVENT_MESSAGE, cleared);
        bus.eventAddHandler(GameEventType.EVENT_INITSTATUS, survivor);
        bus.eventRemoveHandlerType(GameEventType.EVENT_MESSAGE);

        bus.gameEventDispatch(GameEventType.EVENT_MESSAGE, null);
        bus.gameEventDispatch(GameEventType.EVENT_INITSTATUS, null);

        assertEquals(0, cleared.calls());
        assertEquals(1, survivor.calls());
    }

    /**
     * {@code eventRemoveAllHandlers} clears every type, which is what {@code init()} relies on to
     * give a bus with no stale subscriptions. Checked across every one of the 65 types rather than
     * a sample, since the loop that clears them is the thing under test.
     */
    @Test
    void removingAllHandlersClearsEveryEventType() {
        List<RecordingHandler> handlers = new ArrayList<>();

        for (GameEventType eventType : GameEventType.values()) {
            RecordingHandler handler = new RecordingHandler();
            handlers.add(handler);
            bus.eventAddHandler(eventType, handler);
        }

        bus.eventRemoveAllHandlers();

        for (GameEventType eventType : GameEventType.values()) {
            bus.gameEventDispatch(eventType, null);
        }

        for (RecordingHandler handler : handlers) {
            assertEquals(0, handler.calls(), "every handler should have been cleared");
        }
    }

    /**
     * A newly constructed bus has no handlers on any type. The constructor calls {@code init()},
     * and this is what makes a fresh bus safe to use in a test without a teardown.
     */
    @Test
    void aFreshBusHasNoSubscriptions() {
        for (GameEventType eventType : GameEventType.values()) {
            new EventsBusHandler().gameEventDispatch(eventType, null);
        }
    }

    /**
     * {@code eventAddHandlerSet} is C's {@code event_add_handler_set}: one handler, several event
     * types, one call. Each listed type must reach it, and exactly once.
     */
    @Test
    void addingAHandlerToASetRegistersItForEveryTypeInTheSet() {
        RecordingHandler handler = new RecordingHandler();
        List<GameEventType> set = List.of(GameEventType.EVENT_ENTER_INIT,
                GameEventType.EVENT_INITSTATUS, GameEventType.EVENT_LEAVE_INIT);

        bus.eventAddHandlerSet(set, handler);
        for (GameEventType eventType : set) {
            bus.gameEventDispatch(eventType, null);
        }

        assertIterableEquals(set, handler.types);
    }

    /**
     * And the matching removal takes it off all of them.
     */
    @Test
    void removingAHandlerFromASetUnregistersItEverywhereInTheSet() {
        RecordingHandler handler = new RecordingHandler();
        List<GameEventType> set = List.of(GameEventType.EVENT_ENTER_INIT, GameEventType.EVENT_INITSTATUS);

        bus.eventAddHandlerSet(set, handler);
        bus.eventRemoveHandlerSet(set, handler);
        for (GameEventType eventType : set) {
            bus.gameEventDispatch(eventType, null);
        }

        assertEquals(0, handler.calls());
    }

    /**
     * The same handler registered twice for one event is called twice. Recorded as the current
     * behaviour rather than endorsed: C's bus does not deduplicate either, but a double
     * registration during wiring would show up as doubled traffic on the channel, and this test is
     * where that behaviour is written down.
     */
    @Test
    void registeringTheSameHandlerTwiceCallsItTwice() {
        RecordingHandler handler = new RecordingHandler();

        bus.eventAddHandler(GameEventType.EVENT_MESSAGE, handler);
        bus.eventAddHandler(GameEventType.EVENT_MESSAGE, handler);
        bus.gameEventDispatch(GameEventType.EVENT_MESSAGE, null);

        assertEquals(2, handler.calls(), "the bus does not deduplicate registrations");
    }

    /**
     * A handler may register another handler while being dispatched to — the backing list is a
     * {@link java.util.concurrent.CopyOnWriteArrayList}, so the in-flight iteration is not
     * disturbed. Without that, wiring performed from inside a handler would throw a
     * {@code ConcurrentModificationException}, and the natural place to do such wiring is exactly
     * inside an {@code EVENT_ENTER_INIT} handler.
     */
    @Test
    void aHandlerMayRegisterAnotherWhileBeingDispatchedTo() {
        RecordingHandler late = new RecordingHandler();
        EventHandlerInterface wiring = (type, data) -> bus.eventAddHandler(GameEventType.EVENT_ENTER_INIT, late);

        bus.eventAddHandler(GameEventType.EVENT_ENTER_INIT, wiring);
        bus.gameEventDispatch(GameEventType.EVENT_ENTER_INIT, null);

        assertEquals(0, late.calls(), "the handler added mid-dispatch does not hear the event it was added during");

        bus.gameEventDispatch(GameEventType.EVENT_ENTER_INIT, null);

        assertTrue(late.calls() > 0, "but it does hear the next one");
    }

    /**
     * A handler records what it was called with, so assertions can be about the sequence of
     * dispatches rather than about a flag having been set.
     *
     * @author Rowan Crowther
     */
    private static final class RecordingHandler implements EventHandlerInterface {
        private final List<GameEventType> types = new ArrayList<>();
        private final List<GameEventData> payloads = new ArrayList<>();

        @Override
        public void dispatch(GameEventType eventType, GameEventData data) {
            types.add(eventType);
            payloads.add(data);
        }

        private int calls() {
            return types.size();
        }
    }
}
