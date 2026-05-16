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

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link TerrainParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface TerrainVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link TerrainParser#code}.
	 *
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCode(TerrainParser.CodeContext ctx);

	/**
	 * Visit a parse tree produced by {@link TerrainParser#name}.
	 *
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitName(TerrainParser.NameContext ctx);

	/**
	 * Visit a parse tree produced by {@link TerrainParser#graphics}.
	 *
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGraphics(TerrainParser.GraphicsContext ctx);

	/**
	 * Visit a parse tree produced by {@link TerrainParser#mimic}.
	 *
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMimic(TerrainParser.MimicContext ctx);

	/**
	 * Visit a parse tree produced by {@link TerrainParser#priority}.
	 *
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPriority(TerrainParser.PriorityContext ctx);

	/**
	 * Visit a parse tree produced by {@link TerrainParser#flags}.
	 *
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFlags(TerrainParser.FlagsContext ctx);

	/**
	 * Visit a parse tree produced by {@link TerrainParser#walk_msg}.
	 *
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWalk_msg(TerrainParser.Walk_msgContext ctx);

	/**
	 * Visit a parse tree produced by {@link TerrainParser#run_msg}.
	 *
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRun_msg(TerrainParser.Run_msgContext ctx);

	/**
	 * Visit a parse tree produced by {@link TerrainParser#hurt_msg}.
	 *
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHurt_msg(TerrainParser.Hurt_msgContext ctx);

	/**
	 * Visit a parse tree produced by {@link TerrainParser#die_msg}.
	 *
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDie_msg(TerrainParser.Die_msgContext ctx);

	/**
	 * Visit a parse tree produced by {@link TerrainParser#confused_msg}.
	 *
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConfused_msg(TerrainParser.Confused_msgContext ctx);

	/**
	 * Visit a parse tree produced by {@link TerrainParser#look_prefix}.
	 *
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLook_prefix(TerrainParser.Look_prefixContext ctx);

	/**
	 * Visit a parse tree produced by {@link TerrainParser#look_in_preposition}.
	 *
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLook_in_preposition(TerrainParser.Look_in_prepositionContext ctx);

	/**
	 * Visit a parse tree produced by {@link TerrainParser#resist_flag}.
	 *
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitResist_flag(TerrainParser.Resist_flagContext ctx);

	/**
	 * Visit a parse tree produced by {@link TerrainParser#desc}.
	 *
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDesc(TerrainParser.DescContext ctx);

	/**
	 * Visit a parse tree produced by {@link TerrainParser#digging}.
	 *
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDigging(TerrainParser.DiggingContext ctx);

	/**
	 * Visit a parse tree produced by {@link TerrainParser#feature}.
	 *
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFeature(TerrainParser.FeatureContext ctx);

	/**
	 * Visit a parse tree produced by {@link TerrainParser#features}.
	 *
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFeatures(TerrainParser.FeaturesContext ctx);
}