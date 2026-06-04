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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/parser/grammars/EgoItem.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.egoitem;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link EgoItemParser}.
 */
public interface EgoItemListener extends ParseTreeListener {
    /**
     * Enter a parse tree produced by {@link EgoItemParser#name}.
     *
     * @param ctx the parse tree
     */
    void enterName(EgoItemParser.NameContext ctx);

    /**
     * Exit a parse tree produced by {@link EgoItemParser#name}.
     *
     * @param ctx the parse tree
     */
    void exitName(EgoItemParser.NameContext ctx);

    /**
     * Enter a parse tree produced by {@link EgoItemParser#info}.
     *
     * @param ctx the parse tree
     */
    void enterInfo(EgoItemParser.InfoContext ctx);

    /**
     * Exit a parse tree produced by {@link EgoItemParser#info}.
     *
     * @param ctx the parse tree
     */
    void exitInfo(EgoItemParser.InfoContext ctx);

    /**
     * Enter a parse tree produced by {@link EgoItemParser#alloc}.
     *
     * @param ctx the parse tree
     */
    void enterAlloc(EgoItemParser.AllocContext ctx);

    /**
     * Exit a parse tree produced by {@link EgoItemParser#alloc}.
     *
     * @param ctx the parse tree
     */
    void exitAlloc(EgoItemParser.AllocContext ctx);

    /**
     * Enter a parse tree produced by {@link EgoItemParser#diceString}.
     *
     * @param ctx the parse tree
     */
    void enterDiceString(EgoItemParser.DiceStringContext ctx);

    /**
     * Exit a parse tree produced by {@link EgoItemParser#diceString}.
     *
     * @param ctx the parse tree
     */
    void exitDiceString(EgoItemParser.DiceStringContext ctx);

    /**
     * Enter a parse tree produced by {@link EgoItemParser#combat}.
     *
     * @param ctx the parse tree
     */
    void enterCombat(EgoItemParser.CombatContext ctx);

    /**
     * Exit a parse tree produced by {@link EgoItemParser#combat}.
     *
     * @param ctx the parse tree
     */
    void exitCombat(EgoItemParser.CombatContext ctx);

    /**
     * Enter a parse tree produced by {@link EgoItemParser#minCombat}.
     *
     * @param ctx the parse tree
     */
    void enterMinCombat(EgoItemParser.MinCombatContext ctx);

    /**
     * Exit a parse tree produced by {@link EgoItemParser#minCombat}.
     *
     * @param ctx the parse tree
     */
    void exitMinCombat(EgoItemParser.MinCombatContext ctx);

    /**
     * Enter a parse tree produced by {@link EgoItemParser#type}.
     *
     * @param ctx the parse tree
     */
    void enterType(EgoItemParser.TypeContext ctx);

    /**
     * Exit a parse tree produced by {@link EgoItemParser#type}.
     *
     * @param ctx the parse tree
     */
    void exitType(EgoItemParser.TypeContext ctx);

    /**
     * Enter a parse tree produced by {@link EgoItemParser#item}.
     *
     * @param ctx the parse tree
     */
    void enterItem(EgoItemParser.ItemContext ctx);

    /**
     * Exit a parse tree produced by {@link EgoItemParser#item}.
     *
     * @param ctx the parse tree
     */
    void exitItem(EgoItemParser.ItemContext ctx);

    /**
     * Enter a parse tree produced by {@link EgoItemParser#flags}.
     *
     * @param ctx the parse tree
     */
    void enterFlags(EgoItemParser.FlagsContext ctx);

    /**
     * Exit a parse tree produced by {@link EgoItemParser#flags}.
     *
     * @param ctx the parse tree
     */
    void exitFlags(EgoItemParser.FlagsContext ctx);

    /**
     * Enter a parse tree produced by {@link EgoItemParser#flags_off}.
     *
     * @param ctx the parse tree
     */
    void enterFlags_off(EgoItemParser.Flags_offContext ctx);

    /**
     * Exit a parse tree produced by {@link EgoItemParser#flags_off}.
     *
     * @param ctx the parse tree
     */
    void exitFlags_off(EgoItemParser.Flags_offContext ctx);

    /**
     * Enter a parse tree produced by {@link EgoItemParser#values}.
     *
     * @param ctx the parse tree
     */
    void enterValues(EgoItemParser.ValuesContext ctx);

    /**
     * Exit a parse tree produced by {@link EgoItemParser#values}.
     *
     * @param ctx the parse tree
     */
    void exitValues(EgoItemParser.ValuesContext ctx);

    /**
     * Enter a parse tree produced by {@link EgoItemParser#minValues}.
     *
     * @param ctx the parse tree
     */
    void enterMinValues(EgoItemParser.MinValuesContext ctx);

    /**
     * Exit a parse tree produced by {@link EgoItemParser#minValues}.
     *
     * @param ctx the parse tree
     */
    void exitMinValues(EgoItemParser.MinValuesContext ctx);

    /**
     * Enter a parse tree produced by {@link EgoItemParser#act}.
     *
     * @param ctx the parse tree
     */
    void enterAct(EgoItemParser.ActContext ctx);

    /**
     * Exit a parse tree produced by {@link EgoItemParser#act}.
     *
     * @param ctx the parse tree
     */
    void exitAct(EgoItemParser.ActContext ctx);

    /**
     * Enter a parse tree produced by {@link EgoItemParser#time}.
     *
     * @param ctx the parse tree
     */
    void enterTime(EgoItemParser.TimeContext ctx);

    /**
     * Exit a parse tree produced by {@link EgoItemParser#time}.
     *
     * @param ctx the parse tree
     */
    void exitTime(EgoItemParser.TimeContext ctx);

    /**
     * Enter a parse tree produced by {@link EgoItemParser#brand}.
     *
     * @param ctx the parse tree
     */
    void enterBrand(EgoItemParser.BrandContext ctx);

    /**
     * Exit a parse tree produced by {@link EgoItemParser#brand}.
     *
     * @param ctx the parse tree
     */
    void exitBrand(EgoItemParser.BrandContext ctx);

    /**
     * Enter a parse tree produced by {@link EgoItemParser#slay}.
     *
     * @param ctx the parse tree
     */
    void enterSlay(EgoItemParser.SlayContext ctx);

    /**
     * Exit a parse tree produced by {@link EgoItemParser#slay}.
     *
     * @param ctx the parse tree
     */
    void exitSlay(EgoItemParser.SlayContext ctx);

    /**
     * Enter a parse tree produced by {@link EgoItemParser#desc}.
     *
     * @param ctx the parse tree
     */
    void enterDesc(EgoItemParser.DescContext ctx);

    /**
     * Exit a parse tree produced by {@link EgoItemParser#desc}.
     *
     * @param ctx the parse tree
     */
    void exitDesc(EgoItemParser.DescContext ctx);

    /**
     * Enter a parse tree produced by {@link EgoItemParser#egoItem}.
     *
     * @param ctx the parse tree
     */
    void enterEgoItem(EgoItemParser.EgoItemContext ctx);

    /**
     * Exit a parse tree produced by {@link EgoItemParser#egoItem}.
     *
     * @param ctx the parse tree
     */
    void exitEgoItem(EgoItemParser.EgoItemContext ctx);

    /**
     * Enter a parse tree produced by {@link EgoItemParser#file}.
     *
     * @param ctx the parse tree
     */
    void enterFile(EgoItemParser.FileContext ctx);

    /**
     * Exit a parse tree produced by {@link EgoItemParser#file}.
     *
     * @param ctx the parse tree
     */
    void exitFile(EgoItemParser.FileContext ctx);
}