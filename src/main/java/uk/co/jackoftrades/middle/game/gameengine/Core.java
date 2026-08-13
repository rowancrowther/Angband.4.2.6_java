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

package uk.co.jackoftrades.middle.game.gameengine;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.jackoftrades.StartupOptions;
import uk.co.jackoftrades.channel.CoreChannel;
import uk.co.jackoftrades.channel.corechannel.CoreSender;
import uk.co.jackoftrades.channel.enums.CoreLifecycleEvent;
import uk.co.jackoftrades.channel.messages.CoreMessage;
import uk.co.jackoftrades.channel.messages.UIMessage;
import uk.co.jackoftrades.middle.game.event.eventhandlers.InitHandlers;

/**
 * The middle end's body: what the game thread runs, kept clear of Swing's event
 * dispatch thread (EDT) so game work never blocks repainting and input never
 * blocks the game. The thread itself belongs to {@code main()}, which names it
 * {@code angband-core}, starts it and waits for it.
 *
 * <p><b>Built by {@code main()}, named by nothing else.</b> This used to be the
 * front end's single handle on the middle end - {@code SwingUI} constructed one
 * and drove it. Stage 4 moved that: {@code main()} now builds this with the
 * core's channel ends and the parsed command line, hands it to a thread, and the
 * front end holds nothing of the middle end at all. What this class knows of the
 * other half is a queue it reads and a queue it writes, which is what keeps the
 * boundary between them one package wide.
 *
 * <p>{@link #gameLoop()} is the thread's body, reached through the {@code
 * Runnable} {@code main()} wraps this in. The wrapping is why the loop builds the
 * engine itself rather than trusting a caller to have done it first: there is one
 * entry point, and everything it needs is set up inside it.
 *
 * <p>There is no C counterpart to this class. The C original is single-threaded:
 * {@code main()} ({@code src/main.c}) initialises and then calls
 * {@code play_game()} ({@code src/ui-game.c}) on that same thread, blocking for
 * input inside the command hook. Splitting the loop onto its own thread is a
 * port-only decision forced by Swing, which reserves the EDT for the UI.
 *
 * <p><b>Wired at both ends, with nothing in the middle yet.</b> {@link #gameLoop()}
 * builds the engine, installs the boundary the start-up events leave through,
 * registers the handlers and runs the data load - all on this thread, so the
 * engine's mutable state is confined to it by construction; it then blocks on the
 * core's inbox and answers the shutdown handshake. What is still missing is the
 * game: the loop understands the two lifecycle messages and nothing else, because
 * nothing else is sent yet. Commands arrive with Chapter 5 and the save with
 * Chapter 8, and both land as further arms of the same switch.
 *
 * @author Rowan Crowther
 */
public class Core {
    private static final Logger logger = LogManager.getLogger(Core.class);

    /**
     * The middle end this class drives, built by {@link #getGameEngine()} and used only on the
     * game thread.
     *
     * <p>Built at the top of {@link #gameLoop()} and before {@code initHandlers()}, which is an
     * ordering rather than a preference: constructing the engine replaces the event bus, so
     * handlers registered first would go onto a bus that is then thrown away, and their events
     * would never arrive.
     *
     * <p>Not {@code volatile}, and does not need to be. It is written and read on one thread - this
     * object is constructed on {@code main}'s thread but every field it touches after that belongs
     * to the game thread, and starting a thread publishes everything written before the start to
     * it.
     *
     * @author Rowan Crowther
     */
    private GameEngine gameEngine;

    /**
     * The core's pair of channel ends: the sender it reports to the other half on,
     * and the receiver {@link #gameLoop()} is a loop over.
     *
     * <p>The receiving end is this class's whole knowledge of the front end - the
     * loop waits on it and never calls the UI at all. The sending end goes out
     * twice over: directly, for the shutdown reply, and handed to
     * {@code InitHandlers}, so the start-up narration the event handlers produce
     * leaves by the same queue. Both routes are this one sender. Stage 4 made that
     * true - the front end used to hold a copy of the sender it had no business
     * holding - and stage 5 removed the last indirection on the second route, so the
     * handlers now send on it themselves rather than through a registered display.
     *
     * <p>Handed in at construction, on {@code main}'s thread, and read on the game
     * thread; safe without {@code volatile} because starting a thread publishes
     * everything written before it.
     *
     * @author Rowan Crowther
     */
    private CoreChannel coreChannel;

    /**
     * The parsed command line, handed in by {@code main()} so the core has its own copy rather
     * than reading one the front end owns.
     *
     * <p>Nothing reads it yet. The savefile group is what this half will want -
     * {@code selectSavefile}, {@code startNewCharacter} and {@code useSpecificCharacter} all decide
     * what happens before the first turn, which is Chapter 3's work. {@code requestGraphicsMode}
     * belongs to the other half and is carried here only because both halves get the whole record.
     *
     * @author Rowan Crowther
     */
    private StartupOptions startupOptions;

    /**
     * Put the game engine in {@link #gameEngine}, building the singleton on first call.
     *
     * <p>An instance method wrapping a static call, so the dependency on
     * {@link GameEngine#getGame()} is reachable by name and could be replaced without touching the
     * singleton itself.
     *
     * <p><b>Caveat, and it is a real one.</b> This assigns the field rather than returning the
     * engine, which means a subclass overriding it cannot install a stand-in: {@link #gameEngine}
     * is private, so the override has nothing it can write to, and {@link #gameLoop()} would find
     * the field still null. The boundary is nominal until either the field is made visible to
     * subclasses or this hands the engine back instead of storing it.
     *
     * @author Rowan Crowther
     */
    public void getGameEngine() {
        gameEngine = GameEngine.getGame();
    }

    /**
     * Build the core around the channel ends and the options {@code main()} gives it.
     *
     * <p>Only assembles state: no thread is started, no engine is built and no message is sent
     * until {@link #gameLoop()} runs on the thread {@code main()} wraps this in. That split is what
     * lets construction happen on one thread and everything else on another.
     *
     * @param coreChannel    the core's pair of channel ends - its inbox and its way of replying
     * @param startupOptions the parsed command line
     * @author Rowan Crowther
     */
    public Core(CoreChannel coreChannel, StartupOptions startupOptions) {
        this.coreChannel = coreChannel;
        this.startupOptions = startupOptions;
    }

    /**
     * The game thread's body: load the data, then block on the core's inbox and act
     * on what the front end sends, until it sends the message that ends the loop.
     *
     * <p>The port of C's {@code play_game()} ({@code src/ui-game.c}), at the stage
     * where only its skeleton exists. C initialises and then alternates between
     * fetching a command and running the game world; this initialises and then waits
     * for messages, which is the same shape with a queue where C has a blocking call
     * into the display module. The alternation itself is Chapter 5's.
     *
     * <p><b>The statements before the loop are the core's whole set-up, in the only order that
     * works.</b> The engine is built first, because building it replaces the event bus. The
     * handlers are then constructed around the core's sender and subscribed to that bus - both
     * before the load, because {@code EVENT_ENTER_INIT} is raised from inside it and a handler
     * registered afterwards would miss it: the title screen would stay blank and the notes would go
     * nowhere. Only then does {@code loadGameConstants()} produce the events they carry across.
     *
     * <p>Nothing keeps the {@code InitHandlers} instance after this method drops its local, and
     * nothing needs to: subscribing hands the bus a bound method reference per handler, and each of
     * those holds the object.
     *
     * <p>The engine is built here rather than by the caller, replacing a two-call sequence the
     * caller had to know about, where calling this method alone dereferenced a null field. The null
     * check would leave a pre-supplied engine alone, but nothing can supply one today:
     * {@link #gameEngine} is private and {@link #getGameEngine()} assigns it rather than returning
     * it, which is the caveat recorded on that method.
     *
     * <p><b>Blocked, not spinning.</b> {@code receive()} parks the thread until
     * something arrives, so an idle game costs no CPU at all - the arrangement the
     * placeholder {@code sleep} loop this replaced was standing in for. The flip side
     * is that the thread is unresponsive to anything that is not a message, which is
     * the point: there is no longer any other way to reach it.
     *
     * <p><b>The shutdown handshake, core half.</b> {@code SAVE_AND_STOP} is answered
     * with {@code STOPPED} and then the loop ends, in that order, so the reply is on
     * the queue before this thread stops existing. The front end is waiting for that
     * reply before it disposes anything, which is what makes "the save finished
     * before the window went" a guarantee rather than a race - and why nothing here
     * calls {@code System.exit}. The thread simply returns; the JVM exits when the
     * last non-daemon thread does. (There is nothing to save yet. When there is, it
     * goes between the two lines below, and the guarantee is already in place for
     * it.)
     *
     * <p>An interrupt is treated as a hard stop rather than as a request: nothing
     * interrupts this thread today, so an interrupt means something unexpected has
     * happened, and the loop logs and gives up rather than pretending it can carry
     * on. Note that it gives up <em>without</em> sending {@code STOPPED} - the UI
     * would then wait forever, which is a real gap and the reason interrupting this
     * thread is not part of any shutdown path.
     *
     * @author Rowan Crowther
     */
    public void gameLoop() {
        if (gameEngine == null)
            getGameEngine();

        CoreSender coreSender = coreChannel.coreSender();

        InitHandlers initHandlers = new InitHandlers(coreSender);
        initHandlers.initHandlers();

        gameEngine.loadGameConstants();

        while (true) {
            try {
                UIMessage uiMessage = coreChannel.coreReceiver().receive();

                // Exhaustive over the sealed UIMessage, so a new record added to the protocol
                // breaks this switch at compile time rather than being silently ignored at run
                // time. The inner switch over UILifecycleEvent is exhaustive for the same reason.
                switch (uiMessage) {
                    case UIMessage.LifecycleUIMessage lifecycleUIMessage -> {
                        switch (lifecycleUIMessage.event()) {
                            // Reply first, then leave: the STOPPED must be on the queue before
                            // this thread ends, because it is what releases the front end to
                            // dispose the windows. The save goes above the send, in Chapter 8.
                            case SAVE_AND_STOP -> {
                                coreChannel.coreSender()
                                        .send(new CoreMessage.LifecycleCoreMessage(CoreLifecycleEvent.STOPPED));
                                return;
                            }
                            // Nothing to do with it yet - the window is up and the data is
                            // already loaded by the time it arrives. This is where character
                            // birth goes in Chapter 3.
                            case START -> logger.debug("Start message received");
                        }
                    }
                    // Not ours: the EDT posts these to the UI thread's inbox, not to this one.
                    // The arm exists to make the switch exhaustive, and ignores rather than
                    // failing because a raw window event means nothing to the core in any case.
                    case UIMessage.WindowCloseRequested ignored -> {
                    }
                }
            } catch (InterruptedException e) {
                // Nothing interrupts this thread, so reaching here means something unexpected
                // did. Give up rather than continue - but note the UI is left waiting.
                logger.error("Game loop interrupted");
                return;
            }
        }
    }
}
