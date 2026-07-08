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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/parser/grammars/BrandGrammar.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.grammars.brand;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link BrandGrammar}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 *            operations with no return type.
 */
public interface BrandGrammarVisitor<T> extends ParseTreeVisitor<T> {
    /**
     * Visit a parse tree produced by {@link BrandGrammar#recordCount}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitRecordCount(BrandGrammar.RecordCountContext ctx);

    /**
     * Visit a parse tree produced by {@link BrandGrammar#code}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitCode(BrandGrammar.CodeContext ctx);

    /**
     * Visit a parse tree produced by {@link BrandGrammar#name}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitName(BrandGrammar.NameContext ctx);

    /**
     * Visit a parse tree produced by {@link BrandGrammar#multiplier}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitMultiplier(BrandGrammar.MultiplierContext ctx);

    /**
     * Visit a parse tree produced by {@link BrandGrammar#oMultiplier}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitOMultiplier(BrandGrammar.OMultiplierContext ctx);

    /**
     * Visit a parse tree produced by {@link BrandGrammar#power}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitPower(BrandGrammar.PowerContext ctx);

    /**
     * Visit a parse tree produced by {@link BrandGrammar#verb}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitVerb(BrandGrammar.VerbContext ctx);

    /**
     * Visit a parse tree produced by {@link BrandGrammar#resistFlag}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitResistFlag(BrandGrammar.ResistFlagContext ctx);

    /**
     * Visit a parse tree produced by {@link BrandGrammar#vulnFlag}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitVulnFlag(BrandGrammar.VulnFlagContext ctx);

    /**
     * Visit a parse tree produced by {@link BrandGrammar#brand}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitBrand(BrandGrammar.BrandContext ctx);

    /**
     * Visit a parse tree produced by {@link BrandGrammar#file}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFile(BrandGrammar.FileContext ctx);
}