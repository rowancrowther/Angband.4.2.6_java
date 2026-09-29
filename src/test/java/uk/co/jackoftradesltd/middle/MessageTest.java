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
import uk.co.jackoftradesltd.middle.MessageLogProbe.CapturingBus;
import uk.co.jackoftradesltd.middle.enums.MessageType;
import uk.co.jackoftradesltd.middle.game.event.EventsHandler;
import uk.co.jackoftradesltd.middle.game.gameengine.GameEngine;
import uk.co.jackoftradesltd.middle.game.gameengine.GameState;
import uk.co.jackoftradesltd.middle.player.Player;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Unit tests for {@link Message#message} and {@link Message#messageType}, the ports of C's
 * {@code msg} and {@code msgt} ({@code message.c}). Expected values come from the C source:
 *
 * <ul>
 *   <li>{@code msg} formats into a {@code char buf[1024]}, calls {@code message_add(buf,
 *       MSG_GENERIC)} and signals {@code EVENT_MESSAGE}. It makes no sound.</li>
 *   <li>{@code msgt} does the same under the caller's type, and calls {@code sound(type)}
 *       <em>between</em> {@code message_add} and the {@code EVENT_MESSAGE} signal.</li>
 *   <li>The buffer holds 1023 characters plus the terminator, so longer text is cut to 1023
 *       before it reaches either the log or the event.</li>
 *   <li>The signalled text is the plain buffer every time; the repeat count lives in the log
 *       only. (An earlier version of the port appended {@code " (xN)"}; it no longer does.)</li>
 * </ul>
 *
 * <p>The log is emptied before each test through {@link MessageLogProbe}, and the engine's bus and
 * current player are swapped for test doubles and restored afterwards.
 *
 * @author Rowan Crowther
 */
class MessageTest {
    private EventsHandler realBus;
    private Player realPlayer;
    private CapturingBus bus;

    @BeforeEach
    void setUp() throws ReflectiveOperationException {
        MessageLogProbe.clear();
        realBus = GameEngine.getEventsBusHandler();
        realPlayer = GameState.getPlayer();
        bus = new CapturingBus();
        GameEngine.setEventsBusHandler(bus);
        GameState.setPlayer(null);
    }

    @AfterEach
    void tearDown() {
        GameEngine.setEventsBusHandler(realBus);
        GameState.setPlayer(realPlayer);
    }

    private Player playerWithSound(boolean on) throws ReflectiveOperationException {
        Player p = MessageLogProbe.playerWithSound(on);
        GameState.setPlayer(p);
        return p;
    }

    // ---- msg ----

    @Test
    void aPlainMessageIsLoggedAndSignalledAsGeneric() throws Exception {
        Message.message("You feel a sense of loss");

        assertEquals(List.of(GameEventType.EVENT_MESSAGE), bus.types);
        assertEquals(MessageType.MSG_GENERIC, bus.last().type());
        assertEquals("You feel a sense of loss", bus.last().message());
        assertEquals(1, MessageLogProbe.size());
        assertEquals("You feel a sense of loss", MessageLogProbe.text(0));
        assertEquals(MessageType.MSG_GENERIC, MessageLogProbe.type(0));
        assertEquals(1, MessageLogProbe.count(0));
    }

    @Test
    void formatArgumentsAreSubstituted() throws Exception {
        Message.message("The %s hits you for %d", "orc", 7);

        assertEquals("The orc hits you for 7", bus.last().message());
        assertEquals("The orc hits you for 7", MessageLogProbe.text(0));
    }

    @Test
    void textPassedAsAnArgumentSurvivesAStrayPercentSign() throws Exception {
        Message.message("%s", "50% resistant grue");

        assertEquals("50% resistant grue", bus.last().message());
        assertEquals("50% resistant grue", MessageLogProbe.text(0));
    }

    /**
     * C's {@code msg} never calls {@code sound}, even with {@code use_sound} on.
     */
    @Test
    void aPlainMessageNeverMakesASound() throws Exception {
        playerWithSound(true);

        Message.message("Silence");

        assertEquals(List.of(GameEventType.EVENT_MESSAGE), bus.types);
    }

    // ---- msgt ----

    @Test
    void aTypedMessageKeepsItsTypeInTheLogAndTheEvent() throws Exception {
        Message.messageType(MessageType.MSG_HIT, "You hit the orc");

        assertEquals(List.of(GameEventType.EVENT_MESSAGE), bus.types);
        assertEquals(MessageType.MSG_HIT, bus.last().type());
        assertEquals("You hit the orc", bus.last().message());
        assertEquals(MessageType.MSG_HIT, MessageLogProbe.type(0));
    }

    @Test
    void aTypedMessageSubstitutesFormatArguments() throws Exception {
        Message.messageType(MessageType.MSG_MISS, "You miss the %s (%d)", "jelly", 3);

        assertEquals("You miss the jelly (3)", bus.last().message());
        assertEquals("You miss the jelly (3)", MessageLogProbe.text(0));
    }

    /**
     * With {@code use_sound} on, {@code msgt} sounds first, then signals the message.
     */
    @Test
    void aTypedMessageSoundsBeforeItIsSignalledWhenSoundIsOn() throws Exception {
        playerWithSound(true);

        Message.messageType(MessageType.MSG_HIT, "You hit the orc");

        assertEquals(List.of(GameEventType.EVENT_SOUND, GameEventType.EVENT_MESSAGE), bus.types);
        assertEquals(MessageType.MSG_HIT, bus.payload(0).type());
        assertNull(bus.payload(0).message(), "sound() carries no text");
        assertEquals("You hit the orc", bus.payload(1).message());
    }

    @Test
    void aTypedMessageIsSilentWhenSoundIsOff() throws Exception {
        playerWithSound(false);

        Message.messageType(MessageType.MSG_HIT, "You hit the orc");

        assertEquals(List.of(GameEventType.EVENT_MESSAGE), bus.types);
    }

    /**
     * The port is null-safe where C would dereference; the message still goes out.
     */
    @Test
    void aTypedMessageStillGoesOutWithNoPlayer() throws Exception {
        GameState.setPlayer(null);

        Message.messageType(MessageType.MSG_HIT, "You hit the orc");

        assertEquals(List.of(GameEventType.EVENT_MESSAGE), bus.types);
        assertEquals(1, MessageLogProbe.size());
    }

    // ---- repeat coalescing (message_add, seen through msg/msgt) ----

    /** Every call signals; only the log coalesces, and the event text stays plain. */
    @Test
    void aBurstCoalescesInTheLogButEveryCallSignalsPlainText() throws Exception {
        for (int i = 0; i < 3; i++) Message.messageType(MessageType.MSG_MISS, "You miss the orc");

        assertEquals(3, bus.types.size());
        for (int i = 0; i < 3; i++) assertEquals("You miss the orc", bus.payload(i).message());
        assertEquals(1, MessageLogProbe.size());
        assertEquals(3, MessageLogProbe.count(0));
        assertEquals("You miss the orc", MessageLogProbe.text(0));
    }

    @Test
    void theSameTextUnderADifferentTypeDoesNotCoalesce() throws Exception {
        Message.messageType(MessageType.MSG_HIT, "Something happens");
        Message.messageType(MessageType.MSG_MISS, "Something happens");

        assertEquals(2, MessageLogProbe.size());
        assertEquals(1, MessageLogProbe.count(0));
        assertEquals(MessageType.MSG_MISS, MessageLogProbe.type(0));
        assertEquals(1, MessageLogProbe.count(1));
    }

    /** {@code message_add} compares only {@code messages->head}: A, B, A is three entries. */
    @Test
    void aRecurrenceDoesNotCoalesceWithAnEarlierEntry() throws Exception {
        Message.message("A");
        Message.message("B");
        Message.message("A");

        assertEquals(3, MessageLogProbe.size());
        assertEquals("A", MessageLogProbe.text(0));
        assertEquals("B", MessageLogProbe.text(1));
        assertEquals("A", MessageLogProbe.text(2));
        assertEquals(1, MessageLogProbe.count(0));
    }

    @Test
    void anInterruptedRunStartsCountingAgain() throws Exception {
        Message.message("A");
        Message.message("A");
        Message.message("B");
        Message.message("A");
        Message.message("A");

        assertEquals(3, MessageLogProbe.size());
        assertEquals(2, MessageLogProbe.count(0));
        assertEquals(1, MessageLogProbe.count(1));
        assertEquals(2, MessageLogProbe.count(2));
    }

    /** {@code msg} and {@code msgt(MSG_GENERIC)} are the same message to {@code message_add}. */
    @Test
    void msgAndMsgtGenericShareAnEntry() throws Exception {
        Message.message("Same");
        Message.messageType(MessageType.MSG_GENERIC, "Same");

        assertEquals(1, MessageLogProbe.size());
        assertEquals(2, MessageLogProbe.count(0));
    }

    // ---- the format vocabulary shared with vstrnfmt (z-form.c) ----

    @Test
    void doublePercentIsALiteralPercent() throws Exception {
        Message.message("100%% sure");

        assertEquals("100% sure", bus.last().message());
    }

    /** Values are what {@code snprintf} gives for the same directive in {@code vstrnfmt}. */
    @Test
    void integerAndCharacterDirectivesMatchC() throws Exception {
        Message.message("%x|%X|%o|%c|%+d", 255, 255, 8, 'A', 5);

        assertEquals("ff|FF|10|A|+5", bus.last().message());
    }

    @Test
    void widthFlagsAndPrecisionMatchC() throws Exception {
        Message.message("[%5d][%-5d][%05d][%.3s]", 42, 42, 42, "abcdef");

        assertEquals("[   42][42   ][00042][abc]", bus.last().message());
    }

    @Test
    void floatingPointDirectivesMatchC() throws Exception {
        Message.message("%f %e", 1.5, 12345.678);

        assertEquals("1.500000 1.234568e+04", bus.last().message());
    }

    // ---- malformed patterns: vstrnfmt empties the buffer, msg/msgt carry on ----

    /**
     * An unterminated {@code %}: C's {@code vstrnfmt} returns with {@code buf[0] = '\0'}.
     */
    @Test
    void anUnterminatedPercentIsLoggedAndSignalledAsAnEmptyMessage() throws Exception {
        Message.message("100%");

        assertEquals(List.of(GameEventType.EVENT_MESSAGE), bus.types);
        assertEquals("", bus.last().message());
        assertEquals(MessageType.MSG_GENERIC, bus.last().type());
        assertEquals(1, MessageLogProbe.size());
        assertEquals("", MessageLogProbe.text(0));
    }

    /**
     * {@code %q} reaches the {@code default:} branch of {@code vstrnfmt}.
     */
    @Test
    void anIllegalConversionIsAnEmptyMessage() throws Exception {
        Message.message("You hit the %q", "orc");

        assertEquals("", bus.last().message());
        assertEquals("", MessageLogProbe.text(0));
    }

    /**
     * A missing argument is an error in Java only; it takes the same empty-message path.
     */
    @Test
    void aMissingArgumentIsAnEmptyMessage() throws Exception {
        Message.message("You hit the %s");

        assertEquals("", bus.last().message());
        assertEquals(1, MessageLogProbe.size());
    }

    /**
     * {@code msgt} still sounds, between the log write and the signal, on the empty message.
     */
    @Test
    void aMalformedTypedMessageStillSoundsThenSignalsEmpty() throws Exception {
        playerWithSound(true);

        Message.messageType(MessageType.MSG_HIT, "100%");

        assertEquals(List.of(GameEventType.EVENT_SOUND, GameEventType.EVENT_MESSAGE), bus.types);
        assertEquals("", bus.payload(1).message());
        assertEquals(MessageType.MSG_HIT, bus.payload(1).type());
        assertEquals("", MessageLogProbe.text(0));
        assertEquals(MessageType.MSG_HIT, MessageLogProbe.type(0));
    }

    /**
     * Empty is just another text to {@code message_add}: consecutive blanks coalesce.
     */
    @Test
    void consecutiveEmptyMessagesCoalesce() throws Exception {
        Message.message("100%");
        Message.message("50%");

        assertEquals(1, MessageLogProbe.size());
        assertEquals(2, MessageLogProbe.count(0));
    }

    /**
     * Documented divergence: {@code %i}, {@code %u} and the {@code l} modifier are legal in
     * {@code vstrnfmt} (which would print "5"), but {@link String#format} rejects them, so the
     * port produces the empty message instead.
     */
    @Test
    void directivesOnlyCAcceptsBecomeEmptyMessages() throws Exception {
        Message.message("%i", 5);
        assertEquals("", bus.last().message());

        Message.message("%u", 5);
        assertEquals("", bus.last().message());

        Message.message("%ld", 5L);
        assertEquals("", bus.last().message());
    }

    // ---- the 1024-byte buffer ----

    @Test
    void textOf1023CharactersIsUntouched() throws Exception {
        String text = "x".repeat(1023);

        Message.message(text);

        assertEquals(text, bus.last().message());
        assertEquals(text, MessageLogProbe.text(0));
    }

    /** One past the buffer: {@code vstrnfmt} into {@code buf[1024]} keeps 1023 characters. */
    @Test
    void textOf1024CharactersIsCutTo1023() throws Exception {
        Message.message("x".repeat(1024));

        assertEquals("x".repeat(1023), bus.last().message());
        assertEquals("x".repeat(1023), MessageLogProbe.text(0));
    }

    @Test
    void aTypedMessageIsCutTheSameWayInLogAndEvent() throws Exception {
        Message.messageType(MessageType.MSG_HIT, "y".repeat(3000));

        assertEquals(1023, bus.last().message().length());
        assertEquals(1023, MessageLogProbe.text(0).length());
    }

    /**
     * Two over-long lines that differ only past the cut are the same message, as in C.
     */
    @Test
    void linesThatDifferOnlyPastTheCutCoalesce() throws Exception {
        Message.message("z".repeat(1023) + "1");
        Message.message("z".repeat(1023) + "2");

        assertEquals(1, MessageLogProbe.size());
        assertEquals(2, MessageLogProbe.count(0));
    }
}
