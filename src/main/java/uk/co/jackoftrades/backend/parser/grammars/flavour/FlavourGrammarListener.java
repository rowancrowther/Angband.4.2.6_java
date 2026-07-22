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

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link FlavourGrammar}.
 */
public interface FlavourGrammarListener extends ParseTreeListener {
    /**
     * Enter a parse tree produced by {@link FlavourGrammar#recordCount}.
     *
     * @param ctx the parse tree
     */
    void enterRecordCount(FlavourGrammar.RecordCountContext ctx);

    /**
     * Exit a parse tree produced by {@link FlavourGrammar#recordCount}.
     *
     * @param ctx the parse tree
     */
    void exitRecordCount(FlavourGrammar.RecordCountContext ctx);

    /**
     * Enter a parse tree produced by {@link FlavourGrammar#kind}.
     *
     * @param ctx the parse tree
     */
    void enterKind(FlavourGrammar.KindContext ctx);

    /**
     * Exit a parse tree produced by {@link FlavourGrammar#kind}.
     *
     * @param ctx the parse tree
     */
    void exitKind(FlavourGrammar.KindContext ctx);

    /**
     * Enter a parse tree produced by {@link FlavourGrammar#fixed}.
     *
     * @param ctx the parse tree
     */
    void enterFixed(FlavourGrammar.FixedContext ctx);

    /**
     * Exit a parse tree produced by {@link FlavourGrammar#fixed}.
     *
     * @param ctx the parse tree
     */
    void exitFixed(FlavourGrammar.FixedContext ctx);

    /**
     * Enter a parse tree produced by {@link FlavourGrammar#flavour}.
     *
     * @param ctx the parse tree
     */
    void enterFlavour(FlavourGrammar.FlavourContext ctx);

    /**
     * Exit a parse tree produced by {@link FlavourGrammar#flavour}.
     *
     * @param ctx the parse tree
     */
    void exitFlavour(FlavourGrammar.FlavourContext ctx);

    /**
     * Enter a parse tree produced by {@link FlavourGrammar#fixedOrFlavourBlock}.
     *
     * @param ctx the parse tree
     */
    void enterFixedOrFlavourBlock(FlavourGrammar.FixedOrFlavourBlockContext ctx);

    /**
     * Exit a parse tree produced by {@link FlavourGrammar#fixedOrFlavourBlock}.
     *
     * @param ctx the parse tree
     */
    void exitFixedOrFlavourBlock(FlavourGrammar.FixedOrFlavourBlockContext ctx);

    /**
     * Enter a parse tree produced by {@link FlavourGrammar#section}.
     *
     * @param ctx the parse tree
     */
    void enterSection(FlavourGrammar.SectionContext ctx);

    /**
     * Exit a parse tree produced by {@link FlavourGrammar#section}.
     *
     * @param ctx the parse tree
     */
    void exitSection(FlavourGrammar.SectionContext ctx);

    /**
     * Enter a parse tree produced by {@link FlavourGrammar#file}.
     *
     * @param ctx the parse tree
     */
    void enterFile(FlavourGrammar.FileContext ctx);

    /**
     * Exit a parse tree produced by {@link FlavourGrammar#file}.
     *
     * @param ctx the parse tree
     */
    void exitFile(FlavourGrammar.FileContext ctx);
}