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
// Generated from ShapeGrammar.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.grammars.shape;

import uk.co.jackoftrades.backend.parser.grammars.EffectParseRecord;
import uk.co.jackoftrades.backend.parser.shape.ShapeParseRecord;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link ShapeGrammar}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface ShapeGrammarVisitor<T> extends ParseTreeVisitor<T> {
    /**
     * Visit a parse tree produced by {@link ShapeGrammar#recordCount}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitRecordCount(ShapeGrammar.RecordCountContext ctx);

    /**
     * Visit a parse tree produced by {@link ShapeGrammar#name}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitName(ShapeGrammar.NameContext ctx);

    /**
     * Visit a parse tree produced by {@link ShapeGrammar#combat}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitCombat(ShapeGrammar.CombatContext ctx);

    /**
     * Visit a parse tree produced by {@link ShapeGrammar#skillDisarmP}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitSkillDisarmP(ShapeGrammar.SkillDisarmPContext ctx);

    /**
     * Visit a parse tree produced by {@link ShapeGrammar#skillDisarmM}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitSkillDisarmM(ShapeGrammar.SkillDisarmMContext ctx);

    /**
     * Visit a parse tree produced by {@link ShapeGrammar#skillSave}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitSkillSave(ShapeGrammar.SkillSaveContext ctx);

    /**
     * Visit a parse tree produced by {@link ShapeGrammar#skillStealth}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitSkillStealth(ShapeGrammar.SkillStealthContext ctx);

    /**
     * Visit a parse tree produced by {@link ShapeGrammar#skillSearch}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitSkillSearch(ShapeGrammar.SkillSearchContext ctx);

    /**
     * Visit a parse tree produced by {@link ShapeGrammar#skillMelee}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitSkillMelee(ShapeGrammar.SkillMeleeContext ctx);

    /**
     * Visit a parse tree produced by {@link ShapeGrammar#skillThrow}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitSkillThrow(ShapeGrammar.SkillThrowContext ctx);

    /**
     * Visit a parse tree produced by {@link ShapeGrammar#skillDig}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitSkillDig(ShapeGrammar.SkillDigContext ctx);

    /**
     * Visit a parse tree produced by {@link ShapeGrammar#objFlags}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitObjFlags(ShapeGrammar.ObjFlagsContext ctx);

    /**
     * Visit a parse tree produced by {@link ShapeGrammar#playerFlags}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitPlayerFlags(ShapeGrammar.PlayerFlagsContext ctx);

    /**
     * Visit a parse tree produced by {@link ShapeGrammar#values}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitValues(ShapeGrammar.ValuesContext ctx);

    /**
     * Visit a parse tree produced by {@link ShapeGrammar#blow}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitBlow(ShapeGrammar.BlowContext ctx);

    /**
     * Visit a parse tree produced by {@link ShapeGrammar#shape}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitShape(ShapeGrammar.ShapeContext ctx);

    /**
     * Visit a parse tree produced by {@link ShapeGrammar#file}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFile(ShapeGrammar.FileContext ctx);

    /**
     * Visit a parse tree produced by {@link ShapeGrammar#effect}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitEffect(ShapeGrammar.EffectContext ctx);

    /**
     * Visit a parse tree produced by {@link ShapeGrammar#time}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitTime(ShapeGrammar.TimeContext ctx);

    /**
     * Visit a parse tree produced by {@link ShapeGrammar#effectYX}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitEffectYX(ShapeGrammar.EffectYXContext ctx);

    /**
     * Visit a parse tree produced by {@link ShapeGrammar#dice}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitDice(ShapeGrammar.DiceContext ctx);

    /**
     * Visit a parse tree produced by {@link ShapeGrammar#expr}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitExpr(ShapeGrammar.ExprContext ctx);

    /**
     * Visit a parse tree produced by {@link ShapeGrammar#effectMsg}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitEffectMsg(ShapeGrammar.EffectMsgContext ctx);

    /**
     * Visit a parse tree produced by {@link ShapeGrammar#effectBlock}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitEffectBlock(ShapeGrammar.EffectBlockContext ctx);
}