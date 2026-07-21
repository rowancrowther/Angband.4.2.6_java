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

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link MonsterGrammar}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 *            operations with no return type.
 */
public interface MonsterGrammarVisitor<T> extends ParseTreeVisitor<T> {
    /**
     * Visit a parse tree produced by {@link MonsterGrammar#recordCount}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitRecordCount(MonsterGrammar.RecordCountContext ctx);

    /**
     * Visit a parse tree produced by {@link MonsterGrammar#name}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitName(MonsterGrammar.NameContext ctx);

    /**
     * Visit a parse tree produced by {@link MonsterGrammar#plural}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitPlural(MonsterGrammar.PluralContext ctx);

    /**
     * Visit a parse tree produced by {@link MonsterGrammar#base}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitBase(MonsterGrammar.BaseContext ctx);

    /**
     * Visit a parse tree produced by {@link MonsterGrammar#glyph}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitGlyph(MonsterGrammar.GlyphContext ctx);

    /**
     * Visit a parse tree produced by {@link MonsterGrammar#colour}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitColour(MonsterGrammar.ColourContext ctx);

    /**
     * Visit a parse tree produced by {@link MonsterGrammar#speed}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitSpeed(MonsterGrammar.SpeedContext ctx);

    /**
     * Visit a parse tree produced by {@link MonsterGrammar#hitPoints}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitHitPoints(MonsterGrammar.HitPointsContext ctx);

    /**
     * Visit a parse tree produced by {@link MonsterGrammar#light}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitLight(MonsterGrammar.LightContext ctx);

    /**
     * Visit a parse tree produced by {@link MonsterGrammar#hearing}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitHearing(MonsterGrammar.HearingContext ctx);

    /**
     * Visit a parse tree produced by {@link MonsterGrammar#smell}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitSmell(MonsterGrammar.SmellContext ctx);

    /**
     * Visit a parse tree produced by {@link MonsterGrammar#shape}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitShape(MonsterGrammar.ShapeContext ctx);

    /**
     * Visit a parse tree produced by {@link MonsterGrammar#colourCycle}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitColourCycle(MonsterGrammar.ColourCycleContext ctx);

    /**
     * Visit a parse tree produced by {@link MonsterGrammar#armourClass}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitArmourClass(MonsterGrammar.ArmourClassContext ctx);

    /**
     * Visit a parse tree produced by {@link MonsterGrammar#sleepiness}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitSleepiness(MonsterGrammar.SleepinessContext ctx);

    /**
     * Visit a parse tree produced by {@link MonsterGrammar#depthLevel}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitDepthLevel(MonsterGrammar.DepthLevelContext ctx);

    /**
     * Visit a parse tree produced by {@link MonsterGrammar#rarity}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitRarity(MonsterGrammar.RarityContext ctx);

    /**
     * Visit a parse tree produced by {@link MonsterGrammar#experience}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitExperience(MonsterGrammar.ExperienceContext ctx);

    /**
     * Visit a parse tree produced by {@link MonsterGrammar#blow}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitBlow(MonsterGrammar.BlowContext ctx);

    /**
     * Visit a parse tree produced by {@link MonsterGrammar#flags}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFlags(MonsterGrammar.FlagsContext ctx);

    /**
     * Visit a parse tree produced by {@link MonsterGrammar#flagsOff}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFlagsOff(MonsterGrammar.FlagsOffContext ctx);

    /**
     * Visit a parse tree produced by {@link MonsterGrammar#innateFreq}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitInnateFreq(MonsterGrammar.InnateFreqContext ctx);

    /**
     * Visit a parse tree produced by {@link MonsterGrammar#spellFreq}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitSpellFreq(MonsterGrammar.SpellFreqContext ctx);

    /**
     * Visit a parse tree produced by {@link MonsterGrammar#spellPower}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitSpellPower(MonsterGrammar.SpellPowerContext ctx);

    /**
     * Visit a parse tree produced by {@link MonsterGrammar#spells}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitSpells(MonsterGrammar.SpellsContext ctx);

    /**
     * Visit a parse tree produced by {@link MonsterGrammar#messageVis}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitMessageVis(MonsterGrammar.MessageVisContext ctx);

    /**
     * Visit a parse tree produced by {@link MonsterGrammar#messageInvis}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitMessageInvis(MonsterGrammar.MessageInvisContext ctx);

    /**
     * Visit a parse tree produced by {@link MonsterGrammar#messageMiss}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitMessageMiss(MonsterGrammar.MessageMissContext ctx);

    /**
     * Visit a parse tree produced by {@link MonsterGrammar#desc}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitDesc(MonsterGrammar.DescContext ctx);

    /**
     * Visit a parse tree produced by {@link MonsterGrammar#drop}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitDrop(MonsterGrammar.DropContext ctx);

    /**
     * Visit a parse tree produced by {@link MonsterGrammar#dropBase}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitDropBase(MonsterGrammar.DropBaseContext ctx);

    /**
     * Visit a parse tree produced by {@link MonsterGrammar#mimic}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitMimic(MonsterGrammar.MimicContext ctx);

    /**
     * Visit a parse tree produced by {@link MonsterGrammar#friends}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFriends(MonsterGrammar.FriendsContext ctx);

    /**
     * Visit a parse tree produced by {@link MonsterGrammar#friendsBase}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFriendsBase(MonsterGrammar.FriendsBaseContext ctx);

    /**
     * Visit a parse tree produced by {@link MonsterGrammar#monster}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitMonster(MonsterGrammar.MonsterContext ctx);

    /**
     * Visit a parse tree produced by {@link MonsterGrammar#file}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFile(MonsterGrammar.FileContext ctx);
}