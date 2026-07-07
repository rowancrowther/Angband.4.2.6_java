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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/parser/grammars/MonsterBaseGrammar.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.grammars.monsterbase;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link MonsterBaseGrammar}.
 */
public interface MonsterBaseGrammarListener extends ParseTreeListener {
    /**
     * Enter a parse tree produced by {@link MonsterBaseGrammar#recordCount}.
     *
     * @param ctx the parse tree
     */
    void enterRecordCount(MonsterBaseGrammar.RecordCountContext ctx);

    /**
     * Exit a parse tree produced by {@link MonsterBaseGrammar#recordCount}.
     *
     * @param ctx the parse tree
     */
    void exitRecordCount(MonsterBaseGrammar.RecordCountContext ctx);

    /**
     * Enter a parse tree produced by {@link MonsterBaseGrammar#name}.
     *
     * @param ctx the parse tree
     */
    void enterName(MonsterBaseGrammar.NameContext ctx);

    /**
     * Exit a parse tree produced by {@link MonsterBaseGrammar#name}.
     *
     * @param ctx the parse tree
     */
    void exitName(MonsterBaseGrammar.NameContext ctx);

    /**
     * Enter a parse tree produced by {@link MonsterBaseGrammar#glyph}.
     *
     * @param ctx the parse tree
     */
    void enterGlyph(MonsterBaseGrammar.GlyphContext ctx);

    /**
     * Exit a parse tree produced by {@link MonsterBaseGrammar#glyph}.
     *
     * @param ctx the parse tree
     */
    void exitGlyph(MonsterBaseGrammar.GlyphContext ctx);

    /**
     * Enter a parse tree produced by {@link MonsterBaseGrammar#pain}.
     *
     * @param ctx the parse tree
     */
    void enterPain(MonsterBaseGrammar.PainContext ctx);

    /**
     * Exit a parse tree produced by {@link MonsterBaseGrammar#pain}.
     *
     * @param ctx the parse tree
     */
    void exitPain(MonsterBaseGrammar.PainContext ctx);

    /**
     * Enter a parse tree produced by {@link MonsterBaseGrammar#flags}.
     *
     * @param ctx the parse tree
     */
    void enterFlags(MonsterBaseGrammar.FlagsContext ctx);

    /**
     * Exit a parse tree produced by {@link MonsterBaseGrammar#flags}.
     *
     * @param ctx the parse tree
     */
    void exitFlags(MonsterBaseGrammar.FlagsContext ctx);

    /**
     * Enter a parse tree produced by {@link MonsterBaseGrammar#desc}.
     *
     * @param ctx the parse tree
     */
    void enterDesc(MonsterBaseGrammar.DescContext ctx);

    /**
     * Exit a parse tree produced by {@link MonsterBaseGrammar#desc}.
     *
     * @param ctx the parse tree
     */
    void exitDesc(MonsterBaseGrammar.DescContext ctx);

    /**
     * Enter a parse tree produced by {@link MonsterBaseGrammar#monsterBase}.
     *
     * @param ctx the parse tree
     */
    void enterMonsterBase(MonsterBaseGrammar.MonsterBaseContext ctx);

    /**
     * Exit a parse tree produced by {@link MonsterBaseGrammar#monsterBase}.
     *
     * @param ctx the parse tree
     */
    void exitMonsterBase(MonsterBaseGrammar.MonsterBaseContext ctx);

    /**
     * Enter a parse tree produced by {@link MonsterBaseGrammar#file}.
     *
     * @param ctx the parse tree
     */
    void enterFile(MonsterBaseGrammar.FileContext ctx);

    /**
     * Exit a parse tree produced by {@link MonsterBaseGrammar#file}.
     *
     * @param ctx the parse tree
     */
    void exitFile(MonsterBaseGrammar.FileContext ctx);
}