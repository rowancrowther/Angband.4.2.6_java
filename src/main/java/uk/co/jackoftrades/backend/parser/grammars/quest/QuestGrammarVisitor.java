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

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link QuestGrammar}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 *            operations with no return type.
 */
public interface QuestGrammarVisitor<T> extends ParseTreeVisitor<T> {
    /**
     * Visit a parse tree produced by {@link QuestGrammar#recordCount}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitRecordCount(QuestGrammar.RecordCountContext ctx);

    /**
     * Visit a parse tree produced by {@link QuestGrammar#name}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitName(QuestGrammar.NameContext ctx);

    /**
     * Visit a parse tree produced by {@link QuestGrammar#level}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitLevel(QuestGrammar.LevelContext ctx);

    /**
     * Visit a parse tree produced by {@link QuestGrammar#race}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitRace(QuestGrammar.RaceContext ctx);

    /**
     * Visit a parse tree produced by {@link QuestGrammar#number}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitNumber(QuestGrammar.NumberContext ctx);

    /**
     * Visit a parse tree produced by {@link QuestGrammar#quest}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitQuest(QuestGrammar.QuestContext ctx);

    /**
     * Visit a parse tree produced by {@link QuestGrammar#file}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFile(QuestGrammar.FileContext ctx);
}