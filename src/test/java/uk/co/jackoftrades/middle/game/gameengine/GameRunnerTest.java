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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import uk.co.jackoftrades.channel.Channels;
import uk.co.jackoftrades.channel.Receiver;
import uk.co.jackoftrades.channel.enums.CoreLifecycleEvent;
import uk.co.jackoftrades.channel.enums.UILifecycleEvent;
import uk.co.jackoftrades.channel.messages.ChannelMessage;
import uk.co.jackoftrades.channel.messages.CoreMessage;
import uk.co.jackoftrades.channel.messages.UIMessage;
import uk.co.jackoftrades.middle.game.event.statusdisplay.StatusDisplayHolder;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Stage 3's core half: {@link GameRunner#gameLoop()} as a receive loop, and the reply that ends
 * it.
 *
 * <p>What is asserted is the protocol, not the game: that {@code SAVE_AND_STOP} is answered with
 * {@code STOPPED} <em>and then</em> the thread ends, in that order, and that nothing else on the
 * queue ends it. The order is the whole point of the handshake - a core that stopped first and
 * replied never would leave the front end waiting for a message that cannot arrive, and a core
 * that replied but kept running would hold the JVM open after the windows had gone.
 *
 * <p><b>These tests run the real thing, data load and all.</b> {@code start()} builds the engine
 * and {@code gameLoop()} loads the game constants before it reaches its first receive, and none of
 * that can be stood in for: {@code GameEngine} is a singleton with a private constructor, so the
 * {@code getGameEngine()} boundary can only ever hand back the real one. That makes this an
 * integration test by nature - it will fail if the data files are unreadable, for reasons that
 * have nothing to do with the handshake - though in practice a load costs well under a second, so
 * the cost is a reason to keep the file focused rather than a reason to avoid it. The unit-level
 * version of the same protocol, with no engine involved, is
 * {@code ChannelsTest.theShutdownHandshakeCompletes}; this is the one that pins {@code GameRunner}
 * itself.
 *
 * <p><b>Every wait is bounded.</b> A {@link Receiver} can only block, so "nothing arrived" is
 * proved by reading on a throwaway thread and giving up on it - see {@link #poll}. A test that
 * simply called {@code receive()} would hang the build on exactly the failure it exists to catch.
 *
 * @author Rowan Crowther
 */
@Timeout(value = 60, unit = TimeUnit.SECONDS)
class GameRunnerTest {

    /**
     * How long to allow for the data load and the reply that follows it. Twenty times what it
     * takes today, since a deadline that fires on a slow machine would be a flaky test rather
     * than a found bug; the class timeout is the real backstop for a loop that never ends.
     */
    private static final long LOAD_AND_REPLY_MILLIS = 15_000;

    /**
     * How long to leave the loop alone before concluding that it is still running happily.
     */
    private static final long STILL_RUNNING_MILLIS = 500;

    /**
     * The thread name {@link GameRunner#start} gives the game thread, and this test's only handle
     * on it - the runner exposes none.
     */
    private static final String CORE_THREAD = "angband-game-loop";

    private Channels channels;
    private GameRunner runner;

    @BeforeEach
    void setUp() {
        channels = Channels.create();
        runner = new GameRunner();
    }

    /**
     * Leaves nothing running and nothing global changed: the holder is static, and a game thread
     * left blocked on a queue would outlive the test that started it.
     */
    @AfterEach
    void tearDown() throws InterruptedException {
        if (coreThreadIsAlive()) {
            requestStop();
            awaitThreadEnd();
        }
        StatusDisplayHolder.resetInstance();
    }

    /**
     * Sends the shutdown request the front end sends.
     */
    private void requestStop() {
        channels.uiChannel().uiSender()
                .send(new UIMessage.LifecycleUIMessage(UILifecycleEvent.SAVE_AND_STOP));
    }

    /**
     * Reads one message from the UI half's inbox, or gives up.
     *
     * <p>The read happens on a throwaway thread because that is the only way to put a deadline on
     * a blocking receive without reaching past the view to the queue underneath - which would be
     * testing something other than what the core actually writes to.
     *
     * @param millis how long to wait
     * @return the message, or {@code null} if none arrived in time
     * @author Rowan Crowther
     */
    private ChannelMessage poll(long millis) throws InterruptedException {
        AtomicReference<ChannelMessage> received = new AtomicReference<>();

        Thread reader = new Thread(() -> {
            try {
                received.set(channels.uiChannel().uiReceiver().receive());
            } catch (InterruptedException giveUp) {
                // nothing arrived in time, which is the answer
            }
        }, "poll");

        reader.start();
        reader.join(millis);
        reader.interrupt();
        reader.join();

        return received.get();
    }

    /**
     * Drains the inbox until the core reports stopping, ignoring the start-up traffic the data
     * load puts there first.
     *
     * @return true if {@code STOPPED} arrived within the deadline
     * @author Rowan Crowther
     */
    private boolean awaitStopped() throws InterruptedException {
        long deadline = System.currentTimeMillis() + LOAD_AND_REPLY_MILLIS;

        while (System.currentTimeMillis() < deadline) {
            ChannelMessage message = poll(deadline - System.currentTimeMillis());

            if (message == null) {
                return false;
            }
            if (message instanceof CoreMessage.LifecycleCoreMessage(CoreLifecycleEvent event)
                    && event == CoreLifecycleEvent.STOPPED) {
                return true;
            }
        }

        return false;
    }

    /**
     * Whether the game thread has finished, allowing for the data load in front of it.
     */
    private boolean awaitThreadEnd() throws InterruptedException {
        long deadline = System.currentTimeMillis() + LOAD_AND_REPLY_MILLIS;

        while (System.currentTimeMillis() < deadline) {
            if (!coreThreadIsAlive()) {
                return true;
            }
            Thread.sleep(50);
        }

        return false;
    }

    /**
     * Whether a live game thread is still about.
     *
     * <p>Asked of the thread registry rather than of the runner, which exposes no handle on its
     * thread. That is also closer to what the shutdown really promises: it is a live non-daemon
     * thread, whoever holds it, that keeps the JVM up after the windows have gone.
     */
    private boolean coreThreadIsAlive() {
        return Thread.getAllStackTraces().keySet().stream()
                .anyMatch(thread -> CORE_THREAD.equals(thread.getName()) && thread.isAlive());
    }

    /**
     * The core's whole half of the shutdown: asked to save and stop, it reports {@code STOPPED}
     * and its thread finishes - the reply first, so the front end is released before the thread
     * that owed it the reply disappears.
     *
     * <p>Note what is not asserted: that {@code STOPPED} is the last thing said. The core is free
     * to say more on its way out, and Chapter 8's save will.
     *
     * @author Rowan Crowther
     */
    @Test
    void saveAndStopIsAnsweredWithStoppedAndThenTheThreadEnds() throws Exception {
        runner.start(channels.coreChannel());

        requestStop();

        assertTrue(awaitStopped(), "the core never reported STOPPED");
        assertTrue(awaitThreadEnd(), "the core replied but its thread kept running");
    }

    /**
     * {@code START} is not a stop. It is the first message the front end ever sends, so a switch
     * arm that fell through to the shutdown would end the game before it began - and would do it
     * during start-up, where a dead core looks like a slow one.
     *
     * @author Rowan Crowther
     */
    @Test
    void startDoesNotEndTheLoop() throws Exception {
        runner.start(channels.coreChannel());

        channels.uiChannel().uiSender()
                .send(new UIMessage.LifecycleUIMessage(UILifecycleEvent.START));
        Thread.sleep(STILL_RUNNING_MILLIS);

        assertTrue(coreThreadIsAlive(), "START must not end the game loop");

        requestStop();
        assertTrue(awaitStopped(), "the core stopped listening after START");
    }

    /**
     * A raw window event on the core's inbox is ignored: not acted on, and not fatal. It should
     * never get here - the EDT posts those to the UI thread - but the arm exists to keep the
     * switch exhaustive, and this pins that it stays harmless if the wiring ever slips.
     *
     * @author Rowan Crowther
     */
    @Test
    void aMisroutedWindowEventIsIgnored() throws Exception {
        runner.start(channels.coreChannel());

        channels.uiChannel().uiSender().send(new UIMessage.WindowCloseRequested());
        Thread.sleep(STILL_RUNNING_MILLIS);

        assertTrue(coreThreadIsAlive(), "a raw window event must not shut the core down");

        requestStop();
        assertTrue(awaitStopped(), "the core should have ignored it and carried on listening");
        assertTrue(awaitThreadEnd(), "the core never finished");
    }
}