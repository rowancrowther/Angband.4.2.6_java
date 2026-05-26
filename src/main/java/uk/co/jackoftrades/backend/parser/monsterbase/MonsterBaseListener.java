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

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link MonsterBaseParser}.
 */
public interface MonsterBaseListener extends ParseTreeListener {
    /**
     * Enter a parse tree produced by {@link MonsterBaseParser#name}.
     *
     * @param ctx the parse tree
     */
    void enterName(MonsterBaseParser.NameContext ctx);

    /**
     * Exit a parse tree produced by {@link MonsterBaseParser#name}.
     *
     * @param ctx the parse tree
     */
    void exitName(MonsterBaseParser.NameContext ctx);

    /**
     * Enter a parse tree produced by {@link MonsterBaseParser#glyph}.
     *
     * @param ctx the parse tree
     */
    void enterGlyph(MonsterBaseParser.GlyphContext ctx);

    /**
     * Exit a parse tree produced by {@link MonsterBaseParser#glyph}.
     *
     * @param ctx the parse tree
     */
    void exitGlyph(MonsterBaseParser.GlyphContext ctx);

    /**
     * Enter a parse tree produced by {@link MonsterBaseParser#pain}.
     *
     * @param ctx the parse tree
     */
    void enterPain(MonsterBaseParser.PainContext ctx);

    /**
     * Exit a parse tree produced by {@link MonsterBaseParser#pain}.
     *
     * @param ctx the parse tree
     */
    void exitPain(MonsterBaseParser.PainContext ctx);

    /**
     * Enter a parse tree produced by {@link MonsterBaseParser#flags}.
     *
     * @param ctx the parse tree
     */
    void enterFlags(MonsterBaseParser.FlagsContext ctx);

    /**
     * Exit a parse tree produced by {@link MonsterBaseParser#flags}.
     *
     * @param ctx the parse tree
     */
    void exitFlags(MonsterBaseParser.FlagsContext ctx);

    /**
     * Enter a parse tree produced by {@link MonsterBaseParser#desc}.
     *
     * @param ctx the parse tree
     */
    void enterDesc(MonsterBaseParser.DescContext ctx);

    /**
     * Exit a parse tree produced by {@link MonsterBaseParser#desc}.
     *
     * @param ctx the parse tree
     */
    void exitDesc(MonsterBaseParser.DescContext ctx);

    /**
     * Enter a parse tree produced by {@link MonsterBaseParser#monsterBase}.
     *
     * @param ctx the parse tree
     */
    void enterMonsterBase(MonsterBaseParser.MonsterBaseContext ctx);

    /**
     * Exit a parse tree produced by {@link MonsterBaseParser#monsterBase}.
     *
     * @param ctx the parse tree
     */
    void exitMonsterBase(MonsterBaseParser.MonsterBaseContext ctx);

    /**
     * Enter a parse tree produced by {@link MonsterBaseParser#file}.
     *
     * @param ctx the parse tree
     */
    void enterFile(MonsterBaseParser.FileContext ctx);

    /**
     * Exit a parse tree produced by {@link MonsterBaseParser#file}.
     *
     * @param ctx the parse tree
     */
    void exitFile(MonsterBaseParser.FileContext ctx);
}