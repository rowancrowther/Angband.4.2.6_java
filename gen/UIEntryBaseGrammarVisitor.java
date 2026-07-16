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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/scratch/antlr_artifact_check/src/uk/co/jackoftrades/backend/parser/grammars/UIEntryBaseGrammar.g4 by ANTLR 4.13.2

    import uk.co.jackoftrades.backend.parser.uientrybase.UIEntryBaseParseRecord;

    import java.util.List;
    import java.util.ArrayList;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link UIEntryBaseGrammar}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface UIEntryBaseGrammarVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link UIEntryBaseGrammar#recordCount}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRecordCount(UIEntryBaseGrammar.RecordCountContext ctx);
	/**
	 * Visit a parse tree produced by {@link UIEntryBaseGrammar#name}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitName(UIEntryBaseGrammar.NameContext ctx);
	/**
	 * Visit a parse tree produced by {@link UIEntryBaseGrammar#renderer}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRenderer(UIEntryBaseGrammar.RendererContext ctx);
	/**
	 * Visit a parse tree produced by {@link UIEntryBaseGrammar#combine}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCombine(UIEntryBaseGrammar.CombineContext ctx);
	/**
	 * Visit a parse tree produced by {@link UIEntryBaseGrammar#category}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCategory(UIEntryBaseGrammar.CategoryContext ctx);
	/**
	 * Visit a parse tree produced by {@link UIEntryBaseGrammar#flags}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFlags(UIEntryBaseGrammar.FlagsContext ctx);
	/**
	 * Visit a parse tree produced by {@link UIEntryBaseGrammar#desc}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDesc(UIEntryBaseGrammar.DescContext ctx);
	/**
	 * Visit a parse tree produced by {@link UIEntryBaseGrammar#entryBase}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEntryBase(UIEntryBaseGrammar.EntryBaseContext ctx);
	/**
	 * Visit a parse tree produced by {@link UIEntryBaseGrammar#file}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFile(UIEntryBaseGrammar.FileContext ctx);
}