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

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link ProjectionParser}.
 */
public interface ProjectionListener extends ParseTreeListener {
    /**
     * Enter a parse tree produced by {@link ProjectionParser#code}.
     *
     * @param ctx the parse tree
     */
    void enterCode(ProjectionParser.CodeContext ctx);

    /**
     * Exit a parse tree produced by {@link ProjectionParser#code}.
     *
     * @param ctx the parse tree
     */
    void exitCode(ProjectionParser.CodeContext ctx);

    /**
     * Enter a parse tree produced by {@link ProjectionParser#name}.
     *
     * @param ctx the parse tree
     */
    void enterName(ProjectionParser.NameContext ctx);

    /**
     * Exit a parse tree produced by {@link ProjectionParser#name}.
     *
     * @param ctx the parse tree
     */
    void exitName(ProjectionParser.NameContext ctx);

    /**
     * Enter a parse tree produced by {@link ProjectionParser#type}.
     *
     * @param ctx the parse tree
     */
    void enterType(ProjectionParser.TypeContext ctx);

    /**
     * Exit a parse tree produced by {@link ProjectionParser#type}.
     *
     * @param ctx the parse tree
     */
    void exitType(ProjectionParser.TypeContext ctx);

    /**
     * Enter a parse tree produced by {@link ProjectionParser#desc}.
     *
     * @param ctx the parse tree
     */
    void enterDesc(ProjectionParser.DescContext ctx);

    /**
     * Exit a parse tree produced by {@link ProjectionParser#desc}.
     *
     * @param ctx the parse tree
     */
    void exitDesc(ProjectionParser.DescContext ctx);

    /**
     * Enter a parse tree produced by {@link ProjectionParser#playerDesc}.
     *
     * @param ctx the parse tree
     */
    void enterPlayerDesc(ProjectionParser.PlayerDescContext ctx);

    /**
     * Exit a parse tree produced by {@link ProjectionParser#playerDesc}.
     *
     * @param ctx the parse tree
     */
    void exitPlayerDesc(ProjectionParser.PlayerDescContext ctx);

    /**
     * Enter a parse tree produced by {@link ProjectionParser#blindDesc}.
     *
     * @param ctx the parse tree
     */
    void enterBlindDesc(ProjectionParser.BlindDescContext ctx);

    /**
     * Exit a parse tree produced by {@link ProjectionParser#blindDesc}.
     *
     * @param ctx the parse tree
     */
    void exitBlindDesc(ProjectionParser.BlindDescContext ctx);

    /**
     * Enter a parse tree produced by {@link ProjectionParser#lashDesc}.
     *
     * @param ctx the parse tree
     */
    void enterLashDesc(ProjectionParser.LashDescContext ctx);

    /**
     * Exit a parse tree produced by {@link ProjectionParser#lashDesc}.
     *
     * @param ctx the parse tree
     */
    void exitLashDesc(ProjectionParser.LashDescContext ctx);

    /**
     * Enter a parse tree produced by {@link ProjectionParser#numerator}.
     *
     * @param ctx the parse tree
     */
    void enterNumerator(ProjectionParser.NumeratorContext ctx);

    /**
     * Exit a parse tree produced by {@link ProjectionParser#numerator}.
     *
     * @param ctx the parse tree
     */
    void exitNumerator(ProjectionParser.NumeratorContext ctx);

    /**
     * Enter a parse tree produced by {@link ProjectionParser#dice}.
     *
     * @param ctx the parse tree
     */
    void enterDice(ProjectionParser.DiceContext ctx);

    /**
     * Exit a parse tree produced by {@link ProjectionParser#dice}.
     *
     * @param ctx the parse tree
     */
    void exitDice(ProjectionParser.DiceContext ctx);

    /**
     * Enter a parse tree produced by {@link ProjectionParser#denominator}.
     *
     * @param ctx the parse tree
     */
    void enterDenominator(ProjectionParser.DenominatorContext ctx);

    /**
     * Exit a parse tree produced by {@link ProjectionParser#denominator}.
     *
     * @param ctx the parse tree
     */
    void exitDenominator(ProjectionParser.DenominatorContext ctx);

    /**
     * Enter a parse tree produced by {@link ProjectionParser#divisor}.
     *
     * @param ctx the parse tree
     */
    void enterDivisor(ProjectionParser.DivisorContext ctx);

    /**
     * Exit a parse tree produced by {@link ProjectionParser#divisor}.
     *
     * @param ctx the parse tree
     */
    void exitDivisor(ProjectionParser.DivisorContext ctx);

    /**
     * Enter a parse tree produced by {@link ProjectionParser#damageCap}.
     *
     * @param ctx the parse tree
     */
    void enterDamageCap(ProjectionParser.DamageCapContext ctx);

    /**
     * Exit a parse tree produced by {@link ProjectionParser#damageCap}.
     *
     * @param ctx the parse tree
     */
    void exitDamageCap(ProjectionParser.DamageCapContext ctx);

    /**
     * Enter a parse tree produced by {@link ProjectionParser#msgt}.
     *
     * @param ctx the parse tree
     */
    void enterMsgt(ProjectionParser.MsgtContext ctx);

    /**
     * Exit a parse tree produced by {@link ProjectionParser#msgt}.
     *
     * @param ctx the parse tree
     */
    void exitMsgt(ProjectionParser.MsgtContext ctx);

    /**
     * Enter a parse tree produced by {@link ProjectionParser#obvious}.
     *
     * @param ctx the parse tree
     */
    void enterObvious(ProjectionParser.ObviousContext ctx);

    /**
     * Exit a parse tree produced by {@link ProjectionParser#obvious}.
     *
     * @param ctx the parse tree
     */
    void exitObvious(ProjectionParser.ObviousContext ctx);

    /**
     * Enter a parse tree produced by {@link ProjectionParser#wake}.
     *
     * @param ctx the parse tree
     */
    void enterWake(ProjectionParser.WakeContext ctx);

    /**
     * Exit a parse tree produced by {@link ProjectionParser#wake}.
     *
     * @param ctx the parse tree
     */
    void exitWake(ProjectionParser.WakeContext ctx);

    /**
     * Enter a parse tree produced by {@link ProjectionParser#colour}.
     *
     * @param ctx the parse tree
     */
    void enterColour(ProjectionParser.ColourContext ctx);

    /**
     * Exit a parse tree produced by {@link ProjectionParser#colour}.
     *
     * @param ctx the parse tree
     */
    void exitColour(ProjectionParser.ColourContext ctx);

    /**
     * Enter a parse tree produced by {@link ProjectionParser#projection}.
     *
     * @param ctx the parse tree
     */
    void enterProjection(ProjectionParser.ProjectionContext ctx);

    /**
     * Exit a parse tree produced by {@link ProjectionParser#projection}.
     *
     * @param ctx the parse tree
     */
    void exitProjection(ProjectionParser.ProjectionContext ctx);

    /**
     * Enter a parse tree produced by {@link ProjectionParser#file}.
     *
     * @param ctx the parse tree
     */
    void enterFile(ProjectionParser.FileContext ctx);

    /**
     * Exit a parse tree produced by {@link ProjectionParser#file}.
     *
     * @param ctx the parse tree
     */
    void exitFile(ProjectionParser.FileContext ctx);
}