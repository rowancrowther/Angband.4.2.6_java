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

package uk.co.jackoftrades.middle.game.event.eventhandlers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.jackoftrades.channel.Channels;
import uk.co.jackoftrades.channel.enums.GameEventType;
import uk.co.jackoftrades.channel.messages.CoreMessage;
import uk.co.jackoftrades.channel.messages.data.EventDataString;
import uk.co.jackoftrades.middle.game.event.EventsHandler;
import uk.co.jackoftrades.channel.messages.data.GameEventData;
import uk.co.jackoftrades.middle.game.event.birthhandlers.UIBirth;
import uk.co.jackoftrades.middle.game.event.statusdisplay.StatusDisplayHolder;
import uk.co.jackoftrades.middle.game.gameengine.GameEngine;

import java.nio.channels.Channel;

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
 * <p>Only {@link #enterInit} does real work so far. The rest log and return, standing in for the
 * screen setup and teardown C does at each transition.
 *
 * @author Rowan Crowther
 */
public class InitHandlers {
    /**
     * Logger for the phase transitions, which is currently all most of these handlers do.
     *
     * @author Rowan Crowther
     */
    private static final Logger logger = LogManager.getLogger(InitHandlers.class);

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
     * that has nothing on it yet.
     *
     * @author Rowan Crowther
     */
    public static void initHandlers() {
        EventsHandler eventsHandler = GameEngine.getEventsBusHandler();
        eventsHandler.eventAddHandler(GameEventType.EVENT_ENTER_INIT, InitHandlers::enterInit);
        eventsHandler.eventAddHandler(GameEventType.EVENT_LEAVE_INIT, InitHandlers::leaveInit);

        eventsHandler.eventAddHandler(GameEventType.EVENT_ENTER_GAME, InitHandlers::enterGame);
        eventsHandler.eventAddHandler(GameEventType.EVENT_LEAVE_GAME, InitHandlers::leaveGame);

        eventsHandler.eventAddHandler(GameEventType.EVENT_ENTER_WORLD, InitHandlers::enterWorld);
        eventsHandler.eventAddHandler(GameEventType.EVENT_LEAVE_WORLD, InitHandlers::leaveWorld);

        UIBirth.uiInitBirthstateHandlers();
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
     * <p>The display is fetched from the holder at call time, not captured at registration, so a
     * front end that registers itself late still gets the call.
     *
     * <p><b>What "put the title screen up" now means.</b> Since stage 2 the holder contains a
     * {@code ChannelStatusDisplay}, so this call sends {@code SimpleCoreMessage(EVENT_ENTER_INIT)}
     * and returns; the other half decides that means {@code news.txt} on the screen. Reading
     * the file, parsing it and painting it all moved to {@code UILoop} with it - which is C's own
     * arrangement, where {@code game-event.c} broadcasts and {@code ui-*.c} draws.
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
     * @param eventType the event being handled, always {@code EVENT_ENTER_INIT}; not read
     * @param data      the payload; must be an {@link EventDataString} or nothing happens
     * @author Rowan Crowther
     */
    public static void enterInit(GameEventType eventType, GameEventData data) {
        // logger.info("Entering init");

        if (data instanceof EventDataString message) {
            StatusDisplayHolder.getInstance().showSplashScreen();

            EventsHandler eventsHandler = GameEngine.getEventsBusHandler();
            eventsHandler.eventAddHandler(GameEventType.EVENT_INITSTATUS, InitHandlers::splashScreenNote);
        }
    }

    /**
     * A progress note arrived during the data load. Subscribed by {@link #enterInit}, not by
     * {@link #initHandlers()}.
     *
     * <p>The port of {@code splashscreen_note} ({@code [C] src/ui-display.c})'s non-birth branch,
     * which reads the string out of the payload and prints it under the title screen. This does the
     * first half and hands the second across the boundary: the note goes to
     * {@code StatusDisplayHolder.getInstance().splashScreenNote(...)}, which since stage 2 is a
     * {@code ChannelStatusDisplay} and so puts it on the core channel rather than painting it.
     *
     * <p><b>Guarded on the payload</b>, as {@link #enterInit} is: a signal carrying no
     * {@link EventDataString} is dropped, so a caller using a bare {@code eventSignal} would lose
     * the note silently. {@code InitHandlersTest} pins that.
     *
     * @param eventType the event being handled, always {@code EVENT_INITSTATUS}; not read
     * @param data      the payload carrying the note; must be an {@link EventDataString} or nothing
     *                  is forwarded
     * @author Rowan Crowther
     */
    public static void splashScreenNote(GameEventType eventType, GameEventData data) {
        logger.info("Splash screen note");
        if (data instanceof EventDataString message) {
            String messageString = message.string();
            StatusDisplayHolder.getInstance().splashScreenNote(messageString);
        }
    }

    /**
     * The data load has finished. C takes the splash screen down here; this logs and returns.
     *
     * @param eventType the event being handled, always {@code EVENT_LEAVE_INIT}; not read
     * @param data      the payload; not read
     * @author Rowan Crowther
     */
    public static void leaveInit(GameEventType eventType, GameEventData data) {
        logger.info("Leaving init");
    }

    /**
     * A character now exists and play is starting. C builds the main game display here; this logs
     * and returns.
     *
     * @param eventType the event being handled, always {@code EVENT_ENTER_GAME}; not read
     * @param data      the payload; not read
     * @author Rowan Crowther
     */
    public static void enterGame(GameEventType eventType, GameEventData data) {
        logger.info("Entering game");
    }

    /**
     * Play has ended - the character died, or the player quit. C tears the game display down here;
     * this logs and returns.
     *
     * @param eventType the event being handled, always {@code EVENT_LEAVE_GAME}; not read
     * @param data      the payload; not read
     * @author Rowan Crowther
     */
    public static void leaveGame(GameEventType eventType, GameEventData data) {
        logger.info("Leaving game");
    }

    /**
     * A level is being entered and the world is live. C starts the map subwindows here; this logs
     * and returns.
     *
     * @param eventType the event being handled, always {@code EVENT_ENTER_WORLD}; not read
     * @param data      the payload; not read
     * @author Rowan Crowther
     */
    public static void enterWorld(GameEventType eventType, GameEventData data) {
        logger.info("Entering world");
    }

    /**
     * The level is being left. C stops the map subwindows here; this logs and returns.
     *
     * @param eventType the event being handled, always {@code EVENT_LEAVE_WORLD}; not read
     * @param data      the payload; not read
     * @author Rowan Crowther
     */
    public static void leaveWorld(GameEventType eventType, GameEventData data) {
        logger.info("Leaving world");
    }

    // uiInitBirthstateHandlers();
}