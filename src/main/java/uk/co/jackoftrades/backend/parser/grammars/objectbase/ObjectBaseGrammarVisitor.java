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

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link ObjectBaseGrammar}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 *            operations with no return type.
 */
public interface ObjectBaseGrammarVisitor<T> extends ParseTreeVisitor<T> {
    /**
     * Visit a parse tree produced by {@link ObjectBaseGrammar#recordCount}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitRecordCount(ObjectBaseGrammar.RecordCountContext ctx);

    /**
     * Visit a parse tree produced by {@link ObjectBaseGrammar#default_value}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitDefault_value(ObjectBaseGrammar.Default_valueContext ctx);

    /**
     * Visit a parse tree produced by {@link ObjectBaseGrammar#name}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitName(ObjectBaseGrammar.NameContext ctx);

    /**
     * Visit a parse tree produced by {@link ObjectBaseGrammar#graphics}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitGraphics(ObjectBaseGrammar.GraphicsContext ctx);

    /**
     * Visit a parse tree produced by {@link ObjectBaseGrammar#break_chance}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitBreak_chance(ObjectBaseGrammar.Break_chanceContext ctx);

    /**
     * Visit a parse tree produced by {@link ObjectBaseGrammar#max_stack}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitMax_stack(ObjectBaseGrammar.Max_stackContext ctx);

    /**
     * Visit a parse tree produced by {@link ObjectBaseGrammar#flags}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFlags(ObjectBaseGrammar.FlagsContext ctx);

    /**
     * Visit a parse tree produced by {@link ObjectBaseGrammar#object_base}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitObject_base(ObjectBaseGrammar.Object_baseContext ctx);

    /**
     * Visit a parse tree produced by {@link ObjectBaseGrammar#file}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFile(ObjectBaseGrammar.FileContext ctx);
}