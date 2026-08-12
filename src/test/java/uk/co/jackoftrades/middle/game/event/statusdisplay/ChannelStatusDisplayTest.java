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

package uk.co.jackoftrades.middle.game.event.statusdisplay;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.jackoftrades.channel.Sender;
import uk.co.jackoftrades.channel.enums.GameEventType;
import uk.co.jackoftrades.channel.messages.CoreMessage;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

/**
 * Tests {@link ChannelStatusDisplay}, the core's side of stage 2's pivot: the {@link StatusDisplay}
 * implementation that turns each boundary call into a {@link CoreMessage} on the core channel
 * instead of painting anything.
 *
 * <p>A fake {@link Sender} stands in for the channel, so these assert exactly what stage 2 asks
 * for - "each {@code StatusDisplay} call becomes the right message" - without a second thread or a
 * queue in the way. The mapping is two lines of production code and worth pinning anyway: both
 * methods send a message carrying a {@link GameEventType}, and the failure mode of getting the
 * type wrong is a message that arrives, deserialises, matches no arm of the UI loop's
 * {@code switch}, and vanishes silently.
 *
 * @author Rowan Crowther
 */
class ChannelStatusDisplayTest {

    private RecordingSender sender;
    private ChannelStatusDisplay display;

    @BeforeEach
    void setUp() {
        sender = new RecordingSender();
        display = new ChannelStatusDisplay(sender);
    }

    /**
     * The title screen: payload-free, so it rides {@code SimpleCoreMessage}.
     */
    @Test
    void showSplashScreenSendsSimpleEnterInit() {
        display.showSplashScreen();

        assertIterableEquals(
                List.of(new CoreMessage.SimpleCoreMessage(GameEventType.EVENT_ENTER_INIT)),
                sender.sent);
    }

    /**
     * A progress note carries text, so it rides {@code TextCoreMessage} with the note intact.
     */
    @Test
    void splashScreenNoteSendsTextInitStatus() {
        display.splashScreenNote("Initialising world...");

        assertIterableEquals(
                List.of(new CoreMessage.TextCoreMessage(GameEventType.EVENT_INITSTATUS,
                        "Initialising world...")),
                sender.sent);
    }

    /**
     * Nothing is coalesced or held back: every call sends exactly one message, in call order.
     * Notes repeat with different text throughout the load, and dropping duplicates or reordering
     * them would show up as a splash screen stuck on the wrong line.
     */
    @Test
    void everyCallSendsOneMessageInOrder() {
        display.showSplashScreen();
        display.splashScreenNote("one");
        display.splashScreenNote("two");
        display.splashScreenNote("two");

        assertEquals(4, sender.sent.size());
        assertIterableEquals(
                List.of(new CoreMessage.SimpleCoreMessage(GameEventType.EVENT_ENTER_INIT),
                        new CoreMessage.TextCoreMessage(GameEventType.EVENT_INITSTATUS, "one"),
                        new CoreMessage.TextCoreMessage(GameEventType.EVENT_INITSTATUS, "two"),
                        new CoreMessage.TextCoreMessage(GameEventType.EVENT_INITSTATUS, "two")),
                sender.sent);
    }

    /**
     * Records what was sent, in order, in place of a real queue.
     */
    private static final class RecordingSender implements Sender<CoreMessage> {
        private final List<CoreMessage> sent = new ArrayList<>();

        @Override
        public void send(CoreMessage message) {
            sent.add(message);
        }
    }
}
