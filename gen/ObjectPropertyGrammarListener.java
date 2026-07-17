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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/scratch/op_probe/ObjectPropertyGrammar.g4 by ANTLR 4.13.2

    import uk.co.jackoftrades.backend.parser.objectproperty.ObjectPropertyParseRecord;

    import java.util.List;
    import java.util.ArrayList;
    import java.util.Map;
    import java.util.HashMap;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link ObjectPropertyGrammar}.
 */
public interface ObjectPropertyGrammarListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link ObjectPropertyGrammar#recordCount}.
	 * @param ctx the parse tree
	 */
	void enterRecordCount(ObjectPropertyGrammar.RecordCountContext ctx);
	/**
	 * Exit a parse tree produced by {@link ObjectPropertyGrammar#recordCount}.
	 * @param ctx the parse tree
	 */
	void exitRecordCount(ObjectPropertyGrammar.RecordCountContext ctx);
	/**
	 * Enter a parse tree produced by {@link ObjectPropertyGrammar#name}.
	 * @param ctx the parse tree
	 */
	void enterName(ObjectPropertyGrammar.NameContext ctx);
	/**
	 * Exit a parse tree produced by {@link ObjectPropertyGrammar#name}.
	 * @param ctx the parse tree
	 */
	void exitName(ObjectPropertyGrammar.NameContext ctx);
	/**
	 * Enter a parse tree produced by {@link ObjectPropertyGrammar#type}.
	 * @param ctx the parse tree
	 */
	void enterType(ObjectPropertyGrammar.TypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link ObjectPropertyGrammar#type}.
	 * @param ctx the parse tree
	 */
	void exitType(ObjectPropertyGrammar.TypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link ObjectPropertyGrammar#subtype}.
	 * @param ctx the parse tree
	 */
	void enterSubtype(ObjectPropertyGrammar.SubtypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link ObjectPropertyGrammar#subtype}.
	 * @param ctx the parse tree
	 */
	void exitSubtype(ObjectPropertyGrammar.SubtypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link ObjectPropertyGrammar#idType}.
	 * @param ctx the parse tree
	 */
	void enterIdType(ObjectPropertyGrammar.IdTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link ObjectPropertyGrammar#idType}.
	 * @param ctx the parse tree
	 */
	void exitIdType(ObjectPropertyGrammar.IdTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link ObjectPropertyGrammar#codeVal}.
	 * @param ctx the parse tree
	 */
	void enterCodeVal(ObjectPropertyGrammar.CodeValContext ctx);
	/**
	 * Exit a parse tree produced by {@link ObjectPropertyGrammar#codeVal}.
	 * @param ctx the parse tree
	 */
	void exitCodeVal(ObjectPropertyGrammar.CodeValContext ctx);
	/**
	 * Enter a parse tree produced by {@link ObjectPropertyGrammar#power}.
	 * @param ctx the parse tree
	 */
	void enterPower(ObjectPropertyGrammar.PowerContext ctx);
	/**
	 * Exit a parse tree produced by {@link ObjectPropertyGrammar#power}.
	 * @param ctx the parse tree
	 */
	void exitPower(ObjectPropertyGrammar.PowerContext ctx);
	/**
	 * Enter a parse tree produced by {@link ObjectPropertyGrammar#mult}.
	 * @param ctx the parse tree
	 */
	void enterMult(ObjectPropertyGrammar.MultContext ctx);
	/**
	 * Exit a parse tree produced by {@link ObjectPropertyGrammar#mult}.
	 * @param ctx the parse tree
	 */
	void exitMult(ObjectPropertyGrammar.MultContext ctx);
	/**
	 * Enter a parse tree produced by {@link ObjectPropertyGrammar#typeMult}.
	 * @param ctx the parse tree
	 */
	void enterTypeMult(ObjectPropertyGrammar.TypeMultContext ctx);
	/**
	 * Exit a parse tree produced by {@link ObjectPropertyGrammar#typeMult}.
	 * @param ctx the parse tree
	 */
	void exitTypeMult(ObjectPropertyGrammar.TypeMultContext ctx);
	/**
	 * Enter a parse tree produced by {@link ObjectPropertyGrammar#adjective}.
	 * @param ctx the parse tree
	 */
	void enterAdjective(ObjectPropertyGrammar.AdjectiveContext ctx);
	/**
	 * Exit a parse tree produced by {@link ObjectPropertyGrammar#adjective}.
	 * @param ctx the parse tree
	 */
	void exitAdjective(ObjectPropertyGrammar.AdjectiveContext ctx);
	/**
	 * Enter a parse tree produced by {@link ObjectPropertyGrammar#negAdjective}.
	 * @param ctx the parse tree
	 */
	void enterNegAdjective(ObjectPropertyGrammar.NegAdjectiveContext ctx);
	/**
	 * Exit a parse tree produced by {@link ObjectPropertyGrammar#negAdjective}.
	 * @param ctx the parse tree
	 */
	void exitNegAdjective(ObjectPropertyGrammar.NegAdjectiveContext ctx);
	/**
	 * Enter a parse tree produced by {@link ObjectPropertyGrammar#msg}.
	 * @param ctx the parse tree
	 */
	void enterMsg(ObjectPropertyGrammar.MsgContext ctx);
	/**
	 * Exit a parse tree produced by {@link ObjectPropertyGrammar#msg}.
	 * @param ctx the parse tree
	 */
	void exitMsg(ObjectPropertyGrammar.MsgContext ctx);
	/**
	 * Enter a parse tree produced by {@link ObjectPropertyGrammar#bindui}.
	 * @param ctx the parse tree
	 */
	void enterBindui(ObjectPropertyGrammar.BinduiContext ctx);
	/**
	 * Exit a parse tree produced by {@link ObjectPropertyGrammar#bindui}.
	 * @param ctx the parse tree
	 */
	void exitBindui(ObjectPropertyGrammar.BinduiContext ctx);
	/**
	 * Enter a parse tree produced by {@link ObjectPropertyGrammar#desc}.
	 * @param ctx the parse tree
	 */
	void enterDesc(ObjectPropertyGrammar.DescContext ctx);
	/**
	 * Exit a parse tree produced by {@link ObjectPropertyGrammar#desc}.
	 * @param ctx the parse tree
	 */
	void exitDesc(ObjectPropertyGrammar.DescContext ctx);
	/**
	 * Enter a parse tree produced by {@link ObjectPropertyGrammar#objectProperty}.
	 * @param ctx the parse tree
	 */
	void enterObjectProperty(ObjectPropertyGrammar.ObjectPropertyContext ctx);
	/**
	 * Exit a parse tree produced by {@link ObjectPropertyGrammar#objectProperty}.
	 * @param ctx the parse tree
	 */
	void exitObjectProperty(ObjectPropertyGrammar.ObjectPropertyContext ctx);
	/**
	 * Enter a parse tree produced by {@link ObjectPropertyGrammar#file}.
	 * @param ctx the parse tree
	 */
	void enterFile(ObjectPropertyGrammar.FileContext ctx);
	/**
	 * Exit a parse tree produced by {@link ObjectPropertyGrammar#file}.
	 * @param ctx the parse tree
	 */
	void exitFile(ObjectPropertyGrammar.FileContext ctx);
}