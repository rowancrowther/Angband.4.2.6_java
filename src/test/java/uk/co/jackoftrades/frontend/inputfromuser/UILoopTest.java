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

package uk.co.jackoftrades.frontend.inputfromuser;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import uk.co.jackoftrades.channel.Channels;
import uk.co.jackoftrades.channel.enums.CoreLifecycleEvent;
import uk.co.jackoftrades.channel.enums.GameEventType;
import uk.co.jackoftrades.channel.enums.UILifecycleEvent;
import uk.co.jackoftrades.channel.messages.CoreMessage;
import uk.co.jackoftrades.channel.messages.UIMessage;
import uk.co.jackoftrades.frontend.SwingUI;

import javax.swing.SwingUtilities;
import java.awt.GraphicsEnvironment;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

/**
 * Stage 3's UI half: {@link UILoop} as the thread that turns window events into requests the core
 * understands, and the core's reply into a closed window.
 *
 * <p>Two things are under test here, and they are different in kind. The first is a
 * <b>translation</b> - a {@code WindowCloseRequested} in, a {@code SAVE_AND_STOP} out - which is
 * pure message handling and needs no Swing at all. The second is the <b>ending</b>: on
 * {@code STOPPED} the loop must dispose the windows on the event dispatch thread and then return,
 * so the thread finishes rather than being killed. That one is about which thread runs what, so it
 * is asserted by recording the thread the disposal happened on.
 *
 * <p><b>The loop is run on a real thread throughout,</b> because "and then the thread ends" is
 * half of what stage 3 claims. A test that called {@code loop()} inline could assert what it sends
 * but never that it stops - and stopping is the part that used to be done by killing the thread.
 * Every test carries a timeout, so a loop that fails to end fails the build rather than hanging
 * it.
 *
 * <p><b>Where {@code SwingUI} is needed it is subclassed, not mocked.</b> {@link RecordingSwingUI}
 * overrides {@code closeDown} to note that it was called and on which thread, which keeps the real
 * constructor - and so the real window - out of the assertions while still exercising the call the
 * loop actually makes. Building any {@code SwingUI} means building a {@code JFrame}, so those
 * tests are skipped on a headless machine; the translation tests are not, and pass a {@code null}
 * front end deliberately, since an arm that touched it would then fail loudly rather than quietly
 * passing.
 *
 * @author Rowan Crowther
 */
@Timeout(value = 10, unit = TimeUnit.SECONDS)
class UILoopTest {

    /**
     * How long to wait before concluding that the loop is not going to do something.
     */
    private static final long NOT_COMING_MILLIS = 250;

    /**
     * How long to give a thread that should be finishing.
     */
    private static final long SHOULD_FINISH_MILLIS = 5_000;
    /**
     * Whatever the loop threw, if anything - the loop's failures happen on another thread, so they
     * have to be caught there and re-examined here.
     */
    private final AtomicReference<Throwable> uiFailure = new AtomicReference<>();
    /**
     * A fresh pair of queues per test, so nothing one test leaves unread reaches the next.
     */
    private Channels channels;
    /**
     * The loop under test, running on its own thread; joined and cleaned up after each test.
     */
    private Thread uiThread;

    /**
     * Skips a test on a machine with no display, where no {@code JFrame} can be built.
     */
    private static void requireADisplay() {
        assumeFalse(GraphicsEnvironment.isHeadless(),
                "needs a display: constructing SwingUI builds a JFrame");
    }

    @BeforeEach
    void setUp() {
        channels = Channels.create();
    }

    /**
     * Stops the loop's thread however the test left it, so a test that fails mid-handshake does
     * not leave a thread blocked on a queue for the rest of the run.
     */
    @AfterEach
    void tearDown() throws InterruptedException {
        if (uiThread != null && uiThread.isAlive()) {
            uiThread.interrupt();
            uiThread.join(SHOULD_FINISH_MILLIS);
        }
    }

    /**
     * Starts the loop on its own thread, catching anything it throws.
     *
     * @param swingUI the front end the loop paints and closes through
     * @return the running thread
     * @author Rowan Crowther
     */
    private Thread startLoop(SwingUI swingUI) {
        UILoop loop = new UILoop(channels.uiChannel(), swingUI);

        uiThread = new Thread(loop::loop, "angband-display-under-test");
        uiThread.setUncaughtExceptionHandler((thread, thrown) -> uiFailure.set(thrown));
        uiThread.start();

        return uiThread;
    }

    /**
     * The EDT's part: post a close request the way the window listener does.
     */
    private void clickTheCloseButton() {
        channels.edtChannel().edtSender().send(new UIMessage.WindowCloseRequested());
    }

    /**
     * The core's part: reply that it has finished.
     */
    private void coreReportsStopped() {
        channels.coreChannel().coreSender()
                .send(new CoreMessage.LifecycleCoreMessage(CoreLifecycleEvent.STOPPED));
    }

    /**
     * A front end that records the shutdown call instead of performing it.
     *
     * <p>Subclassing rather than standing in for {@code SwingUI} wholesale is what keeps this
     * honest: the loop calls the real method signature on a real instance, so a rename or a
     * changed contract breaks the test rather than sliding past it. The constructor still builds a
     * window, which is why every user of this class checks for a display first.
     *
     * @author Rowan Crowther
     */
    private static class RecordingSwingUI extends SwingUI {

        /**
         * Released once the disposal has run, since it happens on the EDT and not inline.
         */
        private final CountDownLatch closed = new CountDownLatch(1);

        /**
         * Whether the disposal ran on the event dispatch thread, as Swing requires.
         */
        private final AtomicBoolean closedOnTheEdt = new AtomicBoolean();

        RecordingSwingUI() {
            super(null, null, null);
        }

        @Override
        public void closeDown() {
            closedOnTheEdt.set(SwingUtilities.isEventDispatchThread());
            closed.countDown();
        }

        /**
         * @return whether the windows were disposed within the timeout
         */
        boolean awaitClose() throws InterruptedException {
            return closed.await(SHOULD_FINISH_MILLIS, TimeUnit.MILLISECONDS);
        }
    }

    /**
     * The one job the EDT is not allowed to do itself.
     *
     * @author Rowan Crowther
     */
    @Nested
    class TranslatingWindowEvents {

        /**
         * A close click becomes a save-and-stop request to the core. This is the whole reason the
         * EDT posts a message rather than acting: the decision is taken here, on a thread that is
         * allowed to wait for an answer.
         *
         * @author Rowan Crowther
         */
        @Test
        void aCloseRequestBecomesSaveAndStop() throws Exception {
            startLoop(null);

            clickTheCloseButton();

            assertEquals(new UIMessage.LifecycleUIMessage(UILifecycleEvent.SAVE_AND_STOP),
                    channels.coreChannel().coreReceiver().receive());
        }

        /**
         * The request goes to the core's inbox, not back onto the loop's own. Sending it to the
         * wrong queue would be a loop that reads its own message, translates it again, and spins -
         * so this is worth pinning even though the previous test would also fail.
         *
         * @author Rowan Crowther
         */
        @Test
        void theRequestDoesNotComeBackRound() throws Exception {
            startLoop(null);

            clickTheCloseButton();
            channels.coreChannel().coreReceiver().receive();

            Thread.sleep(NOT_COMING_MILLIS);

            assertNull(uiFailure.get(), "the loop saw its own message come back");
            assertTrue(uiThread.isAlive(), "the loop should still be waiting for the core's reply");
        }

        /**
         * Translating is not stopping. The loop must go on serving the core until the core says it
         * has finished - the window stays up and painting while the save runs.
         *
         * @author Rowan Crowther
         */
        @Test
        void theLoopKeepsRunningAfterTheRequest() throws Exception {
            startLoop(null);

            clickTheCloseButton();
            channels.coreChannel().coreReceiver().receive();

            assertTrue(uiThread.isAlive(), "the loop stopped before the core had replied");
        }
    }

    /**
     * The end of the handshake, which is also the end of the thread.
     *
     * @author Rowan Crowther
     */
    @Nested
    class EndingOnStopped {

        /**
         * {@code STOPPED} disposes the windows and ends the loop. Both halves matter: a loop that
         * disposed but kept running would leave a non-daemon thread holding the JVM open with
         * nothing on screen, which looks exactly like a hang.
         *
         * @author Rowan Crowther
         */
        @Test
        void stoppedDisposesTheWindowsAndEndsTheLoop() throws Exception {
            requireADisplay();
            RecordingSwingUI swingUI = new RecordingSwingUI();

            Thread ui = startLoop(swingUI);
            coreReportsStopped();

            assertTrue(swingUI.awaitClose(), "the windows were never disposed");
            ui.join(SHOULD_FINISH_MILLIS);

            assertFalse(ui.isAlive(), "the loop did not end after the core reported STOPPED");
            assertNull(uiFailure.get(), "the loop failed on its way out");
        }

        /**
         * The disposal happens on the event dispatch thread. It is a Swing call made from a thread
         * that is not the EDT, so the {@code invokeLater} is not a nicety - without it this is a
         * race that will mostly work, which is the worst kind.
         *
         * @author Rowan Crowther
         */
        @Test
        void theDisposalHappensOnTheEdt() throws Exception {
            requireADisplay();
            RecordingSwingUI swingUI = new RecordingSwingUI();

            startLoop(swingUI);
            coreReportsStopped();

            assertTrue(swingUI.awaitClose(), "the windows were never disposed");
            assertTrue(swingUI.closedOnTheEdt.get(),
                    "closeDown must be hopped onto the EDT, not called on the UI thread");
        }

        /**
         * The whole exchange in the order it really happens: the player clicks, the loop asks, a
         * stand-in core answers, the loop closes down and finishes.
         *
         * <p>This is the test that would catch the handshake being wired up backwards - each half
         * of it can pass alone while the two do not meet.
         *
         * @author Rowan Crowther
         */
        @Test
        void theFullHandshakeFromClickToClosedWindow() throws Exception {
            requireADisplay();
            RecordingSwingUI swingUI = new RecordingSwingUI();

            Thread ui = startLoop(swingUI);
            clickTheCloseButton();

            UIMessage request = channels.coreChannel().coreReceiver().receive();
            assertEquals(new UIMessage.LifecycleUIMessage(UILifecycleEvent.SAVE_AND_STOP), request);

            coreReportsStopped();

            assertTrue(swingUI.awaitClose(), "the windows were never disposed");
            ui.join(SHOULD_FINISH_MILLIS);
            assertFalse(ui.isAlive(), "the UI thread outlived the handshake");
        }

        /**
         * The core may say {@code STOPPED} without having been asked - a fatal error core-side
         * would arrive that way. The loop shuts down all the same rather than waiting for a
         * request it will never see.
         *
         * @author Rowan Crowther
         */
        @Test
        void stoppedIsObeyedEvenWithoutACloseRequest() throws Exception {
            requireADisplay();
            RecordingSwingUI swingUI = new RecordingSwingUI();

            Thread ui = startLoop(swingUI);
            coreReportsStopped();
            ui.join(SHOULD_FINISH_MILLIS);

            assertFalse(ui.isAlive(), "an unprompted STOPPED should still end the loop");
        }
    }

    /**
     * The paths that are not a shutdown, kept distinct from the paths that are.
     *
     * @author Rowan Crowther
     */
    @Nested
    class FailureAndOtherTraffic {

        /**
         * Game traffic does not end the loop. Obvious, and worth a test only because the switch
         * gained two arms that do: a mis-placed {@code return} would turn the first painted
         * message into a shutdown.
         *
         * @author Rowan Crowther
         */
        @Test
        void ordinaryCoreTrafficLeavesTheLoopRunning() throws Exception {
            startLoop(null);

            channels.coreChannel().coreSender()
                    .send(new CoreMessage.TextCoreMessage(GameEventType.EVENT_INITSTATUS, "note"));
            Thread.sleep(NOT_COMING_MILLIS);

            assertTrue(uiThread.isAlive(), "a progress note must not end the loop");
        }

        /**
         * A UI message that is not the close request has been posted to the wrong queue, and the
         * loop says so loudly. Only this half sends {@code UIMessage}s, so one arriving here is a
         * wiring mistake, and nothing else reads this queue - stay quiet and the message is simply
         * gone.
         *
         * @author Rowan Crowther
         */
        @Test
        void aMisroutedUiMessageIsRejected() throws Exception {
            startLoop(null);

            channels.edtChannel().edtSender()
                    .send(new UIMessage.LifecycleUIMessage(UILifecycleEvent.START));
            uiThread.join(SHOULD_FINISH_MILLIS);

            assertFalse(uiThread.isAlive(), "the loop carried on after a misrouted message");
            assertInstanceOf(RuntimeException.class, uiFailure.get(),
                    "a message on the wrong queue must not be swallowed");
        }

        /**
         * An interrupt ends the loop, but is not a shutdown: nothing interrupts this thread in
         * normal running, so reaching that path means something has gone wrong, and the windows
         * are deliberately left alone rather than being disposed as though the core had finished
         * cleanly.
         *
         * @author Rowan Crowther
         */
        @Test
        void anInterruptEndsTheLoopWithoutDisposingAnything() throws Exception {
            requireADisplay();
            RecordingSwingUI swingUI = new RecordingSwingUI();

            Thread ui = startLoop(swingUI);
            Thread.sleep(NOT_COMING_MILLIS);
            ui.interrupt();
            ui.join(SHOULD_FINISH_MILLIS);

            assertFalse(ui.isAlive(), "an interrupted loop should end");
            assertFalse(swingUI.closed.await(NOT_COMING_MILLIS, TimeUnit.MILLISECONDS),
                    "an interrupt is a failure, not a clean shutdown: nothing should be disposed");
        }
    }
}