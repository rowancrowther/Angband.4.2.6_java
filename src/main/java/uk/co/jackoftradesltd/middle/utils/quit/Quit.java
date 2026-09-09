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
import org.jetbrains.annotations.NotNull;

/**
 * Default {@link QuitAux} implementation: the standard way the game terminates.
 * It corresponds to the C original's {@code quit_aux} hook ({@code src/z-util.c}),
 * which lets the front end substitute its own shutdown behaviour.
 *
 * <p>Unlike C, this does not end the process itself. The game runs as two threads,
 * {@code angband-ui} and {@code angband-core}, that only end cleanly through a
 * channel handshake - the core answering a shutdown request with {@code STOPPED},
 * which is what lets the front end dispose its windows and lets the save (once there
 * is one) finish before the JVM does. A bare {@code System.exit} here would cut
 * straight through that, on whichever thread happened to call {@link #quit(String)}.
 * So {@link #quit(String)} logs the message and throws {@link GameQuitException}
 * instead, and lets it surface as an uncaught exception - both halves already have a
 * crash path that turns an uncaught exception on their own thread into the same
 * shutdown the handshake produces, so quitting this way rides that path rather than
 * bypassing it.
 *
 * <p>Class Quit commented in full on 260909.
 *
 * @author Rowan Crowther
 */
public class Quit implements QuitAux {
    /**
     * Logger used to record the quit message before (eventually) exiting.
     */
    private final Logger logger = LogManager.getLogger();

    /**
     * Standard quit - logs {@code quitMessage} at fatal severity, then throws
     * {@link GameQuitException} carrying the same message rather than ending the JVM
     * directly (see the class Javadoc for why).
     *
     * <p>Function quit commented in full on 260909.
     *
     * @param quitMessage the message to log and to carry on the thrown exception
     * @throws GameQuitException always; this method does not return
     */
    @Override
    public void quit(@NotNull String quitMessage) {
        logger.fatal(quitMessage);

        throw new GameQuitException(quitMessage);
    }

    /**
     * Thrown by {@link #quit(String)} in place of ending the JVM directly - the fatal
     * quit C would have carried out as {@code System.exit}, now an unchecked exception
     * so it can surface through the ordinary crash path on whichever thread called
     * {@link #quit(String)} instead of cutting through the shutdown handshake between
     * {@code angband-ui} and {@code angband-core} (see the class Javadoc).
     *
     * <p>Unchecked because a fatal quit is not a condition {@link #quit(String)}'s
     * callers should have to declare or catch - it is meant to be left alone and let
     * the crash path deal with it.
     *
     * <p>Class GameQuitException commented in full on 260909.
     *
     * @author Rowan Crowther
     */
    public static class GameQuitException extends RuntimeException {
        /**
         * @param message the quit message, the same one {@link #quit(String)} already logged
         */
        public GameQuitException(String message) {
            super(message);
        }
    }
}
