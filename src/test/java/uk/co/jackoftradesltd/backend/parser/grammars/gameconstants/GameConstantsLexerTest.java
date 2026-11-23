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

package uk.co.jackoftradesltd.backend.parser.grammars.gameconstants;

import org.antlr.v4.runtime.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for the generated {@link GameConstantsLexer} (grammar-track step 8, lexer half).
 *
 * <p>These exercise the lexer in isolation, asserting the exact token stream — symbolic name plus
 * text — for each {@code constants.txt} directive shape. They pin down the behaviours designed by
 * hand: the three token kinds ({@code GC_NAME}, {@code GC_MSG}, {@code COLON}), hyphenated
 * lowercase names, the lexer modes that decide which of them a field is, and that the imported {@code CommentsAndEol} comment/EOL rules are skipped (so
 * CRLF gamedata tokenises clean — C9).
 *
 * <p>{@code GC_MSG} is any text up to a colon or a line end, as a {@code str} or an {@code int} field
 * is in C, and there is no {@code INTEGER} token: a number lexes as {@code GC_MSG} and the reader
 * turns it into an {@code int} with the {@code strtol} rules. Only the first token of a line can be a
 * {@code GC_NAME} (the category); every field after a colon is a {@code GC_MSG}, whatever it spells,
 * so a label such as {@code monsters} or {@code max-depth} is a {@code GC_MSG} too.
 *
 * <p>Class GameConstantsLexerTest coded before 261104, and updated on 261122 once {@code GC_MSG}
 * took any text up to a colon and the {@code INTEGER} token was removed, and on 261123 once the
 * lexer modes made the label after the category a {@code GC_MSG}.
 *
 * @author Rowan Crowther
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
        // The value is a GC_MSG: there is no INTEGER token, the reader reads the number from the text.
        assertEquals(
                List.of("GC_NAME=level-max", "COLON=:", "GC_MSG=monsters", "COLON=:", "GC_MSG=1024"),
                tokens("level-max:monsters:1024"));
    }

    @Test
    void hyphenatedNamesAreSingleTokens() {
        // Both the category and the label carry hyphens; each must be one token, not split.
        assertEquals(
                List.of("GC_NAME=carry-cap", "COLON=:", "GC_MSG=quiver-slot-size", "COLON=:", "GC_MSG=40"),
                tokens("carry-cap:quiver-slot-size:40"));
    }

    @Test
    void aNegativeValueIsOneGcMsgToken() {
        // Real data (melee-critical:chance-offset:-60): the '-' stays in the one token, it is not a
        // separate token nor a stray COLON-adjacent dash.
        assertEquals(
                List.of("GC_NAME=melee-critical", "COLON=:", "GC_MSG=chance-offset", "COLON=:", "GC_MSG=-60"),
                tokens("melee-critical:chance-offset:-60"));
    }

    @Test
    void criticalLevelFieldsAreAllGcMsgTokens() {
        // The three numbers and the message name after the category are each one GC_MSG.
        assertEquals(
                List.of("GC_NAME=melee-critical-level", "COLON=:", "GC_MSG=400", "COLON=:", "GC_MSG=2",
                        "COLON=:", "GC_MSG=5", "COLON=:", "GC_MSG=HIT_GOOD"),
                tokens("melee-critical-level:400:2:5:HIT_GOOD"));
    }

    /**
     * A message name is any text up to the colon, so a lower-case name with an underscore and a name
     * with a digit are each one {@code GC_MSG}. C's {@code message_lookup_by_name} matches
     * {@code hit_good} and {@code STORE3} too.
     *
     * <p>Function aMessageNameOfAnyCaseOrWithDigitsIsOneGcMsgToken coded on 261122, commented in full
     * on 261122.
     */
    @Test
    void aMessageNameOfAnyCaseOrWithDigitsIsOneGcMsgToken() {
        assertEquals(
                List.of("GC_NAME=melee-critical-level", "COLON=:", "GC_MSG=10", "COLON=:", "GC_MSG=2",
                        "COLON=:", "GC_MSG=5", "COLON=:", "GC_MSG=hit_good"),
                tokens("melee-critical-level:10:2:5:hit_good"));
        assertEquals(
                List.of("GC_NAME=melee-critical-level", "COLON=:", "GC_MSG=10", "COLON=:", "GC_MSG=2",
                        "COLON=:", "GC_MSG=5", "COLON=:", "GC_MSG=STORE3"),
                tokens("melee-critical-level:10:2:5:STORE3"));
    }

    @Test
    void commentsAndBlankLinesAreSkipped() {
        // A leading comment and a blank line carry no tokens; only the directive survives.
        assertEquals(
                List.of("GC_NAME=level-max", "COLON=:", "GC_MSG=monsters", "COLON=:", "GC_MSG=1024"),
                tokens("# a header comment\n\nlevel-max:monsters:1024\n"));
    }

    @Test
    void crlfLineEndingsTokeniseCleanly() {
        // Gamedata files are CRLF; the imported EOL rule eats '\r'? '\n', so two CRLF lines yield the
        // flat token stream with no recognition errors (C9).
        String src = "level-max:monsters:1024\r\nmon-gen:chance:500\r\n";
        assertEquals(
                List.of("GC_NAME=level-max", "COLON=:", "GC_MSG=monsters", "COLON=:", "GC_MSG=1024",
                        "GC_NAME=mon-gen", "COLON=:", "GC_MSG=chance", "COLON=:", "GC_MSG=500"),
                tokens(src));
        assertEquals(0, lexErrors(src));
    }

    /**
     * Any character but a colon or a line end is a {@code GC_MSG}, so a decimal value is no lexer
     * error. C reads {@code 10.5} with {@code strtol}, takes the 10 and ignores the rest.
     *
     * <p>Function aDecimalValueIsOneGcMsgTokenAndNoLexerError coded on 261122, commented in full on
     * 261122. It replaces unrecognisedCharacterRaisesLexerError.
     */
    @Test
    void aDecimalValueIsOneGcMsgTokenAndNoLexerError() {
        assertEquals(
                List.of("GC_NAME=world", "COLON=:", "GC_MSG=max-depth", "COLON=:", "GC_MSG=10.5"),
                tokens("world:max-depth:10.5"));
        assertEquals(0, lexErrors("world:max-depth:10.5"));
    }

    /**
     * Leading blanks on a line are skipped by the imported {@code INDENT} rule, as C's
     * {@code parse_file} skips them. {@code INDENT} must beat {@code GC_MSG} at column 0, but
     * {@code GC_MSG} takes blanks too, so {@code "  world"} would be the longer match and the indent
     * would be swallowed into the first token. The default mode has no {@code GC_MSG}, only
     * {@code GC_NAME}, so the indent is skipped and the category is a clean {@code GC_NAME}.
     *
     * <p>Function anIndentBeforeTheCategoryIsSkipped coded on 261122, commented in full on 261123.
     */
    @Test
    void anIndentBeforeTheCategoryIsSkipped() {
        assertEquals(
                List.of("GC_NAME=world", "COLON=:", "GC_MSG=max-depth", "COLON=:", "GC_MSG=7"),
                tokens("  world:max-depth:7"));
    }
}
