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

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link SlayGrammarParser}.
 */
public interface SlayGrammarListener extends ParseTreeListener {
    /**
     * Enter a parse tree produced by {@link SlayGrammarParser#code}.
     *
     * @param ctx the parse tree
     */
    void enterCode(SlayGrammarParser.CodeContext ctx);

    /**
     * Exit a parse tree produced by {@link SlayGrammarParser#code}.
     *
     * @param ctx the parse tree
     */
    void exitCode(SlayGrammarParser.CodeContext ctx);

    /**
     * Enter a parse tree produced by {@link SlayGrammarParser#name}.
     *
     * @param ctx the parse tree
     */
    void enterName(SlayGrammarParser.NameContext ctx);

    /**
     * Exit a parse tree produced by {@link SlayGrammarParser#name}.
     *
     * @param ctx the parse tree
     */
    void exitName(SlayGrammarParser.NameContext ctx);

    /**
     * Enter a parse tree produced by {@link SlayGrammarParser#raceFlag}.
     *
     * @param ctx the parse tree
     */
    void enterRaceFlag(SlayGrammarParser.RaceFlagContext ctx);

    /**
     * Exit a parse tree produced by {@link SlayGrammarParser#raceFlag}.
     *
     * @param ctx the parse tree
     */
    void exitRaceFlag(SlayGrammarParser.RaceFlagContext ctx);

    /**
     * Enter a parse tree produced by {@link SlayGrammarParser#multiplier}.
     *
     * @param ctx the parse tree
     */
    void enterMultiplier(SlayGrammarParser.MultiplierContext ctx);

    /**
     * Exit a parse tree produced by {@link SlayGrammarParser#multiplier}.
     *
     * @param ctx the parse tree
     */
    void exitMultiplier(SlayGrammarParser.MultiplierContext ctx);

    /**
     * Enter a parse tree produced by {@link SlayGrammarParser#oMultiplier}.
     *
     * @param ctx the parse tree
     */
    void enterOMultiplier(SlayGrammarParser.OMultiplierContext ctx);

    /**
     * Exit a parse tree produced by {@link SlayGrammarParser#oMultiplier}.
     *
     * @param ctx the parse tree
     */
    void exitOMultiplier(SlayGrammarParser.OMultiplierContext ctx);

    /**
     * Enter a parse tree produced by {@link SlayGrammarParser#power}.
     *
     * @param ctx the parse tree
     */
    void enterPower(SlayGrammarParser.PowerContext ctx);

    /**
     * Exit a parse tree produced by {@link SlayGrammarParser#power}.
     *
     * @param ctx the parse tree
     */
    void exitPower(SlayGrammarParser.PowerContext ctx);

    /**
     * Enter a parse tree produced by {@link SlayGrammarParser#meleeVerb}.
     *
     * @param ctx the parse tree
     */
    void enterMeleeVerb(SlayGrammarParser.MeleeVerbContext ctx);

    /**
     * Exit a parse tree produced by {@link SlayGrammarParser#meleeVerb}.
     *
     * @param ctx the parse tree
     */
    void exitMeleeVerb(SlayGrammarParser.MeleeVerbContext ctx);

    /**
     * Enter a parse tree produced by {@link SlayGrammarParser#rangeVerb}.
     *
     * @param ctx the parse tree
     */
    void enterRangeVerb(SlayGrammarParser.RangeVerbContext ctx);

    /**
     * Exit a parse tree produced by {@link SlayGrammarParser#rangeVerb}.
     *
     * @param ctx the parse tree
     */
    void exitRangeVerb(SlayGrammarParser.RangeVerbContext ctx);

    /**
     * Enter a parse tree produced by {@link SlayGrammarParser#slay}.
     *
     * @param ctx the parse tree
     */
    void enterSlay(SlayGrammarParser.SlayContext ctx);

    /**
     * Exit a parse tree produced by {@link SlayGrammarParser#slay}.
     *
     * @param ctx the parse tree
     */
    void exitSlay(SlayGrammarParser.SlayContext ctx);

    /**
     * Enter a parse tree produced by {@link SlayGrammarParser#slays}.
     *
     * @param ctx the parse tree
     */
    void enterSlays(SlayGrammarParser.SlaysContext ctx);

    /**
     * Exit a parse tree produced by {@link SlayGrammarParser#slays}.
     *
     * @param ctx the parse tree
     */
    void exitSlays(SlayGrammarParser.SlaysContext ctx);
}