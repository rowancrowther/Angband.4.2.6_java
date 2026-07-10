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

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link ShapeGrammar}.
 */
public interface ShapeGrammarListener extends ParseTreeListener {
    /**
     * Enter a parse tree produced by {@link ShapeGrammar#recordCount}.
     *
     * @param ctx the parse tree
     */
    void enterRecordCount(ShapeGrammar.RecordCountContext ctx);

    /**
     * Exit a parse tree produced by {@link ShapeGrammar#recordCount}.
     *
     * @param ctx the parse tree
     */
    void exitRecordCount(ShapeGrammar.RecordCountContext ctx);

    /**
     * Enter a parse tree produced by {@link ShapeGrammar#name}.
     *
     * @param ctx the parse tree
     */
    void enterName(ShapeGrammar.NameContext ctx);

    /**
     * Exit a parse tree produced by {@link ShapeGrammar#name}.
     *
     * @param ctx the parse tree
     */
    void exitName(ShapeGrammar.NameContext ctx);

    /**
     * Enter a parse tree produced by {@link ShapeGrammar#combat}.
     *
     * @param ctx the parse tree
     */
    void enterCombat(ShapeGrammar.CombatContext ctx);

    /**
     * Exit a parse tree produced by {@link ShapeGrammar#combat}.
     *
     * @param ctx the parse tree
     */
    void exitCombat(ShapeGrammar.CombatContext ctx);

    /**
     * Enter a parse tree produced by {@link ShapeGrammar#skillDisarmP}.
     *
     * @param ctx the parse tree
     */
    void enterSkillDisarmP(ShapeGrammar.SkillDisarmPContext ctx);

    /**
     * Exit a parse tree produced by {@link ShapeGrammar#skillDisarmP}.
     *
     * @param ctx the parse tree
     */
    void exitSkillDisarmP(ShapeGrammar.SkillDisarmPContext ctx);

    /**
     * Enter a parse tree produced by {@link ShapeGrammar#skillDisarmM}.
     *
     * @param ctx the parse tree
     */
    void enterSkillDisarmM(ShapeGrammar.SkillDisarmMContext ctx);

    /**
     * Exit a parse tree produced by {@link ShapeGrammar#skillDisarmM}.
     *
     * @param ctx the parse tree
     */
    void exitSkillDisarmM(ShapeGrammar.SkillDisarmMContext ctx);

    /**
     * Enter a parse tree produced by {@link ShapeGrammar#skillSave}.
     *
     * @param ctx the parse tree
     */
    void enterSkillSave(ShapeGrammar.SkillSaveContext ctx);

    /**
     * Exit a parse tree produced by {@link ShapeGrammar#skillSave}.
     *
     * @param ctx the parse tree
     */
    void exitSkillSave(ShapeGrammar.SkillSaveContext ctx);

    /**
     * Enter a parse tree produced by {@link ShapeGrammar#skillStealth}.
     *
     * @param ctx the parse tree
     */
    void enterSkillStealth(ShapeGrammar.SkillStealthContext ctx);

    /**
     * Exit a parse tree produced by {@link ShapeGrammar#skillStealth}.
     *
     * @param ctx the parse tree
     */
    void exitSkillStealth(ShapeGrammar.SkillStealthContext ctx);

    /**
     * Enter a parse tree produced by {@link ShapeGrammar#skillSearch}.
     *
     * @param ctx the parse tree
     */
    void enterSkillSearch(ShapeGrammar.SkillSearchContext ctx);

    /**
     * Exit a parse tree produced by {@link ShapeGrammar#skillSearch}.
     *
     * @param ctx the parse tree
     */
    void exitSkillSearch(ShapeGrammar.SkillSearchContext ctx);

    /**
     * Enter a parse tree produced by {@link ShapeGrammar#skillMelee}.
     *
     * @param ctx the parse tree
     */
    void enterSkillMelee(ShapeGrammar.SkillMeleeContext ctx);

    /**
     * Exit a parse tree produced by {@link ShapeGrammar#skillMelee}.
     *
     * @param ctx the parse tree
     */
    void exitSkillMelee(ShapeGrammar.SkillMeleeContext ctx);

    /**
     * Enter a parse tree produced by {@link ShapeGrammar#skillThrow}.
     *
     * @param ctx the parse tree
     */
    void enterSkillThrow(ShapeGrammar.SkillThrowContext ctx);

    /**
     * Exit a parse tree produced by {@link ShapeGrammar#skillThrow}.
     *
     * @param ctx the parse tree
     */
    void exitSkillThrow(ShapeGrammar.SkillThrowContext ctx);

    /**
     * Enter a parse tree produced by {@link ShapeGrammar#skillDig}.
     *
     * @param ctx the parse tree
     */
    void enterSkillDig(ShapeGrammar.SkillDigContext ctx);

    /**
     * Exit a parse tree produced by {@link ShapeGrammar#skillDig}.
     *
     * @param ctx the parse tree
     */
    void exitSkillDig(ShapeGrammar.SkillDigContext ctx);

    /**
     * Enter a parse tree produced by {@link ShapeGrammar#objFlags}.
     *
     * @param ctx the parse tree
     */
    void enterObjFlags(ShapeGrammar.ObjFlagsContext ctx);

    /**
     * Exit a parse tree produced by {@link ShapeGrammar#objFlags}.
     *
     * @param ctx the parse tree
     */
    void exitObjFlags(ShapeGrammar.ObjFlagsContext ctx);

    /**
     * Enter a parse tree produced by {@link ShapeGrammar#playerFlags}.
     *
     * @param ctx the parse tree
     */
    void enterPlayerFlags(ShapeGrammar.PlayerFlagsContext ctx);

    /**
     * Exit a parse tree produced by {@link ShapeGrammar#playerFlags}.
     *
     * @param ctx the parse tree
     */
    void exitPlayerFlags(ShapeGrammar.PlayerFlagsContext ctx);

    /**
     * Enter a parse tree produced by {@link ShapeGrammar#values}.
     *
     * @param ctx the parse tree
     */
    void enterValues(ShapeGrammar.ValuesContext ctx);

    /**
     * Exit a parse tree produced by {@link ShapeGrammar#values}.
     *
     * @param ctx the parse tree
     */
    void exitValues(ShapeGrammar.ValuesContext ctx);

    /**
     * Enter a parse tree produced by {@link ShapeGrammar#blow}.
     *
     * @param ctx the parse tree
     */
    void enterBlow(ShapeGrammar.BlowContext ctx);

    /**
     * Exit a parse tree produced by {@link ShapeGrammar#blow}.
     *
     * @param ctx the parse tree
     */
    void exitBlow(ShapeGrammar.BlowContext ctx);

    /**
     * Enter a parse tree produced by {@link ShapeGrammar#shape}.
     *
     * @param ctx the parse tree
     */
    void enterShape(ShapeGrammar.ShapeContext ctx);

    /**
     * Exit a parse tree produced by {@link ShapeGrammar#shape}.
     *
     * @param ctx the parse tree
     */
    void exitShape(ShapeGrammar.ShapeContext ctx);

    /**
     * Enter a parse tree produced by {@link ShapeGrammar#file}.
     *
     * @param ctx the parse tree
     */
    void enterFile(ShapeGrammar.FileContext ctx);

    /**
     * Exit a parse tree produced by {@link ShapeGrammar#file}.
     *
     * @param ctx the parse tree
     */
    void exitFile(ShapeGrammar.FileContext ctx);

    /**
     * Enter a parse tree produced by {@link ShapeGrammar#effect}.
     *
     * @param ctx the parse tree
     */
    void enterEffect(ShapeGrammar.EffectContext ctx);

    /**
     * Exit a parse tree produced by {@link ShapeGrammar#effect}.
     *
     * @param ctx the parse tree
     */
    void exitEffect(ShapeGrammar.EffectContext ctx);

    /**
     * Enter a parse tree produced by {@link ShapeGrammar#time}.
     *
     * @param ctx the parse tree
     */
    void enterTime(ShapeGrammar.TimeContext ctx);

    /**
     * Exit a parse tree produced by {@link ShapeGrammar#time}.
     *
     * @param ctx the parse tree
     */
    void exitTime(ShapeGrammar.TimeContext ctx);

    /**
     * Enter a parse tree produced by {@link ShapeGrammar#effectYX}.
     *
     * @param ctx the parse tree
     */
    void enterEffectYX(ShapeGrammar.EffectYXContext ctx);

    /**
     * Exit a parse tree produced by {@link ShapeGrammar#effectYX}.
     *
     * @param ctx the parse tree
     */
    void exitEffectYX(ShapeGrammar.EffectYXContext ctx);

    /**
     * Enter a parse tree produced by {@link ShapeGrammar#dice}.
     *
     * @param ctx the parse tree
     */
    void enterDice(ShapeGrammar.DiceContext ctx);

    /**
     * Exit a parse tree produced by {@link ShapeGrammar#dice}.
     *
     * @param ctx the parse tree
     */
    void exitDice(ShapeGrammar.DiceContext ctx);

    /**
     * Enter a parse tree produced by {@link ShapeGrammar#expr}.
     *
     * @param ctx the parse tree
     */
    void enterExpr(ShapeGrammar.ExprContext ctx);

    /**
     * Exit a parse tree produced by {@link ShapeGrammar#expr}.
     *
     * @param ctx the parse tree
     */
    void exitExpr(ShapeGrammar.ExprContext ctx);

    /**
     * Enter a parse tree produced by {@link ShapeGrammar#effectMsg}.
     *
     * @param ctx the parse tree
     */
    void enterEffectMsg(ShapeGrammar.EffectMsgContext ctx);

    /**
     * Exit a parse tree produced by {@link ShapeGrammar#effectMsg}.
     *
     * @param ctx the parse tree
     */
    void exitEffectMsg(ShapeGrammar.EffectMsgContext ctx);

    /**
     * Enter a parse tree produced by {@link ShapeGrammar#effectBlock}.
     *
     * @param ctx the parse tree
     */
    void enterEffectBlock(ShapeGrammar.EffectBlockContext ctx);

    /**
     * Exit a parse tree produced by {@link ShapeGrammar#effectBlock}.
     *
     * @param ctx the parse tree
     */
    void exitEffectBlock(ShapeGrammar.EffectBlockContext ctx);
}