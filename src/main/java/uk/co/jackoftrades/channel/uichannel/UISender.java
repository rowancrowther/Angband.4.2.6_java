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

package uk.co.jackoftrades.channel.uichannel;

import uk.co.jackoftrades.channel.Sender;
import uk.co.jackoftrades.channel.messages.UIMessage;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * The UI thread's writing end of the core's inbox: the one way the front end can say anything to
 * the game.
 * <p>
 * Field and method agree on {@link UIMessage} here, unlike {@link
 * uk.co.jackoftrades.channel.corechannel.CoreSender}, because this queue has a single sender and
 * so needs no narrowing.
 *
 * @see uk.co.jackoftrades.channel.corechannel.CoreReceiver the other end of this queue
 */
public class UISender implements Sender<UIMessage> {

    /**
     * The core's inbox. Shared with the {@code CoreReceiver} that reads it.
     */
    private final LinkedBlockingQueue<UIMessage> queue;

    /**
     * @param queue the core's inbox, obtained from {@link uk.co.jackoftrades.channel.Channels}
     */
    public UISender(LinkedBlockingQueue<UIMessage> queue) {
        this.queue = queue;
    }

    /**
     * {@inheritDoc}
     * <p>
     * {@code offer} on an unbounded queue cannot fail, so the discarded {@code boolean} return
     * loses nothing.
     */
    @Override
    public void send(UIMessage message) {
        queue.offer(message);
    }
}
