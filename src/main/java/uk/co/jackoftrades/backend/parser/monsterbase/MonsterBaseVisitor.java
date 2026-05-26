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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/parser/grammars/MonsterBase.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.monsterbase;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link MonsterBaseParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 *            operations with no return type.
 */
public interface MonsterBaseVisitor<T> extends ParseTreeVisitor<T> {
    /**
     * Visit a parse tree produced by {@link MonsterBaseParser#name}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitName(MonsterBaseParser.NameContext ctx);

    /**
     * Visit a parse tree produced by {@link MonsterBaseParser#glyph}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitGlyph(MonsterBaseParser.GlyphContext ctx);

    /**
     * Visit a parse tree produced by {@link MonsterBaseParser#pain}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitPain(MonsterBaseParser.PainContext ctx);

    /**
     * Visit a parse tree produced by {@link MonsterBaseParser#flags}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFlags(MonsterBaseParser.FlagsContext ctx);

    /**
     * Visit a parse tree produced by {@link MonsterBaseParser#desc}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitDesc(MonsterBaseParser.DescContext ctx);

    /**
     * Visit a parse tree produced by {@link MonsterBaseParser#monsterBase}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitMonsterBase(MonsterBaseParser.MonsterBaseContext ctx);

    /**
     * Visit a parse tree produced by {@link MonsterBaseParser#file}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFile(MonsterBaseParser.FileContext ctx);
}