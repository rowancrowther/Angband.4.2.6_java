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

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link SummonGrammar}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface SummonGrammarVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link SummonGrammar#recordCount}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRecordCount(SummonGrammar.RecordCountContext ctx);
	/**
	 * Visit a parse tree produced by {@link SummonGrammar#name}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitName(SummonGrammar.NameContext ctx);
	/**
	 * Visit a parse tree produced by {@link SummonGrammar#msgt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMsgt(SummonGrammar.MsgtContext ctx);
	/**
	 * Visit a parse tree produced by {@link SummonGrammar#uniques}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUniques(SummonGrammar.UniquesContext ctx);
	/**
	 * Visit a parse tree produced by {@link SummonGrammar#base}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBase(SummonGrammar.BaseContext ctx);
	/**
	 * Visit a parse tree produced by {@link SummonGrammar#raceFlag}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRaceFlag(SummonGrammar.RaceFlagContext ctx);
	/**
	 * Visit a parse tree produced by {@link SummonGrammar#fallback}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFallback(SummonGrammar.FallbackContext ctx);
	/**
	 * Visit a parse tree produced by {@link SummonGrammar#desc}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDesc(SummonGrammar.DescContext ctx);
	/**
	 * Visit a parse tree produced by {@link SummonGrammar#summon}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSummon(SummonGrammar.SummonContext ctx);
	/**
	 * Visit a parse tree produced by {@link SummonGrammar#file}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFile(SummonGrammar.FileContext ctx);
}