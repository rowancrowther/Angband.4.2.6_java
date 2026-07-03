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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/parser/grammars/ItemObject.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.grammars;

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

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link ItemObjectParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface ItemObjectVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link ItemObjectParser#name}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitName(ItemObjectParser.NameContext ctx);
	/**
	 * Visit a parse tree produced by {@link ItemObjectParser#graphics}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGraphics(ItemObjectParser.GraphicsContext ctx);
	/**
	 * Visit a parse tree produced by {@link ItemObjectParser#type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitType(ItemObjectParser.TypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link ItemObjectParser#level}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLevel(ItemObjectParser.LevelContext ctx);
	/**
	 * Visit a parse tree produced by {@link ItemObjectParser#weight}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWeight(ItemObjectParser.WeightContext ctx);
	/**
	 * Visit a parse tree produced by {@link ItemObjectParser#cost}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCost(ItemObjectParser.CostContext ctx);
	/**
	 * Visit a parse tree produced by {@link ItemObjectParser#attack}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAttack(ItemObjectParser.AttackContext ctx);
	/**
	 * Visit a parse tree produced by {@link ItemObjectParser#armour}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArmour(ItemObjectParser.ArmourContext ctx);
	/**
	 * Visit a parse tree produced by {@link ItemObjectParser#alloc}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAlloc(ItemObjectParser.AllocContext ctx);
	/**
	 * Visit a parse tree produced by {@link ItemObjectParser#charges}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCharges(ItemObjectParser.ChargesContext ctx);
	/**
	 * Visit a parse tree produced by {@link ItemObjectParser#pile}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPile(ItemObjectParser.PileContext ctx);
	/**
	 * Visit a parse tree produced by {@link ItemObjectParser#power}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPower(ItemObjectParser.PowerContext ctx);
	/**
	 * Visit a parse tree produced by {@link ItemObjectParser#effect}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEffect(ItemObjectParser.EffectContext ctx);
	/**
	 * Visit a parse tree produced by {@link ItemObjectParser#dice}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDice(ItemObjectParser.DiceContext ctx);
	/**
	 * Visit a parse tree produced by {@link ItemObjectParser#msg}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMsg(ItemObjectParser.MsgContext ctx);
	/**
	 * Visit a parse tree produced by {@link ItemObjectParser#vis_msg}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVis_msg(ItemObjectParser.Vis_msgContext ctx);
	/**
	 * Visit a parse tree produced by {@link ItemObjectParser#time}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTime(ItemObjectParser.TimeContext ctx);
	/**
	 * Visit a parse tree produced by {@link ItemObjectParser#effect_yx}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEffect_yx(ItemObjectParser.Effect_yxContext ctx);
	/**
	 * Visit a parse tree produced by {@link ItemObjectParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpr(ItemObjectParser.ExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link ItemObjectParser#effect_block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEffect_block(ItemObjectParser.Effect_blockContext ctx);
	/**
	 * Visit a parse tree produced by {@link ItemObjectParser#flags}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFlags(ItemObjectParser.FlagsContext ctx);
	/**
	 * Visit a parse tree produced by {@link ItemObjectParser#values}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitValues(ItemObjectParser.ValuesContext ctx);
	/**
	 * Visit a parse tree produced by {@link ItemObjectParser#slay}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSlay(ItemObjectParser.SlayContext ctx);
	/**
	 * Visit a parse tree produced by {@link ItemObjectParser#curse}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCurse(ItemObjectParser.CurseContext ctx);
	/**
	 * Visit a parse tree produced by {@link ItemObjectParser#pval}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPval(ItemObjectParser.PvalContext ctx);
	/**
	 * Visit a parse tree produced by {@link ItemObjectParser#desc}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDesc(ItemObjectParser.DescContext ctx);
	/**
	 * Visit a parse tree produced by {@link ItemObjectParser#item_object}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitItem_object(ItemObjectParser.Item_objectContext ctx);
	/**
	 * Visit a parse tree produced by {@link ItemObjectParser#file}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFile(ItemObjectParser.FileContext ctx);
}