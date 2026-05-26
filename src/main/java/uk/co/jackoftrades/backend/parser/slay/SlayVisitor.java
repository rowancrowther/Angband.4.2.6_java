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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/parser/grammars/Slay.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.slay;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link SlayParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 *            operations with no return type.
 */
public interface SlayVisitor<T> extends ParseTreeVisitor<T> {
    /**
     * Visit a parse tree produced by {@link SlayParser#code}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitCode(SlayParser.CodeContext ctx);

    /**
     * Visit a parse tree produced by {@link SlayParser#name}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitName(SlayParser.NameContext ctx);

    /**
     * Visit a parse tree produced by {@link SlayParser#race_flag}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitRace_flag(SlayParser.Race_flagContext ctx);

    /**
     * Visit a parse tree produced by {@link SlayParser#base}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitBase(SlayParser.BaseContext ctx);

    /**
     * Visit a parse tree produced by {@link SlayParser#multiplier}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitMultiplier(SlayParser.MultiplierContext ctx);

    /**
     * Visit a parse tree produced by {@link SlayParser#o_multiplier}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitO_multiplier(SlayParser.O_multiplierContext ctx);

    /**
     * Visit a parse tree produced by {@link SlayParser#power}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitPower(SlayParser.PowerContext ctx);

    /**
     * Visit a parse tree produced by {@link SlayParser#melee_verb}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitMelee_verb(SlayParser.Melee_verbContext ctx);

    /**
     * Visit a parse tree produced by {@link SlayParser#ranged_verb}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitRanged_verb(SlayParser.Ranged_verbContext ctx);

    /**
     * Visit a parse tree produced by {@link SlayParser#slay}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitSlay(SlayParser.SlayContext ctx);

    /**
     * Visit a parse tree produced by {@link SlayParser#file}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFile(SlayParser.FileContext ctx);
}