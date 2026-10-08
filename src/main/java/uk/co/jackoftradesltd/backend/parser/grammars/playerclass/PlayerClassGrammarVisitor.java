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

// Generated from PlayerClassGrammar.g4 by ANTLR 4.13.2
package uk.co.jackoftradesltd.backend.parser.grammars.playerclass;

    import uk.co.jackoftradesltd.backend.parser.playerclass.*;
    import uk.co.jackoftradesltd.backend.parser.grammars.EffectParseRecord;

    import java.util.List;
    import java.util.ArrayList;
    import java.util.Map;
    import java.util.HashMap;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link PlayerClassGrammar}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface PlayerClassGrammarVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link PlayerClassGrammar#recordCount}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRecordCount(PlayerClassGrammar.RecordCountContext ctx);
	/**
	 * Visit a parse tree produced by {@link PlayerClassGrammar#name}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitName(PlayerClassGrammar.NameContext ctx);
	/**
	 * Visit a parse tree produced by {@link PlayerClassGrammar#stats}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStats(PlayerClassGrammar.StatsContext ctx);
	/**
	 * Visit a parse tree produced by {@link PlayerClassGrammar#skillDisarmPhys}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSkillDisarmPhys(PlayerClassGrammar.SkillDisarmPhysContext ctx);
	/**
	 * Visit a parse tree produced by {@link PlayerClassGrammar#skillDisarmMagic}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSkillDisarmMagic(PlayerClassGrammar.SkillDisarmMagicContext ctx);
	/**
	 * Visit a parse tree produced by {@link PlayerClassGrammar#skillDevice}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSkillDevice(PlayerClassGrammar.SkillDeviceContext ctx);
	/**
	 * Visit a parse tree produced by {@link PlayerClassGrammar#skillSave}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSkillSave(PlayerClassGrammar.SkillSaveContext ctx);
	/**
	 * Visit a parse tree produced by {@link PlayerClassGrammar#skillStealth}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSkillStealth(PlayerClassGrammar.SkillStealthContext ctx);
	/**
	 * Visit a parse tree produced by {@link PlayerClassGrammar#skillSearch}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSkillSearch(PlayerClassGrammar.SkillSearchContext ctx);
	/**
	 * Visit a parse tree produced by {@link PlayerClassGrammar#skillMelee}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSkillMelee(PlayerClassGrammar.SkillMeleeContext ctx);
	/**
	 * Visit a parse tree produced by {@link PlayerClassGrammar#skillShoot}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSkillShoot(PlayerClassGrammar.SkillShootContext ctx);
	/**
	 * Visit a parse tree produced by {@link PlayerClassGrammar#skillThrow}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSkillThrow(PlayerClassGrammar.SkillThrowContext ctx);
	/**
	 * Visit a parse tree produced by {@link PlayerClassGrammar#skillDig}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSkillDig(PlayerClassGrammar.SkillDigContext ctx);
	/**
	 * Visit a parse tree produced by {@link PlayerClassGrammar#hitdie}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHitdie(PlayerClassGrammar.HitdieContext ctx);
	/**
	 * Visit a parse tree produced by {@link PlayerClassGrammar#maxAttacks}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMaxAttacks(PlayerClassGrammar.MaxAttacksContext ctx);
	/**
	 * Visit a parse tree produced by {@link PlayerClassGrammar#minWeight}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMinWeight(PlayerClassGrammar.MinWeightContext ctx);
	/**
	 * Visit a parse tree produced by {@link PlayerClassGrammar#strengthMultiplier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStrengthMultiplier(PlayerClassGrammar.StrengthMultiplierContext ctx);
	/**
	 * Visit a parse tree produced by {@link PlayerClassGrammar#title}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTitle(PlayerClassGrammar.TitleContext ctx);
	/**
	 * Visit a parse tree produced by {@link PlayerClassGrammar#equip}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEquip(PlayerClassGrammar.EquipContext ctx);
	/**
	 * Visit a parse tree produced by {@link PlayerClassGrammar#objFlag}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitObjFlag(PlayerClassGrammar.ObjFlagContext ctx);
	/**
	 * Visit a parse tree produced by {@link PlayerClassGrammar#playerFlags}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPlayerFlags(PlayerClassGrammar.PlayerFlagsContext ctx);
	/**
	 * Visit a parse tree produced by {@link PlayerClassGrammar#exp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExp(PlayerClassGrammar.ExpContext ctx);
	/**
	 * Visit a parse tree produced by {@link PlayerClassGrammar#magic}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMagic(PlayerClassGrammar.MagicContext ctx);
	/**
	 * Visit a parse tree produced by {@link PlayerClassGrammar#magicBlock}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMagicBlock(PlayerClassGrammar.MagicBlockContext ctx);
	/**
	 * Visit a parse tree produced by {@link PlayerClassGrammar#book}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBook(PlayerClassGrammar.BookContext ctx);
	/**
	 * Visit a parse tree produced by {@link PlayerClassGrammar#bookGraphics}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBookGraphics(PlayerClassGrammar.BookGraphicsContext ctx);
	/**
	 * Visit a parse tree produced by {@link PlayerClassGrammar#bookProperties}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBookProperties(PlayerClassGrammar.BookPropertiesContext ctx);
	/**
	 * Visit a parse tree produced by {@link PlayerClassGrammar#bookBlock}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBookBlock(PlayerClassGrammar.BookBlockContext ctx);
	/**
	 * Visit a parse tree produced by {@link PlayerClassGrammar#spell}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSpell(PlayerClassGrammar.SpellContext ctx);
	/**
	 * Visit a parse tree produced by {@link PlayerClassGrammar#desc}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDesc(PlayerClassGrammar.DescContext ctx);
	/**
	 * Visit a parse tree produced by {@link PlayerClassGrammar#spellBlock}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSpellBlock(PlayerClassGrammar.SpellBlockContext ctx);
	/**
	 * Visit a parse tree produced by {@link PlayerClassGrammar#playerClass}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPlayerClass(PlayerClassGrammar.PlayerClassContext ctx);
	/**
	 * Visit a parse tree produced by {@link PlayerClassGrammar#file}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFile(PlayerClassGrammar.FileContext ctx);
	/**
	 * Visit a parse tree produced by {@link PlayerClassGrammar#effect}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEffect(PlayerClassGrammar.EffectContext ctx);
	/**
	 * Visit a parse tree produced by {@link PlayerClassGrammar#effectYX}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEffectYX(PlayerClassGrammar.EffectYXContext ctx);
	/**
	 * Visit a parse tree produced by {@link PlayerClassGrammar#dice}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDice(PlayerClassGrammar.DiceContext ctx);
	/**
	 * Visit a parse tree produced by {@link PlayerClassGrammar#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpr(PlayerClassGrammar.ExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link PlayerClassGrammar#effectMsg}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEffectMsg(PlayerClassGrammar.EffectMsgContext ctx);
	/**
	 * Visit a parse tree produced by {@link PlayerClassGrammar#time}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTime(PlayerClassGrammar.TimeContext ctx);
	/**
	 * Visit a parse tree produced by {@link PlayerClassGrammar#effectBlock}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEffectBlock(PlayerClassGrammar.EffectBlockContext ctx);
}