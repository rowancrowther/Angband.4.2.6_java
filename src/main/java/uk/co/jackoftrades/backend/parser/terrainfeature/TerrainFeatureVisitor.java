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

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link TerrainFeatureParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 *            operations with no return type.
 */
public interface TerrainFeatureVisitor<T> extends ParseTreeVisitor<T> {
    /**
     * Visit a parse tree produced by {@link TerrainFeatureParser#code}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitCode(TerrainFeatureParser.CodeContext ctx);

    /**
     * Visit a parse tree produced by {@link TerrainFeatureParser#name}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitName(TerrainFeatureParser.NameContext ctx);

    /**
     * Visit a parse tree produced by {@link TerrainFeatureParser#graphics}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitGraphics(TerrainFeatureParser.GraphicsContext ctx);

    /**
     * Visit a parse tree produced by {@link TerrainFeatureParser#mimic}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitMimic(TerrainFeatureParser.MimicContext ctx);

    /**
     * Visit a parse tree produced by {@link TerrainFeatureParser#priority}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitPriority(TerrainFeatureParser.PriorityContext ctx);

    /**
     * Visit a parse tree produced by {@link TerrainFeatureParser#flags}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFlags(TerrainFeatureParser.FlagsContext ctx);

    /**
     * Visit a parse tree produced by {@link TerrainFeatureParser#digging}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitDigging(TerrainFeatureParser.DiggingContext ctx);

    /**
     * Visit a parse tree produced by {@link TerrainFeatureParser#walk_message}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitWalk_message(TerrainFeatureParser.Walk_messageContext ctx);

    /**
     * Visit a parse tree produced by {@link TerrainFeatureParser#run_message}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitRun_message(TerrainFeatureParser.Run_messageContext ctx);

    /**
     * Visit a parse tree produced by {@link TerrainFeatureParser#hurt_message}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitHurt_message(TerrainFeatureParser.Hurt_messageContext ctx);

    /**
     * Visit a parse tree produced by {@link TerrainFeatureParser#die_message}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitDie_message(TerrainFeatureParser.Die_messageContext ctx);

    /**
     * Visit a parse tree produced by {@link TerrainFeatureParser#confused_message}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitConfused_message(TerrainFeatureParser.Confused_messageContext ctx);

    /**
     * Visit a parse tree produced by {@link TerrainFeatureParser#look_prefix}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitLook_prefix(TerrainFeatureParser.Look_prefixContext ctx);

    /**
     * Visit a parse tree produced by {@link TerrainFeatureParser#look_in_preposition}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitLook_in_preposition(TerrainFeatureParser.Look_in_prepositionContext ctx);

    /**
     * Visit a parse tree produced by {@link TerrainFeatureParser#resist_flag}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitResist_flag(TerrainFeatureParser.Resist_flagContext ctx);

    /**
     * Visit a parse tree produced by {@link TerrainFeatureParser#desc}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitDesc(TerrainFeatureParser.DescContext ctx);

    /**
     * Visit a parse tree produced by {@link TerrainFeatureParser#terrain}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitTerrain(TerrainFeatureParser.TerrainContext ctx);

    /**
     * Visit a parse tree produced by {@link TerrainFeatureParser#file}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFile(TerrainFeatureParser.FileContext ctx);
}