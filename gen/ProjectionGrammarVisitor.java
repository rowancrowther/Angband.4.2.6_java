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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/scratch/antlr_artifact_check/src/uk/co/jackoftrades/backend/parser/grammars/ProjectionGrammar.g4 by ANTLR 4.13.2

            import java.util.Arrays;
            import java.util.ArrayList;
            import java.util.List;
        
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link ProjectionGrammar}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface ProjectionGrammarVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link ProjectionGrammar#recordCount}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRecordCount(ProjectionGrammar.RecordCountContext ctx);
	/**
	 * Visit a parse tree produced by {@link ProjectionGrammar#code}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCode(ProjectionGrammar.CodeContext ctx);
	/**
	 * Visit a parse tree produced by {@link ProjectionGrammar#name}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitName(ProjectionGrammar.NameContext ctx);
	/**
	 * Visit a parse tree produced by {@link ProjectionGrammar#type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitType(ProjectionGrammar.TypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link ProjectionGrammar#desc}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDesc(ProjectionGrammar.DescContext ctx);
	/**
	 * Visit a parse tree produced by {@link ProjectionGrammar#playerDesc}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPlayerDesc(ProjectionGrammar.PlayerDescContext ctx);
	/**
	 * Visit a parse tree produced by {@link ProjectionGrammar#blindDesc}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBlindDesc(ProjectionGrammar.BlindDescContext ctx);
	/**
	 * Visit a parse tree produced by {@link ProjectionGrammar#lashDesc}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLashDesc(ProjectionGrammar.LashDescContext ctx);
	/**
	 * Visit a parse tree produced by {@link ProjectionGrammar#numerator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNumerator(ProjectionGrammar.NumeratorContext ctx);
	/**
	 * Visit a parse tree produced by {@link ProjectionGrammar#denominator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDenominator(ProjectionGrammar.DenominatorContext ctx);
	/**
	 * Visit a parse tree produced by {@link ProjectionGrammar#divisor}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDivisor(ProjectionGrammar.DivisorContext ctx);
	/**
	 * Visit a parse tree produced by {@link ProjectionGrammar#damageCap}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDamageCap(ProjectionGrammar.DamageCapContext ctx);
	/**
	 * Visit a parse tree produced by {@link ProjectionGrammar#msgt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMsgt(ProjectionGrammar.MsgtContext ctx);
	/**
	 * Visit a parse tree produced by {@link ProjectionGrammar#obvious}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitObvious(ProjectionGrammar.ObviousContext ctx);
	/**
	 * Visit a parse tree produced by {@link ProjectionGrammar#wake}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWake(ProjectionGrammar.WakeContext ctx);
	/**
	 * Visit a parse tree produced by {@link ProjectionGrammar#colour}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitColour(ProjectionGrammar.ColourContext ctx);
	/**
	 * Visit a parse tree produced by {@link ProjectionGrammar#projection}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProjection(ProjectionGrammar.ProjectionContext ctx);
	/**
	 * Visit a parse tree produced by {@link ProjectionGrammar#file}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFile(ProjectionGrammar.FileContext ctx);
}