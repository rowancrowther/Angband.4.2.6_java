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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/parser/grammars/UIEntryGrammar.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.grammars;

    import uk.co.jackoftrades.backend.parser.uientry.UIEntryParseRecord;

    import java.util.List;
    import java.util.ArrayList;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link UIEntryGrammar}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface UIEntryGrammarVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link UIEntryGrammar#recordCount}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRecordCount(UIEntryGrammar.RecordCountContext ctx);
	/**
	 * Visit a parse tree produced by {@link UIEntryGrammar#tag}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTag(UIEntryGrammar.TagContext ctx);
	/**
	 * Visit a parse tree produced by {@link UIEntryGrammar#name}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitName(UIEntryGrammar.NameContext ctx);
	/**
	 * Visit a parse tree produced by {@link UIEntryGrammar#parameter}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParameter(UIEntryGrammar.ParameterContext ctx);
	/**
	 * Visit a parse tree produced by {@link UIEntryGrammar#renderer}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRenderer(UIEntryGrammar.RendererContext ctx);
	/**
	 * Visit a parse tree produced by {@link UIEntryGrammar#combine}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCombine(UIEntryGrammar.CombineContext ctx);
	/**
	 * Visit a parse tree produced by {@link UIEntryGrammar#priority}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPriority(UIEntryGrammar.PriorityContext ctx);
	/**
	 * Visit a parse tree produced by {@link UIEntryGrammar#category}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCategory(UIEntryGrammar.CategoryContext ctx);
	/**
	 * Visit a parse tree produced by {@link UIEntryGrammar#flags}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFlags(UIEntryGrammar.FlagsContext ctx);
	/**
	 * Visit a parse tree produced by {@link UIEntryGrammar#desc}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDesc(UIEntryGrammar.DescContext ctx);
	/**
	 * Visit a parse tree produced by {@link UIEntryGrammar#label}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLabel(UIEntryGrammar.LabelContext ctx);
	/**
	 * Visit a parse tree produced by {@link UIEntryGrammar#label5}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLabel5(UIEntryGrammar.Label5Context ctx);
	/**
	 * Visit a parse tree produced by {@link UIEntryGrammar#label2}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLabel2(UIEntryGrammar.Label2Context ctx);
	/**
	 * Visit a parse tree produced by {@link UIEntryGrammar#template}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTemplate(UIEntryGrammar.TemplateContext ctx);
	/**
	 * Visit a parse tree produced by {@link UIEntryGrammar#uiEntry}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUiEntry(UIEntryGrammar.UiEntryContext ctx);
	/**
	 * Visit a parse tree produced by {@link UIEntryGrammar#file}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFile(UIEntryGrammar.FileContext ctx);
}