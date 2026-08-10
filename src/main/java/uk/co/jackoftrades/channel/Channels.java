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

import uk.co.jackoftrades.channel.corechannel.CoreReceiver;
import uk.co.jackoftrades.channel.corechannel.CoreSender;
import uk.co.jackoftrades.channel.messages.ChannelMessage;
import uk.co.jackoftrades.channel.messages.UIMessage;
import uk.co.jackoftrades.channel.uichannel.UIReceiver;
import uk.co.jackoftrades.channel.uichannel.UISender;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * The two queues the halves of the game talk over, and the only place they are created.
 * <p>
 * Everything else in this package is a view onto one end of one of these queues. This record owns
 * both queues, hands each half exactly the two ends it is entitled to, and then gets out of the
 * way. Concentrating that in one method is not tidiness: a sender and the receiver that reads it
 * must hold the <em>same</em> queue object, and nothing in the type system can check that. Four
 * separately created queues would compile, run, and simply never deliver anything — both threads
 * would block forever on empty queues, with no exception to point at the mistake.
 * <p>
 * Deliberately not a singleton, and deliberately without a static accessor. A global
 * {@code getUIChannel()} would let the core reach the UI's ends and undo the containment the views
 * exist to provide; it would also share one pair of queues across every test in a run, so a
 * message left unread by one test would surface in the next. Construct one in {@code main}, pass
 * {@link #coreChannel()} to the core and {@link #uiChannel()} to the front end, and let the two
 * references go their separate ways.
 *
 * @param coreChannel the core's pair of ends
 * @param uiChannel   the UI half's pair of ends
 */
public record Channels(CoreChannel coreChannel, UIChannel uiChannel) {

    /**
     * Creates the two queues and the four views onto them.
     * <p>
     * The crossover in the wiring is the part to read carefully: {@code coreQueue} is the core's
     * <em>inbox</em>, so the UI sends to it and the core receives from it, and {@code uiQueue} is
     * the reverse. Each queue is named for the half that reads it, not the half that writes it.
     *
     * @return a matched set; the only correctly wired one there is
     */
    public static Channels create() {
        LinkedBlockingQueue<UIMessage> coreQueue = new LinkedBlockingQueue<>();
        LinkedBlockingQueue<ChannelMessage> uiQueue = new LinkedBlockingQueue<>();

        return new Channels(
                new CoreChannel(new CoreReceiver(coreQueue), new CoreSender(uiQueue)),
                new UIChannel(new UIReceiver(uiQueue), new UISender(coreQueue)));
    }
}
