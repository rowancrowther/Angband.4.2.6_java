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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/parser/grammars/PainGrammar.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.grammars.pain;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link PainGrammar}.
 */
public interface PainGrammarListener extends ParseTreeListener {
    /**
     * Enter a parse tree produced by {@link PainGrammar#recordCount}.
     *
     * @param ctx the parse tree
     */
    void enterRecordCount(PainGrammar.RecordCountContext ctx);

    /**
     * Exit a parse tree produced by {@link PainGrammar#recordCount}.
     *
     * @param ctx the parse tree
     */
    void exitRecordCount(PainGrammar.RecordCountContext ctx);

    /**
     * Enter a parse tree produced by {@link PainGrammar#type}.
     *
     * @param ctx the parse tree
     */
    void enterType(PainGrammar.TypeContext ctx);

    /**
     * Exit a parse tree produced by {@link PainGrammar#type}.
     *
     * @param ctx the parse tree
     */
    void exitType(PainGrammar.TypeContext ctx);

    /**
     * Enter a parse tree produced by {@link PainGrammar#message}.
     *
     * @param ctx the parse tree
     */
    void enterMessage(PainGrammar.MessageContext ctx);

    /**
     * Exit a parse tree produced by {@link PainGrammar#message}.
     *
     * @param ctx the parse tree
     */
    void exitMessage(PainGrammar.MessageContext ctx);

    /**
     * Enter a parse tree produced by {@link PainGrammar#painEntry}.
     *
     * @param ctx the parse tree
     */
    void enterPainEntry(PainGrammar.PainEntryContext ctx);

    /**
     * Exit a parse tree produced by {@link PainGrammar#painEntry}.
     *
     * @param ctx the parse tree
     */
    void exitPainEntry(PainGrammar.PainEntryContext ctx);

    /**
     * Enter a parse tree produced by {@link PainGrammar#file}.
     *
     * @param ctx the parse tree
     */
    void enterFile(PainGrammar.FileContext ctx);

    /**
     * Exit a parse tree produced by {@link PainGrammar#file}.
     *
     * @param ctx the parse tree
     */
    void exitFile(PainGrammar.FileContext ctx);
}