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

package uk.co.jackoftradesltd.middle.utils.quit;

/**
 * Strategy interface for terminating the game, mirroring the swappable
 * {@code quit_aux} function pointer in the original C source ({@code z-util.c}).
 * Abstracting the quit behaviour behind an interface lets the front end decide what
 * "quit" actually does (clean exit, save-and-exit, error abort, …) without the core
 * game code needing to know.
 *
 * <p>Nothing swaps it yet: {@code ControlUtils} constructs {@link Quit} directly at each
 * call, so there is no counterpart to C assigning {@code quit_aux} at start-up.
 *
 * <p>coded before 2026-09-09 / commented in full on 2026-10-15
 *
 * @author Rowan Crowther
 */
public interface QuitAux {

    /**
     * Ends the game - the contract of the function C's {@code quit()} calls through
     * {@code quit_aux}. In C the hook runs first and {@code quit()} then logs and exits;
     * here an implementation is the whole quit, and is expected not to return.
     *
     * <p>A {@code null} message is the clean quit and any string, the empty string included,
     * is a failure, as in C. See {@link Quit#quit(String)} for how the default implementation
     * handles each.
     *
     * <p>coded before 2026-09-09 / commented in full on 2026-10-15
     *
     * @param quitMessage the failure message to report as the game quits, or {@code null} for
     *                    a clean quit
     */
    void quit(String quitMessage);
}
