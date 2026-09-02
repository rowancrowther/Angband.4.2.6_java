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

/**
 * Everything the EDT is allowed to do with the channels: post a message, and nothing else. The
 * third and smallest of the holders {@link Channels} hands out, alongside {@link CoreChannel} and
 * {@link UIChannel}.
 * <p>
 * <b>A record with one component, and the missing component is the point.</b> There is no
 * receiver here, because {@link javax.swing.SwingUtilities#invokeLater} is what delivers work
 * <em>to</em> the EDT — a queue it read from would be a second inbox competing with Swing's own,
 * and reading it would mean blocking the thread that repaints the window. A listener holding one
 * of these cannot make that mistake: the method it would need does not exist.
 * <p>
 * The one queue it writes to is the UI thread's inbox rather than the core's, so window events
 * reach the thread that is actually waiting for them. {@code UILoop} translates them there;
 * see {@link Channels#create()} for why the raw event does not go to the core direct.
 *
 * @param edtSender the EDT's writing end of the UI thread's inbox
 * @see Channels#create()
 */
public record EDTChannel(EDTSender edtSender) {
}
