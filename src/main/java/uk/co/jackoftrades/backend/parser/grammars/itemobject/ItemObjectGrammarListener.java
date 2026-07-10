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
// Generated from ItemObjectGrammar.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.grammars.itemobject;

import uk.co.jackoftrades.backend.parser.grammars.EffectParseRecord;
import uk.co.jackoftrades.backend.parser.itemobject.ItemObjectParseRecord;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link ItemObjectGrammar}.
 */
public interface ItemObjectGrammarListener extends ParseTreeListener {
    /**
     * Enter a parse tree produced by {@link ItemObjectGrammar#recordCount}.
     *
     * @param ctx the parse tree
     */
    void enterRecordCount(ItemObjectGrammar.RecordCountContext ctx);

    /**
     * Exit a parse tree produced by {@link ItemObjectGrammar#recordCount}.
     *
     * @param ctx the parse tree
     */
    void exitRecordCount(ItemObjectGrammar.RecordCountContext ctx);

    /**
     * Enter a parse tree produced by {@link ItemObjectGrammar#name}.
     *
     * @param ctx the parse tree
     */
    void enterName(ItemObjectGrammar.NameContext ctx);

    /**
     * Exit a parse tree produced by {@link ItemObjectGrammar#name}.
     *
     * @param ctx the parse tree
     */
    void exitName(ItemObjectGrammar.NameContext ctx);

    /**
     * Enter a parse tree produced by {@link ItemObjectGrammar#tval}.
     *
     * @param ctx the parse tree
     */
    void enterTval(ItemObjectGrammar.TvalContext ctx);

    /**
     * Exit a parse tree produced by {@link ItemObjectGrammar#tval}.
     *
     * @param ctx the parse tree
     */
    void exitTval(ItemObjectGrammar.TvalContext ctx);

    /**
     * Enter a parse tree produced by {@link ItemObjectGrammar#graphics}.
     *
     * @param ctx the parse tree
     */
    void enterGraphics(ItemObjectGrammar.GraphicsContext ctx);

    /**
     * Exit a parse tree produced by {@link ItemObjectGrammar#graphics}.
     *
     * @param ctx the parse tree
     */
    void exitGraphics(ItemObjectGrammar.GraphicsContext ctx);

    /**
     * Enter a parse tree produced by {@link ItemObjectGrammar#level}.
     *
     * @param ctx the parse tree
     */
    void enterLevel(ItemObjectGrammar.LevelContext ctx);

    /**
     * Exit a parse tree produced by {@link ItemObjectGrammar#level}.
     *
     * @param ctx the parse tree
     */
    void exitLevel(ItemObjectGrammar.LevelContext ctx);

    /**
     * Enter a parse tree produced by {@link ItemObjectGrammar#weight}.
     *
     * @param ctx the parse tree
     */
    void enterWeight(ItemObjectGrammar.WeightContext ctx);

    /**
     * Exit a parse tree produced by {@link ItemObjectGrammar#weight}.
     *
     * @param ctx the parse tree
     */
    void exitWeight(ItemObjectGrammar.WeightContext ctx);

    /**
     * Enter a parse tree produced by {@link ItemObjectGrammar#cost}.
     *
     * @param ctx the parse tree
     */
    void enterCost(ItemObjectGrammar.CostContext ctx);

    /**
     * Exit a parse tree produced by {@link ItemObjectGrammar#cost}.
     *
     * @param ctx the parse tree
     */
    void exitCost(ItemObjectGrammar.CostContext ctx);

    /**
     * Enter a parse tree produced by {@link ItemObjectGrammar#attack}.
     *
     * @param ctx the parse tree
     */
    void enterAttack(ItemObjectGrammar.AttackContext ctx);

    /**
     * Exit a parse tree produced by {@link ItemObjectGrammar#attack}.
     *
     * @param ctx the parse tree
     */
    void exitAttack(ItemObjectGrammar.AttackContext ctx);

    /**
     * Enter a parse tree produced by {@link ItemObjectGrammar#armour}.
     *
     * @param ctx the parse tree
     */
    void enterArmour(ItemObjectGrammar.ArmourContext ctx);

    /**
     * Exit a parse tree produced by {@link ItemObjectGrammar#armour}.
     *
     * @param ctx the parse tree
     */
    void exitArmour(ItemObjectGrammar.ArmourContext ctx);

    /**
     * Enter a parse tree produced by {@link ItemObjectGrammar#alloc}.
     *
     * @param ctx the parse tree
     */
    void enterAlloc(ItemObjectGrammar.AllocContext ctx);

    /**
     * Exit a parse tree produced by {@link ItemObjectGrammar#alloc}.
     *
     * @param ctx the parse tree
     */
    void exitAlloc(ItemObjectGrammar.AllocContext ctx);

    /**
     * Enter a parse tree produced by {@link ItemObjectGrammar#charges}.
     *
     * @param ctx the parse tree
     */
    void enterCharges(ItemObjectGrammar.ChargesContext ctx);

    /**
     * Exit a parse tree produced by {@link ItemObjectGrammar#charges}.
     *
     * @param ctx the parse tree
     */
    void exitCharges(ItemObjectGrammar.ChargesContext ctx);

    /**
     * Enter a parse tree produced by {@link ItemObjectGrammar#pile}.
     *
     * @param ctx the parse tree
     */
    void enterPile(ItemObjectGrammar.PileContext ctx);

    /**
     * Exit a parse tree produced by {@link ItemObjectGrammar#pile}.
     *
     * @param ctx the parse tree
     */
    void exitPile(ItemObjectGrammar.PileContext ctx);

    /**
     * Enter a parse tree produced by {@link ItemObjectGrammar#power}.
     *
     * @param ctx the parse tree
     */
    void enterPower(ItemObjectGrammar.PowerContext ctx);

    /**
     * Exit a parse tree produced by {@link ItemObjectGrammar#power}.
     *
     * @param ctx the parse tree
     */
    void exitPower(ItemObjectGrammar.PowerContext ctx);

    /**
     * Enter a parse tree produced by {@link ItemObjectGrammar#msg}.
     *
     * @param ctx the parse tree
     */
    void enterMsg(ItemObjectGrammar.MsgContext ctx);

    /**
     * Exit a parse tree produced by {@link ItemObjectGrammar#msg}.
     *
     * @param ctx the parse tree
     */
    void exitMsg(ItemObjectGrammar.MsgContext ctx);

    /**
     * Enter a parse tree produced by {@link ItemObjectGrammar#visMsg}.
     *
     * @param ctx the parse tree
     */
    void enterVisMsg(ItemObjectGrammar.VisMsgContext ctx);

    /**
     * Exit a parse tree produced by {@link ItemObjectGrammar#visMsg}.
     *
     * @param ctx the parse tree
     */
    void exitVisMsg(ItemObjectGrammar.VisMsgContext ctx);

    /**
     * Enter a parse tree produced by {@link ItemObjectGrammar#flags}.
     *
     * @param ctx the parse tree
     */
    void enterFlags(ItemObjectGrammar.FlagsContext ctx);

    /**
     * Exit a parse tree produced by {@link ItemObjectGrammar#flags}.
     *
     * @param ctx the parse tree
     */
    void exitFlags(ItemObjectGrammar.FlagsContext ctx);

    /**
     * Enter a parse tree produced by {@link ItemObjectGrammar#values}.
     *
     * @param ctx the parse tree
     */
    void enterValues(ItemObjectGrammar.ValuesContext ctx);

    /**
     * Exit a parse tree produced by {@link ItemObjectGrammar#values}.
     *
     * @param ctx the parse tree
     */
    void exitValues(ItemObjectGrammar.ValuesContext ctx);

    /**
     * Enter a parse tree produced by {@link ItemObjectGrammar#brand}.
     *
     * @param ctx the parse tree
     */
    void enterBrand(ItemObjectGrammar.BrandContext ctx);

    /**
     * Exit a parse tree produced by {@link ItemObjectGrammar#brand}.
     *
     * @param ctx the parse tree
     */
    void exitBrand(ItemObjectGrammar.BrandContext ctx);

    /**
     * Enter a parse tree produced by {@link ItemObjectGrammar#slay}.
     *
     * @param ctx the parse tree
     */
    void enterSlay(ItemObjectGrammar.SlayContext ctx);

    /**
     * Exit a parse tree produced by {@link ItemObjectGrammar#slay}.
     *
     * @param ctx the parse tree
     */
    void exitSlay(ItemObjectGrammar.SlayContext ctx);

    /**
     * Enter a parse tree produced by {@link ItemObjectGrammar#curse}.
     *
     * @param ctx the parse tree
     */
    void enterCurse(ItemObjectGrammar.CurseContext ctx);

    /**
     * Exit a parse tree produced by {@link ItemObjectGrammar#curse}.
     *
     * @param ctx the parse tree
     */
    void exitCurse(ItemObjectGrammar.CurseContext ctx);

    /**
     * Enter a parse tree produced by {@link ItemObjectGrammar#pval}.
     *
     * @param ctx the parse tree
     */
    void enterPval(ItemObjectGrammar.PvalContext ctx);

    /**
     * Exit a parse tree produced by {@link ItemObjectGrammar#pval}.
     *
     * @param ctx the parse tree
     */
    void exitPval(ItemObjectGrammar.PvalContext ctx);

    /**
     * Enter a parse tree produced by {@link ItemObjectGrammar#desc}.
     *
     * @param ctx the parse tree
     */
    void enterDesc(ItemObjectGrammar.DescContext ctx);

    /**
     * Exit a parse tree produced by {@link ItemObjectGrammar#desc}.
     *
     * @param ctx the parse tree
     */
    void exitDesc(ItemObjectGrammar.DescContext ctx);

    /**
     * Enter a parse tree produced by {@link ItemObjectGrammar#itemObject}.
     *
     * @param ctx the parse tree
     */
    void enterItemObject(ItemObjectGrammar.ItemObjectContext ctx);

    /**
     * Exit a parse tree produced by {@link ItemObjectGrammar#itemObject}.
     *
     * @param ctx the parse tree
     */
    void exitItemObject(ItemObjectGrammar.ItemObjectContext ctx);

    /**
     * Enter a parse tree produced by {@link ItemObjectGrammar#file}.
     *
     * @param ctx the parse tree
     */
    void enterFile(ItemObjectGrammar.FileContext ctx);

    /**
     * Exit a parse tree produced by {@link ItemObjectGrammar#file}.
     *
     * @param ctx the parse tree
     */
    void exitFile(ItemObjectGrammar.FileContext ctx);

    /**
     * Enter a parse tree produced by {@link ItemObjectGrammar#effect}.
     *
     * @param ctx the parse tree
     */
    void enterEffect(ItemObjectGrammar.EffectContext ctx);

    /**
     * Exit a parse tree produced by {@link ItemObjectGrammar#effect}.
     *
     * @param ctx the parse tree
     */
    void exitEffect(ItemObjectGrammar.EffectContext ctx);

    /**
     * Enter a parse tree produced by {@link ItemObjectGrammar#time}.
     *
     * @param ctx the parse tree
     */
    void enterTime(ItemObjectGrammar.TimeContext ctx);

    /**
     * Exit a parse tree produced by {@link ItemObjectGrammar#time}.
     *
     * @param ctx the parse tree
     */
    void exitTime(ItemObjectGrammar.TimeContext ctx);

    /**
     * Enter a parse tree produced by {@link ItemObjectGrammar#effectYX}.
     *
     * @param ctx the parse tree
     */
    void enterEffectYX(ItemObjectGrammar.EffectYXContext ctx);

    /**
     * Exit a parse tree produced by {@link ItemObjectGrammar#effectYX}.
     *
     * @param ctx the parse tree
     */
    void exitEffectYX(ItemObjectGrammar.EffectYXContext ctx);

    /**
     * Enter a parse tree produced by {@link ItemObjectGrammar#dice}.
     *
     * @param ctx the parse tree
     */
    void enterDice(ItemObjectGrammar.DiceContext ctx);

    /**
     * Exit a parse tree produced by {@link ItemObjectGrammar#dice}.
     *
     * @param ctx the parse tree
     */
    void exitDice(ItemObjectGrammar.DiceContext ctx);

    /**
     * Enter a parse tree produced by {@link ItemObjectGrammar#expr}.
     *
     * @param ctx the parse tree
     */
    void enterExpr(ItemObjectGrammar.ExprContext ctx);

    /**
     * Exit a parse tree produced by {@link ItemObjectGrammar#expr}.
     *
     * @param ctx the parse tree
     */
    void exitExpr(ItemObjectGrammar.ExprContext ctx);

    /**
     * Enter a parse tree produced by {@link ItemObjectGrammar#effectMsg}.
     *
     * @param ctx the parse tree
     */
    void enterEffectMsg(ItemObjectGrammar.EffectMsgContext ctx);

    /**
     * Exit a parse tree produced by {@link ItemObjectGrammar#effectMsg}.
     *
     * @param ctx the parse tree
     */
    void exitEffectMsg(ItemObjectGrammar.EffectMsgContext ctx);

    /**
     * Enter a parse tree produced by {@link ItemObjectGrammar#effectBlock}.
     *
     * @param ctx the parse tree
     */
    void enterEffectBlock(ItemObjectGrammar.EffectBlockContext ctx);

    /**
     * Exit a parse tree produced by {@link ItemObjectGrammar#effectBlock}.
     *
     * @param ctx the parse tree
     */
    void exitEffectBlock(ItemObjectGrammar.EffectBlockContext ctx);
}