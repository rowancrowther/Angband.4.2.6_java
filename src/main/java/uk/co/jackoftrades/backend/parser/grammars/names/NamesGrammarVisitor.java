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
// Generated from NamesGrammar.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.grammars.names;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link NamesGrammar}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 *            operations with no return type.
 */
public interface NamesGrammarVisitor<T> extends ParseTreeVisitor<T> {
    /**
     * Visit a parse tree produced by {@link NamesGrammar#recordCount}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitRecordCount(NamesGrammar.RecordCountContext ctx);

    /**
     * Visit a parse tree produced by {@link NamesGrammar#section}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitSection(NamesGrammar.SectionContext ctx);

    /**
     * Visit a parse tree produced by {@link NamesGrammar#word}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitWord(NamesGrammar.WordContext ctx);

    /**
     * Visit a parse tree produced by {@link NamesGrammar#record}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitRecord(NamesGrammar.RecordContext ctx);

    /**
     * Visit a parse tree produced by {@link NamesGrammar#file}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFile(NamesGrammar.FileContext ctx);
}