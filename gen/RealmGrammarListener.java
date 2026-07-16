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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/scratch/antlr_artifact_check/src/uk/co/jackoftrades/backend/parser/grammars/RealmGrammar.g4 by ANTLR 4.13.2

    import uk.co.jackoftrades.backend.parser.realm.RealmParseRecord;

    import java.util.List;
    import java.util.ArrayList;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link RealmGrammar}.
 */
public interface RealmGrammarListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link RealmGrammar#recordCount}.
	 * @param ctx the parse tree
	 */
	void enterRecordCount(RealmGrammar.RecordCountContext ctx);
	/**
	 * Exit a parse tree produced by {@link RealmGrammar#recordCount}.
	 * @param ctx the parse tree
	 */
	void exitRecordCount(RealmGrammar.RecordCountContext ctx);
	/**
	 * Enter a parse tree produced by {@link RealmGrammar#name}.
	 * @param ctx the parse tree
	 */
	void enterName(RealmGrammar.NameContext ctx);
	/**
	 * Exit a parse tree produced by {@link RealmGrammar#name}.
	 * @param ctx the parse tree
	 */
	void exitName(RealmGrammar.NameContext ctx);
	/**
	 * Enter a parse tree produced by {@link RealmGrammar#stat}.
	 * @param ctx the parse tree
	 */
	void enterStat(RealmGrammar.StatContext ctx);
	/**
	 * Exit a parse tree produced by {@link RealmGrammar#stat}.
	 * @param ctx the parse tree
	 */
	void exitStat(RealmGrammar.StatContext ctx);
	/**
	 * Enter a parse tree produced by {@link RealmGrammar#verb}.
	 * @param ctx the parse tree
	 */
	void enterVerb(RealmGrammar.VerbContext ctx);
	/**
	 * Exit a parse tree produced by {@link RealmGrammar#verb}.
	 * @param ctx the parse tree
	 */
	void exitVerb(RealmGrammar.VerbContext ctx);
	/**
	 * Enter a parse tree produced by {@link RealmGrammar#spellNoun}.
	 * @param ctx the parse tree
	 */
	void enterSpellNoun(RealmGrammar.SpellNounContext ctx);
	/**
	 * Exit a parse tree produced by {@link RealmGrammar#spellNoun}.
	 * @param ctx the parse tree
	 */
	void exitSpellNoun(RealmGrammar.SpellNounContext ctx);
	/**
	 * Enter a parse tree produced by {@link RealmGrammar#bookNoun}.
	 * @param ctx the parse tree
	 */
	void enterBookNoun(RealmGrammar.BookNounContext ctx);
	/**
	 * Exit a parse tree produced by {@link RealmGrammar#bookNoun}.
	 * @param ctx the parse tree
	 */
	void exitBookNoun(RealmGrammar.BookNounContext ctx);
	/**
	 * Enter a parse tree produced by {@link RealmGrammar#realm}.
	 * @param ctx the parse tree
	 */
	void enterRealm(RealmGrammar.RealmContext ctx);
	/**
	 * Exit a parse tree produced by {@link RealmGrammar#realm}.
	 * @param ctx the parse tree
	 */
	void exitRealm(RealmGrammar.RealmContext ctx);
	/**
	 * Enter a parse tree produced by {@link RealmGrammar#file}.
	 * @param ctx the parse tree
	 */
	void enterFile(RealmGrammar.FileContext ctx);
	/**
	 * Exit a parse tree produced by {@link RealmGrammar#file}.
	 * @param ctx the parse tree
	 */
	void exitFile(RealmGrammar.FileContext ctx);
}