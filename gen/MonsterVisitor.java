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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/scratch/antlr_artifact_check/src/uk/co/jackoftrades/backend/parser/grammars/Monster.g4 by ANTLR 4.13.2

    import uk.co.jackoftrades.backend.numerics.Random;
    import uk.co.jackoftrades.backend.strings.AngbandDisplayCharacter;
    import uk.co.jackoftrades.backend.utils.Flag;
    import uk.co.jackoftrades.middle.combat.BlowMethod;
    import uk.co.jackoftrades.middle.game.globals.GameConstants;
    import uk.co.jackoftrades.middle.monsters.BlowEffect;
    import uk.co.jackoftrades.middle.monsters.MonsterAltmsg;
    import uk.co.jackoftrades.middle.monsters.MonsterBase;
    import uk.co.jackoftrades.middle.monsters.MonsterBlow;
    import uk.co.jackoftrades.middle.monsters.MonsterDrop;
    import uk.co.jackoftrades.middle.monsters.MonsterFriends;
    import uk.co.jackoftrades.middle.monsters.MonsterFriendsBase;
    import uk.co.jackoftrades.middle.monsters.MonsterMimic;
    import uk.co.jackoftrades.middle.monsters.MonsterRace;
    import uk.co.jackoftrades.middle.monsters.MonsterShape;
    import uk.co.jackoftrades.middle.monsters.enums.MonsterAltmsgType;
    import uk.co.jackoftrades.middle.monsters.enums.MonsterGroupRole;
    import uk.co.jackoftrades.middle.monsters.enums.MonsterRaceFlag;
    import uk.co.jackoftrades.middle.monsters.enums.MonsterSpell;
    import uk.co.jackoftrades.middle.objects.ObjectKind;
    import uk.co.jackoftrades.middle.objects.enums.TValue;
    import uk.co.jackoftrades.frontend.colour.VisualsColourCyclesByRace;
    import uk.co.jackoftrades.frontend.colour.enums.ColourType;


    import java.util.ArrayList;
    import java.util.List;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link MonsterParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface MonsterVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link MonsterParser#name}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitName(MonsterParser.NameContext ctx);
	/**
	 * Visit a parse tree produced by {@link MonsterParser#plural}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPlural(MonsterParser.PluralContext ctx);
	/**
	 * Visit a parse tree produced by {@link MonsterParser#base}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBase(MonsterParser.BaseContext ctx);
	/**
	 * Visit a parse tree produced by {@link MonsterParser#glyph}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGlyph(MonsterParser.GlyphContext ctx);
	/**
	 * Visit a parse tree produced by {@link MonsterParser#colour}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitColour(MonsterParser.ColourContext ctx);
	/**
	 * Visit a parse tree produced by {@link MonsterParser#colourCycle}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitColourCycle(MonsterParser.ColourCycleContext ctx);
	/**
	 * Visit a parse tree produced by {@link MonsterParser#speed}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSpeed(MonsterParser.SpeedContext ctx);
	/**
	 * Visit a parse tree produced by {@link MonsterParser#hitPoints}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHitPoints(MonsterParser.HitPointsContext ctx);
	/**
	 * Visit a parse tree produced by {@link MonsterParser#light}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLight(MonsterParser.LightContext ctx);
	/**
	 * Visit a parse tree produced by {@link MonsterParser#hearing}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHearing(MonsterParser.HearingContext ctx);
	/**
	 * Visit a parse tree produced by {@link MonsterParser#smell}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSmell(MonsterParser.SmellContext ctx);
	/**
	 * Visit a parse tree produced by {@link MonsterParser#armourClass}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArmourClass(MonsterParser.ArmourClassContext ctx);
	/**
	 * Visit a parse tree produced by {@link MonsterParser#sleepiness}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSleepiness(MonsterParser.SleepinessContext ctx);
	/**
	 * Visit a parse tree produced by {@link MonsterParser#dungeonDepth}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDungeonDepth(MonsterParser.DungeonDepthContext ctx);
	/**
	 * Visit a parse tree produced by {@link MonsterParser#rarity}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRarity(MonsterParser.RarityContext ctx);
	/**
	 * Visit a parse tree produced by {@link MonsterParser#experience}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExperience(MonsterParser.ExperienceContext ctx);
	/**
	 * Visit a parse tree produced by {@link MonsterParser#blow}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBlow(MonsterParser.BlowContext ctx);
	/**
	 * Visit a parse tree produced by {@link MonsterParser#flags}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFlags(MonsterParser.FlagsContext ctx);
	/**
	 * Visit a parse tree produced by {@link MonsterParser#flagsOff}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFlagsOff(MonsterParser.FlagsOffContext ctx);
	/**
	 * Visit a parse tree produced by {@link MonsterParser#innateFreq}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInnateFreq(MonsterParser.InnateFreqContext ctx);
	/**
	 * Visit a parse tree produced by {@link MonsterParser#spellFreq}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSpellFreq(MonsterParser.SpellFreqContext ctx);
	/**
	 * Visit a parse tree produced by {@link MonsterParser#spellPower}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSpellPower(MonsterParser.SpellPowerContext ctx);
	/**
	 * Visit a parse tree produced by {@link MonsterParser#spells}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSpells(MonsterParser.SpellsContext ctx);
	/**
	 * Visit a parse tree produced by {@link MonsterParser#messageVis}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMessageVis(MonsterParser.MessageVisContext ctx);
	/**
	 * Visit a parse tree produced by {@link MonsterParser#messageInvis}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMessageInvis(MonsterParser.MessageInvisContext ctx);
	/**
	 * Visit a parse tree produced by {@link MonsterParser#messageMiss}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMessageMiss(MonsterParser.MessageMissContext ctx);
	/**
	 * Visit a parse tree produced by {@link MonsterParser#desc}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDesc(MonsterParser.DescContext ctx);
	/**
	 * Visit a parse tree produced by {@link MonsterParser#shape}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitShape(MonsterParser.ShapeContext ctx);
	/**
	 * Visit a parse tree produced by {@link MonsterParser#drop}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDrop(MonsterParser.DropContext ctx);
	/**
	 * Visit a parse tree produced by {@link MonsterParser#dropBase}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDropBase(MonsterParser.DropBaseContext ctx);
	/**
	 * Visit a parse tree produced by {@link MonsterParser#mimic}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMimic(MonsterParser.MimicContext ctx);
	/**
	 * Visit a parse tree produced by {@link MonsterParser#friends}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFriends(MonsterParser.FriendsContext ctx);
	/**
	 * Visit a parse tree produced by {@link MonsterParser#friendsBase}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFriendsBase(MonsterParser.FriendsBaseContext ctx);
	/**
	 * Visit a parse tree produced by {@link MonsterParser#monster}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMonster(MonsterParser.MonsterContext ctx);
	/**
	 * Visit a parse tree produced by {@link MonsterParser#file}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFile(MonsterParser.FileContext ctx);
}