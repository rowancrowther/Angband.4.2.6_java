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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/parser/grammars/BlowMethod.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.blowmethod;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link BlowMethodParser}.
 */
public interface BlowMethodListener extends ParseTreeListener {
    /**
     * Enter a parse tree produced by {@link BlowMethodParser#name}.
     *
     * @param ctx the parse tree
     */
    void enterName(BlowMethodParser.NameContext ctx);

    /**
     * Exit a parse tree produced by {@link BlowMethodParser#name}.
     *
     * @param ctx the parse tree
     */
    void exitName(BlowMethodParser.NameContext ctx);

    /**
     * Enter a parse tree produced by {@link BlowMethodParser#cut}.
     *
     * @param ctx the parse tree
     */
    void enterCut(BlowMethodParser.CutContext ctx);

    /**
     * Exit a parse tree produced by {@link BlowMethodParser#cut}.
     *
     * @param ctx the parse tree
     */
    void exitCut(BlowMethodParser.CutContext ctx);

    /**
     * Enter a parse tree produced by {@link BlowMethodParser#stun}.
     *
     * @param ctx the parse tree
     */
    void enterStun(BlowMethodParser.StunContext ctx);

    /**
     * Exit a parse tree produced by {@link BlowMethodParser#stun}.
     *
     * @param ctx the parse tree
     */
    void exitStun(BlowMethodParser.StunContext ctx);

    /**
     * Enter a parse tree produced by {@link BlowMethodParser#miss}.
     *
     * @param ctx the parse tree
     */
    void enterMiss(BlowMethodParser.MissContext ctx);

    /**
     * Exit a parse tree produced by {@link BlowMethodParser#miss}.
     *
     * @param ctx the parse tree
     */
    void exitMiss(BlowMethodParser.MissContext ctx);

    /**
     * Enter a parse tree produced by {@link BlowMethodParser#phys}.
     *
     * @param ctx the parse tree
     */
    void enterPhys(BlowMethodParser.PhysContext ctx);

    /**
     * Exit a parse tree produced by {@link BlowMethodParser#phys}.
     *
     * @param ctx the parse tree
     */
    void exitPhys(BlowMethodParser.PhysContext ctx);

    /**
     * Enter a parse tree produced by {@link BlowMethodParser#msg}.
     *
     * @param ctx the parse tree
     */
    void enterMsg(BlowMethodParser.MsgContext ctx);

    /**
     * Exit a parse tree produced by {@link BlowMethodParser#msg}.
     *
     * @param ctx the parse tree
     */
    void exitMsg(BlowMethodParser.MsgContext ctx);

    /**
     * Enter a parse tree produced by {@link BlowMethodParser#act}.
     *
     * @param ctx the parse tree
     */
    void enterAct(BlowMethodParser.ActContext ctx);

    /**
     * Exit a parse tree produced by {@link BlowMethodParser#act}.
     *
     * @param ctx the parse tree
     */
    void exitAct(BlowMethodParser.ActContext ctx);

    /**
     * Enter a parse tree produced by {@link BlowMethodParser#desc}.
     *
     * @param ctx the parse tree
     */
    void enterDesc(BlowMethodParser.DescContext ctx);

    /**
     * Exit a parse tree produced by {@link BlowMethodParser#desc}.
     *
     * @param ctx the parse tree
     */
    void exitDesc(BlowMethodParser.DescContext ctx);

    /**
     * Enter a parse tree produced by {@link BlowMethodParser#blowMethod}.
     *
     * @param ctx the parse tree
     */
    void enterBlowMethod(BlowMethodParser.BlowMethodContext ctx);

    /**
     * Exit a parse tree produced by {@link BlowMethodParser#blowMethod}.
     *
     * @param ctx the parse tree
     */
    void exitBlowMethod(BlowMethodParser.BlowMethodContext ctx);

    /**
     * Enter a parse tree produced by {@link BlowMethodParser#file}.
     *
     * @param ctx the parse tree
     */
    void enterFile(BlowMethodParser.FileContext ctx);

    /**
     * Exit a parse tree produced by {@link BlowMethodParser#file}.
     *
     * @param ctx the parse tree
     */
    void exitFile(BlowMethodParser.FileContext ctx);
}