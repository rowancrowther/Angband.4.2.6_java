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

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link SlayGrammar}.
 */
public interface SlayGrammarListener extends ParseTreeListener {
    /**
     * Enter a parse tree produced by {@link SlayGrammar#recordCount}.
     *
     * @param ctx the parse tree
     */
    void enterRecordCount(SlayGrammar.RecordCountContext ctx);

    /**
     * Exit a parse tree produced by {@link SlayGrammar#recordCount}.
     *
     * @param ctx the parse tree
     */
    void exitRecordCount(SlayGrammar.RecordCountContext ctx);

    /**
     * Enter a parse tree produced by {@link SlayGrammar#code}.
     *
     * @param ctx the parse tree
     */
    void enterCode(SlayGrammar.CodeContext ctx);

    /**
     * Exit a parse tree produced by {@link SlayGrammar#code}.
     *
     * @param ctx the parse tree
     */
    void exitCode(SlayGrammar.CodeContext ctx);

    /**
     * Enter a parse tree produced by {@link SlayGrammar#name}.
     *
     * @param ctx the parse tree
     */
    void enterName(SlayGrammar.NameContext ctx);

    /**
     * Exit a parse tree produced by {@link SlayGrammar#name}.
     *
     * @param ctx the parse tree
     */
    void exitName(SlayGrammar.NameContext ctx);

    /**
     * Enter a parse tree produced by {@link SlayGrammar#raceFlag}.
     *
     * @param ctx the parse tree
     */
    void enterRaceFlag(SlayGrammar.RaceFlagContext ctx);

    /**
     * Exit a parse tree produced by {@link SlayGrammar#raceFlag}.
     *
     * @param ctx the parse tree
     */
    void exitRaceFlag(SlayGrammar.RaceFlagContext ctx);

    /**
     * Enter a parse tree produced by {@link SlayGrammar#base}.
     *
     * @param ctx the parse tree
     */
    void enterBase(SlayGrammar.BaseContext ctx);

    /**
     * Exit a parse tree produced by {@link SlayGrammar#base}.
     *
     * @param ctx the parse tree
     */
    void exitBase(SlayGrammar.BaseContext ctx);

    /**
     * Enter a parse tree produced by {@link SlayGrammar#multiplier}.
     *
     * @param ctx the parse tree
     */
    void enterMultiplier(SlayGrammar.MultiplierContext ctx);

    /**
     * Exit a parse tree produced by {@link SlayGrammar#multiplier}.
     *
     * @param ctx the parse tree
     */
    void exitMultiplier(SlayGrammar.MultiplierContext ctx);

    /**
     * Enter a parse tree produced by {@link SlayGrammar#oMultiplier}.
     *
     * @param ctx the parse tree
     */
    void enterOMultiplier(SlayGrammar.OMultiplierContext ctx);

    /**
     * Exit a parse tree produced by {@link SlayGrammar#oMultiplier}.
     *
     * @param ctx the parse tree
     */
    void exitOMultiplier(SlayGrammar.OMultiplierContext ctx);

    /**
     * Enter a parse tree produced by {@link SlayGrammar#power}.
     *
     * @param ctx the parse tree
     */
    void enterPower(SlayGrammar.PowerContext ctx);

    /**
     * Exit a parse tree produced by {@link SlayGrammar#power}.
     *
     * @param ctx the parse tree
     */
    void exitPower(SlayGrammar.PowerContext ctx);

    /**
     * Enter a parse tree produced by {@link SlayGrammar#meleeVerb}.
     *
     * @param ctx the parse tree
     */
    void enterMeleeVerb(SlayGrammar.MeleeVerbContext ctx);

    /**
     * Exit a parse tree produced by {@link SlayGrammar#meleeVerb}.
     *
     * @param ctx the parse tree
     */
    void exitMeleeVerb(SlayGrammar.MeleeVerbContext ctx);

    /**
     * Enter a parse tree produced by {@link SlayGrammar#rangeVerb}.
     *
     * @param ctx the parse tree
     */
    void enterRangeVerb(SlayGrammar.RangeVerbContext ctx);

    /**
     * Exit a parse tree produced by {@link SlayGrammar#rangeVerb}.
     *
     * @param ctx the parse tree
     */
    void exitRangeVerb(SlayGrammar.RangeVerbContext ctx);

    /**
     * Enter a parse tree produced by {@link SlayGrammar#slay}.
     *
     * @param ctx the parse tree
     */
    void enterSlay(SlayGrammar.SlayContext ctx);

    /**
     * Exit a parse tree produced by {@link SlayGrammar#slay}.
     *
     * @param ctx the parse tree
     */
    void exitSlay(SlayGrammar.SlayContext ctx);

    /**
     * Enter a parse tree produced by {@link SlayGrammar#file}.
     *
     * @param ctx the parse tree
     */
    void enterFile(SlayGrammar.FileContext ctx);

    /**
     * Exit a parse tree produced by {@link SlayGrammar#file}.
     *
     * @param ctx the parse tree
     */
    void exitFile(SlayGrammar.FileContext ctx);
}