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

package uk.co.jackoftrades.middle.game.event.statusdisplay;

import uk.co.jackoftrades.channel.Sender;
import uk.co.jackoftrades.channel.enums.GameEventType;
import uk.co.jackoftrades.channel.messages.CoreMessage;

/**
 * The {@link StatusDisplay} that displays nothing: it turns each call into a {@link CoreMessage} and
 * puts it on the core channel, leaving what the screen should look like to the other half.
 *
 * <p>This is the pivot of the channel migration's stage 2, and it is small because the boundary was
 * already the right shape. The core still calls {@code showSplashScreen()} from exactly where it
 * always did; only which implementation is registered in {@link StatusDisplayHolder} changed, from
 * the front end's own {@code SplashScreen} to this. Nothing in {@code GameConstants.init()} or
 * {@code InitHandlers} knows the difference - which is the property the whole migration is buying.
 *
 * <p><b>The mapping is by payload shape, not by occasion.</b> "Show the title screen" carries
 * nothing, so it rides {@code SimpleCoreMessage}; a progress note carries text, so it rides
 * {@code TextCoreMessage}. Both carry the {@link GameEventType} that says what they mean, which is
 * why a third method would very likely need no third record. See {@code CoreMessage}'s Javadoc for
 * why the protocol is built that way round.
 *
 * <p><b>Core-side, despite what it does.</b> This runs on the game thread and touches no Swing type
 * - it is the last core code a start-up message passes through. Stage 5 folds it into a bus handler
 * that does the same job for every event that crosses, at which point this class has served its
 * purpose: two methods was worth a named interface only while the boundary had exactly two calls.
 *
 * @author Rowan Crowther
 */
public class ChannelStatusDisplay implements StatusDisplay {
    /**
     * The sending end of the core channel, handed in at construction.
     *
     * <p>A {@link Sender} rather than the queue, so this class can only put messages on - it cannot
     * read the UI's traffic, and cannot send anything that is not a {@link CoreMessage}. The
     * restriction is the point of the view types: the compiler enforces the direction that would
     * otherwise be a convention.
     *
     * @author Rowan Crowther
     */
    private final Sender<CoreMessage> sender;

    /**
     * Build the display around the channel end it sends on.
     *
     * @param sender the core's sending end of the core channel
     * @author Rowan Crowther
     */
    public ChannelStatusDisplay(Sender<CoreMessage> sender) {
        this.sender = sender;
    }

    /**
     * Tell the other half the data load has started. Sends {@code EVENT_ENTER_INIT} and returns
     * immediately - the channel is unbounded, so the core never waits for the screen to appear.
     *
     * @author Rowan Crowther
     */
    @Override
    public void showSplashScreen() {
        CoreMessage.SimpleCoreMessage coreMessage = new CoreMessage.SimpleCoreMessage(GameEventType.EVENT_ENTER_INIT);
        sender.send(coreMessage);
    }

    /**
     * Pass a progress note across as {@code EVENT_INITSTATUS} with its text.
     *
     * <p>Nothing on the wire says whether this is a load note or a birth note - C distinguishes them
     * with {@code MSG_BIRTH} on the message payload, and the port has no equivalent yet. That is why
     * {@code splashScreenBirthNote} is not on this interface; adding it would mean inventing the
     * discriminator against no caller. Chapter 3 gives it one.
     *
     * @param message the note to show, unbracketed; the front end decides the presentation
     * @author Rowan Crowther
     */
    @Override
    public void splashScreenNote(String message) {
        CoreMessage.TextCoreMessage coreMessage = new CoreMessage.TextCoreMessage(GameEventType.EVENT_INITSTATUS, message);
        sender.send(coreMessage);
    }
}
