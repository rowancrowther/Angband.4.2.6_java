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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/.claude/worktrees/gameconstants-assembler/src/main/java/uk/co/jackoftrades/backend/parser/grammars/Slay.g4 by ANTLR 4.13.2

    import uk.co.jackoftrades.middle.monsters.enums.MonsterRaceFlag;
    import uk.co.jackoftrades.middle.monsters.MonsterBase;
    import uk.co.jackoftrades.middle.objects.Slay;
    import uk.co.jackoftrades.middle.game.globals.GameConstants;

    import java.util.List;
    import java.util.ArrayList;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link SlayParser}.
 */
public interface SlayListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link SlayParser#code}.
	 * @param ctx the parse tree
	 */
	void enterCode(SlayParser.CodeContext ctx);
	/**
	 * Exit a parse tree produced by {@link SlayParser#code}.
	 * @param ctx the parse tree
	 */
	void exitCode(SlayParser.CodeContext ctx);
	/**
	 * Enter a parse tree produced by {@link SlayParser#name}.
	 * @param ctx the parse tree
	 */
	void enterName(SlayParser.NameContext ctx);
	/**
	 * Exit a parse tree produced by {@link SlayParser#name}.
	 * @param ctx the parse tree
	 */
	void exitName(SlayParser.NameContext ctx);
	/**
	 * Enter a parse tree produced by {@link SlayParser#race_flag}.
	 * @param ctx the parse tree
	 */
	void enterRace_flag(SlayParser.Race_flagContext ctx);
	/**
	 * Exit a parse tree produced by {@link SlayParser#race_flag}.
	 * @param ctx the parse tree
	 */
	void exitRace_flag(SlayParser.Race_flagContext ctx);
	/**
	 * Enter a parse tree produced by {@link SlayParser#base}.
	 * @param ctx the parse tree
	 */
	void enterBase(SlayParser.BaseContext ctx);
	/**
	 * Exit a parse tree produced by {@link SlayParser#base}.
	 * @param ctx the parse tree
	 */
	void exitBase(SlayParser.BaseContext ctx);
	/**
	 * Enter a parse tree produced by {@link SlayParser#multiplier}.
	 * @param ctx the parse tree
	 */
	void enterMultiplier(SlayParser.MultiplierContext ctx);
	/**
	 * Exit a parse tree produced by {@link SlayParser#multiplier}.
	 * @param ctx the parse tree
	 */
	void exitMultiplier(SlayParser.MultiplierContext ctx);
	/**
	 * Enter a parse tree produced by {@link SlayParser#o_multiplier}.
	 * @param ctx the parse tree
	 */
	void enterO_multiplier(SlayParser.O_multiplierContext ctx);
	/**
	 * Exit a parse tree produced by {@link SlayParser#o_multiplier}.
	 * @param ctx the parse tree
	 */
	void exitO_multiplier(SlayParser.O_multiplierContext ctx);
	/**
	 * Enter a parse tree produced by {@link SlayParser#power}.
	 * @param ctx the parse tree
	 */
	void enterPower(SlayParser.PowerContext ctx);
	/**
	 * Exit a parse tree produced by {@link SlayParser#power}.
	 * @param ctx the parse tree
	 */
	void exitPower(SlayParser.PowerContext ctx);
	/**
	 * Enter a parse tree produced by {@link SlayParser#melee_verb}.
	 * @param ctx the parse tree
	 */
	void enterMelee_verb(SlayParser.Melee_verbContext ctx);
	/**
	 * Exit a parse tree produced by {@link SlayParser#melee_verb}.
	 * @param ctx the parse tree
	 */
	void exitMelee_verb(SlayParser.Melee_verbContext ctx);
	/**
	 * Enter a parse tree produced by {@link SlayParser#ranged_verb}.
	 * @param ctx the parse tree
	 */
	void enterRanged_verb(SlayParser.Ranged_verbContext ctx);
	/**
	 * Exit a parse tree produced by {@link SlayParser#ranged_verb}.
	 * @param ctx the parse tree
	 */
	void exitRanged_verb(SlayParser.Ranged_verbContext ctx);
	/**
	 * Enter a parse tree produced by {@link SlayParser#slay}.
	 * @param ctx the parse tree
	 */
	void enterSlay(SlayParser.SlayContext ctx);
	/**
	 * Exit a parse tree produced by {@link SlayParser#slay}.
	 * @param ctx the parse tree
	 */
	void exitSlay(SlayParser.SlayContext ctx);
	/**
	 * Enter a parse tree produced by {@link SlayParser#file}.
	 * @param ctx the parse tree
	 */
	void enterFile(SlayParser.FileContext ctx);
	/**
	 * Exit a parse tree produced by {@link SlayParser#file}.
	 * @param ctx the parse tree
	 */
	void exitFile(SlayParser.FileContext ctx);
}