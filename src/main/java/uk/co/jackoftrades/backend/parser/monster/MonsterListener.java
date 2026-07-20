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
// Generated from Monster.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.monster;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link MonsterParser}.
 */
public interface MonsterListener extends ParseTreeListener {
    /**
     * Enter a parse tree produced by {@link MonsterParser#name}.
     *
     * @param ctx the parse tree
     */
    void enterName(MonsterParser.NameContext ctx);

    /**
     * Exit a parse tree produced by {@link MonsterParser#name}.
     *
     * @param ctx the parse tree
     */
    void exitName(MonsterParser.NameContext ctx);

    /**
     * Enter a parse tree produced by {@link MonsterParser#plural}.
     *
     * @param ctx the parse tree
     */
    void enterPlural(MonsterParser.PluralContext ctx);

    /**
     * Exit a parse tree produced by {@link MonsterParser#plural}.
     *
     * @param ctx the parse tree
     */
    void exitPlural(MonsterParser.PluralContext ctx);

    /**
     * Enter a parse tree produced by {@link MonsterParser#base}.
     *
     * @param ctx the parse tree
     */
    void enterBase(MonsterParser.BaseContext ctx);

    /**
     * Exit a parse tree produced by {@link MonsterParser#base}.
     *
     * @param ctx the parse tree
     */
    void exitBase(MonsterParser.BaseContext ctx);

    /**
     * Enter a parse tree produced by {@link MonsterParser#glyph}.
     *
     * @param ctx the parse tree
     */
    void enterGlyph(MonsterParser.GlyphContext ctx);

    /**
     * Exit a parse tree produced by {@link MonsterParser#glyph}.
     *
     * @param ctx the parse tree
     */
    void exitGlyph(MonsterParser.GlyphContext ctx);

    /**
     * Enter a parse tree produced by {@link MonsterParser#colour}.
     *
     * @param ctx the parse tree
     */
    void enterColour(MonsterParser.ColourContext ctx);

    /**
     * Exit a parse tree produced by {@link MonsterParser#colour}.
     *
     * @param ctx the parse tree
     */
    void exitColour(MonsterParser.ColourContext ctx);

    /**
     * Enter a parse tree produced by {@link MonsterParser#colourCycle}.
     *
     * @param ctx the parse tree
     */
    void enterColourCycle(MonsterParser.ColourCycleContext ctx);

    /**
     * Exit a parse tree produced by {@link MonsterParser#colourCycle}.
     *
     * @param ctx the parse tree
     */
    void exitColourCycle(MonsterParser.ColourCycleContext ctx);

    /**
     * Enter a parse tree produced by {@link MonsterParser#speed}.
     *
     * @param ctx the parse tree
     */
    void enterSpeed(MonsterParser.SpeedContext ctx);

    /**
     * Exit a parse tree produced by {@link MonsterParser#speed}.
     *
     * @param ctx the parse tree
     */
    void exitSpeed(MonsterParser.SpeedContext ctx);

    /**
     * Enter a parse tree produced by {@link MonsterParser#hitPoints}.
     *
     * @param ctx the parse tree
     */
    void enterHitPoints(MonsterParser.HitPointsContext ctx);

    /**
     * Exit a parse tree produced by {@link MonsterParser#hitPoints}.
     *
     * @param ctx the parse tree
     */
    void exitHitPoints(MonsterParser.HitPointsContext ctx);

    /**
     * Enter a parse tree produced by {@link MonsterParser#light}.
     *
     * @param ctx the parse tree
     */
    void enterLight(MonsterParser.LightContext ctx);

    /**
     * Exit a parse tree produced by {@link MonsterParser#light}.
     *
     * @param ctx the parse tree
     */
    void exitLight(MonsterParser.LightContext ctx);

    /**
     * Enter a parse tree produced by {@link MonsterParser#hearing}.
     *
     * @param ctx the parse tree
     */
    void enterHearing(MonsterParser.HearingContext ctx);

    /**
     * Exit a parse tree produced by {@link MonsterParser#hearing}.
     *
     * @param ctx the parse tree
     */
    void exitHearing(MonsterParser.HearingContext ctx);

    /**
     * Enter a parse tree produced by {@link MonsterParser#smell}.
     *
     * @param ctx the parse tree
     */
    void enterSmell(MonsterParser.SmellContext ctx);

    /**
     * Exit a parse tree produced by {@link MonsterParser#smell}.
     *
     * @param ctx the parse tree
     */
    void exitSmell(MonsterParser.SmellContext ctx);

    /**
     * Enter a parse tree produced by {@link MonsterParser#armourClass}.
     *
     * @param ctx the parse tree
     */
    void enterArmourClass(MonsterParser.ArmourClassContext ctx);

    /**
     * Exit a parse tree produced by {@link MonsterParser#armourClass}.
     *
     * @param ctx the parse tree
     */
    void exitArmourClass(MonsterParser.ArmourClassContext ctx);

    /**
     * Enter a parse tree produced by {@link MonsterParser#sleepiness}.
     *
     * @param ctx the parse tree
     */
    void enterSleepiness(MonsterParser.SleepinessContext ctx);

    /**
     * Exit a parse tree produced by {@link MonsterParser#sleepiness}.
     *
     * @param ctx the parse tree
     */
    void exitSleepiness(MonsterParser.SleepinessContext ctx);

    /**
     * Enter a parse tree produced by {@link MonsterParser#dungeonDepth}.
     *
     * @param ctx the parse tree
     */
    void enterDungeonDepth(MonsterParser.DungeonDepthContext ctx);

    /**
     * Exit a parse tree produced by {@link MonsterParser#dungeonDepth}.
     *
     * @param ctx the parse tree
     */
    void exitDungeonDepth(MonsterParser.DungeonDepthContext ctx);

    /**
     * Enter a parse tree produced by {@link MonsterParser#rarity}.
     *
     * @param ctx the parse tree
     */
    void enterRarity(MonsterParser.RarityContext ctx);

    /**
     * Exit a parse tree produced by {@link MonsterParser#rarity}.
     *
     * @param ctx the parse tree
     */
    void exitRarity(MonsterParser.RarityContext ctx);

    /**
     * Enter a parse tree produced by {@link MonsterParser#experience}.
     *
     * @param ctx the parse tree
     */
    void enterExperience(MonsterParser.ExperienceContext ctx);

    /**
     * Exit a parse tree produced by {@link MonsterParser#experience}.
     *
     * @param ctx the parse tree
     */
    void exitExperience(MonsterParser.ExperienceContext ctx);

    /**
     * Enter a parse tree produced by {@link MonsterParser#blow}.
     *
     * @param ctx the parse tree
     */
    void enterBlow(MonsterParser.BlowContext ctx);

    /**
     * Exit a parse tree produced by {@link MonsterParser#blow}.
     *
     * @param ctx the parse tree
     */
    void exitBlow(MonsterParser.BlowContext ctx);

    /**
     * Enter a parse tree produced by {@link MonsterParser#flags}.
     *
     * @param ctx the parse tree
     */
    void enterFlags(MonsterParser.FlagsContext ctx);

    /**
     * Exit a parse tree produced by {@link MonsterParser#flags}.
     *
     * @param ctx the parse tree
     */
    void exitFlags(MonsterParser.FlagsContext ctx);

    /**
     * Enter a parse tree produced by {@link MonsterParser#flagsOff}.
     *
     * @param ctx the parse tree
     */
    void enterFlagsOff(MonsterParser.FlagsOffContext ctx);

    /**
     * Exit a parse tree produced by {@link MonsterParser#flagsOff}.
     *
     * @param ctx the parse tree
     */
    void exitFlagsOff(MonsterParser.FlagsOffContext ctx);

    /**
     * Enter a parse tree produced by {@link MonsterParser#innateFreq}.
     *
     * @param ctx the parse tree
     */
    void enterInnateFreq(MonsterParser.InnateFreqContext ctx);

    /**
     * Exit a parse tree produced by {@link MonsterParser#innateFreq}.
     *
     * @param ctx the parse tree
     */
    void exitInnateFreq(MonsterParser.InnateFreqContext ctx);

    /**
     * Enter a parse tree produced by {@link MonsterParser#spellFreq}.
     *
     * @param ctx the parse tree
     */
    void enterSpellFreq(MonsterParser.SpellFreqContext ctx);

    /**
     * Exit a parse tree produced by {@link MonsterParser#spellFreq}.
     *
     * @param ctx the parse tree
     */
    void exitSpellFreq(MonsterParser.SpellFreqContext ctx);

    /**
     * Enter a parse tree produced by {@link MonsterParser#spellPower}.
     *
     * @param ctx the parse tree
     */
    void enterSpellPower(MonsterParser.SpellPowerContext ctx);

    /**
     * Exit a parse tree produced by {@link MonsterParser#spellPower}.
     *
     * @param ctx the parse tree
     */
    void exitSpellPower(MonsterParser.SpellPowerContext ctx);

    /**
     * Enter a parse tree produced by {@link MonsterParser#spells}.
     *
     * @param ctx the parse tree
     */
    void enterSpells(MonsterParser.SpellsContext ctx);

    /**
     * Exit a parse tree produced by {@link MonsterParser#spells}.
     *
     * @param ctx the parse tree
     */
    void exitSpells(MonsterParser.SpellsContext ctx);

    /**
     * Enter a parse tree produced by {@link MonsterParser#messageVis}.
     *
     * @param ctx the parse tree
     */
    void enterMessageVis(MonsterParser.MessageVisContext ctx);

    /**
     * Exit a parse tree produced by {@link MonsterParser#messageVis}.
     *
     * @param ctx the parse tree
     */
    void exitMessageVis(MonsterParser.MessageVisContext ctx);

    /**
     * Enter a parse tree produced by {@link MonsterParser#messageInvis}.
     *
     * @param ctx the parse tree
     */
    void enterMessageInvis(MonsterParser.MessageInvisContext ctx);

    /**
     * Exit a parse tree produced by {@link MonsterParser#messageInvis}.
     *
     * @param ctx the parse tree
     */
    void exitMessageInvis(MonsterParser.MessageInvisContext ctx);

    /**
     * Enter a parse tree produced by {@link MonsterParser#messageMiss}.
     *
     * @param ctx the parse tree
     */
    void enterMessageMiss(MonsterParser.MessageMissContext ctx);

    /**
     * Exit a parse tree produced by {@link MonsterParser#messageMiss}.
     *
     * @param ctx the parse tree
     */
    void exitMessageMiss(MonsterParser.MessageMissContext ctx);

    /**
     * Enter a parse tree produced by {@link MonsterParser#desc}.
     *
     * @param ctx the parse tree
     */
    void enterDesc(MonsterParser.DescContext ctx);

    /**
     * Exit a parse tree produced by {@link MonsterParser#desc}.
     *
     * @param ctx the parse tree
     */
    void exitDesc(MonsterParser.DescContext ctx);

    /**
     * Enter a parse tree produced by {@link MonsterParser#shape}.
     *
     * @param ctx the parse tree
     */
    void enterShape(MonsterParser.ShapeContext ctx);

    /**
     * Exit a parse tree produced by {@link MonsterParser#shape}.
     *
     * @param ctx the parse tree
     */
    void exitShape(MonsterParser.ShapeContext ctx);

    /**
     * Enter a parse tree produced by {@link MonsterParser#drop}.
     *
     * @param ctx the parse tree
     */
    void enterDrop(MonsterParser.DropContext ctx);

    /**
     * Exit a parse tree produced by {@link MonsterParser#drop}.
     *
     * @param ctx the parse tree
     */
    void exitDrop(MonsterParser.DropContext ctx);

    /**
     * Enter a parse tree produced by {@link MonsterParser#dropBase}.
     *
     * @param ctx the parse tree
     */
    void enterDropBase(MonsterParser.DropBaseContext ctx);

    /**
     * Exit a parse tree produced by {@link MonsterParser#dropBase}.
     *
     * @param ctx the parse tree
     */
    void exitDropBase(MonsterParser.DropBaseContext ctx);

    /**
     * Enter a parse tree produced by {@link MonsterParser#mimic}.
     *
     * @param ctx the parse tree
     */
    void enterMimic(MonsterParser.MimicContext ctx);

    /**
     * Exit a parse tree produced by {@link MonsterParser#mimic}.
     *
     * @param ctx the parse tree
     */
    void exitMimic(MonsterParser.MimicContext ctx);

    /**
     * Enter a parse tree produced by {@link MonsterParser#friends}.
     *
     * @param ctx the parse tree
     */
    void enterFriends(MonsterParser.FriendsContext ctx);

    /**
     * Exit a parse tree produced by {@link MonsterParser#friends}.
     *
     * @param ctx the parse tree
     */
    void exitFriends(MonsterParser.FriendsContext ctx);

    /**
     * Enter a parse tree produced by {@link MonsterParser#friendsBase}.
     *
     * @param ctx the parse tree
     */
    void enterFriendsBase(MonsterParser.FriendsBaseContext ctx);

    /**
     * Exit a parse tree produced by {@link MonsterParser#friendsBase}.
     *
     * @param ctx the parse tree
     */
    void exitFriendsBase(MonsterParser.FriendsBaseContext ctx);

    /**
     * Enter a parse tree produced by {@link MonsterParser#monster}.
     *
     * @param ctx the parse tree
     */
    void enterMonster(MonsterParser.MonsterContext ctx);

    /**
     * Exit a parse tree produced by {@link MonsterParser#monster}.
     *
     * @param ctx the parse tree
     */
    void exitMonster(MonsterParser.MonsterContext ctx);

    /**
     * Enter a parse tree produced by {@link MonsterParser#file}.
     *
     * @param ctx the parse tree
     */
    void enterFile(MonsterParser.FileContext ctx);

    /**
     * Exit a parse tree produced by {@link MonsterParser#file}.
     *
     * @param ctx the parse tree
     */
    void exitFile(MonsterParser.FileContext ctx);
}