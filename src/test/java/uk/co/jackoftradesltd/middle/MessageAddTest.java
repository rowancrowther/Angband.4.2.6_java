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

package uk.co.jackoftradesltd.middle;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.channel.enums.GameEventType;
import uk.co.jackoftradesltd.channel.messages.data.EventDataMessage;
import uk.co.jackoftradesltd.channel.messages.data.GameEventData;
import uk.co.jackoftradesltd.middle.enums.MessageType;
import uk.co.jackoftradesltd.middle.game.event.EventHandlerInterface;
import uk.co.jackoftradesltd.middle.game.event.EventsHandler;
import uk.co.jackoftradesltd.middle.game.gameengine.GameEngine;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

/**
 * Unit tests for {@link Message#messageAdd(String, MessageType)}, the port of C's
 * {@code message_add} ({@code src/message.c}) — the log-only half of {@code msgt} that
 * {@link Message#messageType} calls before it raises {@code EVENT_MESSAGE}.
 *
 * <p>{@code messageAdd} never signals a bus event of its own, so most of these tests observe it
 * indirectly: they seed the log with {@code messageAdd}, then make one {@link Message#messageType}
 * call against the same text and type and read the repeat count off the announced text. Since
 * coalescing only ever compares against the newest entry, that one probing call is enough to prove
 * what {@code messageAdd} left at the head without disturbing anything else in the shared log.
 *
 * <p>The 2048-entry cap has no observable effect through the public API — C's {@code message_add}
 * only ever exposes the head, and this port has not carried {@code message_get}/{@code
 * message_count} across — so the boundary test reaches the private {@code messageLog} field by
 * reflection, matching the pattern already used elsewhere in this suite (for example {@code
 * PlayerCalcBonusesTest}).
 *
 * @author Rowan Crowther
 */
class MessageAddTest {

    private static final AtomicInteger UNIQUE = new AtomicInteger();
    private EventsHandler realBus;
    private CapturingBus bus;

    private static String unique(String stem) {
        return stem + " #" + UNIQUE.incrementAndGet();
    }

    @BeforeEach
    void setUp() {
        realBus = GameEngine.getEventsBusHandler();
        bus = new CapturingBus();
        GameEngine.setEventsBusHandler(bus);
    }

    @AfterEach
    void tearDown() {
        GameEngine.setEventsBusHandler(realBus);
    }

    /**
     * A fresh call is a silent log write: no event goes out, and the entry it leaves behind
     * becomes the head that the next matching {@link Message#messageType} call coalesces with.
     */
    @Test
    void anOrdinaryCallLogsSilentlyAndBecomesTheNewHead() {
        String text = unique("You feel the cold touch of undeath");

        Message.messageAdd(text, MessageType.MSG_HIT);
        assertEquals(0, bus.types.size(), "message_add in C never raises an event of its own");

        Message.messageType(MessageType.MSG_HIT, text);
        assertEquals(text + " (x2)", bus.lastMessage().message(),
                "the messageAdd entry was the head, so the matching messageType call bumped its count");
    }

    /**
     * Repeats coalesce within {@code messageAdd} itself, exactly as C's {@code message_add} bumps
     * {@code messages->head->count} in place when text and type both match the newest entry.
     */
    @Test
    void repeatedCallsCoalesceIntoOneEntry() {
        String text = unique("You miss the jelly");

        Message.messageAdd(text, MessageType.MSG_MISS);
        Message.messageAdd(text, MessageType.MSG_MISS);
        Message.messageAdd(text, MessageType.MSG_MISS);

        Message.messageType(MessageType.MSG_MISS, text);
        assertEquals(text + " (x4)", bus.lastMessage().message(),
                "three coalesced messageAdd calls plus the probing messageType call");
    }

    /**
     * Coalescing compares text <em>and</em> type, same as {@link Message#messageType}: the same
     * words under a different type start a fresh entry rather than joining the existing one.
     */
    @Test
    void aDifferentTypeStartsAFreshEntry() {
        String text = unique("Something stirs");

        Message.messageAdd(text, MessageType.MSG_HIT);
        Message.messageAdd(text, MessageType.MSG_MISS);

        Message.messageType(MessageType.MSG_MISS, text);
        assertEquals(text + " (x2)", bus.lastMessage().message(),
                "the MISS entry was fresh (count 1), not sharing the HIT entry's count");
    }

    /**
     * C's {@code message_add} takes the already-formatted string and never calls a printf-family
     * function on it — formatting happens earlier, in {@code msg}/{@code msgt}. This port mirrors
     * that split: {@code messageAdd} has no {@code Object...} parameter and stores the text as
     * given. A stray {@code %} that would blow up {@link String#format} in {@link Message#message}
     * must pass through untouched here.
     */
    @Test
    void aStrayPercentSignIsStoredLiterally() {
        String text = unique("50% resistant grue");

        assertDoesNotThrow(() -> Message.messageAdd(text, MessageType.MSG_GENERIC));
    }

    /**
     * The log is capped at {@link Message}'s {@code queueSize} (2048), C's {@code messages->max}:
     * once a genuinely new entry would push the log over the cap, the oldest is dropped first so
     * the log never grows past it. Reaches the private log via reflection since nothing in the
     * public surface exposes its size.
     */
    @Test
    void theLogNeverGrowsPastTheCap() throws Exception {
        Field logField = Message.class.getDeclaredField("messageLog");
        logField.setAccessible(true);
        Deque<?> log = (Deque<?>) logField.get(null);

        Field capField = Message.class.getDeclaredField("queueSize");
        capField.setAccessible(true);
        int cap = capField.getInt(null);

        // Push well past the cap with distinct messages so every call is a genuine insert, never
        // a coalesce - a coalesce would not grow the log and would tell us nothing about eviction.
        for (int i = 0; i < cap + 50; i++) {
            Message.messageAdd(unique("cap probe"), MessageType.MSG_GENERIC);
        }

        assertEquals(cap, log.size(), "a full log must drop its oldest entry before growing further, per C's message_add");
    }

    /**
     * Records every dispatch so assertions can be made about the sequence.
     *
     * @author Rowan Crowther
     */
    private static final class CapturingBus implements EventsHandler {
        private final List<GameEventType> types = new ArrayList<>();
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
        public void gameEventDispatch(GameEventType eventType, GameEventData data) {
            types.add(eventType);
            payloads.add(data);
        }

        private EventDataMessage lastMessage() {
            assertInstanceOf(EventDataMessage.class, payloads.get(payloads.size() - 1));
            return (EventDataMessage) payloads.get(payloads.size() - 1);
        }
    }
}
