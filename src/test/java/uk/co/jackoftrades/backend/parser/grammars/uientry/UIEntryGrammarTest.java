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

package uk.co.jackoftrades.backend.parser.grammars.uientry;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.junit.jupiter.api.Test;
import uk.co.jackoftrades.backend.parser.ParseErrors;
import uk.co.jackoftrades.backend.parser.uientry.UIEntryParseRecord;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the generated {@link UIEntryGrammar} parser (paired with {@link UIEntryLexer}).
 *
 * <p>These drive the {@code file} start rule and assert on the extraction output — the declared
 * {@code record-count} (a {@code String}) and the list of {@link UIEntryParseRecord}s, each a DTO
 * carrying the record's fields verbatim. Everything the grammar emits is a raw string; enum
 * resolution, defaulting and the generic-entry expansion are the reader/assembler's job.
 *
 * <p>The {@code uiEntry} rule is {@code name} followed by every other field optional, in a fixed
 * relative order. Two field behaviours deserve a note because they are easy to get wrong:
 * <ul>
 *   <li><b>{@code parameter} has two sources.</b> A specialized entry supplies it through the
 *       {@code <TAG>} suffix on its name (e.g. {@code name:resist_ui_compact_0<ACID>} → {@code ACID},
 *       with the tag stripped from the name); a generic entry supplies it through a {@code parameter:}
 *       line. When neither is present the field keeps its {@code ""} default (see
 *       {@link #minimalRecordIsJustNameAndLeavesOptionalFieldsUnset()}); when both are present the
 *       {@code parameter:} line wins (see {@link #parameterLineOverridesTheNameTag()}).</li>
 *   <li><b>{@code category} appears in two slots.</b> Once before {@code parameter} and once after
 *       {@code priority}; both feed the same list, so categories from either position are collected
 *       in source order (see {@link #categoriesFromBothSlotsAreCollectedInOrder()}).</li>
 * </ul>
 *
 * <p>The failure tests confirm the collect-and-report contract: the parser does not stop on the first
 * syntax error but recovers and keeps going, so {@link ParseErrors} accumulates every error and
 * {@link ParseErrors#throwIfAny()} reports them together — see
 * {@link #twoMalformedRecordsBothReportedThenThrownTogether()}.
 *
 * @author ClaudeCode
 */
class UIEntryGrammarTest {

    /**
     * A lightweight error listener that records each syntax error as {@code line:pos msg}, so a test
     * can assert on the count and the offending positions without pulling in the logging machinery.
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
     * Build a parser over {@code src} wired to a local {@link Errors} listener on both lexer and
     * parser (the defaults, which print to stderr and throw, are removed).
     */
    private UIEntryGrammar parser(String src, Errors errors) {
        UIEntryLexer lexer = new UIEntryLexer(CharStreams.fromString(src));
        UIEntryGrammar parser = new UIEntryGrammar(new CommonTokenStream(lexer));
        lexer.removeErrorListeners();
        parser.removeErrorListeners();
        lexer.addErrorListener(errors);
        parser.addErrorListener(errors);
        return parser;
    }

    // ---------------------------------------------------------------------------------------------
    // Passes
    // ---------------------------------------------------------------------------------------------

    @Test
    void fullRecordKeepsEveryFieldVerbatimAndCapturesTheLineNumber() {
        Errors errors = new Errors();
        UIEntryGrammar.FileContext ctx = parser(
                "record-count:1\n"
                        + "name:foo\ntemplate:tmpl\nlabel:Acid\nlabel5:Acidx\nlabel2:Ac\n"
                        + "category:CHAR_SCREEN1\nparameter:element\nrenderer:some_renderer\n"
                        + "combine:RESIST_0\npriority:negative_index\ncategory:EQUIPCMP_SCREEN\n"
                        + "flags:TIMED_AS_AUX\ndesc:hello world\n",
                errors).file();

        assertTrue(errors.messages.isEmpty(), () -> "unexpected errors: " + errors.messages);
        assertEquals("1", ctx.declaredRecordCount);
        assertEquals(1, ctx.entries.size());
        UIEntryParseRecord rec = ctx.entries.get(0);
        assertEquals("foo", rec.name());
        assertEquals("tmpl", rec.template());
        assertEquals("Acid", rec.label());
        assertEquals("Acidx", rec.label5());
        assertEquals("Ac", rec.label2());
        assertEquals("element", rec.parameter());
        assertEquals("some_renderer", rec.renderer());
        assertEquals("RESIST_0", rec.combine());
        assertEquals("negative_index", rec.priority());
        assertEquals(List.of("CHAR_SCREEN1", "EQUIPCMP_SCREEN"), rec.category());
        assertEquals(List.of("TIMED_AS_AUX"), rec.flags());
        assertEquals("hello world", rec.desc());
        // name: is source line 2 (record-count: is line 1).
        assertEquals(2, rec.line());
    }

    @Test
    void minimalRecordIsJustNameAndLeavesOptionalFieldsUnset() {
        Errors errors = new Errors();
        UIEntryGrammar.FileContext ctx =
                parser("record-count:1\nname:bar\n", errors).file();

        assertTrue(errors.messages.isEmpty(), () -> "unexpected errors: " + errors.messages);
        assertEquals(1, ctx.entries.size());
        UIEntryParseRecord rec = ctx.entries.get(0);
        assertEquals("bar", rec.name());
        // With no <TAG> and no parameter: line, the grammar keeps parameter at its "" default (the
        // name rule's null tag is guarded out, so it never overwrites the default with null).
        assertEquals("", rec.parameter());
        // The optional single-valued fields default to the empty string, the list fields to empty lists.
        assertEquals("", rec.template());
        assertEquals("", rec.label());
        assertEquals("", rec.renderer());
        assertEquals("", rec.combine());
        assertEquals("", rec.priority());
        assertEquals("", rec.desc());
        assertTrue(rec.category().isEmpty());
        assertTrue(rec.flags().isEmpty());
    }

    @Test
    void nameTagIsCapturedSeparatelyAndTheNameKeepsTheTag() {
        Errors errors = new Errors();
        UIEntryGrammar.FileContext ctx =
                parser("record-count:1\nname:resist_ui_compact_0<STR>\nlabel:Str\n", errors).file();

        assertTrue(errors.messages.isEmpty(), () -> "unexpected errors: " + errors.messages);
        UIEntryParseRecord rec = ctx.entries.get(0);
        // The <STR> tag is captured in nameTag; the name keeps the full tagged form so that
        // bindui look-ups by "name<TAG>" still match. With no parameter: line, parameter stays empty.
        assertEquals("resist_ui_compact_0<STR>", rec.name());
        assertEquals("STR", rec.nameTag());
        assertEquals("", rec.parameter());
        assertEquals("Str", rec.label());
    }

    @Test
    void parameterLineAndNameTagAreCapturedIndependently() {
        Errors errors = new Errors();
        UIEntryGrammar.FileContext ctx =
                parser("record-count:1\nname:foo<ACID>\nparameter:element\n", errors).file();

        assertTrue(errors.messages.isEmpty(), () -> "unexpected errors: " + errors.messages);
        UIEntryParseRecord rec = ctx.entries.get(0);
        // Tag and parameter: now live in separate fields: the <ACID> tag no longer overwrites the
        // parameter: directive; the name retains the tag and nameTag/parameter are both populated.
        assertEquals("foo<ACID>", rec.name());
        assertEquals("ACID", rec.nameTag());
        assertEquals("element", rec.parameter());
    }

    @Test
    void categoriesFromBothSlotsAreCollectedInOrder() {
        // A category before parameter: and a category after priority: both feed categoryInit; the
        // grammar addAll()s each in turn, so they arrive in source order regardless of slot.
        Errors errors = new Errors();
        UIEntryGrammar.FileContext ctx = parser(
                "record-count:1\nname:foo\ncategory:CHAR_SCREEN1\nparameter:element\n"
                        + "priority:index\ncategory:EQUIPCMP_SCREEN\n",
                errors).file();

        assertTrue(errors.messages.isEmpty(), () -> "unexpected errors: " + errors.messages);
        assertEquals(List.of("CHAR_SCREEN1", "EQUIPCMP_SCREEN"), ctx.entries.get(0).category());
    }

    @Test
    void flagListWithDigitBearingNamesIsKeptVerbatim() {
        // The grammar does not validate flag enums; it keeps each raw symbolic name, digits and all,
        // and a '|'-separated list is collected in order (here including the digit-tail RAND_50).
        Errors errors = new Errors();
        UIEntryGrammar.FileContext ctx = parser(
                "record-count:1\nname:foo\nflags:RAND_50 | TIMED_AS_AUX\n", errors).file();

        assertTrue(errors.messages.isEmpty(), () -> "unexpected errors: " + errors.messages);
        assertEquals(List.of("RAND_50", "TIMED_AS_AUX"), ctx.entries.get(0).flags());
    }

    @Test
    void repeatedDescLinesAreConcatenatedInOrder() {
        // Each desc: STRING captures from just after the colon to end of line; the rule appends them
        // with no separator, so the leading space on the continuation line is preserved verbatim.
        Errors errors = new Errors();
        UIEntryGrammar.FileContext ctx =
                parser("record-count:1\nname:foo\ndesc:one\ndesc: two\n", errors).file();

        assertTrue(errors.messages.isEmpty(), () -> "unexpected errors: " + errors.messages);
        assertEquals("one two", ctx.entries.get(0).desc());
    }

    @Test
    void integerPriorityIsKeptAsAVerbatimString() {
        // priority: is free text to the grammar (index/negative_index/number are only distinguished
        // later, in the assembler); a numeric priority survives as its raw string.
        Errors errors = new Errors();
        UIEntryGrammar.FileContext ctx =
                parser("record-count:1\nname:foo\npriority:5\n", errors).file();

        assertTrue(errors.messages.isEmpty(), () -> "unexpected errors: " + errors.messages);
        assertEquals("5", ctx.entries.get(0).priority());
    }

    @Test
    void recordCountIsCapturedAndMultipleRecordsAreCollectedAcrossSkippedComments() {
        Errors errors = new Errors();
        UIEntryGrammar.FileContext ctx = parser(
                "record-count:2\nname:a\n# a comment line\nname:b\n", errors).file();

        assertTrue(errors.messages.isEmpty(), () -> "unexpected errors: " + errors.messages);
        assertEquals("2", ctx.declaredRecordCount);
        assertEquals(2, ctx.entries.size());
        assertEquals("a", ctx.entries.get(0).name());
        assertEquals("b", ctx.entries.get(1).name());
        // First name: on line 2; the comment on line 3 is skipped but still counted, so b is line 4.
        assertEquals(2, ctx.entries.get(0).line());
        assertEquals(4, ctx.entries.get(1).line());
    }

    // ---------------------------------------------------------------------------------------------
    // Failures
    // ---------------------------------------------------------------------------------------------

    @Test
    void missingRecordCountIsAParseError() {
        Errors errors = new Errors();
        parser("name:foo\n", errors).file();

        assertFalse(errors.messages.isEmpty(), "a file that does not open with record-count must not parse");
    }

    @Test
    void fileWithNoEntriesIsAParseError() {
        // file is `recordCount (uiEntry)+ EOF`: at least one entry is mandatory.
        Errors errors = new Errors();
        parser("record-count:1\n", errors).file();

        assertFalse(errors.messages.isEmpty(), "a record-count with no following entry must not parse");
    }

    @Test
    void uppercaseNameValueIsAParseError() {
        // The name: value is an LCASE token (lower-case/digits/underscore); an all-caps value lexes as
        // PARAMETER_VALUE instead, so it cannot satisfy `NAME LCASE`.
        Errors errors = new Errors();
        parser("record-count:1\nname:FOO\n", errors).file();

        assertFalse(errors.messages.isEmpty(), "an upper-case name: value must not parse");
        assertTrue(errors.messages.stream().anyMatch(m -> m.contains("LCASE")),
                () -> "expected an LCASE mismatch, got: " + errors.messages);
    }

    @Test
    void unterminatedNameTagIsAParseError() {
        // The tag rule is `LTHAN PARAMETER_VALUE GTHAN`; dropping the closing '>' leaves the tag open.
        Errors errors = new Errors();
        parser("record-count:1\nname:foo<ACID\n", errors).file();

        assertFalse(errors.messages.isEmpty(), "an unterminated <TAG> must not parse");
        assertTrue(errors.messages.stream().anyMatch(m -> m.contains("'>'")),
                () -> "expected a missing '>' error, got: " + errors.messages);
    }

    @Test
    void twoMalformedRecordsBothReportedThenThrownTogether() {
        // The heart of the collect-and-report contract: two independent bad records in one file each
        // produce their own syntax error, the parser recovers between them, and ParseErrors holds both
        // so throwIfAny() reports them one after the other before raising a single cancellation.
        UIEntryLexer lexer = new UIEntryLexer(CharStreams.fromString(
                "record-count:2\nname:FOO\nname:BAR\n"));
        UIEntryGrammar parser = new UIEntryGrammar(new CommonTokenStream(lexer));
        ParseErrors errorCatcher = ParseErrors.install(lexer, parser, "two-faults");

        parser.file();

        assertTrue(errorCatcher.hasErrors());
        List<String> collected = errorCatcher.getErrors();
        assertEquals(2, collected.size(), () -> "expected exactly two collected errors, got: " + collected);
        // Two distinct failures, one per bad record, on consecutive source lines (2 and 3).
        assertTrue(collected.get(0).contains("line 2"), () -> collected.toString());
        assertTrue(collected.get(1).contains("line 3"), () -> collected.toString());
        // Only after both are collected does throwIfAny raise - and it raises once, for the pair.
        assertThrows(ParseCancellationException.class, errorCatcher::throwIfAny);
    }
}
