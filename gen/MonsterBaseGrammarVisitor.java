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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/scratch/antlr_artifact_check/src/uk/co/jackoftrades/backend/parser/grammars/MonsterBaseGrammar.g4 by ANTLR 4.13.2

    import uk.co.jackoftrades.backend.parser.monsterbase.MonsterBaseParseRecord;

    import java.util.List;
    import java.util.ArrayList;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link MonsterBaseGrammar}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface MonsterBaseGrammarVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link MonsterBaseGrammar#recordCount}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRecordCount(MonsterBaseGrammar.RecordCountContext ctx);
	/**
	 * Visit a parse tree produced by {@link MonsterBaseGrammar#name}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitName(MonsterBaseGrammar.NameContext ctx);
	/**
	 * Visit a parse tree produced by {@link MonsterBaseGrammar#glyph}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGlyph(MonsterBaseGrammar.GlyphContext ctx);
	/**
	 * Visit a parse tree produced by {@link MonsterBaseGrammar#pain}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPain(MonsterBaseGrammar.PainContext ctx);
	/**
	 * Visit a parse tree produced by {@link MonsterBaseGrammar#flags}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFlags(MonsterBaseGrammar.FlagsContext ctx);
	/**
	 * Visit a parse tree produced by {@link MonsterBaseGrammar#desc}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDesc(MonsterBaseGrammar.DescContext ctx);
	/**
	 * Visit a parse tree produced by {@link MonsterBaseGrammar#monsterBase}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMonsterBase(MonsterBaseGrammar.MonsterBaseContext ctx);
	/**
	 * Visit a parse tree produced by {@link MonsterBaseGrammar#file}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFile(MonsterBaseGrammar.FileContext ctx);
}