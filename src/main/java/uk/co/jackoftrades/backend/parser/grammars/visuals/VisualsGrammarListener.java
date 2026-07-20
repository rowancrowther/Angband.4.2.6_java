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

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link VisualsGrammar}.
 */
public interface VisualsGrammarListener extends ParseTreeListener {
    /**
     * Enter a parse tree produced by {@link VisualsGrammar#flicker}.
     *
     * @param ctx the parse tree
     */
    void enterFlicker(VisualsGrammar.FlickerContext ctx);

    /**
     * Exit a parse tree produced by {@link VisualsGrammar#flicker}.
     *
     * @param ctx the parse tree
     */
    void exitFlicker(VisualsGrammar.FlickerContext ctx);

    /**
     * Enter a parse tree produced by {@link VisualsGrammar#flickerColour}.
     *
     * @param ctx the parse tree
     */
    void enterFlickerColour(VisualsGrammar.FlickerColourContext ctx);

    /**
     * Exit a parse tree produced by {@link VisualsGrammar#flickerColour}.
     *
     * @param ctx the parse tree
     */
    void exitFlickerColour(VisualsGrammar.FlickerColourContext ctx);

    /**
     * Enter a parse tree produced by {@link VisualsGrammar#flickerBlock}.
     *
     * @param ctx the parse tree
     */
    void enterFlickerBlock(VisualsGrammar.FlickerBlockContext ctx);

    /**
     * Exit a parse tree produced by {@link VisualsGrammar#flickerBlock}.
     *
     * @param ctx the parse tree
     */
    void exitFlickerBlock(VisualsGrammar.FlickerBlockContext ctx);

    /**
     * Enter a parse tree produced by {@link VisualsGrammar#cycle}.
     *
     * @param ctx the parse tree
     */
    void enterCycle(VisualsGrammar.CycleContext ctx);

    /**
     * Exit a parse tree produced by {@link VisualsGrammar#cycle}.
     *
     * @param ctx the parse tree
     */
    void exitCycle(VisualsGrammar.CycleContext ctx);

    /**
     * Enter a parse tree produced by {@link VisualsGrammar#cycleColour}.
     *
     * @param ctx the parse tree
     */
    void enterCycleColour(VisualsGrammar.CycleColourContext ctx);

    /**
     * Exit a parse tree produced by {@link VisualsGrammar#cycleColour}.
     *
     * @param ctx the parse tree
     */
    void exitCycleColour(VisualsGrammar.CycleColourContext ctx);

    /**
     * Enter a parse tree produced by {@link VisualsGrammar#cycleBlock}.
     *
     * @param ctx the parse tree
     */
    void enterCycleBlock(VisualsGrammar.CycleBlockContext ctx);

    /**
     * Exit a parse tree produced by {@link VisualsGrammar#cycleBlock}.
     *
     * @param ctx the parse tree
     */
    void exitCycleBlock(VisualsGrammar.CycleBlockContext ctx);

    /**
     * Enter a parse tree produced by {@link VisualsGrammar#file}.
     *
     * @param ctx the parse tree
     */
    void enterFile(VisualsGrammar.FileContext ctx);

    /**
     * Exit a parse tree produced by {@link VisualsGrammar#file}.
     *
     * @param ctx the parse tree
     */
    void exitFile(VisualsGrammar.FileContext ctx);
}