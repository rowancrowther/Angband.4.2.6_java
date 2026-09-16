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

import uk.co.jackoftradesltd.frontend.screen.enums.TermXtraEventEnum;

/**
 * A {@link TermEventHook} implementation dedicated to delay handling only, the Java
 * counterpart to C's {@code Term_xtra_win_delay(int v)} ({@code [C] src/main-win.c}),
 * the handler {@code Term_xtra_win} dispatches to for {@code TERM_XTRA_DELAY}. Every
 * other {@link TermXtraEventEnum} value passed to {@link #doSomething} is ignored -
 * this hook only ever performs the delay action.
 *
 * <p>Class TermXtraWinDelay coded on 260916, commented in full on 260916.
 *
 * @author Rowan Crowther
 */
public class TermXtraWinDelay implements TermEventHook {

    /**
     * Pause the calling thread for {@code value} milliseconds. The C original
     * guards its call to {@code Sleep(v)} with {@code if (v > 0)}; here a negative
     * {@code value} returns immediately without sleeping, matching that guard,
     * while {@code value == 0} falls through to {@link Thread#sleep(long)}, itself
     * a near no-op. An {@link InterruptedException} raised mid-sleep is caught and
     * its interrupt status is restored via {@link Thread#interrupt()} rather than
     * swallowed, so a caller further up the stack can still observe the interrupt -
     * C has no equivalent concept, since {@code Sleep()} on Windows isn't
     * interruptible the same way. {@code event} itself is never consulted.
     *
     * <p>Function doSomething coded on 260916, commented in full on 260916.
     *
     * @param event the requested action (ignored; this hook always delays)
     * @param value the delay in milliseconds; non-positive values are a no-op
     */
    @Override
    public void doSomething(TermXtraEventEnum event, int value) {
        if (value < 0) return;

        try {
            Thread.sleep((long) value);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
