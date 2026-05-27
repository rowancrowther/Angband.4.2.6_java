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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/parser/grammars/Summon.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.summon;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link SummonParser}.
 */
public interface SummonListener extends ParseTreeListener {
    /**
     * Enter a parse tree produced by {@link SummonParser#name}.
     *
     * @param ctx the parse tree
     */
    void enterName(SummonParser.NameContext ctx);

    /**
     * Exit a parse tree produced by {@link SummonParser#name}.
     *
     * @param ctx the parse tree
     */
    void exitName(SummonParser.NameContext ctx);

    /**
     * Enter a parse tree produced by {@link SummonParser#msgt}.
     *
     * @param ctx the parse tree
     */
    void enterMsgt(SummonParser.MsgtContext ctx);

    /**
     * Exit a parse tree produced by {@link SummonParser#msgt}.
     *
     * @param ctx the parse tree
     */
    void exitMsgt(SummonParser.MsgtContext ctx);

    /**
     * Enter a parse tree produced by {@link SummonParser#uniques}.
     *
     * @param ctx the parse tree
     */
    void enterUniques(SummonParser.UniquesContext ctx);

    /**
     * Exit a parse tree produced by {@link SummonParser#uniques}.
     *
     * @param ctx the parse tree
     */
    void exitUniques(SummonParser.UniquesContext ctx);

    /**
     * Enter a parse tree produced by {@link SummonParser#base}.
     *
     * @param ctx the parse tree
     */
    void enterBase(SummonParser.BaseContext ctx);

    /**
     * Exit a parse tree produced by {@link SummonParser#base}.
     *
     * @param ctx the parse tree
     */
    void exitBase(SummonParser.BaseContext ctx);

    /**
     * Enter a parse tree produced by {@link SummonParser#race_flag}.
     *
     * @param ctx the parse tree
     */
    void enterRace_flag(SummonParser.Race_flagContext ctx);

    /**
     * Exit a parse tree produced by {@link SummonParser#race_flag}.
     *
     * @param ctx the parse tree
     */
    void exitRace_flag(SummonParser.Race_flagContext ctx);

    /**
     * Enter a parse tree produced by {@link SummonParser#fallback}.
     *
     * @param ctx the parse tree
     */
    void enterFallback(SummonParser.FallbackContext ctx);

    /**
     * Exit a parse tree produced by {@link SummonParser#fallback}.
     *
     * @param ctx the parse tree
     */
    void exitFallback(SummonParser.FallbackContext ctx);

    /**
     * Enter a parse tree produced by {@link SummonParser#desc}.
     *
     * @param ctx the parse tree
     */
    void enterDesc(SummonParser.DescContext ctx);

    /**
     * Exit a parse tree produced by {@link SummonParser#desc}.
     *
     * @param ctx the parse tree
     */
    void exitDesc(SummonParser.DescContext ctx);

    /**
     * Enter a parse tree produced by {@link SummonParser#summon}.
     *
     * @param ctx the parse tree
     */
    void enterSummon(SummonParser.SummonContext ctx);

    /**
     * Exit a parse tree produced by {@link SummonParser#summon}.
     *
     * @param ctx the parse tree
     */
    void exitSummon(SummonParser.SummonContext ctx);

    /**
     * Enter a parse tree produced by {@link SummonParser#file}.
     *
     * @param ctx the parse tree
     */
    void enterFile(SummonParser.FileContext ctx);

    /**
     * Exit a parse tree produced by {@link SummonParser#file}.
     *
     * @param ctx the parse tree
     */
    void exitFile(SummonParser.FileContext ctx);
}