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
 * Unit tests for the generated {@link EffectBlock} parser (paired with {@link EffectBlockLexer}).
 *
 * <p>These drive the {@code effectBlock} start rule on small inline sources and assert on the
 * rule context's {@code returns[...]} fields and on the syntax-error count (collected via a
 * custom error listener). They lock in the design decisions verified during development:
 * <ul>
 *   <li>multi-{@code expr} caret accumulation for a complex dice;</li>
 *   <li>a {@code $}-dice with no {@code expr:} straight after it parses, as in C, which binds
 *       the variables later by name (or never);</li>
 *   <li>{@code effect-msg:} left outside the block, with its own {@code effectMsg} rule;</li>
 *   <li>a signed (negative) {@code other} parameter;</li>
 *   <li>the {@code effect-yx:} y-then-x ordering.</li>
 * </ul>
 *
 * @author Rowan Crowther
 */
class EffectBlockTest {

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
     * Builds an {@link EffectBlock} parser over {@code src}, routing errors to {@code errors}.
     */
    private EffectBlock parser(String src, Errors errors) {
        EffectBlockLexer lexer = new EffectBlockLexer(CharStreams.fromString(src));
        lexer.removeErrorListeners();
        lexer.addErrorListener(errors);
        EffectBlock parser = new EffectBlock(new CommonTokenStream(lexer));
        parser.removeErrorListeners();
        parser.addErrorListener(errors);
        return parser;
    }

    @Test
    void complexDiceWithTwoExprs() {
        Errors errors = new Errors();
        EffectBlock.EffectBlockContext ctx = parser(
                "effect:BALL:FIRE:2\n"
                        + "dice:$Dd$S\n"
                        + "expr:D:PLAYER_LEVEL:* 2\n"
                        + "expr:S:DUNGEON_LEVEL:/ 2\n",
                errors).effectBlock();

        assertTrue(errors.messages.isEmpty(), () -> "unexpected errors: " + errors.messages);
        assertEquals("BALL", ctx.typeInit);
        assertEquals("FIRE", ctx.subtypeWrapperInit);
        assertEquals("2", ctx.radius);
        assertEquals("$Dd$S", ctx.diceString);
        // two expr: lines accumulate caret-delimited, in order
        assertEquals("D^S", ctx.expressionChars);
        assertEquals("PLAYER_LEVEL^DUNGEON_LEVEL", ctx.expressionBase);
        assertEquals("* 2^/ 2", ctx.expressionOperation);
    }

    @Test
    void simpleDiceWithNoExpr() {
        Errors errors = new Errors();
        EffectBlock.EffectBlockContext ctx = parser("effect:TELEPORT\ndice:10\n", errors).effectBlock();

        assertTrue(errors.messages.isEmpty(), () -> "unexpected errors: " + errors.messages);
        assertEquals("TELEPORT", ctx.typeInit);
        assertEquals("10", ctx.diceString);
        assertEquals("", ctx.expressionChars);
    }

    /**
     * {@code time:} belongs to the record that owns the effect, not to the effect, so
     * {@code effectBlock} stops at it: the block parses the {@code effect:} and {@code dice:}
     * lines and leaves the {@code time:} token for the calling grammar.
     */
    @Test
    void effectBlockStopsBeforeTime() {
        Errors errors = new Errors();
        EffectBlock parser = parser("effect:DAMAGE\ndice:5+1d5\ntime:5+1d5\n", errors);
        EffectBlock.EffectBlockContext ctx = parser.effectBlock();

        assertTrue(errors.messages.isEmpty(), () -> "unexpected errors: " + errors.messages);
        assertEquals("5+1d5", ctx.diceString);
        assertEquals(EffectBlockLexer.TIME, parser.getCurrentToken().getType(),
                "the time: line must be left unconsumed for the owning record");
    }

    /**
     * The standalone {@code time} rule is still there for importing grammars that want it.
     */
    @Test
    void standaloneTimeRuleCapturesDice() {
        Errors errors = new Errors();
        EffectBlock.TimeContext ctx = parser("time:5+1d5\n", errors).time();

        assertTrue(errors.messages.isEmpty(), () -> "unexpected errors: " + errors.messages);
        assertEquals("5+1d5", ctx.timeStr);
    }

    /**
     * C ({@code dice_parse_string} in {@code z-dice.c}) accepts a {@code $} variable and records it
     * as waiting for a value; nothing needs binding at parse time. So a complex {@code dice:} with
     * no {@code expr:} after it is legal, and reaches the caller as a complex string with empty
     * expression strings.
     *
     * <p>Function complexDiceWithoutExprLoadsAsInC coded on 261111, commented in full on 261111.
     */
    @Test
    void complexDiceWithoutExprLoadsAsInC() {
        Errors errors = new Errors();
        EffectBlock.EffectBlockContext ctx = parser("effect:BALL:FIRE:2\ndice:$B\n", errors).effectBlock();

        assertTrue(errors.messages.isEmpty(), () -> "unexpected errors: " + errors.messages);
        assertEquals("$B", ctx.diceString);
        assertEquals("", ctx.expressionChars);
        assertEquals("", ctx.expressionBase);
        assertEquals("", ctx.expressionOperation);
    }

    /**
     * The block ends cleanly at the next {@code effect:} when the complex dice before it had no
     * {@code expr:}: the second effect starts a new block, which the caller loops on.
     *
     * <p>Function complexDiceWithoutExprStopsBeforeTheNextEffect coded on 261111, commented in full on 261111.
     */
    @Test
    void complexDiceWithoutExprStopsBeforeTheNextEffect() {
        Errors errors = new Errors();
        EffectBlock parser = parser("effect:DAMAGE\ndice:$B+1d4\neffect:WAKE\n", errors);
        EffectBlock.EffectBlockContext ctx = parser.effectBlock();

        assertTrue(errors.messages.isEmpty(), () -> "unexpected errors: " + errors.messages);
        assertEquals("$B+1d4", ctx.diceString);
        assertEquals("", ctx.expressionChars);
        assertEquals(EffectBlockLexer.EFFECT, parser.getCurrentToken().getType(),
                "the second effect: line must be left unconsumed");
    }

    @Test
    void effectBlockStopsBeforeAnEffectMsgLine() {
        Errors errors = new Errors();
        EffectBlock parser = parser(
                "effect:DAMAGE\ndice:1d$S\nexpr:S:PLAYER_LEVEL:+ 0\neffect-msg:taking warg form\n",
                errors);
        EffectBlock.EffectBlockContext ctx = parser.effectBlock();

        assertTrue(errors.messages.isEmpty(), () -> "unexpected errors: " + errors.messages);
        assertEquals("1d$S", ctx.diceString);
        assertEquals(EffectBlockLexer.EFFECT_MESSAGE, parser.getCurrentToken().getType(),
                "effect-msg: is not part of the block; Shape and PlayerClass take it themselves");
    }

    @Test
    void effectMsgRuleCapturesItsFreeText() {
        Errors errors = new Errors();
        EffectBlock.EffectMsgContext ctx =
                parser("effect-msg:taking warg form\n", errors).effectMsg();

        assertTrue(errors.messages.isEmpty(), () -> "unexpected errors: " + errors.messages);
        assertEquals("taking warg form", ctx.message);
    }

    @Test
    void negativeOtherParameterIsAccepted() {
        Errors errors = new Errors();
        EffectBlock.EffectBlockContext ctx =
                parser("effect:BOLT_OR_BEAM:MISSILE:0:-10\n", errors).effectBlock();

        assertTrue(errors.messages.isEmpty(), () -> "unexpected errors: " + errors.messages);
        assertEquals("0", ctx.radius);
        assertEquals("-10", ctx.other);
    }

    @Test
    void effectYxIsYThenX() {
        Errors errors = new Errors();
        EffectBlock.EffectBlockContext ctx = parser("effect:DAMAGE\neffect-yx:3:4\n", errors).effectBlock();

        assertTrue(errors.messages.isEmpty(), () -> "unexpected errors: " + errors.messages);
        assertEquals("3", ctx.yVal);
        assertEquals("4", ctx.xVal);
    }

    @Test
    void simpleBasePlusMBonusDice() {
        // "base + M-bonus, no dice" (e.g. 300+m35) is a real gamedata shape; the value must survive whole.
        Errors errors = new Errors();
        EffectBlock.EffectBlockContext ctx = parser("effect:DAMAGE\ndice:300+m35\n", errors).effectBlock();

        assertTrue(errors.messages.isEmpty(), () -> "unexpected errors: " + errors.messages);
        assertEquals("300+m35", ctx.diceString);
    }

    @Test
    void complexBasePlusMBonusDice() {
        // The same shape with variable base and bonus (e.g. $B+m$M) is the complex form; needs its expr: lines.
        Errors errors = new Errors();
        EffectBlock.EffectBlockContext ctx = parser(
                "effect:DAMAGE\n"
                        + "dice:$B+m$M\n"
                        + "expr:B:PLAYER_LEVEL:* 2\n"
                        + "expr:M:PLAYER_LEVEL:/ 5\n",
                errors).effectBlock();

        assertTrue(errors.messages.isEmpty(), () -> "unexpected errors: " + errors.messages);
        assertEquals("$B+m$M", ctx.diceString);
    }

    /**
     * Radius and other come through as the text written, whatever the shape: sign, 0x prefix,
     * leading zero, hex letters, trailing text. Turning the text into a number is the assembler's
     * job, so nothing is refused here.
     *
     * <p>Function radiusAndOtherKeepTheTextAsWritten coded on 261112, commented in full on 261112.
     */
    @Test
    void radiusAndOtherKeepTheTextAsWritten() {
        String[][] cases = {{"-5", "+3"}, {"0x10", "010"}, {"ff", "5abc"}, {"0X1f", "-0"}};
        for (String[] c : cases) {
            Errors errors = new Errors();
            EffectBlock.EffectBlockContext ctx =
                    parser("effect:BOLT:ARROW:" + c[0] + ":" + c[1] + "\n", errors).effectBlock();

            assertTrue(errors.messages.isEmpty(), () -> "unexpected errors: " + errors.messages);
            assertEquals("ARROW", ctx.subtypeWrapperInit);
            assertEquals(c[0], ctx.radius);
            assertEquals(c[1], ctx.other);
        }
    }

    /**
     * A radius with no other parameter leaves {@code other} empty.
     *
     * <p>Function aRadiusAloneLeavesOtherEmpty coded on 261112, commented in full on 261112.
     */
    @Test
    void aRadiusAloneLeavesOtherEmpty() {
        Errors errors = new Errors();
        EffectBlock.EffectBlockContext ctx = parser("effect:BALL:FIRE:2\n", errors).effectBlock();

        assertTrue(errors.messages.isEmpty(), () -> "unexpected errors: " + errors.messages);
        assertEquals("2", ctx.radius);
        assertEquals("", ctx.other);
    }

    /**
     * {@code effect-yx:} values come through as the text written, whatever the shape.
     *
     * <p>Function effectYxKeepsTheTextAsWritten coded on 261112, commented in full on 261112.
     */
    @Test
    void effectYxKeepsTheTextAsWritten() {
        String[][] cases = {{"-1", "+2"}, {"0x10", "010"}, {"ff", "5abc"}};
        for (String[] c : cases) {
            Errors errors = new Errors();
            EffectBlock.EffectBlockContext ctx =
                    parser("effect:DAMAGE\neffect-yx:" + c[0] + ":" + c[1] + "\n", errors).effectBlock();

            assertTrue(errors.messages.isEmpty(), () -> "unexpected errors: " + errors.messages);
            assertEquals(c[0], ctx.yVal);
            assertEquals(c[1], ctx.xVal);
        }
    }

    /**
     * {@code effect-yx:} with only one value is a syntax error, as in C where both are required.
     *
     * <p>Function effectYxWithOneValueIsASyntaxError coded on 261112, commented in full on 261112.
     */
    @Test
    void effectYxWithOneValueIsASyntaxError() {
        Errors errors = new Errors();
        parser("effect:DAMAGE\neffect-yx:1\n", errors).effectBlock();

        assertTrue(!errors.messages.isEmpty(), "effect-yx:1 has no x value");
    }

    /**
     * A blank before the radius or y is reported as an error through the lexer.
     *
     * <p>Function aBlankBeforeARadiusOrYIsAnError coded on 261112, commented in full on 261112.
     */
    @Test
    void aBlankBeforeARadiusOrYIsAnError() {
        Errors radius = new Errors();
        parser("effect:BOLT:ARROW: 5\n", radius).effectBlock();
        assertTrue(!radius.messages.isEmpty(), "a blank before the radius");

        Errors yx = new Errors();
        parser("effect:DAMAGE\neffect-yx: 1:2\n", yx).effectBlock();
        assertTrue(!yx.messages.isEmpty(), "a blank before y");
    }
}
