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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/.claude/worktrees/gameconstants-assembler/src/main/java/uk/co/jackoftrades/backend/parser/grammars/MonsterNest.g4 by ANTLR 4.13.2

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

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link MonsterNestParser}.
 */
public interface MonsterNestListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link MonsterNestParser#name}.
	 * @param ctx the parse tree
	 */
	void enterName(MonsterNestParser.NameContext ctx);
	/**
	 * Exit a parse tree produced by {@link MonsterNestParser#name}.
	 * @param ctx the parse tree
	 */
	void exitName(MonsterNestParser.NameContext ctx);
	/**
	 * Enter a parse tree produced by {@link MonsterNestParser#room}.
	 * @param ctx the parse tree
	 */
	void enterRoom(MonsterNestParser.RoomContext ctx);
	/**
	 * Exit a parse tree produced by {@link MonsterNestParser#room}.
	 * @param ctx the parse tree
	 */
	void exitRoom(MonsterNestParser.RoomContext ctx);
	/**
	 * Enter a parse tree produced by {@link MonsterNestParser#alloc}.
	 * @param ctx the parse tree
	 */
	void enterAlloc(MonsterNestParser.AllocContext ctx);
	/**
	 * Exit a parse tree produced by {@link MonsterNestParser#alloc}.
	 * @param ctx the parse tree
	 */
	void exitAlloc(MonsterNestParser.AllocContext ctx);
	/**
	 * Enter a parse tree produced by {@link MonsterNestParser#objRarity}.
	 * @param ctx the parse tree
	 */
	void enterObjRarity(MonsterNestParser.ObjRarityContext ctx);
	/**
	 * Exit a parse tree produced by {@link MonsterNestParser#objRarity}.
	 * @param ctx the parse tree
	 */
	void exitObjRarity(MonsterNestParser.ObjRarityContext ctx);
	/**
	 * Enter a parse tree produced by {@link MonsterNestParser#colour}.
	 * @param ctx the parse tree
	 */
	void enterColour(MonsterNestParser.ColourContext ctx);
	/**
	 * Exit a parse tree produced by {@link MonsterNestParser#colour}.
	 * @param ctx the parse tree
	 */
	void exitColour(MonsterNestParser.ColourContext ctx);
	/**
	 * Enter a parse tree produced by {@link MonsterNestParser#monBase}.
	 * @param ctx the parse tree
	 */
	void enterMonBase(MonsterNestParser.MonBaseContext ctx);
	/**
	 * Exit a parse tree produced by {@link MonsterNestParser#monBase}.
	 * @param ctx the parse tree
	 */
	void exitMonBase(MonsterNestParser.MonBaseContext ctx);
	/**
	 * Enter a parse tree produced by {@link MonsterNestParser#flagsReq}.
	 * @param ctx the parse tree
	 */
	void enterFlagsReq(MonsterNestParser.FlagsReqContext ctx);
	/**
	 * Exit a parse tree produced by {@link MonsterNestParser#flagsReq}.
	 * @param ctx the parse tree
	 */
	void exitFlagsReq(MonsterNestParser.FlagsReqContext ctx);
	/**
	 * Enter a parse tree produced by {@link MonsterNestParser#flagsBan}.
	 * @param ctx the parse tree
	 */
	void enterFlagsBan(MonsterNestParser.FlagsBanContext ctx);
	/**
	 * Exit a parse tree produced by {@link MonsterNestParser#flagsBan}.
	 * @param ctx the parse tree
	 */
	void exitFlagsBan(MonsterNestParser.FlagsBanContext ctx);
	/**
	 * Enter a parse tree produced by {@link MonsterNestParser#innateFreq}.
	 * @param ctx the parse tree
	 */
	void enterInnateFreq(MonsterNestParser.InnateFreqContext ctx);
	/**
	 * Exit a parse tree produced by {@link MonsterNestParser#innateFreq}.
	 * @param ctx the parse tree
	 */
	void exitInnateFreq(MonsterNestParser.InnateFreqContext ctx);
	/**
	 * Enter a parse tree produced by {@link MonsterNestParser#spellReq}.
	 * @param ctx the parse tree
	 */
	void enterSpellReq(MonsterNestParser.SpellReqContext ctx);
	/**
	 * Exit a parse tree produced by {@link MonsterNestParser#spellReq}.
	 * @param ctx the parse tree
	 */
	void exitSpellReq(MonsterNestParser.SpellReqContext ctx);
	/**
	 * Enter a parse tree produced by {@link MonsterNestParser#spellBan}.
	 * @param ctx the parse tree
	 */
	void enterSpellBan(MonsterNestParser.SpellBanContext ctx);
	/**
	 * Exit a parse tree produced by {@link MonsterNestParser#spellBan}.
	 * @param ctx the parse tree
	 */
	void exitSpellBan(MonsterNestParser.SpellBanContext ctx);
	/**
	 * Enter a parse tree produced by {@link MonsterNestParser#monBan}.
	 * @param ctx the parse tree
	 */
	void enterMonBan(MonsterNestParser.MonBanContext ctx);
	/**
	 * Exit a parse tree produced by {@link MonsterNestParser#monBan}.
	 * @param ctx the parse tree
	 */
	void exitMonBan(MonsterNestParser.MonBanContext ctx);
	/**
	 * Enter a parse tree produced by {@link MonsterNestParser#pit}.
	 * @param ctx the parse tree
	 */
	void enterPit(MonsterNestParser.PitContext ctx);
	/**
	 * Exit a parse tree produced by {@link MonsterNestParser#pit}.
	 * @param ctx the parse tree
	 */
	void exitPit(MonsterNestParser.PitContext ctx);
	/**
	 * Enter a parse tree produced by {@link MonsterNestParser#file}.
	 * @param ctx the parse tree
	 */
	void enterFile(MonsterNestParser.FileContext ctx);
	/**
	 * Exit a parse tree produced by {@link MonsterNestParser#file}.
	 * @param ctx the parse tree
	 */
	void exitFile(MonsterNestParser.FileContext ctx);
}