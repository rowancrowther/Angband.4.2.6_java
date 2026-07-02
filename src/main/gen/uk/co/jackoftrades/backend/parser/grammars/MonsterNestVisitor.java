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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/parser/grammars/MonsterNest.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.grammars;

    import uk.co.jackoftrades.backend.utils.Flag;
    import uk.co.jackoftrades.middle.cave.PitProfile;
    import uk.co.jackoftrades.middle.cave.enums.PitRoomType;
    import uk.co.jackoftrades.middle.game.globals.GameConstants;
    import uk.co.jackoftrades.middle.monsters.MonsterBase;
    import uk.co.jackoftrades.middle.monsters.MonsterRace;
    import uk.co.jackoftrades.middle.monsters.enums.MonsterRaceFlag;
    import uk.co.jackoftrades.middle.monsters.enums.MonsterSpell;
    import uk.co.jackoftrades.frontend.colour.enums.ColourType;

    import java.util.ArrayList;
    import java.util.List;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link MonsterNestParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface MonsterNestVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link MonsterNestParser#name}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitName(MonsterNestParser.NameContext ctx);
	/**
	 * Visit a parse tree produced by {@link MonsterNestParser#room}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRoom(MonsterNestParser.RoomContext ctx);
	/**
	 * Visit a parse tree produced by {@link MonsterNestParser#alloc}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAlloc(MonsterNestParser.AllocContext ctx);
	/**
	 * Visit a parse tree produced by {@link MonsterNestParser#objRarity}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitObjRarity(MonsterNestParser.ObjRarityContext ctx);
	/**
	 * Visit a parse tree produced by {@link MonsterNestParser#colour}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitColour(MonsterNestParser.ColourContext ctx);
	/**
	 * Visit a parse tree produced by {@link MonsterNestParser#monBase}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMonBase(MonsterNestParser.MonBaseContext ctx);
	/**
	 * Visit a parse tree produced by {@link MonsterNestParser#flagsReq}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFlagsReq(MonsterNestParser.FlagsReqContext ctx);
	/**
	 * Visit a parse tree produced by {@link MonsterNestParser#flagsBan}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFlagsBan(MonsterNestParser.FlagsBanContext ctx);
	/**
	 * Visit a parse tree produced by {@link MonsterNestParser#innateFreq}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInnateFreq(MonsterNestParser.InnateFreqContext ctx);
	/**
	 * Visit a parse tree produced by {@link MonsterNestParser#spellReq}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSpellReq(MonsterNestParser.SpellReqContext ctx);
	/**
	 * Visit a parse tree produced by {@link MonsterNestParser#spellBan}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSpellBan(MonsterNestParser.SpellBanContext ctx);
	/**
	 * Visit a parse tree produced by {@link MonsterNestParser#monBan}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMonBan(MonsterNestParser.MonBanContext ctx);
	/**
	 * Visit a parse tree produced by {@link MonsterNestParser#pit}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPit(MonsterNestParser.PitContext ctx);
	/**
	 * Visit a parse tree produced by {@link MonsterNestParser#file}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFile(MonsterNestParser.FileContext ctx);
}