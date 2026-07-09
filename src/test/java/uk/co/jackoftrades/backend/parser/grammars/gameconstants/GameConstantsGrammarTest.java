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

package uk.co.jackoftrades.backend.parser.grammars.gameconstants;

import org.antlr.v4.runtime.*;
import org.junit.jupiter.api.Test;
import uk.co.jackoftrades.backend.parser.gameconstants.GameConstantsParseRecord;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the generated {@link GameConstantsGrammar} parser (grammar-track step 8, parser
 * half; paired with {@link GameConstantsLexer}).
 *
 * <p>These drive the {@code file} start rule on small inline sources and assert on the extraction
 * output — the {@code List<WorldParseRecord>} of {@code (category, ordered field strings,
 * line number)} — plus the syntax-error count (collected via a custom listener). They lock in the
 * design decisions: the single uniform {@code line} rule handles every directive arity (the 2-/3-/4-
 * field split is the reader's job, not the grammar's), fields keep file order, line numbers survive
 * skipped comment/blank lines, and a category with no fields is a parse error.
 *
 * @author Rowan Crowther
 */
class GameConstantsGrammarTest {

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
     * Builds a {@link GameConstantsGrammar} parser over {@code src}, routing errors to {@code errors}.
     */
    private GameConstantsGrammar parser(String src, Errors errors) {
        GameConstantsLexer lexer = new GameConstantsLexer(CharStreams.fromString(src));
        GameConstantsGrammar parser = new GameConstantsGrammar(new CommonTokenStream(lexer));
        parser.removeErrorListeners();
        parser.addErrorListener(errors);
        return parser;
    }

    @Test
    void scalarLineExtractsCategoryAndOrderedFields() {
        Errors errors = new Errors();
        List<GameConstantsParseRecord> records = parser("level-max:monsters:1024\n", errors).file().results;

        assertTrue(errors.messages.isEmpty(), () -> "unexpected errors: " + errors.messages);
        assertEquals(1, records.size());
        GameConstantsParseRecord rec = records.get(0);
        assertEquals("level-max", rec.getCategory());
        assertEquals(List.of("monsters", "1024"), rec.getFields());
        assertEquals(1, rec.getLineNumber());
    }

    @Test
    void fourFieldCriticalLevelLineKeepsEveryFieldInOrder() {
        // Real data (melee-critical-level:-1:4:20:HIT_HI_SUPERB): negative cutoff plus trailing message.
        // The grammar extracts all four fields verbatim; arity meaning is the reader's concern.
        Errors errors = new Errors();
        List<GameConstantsParseRecord> records =
                parser("melee-critical-level:-1:4:20:HIT_HI_SUPERB\n", errors).file().results;

        assertTrue(errors.messages.isEmpty(), () -> "unexpected errors: " + errors.messages);
        assertEquals(1, records.size());
        assertEquals("melee-critical-level", records.get(0).getCategory());
        assertEquals(List.of("-1", "4", "20", "HIT_HI_SUPERB"), records.get(0).getFields());
    }

    @Test
    void threeFieldCriticalLevelLineKeepsEveryFieldInOrder() {
        Errors errors = new Errors();
        List<GameConstantsParseRecord> records =
                parser("o-melee-critical-level:40:2:HIT_GOOD\n", errors).file().results;

        assertTrue(errors.messages.isEmpty(), () -> "unexpected errors: " + errors.messages);
        assertEquals(List.of("40", "2", "HIT_GOOD"), records.get(0).getFields());
    }

    @Test
    void multipleLinesPreserveOrderAndLineNumbersAcrossComments() {
        // The comment on line 2 carries no record, but line counting still advances, so the second
        // directive is reported on line 3.
        Errors errors = new Errors();
        List<GameConstantsParseRecord> records = parser(
                "level-max:monsters:1024\n# a comment line\nmon-gen:chance:500\n", errors).file().results;

        assertTrue(errors.messages.isEmpty(), () -> "unexpected errors: " + errors.messages);
        assertEquals(2, records.size());
        assertEquals("level-max", records.get(0).getCategory());
        assertEquals(1, records.get(0).getLineNumber());
        assertEquals("mon-gen", records.get(1).getCategory());
        assertEquals(3, records.get(1).getLineNumber());
    }

    @Test
    void emptyInputProducesNoRecords() {
        Errors errors = new Errors();
        List<GameConstantsParseRecord> records = parser("", errors).file().results;

        assertTrue(errors.messages.isEmpty(), () -> "unexpected errors: " + errors.messages);
        assertTrue(records.isEmpty());
    }

    @Test
    void categoryWithNoFieldsIsAParseError() {
        // line requires at least one (COLON field); a bare category with no value must not parse.
        Errors errors = new Errors();
        parser("level-max\n", errors).file();

        assertFalse(errors.messages.isEmpty(), "a category with no fields must be a parse error");
    }
}
