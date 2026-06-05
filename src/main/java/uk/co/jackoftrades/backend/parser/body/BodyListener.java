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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/parser/grammars/Body.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.body;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link BodyParser}.
 */
public interface BodyListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link BodyParser#body}.
	 *
	 * @param ctx the parse tree
	 */
	void enterBody(BodyParser.BodyContext ctx);

	/**
	 * Exit a parse tree produced by {@link BodyParser#body}.
	 *
	 * @param ctx the parse tree
	 */
	void exitBody(BodyParser.BodyContext ctx);

	/**
	 * Enter a parse tree produced by {@link BodyParser#slot}.
	 *
	 * @param ctx the parse tree
	 */
	void enterSlot(BodyParser.SlotContext ctx);

	/**
	 * Exit a parse tree produced by {@link BodyParser#slot}.
	 *
	 * @param ctx the parse tree
	 */
	void exitSlot(BodyParser.SlotContext ctx);

	/**
	 * Enter a parse tree produced by {@link BodyParser#entry}.
	 *
	 * @param ctx the parse tree
	 */
	void enterEntry(BodyParser.EntryContext ctx);

	/**
	 * Exit a parse tree produced by {@link BodyParser#entry}.
	 *
	 * @param ctx the parse tree
	 */
	void exitEntry(BodyParser.EntryContext ctx);

	/**
	 * Enter a parse tree produced by {@link BodyParser#file}.
	 *
	 * @param ctx the parse tree
	 */
	void enterFile(BodyParser.FileContext ctx);

	/**
	 * Exit a parse tree produced by {@link BodyParser#file}.
	 *
	 * @param ctx the parse tree
	 */
	void exitFile(BodyParser.FileContext ctx);
}