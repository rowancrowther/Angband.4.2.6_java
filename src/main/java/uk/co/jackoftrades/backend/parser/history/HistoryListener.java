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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/parser/grammars/History.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.history;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link HistoryParser}.
 */
public interface HistoryListener extends ParseTreeListener {
    /**
     * Enter a parse tree produced by {@link HistoryParser#chart}.
     *
     * @param ctx the parse tree
     */
    void enterChart(HistoryParser.ChartContext ctx);

    /**
     * Exit a parse tree produced by {@link HistoryParser#chart}.
     *
     * @param ctx the parse tree
     */
    void exitChart(HistoryParser.ChartContext ctx);

    /**
     * Enter a parse tree produced by {@link HistoryParser#phrase}.
     *
     * @param ctx the parse tree
     */
    void enterPhrase(HistoryParser.PhraseContext ctx);

    /**
     * Exit a parse tree produced by {@link HistoryParser#phrase}.
     *
     * @param ctx the parse tree
     */
    void exitPhrase(HistoryParser.PhraseContext ctx);

    /**
     * Enter a parse tree produced by {@link HistoryParser#history}.
     *
     * @param ctx the parse tree
     */
    void enterHistory(HistoryParser.HistoryContext ctx);

    /**
     * Exit a parse tree produced by {@link HistoryParser#history}.
     *
     * @param ctx the parse tree
     */
    void exitHistory(HistoryParser.HistoryContext ctx);

    /**
     * Enter a parse tree produced by {@link HistoryParser#file}.
     *
     * @param ctx the parse tree
     */
    void enterFile(HistoryParser.FileContext ctx);

    /**
     * Exit a parse tree produced by {@link HistoryParser#file}.
     *
     * @param ctx the parse tree
     */
    void exitFile(HistoryParser.FileContext ctx);
}