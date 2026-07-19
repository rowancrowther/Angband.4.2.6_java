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

// Generated from BlowEffectGrammar.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.grammars.bloweffect;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link BlowEffectGrammar}.
 */
public interface BlowEffectGrammarListener extends ParseTreeListener {
    /**
     * Enter a parse tree produced by {@link BlowEffectGrammar#recordCount}.
     *
     * @param ctx the parse tree
     */
    void enterRecordCount(BlowEffectGrammar.RecordCountContext ctx);

    /**
     * Exit a parse tree produced by {@link BlowEffectGrammar#recordCount}.
     *
     * @param ctx the parse tree
     */
    void exitRecordCount(BlowEffectGrammar.RecordCountContext ctx);

    /**
     * Enter a parse tree produced by {@link BlowEffectGrammar#name}.
     *
     * @param ctx the parse tree
     */
    void enterName(BlowEffectGrammar.NameContext ctx);

    /**
     * Exit a parse tree produced by {@link BlowEffectGrammar#name}.
     *
     * @param ctx the parse tree
     */
    void exitName(BlowEffectGrammar.NameContext ctx);

    /**
     * Enter a parse tree produced by {@link BlowEffectGrammar#power}.
     *
     * @param ctx the parse tree
     */
    void enterPower(BlowEffectGrammar.PowerContext ctx);

    /**
     * Exit a parse tree produced by {@link BlowEffectGrammar#power}.
     *
     * @param ctx the parse tree
     */
    void exitPower(BlowEffectGrammar.PowerContext ctx);

    /**
     * Enter a parse tree produced by {@link BlowEffectGrammar#eval}.
     *
     * @param ctx the parse tree
     */
    void enterEval(BlowEffectGrammar.EvalContext ctx);

    /**
     * Exit a parse tree produced by {@link BlowEffectGrammar#eval}.
     *
     * @param ctx the parse tree
     */
    void exitEval(BlowEffectGrammar.EvalContext ctx);

    /**
     * Enter a parse tree produced by {@link BlowEffectGrammar#desc}.
     *
     * @param ctx the parse tree
     */
    void enterDesc(BlowEffectGrammar.DescContext ctx);

    /**
     * Exit a parse tree produced by {@link BlowEffectGrammar#desc}.
     *
     * @param ctx the parse tree
     */
    void exitDesc(BlowEffectGrammar.DescContext ctx);

    /**
     * Enter a parse tree produced by {@link BlowEffectGrammar#loreColourBase}.
     *
     * @param ctx the parse tree
     */
    void enterLoreColourBase(BlowEffectGrammar.LoreColourBaseContext ctx);

    /**
     * Exit a parse tree produced by {@link BlowEffectGrammar#loreColourBase}.
     *
     * @param ctx the parse tree
     */
    void exitLoreColourBase(BlowEffectGrammar.LoreColourBaseContext ctx);

    /**
     * Enter a parse tree produced by {@link BlowEffectGrammar#loreColourResist}.
     *
     * @param ctx the parse tree
     */
    void enterLoreColourResist(BlowEffectGrammar.LoreColourResistContext ctx);

    /**
     * Exit a parse tree produced by {@link BlowEffectGrammar#loreColourResist}.
     *
     * @param ctx the parse tree
     */
    void exitLoreColourResist(BlowEffectGrammar.LoreColourResistContext ctx);

    /**
     * Enter a parse tree produced by {@link BlowEffectGrammar#loreColourImmune}.
     *
     * @param ctx the parse tree
     */
    void enterLoreColourImmune(BlowEffectGrammar.LoreColourImmuneContext ctx);

    /**
     * Exit a parse tree produced by {@link BlowEffectGrammar#loreColourImmune}.
     *
     * @param ctx the parse tree
     */
    void exitLoreColourImmune(BlowEffectGrammar.LoreColourImmuneContext ctx);

    /**
     * Enter a parse tree produced by {@link BlowEffectGrammar#effectType}.
     *
     * @param ctx the parse tree
     */
    void enterEffectType(BlowEffectGrammar.EffectTypeContext ctx);

    /**
     * Exit a parse tree produced by {@link BlowEffectGrammar#effectType}.
     *
     * @param ctx the parse tree
     */
    void exitEffectType(BlowEffectGrammar.EffectTypeContext ctx);

    /**
     * Enter a parse tree produced by {@link BlowEffectGrammar#resist}.
     *
     * @param ctx the parse tree
     */
    void enterResist(BlowEffectGrammar.ResistContext ctx);

    /**
     * Exit a parse tree produced by {@link BlowEffectGrammar#resist}.
     *
     * @param ctx the parse tree
     */
    void exitResist(BlowEffectGrammar.ResistContext ctx);

    /**
     * Enter a parse tree produced by {@link BlowEffectGrammar#lashType}.
     *
     * @param ctx the parse tree
     */
    void enterLashType(BlowEffectGrammar.LashTypeContext ctx);

    /**
     * Exit a parse tree produced by {@link BlowEffectGrammar#lashType}.
     *
     * @param ctx the parse tree
     */
    void exitLashType(BlowEffectGrammar.LashTypeContext ctx);

    /**
     * Enter a parse tree produced by {@link BlowEffectGrammar#blowEffect}.
     *
     * @param ctx the parse tree
     */
    void enterBlowEffect(BlowEffectGrammar.BlowEffectContext ctx);

    /**
     * Exit a parse tree produced by {@link BlowEffectGrammar#blowEffect}.
     *
     * @param ctx the parse tree
     */
    void exitBlowEffect(BlowEffectGrammar.BlowEffectContext ctx);

    /**
     * Enter a parse tree produced by {@link BlowEffectGrammar#file}.
     *
     * @param ctx the parse tree
     */
    void enterFile(BlowEffectGrammar.FileContext ctx);

    /**
     * Exit a parse tree produced by {@link BlowEffectGrammar#file}.
     *
     * @param ctx the parse tree
     */
    void exitFile(BlowEffectGrammar.FileContext ctx);
}