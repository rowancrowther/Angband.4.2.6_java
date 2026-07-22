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

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link TrapGrammar}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 *            operations with no return type.
 */
public interface TrapGrammarVisitor<T> extends ParseTreeVisitor<T> {
    /**
     * Visit a parse tree produced by {@link TrapGrammar#recordCount}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitRecordCount(TrapGrammar.RecordCountContext ctx);

    /**
     * Visit a parse tree produced by {@link TrapGrammar#name}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitName(TrapGrammar.NameContext ctx);

    /**
     * Visit a parse tree produced by {@link TrapGrammar#graphics}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitGraphics(TrapGrammar.GraphicsContext ctx);

    /**
     * Visit a parse tree produced by {@link TrapGrammar#appear}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitAppear(TrapGrammar.AppearContext ctx);

    /**
     * Visit a parse tree produced by {@link TrapGrammar#visibility}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitVisibility(TrapGrammar.VisibilityContext ctx);

    /**
     * Visit a parse tree produced by {@link TrapGrammar#desc}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitDesc(TrapGrammar.DescContext ctx);

    /**
     * Visit a parse tree produced by {@link TrapGrammar#flags}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFlags(TrapGrammar.FlagsContext ctx);

    /**
     * Visit a parse tree produced by {@link TrapGrammar#msg}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitMsg(TrapGrammar.MsgContext ctx);

    /**
     * Visit a parse tree produced by {@link TrapGrammar#save}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitSave(TrapGrammar.SaveContext ctx);

    /**
     * Visit a parse tree produced by {@link TrapGrammar#msgGood}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitMsgGood(TrapGrammar.MsgGoodContext ctx);

    /**
     * Visit a parse tree produced by {@link TrapGrammar#msgBad}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitMsgBad(TrapGrammar.MsgBadContext ctx);

    /**
     * Visit a parse tree produced by {@link TrapGrammar#msgXtra}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitMsgXtra(TrapGrammar.MsgXtraContext ctx);

    /**
     * Visit a parse tree produced by {@link TrapGrammar#effectXtra}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitEffectXtra(TrapGrammar.EffectXtraContext ctx);

    /**
     * Visit a parse tree produced by {@link TrapGrammar#diceXtra}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitDiceXtra(TrapGrammar.DiceXtraContext ctx);

    /**
     * Visit a parse tree produced by {@link TrapGrammar#exprXtra}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitExprXtra(TrapGrammar.ExprXtraContext ctx);

    /**
     * Visit a parse tree produced by {@link TrapGrammar#effectXtraBlock}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitEffectXtraBlock(TrapGrammar.EffectXtraBlockContext ctx);

    /**
     * Visit a parse tree produced by {@link TrapGrammar#trapRecord}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitTrapRecord(TrapGrammar.TrapRecordContext ctx);

    /**
     * Visit a parse tree produced by {@link TrapGrammar#file}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFile(TrapGrammar.FileContext ctx);

    /**
     * Visit a parse tree produced by {@link TrapGrammar#effect}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitEffect(TrapGrammar.EffectContext ctx);

    /**
     * Visit a parse tree produced by {@link TrapGrammar#time}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitTime(TrapGrammar.TimeContext ctx);

    /**
     * Visit a parse tree produced by {@link TrapGrammar#effectYX}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitEffectYX(TrapGrammar.EffectYXContext ctx);

    /**
     * Visit a parse tree produced by {@link TrapGrammar#dice}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitDice(TrapGrammar.DiceContext ctx);

    /**
     * Visit a parse tree produced by {@link TrapGrammar#expr}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitExpr(TrapGrammar.ExprContext ctx);

    /**
     * Visit a parse tree produced by {@link TrapGrammar#effectMsg}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitEffectMsg(TrapGrammar.EffectMsgContext ctx);

    /**
     * Visit a parse tree produced by {@link TrapGrammar#effectBlock}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitEffectBlock(TrapGrammar.EffectBlockContext ctx);
}