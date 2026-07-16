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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/scratch/antlr_artifact_check/src/uk/co/jackoftrades/backend/parser/grammars/Visuals.g4 by ANTLR 4.13.2

    import uk.co.jackoftrades.frontend.colour.VisualsColourCycle;
    import uk.co.jackoftrades.frontend.colour.VisualsCycleGroup;
    import uk.co.jackoftrades.frontend.colour.VisualsCycler;
    import uk.co.jackoftrades.frontend.colour.enums.ColourType;

    import java.util.Map;
    import java.util.List;
    import java.util.HashMap;
    import java.util.ArrayList;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link VisualsParser}.
 */
public interface VisualsListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link VisualsParser#flicker}.
	 * @param ctx the parse tree
	 */
	void enterFlicker(VisualsParser.FlickerContext ctx);
	/**
	 * Exit a parse tree produced by {@link VisualsParser#flicker}.
	 * @param ctx the parse tree
	 */
	void exitFlicker(VisualsParser.FlickerContext ctx);
	/**
	 * Enter a parse tree produced by {@link VisualsParser#flickerColour}.
	 * @param ctx the parse tree
	 */
	void enterFlickerColour(VisualsParser.FlickerColourContext ctx);
	/**
	 * Exit a parse tree produced by {@link VisualsParser#flickerColour}.
	 * @param ctx the parse tree
	 */
	void exitFlickerColour(VisualsParser.FlickerColourContext ctx);
	/**
	 * Enter a parse tree produced by {@link VisualsParser#flickerBlock}.
	 * @param ctx the parse tree
	 */
	void enterFlickerBlock(VisualsParser.FlickerBlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link VisualsParser#flickerBlock}.
	 * @param ctx the parse tree
	 */
	void exitFlickerBlock(VisualsParser.FlickerBlockContext ctx);
	/**
	 * Enter a parse tree produced by {@link VisualsParser#cycle}.
	 * @param ctx the parse tree
	 */
	void enterCycle(VisualsParser.CycleContext ctx);
	/**
	 * Exit a parse tree produced by {@link VisualsParser#cycle}.
	 * @param ctx the parse tree
	 */
	void exitCycle(VisualsParser.CycleContext ctx);
	/**
	 * Enter a parse tree produced by {@link VisualsParser#cycleColour}.
	 * @param ctx the parse tree
	 */
	void enterCycleColour(VisualsParser.CycleColourContext ctx);
	/**
	 * Exit a parse tree produced by {@link VisualsParser#cycleColour}.
	 * @param ctx the parse tree
	 */
	void exitCycleColour(VisualsParser.CycleColourContext ctx);
	/**
	 * Enter a parse tree produced by {@link VisualsParser#cycleBlock}.
	 * @param ctx the parse tree
	 */
	void enterCycleBlock(VisualsParser.CycleBlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link VisualsParser#cycleBlock}.
	 * @param ctx the parse tree
	 */
	void exitCycleBlock(VisualsParser.CycleBlockContext ctx);
	/**
	 * Enter a parse tree produced by {@link VisualsParser#file}.
	 * @param ctx the parse tree
	 */
	void enterFile(VisualsParser.FileContext ctx);
	/**
	 * Exit a parse tree produced by {@link VisualsParser#file}.
	 * @param ctx the parse tree
	 */
	void exitFile(VisualsParser.FileContext ctx);
}