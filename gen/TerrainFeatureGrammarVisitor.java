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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/scratch/antlr_artifact_check/src/uk/co/jackoftrades/backend/parser/grammars/TerrainFeatureGrammar.g4 by ANTLR 4.13.2

            import uk.co.jackoftrades.backend.parser.terrainfeature.TerrainFeatureParseRecord;

            import java.util.List;
            import java.util.ArrayList;
        
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link TerrainFeatureGrammar}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface TerrainFeatureGrammarVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link TerrainFeatureGrammar#recordCount}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRecordCount(TerrainFeatureGrammar.RecordCountContext ctx);
	/**
	 * Visit a parse tree produced by {@link TerrainFeatureGrammar#code}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCode(TerrainFeatureGrammar.CodeContext ctx);
	/**
	 * Visit a parse tree produced by {@link TerrainFeatureGrammar#name}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitName(TerrainFeatureGrammar.NameContext ctx);
	/**
	 * Visit a parse tree produced by {@link TerrainFeatureGrammar#graphics}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGraphics(TerrainFeatureGrammar.GraphicsContext ctx);
	/**
	 * Visit a parse tree produced by {@link TerrainFeatureGrammar#mimic}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMimic(TerrainFeatureGrammar.MimicContext ctx);
	/**
	 * Visit a parse tree produced by {@link TerrainFeatureGrammar#priority}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPriority(TerrainFeatureGrammar.PriorityContext ctx);
	/**
	 * Visit a parse tree produced by {@link TerrainFeatureGrammar#flags}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFlags(TerrainFeatureGrammar.FlagsContext ctx);
	/**
	 * Visit a parse tree produced by {@link TerrainFeatureGrammar#digging}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDigging(TerrainFeatureGrammar.DiggingContext ctx);
	/**
	 * Visit a parse tree produced by {@link TerrainFeatureGrammar#walk_message}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWalk_message(TerrainFeatureGrammar.Walk_messageContext ctx);
	/**
	 * Visit a parse tree produced by {@link TerrainFeatureGrammar#run_message}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRun_message(TerrainFeatureGrammar.Run_messageContext ctx);
	/**
	 * Visit a parse tree produced by {@link TerrainFeatureGrammar#hurt_message}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHurt_message(TerrainFeatureGrammar.Hurt_messageContext ctx);
	/**
	 * Visit a parse tree produced by {@link TerrainFeatureGrammar#die_message}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDie_message(TerrainFeatureGrammar.Die_messageContext ctx);
	/**
	 * Visit a parse tree produced by {@link TerrainFeatureGrammar#confused_message}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConfused_message(TerrainFeatureGrammar.Confused_messageContext ctx);
	/**
	 * Visit a parse tree produced by {@link TerrainFeatureGrammar#look_prefix}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLook_prefix(TerrainFeatureGrammar.Look_prefixContext ctx);
	/**
	 * Visit a parse tree produced by {@link TerrainFeatureGrammar#look_in_preposition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLook_in_preposition(TerrainFeatureGrammar.Look_in_prepositionContext ctx);
	/**
	 * Visit a parse tree produced by {@link TerrainFeatureGrammar#resist_flag}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitResist_flag(TerrainFeatureGrammar.Resist_flagContext ctx);
	/**
	 * Visit a parse tree produced by {@link TerrainFeatureGrammar#desc}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDesc(TerrainFeatureGrammar.DescContext ctx);
	/**
	 * Visit a parse tree produced by {@link TerrainFeatureGrammar#terrain}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTerrain(TerrainFeatureGrammar.TerrainContext ctx);
	/**
	 * Visit a parse tree produced by {@link TerrainFeatureGrammar#file}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFile(TerrainFeatureGrammar.FileContext ctx);
}