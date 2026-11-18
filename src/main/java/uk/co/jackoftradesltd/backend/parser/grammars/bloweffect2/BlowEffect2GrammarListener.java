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

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link BlowEffect2Grammar}.
 */
public interface BlowEffect2GrammarListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link BlowEffect2Grammar#name}.
	 * @param ctx the parse tree
	 */
	void enterName(BlowEffect2Grammar.NameContext ctx);
	/**
	 * Exit a parse tree produced by {@link BlowEffect2Grammar#name}.
	 * @param ctx the parse tree
	 */
	void exitName(BlowEffect2Grammar.NameContext ctx);
	/**
	 * Enter a parse tree produced by {@link BlowEffect2Grammar#power}.
	 * @param ctx the parse tree
	 */
	void enterPower(BlowEffect2Grammar.PowerContext ctx);
	/**
	 * Exit a parse tree produced by {@link BlowEffect2Grammar#power}.
	 * @param ctx the parse tree
	 */
	void exitPower(BlowEffect2Grammar.PowerContext ctx);
	/**
	 * Enter a parse tree produced by {@link BlowEffect2Grammar#eval}.
	 * @param ctx the parse tree
	 */
	void enterEval(BlowEffect2Grammar.EvalContext ctx);
	/**
	 * Exit a parse tree produced by {@link BlowEffect2Grammar#eval}.
	 * @param ctx the parse tree
	 */
	void exitEval(BlowEffect2Grammar.EvalContext ctx);
	/**
	 * Enter a parse tree produced by {@link BlowEffect2Grammar#desc}.
	 * @param ctx the parse tree
	 */
	void enterDesc(BlowEffect2Grammar.DescContext ctx);
	/**
	 * Exit a parse tree produced by {@link BlowEffect2Grammar#desc}.
	 * @param ctx the parse tree
	 */
	void exitDesc(BlowEffect2Grammar.DescContext ctx);
	/**
	 * Enter a parse tree produced by {@link BlowEffect2Grammar#loreColourBase}.
	 * @param ctx the parse tree
	 */
	void enterLoreColourBase(BlowEffect2Grammar.LoreColourBaseContext ctx);
	/**
	 * Exit a parse tree produced by {@link BlowEffect2Grammar#loreColourBase}.
	 * @param ctx the parse tree
	 */
	void exitLoreColourBase(BlowEffect2Grammar.LoreColourBaseContext ctx);
	/**
	 * Enter a parse tree produced by {@link BlowEffect2Grammar#loreColourResist}.
	 * @param ctx the parse tree
	 */
	void enterLoreColourResist(BlowEffect2Grammar.LoreColourResistContext ctx);
	/**
	 * Exit a parse tree produced by {@link BlowEffect2Grammar#loreColourResist}.
	 * @param ctx the parse tree
	 */
	void exitLoreColourResist(BlowEffect2Grammar.LoreColourResistContext ctx);
	/**
	 * Enter a parse tree produced by {@link BlowEffect2Grammar#loreColourImmune}.
	 * @param ctx the parse tree
	 */
	void enterLoreColourImmune(BlowEffect2Grammar.LoreColourImmuneContext ctx);
	/**
	 * Exit a parse tree produced by {@link BlowEffect2Grammar#loreColourImmune}.
	 * @param ctx the parse tree
	 */
	void exitLoreColourImmune(BlowEffect2Grammar.LoreColourImmuneContext ctx);
	/**
	 * Enter a parse tree produced by {@link BlowEffect2Grammar#effectType}.
	 * @param ctx the parse tree
	 */
	void enterEffectType(BlowEffect2Grammar.EffectTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link BlowEffect2Grammar#effectType}.
	 * @param ctx the parse tree
	 */
	void exitEffectType(BlowEffect2Grammar.EffectTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link BlowEffect2Grammar#resist}.
	 * @param ctx the parse tree
	 */
	void enterResist(BlowEffect2Grammar.ResistContext ctx);
	/**
	 * Exit a parse tree produced by {@link BlowEffect2Grammar#resist}.
	 * @param ctx the parse tree
	 */
	void exitResist(BlowEffect2Grammar.ResistContext ctx);
	/**
	 * Enter a parse tree produced by {@link BlowEffect2Grammar#lashType}.
	 * @param ctx the parse tree
	 */
	void enterLashType(BlowEffect2Grammar.LashTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link BlowEffect2Grammar#lashType}.
	 * @param ctx the parse tree
	 */
	void exitLashType(BlowEffect2Grammar.LashTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link BlowEffect2Grammar#blowEffect}.
	 * @param ctx the parse tree
	 */
	void enterBlowEffect(BlowEffect2Grammar.BlowEffectContext ctx);
	/**
	 * Exit a parse tree produced by {@link BlowEffect2Grammar#blowEffect}.
	 * @param ctx the parse tree
	 */
	void exitBlowEffect(BlowEffect2Grammar.BlowEffectContext ctx);
	/**
	 * Enter a parse tree produced by {@link BlowEffect2Grammar#file}.
	 * @param ctx the parse tree
	 */
	void enterFile(BlowEffect2Grammar.FileContext ctx);
	/**
	 * Exit a parse tree produced by {@link BlowEffect2Grammar#file}.
	 * @param ctx the parse tree
	 */
	void exitFile(BlowEffect2Grammar.FileContext ctx);
}