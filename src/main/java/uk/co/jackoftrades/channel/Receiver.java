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

package uk.co.jackoftrades.channel;

import uk.co.jackoftrades.channel.messages.ChannelMessage;

/**
 * A receive-only view of one channel, typed to everything that can arrive on it.
 * <p>
 * The mirror of {@link Sender}, and it leaves out the writing end for the same reason: a half of
 * the game holding one of these can take what it has been sent and cannot put anything back.
 * Unlike a sender, {@code T} here is as <em>wide</em> as the channel — a receiver must be able to
 * name every message that can turn up, or the {@code switch} that handles them cannot be
 * exhaustive.
 *
 * @param <T> everything that can arrive on this channel
 * @see Sender
 */
@FunctionalInterface
public interface Receiver<T extends ChannelMessage> {

    /**
     * Waits for the next message and returns it.
     * <p>
     * Blocking is the feature: a thread with nothing to do sleeps here rather than spinning
     * on an empty queue, and wakes when the other half has something to say.
     *
     * @return the next message, once one arrives
     * @throws InterruptedException if the waiting thread is interrupted. This is how a
     *                              blocked thread is asked to shut down, so it is a normal control path rather
     *                              than a failure, and should not be swallowed.
     */
    T receive() throws InterruptedException;
}
