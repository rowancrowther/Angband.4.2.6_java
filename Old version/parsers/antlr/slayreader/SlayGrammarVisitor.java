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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/io/parsers/antlr/grammars/SlayGrammar.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.io.parsers.antlr.slayreader;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link SlayGrammarParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 *            operations with no return type.
 */
public interface SlayGrammarVisitor<T> extends ParseTreeVisitor<T> {
    /**
     * Visit a parse tree produced by {@link SlayGrammarParser#code}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitCode(SlayGrammarParser.CodeContext ctx);

    /**
     * Visit a parse tree produced by {@link SlayGrammarParser#name}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitName(SlayGrammarParser.NameContext ctx);

    /**
     * Visit a parse tree produced by {@link SlayGrammarParser#raceFlag}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitRaceFlag(SlayGrammarParser.RaceFlagContext ctx);

    /**
     * Visit a parse tree produced by {@link SlayGrammarParser#multiplier}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitMultiplier(SlayGrammarParser.MultiplierContext ctx);

    /**
     * Visit a parse tree produced by {@link SlayGrammarParser#oMultiplier}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitOMultiplier(SlayGrammarParser.OMultiplierContext ctx);

    /**
     * Visit a parse tree produced by {@link SlayGrammarParser#power}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitPower(SlayGrammarParser.PowerContext ctx);

    /**
     * Visit a parse tree produced by {@link SlayGrammarParser#meleeVerb}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitMeleeVerb(SlayGrammarParser.MeleeVerbContext ctx);

    /**
     * Visit a parse tree produced by {@link SlayGrammarParser#rangeVerb}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitRangeVerb(SlayGrammarParser.RangeVerbContext ctx);

    /**
     * Visit a parse tree produced by {@link SlayGrammarParser#slay}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitSlay(SlayGrammarParser.SlayContext ctx);

    /**
     * Visit a parse tree produced by {@link SlayGrammarParser#slays}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitSlays(SlayGrammarParser.SlaysContext ctx);
}