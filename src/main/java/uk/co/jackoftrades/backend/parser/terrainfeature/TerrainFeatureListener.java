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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/parser/grammars/TerrainFeature.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.terrainfeature;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link TerrainFeatureParser}.
 */
public interface TerrainFeatureListener extends ParseTreeListener {
    /**
     * Enter a parse tree produced by {@link TerrainFeatureParser#code}.
     *
     * @param ctx the parse tree
     */
    void enterCode(TerrainFeatureParser.CodeContext ctx);

    /**
     * Exit a parse tree produced by {@link TerrainFeatureParser#code}.
     *
     * @param ctx the parse tree
     */
    void exitCode(TerrainFeatureParser.CodeContext ctx);

    /**
     * Enter a parse tree produced by {@link TerrainFeatureParser#name}.
     *
     * @param ctx the parse tree
     */
    void enterName(TerrainFeatureParser.NameContext ctx);

    /**
     * Exit a parse tree produced by {@link TerrainFeatureParser#name}.
     *
     * @param ctx the parse tree
     */
    void exitName(TerrainFeatureParser.NameContext ctx);

    /**
     * Enter a parse tree produced by {@link TerrainFeatureParser#graphics}.
     *
     * @param ctx the parse tree
     */
    void enterGraphics(TerrainFeatureParser.GraphicsContext ctx);

    /**
     * Exit a parse tree produced by {@link TerrainFeatureParser#graphics}.
     *
     * @param ctx the parse tree
     */
    void exitGraphics(TerrainFeatureParser.GraphicsContext ctx);

    /**
     * Enter a parse tree produced by {@link TerrainFeatureParser#mimic}.
     *
     * @param ctx the parse tree
     */
    void enterMimic(TerrainFeatureParser.MimicContext ctx);

    /**
     * Exit a parse tree produced by {@link TerrainFeatureParser#mimic}.
     *
     * @param ctx the parse tree
     */
    void exitMimic(TerrainFeatureParser.MimicContext ctx);

    /**
     * Enter a parse tree produced by {@link TerrainFeatureParser#priority}.
     *
     * @param ctx the parse tree
     */
    void enterPriority(TerrainFeatureParser.PriorityContext ctx);

    /**
     * Exit a parse tree produced by {@link TerrainFeatureParser#priority}.
     *
     * @param ctx the parse tree
     */
    void exitPriority(TerrainFeatureParser.PriorityContext ctx);

    /**
     * Enter a parse tree produced by {@link TerrainFeatureParser#flags}.
     *
     * @param ctx the parse tree
     */
    void enterFlags(TerrainFeatureParser.FlagsContext ctx);

    /**
     * Exit a parse tree produced by {@link TerrainFeatureParser#flags}.
     *
     * @param ctx the parse tree
     */
    void exitFlags(TerrainFeatureParser.FlagsContext ctx);

    /**
     * Enter a parse tree produced by {@link TerrainFeatureParser#digging}.
     *
     * @param ctx the parse tree
     */
    void enterDigging(TerrainFeatureParser.DiggingContext ctx);

    /**
     * Exit a parse tree produced by {@link TerrainFeatureParser#digging}.
     *
     * @param ctx the parse tree
     */
    void exitDigging(TerrainFeatureParser.DiggingContext ctx);

    /**
     * Enter a parse tree produced by {@link TerrainFeatureParser#walk_message}.
     *
     * @param ctx the parse tree
     */
    void enterWalk_message(TerrainFeatureParser.Walk_messageContext ctx);

    /**
     * Exit a parse tree produced by {@link TerrainFeatureParser#walk_message}.
     *
     * @param ctx the parse tree
     */
    void exitWalk_message(TerrainFeatureParser.Walk_messageContext ctx);

    /**
     * Enter a parse tree produced by {@link TerrainFeatureParser#run_message}.
     *
     * @param ctx the parse tree
     */
    void enterRun_message(TerrainFeatureParser.Run_messageContext ctx);

    /**
     * Exit a parse tree produced by {@link TerrainFeatureParser#run_message}.
     *
     * @param ctx the parse tree
     */
    void exitRun_message(TerrainFeatureParser.Run_messageContext ctx);

    /**
     * Enter a parse tree produced by {@link TerrainFeatureParser#hurt_message}.
     *
     * @param ctx the parse tree
     */
    void enterHurt_message(TerrainFeatureParser.Hurt_messageContext ctx);

    /**
     * Exit a parse tree produced by {@link TerrainFeatureParser#hurt_message}.
     *
     * @param ctx the parse tree
     */
    void exitHurt_message(TerrainFeatureParser.Hurt_messageContext ctx);

    /**
     * Enter a parse tree produced by {@link TerrainFeatureParser#die_message}.
     *
     * @param ctx the parse tree
     */
    void enterDie_message(TerrainFeatureParser.Die_messageContext ctx);

    /**
     * Exit a parse tree produced by {@link TerrainFeatureParser#die_message}.
     *
     * @param ctx the parse tree
     */
    void exitDie_message(TerrainFeatureParser.Die_messageContext ctx);

    /**
     * Enter a parse tree produced by {@link TerrainFeatureParser#confused_message}.
     *
     * @param ctx the parse tree
     */
    void enterConfused_message(TerrainFeatureParser.Confused_messageContext ctx);

    /**
     * Exit a parse tree produced by {@link TerrainFeatureParser#confused_message}.
     *
     * @param ctx the parse tree
     */
    void exitConfused_message(TerrainFeatureParser.Confused_messageContext ctx);

    /**
     * Enter a parse tree produced by {@link TerrainFeatureParser#look_prefix}.
     *
     * @param ctx the parse tree
     */
    void enterLook_prefix(TerrainFeatureParser.Look_prefixContext ctx);

    /**
     * Exit a parse tree produced by {@link TerrainFeatureParser#look_prefix}.
     *
     * @param ctx the parse tree
     */
    void exitLook_prefix(TerrainFeatureParser.Look_prefixContext ctx);

    /**
     * Enter a parse tree produced by {@link TerrainFeatureParser#look_in_preposition}.
     *
     * @param ctx the parse tree
     */
    void enterLook_in_preposition(TerrainFeatureParser.Look_in_prepositionContext ctx);

    /**
     * Exit a parse tree produced by {@link TerrainFeatureParser#look_in_preposition}.
     *
     * @param ctx the parse tree
     */
    void exitLook_in_preposition(TerrainFeatureParser.Look_in_prepositionContext ctx);

    /**
     * Enter a parse tree produced by {@link TerrainFeatureParser#resist_flag}.
     *
     * @param ctx the parse tree
     */
    void enterResist_flag(TerrainFeatureParser.Resist_flagContext ctx);

    /**
     * Exit a parse tree produced by {@link TerrainFeatureParser#resist_flag}.
     *
     * @param ctx the parse tree
     */
    void exitResist_flag(TerrainFeatureParser.Resist_flagContext ctx);

    /**
     * Enter a parse tree produced by {@link TerrainFeatureParser#desc}.
     *
     * @param ctx the parse tree
     */
    void enterDesc(TerrainFeatureParser.DescContext ctx);

    /**
     * Exit a parse tree produced by {@link TerrainFeatureParser#desc}.
     *
     * @param ctx the parse tree
     */
    void exitDesc(TerrainFeatureParser.DescContext ctx);

    /**
     * Enter a parse tree produced by {@link TerrainFeatureParser#terrain}.
     *
     * @param ctx the parse tree
     */
    void enterTerrain(TerrainFeatureParser.TerrainContext ctx);

    /**
     * Exit a parse tree produced by {@link TerrainFeatureParser#terrain}.
     *
     * @param ctx the parse tree
     */
    void exitTerrain(TerrainFeatureParser.TerrainContext ctx);

    /**
     * Enter a parse tree produced by {@link TerrainFeatureParser#file}.
     *
     * @param ctx the parse tree
     */
    void enterFile(TerrainFeatureParser.FileContext ctx);

    /**
     * Exit a parse tree produced by {@link TerrainFeatureParser#file}.
     *
     * @param ctx the parse tree
     */
    void exitFile(TerrainFeatureParser.FileContext ctx);
}