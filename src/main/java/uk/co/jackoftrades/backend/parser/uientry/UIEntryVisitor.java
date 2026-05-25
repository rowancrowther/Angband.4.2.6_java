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

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link UIEntryParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 *            operations with no return type.
 */
public interface UIEntryVisitor<T> extends ParseTreeVisitor<T> {
    /**
     * Visit a parse tree produced by {@link UIEntryParser#tag}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitTag(UIEntryParser.TagContext ctx);

    /**
     * Visit a parse tree produced by {@link UIEntryParser#name}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitName(UIEntryParser.NameContext ctx);

    /**
     * Visit a parse tree produced by {@link UIEntryParser#parameter}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitParameter(UIEntryParser.ParameterContext ctx);

    /**
     * Visit a parse tree produced by {@link UIEntryParser#renderer}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitRenderer(UIEntryParser.RendererContext ctx);

    /**
     * Visit a parse tree produced by {@link UIEntryParser#combine}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitCombine(UIEntryParser.CombineContext ctx);

    /**
     * Visit a parse tree produced by {@link UIEntryParser#priority}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitPriority(UIEntryParser.PriorityContext ctx);

    /**
     * Visit a parse tree produced by {@link UIEntryParser#category}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitCategory(UIEntryParser.CategoryContext ctx);

    /**
     * Visit a parse tree produced by {@link UIEntryParser#flags}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFlags(UIEntryParser.FlagsContext ctx);

    /**
     * Visit a parse tree produced by {@link UIEntryParser#desc}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitDesc(UIEntryParser.DescContext ctx);

    /**
     * Visit a parse tree produced by {@link UIEntryParser#label}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitLabel(UIEntryParser.LabelContext ctx);

    /**
     * Visit a parse tree produced by {@link UIEntryParser#label5}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitLabel5(UIEntryParser.Label5Context ctx);

    /**
     * Visit a parse tree produced by {@link UIEntryParser#label2}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitLabel2(UIEntryParser.Label2Context ctx);

    /**
     * Visit a parse tree produced by {@link UIEntryParser#template}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitTemplate(UIEntryParser.TemplateContext ctx);

    /**
     * Visit a parse tree produced by {@link UIEntryParser#uiEntry}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitUiEntry(UIEntryParser.UiEntryContext ctx);

    /**
     * Visit a parse tree produced by {@link UIEntryParser#file}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFile(UIEntryParser.FileContext ctx);
}