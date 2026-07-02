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
// Generated from WorldGrammar.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.grammars.world;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link WorldGrammar}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface WorldGrammarVisitor<T> extends ParseTreeVisitor<T> {
    /**
     * Visit a parse tree produced by {@link WorldGrammar#recordCount}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitRecordCount(WorldGrammar.RecordCountContext ctx);

    /**
     * Visit a parse tree produced by {@link WorldGrammar#levelNum}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitLevelNum(WorldGrammar.LevelNumContext ctx);

    /**
     * Visit a parse tree produced by {@link WorldGrammar#levelName}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitLevelName(WorldGrammar.LevelNameContext ctx);

    /**
     * Visit a parse tree produced by {@link WorldGrammar#upAndDown}.
	 * @param ctx the parse tree
     * @return the visitor result
     */
	T visitUpAndDown(WorldGrammar.UpAndDownContext ctx);

    /**
     * Visit a parse tree produced by {@link WorldGrammar#line}.
	 * @param ctx the parse tree
     * @return the visitor result
     */
    T visitLine(WorldGrammar.LineContext ctx);

    /**
     * Visit a parse tree produced by {@link WorldGrammar#file}.
	 * @param ctx the parse tree
     * @return the visitor result
	 */
	T visitFile(WorldGrammar.FileContext ctx);
}