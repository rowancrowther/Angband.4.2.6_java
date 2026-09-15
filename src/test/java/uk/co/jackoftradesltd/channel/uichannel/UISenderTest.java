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

package uk.co.jackoftradesltd.channel.uichannel;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import uk.co.jackoftradesltd.channel.enums.UILifecycleEvent;
import uk.co.jackoftradesltd.channel.messages.UIMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link UISender} in isolation, constructed directly against a raw queue rather
 * than through {@link uk.co.jackoftradesltd.channel.Channels}.
 *
 * <p>{@code ChannelsTest} already pins the wiring — that a {@code UISender} obtained from
 * {@code Channels} really does reach the core's receiver and nobody else's. What that leaves
 * untested is the class on its own: that {@link UISender#send(UIMessage)} really is the
 * one-line {@code queue.offer(message)} its Javadoc claims, with no hidden blocking, copying, or
 * reordering, independent of how the queue was obtained.
 *
 * <p>There is no C original to check this against; {@code UISender} is Java-only channel
 * plumbing from the two-channel CSP architecture migration (see
 * {@code docs/Architecture_migration.md}), not a port of a C function.
 *
 * <p>Class UISenderTest coded on 260915, commented in full on 260915.
 *
 * @author Rowan Crowther
 */
@Timeout(value = 10, unit = TimeUnit.SECONDS)
class UISenderTest {

    /**
     * The ordinary path: a sent message is the one waiting on the queue.
     *
     * <p>Function sendPutsTheMessageOnTheQueue coded on 260915, commented in full on 260915.
     */
    @Test
    void sendPutsTheMessageOnTheQueue() {
        LinkedBlockingQueue<UIMessage> queue = new LinkedBlockingQueue<>();
        UISender sender = new UISender(queue);
        UIMessage message = new UIMessage.LifecycleUIMessage(UILifecycleEvent.START);

        sender.send(message);

        assertEquals(1, queue.size());
        assertEquals(message, queue.peek());
    }

    /**
     * Repeated sends preserve FIFO order, since the shutdown handshake and every other exchange
     * on this channel depends on messages arriving in the order they were sent.
     *
     * <p>Function repeatedSendsPreserveOrder coded on 260915, commented in full on 260915.
     */
    @Test
    void repeatedSendsPreserveOrder() throws InterruptedException {
        LinkedBlockingQueue<UIMessage> queue = new LinkedBlockingQueue<>();
        UISender sender = new UISender(queue);

        UIMessage first = new UIMessage.LifecycleUIMessage(UILifecycleEvent.START);
        UIMessage second = new UIMessage.WindowCloseRequested();
        UIMessage third = new UIMessage.LifecycleUIMessage(UILifecycleEvent.SAVE_AND_STOP);

        sender.send(first);
        sender.send(second);
        sender.send(third);

        List<UIMessage> received = new ArrayList<>();
        received.add(queue.take());
        received.add(queue.take());
        received.add(queue.take());

        assertEquals(List.of(first, second, third), received);
    }

    /**
     * Sending never blocks, however many messages pile up unread — the boundary case an unbounded
     * queue exists to remove. A bounded queue would eventually make this call block or throw; this
     * pins that {@code UISender} does neither.
     *
     * <p>Function sendingNeverBlocksEvenUnread coded on 260915, commented in full on 260915.
     */
    @Test
    void sendingNeverBlocksEvenUnread() {
        LinkedBlockingQueue<UIMessage> queue = new LinkedBlockingQueue<>();
        UISender sender = new UISender(queue);

        for (int i = 0; i < 10_000; i++) {
            sender.send(new UIMessage.WindowCloseRequested());
        }

        assertEquals(10_000, queue.size());
    }

    /**
     * The queue handed to the constructor is the one {@code send} writes to — not a copy, and not
     * one created internally — since a sender and its receiver must share the identical queue
     * object for delivery to happen at all.
     *
     * <p>Function sendWritesToTheConstructorSuppliedQueue coded on 260915, commented in full on
     * 260915.
     */
    @Test
    void sendWritesToTheConstructorSuppliedQueue() {
        LinkedBlockingQueue<UIMessage> queue = new LinkedBlockingQueue<>();
        UISender sender = new UISender(queue);

        sender.send(new UIMessage.WindowCloseRequested());

        assertTrue(queue.contains(new UIMessage.WindowCloseRequested()));
    }
}
