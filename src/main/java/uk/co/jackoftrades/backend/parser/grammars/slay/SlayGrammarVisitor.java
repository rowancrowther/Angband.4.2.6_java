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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/parser/grammars/SlayGrammar.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.grammars.slay;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link SlayGrammar}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 *            operations with no return type.
 */
public interface SlayGrammarVisitor<T> extends ParseTreeVisitor<T> {
    /**
     * Visit a parse tree produced by {@link SlayGrammar#recordCount}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitRecordCount(SlayGrammar.RecordCountContext ctx);

    /**
     * Visit a parse tree produced by {@link SlayGrammar#code}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitCode(SlayGrammar.CodeContext ctx);

    /**
     * Visit a parse tree produced by {@link SlayGrammar#name}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitName(SlayGrammar.NameContext ctx);

    /**
     * Visit a parse tree produced by {@link SlayGrammar#raceFlag}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitRaceFlag(SlayGrammar.RaceFlagContext ctx);

    /**
     * Visit a parse tree produced by {@link SlayGrammar#base}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitBase(SlayGrammar.BaseContext ctx);

    /**
     * Visit a parse tree produced by {@link SlayGrammar#multiplier}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitMultiplier(SlayGrammar.MultiplierContext ctx);

    /**
     * Visit a parse tree produced by {@link SlayGrammar#oMultiplier}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitOMultiplier(SlayGrammar.OMultiplierContext ctx);

    /**
     * Visit a parse tree produced by {@link SlayGrammar#power}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitPower(SlayGrammar.PowerContext ctx);

    /**
     * Visit a parse tree produced by {@link SlayGrammar#meleeVerb}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitMeleeVerb(SlayGrammar.MeleeVerbContext ctx);

    /**
     * Visit a parse tree produced by {@link SlayGrammar#rangeVerb}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitRangeVerb(SlayGrammar.RangeVerbContext ctx);

    /**
     * Visit a parse tree produced by {@link SlayGrammar#slay}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitSlay(SlayGrammar.SlayContext ctx);

    /**
     * Visit a parse tree produced by {@link SlayGrammar#file}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFile(SlayGrammar.FileContext ctx);
}