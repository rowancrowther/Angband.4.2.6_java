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

package uk.co.jackoftradesltd.backend.parser;

import org.junit.jupiter.api.Test;

import uk.co.jackoftradesltd.middle.game.globals.data.GameConstantsData;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Direct unit tests for {@link GameConstantsParseResult}, isolated from the ANTLR-backed
 * {@link GameConstantsReader} pipeline that {@code GameConstantsReaderTest} exercises it through.
 *
 * <p>There is no single C original for this holder to be checked against - the C constants
 * parser (see {@code constants_parser} in {@code init.c}) fails fast on a single
 * {@code enum parser_error} rather than aggregating a soft-error list, and that divergence is
 * already the documented design of {@link GameConstantsReader#parseWithResults}. These tests
 * only pin the holder's own two-state contract: a data-bearing result reports no errors, and a
 * null-data result carries them.
 *
 * @author Rowan Crowther
 */
class GameConstantsParseResultTest {
    // Every field is null; the holder never inspects the record's contents, only its presence.
    private static final GameConstantsData EMPTY_DATA =
            new GameConstantsData(null, null, null, null, null, null, null, null, null, null, null, null, null, null,
                    null, null, null);

    @Test
    void successfulResultHasNoErrorsAndExposesTheSameDataInstance() {
        List<String> errors = Collections.emptyList();

        GameConstantsParseResult result = new GameConstantsParseResult(EMPTY_DATA, errors);

        assertFalse(result.hasErrors());
        assertSame(EMPTY_DATA, result.getData());
        assertSame(errors, result.getErrors());
    }

    @Test
    void failedResultHasNullDataAndExposesTheSameErrorList() {
        List<String> errors = List.of("Line: 12: duplicate world:max-depth");

        GameConstantsParseResult result = new GameConstantsParseResult(null, errors);

        assertTrue(result.hasErrors());
        assertNull(result.getData());
        assertSame(errors, result.getErrors());
    }
}
