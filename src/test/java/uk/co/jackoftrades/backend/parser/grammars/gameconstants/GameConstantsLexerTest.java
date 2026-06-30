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

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the generated {@link GameConstantsLexer} (grammar-track step 8, lexer half).
 *
 * <p>These exercise the lexer in isolation, asserting the exact token stream — symbolic name plus
 * text — for each {@code constants.txt} directive shape. They pin down the behaviours designed by
 * hand: the four token kinds ({@code GC_NAME}, {@code GC_MSG}, {@code COLON}, {@code INTEGER}), the
 * single sign-capable {@code INTEGER} (C7), hyphenated lowercase names, and that the imported
 * {@link uk.co.jackoftrades.backend.parser.grammars.imports.CommentsAndEol} comment/EOL rules are
 * skipped (so CRLF gamedata tokenises clean — C9).
 *
 * @author ClaudeCode
 */
class GameConstantsLexerTest {

    /**
     * Tokenises {@code src}, returning one {@code "SYMBOLIC_NAME=text"} entry per token (EOF excluded).
     * Skipped tokens (comments, EOLs) never appear, so the result is the bare directive stream.
     */
    private List<String> tokens(String src) {
        GameConstantsLexer lexer = new GameConstantsLexer(CharStreams.fromString(src));
        Vocabulary vocab = lexer.getVocabulary();
        List<String> out = new ArrayList<>();
        for (Token t = lexer.nextToken(); t.getType() != Token.EOF; t = lexer.nextToken()) {
            out.add(vocab.getSymbolicName(t.getType()) + "=" + t.getText());
        }
        return out;
    }

    /**
     * Counts lexer recognition errors raised while tokenising {@code src}.
     */
    private int lexErrors(String src) {
        GameConstantsLexer lexer = new GameConstantsLexer(CharStreams.fromString(src));
        int[] count = {0};
        lexer.removeErrorListeners();
        lexer.addErrorListener(new BaseErrorListener() {
            @Override
            public void syntaxError(Recognizer<?, ?> r, Object sym, int line, int pos,
                                    String msg, RecognitionException e) {
                count[0]++;
            }
        });
        while (lexer.nextToken().getType() != Token.EOF) {
            // drain
        }
        return count[0];
    }

    @Test
    void scalarDirectiveTokenises() {
        assertEquals(
                List.of("GC_NAME=level-max", "COLON=:", "GC_NAME=monsters", "COLON=:", "INTEGER=1024"),
                tokens("level-max:monsters:1024"));
    }

    @Test
    void hyphenatedNamesAreSingleTokens() {
        // Both the category and the label carry hyphens; each must be one GC_NAME, not split.
        assertEquals(
                List.of("GC_NAME=carry-cap", "COLON=:", "GC_NAME=quiver-slot-size", "COLON=:", "INTEGER=40"),
                tokens("carry-cap:quiver-slot-size:40"));
    }

    @Test
    void negativeIntegerIsOneSignedToken() {
        // Real data (melee-critical:chance-offset:-60): the '-' binds into a single INTEGER (C7),
        // it is not a separate token nor a stray COLON-adjacent dash.
        assertEquals(
                List.of("GC_NAME=melee-critical", "COLON=:", "GC_NAME=chance-offset", "COLON=:", "INTEGER=-60"),
                tokens("melee-critical:chance-offset:-60"));
    }

    @Test
    void criticalLevelMessageNameIsAGcMsgToken() {
        // The trailing uppercase/underscore message name is GC_MSG, distinct from the lowercase GC_NAME.
        assertEquals(
                List.of("GC_NAME=melee-critical-level", "COLON=:", "INTEGER=400", "COLON=:", "INTEGER=2",
                        "COLON=:", "INTEGER=5", "COLON=:", "GC_MSG=HIT_GOOD"),
                tokens("melee-critical-level:400:2:5:HIT_GOOD"));
    }

    @Test
    void commentsAndBlankLinesAreSkipped() {
        // A leading comment and a blank line carry no tokens; only the directive survives.
        assertEquals(
                List.of("GC_NAME=level-max", "COLON=:", "GC_NAME=monsters", "COLON=:", "INTEGER=1024"),
                tokens("# a header comment\n\nlevel-max:monsters:1024\n"));
    }

    @Test
    void crlfLineEndingsTokeniseCleanly() {
        // Gamedata files are CRLF; the imported EOL rule eats '\r'? '\n', so two CRLF lines yield the
        // flat token stream with no recognition errors (C9).
        String src = "level-max:monsters:1024\r\nmon-gen:chance:500\r\n";
        assertEquals(
                List.of("GC_NAME=level-max", "COLON=:", "GC_NAME=monsters", "COLON=:", "INTEGER=1024",
                        "GC_NAME=mon-gen", "COLON=:", "GC_NAME=chance", "COLON=:", "INTEGER=500"),
                tokens(src));
        assertEquals(0, lexErrors(src));
    }

    @Test
    void unrecognisedCharacterRaisesLexerError() {
        // '.' matches no token rule (values are integer-only), so a decimal value is a lexer error.
        assertTrue(lexErrors("world:max-depth:10.5") > 0,
                "a '.' in a value position should be a lexer error");
    }
}
