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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/parser/grammars/GameConstants.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.gameconstants;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link GameConstantsParser}.
 */
public interface GameConstantsListener extends ParseTreeListener {
    /**
     * Enter a parse tree produced by {@link GameConstantsParser#section}.
     *
     * @param ctx the parse tree
     */
    void enterSection(GameConstantsParser.SectionContext ctx);

    /**
     * Exit a parse tree produced by {@link GameConstantsParser#section}.
     *
     * @param ctx the parse tree
     */
    void exitSection(GameConstantsParser.SectionContext ctx);

    /**
     * Enter a parse tree produced by {@link GameConstantsParser#furtherValue}.
     *
     * @param ctx the parse tree
     */
    void enterFurtherValue(GameConstantsParser.FurtherValueContext ctx);

    /**
     * Exit a parse tree produced by {@link GameConstantsParser#furtherValue}.
     *
     * @param ctx the parse tree
     */
    void exitFurtherValue(GameConstantsParser.FurtherValueContext ctx);

    /**
     * Enter a parse tree produced by {@link GameConstantsParser#multiValue}.
     *
     * @param ctx the parse tree
     */
    void enterMultiValue(GameConstantsParser.MultiValueContext ctx);

    /**
     * Exit a parse tree produced by {@link GameConstantsParser#multiValue}.
     *
     * @param ctx the parse tree
     */
    void exitMultiValue(GameConstantsParser.MultiValueContext ctx);

    /**
     * Enter a parse tree produced by {@link GameConstantsParser#line}.
     *
     * @param ctx the parse tree
     */
    void enterLine(GameConstantsParser.LineContext ctx);

    /**
     * Exit a parse tree produced by {@link GameConstantsParser#line}.
     *
     * @param ctx the parse tree
     */
    void exitLine(GameConstantsParser.LineContext ctx);

    /**
     * Enter a parse tree produced by {@link GameConstantsParser#file}.
     *
     * @param ctx the parse tree
     */
    void enterFile(GameConstantsParser.FileContext ctx);

    /**
     * Exit a parse tree produced by {@link GameConstantsParser#file}.
     *
     * @param ctx the parse tree
     */
    void exitFile(GameConstantsParser.FileContext ctx);
}