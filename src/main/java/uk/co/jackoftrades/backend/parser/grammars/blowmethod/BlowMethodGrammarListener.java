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
// Generated from BlowMethodGrammar.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.grammars.blowmethod;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link BlowMethodGrammar}.
 */
public interface BlowMethodGrammarListener extends ParseTreeListener {
    /**
     * Enter a parse tree produced by {@link BlowMethodGrammar#recordCount}.
     *
     * @param ctx the parse tree
     */
    void enterRecordCount(BlowMethodGrammar.RecordCountContext ctx);

    /**
     * Exit a parse tree produced by {@link BlowMethodGrammar#recordCount}.
     *
     * @param ctx the parse tree
     */
    void exitRecordCount(BlowMethodGrammar.RecordCountContext ctx);

    /**
     * Enter a parse tree produced by {@link BlowMethodGrammar#name}.
     *
     * @param ctx the parse tree
     */
    void enterName(BlowMethodGrammar.NameContext ctx);

    /**
     * Exit a parse tree produced by {@link BlowMethodGrammar#name}.
     *
     * @param ctx the parse tree
     */
    void exitName(BlowMethodGrammar.NameContext ctx);

    /**
     * Enter a parse tree produced by {@link BlowMethodGrammar#cut}.
     *
     * @param ctx the parse tree
     */
    void enterCut(BlowMethodGrammar.CutContext ctx);

    /**
     * Exit a parse tree produced by {@link BlowMethodGrammar#cut}.
     *
     * @param ctx the parse tree
     */
    void exitCut(BlowMethodGrammar.CutContext ctx);

    /**
     * Enter a parse tree produced by {@link BlowMethodGrammar#stun}.
     *
     * @param ctx the parse tree
     */
    void enterStun(BlowMethodGrammar.StunContext ctx);

    /**
     * Exit a parse tree produced by {@link BlowMethodGrammar#stun}.
     *
     * @param ctx the parse tree
     */
    void exitStun(BlowMethodGrammar.StunContext ctx);

    /**
     * Enter a parse tree produced by {@link BlowMethodGrammar#miss}.
     *
     * @param ctx the parse tree
     */
    void enterMiss(BlowMethodGrammar.MissContext ctx);

    /**
     * Exit a parse tree produced by {@link BlowMethodGrammar#miss}.
     *
     * @param ctx the parse tree
     */
    void exitMiss(BlowMethodGrammar.MissContext ctx);

    /**
     * Enter a parse tree produced by {@link BlowMethodGrammar#phys}.
     *
     * @param ctx the parse tree
     */
    void enterPhys(BlowMethodGrammar.PhysContext ctx);

    /**
     * Exit a parse tree produced by {@link BlowMethodGrammar#phys}.
     *
     * @param ctx the parse tree
     */
    void exitPhys(BlowMethodGrammar.PhysContext ctx);

    /**
     * Enter a parse tree produced by {@link BlowMethodGrammar#msg}.
     *
     * @param ctx the parse tree
     */
    void enterMsg(BlowMethodGrammar.MsgContext ctx);

    /**
     * Exit a parse tree produced by {@link BlowMethodGrammar#msg}.
     *
     * @param ctx the parse tree
     */
    void exitMsg(BlowMethodGrammar.MsgContext ctx);

    /**
     * Enter a parse tree produced by {@link BlowMethodGrammar#act}.
     *
     * @param ctx the parse tree
     */
    void enterAct(BlowMethodGrammar.ActContext ctx);

    /**
     * Exit a parse tree produced by {@link BlowMethodGrammar#act}.
     *
     * @param ctx the parse tree
     */
    void exitAct(BlowMethodGrammar.ActContext ctx);

    /**
     * Enter a parse tree produced by {@link BlowMethodGrammar#desc}.
     *
     * @param ctx the parse tree
     */
    void enterDesc(BlowMethodGrammar.DescContext ctx);

    /**
     * Exit a parse tree produced by {@link BlowMethodGrammar#desc}.
     *
     * @param ctx the parse tree
     */
    void exitDesc(BlowMethodGrammar.DescContext ctx);

    /**
     * Enter a parse tree produced by {@link BlowMethodGrammar#blow}.
     *
     * @param ctx the parse tree
     */
    void enterBlow(BlowMethodGrammar.BlowContext ctx);

    /**
     * Exit a parse tree produced by {@link BlowMethodGrammar#blow}.
     *
     * @param ctx the parse tree
     */
    void exitBlow(BlowMethodGrammar.BlowContext ctx);

    /**
     * Enter a parse tree produced by {@link BlowMethodGrammar#file}.
     *
     * @param ctx the parse tree
     */
    void enterFile(BlowMethodGrammar.FileContext ctx);

    /**
     * Exit a parse tree produced by {@link BlowMethodGrammar#file}.
     *
     * @param ctx the parse tree
     */
    void exitFile(BlowMethodGrammar.FileContext ctx);
}