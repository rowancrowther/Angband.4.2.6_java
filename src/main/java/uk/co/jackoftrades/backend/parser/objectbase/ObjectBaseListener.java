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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/parser/grammars/ObjectBase.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.objectbase;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link ObjectBaseParser}.
 */
public interface ObjectBaseListener extends ParseTreeListener {
    /**
     * Enter a parse tree produced by {@link ObjectBaseParser#default_value}.
     *
     * @param ctx the parse tree
     */
    void enterDefault_value(ObjectBaseParser.Default_valueContext ctx);

    /**
     * Exit a parse tree produced by {@link ObjectBaseParser#default_value}.
     *
     * @param ctx the parse tree
     */
    void exitDefault_value(ObjectBaseParser.Default_valueContext ctx);

    /**
     * Enter a parse tree produced by {@link ObjectBaseParser#name}.
     *
     * @param ctx the parse tree
     */
    void enterName(ObjectBaseParser.NameContext ctx);

    /**
     * Exit a parse tree produced by {@link ObjectBaseParser#name}.
     *
     * @param ctx the parse tree
     */
    void exitName(ObjectBaseParser.NameContext ctx);

    /**
     * Enter a parse tree produced by {@link ObjectBaseParser#graphics}.
     *
     * @param ctx the parse tree
     */
    void enterGraphics(ObjectBaseParser.GraphicsContext ctx);

    /**
     * Exit a parse tree produced by {@link ObjectBaseParser#graphics}.
     *
     * @param ctx the parse tree
     */
    void exitGraphics(ObjectBaseParser.GraphicsContext ctx);

    /**
     * Enter a parse tree produced by {@link ObjectBaseParser#break_chance}.
     *
     * @param ctx the parse tree
     */
    void enterBreak_chance(ObjectBaseParser.Break_chanceContext ctx);

    /**
     * Exit a parse tree produced by {@link ObjectBaseParser#break_chance}.
     *
     * @param ctx the parse tree
     */
    void exitBreak_chance(ObjectBaseParser.Break_chanceContext ctx);

    /**
     * Enter a parse tree produced by {@link ObjectBaseParser#max_stack}.
     *
     * @param ctx the parse tree
     */
    void enterMax_stack(ObjectBaseParser.Max_stackContext ctx);

    /**
     * Exit a parse tree produced by {@link ObjectBaseParser#max_stack}.
     *
     * @param ctx the parse tree
     */
    void exitMax_stack(ObjectBaseParser.Max_stackContext ctx);

    /**
     * Enter a parse tree produced by {@link ObjectBaseParser#flags}.
     *
     * @param ctx the parse tree
     */
    void enterFlags(ObjectBaseParser.FlagsContext ctx);

    /**
     * Exit a parse tree produced by {@link ObjectBaseParser#flags}.
     *
     * @param ctx the parse tree
     */
    void exitFlags(ObjectBaseParser.FlagsContext ctx);

    /**
     * Enter a parse tree produced by {@link ObjectBaseParser#object_base}.
     *
     * @param ctx the parse tree
     */
    void enterObject_base(ObjectBaseParser.Object_baseContext ctx);

    /**
     * Exit a parse tree produced by {@link ObjectBaseParser#object_base}.
     *
     * @param ctx the parse tree
     */
    void exitObject_base(ObjectBaseParser.Object_baseContext ctx);

    /**
     * Enter a parse tree produced by {@link ObjectBaseParser#file}.
     *
     * @param ctx the parse tree
     */
    void enterFile(ObjectBaseParser.FileContext ctx);

    /**
     * Exit a parse tree produced by {@link ObjectBaseParser#file}.
     *
     * @param ctx the parse tree
     */
    void exitFile(ObjectBaseParser.FileContext ctx);
}