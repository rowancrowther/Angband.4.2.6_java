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

package uk.co.jackoftrades.backend.parser.grammars.uientryrenderer;

import org.antlr.v4.runtime.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the generated {@link UIEntryRendererGrammar} parser (grammar-track step 8, parser
 * half; paired with {@link UIEntryRendererLexer}).
 *
 * <p>These drive the {@code file} start rule and assert on the extraction output — the declared
 * {@code record} count string and {@code renderers}, a {@code List<List<String>>} where each inner
 * list is the 7 fields ({@code name,code,colours,labelcolours,symbols,ndigit,sign}, {@code ""} for an
 * absent optional) followed by the record's source line number. All values are verbatim strings;
 * enum resolution and defaulting are the reader's job.
 *
 * @author ClaudeCode
 */
class UIEntryRendererGrammarTest {

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

    private UIEntryRendererGrammar parser(String src, Errors errors) {
        UIEntryRendererLexer lexer = new UIEntryRendererLexer(CharStreams.fromString(src));
        UIEntryRendererGrammar parser = new UIEntryRendererGrammar(new CommonTokenStream(lexer));
        parser.removeErrorListeners();
        parser.addErrorListener(errors);
        return parser;
    }

    @Test
    void fullRecordKeepsEveryFieldVerbatimAndAppendsLineNumber() {
        Errors errors = new Errors();
        UIEntryRendererGrammar.FileContext ctx = parser(
                "record-count:1\nname:foo\ncode:BAR\ncolors:wG\nlabelcolors:sr\n"
                        + "symbols:?.+\nndigit:1\nsign:NO_SIGN\n",
                errors).file();

        assertTrue(errors.messages.isEmpty(), () -> "unexpected errors: " + errors.messages);
        assertEquals("1", ctx.record);
        assertEquals(1, ctx.renderers.size());
        // [name, code, colours, labelcolours, symbols, ndigit, sign, lineNo]; name: is on source line 2.
        assertEquals(
                List.of("foo", "BAR", "wG", "sr", "?.+", "1", "NO_SIGN", "2"),
                ctx.renderers.get(0));
    }

    @Test
    void absentNdigitAndSignAreEmptyStrings() {
        Errors errors = new Errors();
        UIEntryRendererGrammar.FileContext ctx = parser(
                "record-count:1\nname:foo\ncode:BAR\ncolors:wG\nlabelcolors:sr\nsymbols:?.+\n",
                errors).file();

        assertTrue(errors.messages.isEmpty(), () -> "unexpected errors: " + errors.messages);
        assertEquals(
                List.of("foo", "BAR", "wG", "sr", "?.+", "", "", "2"),
                ctx.renderers.get(0));
    }

    @Test
    void digitBearingFlagIsKeptVerbatim() {
        Errors errors = new Errors();
        UIEntryRendererGrammar.FileContext ctx = parser(
                "record-count:1\nname:foo\ncode:FOO_2\ncolors:wG\nlabelcolors:sr\nsymbols:?.+\n",
                errors).file();

        assertTrue(errors.messages.isEmpty(), () -> "unexpected errors: " + errors.messages);
        assertEquals("FOO_2", ctx.renderers.get(0).get(1));
    }

    @Test
    void recordCountIsCapturedAndMultipleRecordsAreCollectedInOrder() {
        Errors errors = new Errors();
        UIEntryRendererGrammar.FileContext ctx = parser(
                "record-count:2\n"
                        + "name:a\ncode:A\ncolors:w\nlabelcolors:s\nsymbols:?\n"
                        + "name:b\ncode:B\ncolors:G\nlabelcolors:r\nsymbols:.\n",
                errors).file();

        assertTrue(errors.messages.isEmpty(), () -> "unexpected errors: " + errors.messages);
        assertEquals("2", ctx.record);
        assertEquals(2, ctx.renderers.size());
        assertEquals("a", ctx.renderers.get(0).get(0));
        assertEquals("b", ctx.renderers.get(1).get(0));
        // First name: on line 2, second on line 7 (five directives per record).
        assertEquals("2", ctx.renderers.get(0).getLast());
        assertEquals("7", ctx.renderers.get(1).getLast());
    }

    @Test
    void lineNumbersSurviveSkippedComments() {
        Errors errors = new Errors();
        UIEntryRendererGrammar.FileContext ctx = parser(
                "record-count:2\n"
                        + "name:a\ncode:A\ncolors:w\nlabelcolors:s\nsymbols:?\n"
                        + "# a comment line\n"
                        + "name:b\ncode:B\ncolors:G\nlabelcolors:r\nsymbols:.\n",
                errors).file();

        assertTrue(errors.messages.isEmpty(), () -> "unexpected errors: " + errors.messages);
        // First name: on line 2; second on line 8 (the comment on line 7 is skipped but still counted).
        assertEquals("2", ctx.renderers.get(0).getLast());
        assertEquals("8", ctx.renderers.get(1).getLast());
    }

    @Test
    void missingRecordCountIsAParseError() {
        Errors errors = new Errors();
        parser("name:foo\ncode:BAR\ncolors:wG\nlabelcolors:sr\nsymbols:?.+\n", errors).file();

        assertFalse(errors.messages.isEmpty(), "a file that does not open with record-count must not parse");
    }

    @Test
    void recordMissingAMandatoryFieldIsAParseError() {
        // No colors/labelcolors/symbols after code: the mandatory sequence can't complete.
        Errors errors = new Errors();
        parser("record-count:1\nname:foo\ncode:BAR\n", errors).file();

        assertFalse(errors.messages.isEmpty(), "a record missing a mandatory field must not parse");
    }
}
