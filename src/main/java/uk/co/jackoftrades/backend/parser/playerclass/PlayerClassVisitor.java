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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/parser/grammars/PlayerClass.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.playerclass;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link PlayerClassParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface PlayerClassVisitor<T> extends ParseTreeVisitor<T> {
    /**
     * Visit a parse tree produced by {@link PlayerClassParser#name}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitName(PlayerClassParser.NameContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerClassParser#stats}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitStats(PlayerClassParser.StatsContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerClassParser#skillDisarmPhys}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitSkillDisarmPhys(PlayerClassParser.SkillDisarmPhysContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerClassParser#skillDisarmMagic}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitSkillDisarmMagic(PlayerClassParser.SkillDisarmMagicContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerClassParser#skillDevice}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitSkillDevice(PlayerClassParser.SkillDeviceContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerClassParser#skillSave}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitSkillSave(PlayerClassParser.SkillSaveContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerClassParser#skillStealth}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitSkillStealth(PlayerClassParser.SkillStealthContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerClassParser#skillSearch}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitSkillSearch(PlayerClassParser.SkillSearchContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerClassParser#skillMelee}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitSkillMelee(PlayerClassParser.SkillMeleeContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerClassParser#skillShoot}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitSkillShoot(PlayerClassParser.SkillShootContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerClassParser#skillThrow}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitSkillThrow(PlayerClassParser.SkillThrowContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerClassParser#skillDig}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitSkillDig(PlayerClassParser.SkillDigContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerClassParser#hitdie}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitHitdie(PlayerClassParser.HitdieContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerClassParser#maxAttacks}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitMaxAttacks(PlayerClassParser.MaxAttacksContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerClassParser#minWeight}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitMinWeight(PlayerClassParser.MinWeightContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerClassParser#strengthMultiplier}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitStrengthMultiplier(PlayerClassParser.StrengthMultiplierContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerClassParser#equip}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitEquip(PlayerClassParser.EquipContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerClassParser#equipBlock}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitEquipBlock(PlayerClassParser.EquipBlockContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerClassParser#objectFlags}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitObjectFlags(PlayerClassParser.ObjectFlagsContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerClassParser#playerFlags}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitPlayerFlags(PlayerClassParser.PlayerFlagsContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerClassParser#title}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitTitle(PlayerClassParser.TitleContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerClassParser#titleBlock}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitTitleBlock(PlayerClassParser.TitleBlockContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerClassParser#magic}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitMagic(PlayerClassParser.MagicContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerClassParser#book}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitBook(PlayerClassParser.BookContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerClassParser#bookGraphics}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitBookGraphics(PlayerClassParser.BookGraphicsContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerClassParser#bookProperties}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitBookProperties(PlayerClassParser.BookPropertiesContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerClassParser#spell}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitSpell(PlayerClassParser.SpellContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerClassParser#effect}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitEffect(PlayerClassParser.EffectContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerClassParser#effectMsg}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitEffectMsg(PlayerClassParser.EffectMsgContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerClassParser#effectYX}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitEffectYX(PlayerClassParser.EffectYXContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerClassParser#dice}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitDice(PlayerClassParser.DiceContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerClassParser#effectBlock}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitEffectBlock(PlayerClassParser.EffectBlockContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerClassParser#expr}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitExpr(PlayerClassParser.ExprContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerClassParser#desc}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitDesc(PlayerClassParser.DescContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerClassParser#spellBlock}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitSpellBlock(PlayerClassParser.SpellBlockContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerClassParser#bookBlock}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitBookBlock(PlayerClassParser.BookBlockContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerClassParser#playerClass}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitPlayerClass(PlayerClassParser.PlayerClassContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerClassParser#file}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFile(PlayerClassParser.FileContext ctx);
}