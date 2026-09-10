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

// Generated from src/main/java/uk/co/jackoftradesltd/backend/parser/grammars/LoreGrammar.g4 by ANTLR 4.13.2
package uk.co.jackoftradesltd.backend.parser.lore;

import uk.co.jackoftradesltd.middle.numerics.Random;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.combat.BlowMethod;
import uk.co.jackoftradesltd.middle.game.globals.registry.MonsterRegistry;
import uk.co.jackoftradesltd.middle.game.globals.registry.ObjectRegistry;
import uk.co.jackoftradesltd.middle.monsters.BlowEffect;
import uk.co.jackoftradesltd.middle.monsters.MonsterBase;
import uk.co.jackoftradesltd.middle.monsters.MonsterBlow;
import uk.co.jackoftradesltd.middle.monsters.MonsterDrop;
import uk.co.jackoftradesltd.middle.monsters.MonsterFriends;
import uk.co.jackoftradesltd.middle.monsters.MonsterFriendsBase;
import uk.co.jackoftradesltd.middle.monsters.MonsterLore;
import uk.co.jackoftradesltd.middle.monsters.MonsterMimic;
import uk.co.jackoftradesltd.middle.monsters.MonsterRace;
import uk.co.jackoftradesltd.middle.monsters.enums.MonsterGroupRole;
import uk.co.jackoftradesltd.middle.monsters.enums.MonsterRaceFlag;
import uk.co.jackoftradesltd.middle.monsters.enums.MonsterSpell;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;
import uk.co.jackoftradesltd.middle.objects.ObjectKind;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link LoreGrammar}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface LoreGrammarVisitor<T> extends ParseTreeVisitor<T> {
    /**
     * Visit a parse tree produced by {@link LoreGrammar#name}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitName(LoreGrammar.NameContext ctx);

    /**
     * Visit a parse tree produced by {@link LoreGrammar#counts}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitCounts(LoreGrammar.CountsContext ctx);

    /**
     * Visit a parse tree produced by {@link LoreGrammar#blow}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitBlow(LoreGrammar.BlowContext ctx);

    /**
     * Visit a parse tree produced by {@link LoreGrammar#flags}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFlags(LoreGrammar.FlagsContext ctx);

    /**
     * Visit a parse tree produced by {@link LoreGrammar#spells}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitSpells(LoreGrammar.SpellsContext ctx);

    /**
     * Visit a parse tree produced by {@link LoreGrammar#base}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitBase(LoreGrammar.BaseContext ctx);

    /**
     * Visit a parse tree produced by {@link LoreGrammar#drop}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitDrop(LoreGrammar.DropContext ctx);

    /**
     * Visit a parse tree produced by {@link LoreGrammar#dropBase}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitDropBase(LoreGrammar.DropBaseContext ctx);

    /**
     * Visit a parse tree produced by {@link LoreGrammar#friends}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFriends(LoreGrammar.FriendsContext ctx);

    /**
     * Visit a parse tree produced by {@link LoreGrammar#friendsBase}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFriendsBase(LoreGrammar.FriendsBaseContext ctx);

    /**
     * Visit a parse tree produced by {@link LoreGrammar#mimic}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitMimic(LoreGrammar.MimicContext ctx);

    /**
     * Visit a parse tree produced by {@link LoreGrammar#monsterLore}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitMonsterLore(LoreGrammar.MonsterLoreContext ctx);

    /**
     * Visit a parse tree produced by {@link LoreGrammar#file}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFile(LoreGrammar.FileContext ctx);
}