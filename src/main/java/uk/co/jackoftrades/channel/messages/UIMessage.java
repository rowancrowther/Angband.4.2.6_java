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

import uk.co.jackoftrades.channel.enums.UILifecycleEvent;

/**
 * Everything the UI side can say, to the core and to itself.
 * <p>
 * Same rule as {@link CoreMessage} — one record per payload shape, not one per occasion — but the
 * population is organised on a different axis, because the UI side has two senders rather than
 * one:
 * <ul>
 *   <li><b>Raw input</b> from the EDT: keypresses and mouse clicks, the port of C's
 *       {@code ui_event}. Arrives with Chapter 5.</li>
 *   <li><b>Intent</b> from the UI thread: what the player has asked the game to do, the port of
 *       C's {@code struct command}. Also Chapter 5.</li>
 * </ul>
 * Today neither exists, and the only traffic is the lifecycle record below.
 * <p>
 * Note what naming by <em>sender</em> rather than by direction costs. A {@code UIMessage} is not
 * bound for one particular queue: the lifecycle messages go to the core, while the EDT's future
 * window messages go to the UI thread's own inbox, since that is the only queue it waits on. So
 * the compiler alone cannot stop one being posted to the wrong channel. The fix, when it is worth
 * having, is two sealed sub-interfaces here — one per UI-side sender — and it is deferred to
 * Chapter 5 for the good reason that the two sub-interfaces would currently have one member
 * between them. Populations A and B above <em>are</em> that split; it is arriving anyway.
 *
 * @see CoreMessage the traffic going the other way
 */
public sealed interface UIMessage extends ChannelMessage permits UIMessage.LifecycleUIMessage {

    /**
     * Protocol rather than gameplay: the front end telling the core to begin, or to save and shut
     * down.
     * <p>
     * The meaning lives in the enum field rather than in the record's identity — one record
     * carrying {@link UILifecycleEvent#START} or {@link UILifecycleEvent#SAVE_AND_STOP}, not a
     * record apiece. Its opposite number is {@link CoreMessage.LifecycleCoreMessage}, and the
     * pair of them carry the whole shutdown handshake: the UI asks, the core does the saving, the
     * core reports {@code STOPPED}, and only then may the front end exit.
     *
     * @param event what the front end is asking for
     */
    record LifecycleUIMessage(UILifecycleEvent event) implements UIMessage {
    }
}
