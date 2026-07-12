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

// Generated from BodyGrammar.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.grammars.body;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link BodyGrammar}.
 */
public interface BodyGrammarListener extends ParseTreeListener {
    /**
     * Enter a parse tree produced by {@link BodyGrammar#recordCount}.
     *
     * @param ctx the parse tree
     */
    void enterRecordCount(BodyGrammar.RecordCountContext ctx);

    /**
     * Exit a parse tree produced by {@link BodyGrammar#recordCount}.
     *
     * @param ctx the parse tree
     */
    void exitRecordCount(BodyGrammar.RecordCountContext ctx);

    /**
     * Enter a parse tree produced by {@link BodyGrammar#body}.
     *
     * @param ctx the parse tree
     */
    void enterBody(BodyGrammar.BodyContext ctx);

    /**
     * Exit a parse tree produced by {@link BodyGrammar#body}.
     *
     * @param ctx the parse tree
     */
    void exitBody(BodyGrammar.BodyContext ctx);

    /**
     * Enter a parse tree produced by {@link BodyGrammar#slot}.
     *
     * @param ctx the parse tree
     */
    void enterSlot(BodyGrammar.SlotContext ctx);

    /**
     * Exit a parse tree produced by {@link BodyGrammar#slot}.
     *
     * @param ctx the parse tree
     */
    void exitSlot(BodyGrammar.SlotContext ctx);

    /**
     * Enter a parse tree produced by {@link BodyGrammar#bodyType}.
     *
     * @param ctx the parse tree
     */
    void enterBodyType(BodyGrammar.BodyTypeContext ctx);

    /**
     * Exit a parse tree produced by {@link BodyGrammar#bodyType}.
     *
     * @param ctx the parse tree
     */
    void exitBodyType(BodyGrammar.BodyTypeContext ctx);

    /**
     * Enter a parse tree produced by {@link BodyGrammar#file}.
     *
     * @param ctx the parse tree
     */
    void enterFile(BodyGrammar.FileContext ctx);

    /**
     * Exit a parse tree produced by {@link BodyGrammar#file}.
     *
     * @param ctx the parse tree
     */
    void exitFile(BodyGrammar.FileContext ctx);
}