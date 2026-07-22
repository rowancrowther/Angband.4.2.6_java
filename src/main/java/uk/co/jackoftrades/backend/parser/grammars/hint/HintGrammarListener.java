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
// Generated from HintGrammar.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.grammars.hint;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link HintGrammar}.
 */
public interface HintGrammarListener extends ParseTreeListener {
    /**
     * Enter a parse tree produced by {@link HintGrammar#recordCount}.
     *
     * @param ctx the parse tree
     */
    void enterRecordCount(HintGrammar.RecordCountContext ctx);

    /**
     * Exit a parse tree produced by {@link HintGrammar#recordCount}.
     *
     * @param ctx the parse tree
     */
    void exitRecordCount(HintGrammar.RecordCountContext ctx);

    /**
     * Enter a parse tree produced by {@link HintGrammar#hint}.
     *
     * @param ctx the parse tree
     */
    void enterHint(HintGrammar.HintContext ctx);

    /**
     * Exit a parse tree produced by {@link HintGrammar#hint}.
     *
     * @param ctx the parse tree
     */
    void exitHint(HintGrammar.HintContext ctx);

    /**
     * Enter a parse tree produced by {@link HintGrammar#hintRecord}.
     *
     * @param ctx the parse tree
     */
    void enterHintRecord(HintGrammar.HintRecordContext ctx);

    /**
     * Exit a parse tree produced by {@link HintGrammar#hintRecord}.
     *
     * @param ctx the parse tree
     */
    void exitHintRecord(HintGrammar.HintRecordContext ctx);

    /**
     * Enter a parse tree produced by {@link HintGrammar#file}.
     *
     * @param ctx the parse tree
     */
    void enterFile(HintGrammar.FileContext ctx);

    /**
     * Exit a parse tree produced by {@link HintGrammar#file}.
     *
     * @param ctx the parse tree
     */
    void exitFile(HintGrammar.FileContext ctx);
}