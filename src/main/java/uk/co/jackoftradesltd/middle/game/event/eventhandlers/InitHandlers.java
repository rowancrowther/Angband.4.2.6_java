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

package uk.co.jackoftradesltd.middle.game.event.eventhandlers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.jackoftradesltd.channel.Sender;
import uk.co.jackoftradesltd.channel.enums.GameEventType;
import uk.co.jackoftradesltd.channel.messages.CoreMessage;
import uk.co.jackoftradesltd.channel.messages.data.EventDataString;
import uk.co.jackoftradesltd.middle.game.event.EventsHandler;
import uk.co.jackoftradesltd.channel.messages.data.GameEventData;
import uk.co.jackoftradesltd.middle.game.event.birthhandlers.UIBirth;
import uk.co.jackoftradesltd.middle.game.gameengine.GameEngine;

/**
 * The middle end's start-up event handlers, and the one place they are registered. This is the port
 * of C's {@code init_display()} ({@code [C] src/ui-display.c}), which subscribes to the enter/leave
 * events for each phase of a session before {@code init_angband()} raises the first of them.
 *
 * <p>Three nested phases, each with an enter and a leave: <em>init</em> (reading
 * {@code lib/gamedata}), <em>game</em> (a character exists), and <em>world</em> (a level is being
 * played). {@code UIBirth} adds the fourth pair, for character creation.
 *
 * <p><b>Registration timing is load-bearing.</b> These are wired from {@code Core.gameLoop()},
 * which runs on the game thread <em>before</em> {@code loadGameConstants()} - because
 * {@code EVENT_ENTER_INIT} is raised from inside that load, and a handler registered afterwards
 * would miss it. It also has to happen after {@code GameEngine.getGame()}, which replaces the bus
 * with a fresh one and would discard anything registered earlier. {@code EnterInitWiringTest} pins
 * that ordering; {@code InitHandlersTest} pins what this class registers.
 *
 * <p>Only {@link #enterInit} and {@link #splashScreenNote} do real work so far. The rest log and
 * return, standing in for the screen setup and teardown C does at each transition.
 *
 * <p><b>An instance, since stage 5, and that is the whole of the change.</b> These handlers used to
 * be static and reach the front end through {@code StatusDisplayHolder}, a process-wide slot the
 * other half wrote. They now hold {@link #coreSender} and put a {@link CoreMessage} on the core
 * channel themselves, which is the same information travelling by a route the compiler can see: a
 * field handed in at construction rather than a static read at call time. The holder, the
 * {@code StatusDisplay} interface and its two implementations all lose their last caller here.
 *
 * <p>What that costs is late registration. The holder was read at the point of use, so a front end
 * that registered itself after the handlers were wired still received the calls; a captured sender
 * fixes the destination when {@code Core.gameLoop()} constructs this. Nothing needs the old
 * property - the channel ends exist before either thread starts, and the front end no longer
 * registers anything with the core at all.
 *
 * @author Rowan Crowther
 */
public class InitHandlers {
    /**
     * Logger for the phase transitions, which is currently all most of these handlers do.
     */
    private static final Logger logger = LogManager.getLogger(InitHandlers.class);

    /**
     * The core's writing end of the UI thread's inbox: where every handler that has something to
     * say puts it.
     *
     * <p>Declared as {@link Sender} rather than as the concrete {@code CoreSender} it is handed.
     * The interface is the whole of what these handlers need - one {@code send} - and typing the
     * field to it says so: this class depends on somewhere to put {@link CoreMessage}s, not on
     * which end of which queue that turns out to be. A test can therefore supply a recording
     * {@code Sender} without owning a queue, and the compiler still refuses anything that is not a
     * {@code CoreMessage}, which is the direction of travel enforced rather than remembered.
     *
     * <p>{@code final}, so it is safely published: the object is constructed on the game thread
     * today, but a final field is visible to any thread that sees the object at all, which does not
     * depend on that staying true.
     */
    private final Sender<CoreMessage> coreSender;

    /**
     * Build the handlers around the channel end they report on.
     *
     * <p>Constructing does not subscribe anything - {@link #initHandlers()} does that, and the two
     * are separate because the bus this wires must be the live one, which is only true after
     * {@code GameEngine.getGame()} has run.
     *
     * @param coreSender the core's sending end of the core channel; not checked for null, and a
     *                   null would fail at the first event rather than here
     */
    public InitHandlers(Sender<CoreMessage> coreSender) {
        this.coreSender = coreSender;
    }

    /**
     * Subscribe every start-up handler to the live event bus, including the birth-screen pair
     * {@code UIBirth} owns.
     *
     * <p>Reads the bus through {@code GameEngine.getEventsBusHandler()} at call time rather than
     * taking it as a parameter, so it always wires the bus that is actually live - the same one
     * {@code GameConstants.init()} will signal through.
     *
     * <p>Not idempotent: dispatch is non-consuming, so calling this twice on one bus registers
     * every handler twice and each would then run twice per signal. Safe today only because
     * {@code Core.gameLoop()} is called once, and each {@code GameEngine} arrives with a bus
     * that has nothing on it yet. Two {@code InitHandlers} instances wired to one bus would do the
     * same damage, which is the same rule seen from the other side.
     *
     * <p>The registrations are bound method references ({@code this::enterInit}), so each one holds
     * this object. That is what keeps the instance reachable once {@code gameLoop()} drops its
     * local: the bus owns the handlers, and the handlers own their receiver. Nothing needs to
     * retain this class deliberately.
     *
     * <p>{@code UIBirth} is constructed here with the same {@link #coreSender} and asked to
     * subscribe its own pair, so start-up still has one entry point and both halves of it report
     * on one channel end.
     */
    public void initHandlers() {
        EventsHandler eventsHandler = GameEngine.getEventsBusHandler();
        eventsHandler.eventAddHandler(GameEventType.EVENT_ENTER_INIT, this::enterInit);
        eventsHandler.eventAddHandler(GameEventType.EVENT_LEAVE_INIT, this::leaveInit);

        eventsHandler.eventAddHandler(GameEventType.EVENT_ENTER_GAME, this::enterGame);
        eventsHandler.eventAddHandler(GameEventType.EVENT_LEAVE_GAME, this::leaveGame);

        eventsHandler.eventAddHandler(GameEventType.EVENT_ENTER_WORLD, this::enterWorld);
        eventsHandler.eventAddHandler(GameEventType.EVENT_LEAVE_WORLD, this::leaveWorld);

        UIBirth uiBirth = new UIBirth(coreSender);
        uiBirth.uiInitBirthstateHandlers();
    }

    /**
     * The data load has started: put the title screen up, and subscribe to the progress notes that
     * will follow. The port of {@code ui_enter_init} ({@code [C] src/ui-display.c}), which does the
     * same two things in the same order.
     *
     * <p>Subscribing to {@code EVENT_INITSTATUS} from in here, rather than in
     * {@link #initHandlers()}, is deliberate and matches C: the notes are only meaningful while the
     * splash screen is up, so nothing listens for them before it exists. Registering during a
     * dispatch is safe because the bus holds each type's handlers in a
     * {@link java.util.concurrent.CopyOnWriteArrayList} - a plain list's fail-fast iterator would
     * throw here.
     *
     * <p><b>What "put the title screen up" means here.</b> One {@code SimpleCoreMessage} onto the
     * core channel, and return; the other half decides that means {@code news.txt} on the screen.
     * Reading the file, parsing it and painting it all live in {@code UILoop} - which is C's own
     * arrangement, where {@code game-event.c} broadcasts and {@code ui-*.c} draws. The send does
     * not wait: the channel is unbounded, so the data load carries on at its own speed whatever the
     * display is doing.
     *
     * <p><b>The message carries {@code eventType}, not the constant.</b> They are the same value
     * today - this is subscribed to {@code EVENT_ENTER_INIT} and nothing else - but forwarding the
     * event that was actually signalled is what the message means, and it keeps the two from
     * drifting apart if this is ever registered against a second type.
     *
     * <p><b>Guarded on the payload.</b> A signal carrying no {@link EventDataString} is ignored
     * entirely - no splash screen, no subscription. The bound {@code message} is never read; the
     * guard is really a check that the sender used {@code eventSignalString}, so switching
     * {@code GameConstants.init()} to a bare {@code eventSignal} would silently lose the title
     * screen rather than fail. {@code InitHandlersTest} pins that.
     *
     * <p>Called on the game thread - but that no longer constrains what it may reach. The call
     * crosses a channel, so the thread that paints is the one that took the message off the queue,
     * and the hop onto the event dispatch thread is made over there. Nothing on this side of the
     * boundary touches Swing at all now.
     *
     * @param eventType the event being handled, always {@code EVENT_ENTER_INIT}; forwarded as the
     *                  message's type
     * @param data      the payload; must be an {@link EventDataString} or nothing happens
     */
    private void enterInit(GameEventType eventType, GameEventData data) {
        // logger.info("Entering init");

        if (data instanceof EventDataString message) {
            coreSender.send(new CoreMessage.SimpleCoreMessage(eventType));

            EventsHandler eventsHandler = GameEngine.getEventsBusHandler();
            eventsHandler.eventAddHandler(GameEventType.EVENT_INITSTATUS, this::splashScreenNote);
        }
    }

    /**
     * A progress note arrived during the data load. Subscribed by {@link #enterInit}, not by
     * {@link #initHandlers()}.
     *
     * <p>The port of {@code splashscreen_note} ({@code [C] src/ui-display.c})'s non-birth branch,
     * which reads the string out of the payload and prints it under the title screen. This does the
     * first half and hands the second across the boundary: the note is unwrapped here and goes
     * straight back onto the core channel as a {@code TextCoreMessage}, leaving where and how to
     * paint it to the other half.
     *
     * <p><b>Two shapes, chosen by payload rather than by occasion.</b> {@link #enterInit} carries
     * nothing so it sends a {@code SimpleCoreMessage}; this carries text so it sends a
     * {@code TextCoreMessage}. Both put the {@link GameEventType} on the message to say what they
     * mean, which is why a third handler with a text payload would need no third record. See
     * {@code CoreMessage}'s Javadoc for why the protocol is built that way round.
     *
     * <p>Nothing on the wire says whether this is a load note or a birth note - C distinguishes them
     * with {@code MSG_BIRTH} on the message payload, and the port has no equivalent yet. Chapter 3
     * is what gives the birth note a caller and has to invent the discriminator; the migration
     * document records what that needs.
     *
     * <p><b>Guarded on the payload</b>, as {@link #enterInit} is: a signal carrying no
     * {@link EventDataString} is dropped, so a caller using a bare {@code eventSignal} would lose
     * the note silently. {@code InitHandlersTest} pins that.
     *
     * @param eventType the event being handled, always {@code EVENT_INITSTATUS}; forwarded as the
     *                  message's type
     * @param data      the payload carrying the note; must be an {@link EventDataString} or nothing
     *                  is forwarded
     */
    private void splashScreenNote(GameEventType eventType, GameEventData data) {
        logger.info("Splash screen note");
        if (data instanceof EventDataString message) {
            String messageString = message.string();
            coreSender.send(new CoreMessage.TextCoreMessage(eventType, messageString));
        }
    }

    /**
     * The data load has finished. C takes the splash screen down here; this logs and returns.
     *
     * @param eventType the event being handled, always {@code EVENT_LEAVE_INIT}; not read
     * @param data      the payload; not read
     */
    private void leaveInit(GameEventType eventType, GameEventData data) {
        logger.info("Leaving init");
        coreSender.send(new CoreMessage.SimpleCoreMessage(eventType));
    }

    /**
     * A character now exists and play is starting. C builds the main game display here; this logs
     * and returns.
     *
     * @param eventType the event being handled, always {@code EVENT_ENTER_GAME}; not read
     * @param data      the payload; not read
     */
    private void enterGame(GameEventType eventType, GameEventData data) {
        logger.info("Entering game");
        coreSender.send(new CoreMessage.SimpleCoreMessage(eventType));
    }

    /**
     * Play has ended - the character died, or the player quit. C tears the game display down here;
     * this logs and returns.
     *
     * @param eventType the event being handled, always {@code EVENT_LEAVE_GAME}; not read
     * @param data      the payload; not read
     */
    private void leaveGame(GameEventType eventType, GameEventData data) {
        logger.info("Leaving game");
        coreSender.send(new CoreMessage.SimpleCoreMessage(eventType));
    }

    /**
     * A level is being entered and the world is live. C starts the map subwindows here; this logs
     * and returns.
     *
     * @param eventType the event being handled, always {@code EVENT_ENTER_WORLD}; not read
     * @param data      the payload; not read
     */
    private void enterWorld(GameEventType eventType, GameEventData data) {
        logger.info("Entering world");
        coreSender.send(new CoreMessage.SimpleCoreMessage(eventType));
    }

    /**
     * The level is being left. C stops the map subwindows here; this logs and returns.
     *
     * @param eventType the event being handled, always {@code EVENT_LEAVE_WORLD}; not read
     * @param data      the payload; not read
     */
    private void leaveWorld(GameEventType eventType, GameEventData data) {
        logger.info("Leaving world");
        coreSender.send(new CoreMessage.SimpleCoreMessage(eventType));
    }
}