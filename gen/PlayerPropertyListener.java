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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/.claude/worktrees/gameconstants-assembler/src/main/java/uk/co/jackoftrades/backend/parser/grammars/PlayerProperty.g4 by ANTLR 4.13.2

    import uk.co.jackoftrades.middle.player.PlayerProperty;
    import uk.co.jackoftrades.middle.player.PlayerProperty.PlayerPropertyType;
    import uk.co.jackoftrades.middle.player.PlayerProperty.PlayerPropertyValue;
    import uk.co.jackoftrades.middle.player.enums.PlayerFlag;
    import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;
    import uk.co.jackoftrades.middle.game.globals.GameConstants;
    import uk.co.jackoftrades.middle.enums.Stats;
    import uk.co.jackoftrades.middle.objects.enums.ElementEnum
    import uk.co.jackoftrades.frontend.entries.UIEntry;

    import java.util.List;
    import java.util.ArrayList;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link PlayerPropertyParser}.
 */
public interface PlayerPropertyListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link PlayerPropertyParser#type}.
	 * @param ctx the parse tree
	 */
	void enterType(PlayerPropertyParser.TypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link PlayerPropertyParser#type}.
	 * @param ctx the parse tree
	 */
	void exitType(PlayerPropertyParser.TypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link PlayerPropertyParser#code}.
	 * @param ctx the parse tree
	 */
	void enterCode(PlayerPropertyParser.CodeContext ctx);
	/**
	 * Exit a parse tree produced by {@link PlayerPropertyParser#code}.
	 * @param ctx the parse tree
	 */
	void exitCode(PlayerPropertyParser.CodeContext ctx);
	/**
	 * Enter a parse tree produced by {@link PlayerPropertyParser#bindui}.
	 * @param ctx the parse tree
	 */
	void enterBindui(PlayerPropertyParser.BinduiContext ctx);
	/**
	 * Exit a parse tree produced by {@link PlayerPropertyParser#bindui}.
	 * @param ctx the parse tree
	 */
	void exitBindui(PlayerPropertyParser.BinduiContext ctx);
	/**
	 * Enter a parse tree produced by {@link PlayerPropertyParser#name}.
	 * @param ctx the parse tree
	 */
	void enterName(PlayerPropertyParser.NameContext ctx);
	/**
	 * Exit a parse tree produced by {@link PlayerPropertyParser#name}.
	 * @param ctx the parse tree
	 */
	void exitName(PlayerPropertyParser.NameContext ctx);
	/**
	 * Enter a parse tree produced by {@link PlayerPropertyParser#desc}.
	 * @param ctx the parse tree
	 */
	void enterDesc(PlayerPropertyParser.DescContext ctx);
	/**
	 * Exit a parse tree produced by {@link PlayerPropertyParser#desc}.
	 * @param ctx the parse tree
	 */
	void exitDesc(PlayerPropertyParser.DescContext ctx);
	/**
	 * Enter a parse tree produced by {@link PlayerPropertyParser#value}.
	 * @param ctx the parse tree
	 */
	void enterValue(PlayerPropertyParser.ValueContext ctx);
	/**
	 * Exit a parse tree produced by {@link PlayerPropertyParser#value}.
	 * @param ctx the parse tree
	 */
	void exitValue(PlayerPropertyParser.ValueContext ctx);
	/**
	 * Enter a parse tree produced by {@link PlayerPropertyParser#property}.
	 * @param ctx the parse tree
	 */
	void enterProperty(PlayerPropertyParser.PropertyContext ctx);
	/**
	 * Exit a parse tree produced by {@link PlayerPropertyParser#property}.
	 * @param ctx the parse tree
	 */
	void exitProperty(PlayerPropertyParser.PropertyContext ctx);
	/**
	 * Enter a parse tree produced by {@link PlayerPropertyParser#file}.
	 * @param ctx the parse tree
	 */
	void enterFile(PlayerPropertyParser.FileContext ctx);
	/**
	 * Exit a parse tree produced by {@link PlayerPropertyParser#file}.
	 * @param ctx the parse tree
	 */
	void exitFile(PlayerPropertyParser.FileContext ctx);
}