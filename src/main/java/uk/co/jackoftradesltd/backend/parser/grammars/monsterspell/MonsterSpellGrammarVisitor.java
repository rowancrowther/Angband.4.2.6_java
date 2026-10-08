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
package uk.co.jackoftradesltd.backend.parser.grammars.monsterspell;

    import uk.co.jackoftradesltd.backend.parser.monsterspell.MonsterSpellParseRecord;
    import uk.co.jackoftradesltd.backend.parser.monsterspell.MonsterSpellParseRecord.MonsterSpellLevelParseRecord;
    import uk.co.jackoftradesltd.backend.parser.grammars.EffectParseRecord;

    import java.util.ArrayList;
    import java.util.List;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link MonsterSpellGrammar}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface MonsterSpellGrammarVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link MonsterSpellGrammar#recordCount}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRecordCount(MonsterSpellGrammar.RecordCountContext ctx);
	/**
	 * Visit a parse tree produced by {@link MonsterSpellGrammar#name}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitName(MonsterSpellGrammar.NameContext ctx);
	/**
	 * Visit a parse tree produced by {@link MonsterSpellGrammar#msgt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMsgt(MonsterSpellGrammar.MsgtContext ctx);
	/**
	 * Visit a parse tree produced by {@link MonsterSpellGrammar#hit}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHit(MonsterSpellGrammar.HitContext ctx);
	/**
	 * Visit a parse tree produced by {@link MonsterSpellGrammar#powerCutoff}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPowerCutoff(MonsterSpellGrammar.PowerCutoffContext ctx);
	/**
	 * Visit a parse tree produced by {@link MonsterSpellGrammar#lore}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLore(MonsterSpellGrammar.LoreContext ctx);
	/**
	 * Visit a parse tree produced by {@link MonsterSpellGrammar#loreColourBase}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLoreColourBase(MonsterSpellGrammar.LoreColourBaseContext ctx);
	/**
	 * Visit a parse tree produced by {@link MonsterSpellGrammar#loreColourResist}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLoreColourResist(MonsterSpellGrammar.LoreColourResistContext ctx);
	/**
	 * Visit a parse tree produced by {@link MonsterSpellGrammar#loreColourImmune}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLoreColourImmune(MonsterSpellGrammar.LoreColourImmuneContext ctx);
	/**
	 * Visit a parse tree produced by {@link MonsterSpellGrammar#messageSave}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMessageSave(MonsterSpellGrammar.MessageSaveContext ctx);
	/**
	 * Visit a parse tree produced by {@link MonsterSpellGrammar#messageVis}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMessageVis(MonsterSpellGrammar.MessageVisContext ctx);
	/**
	 * Visit a parse tree produced by {@link MonsterSpellGrammar#messageInvis}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMessageInvis(MonsterSpellGrammar.MessageInvisContext ctx);
	/**
	 * Visit a parse tree produced by {@link MonsterSpellGrammar#messageMiss}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMessageMiss(MonsterSpellGrammar.MessageMissContext ctx);
	/**
	 * Visit a parse tree produced by {@link MonsterSpellGrammar#powerCutoffBlock}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPowerCutoffBlock(MonsterSpellGrammar.PowerCutoffBlockContext ctx);
	/**
	 * Visit a parse tree produced by {@link MonsterSpellGrammar#monsterSpell}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMonsterSpell(MonsterSpellGrammar.MonsterSpellContext ctx);
	/**
	 * Visit a parse tree produced by {@link MonsterSpellGrammar#file}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFile(MonsterSpellGrammar.FileContext ctx);
	/**
	 * Visit a parse tree produced by {@link MonsterSpellGrammar#effect}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEffect(MonsterSpellGrammar.EffectContext ctx);
	/**
	 * Visit a parse tree produced by {@link MonsterSpellGrammar#effectYX}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEffectYX(MonsterSpellGrammar.EffectYXContext ctx);
	/**
	 * Visit a parse tree produced by {@link MonsterSpellGrammar#dice}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDice(MonsterSpellGrammar.DiceContext ctx);
	/**
	 * Visit a parse tree produced by {@link MonsterSpellGrammar#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpr(MonsterSpellGrammar.ExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link MonsterSpellGrammar#effectMsg}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEffectMsg(MonsterSpellGrammar.EffectMsgContext ctx);
	/**
	 * Visit a parse tree produced by {@link MonsterSpellGrammar#time}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTime(MonsterSpellGrammar.TimeContext ctx);
	/**
	 * Visit a parse tree produced by {@link MonsterSpellGrammar#effectBlock}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEffectBlock(MonsterSpellGrammar.EffectBlockContext ctx);
}