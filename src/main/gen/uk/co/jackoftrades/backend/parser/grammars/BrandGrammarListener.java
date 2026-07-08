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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/parser/grammars/BrandGrammar.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.grammars;

    import uk.co.jackoftrades.backend.parser.brand.BrandParseRecord;

    import java.util.ArrayList;
    import java.util.List;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link BrandGrammar}.
 */
public interface BrandGrammarListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link BrandGrammar#recordCount}.
	 * @param ctx the parse tree
	 */
	void enterRecordCount(BrandGrammar.RecordCountContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrandGrammar#recordCount}.
	 * @param ctx the parse tree
	 */
	void exitRecordCount(BrandGrammar.RecordCountContext ctx);
	/**
	 * Enter a parse tree produced by {@link BrandGrammar#code}.
	 * @param ctx the parse tree
	 */
	void enterCode(BrandGrammar.CodeContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrandGrammar#code}.
	 * @param ctx the parse tree
	 */
	void exitCode(BrandGrammar.CodeContext ctx);
	/**
	 * Enter a parse tree produced by {@link BrandGrammar#name}.
	 * @param ctx the parse tree
	 */
	void enterName(BrandGrammar.NameContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrandGrammar#name}.
	 * @param ctx the parse tree
	 */
	void exitName(BrandGrammar.NameContext ctx);
	/**
	 * Enter a parse tree produced by {@link BrandGrammar#multiplier}.
	 * @param ctx the parse tree
	 */
	void enterMultiplier(BrandGrammar.MultiplierContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrandGrammar#multiplier}.
	 * @param ctx the parse tree
	 */
	void exitMultiplier(BrandGrammar.MultiplierContext ctx);
	/**
	 * Enter a parse tree produced by {@link BrandGrammar#oMultiplier}.
	 * @param ctx the parse tree
	 */
	void enterOMultiplier(BrandGrammar.OMultiplierContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrandGrammar#oMultiplier}.
	 * @param ctx the parse tree
	 */
	void exitOMultiplier(BrandGrammar.OMultiplierContext ctx);
	/**
	 * Enter a parse tree produced by {@link BrandGrammar#power}.
	 * @param ctx the parse tree
	 */
	void enterPower(BrandGrammar.PowerContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrandGrammar#power}.
	 * @param ctx the parse tree
	 */
	void exitPower(BrandGrammar.PowerContext ctx);
	/**
	 * Enter a parse tree produced by {@link BrandGrammar#verb}.
	 * @param ctx the parse tree
	 */
	void enterVerb(BrandGrammar.VerbContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrandGrammar#verb}.
	 * @param ctx the parse tree
	 */
	void exitVerb(BrandGrammar.VerbContext ctx);
	/**
	 * Enter a parse tree produced by {@link BrandGrammar#resistFlag}.
	 * @param ctx the parse tree
	 */
	void enterResistFlag(BrandGrammar.ResistFlagContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrandGrammar#resistFlag}.
	 * @param ctx the parse tree
	 */
	void exitResistFlag(BrandGrammar.ResistFlagContext ctx);
	/**
	 * Enter a parse tree produced by {@link BrandGrammar#vulnFlag}.
	 * @param ctx the parse tree
	 */
	void enterVulnFlag(BrandGrammar.VulnFlagContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrandGrammar#vulnFlag}.
	 * @param ctx the parse tree
	 */
	void exitVulnFlag(BrandGrammar.VulnFlagContext ctx);
	/**
	 * Enter a parse tree produced by {@link BrandGrammar#brand}.
	 * @param ctx the parse tree
	 */
	void enterBrand(BrandGrammar.BrandContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrandGrammar#brand}.
	 * @param ctx the parse tree
	 */
	void exitBrand(BrandGrammar.BrandContext ctx);
	/**
	 * Enter a parse tree produced by {@link BrandGrammar#file}.
	 * @param ctx the parse tree
	 */
	void enterFile(BrandGrammar.FileContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrandGrammar#file}.
	 * @param ctx the parse tree
	 */
	void exitFile(BrandGrammar.FileContext ctx);
}