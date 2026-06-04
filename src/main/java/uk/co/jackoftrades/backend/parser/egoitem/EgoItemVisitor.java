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

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link EgoItemParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 *            operations with no return type.
 */
public interface EgoItemVisitor<T> extends ParseTreeVisitor<T> {
    /**
     * Visit a parse tree produced by {@link EgoItemParser#name}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitName(EgoItemParser.NameContext ctx);

    /**
     * Visit a parse tree produced by {@link EgoItemParser#info}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitInfo(EgoItemParser.InfoContext ctx);

    /**
     * Visit a parse tree produced by {@link EgoItemParser#alloc}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitAlloc(EgoItemParser.AllocContext ctx);

    /**
     * Visit a parse tree produced by {@link EgoItemParser#diceString}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitDiceString(EgoItemParser.DiceStringContext ctx);

    /**
     * Visit a parse tree produced by {@link EgoItemParser#combat}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitCombat(EgoItemParser.CombatContext ctx);

    /**
     * Visit a parse tree produced by {@link EgoItemParser#minCombat}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitMinCombat(EgoItemParser.MinCombatContext ctx);

    /**
     * Visit a parse tree produced by {@link EgoItemParser#type}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitType(EgoItemParser.TypeContext ctx);

    /**
     * Visit a parse tree produced by {@link EgoItemParser#item}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitItem(EgoItemParser.ItemContext ctx);

    /**
     * Visit a parse tree produced by {@link EgoItemParser#flags}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFlags(EgoItemParser.FlagsContext ctx);

    /**
     * Visit a parse tree produced by {@link EgoItemParser#flags_off}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFlags_off(EgoItemParser.Flags_offContext ctx);

    /**
     * Visit a parse tree produced by {@link EgoItemParser#values}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitValues(EgoItemParser.ValuesContext ctx);

    /**
     * Visit a parse tree produced by {@link EgoItemParser#minValues}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitMinValues(EgoItemParser.MinValuesContext ctx);

    /**
     * Visit a parse tree produced by {@link EgoItemParser#act}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitAct(EgoItemParser.ActContext ctx);

    /**
     * Visit a parse tree produced by {@link EgoItemParser#time}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitTime(EgoItemParser.TimeContext ctx);

    /**
     * Visit a parse tree produced by {@link EgoItemParser#brand}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitBrand(EgoItemParser.BrandContext ctx);

    /**
     * Visit a parse tree produced by {@link EgoItemParser#slay}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitSlay(EgoItemParser.SlayContext ctx);

    /**
     * Visit a parse tree produced by {@link EgoItemParser#desc}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitDesc(EgoItemParser.DescContext ctx);

    /**
     * Visit a parse tree produced by {@link EgoItemParser#egoItem}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitEgoItem(EgoItemParser.EgoItemContext ctx);

    /**
     * Visit a parse tree produced by {@link EgoItemParser#file}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFile(EgoItemParser.FileContext ctx);
}