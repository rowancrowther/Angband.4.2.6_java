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
// Generated from PlayerPropertyGrammar.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.grammars.playerproperty;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link PlayerPropertyGrammar}.
 */
public interface PlayerPropertyGrammarListener extends ParseTreeListener {
    /**
     * Enter a parse tree produced by {@link PlayerPropertyGrammar#recordCount}.
     *
     * @param ctx the parse tree
     */
    void enterRecordCount(PlayerPropertyGrammar.RecordCountContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerPropertyGrammar#recordCount}.
     *
     * @param ctx the parse tree
     */
    void exitRecordCount(PlayerPropertyGrammar.RecordCountContext ctx);

    /**
     * Enter a parse tree produced by {@link PlayerPropertyGrammar#type}.
     *
     * @param ctx the parse tree
     */
    void enterType(PlayerPropertyGrammar.TypeContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerPropertyGrammar#type}.
     *
     * @param ctx the parse tree
     */
    void exitType(PlayerPropertyGrammar.TypeContext ctx);

    /**
     * Enter a parse tree produced by {@link PlayerPropertyGrammar#code}.
     *
     * @param ctx the parse tree
     */
    void enterCode(PlayerPropertyGrammar.CodeContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerPropertyGrammar#code}.
     *
     * @param ctx the parse tree
     */
    void exitCode(PlayerPropertyGrammar.CodeContext ctx);

    /**
     * Enter a parse tree produced by {@link PlayerPropertyGrammar#binduiEntry}.
     *
     * @param ctx the parse tree
     */
    void enterBinduiEntry(PlayerPropertyGrammar.BinduiEntryContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerPropertyGrammar#binduiEntry}.
     *
     * @param ctx the parse tree
     */
    void exitBinduiEntry(PlayerPropertyGrammar.BinduiEntryContext ctx);

    /**
     * Enter a parse tree produced by {@link PlayerPropertyGrammar#bindUI}.
     *
     * @param ctx the parse tree
     */
    void enterBindUI(PlayerPropertyGrammar.BindUIContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerPropertyGrammar#bindUI}.
     *
     * @param ctx the parse tree
     */
    void exitBindUI(PlayerPropertyGrammar.BindUIContext ctx);

    /**
     * Enter a parse tree produced by {@link PlayerPropertyGrammar#name}.
     *
     * @param ctx the parse tree
     */
    void enterName(PlayerPropertyGrammar.NameContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerPropertyGrammar#name}.
     *
     * @param ctx the parse tree
     */
    void exitName(PlayerPropertyGrammar.NameContext ctx);

    /**
     * Enter a parse tree produced by {@link PlayerPropertyGrammar#desc}.
     *
     * @param ctx the parse tree
     */
    void enterDesc(PlayerPropertyGrammar.DescContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerPropertyGrammar#desc}.
     *
     * @param ctx the parse tree
     */
    void exitDesc(PlayerPropertyGrammar.DescContext ctx);

    /**
     * Enter a parse tree produced by {@link PlayerPropertyGrammar#value}.
     *
     * @param ctx the parse tree
     */
    void enterValue(PlayerPropertyGrammar.ValueContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerPropertyGrammar#value}.
     *
     * @param ctx the parse tree
     */
    void exitValue(PlayerPropertyGrammar.ValueContext ctx);

    /**
     * Enter a parse tree produced by {@link PlayerPropertyGrammar#property}.
     *
     * @param ctx the parse tree
     */
    void enterProperty(PlayerPropertyGrammar.PropertyContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerPropertyGrammar#property}.
     *
     * @param ctx the parse tree
     */
    void exitProperty(PlayerPropertyGrammar.PropertyContext ctx);

    /**
     * Enter a parse tree produced by {@link PlayerPropertyGrammar#file}.
     *
     * @param ctx the parse tree
     */
    void enterFile(PlayerPropertyGrammar.FileContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerPropertyGrammar#file}.
     *
     * @param ctx the parse tree
     */
    void exitFile(PlayerPropertyGrammar.FileContext ctx);
}