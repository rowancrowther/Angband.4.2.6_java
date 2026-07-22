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

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link NamesGrammar}.
 */
public interface NamesGrammarListener extends ParseTreeListener {
    /**
     * Enter a parse tree produced by {@link NamesGrammar#recordCount}.
     *
     * @param ctx the parse tree
     */
    void enterRecordCount(NamesGrammar.RecordCountContext ctx);

    /**
     * Exit a parse tree produced by {@link NamesGrammar#recordCount}.
     *
     * @param ctx the parse tree
     */
    void exitRecordCount(NamesGrammar.RecordCountContext ctx);

    /**
     * Enter a parse tree produced by {@link NamesGrammar#section}.
     *
     * @param ctx the parse tree
     */
    void enterSection(NamesGrammar.SectionContext ctx);

    /**
     * Exit a parse tree produced by {@link NamesGrammar#section}.
     *
     * @param ctx the parse tree
     */
    void exitSection(NamesGrammar.SectionContext ctx);

    /**
     * Enter a parse tree produced by {@link NamesGrammar#word}.
     *
     * @param ctx the parse tree
     */
    void enterWord(NamesGrammar.WordContext ctx);

    /**
     * Exit a parse tree produced by {@link NamesGrammar#word}.
     *
     * @param ctx the parse tree
     */
    void exitWord(NamesGrammar.WordContext ctx);

    /**
     * Enter a parse tree produced by {@link NamesGrammar#record}.
     *
     * @param ctx the parse tree
     */
    void enterRecord(NamesGrammar.RecordContext ctx);

    /**
     * Exit a parse tree produced by {@link NamesGrammar#record}.
     *
     * @param ctx the parse tree
     */
    void exitRecord(NamesGrammar.RecordContext ctx);

    /**
     * Enter a parse tree produced by {@link NamesGrammar#file}.
     *
     * @param ctx the parse tree
     */
    void enterFile(NamesGrammar.FileContext ctx);

    /**
     * Exit a parse tree produced by {@link NamesGrammar#file}.
     *
     * @param ctx the parse tree
     */
    void exitFile(NamesGrammar.FileContext ctx);
}