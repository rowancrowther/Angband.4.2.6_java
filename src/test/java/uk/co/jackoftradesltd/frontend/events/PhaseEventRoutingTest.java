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

package uk.co.jackoftradesltd.frontend.events;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import uk.co.jackoftradesltd.channel.Channels;
import uk.co.jackoftradesltd.channel.enums.GameEventType;
import uk.co.jackoftradesltd.channel.messages.CoreMessage;
import uk.co.jackoftradesltd.frontend.inputfromuser.UILoop;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Stage 5's split, end to end on the UI side: a phase event put on the core channel reaches the
 * receiver that stands for it, having crossed a real queue on a real thread.
 *
 * <p><b>Why this is separate from {@code MainEventsTest}.</b> That class asks whether
 * {@code enterWorld()} announces the right event; this asks whether {@code EVENT_ENTER_WORLD}
 * <em>arrives at</em> {@code enterWorld()}. They are different bugs. {@code UILoop}'s switch has
 * seven near-identical arms wiring one to the other, and a pair swapped there passes every test in
 * the other class - the methods are individually perfect and the phone lines are crossed.
 *
 * <p><b>This is the test the migration document's checkbox is really asking for.</b> Its stated
 * form is a {@code grep} - {@code GameEventType} appears on the UI side, Swing does not appear on
 * the core side - and a grep is a habit rather than a regression test. Driving the real loop turns
 * the same claim into something the build can fail on.
 *
 * <p><b>The front end is deliberately {@code null}.</b> None of the seven arms under test touches
 * it, so a {@code null} is not a gap in the fixture but an assertion in its own right: an arm that
 * started painting through {@code SwingUI} would fail here loudly, and painting is what the core
 * half is not allowed to cause directly. It also keeps this test running on a headless machine,
 * unlike the {@code SwingUI} half of {@code UILoopTest}.
 *
 * <p>{@code EVENT_ENTER_INIT} is not covered: it is the one arm that does paint, so it belongs with
 * the splash screen's tests rather than here.
 *
 * @author Rowan Crowther
 */
@Timeout(value = 10, unit = TimeUnit.SECONDS)
class PhaseEventRoutingTest {

    /**
     * How long to give a message to cross the queue and be handled.
     */
    private static final long SHOULD_ARRIVE_MILLIS = 5_000;

    /**
     * Whatever the loop threw, if anything: it runs on another thread, so failures have to be
     * caught there and re-examined here.
     */
    private final AtomicReference<Throwable> uiFailure = new AtomicReference<>();

    private Channels channels;

    private Thread uiThread;

    private RecordingLog mainLog;

    private RecordingLog birthLog;

    @BeforeEach
    void setUp() {
        channels = Channels.create();
        mainLog = RecordingLog.attachedTo(MainEvents.class);
        birthLog = RecordingLog.attachedTo(BirthEvents.class);

        UILoop loop = new UILoop(channels.uiChannel(), null);

        uiThread = new Thread(loop::loop, "angband-ui-under-test");
        uiThread.setUncaughtExceptionHandler((thread, thrown) -> uiFailure.set(thrown));
        uiThread.start();
    }

    /**
     * Ends the loop's thread and unhooks the recorders, so a failed test leaves neither a thread
     * blocked on a queue nor an appender collecting the next test's output.
     */
    @AfterEach
    void tearDown() throws InterruptedException {
        if (uiThread != null && uiThread.isAlive()) {
            uiThread.interrupt();
            uiThread.join(SHOULD_ARRIVE_MILLIS);
        }

        mainLog.detach();
        birthLog.detach();
    }

    /**
     * Puts a phase event on the core channel, the way {@code InitHandlers} does.
     *
     * @param event the transition the core is reporting
     */
    private void coreReports(GameEventType event) {
        channels.coreChannel().coreSender().send(new CoreMessage.SimpleCoreMessage(event));
    }

    /**
     * Each of the five phase transitions reaches {@code MainEvents}, and specifically reaches the
     * method that stands for it.
     *
     * <p>Parameterised over the events rather than written out five times because the failure this
     * catches is a mis-wired arm, and a mis-wired arm is only visible when every arm is exercised
     * the same way.
     */
    @ParameterizedTest
    @EnumSource(value = GameEventType.class, names = {
            "EVENT_LEAVE_INIT", "EVENT_ENTER_GAME", "EVENT_LEAVE_GAME",
            "EVENT_ENTER_WORLD", "EVENT_LEAVE_WORLD"})
    void aPhaseEventReachesItsReceiver(GameEventType event) throws Exception {
        coreReports(event);

        assertTrue(mainLog.await("Executing " + event, SHOULD_ARRIVE_MILLIS),
                event + " never reached MainEvents: the UILoop arm is missing or mis-wired");
        assertNull(uiFailure.get(), "the loop failed while handling " + event);
    }

    /**
     * The birth pair reaches {@code BirthEvents} rather than {@code MainEvents}. The two receivers
     * are the port's version of C's split between {@code ui-birth.c} and {@code ui-display.c}, and
     * this is what stops that split existing only in the file names.
     */
    @ParameterizedTest
    @EnumSource(value = GameEventType.class, names = {"EVENT_ENTER_BIRTH", "EVENT_LEAVE_BIRTH"})
    void aBirthEventReachesTheBirthReceiver(GameEventType event) throws Exception {
        coreReports(event);

        assertTrue(birthLog.await("Executing " + event, SHOULD_ARRIVE_MILLIS),
                event + " never reached BirthEvents");
        assertTrue(mainLog.lines().isEmpty(),
                event + " should not also be announced by MainEvents");
    }

    /**
     * A phase event does not end the loop. Obvious, and worth stating because the same switch has
     * an arm that does end it: a {@code return} pasted into a phase arm would turn the first
     * transition of a session into a shutdown, and the game would close as the data load finished.
     */
    @Test
    void aPhaseEventLeavesTheLoopRunning() throws Exception {
        coreReports(GameEventType.EVENT_ENTER_WORLD);

        assertTrue(mainLog.await("Executing " + GameEventType.EVENT_ENTER_WORLD,
                SHOULD_ARRIVE_MILLIS), "the event never arrived");
        assertTrue(uiThread.isAlive(), "a phase transition must not end the loop");
    }

    /**
     * All seven in the order a session really produces them, on one loop. The individual tests each
     * start a fresh loop, which hides anything that only goes wrong the second time - a receiver
     * that cannot be built twice, or state left behind by the previous transition.
     */
    @Test
    void aWholeSessionOfTransitionsArrivesInOrder() throws Exception {
        GameEventType[] session = {
                GameEventType.EVENT_LEAVE_INIT,
                GameEventType.EVENT_ENTER_BIRTH,
                GameEventType.EVENT_LEAVE_BIRTH,
                GameEventType.EVENT_ENTER_GAME,
                GameEventType.EVENT_ENTER_WORLD,
                GameEventType.EVENT_LEAVE_WORLD,
                GameEventType.EVENT_LEAVE_GAME};

        for (GameEventType event : session) {
            coreReports(event);
        }

        assertTrue(mainLog.await("Executing " + GameEventType.EVENT_LEAVE_GAME,
                SHOULD_ARRIVE_MILLIS), "the session's last transition never arrived");

        assertEquals(List.of(
                        "Executing " + GameEventType.EVENT_LEAVE_INIT,
                        "Executing " + GameEventType.EVENT_ENTER_GAME,
                        "Executing " + GameEventType.EVENT_ENTER_WORLD,
                        "Executing " + GameEventType.EVENT_LEAVE_WORLD,
                        "Executing " + GameEventType.EVENT_LEAVE_GAME),
                mainLog.lines(),
                "the phase transitions should arrive in the order the core sent them");

        assertEquals(List.of(
                        "Executing " + GameEventType.EVENT_ENTER_BIRTH,
                        "Executing " + GameEventType.EVENT_LEAVE_BIRTH),
                birthLog.lines(),
                "the birth transitions should arrive in order, and only on the birth receiver");

        assertNull(uiFailure.get(), "the loop failed part-way through the session");
    }
}
