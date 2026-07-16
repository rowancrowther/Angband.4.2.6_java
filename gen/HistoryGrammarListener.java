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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/scratch/antlr_artifact_check/src/uk/co/jackoftrades/backend/parser/grammars/HistoryGrammar.g4 by ANTLR 4.13.2

    import uk.co.jackoftrades.backend.parser.history.HistoryParseRecord;

    import java.util.List;
    import java.util.ArrayList;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link HistoryGrammar}.
 */
public interface HistoryGrammarListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link HistoryGrammar#recordCount}.
	 * @param ctx the parse tree
	 */
	void enterRecordCount(HistoryGrammar.RecordCountContext ctx);
	/**
	 * Exit a parse tree produced by {@link HistoryGrammar#recordCount}.
	 * @param ctx the parse tree
	 */
	void exitRecordCount(HistoryGrammar.RecordCountContext ctx);
	/**
	 * Enter a parse tree produced by {@link HistoryGrammar#chart}.
	 * @param ctx the parse tree
	 */
	void enterChart(HistoryGrammar.ChartContext ctx);
	/**
	 * Exit a parse tree produced by {@link HistoryGrammar#chart}.
	 * @param ctx the parse tree
	 */
	void exitChart(HistoryGrammar.ChartContext ctx);
	/**
	 * Enter a parse tree produced by {@link HistoryGrammar#phrase}.
	 * @param ctx the parse tree
	 */
	void enterPhrase(HistoryGrammar.PhraseContext ctx);
	/**
	 * Exit a parse tree produced by {@link HistoryGrammar#phrase}.
	 * @param ctx the parse tree
	 */
	void exitPhrase(HistoryGrammar.PhraseContext ctx);
	/**
	 * Enter a parse tree produced by {@link HistoryGrammar#record}.
	 * @param ctx the parse tree
	 */
	void enterRecord(HistoryGrammar.RecordContext ctx);
	/**
	 * Exit a parse tree produced by {@link HistoryGrammar#record}.
	 * @param ctx the parse tree
	 */
	void exitRecord(HistoryGrammar.RecordContext ctx);
	/**
	 * Enter a parse tree produced by {@link HistoryGrammar#file}.
	 * @param ctx the parse tree
	 */
	void enterFile(HistoryGrammar.FileContext ctx);
	/**
	 * Exit a parse tree produced by {@link HistoryGrammar#file}.
	 * @param ctx the parse tree
	 */
	void exitFile(HistoryGrammar.FileContext ctx);
}