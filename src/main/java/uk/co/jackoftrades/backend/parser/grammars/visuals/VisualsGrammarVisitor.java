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
// Generated from VisualsGrammar.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.grammars.visuals;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link VisualsGrammar}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 *            operations with no return type.
 */
public interface VisualsGrammarVisitor<T> extends ParseTreeVisitor<T> {
    /**
     * Visit a parse tree produced by {@link VisualsGrammar#flicker}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFlicker(VisualsGrammar.FlickerContext ctx);

    /**
     * Visit a parse tree produced by {@link VisualsGrammar#flickerColour}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFlickerColour(VisualsGrammar.FlickerColourContext ctx);

    /**
     * Visit a parse tree produced by {@link VisualsGrammar#flickerBlock}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFlickerBlock(VisualsGrammar.FlickerBlockContext ctx);

    /**
     * Visit a parse tree produced by {@link VisualsGrammar#cycle}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitCycle(VisualsGrammar.CycleContext ctx);

    /**
     * Visit a parse tree produced by {@link VisualsGrammar#cycleColour}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitCycleColour(VisualsGrammar.CycleColourContext ctx);

    /**
     * Visit a parse tree produced by {@link VisualsGrammar#cycleBlock}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitCycleBlock(VisualsGrammar.CycleBlockContext ctx);

    /**
     * Visit a parse tree produced by {@link VisualsGrammar#file}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFile(VisualsGrammar.FileContext ctx);
}