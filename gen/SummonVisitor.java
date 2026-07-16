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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/scratch/antlr_artifact_check/src/uk/co/jackoftrades/backend/parser/grammars/Summon.g4 by ANTLR 4.13.2

    import uk.co.jackoftrades.middle.monsters.Summon;
    import uk.co.jackoftrades.middle.monsters.MonsterBase;
    import uk.co.jackoftrades.middle.enums.MessageType;
    import uk.co.jackoftrades.middle.game.globals.GameConstants;
    import uk.co.jackoftrades.middle.monsters.enums.MonsterRaceFlag;

    import java.util.List;
    import java.util.ArrayList;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link SummonParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface SummonVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link SummonParser#name}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitName(SummonParser.NameContext ctx);
	/**
	 * Visit a parse tree produced by {@link SummonParser#msgt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMsgt(SummonParser.MsgtContext ctx);
	/**
	 * Visit a parse tree produced by {@link SummonParser#uniques}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUniques(SummonParser.UniquesContext ctx);
	/**
	 * Visit a parse tree produced by {@link SummonParser#base}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBase(SummonParser.BaseContext ctx);
	/**
	 * Visit a parse tree produced by {@link SummonParser#race_flag}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRace_flag(SummonParser.Race_flagContext ctx);
	/**
	 * Visit a parse tree produced by {@link SummonParser#fallback}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFallback(SummonParser.FallbackContext ctx);
	/**
	 * Visit a parse tree produced by {@link SummonParser#desc}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDesc(SummonParser.DescContext ctx);
	/**
	 * Visit a parse tree produced by {@link SummonParser#summon}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSummon(SummonParser.SummonContext ctx);
	/**
	 * Visit a parse tree produced by {@link SummonParser#file}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFile(SummonParser.FileContext ctx);
}