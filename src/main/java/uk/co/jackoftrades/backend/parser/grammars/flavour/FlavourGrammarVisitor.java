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

// Generated from FlavourGrammar.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.grammars.flavour;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link FlavourGrammar}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 *            operations with no return type.
 */
public interface FlavourGrammarVisitor<T> extends ParseTreeVisitor<T> {
    /**
     * Visit a parse tree produced by {@link FlavourGrammar#recordCount}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitRecordCount(FlavourGrammar.RecordCountContext ctx);

    /**
     * Visit a parse tree produced by {@link FlavourGrammar#kind}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitKind(FlavourGrammar.KindContext ctx);

    /**
     * Visit a parse tree produced by {@link FlavourGrammar#fixed}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFixed(FlavourGrammar.FixedContext ctx);

    /**
     * Visit a parse tree produced by {@link FlavourGrammar#flavour}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFlavour(FlavourGrammar.FlavourContext ctx);

    /**
     * Visit a parse tree produced by {@link FlavourGrammar#fixedOrFlavourBlock}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFixedOrFlavourBlock(FlavourGrammar.FixedOrFlavourBlockContext ctx);

    /**
     * Visit a parse tree produced by {@link FlavourGrammar#section}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitSection(FlavourGrammar.SectionContext ctx);

    /**
     * Visit a parse tree produced by {@link FlavourGrammar#file}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFile(FlavourGrammar.FileContext ctx);
}