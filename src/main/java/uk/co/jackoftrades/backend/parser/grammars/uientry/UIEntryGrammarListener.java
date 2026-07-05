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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/parser/grammars/UIEntryGrammar.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.grammars.uientry;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link UIEntryGrammar}.
 */
public interface UIEntryGrammarListener extends ParseTreeListener {
    /**
     * Enter a parse tree produced by {@link UIEntryGrammar#recordCount}.
     *
     * @param ctx the parse tree
     */
    void enterRecordCount(UIEntryGrammar.RecordCountContext ctx);

    /**
     * Exit a parse tree produced by {@link UIEntryGrammar#recordCount}.
     *
     * @param ctx the parse tree
     */
    void exitRecordCount(UIEntryGrammar.RecordCountContext ctx);

    /**
     * Enter a parse tree produced by {@link UIEntryGrammar#tag}.
     *
     * @param ctx the parse tree
     */
    void enterTag(UIEntryGrammar.TagContext ctx);

    /**
     * Exit a parse tree produced by {@link UIEntryGrammar#tag}.
     *
     * @param ctx the parse tree
     */
    void exitTag(UIEntryGrammar.TagContext ctx);

    /**
     * Enter a parse tree produced by {@link UIEntryGrammar#name}.
     *
     * @param ctx the parse tree
     */
    void enterName(UIEntryGrammar.NameContext ctx);

    /**
     * Exit a parse tree produced by {@link UIEntryGrammar#name}.
     *
     * @param ctx the parse tree
     */
    void exitName(UIEntryGrammar.NameContext ctx);

    /**
     * Enter a parse tree produced by {@link UIEntryGrammar#parameter}.
     *
     * @param ctx the parse tree
     */
    void enterParameter(UIEntryGrammar.ParameterContext ctx);

    /**
     * Exit a parse tree produced by {@link UIEntryGrammar#parameter}.
     *
     * @param ctx the parse tree
     */
    void exitParameter(UIEntryGrammar.ParameterContext ctx);

    /**
     * Enter a parse tree produced by {@link UIEntryGrammar#renderer}.
     *
     * @param ctx the parse tree
     */
    void enterRenderer(UIEntryGrammar.RendererContext ctx);

    /**
     * Exit a parse tree produced by {@link UIEntryGrammar#renderer}.
     *
     * @param ctx the parse tree
     */
    void exitRenderer(UIEntryGrammar.RendererContext ctx);

    /**
     * Enter a parse tree produced by {@link UIEntryGrammar#combine}.
     *
     * @param ctx the parse tree
     */
    void enterCombine(UIEntryGrammar.CombineContext ctx);

    /**
     * Exit a parse tree produced by {@link UIEntryGrammar#combine}.
     *
     * @param ctx the parse tree
     */
    void exitCombine(UIEntryGrammar.CombineContext ctx);

    /**
     * Enter a parse tree produced by {@link UIEntryGrammar#priority}.
     *
     * @param ctx the parse tree
     */
    void enterPriority(UIEntryGrammar.PriorityContext ctx);

    /**
     * Exit a parse tree produced by {@link UIEntryGrammar#priority}.
     *
     * @param ctx the parse tree
     */
    void exitPriority(UIEntryGrammar.PriorityContext ctx);

    /**
     * Enter a parse tree produced by {@link UIEntryGrammar#category}.
     *
     * @param ctx the parse tree
     */
    void enterCategory(UIEntryGrammar.CategoryContext ctx);

    /**
     * Exit a parse tree produced by {@link UIEntryGrammar#category}.
     *
     * @param ctx the parse tree
     */
    void exitCategory(UIEntryGrammar.CategoryContext ctx);

    /**
     * Enter a parse tree produced by {@link UIEntryGrammar#flags}.
     *
     * @param ctx the parse tree
     */
    void enterFlags(UIEntryGrammar.FlagsContext ctx);

    /**
     * Exit a parse tree produced by {@link UIEntryGrammar#flags}.
     *
     * @param ctx the parse tree
     */
    void exitFlags(UIEntryGrammar.FlagsContext ctx);

    /**
     * Enter a parse tree produced by {@link UIEntryGrammar#desc}.
     *
     * @param ctx the parse tree
     */
    void enterDesc(UIEntryGrammar.DescContext ctx);

    /**
     * Exit a parse tree produced by {@link UIEntryGrammar#desc}.
     *
     * @param ctx the parse tree
     */
    void exitDesc(UIEntryGrammar.DescContext ctx);

    /**
     * Enter a parse tree produced by {@link UIEntryGrammar#label}.
     *
     * @param ctx the parse tree
     */
    void enterLabel(UIEntryGrammar.LabelContext ctx);

    /**
     * Exit a parse tree produced by {@link UIEntryGrammar#label}.
     *
     * @param ctx the parse tree
     */
    void exitLabel(UIEntryGrammar.LabelContext ctx);

    /**
     * Enter a parse tree produced by {@link UIEntryGrammar#label5}.
     *
     * @param ctx the parse tree
     */
    void enterLabel5(UIEntryGrammar.Label5Context ctx);

    /**
     * Exit a parse tree produced by {@link UIEntryGrammar#label5}.
     *
     * @param ctx the parse tree
     */
    void exitLabel5(UIEntryGrammar.Label5Context ctx);

    /**
     * Enter a parse tree produced by {@link UIEntryGrammar#label2}.
     *
     * @param ctx the parse tree
     */
    void enterLabel2(UIEntryGrammar.Label2Context ctx);

    /**
     * Exit a parse tree produced by {@link UIEntryGrammar#label2}.
     *
     * @param ctx the parse tree
     */
    void exitLabel2(UIEntryGrammar.Label2Context ctx);

    /**
     * Enter a parse tree produced by {@link UIEntryGrammar#template}.
     *
     * @param ctx the parse tree
     */
    void enterTemplate(UIEntryGrammar.TemplateContext ctx);

    /**
     * Exit a parse tree produced by {@link UIEntryGrammar#template}.
     *
     * @param ctx the parse tree
     */
    void exitTemplate(UIEntryGrammar.TemplateContext ctx);

    /**
     * Enter a parse tree produced by {@link UIEntryGrammar#uiEntry}.
     *
     * @param ctx the parse tree
     */
    void enterUiEntry(UIEntryGrammar.UiEntryContext ctx);

    /**
     * Exit a parse tree produced by {@link UIEntryGrammar#uiEntry}.
     *
     * @param ctx the parse tree
     */
    void exitUiEntry(UIEntryGrammar.UiEntryContext ctx);

    /**
     * Enter a parse tree produced by {@link UIEntryGrammar#file}.
     *
     * @param ctx the parse tree
     */
    void enterFile(UIEntryGrammar.FileContext ctx);

    /**
     * Exit a parse tree produced by {@link UIEntryGrammar#file}.
     *
     * @param ctx the parse tree
     */
    void exitFile(UIEntryGrammar.FileContext ctx);
}