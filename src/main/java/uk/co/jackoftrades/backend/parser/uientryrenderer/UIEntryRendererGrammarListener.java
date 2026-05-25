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

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link UIEntryRendererGrammarParser}.
 */
public interface UIEntryRendererGrammarListener extends ParseTreeListener {
    /**
     * Enter a parse tree produced by {@link UIEntryRendererGrammarParser#name}.
     *
     * @param ctx the parse tree
     */
    void enterName(UIEntryRendererGrammarParser.NameContext ctx);

    /**
     * Exit a parse tree produced by {@link UIEntryRendererGrammarParser#name}.
     *
     * @param ctx the parse tree
     */
    void exitName(UIEntryRendererGrammarParser.NameContext ctx);

    /**
     * Enter a parse tree produced by {@link UIEntryRendererGrammarParser#code}.
     *
     * @param ctx the parse tree
     */
    void enterCode(UIEntryRendererGrammarParser.CodeContext ctx);

    /**
     * Exit a parse tree produced by {@link UIEntryRendererGrammarParser#code}.
     *
     * @param ctx the parse tree
     */
    void exitCode(UIEntryRendererGrammarParser.CodeContext ctx);

    /**
     * Enter a parse tree produced by {@link UIEntryRendererGrammarParser#colours}.
     *
     * @param ctx the parse tree
     */
    void enterColours(UIEntryRendererGrammarParser.ColoursContext ctx);

    /**
     * Exit a parse tree produced by {@link UIEntryRendererGrammarParser#colours}.
     *
     * @param ctx the parse tree
     */
    void exitColours(UIEntryRendererGrammarParser.ColoursContext ctx);

    /**
     * Enter a parse tree produced by {@link UIEntryRendererGrammarParser#labelcolours}.
     *
     * @param ctx the parse tree
     */
    void enterLabelcolours(UIEntryRendererGrammarParser.LabelcoloursContext ctx);

    /**
     * Exit a parse tree produced by {@link UIEntryRendererGrammarParser#labelcolours}.
     *
     * @param ctx the parse tree
     */
    void exitLabelcolours(UIEntryRendererGrammarParser.LabelcoloursContext ctx);

    /**
     * Enter a parse tree produced by {@link UIEntryRendererGrammarParser#symbols}.
     *
     * @param ctx the parse tree
     */
    void enterSymbols(UIEntryRendererGrammarParser.SymbolsContext ctx);

    /**
     * Exit a parse tree produced by {@link UIEntryRendererGrammarParser#symbols}.
     *
     * @param ctx the parse tree
     */
    void exitSymbols(UIEntryRendererGrammarParser.SymbolsContext ctx);

    /**
     * Enter a parse tree produced by {@link UIEntryRendererGrammarParser#ndigit}.
     *
     * @param ctx the parse tree
     */
    void enterNdigit(UIEntryRendererGrammarParser.NdigitContext ctx);

    /**
     * Exit a parse tree produced by {@link UIEntryRendererGrammarParser#ndigit}.
     *
     * @param ctx the parse tree
     */
    void exitNdigit(UIEntryRendererGrammarParser.NdigitContext ctx);

    /**
     * Enter a parse tree produced by {@link UIEntryRendererGrammarParser#sign}.
     *
     * @param ctx the parse tree
     */
    void enterSign(UIEntryRendererGrammarParser.SignContext ctx);

    /**
     * Exit a parse tree produced by {@link UIEntryRendererGrammarParser#sign}.
     *
     * @param ctx the parse tree
     */
    void exitSign(UIEntryRendererGrammarParser.SignContext ctx);

    /**
     * Enter a parse tree produced by {@link UIEntryRendererGrammarParser#uiEntry}.
     *
     * @param ctx the parse tree
     */
    void enterUiEntry(UIEntryRendererGrammarParser.UiEntryContext ctx);

    /**
     * Exit a parse tree produced by {@link UIEntryRendererGrammarParser#uiEntry}.
     *
     * @param ctx the parse tree
     */
    void exitUiEntry(UIEntryRendererGrammarParser.UiEntryContext ctx);

    /**
     * Enter a parse tree produced by {@link UIEntryRendererGrammarParser#file}.
     *
     * @param ctx the parse tree
     */
    void enterFile(UIEntryRendererGrammarParser.FileContext ctx);

    /**
     * Exit a parse tree produced by {@link UIEntryRendererGrammarParser#file}.
     *
     * @param ctx the parse tree
     */
    void exitFile(UIEntryRendererGrammarParser.FileContext ctx);
}