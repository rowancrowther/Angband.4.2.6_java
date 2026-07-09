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

// Generated from CurseGrammar.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.grammars.curse;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link CurseGrammar}.
 */
public interface CurseGrammarListener extends ParseTreeListener {
    /**
     * Enter a parse tree produced by {@link CurseGrammar#recordCount}.
     *
     * @param ctx the parse tree
     */
    void enterRecordCount(CurseGrammar.RecordCountContext ctx);

    /**
     * Exit a parse tree produced by {@link CurseGrammar#recordCount}.
     *
     * @param ctx the parse tree
     */
    void exitRecordCount(CurseGrammar.RecordCountContext ctx);

    /**
     * Enter a parse tree produced by {@link CurseGrammar#name}.
     *
     * @param ctx the parse tree
     */
    void enterName(CurseGrammar.NameContext ctx);

    /**
     * Exit a parse tree produced by {@link CurseGrammar#name}.
     *
     * @param ctx the parse tree
     */
    void exitName(CurseGrammar.NameContext ctx);

    /**
     * Enter a parse tree produced by {@link CurseGrammar#curseType}.
     *
     * @param ctx the parse tree
     */
    void enterCurseType(CurseGrammar.CurseTypeContext ctx);

    /**
     * Exit a parse tree produced by {@link CurseGrammar#curseType}.
     *
     * @param ctx the parse tree
     */
    void exitCurseType(CurseGrammar.CurseTypeContext ctx);

    /**
     * Enter a parse tree produced by {@link CurseGrammar#weight}.
     *
     * @param ctx the parse tree
     */
    void enterWeight(CurseGrammar.WeightContext ctx);

    /**
     * Exit a parse tree produced by {@link CurseGrammar#weight}.
     *
     * @param ctx the parse tree
     */
    void exitWeight(CurseGrammar.WeightContext ctx);

    /**
     * Enter a parse tree produced by {@link CurseGrammar#combat}.
     *
     * @param ctx the parse tree
     */
    void enterCombat(CurseGrammar.CombatContext ctx);

    /**
     * Exit a parse tree produced by {@link CurseGrammar#combat}.
     *
     * @param ctx the parse tree
     */
    void exitCombat(CurseGrammar.CombatContext ctx);

    /**
     * Enter a parse tree produced by {@link CurseGrammar#flags}.
     *
     * @param ctx the parse tree
     */
    void enterFlags(CurseGrammar.FlagsContext ctx);

    /**
     * Exit a parse tree produced by {@link CurseGrammar#flags}.
     *
     * @param ctx the parse tree
     */
    void exitFlags(CurseGrammar.FlagsContext ctx);

    /**
     * Enter a parse tree produced by {@link CurseGrammar#values}.
     *
     * @param ctx the parse tree
     */
    void enterValues(CurseGrammar.ValuesContext ctx);

    /**
     * Exit a parse tree produced by {@link CurseGrammar#values}.
     *
     * @param ctx the parse tree
     */
    void exitValues(CurseGrammar.ValuesContext ctx);

    /**
     * Enter a parse tree produced by {@link CurseGrammar#msg}.
     *
     * @param ctx the parse tree
     */
    void enterMsg(CurseGrammar.MsgContext ctx);

    /**
     * Exit a parse tree produced by {@link CurseGrammar#msg}.
     *
     * @param ctx the parse tree
     */
    void exitMsg(CurseGrammar.MsgContext ctx);

    /**
     * Enter a parse tree produced by {@link CurseGrammar#desc}.
     *
     * @param ctx the parse tree
     */
    void enterDesc(CurseGrammar.DescContext ctx);

    /**
     * Exit a parse tree produced by {@link CurseGrammar#desc}.
     *
     * @param ctx the parse tree
     */
    void exitDesc(CurseGrammar.DescContext ctx);

    /**
     * Enter a parse tree produced by {@link CurseGrammar#conflict}.
     *
     * @param ctx the parse tree
     */
    void enterConflict(CurseGrammar.ConflictContext ctx);

    /**
     * Exit a parse tree produced by {@link CurseGrammar#conflict}.
     *
     * @param ctx the parse tree
     */
    void exitConflict(CurseGrammar.ConflictContext ctx);

    /**
     * Enter a parse tree produced by {@link CurseGrammar#conflictFlags}.
     *
     * @param ctx the parse tree
     */
    void enterConflictFlags(CurseGrammar.ConflictFlagsContext ctx);

    /**
     * Exit a parse tree produced by {@link CurseGrammar#conflictFlags}.
     *
     * @param ctx the parse tree
     */
    void exitConflictFlags(CurseGrammar.ConflictFlagsContext ctx);

    /**
     * Enter a parse tree produced by {@link CurseGrammar#curseRecord}.
     *
     * @param ctx the parse tree
     */
    void enterCurseRecord(CurseGrammar.CurseRecordContext ctx);

    /**
     * Exit a parse tree produced by {@link CurseGrammar#curseRecord}.
     *
     * @param ctx the parse tree
     */
    void exitCurseRecord(CurseGrammar.CurseRecordContext ctx);

    /**
     * Enter a parse tree produced by {@link CurseGrammar#file}.
     *
     * @param ctx the parse tree
     */
    void enterFile(CurseGrammar.FileContext ctx);

    /**
     * Exit a parse tree produced by {@link CurseGrammar#file}.
     *
     * @param ctx the parse tree
     */
    void exitFile(CurseGrammar.FileContext ctx);

    /**
     * Enter a parse tree produced by {@link CurseGrammar#effect}.
     *
     * @param ctx the parse tree
     */
    void enterEffect(CurseGrammar.EffectContext ctx);

    /**
     * Exit a parse tree produced by {@link CurseGrammar#effect}.
     *
     * @param ctx the parse tree
     */
    void exitEffect(CurseGrammar.EffectContext ctx);

    /**
     * Enter a parse tree produced by {@link CurseGrammar#time}.
     *
     * @param ctx the parse tree
     */
    void enterTime(CurseGrammar.TimeContext ctx);

    /**
     * Exit a parse tree produced by {@link CurseGrammar#time}.
     *
     * @param ctx the parse tree
     */
    void exitTime(CurseGrammar.TimeContext ctx);

    /**
     * Enter a parse tree produced by {@link CurseGrammar#effectYX}.
     *
     * @param ctx the parse tree
     */
    void enterEffectYX(CurseGrammar.EffectYXContext ctx);

    /**
     * Exit a parse tree produced by {@link CurseGrammar#effectYX}.
     *
     * @param ctx the parse tree
     */
    void exitEffectYX(CurseGrammar.EffectYXContext ctx);

    /**
     * Enter a parse tree produced by {@link CurseGrammar#dice}.
     *
     * @param ctx the parse tree
     */
    void enterDice(CurseGrammar.DiceContext ctx);

    /**
     * Exit a parse tree produced by {@link CurseGrammar#dice}.
     *
     * @param ctx the parse tree
     */
    void exitDice(CurseGrammar.DiceContext ctx);

    /**
     * Enter a parse tree produced by {@link CurseGrammar#expr}.
     *
     * @param ctx the parse tree
     */
    void enterExpr(CurseGrammar.ExprContext ctx);

    /**
     * Exit a parse tree produced by {@link CurseGrammar#expr}.
     *
     * @param ctx the parse tree
     */
    void exitExpr(CurseGrammar.ExprContext ctx);

    /**
     * Enter a parse tree produced by {@link CurseGrammar#effectMsg}.
     *
     * @param ctx the parse tree
     */
    void enterEffectMsg(CurseGrammar.EffectMsgContext ctx);

    /**
     * Exit a parse tree produced by {@link CurseGrammar#effectMsg}.
     *
     * @param ctx the parse tree
     */
    void exitEffectMsg(CurseGrammar.EffectMsgContext ctx);

    /**
     * Enter a parse tree produced by {@link CurseGrammar#effectBlock}.
     *
     * @param ctx the parse tree
     */
    void enterEffectBlock(CurseGrammar.EffectBlockContext ctx);

    /**
     * Exit a parse tree produced by {@link CurseGrammar#effectBlock}.
     *
     * @param ctx the parse tree
     */
    void exitEffectBlock(CurseGrammar.EffectBlockContext ctx);
}