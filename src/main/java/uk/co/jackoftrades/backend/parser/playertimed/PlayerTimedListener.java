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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/parser/grammars/PlayerTimed.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.playertimed;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link PlayerTimedParser}.
 */
public interface PlayerTimedListener extends ParseTreeListener {
    /**
     * Enter a parse tree produced by {@link PlayerTimedParser#name}.
     *
     * @param ctx the parse tree
     */
    void enterName(PlayerTimedParser.NameContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerTimedParser#name}.
     *
     * @param ctx the parse tree
     */
    void exitName(PlayerTimedParser.NameContext ctx);

    /**
     * Enter a parse tree produced by {@link PlayerTimedParser#desc}.
     *
     * @param ctx the parse tree
     */
    void enterDesc(PlayerTimedParser.DescContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerTimedParser#desc}.
     *
     * @param ctx the parse tree
     */
    void exitDesc(PlayerTimedParser.DescContext ctx);

    /**
     * Enter a parse tree produced by {@link PlayerTimedParser#grade}.
     *
     * @param ctx the parse tree
     */
    void enterGrade(PlayerTimedParser.GradeContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerTimedParser#grade}.
     *
     * @param ctx the parse tree
     */
    void exitGrade(PlayerTimedParser.GradeContext ctx);

    /**
     * Enter a parse tree produced by {@link PlayerTimedParser#onEnd}.
     *
     * @param ctx the parse tree
     */
    void enterOnEnd(PlayerTimedParser.OnEndContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerTimedParser#onEnd}.
     *
     * @param ctx the parse tree
     */
    void exitOnEnd(PlayerTimedParser.OnEndContext ctx);

    /**
     * Enter a parse tree produced by {@link PlayerTimedParser#onIncrease}.
     *
     * @param ctx the parse tree
     */
    void enterOnIncrease(PlayerTimedParser.OnIncreaseContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerTimedParser#onIncrease}.
     *
     * @param ctx the parse tree
     */
    void exitOnIncrease(PlayerTimedParser.OnIncreaseContext ctx);

    /**
     * Enter a parse tree produced by {@link PlayerTimedParser#onDecrease}.
     *
     * @param ctx the parse tree
     */
    void enterOnDecrease(PlayerTimedParser.OnDecreaseContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerTimedParser#onDecrease}.
     *
     * @param ctx the parse tree
     */
    void exitOnDecrease(PlayerTimedParser.OnDecreaseContext ctx);

    /**
     * Enter a parse tree produced by {@link PlayerTimedParser#msgt}.
     *
     * @param ctx the parse tree
     */
    void enterMsgt(PlayerTimedParser.MsgtContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerTimedParser#msgt}.
     *
     * @param ctx the parse tree
     */
    void exitMsgt(PlayerTimedParser.MsgtContext ctx);

    /**
     * Enter a parse tree produced by {@link PlayerTimedParser#fail}.
     *
     * @param ctx the parse tree
     */
    void enterFail(PlayerTimedParser.FailContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerTimedParser#fail}.
     *
     * @param ctx the parse tree
     */
    void exitFail(PlayerTimedParser.FailContext ctx);

    /**
     * Enter a parse tree produced by {@link PlayerTimedParser#onBeginEffect}.
     *
     * @param ctx the parse tree
     */
    void enterOnBeginEffect(PlayerTimedParser.OnBeginEffectContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerTimedParser#onBeginEffect}.
     *
     * @param ctx the parse tree
     */
    void exitOnBeginEffect(PlayerTimedParser.OnBeginEffectContext ctx);

    /**
     * Enter a parse tree produced by {@link PlayerTimedParser#onEndEffect}.
     *
     * @param ctx the parse tree
     */
    void enterOnEndEffect(PlayerTimedParser.OnEndEffectContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerTimedParser#onEndEffect}.
     *
     * @param ctx the parse tree
     */
    void exitOnEndEffect(PlayerTimedParser.OnEndEffectContext ctx);

    /**
     * Enter a parse tree produced by {@link PlayerTimedParser#effectYX}.
     *
     * @param ctx the parse tree
     */
    void enterEffectYX(PlayerTimedParser.EffectYXContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerTimedParser#effectYX}.
     *
     * @param ctx the parse tree
     */
    void exitEffectYX(PlayerTimedParser.EffectYXContext ctx);

    /**
     * Enter a parse tree produced by {@link PlayerTimedParser#effectDice}.
     *
     * @param ctx the parse tree
     */
    void enterEffectDice(PlayerTimedParser.EffectDiceContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerTimedParser#effectDice}.
     *
     * @param ctx the parse tree
     */
    void exitEffectDice(PlayerTimedParser.EffectDiceContext ctx);

    /**
     * Enter a parse tree produced by {@link PlayerTimedParser#effectMsg}.
     *
     * @param ctx the parse tree
     */
    void enterEffectMsg(PlayerTimedParser.EffectMsgContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerTimedParser#effectMsg}.
     *
     * @param ctx the parse tree
     */
    void exitEffectMsg(PlayerTimedParser.EffectMsgContext ctx);

    /**
     * Enter a parse tree produced by {@link PlayerTimedParser#resist}.
     *
     * @param ctx the parse tree
     */
    void enterResist(PlayerTimedParser.ResistContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerTimedParser#resist}.
     *
     * @param ctx the parse tree
     */
    void exitResist(PlayerTimedParser.ResistContext ctx);

    /**
     * Enter a parse tree produced by {@link PlayerTimedParser#brand}.
     *
     * @param ctx the parse tree
     */
    void enterBrand(PlayerTimedParser.BrandContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerTimedParser#brand}.
     *
     * @param ctx the parse tree
     */
    void exitBrand(PlayerTimedParser.BrandContext ctx);

    /**
     * Enter a parse tree produced by {@link PlayerTimedParser#slay}.
     *
     * @param ctx the parse tree
     */
    void enterSlay(PlayerTimedParser.SlayContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerTimedParser#slay}.
     *
     * @param ctx the parse tree
     */
    void exitSlay(PlayerTimedParser.SlayContext ctx);

    /**
     * Enter a parse tree produced by {@link PlayerTimedParser#flagSynonym}.
     *
     * @param ctx the parse tree
     */
    void enterFlagSynonym(PlayerTimedParser.FlagSynonymContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerTimedParser#flagSynonym}.
     *
     * @param ctx the parse tree
     */
    void exitFlagSynonym(PlayerTimedParser.FlagSynonymContext ctx);

    /**
     * Enter a parse tree produced by {@link PlayerTimedParser#lowerBound}.
     *
     * @param ctx the parse tree
     */
    void enterLowerBound(PlayerTimedParser.LowerBoundContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerTimedParser#lowerBound}.
     *
     * @param ctx the parse tree
     */
    void exitLowerBound(PlayerTimedParser.LowerBoundContext ctx);

    /**
     * Enter a parse tree produced by {@link PlayerTimedParser#flags}.
     *
     * @param ctx the parse tree
     */
    void enterFlags(PlayerTimedParser.FlagsContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerTimedParser#flags}.
     *
     * @param ctx the parse tree
     */
    void exitFlags(PlayerTimedParser.FlagsContext ctx);

    /**
     * Enter a parse tree produced by {@link PlayerTimedParser#playerTimed}.
     *
     * @param ctx the parse tree
     */
    void enterPlayerTimed(PlayerTimedParser.PlayerTimedContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerTimedParser#playerTimed}.
     *
     * @param ctx the parse tree
     */
    void exitPlayerTimed(PlayerTimedParser.PlayerTimedContext ctx);

    /**
     * Enter a parse tree produced by {@link PlayerTimedParser#file}.
     *
     * @param ctx the parse tree
     */
    void enterFile(PlayerTimedParser.FileContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerTimedParser#file}.
     *
     * @param ctx the parse tree
     */
    void exitFile(PlayerTimedParser.FileContext ctx);
}