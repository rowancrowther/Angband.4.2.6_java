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

// Generated from src/main/java/uk/co/jackoftrades/backend/parser/grammars/ActivationsGrammar.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.grammars.activations;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link ActivationsGrammar}.
 */
public interface ActivationsGrammarListener extends ParseTreeListener {
    /**
     * Enter a parse tree produced by {@link ActivationsGrammar#recordCount}.
     *
     * @param ctx the parse tree
     */
    void enterRecordCount(ActivationsGrammar.RecordCountContext ctx);

    /**
     * Exit a parse tree produced by {@link ActivationsGrammar#recordCount}.
     *
     * @param ctx the parse tree
     */
    void exitRecordCount(ActivationsGrammar.RecordCountContext ctx);

    /**
     * Enter a parse tree produced by {@link ActivationsGrammar#name}.
     *
     * @param ctx the parse tree
     */
    void enterName(ActivationsGrammar.NameContext ctx);

    /**
     * Exit a parse tree produced by {@link ActivationsGrammar#name}.
     *
     * @param ctx the parse tree
     */
    void exitName(ActivationsGrammar.NameContext ctx);

    /**
     * Enter a parse tree produced by {@link ActivationsGrammar#aim}.
     *
     * @param ctx the parse tree
     */
    void enterAim(ActivationsGrammar.AimContext ctx);

    /**
     * Exit a parse tree produced by {@link ActivationsGrammar#aim}.
     *
     * @param ctx the parse tree
     */
    void exitAim(ActivationsGrammar.AimContext ctx);

    /**
     * Enter a parse tree produced by {@link ActivationsGrammar#level}.
     *
     * @param ctx the parse tree
     */
    void enterLevel(ActivationsGrammar.LevelContext ctx);

    /**
     * Exit a parse tree produced by {@link ActivationsGrammar#level}.
     *
     * @param ctx the parse tree
     */
    void exitLevel(ActivationsGrammar.LevelContext ctx);

    /**
     * Enter a parse tree produced by {@link ActivationsGrammar#power}.
     *
     * @param ctx the parse tree
     */
    void enterPower(ActivationsGrammar.PowerContext ctx);

    /**
     * Exit a parse tree produced by {@link ActivationsGrammar#power}.
     *
     * @param ctx the parse tree
     */
    void exitPower(ActivationsGrammar.PowerContext ctx);

    /**
     * Enter a parse tree produced by {@link ActivationsGrammar#desc}.
     *
     * @param ctx the parse tree
     */
    void enterDesc(ActivationsGrammar.DescContext ctx);

    /**
     * Exit a parse tree produced by {@link ActivationsGrammar#desc}.
     *
     * @param ctx the parse tree
     */
    void exitDesc(ActivationsGrammar.DescContext ctx);

    /**
     * Enter a parse tree produced by {@link ActivationsGrammar#msg}.
     *
     * @param ctx the parse tree
     */
    void enterMsg(ActivationsGrammar.MsgContext ctx);

    /**
     * Exit a parse tree produced by {@link ActivationsGrammar#msg}.
     *
     * @param ctx the parse tree
     */
    void exitMsg(ActivationsGrammar.MsgContext ctx);

    /**
     * Enter a parse tree produced by {@link ActivationsGrammar#activation}.
     *
     * @param ctx the parse tree
     */
    void enterActivation(ActivationsGrammar.ActivationContext ctx);

    /**
     * Exit a parse tree produced by {@link ActivationsGrammar#activation}.
     *
     * @param ctx the parse tree
     */
    void exitActivation(ActivationsGrammar.ActivationContext ctx);

    /**
     * Enter a parse tree produced by {@link ActivationsGrammar#file}.
     *
     * @param ctx the parse tree
     */
    void enterFile(ActivationsGrammar.FileContext ctx);

    /**
     * Exit a parse tree produced by {@link ActivationsGrammar#file}.
     *
     * @param ctx the parse tree
     */
    void exitFile(ActivationsGrammar.FileContext ctx);

    /**
     * Enter a parse tree produced by {@link ActivationsGrammar#effect}.
     *
     * @param ctx the parse tree
     */
    void enterEffect(ActivationsGrammar.EffectContext ctx);

    /**
     * Exit a parse tree produced by {@link ActivationsGrammar#effect}.
     *
     * @param ctx the parse tree
     */
    void exitEffect(ActivationsGrammar.EffectContext ctx);

    /**
     * Enter a parse tree produced by {@link ActivationsGrammar#time}.
     *
     * @param ctx the parse tree
     */
    void enterTime(ActivationsGrammar.TimeContext ctx);

    /**
     * Exit a parse tree produced by {@link ActivationsGrammar#time}.
     *
     * @param ctx the parse tree
     */
    void exitTime(ActivationsGrammar.TimeContext ctx);

    /**
     * Enter a parse tree produced by {@link ActivationsGrammar#effectYX}.
     *
     * @param ctx the parse tree
     */
    void enterEffectYX(ActivationsGrammar.EffectYXContext ctx);

    /**
     * Exit a parse tree produced by {@link ActivationsGrammar#effectYX}.
     *
     * @param ctx the parse tree
     */
    void exitEffectYX(ActivationsGrammar.EffectYXContext ctx);

    /**
     * Enter a parse tree produced by {@link ActivationsGrammar#dice}.
     *
     * @param ctx the parse tree
     */
    void enterDice(ActivationsGrammar.DiceContext ctx);

    /**
     * Exit a parse tree produced by {@link ActivationsGrammar#dice}.
     *
     * @param ctx the parse tree
     */
    void exitDice(ActivationsGrammar.DiceContext ctx);

    /**
     * Enter a parse tree produced by {@link ActivationsGrammar#expr}.
     *
     * @param ctx the parse tree
     */
    void enterExpr(ActivationsGrammar.ExprContext ctx);

    /**
     * Exit a parse tree produced by {@link ActivationsGrammar#expr}.
     *
     * @param ctx the parse tree
     */
    void exitExpr(ActivationsGrammar.ExprContext ctx);

    /**
     * Enter a parse tree produced by {@link ActivationsGrammar#effectMsg}.
     *
     * @param ctx the parse tree
     */
    void enterEffectMsg(ActivationsGrammar.EffectMsgContext ctx);

    /**
     * Exit a parse tree produced by {@link ActivationsGrammar#effectMsg}.
     *
     * @param ctx the parse tree
     */
    void exitEffectMsg(ActivationsGrammar.EffectMsgContext ctx);

    /**
     * Enter a parse tree produced by {@link ActivationsGrammar#effectBlock}.
     *
     * @param ctx the parse tree
     */
    void enterEffectBlock(ActivationsGrammar.EffectBlockContext ctx);

    /**
     * Exit a parse tree produced by {@link ActivationsGrammar#effectBlock}.
     *
     * @param ctx the parse tree
     */
    void exitEffectBlock(ActivationsGrammar.EffectBlockContext ctx);
}