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

// Generated from PitGrammar.g4 by ANTLR 4.13.2
package uk.co.jackoftradesltd.backend.parser.grammars.pit;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link PitGrammar}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface PitGrammarVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link PitGrammar#name}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitName(PitGrammar.NameContext ctx);
	/**
	 * Visit a parse tree produced by {@link PitGrammar#room}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRoom(PitGrammar.RoomContext ctx);
	/**
	 * Visit a parse tree produced by {@link PitGrammar#alloc}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAlloc(PitGrammar.AllocContext ctx);
	/**
	 * Visit a parse tree produced by {@link PitGrammar#objRarity}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitObjRarity(PitGrammar.ObjRarityContext ctx);
	/**
	 * Visit a parse tree produced by {@link PitGrammar#colour}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitColour(PitGrammar.ColourContext ctx);
	/**
	 * Visit a parse tree produced by {@link PitGrammar#monBase}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMonBase(PitGrammar.MonBaseContext ctx);
	/**
	 * Visit a parse tree produced by {@link PitGrammar#flagsReq}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFlagsReq(PitGrammar.FlagsReqContext ctx);
	/**
	 * Visit a parse tree produced by {@link PitGrammar#flagsBan}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFlagsBan(PitGrammar.FlagsBanContext ctx);
	/**
	 * Visit a parse tree produced by {@link PitGrammar#innateFreq}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInnateFreq(PitGrammar.InnateFreqContext ctx);
	/**
	 * Visit a parse tree produced by {@link PitGrammar#spellReq}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSpellReq(PitGrammar.SpellReqContext ctx);
	/**
	 * Visit a parse tree produced by {@link PitGrammar#spellBan}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSpellBan(PitGrammar.SpellBanContext ctx);
	/**
	 * Visit a parse tree produced by {@link PitGrammar#monBan}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMonBan(PitGrammar.MonBanContext ctx);
	/**
	 * Visit a parse tree produced by {@link PitGrammar#pitRecord}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPitRecord(PitGrammar.PitRecordContext ctx);
	/**
	 * Visit a parse tree produced by {@link PitGrammar#file}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFile(PitGrammar.FileContext ctx);
}