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
package uk.co.jackoftradesltd.backend.parser;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import uk.co.jackoftradesltd.channel.parser.ParseResult;
import uk.co.jackoftradesltd.middle.cave.TrapKind;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Pins for the extra-effect lines of {@code trap.txt}: {@code effect-xtra:}, {@code effect-yx-xtra:},
 * {@code dice-xtra:} and {@code expr-xtra:}. Each acts on the last entry of the extra list wherever it
 * falls in the record, as the {@code _xtra} twins in {@code init.c} do, and never touches the primary list.
 *
 * <p>Stage 6 item 2.iv of {@code docs/Issue_Approach_Order.md}. Class coded on 261111.
 *
 * @author Rowan Crowther
 */
class TrapReaderXtraTest {

    @TempDir
    Path tempDir;

    private ParseResult<TrapKind> load(String name, String content) throws IOException {
        Path p = tempDir.resolve(name);
        Files.writeString(p, content);
        return new TrapReader().parseWithResults(p.toString());
    }

    private static Object xtra(ParseResult<TrapKind> r, int i) {
        return r.items().getFirst().getEffectXtra().get(i);
    }

    private static Object primary(ParseResult<TrapKind> r, int i) {
        return r.items().getFirst().getEffect().get(i);
    }

    /** C: {@code parse_trap_dice_xtra} frees the old dice and installs the new one, so the second wins. */
    @Test
    void aSecondDiceXtraLineReplacesTheFirstAsInC() throws Exception {
        ParseResult<TrapKind> r = load("dice-xtra-twice.txt",
                "name:a:b\neffect-xtra:DAMAGE\ndice-xtra:1d4\ndice-xtra:2d6\n");

        assertEquals(1, r.items().getFirst().getEffectXtra().size());
        assertEquals("0,0,2d6", EffectProbe.yxBase(xtra(r, 0)));
    }

    /** C: the four line kinds come in any order, so {@code effect-yx-xtra:} may follow {@code dice-xtra:}. */
    @Test
    void effectYxXtraAfterDiceXtraLoadsAsInC() throws Exception {
        ParseResult<TrapKind> r = load("yx-after-dice-xtra.txt",
                "name:a:b\neffect-xtra:DAMAGE\ndice-xtra:5\neffect-yx-xtra:1:2\n");

        assertEquals("1,2,5", EffectProbe.yxBase(xtra(r, 0)));
    }

    @Test
    void effectYxXtraBeforeDiceXtraLoadsAsInC() throws Exception {
        ParseResult<TrapKind> r = load("yx-before-dice-xtra.txt",
                "name:a:b\neffect-xtra:DAMAGE\neffect-yx-xtra:1:2\ndice-xtra:5\n");

        assertEquals("1,2,5", EffectProbe.yxBase(xtra(r, 0)));
    }

    /** C: each {@code expr-xtra:} binds to the current dice, so two lines both land. */
    @Test
    void twoExprXtraLinesBothBindAsInC() throws Exception {
        ParseResult<TrapKind> r = load("expr-xtra-twice.txt",
                "name:a:b\neffect-xtra:DAMAGE\ndice-xtra:$Bd$S\n"
                        + "expr-xtra:S:DUNGEON_LEVEL:/ 2\nexpr-xtra:B:PLAYER_HP:* 2\n");

        assertEquals("0,0,$Bd$S", EffectProbe.yxBase(xtra(r, 0)));
        assertEquals("S:EFB_DUNGEON_LEVEL:/ 2|B:EFB_PLAYER_HP:* 2", EffectProbe.expressions(xtra(r, 0)));
    }

    /** C: {@code parse_trap_expr_xtra} does nothing when the last extra effect has no dice. */
    @Test
    void anExprXtraLineWithNoDiceBeforeItIsIgnoredAsInC() throws Exception {
        ParseResult<TrapKind> r = load("expr-xtra-no-dice.txt",
                "name:a:b\neffect-xtra:DAMAGE\nexpr-xtra:S:DUNGEON_LEVEL:/ 2\n");

        assertEquals(1, r.items().getFirst().getEffectXtra().size());
        assertEquals("0,0,", EffectProbe.yxBase(xtra(r, 0)));
        assertEquals("", EffectProbe.expressions(xtra(r, 0)));
    }

    /** C: with no extra effect yet, {@code parse_trap_expr_xtra} does nothing and gives no error. */
    @Test
    void anExprXtraLineBeforeAnyExtraEffectIsDroppedAsInC() throws Exception {
        ParseResult<TrapKind> r = load("expr-xtra-first.txt",
                "name:a:b\nexpr-xtra:S:DUNGEON_LEVEL:/ 2\n");

        assertEquals(0, r.items().getFirst().getEffectXtra().size());
    }

    /** C: with no extra effect yet, {@code parse_trap_effect_yx_xtra} does nothing and gives no error. */
    @Test
    void anEffectYxXtraLineBeforeAnyExtraEffectIsDroppedAsInC() throws Exception {
        ParseResult<TrapKind> r = load("yx-xtra-first.txt",
                "name:a:b\neffect-yx-xtra:1:2\neffect-xtra:DAMAGE\n");

        assertEquals("0,0,", EffectProbe.yxBase(xtra(r, 0)));
    }

    /** C: the extra twins walk the extra list only, so they never reach a primary {@code effect:}. */
    @Test
    void anExprXtraAfterAPrimaryEffectDoesNotReachThePrimaryEffectAsInC() throws Exception {
        ParseResult<TrapKind> r = load("expr-xtra-after-primary.txt",
                "name:a:b\neffect:DAMAGE\ndice:$Bd$S\nexpr:S:DUNGEON_LEVEL:/ 2\n"
                        + "effect-xtra:DAMAGE\ndice-xtra:$Bd$S\nexpr-xtra:B:PLAYER_HP:* 2\n");

        assertEquals("S:EFB_DUNGEON_LEVEL:/ 2", EffectProbe.expressions(primary(r, 0)));
        assertEquals("B:EFB_PLAYER_HP:* 2", EffectProbe.expressions(xtra(r, 0)));
    }

    @Test
    void aDiceXtraAndEffectYxXtraAfterAPrimaryEffectDoNotReachThePrimaryEffectAsInC() throws Exception {
        ParseResult<TrapKind> r = load("xtra-leaves-primary.txt",
                "name:a:b\neffect:DAMAGE\neffect-xtra:DAMAGE\ndice-xtra:3d3\neffect-yx-xtra:4:5\n");

        assertEquals("0,0,", EffectProbe.yxBase(primary(r, 0)));
        assertEquals("4,5,3d3", EffectProbe.yxBase(xtra(r, 0)));
    }

    /** C: a replaced dice takes its {@code expr-xtra:} lines with it. */
    @Test
    void aReplacedDiceXtraTakesItsExprXtraLinesWithItAsInC() throws Exception {
        ParseResult<TrapKind> r = load("dice-xtra-replaced.txt",
                "name:a:b\neffect-xtra:DAMAGE\ndice-xtra:$Bd$S\nexpr-xtra:S:DUNGEON_LEVEL:/ 2\n"
                        + "dice-xtra:$Bd$S\n");

        assertEquals("", EffectProbe.expressions(xtra(r, 0)));
    }

    /** C: the lines separated from their effect by another directive still reach the last extra effect. */
    @Test
    void xtraLinesSeparatedFromTheirEffectByAnotherDirectiveStillReachItAsInC() throws Exception {
        ParseResult<TrapKind> r = load("xtra-separated.txt",
                "name:a:b\neffect-xtra:DAMAGE\nmsg-xtra:boom\ndice-xtra:2d2\nflags:TRAP\neffect-yx-xtra:6:7\n");

        assertEquals("6,7,2d2", EffectProbe.yxBase(xtra(r, 0)));
    }

    /** Two extra effects: each line goes to the last one only. */
    @Test
    void xtraLinesGoToTheLastExtraEffectOnlyAsInC() throws Exception {
        ParseResult<TrapKind> r = load("two-xtra.txt",
                "name:a:b\neffect-xtra:DAMAGE\ndice-xtra:1d1\neffect-xtra:DAMAGE\ndice-xtra:2d2\n");

        assertEquals("0,0,1d1", EffectProbe.yxBase(xtra(r, 0)));
        assertEquals("0,0,2d2", EffectProbe.yxBase(xtra(r, 1)));
    }
}
