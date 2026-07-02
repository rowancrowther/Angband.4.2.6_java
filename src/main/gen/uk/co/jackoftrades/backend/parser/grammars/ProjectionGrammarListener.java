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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/parser/grammars/ProjectionGrammar.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.grammars;

            import java.util.Arrays;
            import java.util.ArrayList;
            import java.util.List;
        
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link ProjectionGrammar}.
 */
public interface ProjectionGrammarListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link ProjectionGrammar#recordCount}.
	 * @param ctx the parse tree
	 */
	void enterRecordCount(ProjectionGrammar.RecordCountContext ctx);
	/**
	 * Exit a parse tree produced by {@link ProjectionGrammar#recordCount}.
	 * @param ctx the parse tree
	 */
	void exitRecordCount(ProjectionGrammar.RecordCountContext ctx);
	/**
	 * Enter a parse tree produced by {@link ProjectionGrammar#code}.
	 * @param ctx the parse tree
	 */
	void enterCode(ProjectionGrammar.CodeContext ctx);
	/**
	 * Exit a parse tree produced by {@link ProjectionGrammar#code}.
	 * @param ctx the parse tree
	 */
	void exitCode(ProjectionGrammar.CodeContext ctx);
	/**
	 * Enter a parse tree produced by {@link ProjectionGrammar#name}.
	 * @param ctx the parse tree
	 */
	void enterName(ProjectionGrammar.NameContext ctx);
	/**
	 * Exit a parse tree produced by {@link ProjectionGrammar#name}.
	 * @param ctx the parse tree
	 */
	void exitName(ProjectionGrammar.NameContext ctx);
	/**
	 * Enter a parse tree produced by {@link ProjectionGrammar#type}.
	 * @param ctx the parse tree
	 */
	void enterType(ProjectionGrammar.TypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link ProjectionGrammar#type}.
	 * @param ctx the parse tree
	 */
	void exitType(ProjectionGrammar.TypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link ProjectionGrammar#desc}.
	 * @param ctx the parse tree
	 */
	void enterDesc(ProjectionGrammar.DescContext ctx);
	/**
	 * Exit a parse tree produced by {@link ProjectionGrammar#desc}.
	 * @param ctx the parse tree
	 */
	void exitDesc(ProjectionGrammar.DescContext ctx);
	/**
	 * Enter a parse tree produced by {@link ProjectionGrammar#playerDesc}.
	 * @param ctx the parse tree
	 */
	void enterPlayerDesc(ProjectionGrammar.PlayerDescContext ctx);
	/**
	 * Exit a parse tree produced by {@link ProjectionGrammar#playerDesc}.
	 * @param ctx the parse tree
	 */
	void exitPlayerDesc(ProjectionGrammar.PlayerDescContext ctx);
	/**
	 * Enter a parse tree produced by {@link ProjectionGrammar#blindDesc}.
	 * @param ctx the parse tree
	 */
	void enterBlindDesc(ProjectionGrammar.BlindDescContext ctx);
	/**
	 * Exit a parse tree produced by {@link ProjectionGrammar#blindDesc}.
	 * @param ctx the parse tree
	 */
	void exitBlindDesc(ProjectionGrammar.BlindDescContext ctx);
	/**
	 * Enter a parse tree produced by {@link ProjectionGrammar#lashDesc}.
	 * @param ctx the parse tree
	 */
	void enterLashDesc(ProjectionGrammar.LashDescContext ctx);
	/**
	 * Exit a parse tree produced by {@link ProjectionGrammar#lashDesc}.
	 * @param ctx the parse tree
	 */
	void exitLashDesc(ProjectionGrammar.LashDescContext ctx);
	/**
	 * Enter a parse tree produced by {@link ProjectionGrammar#numerator}.
	 * @param ctx the parse tree
	 */
	void enterNumerator(ProjectionGrammar.NumeratorContext ctx);
	/**
	 * Exit a parse tree produced by {@link ProjectionGrammar#numerator}.
	 * @param ctx the parse tree
	 */
	void exitNumerator(ProjectionGrammar.NumeratorContext ctx);
	/**
	 * Enter a parse tree produced by {@link ProjectionGrammar#denominator}.
	 * @param ctx the parse tree
	 */
	void enterDenominator(ProjectionGrammar.DenominatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link ProjectionGrammar#denominator}.
	 * @param ctx the parse tree
	 */
	void exitDenominator(ProjectionGrammar.DenominatorContext ctx);
	/**
	 * Enter a parse tree produced by {@link ProjectionGrammar#divisor}.
	 * @param ctx the parse tree
	 */
	void enterDivisor(ProjectionGrammar.DivisorContext ctx);
	/**
	 * Exit a parse tree produced by {@link ProjectionGrammar#divisor}.
	 * @param ctx the parse tree
	 */
	void exitDivisor(ProjectionGrammar.DivisorContext ctx);
	/**
	 * Enter a parse tree produced by {@link ProjectionGrammar#damageCap}.
	 * @param ctx the parse tree
	 */
	void enterDamageCap(ProjectionGrammar.DamageCapContext ctx);
	/**
	 * Exit a parse tree produced by {@link ProjectionGrammar#damageCap}.
	 * @param ctx the parse tree
	 */
	void exitDamageCap(ProjectionGrammar.DamageCapContext ctx);
	/**
	 * Enter a parse tree produced by {@link ProjectionGrammar#msgt}.
	 * @param ctx the parse tree
	 */
	void enterMsgt(ProjectionGrammar.MsgtContext ctx);
	/**
	 * Exit a parse tree produced by {@link ProjectionGrammar#msgt}.
	 * @param ctx the parse tree
	 */
	void exitMsgt(ProjectionGrammar.MsgtContext ctx);
	/**
	 * Enter a parse tree produced by {@link ProjectionGrammar#obvious}.
	 * @param ctx the parse tree
	 */
	void enterObvious(ProjectionGrammar.ObviousContext ctx);
	/**
	 * Exit a parse tree produced by {@link ProjectionGrammar#obvious}.
	 * @param ctx the parse tree
	 */
	void exitObvious(ProjectionGrammar.ObviousContext ctx);
	/**
	 * Enter a parse tree produced by {@link ProjectionGrammar#wake}.
	 * @param ctx the parse tree
	 */
	void enterWake(ProjectionGrammar.WakeContext ctx);
	/**
	 * Exit a parse tree produced by {@link ProjectionGrammar#wake}.
	 * @param ctx the parse tree
	 */
	void exitWake(ProjectionGrammar.WakeContext ctx);
	/**
	 * Enter a parse tree produced by {@link ProjectionGrammar#colour}.
	 * @param ctx the parse tree
	 */
	void enterColour(ProjectionGrammar.ColourContext ctx);
	/**
	 * Exit a parse tree produced by {@link ProjectionGrammar#colour}.
	 * @param ctx the parse tree
	 */
	void exitColour(ProjectionGrammar.ColourContext ctx);
	/**
	 * Enter a parse tree produced by {@link ProjectionGrammar#projection}.
	 * @param ctx the parse tree
	 */
	void enterProjection(ProjectionGrammar.ProjectionContext ctx);
	/**
	 * Exit a parse tree produced by {@link ProjectionGrammar#projection}.
	 * @param ctx the parse tree
	 */
	void exitProjection(ProjectionGrammar.ProjectionContext ctx);
	/**
	 * Enter a parse tree produced by {@link ProjectionGrammar#file}.
	 * @param ctx the parse tree
	 */
	void enterFile(ProjectionGrammar.FileContext ctx);
	/**
	 * Exit a parse tree produced by {@link ProjectionGrammar#file}.
	 * @param ctx the parse tree
	 */
	void exitFile(ProjectionGrammar.FileContext ctx);
}