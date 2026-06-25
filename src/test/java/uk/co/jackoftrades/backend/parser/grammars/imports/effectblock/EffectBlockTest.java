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

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the generated {@link EffectBlock} parser (paired with {@link EffectBlockLexer}).
 *
 * <p>These drive the {@code effectBlock} start rule on small inline sources and assert on the
 * rule context's {@code returns[...]} fields and on the syntax-error count (collected via a
 * custom error listener). They lock in the design decisions verified during development:
 * <ul>
 *   <li>multi-{@code expr} caret accumulation for a complex dice;</li>
 *   <li>the biconditional — a {@code $}-dice with no {@code expr:} is a parse error;</li>
 *   <li>{@code effect-msg:} captured at the effect level;</li>
 *   <li>a signed (negative) {@code other} parameter;</li>
 *   <li>the {@code effect-yx:} y-then-x ordering.</li>
 * </ul>
 *
 * @author ClaudeCode
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
        EffectBlock parser = new EffectBlock(new CommonTokenStream(lexer));
        parser.removeErrorListeners();
        parser.addErrorListener(errors);
        return parser;
    }

    @Test
    void complexDiceWithTwoExprsAndTime() {
        Errors errors = new Errors();
        EffectBlock.EffectBlockContext ctx = parser(
                "effect:BALL:FIRE:2\n"
                        + "dice:$Dd$S\n"
                        + "expr:D:PLAYER_LEVEL:* 2\n"
                        + "expr:S:DUNGEON_LEVEL:/ 2\n"
                        + "time:5+1d5\n",
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
        assertEquals("5+1d5", ctx.timeDiceString);
    }

    @Test
    void simpleDiceWithNoExpr() {
        Errors errors = new Errors();
        EffectBlock.EffectBlockContext ctx = parser("effect:TELEPORT\ndice:10\n", errors).effectBlock();

        assertTrue(errors.messages.isEmpty(), () -> "unexpected errors: " + errors.messages);
        assertEquals("TELEPORT", ctx.typeInit);
        assertEquals("10", ctx.diceString);
        assertNull(ctx.timeDiceString);
        assertEquals("", ctx.expressionChars);
    }

    @Test
    void complexDiceWithoutExprIsRejected() {
        Errors errors = new Errors();
        parser("effect:BALL:FIRE:2\ndice:$B\n", errors).effectBlock();

        assertFalse(errors.messages.isEmpty(),
                "a $-dice with no expr: must be a parse error (the complex <=> expr biconditional)");
    }

    @Test
    void effectMessageIsCaptured() {
        Errors errors = new Errors();
        EffectBlock.EffectBlockContext ctx = parser(
                "effect:DAMAGE\ndice:1d$S\nexpr:S:PLAYER_LEVEL:+ 0\neffect-msg:taking warg form\n",
                errors).effectBlock();

        assertTrue(errors.messages.isEmpty(), () -> "unexpected errors: " + errors.messages);
        assertEquals("1d$S", ctx.diceString);
        assertEquals("taking warg form", ctx.effectMessage);
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
}
