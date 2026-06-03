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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/parser/grammars/Activations.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.activation;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link ActivationsParser}.
 */
public interface ActivationsListener extends ParseTreeListener {
    /**
     * Enter a parse tree produced by {@link ActivationsParser#name}.
     *
     * @param ctx the parse tree
     */
    void enterName(ActivationsParser.NameContext ctx);

    /**
     * Exit a parse tree produced by {@link ActivationsParser#name}.
     *
     * @param ctx the parse tree
     */
    void exitName(ActivationsParser.NameContext ctx);

    /**
     * Enter a parse tree produced by {@link ActivationsParser#aim}.
     *
     * @param ctx the parse tree
     */
    void enterAim(ActivationsParser.AimContext ctx);

    /**
     * Exit a parse tree produced by {@link ActivationsParser#aim}.
     *
     * @param ctx the parse tree
     */
    void exitAim(ActivationsParser.AimContext ctx);

    /**
     * Enter a parse tree produced by {@link ActivationsParser#level}.
     *
     * @param ctx the parse tree
     */
    void enterLevel(ActivationsParser.LevelContext ctx);

    /**
     * Exit a parse tree produced by {@link ActivationsParser#level}.
     *
     * @param ctx the parse tree
     */
    void exitLevel(ActivationsParser.LevelContext ctx);

    /**
     * Enter a parse tree produced by {@link ActivationsParser#power}.
     *
     * @param ctx the parse tree
     */
    void enterPower(ActivationsParser.PowerContext ctx);

    /**
     * Exit a parse tree produced by {@link ActivationsParser#power}.
     *
     * @param ctx the parse tree
     */
    void exitPower(ActivationsParser.PowerContext ctx);

    /**
     * Enter a parse tree produced by {@link ActivationsParser#effect}.
     *
     * @param ctx the parse tree
     */
    void enterEffect(ActivationsParser.EffectContext ctx);

    /**
     * Exit a parse tree produced by {@link ActivationsParser#effect}.
     *
     * @param ctx the parse tree
     */
    void exitEffect(ActivationsParser.EffectContext ctx);

    /**
     * Enter a parse tree produced by {@link ActivationsParser#effect_yx}.
     *
     * @param ctx the parse tree
     */
    void enterEffect_yx(ActivationsParser.Effect_yxContext ctx);

    /**
     * Exit a parse tree produced by {@link ActivationsParser#effect_yx}.
     *
     * @param ctx the parse tree
     */
    void exitEffect_yx(ActivationsParser.Effect_yxContext ctx);

    /**
     * Enter a parse tree produced by {@link ActivationsParser#dice}.
     *
     * @param ctx the parse tree
     */
    void enterDice(ActivationsParser.DiceContext ctx);

    /**
     * Exit a parse tree produced by {@link ActivationsParser#dice}.
     *
     * @param ctx the parse tree
     */
    void exitDice(ActivationsParser.DiceContext ctx);

    /**
     * Enter a parse tree produced by {@link ActivationsParser#expr}.
     *
     * @param ctx the parse tree
     */
    void enterExpr(ActivationsParser.ExprContext ctx);

    /**
     * Exit a parse tree produced by {@link ActivationsParser#expr}.
     *
     * @param ctx the parse tree
     */
    void exitExpr(ActivationsParser.ExprContext ctx);

    /**
     * Enter a parse tree produced by {@link ActivationsParser#msg}.
     *
     * @param ctx the parse tree
     */
    void enterMsg(ActivationsParser.MsgContext ctx);

    /**
     * Exit a parse tree produced by {@link ActivationsParser#msg}.
     *
     * @param ctx the parse tree
     */
    void exitMsg(ActivationsParser.MsgContext ctx);

    /**
     * Enter a parse tree produced by {@link ActivationsParser#effect_block}.
     *
     * @param ctx the parse tree
     */
    void enterEffect_block(ActivationsParser.Effect_blockContext ctx);

    /**
     * Exit a parse tree produced by {@link ActivationsParser#effect_block}.
     *
     * @param ctx the parse tree
     */
    void exitEffect_block(ActivationsParser.Effect_blockContext ctx);

    /**
     * Enter a parse tree produced by {@link ActivationsParser#desc}.
     *
     * @param ctx the parse tree
     */
    void enterDesc(ActivationsParser.DescContext ctx);

    /**
     * Exit a parse tree produced by {@link ActivationsParser#desc}.
     *
     * @param ctx the parse tree
     */
    void exitDesc(ActivationsParser.DescContext ctx);

    /**
     * Enter a parse tree produced by {@link ActivationsParser#activation}.
     *
     * @param ctx the parse tree
     */
    void enterActivation(ActivationsParser.ActivationContext ctx);

    /**
     * Exit a parse tree produced by {@link ActivationsParser#activation}.
     *
     * @param ctx the parse tree
     */
    void exitActivation(ActivationsParser.ActivationContext ctx);

    /**
     * Enter a parse tree produced by {@link ActivationsParser#file}.
     *
     * @param ctx the parse tree
     */
    void enterFile(ActivationsParser.FileContext ctx);

    /**
     * Exit a parse tree produced by {@link ActivationsParser#file}.
     *
     * @param ctx the parse tree
     */
    void exitFile(ActivationsParser.FileContext ctx);
}