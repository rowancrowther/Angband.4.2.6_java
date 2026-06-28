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

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link Activations}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 *            operations with no return type.
 */
public interface ActivationsVisitor<T> extends ParseTreeVisitor<T> {
    /**
     * Visit a parse tree produced by {@link Activations#recordCount}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitRecordCount(Activations.RecordCountContext ctx);

    /**
     * Visit a parse tree produced by {@link Activations#name}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitName(Activations.NameContext ctx);

    /**
     * Visit a parse tree produced by {@link Activations#aim}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitAim(Activations.AimContext ctx);

    /**
     * Visit a parse tree produced by {@link Activations#level}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitLevel(Activations.LevelContext ctx);

    /**
     * Visit a parse tree produced by {@link Activations#power}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitPower(Activations.PowerContext ctx);

    /**
     * Visit a parse tree produced by {@link Activations#desc}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitDesc(Activations.DescContext ctx);

    /**
     * Visit a parse tree produced by {@link Activations#msg}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitMsg(Activations.MsgContext ctx);

    /**
     * Visit a parse tree produced by {@link Activations#activation}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitActivation(Activations.ActivationContext ctx);

    /**
     * Visit a parse tree produced by {@link Activations#file}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFile(Activations.FileContext ctx);

    /**
     * Visit a parse tree produced by {@link Activations#effect}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitEffect(Activations.EffectContext ctx);

    /**
     * Visit a parse tree produced by {@link Activations#time}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitTime(Activations.TimeContext ctx);

    /**
     * Visit a parse tree produced by {@link Activations#effectYX}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitEffectYX(Activations.EffectYXContext ctx);

    /**
     * Visit a parse tree produced by {@link Activations#dice}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitDice(Activations.DiceContext ctx);

    /**
     * Visit a parse tree produced by {@link Activations#expr}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitExpr(Activations.ExprContext ctx);

    /**
     * Visit a parse tree produced by {@link Activations#effectMsg}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitEffectMsg(Activations.EffectMsgContext ctx);

    /**
     * Visit a parse tree produced by {@link Activations#effectBlock}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitEffectBlock(Activations.EffectBlockContext ctx);
}