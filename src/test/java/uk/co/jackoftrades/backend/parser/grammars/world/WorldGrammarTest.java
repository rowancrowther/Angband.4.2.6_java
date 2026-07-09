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

package uk.co.jackoftrades.backend.parser.grammars.world;

import org.antlr.v4.runtime.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the generated {@link WorldGrammar} parser (grammar-track step 8, parser half; paired
 * with {@link WorldLexer}).
 *
 * <p>These drive the {@code file} start rule on small inline sources and assert on the extraction
 * output — {@code levels} (a {@code List<List<String>>} of {@code [depth, name, up, down, lineNumber]})
 * and the {@code declaredCount} string — plus the syntax-error count (collected via a custom
 * listener). They lock in the design decisions: every field is extracted verbatim as a {@code String}
 * (the {@code "None"} sentinel passed through, not mapped to null; the declared count left unparsed),
 * each record carries its own source line number, line numbers survive skipped comment/blank lines,
 * and the file must open with a {@code record-count} directive.
 *
 * @author Rowan Crowther
 */
class WorldGrammarTest {

    /**
     * Collects syntax errors raised during a parse.
     */
    private static final class Errors extends BaseErrorListener {
        final List<String> messages = new ArrayList<>();

        @Override
        public void syntaxError(Recognizer<?, ?> r, Object sym, int line, int pos,
                                String msg, RecognitionException e) {
            messages.add(line + ":" + pos + " " + msg);
        }
    }

    /**
     * Builds a {@link WorldGrammar} parser over {@code src}, routing errors to {@code errors}.
     */
    private WorldGrammar parser(String src, Errors errors) {
        WorldLexer lexer = new WorldLexer(CharStreams.fromString(src));
        WorldGrammar parser = new WorldGrammar(new CommonTokenStream(lexer));
        parser.removeErrorListeners();
        parser.addErrorListener(errors);
        return parser;
    }

    @Test
    void singleRecordExtractsAllFiveFieldsVerbatim() {
        Errors errors = new Errors();
        WorldGrammar.FileContext ctx = parser("record-count:1\nlevel:0:Town:None:Angband 1\n", errors).file();

        assertTrue(errors.messages.isEmpty(), () -> "unexpected errors: " + errors.messages);
        // declaredCount is left as a String for the reader to parse.
        assertEquals("1", ctx.declaredCount);
        assertEquals(1, ctx.levels.size());
        // [depth, name, up, down, lineNumber]; "None" is verbatim (not null), and the level sits on
        // source line 2 (record-count is line 1).
        assertEquals(List.of("0", "Town", "None", "Angband 1", "2"), ctx.levels.get(0));
    }

    @Test
    void lineNumbersSurviveSkippedComments() {
        Errors errors = new Errors();
        WorldGrammar.FileContext ctx = parser(
                "record-count:2\n"
                        + "level:0:Town:None:Angband 1\n"
                        + "# a comment line\n"
                        + "level:1:Angband 1:Town:Angband 2\n",
                errors).file();

        assertTrue(errors.messages.isEmpty(), () -> "unexpected errors: " + errors.messages);
        assertEquals(2, ctx.levels.size());
        // The comment on line 3 carries no record, but line counting still advances, so the second
        // record reports line 4 (record-count=1, first level=2, comment=3, second level=4).
        assertEquals("2", ctx.levels.get(0).get(4));
        assertEquals("4", ctx.levels.get(1).get(4));
    }

    @Test
    void missingRecordCountIsAParseError() {
        // file requires a recordCount directive before any level record.
        Errors errors = new Errors();
        parser("level:0:Town:None:Angband 1\n", errors).file();

        assertFalse(errors.messages.isEmpty(), "a file with no record-count header must not parse");
    }

    @Test
    void recordMissingAFieldIsAParseError() {
        // "level:0:Town:None" has no down level, so upAndDown (NAME COLON NAME) can't complete.
        Errors errors = new Errors();
        parser("record-count:1\nlevel:0:Town:None\n", errors).file();

        assertFalse(errors.messages.isEmpty(), "a level record missing the down field must not parse");
    }
}
