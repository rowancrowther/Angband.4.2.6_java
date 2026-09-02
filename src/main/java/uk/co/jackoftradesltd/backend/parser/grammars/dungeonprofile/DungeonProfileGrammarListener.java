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

// Generated from DungeonProfileGrammar.g4 by ANTLR 4.13.2
package uk.co.jackoftradesltd.backend.parser.grammars.dungeonprofile;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link DungeonProfileGrammar}.
 */
public interface DungeonProfileGrammarListener extends ParseTreeListener {
    /**
     * Enter a parse tree produced by {@link DungeonProfileGrammar#recordCount}.
     *
     * @param ctx the parse tree
     */
    void enterRecordCount(DungeonProfileGrammar.RecordCountContext ctx);

    /**
     * Exit a parse tree produced by {@link DungeonProfileGrammar#recordCount}.
     *
     * @param ctx the parse tree
     */
    void exitRecordCount(DungeonProfileGrammar.RecordCountContext ctx);

    /**
     * Enter a parse tree produced by {@link DungeonProfileGrammar#name}.
     *
     * @param ctx the parse tree
     */
    void enterName(DungeonProfileGrammar.NameContext ctx);

    /**
     * Exit a parse tree produced by {@link DungeonProfileGrammar#name}.
     *
     * @param ctx the parse tree
     */
    void exitName(DungeonProfileGrammar.NameContext ctx);

    /**
     * Enter a parse tree produced by {@link DungeonProfileGrammar#params}.
     *
     * @param ctx the parse tree
     */
    void enterParams(DungeonProfileGrammar.ParamsContext ctx);

    /**
     * Exit a parse tree produced by {@link DungeonProfileGrammar#params}.
     *
     * @param ctx the parse tree
     */
    void exitParams(DungeonProfileGrammar.ParamsContext ctx);

    /**
     * Enter a parse tree produced by {@link DungeonProfileGrammar#tunnel}.
     *
     * @param ctx the parse tree
     */
    void enterTunnel(DungeonProfileGrammar.TunnelContext ctx);

    /**
     * Exit a parse tree produced by {@link DungeonProfileGrammar#tunnel}.
     *
     * @param ctx the parse tree
     */
    void exitTunnel(DungeonProfileGrammar.TunnelContext ctx);

    /**
     * Enter a parse tree produced by {@link DungeonProfileGrammar#streamer}.
     *
     * @param ctx the parse tree
     */
    void enterStreamer(DungeonProfileGrammar.StreamerContext ctx);

    /**
     * Exit a parse tree produced by {@link DungeonProfileGrammar#streamer}.
     *
     * @param ctx the parse tree
     */
    void exitStreamer(DungeonProfileGrammar.StreamerContext ctx);

    /**
     * Enter a parse tree produced by {@link DungeonProfileGrammar#alloc}.
     *
     * @param ctx the parse tree
     */
    void enterAlloc(DungeonProfileGrammar.AllocContext ctx);

    /**
     * Exit a parse tree produced by {@link DungeonProfileGrammar#alloc}.
     *
     * @param ctx the parse tree
     */
    void exitAlloc(DungeonProfileGrammar.AllocContext ctx);

    /**
     * Enter a parse tree produced by {@link DungeonProfileGrammar#minLevel}.
     *
     * @param ctx the parse tree
     */
    void enterMinLevel(DungeonProfileGrammar.MinLevelContext ctx);

    /**
     * Exit a parse tree produced by {@link DungeonProfileGrammar#minLevel}.
     *
     * @param ctx the parse tree
     */
    void exitMinLevel(DungeonProfileGrammar.MinLevelContext ctx);

    /**
     * Enter a parse tree produced by {@link DungeonProfileGrammar#room}.
     *
     * @param ctx the parse tree
     */
    void enterRoom(DungeonProfileGrammar.RoomContext ctx);

    /**
     * Exit a parse tree produced by {@link DungeonProfileGrammar#room}.
     *
     * @param ctx the parse tree
     */
    void exitRoom(DungeonProfileGrammar.RoomContext ctx);

    /**
     * Enter a parse tree produced by {@link DungeonProfileGrammar#profile}.
     *
     * @param ctx the parse tree
     */
    void enterProfile(DungeonProfileGrammar.ProfileContext ctx);

    /**
     * Exit a parse tree produced by {@link DungeonProfileGrammar#profile}.
     *
     * @param ctx the parse tree
     */
    void exitProfile(DungeonProfileGrammar.ProfileContext ctx);

    /**
     * Enter a parse tree produced by {@link DungeonProfileGrammar#file}.
     *
     * @param ctx the parse tree
     */
    void enterFile(DungeonProfileGrammar.FileContext ctx);

    /**
     * Exit a parse tree produced by {@link DungeonProfileGrammar#file}.
     *
     * @param ctx the parse tree
     */
    void exitFile(DungeonProfileGrammar.FileContext ctx);
}