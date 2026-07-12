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

// Generated from EgoItemsGrammar.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.grammars.egoitems;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link EgoItemsGrammar}.
 */
public interface EgoItemsGrammarListener extends ParseTreeListener {
    /**
     * Enter a parse tree produced by {@link EgoItemsGrammar#recordCount}.
     *
     * @param ctx the parse tree
     */
    void enterRecordCount(EgoItemsGrammar.RecordCountContext ctx);

    /**
     * Exit a parse tree produced by {@link EgoItemsGrammar#recordCount}.
     *
     * @param ctx the parse tree
     */
    void exitRecordCount(EgoItemsGrammar.RecordCountContext ctx);

    /**
     * Enter a parse tree produced by {@link EgoItemsGrammar#name}.
     *
     * @param ctx the parse tree
     */
    void enterName(EgoItemsGrammar.NameContext ctx);

    /**
     * Exit a parse tree produced by {@link EgoItemsGrammar#name}.
     *
     * @param ctx the parse tree
     */
    void exitName(EgoItemsGrammar.NameContext ctx);

    /**
     * Enter a parse tree produced by {@link EgoItemsGrammar#info}.
     *
     * @param ctx the parse tree
     */
    void enterInfo(EgoItemsGrammar.InfoContext ctx);

    /**
     * Exit a parse tree produced by {@link EgoItemsGrammar#info}.
     *
     * @param ctx the parse tree
     */
    void exitInfo(EgoItemsGrammar.InfoContext ctx);

    /**
     * Enter a parse tree produced by {@link EgoItemsGrammar#alloc}.
     *
     * @param ctx the parse tree
     */
    void enterAlloc(EgoItemsGrammar.AllocContext ctx);

    /**
     * Exit a parse tree produced by {@link EgoItemsGrammar#alloc}.
     *
     * @param ctx the parse tree
     */
    void exitAlloc(EgoItemsGrammar.AllocContext ctx);

    /**
     * Enter a parse tree produced by {@link EgoItemsGrammar#combat}.
     *
     * @param ctx the parse tree
     */
    void enterCombat(EgoItemsGrammar.CombatContext ctx);

    /**
     * Exit a parse tree produced by {@link EgoItemsGrammar#combat}.
     *
     * @param ctx the parse tree
     */
    void exitCombat(EgoItemsGrammar.CombatContext ctx);

    /**
     * Enter a parse tree produced by {@link EgoItemsGrammar#minCombat}.
     *
     * @param ctx the parse tree
     */
    void enterMinCombat(EgoItemsGrammar.MinCombatContext ctx);

    /**
     * Exit a parse tree produced by {@link EgoItemsGrammar#minCombat}.
     *
     * @param ctx the parse tree
     */
    void exitMinCombat(EgoItemsGrammar.MinCombatContext ctx);

    /**
     * Enter a parse tree produced by {@link EgoItemsGrammar#type}.
     *
     * @param ctx the parse tree
     */
    void enterType(EgoItemsGrammar.TypeContext ctx);

    /**
     * Exit a parse tree produced by {@link EgoItemsGrammar#type}.
     *
     * @param ctx the parse tree
     */
    void exitType(EgoItemsGrammar.TypeContext ctx);

    /**
     * Enter a parse tree produced by {@link EgoItemsGrammar#item}.
     *
     * @param ctx the parse tree
     */
    void enterItem(EgoItemsGrammar.ItemContext ctx);

    /**
     * Exit a parse tree produced by {@link EgoItemsGrammar#item}.
     *
     * @param ctx the parse tree
     */
    void exitItem(EgoItemsGrammar.ItemContext ctx);

    /**
     * Enter a parse tree produced by {@link EgoItemsGrammar#flags}.
     *
     * @param ctx the parse tree
     */
    void enterFlags(EgoItemsGrammar.FlagsContext ctx);

    /**
     * Exit a parse tree produced by {@link EgoItemsGrammar#flags}.
     *
     * @param ctx the parse tree
     */
    void exitFlags(EgoItemsGrammar.FlagsContext ctx);

    /**
     * Enter a parse tree produced by {@link EgoItemsGrammar#flagsOff}.
     *
     * @param ctx the parse tree
     */
    void enterFlagsOff(EgoItemsGrammar.FlagsOffContext ctx);

    /**
     * Exit a parse tree produced by {@link EgoItemsGrammar#flagsOff}.
     *
     * @param ctx the parse tree
     */
    void exitFlagsOff(EgoItemsGrammar.FlagsOffContext ctx);

    /**
     * Enter a parse tree produced by {@link EgoItemsGrammar#values}.
     *
     * @param ctx the parse tree
     */
    void enterValues(EgoItemsGrammar.ValuesContext ctx);

    /**
     * Exit a parse tree produced by {@link EgoItemsGrammar#values}.
     *
     * @param ctx the parse tree
     */
    void exitValues(EgoItemsGrammar.ValuesContext ctx);

    /**
     * Enter a parse tree produced by {@link EgoItemsGrammar#minValues}.
     *
     * @param ctx the parse tree
     */
    void enterMinValues(EgoItemsGrammar.MinValuesContext ctx);

    /**
     * Exit a parse tree produced by {@link EgoItemsGrammar#minValues}.
     *
     * @param ctx the parse tree
     */
    void exitMinValues(EgoItemsGrammar.MinValuesContext ctx);

    /**
     * Enter a parse tree produced by {@link EgoItemsGrammar#act}.
     *
     * @param ctx the parse tree
     */
    void enterAct(EgoItemsGrammar.ActContext ctx);

    /**
     * Exit a parse tree produced by {@link EgoItemsGrammar#act}.
     *
     * @param ctx the parse tree
     */
    void exitAct(EgoItemsGrammar.ActContext ctx);

    /**
     * Enter a parse tree produced by {@link EgoItemsGrammar#time}.
     *
     * @param ctx the parse tree
     */
    void enterTime(EgoItemsGrammar.TimeContext ctx);

    /**
     * Exit a parse tree produced by {@link EgoItemsGrammar#time}.
     *
     * @param ctx the parse tree
     */
    void exitTime(EgoItemsGrammar.TimeContext ctx);

    /**
     * Enter a parse tree produced by {@link EgoItemsGrammar#brand}.
     *
     * @param ctx the parse tree
     */
    void enterBrand(EgoItemsGrammar.BrandContext ctx);

    /**
     * Exit a parse tree produced by {@link EgoItemsGrammar#brand}.
     *
     * @param ctx the parse tree
     */
    void exitBrand(EgoItemsGrammar.BrandContext ctx);

    /**
     * Enter a parse tree produced by {@link EgoItemsGrammar#slay}.
     *
     * @param ctx the parse tree
     */
    void enterSlay(EgoItemsGrammar.SlayContext ctx);

    /**
     * Exit a parse tree produced by {@link EgoItemsGrammar#slay}.
     *
     * @param ctx the parse tree
     */
    void exitSlay(EgoItemsGrammar.SlayContext ctx);

    /**
     * Enter a parse tree produced by {@link EgoItemsGrammar#curse}.
     *
     * @param ctx the parse tree
     */
    void enterCurse(EgoItemsGrammar.CurseContext ctx);

    /**
     * Exit a parse tree produced by {@link EgoItemsGrammar#curse}.
     *
     * @param ctx the parse tree
     */
    void exitCurse(EgoItemsGrammar.CurseContext ctx);

    /**
     * Enter a parse tree produced by {@link EgoItemsGrammar#desc}.
     *
     * @param ctx the parse tree
     */
    void enterDesc(EgoItemsGrammar.DescContext ctx);

    /**
     * Exit a parse tree produced by {@link EgoItemsGrammar#desc}.
     *
     * @param ctx the parse tree
     */
    void exitDesc(EgoItemsGrammar.DescContext ctx);

    /**
     * Enter a parse tree produced by {@link EgoItemsGrammar#egoItem}.
     *
     * @param ctx the parse tree
     */
    void enterEgoItem(EgoItemsGrammar.EgoItemContext ctx);

    /**
     * Exit a parse tree produced by {@link EgoItemsGrammar#egoItem}.
     *
     * @param ctx the parse tree
     */
    void exitEgoItem(EgoItemsGrammar.EgoItemContext ctx);

    /**
     * Enter a parse tree produced by {@link EgoItemsGrammar#file}.
     *
     * @param ctx the parse tree
     */
    void enterFile(EgoItemsGrammar.FileContext ctx);

    /**
     * Exit a parse tree produced by {@link EgoItemsGrammar#file}.
     *
     * @param ctx the parse tree
     */
    void exitFile(EgoItemsGrammar.FileContext ctx);
}