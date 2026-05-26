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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/parser/grammars/Pain.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.pain;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link PainParser}.
 */
public interface PainListener extends ParseTreeListener {
    /**
     * Enter a parse tree produced by {@link PainParser#type}.
     *
     * @param ctx the parse tree
     */
    void enterType(PainParser.TypeContext ctx);

    /**
     * Exit a parse tree produced by {@link PainParser#type}.
     *
     * @param ctx the parse tree
     */
    void exitType(PainParser.TypeContext ctx);

    /**
     * Enter a parse tree produced by {@link PainParser#message}.
     *
     * @param ctx the parse tree
     */
    void enterMessage(PainParser.MessageContext ctx);

    /**
     * Exit a parse tree produced by {@link PainParser#message}.
     *
     * @param ctx the parse tree
     */
    void exitMessage(PainParser.MessageContext ctx);

    /**
     * Enter a parse tree produced by {@link PainParser#painEntry}.
     *
     * @param ctx the parse tree
     */
    void enterPainEntry(PainParser.PainEntryContext ctx);

    /**
     * Exit a parse tree produced by {@link PainParser#painEntry}.
     *
     * @param ctx the parse tree
     */
    void exitPainEntry(PainParser.PainEntryContext ctx);

    /**
     * Enter a parse tree produced by {@link PainParser#file}.
     *
     * @param ctx the parse tree
     */
    void enterFile(PainParser.FileContext ctx);

    /**
     * Exit a parse tree produced by {@link PainParser#file}.
     *
     * @param ctx the parse tree
     */
    void exitFile(PainParser.FileContext ctx);
}