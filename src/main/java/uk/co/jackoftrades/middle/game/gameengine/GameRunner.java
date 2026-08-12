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
import uk.co.jackoftrades.channel.CoreChannel;
import uk.co.jackoftrades.channel.enums.CoreLifecycleEvent;
import uk.co.jackoftrades.channel.messages.CoreMessage;
import uk.co.jackoftrades.channel.messages.UIMessage;
import uk.co.jackoftrades.middle.game.event.eventhandlers.InitHandlers;

/**
 * Owner of the game thread: the background thread the middle end runs on, kept
 * clear of Swing's event dispatch thread (EDT) so game work never blocks
 * repainting and input never blocks the game.
 *
 * <p>This is the front end's single handle on the middle end. {@code SwingUI}
 * holds one of these and drives it with {@link #start(CoreChannel)} — one method,
 * now that stopping is a message rather than a call; it imports nothing else from
 * the middle end, which is what keeps the boundary between the two halves one
 * object wide.
 *
 * <p><b>On its way out as a handle.</b> {@code start} takes the core's end of
 * the channel, so the front end hands this class the means to talk back rather
 * than being talked to directly. Stage 4 finishes the job: this becomes the core
 * thread's own {@code Runnable}, constructed by {@code main()} with its channel
 * ends, and the front end holds nothing of the middle end at all.
 *
 * <p>There is no C counterpart to this class. The C original is single-threaded:
 * {@code main()} ({@code src/main.c}) initialises and then calls
 * {@code play_game()} ({@code src/ui-game.c}) on that same thread, blocking for
 * input inside the command hook. Splitting the loop onto its own thread is a
 * port-only decision forced by Swing, which reserves the EDT for the UI.
 *
 * <p><b>Wired at both ends, with nothing in the middle yet.</b> {@link #gameLoop()}
 * registers the start-up handlers and runs the data load on this thread, so the
 * engine's mutable state is confined to it by construction; it then blocks on the
 * core's inbox and answers the shutdown handshake. What is still missing is the
 * game: the loop understands the two lifecycle messages and nothing else, because
 * nothing else is sent yet. Commands arrive with Chapter 5 and the save with
 * Chapter 8, and both land as further arms of the same switch.
 *
 * @author Rowan Crowther
 */
public class GameRunner {
    private static final Logger logger = LogManager.getLogger(GameRunner.class);

    /**
     * The game thread, created fresh by each {@link #start(CoreChannel)} call. Null
     * until the first {@code start()}.
     *
     * <p>Non-daemon, being an ordinary {@code new Thread(...)}, which is what makes
     * the handshake's promise real: the JVM cannot exit while this thread is still
     * saving, because a live non-daemon thread keeps it alive.
     *
     * @author Rowan Crowther
     */
    private Thread thread;
    /**
     * Whether {@link #gameLoop()} should keep running - cleared by the loop itself
     * when the front end asks it to stop.
     *
     * <p>Not {@code volatile}, and no longer needs to be. It was, when the EDT set
     * it through a {@code requestStop()} that has since gone: a flag written on one
     * thread and read on another has no guarantee of ever being seen without it.
     * Now that the stop arrives as a message, the only write and the only read are
     * both on the game thread, so ordinary field access is enough — the flag has
     * become bookkeeping local to the loop rather than a means of communication.
     *
     * @author Rowan Crowther
     */
    private boolean running = false;

    /**
     * The middle end this runner drives, obtained in {@link #start(CoreChannel)} and used on the
     * game thread.
     *
     * <p>Built in {@code start()} rather than in {@link #gameLoop()} deliberately: constructing the
     * engine replaces the event bus, and the handlers registered at the top of the loop have to go
     * onto the bus that survives. Assigning it before {@link Thread#start()} also publishes it
     * safely to the game thread without needing {@code volatile}.
     *
     * @author Rowan Crowther
     */
    private GameEngine gameEngine;

    /**
     * The core's pair of channel ends: the sender it reports to the other half on,
     * and the receiver {@link #gameLoop()} is a loop over.
     *
     * <p>The receiving end is this class's whole knowledge of the front end - the
     * loop waits on it and never calls the UI at all. The sending end is a second
     * route to the UI's inbox rather than the only one: the start-up traffic still
     * goes out through {@code ChannelStatusDisplay}, which the front end registers
     * in the holder with its own copy of this sender. So the core has two routes to
     * one queue and uses whichever the caller happened to be given. Stage 5 removes
     * the holder and leaves this as the only route.
     *
     * <p>Assigned in {@link #start(CoreChannel)} before {@link Thread#start()},
     * which publishes it safely to the game thread without needing
     * {@code volatile}.
     *
     * @author Rowan Crowther
     */
    private CoreChannel coreChannel;

    /**
     * Start the game thread and begin running {@link #gameLoop()} on it.
     *
     * <p>{@link #running} is set before {@link Thread#start()} so the loop cannot
     * observe {@code false} and exit immediately on its first test. Every field the
     * loop reads is likewise assigned before the thread exists, which is what makes
     * their lack of {@code volatile} safe: starting a thread publishes everything
     * written before it to that thread.
     *
     * @param channel the core's pair of channel ends, kept for the loop to use
     * @author Rowan Crowther
     */
    public void start(CoreChannel channel) {
        this.coreChannel = channel;
        
        thread = new Thread(this::gameLoop, "angband-game-loop");
        running = true;

        // Must keep the following two lines (gameEngine = getGameEngine(); & thread.start();)
        // in this order as getGameEngine publishes the bus.
        gameEngine = getGameEngine();
        thread.start();
    }

    /**
     * The game engine singleton, building it on first call.
     *
     * <p>An instance method wrapping a static call, which is what makes it a boundary: a test can
     * subclass {@code GameRunner} and return a stand-in engine without touching
     * {@link GameEngine#getGame()} or the singleton behind it.
     *
     * @return the game engine
     * @author Rowan Crowther
     */
    public GameEngine getGameEngine() {
        return GameEngine.getGame();
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
        InitHandlers.initHandlers();

        gameEngine.loadGameConstants();

        while (running) {
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
                                running = false;
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
                running = false;
                return;
            }
        }
    }
}
