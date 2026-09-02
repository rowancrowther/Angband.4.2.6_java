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

/**
 * A send-only view of one channel, narrowed to the messages its holder is entitled to send.
 * <p>
 * The point of this interface is what it leaves out. A {@link java.util.concurrent.BlockingQueue}
 * offers both ends of the channel to whoever holds it; a {@code Sender} offers only the writing
 * end, so a half of the game that is given one cannot read traffic addressed to the other half.
 * Narrowing {@code T} does the other half of the job: a {@code Sender<CoreMessage>} over a queue
 * of {@link ChannelMessage} can put core messages on it and nothing else, so the core cannot
 * forge a message purporting to come from the UI.
 *
 * @param <T> the messages this holder is entitled to send
 * @see Receiver
 */
@FunctionalInterface
public interface Sender<T extends ChannelMessage> {

    /**
     * Sends a message and returns immediately.
     * <p>
     * The channels are unbounded, so a send always succeeds and never waits for the receiver to
     * catch up. That asymmetry with {@link Receiver#receive()} is deliberate: a thread with
     * something to say should never block, whereas a thread with nothing to do should.
     *
     * @param message the message to send
     */
    void send(T message);
}
