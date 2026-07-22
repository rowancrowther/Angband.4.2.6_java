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
// Generated from QuestGrammar.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.grammars.quest;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link QuestGrammar}.
 */
public interface QuestGrammarListener extends ParseTreeListener {
    /**
     * Enter a parse tree produced by {@link QuestGrammar#recordCount}.
     *
     * @param ctx the parse tree
     */
    void enterRecordCount(QuestGrammar.RecordCountContext ctx);

    /**
     * Exit a parse tree produced by {@link QuestGrammar#recordCount}.
     *
     * @param ctx the parse tree
     */
    void exitRecordCount(QuestGrammar.RecordCountContext ctx);

    /**
     * Enter a parse tree produced by {@link QuestGrammar#name}.
     *
     * @param ctx the parse tree
     */
    void enterName(QuestGrammar.NameContext ctx);

    /**
     * Exit a parse tree produced by {@link QuestGrammar#name}.
     *
     * @param ctx the parse tree
     */
    void exitName(QuestGrammar.NameContext ctx);

    /**
     * Enter a parse tree produced by {@link QuestGrammar#level}.
     *
     * @param ctx the parse tree
     */
    void enterLevel(QuestGrammar.LevelContext ctx);

    /**
     * Exit a parse tree produced by {@link QuestGrammar#level}.
     *
     * @param ctx the parse tree
     */
    void exitLevel(QuestGrammar.LevelContext ctx);

    /**
     * Enter a parse tree produced by {@link QuestGrammar#race}.
     *
     * @param ctx the parse tree
     */
    void enterRace(QuestGrammar.RaceContext ctx);

    /**
     * Exit a parse tree produced by {@link QuestGrammar#race}.
     *
     * @param ctx the parse tree
     */
    void exitRace(QuestGrammar.RaceContext ctx);

    /**
     * Enter a parse tree produced by {@link QuestGrammar#number}.
     *
     * @param ctx the parse tree
     */
    void enterNumber(QuestGrammar.NumberContext ctx);

    /**
     * Exit a parse tree produced by {@link QuestGrammar#number}.
     *
     * @param ctx the parse tree
     */
    void exitNumber(QuestGrammar.NumberContext ctx);

    /**
     * Enter a parse tree produced by {@link QuestGrammar#quest}.
     *
     * @param ctx the parse tree
     */
    void enterQuest(QuestGrammar.QuestContext ctx);

    /**
     * Exit a parse tree produced by {@link QuestGrammar#quest}.
     *
     * @param ctx the parse tree
     */
    void exitQuest(QuestGrammar.QuestContext ctx);

    /**
     * Enter a parse tree produced by {@link QuestGrammar#file}.
     *
     * @param ctx the parse tree
     */
    void enterFile(QuestGrammar.FileContext ctx);

    /**
     * Exit a parse tree produced by {@link QuestGrammar#file}.
     *
     * @param ctx the parse tree
     */
    void exitFile(QuestGrammar.FileContext ctx);
}