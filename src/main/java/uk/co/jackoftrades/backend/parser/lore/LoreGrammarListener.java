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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/parser/grammars/LoreGrammar.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.lore;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link LoreGrammar}.
 */
public interface LoreGrammarListener extends ParseTreeListener {
    /**
     * Enter a parse tree produced by {@link LoreGrammar#name}.
     *
     * @param ctx the parse tree
     */
    void enterName(LoreGrammar.NameContext ctx);

    /**
     * Exit a parse tree produced by {@link LoreGrammar#name}.
     *
     * @param ctx the parse tree
     */
    void exitName(LoreGrammar.NameContext ctx);

    /**
     * Enter a parse tree produced by {@link LoreGrammar#counts}.
     *
     * @param ctx the parse tree
     */
    void enterCounts(LoreGrammar.CountsContext ctx);

    /**
     * Exit a parse tree produced by {@link LoreGrammar#counts}.
     *
     * @param ctx the parse tree
     */
    void exitCounts(LoreGrammar.CountsContext ctx);

    /**
     * Enter a parse tree produced by {@link LoreGrammar#blow}.
     *
     * @param ctx the parse tree
     */
    void enterBlow(LoreGrammar.BlowContext ctx);

    /**
     * Exit a parse tree produced by {@link LoreGrammar#blow}.
     *
     * @param ctx the parse tree
     */
    void exitBlow(LoreGrammar.BlowContext ctx);

    /**
     * Enter a parse tree produced by {@link LoreGrammar#flags}.
     *
     * @param ctx the parse tree
     */
    void enterFlags(LoreGrammar.FlagsContext ctx);

    /**
     * Exit a parse tree produced by {@link LoreGrammar#flags}.
     *
     * @param ctx the parse tree
     */
    void exitFlags(LoreGrammar.FlagsContext ctx);

    /**
     * Enter a parse tree produced by {@link LoreGrammar#spells}.
     *
     * @param ctx the parse tree
     */
    void enterSpells(LoreGrammar.SpellsContext ctx);

    /**
     * Exit a parse tree produced by {@link LoreGrammar#spells}.
     *
     * @param ctx the parse tree
     */
    void exitSpells(LoreGrammar.SpellsContext ctx);

    /**
     * Enter a parse tree produced by {@link LoreGrammar#base}.
     *
     * @param ctx the parse tree
     */
    void enterBase(LoreGrammar.BaseContext ctx);

    /**
     * Exit a parse tree produced by {@link LoreGrammar#base}.
     *
     * @param ctx the parse tree
     */
    void exitBase(LoreGrammar.BaseContext ctx);

    /**
     * Enter a parse tree produced by {@link LoreGrammar#drop}.
     *
     * @param ctx the parse tree
     */
    void enterDrop(LoreGrammar.DropContext ctx);

    /**
     * Exit a parse tree produced by {@link LoreGrammar#drop}.
     *
     * @param ctx the parse tree
     */
    void exitDrop(LoreGrammar.DropContext ctx);

    /**
     * Enter a parse tree produced by {@link LoreGrammar#dropBase}.
     *
     * @param ctx the parse tree
     */
    void enterDropBase(LoreGrammar.DropBaseContext ctx);

    /**
     * Exit a parse tree produced by {@link LoreGrammar#dropBase}.
     *
     * @param ctx the parse tree
     */
    void exitDropBase(LoreGrammar.DropBaseContext ctx);

    /**
     * Enter a parse tree produced by {@link LoreGrammar#friends}.
     *
     * @param ctx the parse tree
     */
    void enterFriends(LoreGrammar.FriendsContext ctx);

    /**
     * Exit a parse tree produced by {@link LoreGrammar#friends}.
     *
     * @param ctx the parse tree
     */
    void exitFriends(LoreGrammar.FriendsContext ctx);

    /**
     * Enter a parse tree produced by {@link LoreGrammar#friendsBase}.
     *
     * @param ctx the parse tree
     */
    void enterFriendsBase(LoreGrammar.FriendsBaseContext ctx);

    /**
     * Exit a parse tree produced by {@link LoreGrammar#friendsBase}.
     *
     * @param ctx the parse tree
     */
    void exitFriendsBase(LoreGrammar.FriendsBaseContext ctx);

    /**
     * Enter a parse tree produced by {@link LoreGrammar#mimic}.
     *
     * @param ctx the parse tree
     */
    void enterMimic(LoreGrammar.MimicContext ctx);

    /**
     * Exit a parse tree produced by {@link LoreGrammar#mimic}.
     *
     * @param ctx the parse tree
     */
    void exitMimic(LoreGrammar.MimicContext ctx);

    /**
     * Enter a parse tree produced by {@link LoreGrammar#monsterLore}.
     *
     * @param ctx the parse tree
     */
    void enterMonsterLore(LoreGrammar.MonsterLoreContext ctx);

    /**
     * Exit a parse tree produced by {@link LoreGrammar#monsterLore}.
     *
     * @param ctx the parse tree
     */
    void exitMonsterLore(LoreGrammar.MonsterLoreContext ctx);

    /**
     * Enter a parse tree produced by {@link LoreGrammar#file}.
     *
     * @param ctx the parse tree
     */
    void enterFile(LoreGrammar.FileContext ctx);

    /**
     * Exit a parse tree produced by {@link LoreGrammar#file}.
     *
     * @param ctx the parse tree
     */
    void exitFile(LoreGrammar.FileContext ctx);
}