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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/scratch/pt_lexcheck/PlayerTimedGrammar.g4 by ANTLR 4.13.2

    import uk.co.jackoftrades.backend.parser.playertimed.PlayerTimedParseRecord;
    import uk.co.jackoftrades.backend.parser.playertimed.PlayerTimedParseRecord.PlayerTimedGradeParseRecord;

    import java.util.List;
    import java.util.ArrayList;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link PlayerTimedGrammar}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface PlayerTimedGrammarVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link PlayerTimedGrammar#recordCount}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRecordCount(PlayerTimedGrammar.RecordCountContext ctx);
	/**
	 * Visit a parse tree produced by {@link PlayerTimedGrammar#name}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitName(PlayerTimedGrammar.NameContext ctx);
	/**
	 * Visit a parse tree produced by {@link PlayerTimedGrammar#desc}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDesc(PlayerTimedGrammar.DescContext ctx);
	/**
	 * Visit a parse tree produced by {@link PlayerTimedGrammar#grade}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGrade(PlayerTimedGrammar.GradeContext ctx);
	/**
	 * Visit a parse tree produced by {@link PlayerTimedGrammar#onEnd}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOnEnd(PlayerTimedGrammar.OnEndContext ctx);
	/**
	 * Visit a parse tree produced by {@link PlayerTimedGrammar#onIncrease}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOnIncrease(PlayerTimedGrammar.OnIncreaseContext ctx);
	/**
	 * Visit a parse tree produced by {@link PlayerTimedGrammar#onDecrease}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOnDecrease(PlayerTimedGrammar.OnDecreaseContext ctx);
	/**
	 * Visit a parse tree produced by {@link PlayerTimedGrammar#msgt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMsgt(PlayerTimedGrammar.MsgtContext ctx);
	/**
	 * Visit a parse tree produced by {@link PlayerTimedGrammar#fail}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFail(PlayerTimedGrammar.FailContext ctx);
	/**
	 * Visit a parse tree produced by {@link PlayerTimedGrammar#onBeginEffect}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOnBeginEffect(PlayerTimedGrammar.OnBeginEffectContext ctx);
	/**
	 * Visit a parse tree produced by {@link PlayerTimedGrammar#onEndEffect}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOnEndEffect(PlayerTimedGrammar.OnEndEffectContext ctx);
	/**
	 * Visit a parse tree produced by {@link PlayerTimedGrammar#effectDice}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEffectDice(PlayerTimedGrammar.EffectDiceContext ctx);
	/**
	 * Visit a parse tree produced by {@link PlayerTimedGrammar#resist}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitResist(PlayerTimedGrammar.ResistContext ctx);
	/**
	 * Visit a parse tree produced by {@link PlayerTimedGrammar#brand}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBrand(PlayerTimedGrammar.BrandContext ctx);
	/**
	 * Visit a parse tree produced by {@link PlayerTimedGrammar#slay}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSlay(PlayerTimedGrammar.SlayContext ctx);
	/**
	 * Visit a parse tree produced by {@link PlayerTimedGrammar#flagSynonym}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFlagSynonym(PlayerTimedGrammar.FlagSynonymContext ctx);
	/**
	 * Visit a parse tree produced by {@link PlayerTimedGrammar#lowerBound}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLowerBound(PlayerTimedGrammar.LowerBoundContext ctx);
	/**
	 * Visit a parse tree produced by {@link PlayerTimedGrammar#flags}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFlags(PlayerTimedGrammar.FlagsContext ctx);
	/**
	 * Visit a parse tree produced by {@link PlayerTimedGrammar#playerTimed}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPlayerTimed(PlayerTimedGrammar.PlayerTimedContext ctx);
	/**
	 * Visit a parse tree produced by {@link PlayerTimedGrammar#file}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFile(PlayerTimedGrammar.FileContext ctx);
}