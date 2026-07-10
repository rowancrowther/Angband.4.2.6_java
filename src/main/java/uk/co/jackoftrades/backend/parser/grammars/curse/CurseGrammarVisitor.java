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
// Generated from CurseGrammar.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.grammars.curse;

import uk.co.jackoftrades.backend.parser.curse.CurseParseRecord;
import uk.co.jackoftrades.backend.parser.grammars.EffectParseRecord;

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link CurseGrammar}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface CurseGrammarVisitor<T> extends ParseTreeVisitor<T> {
    /**
     * Visit a parse tree produced by {@link CurseGrammar#recordCount}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitRecordCount(CurseGrammar.RecordCountContext ctx);

    /**
     * Visit a parse tree produced by {@link CurseGrammar#name}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitName(CurseGrammar.NameContext ctx);

    /**
     * Visit a parse tree produced by {@link CurseGrammar#curseType}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitCurseType(CurseGrammar.CurseTypeContext ctx);

    /**
     * Visit a parse tree produced by {@link CurseGrammar#weight}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitWeight(CurseGrammar.WeightContext ctx);

    /**
     * Visit a parse tree produced by {@link CurseGrammar#combat}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitCombat(CurseGrammar.CombatContext ctx);

    /**
     * Visit a parse tree produced by {@link CurseGrammar#flags}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFlags(CurseGrammar.FlagsContext ctx);

    /**
     * Visit a parse tree produced by {@link CurseGrammar#values}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitValues(CurseGrammar.ValuesContext ctx);

    /**
     * Visit a parse tree produced by {@link CurseGrammar#msg}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitMsg(CurseGrammar.MsgContext ctx);

    /**
     * Visit a parse tree produced by {@link CurseGrammar#desc}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitDesc(CurseGrammar.DescContext ctx);

    /**
     * Visit a parse tree produced by {@link CurseGrammar#conflict}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitConflict(CurseGrammar.ConflictContext ctx);

    /**
     * Visit a parse tree produced by {@link CurseGrammar#conflictFlags}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitConflictFlags(CurseGrammar.ConflictFlagsContext ctx);

    /**
     * Visit a parse tree produced by {@link CurseGrammar#curseRecord}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitCurseRecord(CurseGrammar.CurseRecordContext ctx);

    /**
     * Visit a parse tree produced by {@link CurseGrammar#file}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFile(CurseGrammar.FileContext ctx);

    /**
     * Visit a parse tree produced by {@link CurseGrammar#effect}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitEffect(CurseGrammar.EffectContext ctx);

    /**
     * Visit a parse tree produced by {@link CurseGrammar#time}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitTime(CurseGrammar.TimeContext ctx);

    /**
     * Visit a parse tree produced by {@link CurseGrammar#effectYX}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitEffectYX(CurseGrammar.EffectYXContext ctx);

    /**
     * Visit a parse tree produced by {@link CurseGrammar#dice}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitDice(CurseGrammar.DiceContext ctx);

    /**
     * Visit a parse tree produced by {@link CurseGrammar#expr}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitExpr(CurseGrammar.ExprContext ctx);

    /**
     * Visit a parse tree produced by {@link CurseGrammar#effectMsg}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitEffectMsg(CurseGrammar.EffectMsgContext ctx);

    /**
     * Visit a parse tree produced by {@link CurseGrammar#effectBlock}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitEffectBlock(CurseGrammar.EffectBlockContext ctx);
}