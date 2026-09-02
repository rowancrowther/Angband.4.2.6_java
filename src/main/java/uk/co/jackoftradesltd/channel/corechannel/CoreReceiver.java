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

package uk.co.jackoftradesltd.channel.corechannel;

import uk.co.jackoftradesltd.channel.Receiver;
import uk.co.jackoftradesltd.channel.messages.UIMessage;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * The core's reading end of its own inbox: everything the front end has to say to the game.
 * <p>
 * Typed to {@link UIMessage} rather than {@code ChannelMessage} because only the UI side writes
 * here, so nothing else can arrive. Keeping it that narrow is what lets the core's {@code switch}
 * over the result be exhaustive without arms for messages that could never turn up.
 *
 * @see uk.co.jackoftradesltd.channel.uichannel.UISender the other end of this queue
 */
public class CoreReceiver implements Receiver<UIMessage> {

    /**
     * The core's inbox. Shared with the {@code UISender} that writes to it.
     */
    private final LinkedBlockingQueue<UIMessage> queue;

    /**
     * @param queue the core's inbox, obtained from {@link uk.co.jackoftradesltd.channel.Channels}
     */
    public CoreReceiver(LinkedBlockingQueue<UIMessage> queue) {
        this.queue = queue;
    }

    /**
     * {@inheritDoc}
     * <p>
     * {@code take} blocks until a message arrives, which is how the core waits on the player
     * without a polling loop.
     */
    @Override
    public UIMessage receive() throws InterruptedException {
        return queue.take();
    }
}
