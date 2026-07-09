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

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link HistoryParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 *            operations with no return type.
 */
public interface HistoryVisitor<T> extends ParseTreeVisitor<T> {
    /**
     * Visit a parse tree produced by {@link HistoryParser#chart}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitChart(HistoryParser.ChartContext ctx);

    /**
     * Visit a parse tree produced by {@link HistoryParser#phrase}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitPhrase(HistoryParser.PhraseContext ctx);

    /**
     * Visit a parse tree produced by {@link HistoryParser#history}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitHistory(HistoryParser.HistoryContext ctx);

    /**
     * Visit a parse tree produced by {@link HistoryParser#file}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFile(HistoryParser.FileContext ctx);
}