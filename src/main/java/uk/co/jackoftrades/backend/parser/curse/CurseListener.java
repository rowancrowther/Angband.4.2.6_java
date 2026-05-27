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

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link CurseParser}.
 */
public interface CurseListener extends ParseTreeListener {
    /**
     * Enter a parse tree produced by {@link CurseParser#name}.
     *
     * @param ctx the parse tree
     */
    void enterName(CurseParser.NameContext ctx);

    /**
     * Exit a parse tree produced by {@link CurseParser#name}.
     *
     * @param ctx the parse tree
     */
    void exitName(CurseParser.NameContext ctx);

    /**
     * Enter a parse tree produced by {@link CurseParser#type}.
     *
     * @param ctx the parse tree
     */
    void enterType(CurseParser.TypeContext ctx);

    /**
     * Exit a parse tree produced by {@link CurseParser#type}.
     *
     * @param ctx the parse tree
     */
    void exitType(CurseParser.TypeContext ctx);

    /**
     * Enter a parse tree produced by {@link CurseParser#weight}.
     *
     * @param ctx the parse tree
     */
    void enterWeight(CurseParser.WeightContext ctx);

    /**
     * Exit a parse tree produced by {@link CurseParser#weight}.
     *
     * @param ctx the parse tree
     */
    void exitWeight(CurseParser.WeightContext ctx);

    /**
     * Enter a parse tree produced by {@link CurseParser#combat}.
     *
     * @param ctx the parse tree
     */
    void enterCombat(CurseParser.CombatContext ctx);

    /**
     * Exit a parse tree produced by {@link CurseParser#combat}.
     *
     * @param ctx the parse tree
     */
    void exitCombat(CurseParser.CombatContext ctx);

    /**
     * Enter a parse tree produced by {@link CurseParser#effect}.
     *
     * @param ctx the parse tree
     */
    void enterEffect(CurseParser.EffectContext ctx);

    /**
     * Exit a parse tree produced by {@link CurseParser#effect}.
     *
     * @param ctx the parse tree
     */
    void exitEffect(CurseParser.EffectContext ctx);

    /**
     * Enter a parse tree produced by {@link CurseParser#dice}.
     *
     * @param ctx the parse tree
     */
    void enterDice(CurseParser.DiceContext ctx);

    /**
     * Exit a parse tree produced by {@link CurseParser#dice}.
     *
     * @param ctx the parse tree
     */
    void exitDice(CurseParser.DiceContext ctx);

    /**
     * Enter a parse tree produced by {@link CurseParser#expr}.
     *
     * @param ctx the parse tree
     */
    void enterExpr(CurseParser.ExprContext ctx);

    /**
     * Exit a parse tree produced by {@link CurseParser#expr}.
     *
     * @param ctx the parse tree
     */
    void exitExpr(CurseParser.ExprContext ctx);

    /**
     * Enter a parse tree produced by {@link CurseParser#time}.
     *
     * @param ctx the parse tree
     */
    void enterTime(CurseParser.TimeContext ctx);

    /**
     * Exit a parse tree produced by {@link CurseParser#time}.
     *
     * @param ctx the parse tree
     */
    void exitTime(CurseParser.TimeContext ctx);

    /**
     * Enter a parse tree produced by {@link CurseParser#flags}.
     *
     * @param ctx the parse tree
     */
    void enterFlags(CurseParser.FlagsContext ctx);

    /**
     * Exit a parse tree produced by {@link CurseParser#flags}.
     *
     * @param ctx the parse tree
     */
    void exitFlags(CurseParser.FlagsContext ctx);

    /**
     * Enter a parse tree produced by {@link CurseParser#values}.
     *
     * @param ctx the parse tree
     */
    void enterValues(CurseParser.ValuesContext ctx);

    /**
     * Exit a parse tree produced by {@link CurseParser#values}.
     *
     * @param ctx the parse tree
     */
    void exitValues(CurseParser.ValuesContext ctx);

    /**
     * Enter a parse tree produced by {@link CurseParser#msg}.
     *
     * @param ctx the parse tree
     */
    void enterMsg(CurseParser.MsgContext ctx);

    /**
     * Exit a parse tree produced by {@link CurseParser#msg}.
     *
     * @param ctx the parse tree
     */
    void exitMsg(CurseParser.MsgContext ctx);

    /**
     * Enter a parse tree produced by {@link CurseParser#desc}.
     *
     * @param ctx the parse tree
     */
    void enterDesc(CurseParser.DescContext ctx);

    /**
     * Exit a parse tree produced by {@link CurseParser#desc}.
     *
     * @param ctx the parse tree
     */
    void exitDesc(CurseParser.DescContext ctx);

    /**
     * Enter a parse tree produced by {@link CurseParser#conflict}.
     *
     * @param ctx the parse tree
     */
    void enterConflict(CurseParser.ConflictContext ctx);

    /**
     * Exit a parse tree produced by {@link CurseParser#conflict}.
     *
     * @param ctx the parse tree
     */
    void exitConflict(CurseParser.ConflictContext ctx);

    /**
     * Enter a parse tree produced by {@link CurseParser#conflict_flags}.
     *
     * @param ctx the parse tree
     */
    void enterConflict_flags(CurseParser.Conflict_flagsContext ctx);

    /**
     * Exit a parse tree produced by {@link CurseParser#conflict_flags}.
     *
     * @param ctx the parse tree
     */
    void exitConflict_flags(CurseParser.Conflict_flagsContext ctx);

    /**
     * Enter a parse tree produced by {@link CurseParser#curse}.
     *
     * @param ctx the parse tree
     */
    void enterCurse(CurseParser.CurseContext ctx);

    /**
     * Exit a parse tree produced by {@link CurseParser#curse}.
     *
     * @param ctx the parse tree
     */
    void exitCurse(CurseParser.CurseContext ctx);

    /**
     * Enter a parse tree produced by {@link CurseParser#file}.
     *
     * @param ctx the parse tree
     */
    void enterFile(CurseParser.FileContext ctx);

    /**
     * Exit a parse tree produced by {@link CurseParser#file}.
     *
     * @param ctx the parse tree
     */
    void exitFile(CurseParser.FileContext ctx);
}