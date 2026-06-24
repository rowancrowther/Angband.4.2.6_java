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

package uk.co.jackoftrades.backend.io.bespokeexceptions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Unchecked exception thrown when one of the data-file parsers encounters a
 * token it cannot interpret. It is a {@link RuntimeException} because a
 * malformed game data file is unrecoverable at load time and should abort
 * parsing rather than force every call site to handle it; the message is also
 * logged on construction so the failure is recorded even if the stack trace is
 * swallowed higher up.
 *
 * @author ClaudeCode
 */
public class InvalidTokenFoundDuringParse extends RuntimeException {
    /**
     * Logger used to record the failure as soon as the exception is built.
     *
     * @author ClaudeCode
     */
    private final Logger logger = LogManager.getLogger();

    /**
     * Build the exception, logging the supplied message immediately.
     *
     * @param message description of the offending token / parse failure
     * @author ClaudeCode
     */
    public InvalidTokenFoundDuringParse(String message) {
        super(message);
        logger.error(message);
    }
}
