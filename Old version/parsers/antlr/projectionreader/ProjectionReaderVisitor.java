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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/background/io/parsers/antlr/grammars/ProjectionReader.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.io.parsers.antlr.projectionreader;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link ProjectionReaderParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 *            operations with no return type.
 */
public interface ProjectionReaderVisitor<T> extends ParseTreeVisitor<T> {
    /**
     * Visit a parse tree produced by {@link ProjectionReaderParser#code}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitCode(ProjectionReaderParser.CodeContext ctx);

    /**
     * Visit a parse tree produced by {@link ProjectionReaderParser#name}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitName(ProjectionReaderParser.NameContext ctx);

    /**
     * Visit a parse tree produced by {@link ProjectionReaderParser#type}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitType(ProjectionReaderParser.TypeContext ctx);

    /**
     * Visit a parse tree produced by {@link ProjectionReaderParser#desc}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitDesc(ProjectionReaderParser.DescContext ctx);

    /**
     * Visit a parse tree produced by {@link ProjectionReaderParser#playerDesc}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitPlayerDesc(ProjectionReaderParser.PlayerDescContext ctx);

    /**
     * Visit a parse tree produced by {@link ProjectionReaderParser#blindDesc}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitBlindDesc(ProjectionReaderParser.BlindDescContext ctx);

    /**
     * Visit a parse tree produced by {@link ProjectionReaderParser#lashDesc}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitLashDesc(ProjectionReaderParser.LashDescContext ctx);

    /**
     * Visit a parse tree produced by {@link ProjectionReaderParser#numerator}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitNumerator(ProjectionReaderParser.NumeratorContext ctx);

    /**
     * Visit a parse tree produced by {@link ProjectionReaderParser#dice}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitDice(ProjectionReaderParser.DiceContext ctx);

    /**
     * Visit a parse tree produced by {@link ProjectionReaderParser#denominator}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitDenominator(ProjectionReaderParser.DenominatorContext ctx);

    /**
     * Visit a parse tree produced by {@link ProjectionReaderParser#divisor}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitDivisor(ProjectionReaderParser.DivisorContext ctx);

    /**
     * Visit a parse tree produced by {@link ProjectionReaderParser#damageCap}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitDamageCap(ProjectionReaderParser.DamageCapContext ctx);

    /**
     * Visit a parse tree produced by {@link ProjectionReaderParser#msgt}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitMsgt(ProjectionReaderParser.MsgtContext ctx);

    /**
     * Visit a parse tree produced by {@link ProjectionReaderParser#obvious}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitObvious(ProjectionReaderParser.ObviousContext ctx);

    /**
     * Visit a parse tree produced by {@link ProjectionReaderParser#wake}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitWake(ProjectionReaderParser.WakeContext ctx);

    /**
     * Visit a parse tree produced by {@link ProjectionReaderParser#colour}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitColour(ProjectionReaderParser.ColourContext ctx);

    /**
     * Visit a parse tree produced by {@link ProjectionReaderParser#projection}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitProjection(ProjectionReaderParser.ProjectionContext ctx);

    /**
     * Visit a parse tree produced by {@link ProjectionReaderParser#file}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFile(ProjectionReaderParser.FileContext ctx);
}