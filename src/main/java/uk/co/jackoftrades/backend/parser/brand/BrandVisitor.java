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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/parser/grammars/Brand.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.brand;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link BrandParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 *            operations with no return type.
 */
public interface BrandVisitor<T> extends ParseTreeVisitor<T> {
    /**
     * Visit a parse tree produced by {@link BrandParser#code}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitCode(BrandParser.CodeContext ctx);

    /**
     * Visit a parse tree produced by {@link BrandParser#name}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitName(BrandParser.NameContext ctx);

    /**
     * Visit a parse tree produced by {@link BrandParser#multiplier}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitMultiplier(BrandParser.MultiplierContext ctx);

    /**
     * Visit a parse tree produced by {@link BrandParser#o_multiplier}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitO_multiplier(BrandParser.O_multiplierContext ctx);

    /**
     * Visit a parse tree produced by {@link BrandParser#power}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitPower(BrandParser.PowerContext ctx);

    /**
     * Visit a parse tree produced by {@link BrandParser#verb}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitVerb(BrandParser.VerbContext ctx);

    /**
     * Visit a parse tree produced by {@link BrandParser#resist_flag}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitResist_flag(BrandParser.Resist_flagContext ctx);

    /**
     * Visit a parse tree produced by {@link BrandParser#vuln_flag}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitVuln_flag(BrandParser.Vuln_flagContext ctx);

    /**
     * Visit a parse tree produced by {@link BrandParser#brand}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitBrand(BrandParser.BrandContext ctx);

    /**
     * Visit a parse tree produced by {@link BrandParser#file}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFile(BrandParser.FileContext ctx);
}