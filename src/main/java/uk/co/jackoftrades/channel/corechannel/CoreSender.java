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

package uk.co.jackoftrades.channel.corechannel;

import uk.co.jackoftrades.channel.Sender;
import uk.co.jackoftrades.channel.messages.ChannelMessage;
import uk.co.jackoftrades.channel.messages.CoreMessage;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * The core's writing end of the UI thread's inbox: the one way the game can say anything to the
 * front end.
 * <p>
 * Note the two types. The queue holds {@link ChannelMessage}, because that inbox carries both
 * senders — the core's events and, in time, the EDT's window messages — but {@link
 * #send(CoreMessage)} accepts only {@link CoreMessage}. Wide field, narrow method: that gap is
 * where the core's inability to forge a UI message lives, and it is checked by the compiler
 * rather than by anybody remembering.
 *
 * @see uk.co.jackoftrades.channel.uichannel.UIReceiver the other end of this queue
 */
public class CoreSender implements Sender<CoreMessage> {

    /**
     * The UI thread's inbox. Shared with the {@code UIReceiver} that reads it.
     */
    private final LinkedBlockingQueue<ChannelMessage> queue;

    /**
     * @param queue the UI thread's inbox, obtained from {@link uk.co.jackoftrades.channel.Channels}
     */
    public CoreSender(LinkedBlockingQueue<ChannelMessage> queue) {
        this.queue = queue;
    }

    /**
     * {@inheritDoc}
     * <p>
     * {@code offer} rather than {@code put}: on an unbounded queue it can never fail, so its
     * {@code boolean} return carries no information and discarding it is honest. It also cannot
     * block, which keeps the core running at the speed of the game rather than the speed of the
     * display.
     */
    @Override
    public void send(CoreMessage message) {
        queue.offer(message);
    }
}
