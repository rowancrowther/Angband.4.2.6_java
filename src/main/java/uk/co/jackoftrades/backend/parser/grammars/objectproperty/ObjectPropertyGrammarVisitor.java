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
// Generated from ObjectPropertyGrammar.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.grammars.objectproperty;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link ObjectPropertyGrammar}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 *            operations with no return type.
 */
public interface ObjectPropertyGrammarVisitor<T> extends ParseTreeVisitor<T> {
    /**
     * Visit a parse tree produced by {@link ObjectPropertyGrammar#recordCount}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitRecordCount(ObjectPropertyGrammar.RecordCountContext ctx);

    /**
     * Visit a parse tree produced by {@link ObjectPropertyGrammar#name}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitName(ObjectPropertyGrammar.NameContext ctx);

    /**
     * Visit a parse tree produced by {@link ObjectPropertyGrammar#type}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitType(ObjectPropertyGrammar.TypeContext ctx);

    /**
     * Visit a parse tree produced by {@link ObjectPropertyGrammar#subtype}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitSubtype(ObjectPropertyGrammar.SubtypeContext ctx);

    /**
     * Visit a parse tree produced by {@link ObjectPropertyGrammar#idType}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitIdType(ObjectPropertyGrammar.IdTypeContext ctx);

    /**
     * Visit a parse tree produced by {@link ObjectPropertyGrammar#codeVal}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitCodeVal(ObjectPropertyGrammar.CodeValContext ctx);

    /**
     * Visit a parse tree produced by {@link ObjectPropertyGrammar#power}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitPower(ObjectPropertyGrammar.PowerContext ctx);

    /**
     * Visit a parse tree produced by {@link ObjectPropertyGrammar#mult}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitMult(ObjectPropertyGrammar.MultContext ctx);

    /**
     * Visit a parse tree produced by {@link ObjectPropertyGrammar#typeMult}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitTypeMult(ObjectPropertyGrammar.TypeMultContext ctx);

    /**
     * Visit a parse tree produced by {@link ObjectPropertyGrammar#adjective}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitAdjective(ObjectPropertyGrammar.AdjectiveContext ctx);

    /**
     * Visit a parse tree produced by {@link ObjectPropertyGrammar#negAdjective}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitNegAdjective(ObjectPropertyGrammar.NegAdjectiveContext ctx);

    /**
     * Visit a parse tree produced by {@link ObjectPropertyGrammar#msg}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitMsg(ObjectPropertyGrammar.MsgContext ctx);

    /**
     * Visit a parse tree produced by {@link ObjectPropertyGrammar#bindui}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitBindui(ObjectPropertyGrammar.BinduiContext ctx);

    /**
     * Visit a parse tree produced by {@link ObjectPropertyGrammar#desc}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitDesc(ObjectPropertyGrammar.DescContext ctx);

    /**
     * Visit a parse tree produced by {@link ObjectPropertyGrammar#objectProperty}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitObjectProperty(ObjectPropertyGrammar.ObjectPropertyContext ctx);

    /**
     * Visit a parse tree produced by {@link ObjectPropertyGrammar#file}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFile(ObjectPropertyGrammar.FileContext ctx);
}