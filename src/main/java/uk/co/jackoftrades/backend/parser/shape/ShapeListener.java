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
 *    Java code copyright (c) Rowan Crowther 2026
 */

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/parser/grammars/Shape.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.shape;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link ShapeParser}.
 */
public interface ShapeListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link ShapeParser#name}.
	 * @param ctx the parse tree
	 */
	void enterName(ShapeParser.NameContext ctx);
	/**
	 * Exit a parse tree produced by {@link ShapeParser#name}.
	 * @param ctx the parse tree
	 */
	void exitName(ShapeParser.NameContext ctx);
	/**
	 * Enter a parse tree produced by {@link ShapeParser#combat}.
	 * @param ctx the parse tree
	 */
	void enterCombat(ShapeParser.CombatContext ctx);
	/**
	 * Exit a parse tree produced by {@link ShapeParser#combat}.
	 * @param ctx the parse tree
	 */
	void exitCombat(ShapeParser.CombatContext ctx);
	/**
	 * Enter a parse tree produced by {@link ShapeParser#skill_disarm_phys}.
	 * @param ctx the parse tree
	 */
	void enterSkill_disarm_phys(ShapeParser.Skill_disarm_physContext ctx);
	/**
	 * Exit a parse tree produced by {@link ShapeParser#skill_disarm_phys}.
	 * @param ctx the parse tree
	 */
	void exitSkill_disarm_phys(ShapeParser.Skill_disarm_physContext ctx);
	/**
	 * Enter a parse tree produced by {@link ShapeParser#skill_disarm_magic}.
	 * @param ctx the parse tree
	 */
	void enterSkill_disarm_magic(ShapeParser.Skill_disarm_magicContext ctx);
	/**
	 * Exit a parse tree produced by {@link ShapeParser#skill_disarm_magic}.
	 * @param ctx the parse tree
	 */
	void exitSkill_disarm_magic(ShapeParser.Skill_disarm_magicContext ctx);
	/**
	 * Enter a parse tree produced by {@link ShapeParser#skill_save}.
	 * @param ctx the parse tree
	 */
	void enterSkill_save(ShapeParser.Skill_saveContext ctx);
	/**
	 * Exit a parse tree produced by {@link ShapeParser#skill_save}.
	 * @param ctx the parse tree
	 */
	void exitSkill_save(ShapeParser.Skill_saveContext ctx);
	/**
	 * Enter a parse tree produced by {@link ShapeParser#skill_stealth}.
	 * @param ctx the parse tree
	 */
	void enterSkill_stealth(ShapeParser.Skill_stealthContext ctx);
	/**
	 * Exit a parse tree produced by {@link ShapeParser#skill_stealth}.
	 * @param ctx the parse tree
	 */
	void exitSkill_stealth(ShapeParser.Skill_stealthContext ctx);
	/**
	 * Enter a parse tree produced by {@link ShapeParser#skill_search}.
	 * @param ctx the parse tree
	 */
	void enterSkill_search(ShapeParser.Skill_searchContext ctx);
	/**
	 * Exit a parse tree produced by {@link ShapeParser#skill_search}.
	 * @param ctx the parse tree
	 */
	void exitSkill_search(ShapeParser.Skill_searchContext ctx);
	/**
	 * Enter a parse tree produced by {@link ShapeParser#skill_melee}.
	 * @param ctx the parse tree
	 */
	void enterSkill_melee(ShapeParser.Skill_meleeContext ctx);
	/**
	 * Exit a parse tree produced by {@link ShapeParser#skill_melee}.
	 * @param ctx the parse tree
	 */
	void exitSkill_melee(ShapeParser.Skill_meleeContext ctx);
	/**
	 * Enter a parse tree produced by {@link ShapeParser#skill_throw}.
	 * @param ctx the parse tree
	 */
	void enterSkill_throw(ShapeParser.Skill_throwContext ctx);
	/**
	 * Exit a parse tree produced by {@link ShapeParser#skill_throw}.
	 * @param ctx the parse tree
	 */
	void exitSkill_throw(ShapeParser.Skill_throwContext ctx);
	/**
	 * Enter a parse tree produced by {@link ShapeParser#skill_dig}.
	 * @param ctx the parse tree
	 */
	void enterSkill_dig(ShapeParser.Skill_digContext ctx);
	/**
	 * Exit a parse tree produced by {@link ShapeParser#skill_dig}.
	 * @param ctx the parse tree
	 */
	void exitSkill_dig(ShapeParser.Skill_digContext ctx);
	/**
	 * Enter a parse tree produced by {@link ShapeParser#obj_flags}.
	 * @param ctx the parse tree
	 */
	void enterObj_flags(ShapeParser.Obj_flagsContext ctx);
	/**
	 * Exit a parse tree produced by {@link ShapeParser#obj_flags}.
	 * @param ctx the parse tree
	 */
	void exitObj_flags(ShapeParser.Obj_flagsContext ctx);
	/**
	 * Enter a parse tree produced by {@link ShapeParser#player_flags}.
	 * @param ctx the parse tree
	 */
	void enterPlayer_flags(ShapeParser.Player_flagsContext ctx);
	/**
	 * Exit a parse tree produced by {@link ShapeParser#player_flags}.
	 * @param ctx the parse tree
	 */
	void exitPlayer_flags(ShapeParser.Player_flagsContext ctx);
	/**
	 * Enter a parse tree produced by {@link ShapeParser#values}.
	 * @param ctx the parse tree
	 */
	void enterValues(ShapeParser.ValuesContext ctx);
	/**
	 * Exit a parse tree produced by {@link ShapeParser#values}.
	 * @param ctx the parse tree
	 */
	void exitValues(ShapeParser.ValuesContext ctx);
	/**
	 * Enter a parse tree produced by {@link ShapeParser#effect}.
	 * @param ctx the parse tree
	 */
	void enterEffect(ShapeParser.EffectContext ctx);
	/**
	 * Exit a parse tree produced by {@link ShapeParser#effect}.
	 * @param ctx the parse tree
	 */
	void exitEffect(ShapeParser.EffectContext ctx);
	/**
	 * Enter a parse tree produced by {@link ShapeParser#dice}.
	 * @param ctx the parse tree
	 */
	void enterDice(ShapeParser.DiceContext ctx);
	/**
	 * Exit a parse tree produced by {@link ShapeParser#dice}.
	 * @param ctx the parse tree
	 */
	void exitDice(ShapeParser.DiceContext ctx);
	/**
	 * Enter a parse tree produced by {@link ShapeParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterExpr(ShapeParser.ExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link ShapeParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitExpr(ShapeParser.ExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link ShapeParser#effect_msg}.
	 * @param ctx the parse tree
	 */
	void enterEffect_msg(ShapeParser.Effect_msgContext ctx);
	/**
	 * Exit a parse tree produced by {@link ShapeParser#effect_msg}.
	 * @param ctx the parse tree
	 */
	void exitEffect_msg(ShapeParser.Effect_msgContext ctx);
	/**
	 * Enter a parse tree produced by {@link ShapeParser#effect_block}.
	 * @param ctx the parse tree
	 */
	void enterEffect_block(ShapeParser.Effect_blockContext ctx);
	/**
	 * Exit a parse tree produced by {@link ShapeParser#effect_block}.
	 * @param ctx the parse tree
	 */
	void exitEffect_block(ShapeParser.Effect_blockContext ctx);
	/**
	 * Enter a parse tree produced by {@link ShapeParser#blow}.
	 * @param ctx the parse tree
	 */
	void enterBlow(ShapeParser.BlowContext ctx);
	/**
	 * Exit a parse tree produced by {@link ShapeParser#blow}.
	 * @param ctx the parse tree
	 */
	void exitBlow(ShapeParser.BlowContext ctx);
	/**
	 * Enter a parse tree produced by {@link ShapeParser#shape}.
	 * @param ctx the parse tree
	 */
	void enterShape(ShapeParser.ShapeContext ctx);
	/**
	 * Exit a parse tree produced by {@link ShapeParser#shape}.
	 * @param ctx the parse tree
	 */
	void exitShape(ShapeParser.ShapeContext ctx);
	/**
	 * Enter a parse tree produced by {@link ShapeParser#file}.
	 * @param ctx the parse tree
	 */
	void enterFile(ShapeParser.FileContext ctx);
	/**
	 * Exit a parse tree produced by {@link ShapeParser#file}.
	 * @param ctx the parse tree
	 */
	void exitFile(ShapeParser.FileContext ctx);
}