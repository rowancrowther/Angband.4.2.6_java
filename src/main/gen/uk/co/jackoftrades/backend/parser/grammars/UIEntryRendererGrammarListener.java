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
        
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link UIEntryRendererGrammar}.
 */
public interface UIEntryRendererGrammarListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link UIEntryRendererGrammar#recordCount}.
	 * @param ctx the parse tree
	 */
	void enterRecordCount(UIEntryRendererGrammar.RecordCountContext ctx);
	/**
	 * Exit a parse tree produced by {@link UIEntryRendererGrammar#recordCount}.
	 * @param ctx the parse tree
	 */
	void exitRecordCount(UIEntryRendererGrammar.RecordCountContext ctx);
	/**
	 * Enter a parse tree produced by {@link UIEntryRendererGrammar#name}.
	 * @param ctx the parse tree
	 */
	void enterName(UIEntryRendererGrammar.NameContext ctx);
	/**
	 * Exit a parse tree produced by {@link UIEntryRendererGrammar#name}.
	 * @param ctx the parse tree
	 */
	void exitName(UIEntryRendererGrammar.NameContext ctx);
	/**
	 * Enter a parse tree produced by {@link UIEntryRendererGrammar#code}.
	 * @param ctx the parse tree
	 */
	void enterCode(UIEntryRendererGrammar.CodeContext ctx);
	/**
	 * Exit a parse tree produced by {@link UIEntryRendererGrammar#code}.
	 * @param ctx the parse tree
	 */
	void exitCode(UIEntryRendererGrammar.CodeContext ctx);
	/**
	 * Enter a parse tree produced by {@link UIEntryRendererGrammar#colours}.
	 * @param ctx the parse tree
	 */
	void enterColours(UIEntryRendererGrammar.ColoursContext ctx);
	/**
	 * Exit a parse tree produced by {@link UIEntryRendererGrammar#colours}.
	 * @param ctx the parse tree
	 */
	void exitColours(UIEntryRendererGrammar.ColoursContext ctx);
	/**
	 * Enter a parse tree produced by {@link UIEntryRendererGrammar#labelcolours}.
	 * @param ctx the parse tree
	 */
	void enterLabelcolours(UIEntryRendererGrammar.LabelcoloursContext ctx);
	/**
	 * Exit a parse tree produced by {@link UIEntryRendererGrammar#labelcolours}.
	 * @param ctx the parse tree
	 */
	void exitLabelcolours(UIEntryRendererGrammar.LabelcoloursContext ctx);
	/**
	 * Enter a parse tree produced by {@link UIEntryRendererGrammar#symbols}.
	 * @param ctx the parse tree
	 */
	void enterSymbols(UIEntryRendererGrammar.SymbolsContext ctx);
	/**
	 * Exit a parse tree produced by {@link UIEntryRendererGrammar#symbols}.
	 * @param ctx the parse tree
	 */
	void exitSymbols(UIEntryRendererGrammar.SymbolsContext ctx);
	/**
	 * Enter a parse tree produced by {@link UIEntryRendererGrammar#ndigit}.
	 * @param ctx the parse tree
	 */
	void enterNdigit(UIEntryRendererGrammar.NdigitContext ctx);
	/**
	 * Exit a parse tree produced by {@link UIEntryRendererGrammar#ndigit}.
	 * @param ctx the parse tree
	 */
	void exitNdigit(UIEntryRendererGrammar.NdigitContext ctx);
	/**
	 * Enter a parse tree produced by {@link UIEntryRendererGrammar#sign}.
	 * @param ctx the parse tree
	 */
	void enterSign(UIEntryRendererGrammar.SignContext ctx);
	/**
	 * Exit a parse tree produced by {@link UIEntryRendererGrammar#sign}.
	 * @param ctx the parse tree
	 */
	void exitSign(UIEntryRendererGrammar.SignContext ctx);
	/**
	 * Enter a parse tree produced by {@link UIEntryRendererGrammar#uiEntry}.
	 * @param ctx the parse tree
	 */
	void enterUiEntry(UIEntryRendererGrammar.UiEntryContext ctx);
	/**
	 * Exit a parse tree produced by {@link UIEntryRendererGrammar#uiEntry}.
	 * @param ctx the parse tree
	 */
	void exitUiEntry(UIEntryRendererGrammar.UiEntryContext ctx);
	/**
	 * Enter a parse tree produced by {@link UIEntryRendererGrammar#file}.
	 * @param ctx the parse tree
	 */
	void enterFile(UIEntryRendererGrammar.FileContext ctx);
	/**
	 * Exit a parse tree produced by {@link UIEntryRendererGrammar#file}.
	 * @param ctx the parse tree
	 */
	void exitFile(UIEntryRendererGrammar.FileContext ctx);
}