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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/parser/grammars/UIEntry.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.uientry;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link UIEntryParser}.
 */
public interface UIEntryListener extends ParseTreeListener {
    /**
     * Enter a parse tree produced by {@link UIEntryParser#tag}.
     *
     * @param ctx the parse tree
     */
    void enterTag(UIEntryParser.TagContext ctx);

    /**
     * Exit a parse tree produced by {@link UIEntryParser#tag}.
     *
     * @param ctx the parse tree
     */
    void exitTag(UIEntryParser.TagContext ctx);

    /**
     * Enter a parse tree produced by {@link UIEntryParser#name}.
     *
     * @param ctx the parse tree
     */
    void enterName(UIEntryParser.NameContext ctx);

    /**
     * Exit a parse tree produced by {@link UIEntryParser#name}.
     *
     * @param ctx the parse tree
     */
    void exitName(UIEntryParser.NameContext ctx);

    /**
     * Enter a parse tree produced by {@link UIEntryParser#parameter}.
     *
     * @param ctx the parse tree
     */
    void enterParameter(UIEntryParser.ParameterContext ctx);

    /**
     * Exit a parse tree produced by {@link UIEntryParser#parameter}.
     *
     * @param ctx the parse tree
     */
    void exitParameter(UIEntryParser.ParameterContext ctx);

    /**
     * Enter a parse tree produced by {@link UIEntryParser#renderer}.
     *
     * @param ctx the parse tree
     */
    void enterRenderer(UIEntryParser.RendererContext ctx);

    /**
     * Exit a parse tree produced by {@link UIEntryParser#renderer}.
     *
     * @param ctx the parse tree
     */
    void exitRenderer(UIEntryParser.RendererContext ctx);

    /**
     * Enter a parse tree produced by {@link UIEntryParser#combine}.
     *
     * @param ctx the parse tree
     */
    void enterCombine(UIEntryParser.CombineContext ctx);

    /**
     * Exit a parse tree produced by {@link UIEntryParser#combine}.
     *
     * @param ctx the parse tree
     */
    void exitCombine(UIEntryParser.CombineContext ctx);

    /**
     * Enter a parse tree produced by {@link UIEntryParser#priority}.
     *
     * @param ctx the parse tree
     */
    void enterPriority(UIEntryParser.PriorityContext ctx);

    /**
     * Exit a parse tree produced by {@link UIEntryParser#priority}.
     *
     * @param ctx the parse tree
     */
    void exitPriority(UIEntryParser.PriorityContext ctx);

    /**
     * Enter a parse tree produced by {@link UIEntryParser#category}.
     *
     * @param ctx the parse tree
     */
    void enterCategory(UIEntryParser.CategoryContext ctx);

    /**
     * Exit a parse tree produced by {@link UIEntryParser#category}.
     *
     * @param ctx the parse tree
     */
    void exitCategory(UIEntryParser.CategoryContext ctx);

    /**
     * Enter a parse tree produced by {@link UIEntryParser#flags}.
     *
     * @param ctx the parse tree
     */
    void enterFlags(UIEntryParser.FlagsContext ctx);

    /**
     * Exit a parse tree produced by {@link UIEntryParser#flags}.
     *
     * @param ctx the parse tree
     */
    void exitFlags(UIEntryParser.FlagsContext ctx);

    /**
     * Enter a parse tree produced by {@link UIEntryParser#desc}.
     *
     * @param ctx the parse tree
     */
    void enterDesc(UIEntryParser.DescContext ctx);

    /**
     * Exit a parse tree produced by {@link UIEntryParser#desc}.
     *
     * @param ctx the parse tree
     */
    void exitDesc(UIEntryParser.DescContext ctx);

    /**
     * Enter a parse tree produced by {@link UIEntryParser#label}.
     *
     * @param ctx the parse tree
     */
    void enterLabel(UIEntryParser.LabelContext ctx);

    /**
     * Exit a parse tree produced by {@link UIEntryParser#label}.
     *
     * @param ctx the parse tree
     */
    void exitLabel(UIEntryParser.LabelContext ctx);

    /**
     * Enter a parse tree produced by {@link UIEntryParser#label5}.
     *
     * @param ctx the parse tree
     */
    void enterLabel5(UIEntryParser.Label5Context ctx);

    /**
     * Exit a parse tree produced by {@link UIEntryParser#label5}.
     *
     * @param ctx the parse tree
     */
    void exitLabel5(UIEntryParser.Label5Context ctx);

    /**
     * Enter a parse tree produced by {@link UIEntryParser#label2}.
     *
     * @param ctx the parse tree
     */
    void enterLabel2(UIEntryParser.Label2Context ctx);

    /**
     * Exit a parse tree produced by {@link UIEntryParser#label2}.
     *
     * @param ctx the parse tree
     */
    void exitLabel2(UIEntryParser.Label2Context ctx);

    /**
     * Enter a parse tree produced by {@link UIEntryParser#template}.
     *
     * @param ctx the parse tree
     */
    void enterTemplate(UIEntryParser.TemplateContext ctx);

    /**
     * Exit a parse tree produced by {@link UIEntryParser#template}.
     *
     * @param ctx the parse tree
     */
    void exitTemplate(UIEntryParser.TemplateContext ctx);

    /**
     * Enter a parse tree produced by {@link UIEntryParser#uiEntry}.
     *
     * @param ctx the parse tree
     */
    void enterUiEntry(UIEntryParser.UiEntryContext ctx);

    /**
     * Exit a parse tree produced by {@link UIEntryParser#uiEntry}.
     *
     * @param ctx the parse tree
     */
    void exitUiEntry(UIEntryParser.UiEntryContext ctx);

    /**
     * Enter a parse tree produced by {@link UIEntryParser#file}.
     *
     * @param ctx the parse tree
     */
    void enterFile(UIEntryParser.FileContext ctx);

    /**
     * Exit a parse tree produced by {@link UIEntryParser#file}.
     *
     * @param ctx the parse tree
     */
    void exitFile(UIEntryParser.FileContext ctx);
}