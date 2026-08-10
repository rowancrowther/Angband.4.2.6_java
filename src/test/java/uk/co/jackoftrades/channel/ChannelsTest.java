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

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import uk.co.jackoftrades.channel.enums.CoreLifecycleEvent;
import uk.co.jackoftrades.channel.enums.GameEventType;
import uk.co.jackoftrades.channel.enums.UILifecycleEvent;
import uk.co.jackoftrades.channel.messages.ChannelMessage;
import uk.co.jackoftrades.channel.messages.CoreMessage;
import uk.co.jackoftrades.channel.messages.UIMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Unit tests for the transport itself — {@link Channels} and the four views onto its two queues.
 *
 * <p>What is worth testing here is not the queues, which are the JDK's and already work, but the
 * <b>wiring</b>: that a sender and the receiver meant to read it hold the same queue object.
 * Nothing in the type system can check that, and getting it wrong does not throw. Four separately
 * created queues, or a crossed pair, compile perfectly and simply never deliver — both threads
 * block forever with no exception to point at. So the tests assert delivery in both directions,
 * and, just as importantly, assert that messages do <em>not</em> arrive where they should not.
 *
 * <p>The threading tests use two real threads rather than driving the queues from one, because the
 * property under test is that a receiver <em>blocks</em> until the other thread speaks. That
 * cannot be observed from a single thread: {@code take()} on an empty queue would simply deadlock
 * the test. Every test carries a timeout, so a wiring mistake fails the build in seconds instead
 * of hanging it.
 *
 * @author Rowan Crowther
 */
@Timeout(value = 10, unit = TimeUnit.SECONDS)
class ChannelsTest {

    /**
     * How long to wait before concluding that a message is genuinely not coming.
     */
    private static final long NOT_COMING_MILLIS = 250;

    /**
     * A representative core message; the payload is irrelevant to the transport.
     */
    private static final CoreMessage ENTER_INIT =
            new CoreMessage.SimpleCoreMessage(GameEventType.EVENT_ENTER_INIT);

    /**
     * A representative UI message.
     */
    private static final UIMessage START =
            new UIMessage.LifecycleUIMessage(UILifecycleEvent.START);

    /**
     * Starts a thread and returns it, so a test can join on it and surface anything it threw.
     *
     * @param name what to call the thread, so a stack trace says which half failed
     * @param body what the thread should do
     * @return the running thread
     * @author Rowan Crowther
     */
    private static Thread run(String name, ThrowingRunnable body) {
        AtomicReference<Throwable> failure = new AtomicReference<>();

        Thread thread = new Thread(() -> {
            try {
                body.run();
            } catch (Throwable t) {
                failure.set(t);
            }
        }, name);

        thread.setUncaughtExceptionHandler((t, e) -> failure.set(e));
        thread.start();

        return thread;
    }

    /**
     * Asserts that nothing arrives on a receiver within {@link #NOT_COMING_MILLIS}.
     *
     * <p>Necessarily a negative proved by waiting: there is no way to ask a blocking receiver
     * "is anything there?" without either blocking forever or reaching past it to the queue,
     * which would test something other than the view. A quarter of a second is long enough that a
     * genuinely delivered message would have arrived, and short enough not to slow the suite.
     *
     * @param receiver the receiver that should stay empty
     * @param what     what would have arrived, for the failure message
     * @author Rowan Crowther
     */
    private static void assertNothingArrives(Receiver<?> receiver, String what)
            throws InterruptedException {
        AtomicReference<Object> arrived = new AtomicReference<>();

        Thread waiter = run("waiter", () -> arrived.set(receiver.receive()));
        waiter.join(NOT_COMING_MILLIS);

        boolean stillWaiting = waiter.isAlive();
        waiter.interrupt();
        waiter.join();

        assertNull(arrived.get(), what + " arrived somewhere it should never reach");
        assertTrue(stillWaiting, "the receiver should still have been blocked, empty");
    }

    /**
     * A body that may throw, since everything on these channels throws InterruptedException.
     */
    @FunctionalInterface
    private interface ThrowingRunnable {
        void run() throws Exception;
    }

    /**
     * Sends a run of core messages and remembers what it sent, so a test can compare the two
     * sequences without building both by hand.
     *
     * @author Rowan Crowther
     */
    private record CoreSenderFixture(Channels channels) {

        /**
         * @param notes the text of each message to send, in order
         * @return the messages sent, in the same order
         * @author Rowan Crowther
         */
        List<CoreMessage> sendNotes(String... notes) {
            List<CoreMessage> sent = new ArrayList<>();

            for (String note : notes) {
                CoreMessage message =
                        new CoreMessage.TextCoreMessage(GameEventType.EVENT_INITSTATUS, note);
                channels.coreChannel().coreSender().send(message);
                sent.add(message);
            }

            return sent;
        }
    }

    /**
     * The four views must be wired onto exactly two queues, crossed so that each sender feeds the
     * other half's receiver.
     *
     * @author Rowan Crowther
     */
    @Nested
    class Wiring {

        /**
         * The core's route to the front end. This is the one that carries every game event, so a
         * break here is a game that runs invisibly.
         *
         * @author Rowan Crowther
         */
        @Test
        void coreSenderReachesUiReceiver() throws InterruptedException {
            Channels channels = Channels.create();

            channels.coreChannel().coreSender().send(ENTER_INIT);

            assertEquals(ENTER_INIT, channels.uiChannel().uiReceiver().receive());
        }

        /**
         * The front end's route to the core, and the only way the player reaches the game.
         *
         * @author Rowan Crowther
         */
        @Test
        void uiSenderReachesCoreReceiver() throws InterruptedException {
            Channels channels = Channels.create();

            channels.uiChannel().uiSender().send(START);

            assertEquals(START, channels.coreChannel().coreReceiver().receive());
        }

        /**
         * The two queues must be distinct. If {@code create} used one queue for both directions
         * the delivery tests above would still pass — each half would simply read its own
         * traffic back, and the shutdown handshake would appear to work while the other thread
         * heard nothing.
         *
         * @author Rowan Crowther
         */
        @Test
        void aSenderDoesNotFeedItsOwnHalfsReceiver() throws InterruptedException {
            Channels channels = Channels.create();

            channels.coreChannel().coreSender().send(ENTER_INIT);

            assertNothingArrives(channels.coreChannel().coreReceiver(), "the core's own message");
        }

        /**
         * The mirror of the above, for the UI half.
         *
         * @author Rowan Crowther
         */
        @Test
        void uiSenderDoesNotFeedUiReceiver() throws InterruptedException {
            Channels channels = Channels.create();

            channels.uiChannel().uiSender().send(START);

            assertNothingArrives(channels.uiChannel().uiReceiver(), "the UI's own message");
        }

        /**
         * Two sets of channels must not share queues. This is what makes the class safe to
         * construct per test — and it is the property a static field would have destroyed, since
         * one test's unread message would then surface in the next.
         *
         * @author Rowan Crowther
         */
        @Test
        void separateChannelsAreIndependent() throws InterruptedException {
            Channels first = Channels.create();
            Channels second = Channels.create();

            assertNotSame(first.coreChannel(), second.coreChannel());

            first.coreChannel().coreSender().send(ENTER_INIT);

            assertNothingArrives(second.uiChannel().uiReceiver(), "the first set's message");
        }

        /**
         * The views are handed out as constructed, not rebuilt per call, so a half can hold on to
         * its sender rather than reaching through the record every time.
         *
         * @author Rowan Crowther
         */
        @Test
        void accessorsReturnTheSameViewsEachTime() {
            Channels channels = Channels.create();

            assertSame(channels.coreChannel(), channels.coreChannel());
            assertSame(channels.coreChannel().coreSender(), channels.coreChannel().coreSender());
            assertSame(channels.uiChannel().uiReceiver(), channels.uiChannel().uiReceiver());
        }
    }

    /**
     * What the queue guarantees about the traffic it carries.
     *
     * @author Rowan Crowther
     */
    @Nested
    class Delivery {

        /**
         * Messages arrive in the order they were sent. The core relies on this without ever
         * saying so: a progress line that overtook the message announcing the stage it belongs to
         * would be nonsense, and the shutdown handshake depends on {@code STOPPED} arriving after
         * everything the core sent before it.
         *
         * @author Rowan Crowther
         */
        @Test
        void messagesArriveInTheOrderSent() throws InterruptedException {
            Channels channels = Channels.create();
            CoreSenderFixture core = new CoreSenderFixture(channels);

            List<CoreMessage> sent = core.sendNotes("first", "second", "third");

            List<ChannelMessage> received = new ArrayList<>();
            for (int i = 0; i < sent.size(); i++) {
                received.add(channels.uiChannel().uiReceiver().receive());
            }

            assertEquals(sent, received);
        }

        /**
         * Both branches of {@link ChannelMessage} fit down the UI thread's inbox. That queue is
         * typed to the root interface precisely so the EDT can post to it later, and this pins
         * that it really does carry more than {@link CoreMessage}.
         *
         * @author Rowan Crowther
         */
        @Test
        void theUiInboxCarriesBothSenders() throws InterruptedException {
            Channels channels = Channels.create();
            Receiver<ChannelMessage> inbox = channels.uiChannel().uiReceiver();

            channels.coreChannel().coreSender().send(ENTER_INIT);

            assertInstanceOf(CoreMessage.class, inbox.receive());
        }

        /**
         * Sending never blocks, however far behind the reader is. The core must not be paced by
         * the display: a burst of events during level generation has to be absorbed, not waited
         * on.
         *
         * @author Rowan Crowther
         */
        @Test
        void sendingDoesNotWaitForAReader() {
            Channels channels = Channels.create();
            Sender<CoreMessage> sender = channels.coreChannel().coreSender();

            for (int i = 0; i < 1_000; i++) {
                sender.send(new CoreMessage.TextCoreMessage(GameEventType.EVENT_INITSTATUS,
                        "note " + i));
            }
        }
    }

    /**
     * The behaviour that needs two real threads: blocking, waking, and interruption.
     *
     * @author Rowan Crowther
     */
    @Nested
    class AcrossTwoThreads {

        /**
         * A receiver with nothing to read sleeps until the other half speaks, rather than
         * returning null or spinning. This is the property the whole design rests on — it is why
         * the UI thread costs nothing while the player is thinking.
         *
         * @author Rowan Crowther
         */
        @Test
        void receiveBlocksUntilAMessageArrives() throws Exception {
            Channels channels = Channels.create();
            CountDownLatch receiverIsWaiting = new CountDownLatch(1);
            AtomicReference<ChannelMessage> received = new AtomicReference<>();

            Thread ui = run("ui", () -> {
                receiverIsWaiting.countDown();
                received.set(channels.uiChannel().uiReceiver().receive());
            });

            assertTrue(receiverIsWaiting.await(5, TimeUnit.SECONDS), "the UI thread never started");
            Thread.sleep(NOT_COMING_MILLIS);

            assertNull(received.get(), "the receiver returned before anything was sent");
            assertTrue(ui.isAlive(), "the receiver should still be blocked, waiting");

            channels.coreChannel().coreSender().send(ENTER_INIT);
            ui.join(5_000);

            assertFalse(ui.isAlive(), "the receiver should have woken when the message arrived");
            assertEquals(ENTER_INIT, received.get());
        }

        /**
         * Interrupting a blocked receiver raises {@link InterruptedException} rather than
         * returning. That is how a thread parked on an empty channel is told to shut down, so it
         * has to escape the queue rather than be swallowed by it.
         *
         * @author Rowan Crowther
         */
        @Test
        void interruptingABlockedReceiverEndsTheWait() throws Exception {
            Channels channels = Channels.create();
            CountDownLatch interrupted = new CountDownLatch(1);

            Thread core = new Thread(() -> {
                try {
                    channels.coreChannel().coreReceiver().receive();
                    fail("receive returned although nothing was sent");
                } catch (InterruptedException expected) {
                    interrupted.countDown();
                }
            }, "core");

            core.start();
            Thread.sleep(NOT_COMING_MILLIS);
            core.interrupt();

            assertTrue(interrupted.await(5, TimeUnit.SECONDS),
                    "the blocked receiver did not report being interrupted");
        }

        /**
         * The whole shutdown handshake, end to end, with each half on its own thread and neither
         * touching the other's ends.
         *
         * <p>This is the traffic the migration actually has today, and it is the test that would
         * catch a crossed pair in the way it matters: the UI asks to save and stop, the core
         * hears it, does its work, reports {@code STOPPED}, and only then may the front end
         * finish. A break anywhere in that chain leaves one thread waiting forever, which the
         * class timeout turns into a failure rather than a hung build.
         *
         * @author Rowan Crowther
         */
        @Test
        void theShutdownHandshakeCompletes() throws Exception {
            Channels channels = Channels.create();
            AtomicReference<UIMessage> coreHeard = new AtomicReference<>();
            AtomicReference<ChannelMessage> uiHeard = new AtomicReference<>();

            Thread core = run("core", () -> {
                UIMessage request = channels.coreChannel().coreReceiver().receive();
                coreHeard.set(request);

                if (request instanceof UIMessage.LifecycleUIMessage(UILifecycleEvent event)
                        && event == UILifecycleEvent.SAVE_AND_STOP) {
                    channels.coreChannel().coreSender().send(
                            new CoreMessage.LifecycleCoreMessage(CoreLifecycleEvent.STOPPED));
                }
            });

            Thread ui = run("ui", () -> {
                channels.uiChannel().uiSender().send(
                        new UIMessage.LifecycleUIMessage(UILifecycleEvent.SAVE_AND_STOP));
                uiHeard.set(channels.uiChannel().uiReceiver().receive());
            });

            core.join(5_000);
            ui.join(5_000);

            assertFalse(core.isAlive(), "the core never finished its half of the handshake");
            assertFalse(ui.isAlive(), "the front end never heard that the core had stopped");

            assertEquals(new UIMessage.LifecycleUIMessage(UILifecycleEvent.SAVE_AND_STOP),
                    coreHeard.get());
            assertEquals(new CoreMessage.LifecycleCoreMessage(CoreLifecycleEvent.STOPPED),
                    uiHeard.get());
        }
    }
}