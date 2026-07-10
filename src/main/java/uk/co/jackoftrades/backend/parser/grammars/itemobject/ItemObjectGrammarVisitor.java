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

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link ItemObjectGrammar}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 *            operations with no return type.
 */
public interface ItemObjectGrammarVisitor<T> extends ParseTreeVisitor<T> {
    /**
     * Visit a parse tree produced by {@link ItemObjectGrammar#recordCount}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitRecordCount(ItemObjectGrammar.RecordCountContext ctx);

    /**
     * Visit a parse tree produced by {@link ItemObjectGrammar#name}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitName(ItemObjectGrammar.NameContext ctx);

    /**
     * Visit a parse tree produced by {@link ItemObjectGrammar#tval}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitTval(ItemObjectGrammar.TvalContext ctx);

    /**
     * Visit a parse tree produced by {@link ItemObjectGrammar#graphics}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitGraphics(ItemObjectGrammar.GraphicsContext ctx);

    /**
     * Visit a parse tree produced by {@link ItemObjectGrammar#level}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitLevel(ItemObjectGrammar.LevelContext ctx);

    /**
     * Visit a parse tree produced by {@link ItemObjectGrammar#weight}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitWeight(ItemObjectGrammar.WeightContext ctx);

    /**
     * Visit a parse tree produced by {@link ItemObjectGrammar#cost}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitCost(ItemObjectGrammar.CostContext ctx);

    /**
     * Visit a parse tree produced by {@link ItemObjectGrammar#attack}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitAttack(ItemObjectGrammar.AttackContext ctx);

    /**
     * Visit a parse tree produced by {@link ItemObjectGrammar#armour}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitArmour(ItemObjectGrammar.ArmourContext ctx);

    /**
     * Visit a parse tree produced by {@link ItemObjectGrammar#alloc}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitAlloc(ItemObjectGrammar.AllocContext ctx);

    /**
     * Visit a parse tree produced by {@link ItemObjectGrammar#charges}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitCharges(ItemObjectGrammar.ChargesContext ctx);

    /**
     * Visit a parse tree produced by {@link ItemObjectGrammar#pile}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitPile(ItemObjectGrammar.PileContext ctx);

    /**
     * Visit a parse tree produced by {@link ItemObjectGrammar#power}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitPower(ItemObjectGrammar.PowerContext ctx);

    /**
     * Visit a parse tree produced by {@link ItemObjectGrammar#msg}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitMsg(ItemObjectGrammar.MsgContext ctx);

    /**
     * Visit a parse tree produced by {@link ItemObjectGrammar#visMsg}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitVisMsg(ItemObjectGrammar.VisMsgContext ctx);

    /**
     * Visit a parse tree produced by {@link ItemObjectGrammar#flags}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFlags(ItemObjectGrammar.FlagsContext ctx);

    /**
     * Visit a parse tree produced by {@link ItemObjectGrammar#values}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitValues(ItemObjectGrammar.ValuesContext ctx);

    /**
     * Visit a parse tree produced by {@link ItemObjectGrammar#brand}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitBrand(ItemObjectGrammar.BrandContext ctx);

    /**
     * Visit a parse tree produced by {@link ItemObjectGrammar#slay}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitSlay(ItemObjectGrammar.SlayContext ctx);

    /**
     * Visit a parse tree produced by {@link ItemObjectGrammar#curse}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitCurse(ItemObjectGrammar.CurseContext ctx);

    /**
     * Visit a parse tree produced by {@link ItemObjectGrammar#pval}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitPval(ItemObjectGrammar.PvalContext ctx);

    /**
     * Visit a parse tree produced by {@link ItemObjectGrammar#desc}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitDesc(ItemObjectGrammar.DescContext ctx);

    /**
     * Visit a parse tree produced by {@link ItemObjectGrammar#itemObject}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitItemObject(ItemObjectGrammar.ItemObjectContext ctx);

    /**
     * Visit a parse tree produced by {@link ItemObjectGrammar#file}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFile(ItemObjectGrammar.FileContext ctx);

    /**
     * Visit a parse tree produced by {@link ItemObjectGrammar#effect}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitEffect(ItemObjectGrammar.EffectContext ctx);

    /**
     * Visit a parse tree produced by {@link ItemObjectGrammar#time}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitTime(ItemObjectGrammar.TimeContext ctx);

    /**
     * Visit a parse tree produced by {@link ItemObjectGrammar#effectYX}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitEffectYX(ItemObjectGrammar.EffectYXContext ctx);

    /**
     * Visit a parse tree produced by {@link ItemObjectGrammar#dice}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitDice(ItemObjectGrammar.DiceContext ctx);

    /**
     * Visit a parse tree produced by {@link ItemObjectGrammar#expr}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitExpr(ItemObjectGrammar.ExprContext ctx);

    /**
     * Visit a parse tree produced by {@link ItemObjectGrammar#effectMsg}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitEffectMsg(ItemObjectGrammar.EffectMsgContext ctx);

    /**
     * Visit a parse tree produced by {@link ItemObjectGrammar#effectBlock}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitEffectBlock(ItemObjectGrammar.EffectBlockContext ctx);
}