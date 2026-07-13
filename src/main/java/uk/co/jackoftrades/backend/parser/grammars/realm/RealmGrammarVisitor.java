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

// Generated from RealmGrammar.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.grammars.realm;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link RealmGrammar}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 *            operations with no return type.
 */
public interface RealmGrammarVisitor<T> extends ParseTreeVisitor<T> {
    /**
     * Visit a parse tree produced by {@link RealmGrammar#recordCount}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitRecordCount(RealmGrammar.RecordCountContext ctx);

    /**
     * Visit a parse tree produced by {@link RealmGrammar#name}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitName(RealmGrammar.NameContext ctx);

    /**
     * Visit a parse tree produced by {@link RealmGrammar#stat}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitStat(RealmGrammar.StatContext ctx);

    /**
     * Visit a parse tree produced by {@link RealmGrammar#verb}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitVerb(RealmGrammar.VerbContext ctx);

    /**
     * Visit a parse tree produced by {@link RealmGrammar#spellNoun}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitSpellNoun(RealmGrammar.SpellNounContext ctx);

    /**
     * Visit a parse tree produced by {@link RealmGrammar#bookNoun}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitBookNoun(RealmGrammar.BookNounContext ctx);

    /**
     * Visit a parse tree produced by {@link RealmGrammar#realm}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitRealm(RealmGrammar.RealmContext ctx);

    /**
     * Visit a parse tree produced by {@link RealmGrammar#file}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFile(RealmGrammar.FileContext ctx);
}