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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/parser/grammars/UIEntryBase.g4 by ANTLR 4.13.2

package uk.co.jackoftrades.backend.parser.uientrybase;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link UIEntryBaseParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface UIEntryBaseVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link UIEntryBaseParser#name}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitName(UIEntryBaseParser.NameContext ctx);
	/**
	 * Visit a parse tree produced by {@link UIEntryBaseParser#renderer}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRenderer(UIEntryBaseParser.RendererContext ctx);
	/**
	 * Visit a parse tree produced by {@link UIEntryBaseParser#combine}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCombine(UIEntryBaseParser.CombineContext ctx);
	/**
	 * Visit a parse tree produced by {@link UIEntryBaseParser#category}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCategory(UIEntryBaseParser.CategoryContext ctx);
	/**
	 * Visit a parse tree produced by {@link UIEntryBaseParser#flags}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFlags(UIEntryBaseParser.FlagsContext ctx);
	/**
	 * Visit a parse tree produced by {@link UIEntryBaseParser#desc}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDesc(UIEntryBaseParser.DescContext ctx);
	/**
	 * Visit a parse tree produced by {@link UIEntryBaseParser#entryBase}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEntryBase(UIEntryBaseParser.EntryBaseContext ctx);
	/**
	 * Visit a parse tree produced by {@link UIEntryBaseParser#file}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFile(UIEntryBaseParser.FileContext ctx);
}