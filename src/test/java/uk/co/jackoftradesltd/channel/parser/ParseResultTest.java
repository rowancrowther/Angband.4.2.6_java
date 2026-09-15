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

package uk.co.jackoftradesltd.channel.parser;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Direct unit tests for {@link ParseResult}, the generic result holder every list-shaped
 * {@code lib/gamedata} reader wraps its output in.
 *
 * <p>There is no single C original for this record to be checked against - the C parser
 * (see {@code struct parser_state} in {@code parser.h}) reports one {@code enum parser_error}
 * at a time and fails fast, rather than aggregating a soft-error list. These tests only pin
 * the holder's own contract: {@link ParseResult#hasErrors()} reflects whether the errors list
 * is empty, independently of whether any items were parsed.
 *
 * @author Rowan Crowther
 */
class ParseResultTest {
    @Test
    void resultWithItemsAndNoErrorsReportsNoErrors() {
        List<String> items = List.of("a", "b");
        List<String> errors = Collections.emptyList();

        ParseResult<String> result = new ParseResult<>(items, errors);

        assertFalse(result.hasErrors());
        assertSame(items, result.items());
        assertSame(errors, result.errors());
    }

    @Test
    void resultWithErrorsReportsErrorsRegardlessOfItems() {
        List<String> items = List.of("a");
        List<String> errors = List.of("Line: 12: duplicate world:max-depth");

        ParseResult<String> result = new ParseResult<>(items, errors);

        assertTrue(result.hasErrors());
        assertSame(items, result.items());
        assertSame(errors, result.errors());
    }

    @Test
    void emptyItemsWithNoErrorsReportsNoErrors() {
        ParseResult<String> result = new ParseResult<>(Collections.emptyList(), Collections.emptyList());

        assertFalse(result.hasErrors());
        assertTrue(result.items().isEmpty());
    }

    @Test
    void emptyItemsWithErrorsStillReportsErrors() {
        List<String> errors = List.of("Line: 3: unrecognised directive");

        ParseResult<String> result = new ParseResult<>(Collections.emptyList(), errors);

        assertTrue(result.hasErrors());
        assertTrue(result.items().isEmpty());
    }
}
