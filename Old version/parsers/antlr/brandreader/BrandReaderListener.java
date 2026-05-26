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

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link BrandReaderParser}.
 */
public interface BrandReaderListener extends ParseTreeListener {
    /**
     * Enter a parse tree produced by {@link BrandReaderParser#code}.
     *
     * @param ctx the parse tree
     */
    void enterCode(BrandReaderParser.CodeContext ctx);

    /**
     * Exit a parse tree produced by {@link BrandReaderParser#code}.
     *
     * @param ctx the parse tree
     */
    void exitCode(BrandReaderParser.CodeContext ctx);

    /**
     * Enter a parse tree produced by {@link BrandReaderParser#name}.
     *
     * @param ctx the parse tree
     */
    void enterName(BrandReaderParser.NameContext ctx);

    /**
     * Exit a parse tree produced by {@link BrandReaderParser#name}.
     *
     * @param ctx the parse tree
     */
    void exitName(BrandReaderParser.NameContext ctx);

    /**
     * Enter a parse tree produced by {@link BrandReaderParser#resistFlag}.
     *
     * @param ctx the parse tree
     */
    void enterResistFlag(BrandReaderParser.ResistFlagContext ctx);

    /**
     * Exit a parse tree produced by {@link BrandReaderParser#resistFlag}.
     *
     * @param ctx the parse tree
     */
    void exitResistFlag(BrandReaderParser.ResistFlagContext ctx);

    /**
     * Enter a parse tree produced by {@link BrandReaderParser#vulnFlag}.
     *
     * @param ctx the parse tree
     */
    void enterVulnFlag(BrandReaderParser.VulnFlagContext ctx);

    /**
     * Exit a parse tree produced by {@link BrandReaderParser#vulnFlag}.
     *
     * @param ctx the parse tree
     */
    void exitVulnFlag(BrandReaderParser.VulnFlagContext ctx);

    /**
     * Enter a parse tree produced by {@link BrandReaderParser#multiplier}.
     *
     * @param ctx the parse tree
     */
    void enterMultiplier(BrandReaderParser.MultiplierContext ctx);

    /**
     * Exit a parse tree produced by {@link BrandReaderParser#multiplier}.
     *
     * @param ctx the parse tree
     */
    void exitMultiplier(BrandReaderParser.MultiplierContext ctx);

    /**
     * Enter a parse tree produced by {@link BrandReaderParser#oMultiplier}.
     *
     * @param ctx the parse tree
     */
    void enterOMultiplier(BrandReaderParser.OMultiplierContext ctx);

    /**
     * Exit a parse tree produced by {@link BrandReaderParser#oMultiplier}.
     *
     * @param ctx the parse tree
     */
    void exitOMultiplier(BrandReaderParser.OMultiplierContext ctx);

    /**
     * Enter a parse tree produced by {@link BrandReaderParser#power}.
     *
     * @param ctx the parse tree
     */
    void enterPower(BrandReaderParser.PowerContext ctx);

    /**
     * Exit a parse tree produced by {@link BrandReaderParser#power}.
     *
     * @param ctx the parse tree
     */
    void exitPower(BrandReaderParser.PowerContext ctx);

    /**
     * Enter a parse tree produced by {@link BrandReaderParser#verb}.
     *
     * @param ctx the parse tree
     */
    void enterVerb(BrandReaderParser.VerbContext ctx);

    /**
     * Exit a parse tree produced by {@link BrandReaderParser#verb}.
     *
     * @param ctx the parse tree
     */
    void exitVerb(BrandReaderParser.VerbContext ctx);

    /**
     * Enter a parse tree produced by {@link BrandReaderParser#brand}.
     *
     * @param ctx the parse tree
     */
    void enterBrand(BrandReaderParser.BrandContext ctx);

    /**
     * Exit a parse tree produced by {@link BrandReaderParser#brand}.
     *
     * @param ctx the parse tree
     */
    void exitBrand(BrandReaderParser.BrandContext ctx);

    /**
     * Enter a parse tree produced by {@link BrandReaderParser#brands}.
     *
     * @param ctx the parse tree
     */
    void enterBrands(BrandReaderParser.BrandsContext ctx);

    /**
     * Exit a parse tree produced by {@link BrandReaderParser#brands}.
     *
     * @param ctx the parse tree
     */
    void exitBrands(BrandReaderParser.BrandsContext ctx);
}