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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/parser/grammars/MonsterSpell.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.monsterspell;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link MonsterSpellParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 *            operations with no return type.
 */
public interface MonsterSpellVisitor<T> extends ParseTreeVisitor<T> {
    /**
     * Visit a parse tree produced by {@link MonsterSpellParser#name}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitName(MonsterSpellParser.NameContext ctx);

    /**
     * Visit a parse tree produced by {@link MonsterSpellParser#msgt}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitMsgt(MonsterSpellParser.MsgtContext ctx);

    /**
     * Visit a parse tree produced by {@link MonsterSpellParser#hit}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitHit(MonsterSpellParser.HitContext ctx);

    /**
     * Visit a parse tree produced by {@link MonsterSpellParser#effect}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitEffect(MonsterSpellParser.EffectContext ctx);

    /**
     * Visit a parse tree produced by {@link MonsterSpellParser#effectYX}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitEffectYX(MonsterSpellParser.EffectYXContext ctx);

    /**
     * Visit a parse tree produced by {@link MonsterSpellParser#dice}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitDice(MonsterSpellParser.DiceContext ctx);

    /**
     * Visit a parse tree produced by {@link MonsterSpellParser#expr}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitExpr(MonsterSpellParser.ExprContext ctx);

    /**
     * Visit a parse tree produced by {@link MonsterSpellParser#effectBlock}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitEffectBlock(MonsterSpellParser.EffectBlockContext ctx);

    /**
     * Visit a parse tree produced by {@link MonsterSpellParser#powerCutoff}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitPowerCutoff(MonsterSpellParser.PowerCutoffContext ctx);

    /**
     * Visit a parse tree produced by {@link MonsterSpellParser#lore}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitLore(MonsterSpellParser.LoreContext ctx);

    /**
     * Visit a parse tree produced by {@link MonsterSpellParser#loreColourBase}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitLoreColourBase(MonsterSpellParser.LoreColourBaseContext ctx);

    /**
     * Visit a parse tree produced by {@link MonsterSpellParser#loreColourResist}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitLoreColourResist(MonsterSpellParser.LoreColourResistContext ctx);

    /**
     * Visit a parse tree produced by {@link MonsterSpellParser#loreColourImmune}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitLoreColourImmune(MonsterSpellParser.LoreColourImmuneContext ctx);

    /**
     * Visit a parse tree produced by {@link MonsterSpellParser#messageSave}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitMessageSave(MonsterSpellParser.MessageSaveContext ctx);

    /**
     * Visit a parse tree produced by {@link MonsterSpellParser#messageVis}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitMessageVis(MonsterSpellParser.MessageVisContext ctx);

    /**
     * Visit a parse tree produced by {@link MonsterSpellParser#messageInvis}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitMessageInvis(MonsterSpellParser.MessageInvisContext ctx);

    /**
     * Visit a parse tree produced by {@link MonsterSpellParser#messageMiss}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitMessageMiss(MonsterSpellParser.MessageMissContext ctx);

    /**
     * Visit a parse tree produced by {@link MonsterSpellParser#monsterSpellLevel}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitMonsterSpellLevel(MonsterSpellParser.MonsterSpellLevelContext ctx);

    /**
     * Visit a parse tree produced by {@link MonsterSpellParser#monsterSpellType}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitMonsterSpellType(MonsterSpellParser.MonsterSpellTypeContext ctx);

    /**
     * Visit a parse tree produced by {@link MonsterSpellParser#file}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFile(MonsterSpellParser.FileContext ctx);
}