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

package uk.co.jackoftradesltd.channel;

import uk.co.jackoftradesltd.channel.messages.ChannelMessage;
import uk.co.jackoftradesltd.channel.messages.UIMessage;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * The EDT's writing end of the UI thread's inbox: the one way a Swing listener can say anything to
 * the rest of the program.
 * <p>
 * <b>Field and method disagree on type, deliberately.</b> The queue holds {@link ChannelMessage},
 * because the UI thread's inbox also carries everything the core sends; the {@code send} method
 * accepts only {@link UIMessage}, because a listener has no business forging a message that
 * purports to come from the core. Narrowing the writing end while the queue stays wide is the same
 * trick {@link uk.co.jackoftradesltd.channel.corechannel.CoreSender} uses, and it is what
 * {@link Sender}'s type parameter exists for.
 * <p>
 * Written to from the EDT and read from the UI thread, so the queue is doing the thread-safety
 * work as well as the delivery: {@link java.util.concurrent.LinkedBlockingQueue} publishes the
 * message safely, which is why a window event needs no synchronisation of its own on the way
 * across.
 *
 * @see uk.co.jackoftradesltd.channel.uichannel.UIReceiver the end that reads what this writes
 */
public class EDTSender implements Sender<UIMessage> {

    /**
     * The UI thread's inbox. Shared with the {@code UIReceiver} that reads it, and with the
     * {@code CoreSender} that is its other writer.
     */
    private final LinkedBlockingQueue<ChannelMessage> queue;

    /**
     * @param queue the UI thread's inbox, obtained from {@link Channels}
     */
    public EDTSender(LinkedBlockingQueue<ChannelMessage> queue) {
        this.queue = queue;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Never blocks, which matters more here than on the other senders: this runs on the EDT, and a
     * send that waited would freeze the window. The queue is unbounded, so {@code offer} cannot
     * fail and the discarded {@code boolean} return loses nothing.
     */
    @Override
    public void send(UIMessage message) {
        queue.offer(message);
    }
}
