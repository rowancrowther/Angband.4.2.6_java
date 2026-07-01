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

package uk.co.jackoftrades.backend.parser.grammars.projection;

import org.antlr.v4.runtime.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the generated {@link Projections} parser (grammar-track step 8, parser half;
 * paired with {@link ProjectionsLexer}).
 *
 * <p>These drive the {@code file} start rule and assert on the extraction output — the declared
 * {@code records} count string and {@code projections}, a {@code List<List<String>>} where each
 * inner list is the 15 fields (positional, {@code ""} for an absent optional) followed by the
 * record's source line number. All values are verbatim strings; enum resolution and defaulting are
 * the reader's job.
 *
 * @author ClaudeCode
 */
class ProjectionsTest {

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

    private Projections parser(String src, Errors errors) {
        ProjectionsLexer lexer = new ProjectionsLexer(CharStreams.fromString(src));
        Projections parser = new Projections(new CommonTokenStream(lexer));
        parser.removeErrorListeners();
        parser.addErrorListener(errors);
        return parser;
    }

    @Test
    void mandatoryOnlyRecordFillsOptionalsWithEmptyAndAppendsLineNumber() {
        Errors errors = new Errors();
        Projections.FileContext ctx = parser(
                "record-count:1\ncode:ACID\ntype:element\ndesc:acid\nblind-desc:acid\nobvious:1\ncolor:Slate\n",
                errors).file();

        assertTrue(errors.messages.isEmpty(), () -> "unexpected errors: " + errors.messages);
        assertEquals("1", ctx.records);
        assertEquals(1, ctx.projections.size());
        // [code,name,type,desc,player,blind,lash,num,den,div,cap,msgt,obvious,wake,colour, lineNo]
        // code: is on source line 2, so the trailing line-number element is "2".
        assertEquals(
                List.of("ACID", "", "element", "acid", "", "acid", "", "", "", "", "", "", "1", "", "Slate", "2"),
                ctx.projections.get(0));
    }

    @Test
    void fullRecordKeepsEveryFieldVerbatim() {
        Errors errors = new Errors();
        Projections.FileContext ctx = parser(
                "record-count:1\ncode:ACID\nname:acid\ntype:element\ndesc:acid\nplayer-desc:acid\n"
                        + "blind-desc:acid\nlash-desc:acid\nnumerator:1\ndenominator:8+1d4\ndivisor:3\n"
                        + "damage-cap:1600\nmsgt:BR_ACID\nobvious:1\nwake:1\ncolor:Slate\n",
                errors).file();

        assertTrue(errors.messages.isEmpty(), () -> "unexpected errors: " + errors.messages);
        assertEquals(
                List.of("ACID", "acid", "element", "acid", "acid", "acid", "acid", "1", "8+1d4", "3",
                        "1600", "BR_ACID", "1", "1", "Slate", "2"),
                ctx.projections.get(0));
    }

    @Test
    void lineNumbersSurviveSkippedComments() {
        Errors errors = new Errors();
        Projections.FileContext ctx = parser(
                "record-count:2\n"
                        + "code:ACID\ntype:element\ndesc:acid\nblind-desc:acid\nobvious:1\ncolor:Slate\n"
                        + "# a comment line\n"
                        + "code:ELEC\ntype:element\ndesc:lightning\nblind-desc:lightning\nobvious:1\ncolor:Blue\n",
                errors).file();

        assertTrue(errors.messages.isEmpty(), () -> "unexpected errors: " + errors.messages);
        assertEquals(2, ctx.projections.size());
        // First code: on line 2; second on line 9 (the comment on line 8 is skipped but still counted).
        assertEquals("2", ctx.projections.get(0).getLast());
        assertEquals("9", ctx.projections.get(1).getLast());
    }

    @Test
    void missingRecordCountIsAParseError() {
        Errors errors = new Errors();
        parser("code:ACID\ntype:element\ndesc:acid\nblind-desc:acid\nobvious:1\ncolor:Slate\n", errors).file();

        assertFalse(errors.messages.isEmpty(), "a file that does not open with record-count must not parse");
    }

    @Test
    void recordMissingAMandatoryFieldIsAParseError() {
        // No desc/blind-desc/obvious/color after type: the mandatory sequence can't complete.
        Errors errors = new Errors();
        parser("record-count:1\ncode:ACID\ntype:element\n", errors).file();

        assertFalse(errors.messages.isEmpty(), "a record missing a mandatory field must not parse");
    }
}
