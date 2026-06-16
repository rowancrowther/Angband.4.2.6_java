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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/parser/grammars/ObjectProperty.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.objectproperty;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link ObjectPropertyParser}.
 */
public interface ObjectPropertyListener extends ParseTreeListener {
    /**
     * Enter a parse tree produced by {@link ObjectPropertyParser#name}.
     *
     * @param ctx the parse tree
     */
    void enterName(ObjectPropertyParser.NameContext ctx);

    /**
     * Exit a parse tree produced by {@link ObjectPropertyParser#name}.
     *
     * @param ctx the parse tree
     */
    void exitName(ObjectPropertyParser.NameContext ctx);

    /**
     * Enter a parse tree produced by {@link ObjectPropertyParser#type}.
     *
     * @param ctx the parse tree
     */
    void enterType(ObjectPropertyParser.TypeContext ctx);

    /**
     * Exit a parse tree produced by {@link ObjectPropertyParser#type}.
     *
     * @param ctx the parse tree
     */
    void exitType(ObjectPropertyParser.TypeContext ctx);

    /**
     * Enter a parse tree produced by {@link ObjectPropertyParser#subType}.
     *
     * @param ctx the parse tree
     */
    void enterSubType(ObjectPropertyParser.SubTypeContext ctx);

    /**
     * Exit a parse tree produced by {@link ObjectPropertyParser#subType}.
     *
     * @param ctx the parse tree
     */
    void exitSubType(ObjectPropertyParser.SubTypeContext ctx);

    /**
     * Enter a parse tree produced by {@link ObjectPropertyParser#idType}.
     *
     * @param ctx the parse tree
     */
    void enterIdType(ObjectPropertyParser.IdTypeContext ctx);

    /**
     * Exit a parse tree produced by {@link ObjectPropertyParser#idType}.
     *
     * @param ctx the parse tree
     */
    void exitIdType(ObjectPropertyParser.IdTypeContext ctx);

    /**
     * Enter a parse tree produced by {@link ObjectPropertyParser#code}.
     *
     * @param ctx the parse tree
     */
    void enterCode(ObjectPropertyParser.CodeContext ctx);

    /**
     * Exit a parse tree produced by {@link ObjectPropertyParser#code}.
     *
     * @param ctx the parse tree
     */
    void exitCode(ObjectPropertyParser.CodeContext ctx);

    /**
     * Enter a parse tree produced by {@link ObjectPropertyParser#power}.
     *
     * @param ctx the parse tree
     */
    void enterPower(ObjectPropertyParser.PowerContext ctx);

    /**
     * Exit a parse tree produced by {@link ObjectPropertyParser#power}.
     *
     * @param ctx the parse tree
     */
    void exitPower(ObjectPropertyParser.PowerContext ctx);

    /**
     * Enter a parse tree produced by {@link ObjectPropertyParser#mult}.
     *
     * @param ctx the parse tree
     */
    void enterMult(ObjectPropertyParser.MultContext ctx);

    /**
     * Exit a parse tree produced by {@link ObjectPropertyParser#mult}.
     *
     * @param ctx the parse tree
     */
    void exitMult(ObjectPropertyParser.MultContext ctx);

    /**
     * Enter a parse tree produced by {@link ObjectPropertyParser#typeMult}.
     *
     * @param ctx the parse tree
     */
    void enterTypeMult(ObjectPropertyParser.TypeMultContext ctx);

    /**
     * Exit a parse tree produced by {@link ObjectPropertyParser#typeMult}.
     *
     * @param ctx the parse tree
     */
    void exitTypeMult(ObjectPropertyParser.TypeMultContext ctx);

    /**
     * Enter a parse tree produced by {@link ObjectPropertyParser#adjective}.
     *
     * @param ctx the parse tree
     */
    void enterAdjective(ObjectPropertyParser.AdjectiveContext ctx);

    /**
     * Exit a parse tree produced by {@link ObjectPropertyParser#adjective}.
     *
     * @param ctx the parse tree
     */
    void exitAdjective(ObjectPropertyParser.AdjectiveContext ctx);

    /**
     * Enter a parse tree produced by {@link ObjectPropertyParser#negAdjective}.
     *
     * @param ctx the parse tree
     */
    void enterNegAdjective(ObjectPropertyParser.NegAdjectiveContext ctx);

    /**
     * Exit a parse tree produced by {@link ObjectPropertyParser#negAdjective}.
     *
     * @param ctx the parse tree
     */
    void exitNegAdjective(ObjectPropertyParser.NegAdjectiveContext ctx);

    /**
     * Enter a parse tree produced by {@link ObjectPropertyParser#msg}.
     *
     * @param ctx the parse tree
     */
    void enterMsg(ObjectPropertyParser.MsgContext ctx);

    /**
     * Exit a parse tree produced by {@link ObjectPropertyParser#msg}.
     *
     * @param ctx the parse tree
     */
    void exitMsg(ObjectPropertyParser.MsgContext ctx);

    /**
     * Enter a parse tree produced by {@link ObjectPropertyParser#bindUI}.
     *
     * @param ctx the parse tree
     */
    void enterBindUI(ObjectPropertyParser.BindUIContext ctx);

    /**
     * Exit a parse tree produced by {@link ObjectPropertyParser#bindUI}.
     *
     * @param ctx the parse tree
     */
    void exitBindUI(ObjectPropertyParser.BindUIContext ctx);

    /**
     * Enter a parse tree produced by {@link ObjectPropertyParser#desc}.
     *
     * @param ctx the parse tree
     */
    void enterDesc(ObjectPropertyParser.DescContext ctx);

    /**
     * Exit a parse tree produced by {@link ObjectPropertyParser#desc}.
     *
     * @param ctx the parse tree
     */
    void exitDesc(ObjectPropertyParser.DescContext ctx);

    /**
     * Enter a parse tree produced by {@link ObjectPropertyParser#objProperty}.
     *
     * @param ctx the parse tree
     */
    void enterObjProperty(ObjectPropertyParser.ObjPropertyContext ctx);

    /**
     * Exit a parse tree produced by {@link ObjectPropertyParser#objProperty}.
     *
     * @param ctx the parse tree
     */
    void exitObjProperty(ObjectPropertyParser.ObjPropertyContext ctx);

    /**
     * Enter a parse tree produced by {@link ObjectPropertyParser#file}.
     *
     * @param ctx the parse tree
     */
    void enterFile(ObjectPropertyParser.FileContext ctx);

    /**
     * Exit a parse tree produced by {@link ObjectPropertyParser#file}.
     *
     * @param ctx the parse tree
     */
    void exitFile(ObjectPropertyParser.FileContext ctx);
}