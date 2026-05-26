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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/background/io/parsers/antlr/grammars/WorldReader.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.io.parsers.antlr.world;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link WorldReaderParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 *            operations with no return type.
 */
public interface WorldReaderVisitor<T> extends ParseTreeVisitor<T> {
    /**
     * Visit a parse tree produced by {@link WorldReaderParser#levelNum}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitLevelNum(WorldReaderParser.LevelNumContext ctx);

    /**
     * Visit a parse tree produced by {@link WorldReaderParser#levelName}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitLevelName(WorldReaderParser.LevelNameContext ctx);

    /**
     * Visit a parse tree produced by {@link WorldReaderParser#upAndDown}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitUpAndDown(WorldReaderParser.UpAndDownContext ctx);

    /**
     * Visit a parse tree produced by {@link WorldReaderParser#line}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitLine(WorldReaderParser.LineContext ctx);

    /**
     * Visit a parse tree produced by {@link WorldReaderParser#file}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFile(WorldReaderParser.FileContext ctx);
}