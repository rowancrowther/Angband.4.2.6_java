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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/io/parsers/antlr/grammars/BrandReader.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.io.parsers.antlr.brandreader;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link BrandReaderParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 *            operations with no return type.
 */
public interface BrandReaderVisitor<T> extends ParseTreeVisitor<T> {
    /**
     * Visit a parse tree produced by {@link BrandReaderParser#code}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitCode(BrandReaderParser.CodeContext ctx);

    /**
     * Visit a parse tree produced by {@link BrandReaderParser#name}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitName(BrandReaderParser.NameContext ctx);

    /**
     * Visit a parse tree produced by {@link BrandReaderParser#resistFlag}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitResistFlag(BrandReaderParser.ResistFlagContext ctx);

    /**
     * Visit a parse tree produced by {@link BrandReaderParser#vulnFlag}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitVulnFlag(BrandReaderParser.VulnFlagContext ctx);

    /**
     * Visit a parse tree produced by {@link BrandReaderParser#multiplier}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitMultiplier(BrandReaderParser.MultiplierContext ctx);

    /**
     * Visit a parse tree produced by {@link BrandReaderParser#oMultiplier}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitOMultiplier(BrandReaderParser.OMultiplierContext ctx);

    /**
     * Visit a parse tree produced by {@link BrandReaderParser#power}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitPower(BrandReaderParser.PowerContext ctx);

    /**
     * Visit a parse tree produced by {@link BrandReaderParser#verb}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitVerb(BrandReaderParser.VerbContext ctx);

    /**
     * Visit a parse tree produced by {@link BrandReaderParser#brand}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitBrand(BrandReaderParser.BrandContext ctx);

    /**
     * Visit a parse tree produced by {@link BrandReaderParser#brands}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitBrands(BrandReaderParser.BrandsContext ctx);
}