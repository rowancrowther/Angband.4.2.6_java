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
 *    Java code copyright (c) Rowan Crowther 2026
 */

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/parser/grammars/BlowEffect.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.bloweffect;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link BlowEffectParser}.
 */
public interface BlowEffectListener extends ParseTreeListener {
    /**
     * Enter a parse tree produced by {@link BlowEffectParser#name}.
     *
     * @param ctx the parse tree
     */
    void enterName(BlowEffectParser.NameContext ctx);

    /**
     * Exit a parse tree produced by {@link BlowEffectParser#name}.
     *
     * @param ctx the parse tree
     */
    void exitName(BlowEffectParser.NameContext ctx);

    /**
     * Enter a parse tree produced by {@link BlowEffectParser#power}.
     *
     * @param ctx the parse tree
     */
    void enterPower(BlowEffectParser.PowerContext ctx);

    /**
     * Exit a parse tree produced by {@link BlowEffectParser#power}.
     *
     * @param ctx the parse tree
     */
    void exitPower(BlowEffectParser.PowerContext ctx);

    /**
     * Enter a parse tree produced by {@link BlowEffectParser#eval}.
     *
     * @param ctx the parse tree
     */
    void enterEval(BlowEffectParser.EvalContext ctx);

    /**
     * Exit a parse tree produced by {@link BlowEffectParser#eval}.
     *
     * @param ctx the parse tree
     */
    void exitEval(BlowEffectParser.EvalContext ctx);

    /**
     * Enter a parse tree produced by {@link BlowEffectParser#desc}.
     *
     * @param ctx the parse tree
     */
    void enterDesc(BlowEffectParser.DescContext ctx);

    /**
     * Exit a parse tree produced by {@link BlowEffectParser#desc}.
     *
     * @param ctx the parse tree
     */
    void exitDesc(BlowEffectParser.DescContext ctx);

    /**
     * Enter a parse tree produced by {@link BlowEffectParser#loreColourBase}.
     *
     * @param ctx the parse tree
     */
    void enterLoreColourBase(BlowEffectParser.LoreColourBaseContext ctx);

    /**
     * Exit a parse tree produced by {@link BlowEffectParser#loreColourBase}.
     *
     * @param ctx the parse tree
     */
    void exitLoreColourBase(BlowEffectParser.LoreColourBaseContext ctx);

    /**
     * Enter a parse tree produced by {@link BlowEffectParser#loreColourResist}.
     *
     * @param ctx the parse tree
     */
    void enterLoreColourResist(BlowEffectParser.LoreColourResistContext ctx);

    /**
     * Exit a parse tree produced by {@link BlowEffectParser#loreColourResist}.
     *
     * @param ctx the parse tree
     */
    void exitLoreColourResist(BlowEffectParser.LoreColourResistContext ctx);

    /**
     * Enter a parse tree produced by {@link BlowEffectParser#loreColourImmune}.
     *
     * @param ctx the parse tree
     */
    void enterLoreColourImmune(BlowEffectParser.LoreColourImmuneContext ctx);

    /**
     * Exit a parse tree produced by {@link BlowEffectParser#loreColourImmune}.
     *
     * @param ctx the parse tree
     */
    void exitLoreColourImmune(BlowEffectParser.LoreColourImmuneContext ctx);

    /**
     * Enter a parse tree produced by {@link BlowEffectParser#effectType}.
     *
     * @param ctx the parse tree
     */
    void enterEffectType(BlowEffectParser.EffectTypeContext ctx);

    /**
     * Exit a parse tree produced by {@link BlowEffectParser#effectType}.
     *
     * @param ctx the parse tree
     */
    void exitEffectType(BlowEffectParser.EffectTypeContext ctx);

    /**
     * Enter a parse tree produced by {@link BlowEffectParser#resist}.
     *
     * @param ctx the parse tree
     */
    void enterResist(BlowEffectParser.ResistContext ctx);

    /**
     * Exit a parse tree produced by {@link BlowEffectParser#resist}.
     *
     * @param ctx the parse tree
     */
    void exitResist(BlowEffectParser.ResistContext ctx);

    /**
     * Enter a parse tree produced by {@link BlowEffectParser#lashType}.
     *
     * @param ctx the parse tree
     */
    void enterLashType(BlowEffectParser.LashTypeContext ctx);

    /**
     * Exit a parse tree produced by {@link BlowEffectParser#lashType}.
     *
     * @param ctx the parse tree
     */
    void exitLashType(BlowEffectParser.LashTypeContext ctx);

    /**
     * Enter a parse tree produced by {@link BlowEffectParser#blowEffect}.
     *
     * @param ctx the parse tree
     */
    void enterBlowEffect(BlowEffectParser.BlowEffectContext ctx);

    /**
     * Exit a parse tree produced by {@link BlowEffectParser#blowEffect}.
     *
     * @param ctx the parse tree
     */
    void exitBlowEffect(BlowEffectParser.BlowEffectContext ctx);

    /**
     * Enter a parse tree produced by {@link BlowEffectParser#file}.
     *
     * @param ctx the parse tree
     */
    void enterFile(BlowEffectParser.FileContext ctx);

    /**
     * Exit a parse tree produced by {@link BlowEffectParser#file}.
     *
     * @param ctx the parse tree
     */
    void exitFile(BlowEffectParser.FileContext ctx);
}