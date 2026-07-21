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
// Generated from MonsterGrammar.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.grammars.monster;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link MonsterGrammar}.
 */
public interface MonsterGrammarListener extends ParseTreeListener {
    /**
     * Enter a parse tree produced by {@link MonsterGrammar#recordCount}.
     *
     * @param ctx the parse tree
     */
    void enterRecordCount(MonsterGrammar.RecordCountContext ctx);

    /**
     * Exit a parse tree produced by {@link MonsterGrammar#recordCount}.
     *
     * @param ctx the parse tree
     */
    void exitRecordCount(MonsterGrammar.RecordCountContext ctx);

    /**
     * Enter a parse tree produced by {@link MonsterGrammar#name}.
     *
     * @param ctx the parse tree
     */
    void enterName(MonsterGrammar.NameContext ctx);

    /**
     * Exit a parse tree produced by {@link MonsterGrammar#name}.
     *
     * @param ctx the parse tree
     */
    void exitName(MonsterGrammar.NameContext ctx);

    /**
     * Enter a parse tree produced by {@link MonsterGrammar#plural}.
     *
     * @param ctx the parse tree
     */
    void enterPlural(MonsterGrammar.PluralContext ctx);

    /**
     * Exit a parse tree produced by {@link MonsterGrammar#plural}.
     *
     * @param ctx the parse tree
     */
    void exitPlural(MonsterGrammar.PluralContext ctx);

    /**
     * Enter a parse tree produced by {@link MonsterGrammar#base}.
     *
     * @param ctx the parse tree
     */
    void enterBase(MonsterGrammar.BaseContext ctx);

    /**
     * Exit a parse tree produced by {@link MonsterGrammar#base}.
     *
     * @param ctx the parse tree
     */
    void exitBase(MonsterGrammar.BaseContext ctx);

    /**
     * Enter a parse tree produced by {@link MonsterGrammar#glyph}.
     *
     * @param ctx the parse tree
     */
    void enterGlyph(MonsterGrammar.GlyphContext ctx);

    /**
     * Exit a parse tree produced by {@link MonsterGrammar#glyph}.
     *
     * @param ctx the parse tree
     */
    void exitGlyph(MonsterGrammar.GlyphContext ctx);

    /**
     * Enter a parse tree produced by {@link MonsterGrammar#colour}.
     *
     * @param ctx the parse tree
     */
    void enterColour(MonsterGrammar.ColourContext ctx);

    /**
     * Exit a parse tree produced by {@link MonsterGrammar#colour}.
     *
     * @param ctx the parse tree
     */
    void exitColour(MonsterGrammar.ColourContext ctx);

    /**
     * Enter a parse tree produced by {@link MonsterGrammar#speed}.
     *
     * @param ctx the parse tree
     */
    void enterSpeed(MonsterGrammar.SpeedContext ctx);

    /**
     * Exit a parse tree produced by {@link MonsterGrammar#speed}.
     *
     * @param ctx the parse tree
     */
    void exitSpeed(MonsterGrammar.SpeedContext ctx);

    /**
     * Enter a parse tree produced by {@link MonsterGrammar#hitPoints}.
     *
     * @param ctx the parse tree
     */
    void enterHitPoints(MonsterGrammar.HitPointsContext ctx);

    /**
     * Exit a parse tree produced by {@link MonsterGrammar#hitPoints}.
     *
     * @param ctx the parse tree
     */
    void exitHitPoints(MonsterGrammar.HitPointsContext ctx);

    /**
     * Enter a parse tree produced by {@link MonsterGrammar#light}.
     *
     * @param ctx the parse tree
     */
    void enterLight(MonsterGrammar.LightContext ctx);

    /**
     * Exit a parse tree produced by {@link MonsterGrammar#light}.
     *
     * @param ctx the parse tree
     */
    void exitLight(MonsterGrammar.LightContext ctx);

    /**
     * Enter a parse tree produced by {@link MonsterGrammar#hearing}.
     *
     * @param ctx the parse tree
     */
    void enterHearing(MonsterGrammar.HearingContext ctx);

    /**
     * Exit a parse tree produced by {@link MonsterGrammar#hearing}.
     *
     * @param ctx the parse tree
     */
    void exitHearing(MonsterGrammar.HearingContext ctx);

    /**
     * Enter a parse tree produced by {@link MonsterGrammar#smell}.
     *
     * @param ctx the parse tree
     */
    void enterSmell(MonsterGrammar.SmellContext ctx);

    /**
     * Exit a parse tree produced by {@link MonsterGrammar#smell}.
     *
     * @param ctx the parse tree
     */
    void exitSmell(MonsterGrammar.SmellContext ctx);

    /**
     * Enter a parse tree produced by {@link MonsterGrammar#shape}.
     *
     * @param ctx the parse tree
     */
    void enterShape(MonsterGrammar.ShapeContext ctx);

    /**
     * Exit a parse tree produced by {@link MonsterGrammar#shape}.
     *
     * @param ctx the parse tree
     */
    void exitShape(MonsterGrammar.ShapeContext ctx);

    /**
     * Enter a parse tree produced by {@link MonsterGrammar#colourCycle}.
     *
     * @param ctx the parse tree
     */
    void enterColourCycle(MonsterGrammar.ColourCycleContext ctx);

    /**
     * Exit a parse tree produced by {@link MonsterGrammar#colourCycle}.
     *
     * @param ctx the parse tree
     */
    void exitColourCycle(MonsterGrammar.ColourCycleContext ctx);

    /**
     * Enter a parse tree produced by {@link MonsterGrammar#armourClass}.
     *
     * @param ctx the parse tree
     */
    void enterArmourClass(MonsterGrammar.ArmourClassContext ctx);

    /**
     * Exit a parse tree produced by {@link MonsterGrammar#armourClass}.
     *
     * @param ctx the parse tree
     */
    void exitArmourClass(MonsterGrammar.ArmourClassContext ctx);

    /**
     * Enter a parse tree produced by {@link MonsterGrammar#sleepiness}.
     *
     * @param ctx the parse tree
     */
    void enterSleepiness(MonsterGrammar.SleepinessContext ctx);

    /**
     * Exit a parse tree produced by {@link MonsterGrammar#sleepiness}.
     *
     * @param ctx the parse tree
     */
    void exitSleepiness(MonsterGrammar.SleepinessContext ctx);

    /**
     * Enter a parse tree produced by {@link MonsterGrammar#depthLevel}.
     *
     * @param ctx the parse tree
     */
    void enterDepthLevel(MonsterGrammar.DepthLevelContext ctx);

    /**
     * Exit a parse tree produced by {@link MonsterGrammar#depthLevel}.
     *
     * @param ctx the parse tree
     */
    void exitDepthLevel(MonsterGrammar.DepthLevelContext ctx);

    /**
     * Enter a parse tree produced by {@link MonsterGrammar#rarity}.
     *
     * @param ctx the parse tree
     */
    void enterRarity(MonsterGrammar.RarityContext ctx);

    /**
     * Exit a parse tree produced by {@link MonsterGrammar#rarity}.
     *
     * @param ctx the parse tree
     */
    void exitRarity(MonsterGrammar.RarityContext ctx);

    /**
     * Enter a parse tree produced by {@link MonsterGrammar#experience}.
     *
     * @param ctx the parse tree
     */
    void enterExperience(MonsterGrammar.ExperienceContext ctx);

    /**
     * Exit a parse tree produced by {@link MonsterGrammar#experience}.
     *
     * @param ctx the parse tree
     */
    void exitExperience(MonsterGrammar.ExperienceContext ctx);

    /**
     * Enter a parse tree produced by {@link MonsterGrammar#blow}.
     *
     * @param ctx the parse tree
     */
    void enterBlow(MonsterGrammar.BlowContext ctx);

    /**
     * Exit a parse tree produced by {@link MonsterGrammar#blow}.
     *
     * @param ctx the parse tree
     */
    void exitBlow(MonsterGrammar.BlowContext ctx);

    /**
     * Enter a parse tree produced by {@link MonsterGrammar#flags}.
     *
     * @param ctx the parse tree
     */
    void enterFlags(MonsterGrammar.FlagsContext ctx);

    /**
     * Exit a parse tree produced by {@link MonsterGrammar#flags}.
     *
     * @param ctx the parse tree
     */
    void exitFlags(MonsterGrammar.FlagsContext ctx);

    /**
     * Enter a parse tree produced by {@link MonsterGrammar#flagsOff}.
     *
     * @param ctx the parse tree
     */
    void enterFlagsOff(MonsterGrammar.FlagsOffContext ctx);

    /**
     * Exit a parse tree produced by {@link MonsterGrammar#flagsOff}.
     *
     * @param ctx the parse tree
     */
    void exitFlagsOff(MonsterGrammar.FlagsOffContext ctx);

    /**
     * Enter a parse tree produced by {@link MonsterGrammar#innateFreq}.
     *
     * @param ctx the parse tree
     */
    void enterInnateFreq(MonsterGrammar.InnateFreqContext ctx);

    /**
     * Exit a parse tree produced by {@link MonsterGrammar#innateFreq}.
     *
     * @param ctx the parse tree
     */
    void exitInnateFreq(MonsterGrammar.InnateFreqContext ctx);

    /**
     * Enter a parse tree produced by {@link MonsterGrammar#spellFreq}.
     *
     * @param ctx the parse tree
     */
    void enterSpellFreq(MonsterGrammar.SpellFreqContext ctx);

    /**
     * Exit a parse tree produced by {@link MonsterGrammar#spellFreq}.
     *
     * @param ctx the parse tree
     */
    void exitSpellFreq(MonsterGrammar.SpellFreqContext ctx);

    /**
     * Enter a parse tree produced by {@link MonsterGrammar#spellPower}.
     *
     * @param ctx the parse tree
     */
    void enterSpellPower(MonsterGrammar.SpellPowerContext ctx);

    /**
     * Exit a parse tree produced by {@link MonsterGrammar#spellPower}.
     *
     * @param ctx the parse tree
     */
    void exitSpellPower(MonsterGrammar.SpellPowerContext ctx);

    /**
     * Enter a parse tree produced by {@link MonsterGrammar#spells}.
     *
     * @param ctx the parse tree
     */
    void enterSpells(MonsterGrammar.SpellsContext ctx);

    /**
     * Exit a parse tree produced by {@link MonsterGrammar#spells}.
     *
     * @param ctx the parse tree
     */
    void exitSpells(MonsterGrammar.SpellsContext ctx);

    /**
     * Enter a parse tree produced by {@link MonsterGrammar#messageVis}.
     *
     * @param ctx the parse tree
     */
    void enterMessageVis(MonsterGrammar.MessageVisContext ctx);

    /**
     * Exit a parse tree produced by {@link MonsterGrammar#messageVis}.
     *
     * @param ctx the parse tree
     */
    void exitMessageVis(MonsterGrammar.MessageVisContext ctx);

    /**
     * Enter a parse tree produced by {@link MonsterGrammar#messageInvis}.
     *
     * @param ctx the parse tree
     */
    void enterMessageInvis(MonsterGrammar.MessageInvisContext ctx);

    /**
     * Exit a parse tree produced by {@link MonsterGrammar#messageInvis}.
     *
     * @param ctx the parse tree
     */
    void exitMessageInvis(MonsterGrammar.MessageInvisContext ctx);

    /**
     * Enter a parse tree produced by {@link MonsterGrammar#messageMiss}.
     *
     * @param ctx the parse tree
     */
    void enterMessageMiss(MonsterGrammar.MessageMissContext ctx);

    /**
     * Exit a parse tree produced by {@link MonsterGrammar#messageMiss}.
     *
     * @param ctx the parse tree
     */
    void exitMessageMiss(MonsterGrammar.MessageMissContext ctx);

    /**
     * Enter a parse tree produced by {@link MonsterGrammar#desc}.
     *
     * @param ctx the parse tree
     */
    void enterDesc(MonsterGrammar.DescContext ctx);

    /**
     * Exit a parse tree produced by {@link MonsterGrammar#desc}.
     *
     * @param ctx the parse tree
     */
    void exitDesc(MonsterGrammar.DescContext ctx);

    /**
     * Enter a parse tree produced by {@link MonsterGrammar#drop}.
     *
     * @param ctx the parse tree
     */
    void enterDrop(MonsterGrammar.DropContext ctx);

    /**
     * Exit a parse tree produced by {@link MonsterGrammar#drop}.
     *
     * @param ctx the parse tree
     */
    void exitDrop(MonsterGrammar.DropContext ctx);

    /**
     * Enter a parse tree produced by {@link MonsterGrammar#dropBase}.
     *
     * @param ctx the parse tree
     */
    void enterDropBase(MonsterGrammar.DropBaseContext ctx);

    /**
     * Exit a parse tree produced by {@link MonsterGrammar#dropBase}.
     *
     * @param ctx the parse tree
     */
    void exitDropBase(MonsterGrammar.DropBaseContext ctx);

    /**
     * Enter a parse tree produced by {@link MonsterGrammar#mimic}.
     *
     * @param ctx the parse tree
     */
    void enterMimic(MonsterGrammar.MimicContext ctx);

    /**
     * Exit a parse tree produced by {@link MonsterGrammar#mimic}.
     *
     * @param ctx the parse tree
     */
    void exitMimic(MonsterGrammar.MimicContext ctx);

    /**
     * Enter a parse tree produced by {@link MonsterGrammar#friends}.
     *
     * @param ctx the parse tree
     */
    void enterFriends(MonsterGrammar.FriendsContext ctx);

    /**
     * Exit a parse tree produced by {@link MonsterGrammar#friends}.
     *
     * @param ctx the parse tree
     */
    void exitFriends(MonsterGrammar.FriendsContext ctx);

    /**
     * Enter a parse tree produced by {@link MonsterGrammar#friendsBase}.
     *
     * @param ctx the parse tree
     */
    void enterFriendsBase(MonsterGrammar.FriendsBaseContext ctx);

    /**
     * Exit a parse tree produced by {@link MonsterGrammar#friendsBase}.
     *
     * @param ctx the parse tree
     */
    void exitFriendsBase(MonsterGrammar.FriendsBaseContext ctx);

    /**
     * Enter a parse tree produced by {@link MonsterGrammar#monster}.
     *
     * @param ctx the parse tree
     */
    void enterMonster(MonsterGrammar.MonsterContext ctx);

    /**
     * Exit a parse tree produced by {@link MonsterGrammar#monster}.
     *
     * @param ctx the parse tree
     */
    void exitMonster(MonsterGrammar.MonsterContext ctx);

    /**
     * Enter a parse tree produced by {@link MonsterGrammar#file}.
     *
     * @param ctx the parse tree
     */
    void enterFile(MonsterGrammar.FileContext ctx);

    /**
     * Exit a parse tree produced by {@link MonsterGrammar#file}.
     *
     * @param ctx the parse tree
     */
    void exitFile(MonsterGrammar.FileContext ctx);
}