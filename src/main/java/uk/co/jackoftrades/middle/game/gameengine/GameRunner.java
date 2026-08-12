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
import uk.co.jackoftrades.middle.game.event.eventhandlers.InitHandlers;

/**
 * Owner of the game thread: the background thread the middle end runs on, kept
 * clear of Swing's event dispatch thread (EDT) so game work never blocks
 * repainting and input never blocks the game.
 *
 * <p>This is the front end's single handle on the middle end. {@code SwingUI}
 * holds one of these and drives it with {@link #start(CoreChannel)} and
 * {@link #requestStop()}; it imports nothing else from the middle end, which is
 * what keeps the boundary between the two halves one object wide.
 *
 * <p><b>On its way out as a handle.</b> {@code start} now takes the core's end of
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
 * <p><b>Half wired.</b> {@link #gameLoop()} now registers the start-up handlers
 * and runs the data load on this thread, so the engine's mutable state is
 * confined to it by construction - but once the load finishes it falls into a
 * bare timing loop with nothing to do. Stage 3 replaces that with a receive on
 * the UI channel, at which point the thread spends its idle time blocked on a
 * queue rather than sleeping.
 *
 * @author Rowan Crowther
 */
public class GameRunner {
    private static final Logger logger = LogManager.getLogger(GameRunner.class);

    /**
     * The game thread, created fresh by each {@link #start()} call. Null until
     * the first {@code start()}, so {@link #requestStop()} must not be called
     * before then.
     *
     * @author Rowan Crowther
     */
    private Thread thread;
    /**
     * Whether {@link #gameLoop()} should keep running - the flag
     * {@link #requestStop()} clears to ask the loop to finish.
     *
     * <p>{@code volatile} because it is written from the EDT (via
     * {@code requestStop()}, from the window-closing listener) and read from the
     * game thread. Without it the write is not guaranteed to ever become visible
     * to the reader, and the loop could spin on a stale {@code true} forever.
     *
     * @author Rowan Crowther
     */
    private volatile boolean running = false;

    /**
     * The middle end this runner drives, obtained in {@link #start()} and used on the game thread.
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
     * and the receiver stage 3 turns {@link #gameLoop()} into a loop over.
     *
     * <p>Held but not yet read. The start-up traffic that already crosses goes out
     * through {@code ChannelStatusDisplay}, which the front end registers in the
     * holder with its own copy of this sender - so today the core has two routes to
     * one queue and uses the other one. Stage 5 removes the holder and this becomes
     * the only route; stage 3 gives the receiving end its first reader.
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
     * observe {@code false} and exit immediately on its first test.
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
     * Ask the game thread to stop: clear {@link #running} so the loop's next test
     * fails, then interrupt the thread so it does not sit out a sleep first.
     *
     * <p>Called from the EDT by the window-closing listener. It only
     * <em>requests</em> the stop - it does not join the thread, so the thread may
     * still be finishing when this returns.
     *
     * @author Rowan Crowther
     */
    public void requestStop() {
        running = false;
        thread.interrupt();
    }

    /**
     * The game thread's body: loop until {@link #running} is cleared.
     *
     * <p>A placeholder. It currently only sleeps in 5ms slices to keep the thread
     * alive and idle; the real body is the port of C's {@code play_game()}
     * ({@code src/ui-game.c}) - initialise, then alternate between fetching a
     * command and running the game world until the player dies or quits.
     *
     * <p>Note that in practice the loop presently ends by exception rather than by
     * the flag: {@link #requestStop()} interrupts the sleep, and the resulting
     * {@link InterruptedException} is rethrown wrapped in a
     * {@link RuntimeException}, killing the thread before the {@code while} test is
     * reached. That is survivable only because the loop holds no state and the
     * process is exiting anyway; a real body will need to catch the interrupt and
     * fall out of the loop cleanly instead.
     *
     * @author Rowan Crowther
     */
    public void gameLoop() {
        InitHandlers.initHandlers();

        gameEngine.loadGameConstants();

        while (running) {
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
