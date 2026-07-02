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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/parser/grammars/UIEntryRendererGrammar.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.grammars;

            import java.util.Arrays;
            import java.util.ArrayList;
            import java.util.List;
        
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link UIEntryRendererGrammar}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface UIEntryRendererGrammarVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link UIEntryRendererGrammar#recordCount}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRecordCount(UIEntryRendererGrammar.RecordCountContext ctx);
	/**
	 * Visit a parse tree produced by {@link UIEntryRendererGrammar#name}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitName(UIEntryRendererGrammar.NameContext ctx);
	/**
	 * Visit a parse tree produced by {@link UIEntryRendererGrammar#code}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCode(UIEntryRendererGrammar.CodeContext ctx);
	/**
	 * Visit a parse tree produced by {@link UIEntryRendererGrammar#colours}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitColours(UIEntryRendererGrammar.ColoursContext ctx);
	/**
	 * Visit a parse tree produced by {@link UIEntryRendererGrammar#labelcolours}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLabelcolours(UIEntryRendererGrammar.LabelcoloursContext ctx);
	/**
	 * Visit a parse tree produced by {@link UIEntryRendererGrammar#symbols}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSymbols(UIEntryRendererGrammar.SymbolsContext ctx);
	/**
	 * Visit a parse tree produced by {@link UIEntryRendererGrammar#ndigit}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNdigit(UIEntryRendererGrammar.NdigitContext ctx);
	/**
	 * Visit a parse tree produced by {@link UIEntryRendererGrammar#sign}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSign(UIEntryRendererGrammar.SignContext ctx);
	/**
	 * Visit a parse tree produced by {@link UIEntryRendererGrammar#uiEntry}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUiEntry(UIEntryRendererGrammar.UiEntryContext ctx);
	/**
	 * Visit a parse tree produced by {@link UIEntryRendererGrammar#file}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFile(UIEntryRendererGrammar.FileContext ctx);
}