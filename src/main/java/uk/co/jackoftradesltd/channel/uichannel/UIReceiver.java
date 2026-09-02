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

import uk.co.jackoftradesltd.channel.Receiver;
import uk.co.jackoftradesltd.channel.messages.ChannelMessage;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * The UI thread's reading end of its own inbox, and the queue it spends most of its life blocked
 * on.
 * <p>
 * Typed to {@link ChannelMessage}, not {@code CoreMessage}, because this inbox has two writers:
 * the core, sending game events, and the EDT, forwarding the window messages the UI thread must
 * act on. The EDT writes here rather than to the core's inbox precisely because this is the only
 * queue the UI thread is waiting on, so it is the only place a message can reach it promptly.
 *
 * @see uk.co.jackoftradesltd.channel.corechannel.CoreSender the core's end of this queue
 */
public class UIReceiver implements Receiver<ChannelMessage> {

    /**
     * The UI thread's inbox. Shared with the {@code CoreSender} that writes to it.
     */
    private final LinkedBlockingQueue<ChannelMessage> queue;

    /**
     * @param queue the UI thread's inbox, obtained from {@link uk.co.jackoftradesltd.channel.Channels}
     */
    public UIReceiver(LinkedBlockingQueue<ChannelMessage> queue) {
        this.queue = queue;
    }

    /**
     * {@inheritDoc}
     * <p>
     * The returned message is worth switching over exhaustively: {@link ChannelMessage} is
     * sealed, so the compiler will point at this {@code switch} when a new message type is added.
     */
    @Override
    public ChannelMessage receive() throws InterruptedException {
        return queue.take();
    }
}
