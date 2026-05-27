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

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link BrandParser}.
 */
public interface BrandListener extends ParseTreeListener {
    /**
     * Enter a parse tree produced by {@link BrandParser#code}.
     *
     * @param ctx the parse tree
     */
    void enterCode(BrandParser.CodeContext ctx);

    /**
     * Exit a parse tree produced by {@link BrandParser#code}.
     *
     * @param ctx the parse tree
     */
    void exitCode(BrandParser.CodeContext ctx);

    /**
     * Enter a parse tree produced by {@link BrandParser#name}.
     *
     * @param ctx the parse tree
     */
    void enterName(BrandParser.NameContext ctx);

    /**
     * Exit a parse tree produced by {@link BrandParser#name}.
     *
     * @param ctx the parse tree
     */
    void exitName(BrandParser.NameContext ctx);

    /**
     * Enter a parse tree produced by {@link BrandParser#multiplier}.
     *
     * @param ctx the parse tree
     */
    void enterMultiplier(BrandParser.MultiplierContext ctx);

    /**
     * Exit a parse tree produced by {@link BrandParser#multiplier}.
     *
     * @param ctx the parse tree
     */
    void exitMultiplier(BrandParser.MultiplierContext ctx);

    /**
     * Enter a parse tree produced by {@link BrandParser#o_multiplier}.
     *
     * @param ctx the parse tree
     */
    void enterO_multiplier(BrandParser.O_multiplierContext ctx);

    /**
     * Exit a parse tree produced by {@link BrandParser#o_multiplier}.
     *
     * @param ctx the parse tree
     */
    void exitO_multiplier(BrandParser.O_multiplierContext ctx);

    /**
     * Enter a parse tree produced by {@link BrandParser#power}.
     *
     * @param ctx the parse tree
     */
    void enterPower(BrandParser.PowerContext ctx);

    /**
     * Exit a parse tree produced by {@link BrandParser#power}.
     *
     * @param ctx the parse tree
     */
    void exitPower(BrandParser.PowerContext ctx);

    /**
     * Enter a parse tree produced by {@link BrandParser#verb}.
     *
     * @param ctx the parse tree
     */
    void enterVerb(BrandParser.VerbContext ctx);

    /**
     * Exit a parse tree produced by {@link BrandParser#verb}.
     *
     * @param ctx the parse tree
     */
    void exitVerb(BrandParser.VerbContext ctx);

    /**
     * Enter a parse tree produced by {@link BrandParser#resist_flag}.
     *
     * @param ctx the parse tree
     */
    void enterResist_flag(BrandParser.Resist_flagContext ctx);

    /**
     * Exit a parse tree produced by {@link BrandParser#resist_flag}.
     *
     * @param ctx the parse tree
     */
    void exitResist_flag(BrandParser.Resist_flagContext ctx);

    /**
     * Enter a parse tree produced by {@link BrandParser#vuln_flag}.
     *
     * @param ctx the parse tree
     */
    void enterVuln_flag(BrandParser.Vuln_flagContext ctx);

    /**
     * Exit a parse tree produced by {@link BrandParser#vuln_flag}.
     *
     * @param ctx the parse tree
     */
    void exitVuln_flag(BrandParser.Vuln_flagContext ctx);

    /**
     * Enter a parse tree produced by {@link BrandParser#brand}.
     *
     * @param ctx the parse tree
     */
    void enterBrand(BrandParser.BrandContext ctx);

    /**
     * Exit a parse tree produced by {@link BrandParser#brand}.
     *
     * @param ctx the parse tree
     */
    void exitBrand(BrandParser.BrandContext ctx);

    /**
     * Enter a parse tree produced by {@link BrandParser#file}.
     *
     * @param ctx the parse tree
     */
    void enterFile(BrandParser.FileContext ctx);

    /**
     * Exit a parse tree produced by {@link BrandParser#file}.
     *
     * @param ctx the parse tree
     */
    void exitFile(BrandParser.FileContext ctx);
}