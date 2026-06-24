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

package uk.co.jackoftrades.backend.utils.quit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

/**
 * Default {@link QuitAux} implementation: the standard way the game terminates.
 * It corresponds to the C original's {@code quit_aux} hook ({@code src/z-util.c}),
 * which lets the front end substitute its own shutdown behaviour. The actual
 * {@code System.exit} is currently commented out so that the partially-ported
 * game does not kill the JVM during development/testing.
 *
 * @author ClaudeCode
 */
public class Quit implements QuitAux {
    /**
     * Logger used to record the quit message before (eventually) exiting.
     *
     * @author ClaudeCode
     */
    private final Logger logger = LogManager.getLogger();

    /**
     * Standard quit - writes a message to the logger.info file and stops execution
     * @param quitMessage The message to write to the log file
     */
    @Override
    public void quit(@NotNull String quitMessage) {
        logger.info(quitMessage);
        // TODO: Remove comment on next line once this is totally tested
        // System.exit(0);
    }
}
