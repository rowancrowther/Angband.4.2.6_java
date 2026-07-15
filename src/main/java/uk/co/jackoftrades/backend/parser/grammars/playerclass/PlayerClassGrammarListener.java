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
package uk.co.jackoftrades.backend.parser.grammars.playerclass;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link PlayerClassGrammar}.
 */
public interface PlayerClassGrammarListener extends ParseTreeListener {
    /**
     * Enter a parse tree produced by {@link PlayerClassGrammar#recordCount}.
     *
     * @param ctx the parse tree
     */
    void enterRecordCount(PlayerClassGrammar.RecordCountContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerClassGrammar#recordCount}.
     *
     * @param ctx the parse tree
     */
    void exitRecordCount(PlayerClassGrammar.RecordCountContext ctx);

    /**
     * Enter a parse tree produced by {@link PlayerClassGrammar#name}.
     *
     * @param ctx the parse tree
     */
    void enterName(PlayerClassGrammar.NameContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerClassGrammar#name}.
     *
     * @param ctx the parse tree
     */
    void exitName(PlayerClassGrammar.NameContext ctx);

    /**
     * Enter a parse tree produced by {@link PlayerClassGrammar#stats}.
     *
     * @param ctx the parse tree
     */
    void enterStats(PlayerClassGrammar.StatsContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerClassGrammar#stats}.
     *
     * @param ctx the parse tree
     */
    void exitStats(PlayerClassGrammar.StatsContext ctx);

    /**
     * Enter a parse tree produced by {@link PlayerClassGrammar#skillDisarmPhys}.
     *
     * @param ctx the parse tree
     */
    void enterSkillDisarmPhys(PlayerClassGrammar.SkillDisarmPhysContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerClassGrammar#skillDisarmPhys}.
     *
     * @param ctx the parse tree
     */
    void exitSkillDisarmPhys(PlayerClassGrammar.SkillDisarmPhysContext ctx);

    /**
     * Enter a parse tree produced by {@link PlayerClassGrammar#skillDisarmMagic}.
     *
     * @param ctx the parse tree
     */
    void enterSkillDisarmMagic(PlayerClassGrammar.SkillDisarmMagicContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerClassGrammar#skillDisarmMagic}.
     *
     * @param ctx the parse tree
     */
    void exitSkillDisarmMagic(PlayerClassGrammar.SkillDisarmMagicContext ctx);

    /**
     * Enter a parse tree produced by {@link PlayerClassGrammar#skillDevice}.
     *
     * @param ctx the parse tree
     */
    void enterSkillDevice(PlayerClassGrammar.SkillDeviceContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerClassGrammar#skillDevice}.
     *
     * @param ctx the parse tree
     */
    void exitSkillDevice(PlayerClassGrammar.SkillDeviceContext ctx);

    /**
     * Enter a parse tree produced by {@link PlayerClassGrammar#skillSave}.
     *
     * @param ctx the parse tree
     */
    void enterSkillSave(PlayerClassGrammar.SkillSaveContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerClassGrammar#skillSave}.
     *
     * @param ctx the parse tree
     */
    void exitSkillSave(PlayerClassGrammar.SkillSaveContext ctx);

    /**
     * Enter a parse tree produced by {@link PlayerClassGrammar#skillStealth}.
     *
     * @param ctx the parse tree
     */
    void enterSkillStealth(PlayerClassGrammar.SkillStealthContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerClassGrammar#skillStealth}.
     *
     * @param ctx the parse tree
     */
    void exitSkillStealth(PlayerClassGrammar.SkillStealthContext ctx);

    /**
     * Enter a parse tree produced by {@link PlayerClassGrammar#skillSearch}.
     *
     * @param ctx the parse tree
     */
    void enterSkillSearch(PlayerClassGrammar.SkillSearchContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerClassGrammar#skillSearch}.
     *
     * @param ctx the parse tree
     */
    void exitSkillSearch(PlayerClassGrammar.SkillSearchContext ctx);

    /**
     * Enter a parse tree produced by {@link PlayerClassGrammar#skillMelee}.
     *
     * @param ctx the parse tree
     */
    void enterSkillMelee(PlayerClassGrammar.SkillMeleeContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerClassGrammar#skillMelee}.
     *
     * @param ctx the parse tree
     */
    void exitSkillMelee(PlayerClassGrammar.SkillMeleeContext ctx);

    /**
     * Enter a parse tree produced by {@link PlayerClassGrammar#skillShoot}.
     *
     * @param ctx the parse tree
     */
    void enterSkillShoot(PlayerClassGrammar.SkillShootContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerClassGrammar#skillShoot}.
     *
     * @param ctx the parse tree
     */
    void exitSkillShoot(PlayerClassGrammar.SkillShootContext ctx);

    /**
     * Enter a parse tree produced by {@link PlayerClassGrammar#skillThrow}.
     *
     * @param ctx the parse tree
     */
    void enterSkillThrow(PlayerClassGrammar.SkillThrowContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerClassGrammar#skillThrow}.
     *
     * @param ctx the parse tree
     */
    void exitSkillThrow(PlayerClassGrammar.SkillThrowContext ctx);

    /**
     * Enter a parse tree produced by {@link PlayerClassGrammar#skillDig}.
     *
     * @param ctx the parse tree
     */
    void enterSkillDig(PlayerClassGrammar.SkillDigContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerClassGrammar#skillDig}.
     *
     * @param ctx the parse tree
     */
    void exitSkillDig(PlayerClassGrammar.SkillDigContext ctx);

    /**
     * Enter a parse tree produced by {@link PlayerClassGrammar#hitdie}.
     *
     * @param ctx the parse tree
     */
    void enterHitdie(PlayerClassGrammar.HitdieContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerClassGrammar#hitdie}.
     *
     * @param ctx the parse tree
     */
    void exitHitdie(PlayerClassGrammar.HitdieContext ctx);

    /**
     * Enter a parse tree produced by {@link PlayerClassGrammar#maxAttacks}.
     *
     * @param ctx the parse tree
     */
    void enterMaxAttacks(PlayerClassGrammar.MaxAttacksContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerClassGrammar#maxAttacks}.
     *
     * @param ctx the parse tree
     */
    void exitMaxAttacks(PlayerClassGrammar.MaxAttacksContext ctx);

    /**
     * Enter a parse tree produced by {@link PlayerClassGrammar#minWeight}.
     *
     * @param ctx the parse tree
     */
    void enterMinWeight(PlayerClassGrammar.MinWeightContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerClassGrammar#minWeight}.
     *
     * @param ctx the parse tree
     */
    void exitMinWeight(PlayerClassGrammar.MinWeightContext ctx);

    /**
     * Enter a parse tree produced by {@link PlayerClassGrammar#strengthMultiplier}.
     *
     * @param ctx the parse tree
     */
    void enterStrengthMultiplier(PlayerClassGrammar.StrengthMultiplierContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerClassGrammar#strengthMultiplier}.
     *
     * @param ctx the parse tree
     */
    void exitStrengthMultiplier(PlayerClassGrammar.StrengthMultiplierContext ctx);

    /**
     * Enter a parse tree produced by {@link PlayerClassGrammar#title}.
     *
     * @param ctx the parse tree
     */
    void enterTitle(PlayerClassGrammar.TitleContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerClassGrammar#title}.
     *
     * @param ctx the parse tree
     */
    void exitTitle(PlayerClassGrammar.TitleContext ctx);

    /**
     * Enter a parse tree produced by {@link PlayerClassGrammar#equip}.
     *
     * @param ctx the parse tree
     */
    void enterEquip(PlayerClassGrammar.EquipContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerClassGrammar#equip}.
     *
     * @param ctx the parse tree
     */
    void exitEquip(PlayerClassGrammar.EquipContext ctx);

    /**
     * Enter a parse tree produced by {@link PlayerClassGrammar#objFlag}.
     *
     * @param ctx the parse tree
     */
    void enterObjFlag(PlayerClassGrammar.ObjFlagContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerClassGrammar#objFlag}.
     *
     * @param ctx the parse tree
     */
    void exitObjFlag(PlayerClassGrammar.ObjFlagContext ctx);

    /**
     * Enter a parse tree produced by {@link PlayerClassGrammar#playerFlags}.
     *
     * @param ctx the parse tree
     */
    void enterPlayerFlags(PlayerClassGrammar.PlayerFlagsContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerClassGrammar#playerFlags}.
     *
     * @param ctx the parse tree
     */
    void exitPlayerFlags(PlayerClassGrammar.PlayerFlagsContext ctx);

    /**
     * Enter a parse tree produced by {@link PlayerClassGrammar#exp}.
     *
     * @param ctx the parse tree
     */
    void enterExp(PlayerClassGrammar.ExpContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerClassGrammar#exp}.
     *
     * @param ctx the parse tree
     */
    void exitExp(PlayerClassGrammar.ExpContext ctx);

    /**
     * Enter a parse tree produced by {@link PlayerClassGrammar#magic}.
     *
     * @param ctx the parse tree
     */
    void enterMagic(PlayerClassGrammar.MagicContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerClassGrammar#magic}.
     *
     * @param ctx the parse tree
     */
    void exitMagic(PlayerClassGrammar.MagicContext ctx);

    /**
     * Enter a parse tree produced by {@link PlayerClassGrammar#magicBlock}.
     *
     * @param ctx the parse tree
     */
    void enterMagicBlock(PlayerClassGrammar.MagicBlockContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerClassGrammar#magicBlock}.
     *
     * @param ctx the parse tree
     */
    void exitMagicBlock(PlayerClassGrammar.MagicBlockContext ctx);

    /**
     * Enter a parse tree produced by {@link PlayerClassGrammar#book}.
     *
     * @param ctx the parse tree
     */
    void enterBook(PlayerClassGrammar.BookContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerClassGrammar#book}.
     *
     * @param ctx the parse tree
     */
    void exitBook(PlayerClassGrammar.BookContext ctx);

    /**
     * Enter a parse tree produced by {@link PlayerClassGrammar#bookGraphics}.
     *
     * @param ctx the parse tree
     */
    void enterBookGraphics(PlayerClassGrammar.BookGraphicsContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerClassGrammar#bookGraphics}.
     *
     * @param ctx the parse tree
     */
    void exitBookGraphics(PlayerClassGrammar.BookGraphicsContext ctx);

    /**
     * Enter a parse tree produced by {@link PlayerClassGrammar#bookProperties}.
     *
     * @param ctx the parse tree
     */
    void enterBookProperties(PlayerClassGrammar.BookPropertiesContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerClassGrammar#bookProperties}.
     *
     * @param ctx the parse tree
     */
    void exitBookProperties(PlayerClassGrammar.BookPropertiesContext ctx);

    /**
     * Enter a parse tree produced by {@link PlayerClassGrammar#bookBlock}.
     *
     * @param ctx the parse tree
     */
    void enterBookBlock(PlayerClassGrammar.BookBlockContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerClassGrammar#bookBlock}.
     *
     * @param ctx the parse tree
     */
    void exitBookBlock(PlayerClassGrammar.BookBlockContext ctx);

    /**
     * Enter a parse tree produced by {@link PlayerClassGrammar#spell}.
     *
     * @param ctx the parse tree
     */
    void enterSpell(PlayerClassGrammar.SpellContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerClassGrammar#spell}.
     *
     * @param ctx the parse tree
     */
    void exitSpell(PlayerClassGrammar.SpellContext ctx);

    /**
     * Enter a parse tree produced by {@link PlayerClassGrammar#desc}.
     *
     * @param ctx the parse tree
     */
    void enterDesc(PlayerClassGrammar.DescContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerClassGrammar#desc}.
     *
     * @param ctx the parse tree
     */
    void exitDesc(PlayerClassGrammar.DescContext ctx);

    /**
     * Enter a parse tree produced by {@link PlayerClassGrammar#spellBlock}.
     *
     * @param ctx the parse tree
     */
    void enterSpellBlock(PlayerClassGrammar.SpellBlockContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerClassGrammar#spellBlock}.
     *
     * @param ctx the parse tree
     */
    void exitSpellBlock(PlayerClassGrammar.SpellBlockContext ctx);

    /**
     * Enter a parse tree produced by {@link PlayerClassGrammar#playerClass}.
     *
     * @param ctx the parse tree
     */
    void enterPlayerClass(PlayerClassGrammar.PlayerClassContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerClassGrammar#playerClass}.
     *
     * @param ctx the parse tree
     */
    void exitPlayerClass(PlayerClassGrammar.PlayerClassContext ctx);

    /**
     * Enter a parse tree produced by {@link PlayerClassGrammar#file}.
     *
     * @param ctx the parse tree
     */
    void enterFile(PlayerClassGrammar.FileContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerClassGrammar#file}.
     *
     * @param ctx the parse tree
     */
    void exitFile(PlayerClassGrammar.FileContext ctx);

    /**
     * Enter a parse tree produced by {@link PlayerClassGrammar#effect}.
     *
     * @param ctx the parse tree
     */
    void enterEffect(PlayerClassGrammar.EffectContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerClassGrammar#effect}.
     *
     * @param ctx the parse tree
     */
    void exitEffect(PlayerClassGrammar.EffectContext ctx);

    /**
     * Enter a parse tree produced by {@link PlayerClassGrammar#time}.
     *
     * @param ctx the parse tree
     */
    void enterTime(PlayerClassGrammar.TimeContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerClassGrammar#time}.
     *
     * @param ctx the parse tree
     */
    void exitTime(PlayerClassGrammar.TimeContext ctx);

    /**
     * Enter a parse tree produced by {@link PlayerClassGrammar#effectYX}.
     *
     * @param ctx the parse tree
     */
    void enterEffectYX(PlayerClassGrammar.EffectYXContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerClassGrammar#effectYX}.
     *
     * @param ctx the parse tree
     */
    void exitEffectYX(PlayerClassGrammar.EffectYXContext ctx);

    /**
     * Enter a parse tree produced by {@link PlayerClassGrammar#dice}.
     *
     * @param ctx the parse tree
     */
    void enterDice(PlayerClassGrammar.DiceContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerClassGrammar#dice}.
     *
     * @param ctx the parse tree
     */
    void exitDice(PlayerClassGrammar.DiceContext ctx);

    /**
     * Enter a parse tree produced by {@link PlayerClassGrammar#expr}.
     *
     * @param ctx the parse tree
     */
    void enterExpr(PlayerClassGrammar.ExprContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerClassGrammar#expr}.
     *
     * @param ctx the parse tree
     */
    void exitExpr(PlayerClassGrammar.ExprContext ctx);

    /**
     * Enter a parse tree produced by {@link PlayerClassGrammar#effectMsg}.
     *
     * @param ctx the parse tree
     */
    void enterEffectMsg(PlayerClassGrammar.EffectMsgContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerClassGrammar#effectMsg}.
     *
     * @param ctx the parse tree
     */
    void exitEffectMsg(PlayerClassGrammar.EffectMsgContext ctx);

    /**
     * Enter a parse tree produced by {@link PlayerClassGrammar#effectBlock}.
     *
     * @param ctx the parse tree
     */
    void enterEffectBlock(PlayerClassGrammar.EffectBlockContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerClassGrammar#effectBlock}.
     *
     * @param ctx the parse tree
     */
    void exitEffectBlock(PlayerClassGrammar.EffectBlockContext ctx);
}