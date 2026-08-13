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
package uk.co.jackoftrades.backend.parser.grammars.chesttrap;

import uk.co.jackoftrades.backend.parser.chesttrap.ChestTrapParseRecord;
import uk.co.jackoftrades.backend.parser.grammars.EffectParseRecord;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link ChestTrapGrammar}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 *            operations with no return type.
 */
public interface ChestTrapGrammarVisitor<T> extends ParseTreeVisitor<T> {
    /**
     * Visit a parse tree produced by {@link ChestTrapGrammar#recordCount}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitRecordCount(ChestTrapGrammar.RecordCountContext ctx);

    /**
     * Visit a parse tree produced by {@link ChestTrapGrammar#name}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitName(ChestTrapGrammar.NameContext ctx);

    /**
     * Visit a parse tree produced by {@link ChestTrapGrammar#code}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitCode(ChestTrapGrammar.CodeContext ctx);

    /**
     * Visit a parse tree produced by {@link ChestTrapGrammar#level}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitLevel(ChestTrapGrammar.LevelContext ctx);

    /**
     * Visit a parse tree produced by {@link ChestTrapGrammar#destroy}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitDestroy(ChestTrapGrammar.DestroyContext ctx);

    /**
     * Visit a parse tree produced by {@link ChestTrapGrammar#magic}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitMagic(ChestTrapGrammar.MagicContext ctx);

    /**
     * Visit a parse tree produced by {@link ChestTrapGrammar#msg}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitMsg(ChestTrapGrammar.MsgContext ctx);

    /**
     * Visit a parse tree produced by {@link ChestTrapGrammar#msgDeath}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitMsgDeath(ChestTrapGrammar.MsgDeathContext ctx);

    /**
     * Visit a parse tree produced by {@link ChestTrapGrammar#chestTrap}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitChestTrap(ChestTrapGrammar.ChestTrapContext ctx);

    /**
     * Visit a parse tree produced by {@link ChestTrapGrammar#file}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFile(ChestTrapGrammar.FileContext ctx);

    /**
     * Visit a parse tree produced by {@link ChestTrapGrammar#effect}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitEffect(ChestTrapGrammar.EffectContext ctx);

    /**
     * Visit a parse tree produced by {@link ChestTrapGrammar#time}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitTime(ChestTrapGrammar.TimeContext ctx);

    /**
     * Visit a parse tree produced by {@link ChestTrapGrammar#effectYX}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitEffectYX(ChestTrapGrammar.EffectYXContext ctx);

    /**
     * Visit a parse tree produced by {@link ChestTrapGrammar#dice}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitDice(ChestTrapGrammar.DiceContext ctx);

    /**
     * Visit a parse tree produced by {@link ChestTrapGrammar#expr}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitExpr(ChestTrapGrammar.ExprContext ctx);

    /**
     * Visit a parse tree produced by {@link ChestTrapGrammar#effectMsg}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitEffectMsg(ChestTrapGrammar.EffectMsgContext ctx);

    /**
     * Visit a parse tree produced by {@link ChestTrapGrammar#effectBlock}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitEffectBlock(ChestTrapGrammar.EffectBlockContext ctx);
}