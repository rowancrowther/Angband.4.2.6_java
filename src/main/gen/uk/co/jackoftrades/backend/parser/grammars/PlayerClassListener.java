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
package uk.co.jackoftrades.backend.parser.grammars;

    import uk.co.jackoftrades.middle.objects.enums.TValue;
    import uk.co.jackoftrades.middle.player.StartItem;
    import uk.co.jackoftrades.middle.player.PlayerShape;
    import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;
    import uk.co.jackoftrades.middle.objects.ObjectKind;
    import uk.co.jackoftrades.middle.player.enums.PlayerFlag;
    import uk.co.jackoftrades.backend.utils.Flag;
    import uk.co.jackoftrades.middle.magic.ClassMagic;
    import uk.co.jackoftrades.middle.objects.ObjectBase;
    import uk.co.jackoftrades.middle.magic.MagicBook;
    import uk.co.jackoftrades.middle.magic.MagicRealm;
    import uk.co.jackoftrades.middle.magic.MagicSpell;
    import uk.co.jackoftrades.middle.game.globals.GameConstants;
    import uk.co.jackoftrades.backend.strings.AngbandDisplayCharacter;
    import uk.co.jackoftrades.backend.io.bespokeexceptions.InvalidTokenFoundDuringParse;
    import uk.co.jackoftrades.middle.enums.EffectEnum;
    import uk.co.jackoftrades.middle.enums.GlyphType;
    import uk.co.jackoftrades.middle.enums.EffectEnchant;
    import uk.co.jackoftrades.middle.effect.EffectSubTypeEnum;
    import uk.co.jackoftrades.middle.effect.EffectSubTypeWrapper;
    import uk.co.jackoftrades.middle.player.enums.TimedEffect;
    import uk.co.jackoftrades.middle.combat.enums.ProjectionEnum;
    import uk.co.jackoftrades.middle.enums.EffectNourish;
    import uk.co.jackoftrades.middle.effect.Expression;
    import uk.co.jackoftrades.middle.effect.Earthquake;
    import uk.co.jackoftrades.middle.effect.Effect;
    import uk.co.jackoftrades.middle.enums.Stats;
    import uk.co.jackoftrades.middle.monsters.Summon;
    import uk.co.jackoftrades.middle.enums.EffectBaseType;
    import uk.co.jackoftrades.middle.player.PlayerClass;
    import uk.co.jackoftrades.middle.player.enums.PlayerSkill;

    import java.util.List;
    import java.util.ArrayList;
    import java.util.Map;
    import java.util.HashMap;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link PlayerClassParser}.
 */
public interface PlayerClassListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link PlayerClassParser#name}.
	 * @param ctx the parse tree
	 */
	void enterName(PlayerClassParser.NameContext ctx);
	/**
	 * Exit a parse tree produced by {@link PlayerClassParser#name}.
	 * @param ctx the parse tree
	 */
	void exitName(PlayerClassParser.NameContext ctx);
	/**
	 * Enter a parse tree produced by {@link PlayerClassParser#stats}.
	 * @param ctx the parse tree
	 */
	void enterStats(PlayerClassParser.StatsContext ctx);
	/**
	 * Exit a parse tree produced by {@link PlayerClassParser#stats}.
	 * @param ctx the parse tree
	 */
	void exitStats(PlayerClassParser.StatsContext ctx);
	/**
	 * Enter a parse tree produced by {@link PlayerClassParser#skillDisarmPhys}.
	 * @param ctx the parse tree
	 */
	void enterSkillDisarmPhys(PlayerClassParser.SkillDisarmPhysContext ctx);
	/**
	 * Exit a parse tree produced by {@link PlayerClassParser#skillDisarmPhys}.
	 * @param ctx the parse tree
	 */
	void exitSkillDisarmPhys(PlayerClassParser.SkillDisarmPhysContext ctx);
	/**
	 * Enter a parse tree produced by {@link PlayerClassParser#skillDisarmMagic}.
	 * @param ctx the parse tree
	 */
	void enterSkillDisarmMagic(PlayerClassParser.SkillDisarmMagicContext ctx);
	/**
	 * Exit a parse tree produced by {@link PlayerClassParser#skillDisarmMagic}.
	 * @param ctx the parse tree
	 */
	void exitSkillDisarmMagic(PlayerClassParser.SkillDisarmMagicContext ctx);
	/**
	 * Enter a parse tree produced by {@link PlayerClassParser#skillDevice}.
	 * @param ctx the parse tree
	 */
	void enterSkillDevice(PlayerClassParser.SkillDeviceContext ctx);
	/**
	 * Exit a parse tree produced by {@link PlayerClassParser#skillDevice}.
	 * @param ctx the parse tree
	 */
	void exitSkillDevice(PlayerClassParser.SkillDeviceContext ctx);
	/**
	 * Enter a parse tree produced by {@link PlayerClassParser#skillSave}.
	 * @param ctx the parse tree
	 */
	void enterSkillSave(PlayerClassParser.SkillSaveContext ctx);
	/**
	 * Exit a parse tree produced by {@link PlayerClassParser#skillSave}.
	 * @param ctx the parse tree
	 */
	void exitSkillSave(PlayerClassParser.SkillSaveContext ctx);
	/**
	 * Enter a parse tree produced by {@link PlayerClassParser#skillStealth}.
	 * @param ctx the parse tree
	 */
	void enterSkillStealth(PlayerClassParser.SkillStealthContext ctx);
	/**
	 * Exit a parse tree produced by {@link PlayerClassParser#skillStealth}.
	 * @param ctx the parse tree
	 */
	void exitSkillStealth(PlayerClassParser.SkillStealthContext ctx);
	/**
	 * Enter a parse tree produced by {@link PlayerClassParser#skillSearch}.
	 * @param ctx the parse tree
	 */
	void enterSkillSearch(PlayerClassParser.SkillSearchContext ctx);
	/**
	 * Exit a parse tree produced by {@link PlayerClassParser#skillSearch}.
	 * @param ctx the parse tree
	 */
	void exitSkillSearch(PlayerClassParser.SkillSearchContext ctx);
	/**
	 * Enter a parse tree produced by {@link PlayerClassParser#skillMelee}.
	 * @param ctx the parse tree
	 */
	void enterSkillMelee(PlayerClassParser.SkillMeleeContext ctx);
	/**
	 * Exit a parse tree produced by {@link PlayerClassParser#skillMelee}.
	 * @param ctx the parse tree
	 */
	void exitSkillMelee(PlayerClassParser.SkillMeleeContext ctx);
	/**
	 * Enter a parse tree produced by {@link PlayerClassParser#skillShoot}.
	 * @param ctx the parse tree
	 */
	void enterSkillShoot(PlayerClassParser.SkillShootContext ctx);
	/**
	 * Exit a parse tree produced by {@link PlayerClassParser#skillShoot}.
	 * @param ctx the parse tree
	 */
	void exitSkillShoot(PlayerClassParser.SkillShootContext ctx);
	/**
	 * Enter a parse tree produced by {@link PlayerClassParser#skillThrow}.
	 * @param ctx the parse tree
	 */
	void enterSkillThrow(PlayerClassParser.SkillThrowContext ctx);
	/**
	 * Exit a parse tree produced by {@link PlayerClassParser#skillThrow}.
	 * @param ctx the parse tree
	 */
	void exitSkillThrow(PlayerClassParser.SkillThrowContext ctx);
	/**
	 * Enter a parse tree produced by {@link PlayerClassParser#skillDig}.
	 * @param ctx the parse tree
	 */
	void enterSkillDig(PlayerClassParser.SkillDigContext ctx);
	/**
	 * Exit a parse tree produced by {@link PlayerClassParser#skillDig}.
	 * @param ctx the parse tree
	 */
	void exitSkillDig(PlayerClassParser.SkillDigContext ctx);
	/**
	 * Enter a parse tree produced by {@link PlayerClassParser#hitdie}.
	 * @param ctx the parse tree
	 */
	void enterHitdie(PlayerClassParser.HitdieContext ctx);
	/**
	 * Exit a parse tree produced by {@link PlayerClassParser#hitdie}.
	 * @param ctx the parse tree
	 */
	void exitHitdie(PlayerClassParser.HitdieContext ctx);
	/**
	 * Enter a parse tree produced by {@link PlayerClassParser#maxAttacks}.
	 * @param ctx the parse tree
	 */
	void enterMaxAttacks(PlayerClassParser.MaxAttacksContext ctx);
	/**
	 * Exit a parse tree produced by {@link PlayerClassParser#maxAttacks}.
	 * @param ctx the parse tree
	 */
	void exitMaxAttacks(PlayerClassParser.MaxAttacksContext ctx);
	/**
	 * Enter a parse tree produced by {@link PlayerClassParser#minWeight}.
	 * @param ctx the parse tree
	 */
	void enterMinWeight(PlayerClassParser.MinWeightContext ctx);
	/**
	 * Exit a parse tree produced by {@link PlayerClassParser#minWeight}.
	 * @param ctx the parse tree
	 */
	void exitMinWeight(PlayerClassParser.MinWeightContext ctx);
	/**
	 * Enter a parse tree produced by {@link PlayerClassParser#strengthMultiplier}.
	 * @param ctx the parse tree
	 */
	void enterStrengthMultiplier(PlayerClassParser.StrengthMultiplierContext ctx);
	/**
	 * Exit a parse tree produced by {@link PlayerClassParser#strengthMultiplier}.
	 * @param ctx the parse tree
	 */
	void exitStrengthMultiplier(PlayerClassParser.StrengthMultiplierContext ctx);
	/**
	 * Enter a parse tree produced by {@link PlayerClassParser#equip}.
	 * @param ctx the parse tree
	 */
	void enterEquip(PlayerClassParser.EquipContext ctx);
	/**
	 * Exit a parse tree produced by {@link PlayerClassParser#equip}.
	 * @param ctx the parse tree
	 */
	void exitEquip(PlayerClassParser.EquipContext ctx);
	/**
	 * Enter a parse tree produced by {@link PlayerClassParser#equipBlock}.
	 * @param ctx the parse tree
	 */
	void enterEquipBlock(PlayerClassParser.EquipBlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link PlayerClassParser#equipBlock}.
	 * @param ctx the parse tree
	 */
	void exitEquipBlock(PlayerClassParser.EquipBlockContext ctx);
	/**
	 * Enter a parse tree produced by {@link PlayerClassParser#objectFlags}.
	 * @param ctx the parse tree
	 */
	void enterObjectFlags(PlayerClassParser.ObjectFlagsContext ctx);
	/**
	 * Exit a parse tree produced by {@link PlayerClassParser#objectFlags}.
	 * @param ctx the parse tree
	 */
	void exitObjectFlags(PlayerClassParser.ObjectFlagsContext ctx);
	/**
	 * Enter a parse tree produced by {@link PlayerClassParser#playerFlags}.
	 * @param ctx the parse tree
	 */
	void enterPlayerFlags(PlayerClassParser.PlayerFlagsContext ctx);
	/**
	 * Exit a parse tree produced by {@link PlayerClassParser#playerFlags}.
	 * @param ctx the parse tree
	 */
	void exitPlayerFlags(PlayerClassParser.PlayerFlagsContext ctx);
	/**
	 * Enter a parse tree produced by {@link PlayerClassParser#title}.
	 * @param ctx the parse tree
	 */
	void enterTitle(PlayerClassParser.TitleContext ctx);
	/**
	 * Exit a parse tree produced by {@link PlayerClassParser#title}.
	 * @param ctx the parse tree
	 */
	void exitTitle(PlayerClassParser.TitleContext ctx);
	/**
	 * Enter a parse tree produced by {@link PlayerClassParser#titleBlock}.
	 * @param ctx the parse tree
	 */
	void enterTitleBlock(PlayerClassParser.TitleBlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link PlayerClassParser#titleBlock}.
	 * @param ctx the parse tree
	 */
	void exitTitleBlock(PlayerClassParser.TitleBlockContext ctx);
	/**
	 * Enter a parse tree produced by {@link PlayerClassParser#magic}.
	 * @param ctx the parse tree
	 */
	void enterMagic(PlayerClassParser.MagicContext ctx);
	/**
	 * Exit a parse tree produced by {@link PlayerClassParser#magic}.
	 * @param ctx the parse tree
	 */
	void exitMagic(PlayerClassParser.MagicContext ctx);
	/**
	 * Enter a parse tree produced by {@link PlayerClassParser#book}.
	 * @param ctx the parse tree
	 */
	void enterBook(PlayerClassParser.BookContext ctx);
	/**
	 * Exit a parse tree produced by {@link PlayerClassParser#book}.
	 * @param ctx the parse tree
	 */
	void exitBook(PlayerClassParser.BookContext ctx);
	/**
	 * Enter a parse tree produced by {@link PlayerClassParser#bookGraphics}.
	 * @param ctx the parse tree
	 */
	void enterBookGraphics(PlayerClassParser.BookGraphicsContext ctx);
	/**
	 * Exit a parse tree produced by {@link PlayerClassParser#bookGraphics}.
	 * @param ctx the parse tree
	 */
	void exitBookGraphics(PlayerClassParser.BookGraphicsContext ctx);
	/**
	 * Enter a parse tree produced by {@link PlayerClassParser#bookProperties}.
	 * @param ctx the parse tree
	 */
	void enterBookProperties(PlayerClassParser.BookPropertiesContext ctx);
	/**
	 * Exit a parse tree produced by {@link PlayerClassParser#bookProperties}.
	 * @param ctx the parse tree
	 */
	void exitBookProperties(PlayerClassParser.BookPropertiesContext ctx);
	/**
	 * Enter a parse tree produced by {@link PlayerClassParser#spell}.
	 * @param ctx the parse tree
	 */
	void enterSpell(PlayerClassParser.SpellContext ctx);
	/**
	 * Exit a parse tree produced by {@link PlayerClassParser#spell}.
	 * @param ctx the parse tree
	 */
	void exitSpell(PlayerClassParser.SpellContext ctx);
	/**
	 * Enter a parse tree produced by {@link PlayerClassParser#effect}.
	 * @param ctx the parse tree
	 */
	void enterEffect(PlayerClassParser.EffectContext ctx);
	/**
	 * Exit a parse tree produced by {@link PlayerClassParser#effect}.
	 * @param ctx the parse tree
	 */
	void exitEffect(PlayerClassParser.EffectContext ctx);
	/**
	 * Enter a parse tree produced by {@link PlayerClassParser#effectMsg}.
	 * @param ctx the parse tree
	 */
	void enterEffectMsg(PlayerClassParser.EffectMsgContext ctx);
	/**
	 * Exit a parse tree produced by {@link PlayerClassParser#effectMsg}.
	 * @param ctx the parse tree
	 */
	void exitEffectMsg(PlayerClassParser.EffectMsgContext ctx);
	/**
	 * Enter a parse tree produced by {@link PlayerClassParser#effectYX}.
	 * @param ctx the parse tree
	 */
	void enterEffectYX(PlayerClassParser.EffectYXContext ctx);
	/**
	 * Exit a parse tree produced by {@link PlayerClassParser#effectYX}.
	 * @param ctx the parse tree
	 */
	void exitEffectYX(PlayerClassParser.EffectYXContext ctx);
	/**
	 * Enter a parse tree produced by {@link PlayerClassParser#dice}.
	 * @param ctx the parse tree
	 */
	void enterDice(PlayerClassParser.DiceContext ctx);
	/**
	 * Exit a parse tree produced by {@link PlayerClassParser#dice}.
	 * @param ctx the parse tree
	 */
	void exitDice(PlayerClassParser.DiceContext ctx);
	/**
	 * Enter a parse tree produced by {@link PlayerClassParser#effectBlock}.
	 * @param ctx the parse tree
	 */
	void enterEffectBlock(PlayerClassParser.EffectBlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link PlayerClassParser#effectBlock}.
	 * @param ctx the parse tree
	 */
	void exitEffectBlock(PlayerClassParser.EffectBlockContext ctx);
	/**
	 * Enter a parse tree produced by {@link PlayerClassParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterExpr(PlayerClassParser.ExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link PlayerClassParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitExpr(PlayerClassParser.ExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link PlayerClassParser#desc}.
	 * @param ctx the parse tree
	 */
	void enterDesc(PlayerClassParser.DescContext ctx);
	/**
	 * Exit a parse tree produced by {@link PlayerClassParser#desc}.
	 * @param ctx the parse tree
	 */
	void exitDesc(PlayerClassParser.DescContext ctx);
	/**
	 * Enter a parse tree produced by {@link PlayerClassParser#spellBlock}.
	 * @param ctx the parse tree
	 */
	void enterSpellBlock(PlayerClassParser.SpellBlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link PlayerClassParser#spellBlock}.
	 * @param ctx the parse tree
	 */
	void exitSpellBlock(PlayerClassParser.SpellBlockContext ctx);
	/**
	 * Enter a parse tree produced by {@link PlayerClassParser#bookBlock}.
	 * @param ctx the parse tree
	 */
	void enterBookBlock(PlayerClassParser.BookBlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link PlayerClassParser#bookBlock}.
	 * @param ctx the parse tree
	 */
	void exitBookBlock(PlayerClassParser.BookBlockContext ctx);
	/**
	 * Enter a parse tree produced by {@link PlayerClassParser#playerClass}.
	 * @param ctx the parse tree
	 */
	void enterPlayerClass(PlayerClassParser.PlayerClassContext ctx);
	/**
	 * Exit a parse tree produced by {@link PlayerClassParser#playerClass}.
	 * @param ctx the parse tree
	 */
	void exitPlayerClass(PlayerClassParser.PlayerClassContext ctx);
	/**
	 * Enter a parse tree produced by {@link PlayerClassParser#file}.
	 * @param ctx the parse tree
	 */
	void enterFile(PlayerClassParser.FileContext ctx);
	/**
	 * Exit a parse tree produced by {@link PlayerClassParser#file}.
	 * @param ctx the parse tree
	 */
	void exitFile(PlayerClassParser.FileContext ctx);
}