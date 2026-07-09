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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/parser/grammars/Artifact.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.artifact;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link ArtifactParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 *            operations with no return type.
 */
public interface ArtifactVisitor<T> extends ParseTreeVisitor<T> {
    /**
     * Visit a parse tree produced by {@link ArtifactParser#name}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitName(ArtifactParser.NameContext ctx);

    /**
     * Visit a parse tree produced by {@link ArtifactParser#baseObject}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitBaseObject(ArtifactParser.BaseObjectContext ctx);

    /**
     * Visit a parse tree produced by {@link ArtifactParser#graphics}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitGraphics(ArtifactParser.GraphicsContext ctx);

    /**
     * Visit a parse tree produced by {@link ArtifactParser#level}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitLevel(ArtifactParser.LevelContext ctx);

    /**
     * Visit a parse tree produced by {@link ArtifactParser#weight}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitWeight(ArtifactParser.WeightContext ctx);

    /**
     * Visit a parse tree produced by {@link ArtifactParser#cost}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitCost(ArtifactParser.CostContext ctx);

    /**
     * Visit a parse tree produced by {@link ArtifactParser#alloc}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitAlloc(ArtifactParser.AllocContext ctx);

    /**
     * Visit a parse tree produced by {@link ArtifactParser#attack}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitAttack(ArtifactParser.AttackContext ctx);

    /**
     * Visit a parse tree produced by {@link ArtifactParser#armour}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitArmour(ArtifactParser.ArmourContext ctx);

    /**
     * Visit a parse tree produced by {@link ArtifactParser#flags}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFlags(ArtifactParser.FlagsContext ctx);

    /**
     * Visit a parse tree produced by {@link ArtifactParser#values}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitValues(ArtifactParser.ValuesContext ctx);

    /**
     * Visit a parse tree produced by {@link ArtifactParser#act}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitAct(ArtifactParser.ActContext ctx);

    /**
     * Visit a parse tree produced by {@link ArtifactParser#time}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitTime(ArtifactParser.TimeContext ctx);

    /**
     * Visit a parse tree produced by {@link ArtifactParser#desc}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitDesc(ArtifactParser.DescContext ctx);

    /**
     * Visit a parse tree produced by {@link ArtifactParser#brand}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitBrand(ArtifactParser.BrandContext ctx);

    /**
     * Visit a parse tree produced by {@link ArtifactParser#slay}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitSlay(ArtifactParser.SlayContext ctx);

    /**
     * Visit a parse tree produced by {@link ArtifactParser#curse}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitCurse(ArtifactParser.CurseContext ctx);

    /**
     * Visit a parse tree produced by {@link ArtifactParser#msg}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitMsg(ArtifactParser.MsgContext ctx);

    /**
     * Visit a parse tree produced by {@link ArtifactParser#artifact}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitArtifact(ArtifactParser.ArtifactContext ctx);

    /**
     * Visit a parse tree produced by {@link ArtifactParser#file}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFile(ArtifactParser.FileContext ctx);
}