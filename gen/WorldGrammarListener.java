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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/scratch/antlr_artifact_check/src/uk/co/jackoftrades/backend/parser/grammars/WorldGrammar.g4 by ANTLR 4.13.2

            import java.util.ArrayList;
            import java.util.List;
        
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link WorldGrammar}.
 */
public interface WorldGrammarListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link WorldGrammar#recordCount}.
	 * @param ctx the parse tree
	 */
	void enterRecordCount(WorldGrammar.RecordCountContext ctx);
	/**
	 * Exit a parse tree produced by {@link WorldGrammar#recordCount}.
	 * @param ctx the parse tree
	 */
	void exitRecordCount(WorldGrammar.RecordCountContext ctx);
	/**
	 * Enter a parse tree produced by {@link WorldGrammar#levelNum}.
	 * @param ctx the parse tree
	 */
	void enterLevelNum(WorldGrammar.LevelNumContext ctx);
	/**
	 * Exit a parse tree produced by {@link WorldGrammar#levelNum}.
	 * @param ctx the parse tree
	 */
	void exitLevelNum(WorldGrammar.LevelNumContext ctx);
	/**
	 * Enter a parse tree produced by {@link WorldGrammar#levelName}.
	 * @param ctx the parse tree
	 */
	void enterLevelName(WorldGrammar.LevelNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link WorldGrammar#levelName}.
	 * @param ctx the parse tree
	 */
	void exitLevelName(WorldGrammar.LevelNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link WorldGrammar#upAndDown}.
	 * @param ctx the parse tree
	 */
	void enterUpAndDown(WorldGrammar.UpAndDownContext ctx);
	/**
	 * Exit a parse tree produced by {@link WorldGrammar#upAndDown}.
	 * @param ctx the parse tree
	 */
	void exitUpAndDown(WorldGrammar.UpAndDownContext ctx);
	/**
	 * Enter a parse tree produced by {@link WorldGrammar#line}.
	 * @param ctx the parse tree
	 */
	void enterLine(WorldGrammar.LineContext ctx);
	/**
	 * Exit a parse tree produced by {@link WorldGrammar#line}.
	 * @param ctx the parse tree
	 */
	void exitLine(WorldGrammar.LineContext ctx);
	/**
	 * Enter a parse tree produced by {@link WorldGrammar#file}.
	 * @param ctx the parse tree
	 */
	void enterFile(WorldGrammar.FileContext ctx);
	/**
	 * Exit a parse tree produced by {@link WorldGrammar#file}.
	 * @param ctx the parse tree
	 */
	void exitFile(WorldGrammar.FileContext ctx);
}