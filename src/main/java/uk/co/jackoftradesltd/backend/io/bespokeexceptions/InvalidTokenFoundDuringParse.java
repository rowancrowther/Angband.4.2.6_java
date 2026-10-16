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

package uk.co.jackoftradesltd.backend.io.bespokeexceptions;

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
 * <p>This class has no single C counterpart. It stands in for the fatal path
 * of {@code datafile.c}: in {@code run_parser()} a non-zero result from the
 * parser's run step goes to {@code print_error()}, which prints the file name,
 * line, column, message and the {@code parser_error_str} description (the
 * {@code PARSE_ERROR_*} codes in {@code list-parser-errors.h}) and then calls
 * {@code quit_fmt()}; a failure in the finish step does the same with "Parser
 * finish error". C never returns from either, so the exception is the Java
 * equivalent of that {@code quit_fmt()} call. Java carries only a free-text
 * message, not a {@code PARSE_ERROR_*} code, line or column, so a thrower must
 * put whatever location and reason it has into the message.
 *
 * <p>Because the constructor logs the message at error level, a thrower does
 * not need to log it as well.
 *
 * <p>Class InvalidTokenFoundDuringParse coded before 261016, commented in full on 261016.
 *
 * @author Rowan Crowther
 */
public class InvalidTokenFoundDuringParse extends RuntimeException {
    /**
     * Logger used to record the failure as soon as the exception is built. It
     * is the Java counterpart of the {@code msg()} call in C's
     * {@code print_error()}, which reports the parse error before
     * {@code quit_fmt()} ends the program. It is an instance field, so each
     * exception obtains its own logger reference from {@link LogManager}.
     *
     * <p>Field logger coded before 261016, commented in full on 261016.
     */
    private final Logger logger = LogManager.getLogger();

    /**
     * Build the exception, logging the supplied message immediately. The
     * message is handed to {@link RuntimeException} unchanged, so
     * {@link #getMessage()} returns exactly what the thrower supplied.
     *
     * <p>Constructor InvalidTokenFoundDuringParse coded before 261016, commented in full on 261016.
     *
     * @param message description of the offending token / parse failure
     */
    public InvalidTokenFoundDuringParse(String message) {
        super(message);
        logger.error(message);
    }
}
