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
// Generated from Projections.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.grammars.projection;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link Projections}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 *            operations with no return type.
 */
public interface ProjectionsVisitor<T> extends ParseTreeVisitor<T> {
    /**
     * Visit a parse tree produced by {@link Projections#recordCount}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitRecordCount(Projections.RecordCountContext ctx);

    /**
     * Visit a parse tree produced by {@link Projections#code}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitCode(Projections.CodeContext ctx);

    /**
     * Visit a parse tree produced by {@link Projections#name}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitName(Projections.NameContext ctx);

    /**
     * Visit a parse tree produced by {@link Projections#type}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitType(Projections.TypeContext ctx);

    /**
     * Visit a parse tree produced by {@link Projections#desc}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitDesc(Projections.DescContext ctx);

    /**
     * Visit a parse tree produced by {@link Projections#playerDesc}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitPlayerDesc(Projections.PlayerDescContext ctx);

    /**
     * Visit a parse tree produced by {@link Projections#blindDesc}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitBlindDesc(Projections.BlindDescContext ctx);

    /**
     * Visit a parse tree produced by {@link Projections#lashDesc}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitLashDesc(Projections.LashDescContext ctx);

    /**
     * Visit a parse tree produced by {@link Projections#numerator}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitNumerator(Projections.NumeratorContext ctx);

    /**
     * Visit a parse tree produced by {@link Projections#denominator}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitDenominator(Projections.DenominatorContext ctx);

    /**
     * Visit a parse tree produced by {@link Projections#divisor}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitDivisor(Projections.DivisorContext ctx);

    /**
     * Visit a parse tree produced by {@link Projections#damageCap}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitDamageCap(Projections.DamageCapContext ctx);

    /**
     * Visit a parse tree produced by {@link Projections#msgt}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitMsgt(Projections.MsgtContext ctx);

    /**
     * Visit a parse tree produced by {@link Projections#obvious}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitObvious(Projections.ObviousContext ctx);

    /**
     * Visit a parse tree produced by {@link Projections#wake}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitWake(Projections.WakeContext ctx);

    /**
     * Visit a parse tree produced by {@link Projections#colour}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitColour(Projections.ColourContext ctx);

    /**
     * Visit a parse tree produced by {@link Projections#projection}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitProjection(Projections.ProjectionContext ctx);

    /**
     * Visit a parse tree produced by {@link Projections#file}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFile(Projections.FileContext ctx);
}