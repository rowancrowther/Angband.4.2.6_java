// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/io/parsers/antlr/grammars/SummonFormatter.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.io.parsers.antlr.summon;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link SummonFormatterParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface SummonFormatterVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link SummonFormatterParser#name}.
	 *
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitName(SummonFormatterParser.NameContext ctx);

	/**
	 * Visit a parse tree produced by {@link SummonFormatterParser#msgt}.
	 *
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMsgt(SummonFormatterParser.MsgtContext ctx);

	/**
	 * Visit a parse tree produced by {@link SummonFormatterParser#uniques}.
	 *
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUniques(SummonFormatterParser.UniquesContext ctx);

	/**
	 * Visit a parse tree produced by {@link SummonFormatterParser#base}.
	 *
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBase(SummonFormatterParser.BaseContext ctx);

	/**
	 * Visit a parse tree produced by {@link SummonFormatterParser#raceFlag}.
	 *
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRaceFlag(SummonFormatterParser.RaceFlagContext ctx);

	/**
	 * Visit a parse tree produced by {@link SummonFormatterParser#fallback}.
	 *
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFallback(SummonFormatterParser.FallbackContext ctx);

	/**
	 * Visit a parse tree produced by {@link SummonFormatterParser#desc}.
	 *
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDesc(SummonFormatterParser.DescContext ctx);

	/**
	 * Visit a parse tree produced by {@link SummonFormatterParser#summon}.
	 *
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSummon(SummonFormatterParser.SummonContext ctx);

	/**
	 * Visit a parse tree produced by {@link SummonFormatterParser#file}.
	 *
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFile(SummonFormatterParser.FileContext ctx);
}