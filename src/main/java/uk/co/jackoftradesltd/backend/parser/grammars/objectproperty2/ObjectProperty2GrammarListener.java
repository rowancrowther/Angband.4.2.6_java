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

// Generated from /tmp/claude-1000/-home-rowan-IdeaProjects-Angband-4-2-6-java/f8028824-ed88-432e-9012-db1970966e36/scratchpad/gen7/ObjectProperty2Grammar.g4 by ANTLR 4.13.2
package uk.co.jackoftradesltd.backend.parser.grammars.objectproperty2;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link ObjectProperty2Grammar}.
 */
public interface ObjectProperty2GrammarListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link ObjectProperty2Grammar#name}.
	 * @param ctx the parse tree
	 */
	void enterName(ObjectProperty2Grammar.NameContext ctx);
	/**
	 * Exit a parse tree produced by {@link ObjectProperty2Grammar#name}.
	 * @param ctx the parse tree
	 */
	void exitName(ObjectProperty2Grammar.NameContext ctx);
	/**
	 * Enter a parse tree produced by {@link ObjectProperty2Grammar#type}.
	 * @param ctx the parse tree
	 */
	void enterType(ObjectProperty2Grammar.TypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link ObjectProperty2Grammar#type}.
	 * @param ctx the parse tree
	 */
	void exitType(ObjectProperty2Grammar.TypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link ObjectProperty2Grammar#subtype}.
	 * @param ctx the parse tree
	 */
	void enterSubtype(ObjectProperty2Grammar.SubtypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link ObjectProperty2Grammar#subtype}.
	 * @param ctx the parse tree
	 */
	void exitSubtype(ObjectProperty2Grammar.SubtypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link ObjectProperty2Grammar#idType}.
	 * @param ctx the parse tree
	 */
	void enterIdType(ObjectProperty2Grammar.IdTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link ObjectProperty2Grammar#idType}.
	 * @param ctx the parse tree
	 */
	void exitIdType(ObjectProperty2Grammar.IdTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link ObjectProperty2Grammar#codeVal}.
	 * @param ctx the parse tree
	 */
	void enterCodeVal(ObjectProperty2Grammar.CodeValContext ctx);
	/**
	 * Exit a parse tree produced by {@link ObjectProperty2Grammar#codeVal}.
	 * @param ctx the parse tree
	 */
	void exitCodeVal(ObjectProperty2Grammar.CodeValContext ctx);
	/**
	 * Enter a parse tree produced by {@link ObjectProperty2Grammar#power}.
	 * @param ctx the parse tree
	 */
	void enterPower(ObjectProperty2Grammar.PowerContext ctx);
	/**
	 * Exit a parse tree produced by {@link ObjectProperty2Grammar#power}.
	 * @param ctx the parse tree
	 */
	void exitPower(ObjectProperty2Grammar.PowerContext ctx);
	/**
	 * Enter a parse tree produced by {@link ObjectProperty2Grammar#mult}.
	 * @param ctx the parse tree
	 */
	void enterMult(ObjectProperty2Grammar.MultContext ctx);
	/**
	 * Exit a parse tree produced by {@link ObjectProperty2Grammar#mult}.
	 * @param ctx the parse tree
	 */
	void exitMult(ObjectProperty2Grammar.MultContext ctx);
	/**
	 * Enter a parse tree produced by {@link ObjectProperty2Grammar#typeMult}.
	 * @param ctx the parse tree
	 */
	void enterTypeMult(ObjectProperty2Grammar.TypeMultContext ctx);
	/**
	 * Exit a parse tree produced by {@link ObjectProperty2Grammar#typeMult}.
	 * @param ctx the parse tree
	 */
	void exitTypeMult(ObjectProperty2Grammar.TypeMultContext ctx);
	/**
	 * Enter a parse tree produced by {@link ObjectProperty2Grammar#adjective}.
	 * @param ctx the parse tree
	 */
	void enterAdjective(ObjectProperty2Grammar.AdjectiveContext ctx);
	/**
	 * Exit a parse tree produced by {@link ObjectProperty2Grammar#adjective}.
	 * @param ctx the parse tree
	 */
	void exitAdjective(ObjectProperty2Grammar.AdjectiveContext ctx);
	/**
	 * Enter a parse tree produced by {@link ObjectProperty2Grammar#negAdjective}.
	 * @param ctx the parse tree
	 */
	void enterNegAdjective(ObjectProperty2Grammar.NegAdjectiveContext ctx);
	/**
	 * Exit a parse tree produced by {@link ObjectProperty2Grammar#negAdjective}.
	 * @param ctx the parse tree
	 */
	void exitNegAdjective(ObjectProperty2Grammar.NegAdjectiveContext ctx);
	/**
	 * Enter a parse tree produced by {@link ObjectProperty2Grammar#msg}.
	 * @param ctx the parse tree
	 */
	void enterMsg(ObjectProperty2Grammar.MsgContext ctx);
	/**
	 * Exit a parse tree produced by {@link ObjectProperty2Grammar#msg}.
	 * @param ctx the parse tree
	 */
	void exitMsg(ObjectProperty2Grammar.MsgContext ctx);
	/**
	 * Enter a parse tree produced by {@link ObjectProperty2Grammar#bindui}.
	 * @param ctx the parse tree
	 */
	void enterBindui(ObjectProperty2Grammar.BinduiContext ctx);
	/**
	 * Exit a parse tree produced by {@link ObjectProperty2Grammar#bindui}.
	 * @param ctx the parse tree
	 */
	void exitBindui(ObjectProperty2Grammar.BinduiContext ctx);
	/**
	 * Enter a parse tree produced by {@link ObjectProperty2Grammar#desc}.
	 * @param ctx the parse tree
	 */
	void enterDesc(ObjectProperty2Grammar.DescContext ctx);
	/**
	 * Exit a parse tree produced by {@link ObjectProperty2Grammar#desc}.
	 * @param ctx the parse tree
	 */
	void exitDesc(ObjectProperty2Grammar.DescContext ctx);
	/**
	 * Enter a parse tree produced by {@link ObjectProperty2Grammar#objectProperty}.
	 * @param ctx the parse tree
	 */
	void enterObjectProperty(ObjectProperty2Grammar.ObjectPropertyContext ctx);
	/**
	 * Exit a parse tree produced by {@link ObjectProperty2Grammar#objectProperty}.
	 * @param ctx the parse tree
	 */
	void exitObjectProperty(ObjectProperty2Grammar.ObjectPropertyContext ctx);
	/**
	 * Enter a parse tree produced by {@link ObjectProperty2Grammar#file}.
	 * @param ctx the parse tree
	 */
	void enterFile(ObjectProperty2Grammar.FileContext ctx);
	/**
	 * Exit a parse tree produced by {@link ObjectProperty2Grammar#file}.
	 * @param ctx the parse tree
	 */
	void exitFile(ObjectProperty2Grammar.FileContext ctx);
}