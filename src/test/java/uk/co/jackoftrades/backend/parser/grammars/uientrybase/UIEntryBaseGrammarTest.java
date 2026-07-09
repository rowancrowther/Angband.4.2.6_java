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

package uk.co.jackoftrades.backend.parser.grammars.uientrybase;

import org.antlr.v4.runtime.*;
import org.junit.jupiter.api.Test;
import uk.co.jackoftrades.backend.parser.uientrybase.UIEntryBaseParseRecord;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the generated {@link UIEntryBaseGrammar} parser (paired with {@link UIEntryBaseLexer}).
 *
 * <p>These drive the {@code file} start rule and assert on the extraction output — the declared
 * {@code record-count} (a {@code String}) and the list of {@link UIEntryBaseParseRecord}s, each a DTO
 * carrying the record's fields verbatim (name/renderer/combine/flags/desc, the collected categories,
 * and the source line number). All values are raw strings; enum resolution and defaulting are the
 * reader/assembler's job. The record's fixed shape is
 * {@code name renderer combine category+ flags desc+}.
 *
 * @author Rowan Crowther
 */
class UIEntryBaseGrammarTest {

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

    private UIEntryBaseGrammar parser(String src, Errors errors) {
        UIEntryBaseLexer lexer = new UIEntryBaseLexer(CharStreams.fromString(src));
        UIEntryBaseGrammar parser = new UIEntryBaseGrammar(new CommonTokenStream(lexer));
        parser.removeErrorListeners();
        parser.addErrorListener(errors);
        return parser;
    }

    @Test
    void fullRecordKeepsEveryFieldVerbatimAndCapturesTheLineNumber() {
        Errors errors = new Errors();
        UIEntryBaseGrammar.FileContext ctx = parser(
                "record-count:1\nname:foo\nrenderer:bar_renderer\ncombine:LOGICAL_OR\n"
                        + "category:CHAR_SCREEN1\nflags:TIMED_AS_AUX\ndesc:hello world\n",
                errors).file();

        assertTrue(errors.messages.isEmpty(), () -> "unexpected errors: " + errors.messages);
        assertEquals("1", ctx.declaredCount);
        assertEquals(1, ctx.uiEntryBases.size());
        UIEntryBaseParseRecord rec = ctx.uiEntryBases.get(0);
        assertEquals("foo", rec.name());
        assertEquals("bar_renderer", rec.renderer());
        assertEquals("LOGICAL_OR", rec.combine());
        assertEquals(List.of("CHAR_SCREEN1"), rec.categories());
        assertEquals("TIMED_AS_AUX", rec.flags());
        assertEquals("hello world", rec.desc());
        // name: is on source line 2 (record-count: is line 1).
        assertEquals(2, rec.lineNumber());
    }

    @Test
    void repeatedCategoryLinesAreCollectedInOrder() {
        Errors errors = new Errors();
        UIEntryBaseGrammar.FileContext ctx = parser(
                "record-count:1\nname:foo\nrenderer:r\ncombine:ADD\n"
                        + "category:CHAR_SCREEN1\ncategory:EQUIPCMP_SCREEN\ncategory:abilities\n"
                        + "flags:TIMED_AS_AUX\ndesc:d\n",
                errors).file();

        assertTrue(errors.messages.isEmpty(), () -> "unexpected errors: " + errors.messages);
        assertEquals(List.of("CHAR_SCREEN1", "EQUIPCMP_SCREEN", "abilities"),
                ctx.uiEntryBases.get(0).categories());
    }

    @Test
    void repeatedDescLinesAreConcatenatedInOrder() {
        // Each desc: STRING captures from just after the colon to end of line; the rule appends them
        // with no separator, so the leading space on continuation lines is preserved verbatim.
        Errors errors = new Errors();
        UIEntryBaseGrammar.FileContext ctx = parser(
                "record-count:1\nname:foo\nrenderer:r\ncombine:ADD\ncategory:C\nflags:F\n"
                        + "desc:one\ndesc: two\n",
                errors).file();

        assertTrue(errors.messages.isEmpty(), () -> "unexpected errors: " + errors.messages);
        assertEquals("one two", ctx.uiEntryBases.get(0).desc());
    }

    @Test
    void digitBearingCombineAndFlagsAreKeptVerbatim() {
        // The grammar does not validate the enums (that is the assembler's job); it keeps the raw
        // symbolic name, digits and all, so a digit-tail flag survives the parse untouched.
        Errors errors = new Errors();
        UIEntryBaseGrammar.FileContext ctx = parser(
                "record-count:1\nname:foo\nrenderer:r\ncombine:ADD_2\ncategory:C\n"
                        + "flags:RAND_50\ndesc:d\n",
                errors).file();

        assertTrue(errors.messages.isEmpty(), () -> "unexpected errors: " + errors.messages);
        assertEquals("ADD_2", ctx.uiEntryBases.get(0).combine());
        assertEquals("RAND_50", ctx.uiEntryBases.get(0).flags());
    }

    @Test
    void recordCountIsCapturedAndMultipleRecordsAreCollectedInOrder() {
        Errors errors = new Errors();
        UIEntryBaseGrammar.FileContext ctx = parser(
                "record-count:2\n"
                        + "name:a\nrenderer:ra\ncombine:ADD\ncategory:CAT\nflags:FL\ndesc:da\n"
                        + "name:b\nrenderer:rb\ncombine:ADD\ncategory:CAT\nflags:FL\ndesc:db\n",
                errors).file();

        assertTrue(errors.messages.isEmpty(), () -> "unexpected errors: " + errors.messages);
        assertEquals("2", ctx.declaredCount);
        assertEquals(2, ctx.uiEntryBases.size());
        assertEquals("a", ctx.uiEntryBases.get(0).name());
        assertEquals("b", ctx.uiEntryBases.get(1).name());
        // First name: on line 2, second on line 8 (six directive lines per record).
        assertEquals(2, ctx.uiEntryBases.get(0).lineNumber());
        assertEquals(8, ctx.uiEntryBases.get(1).lineNumber());
    }

    @Test
    void lineNumbersSurviveSkippedComments() {
        Errors errors = new Errors();
        UIEntryBaseGrammar.FileContext ctx = parser(
                "record-count:2\n"
                        + "name:a\nrenderer:ra\ncombine:ADD\ncategory:CAT\nflags:FL\ndesc:da\n"
                        + "# a comment line\n"
                        + "name:b\nrenderer:rb\ncombine:ADD\ncategory:CAT\nflags:FL\ndesc:db\n",
                errors).file();

        assertTrue(errors.messages.isEmpty(), () -> "unexpected errors: " + errors.messages);
        // The comment on line 8 is skipped but still counted, so the second record's name: is line 9.
        assertEquals(9, ctx.uiEntryBases.get(1).lineNumber());
    }

    @Test
    void missingRecordCountIsAParseError() {
        Errors errors = new Errors();
        parser("name:foo\nrenderer:r\ncombine:ADD\ncategory:C\nflags:F\ndesc:d\n", errors).file();

        assertFalse(errors.messages.isEmpty(), "a file that does not open with record-count must not parse");
    }

    @Test
    void recordMissingTheMandatoryDescIsAParseError() {
        // desc+ is mandatory (as are name/renderer/combine/category+/flags): a record that stops
        // after flags cannot complete the fixed sequence.
        Errors errors = new Errors();
        parser("record-count:1\nname:foo\nrenderer:r\ncombine:ADD\ncategory:C\nflags:F\n", errors).file();

        assertFalse(errors.messages.isEmpty(), "a record missing the mandatory desc: field must not parse");
    }
}
