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

package uk.co.jackoftrades.frontend.screen.hooks;

import uk.co.jackoftrades.frontend.screen.enums.TermXtraEventEnum;

/**
 * Callback contract for handling a terminal "extra" event. This is the Java port
 * of the C original's {@code Term_xtra} hook ({@code src/z-term.c}), the single
 * entry point through which the core asks the front end to perform
 * platform-specific actions (make a noise, flush input, clear, delay, …). Each
 * front end provides its own implementation.
 *
 * @author ClaudeCode
 */
public interface TermEventHook {
    /**
     * Perform the action associated with the given terminal event.
     *
     * @param event the kind of action requested
     * @param value an event-specific parameter (e.g. a delay in milliseconds)
     * @author ClaudeCode
     */
    public void doSomething(TermXtraEventEnum event, int value);
}
