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

package uk.co.jackoftrades.channel.messages;

/**
 * The root of the protocol: anything that can cross between the two halves of the game.
 * <p>
 * Exactly two branches, <strong>one per sender</strong> — {@link CoreMessage} for the game,
 * {@link UIMessage} for the front end. Naming by who is speaking rather than by which way the
 * message is travelling is what keeps the hierarchy from doubling: the EDT's window messages are
 * UI messages that happen to travel to the UI thread, and they need no separate branch to say so.
 * <p>
 * This interface deliberately declares nothing. There is no member every message could usefully
 * share — no timestamp, no common {@code apply}, no {@code type()} — and adding one would be the
 * first step back towards a single message class with a tag field, which is the shape sealing is
 * here to replace. Its whole job is to be the type a queue and a {@code switch} can be written
 * against.
 * <p>
 * Because it is sealed, that {@code switch} needs no {@code default} arm, and a new branch fails
 * the build everywhere it must be handled rather than falling silently into a catch-all. Only
 * {@link uk.co.jackoftrades.channel.uichannel.UIReceiver} receives at this width, because only the
 * UI thread's inbox carries both senders; the core's inbox is typed to {@link UIMessage} alone.
 *
 * @see uk.co.jackoftrades.channel.Channels the queues these travel on
 */
public sealed interface ChannelMessage permits CoreMessage, UIMessage {

}
