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

import uk.co.jackoftradesltd.channel.uichannel.UIReceiver;
import uk.co.jackoftradesltd.channel.uichannel.UISender;

/**
 * Everything the UI half is allowed to do with the channels, and nothing else. The mirror of
 * {@link CoreChannel}.
 * <p>
 * One caveat: the UI half is really two senders, the UI thread and the EDT, and this record does
 * not tell them apart. Both would use {@link #uiSender()}, though the EDT's traffic belongs on the
 * <em>other</em> queue. Splitting {@code UIMessage} by sender is the fix, and it is deferred to
 * Chapter 5, when there is enough traffic for the split to separate anything. Until then, keeping
 * the EDT from ever seeing a whole {@code UIChannel} is the cheaper containment.
 *
 * @param uiReceiver the UI thread's inbox, carrying both the core's events and the EDT's window
 *                   messages
 * @param uiSender   the UI half's one route to the core
 * @see Channels#create()
 */
public record UIChannel(UIReceiver uiReceiver, UISender uiSender) {
}
