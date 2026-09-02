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
// Generated from ChestTrapGrammar.g4 by ANTLR 4.13.2
package uk.co.jackoftradesltd.backend.parser.grammars.chesttrap;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link ChestTrapGrammar}.
 */
public interface ChestTrapGrammarListener extends ParseTreeListener {
    /**
     * Enter a parse tree produced by {@link ChestTrapGrammar#recordCount}.
     *
     * @param ctx the parse tree
     */
    void enterRecordCount(ChestTrapGrammar.RecordCountContext ctx);

    /**
     * Exit a parse tree produced by {@link ChestTrapGrammar#recordCount}.
     *
     * @param ctx the parse tree
     */
    void exitRecordCount(ChestTrapGrammar.RecordCountContext ctx);

    /**
     * Enter a parse tree produced by {@link ChestTrapGrammar#name}.
     *
     * @param ctx the parse tree
     */
    void enterName(ChestTrapGrammar.NameContext ctx);

    /**
     * Exit a parse tree produced by {@link ChestTrapGrammar#name}.
     *
     * @param ctx the parse tree
     */
    void exitName(ChestTrapGrammar.NameContext ctx);

    /**
     * Enter a parse tree produced by {@link ChestTrapGrammar#code}.
     *
     * @param ctx the parse tree
     */
    void enterCode(ChestTrapGrammar.CodeContext ctx);

    /**
     * Exit a parse tree produced by {@link ChestTrapGrammar#code}.
     *
     * @param ctx the parse tree
     */
    void exitCode(ChestTrapGrammar.CodeContext ctx);

    /**
     * Enter a parse tree produced by {@link ChestTrapGrammar#level}.
     *
     * @param ctx the parse tree
     */
    void enterLevel(ChestTrapGrammar.LevelContext ctx);

    /**
     * Exit a parse tree produced by {@link ChestTrapGrammar#level}.
     *
     * @param ctx the parse tree
     */
    void exitLevel(ChestTrapGrammar.LevelContext ctx);

    /**
     * Enter a parse tree produced by {@link ChestTrapGrammar#destroy}.
     *
     * @param ctx the parse tree
     */
    void enterDestroy(ChestTrapGrammar.DestroyContext ctx);

    /**
     * Exit a parse tree produced by {@link ChestTrapGrammar#destroy}.
     *
     * @param ctx the parse tree
     */
    void exitDestroy(ChestTrapGrammar.DestroyContext ctx);

    /**
     * Enter a parse tree produced by {@link ChestTrapGrammar#magic}.
     *
     * @param ctx the parse tree
     */
    void enterMagic(ChestTrapGrammar.MagicContext ctx);

    /**
     * Exit a parse tree produced by {@link ChestTrapGrammar#magic}.
     *
     * @param ctx the parse tree
     */
    void exitMagic(ChestTrapGrammar.MagicContext ctx);

    /**
     * Enter a parse tree produced by {@link ChestTrapGrammar#msg}.
     *
     * @param ctx the parse tree
     */
    void enterMsg(ChestTrapGrammar.MsgContext ctx);

    /**
     * Exit a parse tree produced by {@link ChestTrapGrammar#msg}.
     *
     * @param ctx the parse tree
     */
    void exitMsg(ChestTrapGrammar.MsgContext ctx);

    /**
     * Enter a parse tree produced by {@link ChestTrapGrammar#msgDeath}.
     *
     * @param ctx the parse tree
     */
    void enterMsgDeath(ChestTrapGrammar.MsgDeathContext ctx);

    /**
     * Exit a parse tree produced by {@link ChestTrapGrammar#msgDeath}.
     *
     * @param ctx the parse tree
     */
    void exitMsgDeath(ChestTrapGrammar.MsgDeathContext ctx);

    /**
     * Enter a parse tree produced by {@link ChestTrapGrammar#chestTrap}.
     *
     * @param ctx the parse tree
     */
    void enterChestTrap(ChestTrapGrammar.ChestTrapContext ctx);

    /**
     * Exit a parse tree produced by {@link ChestTrapGrammar#chestTrap}.
     *
     * @param ctx the parse tree
     */
    void exitChestTrap(ChestTrapGrammar.ChestTrapContext ctx);

    /**
     * Enter a parse tree produced by {@link ChestTrapGrammar#file}.
     *
     * @param ctx the parse tree
     */
    void enterFile(ChestTrapGrammar.FileContext ctx);

    /**
     * Exit a parse tree produced by {@link ChestTrapGrammar#file}.
     *
     * @param ctx the parse tree
     */
    void exitFile(ChestTrapGrammar.FileContext ctx);

    /**
     * Enter a parse tree produced by {@link ChestTrapGrammar#effect}.
     *
     * @param ctx the parse tree
     */
    void enterEffect(ChestTrapGrammar.EffectContext ctx);

    /**
     * Exit a parse tree produced by {@link ChestTrapGrammar#effect}.
     *
     * @param ctx the parse tree
     */
    void exitEffect(ChestTrapGrammar.EffectContext ctx);

    /**
     * Enter a parse tree produced by {@link ChestTrapGrammar#time}.
     *
     * @param ctx the parse tree
     */
    void enterTime(ChestTrapGrammar.TimeContext ctx);

    /**
     * Exit a parse tree produced by {@link ChestTrapGrammar#time}.
     *
     * @param ctx the parse tree
     */
    void exitTime(ChestTrapGrammar.TimeContext ctx);

    /**
     * Enter a parse tree produced by {@link ChestTrapGrammar#effectYX}.
     *
     * @param ctx the parse tree
     */
    void enterEffectYX(ChestTrapGrammar.EffectYXContext ctx);

    /**
     * Exit a parse tree produced by {@link ChestTrapGrammar#effectYX}.
     *
     * @param ctx the parse tree
     */
    void exitEffectYX(ChestTrapGrammar.EffectYXContext ctx);

    /**
     * Enter a parse tree produced by {@link ChestTrapGrammar#dice}.
     *
     * @param ctx the parse tree
     */
    void enterDice(ChestTrapGrammar.DiceContext ctx);

    /**
     * Exit a parse tree produced by {@link ChestTrapGrammar#dice}.
     *
     * @param ctx the parse tree
     */
    void exitDice(ChestTrapGrammar.DiceContext ctx);

    /**
     * Enter a parse tree produced by {@link ChestTrapGrammar#expr}.
     *
     * @param ctx the parse tree
     */
    void enterExpr(ChestTrapGrammar.ExprContext ctx);

    /**
     * Exit a parse tree produced by {@link ChestTrapGrammar#expr}.
     *
     * @param ctx the parse tree
     */
    void exitExpr(ChestTrapGrammar.ExprContext ctx);

    /**
     * Enter a parse tree produced by {@link ChestTrapGrammar#effectMsg}.
     *
     * @param ctx the parse tree
     */
    void enterEffectMsg(ChestTrapGrammar.EffectMsgContext ctx);

    /**
     * Exit a parse tree produced by {@link ChestTrapGrammar#effectMsg}.
     *
     * @param ctx the parse tree
     */
    void exitEffectMsg(ChestTrapGrammar.EffectMsgContext ctx);

    /**
     * Enter a parse tree produced by {@link ChestTrapGrammar#effectBlock}.
     *
     * @param ctx the parse tree
     */
    void enterEffectBlock(ChestTrapGrammar.EffectBlockContext ctx);

    /**
     * Exit a parse tree produced by {@link ChestTrapGrammar#effectBlock}.
     *
     * @param ctx the parse tree
     */
    void exitEffectBlock(ChestTrapGrammar.EffectBlockContext ctx);
}