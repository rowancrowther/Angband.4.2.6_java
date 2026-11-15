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

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link ObjectProperty2Grammar}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface ObjectProperty2GrammarVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link ObjectProperty2Grammar#name}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitName(ObjectProperty2Grammar.NameContext ctx);
	/**
	 * Visit a parse tree produced by {@link ObjectProperty2Grammar#type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitType(ObjectProperty2Grammar.TypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link ObjectProperty2Grammar#subtype}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSubtype(ObjectProperty2Grammar.SubtypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link ObjectProperty2Grammar#idType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIdType(ObjectProperty2Grammar.IdTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link ObjectProperty2Grammar#codeVal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCodeVal(ObjectProperty2Grammar.CodeValContext ctx);
	/**
	 * Visit a parse tree produced by {@link ObjectProperty2Grammar#power}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPower(ObjectProperty2Grammar.PowerContext ctx);
	/**
	 * Visit a parse tree produced by {@link ObjectProperty2Grammar#mult}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMult(ObjectProperty2Grammar.MultContext ctx);
	/**
	 * Visit a parse tree produced by {@link ObjectProperty2Grammar#typeMult}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTypeMult(ObjectProperty2Grammar.TypeMultContext ctx);
	/**
	 * Visit a parse tree produced by {@link ObjectProperty2Grammar#adjective}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAdjective(ObjectProperty2Grammar.AdjectiveContext ctx);
	/**
	 * Visit a parse tree produced by {@link ObjectProperty2Grammar#negAdjective}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNegAdjective(ObjectProperty2Grammar.NegAdjectiveContext ctx);
	/**
	 * Visit a parse tree produced by {@link ObjectProperty2Grammar#msg}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMsg(ObjectProperty2Grammar.MsgContext ctx);
	/**
	 * Visit a parse tree produced by {@link ObjectProperty2Grammar#bindui}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBindui(ObjectProperty2Grammar.BinduiContext ctx);
	/**
	 * Visit a parse tree produced by {@link ObjectProperty2Grammar#desc}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDesc(ObjectProperty2Grammar.DescContext ctx);
	/**
	 * Visit a parse tree produced by {@link ObjectProperty2Grammar#objectProperty}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitObjectProperty(ObjectProperty2Grammar.ObjectPropertyContext ctx);
	/**
	 * Visit a parse tree produced by {@link ObjectProperty2Grammar#file}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFile(ObjectProperty2Grammar.FileContext ctx);
}