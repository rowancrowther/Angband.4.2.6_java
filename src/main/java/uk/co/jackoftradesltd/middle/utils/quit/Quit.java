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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Default {@link QuitAux} implementation: how the game terminates when nothing has
 * substituted a shutdown behaviour of its own. It stands in for the body of C's
 * {@code quit()} ({@code z-util.c}) - log the message, then end the process.
 *
 * <p>It is not C's {@code quit_aux} itself. In C that is a hook which defaults to
 * {@code NULL}, and {@code main.c} points it at {@code quit_hook()}, which nukes the
 * terminal windows before {@code quit()} carries on. The port has no terminal to restore,
 * so nothing here corresponds to {@code quit_hook()}; closing the windows belongs to
 * the front end, on the shutdown path described below.
 *
 * <p>Like C, the two kinds of quit are told apart by whether there is a message at all.
 * A {@code null} message is the clean quit ({@code quit(NULL)}: nothing logged, exit with
 * success). Any string, the empty string included, is a failure ({@code plog()} the
 * string, exit with failure).
 *
 * <p>Unlike C, this does not end the process itself. The game runs as two threads,
 * {@code angband-ui} and {@code angband-core}, so {@link #quit(String)} logs the message
 * and throws {@link GameQuitException} instead, and lets it surface as an uncaught
 * exception on whichever thread called it. Each half already has a path for that: the
 * {@code catch} in {@code Main.startSwingUI} for the UI half, and the core thread's
 * uncaught-exception handler in {@code Main.main}. Both read the exception's message to
 * tell a clean quit from a failure - no message means no {@code died} log and exit
 * status 0, a message means a fatal log and exit status -1 - then post their shutdown
 * message and call {@code System.exit} themselves.
 *
 * <p>Outstanding: both handlers in {@code Main} send the shutdown message and then exit
 * straight away, so nothing waits for the other half's reply, and the UI half's
 * {@code finally} (which disposes the windows) never runs on that path. They also
 * identify a clean quit by {@code getMessage() == null} rather than by the type
 * {@link GameQuitException}, so any other {@link RuntimeException} thrown without a
 * message would be read as a clean quit.
 *
 * <p>coded before 2026-09-09, clean-quit path added 2026-10-15 / commented in full on 2026-10-15
 *
 * @author Rowan Crowther
 */
public class Quit implements QuitAux {
    /**
     * Logger used to record a failure quit's message at fatal severity before the exception
     * is thrown. Used directly rather than through {@code ControlUtils.plog}, which logs at
     * warn; C's {@code plog_aux} hook is not consulted.
     *
     * <p>coded before 2026-09-09 / commented in full on 2026-10-15
     */
    private final Logger logger = LogManager.getLogger();

    /**
     * Ends the game, logging {@code quitMessage} first if there is one - the port of C's
     * {@code quit()} ({@code z-util.c}), with the process exit replaced by an exception
     * (see the class Javadoc for why).
     *
     * <p>The two cases C distinguishes map as follows:
     * <ul>
     *   <li>{@code null} - C's {@code quit(NULL)}: nothing is logged and the exit is a
     *       success. Here nothing is logged and {@link GameQuitException} is thrown with no
     *       message.</li>
     *   <li>any string - C's {@code plog(str)} followed by a failure exit. Here the string is
     *       logged at fatal severity and {@link GameQuitException} is thrown carrying it.
     *       The empty string is a failure, as it is in C, not a clean quit.</li>
     * </ul>
     *
     * <p>coded before 2026-09-09, clean-quit branch added 2026-10-15 / commented in full on 2026-10-15
     *
     * @param quitMessage the message to log and carry on the exception, or {@code null} for a
     *                    clean quit
     * @throws GameQuitException always; this method does not return
     */
    @Override
    public void quit(String quitMessage) {
        if (quitMessage != null) {
            logger.fatal(quitMessage);
            throw new GameQuitException(quitMessage);
        } else 
            throw new GameQuitException(null);
    }

    /**
     * Thrown by {@link #quit(String)} in place of C's {@code exit()}, as an unchecked
     * exception so it can surface through each half's shutdown path on whichever thread
     * called {@link #quit(String)} (see the class Javadoc).
     *
     * <p>Its message is the whole signal. A {@code null} message means the quit was clean
     * ({@code quit(NULL)} in C); any other message, empty included, means a failure and is
     * the text {@link #quit(String)} already logged. The handlers in {@code Main} read it
     * with {@code getMessage()}.
     *
     * <p>Unchecked because a quit is not a condition {@link #quit(String)}'s callers should
     * have to declare or catch - it is meant to be left alone and let the shutdown path
     * deal with it.
     *
     * <p>coded before 2026-09-09, clean-quit meaning added 2026-10-15 / commented in full on 2026-10-15
     *
     * @author Rowan Crowther
     */
    public static class GameQuitException extends RuntimeException {
        /**
         * @param message the failure message {@link #quit(String)} already logged, or
         *                {@code null} for a clean quit; passed to the superclass unchanged so
         *                {@code getMessage()} returns exactly what was given
         */
        public GameQuitException(String message) {
            super(message);
        }
    }
}
