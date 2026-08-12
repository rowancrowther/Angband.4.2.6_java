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
 * both queues, hands each holder exactly the ends it is entitled to, and then gets out of the
 * way. Concentrating that in one method is not tidiness: a sender and the receiver that reads it
 * must hold the <em>same</em> queue object, and nothing in the type system can check that.
 * Separately created queues would compile, run, and simply never deliver anything — both threads
 * would block forever on empty queues, with no exception to point at the mistake.
 * <p>
 * <b>Three holders, not two.</b> The UI side is two threads with different rights: the UI thread
 * both sends and receives ({@link #uiChannel()}), while the EDT may only post window events onto
 * the UI thread's inbox ({@link #edtChannel()}, a send-only view of one queue). Handing the EDT a
 * whole {@code UIChannel} would let a listener block the event dispatch thread on
 * {@code receive()} — the one thing the front end must never do — so the narrower record is the
 * containment, and it is enforced by what the EDT is given rather than by a rule to remember.
 * <p>
 * Deliberately not a singleton, and deliberately without a static accessor. A global
 * {@code getUIChannel()} would let the core reach the UI's ends and undo the containment the views
 * exist to provide; it would also share one pair of queues across every test in a run, so a
 * message left unread by one test would surface in the next. Construct one in {@code main}, pass
 * {@link #coreChannel()} to the core and {@link #uiChannel()} to the front end, and let the
 * references go their separate ways.
 *
 * @param coreChannel the core's pair of ends
 * @param uiChannel   the UI thread's pair of ends
 * @param edtChannel  the EDT's send-only end, its whole view of the channels
 */
public record Channels(CoreChannel coreChannel, UIChannel uiChannel, EDTChannel edtChannel) {

    /**
     * Creates the two queues and the five views onto them.
     * <p>
     * The crossover in the wiring is the part to read carefully: {@code coreQueue} is the core's
     * <em>inbox</em>, so the UI sends to it and the core receives from it, and {@code uiQueue} is
     * the reverse. Each queue is named for the half that reads it, not the half that writes it.
     * <p>
     * {@code uiQueue} therefore has two writers and {@code coreQueue} one. The {@code EDTSender}
     * is the second writer onto {@code uiQueue}, and it goes there rather than onto
     * {@code coreQueue} for a reason worth keeping in mind when reading the shutdown handshake:
     * the UI thread is blocked on {@code uiQueue} and nothing else, so that is the only queue a
     * window event can reach it on. A close request consequently arrives UI-side first and is
     * translated into a {@code LifecycleUIMessage} there, rather than being sent to the core raw.
     * <p>
     * The queues' element types differ, and that difference is the protocol: {@code coreQueue}
     * holds {@link UIMessage} only, so nothing can put a core message on it, while
     * {@code uiQueue} holds {@link ChannelMessage} because it legitimately carries both the core's
     * traffic and the EDT's.
     *
     * @return a matched set; the only correctly wired one there is
     */
    public static Channels create() {
        LinkedBlockingQueue<UIMessage> coreQueue = new LinkedBlockingQueue<>();
        LinkedBlockingQueue<ChannelMessage> uiQueue = new LinkedBlockingQueue<>();

        return new Channels(
                new CoreChannel(new CoreReceiver(coreQueue), new CoreSender(uiQueue)),
                new UIChannel(new UIReceiver(uiQueue), new UISender(coreQueue)),
                new EDTChannel(new EDTSender(uiQueue)));
    }
}
