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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/io/parsers/antlr/grammars/Terrain.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.io.parsers.antlr.terrainfeature;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link TerrainParser}.
 */
public interface TerrainListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link TerrainParser#code}.
	 *
	 * @param ctx the parse tree
	 */
	void enterCode(TerrainParser.CodeContext ctx);

	/**
	 * Exit a parse tree produced by {@link TerrainParser#code}.
	 *
	 * @param ctx the parse tree
	 */
	void exitCode(TerrainParser.CodeContext ctx);

	/**
	 * Enter a parse tree produced by {@link TerrainParser#name}.
	 *
	 * @param ctx the parse tree
	 */
	void enterName(TerrainParser.NameContext ctx);

	/**
	 * Exit a parse tree produced by {@link TerrainParser#name}.
	 *
	 * @param ctx the parse tree
	 */
	void exitName(TerrainParser.NameContext ctx);

	/**
	 * Enter a parse tree produced by {@link TerrainParser#graphics}.
	 *
	 * @param ctx the parse tree
	 */
	void enterGraphics(TerrainParser.GraphicsContext ctx);

	/**
	 * Exit a parse tree produced by {@link TerrainParser#graphics}.
	 *
	 * @param ctx the parse tree
	 */
	void exitGraphics(TerrainParser.GraphicsContext ctx);

	/**
	 * Enter a parse tree produced by {@link TerrainParser#mimic}.
	 *
	 * @param ctx the parse tree
	 */
	void enterMimic(TerrainParser.MimicContext ctx);

	/**
	 * Exit a parse tree produced by {@link TerrainParser#mimic}.
	 *
	 * @param ctx the parse tree
	 */
	void exitMimic(TerrainParser.MimicContext ctx);

	/**
	 * Enter a parse tree produced by {@link TerrainParser#priority}.
	 *
	 * @param ctx the parse tree
	 */
	void enterPriority(TerrainParser.PriorityContext ctx);

	/**
	 * Exit a parse tree produced by {@link TerrainParser#priority}.
	 *
	 * @param ctx the parse tree
	 */
	void exitPriority(TerrainParser.PriorityContext ctx);

	/**
	 * Enter a parse tree produced by {@link TerrainParser#flags}.
	 *
	 * @param ctx the parse tree
	 */
	void enterFlags(TerrainParser.FlagsContext ctx);

	/**
	 * Exit a parse tree produced by {@link TerrainParser#flags}.
	 *
	 * @param ctx the parse tree
	 */
	void exitFlags(TerrainParser.FlagsContext ctx);

	/**
	 * Enter a parse tree produced by {@link TerrainParser#walk_msg}.
	 *
	 * @param ctx the parse tree
	 */
	void enterWalk_msg(TerrainParser.Walk_msgContext ctx);

	/**
	 * Exit a parse tree produced by {@link TerrainParser#walk_msg}.
	 *
	 * @param ctx the parse tree
	 */
	void exitWalk_msg(TerrainParser.Walk_msgContext ctx);

	/**
	 * Enter a parse tree produced by {@link TerrainParser#run_msg}.
	 *
	 * @param ctx the parse tree
	 */
	void enterRun_msg(TerrainParser.Run_msgContext ctx);

	/**
	 * Exit a parse tree produced by {@link TerrainParser#run_msg}.
	 *
	 * @param ctx the parse tree
	 */
	void exitRun_msg(TerrainParser.Run_msgContext ctx);

	/**
	 * Enter a parse tree produced by {@link TerrainParser#hurt_msg}.
	 *
	 * @param ctx the parse tree
	 */
	void enterHurt_msg(TerrainParser.Hurt_msgContext ctx);

	/**
	 * Exit a parse tree produced by {@link TerrainParser#hurt_msg}.
	 *
	 * @param ctx the parse tree
	 */
	void exitHurt_msg(TerrainParser.Hurt_msgContext ctx);

	/**
	 * Enter a parse tree produced by {@link TerrainParser#die_msg}.
	 *
	 * @param ctx the parse tree
	 */
	void enterDie_msg(TerrainParser.Die_msgContext ctx);

	/**
	 * Exit a parse tree produced by {@link TerrainParser#die_msg}.
	 *
	 * @param ctx the parse tree
	 */
	void exitDie_msg(TerrainParser.Die_msgContext ctx);

	/**
	 * Enter a parse tree produced by {@link TerrainParser#confused_msg}.
	 *
	 * @param ctx the parse tree
	 */
	void enterConfused_msg(TerrainParser.Confused_msgContext ctx);

	/**
	 * Exit a parse tree produced by {@link TerrainParser#confused_msg}.
	 *
	 * @param ctx the parse tree
	 */
	void exitConfused_msg(TerrainParser.Confused_msgContext ctx);

	/**
	 * Enter a parse tree produced by {@link TerrainParser#look_prefix}.
	 *
	 * @param ctx the parse tree
	 */
	void enterLook_prefix(TerrainParser.Look_prefixContext ctx);

	/**
	 * Exit a parse tree produced by {@link TerrainParser#look_prefix}.
	 *
	 * @param ctx the parse tree
	 */
	void exitLook_prefix(TerrainParser.Look_prefixContext ctx);

	/**
	 * Enter a parse tree produced by {@link TerrainParser#look_in_preposition}.
	 *
	 * @param ctx the parse tree
	 */
	void enterLook_in_preposition(TerrainParser.Look_in_prepositionContext ctx);

	/**
	 * Exit a parse tree produced by {@link TerrainParser#look_in_preposition}.
	 *
	 * @param ctx the parse tree
	 */
	void exitLook_in_preposition(TerrainParser.Look_in_prepositionContext ctx);

	/**
	 * Enter a parse tree produced by {@link TerrainParser#resist_flag}.
	 *
	 * @param ctx the parse tree
	 */
	void enterResist_flag(TerrainParser.Resist_flagContext ctx);

	/**
	 * Exit a parse tree produced by {@link TerrainParser#resist_flag}.
	 *
	 * @param ctx the parse tree
	 */
	void exitResist_flag(TerrainParser.Resist_flagContext ctx);

	/**
	 * Enter a parse tree produced by {@link TerrainParser#desc}.
	 *
	 * @param ctx the parse tree
	 */
	void enterDesc(TerrainParser.DescContext ctx);

	/**
	 * Exit a parse tree produced by {@link TerrainParser#desc}.
	 *
	 * @param ctx the parse tree
	 */
	void exitDesc(TerrainParser.DescContext ctx);

	/**
	 * Enter a parse tree produced by {@link TerrainParser#digging}.
	 *
	 * @param ctx the parse tree
	 */
	void enterDigging(TerrainParser.DiggingContext ctx);

	/**
	 * Exit a parse tree produced by {@link TerrainParser#digging}.
	 *
	 * @param ctx the parse tree
	 */
	void exitDigging(TerrainParser.DiggingContext ctx);

	/**
	 * Enter a parse tree produced by {@link TerrainParser#feature}.
	 *
	 * @param ctx the parse tree
	 */
	void enterFeature(TerrainParser.FeatureContext ctx);

	/**
	 * Exit a parse tree produced by {@link TerrainParser#feature}.
	 *
	 * @param ctx the parse tree
	 */
	void exitFeature(TerrainParser.FeatureContext ctx);

	/**
	 * Enter a parse tree produced by {@link TerrainParser#features}.
	 *
	 * @param ctx the parse tree
	 */
	void enterFeatures(TerrainParser.FeaturesContext ctx);

	/**
	 * Exit a parse tree produced by {@link TerrainParser#features}.
	 *
	 * @param ctx the parse tree
	 */
	void exitFeatures(TerrainParser.FeaturesContext ctx);
}