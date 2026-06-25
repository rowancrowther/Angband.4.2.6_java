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

package uk.co.jackoftrades.backend.parser.grammars.imports.effectblock;

import org.antlr.v4.runtime.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the generated {@link EffectBlockLexer}.
 *
 * <p>These exercise the lexer in isolation (token-level), asserting the exact token
 * stream — symbolic name plus text — produced for each directive. They pin down the
 * behaviours we designed by hand: the simple-vs-complex dice classification by {@code $}
 * presence, the dedicated lexer modes for dice / expr / effect-msg, and that {@code time:}
 * only ever yields the simple dice token.
 *
 * @author ClaudeCode
 */
class EffectBlockLexerTest {

    /**
     * Tokenises {@code src}, returning one {@code "SYMBOLIC_NAME=text"} entry per token (EOF excluded).
     */
    private List<String> tokens(String src) {
        EffectBlockLexer lexer = new EffectBlockLexer(CharStreams.fromString(src));
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
        EffectBlockLexer lexer = new EffectBlockLexer(CharStreams.fromString(src));
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
    void effectHeaderTokenises() {
        assertEquals(
                List.of("EFFECT=effect:", "UCASE=BALL", "COLON=:", "UCASE=FIRE", "COLON=:", "INTEGER=2"),
                tokens("effect:BALL:FIRE:2"));
    }

    @Test
    void simpleDiceValueIsTheSimpleToken() {
        assertEquals(List.of("DICE=dice:", "DICE_SIMPLE_VALUE=2d8"), tokens("dice:2d8"));
    }

    @Test
    void complexDiceValueIsTheComplexToken() {
        assertEquals(List.of("DICE=dice:", "DICE_COMPLEX_VALUE=$Dd$S"), tokens("dice:$Dd$S"));
    }

    @Test
    void timeUsesTheSimpleDiceToken() {
        assertEquals(List.of("TIME=time:", "DICE_SIMPLE_VALUE=5+1d5"), tokens("time:5+1d5"));
    }

    @Test
    void exprTokenisesIntoItsFields() {
        assertEquals(
                List.of("EXPR=expr:", "EXPR_CHAR=D", "EXPR_COLON=:", "EXPR_UCASE=PLAYER_LEVEL",
                        "EXPR_COLON=:", "EXPR_OP=* 2"),
                tokens("expr:D:PLAYER_LEVEL:* 2"));
    }

    @Test
    void effectMsgCapturesFreeText() {
        assertEquals(
                List.of("EFFECT_MESSAGE=effect-msg:", "FREE_TEXT=taking warg form"),
                tokens("effect-msg:taking warg form"));
    }

    @Test
    void invalidDiceValueRaisesALexerError() {
        // "zzz" is not a valid dice body in DICE_STRING_MODE.
        assertTrue(lexErrors("dice:zzz") > 0, "a non-dice value after dice: should be a lexer error");
    }

    @Test
    void basePlusMBonusIsSimpleDiceToken() {
        // "300+m35" — base then M-bonus, no dice part; no $, so it is the simple token.
        assertEquals(List.of("DICE=dice:", "DICE_SIMPLE_VALUE=300+m35"), tokens("dice:300+m35"));
    }

    @Test
    void complexBasePlusMBonusIsComplexDiceToken() {
        // "$B+m$M" — base then M-bonus, both variables, so it is the complex token.
        assertEquals(List.of("DICE=dice:", "DICE_COMPLEX_VALUE=$B+m$M"), tokens("dice:$B+m$M"));
    }

    @Test
    void basePlusWithNoBonusRaisesALexerError() {
        // "300+" has a base and '+' but no mandatory M-bonus, so the '+' is unconsumed -> lexer error.
        assertTrue(lexErrors("dice:300+") > 0, "base '+' with no following M-bonus must not be accepted");
    }
}
