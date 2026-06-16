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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/parser/grammars/PlayerTimed.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.playertimed;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link PlayerTimedParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 *            operations with no return type.
 */
public interface PlayerTimedVisitor<T> extends ParseTreeVisitor<T> {
    /**
     * Visit a parse tree produced by {@link PlayerTimedParser#name}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitName(PlayerTimedParser.NameContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerTimedParser#desc}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitDesc(PlayerTimedParser.DescContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerTimedParser#grade}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitGrade(PlayerTimedParser.GradeContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerTimedParser#onEnd}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitOnEnd(PlayerTimedParser.OnEndContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerTimedParser#onIncrease}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitOnIncrease(PlayerTimedParser.OnIncreaseContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerTimedParser#onDecrease}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitOnDecrease(PlayerTimedParser.OnDecreaseContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerTimedParser#msgt}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitMsgt(PlayerTimedParser.MsgtContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerTimedParser#fail}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFail(PlayerTimedParser.FailContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerTimedParser#onBeginEffect}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitOnBeginEffect(PlayerTimedParser.OnBeginEffectContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerTimedParser#onEndEffect}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitOnEndEffect(PlayerTimedParser.OnEndEffectContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerTimedParser#effectYX}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitEffectYX(PlayerTimedParser.EffectYXContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerTimedParser#effectDice}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitEffectDice(PlayerTimedParser.EffectDiceContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerTimedParser#effectMsg}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitEffectMsg(PlayerTimedParser.EffectMsgContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerTimedParser#resist}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitResist(PlayerTimedParser.ResistContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerTimedParser#brand}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitBrand(PlayerTimedParser.BrandContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerTimedParser#slay}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitSlay(PlayerTimedParser.SlayContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerTimedParser#flagSynonym}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFlagSynonym(PlayerTimedParser.FlagSynonymContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerTimedParser#lowerBound}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitLowerBound(PlayerTimedParser.LowerBoundContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerTimedParser#flags}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFlags(PlayerTimedParser.FlagsContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerTimedParser#playerTimed}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitPlayerTimed(PlayerTimedParser.PlayerTimedContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerTimedParser#file}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFile(PlayerTimedParser.FileContext ctx);
}