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
// Generated from PlayerPropertyGrammar.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.grammars.playerproperty;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link PlayerPropertyGrammar}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 *            operations with no return type.
 */
public interface PlayerPropertyGrammarVisitor<T> extends ParseTreeVisitor<T> {
    /**
     * Visit a parse tree produced by {@link PlayerPropertyGrammar#recordCount}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitRecordCount(PlayerPropertyGrammar.RecordCountContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerPropertyGrammar#type}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitType(PlayerPropertyGrammar.TypeContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerPropertyGrammar#code}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitCode(PlayerPropertyGrammar.CodeContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerPropertyGrammar#binduiEntry}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitBinduiEntry(PlayerPropertyGrammar.BinduiEntryContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerPropertyGrammar#bindUI}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitBindUI(PlayerPropertyGrammar.BindUIContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerPropertyGrammar#name}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitName(PlayerPropertyGrammar.NameContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerPropertyGrammar#desc}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitDesc(PlayerPropertyGrammar.DescContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerPropertyGrammar#value}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitValue(PlayerPropertyGrammar.ValueContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerPropertyGrammar#property}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitProperty(PlayerPropertyGrammar.PropertyContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerPropertyGrammar#file}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFile(PlayerPropertyGrammar.FileContext ctx);
}