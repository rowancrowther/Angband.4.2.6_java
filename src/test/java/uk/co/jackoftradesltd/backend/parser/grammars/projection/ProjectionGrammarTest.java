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

package uk.co.jackoftradesltd.backend.parser.grammars.projection;

import org.antlr.v4.runtime.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the generated {@link ProjectionGrammar} parser (grammar-track step 8, parser half;
 * paired with {@link ProjectionLexer}).
 *
 * <p>These drive the {@code file} start rule and assert on the extraction output — the declared
 * {@code records} count string and {@code projections}, a {@code List<List<String>>} where each
 * inner list is the 15 fields (positional, {@code ""} for an absent optional) followed by the
 * record's source line number. All values are verbatim strings; enum resolution and defaulting are
 * the reader's job.
 *
 * <p>Class ProjectionGrammarTest coded before 261104, updated on 261104 once the {@code record-count:} header was removed,
 * and on 261113 once the record became {@code code:} followed by any directives in any order.
 *
 * @author Rowan Crowther
 */
class ProjectionGrammarTest {

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

    private ProjectionGrammar parser(String src, Errors errors) {
        ProjectionLexer lexer = new ProjectionLexer(CharStreams.fromString(src));
        ProjectionGrammar parser = new ProjectionGrammar(new CommonTokenStream(lexer));
        parser.removeErrorListeners();
        parser.addErrorListener(errors);
        return parser;
    }

    @Test
    void mandatoryOnlyRecordFillsOptionalsWithNullAndAppendsLineNumber() {
        Errors errors = new Errors();
        ProjectionGrammar.FileContext ctx = parser(
                "code:ACID\ntype:element\ndesc:acid\nblind-desc:acid\nobvious:1\ncolor:Slate\n",
                errors).file();

        assertTrue(errors.messages.isEmpty(), () -> "unexpected errors: " + errors.messages);
        assertEquals(1, ctx.projections.size());
        // [code,name,type,desc,player,blind,lash,num,den,div,cap,msgt,obvious,wake,colour, lineNo]
        // code: is on source line 1, so the trailing line-number element is "1".
        assertEquals(
                Arrays.asList("ACID", null, "element", "acid", null, "acid", null, null, null, null, null, null, "1", null, "Slate", "1"),
                ctx.projections.get(0));
    }

    @Test
    void fullRecordKeepsEveryFieldVerbatim() {
        Errors errors = new Errors();
        ProjectionGrammar.FileContext ctx = parser(
                "code:ACID\nname:acid\ntype:element\ndesc:acid\nplayer-desc:acid\n"
                        + "blind-desc:acid\nlash-desc:acid\nnumerator:1\ndenominator:8+1d4\ndivisor:3\n"
                        + "damage-cap:1600\nmsgt:BR_ACID\nobvious:1\nwake:1\ncolor:Slate\n",
                errors).file();

        assertTrue(errors.messages.isEmpty(), () -> "unexpected errors: " + errors.messages);
        assertEquals(
                List.of("ACID", "acid", "element", "acid", "acid", "acid", "acid", "1", "8+1d4", "3",
                        "1600", "BR_ACID", "1", "1", "Slate", "1"),
                ctx.projections.get(0));
    }

    @Test
    void lineNumbersSurviveSkippedComments() {
        Errors errors = new Errors();
        ProjectionGrammar.FileContext ctx = parser(
                "code:ACID\ntype:element\ndesc:acid\nblind-desc:acid\nobvious:1\ncolor:Slate\n"
                        + "# a comment line\n"
                        + "code:ELEC\ntype:element\ndesc:lightning\nblind-desc:lightning\nobvious:1\ncolor:Blue\n",
                errors).file();

        assertTrue(errors.messages.isEmpty(), () -> "unexpected errors: " + errors.messages);
        assertEquals(2, ctx.projections.size());
        // First code: on line 1; second on line 8 (the comment on line 7 is skipped but still counted).
        assertEquals("1", ctx.projections.get(0).getLast());
        assertEquals("8", ctx.projections.get(1).getLast());
    }

    /**
     * Only {@code code:} is mandatory, so a record of {@code code:} and {@code type:} parses and the
     * twelve directives it leaves out are {@code null}.
     *
     * <p>Function aRecordOfOnlyCodeAndTypeParsesAndTheRestStayNull coded on 261113, commented in
     * full on 261113. It replaces recordMissingAMandatoryFieldIsAParseError, which expected the old
     * fixed sequence to refuse the record.
     */
    @Test
    void aRecordOfOnlyCodeAndTypeParsesAndTheRestStayNull() {
        Errors errors = new Errors();
        ProjectionGrammar.FileContext ctx = parser("code:ACID\ntype:element\n", errors).file();

        assertTrue(errors.messages.isEmpty(), () -> "unexpected errors: " + errors.messages);
        assertEquals(
                Arrays.asList("ACID", null, "element", null, null, null, null, null, null, null, null,
                        null, null, null, null, "1"),
                ctx.projections.get(0));
    }

    /**
     * The directives after {@code code:} come in any order, and a repeated one replaces the earlier,
     * so the last wins.
     *
     * <p>Function directivesComeInAnyOrderAndTheLastRepeatWins coded on 261113, commented in full
     * on 261113.
     */
    @Test
    void directivesComeInAnyOrderAndTheLastRepeatWins() {
        Errors errors = new Errors();
        ProjectionGrammar.FileContext ctx = parser(
                "code:ACID\ncolor:Blue\ndesc:one\ntype:element\ndesc:two\n", errors).file();

        assertTrue(errors.messages.isEmpty(), () -> "unexpected errors: " + errors.messages);
        assertEquals(
                Arrays.asList("ACID", null, "element", "two", null, null, null, null, null, null, null,
                        null, null, null, "Blue", "1"),
                ctx.projections.get(0));
    }

    /**
     * A directive with no {@code code:} before it is a syntax error: a record starts at
     * {@code code:}.
     *
     * <p>Function aDirectiveAheadOfCodeIsAParseError coded on 261113, commented in full on 261113.
     */
    @Test
    void aDirectiveAheadOfCodeIsAParseError() {
        Errors errors = new Errors();
        parser("name:x\ncode:ACID\ntype:element\n", errors).file();

        assertFalse(errors.messages.isEmpty(), "a directive ahead of code: must not parse");
    }

    /**
     * An empty file parses to no records; the reader's assembler, not the grammar, stops the load on
     * the element count.
     *
     * <p>Function anEmptyFileParsesToNoRecords coded on 261113, commented in full on 261113.
     */
    @Test
    void anEmptyFileParsesToNoRecords() {
        Errors errors = new Errors();
        ProjectionGrammar.FileContext ctx = parser("", errors).file();

        assertTrue(errors.messages.isEmpty(), () -> "unexpected errors: " + errors.messages);
        assertTrue(ctx.projections.isEmpty());
    }
}
