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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/parser/grammars/ObjectBaseGrammar.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.grammars.objectbase;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link ObjectBaseGrammar}.
 */
public interface ObjectBaseGrammarListener extends ParseTreeListener {
    /**
     * Enter a parse tree produced by {@link ObjectBaseGrammar#recordCount}.
     *
     * @param ctx the parse tree
     */
    void enterRecordCount(ObjectBaseGrammar.RecordCountContext ctx);

    /**
     * Exit a parse tree produced by {@link ObjectBaseGrammar#recordCount}.
     *
     * @param ctx the parse tree
     */
    void exitRecordCount(ObjectBaseGrammar.RecordCountContext ctx);

    /**
     * Enter a parse tree produced by {@link ObjectBaseGrammar#default_value}.
     *
     * @param ctx the parse tree
     */
    void enterDefault_value(ObjectBaseGrammar.Default_valueContext ctx);

    /**
     * Exit a parse tree produced by {@link ObjectBaseGrammar#default_value}.
     *
     * @param ctx the parse tree
     */
    void exitDefault_value(ObjectBaseGrammar.Default_valueContext ctx);

    /**
     * Enter a parse tree produced by {@link ObjectBaseGrammar#name}.
     *
     * @param ctx the parse tree
     */
    void enterName(ObjectBaseGrammar.NameContext ctx);

    /**
     * Exit a parse tree produced by {@link ObjectBaseGrammar#name}.
     *
     * @param ctx the parse tree
     */
    void exitName(ObjectBaseGrammar.NameContext ctx);

    /**
     * Enter a parse tree produced by {@link ObjectBaseGrammar#graphics}.
     *
     * @param ctx the parse tree
     */
    void enterGraphics(ObjectBaseGrammar.GraphicsContext ctx);

    /**
     * Exit a parse tree produced by {@link ObjectBaseGrammar#graphics}.
     *
     * @param ctx the parse tree
     */
    void exitGraphics(ObjectBaseGrammar.GraphicsContext ctx);

    /**
     * Enter a parse tree produced by {@link ObjectBaseGrammar#break_chance}.
     *
     * @param ctx the parse tree
     */
    void enterBreak_chance(ObjectBaseGrammar.Break_chanceContext ctx);

    /**
     * Exit a parse tree produced by {@link ObjectBaseGrammar#break_chance}.
     *
     * @param ctx the parse tree
     */
    void exitBreak_chance(ObjectBaseGrammar.Break_chanceContext ctx);

    /**
     * Enter a parse tree produced by {@link ObjectBaseGrammar#max_stack}.
     *
     * @param ctx the parse tree
     */
    void enterMax_stack(ObjectBaseGrammar.Max_stackContext ctx);

    /**
     * Exit a parse tree produced by {@link ObjectBaseGrammar#max_stack}.
     *
     * @param ctx the parse tree
     */
    void exitMax_stack(ObjectBaseGrammar.Max_stackContext ctx);

    /**
     * Enter a parse tree produced by {@link ObjectBaseGrammar#flags}.
     *
     * @param ctx the parse tree
     */
    void enterFlags(ObjectBaseGrammar.FlagsContext ctx);

    /**
     * Exit a parse tree produced by {@link ObjectBaseGrammar#flags}.
     *
     * @param ctx the parse tree
     */
    void exitFlags(ObjectBaseGrammar.FlagsContext ctx);

    /**
     * Enter a parse tree produced by {@link ObjectBaseGrammar#object_base}.
     *
     * @param ctx the parse tree
     */
    void enterObject_base(ObjectBaseGrammar.Object_baseContext ctx);

    /**
     * Exit a parse tree produced by {@link ObjectBaseGrammar#object_base}.
     *
     * @param ctx the parse tree
     */
    void exitObject_base(ObjectBaseGrammar.Object_baseContext ctx);

    /**
     * Enter a parse tree produced by {@link ObjectBaseGrammar#file}.
     *
     * @param ctx the parse tree
     */
    void enterFile(ObjectBaseGrammar.FileContext ctx);

    /**
     * Exit a parse tree produced by {@link ObjectBaseGrammar#file}.
     *
     * @param ctx the parse tree
     */
    void exitFile(ObjectBaseGrammar.FileContext ctx);
}