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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/parser/grammars/UIEntryRenderer.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.uientryrenderer;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link UIEntryRendererParser}.
 */
public interface UIEntryRendererListener extends ParseTreeListener {
    /**
     * Enter a parse tree produced by {@link UIEntryRendererParser#name}.
     *
     * @param ctx the parse tree
     */
    void enterName(UIEntryRendererParser.NameContext ctx);

    /**
     * Exit a parse tree produced by {@link UIEntryRendererParser#name}.
     *
     * @param ctx the parse tree
     */
    void exitName(UIEntryRendererParser.NameContext ctx);

    /**
     * Enter a parse tree produced by {@link UIEntryRendererParser#code}.
     *
     * @param ctx the parse tree
     */
    void enterCode(UIEntryRendererParser.CodeContext ctx);

    /**
     * Exit a parse tree produced by {@link UIEntryRendererParser#code}.
     *
     * @param ctx the parse tree
     */
    void exitCode(UIEntryRendererParser.CodeContext ctx);

    /**
     * Enter a parse tree produced by {@link UIEntryRendererParser#colours}.
     *
     * @param ctx the parse tree
     */
    void enterColours(UIEntryRendererParser.ColoursContext ctx);

    /**
     * Exit a parse tree produced by {@link UIEntryRendererParser#colours}.
     *
     * @param ctx the parse tree
     */
    void exitColours(UIEntryRendererParser.ColoursContext ctx);

    /**
     * Enter a parse tree produced by {@link UIEntryRendererParser#labelcolours}.
     *
     * @param ctx the parse tree
     */
    void enterLabelcolours(UIEntryRendererParser.LabelcoloursContext ctx);

    /**
     * Exit a parse tree produced by {@link UIEntryRendererParser#labelcolours}.
     *
     * @param ctx the parse tree
     */
    void exitLabelcolours(UIEntryRendererParser.LabelcoloursContext ctx);

    /**
     * Enter a parse tree produced by {@link UIEntryRendererParser#symbols}.
     *
     * @param ctx the parse tree
     */
    void enterSymbols(UIEntryRendererParser.SymbolsContext ctx);

    /**
     * Exit a parse tree produced by {@link UIEntryRendererParser#symbols}.
     *
     * @param ctx the parse tree
     */
    void exitSymbols(UIEntryRendererParser.SymbolsContext ctx);

    /**
     * Enter a parse tree produced by {@link UIEntryRendererParser#ndigit}.
     *
     * @param ctx the parse tree
     */
    void enterNdigit(UIEntryRendererParser.NdigitContext ctx);

    /**
     * Exit a parse tree produced by {@link UIEntryRendererParser#ndigit}.
     *
     * @param ctx the parse tree
     */
    void exitNdigit(UIEntryRendererParser.NdigitContext ctx);

    /**
     * Enter a parse tree produced by {@link UIEntryRendererParser#sign}.
     *
     * @param ctx the parse tree
     */
    void enterSign(UIEntryRendererParser.SignContext ctx);

    /**
     * Exit a parse tree produced by {@link UIEntryRendererParser#sign}.
     *
     * @param ctx the parse tree
     */
    void exitSign(UIEntryRendererParser.SignContext ctx);

    /**
     * Enter a parse tree produced by {@link UIEntryRendererParser#uiEntry}.
     *
     * @param ctx the parse tree
     */
    void enterUiEntry(UIEntryRendererParser.UiEntryContext ctx);

    /**
     * Exit a parse tree produced by {@link UIEntryRendererParser#uiEntry}.
     *
     * @param ctx the parse tree
     */
    void exitUiEntry(UIEntryRendererParser.UiEntryContext ctx);

    /**
     * Enter a parse tree produced by {@link UIEntryRendererParser#file}.
     *
     * @param ctx the parse tree
     */
    void enterFile(UIEntryRendererParser.FileContext ctx);

    /**
     * Exit a parse tree produced by {@link UIEntryRendererParser#file}.
     *
     * @param ctx the parse tree
     */
    void exitFile(UIEntryRendererParser.FileContext ctx);
}