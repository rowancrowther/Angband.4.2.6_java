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

package uk.co.jackoftradesltd.backend.parser.grammars.imports.effectblock;

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
 * @author Rowan Crowther
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
                List.of("EFFECT=effect:", "TYPE=BALL", "SUBTYPE=FIRE", "R_P_EFFECT_INTEGER=2"),
                tokens("effect:BALL:FIRE:2"));
    }

    /**
     * The shapes of a number that C's {@code strtol}/{@code strtoul} in base 0 read, and the
     * lexer therefore has to hand on whole: a sign (a '-' is kept for the assembler to refuse on a
     * uint), a 0x prefix, hex letters with or without the prefix, a leading zero (octal, read by
     * the assembler), text after the digits, and a trailing blank.
     */
    private static final List<String> NUMBER_SHAPES =
            List.of("5", "-5", "+3", "0", "-0", "0x10", "0X1f", "010", "ff", "5abc", "5 ", "5\t");

    /**
     * Radius and other-parameter of {@code effect:} are two {@code R_P_EFFECT_INTEGER} tokens; the
     * colon between them is skipped in this mode.
     *
     * <p>Function radiusAndOtherAreTwoTokens coded on 261112, commented in full on 261112.
     */
    @Test
    void radiusAndOtherAreTwoTokens() {
        assertEquals(
                List.of("EFFECT=effect:", "TYPE=BOLT", "SUBTYPE=ARROW",
                        "R_P_EFFECT_INTEGER=-5", "R_P_EFFECT_INTEGER=+3"),
                tokens("effect:BOLT:ARROW:-5:+3"));
    }

    /**
     * Every shape in {@link #NUMBER_SHAPES} is one {@code R_P_EFFECT_INTEGER} holding the text as
     * written, as the radius and as the other parameter.
     *
     * <p>Function effectRadiusAndOtherReadEveryShapeOfNumber coded on 261112, commented in full on 261112.
     */
    @Test
    void effectRadiusAndOtherReadEveryShapeOfNumber() {
        for (String n : NUMBER_SHAPES) {
            assertEquals(List.of("EFFECT=effect:", "TYPE=BOLT", "SUBTYPE=ARROW", "R_P_EFFECT_INTEGER=" + n),
                    tokens("effect:BOLT:ARROW:" + n), "radius " + n);
            assertEquals(List.of("EFFECT=effect:", "TYPE=BOLT", "SUBTYPE=ARROW",
                            "R_P_EFFECT_INTEGER=1", "R_P_EFFECT_INTEGER=" + n),
                    tokens("effect:BOLT:ARROW:1:" + n), "other " + n);
        }
    }

    /**
     * A blank before the number is a lexer error where C skips it (accepted deviation, "Numbers in
     * every data file").
     *
     * <p>Function aBlankBeforeAnEffectRadiusIsALexerError coded on 261112, commented in full on 261112.
     */
    @Test
    void aBlankBeforeAnEffectRadiusIsALexerError() {
        assertTrue(lexErrors("effect:BOLT:ARROW: 5") > 0, "a blank before the radius");
        assertTrue(lexErrors("effect:BOLT:ARROW:1: 5") > 0, "a blank before the other parameter");
    }

    /**
     * {@code effect-yx:} gives two {@code YX_INTEGER} tokens, each of every shape in
     * {@link #NUMBER_SHAPES}.
     *
     * <p>Function effectYxReadsEveryShapeOfNumberForYAndX coded on 261112, commented in full on 261112.
     */
    @Test
    void effectYxReadsEveryShapeOfNumberForYAndX() {
        for (String n : NUMBER_SHAPES) {
            assertEquals(List.of("EFFECT_YX=effect-yx:", "YX_INTEGER=" + n, "YX_INTEGER=2"),
                    tokens("effect-yx:" + n + ":2"), "y " + n);
            assertEquals(List.of("EFFECT_YX=effect-yx:", "YX_INTEGER=1", "YX_INTEGER=" + n),
                    tokens("effect-yx:1:" + n), "x " + n);
        }
    }

    /**
     * A blank before the y value is a lexer error where C skips it.
     *
     * <p>Function aBlankBeforeAnEffectYxValueIsALexerError coded on 261112, commented in full on 261112.
     */
    @Test
    void aBlankBeforeAnEffectYxValueIsALexerError() {
        assertTrue(lexErrors("effect-yx: 1:2") > 0, "a blank before y");
    }

    /**
     * {@code EFFECT_INTEGER}, the default-mode number the importing grammars use for their own
     * integer directives, reads the same shapes.
     *
     * <p>Function effectIntegerReadsEveryShapeOfNumber coded on 261112, commented in full on 261112.
     */
    @Test
    void effectIntegerReadsEveryShapeOfNumber() {
        for (String n : NUMBER_SHAPES) {
            assertEquals(List.of("EFFECT_INTEGER=" + n), tokens(n), n);
        }
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
