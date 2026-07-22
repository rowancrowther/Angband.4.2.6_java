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
// Generated from TrapGrammar.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.grammars.trap;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link TrapGrammar}.
 */
public interface TrapGrammarListener extends ParseTreeListener {
    /**
     * Enter a parse tree produced by {@link TrapGrammar#recordCount}.
     *
     * @param ctx the parse tree
     */
    void enterRecordCount(TrapGrammar.RecordCountContext ctx);

    /**
     * Exit a parse tree produced by {@link TrapGrammar#recordCount}.
     *
     * @param ctx the parse tree
     */
    void exitRecordCount(TrapGrammar.RecordCountContext ctx);

    /**
     * Enter a parse tree produced by {@link TrapGrammar#name}.
     *
     * @param ctx the parse tree
     */
    void enterName(TrapGrammar.NameContext ctx);

    /**
     * Exit a parse tree produced by {@link TrapGrammar#name}.
     *
     * @param ctx the parse tree
     */
    void exitName(TrapGrammar.NameContext ctx);

    /**
     * Enter a parse tree produced by {@link TrapGrammar#graphics}.
     *
     * @param ctx the parse tree
     */
    void enterGraphics(TrapGrammar.GraphicsContext ctx);

    /**
     * Exit a parse tree produced by {@link TrapGrammar#graphics}.
     *
     * @param ctx the parse tree
     */
    void exitGraphics(TrapGrammar.GraphicsContext ctx);

    /**
     * Enter a parse tree produced by {@link TrapGrammar#appear}.
     *
     * @param ctx the parse tree
     */
    void enterAppear(TrapGrammar.AppearContext ctx);

    /**
     * Exit a parse tree produced by {@link TrapGrammar#appear}.
     *
     * @param ctx the parse tree
     */
    void exitAppear(TrapGrammar.AppearContext ctx);

    /**
     * Enter a parse tree produced by {@link TrapGrammar#visibility}.
     *
     * @param ctx the parse tree
     */
    void enterVisibility(TrapGrammar.VisibilityContext ctx);

    /**
     * Exit a parse tree produced by {@link TrapGrammar#visibility}.
     *
     * @param ctx the parse tree
     */
    void exitVisibility(TrapGrammar.VisibilityContext ctx);

    /**
     * Enter a parse tree produced by {@link TrapGrammar#desc}.
     *
     * @param ctx the parse tree
     */
    void enterDesc(TrapGrammar.DescContext ctx);

    /**
     * Exit a parse tree produced by {@link TrapGrammar#desc}.
     *
     * @param ctx the parse tree
     */
    void exitDesc(TrapGrammar.DescContext ctx);

    /**
     * Enter a parse tree produced by {@link TrapGrammar#flags}.
     *
     * @param ctx the parse tree
     */
    void enterFlags(TrapGrammar.FlagsContext ctx);

    /**
     * Exit a parse tree produced by {@link TrapGrammar#flags}.
     *
     * @param ctx the parse tree
     */
    void exitFlags(TrapGrammar.FlagsContext ctx);

    /**
     * Enter a parse tree produced by {@link TrapGrammar#msg}.
     *
     * @param ctx the parse tree
     */
    void enterMsg(TrapGrammar.MsgContext ctx);

    /**
     * Exit a parse tree produced by {@link TrapGrammar#msg}.
     *
     * @param ctx the parse tree
     */
    void exitMsg(TrapGrammar.MsgContext ctx);

    /**
     * Enter a parse tree produced by {@link TrapGrammar#save}.
     *
     * @param ctx the parse tree
     */
    void enterSave(TrapGrammar.SaveContext ctx);

    /**
     * Exit a parse tree produced by {@link TrapGrammar#save}.
     *
     * @param ctx the parse tree
     */
    void exitSave(TrapGrammar.SaveContext ctx);

    /**
     * Enter a parse tree produced by {@link TrapGrammar#msgGood}.
     *
     * @param ctx the parse tree
     */
    void enterMsgGood(TrapGrammar.MsgGoodContext ctx);

    /**
     * Exit a parse tree produced by {@link TrapGrammar#msgGood}.
     *
     * @param ctx the parse tree
     */
    void exitMsgGood(TrapGrammar.MsgGoodContext ctx);

    /**
     * Enter a parse tree produced by {@link TrapGrammar#msgBad}.
     *
     * @param ctx the parse tree
     */
    void enterMsgBad(TrapGrammar.MsgBadContext ctx);

    /**
     * Exit a parse tree produced by {@link TrapGrammar#msgBad}.
     *
     * @param ctx the parse tree
     */
    void exitMsgBad(TrapGrammar.MsgBadContext ctx);

    /**
     * Enter a parse tree produced by {@link TrapGrammar#msgXtra}.
     *
     * @param ctx the parse tree
     */
    void enterMsgXtra(TrapGrammar.MsgXtraContext ctx);

    /**
     * Exit a parse tree produced by {@link TrapGrammar#msgXtra}.
     *
     * @param ctx the parse tree
     */
    void exitMsgXtra(TrapGrammar.MsgXtraContext ctx);

    /**
     * Enter a parse tree produced by {@link TrapGrammar#effectXtra}.
     *
     * @param ctx the parse tree
     */
    void enterEffectXtra(TrapGrammar.EffectXtraContext ctx);

    /**
     * Exit a parse tree produced by {@link TrapGrammar#effectXtra}.
     *
     * @param ctx the parse tree
     */
    void exitEffectXtra(TrapGrammar.EffectXtraContext ctx);

    /**
     * Enter a parse tree produced by {@link TrapGrammar#diceXtra}.
     *
     * @param ctx the parse tree
     */
    void enterDiceXtra(TrapGrammar.DiceXtraContext ctx);

    /**
     * Exit a parse tree produced by {@link TrapGrammar#diceXtra}.
     *
     * @param ctx the parse tree
     */
    void exitDiceXtra(TrapGrammar.DiceXtraContext ctx);

    /**
     * Enter a parse tree produced by {@link TrapGrammar#exprXtra}.
     *
     * @param ctx the parse tree
     */
    void enterExprXtra(TrapGrammar.ExprXtraContext ctx);

    /**
     * Exit a parse tree produced by {@link TrapGrammar#exprXtra}.
     *
     * @param ctx the parse tree
     */
    void exitExprXtra(TrapGrammar.ExprXtraContext ctx);

    /**
     * Enter a parse tree produced by {@link TrapGrammar#effectXtraBlock}.
     *
     * @param ctx the parse tree
     */
    void enterEffectXtraBlock(TrapGrammar.EffectXtraBlockContext ctx);

    /**
     * Exit a parse tree produced by {@link TrapGrammar#effectXtraBlock}.
     *
     * @param ctx the parse tree
     */
    void exitEffectXtraBlock(TrapGrammar.EffectXtraBlockContext ctx);

    /**
     * Enter a parse tree produced by {@link TrapGrammar#trapRecord}.
     *
     * @param ctx the parse tree
     */
    void enterTrapRecord(TrapGrammar.TrapRecordContext ctx);

    /**
     * Exit a parse tree produced by {@link TrapGrammar#trapRecord}.
     *
     * @param ctx the parse tree
     */
    void exitTrapRecord(TrapGrammar.TrapRecordContext ctx);

    /**
     * Enter a parse tree produced by {@link TrapGrammar#file}.
     *
     * @param ctx the parse tree
     */
    void enterFile(TrapGrammar.FileContext ctx);

    /**
     * Exit a parse tree produced by {@link TrapGrammar#file}.
     *
     * @param ctx the parse tree
     */
    void exitFile(TrapGrammar.FileContext ctx);

    /**
     * Enter a parse tree produced by {@link TrapGrammar#effect}.
     *
     * @param ctx the parse tree
     */
    void enterEffect(TrapGrammar.EffectContext ctx);

    /**
     * Exit a parse tree produced by {@link TrapGrammar#effect}.
     *
     * @param ctx the parse tree
     */
    void exitEffect(TrapGrammar.EffectContext ctx);

    /**
     * Enter a parse tree produced by {@link TrapGrammar#time}.
     *
     * @param ctx the parse tree
     */
    void enterTime(TrapGrammar.TimeContext ctx);

    /**
     * Exit a parse tree produced by {@link TrapGrammar#time}.
     *
     * @param ctx the parse tree
     */
    void exitTime(TrapGrammar.TimeContext ctx);

    /**
     * Enter a parse tree produced by {@link TrapGrammar#effectYX}.
     *
     * @param ctx the parse tree
     */
    void enterEffectYX(TrapGrammar.EffectYXContext ctx);

    /**
     * Exit a parse tree produced by {@link TrapGrammar#effectYX}.
     *
     * @param ctx the parse tree
     */
    void exitEffectYX(TrapGrammar.EffectYXContext ctx);

    /**
     * Enter a parse tree produced by {@link TrapGrammar#dice}.
     *
     * @param ctx the parse tree
     */
    void enterDice(TrapGrammar.DiceContext ctx);

    /**
     * Exit a parse tree produced by {@link TrapGrammar#dice}.
     *
     * @param ctx the parse tree
     */
    void exitDice(TrapGrammar.DiceContext ctx);

    /**
     * Enter a parse tree produced by {@link TrapGrammar#expr}.
     *
     * @param ctx the parse tree
     */
    void enterExpr(TrapGrammar.ExprContext ctx);

    /**
     * Exit a parse tree produced by {@link TrapGrammar#expr}.
     *
     * @param ctx the parse tree
     */
    void exitExpr(TrapGrammar.ExprContext ctx);

    /**
     * Enter a parse tree produced by {@link TrapGrammar#effectMsg}.
     *
     * @param ctx the parse tree
     */
    void enterEffectMsg(TrapGrammar.EffectMsgContext ctx);

    /**
     * Exit a parse tree produced by {@link TrapGrammar#effectMsg}.
     *
     * @param ctx the parse tree
     */
    void exitEffectMsg(TrapGrammar.EffectMsgContext ctx);

    /**
     * Enter a parse tree produced by {@link TrapGrammar#effectBlock}.
     *
     * @param ctx the parse tree
     */
    void enterEffectBlock(TrapGrammar.EffectBlockContext ctx);

    /**
     * Exit a parse tree produced by {@link TrapGrammar#effectBlock}.
     *
     * @param ctx the parse tree
     */
    void exitEffectBlock(TrapGrammar.EffectBlockContext ctx);
}