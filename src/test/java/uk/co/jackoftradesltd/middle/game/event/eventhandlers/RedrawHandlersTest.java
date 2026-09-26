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
import uk.co.jackoftradesltd.middle.game.event.EventsBusHandler;
import uk.co.jackoftradesltd.middle.game.gameengine.GameEngine;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the redraw chain {@link RedrawHandlers} owns: bus signal, to handler, to the sending end of
 * the core channel. There is no C function to test against directly - C's UI handlers for these
 * events (for example {@code prt_hp} and {@code prt_sp}, {@code [C] ui-display.c}) read the values
 * they need off the shared {@code player} global at signal time, so {@link RedrawHandlers} exists
 * only because the port's front end has no such global to read on its side of the core-to-front-end
 * boundary.
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
