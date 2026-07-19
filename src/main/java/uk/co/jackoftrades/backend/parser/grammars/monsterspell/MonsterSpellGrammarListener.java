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

// Generated from MonsterSpellGrammar.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.grammars.monsterspell;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link MonsterSpellGrammar}.
 */
public interface MonsterSpellGrammarListener extends ParseTreeListener {
    /**
     * Enter a parse tree produced by {@link MonsterSpellGrammar#recordCount}.
     *
     * @param ctx the parse tree
     */
    void enterRecordCount(MonsterSpellGrammar.RecordCountContext ctx);

    /**
     * Exit a parse tree produced by {@link MonsterSpellGrammar#recordCount}.
     *
     * @param ctx the parse tree
     */
    void exitRecordCount(MonsterSpellGrammar.RecordCountContext ctx);

    /**
     * Enter a parse tree produced by {@link MonsterSpellGrammar#name}.
     *
     * @param ctx the parse tree
     */
    void enterName(MonsterSpellGrammar.NameContext ctx);

    /**
     * Exit a parse tree produced by {@link MonsterSpellGrammar#name}.
     *
     * @param ctx the parse tree
     */
    void exitName(MonsterSpellGrammar.NameContext ctx);

    /**
     * Enter a parse tree produced by {@link MonsterSpellGrammar#msgt}.
     *
     * @param ctx the parse tree
     */
    void enterMsgt(MonsterSpellGrammar.MsgtContext ctx);

    /**
     * Exit a parse tree produced by {@link MonsterSpellGrammar#msgt}.
     *
     * @param ctx the parse tree
     */
    void exitMsgt(MonsterSpellGrammar.MsgtContext ctx);

    /**
     * Enter a parse tree produced by {@link MonsterSpellGrammar#hit}.
     *
     * @param ctx the parse tree
     */
    void enterHit(MonsterSpellGrammar.HitContext ctx);

    /**
     * Exit a parse tree produced by {@link MonsterSpellGrammar#hit}.
     *
     * @param ctx the parse tree
     */
    void exitHit(MonsterSpellGrammar.HitContext ctx);

    /**
     * Enter a parse tree produced by {@link MonsterSpellGrammar#powerCutoff}.
     *
     * @param ctx the parse tree
     */
    void enterPowerCutoff(MonsterSpellGrammar.PowerCutoffContext ctx);

    /**
     * Exit a parse tree produced by {@link MonsterSpellGrammar#powerCutoff}.
     *
     * @param ctx the parse tree
     */
    void exitPowerCutoff(MonsterSpellGrammar.PowerCutoffContext ctx);

    /**
     * Enter a parse tree produced by {@link MonsterSpellGrammar#lore}.
     *
     * @param ctx the parse tree
     */
    void enterLore(MonsterSpellGrammar.LoreContext ctx);

    /**
     * Exit a parse tree produced by {@link MonsterSpellGrammar#lore}.
     *
     * @param ctx the parse tree
     */
    void exitLore(MonsterSpellGrammar.LoreContext ctx);

    /**
     * Enter a parse tree produced by {@link MonsterSpellGrammar#loreColourBase}.
     *
     * @param ctx the parse tree
     */
    void enterLoreColourBase(MonsterSpellGrammar.LoreColourBaseContext ctx);

    /**
     * Exit a parse tree produced by {@link MonsterSpellGrammar#loreColourBase}.
     *
     * @param ctx the parse tree
     */
    void exitLoreColourBase(MonsterSpellGrammar.LoreColourBaseContext ctx);

    /**
     * Enter a parse tree produced by {@link MonsterSpellGrammar#loreColourResist}.
     *
     * @param ctx the parse tree
     */
    void enterLoreColourResist(MonsterSpellGrammar.LoreColourResistContext ctx);

    /**
     * Exit a parse tree produced by {@link MonsterSpellGrammar#loreColourResist}.
     *
     * @param ctx the parse tree
     */
    void exitLoreColourResist(MonsterSpellGrammar.LoreColourResistContext ctx);

    /**
     * Enter a parse tree produced by {@link MonsterSpellGrammar#loreColourImmune}.
     *
     * @param ctx the parse tree
     */
    void enterLoreColourImmune(MonsterSpellGrammar.LoreColourImmuneContext ctx);

    /**
     * Exit a parse tree produced by {@link MonsterSpellGrammar#loreColourImmune}.
     *
     * @param ctx the parse tree
     */
    void exitLoreColourImmune(MonsterSpellGrammar.LoreColourImmuneContext ctx);

    /**
     * Enter a parse tree produced by {@link MonsterSpellGrammar#messageSave}.
     *
     * @param ctx the parse tree
     */
    void enterMessageSave(MonsterSpellGrammar.MessageSaveContext ctx);

    /**
     * Exit a parse tree produced by {@link MonsterSpellGrammar#messageSave}.
     *
     * @param ctx the parse tree
     */
    void exitMessageSave(MonsterSpellGrammar.MessageSaveContext ctx);

    /**
     * Enter a parse tree produced by {@link MonsterSpellGrammar#messageVis}.
     *
     * @param ctx the parse tree
     */
    void enterMessageVis(MonsterSpellGrammar.MessageVisContext ctx);

    /**
     * Exit a parse tree produced by {@link MonsterSpellGrammar#messageVis}.
     *
     * @param ctx the parse tree
     */
    void exitMessageVis(MonsterSpellGrammar.MessageVisContext ctx);

    /**
     * Enter a parse tree produced by {@link MonsterSpellGrammar#messageInvis}.
     *
     * @param ctx the parse tree
     */
    void enterMessageInvis(MonsterSpellGrammar.MessageInvisContext ctx);

    /**
     * Exit a parse tree produced by {@link MonsterSpellGrammar#messageInvis}.
     *
     * @param ctx the parse tree
     */
    void exitMessageInvis(MonsterSpellGrammar.MessageInvisContext ctx);

    /**
     * Enter a parse tree produced by {@link MonsterSpellGrammar#messageMiss}.
     *
     * @param ctx the parse tree
     */
    void enterMessageMiss(MonsterSpellGrammar.MessageMissContext ctx);

    /**
     * Exit a parse tree produced by {@link MonsterSpellGrammar#messageMiss}.
     *
     * @param ctx the parse tree
     */
    void exitMessageMiss(MonsterSpellGrammar.MessageMissContext ctx);

    /**
     * Enter a parse tree produced by {@link MonsterSpellGrammar#powerCutoffBlock}.
     *
     * @param ctx the parse tree
     */
    void enterPowerCutoffBlock(MonsterSpellGrammar.PowerCutoffBlockContext ctx);

    /**
     * Exit a parse tree produced by {@link MonsterSpellGrammar#powerCutoffBlock}.
     *
     * @param ctx the parse tree
     */
    void exitPowerCutoffBlock(MonsterSpellGrammar.PowerCutoffBlockContext ctx);

    /**
     * Enter a parse tree produced by {@link MonsterSpellGrammar#monsterSpell}.
     *
     * @param ctx the parse tree
     */
    void enterMonsterSpell(MonsterSpellGrammar.MonsterSpellContext ctx);

    /**
     * Exit a parse tree produced by {@link MonsterSpellGrammar#monsterSpell}.
     *
     * @param ctx the parse tree
     */
    void exitMonsterSpell(MonsterSpellGrammar.MonsterSpellContext ctx);

    /**
     * Enter a parse tree produced by {@link MonsterSpellGrammar#file}.
     *
     * @param ctx the parse tree
     */
    void enterFile(MonsterSpellGrammar.FileContext ctx);

    /**
     * Exit a parse tree produced by {@link MonsterSpellGrammar#file}.
     *
     * @param ctx the parse tree
     */
    void exitFile(MonsterSpellGrammar.FileContext ctx);

    /**
     * Enter a parse tree produced by {@link MonsterSpellGrammar#effect}.
     *
     * @param ctx the parse tree
     */
    void enterEffect(MonsterSpellGrammar.EffectContext ctx);

    /**
     * Exit a parse tree produced by {@link MonsterSpellGrammar#effect}.
     *
     * @param ctx the parse tree
     */
    void exitEffect(MonsterSpellGrammar.EffectContext ctx);

    /**
     * Enter a parse tree produced by {@link MonsterSpellGrammar#time}.
     *
     * @param ctx the parse tree
     */
    void enterTime(MonsterSpellGrammar.TimeContext ctx);

    /**
     * Exit a parse tree produced by {@link MonsterSpellGrammar#time}.
     *
     * @param ctx the parse tree
     */
    void exitTime(MonsterSpellGrammar.TimeContext ctx);

    /**
     * Enter a parse tree produced by {@link MonsterSpellGrammar#effectYX}.
     *
     * @param ctx the parse tree
     */
    void enterEffectYX(MonsterSpellGrammar.EffectYXContext ctx);

    /**
     * Exit a parse tree produced by {@link MonsterSpellGrammar#effectYX}.
     *
     * @param ctx the parse tree
     */
    void exitEffectYX(MonsterSpellGrammar.EffectYXContext ctx);

    /**
     * Enter a parse tree produced by {@link MonsterSpellGrammar#dice}.
     *
     * @param ctx the parse tree
     */
    void enterDice(MonsterSpellGrammar.DiceContext ctx);

    /**
     * Exit a parse tree produced by {@link MonsterSpellGrammar#dice}.
     *
     * @param ctx the parse tree
     */
    void exitDice(MonsterSpellGrammar.DiceContext ctx);

    /**
     * Enter a parse tree produced by {@link MonsterSpellGrammar#expr}.
     *
     * @param ctx the parse tree
     */
    void enterExpr(MonsterSpellGrammar.ExprContext ctx);

    /**
     * Exit a parse tree produced by {@link MonsterSpellGrammar#expr}.
     *
     * @param ctx the parse tree
     */
    void exitExpr(MonsterSpellGrammar.ExprContext ctx);

    /**
     * Enter a parse tree produced by {@link MonsterSpellGrammar#effectMsg}.
     *
     * @param ctx the parse tree
     */
    void enterEffectMsg(MonsterSpellGrammar.EffectMsgContext ctx);

    /**
     * Exit a parse tree produced by {@link MonsterSpellGrammar#effectMsg}.
     *
     * @param ctx the parse tree
     */
    void exitEffectMsg(MonsterSpellGrammar.EffectMsgContext ctx);

    /**
     * Enter a parse tree produced by {@link MonsterSpellGrammar#effectBlock}.
     *
     * @param ctx the parse tree
     */
    void enterEffectBlock(MonsterSpellGrammar.EffectBlockContext ctx);

    /**
     * Exit a parse tree produced by {@link MonsterSpellGrammar#effectBlock}.
     *
     * @param ctx the parse tree
     */
    void exitEffectBlock(MonsterSpellGrammar.EffectBlockContext ctx);
}