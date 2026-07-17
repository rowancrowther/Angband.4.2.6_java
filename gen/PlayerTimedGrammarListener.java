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

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link PlayerTimedGrammar}.
 */
public interface PlayerTimedGrammarListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link PlayerTimedGrammar#recordCount}.
	 * @param ctx the parse tree
	 */
	void enterRecordCount(PlayerTimedGrammar.RecordCountContext ctx);
	/**
	 * Exit a parse tree produced by {@link PlayerTimedGrammar#recordCount}.
	 * @param ctx the parse tree
	 */
	void exitRecordCount(PlayerTimedGrammar.RecordCountContext ctx);
	/**
	 * Enter a parse tree produced by {@link PlayerTimedGrammar#name}.
	 * @param ctx the parse tree
	 */
	void enterName(PlayerTimedGrammar.NameContext ctx);
	/**
	 * Exit a parse tree produced by {@link PlayerTimedGrammar#name}.
	 * @param ctx the parse tree
	 */
	void exitName(PlayerTimedGrammar.NameContext ctx);
	/**
	 * Enter a parse tree produced by {@link PlayerTimedGrammar#desc}.
	 * @param ctx the parse tree
	 */
	void enterDesc(PlayerTimedGrammar.DescContext ctx);
	/**
	 * Exit a parse tree produced by {@link PlayerTimedGrammar#desc}.
	 * @param ctx the parse tree
	 */
	void exitDesc(PlayerTimedGrammar.DescContext ctx);
	/**
	 * Enter a parse tree produced by {@link PlayerTimedGrammar#grade}.
	 * @param ctx the parse tree
	 */
	void enterGrade(PlayerTimedGrammar.GradeContext ctx);
	/**
	 * Exit a parse tree produced by {@link PlayerTimedGrammar#grade}.
	 * @param ctx the parse tree
	 */
	void exitGrade(PlayerTimedGrammar.GradeContext ctx);
	/**
	 * Enter a parse tree produced by {@link PlayerTimedGrammar#onEnd}.
	 * @param ctx the parse tree
	 */
	void enterOnEnd(PlayerTimedGrammar.OnEndContext ctx);
	/**
	 * Exit a parse tree produced by {@link PlayerTimedGrammar#onEnd}.
	 * @param ctx the parse tree
	 */
	void exitOnEnd(PlayerTimedGrammar.OnEndContext ctx);
	/**
	 * Enter a parse tree produced by {@link PlayerTimedGrammar#onIncrease}.
	 * @param ctx the parse tree
	 */
	void enterOnIncrease(PlayerTimedGrammar.OnIncreaseContext ctx);
	/**
	 * Exit a parse tree produced by {@link PlayerTimedGrammar#onIncrease}.
	 * @param ctx the parse tree
	 */
	void exitOnIncrease(PlayerTimedGrammar.OnIncreaseContext ctx);
	/**
	 * Enter a parse tree produced by {@link PlayerTimedGrammar#onDecrease}.
	 * @param ctx the parse tree
	 */
	void enterOnDecrease(PlayerTimedGrammar.OnDecreaseContext ctx);
	/**
	 * Exit a parse tree produced by {@link PlayerTimedGrammar#onDecrease}.
	 * @param ctx the parse tree
	 */
	void exitOnDecrease(PlayerTimedGrammar.OnDecreaseContext ctx);
	/**
	 * Enter a parse tree produced by {@link PlayerTimedGrammar#msgt}.
	 * @param ctx the parse tree
	 */
	void enterMsgt(PlayerTimedGrammar.MsgtContext ctx);
	/**
	 * Exit a parse tree produced by {@link PlayerTimedGrammar#msgt}.
	 * @param ctx the parse tree
	 */
	void exitMsgt(PlayerTimedGrammar.MsgtContext ctx);
	/**
	 * Enter a parse tree produced by {@link PlayerTimedGrammar#fail}.
	 * @param ctx the parse tree
	 */
	void enterFail(PlayerTimedGrammar.FailContext ctx);
	/**
	 * Exit a parse tree produced by {@link PlayerTimedGrammar#fail}.
	 * @param ctx the parse tree
	 */
	void exitFail(PlayerTimedGrammar.FailContext ctx);
	/**
	 * Enter a parse tree produced by {@link PlayerTimedGrammar#onBeginEffect}.
	 * @param ctx the parse tree
	 */
	void enterOnBeginEffect(PlayerTimedGrammar.OnBeginEffectContext ctx);
	/**
	 * Exit a parse tree produced by {@link PlayerTimedGrammar#onBeginEffect}.
	 * @param ctx the parse tree
	 */
	void exitOnBeginEffect(PlayerTimedGrammar.OnBeginEffectContext ctx);
	/**
	 * Enter a parse tree produced by {@link PlayerTimedGrammar#onEndEffect}.
	 * @param ctx the parse tree
	 */
	void enterOnEndEffect(PlayerTimedGrammar.OnEndEffectContext ctx);
	/**
	 * Exit a parse tree produced by {@link PlayerTimedGrammar#onEndEffect}.
	 * @param ctx the parse tree
	 */
	void exitOnEndEffect(PlayerTimedGrammar.OnEndEffectContext ctx);
	/**
	 * Enter a parse tree produced by {@link PlayerTimedGrammar#effectDice}.
	 * @param ctx the parse tree
	 */
	void enterEffectDice(PlayerTimedGrammar.EffectDiceContext ctx);
	/**
	 * Exit a parse tree produced by {@link PlayerTimedGrammar#effectDice}.
	 * @param ctx the parse tree
	 */
	void exitEffectDice(PlayerTimedGrammar.EffectDiceContext ctx);
	/**
	 * Enter a parse tree produced by {@link PlayerTimedGrammar#resist}.
	 * @param ctx the parse tree
	 */
	void enterResist(PlayerTimedGrammar.ResistContext ctx);
	/**
	 * Exit a parse tree produced by {@link PlayerTimedGrammar#resist}.
	 * @param ctx the parse tree
	 */
	void exitResist(PlayerTimedGrammar.ResistContext ctx);
	/**
	 * Enter a parse tree produced by {@link PlayerTimedGrammar#brand}.
	 * @param ctx the parse tree
	 */
	void enterBrand(PlayerTimedGrammar.BrandContext ctx);
	/**
	 * Exit a parse tree produced by {@link PlayerTimedGrammar#brand}.
	 * @param ctx the parse tree
	 */
	void exitBrand(PlayerTimedGrammar.BrandContext ctx);
	/**
	 * Enter a parse tree produced by {@link PlayerTimedGrammar#slay}.
	 * @param ctx the parse tree
	 */
	void enterSlay(PlayerTimedGrammar.SlayContext ctx);
	/**
	 * Exit a parse tree produced by {@link PlayerTimedGrammar#slay}.
	 * @param ctx the parse tree
	 */
	void exitSlay(PlayerTimedGrammar.SlayContext ctx);
	/**
	 * Enter a parse tree produced by {@link PlayerTimedGrammar#flagSynonym}.
	 * @param ctx the parse tree
	 */
	void enterFlagSynonym(PlayerTimedGrammar.FlagSynonymContext ctx);
	/**
	 * Exit a parse tree produced by {@link PlayerTimedGrammar#flagSynonym}.
	 * @param ctx the parse tree
	 */
	void exitFlagSynonym(PlayerTimedGrammar.FlagSynonymContext ctx);
	/**
	 * Enter a parse tree produced by {@link PlayerTimedGrammar#lowerBound}.
	 * @param ctx the parse tree
	 */
	void enterLowerBound(PlayerTimedGrammar.LowerBoundContext ctx);
	/**
	 * Exit a parse tree produced by {@link PlayerTimedGrammar#lowerBound}.
	 * @param ctx the parse tree
	 */
	void exitLowerBound(PlayerTimedGrammar.LowerBoundContext ctx);
	/**
	 * Enter a parse tree produced by {@link PlayerTimedGrammar#flags}.
	 * @param ctx the parse tree
	 */
	void enterFlags(PlayerTimedGrammar.FlagsContext ctx);
	/**
	 * Exit a parse tree produced by {@link PlayerTimedGrammar#flags}.
	 * @param ctx the parse tree
	 */
	void exitFlags(PlayerTimedGrammar.FlagsContext ctx);
	/**
	 * Enter a parse tree produced by {@link PlayerTimedGrammar#playerTimed}.
	 * @param ctx the parse tree
	 */
	void enterPlayerTimed(PlayerTimedGrammar.PlayerTimedContext ctx);
	/**
	 * Exit a parse tree produced by {@link PlayerTimedGrammar#playerTimed}.
	 * @param ctx the parse tree
	 */
	void exitPlayerTimed(PlayerTimedGrammar.PlayerTimedContext ctx);
	/**
	 * Enter a parse tree produced by {@link PlayerTimedGrammar#file}.
	 * @param ctx the parse tree
	 */
	void enterFile(PlayerTimedGrammar.FileContext ctx);
	/**
	 * Exit a parse tree produced by {@link PlayerTimedGrammar#file}.
	 * @param ctx the parse tree
	 */
	void exitFile(PlayerTimedGrammar.FileContext ctx);
}