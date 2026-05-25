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
 *    Java code copyright (c) Rowan Crowther 2026
 */

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/parser/grammars/UIEntryRendererGrammar.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.uientryrenderer;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link UIEntryRendererGrammarParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 *            operations with no return type.
 */
public interface UIEntryRendererGrammarVisitor<T> extends ParseTreeVisitor<T> {
    /**
     * Visit a parse tree produced by {@link UIEntryRendererGrammarParser#name}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitName(UIEntryRendererGrammarParser.NameContext ctx);

    /**
     * Visit a parse tree produced by {@link UIEntryRendererGrammarParser#code}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitCode(UIEntryRendererGrammarParser.CodeContext ctx);

    /**
     * Visit a parse tree produced by {@link UIEntryRendererGrammarParser#colours}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitColours(UIEntryRendererGrammarParser.ColoursContext ctx);

    /**
     * Visit a parse tree produced by {@link UIEntryRendererGrammarParser#labelcolours}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitLabelcolours(UIEntryRendererGrammarParser.LabelcoloursContext ctx);

    /**
     * Visit a parse tree produced by {@link UIEntryRendererGrammarParser#symbols}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitSymbols(UIEntryRendererGrammarParser.SymbolsContext ctx);

    /**
     * Visit a parse tree produced by {@link UIEntryRendererGrammarParser#ndigit}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitNdigit(UIEntryRendererGrammarParser.NdigitContext ctx);

    /**
     * Visit a parse tree produced by {@link UIEntryRendererGrammarParser#sign}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitSign(UIEntryRendererGrammarParser.SignContext ctx);

    /**
     * Visit a parse tree produced by {@link UIEntryRendererGrammarParser#uiEntry}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitUiEntry(UIEntryRendererGrammarParser.UiEntryContext ctx);

    /**
     * Visit a parse tree produced by {@link UIEntryRendererGrammarParser#file}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFile(UIEntryRendererGrammarParser.FileContext ctx);
}