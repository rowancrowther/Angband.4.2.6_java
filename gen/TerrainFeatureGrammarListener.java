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
        
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link TerrainFeatureGrammar}.
 */
public interface TerrainFeatureGrammarListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link TerrainFeatureGrammar#recordCount}.
	 * @param ctx the parse tree
	 */
	void enterRecordCount(TerrainFeatureGrammar.RecordCountContext ctx);
	/**
	 * Exit a parse tree produced by {@link TerrainFeatureGrammar#recordCount}.
	 * @param ctx the parse tree
	 */
	void exitRecordCount(TerrainFeatureGrammar.RecordCountContext ctx);
	/**
	 * Enter a parse tree produced by {@link TerrainFeatureGrammar#code}.
	 * @param ctx the parse tree
	 */
	void enterCode(TerrainFeatureGrammar.CodeContext ctx);
	/**
	 * Exit a parse tree produced by {@link TerrainFeatureGrammar#code}.
	 * @param ctx the parse tree
	 */
	void exitCode(TerrainFeatureGrammar.CodeContext ctx);
	/**
	 * Enter a parse tree produced by {@link TerrainFeatureGrammar#name}.
	 * @param ctx the parse tree
	 */
	void enterName(TerrainFeatureGrammar.NameContext ctx);
	/**
	 * Exit a parse tree produced by {@link TerrainFeatureGrammar#name}.
	 * @param ctx the parse tree
	 */
	void exitName(TerrainFeatureGrammar.NameContext ctx);
	/**
	 * Enter a parse tree produced by {@link TerrainFeatureGrammar#graphics}.
	 * @param ctx the parse tree
	 */
	void enterGraphics(TerrainFeatureGrammar.GraphicsContext ctx);
	/**
	 * Exit a parse tree produced by {@link TerrainFeatureGrammar#graphics}.
	 * @param ctx the parse tree
	 */
	void exitGraphics(TerrainFeatureGrammar.GraphicsContext ctx);
	/**
	 * Enter a parse tree produced by {@link TerrainFeatureGrammar#mimic}.
	 * @param ctx the parse tree
	 */
	void enterMimic(TerrainFeatureGrammar.MimicContext ctx);
	/**
	 * Exit a parse tree produced by {@link TerrainFeatureGrammar#mimic}.
	 * @param ctx the parse tree
	 */
	void exitMimic(TerrainFeatureGrammar.MimicContext ctx);
	/**
	 * Enter a parse tree produced by {@link TerrainFeatureGrammar#priority}.
	 * @param ctx the parse tree
	 */
	void enterPriority(TerrainFeatureGrammar.PriorityContext ctx);
	/**
	 * Exit a parse tree produced by {@link TerrainFeatureGrammar#priority}.
	 * @param ctx the parse tree
	 */
	void exitPriority(TerrainFeatureGrammar.PriorityContext ctx);
	/**
	 * Enter a parse tree produced by {@link TerrainFeatureGrammar#flags}.
	 * @param ctx the parse tree
	 */
	void enterFlags(TerrainFeatureGrammar.FlagsContext ctx);
	/**
	 * Exit a parse tree produced by {@link TerrainFeatureGrammar#flags}.
	 * @param ctx the parse tree
	 */
	void exitFlags(TerrainFeatureGrammar.FlagsContext ctx);
	/**
	 * Enter a parse tree produced by {@link TerrainFeatureGrammar#digging}.
	 * @param ctx the parse tree
	 */
	void enterDigging(TerrainFeatureGrammar.DiggingContext ctx);
	/**
	 * Exit a parse tree produced by {@link TerrainFeatureGrammar#digging}.
	 * @param ctx the parse tree
	 */
	void exitDigging(TerrainFeatureGrammar.DiggingContext ctx);
	/**
	 * Enter a parse tree produced by {@link TerrainFeatureGrammar#walk_message}.
	 * @param ctx the parse tree
	 */
	void enterWalk_message(TerrainFeatureGrammar.Walk_messageContext ctx);
	/**
	 * Exit a parse tree produced by {@link TerrainFeatureGrammar#walk_message}.
	 * @param ctx the parse tree
	 */
	void exitWalk_message(TerrainFeatureGrammar.Walk_messageContext ctx);
	/**
	 * Enter a parse tree produced by {@link TerrainFeatureGrammar#run_message}.
	 * @param ctx the parse tree
	 */
	void enterRun_message(TerrainFeatureGrammar.Run_messageContext ctx);
	/**
	 * Exit a parse tree produced by {@link TerrainFeatureGrammar#run_message}.
	 * @param ctx the parse tree
	 */
	void exitRun_message(TerrainFeatureGrammar.Run_messageContext ctx);
	/**
	 * Enter a parse tree produced by {@link TerrainFeatureGrammar#hurt_message}.
	 * @param ctx the parse tree
	 */
	void enterHurt_message(TerrainFeatureGrammar.Hurt_messageContext ctx);
	/**
	 * Exit a parse tree produced by {@link TerrainFeatureGrammar#hurt_message}.
	 * @param ctx the parse tree
	 */
	void exitHurt_message(TerrainFeatureGrammar.Hurt_messageContext ctx);
	/**
	 * Enter a parse tree produced by {@link TerrainFeatureGrammar#die_message}.
	 * @param ctx the parse tree
	 */
	void enterDie_message(TerrainFeatureGrammar.Die_messageContext ctx);
	/**
	 * Exit a parse tree produced by {@link TerrainFeatureGrammar#die_message}.
	 * @param ctx the parse tree
	 */
	void exitDie_message(TerrainFeatureGrammar.Die_messageContext ctx);
	/**
	 * Enter a parse tree produced by {@link TerrainFeatureGrammar#confused_message}.
	 * @param ctx the parse tree
	 */
	void enterConfused_message(TerrainFeatureGrammar.Confused_messageContext ctx);
	/**
	 * Exit a parse tree produced by {@link TerrainFeatureGrammar#confused_message}.
	 * @param ctx the parse tree
	 */
	void exitConfused_message(TerrainFeatureGrammar.Confused_messageContext ctx);
	/**
	 * Enter a parse tree produced by {@link TerrainFeatureGrammar#look_prefix}.
	 * @param ctx the parse tree
	 */
	void enterLook_prefix(TerrainFeatureGrammar.Look_prefixContext ctx);
	/**
	 * Exit a parse tree produced by {@link TerrainFeatureGrammar#look_prefix}.
	 * @param ctx the parse tree
	 */
	void exitLook_prefix(TerrainFeatureGrammar.Look_prefixContext ctx);
	/**
	 * Enter a parse tree produced by {@link TerrainFeatureGrammar#look_in_preposition}.
	 * @param ctx the parse tree
	 */
	void enterLook_in_preposition(TerrainFeatureGrammar.Look_in_prepositionContext ctx);
	/**
	 * Exit a parse tree produced by {@link TerrainFeatureGrammar#look_in_preposition}.
	 * @param ctx the parse tree
	 */
	void exitLook_in_preposition(TerrainFeatureGrammar.Look_in_prepositionContext ctx);
	/**
	 * Enter a parse tree produced by {@link TerrainFeatureGrammar#resist_flag}.
	 * @param ctx the parse tree
	 */
	void enterResist_flag(TerrainFeatureGrammar.Resist_flagContext ctx);
	/**
	 * Exit a parse tree produced by {@link TerrainFeatureGrammar#resist_flag}.
	 * @param ctx the parse tree
	 */
	void exitResist_flag(TerrainFeatureGrammar.Resist_flagContext ctx);
	/**
	 * Enter a parse tree produced by {@link TerrainFeatureGrammar#desc}.
	 * @param ctx the parse tree
	 */
	void enterDesc(TerrainFeatureGrammar.DescContext ctx);
	/**
	 * Exit a parse tree produced by {@link TerrainFeatureGrammar#desc}.
	 * @param ctx the parse tree
	 */
	void exitDesc(TerrainFeatureGrammar.DescContext ctx);
	/**
	 * Enter a parse tree produced by {@link TerrainFeatureGrammar#terrain}.
	 * @param ctx the parse tree
	 */
	void enterTerrain(TerrainFeatureGrammar.TerrainContext ctx);
	/**
	 * Exit a parse tree produced by {@link TerrainFeatureGrammar#terrain}.
	 * @param ctx the parse tree
	 */
	void exitTerrain(TerrainFeatureGrammar.TerrainContext ctx);
	/**
	 * Enter a parse tree produced by {@link TerrainFeatureGrammar#file}.
	 * @param ctx the parse tree
	 */
	void enterFile(TerrainFeatureGrammar.FileContext ctx);
	/**
	 * Exit a parse tree produced by {@link TerrainFeatureGrammar#file}.
	 * @param ctx the parse tree
	 */
	void exitFile(TerrainFeatureGrammar.FileContext ctx);
}