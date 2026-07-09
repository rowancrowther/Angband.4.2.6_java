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

package uk.co.jackoftrades.backend.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftrades.backend.utils.quit.Quit;

/**
 * Static control/messaging helpers ported from the original C source's
 * {@code plog}/{@code quit} family ({@code src/z-util.c}). These give the rest
 * of the game a single place to emit player-visible log warnings and to abort
 * the program, so the underlying logging/exit mechanism can be swapped without
 * touching call sites. Abstract because it is a pure utility holder and should
 * never be instantiated.
 *
 * @author Rowan Crowther
 */
public abstract class ControlUtils {
    /**
     * Shared logger backing the {@code plog}/{@code plogFmt} helpers.
     *
     * @author Rowan Crowther
     */
    private final static Logger logger = LogManager.getLogger();

    /**
     * Log a formatted string with no objects (a straight string) at level INFO
     * @param string the string to log
     */
    public static void plogFmt(@NotNull String string) {
        logger.warn(string);
    }

    /**
     * Log a formatted string at level INFO
     * @param format The string to format
     * @param objects The objects to format it with
     */
    public static void plogFmt(@NotNull String format, Object... objects) {
        logger.warn(format, objects);
    }

    /**
     * Log a warning message to the log file
     * @param string The warning message to log
     */
    public static void plog(String string) { logger.warn(string); }

    /**
     * Quit the program using a formatted quit string
     * @param toFormat The string to format
     * @param objects The objects used to format this string
     */
    public static void quitFmt(String toFormat, Object... objects) {
        Quit q = new Quit();
        q.quit(String.format(toFormat, objects));
    }

    /**
     * Quit the program using a blank quit string
     */
    public static void quit() {
        Quit q = new Quit();
        q.quit("");
    }

    /**
     * Quit the program using a straight string
     * @param string THe string message to give out
     */
    public static void quit(String string) {
        Quit q = new Quit();
        q.quit(string);
    }
}