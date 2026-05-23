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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/parser/grammars/Projection.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.projection;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link ProjectionParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 *            operations with no return type.
 */
public interface ProjectionVisitor<T> extends ParseTreeVisitor<T> {
    /**
     * Visit a parse tree produced by {@link ProjectionParser#code}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitCode(ProjectionParser.CodeContext ctx);

    /**
     * Visit a parse tree produced by {@link ProjectionParser#name}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitName(ProjectionParser.NameContext ctx);

    /**
     * Visit a parse tree produced by {@link ProjectionParser#type}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitType(ProjectionParser.TypeContext ctx);

    /**
     * Visit a parse tree produced by {@link ProjectionParser#desc}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitDesc(ProjectionParser.DescContext ctx);

    /**
     * Visit a parse tree produced by {@link ProjectionParser#playerDesc}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitPlayerDesc(ProjectionParser.PlayerDescContext ctx);

    /**
     * Visit a parse tree produced by {@link ProjectionParser#blindDesc}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitBlindDesc(ProjectionParser.BlindDescContext ctx);

    /**
     * Visit a parse tree produced by {@link ProjectionParser#lashDesc}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitLashDesc(ProjectionParser.LashDescContext ctx);

    /**
     * Visit a parse tree produced by {@link ProjectionParser#numerator}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitNumerator(ProjectionParser.NumeratorContext ctx);

    /**
     * Visit a parse tree produced by {@link ProjectionParser#dice}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitDice(ProjectionParser.DiceContext ctx);

    /**
     * Visit a parse tree produced by {@link ProjectionParser#denominator}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitDenominator(ProjectionParser.DenominatorContext ctx);

    /**
     * Visit a parse tree produced by {@link ProjectionParser#divisor}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitDivisor(ProjectionParser.DivisorContext ctx);

    /**
     * Visit a parse tree produced by {@link ProjectionParser#damageCap}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitDamageCap(ProjectionParser.DamageCapContext ctx);

    /**
     * Visit a parse tree produced by {@link ProjectionParser#msgt}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitMsgt(ProjectionParser.MsgtContext ctx);

    /**
     * Visit a parse tree produced by {@link ProjectionParser#obvious}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitObvious(ProjectionParser.ObviousContext ctx);

    /**
     * Visit a parse tree produced by {@link ProjectionParser#wake}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitWake(ProjectionParser.WakeContext ctx);

    /**
     * Visit a parse tree produced by {@link ProjectionParser#colour}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitColour(ProjectionParser.ColourContext ctx);

    /**
     * Visit a parse tree produced by {@link ProjectionParser#projection}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitProjection(ProjectionParser.ProjectionContext ctx);

    /**
     * Visit a parse tree produced by {@link ProjectionParser#file}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFile(ProjectionParser.FileContext ctx);
}