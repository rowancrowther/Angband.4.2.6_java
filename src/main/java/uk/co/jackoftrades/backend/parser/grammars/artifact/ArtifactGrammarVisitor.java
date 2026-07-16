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
// Generated from ArtifactGrammar.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.grammars.artifact;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link ArtifactGrammar}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 *            operations with no return type.
 */
public interface ArtifactGrammarVisitor<T> extends ParseTreeVisitor<T> {
    /**
     * Visit a parse tree produced by {@link ArtifactGrammar#recordCount}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitRecordCount(ArtifactGrammar.RecordCountContext ctx);

    /**
     * Visit a parse tree produced by {@link ArtifactGrammar#name}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitName(ArtifactGrammar.NameContext ctx);

    /**
     * Visit a parse tree produced by {@link ArtifactGrammar#baseObject}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitBaseObject(ArtifactGrammar.BaseObjectContext ctx);

    /**
     * Visit a parse tree produced by {@link ArtifactGrammar#graphics}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitGraphics(ArtifactGrammar.GraphicsContext ctx);

    /**
     * Visit a parse tree produced by {@link ArtifactGrammar#level}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitLevel(ArtifactGrammar.LevelContext ctx);

    /**
     * Visit a parse tree produced by {@link ArtifactGrammar#weight}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitWeight(ArtifactGrammar.WeightContext ctx);

    /**
     * Visit a parse tree produced by {@link ArtifactGrammar#cost}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitCost(ArtifactGrammar.CostContext ctx);

    /**
     * Visit a parse tree produced by {@link ArtifactGrammar#alloc}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitAlloc(ArtifactGrammar.AllocContext ctx);

    /**
     * Visit a parse tree produced by {@link ArtifactGrammar#attack}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitAttack(ArtifactGrammar.AttackContext ctx);

    /**
     * Visit a parse tree produced by {@link ArtifactGrammar#armour}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitArmour(ArtifactGrammar.ArmourContext ctx);

    /**
     * Visit a parse tree produced by {@link ArtifactGrammar#flags}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFlags(ArtifactGrammar.FlagsContext ctx);

    /**
     * Visit a parse tree produced by {@link ArtifactGrammar#act}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitAct(ArtifactGrammar.ActContext ctx);

    /**
     * Visit a parse tree produced by {@link ArtifactGrammar#time}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitTime(ArtifactGrammar.TimeContext ctx);

    /**
     * Visit a parse tree produced by {@link ArtifactGrammar#msg}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitMsg(ArtifactGrammar.MsgContext ctx);

    /**
     * Visit a parse tree produced by {@link ArtifactGrammar#values}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitValues(ArtifactGrammar.ValuesContext ctx);

    /**
     * Visit a parse tree produced by {@link ArtifactGrammar#brand}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitBrand(ArtifactGrammar.BrandContext ctx);

    /**
     * Visit a parse tree produced by {@link ArtifactGrammar#slay}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitSlay(ArtifactGrammar.SlayContext ctx);

    /**
     * Visit a parse tree produced by {@link ArtifactGrammar#curse}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitCurse(ArtifactGrammar.CurseContext ctx);

    /**
     * Visit a parse tree produced by {@link ArtifactGrammar#desc}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitDesc(ArtifactGrammar.DescContext ctx);

    /**
     * Visit a parse tree produced by {@link ArtifactGrammar#artifact}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitArtifact(ArtifactGrammar.ArtifactContext ctx);

    /**
     * Visit a parse tree produced by {@link ArtifactGrammar#file}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFile(ArtifactGrammar.FileContext ctx);
}