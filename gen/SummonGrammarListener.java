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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/scratch/antlr_artifact_check/src/uk/co/jackoftrades/backend/parser/grammars/SummonGrammar.g4 by ANTLR 4.13.2

    import uk.co.jackoftrades.backend.parser.summon.SummonParseRecord;

    import java.util.List;
    import java.util.ArrayList;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link SummonGrammar}.
 */
public interface SummonGrammarListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link SummonGrammar#recordCount}.
	 * @param ctx the parse tree
	 */
	void enterRecordCount(SummonGrammar.RecordCountContext ctx);
	/**
	 * Exit a parse tree produced by {@link SummonGrammar#recordCount}.
	 * @param ctx the parse tree
	 */
	void exitRecordCount(SummonGrammar.RecordCountContext ctx);
	/**
	 * Enter a parse tree produced by {@link SummonGrammar#name}.
	 * @param ctx the parse tree
	 */
	void enterName(SummonGrammar.NameContext ctx);
	/**
	 * Exit a parse tree produced by {@link SummonGrammar#name}.
	 * @param ctx the parse tree
	 */
	void exitName(SummonGrammar.NameContext ctx);
	/**
	 * Enter a parse tree produced by {@link SummonGrammar#msgt}.
	 * @param ctx the parse tree
	 */
	void enterMsgt(SummonGrammar.MsgtContext ctx);
	/**
	 * Exit a parse tree produced by {@link SummonGrammar#msgt}.
	 * @param ctx the parse tree
	 */
	void exitMsgt(SummonGrammar.MsgtContext ctx);
	/**
	 * Enter a parse tree produced by {@link SummonGrammar#uniques}.
	 * @param ctx the parse tree
	 */
	void enterUniques(SummonGrammar.UniquesContext ctx);
	/**
	 * Exit a parse tree produced by {@link SummonGrammar#uniques}.
	 * @param ctx the parse tree
	 */
	void exitUniques(SummonGrammar.UniquesContext ctx);
	/**
	 * Enter a parse tree produced by {@link SummonGrammar#base}.
	 * @param ctx the parse tree
	 */
	void enterBase(SummonGrammar.BaseContext ctx);
	/**
	 * Exit a parse tree produced by {@link SummonGrammar#base}.
	 * @param ctx the parse tree
	 */
	void exitBase(SummonGrammar.BaseContext ctx);
	/**
	 * Enter a parse tree produced by {@link SummonGrammar#raceFlag}.
	 * @param ctx the parse tree
	 */
	void enterRaceFlag(SummonGrammar.RaceFlagContext ctx);
	/**
	 * Exit a parse tree produced by {@link SummonGrammar#raceFlag}.
	 * @param ctx the parse tree
	 */
	void exitRaceFlag(SummonGrammar.RaceFlagContext ctx);
	/**
	 * Enter a parse tree produced by {@link SummonGrammar#fallback}.
	 * @param ctx the parse tree
	 */
	void enterFallback(SummonGrammar.FallbackContext ctx);
	/**
	 * Exit a parse tree produced by {@link SummonGrammar#fallback}.
	 * @param ctx the parse tree
	 */
	void exitFallback(SummonGrammar.FallbackContext ctx);
	/**
	 * Enter a parse tree produced by {@link SummonGrammar#desc}.
	 * @param ctx the parse tree
	 */
	void enterDesc(SummonGrammar.DescContext ctx);
	/**
	 * Exit a parse tree produced by {@link SummonGrammar#desc}.
	 * @param ctx the parse tree
	 */
	void exitDesc(SummonGrammar.DescContext ctx);
	/**
	 * Enter a parse tree produced by {@link SummonGrammar#summon}.
	 * @param ctx the parse tree
	 */
	void enterSummon(SummonGrammar.SummonContext ctx);
	/**
	 * Exit a parse tree produced by {@link SummonGrammar#summon}.
	 * @param ctx the parse tree
	 */
	void exitSummon(SummonGrammar.SummonContext ctx);
	/**
	 * Enter a parse tree produced by {@link SummonGrammar#file}.
	 * @param ctx the parse tree
	 */
	void enterFile(SummonGrammar.FileContext ctx);
	/**
	 * Exit a parse tree produced by {@link SummonGrammar#file}.
	 * @param ctx the parse tree
	 */
	void exitFile(SummonGrammar.FileContext ctx);
}