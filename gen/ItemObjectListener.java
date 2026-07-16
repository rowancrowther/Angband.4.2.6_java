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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/scratch/antlr_artifact_check/src/uk/co/jackoftrades/backend/parser/grammars/ItemObject.g4 by ANTLR 4.13.2

    import uk.co.jackoftrades.backend.strings.AngbandDisplayCharacter;
    import uk.co.jackoftrades.backend.io.bespokeexceptions.InvalidTokenFoundDuringParse;
    import uk.co.jackoftrades.middle.objects.ObjectBase;
    import uk.co.jackoftrades.middle.objects.Slay;
    import uk.co.jackoftrades.middle.objects.enums.ObjectModifier;
    import uk.co.jackoftrades.middle.game.globals.GameConstants;
    import uk.co.jackoftrades.middle.effect.Expression;
    import uk.co.jackoftrades.middle.enums.EffectBaseType;
    import uk.co.jackoftrades.middle.enums.EffectEnum;
    import uk.co.jackoftrades.middle.effect.EffectSubTypeEnum;
    import uk.co.jackoftrades.middle.player.enums.TimedEffect;
    import uk.co.jackoftrades.middle.combat.enums.ProjectionEnum;
    import uk.co.jackoftrades.middle.objects.ObjectKind;
    import uk.co.jackoftrades.middle.objects.ItemObject;
    import uk.co.jackoftrades.middle.objects.enums.TValue;
    import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;
    import uk.co.jackoftrades.middle.objects.enums.ObjectKindFlag;
    import uk.co.jackoftrades.middle.objects.enums.ElementEnum;
    import uk.co.jackoftrades.middle.effect.EffectSubTypeWrapper;
    import uk.co.jackoftrades.middle.objects.ElementInfo;
    import uk.co.jackoftrades.middle.objects.Brand;
    import uk.co.jackoftrades.middle.objects.CurseData;
    import uk.co.jackoftrades.middle.objects.Curse;
    import uk.co.jackoftrades.middle.Activation;
    import uk.co.jackoftrades.middle.objects.Flavour;
    import uk.co.jackoftrades.backend.strings.Quark;
    import uk.co.jackoftrades.middle.effect.Effect;
    import uk.co.jackoftrades.middle.objects.EgoItem;
    import uk.co.jackoftrades.middle.objects.Artifact;
    import uk.co.jackoftrades.middle.objects.enums.ObjectOriginEnum;
    import uk.co.jackoftrades.middle.monsters.MonsterRace;
    import uk.co.jackoftrades.middle.enums.Stats;
    import uk.co.jackoftrades.middle.cave.Loc;
    import uk.co.jackoftrades.backend.utils.Flag;
    import uk.co.jackoftrades.middle.objects.enums.ObjectNotice;
    import uk.co.jackoftrades.middle.enums.EffectNourish;
    import uk.co.jackoftrades.middle.enums.EffectEnchant;
    import uk.co.jackoftrades.middle.monsters.Summon;
    import uk.co.jackoftrades.frontend.colour.enums.ColourType;
    import uk.co.jackoftrades.frontend.colour.enums.ColourTranslation;

    import java.util.Map;
    import java.util.HashMap;
    import java.util.List;
    import java.util.ArrayList;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link ItemObjectParser}.
 */
public interface ItemObjectListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link ItemObjectParser#name}.
	 * @param ctx the parse tree
	 */
	void enterName(ItemObjectParser.NameContext ctx);
	/**
	 * Exit a parse tree produced by {@link ItemObjectParser#name}.
	 * @param ctx the parse tree
	 */
	void exitName(ItemObjectParser.NameContext ctx);
	/**
	 * Enter a parse tree produced by {@link ItemObjectParser#graphics}.
	 * @param ctx the parse tree
	 */
	void enterGraphics(ItemObjectParser.GraphicsContext ctx);
	/**
	 * Exit a parse tree produced by {@link ItemObjectParser#graphics}.
	 * @param ctx the parse tree
	 */
	void exitGraphics(ItemObjectParser.GraphicsContext ctx);
	/**
	 * Enter a parse tree produced by {@link ItemObjectParser#type}.
	 * @param ctx the parse tree
	 */
	void enterType(ItemObjectParser.TypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link ItemObjectParser#type}.
	 * @param ctx the parse tree
	 */
	void exitType(ItemObjectParser.TypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link ItemObjectParser#level}.
	 * @param ctx the parse tree
	 */
	void enterLevel(ItemObjectParser.LevelContext ctx);
	/**
	 * Exit a parse tree produced by {@link ItemObjectParser#level}.
	 * @param ctx the parse tree
	 */
	void exitLevel(ItemObjectParser.LevelContext ctx);
	/**
	 * Enter a parse tree produced by {@link ItemObjectParser#weight}.
	 * @param ctx the parse tree
	 */
	void enterWeight(ItemObjectParser.WeightContext ctx);
	/**
	 * Exit a parse tree produced by {@link ItemObjectParser#weight}.
	 * @param ctx the parse tree
	 */
	void exitWeight(ItemObjectParser.WeightContext ctx);
	/**
	 * Enter a parse tree produced by {@link ItemObjectParser#cost}.
	 * @param ctx the parse tree
	 */
	void enterCost(ItemObjectParser.CostContext ctx);
	/**
	 * Exit a parse tree produced by {@link ItemObjectParser#cost}.
	 * @param ctx the parse tree
	 */
	void exitCost(ItemObjectParser.CostContext ctx);
	/**
	 * Enter a parse tree produced by {@link ItemObjectParser#attack}.
	 * @param ctx the parse tree
	 */
	void enterAttack(ItemObjectParser.AttackContext ctx);
	/**
	 * Exit a parse tree produced by {@link ItemObjectParser#attack}.
	 * @param ctx the parse tree
	 */
	void exitAttack(ItemObjectParser.AttackContext ctx);
	/**
	 * Enter a parse tree produced by {@link ItemObjectParser#armour}.
	 * @param ctx the parse tree
	 */
	void enterArmour(ItemObjectParser.ArmourContext ctx);
	/**
	 * Exit a parse tree produced by {@link ItemObjectParser#armour}.
	 * @param ctx the parse tree
	 */
	void exitArmour(ItemObjectParser.ArmourContext ctx);
	/**
	 * Enter a parse tree produced by {@link ItemObjectParser#alloc}.
	 * @param ctx the parse tree
	 */
	void enterAlloc(ItemObjectParser.AllocContext ctx);
	/**
	 * Exit a parse tree produced by {@link ItemObjectParser#alloc}.
	 * @param ctx the parse tree
	 */
	void exitAlloc(ItemObjectParser.AllocContext ctx);
	/**
	 * Enter a parse tree produced by {@link ItemObjectParser#charges}.
	 * @param ctx the parse tree
	 */
	void enterCharges(ItemObjectParser.ChargesContext ctx);
	/**
	 * Exit a parse tree produced by {@link ItemObjectParser#charges}.
	 * @param ctx the parse tree
	 */
	void exitCharges(ItemObjectParser.ChargesContext ctx);
	/**
	 * Enter a parse tree produced by {@link ItemObjectParser#pile}.
	 * @param ctx the parse tree
	 */
	void enterPile(ItemObjectParser.PileContext ctx);
	/**
	 * Exit a parse tree produced by {@link ItemObjectParser#pile}.
	 * @param ctx the parse tree
	 */
	void exitPile(ItemObjectParser.PileContext ctx);
	/**
	 * Enter a parse tree produced by {@link ItemObjectParser#power}.
	 * @param ctx the parse tree
	 */
	void enterPower(ItemObjectParser.PowerContext ctx);
	/**
	 * Exit a parse tree produced by {@link ItemObjectParser#power}.
	 * @param ctx the parse tree
	 */
	void exitPower(ItemObjectParser.PowerContext ctx);
	/**
	 * Enter a parse tree produced by {@link ItemObjectParser#effect}.
	 * @param ctx the parse tree
	 */
	void enterEffect(ItemObjectParser.EffectContext ctx);
	/**
	 * Exit a parse tree produced by {@link ItemObjectParser#effect}.
	 * @param ctx the parse tree
	 */
	void exitEffect(ItemObjectParser.EffectContext ctx);
	/**
	 * Enter a parse tree produced by {@link ItemObjectParser#dice}.
	 * @param ctx the parse tree
	 */
	void enterDice(ItemObjectParser.DiceContext ctx);
	/**
	 * Exit a parse tree produced by {@link ItemObjectParser#dice}.
	 * @param ctx the parse tree
	 */
	void exitDice(ItemObjectParser.DiceContext ctx);
	/**
	 * Enter a parse tree produced by {@link ItemObjectParser#msg}.
	 * @param ctx the parse tree
	 */
	void enterMsg(ItemObjectParser.MsgContext ctx);
	/**
	 * Exit a parse tree produced by {@link ItemObjectParser#msg}.
	 * @param ctx the parse tree
	 */
	void exitMsg(ItemObjectParser.MsgContext ctx);
	/**
	 * Enter a parse tree produced by {@link ItemObjectParser#vis_msg}.
	 * @param ctx the parse tree
	 */
	void enterVis_msg(ItemObjectParser.Vis_msgContext ctx);
	/**
	 * Exit a parse tree produced by {@link ItemObjectParser#vis_msg}.
	 * @param ctx the parse tree
	 */
	void exitVis_msg(ItemObjectParser.Vis_msgContext ctx);
	/**
	 * Enter a parse tree produced by {@link ItemObjectParser#time}.
	 * @param ctx the parse tree
	 */
	void enterTime(ItemObjectParser.TimeContext ctx);
	/**
	 * Exit a parse tree produced by {@link ItemObjectParser#time}.
	 * @param ctx the parse tree
	 */
	void exitTime(ItemObjectParser.TimeContext ctx);
	/**
	 * Enter a parse tree produced by {@link ItemObjectParser#effect_yx}.
	 * @param ctx the parse tree
	 */
	void enterEffect_yx(ItemObjectParser.Effect_yxContext ctx);
	/**
	 * Exit a parse tree produced by {@link ItemObjectParser#effect_yx}.
	 * @param ctx the parse tree
	 */
	void exitEffect_yx(ItemObjectParser.Effect_yxContext ctx);
	/**
	 * Enter a parse tree produced by {@link ItemObjectParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterExpr(ItemObjectParser.ExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link ItemObjectParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitExpr(ItemObjectParser.ExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link ItemObjectParser#effect_block}.
	 * @param ctx the parse tree
	 */
	void enterEffect_block(ItemObjectParser.Effect_blockContext ctx);
	/**
	 * Exit a parse tree produced by {@link ItemObjectParser#effect_block}.
	 * @param ctx the parse tree
	 */
	void exitEffect_block(ItemObjectParser.Effect_blockContext ctx);
	/**
	 * Enter a parse tree produced by {@link ItemObjectParser#flags}.
	 * @param ctx the parse tree
	 */
	void enterFlags(ItemObjectParser.FlagsContext ctx);
	/**
	 * Exit a parse tree produced by {@link ItemObjectParser#flags}.
	 * @param ctx the parse tree
	 */
	void exitFlags(ItemObjectParser.FlagsContext ctx);
	/**
	 * Enter a parse tree produced by {@link ItemObjectParser#values}.
	 * @param ctx the parse tree
	 */
	void enterValues(ItemObjectParser.ValuesContext ctx);
	/**
	 * Exit a parse tree produced by {@link ItemObjectParser#values}.
	 * @param ctx the parse tree
	 */
	void exitValues(ItemObjectParser.ValuesContext ctx);
	/**
	 * Enter a parse tree produced by {@link ItemObjectParser#slay}.
	 * @param ctx the parse tree
	 */
	void enterSlay(ItemObjectParser.SlayContext ctx);
	/**
	 * Exit a parse tree produced by {@link ItemObjectParser#slay}.
	 * @param ctx the parse tree
	 */
	void exitSlay(ItemObjectParser.SlayContext ctx);
	/**
	 * Enter a parse tree produced by {@link ItemObjectParser#curse}.
	 * @param ctx the parse tree
	 */
	void enterCurse(ItemObjectParser.CurseContext ctx);
	/**
	 * Exit a parse tree produced by {@link ItemObjectParser#curse}.
	 * @param ctx the parse tree
	 */
	void exitCurse(ItemObjectParser.CurseContext ctx);
	/**
	 * Enter a parse tree produced by {@link ItemObjectParser#pval}.
	 * @param ctx the parse tree
	 */
	void enterPval(ItemObjectParser.PvalContext ctx);
	/**
	 * Exit a parse tree produced by {@link ItemObjectParser#pval}.
	 * @param ctx the parse tree
	 */
	void exitPval(ItemObjectParser.PvalContext ctx);
	/**
	 * Enter a parse tree produced by {@link ItemObjectParser#desc}.
	 * @param ctx the parse tree
	 */
	void enterDesc(ItemObjectParser.DescContext ctx);
	/**
	 * Exit a parse tree produced by {@link ItemObjectParser#desc}.
	 * @param ctx the parse tree
	 */
	void exitDesc(ItemObjectParser.DescContext ctx);
	/**
	 * Enter a parse tree produced by {@link ItemObjectParser#item_object}.
	 * @param ctx the parse tree
	 */
	void enterItem_object(ItemObjectParser.Item_objectContext ctx);
	/**
	 * Exit a parse tree produced by {@link ItemObjectParser#item_object}.
	 * @param ctx the parse tree
	 */
	void exitItem_object(ItemObjectParser.Item_objectContext ctx);
	/**
	 * Enter a parse tree produced by {@link ItemObjectParser#file}.
	 * @param ctx the parse tree
	 */
	void enterFile(ItemObjectParser.FileContext ctx);
	/**
	 * Exit a parse tree produced by {@link ItemObjectParser#file}.
	 * @param ctx the parse tree
	 */
	void exitFile(ItemObjectParser.FileContext ctx);
}