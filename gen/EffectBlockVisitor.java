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
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link EffectBlock}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface EffectBlockVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link EffectBlock#effect}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEffect(EffectBlock.EffectContext ctx);
	/**
	 * Visit a parse tree produced by {@link EffectBlock#time}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTime(EffectBlock.TimeContext ctx);
	/**
	 * Visit a parse tree produced by {@link EffectBlock#effectYX}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEffectYX(EffectBlock.EffectYXContext ctx);
	/**
	 * Visit a parse tree produced by {@link EffectBlock#dice}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDice(EffectBlock.DiceContext ctx);
	/**
	 * Visit a parse tree produced by {@link EffectBlock#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpr(EffectBlock.ExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link EffectBlock#effectMsg}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEffectMsg(EffectBlock.EffectMsgContext ctx);
	/**
	 * Visit a parse tree produced by {@link EffectBlock#effectBlock}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEffectBlock(EffectBlock.EffectBlockContext ctx);
}