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

package uk.co.jackoftradesltd.middle.game.event.eventhandlers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.channel.Sender;
import uk.co.jackoftradesltd.channel.enums.GameEventType;
import uk.co.jackoftradesltd.channel.messages.CoreMessage;
import uk.co.jackoftradesltd.channel.messages.data.EventDataStat;
import uk.co.jackoftradesltd.channel.messages.data.EventDataString;
import uk.co.jackoftradesltd.channel.messages.data.EventDataStrings;
import uk.co.jackoftradesltd.middle.game.event.EventsBusHandler;
import uk.co.jackoftradesltd.middle.game.gameengine.GameEngine;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the redraw chain {@link RedrawHandlers} owns: bus signal, to handler, to the sending end of
 * the core channel. There is no C function to test against directly - C's UI handlers for these
 * events (for example {@code prt_hp}, {@code prt_sp}, {@code prt_race}, {@code prt_class} and
 * {@code prt_title}, {@code [C] ui-display.c}) read the values they need off the shared
 * {@code player} global at signal time, so {@link RedrawHandlers} exists only because the port's
 * front end has no such global to read on its side of the core-to-front-end boundary.
 *
 * <p>What is worth pinning is therefore the translation itself: that a redraw event carrying the
 * expected payload is forwarded as a {@link CoreMessage.GameEventCoreMessage} with that payload
 * unchanged, and that a signal carrying the wrong shape is dropped rather than forwarded.
 *
 * <p><b>A fake {@link Sender}, not a real channel</b> - the same reason {@code InitHandlersTest}
 * uses one: this asks what the handler does and can see the answer synchronously, with no queue and
 * no second thread.
 *
 * @author Rowan Crowther
 */
class RedrawHandlersTest {

    private EventsBusHandler bus;
    private RecordingSender sender;

    @BeforeEach
    void setUp() {
        bus = new EventsBusHandler();
        GameEngine.setEventsBusHandler(bus);
        sender = new RecordingSender();
        new RedrawHandlers(sender).initHandlers();
    }

    /**
     * An {@code EVENT_HP} signal carrying an {@link EventDataStat} is forwarded whole, as the
     * message's payload, with the event type it was signalled under.
     */
    @Test
    void hpSignalReachesTheSenderAsAGameEventCoreMessage() {
        bus.eventSignalStat(GameEventType.EVENT_HP, 17, 30);

        assertEquals(List.of(new CoreMessage.GameEventCoreMessage(GameEventType.EVENT_HP,
                        new EventDataStat(17, 30))),
                sender.sent);
    }

    /**
     * A bare {@code eventSignal} carries no payload, so the guard on
     * {@link RedrawHandlers#initHandlers()}'s handler rejects it - the same shape of guard
     * {@code InitHandlers.enterInit} uses, and for the same reason: a caller that used
     * {@code eventSignal} instead of {@code eventSignalStat} would otherwise send a message with no
     * data to forward.
     */
    @Test
    void bareHpSignalSendsNothing() {
        bus.eventSignal(GameEventType.EVENT_HP);

        assertTrue(sender.sent.isEmpty(), "eventHP requires an EventDataStat payload");
    }

    /**
     * An {@code EVENT_MANA} signal carrying an {@link EventDataStat} is forwarded whole, as the
     * message's payload, with the event type it was signalled under - the same shape as
     * {@link #hpSignalReachesTheSenderAsAGameEventCoreMessage()}, for {@link RedrawHandlers#eventSP}
     * rather than {@link RedrawHandlers#eventHP}.
     */
    @Test
    void manaSignalReachesTheSenderAsAGameEventCoreMessage() {
        bus.eventSignalStat(GameEventType.EVENT_MANA, 4, 20);

        assertEquals(List.of(new CoreMessage.GameEventCoreMessage(GameEventType.EVENT_MANA,
                        new EventDataStat(4, 20))),
                sender.sent);
    }

    /**
     * A bare {@code eventSignal} carries no payload, so the guard on
     * {@link RedrawHandlers#eventSP} rejects it, the same way {@link #bareHpSignalSendsNothing()}
     * pins the guard on {@link RedrawHandlers#eventHP}.
     */
    @Test
    void bareManaSignalSendsNothing() {
        bus.eventSignal(GameEventType.EVENT_MANA);

        assertTrue(sender.sent.isEmpty(), "eventSP requires an EventDataStat payload");
    }

    /**
     * An {@code EVENT_PLAYERTITLE} signal carrying an {@link EventDataString} is forwarded whole,
     * as the message's payload, with the event type it was signalled under - the same shape as
     * {@link #hpSignalReachesTheSenderAsAGameEventCoreMessage()}, for
     * {@link RedrawHandlers#eventTitle} rather than {@link RedrawHandlers#eventHP}.
     */
    @Test
    void titleSignalReachesTheSenderAsAGameEventCoreMessage() {
        bus.eventSignalString(GameEventType.EVENT_PLAYERTITLE, "Rogue");

        assertEquals(List.of(new CoreMessage.GameEventCoreMessage(GameEventType.EVENT_PLAYERTITLE,
                        new EventDataString("Rogue"))),
                sender.sent);
    }

    /**
     * A bare {@code eventSignal} carries no payload, so the guard on
     * {@link RedrawHandlers#eventTitle} rejects it.
     */
    @Test
    void bareTitleSignalSendsNothing() {
        bus.eventSignal(GameEventType.EVENT_PLAYERTITLE);

        assertTrue(sender.sent.isEmpty(), "eventTitle requires an EventDataString payload");
    }

    /**
     * An {@code EVENT_PLAYER_NAME} signal carrying an {@link EventDataString} is forwarded whole,
     * as the message's payload, with the event type it was signalled under - the same shape as
     * {@link #hpSignalReachesTheSenderAsAGameEventCoreMessage()}, for
     * {@link RedrawHandlers#eventName} rather than {@link RedrawHandlers#eventHP}.
     */
    @Test
    void nameSignalReachesTheSenderAsAGameEventCoreMessage() {
        bus.eventSignalString(GameEventType.EVENT_PLAYER_NAME, "Legolas");

        assertEquals(List.of(new CoreMessage.GameEventCoreMessage(GameEventType.EVENT_PLAYER_NAME,
                        new EventDataString("Legolas"))),
                sender.sent);
    }

    /**
     * A bare {@code eventSignal} carries no payload, so the guard on
     * {@link RedrawHandlers#eventName} rejects it.
     */
    @Test
    void bareNameSignalSendsNothing() {
        bus.eventSignal(GameEventType.EVENT_PLAYER_NAME);

        assertTrue(sender.sent.isEmpty(), "eventName requires an EventDataString payload");
    }

    /**
     * An {@code EVENT_RACE_CLASS} signal carrying an {@link EventDataStrings} is forwarded whole,
     * as the message's payload, with the event type it was signalled under - the same shape as
     * {@link #hpSignalReachesTheSenderAsAGameEventCoreMessage()}, for
     * {@link RedrawHandlers#eventRaceClass} rather than {@link RedrawHandlers#eventHP}.
     *
     * <p>{@link EventDataStrings}'s generated {@code equals()} compares its {@code String[]}
     * component by identity, not content (see that record's own Javadoc), so - unlike the sibling
     * tests above - this reads the sent message apart and checks its payload with
     * {@link org.junit.jupiter.api.Assertions#assertArrayEquals} rather than comparing the whole
     * message with {@code assertEquals}.
     */
    @Test
    void raceClassSignalReachesTheSenderAsAGameEventCoreMessage() {
        bus.eventSignalStrings(GameEventType.EVENT_RACE_CLASS, "Elf", "Ranger");

        assertEquals(1, sender.sent.size());
        CoreMessage.GameEventCoreMessage message =
                assertInstanceOf(CoreMessage.GameEventCoreMessage.class, sender.sent.get(0));
        assertEquals(GameEventType.EVENT_RACE_CLASS, message.type());
        EventDataStrings strings = assertInstanceOf(EventDataStrings.class, message.data());
        assertArrayEquals(new String[]{"Elf", "Ranger"}, strings.strings());
    }

    /**
     * A bare {@code eventSignal} carries no payload, so the guard on
     * {@link RedrawHandlers#eventRaceClass} rejects it.
     */
    @Test
    void bareRaceClassSignalSendsNothing() {
        bus.eventSignal(GameEventType.EVENT_RACE_CLASS);

        assertTrue(sender.sent.isEmpty(), "eventRaceClass requires an EventDataStrings payload");
    }

    /**
     * A recording stand-in for the core's end of the channel.
     */
    private static final class RecordingSender implements Sender<CoreMessage> {
        private final List<CoreMessage> sent = new ArrayList<>();

        @Override
        public void send(CoreMessage message) {
            sent.add(message);
        }
    }
}
