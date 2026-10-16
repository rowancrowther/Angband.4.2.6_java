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

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests {@link InvalidTokenFoundDuringParse}, the Java stand-in for the fatal path of C's
 * {@code datafile.c}: {@code run_parser()} sends a failed parse to {@code print_error()}, which
 * reports the problem with {@code msg()} and then ends the program with {@code quit_fmt()}.
 *
 * <p>C never returns from that path and never lets a caller recover, so the properties worth
 * pinning are: the exception is unchecked, so no caller is forced to handle it; it carries exactly
 * the text the thrower supplied (C's {@code quit_fmt()} message is not altered on the way out); and
 * the failure is logged once, at error level, when the exception is built rather than when it is
 * thrown, standing in for the {@code msg()} that precedes {@code quit_fmt()}.
 *
 * <p>The messages used are laid out like the ones C builds in {@code print_error()}
 * ({@code "Parse error in %s line %d column %d: %s: %s"}) with the description text taken from
 * {@code parser_error_str} / {@code list-parser-errors.h}.
 *
 * <p>Class InvalidTokenFoundDuringParseTest coded on 261016, commented in full on 261016.
 */
class InvalidTokenFoundDuringParseTest {

    /**
     * Message laid out as C's {@code print_error()} formats it, using the
     * {@code PARSE_ERROR_INVALID_FLAG} description from {@code list-parser-errors.h}.
     *
     * <p>Field C_STYLE_MESSAGE coded on 261016, commented in full on 261016.
     */
    private static final String C_STYLE_MESSAGE =
            "Parse error in monster.txt line 12 column 7: FOO: invalid flag";

    /**
     * Collects every event logged to the exception class's logger while a test runs.
     *
     * <p>Field events coded on 261016, commented in full on 261016.
     */
    private final List<LogEvent> events = new ArrayList<>();

    /**
     * Appender attached to the exception class's logger for the duration of one test.
     *
     * <p>Field appender coded on 261016, commented in full on 261016.
     */
    private AbstractAppender appender;

    /**
     * Logger the exception writes to; named after the exception class by {@code LogManager.getLogger()}.
     *
     * <p>Field coreLogger coded on 261016, commented in full on 261016.
     */
    private Logger coreLogger;

    /**
     * Attaches the capturing appender.
     *
     * <p>Method attach coded on 261016, commented in full on 261016.
     */
    @BeforeEach
    void attach() {
        events.clear();
        coreLogger = (Logger) LogManager.getLogger(InvalidTokenFoundDuringParse.class);
        appender = new AbstractAppender("capture", null, null, true, Property.EMPTY_ARRAY) {
            @Override
            public void append(LogEvent event) {
                events.add(event.toImmutable());
            }
        };
        appender.start();
        coreLogger.addAppender(appender);
    }

    /**
     * Detaches the capturing appender so other tests' logging is unaffected.
     *
     * <p>Method detach coded on 261016, commented in full on 261016.
     */
    @AfterEach
    void detach() {
        coreLogger.removeAppender(appender);
        appender.stop();
    }

    /**
     * A thrown exception carries the supplied text unchanged.
     *
     * <p>Method messageIsPreservedVerbatim coded on 261016, commented in full on 261016.
     */
    @Test
    void messageIsPreservedVerbatim() {
        InvalidTokenFoundDuringParse e = assertThrows(InvalidTokenFoundDuringParse.class, () -> {
            throw new InvalidTokenFoundDuringParse(C_STYLE_MESSAGE);
        });
        assertEquals(C_STYLE_MESSAGE, e.getMessage());
    }

    /**
     * Text with line breaks, leading and trailing whitespace, an empty string and non-ASCII
     * characters all pass through untouched.
     *
     * <p>Method awkwardMessagesAreNotAltered coded on 261016, commented in full on 261016.
     */
    @Test
    void awkwardMessagesAreNotAltered() {
        String[] messages = {"", " ", "  padded  ", "two\nlines", "tab\there", "déjà vu ☃"};
        for (String message : messages) {
            assertEquals(message, new InvalidTokenFoundDuringParse(message).getMessage(),
                    "message was altered: [" + message + "]");
        }
    }

    /**
     * C aborts and never hands the failure back, so the Java equivalent must not be a checked
     * exception that every caller has to declare or catch.
     *
     * <p>Method isUncheckedWithNoCause coded on 261016, commented in full on 261016.
     */
    @Test
    void isUncheckedWithNoCause() {
        InvalidTokenFoundDuringParse e = new InvalidTokenFoundDuringParse(C_STYLE_MESSAGE);
        assertInstanceOf(RuntimeException.class, e);
        assertNull(e.getCause());
    }

    /**
     * An unrelated {@code catch} does not intercept it: a method that only handles
     * {@link IOException} lets the exception propagate, as a fatal error would.
     *
     * <p>Method propagatesThroughUnrelatedCatch coded on 261016, commented in full on 261016.
     */
    @Test
    void propagatesThroughUnrelatedCatch() {
        assertThrows(InvalidTokenFoundDuringParse.class, () -> {
            try {
                throw new InvalidTokenFoundDuringParse(C_STYLE_MESSAGE);
            } catch (Exception e) {
                if (e instanceof IOException) {
                    fail("an IOException handler must not be the one that sees a parse failure");
                }
                throw e;
            }
        });
    }

    /**
     * Building the exception, without throwing it, logs the message once at error level, which is
     * where C's {@code print_error()} reports the failure.
     *
     * <p>Method constructionLogsOnceAtErrorLevel coded on 261016, commented in full on 261016.
     */
    @Test
    void constructionLogsOnceAtErrorLevel() {
        new InvalidTokenFoundDuringParse(C_STYLE_MESSAGE);

        assertEquals(1, events.size(), "exactly one event expected from construction");
        assertEquals(Level.ERROR, events.get(0).getLevel());
        assertEquals(C_STYLE_MESSAGE, events.get(0).getMessage().getFormattedMessage());
    }

    /**
     * Throwing and catching adds no further log event; only construction logs.
     *
     * <p>Method throwingDoesNotLogAgain coded on 261016, commented in full on 261016.
     */
    @Test
    void throwingDoesNotLogAgain() {
        assertThrows(InvalidTokenFoundDuringParse.class, () -> {
            throw new InvalidTokenFoundDuringParse(C_STYLE_MESSAGE);
        });
        assertEquals(1, events.size());
    }

    /**
     * Each exception logs its own message, so two failures in one load give two records in order.
     *
     * <p>Method eachInstanceLogsItsOwnMessage coded on 261016, commented in full on 261016.
     */
    @Test
    void eachInstanceLogsItsOwnMessage() {
        new InvalidTokenFoundDuringParse("first: invalid flag");
        new InvalidTokenFoundDuringParse("second: invalid value");

        assertEquals(2, events.size());
        assertEquals("first: invalid flag", events.get(0).getMessage().getFormattedMessage());
        assertEquals("second: invalid value", events.get(1).getMessage().getFormattedMessage());
    }

    /**
     * The message is logged as plain text, not as a log4j format pattern, so braces in a data-file
     * token (C's {@code %s} fields can contain anything) reach the log as written.
     *
     * <p>Method braceTokensAreNotTreatedAsPlaceholders coded on 261016, commented in full on 261016.
     */
    @Test
    void braceTokensAreNotTreatedAsPlaceholders() {
        String message = "Parse error in object.txt line 3 column 1: {} {0} {name}: invalid value";
        new InvalidTokenFoundDuringParse(message);

        assertEquals(1, events.size());
        assertEquals(message, events.get(0).getMessage().getFormattedMessage());
    }
}
