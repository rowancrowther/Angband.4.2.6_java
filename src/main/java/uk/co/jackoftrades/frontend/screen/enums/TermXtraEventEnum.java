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
 *    Java code copyright (c) Rowan Crowther 2026
 */

package uk.co.jackoftrades.frontend.screen.enums;

/**
 * The set of "extra" terminal actions the core can request of a front end,
 * mirroring the {@code TERM_XTRA_*} constants of the C original's terminal layer
 * ({@code src/z-term.h}). These are the argument to {@link
 * uk.co.jackoftrades.frontend.screen.hooks.TermEventHook}.
 *
 * @author ClaudeCode
 */
public enum TermXtraEventEnum {
    /**
     * Process a pending input/windowing event. @author ClaudeCode
     */
    TERM_XTRA_EVENT,
    /** Flush all pending input. @author ClaudeCode */
    TERM_XTRA_FLUSH,
    /** Clear the whole terminal. @author ClaudeCode */
    TERM_XTRA_CLEAR,
    /** Change the cursor shape/visibility. @author ClaudeCode */
    TERM_XTRA_SHAPE,
    /** Flush a row of pending output to the display. @author ClaudeCode */
    TERM_XTRA_FROSH,
    /** Flush all pending output to the display. @author ClaudeCode */
    TERM_XTRA_FRESH,
    /** Make a noise (audible bell / sound effect). @author ClaudeCode */
    TERM_XTRA_NOISE,
    /** Notify the front end that the game is idle ("bored"). @author ClaudeCode */
    TERM_XTRA_BORED,
    /** React to a change in colours/preferences. @author ClaudeCode */
    TERM_XTRA_REACT,
    /** Check/assert that the terminal is still alive. @author ClaudeCode */
    TERM_XTRA_ALIVE,
    /** Notify of a level (display activation) change. @author ClaudeCode */
    TERM_XTRA_LEVEL,
    /** Delay for a number of milliseconds. @author ClaudeCode */
    TERM_XTRA_DELAY
}
