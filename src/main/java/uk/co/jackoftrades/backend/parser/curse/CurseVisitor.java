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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/parser/grammars/Curse.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.curse;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link CurseParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 *            operations with no return type.
 */
public interface CurseVisitor<T> extends ParseTreeVisitor<T> {
    /**
     * Visit a parse tree produced by {@link CurseParser#name}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitName(CurseParser.NameContext ctx);

    /**
     * Visit a parse tree produced by {@link CurseParser#type}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitType(CurseParser.TypeContext ctx);

    /**
     * Visit a parse tree produced by {@link CurseParser#weight}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitWeight(CurseParser.WeightContext ctx);

    /**
     * Visit a parse tree produced by {@link CurseParser#combat}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitCombat(CurseParser.CombatContext ctx);

    /**
     * Visit a parse tree produced by {@link CurseParser#effect}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitEffect(CurseParser.EffectContext ctx);

    /**
     * Visit a parse tree produced by {@link CurseParser#dice}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitDice(CurseParser.DiceContext ctx);

    /**
     * Visit a parse tree produced by {@link CurseParser#expr}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitExpr(CurseParser.ExprContext ctx);

    /**
     * Visit a parse tree produced by {@link CurseParser#time}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitTime(CurseParser.TimeContext ctx);

    /**
     * Visit a parse tree produced by {@link CurseParser#flags}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFlags(CurseParser.FlagsContext ctx);

    /**
     * Visit a parse tree produced by {@link CurseParser#values}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitValues(CurseParser.ValuesContext ctx);

    /**
     * Visit a parse tree produced by {@link CurseParser#msg}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitMsg(CurseParser.MsgContext ctx);

    /**
     * Visit a parse tree produced by {@link CurseParser#desc}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitDesc(CurseParser.DescContext ctx);

    /**
     * Visit a parse tree produced by {@link CurseParser#conflict}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitConflict(CurseParser.ConflictContext ctx);

    /**
     * Visit a parse tree produced by {@link CurseParser#conflict_flags}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitConflict_flags(CurseParser.Conflict_flagsContext ctx);

    /**
     * Visit a parse tree produced by {@link CurseParser#curse}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitCurse(CurseParser.CurseContext ctx);

    /**
     * Visit a parse tree produced by {@link CurseParser#file}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFile(CurseParser.FileContext ctx);
}