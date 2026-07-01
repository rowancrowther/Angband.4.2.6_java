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

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for the generated {@link ProjectionsLexer} (grammar-track step 8, lexer half).
 *
 * <p>The interesting behaviour is the two lexer modes: the description/name/type/colour keywords
 * push into {@code FREE_TEXT} (one {@code STRING} token to end of line, spaces and punctuation
 * included), and {@code denominator:} pushes into {@code DICE_MODE} (one {@code SIMPLE_DICE} token,
 * via the imported {@code DiceStrings} fragment, covering both a bare number and dice notation).
 * Symbolic fields (code/msgt) stay in the default mode as {@code TAG}; numeric fields are the single
 * sign-capable {@code INTEGER} (C7).
 *
 * @author ClaudeCode
 */
class ProjectionsLexerTest {

    /**
     * Tokenises {@code src}, returning one {@code "SYMBOLIC_NAME=text"} entry per token (EOF excluded).
     * The imported CommentsAndEol comment/EOL rules are skipped, so the result is the bare stream.
     */
    private List<String> tokens(String src) {
        ProjectionsLexer lexer = new ProjectionsLexer(CharStreams.fromString(src));
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
        ProjectionsLexer lexer = new ProjectionsLexer(CharStreams.fromString(src));
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
    void aWholeRecordExercisesTagFreeTextDiceAndInteger() {
        // One directive of each interesting shape, showing the FREE_TEXT and DICE_MODE push/pop
        // return cleanly to the default mode between directives.
        assertEquals(
                List.of("CODE=code:", "TAG=ACID",
                        "NAME=name:", "STRING=acid",
                        "TYPE=type:", "STRING=element",
                        "DENOMINATOR=denominator:", "SIMPLE_DICE=8+1d4",
                        "OBVIOUS=obvious:", "INTEGER=1",
                        "COLOUR=color:", "STRING=Slate"),
                tokens("code:ACID\nname:acid\ntype:element\ndenominator:8+1d4\nobvious:1\ncolor:Slate\n"));
    }

    @Test
    void freeTextCapturesSpacesAndPunctuationToEndOfLine() {
        assertEquals(
                List.of("DESC=desc:", "STRING=a fiery, blazing blast"),
                tokens("desc:a fiery, blazing blast\n"));
    }

    @Test
    void denominatorTakesBothABareNumberAndDiceNotation() {
        assertEquals(List.of("DENOMINATOR=denominator:", "SIMPLE_DICE=3"), tokens("denominator:3"));
        assertEquals(List.of("DENOMINATOR=denominator:", "SIMPLE_DICE=8+1d4"), tokens("denominator:8+1d4"));
    }

    @Test
    void recordCountAndSignedInteger() {
        assertEquals(List.of("RECORD_COUNT=record-count:", "INTEGER=56"), tokens("record-count:56"));
        // INTEGER is sign-capable (C7), even though projection values are non-negative in practice.
        assertEquals(List.of("NUMERATOR=numerator:", "INTEGER=-1"), tokens("numerator:-1"));
    }

    @Test
    void commentsAndBlankLinesAreSkippedAndCrlfIsClean() {
        String src = "# header\r\n\r\nrecord-count:56\r\ncode:ACID\r\n";
        assertEquals(
                List.of("RECORD_COUNT=record-count:", "INTEGER=56", "CODE=code:", "TAG=ACID"),
                tokens(src));
        assertEquals(0, lexErrors(src));
    }
}
