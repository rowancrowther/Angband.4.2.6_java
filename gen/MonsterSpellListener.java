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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/scratch/antlr_artifact_check/src/uk/co/jackoftrades/backend/parser/grammars/MonsterSpell.g4 by ANTLR 4.13.2

    import uk.co.jackoftrades.backend.numerics.Random;
    import uk.co.jackoftrades.frontend.colour.enums.ColourType;
    import uk.co.jackoftrades.middle.combat.enums.ProjectionEnum;
    import uk.co.jackoftrades.middle.effect.Earthquake;
    import uk.co.jackoftrades.middle.effect.Effect;
    import uk.co.jackoftrades.middle.effect.EffectSubTypeWrapper;
    import uk.co.jackoftrades.middle.effect.Expression;
    import uk.co.jackoftrades.middle.effect.SummonType;
    import uk.co.jackoftrades.middle.enums.EffectBaseType;
    import uk.co.jackoftrades.middle.enums.EffectEnchant;
    import uk.co.jackoftrades.middle.enums.EffectEnum;
    import uk.co.jackoftrades.middle.enums.MonTimed;
    import uk.co.jackoftrades.middle.enums.EffectNourish;
    import uk.co.jackoftrades.middle.enums.GlyphType;
    import uk.co.jackoftrades.middle.enums.MessageType;
    import uk.co.jackoftrades.middle.enums.Stats;
    import uk.co.jackoftrades.middle.enums.TeleportEnum;
    import uk.co.jackoftrades.middle.monsters.MonsterSpellLevel;
    import uk.co.jackoftrades.middle.monsters.MonsterSpellType;
    import uk.co.jackoftrades.middle.monsters.enums.MonsterSpell;
    import uk.co.jackoftrades.middle.player.enums.TimedEffect;

    import org.apache.logging.log4j.LogManager;
    import org.apache.logging.log4j.Logger;

    import java.util.ArrayList;
    import java.util.List;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link MonsterSpellParser}.
 */
public interface MonsterSpellListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link MonsterSpellParser#name}.
	 * @param ctx the parse tree
	 */
	void enterName(MonsterSpellParser.NameContext ctx);
	/**
	 * Exit a parse tree produced by {@link MonsterSpellParser#name}.
	 * @param ctx the parse tree
	 */
	void exitName(MonsterSpellParser.NameContext ctx);
	/**
	 * Enter a parse tree produced by {@link MonsterSpellParser#msgt}.
	 * @param ctx the parse tree
	 */
	void enterMsgt(MonsterSpellParser.MsgtContext ctx);
	/**
	 * Exit a parse tree produced by {@link MonsterSpellParser#msgt}.
	 * @param ctx the parse tree
	 */
	void exitMsgt(MonsterSpellParser.MsgtContext ctx);
	/**
	 * Enter a parse tree produced by {@link MonsterSpellParser#hit}.
	 * @param ctx the parse tree
	 */
	void enterHit(MonsterSpellParser.HitContext ctx);
	/**
	 * Exit a parse tree produced by {@link MonsterSpellParser#hit}.
	 * @param ctx the parse tree
	 */
	void exitHit(MonsterSpellParser.HitContext ctx);
	/**
	 * Enter a parse tree produced by {@link MonsterSpellParser#effect}.
	 * @param ctx the parse tree
	 */
	void enterEffect(MonsterSpellParser.EffectContext ctx);
	/**
	 * Exit a parse tree produced by {@link MonsterSpellParser#effect}.
	 * @param ctx the parse tree
	 */
	void exitEffect(MonsterSpellParser.EffectContext ctx);
	/**
	 * Enter a parse tree produced by {@link MonsterSpellParser#effectYX}.
	 * @param ctx the parse tree
	 */
	void enterEffectYX(MonsterSpellParser.EffectYXContext ctx);
	/**
	 * Exit a parse tree produced by {@link MonsterSpellParser#effectYX}.
	 * @param ctx the parse tree
	 */
	void exitEffectYX(MonsterSpellParser.EffectYXContext ctx);
	/**
	 * Enter a parse tree produced by {@link MonsterSpellParser#dice}.
	 * @param ctx the parse tree
	 */
	void enterDice(MonsterSpellParser.DiceContext ctx);
	/**
	 * Exit a parse tree produced by {@link MonsterSpellParser#dice}.
	 * @param ctx the parse tree
	 */
	void exitDice(MonsterSpellParser.DiceContext ctx);
	/**
	 * Enter a parse tree produced by {@link MonsterSpellParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterExpr(MonsterSpellParser.ExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link MonsterSpellParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitExpr(MonsterSpellParser.ExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link MonsterSpellParser#effectBlock}.
	 * @param ctx the parse tree
	 */
	void enterEffectBlock(MonsterSpellParser.EffectBlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link MonsterSpellParser#effectBlock}.
	 * @param ctx the parse tree
	 */
	void exitEffectBlock(MonsterSpellParser.EffectBlockContext ctx);
	/**
	 * Enter a parse tree produced by {@link MonsterSpellParser#powerCutoff}.
	 * @param ctx the parse tree
	 */
	void enterPowerCutoff(MonsterSpellParser.PowerCutoffContext ctx);
	/**
	 * Exit a parse tree produced by {@link MonsterSpellParser#powerCutoff}.
	 * @param ctx the parse tree
	 */
	void exitPowerCutoff(MonsterSpellParser.PowerCutoffContext ctx);
	/**
	 * Enter a parse tree produced by {@link MonsterSpellParser#lore}.
	 * @param ctx the parse tree
	 */
	void enterLore(MonsterSpellParser.LoreContext ctx);
	/**
	 * Exit a parse tree produced by {@link MonsterSpellParser#lore}.
	 * @param ctx the parse tree
	 */
	void exitLore(MonsterSpellParser.LoreContext ctx);
	/**
	 * Enter a parse tree produced by {@link MonsterSpellParser#loreColourBase}.
	 * @param ctx the parse tree
	 */
	void enterLoreColourBase(MonsterSpellParser.LoreColourBaseContext ctx);
	/**
	 * Exit a parse tree produced by {@link MonsterSpellParser#loreColourBase}.
	 * @param ctx the parse tree
	 */
	void exitLoreColourBase(MonsterSpellParser.LoreColourBaseContext ctx);
	/**
	 * Enter a parse tree produced by {@link MonsterSpellParser#loreColourResist}.
	 * @param ctx the parse tree
	 */
	void enterLoreColourResist(MonsterSpellParser.LoreColourResistContext ctx);
	/**
	 * Exit a parse tree produced by {@link MonsterSpellParser#loreColourResist}.
	 * @param ctx the parse tree
	 */
	void exitLoreColourResist(MonsterSpellParser.LoreColourResistContext ctx);
	/**
	 * Enter a parse tree produced by {@link MonsterSpellParser#loreColourImmune}.
	 * @param ctx the parse tree
	 */
	void enterLoreColourImmune(MonsterSpellParser.LoreColourImmuneContext ctx);
	/**
	 * Exit a parse tree produced by {@link MonsterSpellParser#loreColourImmune}.
	 * @param ctx the parse tree
	 */
	void exitLoreColourImmune(MonsterSpellParser.LoreColourImmuneContext ctx);
	/**
	 * Enter a parse tree produced by {@link MonsterSpellParser#messageSave}.
	 * @param ctx the parse tree
	 */
	void enterMessageSave(MonsterSpellParser.MessageSaveContext ctx);
	/**
	 * Exit a parse tree produced by {@link MonsterSpellParser#messageSave}.
	 * @param ctx the parse tree
	 */
	void exitMessageSave(MonsterSpellParser.MessageSaveContext ctx);
	/**
	 * Enter a parse tree produced by {@link MonsterSpellParser#messageVis}.
	 * @param ctx the parse tree
	 */
	void enterMessageVis(MonsterSpellParser.MessageVisContext ctx);
	/**
	 * Exit a parse tree produced by {@link MonsterSpellParser#messageVis}.
	 * @param ctx the parse tree
	 */
	void exitMessageVis(MonsterSpellParser.MessageVisContext ctx);
	/**
	 * Enter a parse tree produced by {@link MonsterSpellParser#messageInvis}.
	 * @param ctx the parse tree
	 */
	void enterMessageInvis(MonsterSpellParser.MessageInvisContext ctx);
	/**
	 * Exit a parse tree produced by {@link MonsterSpellParser#messageInvis}.
	 * @param ctx the parse tree
	 */
	void exitMessageInvis(MonsterSpellParser.MessageInvisContext ctx);
	/**
	 * Enter a parse tree produced by {@link MonsterSpellParser#messageMiss}.
	 * @param ctx the parse tree
	 */
	void enterMessageMiss(MonsterSpellParser.MessageMissContext ctx);
	/**
	 * Exit a parse tree produced by {@link MonsterSpellParser#messageMiss}.
	 * @param ctx the parse tree
	 */
	void exitMessageMiss(MonsterSpellParser.MessageMissContext ctx);
	/**
	 * Enter a parse tree produced by {@link MonsterSpellParser#monsterSpellLevel}.
	 * @param ctx the parse tree
	 */
	void enterMonsterSpellLevel(MonsterSpellParser.MonsterSpellLevelContext ctx);
	/**
	 * Exit a parse tree produced by {@link MonsterSpellParser#monsterSpellLevel}.
	 * @param ctx the parse tree
	 */
	void exitMonsterSpellLevel(MonsterSpellParser.MonsterSpellLevelContext ctx);
	/**
	 * Enter a parse tree produced by {@link MonsterSpellParser#monsterSpellType}.
	 * @param ctx the parse tree
	 */
	void enterMonsterSpellType(MonsterSpellParser.MonsterSpellTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link MonsterSpellParser#monsterSpellType}.
	 * @param ctx the parse tree
	 */
	void exitMonsterSpellType(MonsterSpellParser.MonsterSpellTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link MonsterSpellParser#file}.
	 * @param ctx the parse tree
	 */
	void enterFile(MonsterSpellParser.FileContext ctx);
	/**
	 * Exit a parse tree produced by {@link MonsterSpellParser#file}.
	 * @param ctx the parse tree
	 */
	void exitFile(MonsterSpellParser.FileContext ctx);
}