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

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link BlowMethodGrammar}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 *            operations with no return type.
 */
public interface BlowMethodGrammarVisitor<T> extends ParseTreeVisitor<T> {
    /**
     * Visit a parse tree produced by {@link BlowMethodGrammar#recordCount}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitRecordCount(BlowMethodGrammar.RecordCountContext ctx);

    /**
     * Visit a parse tree produced by {@link BlowMethodGrammar#name}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitName(BlowMethodGrammar.NameContext ctx);

    /**
     * Visit a parse tree produced by {@link BlowMethodGrammar#cut}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitCut(BlowMethodGrammar.CutContext ctx);

    /**
     * Visit a parse tree produced by {@link BlowMethodGrammar#stun}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitStun(BlowMethodGrammar.StunContext ctx);

    /**
     * Visit a parse tree produced by {@link BlowMethodGrammar#miss}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitMiss(BlowMethodGrammar.MissContext ctx);

    /**
     * Visit a parse tree produced by {@link BlowMethodGrammar#phys}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitPhys(BlowMethodGrammar.PhysContext ctx);

    /**
     * Visit a parse tree produced by {@link BlowMethodGrammar#msg}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitMsg(BlowMethodGrammar.MsgContext ctx);

    /**
     * Visit a parse tree produced by {@link BlowMethodGrammar#act}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitAct(BlowMethodGrammar.ActContext ctx);

    /**
     * Visit a parse tree produced by {@link BlowMethodGrammar#desc}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitDesc(BlowMethodGrammar.DescContext ctx);

    /**
     * Visit a parse tree produced by {@link BlowMethodGrammar#blow}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitBlow(BlowMethodGrammar.BlowContext ctx);

    /**
     * Visit a parse tree produced by {@link BlowMethodGrammar#file}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFile(BlowMethodGrammar.FileContext ctx);
}