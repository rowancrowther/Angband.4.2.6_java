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

package uk.co.jackoftradesltd.frontend.screen.hooks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import uk.co.jackoftradesltd.frontend.screen.enums.TermXtraEventEnum;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link TermXtraWinDelay}, exercising the guard C's {@code Term_xtra_win_delay(int v)}
 * ({@code [C] src/main-win.c}) applies with {@code if (v > 0) Sleep(v)} - a positive
 * {@code value} must actually pause the calling thread, while zero or negative values
 * must return without sleeping - plus the restored-interrupt-status behaviour that has
 * no C counterpart.
 *
 * @author Rowan Crowther
 */
@Timeout(value = 30, unit = TimeUnit.SECONDS)
class TermXtraWinDelayTest {

    private TermXtraWinDelay hook;

    @BeforeEach
    void buildAHook() {
        hook = new TermXtraWinDelay();
    }

    /**
     * A positive {@code value} sleeps the calling thread for at least that many
     * milliseconds, matching C's {@code Sleep(v)} under its {@code v > 0} guard.
     */
    @Test
    void positiveValueSleepsForAtLeastThatManyMilliseconds() {
        long start = System.nanoTime();

        hook.doSomething(TermXtraEventEnum.TERM_XTRA_DELAY, 100);

        long elapsedMillis = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
        assertTrue(elapsedMillis >= 100, "expected at least a 100ms pause, took " + elapsedMillis + "ms");
    }

    /**
     * {@code value == 0} does not hit C's {@code v > 0} guard, so C skips the sleep
     * entirely; the Java port instead falls through to {@code Thread.sleep(0)}, which
     * must return near-instantly rather than pausing for any meaningful duration.
     */
    @Test
    void zeroValueReturnsWithoutAnyMeaningfulDelay() {
        long start = System.nanoTime();

        hook.doSomething(TermXtraEventEnum.TERM_XTRA_DELAY, 0);

        long elapsedMillis = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
        assertTrue(elapsedMillis < 100, "expected no meaningful pause, took " + elapsedMillis + "ms");
    }

    /**
     * A negative {@code value} must be a no-op - the guard's Java-side counterpart to
     * C's {@code v > 0} check - rather than sleeping, or throwing
     * {@code IllegalArgumentException} out of {@link Thread#sleep(long)} as a
     * negative-argument call would.
     */
    @Test
    void negativeValueReturnsImmediatelyWithoutSleepingOrThrowing() {
        long start = System.nanoTime();

        hook.doSomething(TermXtraEventEnum.TERM_XTRA_DELAY, -1);

        long elapsedMillis = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
        assertTrue(elapsedMillis < 100, "expected no delay, took " + elapsedMillis + "ms");
    }

    /**
     * Interrupting the sleep must leave the calling thread's interrupt status set once
     * {@link TermXtraWinDelay#doSomething} returns, rather than swallowing it - C has no
     * equivalent, since {@code Sleep()} on Windows is not interruptible the same way.
     * The delay under test is deliberately long, so the interrupt (sent after the worker
     * has had time to enter the sleep) is what ends the wait, not the delay elapsing on
     * its own.
     */
    @Test
    void interruptedSleepLeavesTheThreadsInterruptStatusSet() throws InterruptedException {
        Thread worker = new Thread(() -> hook.doSomething(TermXtraEventEnum.TERM_XTRA_DELAY, 5_000));
        worker.start();
        Thread.sleep(100);
        worker.interrupt();
        worker.join(5_000);

        assertTrue(worker.isInterrupted(), "worker's interrupt status should have been restored, not swallowed");
    }
}
