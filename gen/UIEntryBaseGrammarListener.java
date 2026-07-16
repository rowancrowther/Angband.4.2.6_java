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

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link UIEntryBaseGrammar}.
 */
public interface UIEntryBaseGrammarListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link UIEntryBaseGrammar#recordCount}.
	 * @param ctx the parse tree
	 */
	void enterRecordCount(UIEntryBaseGrammar.RecordCountContext ctx);
	/**
	 * Exit a parse tree produced by {@link UIEntryBaseGrammar#recordCount}.
	 * @param ctx the parse tree
	 */
	void exitRecordCount(UIEntryBaseGrammar.RecordCountContext ctx);
	/**
	 * Enter a parse tree produced by {@link UIEntryBaseGrammar#name}.
	 * @param ctx the parse tree
	 */
	void enterName(UIEntryBaseGrammar.NameContext ctx);
	/**
	 * Exit a parse tree produced by {@link UIEntryBaseGrammar#name}.
	 * @param ctx the parse tree
	 */
	void exitName(UIEntryBaseGrammar.NameContext ctx);
	/**
	 * Enter a parse tree produced by {@link UIEntryBaseGrammar#renderer}.
	 * @param ctx the parse tree
	 */
	void enterRenderer(UIEntryBaseGrammar.RendererContext ctx);
	/**
	 * Exit a parse tree produced by {@link UIEntryBaseGrammar#renderer}.
	 * @param ctx the parse tree
	 */
	void exitRenderer(UIEntryBaseGrammar.RendererContext ctx);
	/**
	 * Enter a parse tree produced by {@link UIEntryBaseGrammar#combine}.
	 * @param ctx the parse tree
	 */
	void enterCombine(UIEntryBaseGrammar.CombineContext ctx);
	/**
	 * Exit a parse tree produced by {@link UIEntryBaseGrammar#combine}.
	 * @param ctx the parse tree
	 */
	void exitCombine(UIEntryBaseGrammar.CombineContext ctx);
	/**
	 * Enter a parse tree produced by {@link UIEntryBaseGrammar#category}.
	 * @param ctx the parse tree
	 */
	void enterCategory(UIEntryBaseGrammar.CategoryContext ctx);
	/**
	 * Exit a parse tree produced by {@link UIEntryBaseGrammar#category}.
	 * @param ctx the parse tree
	 */
	void exitCategory(UIEntryBaseGrammar.CategoryContext ctx);
	/**
	 * Enter a parse tree produced by {@link UIEntryBaseGrammar#flags}.
	 * @param ctx the parse tree
	 */
	void enterFlags(UIEntryBaseGrammar.FlagsContext ctx);
	/**
	 * Exit a parse tree produced by {@link UIEntryBaseGrammar#flags}.
	 * @param ctx the parse tree
	 */
	void exitFlags(UIEntryBaseGrammar.FlagsContext ctx);
	/**
	 * Enter a parse tree produced by {@link UIEntryBaseGrammar#desc}.
	 * @param ctx the parse tree
	 */
	void enterDesc(UIEntryBaseGrammar.DescContext ctx);
	/**
	 * Exit a parse tree produced by {@link UIEntryBaseGrammar#desc}.
	 * @param ctx the parse tree
	 */
	void exitDesc(UIEntryBaseGrammar.DescContext ctx);
	/**
	 * Enter a parse tree produced by {@link UIEntryBaseGrammar#entryBase}.
	 * @param ctx the parse tree
	 */
	void enterEntryBase(UIEntryBaseGrammar.EntryBaseContext ctx);
	/**
	 * Exit a parse tree produced by {@link UIEntryBaseGrammar#entryBase}.
	 * @param ctx the parse tree
	 */
	void exitEntryBase(UIEntryBaseGrammar.EntryBaseContext ctx);
	/**
	 * Enter a parse tree produced by {@link UIEntryBaseGrammar#file}.
	 * @param ctx the parse tree
	 */
	void enterFile(UIEntryBaseGrammar.FileContext ctx);
	/**
	 * Exit a parse tree produced by {@link UIEntryBaseGrammar#file}.
	 * @param ctx the parse tree
	 */
	void exitFile(UIEntryBaseGrammar.FileContext ctx);
}