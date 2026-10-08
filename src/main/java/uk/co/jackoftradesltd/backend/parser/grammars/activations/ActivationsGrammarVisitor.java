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

// Generated from ActivationsGrammar.g4 by ANTLR 4.13.2
package uk.co.jackoftradesltd.backend.parser.grammars.activations;

    import uk.co.jackoftradesltd.backend.parser.activation.ActivationParseRecord;
    import uk.co.jackoftradesltd.backend.parser.grammars.EffectParseRecord;

    import java.util.List;
    import java.util.ArrayList;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link ActivationsGrammar}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface ActivationsGrammarVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link ActivationsGrammar#recordCount}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRecordCount(ActivationsGrammar.RecordCountContext ctx);
	/**
	 * Visit a parse tree produced by {@link ActivationsGrammar#name}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitName(ActivationsGrammar.NameContext ctx);
	/**
	 * Visit a parse tree produced by {@link ActivationsGrammar#aim}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAim(ActivationsGrammar.AimContext ctx);
	/**
	 * Visit a parse tree produced by {@link ActivationsGrammar#level}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLevel(ActivationsGrammar.LevelContext ctx);
	/**
	 * Visit a parse tree produced by {@link ActivationsGrammar#power}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPower(ActivationsGrammar.PowerContext ctx);
	/**
	 * Visit a parse tree produced by {@link ActivationsGrammar#desc}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDesc(ActivationsGrammar.DescContext ctx);
	/**
	 * Visit a parse tree produced by {@link ActivationsGrammar#msg}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMsg(ActivationsGrammar.MsgContext ctx);
	/**
	 * Visit a parse tree produced by {@link ActivationsGrammar#activation}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitActivation(ActivationsGrammar.ActivationContext ctx);
	/**
	 * Visit a parse tree produced by {@link ActivationsGrammar#file}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFile(ActivationsGrammar.FileContext ctx);
	/**
	 * Visit a parse tree produced by {@link ActivationsGrammar#effect}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEffect(ActivationsGrammar.EffectContext ctx);
	/**
	 * Visit a parse tree produced by {@link ActivationsGrammar#effectYX}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEffectYX(ActivationsGrammar.EffectYXContext ctx);
	/**
	 * Visit a parse tree produced by {@link ActivationsGrammar#dice}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDice(ActivationsGrammar.DiceContext ctx);
	/**
	 * Visit a parse tree produced by {@link ActivationsGrammar#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpr(ActivationsGrammar.ExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link ActivationsGrammar#effectMsg}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEffectMsg(ActivationsGrammar.EffectMsgContext ctx);
	/**
	 * Visit a parse tree produced by {@link ActivationsGrammar#time}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTime(ActivationsGrammar.TimeContext ctx);
	/**
	 * Visit a parse tree produced by {@link ActivationsGrammar#effectBlock}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEffectBlock(ActivationsGrammar.EffectBlockContext ctx);
}