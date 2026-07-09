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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/parser/grammars/Visuals.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.visuals;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link VisualsParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 *            operations with no return type.
 */
public interface VisualsVisitor<T> extends ParseTreeVisitor<T> {
    /**
     * Visit a parse tree produced by {@link VisualsParser#flicker}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFlicker(VisualsParser.FlickerContext ctx);

    /**
     * Visit a parse tree produced by {@link VisualsParser#flickerColour}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFlickerColour(VisualsParser.FlickerColourContext ctx);

    /**
     * Visit a parse tree produced by {@link VisualsParser#flickerBlock}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFlickerBlock(VisualsParser.FlickerBlockContext ctx);

    /**
     * Visit a parse tree produced by {@link VisualsParser#cycle}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitCycle(VisualsParser.CycleContext ctx);

    /**
     * Visit a parse tree produced by {@link VisualsParser#cycleColour}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitCycleColour(VisualsParser.CycleColourContext ctx);

    /**
     * Visit a parse tree produced by {@link VisualsParser#cycleBlock}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitCycleBlock(VisualsParser.CycleBlockContext ctx);

    /**
     * Visit a parse tree produced by {@link VisualsParser#file}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFile(VisualsParser.FileContext ctx);
}