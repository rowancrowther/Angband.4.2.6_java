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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/parser/grammars/Realm.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.realm;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link RealmParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 *            operations with no return type.
 */
public interface RealmVisitor<T> extends ParseTreeVisitor<T> {
    /**
     * Visit a parse tree produced by {@link RealmParser#name}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitName(RealmParser.NameContext ctx);

    /**
     * Visit a parse tree produced by {@link RealmParser#stat}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitStat(RealmParser.StatContext ctx);

    /**
     * Visit a parse tree produced by {@link RealmParser#verb}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitVerb(RealmParser.VerbContext ctx);

    /**
     * Visit a parse tree produced by {@link RealmParser#spell_noun}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitSpell_noun(RealmParser.Spell_nounContext ctx);

    /**
     * Visit a parse tree produced by {@link RealmParser#book_noun}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitBook_noun(RealmParser.Book_nounContext ctx);

    /**
     * Visit a parse tree produced by {@link RealmParser#realm}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitRealm(RealmParser.RealmContext ctx);

    /**
     * Visit a parse tree produced by {@link RealmParser#file}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFile(RealmParser.FileContext ctx);
}