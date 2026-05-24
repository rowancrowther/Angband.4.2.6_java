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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/parser/grammars/UIEntryBase.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.uientrybase;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link UIEntryBaseParser}.
 */
public interface UIEntryBaseListener extends ParseTreeListener {
    /**
     * Enter a parse tree produced by {@link UIEntryBaseParser#name}.
     *
     * @param ctx the parse tree
     */
    void enterName(UIEntryBaseParser.NameContext ctx);

    /**
     * Exit a parse tree produced by {@link UIEntryBaseParser#name}.
     *
     * @param ctx the parse tree
     */
    void exitName(UIEntryBaseParser.NameContext ctx);

    /**
     * Enter a parse tree produced by {@link UIEntryBaseParser#renderer}.
     *
     * @param ctx the parse tree
     */
    void enterRenderer(UIEntryBaseParser.RendererContext ctx);

    /**
     * Exit a parse tree produced by {@link UIEntryBaseParser#renderer}.
     *
     * @param ctx the parse tree
     */
    void exitRenderer(UIEntryBaseParser.RendererContext ctx);

    /**
     * Enter a parse tree produced by {@link UIEntryBaseParser#combine}.
     *
     * @param ctx the parse tree
     */
    void enterCombine(UIEntryBaseParser.CombineContext ctx);

    /**
     * Exit a parse tree produced by {@link UIEntryBaseParser#combine}.
     *
     * @param ctx the parse tree
     */
    void exitCombine(UIEntryBaseParser.CombineContext ctx);

    /**
     * Enter a parse tree produced by {@link UIEntryBaseParser#category}.
     *
     * @param ctx the parse tree
     */
    void enterCategory(UIEntryBaseParser.CategoryContext ctx);

    /**
     * Exit a parse tree produced by {@link UIEntryBaseParser#category}.
     *
     * @param ctx the parse tree
     */
    void exitCategory(UIEntryBaseParser.CategoryContext ctx);

    /**
     * Enter a parse tree produced by {@link UIEntryBaseParser#flags}.
     *
     * @param ctx the parse tree
     */
    void enterFlags(UIEntryBaseParser.FlagsContext ctx);

    /**
     * Exit a parse tree produced by {@link UIEntryBaseParser#flags}.
     *
     * @param ctx the parse tree
     */
    void exitFlags(UIEntryBaseParser.FlagsContext ctx);

    /**
     * Enter a parse tree produced by {@link UIEntryBaseParser#desc}.
     *
     * @param ctx the parse tree
     */
    void enterDesc(UIEntryBaseParser.DescContext ctx);

    /**
     * Exit a parse tree produced by {@link UIEntryBaseParser#desc}.
     *
     * @param ctx the parse tree
     */
    void exitDesc(UIEntryBaseParser.DescContext ctx);

    /**
     * Enter a parse tree produced by {@link UIEntryBaseParser#entryBase}.
     *
     * @param ctx the parse tree
     */
    void enterEntryBase(UIEntryBaseParser.EntryBaseContext ctx);

    /**
     * Exit a parse tree produced by {@link UIEntryBaseParser#entryBase}.
     *
     * @param ctx the parse tree
     */
    void exitEntryBase(UIEntryBaseParser.EntryBaseContext ctx);

    /**
     * Enter a parse tree produced by {@link UIEntryBaseParser#file}.
     *
     * @param ctx the parse tree
     */
    void enterFile(UIEntryBaseParser.FileContext ctx);

    /**
     * Exit a parse tree produced by {@link UIEntryBaseParser#file}.
     *
     * @param ctx the parse tree
     */
    void exitFile(UIEntryBaseParser.FileContext ctx);
}