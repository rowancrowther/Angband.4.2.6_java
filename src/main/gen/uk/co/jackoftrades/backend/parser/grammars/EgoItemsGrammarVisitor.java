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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/parser/grammars/EgoItemsGrammar.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.grammars;

    import uk.co.jackoftrades.backend.parser.egoitem.EgoItemParseRecord;
    import uk.co.jackoftrades.backend.parser.egoitem.EgoItemParseRecord.ItemRef;

    import java.util.Map;
    import java.util.List;
    import java.util.HashMap;
    import java.util.ArrayList;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link EgoItemsGrammar}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface EgoItemsGrammarVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link EgoItemsGrammar#recordCount}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRecordCount(EgoItemsGrammar.RecordCountContext ctx);
	/**
	 * Visit a parse tree produced by {@link EgoItemsGrammar#name}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitName(EgoItemsGrammar.NameContext ctx);
	/**
	 * Visit a parse tree produced by {@link EgoItemsGrammar#info}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInfo(EgoItemsGrammar.InfoContext ctx);
	/**
	 * Visit a parse tree produced by {@link EgoItemsGrammar#alloc}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAlloc(EgoItemsGrammar.AllocContext ctx);
	/**
	 * Visit a parse tree produced by {@link EgoItemsGrammar#combat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCombat(EgoItemsGrammar.CombatContext ctx);
	/**
	 * Visit a parse tree produced by {@link EgoItemsGrammar#minCombat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMinCombat(EgoItemsGrammar.MinCombatContext ctx);
	/**
	 * Visit a parse tree produced by {@link EgoItemsGrammar#type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitType(EgoItemsGrammar.TypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link EgoItemsGrammar#item}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitItem(EgoItemsGrammar.ItemContext ctx);
	/**
	 * Visit a parse tree produced by {@link EgoItemsGrammar#flags}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFlags(EgoItemsGrammar.FlagsContext ctx);
	/**
	 * Visit a parse tree produced by {@link EgoItemsGrammar#flagsOff}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFlagsOff(EgoItemsGrammar.FlagsOffContext ctx);
	/**
	 * Visit a parse tree produced by {@link EgoItemsGrammar#values}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitValues(EgoItemsGrammar.ValuesContext ctx);
	/**
	 * Visit a parse tree produced by {@link EgoItemsGrammar#minValues}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMinValues(EgoItemsGrammar.MinValuesContext ctx);
	/**
	 * Visit a parse tree produced by {@link EgoItemsGrammar#act}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAct(EgoItemsGrammar.ActContext ctx);
	/**
	 * Visit a parse tree produced by {@link EgoItemsGrammar#time}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTime(EgoItemsGrammar.TimeContext ctx);
	/**
	 * Visit a parse tree produced by {@link EgoItemsGrammar#brand}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBrand(EgoItemsGrammar.BrandContext ctx);
	/**
	 * Visit a parse tree produced by {@link EgoItemsGrammar#slay}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSlay(EgoItemsGrammar.SlayContext ctx);
	/**
	 * Visit a parse tree produced by {@link EgoItemsGrammar#curse}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCurse(EgoItemsGrammar.CurseContext ctx);
	/**
	 * Visit a parse tree produced by {@link EgoItemsGrammar#desc}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDesc(EgoItemsGrammar.DescContext ctx);
	/**
	 * Visit a parse tree produced by {@link EgoItemsGrammar#egoItem}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEgoItem(EgoItemsGrammar.EgoItemContext ctx);
	/**
	 * Visit a parse tree produced by {@link EgoItemsGrammar#file}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFile(EgoItemsGrammar.FileContext ctx);
}