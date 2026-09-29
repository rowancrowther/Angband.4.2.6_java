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
import uk.co.jackoftradesltd.middle.MessageLogProbe.CapturingBus;
import uk.co.jackoftradesltd.middle.enums.MessageType;
import uk.co.jackoftradesltd.middle.game.event.EventsHandler;
import uk.co.jackoftradesltd.middle.game.gameengine.GameEngine;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for {@link Message#messageAdd(String, MessageType)}, the port of C's
 * {@code message_add} ({@code message.c}). Expected values come from the C source:
 *
 * <ul>
 *   <li>if the head has the same type and text, its count goes up and nothing is inserted;</li>
 *   <li>otherwise a new entry with count 1 becomes the head;</li>
 *   <li>the log holds at most {@code messages->max} (2048) entries, dropping the tail when a
 *       genuine insert takes it over;</li>
 *   <li>it takes an already-formatted string and does no formatting or truncation of its own.</li>
 * </ul>
 *
 * <p>The log is read directly through {@link MessageLogProbe} (age 0 is the newest entry), and
 * the log is emptied before each test.
 *
 * <p>Not ported, and so not tested: C's {@code count != (uint16_t)-1} wrap guard.
 *
 * @author Rowan Crowther
 */
class MessageAddTest {
    private EventsHandler realBus;
    private CapturingBus bus;

    @BeforeEach
    void setUp() throws ReflectiveOperationException {
        MessageLogProbe.clear();
        realBus = GameEngine.getEventsBusHandler();
        bus = new CapturingBus();
        GameEngine.setEventsBusHandler(bus);
    }

    @AfterEach
    void tearDown() {
        GameEngine.setEventsBusHandler(realBus);
    }

    @Test
    void theFirstMessageIntoAnEmptyLogIsAnEntryWithCountOne() throws Exception {
        Message.messageAdd("You feel cold", MessageType.MSG_HIT);

        assertEquals(1, MessageLogProbe.size());
        assertEquals("You feel cold", MessageLogProbe.text(0));
        assertEquals(MessageType.MSG_HIT, MessageLogProbe.type(0));
        assertEquals(1, MessageLogProbe.count(0));
    }

    /**
     * C's {@code message_add} raises no event; that is {@code msgt}'s job.
     */
    @Test
    void loggingIsSilent() {
        Message.messageAdd("quiet", MessageType.MSG_GENERIC);

        assertEquals(0, bus.types.size());
    }

    @Test
    void repeatsCoalesceIntoOneEntry() throws Exception {
        for (int i = 0; i < 4; i++) Message.messageAdd("You miss the jelly", MessageType.MSG_MISS);

        assertEquals(1, MessageLogProbe.size());
        assertEquals(4, MessageLogProbe.count(0));
    }

    @Test
    void aDifferentTypeStartsAFreshEntry() throws Exception {
        Message.messageAdd("Something stirs", MessageType.MSG_HIT);
        Message.messageAdd("Something stirs", MessageType.MSG_MISS);

        assertEquals(2, MessageLogProbe.size());
        assertEquals(MessageType.MSG_MISS, MessageLogProbe.type(0));
        assertEquals(1, MessageLogProbe.count(0));
        assertEquals(MessageType.MSG_HIT, MessageLogProbe.type(1));
        assertEquals(1, MessageLogProbe.count(1));
    }

    @Test
    void differentTextUnderTheSameTypeStartsAFreshEntry() throws Exception {
        Message.messageAdd("one", MessageType.MSG_HIT);
        Message.messageAdd("two", MessageType.MSG_HIT);

        assertEquals(2, MessageLogProbe.size());
        assertEquals("two", MessageLogProbe.text(0));
        assertEquals("one", MessageLogProbe.text(1));
    }

    /**
     * Only {@code messages->head} is compared, so A, B, A is three entries.
     */
    @Test
    void onlyTheNewestEntryIsCompared() throws Exception {
        Message.messageAdd("A", MessageType.MSG_GENERIC);
        Message.messageAdd("B", MessageType.MSG_GENERIC);
        Message.messageAdd("A", MessageType.MSG_GENERIC);

        assertEquals(3, MessageLogProbe.size());
        assertEquals(1, MessageLogProbe.count(0));
    }

    @Test
    void textIsStoredExactlyAsGiven() throws Exception {
        Message.messageAdd("50% resistant %s grue", MessageType.MSG_GENERIC);

        assertEquals("50% resistant %s grue", MessageLogProbe.text(0));
    }

    /**
     * No truncation here: the 1023 cut happens in {@code msg}/{@code msgt}, before this.
     */
    @Test
    void messageAddDoesNotTruncate() throws Exception {
        String longText = "x".repeat(2000);

        Message.messageAdd(longText, MessageType.MSG_GENERIC);

        assertEquals(longText, MessageLogProbe.text(0));
    }

    @Test
    void theLogFillsToTheCapWithoutLosingAnything() throws Exception {
        int cap = MessageLogProbe.cap();
        assertEquals(2048, cap, "messages_init sets max = 2048");

        for (int i = 0; i < cap; i++) Message.messageAdd("m" + i, MessageType.MSG_GENERIC);

        assertEquals(cap, MessageLogProbe.size());
        assertEquals("m" + (cap - 1), MessageLogProbe.text(0));
        assertEquals("m0", MessageLogProbe.text(cap - 1), "the oldest is still there at exactly max");
    }

    /** One over the cap drops the tail and only the tail. */
    @Test
    void theEntryAfterTheCapDropsTheOldest() throws Exception {
        int cap = MessageLogProbe.cap();
        for (int i = 0; i < cap + 1; i++) Message.messageAdd("m" + i, MessageType.MSG_GENERIC);

        assertEquals(cap, MessageLogProbe.size());
        assertEquals("m" + cap, MessageLogProbe.text(0));
        assertEquals("m1", MessageLogProbe.text(cap - 1), "m0 was dropped, m1 is the new tail");
    }

    /** A repeat is not an insert, so it must not cost a full log its oldest entry. */
    @Test
    void aRepeatAtFullCapacityEvictsNothing() throws Exception {
        int cap = MessageLogProbe.cap();
        for (int i = 0; i < cap; i++) Message.messageAdd("m" + i, MessageType.MSG_GENERIC);

        Message.messageAdd("m" + (cap - 1), MessageType.MSG_GENERIC);

        assertEquals(cap, MessageLogProbe.size());
        assertEquals(2, MessageLogProbe.count(0));
        assertEquals("m0", MessageLogProbe.text(cap - 1));
    }

    @Test
    void theLogNeverGrowsPastTheCap() throws Exception {
        int cap = MessageLogProbe.cap();
        for (int i = 0; i < cap + 50; i++) Message.messageAdd("m" + i, MessageType.MSG_GENERIC);

        assertEquals(cap, MessageLogProbe.size());
        assertEquals("m50", MessageLogProbe.text(cap - 1));
    }
}
