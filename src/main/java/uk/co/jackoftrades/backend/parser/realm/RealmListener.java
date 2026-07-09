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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/parser/grammars/Realm.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.realm;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link RealmParser}.
 */
public interface RealmListener extends ParseTreeListener {
    /**
     * Enter a parse tree produced by {@link RealmParser#name}.
     *
     * @param ctx the parse tree
     */
    void enterName(RealmParser.NameContext ctx);

    /**
     * Exit a parse tree produced by {@link RealmParser#name}.
     *
     * @param ctx the parse tree
     */
    void exitName(RealmParser.NameContext ctx);

    /**
     * Enter a parse tree produced by {@link RealmParser#stat}.
     *
     * @param ctx the parse tree
     */
    void enterStat(RealmParser.StatContext ctx);

    /**
     * Exit a parse tree produced by {@link RealmParser#stat}.
     *
     * @param ctx the parse tree
     */
    void exitStat(RealmParser.StatContext ctx);

    /**
     * Enter a parse tree produced by {@link RealmParser#verb}.
     *
     * @param ctx the parse tree
     */
    void enterVerb(RealmParser.VerbContext ctx);

    /**
     * Exit a parse tree produced by {@link RealmParser#verb}.
     *
     * @param ctx the parse tree
     */
    void exitVerb(RealmParser.VerbContext ctx);

    /**
     * Enter a parse tree produced by {@link RealmParser#spell_noun}.
     *
     * @param ctx the parse tree
     */
    void enterSpell_noun(RealmParser.Spell_nounContext ctx);

    /**
     * Exit a parse tree produced by {@link RealmParser#spell_noun}.
     *
     * @param ctx the parse tree
     */
    void exitSpell_noun(RealmParser.Spell_nounContext ctx);

    /**
     * Enter a parse tree produced by {@link RealmParser#book_noun}.
     *
     * @param ctx the parse tree
     */
    void enterBook_noun(RealmParser.Book_nounContext ctx);

    /**
     * Exit a parse tree produced by {@link RealmParser#book_noun}.
     *
     * @param ctx the parse tree
     */
    void exitBook_noun(RealmParser.Book_nounContext ctx);

    /**
     * Enter a parse tree produced by {@link RealmParser#realm}.
     *
     * @param ctx the parse tree
     */
    void enterRealm(RealmParser.RealmContext ctx);

    /**
     * Exit a parse tree produced by {@link RealmParser#realm}.
     *
     * @param ctx the parse tree
     */
    void exitRealm(RealmParser.RealmContext ctx);

    /**
     * Enter a parse tree produced by {@link RealmParser#file}.
     *
     * @param ctx the parse tree
     */
    void enterFile(RealmParser.FileContext ctx);

    /**
     * Exit a parse tree produced by {@link RealmParser#file}.
     *
     * @param ctx the parse tree
     */
    void exitFile(RealmParser.FileContext ctx);
}