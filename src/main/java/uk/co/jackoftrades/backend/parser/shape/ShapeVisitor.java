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

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link ShapeParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface ShapeVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link ShapeParser#name}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitName(ShapeParser.NameContext ctx);
	/**
	 * Visit a parse tree produced by {@link ShapeParser#combat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCombat(ShapeParser.CombatContext ctx);
	/**
	 * Visit a parse tree produced by {@link ShapeParser#skill_disarm_phys}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSkill_disarm_phys(ShapeParser.Skill_disarm_physContext ctx);
	/**
	 * Visit a parse tree produced by {@link ShapeParser#skill_disarm_magic}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSkill_disarm_magic(ShapeParser.Skill_disarm_magicContext ctx);
	/**
	 * Visit a parse tree produced by {@link ShapeParser#skill_save}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSkill_save(ShapeParser.Skill_saveContext ctx);
	/**
	 * Visit a parse tree produced by {@link ShapeParser#skill_stealth}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSkill_stealth(ShapeParser.Skill_stealthContext ctx);
	/**
	 * Visit a parse tree produced by {@link ShapeParser#skill_search}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSkill_search(ShapeParser.Skill_searchContext ctx);
	/**
	 * Visit a parse tree produced by {@link ShapeParser#skill_melee}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSkill_melee(ShapeParser.Skill_meleeContext ctx);
	/**
	 * Visit a parse tree produced by {@link ShapeParser#skill_throw}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSkill_throw(ShapeParser.Skill_throwContext ctx);
	/**
	 * Visit a parse tree produced by {@link ShapeParser#skill_dig}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSkill_dig(ShapeParser.Skill_digContext ctx);
	/**
	 * Visit a parse tree produced by {@link ShapeParser#obj_flags}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitObj_flags(ShapeParser.Obj_flagsContext ctx);
	/**
	 * Visit a parse tree produced by {@link ShapeParser#player_flags}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPlayer_flags(ShapeParser.Player_flagsContext ctx);
	/**
	 * Visit a parse tree produced by {@link ShapeParser#values}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitValues(ShapeParser.ValuesContext ctx);
	/**
	 * Visit a parse tree produced by {@link ShapeParser#effect}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEffect(ShapeParser.EffectContext ctx);
	/**
	 * Visit a parse tree produced by {@link ShapeParser#dice}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDice(ShapeParser.DiceContext ctx);
	/**
	 * Visit a parse tree produced by {@link ShapeParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpr(ShapeParser.ExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link ShapeParser#effect_msg}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEffect_msg(ShapeParser.Effect_msgContext ctx);
	/**
	 * Visit a parse tree produced by {@link ShapeParser#effect_block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEffect_block(ShapeParser.Effect_blockContext ctx);
	/**
	 * Visit a parse tree produced by {@link ShapeParser#blow}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBlow(ShapeParser.BlowContext ctx);
	/**
	 * Visit a parse tree produced by {@link ShapeParser#shape}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitShape(ShapeParser.ShapeContext ctx);
	/**
	 * Visit a parse tree produced by {@link ShapeParser#file}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFile(ShapeParser.FileContext ctx);
}