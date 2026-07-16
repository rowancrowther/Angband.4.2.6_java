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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/scratch/antlr_artifact_check/src/uk/co/jackoftrades/backend/parser/grammars/imports/EffectBlock.g4 by ANTLR 4.13.2
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link EffectBlock}.
 */
public interface EffectBlockListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link EffectBlock#effect}.
	 * @param ctx the parse tree
	 */
	void enterEffect(EffectBlock.EffectContext ctx);
	/**
	 * Exit a parse tree produced by {@link EffectBlock#effect}.
	 * @param ctx the parse tree
	 */
	void exitEffect(EffectBlock.EffectContext ctx);
	/**
	 * Enter a parse tree produced by {@link EffectBlock#time}.
	 * @param ctx the parse tree
	 */
	void enterTime(EffectBlock.TimeContext ctx);
	/**
	 * Exit a parse tree produced by {@link EffectBlock#time}.
	 * @param ctx the parse tree
	 */
	void exitTime(EffectBlock.TimeContext ctx);
	/**
	 * Enter a parse tree produced by {@link EffectBlock#effectYX}.
	 * @param ctx the parse tree
	 */
	void enterEffectYX(EffectBlock.EffectYXContext ctx);
	/**
	 * Exit a parse tree produced by {@link EffectBlock#effectYX}.
	 * @param ctx the parse tree
	 */
	void exitEffectYX(EffectBlock.EffectYXContext ctx);
	/**
	 * Enter a parse tree produced by {@link EffectBlock#dice}.
	 * @param ctx the parse tree
	 */
	void enterDice(EffectBlock.DiceContext ctx);
	/**
	 * Exit a parse tree produced by {@link EffectBlock#dice}.
	 * @param ctx the parse tree
	 */
	void exitDice(EffectBlock.DiceContext ctx);
	/**
	 * Enter a parse tree produced by {@link EffectBlock#expr}.
	 * @param ctx the parse tree
	 */
	void enterExpr(EffectBlock.ExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link EffectBlock#expr}.
	 * @param ctx the parse tree
	 */
	void exitExpr(EffectBlock.ExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link EffectBlock#effectMsg}.
	 * @param ctx the parse tree
	 */
	void enterEffectMsg(EffectBlock.EffectMsgContext ctx);
	/**
	 * Exit a parse tree produced by {@link EffectBlock#effectMsg}.
	 * @param ctx the parse tree
	 */
	void exitEffectMsg(EffectBlock.EffectMsgContext ctx);
	/**
	 * Enter a parse tree produced by {@link EffectBlock#effectBlock}.
	 * @param ctx the parse tree
	 */
	void enterEffectBlock(EffectBlock.EffectBlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link EffectBlock#effectBlock}.
	 * @param ctx the parse tree
	 */
	void exitEffectBlock(EffectBlock.EffectBlockContext ctx);
}