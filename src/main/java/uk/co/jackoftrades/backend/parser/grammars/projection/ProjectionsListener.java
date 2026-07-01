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

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link Projections}.
 */
public interface ProjectionsListener extends ParseTreeListener {
    /**
     * Enter a parse tree produced by {@link Projections#recordCount}.
     *
     * @param ctx the parse tree
     */
    void enterRecordCount(Projections.RecordCountContext ctx);

    /**
     * Exit a parse tree produced by {@link Projections#recordCount}.
     *
     * @param ctx the parse tree
     */
    void exitRecordCount(Projections.RecordCountContext ctx);

    /**
     * Enter a parse tree produced by {@link Projections#code}.
     *
     * @param ctx the parse tree
     */
    void enterCode(Projections.CodeContext ctx);

    /**
     * Exit a parse tree produced by {@link Projections#code}.
     *
     * @param ctx the parse tree
     */
    void exitCode(Projections.CodeContext ctx);

    /**
     * Enter a parse tree produced by {@link Projections#name}.
     *
     * @param ctx the parse tree
     */
    void enterName(Projections.NameContext ctx);

    /**
     * Exit a parse tree produced by {@link Projections#name}.
     *
     * @param ctx the parse tree
     */
    void exitName(Projections.NameContext ctx);

    /**
     * Enter a parse tree produced by {@link Projections#type}.
     *
     * @param ctx the parse tree
     */
    void enterType(Projections.TypeContext ctx);

    /**
     * Exit a parse tree produced by {@link Projections#type}.
     *
     * @param ctx the parse tree
     */
    void exitType(Projections.TypeContext ctx);

    /**
     * Enter a parse tree produced by {@link Projections#desc}.
     *
     * @param ctx the parse tree
     */
    void enterDesc(Projections.DescContext ctx);

    /**
     * Exit a parse tree produced by {@link Projections#desc}.
     *
     * @param ctx the parse tree
     */
    void exitDesc(Projections.DescContext ctx);

    /**
     * Enter a parse tree produced by {@link Projections#playerDesc}.
     *
     * @param ctx the parse tree
     */
    void enterPlayerDesc(Projections.PlayerDescContext ctx);

    /**
     * Exit a parse tree produced by {@link Projections#playerDesc}.
     *
     * @param ctx the parse tree
     */
    void exitPlayerDesc(Projections.PlayerDescContext ctx);

    /**
     * Enter a parse tree produced by {@link Projections#blindDesc}.
     *
     * @param ctx the parse tree
     */
    void enterBlindDesc(Projections.BlindDescContext ctx);

    /**
     * Exit a parse tree produced by {@link Projections#blindDesc}.
     *
     * @param ctx the parse tree
     */
    void exitBlindDesc(Projections.BlindDescContext ctx);

    /**
     * Enter a parse tree produced by {@link Projections#lashDesc}.
     *
     * @param ctx the parse tree
     */
    void enterLashDesc(Projections.LashDescContext ctx);

    /**
     * Exit a parse tree produced by {@link Projections#lashDesc}.
     *
     * @param ctx the parse tree
     */
    void exitLashDesc(Projections.LashDescContext ctx);

    /**
     * Enter a parse tree produced by {@link Projections#numerator}.
     *
     * @param ctx the parse tree
     */
    void enterNumerator(Projections.NumeratorContext ctx);

    /**
     * Exit a parse tree produced by {@link Projections#numerator}.
     *
     * @param ctx the parse tree
     */
    void exitNumerator(Projections.NumeratorContext ctx);

    /**
     * Enter a parse tree produced by {@link Projections#denominator}.
     *
     * @param ctx the parse tree
     */
    void enterDenominator(Projections.DenominatorContext ctx);

    /**
     * Exit a parse tree produced by {@link Projections#denominator}.
     *
     * @param ctx the parse tree
     */
    void exitDenominator(Projections.DenominatorContext ctx);

    /**
     * Enter a parse tree produced by {@link Projections#divisor}.
     *
     * @param ctx the parse tree
     */
    void enterDivisor(Projections.DivisorContext ctx);

    /**
     * Exit a parse tree produced by {@link Projections#divisor}.
     *
     * @param ctx the parse tree
     */
    void exitDivisor(Projections.DivisorContext ctx);

    /**
     * Enter a parse tree produced by {@link Projections#damageCap}.
     *
     * @param ctx the parse tree
     */
    void enterDamageCap(Projections.DamageCapContext ctx);

    /**
     * Exit a parse tree produced by {@link Projections#damageCap}.
     *
     * @param ctx the parse tree
     */
    void exitDamageCap(Projections.DamageCapContext ctx);

    /**
     * Enter a parse tree produced by {@link Projections#msgt}.
     *
     * @param ctx the parse tree
     */
    void enterMsgt(Projections.MsgtContext ctx);

    /**
     * Exit a parse tree produced by {@link Projections#msgt}.
     *
     * @param ctx the parse tree
     */
    void exitMsgt(Projections.MsgtContext ctx);

    /**
     * Enter a parse tree produced by {@link Projections#obvious}.
     *
     * @param ctx the parse tree
     */
    void enterObvious(Projections.ObviousContext ctx);

    /**
     * Exit a parse tree produced by {@link Projections#obvious}.
     *
     * @param ctx the parse tree
     */
    void exitObvious(Projections.ObviousContext ctx);

    /**
     * Enter a parse tree produced by {@link Projections#wake}.
     *
     * @param ctx the parse tree
     */
    void enterWake(Projections.WakeContext ctx);

    /**
     * Exit a parse tree produced by {@link Projections#wake}.
     *
     * @param ctx the parse tree
     */
    void exitWake(Projections.WakeContext ctx);

    /**
     * Enter a parse tree produced by {@link Projections#colour}.
     *
     * @param ctx the parse tree
     */
    void enterColour(Projections.ColourContext ctx);

    /**
     * Exit a parse tree produced by {@link Projections#colour}.
     *
     * @param ctx the parse tree
     */
    void exitColour(Projections.ColourContext ctx);

    /**
     * Enter a parse tree produced by {@link Projections#projection}.
     *
     * @param ctx the parse tree
     */
    void enterProjection(Projections.ProjectionContext ctx);

    /**
     * Exit a parse tree produced by {@link Projections#projection}.
     *
     * @param ctx the parse tree
     */
    void exitProjection(Projections.ProjectionContext ctx);

    /**
     * Enter a parse tree produced by {@link Projections#file}.
     *
     * @param ctx the parse tree
     */
    void enterFile(Projections.FileContext ctx);

    /**
     * Exit a parse tree produced by {@link Projections#file}.
     *
     * @param ctx the parse tree
     */
    void exitFile(Projections.FileContext ctx);
}