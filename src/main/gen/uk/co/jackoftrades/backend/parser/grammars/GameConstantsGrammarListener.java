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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/parser/grammars/GameConstantsGrammar.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.grammars;

            import uk.co.jackoftrades.backend.parser.gameconstants.GameConstantsParseRecord;

            import java.util.List;
            import java.util.ArrayList;
        
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link GameConstantsGrammar}.
 */
public interface GameConstantsGrammarListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link GameConstantsGrammar#field}.
	 * @param ctx the parse tree
	 */
	void enterField(GameConstantsGrammar.FieldContext ctx);
	/**
	 * Exit a parse tree produced by {@link GameConstantsGrammar#field}.
	 * @param ctx the parse tree
	 */
	void exitField(GameConstantsGrammar.FieldContext ctx);
	/**
	 * Enter a parse tree produced by {@link GameConstantsGrammar#line}.
	 * @param ctx the parse tree
	 */
	void enterLine(GameConstantsGrammar.LineContext ctx);
	/**
	 * Exit a parse tree produced by {@link GameConstantsGrammar#line}.
	 * @param ctx the parse tree
	 */
	void exitLine(GameConstantsGrammar.LineContext ctx);
	/**
	 * Enter a parse tree produced by {@link GameConstantsGrammar#file}.
	 * @param ctx the parse tree
	 */
	void enterFile(GameConstantsGrammar.FileContext ctx);
	/**
	 * Exit a parse tree produced by {@link GameConstantsGrammar#file}.
	 * @param ctx the parse tree
	 */
	void exitFile(GameConstantsGrammar.FileContext ctx);
}