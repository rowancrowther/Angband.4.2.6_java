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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/parser/grammars/World.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.world;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link WorldParser}.
 */
public interface WorldListener extends ParseTreeListener {
    /**
     * Enter a parse tree produced by {@link WorldParser#levelNum}.
     *
     * @param ctx the parse tree
     */
    void enterLevelNum(WorldParser.LevelNumContext ctx);

    /**
     * Exit a parse tree produced by {@link WorldParser#levelNum}.
     *
     * @param ctx the parse tree
     */
    void exitLevelNum(WorldParser.LevelNumContext ctx);

    /**
     * Enter a parse tree produced by {@link WorldParser#levelName}.
     *
     * @param ctx the parse tree
     */
    void enterLevelName(WorldParser.LevelNameContext ctx);

    /**
     * Exit a parse tree produced by {@link WorldParser#levelName}.
     *
     * @param ctx the parse tree
     */
    void exitLevelName(WorldParser.LevelNameContext ctx);

    /**
     * Enter a parse tree produced by {@link WorldParser#upAndDown}.
     *
     * @param ctx the parse tree
     */
    void enterUpAndDown(WorldParser.UpAndDownContext ctx);

    /**
     * Exit a parse tree produced by {@link WorldParser#upAndDown}.
     *
     * @param ctx the parse tree
     */
    void exitUpAndDown(WorldParser.UpAndDownContext ctx);

    /**
     * Enter a parse tree produced by {@link WorldParser#line}.
     *
     * @param ctx the parse tree
     */
    void enterLine(WorldParser.LineContext ctx);

    /**
     * Exit a parse tree produced by {@link WorldParser#line}.
     *
     * @param ctx the parse tree
     */
    void exitLine(WorldParser.LineContext ctx);

    /**
     * Enter a parse tree produced by {@link WorldParser#file}.
     *
     * @param ctx the parse tree
     */
    void enterFile(WorldParser.FileContext ctx);

    /**
     * Exit a parse tree produced by {@link WorldParser#file}.
     *
     * @param ctx the parse tree
     */
    void exitFile(WorldParser.FileContext ctx);
}