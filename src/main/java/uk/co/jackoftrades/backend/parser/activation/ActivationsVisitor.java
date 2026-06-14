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

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link ActivationsParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface ActivationsVisitor<T> extends ParseTreeVisitor<T> {
    /**
     * Visit a parse tree produced by {@link ActivationsParser#name}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitName(ActivationsParser.NameContext ctx);

    /**
     * Visit a parse tree produced by {@link ActivationsParser#aim}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitAim(ActivationsParser.AimContext ctx);

    /**
     * Visit a parse tree produced by {@link ActivationsParser#level}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitLevel(ActivationsParser.LevelContext ctx);

    /**
     * Visit a parse tree produced by {@link ActivationsParser#power}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitPower(ActivationsParser.PowerContext ctx);

    /**
     * Visit a parse tree produced by {@link ActivationsParser#effect}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitEffect(ActivationsParser.EffectContext ctx);

    /**
     * Visit a parse tree produced by {@link ActivationsParser#effect_yx}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitEffect_yx(ActivationsParser.Effect_yxContext ctx);

    /**
     * Visit a parse tree produced by {@link ActivationsParser#dice}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitDice(ActivationsParser.DiceContext ctx);

    /**
     * Visit a parse tree produced by {@link ActivationsParser#expr}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitExpr(ActivationsParser.ExprContext ctx);

    /**
     * Visit a parse tree produced by {@link ActivationsParser#msg}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitMsg(ActivationsParser.MsgContext ctx);

    /**
     * Visit a parse tree produced by {@link ActivationsParser#effect_block}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitEffect_block(ActivationsParser.Effect_blockContext ctx);

    /**
     * Visit a parse tree produced by {@link ActivationsParser#desc}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitDesc(ActivationsParser.DescContext ctx);

    /**
     * Visit a parse tree produced by {@link ActivationsParser#activation}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitActivation(ActivationsParser.ActivationContext ctx);

    /**
     * Visit a parse tree produced by {@link ActivationsParser#file}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFile(ActivationsParser.FileContext ctx);
}