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

// Generated from Worlds.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.grammars.world;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link Worlds}.
 */
public interface WorldsListener extends ParseTreeListener {
    /**
     * Enter a parse tree produced by {@link Worlds#recordCount}.
     *
     * @param ctx the parse tree
     */
    void enterRecordCount(Worlds.RecordCountContext ctx);

    /**
     * Exit a parse tree produced by {@link Worlds#recordCount}.
     *
     * @param ctx the parse tree
     */
    void exitRecordCount(Worlds.RecordCountContext ctx);

    /**
     * Enter a parse tree produced by {@link Worlds#levelNum}.
     *
     * @param ctx the parse tree
     */
    void enterLevelNum(Worlds.LevelNumContext ctx);

    /**
     * Exit a parse tree produced by {@link Worlds#levelNum}.
     *
     * @param ctx the parse tree
     */
    void exitLevelNum(Worlds.LevelNumContext ctx);

    /**
     * Enter a parse tree produced by {@link Worlds#levelName}.
     *
     * @param ctx the parse tree
     */
    void enterLevelName(Worlds.LevelNameContext ctx);

    /**
     * Exit a parse tree produced by {@link Worlds#levelName}.
     *
     * @param ctx the parse tree
     */
    void exitLevelName(Worlds.LevelNameContext ctx);

    /**
     * Enter a parse tree produced by {@link Worlds#upAndDown}.
     *
     * @param ctx the parse tree
     */
    void enterUpAndDown(Worlds.UpAndDownContext ctx);

    /**
     * Exit a parse tree produced by {@link Worlds#upAndDown}.
     *
     * @param ctx the parse tree
     */
    void exitUpAndDown(Worlds.UpAndDownContext ctx);

    /**
     * Enter a parse tree produced by {@link Worlds#line}.
     *
     * @param ctx the parse tree
     */
    void enterLine(Worlds.LineContext ctx);

    /**
     * Exit a parse tree produced by {@link Worlds#line}.
     *
     * @param ctx the parse tree
     */
    void exitLine(Worlds.LineContext ctx);

    /**
     * Enter a parse tree produced by {@link Worlds#file}.
     *
     * @param ctx the parse tree
     */
    void enterFile(Worlds.FileContext ctx);

    /**
     * Exit a parse tree produced by {@link Worlds#file}.
     *
     * @param ctx the parse tree
     */
    void exitFile(Worlds.FileContext ctx);
}