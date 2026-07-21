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

// Generated from PitGrammar.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.grammars.pit;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link PitGrammar}.
 */
public interface PitGrammarListener extends ParseTreeListener {
    /**
     * Enter a parse tree produced by {@link PitGrammar#recordCount}.
     *
     * @param ctx the parse tree
     */
    void enterRecordCount(PitGrammar.RecordCountContext ctx);

    /**
     * Exit a parse tree produced by {@link PitGrammar#recordCount}.
     *
     * @param ctx the parse tree
     */
    void exitRecordCount(PitGrammar.RecordCountContext ctx);

    /**
     * Enter a parse tree produced by {@link PitGrammar#name}.
     *
     * @param ctx the parse tree
     */
    void enterName(PitGrammar.NameContext ctx);

    /**
     * Exit a parse tree produced by {@link PitGrammar#name}.
     *
     * @param ctx the parse tree
     */
    void exitName(PitGrammar.NameContext ctx);

    /**
     * Enter a parse tree produced by {@link PitGrammar#room}.
     *
     * @param ctx the parse tree
     */
    void enterRoom(PitGrammar.RoomContext ctx);

    /**
     * Exit a parse tree produced by {@link PitGrammar#room}.
     *
     * @param ctx the parse tree
     */
    void exitRoom(PitGrammar.RoomContext ctx);

    /**
     * Enter a parse tree produced by {@link PitGrammar#alloc}.
     *
     * @param ctx the parse tree
     */
    void enterAlloc(PitGrammar.AllocContext ctx);

    /**
     * Exit a parse tree produced by {@link PitGrammar#alloc}.
     *
     * @param ctx the parse tree
     */
    void exitAlloc(PitGrammar.AllocContext ctx);

    /**
     * Enter a parse tree produced by {@link PitGrammar#objRarity}.
     *
     * @param ctx the parse tree
     */
    void enterObjRarity(PitGrammar.ObjRarityContext ctx);

    /**
     * Exit a parse tree produced by {@link PitGrammar#objRarity}.
     *
     * @param ctx the parse tree
     */
    void exitObjRarity(PitGrammar.ObjRarityContext ctx);

    /**
     * Enter a parse tree produced by {@link PitGrammar#colour}.
     *
     * @param ctx the parse tree
     */
    void enterColour(PitGrammar.ColourContext ctx);

    /**
     * Exit a parse tree produced by {@link PitGrammar#colour}.
     *
     * @param ctx the parse tree
     */
    void exitColour(PitGrammar.ColourContext ctx);

    /**
     * Enter a parse tree produced by {@link PitGrammar#monBase}.
     *
     * @param ctx the parse tree
     */
    void enterMonBase(PitGrammar.MonBaseContext ctx);

    /**
     * Exit a parse tree produced by {@link PitGrammar#monBase}.
     *
     * @param ctx the parse tree
     */
    void exitMonBase(PitGrammar.MonBaseContext ctx);

    /**
     * Enter a parse tree produced by {@link PitGrammar#flagsReq}.
     *
     * @param ctx the parse tree
     */
    void enterFlagsReq(PitGrammar.FlagsReqContext ctx);

    /**
     * Exit a parse tree produced by {@link PitGrammar#flagsReq}.
     *
     * @param ctx the parse tree
     */
    void exitFlagsReq(PitGrammar.FlagsReqContext ctx);

    /**
     * Enter a parse tree produced by {@link PitGrammar#flagsBan}.
     *
     * @param ctx the parse tree
     */
    void enterFlagsBan(PitGrammar.FlagsBanContext ctx);

    /**
     * Exit a parse tree produced by {@link PitGrammar#flagsBan}.
     *
     * @param ctx the parse tree
     */
    void exitFlagsBan(PitGrammar.FlagsBanContext ctx);

    /**
     * Enter a parse tree produced by {@link PitGrammar#innateFreq}.
     *
     * @param ctx the parse tree
     */
    void enterInnateFreq(PitGrammar.InnateFreqContext ctx);

    /**
     * Exit a parse tree produced by {@link PitGrammar#innateFreq}.
     *
     * @param ctx the parse tree
     */
    void exitInnateFreq(PitGrammar.InnateFreqContext ctx);

    /**
     * Enter a parse tree produced by {@link PitGrammar#spellReq}.
     *
     * @param ctx the parse tree
     */
    void enterSpellReq(PitGrammar.SpellReqContext ctx);

    /**
     * Exit a parse tree produced by {@link PitGrammar#spellReq}.
     *
     * @param ctx the parse tree
     */
    void exitSpellReq(PitGrammar.SpellReqContext ctx);

    /**
     * Enter a parse tree produced by {@link PitGrammar#spellBan}.
     *
     * @param ctx the parse tree
     */
    void enterSpellBan(PitGrammar.SpellBanContext ctx);

    /**
     * Exit a parse tree produced by {@link PitGrammar#spellBan}.
     *
     * @param ctx the parse tree
     */
    void exitSpellBan(PitGrammar.SpellBanContext ctx);

    /**
     * Enter a parse tree produced by {@link PitGrammar#monBan}.
     *
     * @param ctx the parse tree
     */
    void enterMonBan(PitGrammar.MonBanContext ctx);

    /**
     * Exit a parse tree produced by {@link PitGrammar#monBan}.
     *
     * @param ctx the parse tree
     */
    void exitMonBan(PitGrammar.MonBanContext ctx);

    /**
     * Enter a parse tree produced by {@link PitGrammar#pitRecord}.
     *
     * @param ctx the parse tree
     */
    void enterPitRecord(PitGrammar.PitRecordContext ctx);

    /**
     * Exit a parse tree produced by {@link PitGrammar#pitRecord}.
     *
     * @param ctx the parse tree
     */
    void exitPitRecord(PitGrammar.PitRecordContext ctx);

    /**
     * Enter a parse tree produced by {@link PitGrammar#file}.
     *
     * @param ctx the parse tree
     */
    void enterFile(PitGrammar.FileContext ctx);

    /**
     * Exit a parse tree produced by {@link PitGrammar#file}.
     *
     * @param ctx the parse tree
     */
    void exitFile(PitGrammar.FileContext ctx);
}