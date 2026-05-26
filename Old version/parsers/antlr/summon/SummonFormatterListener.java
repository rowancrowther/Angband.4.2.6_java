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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/io/parsers/antlr/grammars/SummonFormatter.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.io.parsers.antlr.summon;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link SummonFormatterParser}.
 */
public interface SummonFormatterListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link SummonFormatterParser#name}.
	 *
	 * @param ctx the parse tree
	 */
	void enterName(SummonFormatterParser.NameContext ctx);

	/**
	 * Exit a parse tree produced by {@link SummonFormatterParser#name}.
	 *
	 * @param ctx the parse tree
	 */
	void exitName(SummonFormatterParser.NameContext ctx);

	/**
	 * Enter a parse tree produced by {@link SummonFormatterParser#msgt}.
	 *
	 * @param ctx the parse tree
	 */
	void enterMsgt(SummonFormatterParser.MsgtContext ctx);

	/**
	 * Exit a parse tree produced by {@link SummonFormatterParser#msgt}.
	 *
	 * @param ctx the parse tree
	 */
	void exitMsgt(SummonFormatterParser.MsgtContext ctx);

	/**
	 * Enter a parse tree produced by {@link SummonFormatterParser#uniques}.
	 *
	 * @param ctx the parse tree
	 */
	void enterUniques(SummonFormatterParser.UniquesContext ctx);

	/**
	 * Exit a parse tree produced by {@link SummonFormatterParser#uniques}.
	 *
	 * @param ctx the parse tree
	 */
	void exitUniques(SummonFormatterParser.UniquesContext ctx);

	/**
	 * Enter a parse tree produced by {@link SummonFormatterParser#base}.
	 *
	 * @param ctx the parse tree
	 */
	void enterBase(SummonFormatterParser.BaseContext ctx);

	/**
	 * Exit a parse tree produced by {@link SummonFormatterParser#base}.
	 *
	 * @param ctx the parse tree
	 */
	void exitBase(SummonFormatterParser.BaseContext ctx);

	/**
	 * Enter a parse tree produced by {@link SummonFormatterParser#raceFlag}.
	 *
	 * @param ctx the parse tree
	 */
	void enterRaceFlag(SummonFormatterParser.RaceFlagContext ctx);

	/**
	 * Exit a parse tree produced by {@link SummonFormatterParser#raceFlag}.
	 *
	 * @param ctx the parse tree
	 */
	void exitRaceFlag(SummonFormatterParser.RaceFlagContext ctx);

	/**
	 * Enter a parse tree produced by {@link SummonFormatterParser#fallback}.
	 *
	 * @param ctx the parse tree
	 */
	void enterFallback(SummonFormatterParser.FallbackContext ctx);

	/**
	 * Exit a parse tree produced by {@link SummonFormatterParser#fallback}.
	 *
	 * @param ctx the parse tree
	 */
	void exitFallback(SummonFormatterParser.FallbackContext ctx);

	/**
	 * Enter a parse tree produced by {@link SummonFormatterParser#desc}.
	 *
	 * @param ctx the parse tree
	 */
	void enterDesc(SummonFormatterParser.DescContext ctx);

	/**
	 * Exit a parse tree produced by {@link SummonFormatterParser#desc}.
	 *
	 * @param ctx the parse tree
	 */
	void exitDesc(SummonFormatterParser.DescContext ctx);

	/**
	 * Enter a parse tree produced by {@link SummonFormatterParser#summon}.
	 *
	 * @param ctx the parse tree
	 */
	void enterSummon(SummonFormatterParser.SummonContext ctx);

	/**
	 * Exit a parse tree produced by {@link SummonFormatterParser#summon}.
	 *
	 * @param ctx the parse tree
	 */
	void exitSummon(SummonFormatterParser.SummonContext ctx);

	/**
	 * Enter a parse tree produced by {@link SummonFormatterParser#file}.
	 *
	 * @param ctx the parse tree
	 */
	void enterFile(SummonFormatterParser.FileContext ctx);

	/**
	 * Exit a parse tree produced by {@link SummonFormatterParser#file}.
	 *
	 * @param ctx the parse tree
	 */
	void exitFile(SummonFormatterParser.FileContext ctx);
}