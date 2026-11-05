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

package uk.co.jackoftradesltd.backend.parser.grammars.uientrybase;

import org.antlr.v4.runtime.*;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.frontend.ui.entrybase.antlr4.uientrybase.UIEntryBaseGrammar;
import uk.co.jackoftradesltd.frontend.ui.entrybase.antlr4.uientrybase.UIEntryBaseLexer;
import uk.co.jackoftradesltd.frontend.ui.entrybase.assembler.UIEntryBaseParseRecord;

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
 * <p>Class UIEntryBaseGrammarTest coded before 261104, updated on 261104 once the {@code record-count:} header was removed.
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
                "name:foo\nrenderer:bar_renderer\ncombine:LOGICAL_OR\n"
                        + "category:CHAR_SCREEN1\nflags:TIMED_AS_AUX\ndesc:hello world\n",
                errors).file();

        assertTrue(errors.messages.isEmpty(), () -> "unexpected errors: " + errors.messages);
        assertEquals(1, ctx.uiEntryBases.size());
        UIEntryBaseParseRecord rec = ctx.uiEntryBases.get(0);
        assertEquals("foo", rec.name());
        assertEquals("bar_renderer", rec.renderer());
        assertEquals("LOGICAL_OR", rec.combine());
        assertEquals(List.of("CHAR_SCREEN1"), rec.categories());
        assertEquals("TIMED_AS_AUX", rec.flags());
        assertEquals("hello world", rec.desc());
        // name: is on source line 2 (record-count: is line 1).
        assertEquals(1, rec.lineNumber());
    }

    @Test
    void repeatedCategoryLinesAreCollectedInOrder() {
        Errors errors = new Errors();
        UIEntryBaseGrammar.FileContext ctx = parser(
                "name:foo\nrenderer:r\ncombine:ADD\n"
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
                "name:foo\nrenderer:r\ncombine:ADD\ncategory:C\nflags:F\n"
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
                "name:foo\nrenderer:r\ncombine:ADD_2\ncategory:C\n"
                        + "flags:RAND_50\ndesc:d\n",
                errors).file();

        assertTrue(errors.messages.isEmpty(), () -> "unexpected errors: " + errors.messages);
        assertEquals("ADD_2", ctx.uiEntryBases.get(0).combine());
        assertEquals("RAND_50", ctx.uiEntryBases.get(0).flags());
    }

    @Test
    void lineNumbersSurviveSkippedComments() {
        Errors errors = new Errors();
        UIEntryBaseGrammar.FileContext ctx = parser(
                "name:a\nrenderer:ra\ncombine:ADD\ncategory:CAT\nflags:FL\ndesc:da\n"
                        + "# a comment line\n"
                        + "name:b\nrenderer:rb\ncombine:ADD\ncategory:CAT\nflags:FL\ndesc:db\n",
                errors).file();

        assertTrue(errors.messages.isEmpty(), () -> "unexpected errors: " + errors.messages);
        // The comment on line 7 is skipped but still counted, so the second record's name: is line 8.
        assertEquals(8, ctx.uiEntryBases.get(1).lineNumber());
    }

    @Test
    void recordMissingTheMandatoryDescIsAParseError() {
        // desc+ is mandatory (as are name/renderer/combine/category+/flags): a record that stops
        // after flags cannot complete the fixed sequence.
        Errors errors = new Errors();
        parser("name:foo\nrenderer:r\ncombine:ADD\ncategory:C\nflags:F\n", errors).file();

        assertFalse(errors.messages.isEmpty(), "a record missing the mandatory desc: field must not parse");
    }

    /**
     * Two records are collected in file order, each carrying the line of its {@code name:}.
     *
     * <p>Function multipleRecordsAreCollectedInOrder coded on 261104, commented in full on 261104.
     * It replaces recordCountIsCapturedAndMultipleRecordsAreCollectedInOrder, whose
     * {@code record-count:} half went with the header.
     */
    @Test
    void multipleRecordsAreCollectedInOrder() {
        Errors errors = new Errors();
        UIEntryBaseGrammar.FileContext ctx = parser(
                "name:a\nrenderer:ra\ncombine:ADD\ncategory:CAT\nflags:FL\ndesc:da\n"
                        + "name:b\nrenderer:rb\ncombine:ADD\ncategory:CAT\nflags:FL\ndesc:db\n",
                errors).file();

        assertTrue(errors.messages.isEmpty(), () -> "unexpected errors: " + errors.messages);
        assertEquals(2, ctx.uiEntryBases.size());
        assertEquals("a", ctx.uiEntryBases.get(0).name());
        assertEquals("b", ctx.uiEntryBases.get(1).name());
        // First name: on line 1, second on line 7 (six directive lines per record).
        assertEquals(1, ctx.uiEntryBases.get(0).lineNumber());
        assertEquals(7, ctx.uiEntryBases.get(1).lineNumber());
    }
}
