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

import uk.co.jackoftradesltd.channel.corechannel.CoreReceiver;
import uk.co.jackoftradesltd.channel.corechannel.CoreSender;

/**
 * Everything the core is allowed to do with the channels, and nothing else.
 * <p>
 * The two ends belong to <em>different</em> queues: the core reads its own inbox and writes to
 * the UI thread's. Pairing them here means the core can be handed one object rather than two, and
 * — more to the point — that it is never handed the {@link UIChannel}, so the UI half's ends of
 * the same two queues are not merely off limits but unnameable.
 *
 * @param coreReceiver the core's inbox, carrying the front end's lifecycle and, from Chapter 5,
 *                     the player's commands
 * @param coreSender   the core's one route to the front end
 * @see Channels#create()
 */
public record CoreChannel(CoreReceiver coreReceiver, CoreSender coreSender) {
}
