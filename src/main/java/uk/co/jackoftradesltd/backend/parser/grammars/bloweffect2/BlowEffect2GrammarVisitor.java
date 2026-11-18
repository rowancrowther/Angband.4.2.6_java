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

// Generated from BlowEffect2Grammar.g4 by ANTLR 4.13.2
package uk.co.jackoftradesltd.backend.parser.grammars.bloweffect2;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link BlowEffect2Grammar}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface BlowEffect2GrammarVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link BlowEffect2Grammar#name}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitName(BlowEffect2Grammar.NameContext ctx);
	/**
	 * Visit a parse tree produced by {@link BlowEffect2Grammar#power}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPower(BlowEffect2Grammar.PowerContext ctx);
	/**
	 * Visit a parse tree produced by {@link BlowEffect2Grammar#eval}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEval(BlowEffect2Grammar.EvalContext ctx);
	/**
	 * Visit a parse tree produced by {@link BlowEffect2Grammar#desc}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDesc(BlowEffect2Grammar.DescContext ctx);
	/**
	 * Visit a parse tree produced by {@link BlowEffect2Grammar#loreColourBase}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLoreColourBase(BlowEffect2Grammar.LoreColourBaseContext ctx);
	/**
	 * Visit a parse tree produced by {@link BlowEffect2Grammar#loreColourResist}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLoreColourResist(BlowEffect2Grammar.LoreColourResistContext ctx);
	/**
	 * Visit a parse tree produced by {@link BlowEffect2Grammar#loreColourImmune}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLoreColourImmune(BlowEffect2Grammar.LoreColourImmuneContext ctx);
	/**
	 * Visit a parse tree produced by {@link BlowEffect2Grammar#effectType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEffectType(BlowEffect2Grammar.EffectTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link BlowEffect2Grammar#resist}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitResist(BlowEffect2Grammar.ResistContext ctx);
	/**
	 * Visit a parse tree produced by {@link BlowEffect2Grammar#lashType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLashType(BlowEffect2Grammar.LashTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link BlowEffect2Grammar#blowEffect}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBlowEffect(BlowEffect2Grammar.BlowEffectContext ctx);
	/**
	 * Visit a parse tree produced by {@link BlowEffect2Grammar#file}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFile(BlowEffect2Grammar.FileContext ctx);
}