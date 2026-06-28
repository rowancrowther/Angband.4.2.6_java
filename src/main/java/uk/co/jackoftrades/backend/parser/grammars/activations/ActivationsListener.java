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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/parser/grammars/Activations.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.grammars.activations;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link Activations}.
 */
public interface ActivationsListener extends ParseTreeListener {
    /**
     * Enter a parse tree produced by {@link Activations#recordCount}.
     *
     * @param ctx the parse tree
     */
    void enterRecordCount(Activations.RecordCountContext ctx);

    /**
     * Exit a parse tree produced by {@link Activations#recordCount}.
     *
     * @param ctx the parse tree
     */
    void exitRecordCount(Activations.RecordCountContext ctx);

    /**
     * Enter a parse tree produced by {@link Activations#name}.
     *
     * @param ctx the parse tree
     */
    void enterName(Activations.NameContext ctx);

    /**
     * Exit a parse tree produced by {@link Activations#name}.
     *
     * @param ctx the parse tree
     */
    void exitName(Activations.NameContext ctx);

    /**
     * Enter a parse tree produced by {@link Activations#aim}.
     *
     * @param ctx the parse tree
     */
    void enterAim(Activations.AimContext ctx);

    /**
     * Exit a parse tree produced by {@link Activations#aim}.
     *
     * @param ctx the parse tree
     */
    void exitAim(Activations.AimContext ctx);

    /**
     * Enter a parse tree produced by {@link Activations#level}.
     *
     * @param ctx the parse tree
     */
    void enterLevel(Activations.LevelContext ctx);

    /**
     * Exit a parse tree produced by {@link Activations#level}.
     *
     * @param ctx the parse tree
     */
    void exitLevel(Activations.LevelContext ctx);

    /**
     * Enter a parse tree produced by {@link Activations#power}.
     *
     * @param ctx the parse tree
     */
    void enterPower(Activations.PowerContext ctx);

    /**
     * Exit a parse tree produced by {@link Activations#power}.
     *
     * @param ctx the parse tree
     */
    void exitPower(Activations.PowerContext ctx);

    /**
     * Enter a parse tree produced by {@link Activations#desc}.
     *
     * @param ctx the parse tree
     */
    void enterDesc(Activations.DescContext ctx);

    /**
     * Exit a parse tree produced by {@link Activations#desc}.
     *
     * @param ctx the parse tree
     */
    void exitDesc(Activations.DescContext ctx);

    /**
     * Enter a parse tree produced by {@link Activations#msg}.
     *
     * @param ctx the parse tree
     */
    void enterMsg(Activations.MsgContext ctx);

    /**
     * Exit a parse tree produced by {@link Activations#msg}.
     *
     * @param ctx the parse tree
     */
    void exitMsg(Activations.MsgContext ctx);

    /**
     * Enter a parse tree produced by {@link Activations#activation}.
     *
     * @param ctx the parse tree
     */
    void enterActivation(Activations.ActivationContext ctx);

    /**
     * Exit a parse tree produced by {@link Activations#activation}.
     *
     * @param ctx the parse tree
     */
    void exitActivation(Activations.ActivationContext ctx);

    /**
     * Enter a parse tree produced by {@link Activations#file}.
     *
     * @param ctx the parse tree
     */
    void enterFile(Activations.FileContext ctx);

    /**
     * Exit a parse tree produced by {@link Activations#file}.
     *
     * @param ctx the parse tree
     */
    void exitFile(Activations.FileContext ctx);

    /**
     * Enter a parse tree produced by {@link Activations#effect}.
     *
     * @param ctx the parse tree
     */
    void enterEffect(Activations.EffectContext ctx);

    /**
     * Exit a parse tree produced by {@link Activations#effect}.
     *
     * @param ctx the parse tree
     */
    void exitEffect(Activations.EffectContext ctx);

    /**
     * Enter a parse tree produced by {@link Activations#time}.
     *
     * @param ctx the parse tree
     */
    void enterTime(Activations.TimeContext ctx);

    /**
     * Exit a parse tree produced by {@link Activations#time}.
     *
     * @param ctx the parse tree
     */
    void exitTime(Activations.TimeContext ctx);

    /**
     * Enter a parse tree produced by {@link Activations#effectYX}.
     *
     * @param ctx the parse tree
     */
    void enterEffectYX(Activations.EffectYXContext ctx);

    /**
     * Exit a parse tree produced by {@link Activations#effectYX}.
     *
     * @param ctx the parse tree
     */
    void exitEffectYX(Activations.EffectYXContext ctx);

    /**
     * Enter a parse tree produced by {@link Activations#dice}.
     *
     * @param ctx the parse tree
     */
    void enterDice(Activations.DiceContext ctx);

    /**
     * Exit a parse tree produced by {@link Activations#dice}.
     *
     * @param ctx the parse tree
     */
    void exitDice(Activations.DiceContext ctx);

    /**
     * Enter a parse tree produced by {@link Activations#expr}.
     *
     * @param ctx the parse tree
     */
    void enterExpr(Activations.ExprContext ctx);

    /**
     * Exit a parse tree produced by {@link Activations#expr}.
     *
     * @param ctx the parse tree
     */
    void exitExpr(Activations.ExprContext ctx);

    /**
     * Enter a parse tree produced by {@link Activations#effectMsg}.
     *
     * @param ctx the parse tree
     */
    void enterEffectMsg(Activations.EffectMsgContext ctx);

    /**
     * Exit a parse tree produced by {@link Activations#effectMsg}.
     *
     * @param ctx the parse tree
     */
    void exitEffectMsg(Activations.EffectMsgContext ctx);

    /**
     * Enter a parse tree produced by {@link Activations#effectBlock}.
     *
     * @param ctx the parse tree
     */
    void enterEffectBlock(Activations.EffectBlockContext ctx);

    /**
     * Exit a parse tree produced by {@link Activations#effectBlock}.
     *
     * @param ctx the parse tree
     */
    void exitEffectBlock(Activations.EffectBlockContext ctx);
}