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

package uk.co.jackoftrades.backend.parser.gameconstants;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link GameConstantsParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 *            operations with no return type.
 */
public interface GameConstantsVisitor<T> extends ParseTreeVisitor<T> {
    /**
     * Visit a parse tree produced by {@link GameConstantsParser#section}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitSection(GameConstantsParser.SectionContext ctx);

    /**
     * Visit a parse tree produced by {@link GameConstantsParser#furtherValue}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFurtherValue(GameConstantsParser.FurtherValueContext ctx);

    /**
     * Visit a parse tree produced by {@link GameConstantsParser#multiValue}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitMultiValue(GameConstantsParser.MultiValueContext ctx);

    /**
     * Visit a parse tree produced by {@link GameConstantsParser#line}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitLine(GameConstantsParser.LineContext ctx);

    /**
     * Visit a parse tree produced by {@link GameConstantsParser#file}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFile(GameConstantsParser.FileContext ctx);
}