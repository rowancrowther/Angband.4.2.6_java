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

package uk.co.jackoftrades.middle;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.jackoftrades.channel.enums.GameEventType;
import uk.co.jackoftrades.channel.messages.data.EventDataMessage;
import uk.co.jackoftrades.channel.messages.data.GameEventData;
import uk.co.jackoftrades.middle.enums.MessageType;
import uk.co.jackoftrades.middle.game.event.EventHandlerInterface;
import uk.co.jackoftrades.middle.game.event.EventsHandler;
import uk.co.jackoftrades.middle.game.gameengine.GameEngine;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * Unit tests for {@link Message}, the port of C's {@code msg}/{@code msgt} family
 * ({@code src/message.c}) — the engine's way of putting a line of text in front of the player
 * without knowing how it will be shown.
 *
 * <p>The behaviour worth pinning is the <b>repeat coalescing</b>. C's {@code message_add} compares
 * an incoming message against {@code messages->head} alone, and bumps a count instead of taking a
 * new slot when the text and type both match. Only the newest entry is ever compared, so
 * A, B, A gives three entries rather than folding the two As together — a burst collapses, a
 * recurrence does not. That "newest only" detail is the easy thing to get wrong when reading the C,
 * and the difference is invisible until a log fills up in play.
 *
 * <p>There is also a deliberate <b>divergence from C</b> to hold in place: this port appends a
 * {@code " (xN)"} suffix to the text it signals, where C signals the plain line every time and
 * leaves the counting to a front end that walks the log. The stored text stays plain either way,
 * so the count is never parsed back out of it — and that is the property that stops the divergence
 * turning into a bug.
 *
 * <p><b>On isolation.</b> {@code Message} keeps its log in a static field with no reset, so tests
 * cannot start from an empty log. Rather than reach in and clear it, each test below uses text
 * unique to itself: coalescing only ever compares against the newest entry, so a message no other
 * test sends can never be affected by what ran before it. That makes these tests order-independent
 * without needing access to the internals.
 *
 * @author Rowan Crowther
 */
class MessageTest {

    /**
     * Gives each test its own text, so nothing it sends can coalesce with an entry left in the
     * static log by an earlier test.
     */
    private static final AtomicInteger UNIQUE = new AtomicInteger();
    private EventsHandler realBus;
    private CapturingBus bus;

    /**
     * @param stem a readable stem for the message
     * @return the stem made unique to this test run
     */
    private static String unique(String stem) {
        return stem + " #" + UNIQUE.incrementAndGet();
    }

    /**
     * Swaps the engine's bus for a capture. {@code Message} reaches it through
     * {@link GameEngine#getEventsBusHandler()}, which is static, so the original is put back
     * afterwards to leave the rest of the suite as it was found.
     */
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
     * The plain {@code message} call is C's {@code msg}: tagged generic, and raised as an
     * {@code EVENT_MESSAGE} rather than shown directly.
     */
    @Test
    void aPlainMessageIsSignalledAsGeneric() {
        String text = unique("You feel a sense of loss");

        Message.message(text);

        assertEquals(GameEventType.EVENT_MESSAGE, bus.types.get(0));
        assertEquals(MessageType.MSG_GENERIC, bus.lastMessage().type());
        assertEquals(text, bus.lastMessage().message());
    }

    /**
     * The type travels with the message when one is given, so the display can colour it.
     */
    @Test
    void aTypedMessageKeepsItsType() {
        String text = unique("You hit the orc");

        Message.messageType(MessageType.MSG_HIT, text);

        assertEquals(MessageType.MSG_HIT, bus.lastMessage().type());
        assertEquals(text, bus.lastMessage().message());
    }

    /**
     * Format arguments are substituted before the message goes anywhere — C's {@code msg} is a
     * printf-family call, and this is the port of that.
     */
    @Test
    void formatArgumentsAreSubstituted() {
        String stem = unique("The %s hits you for %d");

        Message.message(stem, "orc", 7);

        assertEquals(String.format(stem, "orc", 7), bus.lastMessage().message());
    }

    /**
     * The reason the Javadoc tells callers to pass caller-controlled text as a {@code "%s"}
     * argument rather than as the pattern: a stray {@code %} in a monster or object name would
     * otherwise be read as a format directive. Passed correctly, the percent survives untouched.
     */
    @Test
    void textPassedAsAnArgumentSurvivesAStrayPercentSign() {
        String awkward = unique("50% resistant grue");

        Message.message("%s", awkward);

        assertEquals(awkward, bus.lastMessage().message());
    }

    /**
     * A run of identical messages collapses: still one signal per call, but the text gains the
     * repeat count. This is the behaviour a player sees as "You miss the orc. (x3)".
     */
    @Test
    void aBurstOfIdenticalMessagesGainsARepeatCount() {
        String text = unique("You miss the orc");

        Message.messageType(MessageType.MSG_MISS, text);
        assertEquals(text, bus.lastMessage().message(), "the first is sent plain");

        Message.messageType(MessageType.MSG_MISS, text);
        assertEquals(text + " (x2)", bus.lastMessage().message());

        Message.messageType(MessageType.MSG_MISS, text);
        assertEquals(text + " (x3)", bus.lastMessage().message());

        assertEquals(3, bus.types.size(), "every call still signals - coalescing affects the log, not the traffic");
    }

    /**
     * Coalescing compares text <em>and</em> type. The same words under a different type are a
     * different message and start their own count.
     */
    @Test
    void theSameTextUnderADifferentTypeDoesNotCoalesce() {
        String text = unique("Something happens");

        Message.messageType(MessageType.MSG_HIT, text);
        Message.messageType(MessageType.MSG_MISS, text);

        assertEquals(text, bus.lastMessage().message(), "a different type starts a fresh entry, so no count");
        assertEquals(MessageType.MSG_MISS, bus.lastMessage().type());
    }

    /**
     * The "newest entry only" rule, which is the detail most easily lost when reading C's
     * {@code message_add}. A, B, A must leave the second A uncounted — it is a recurrence, not a
     * burst, and C keeps them apart.
     */
    @Test
    void aMessageThatMerelyRecursDoesNotCoalesceWithAnEarlierOne() {
        String first = unique("You feel less confident");
        String interrupting = unique("The door opens");

        Message.message(first);
        Message.message(interrupting);
        Message.message(first);

        assertEquals(first, bus.lastMessage().message(),
                "only the newest entry is compared, so A,B,A leaves the second A on a count of one");
    }

    /**
     * Interrupting a run resets it: after A, B, the next A starts counting from one again rather
     * than resuming the earlier run's count.
     */
    @Test
    void anInterruptedRunStartsCountingAgain() {
        String repeated = unique("You are hit");
        String interrupting = unique("You feel a draught");

        Message.message(repeated);
        Message.message(repeated);
        assertEquals(repeated + " (x2)", bus.lastMessage().message());

        Message.message(interrupting);

        Message.message(repeated);
        assertEquals(repeated, bus.lastMessage().message(), "the run was broken, so the count restarts");

        Message.message(repeated);
        assertEquals(repeated + " (x2)", bus.lastMessage().message());
    }

    /**
     * The divergence from C, stated as a test so it cannot be "fixed" by accident: the suffix is
     * added to the outgoing text only. Two sends of the same line therefore produce two
     * <em>different</em> payloads, which is exactly what C would not do — and is why the stored
     * text has to stay plain, or the count would start compounding into the log.
     */
    @Test
    void theRepeatCountDecoratesTheOutgoingTextOnly() {
        String text = unique("Your pack overflows");

        Message.message(text);
        EventDataMessage first = bus.lastMessage();

        Message.message(text);
        EventDataMessage second = bus.lastMessage();

        assertNotEquals(first, second);
        assertEquals(text, first.message());
        assertEquals(text + " (x2)", second.message());

        Message.message(text);
        assertEquals(text + " (x3)", bus.lastMessage().message(),
                "the count must come from the log's counter, not from re-reading the last text - "
                        + "if the suffix were being stored, this would read '(x2) (x2)' or similar");
    }

    /**
     * Every message goes out as {@code EVENT_MESSAGE}, whatever its type. The type distinguishes
     * categories <em>within</em> that event; it is not a second event channel.
     */
    @Test
    void everyMessageIsSignalledOnTheSameEventType() {
        Message.message(unique("first"));
        Message.messageType(MessageType.MSG_HIT, unique("second"));

        for (GameEventType type : bus.types) {
            assertEquals(GameEventType.EVENT_MESSAGE, type);
        }
    }

    /**
     * Records every dispatch so assertions can be made about the sequence, which is what the
     * coalescing behaviour is really about.
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