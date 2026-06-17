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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/parser/grammars/BlowEffect.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.bloweffect;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link BlowEffectParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 *            operations with no return type.
 */
public interface BlowEffectVisitor<T> extends ParseTreeVisitor<T> {
    /**
     * Visit a parse tree produced by {@link BlowEffectParser#name}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitName(BlowEffectParser.NameContext ctx);

    /**
     * Visit a parse tree produced by {@link BlowEffectParser#power}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitPower(BlowEffectParser.PowerContext ctx);

    /**
     * Visit a parse tree produced by {@link BlowEffectParser#eval}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitEval(BlowEffectParser.EvalContext ctx);

    /**
     * Visit a parse tree produced by {@link BlowEffectParser#desc}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitDesc(BlowEffectParser.DescContext ctx);

    /**
     * Visit a parse tree produced by {@link BlowEffectParser#loreColourBase}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitLoreColourBase(BlowEffectParser.LoreColourBaseContext ctx);

    /**
     * Visit a parse tree produced by {@link BlowEffectParser#loreColourResist}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitLoreColourResist(BlowEffectParser.LoreColourResistContext ctx);

    /**
     * Visit a parse tree produced by {@link BlowEffectParser#loreColourImmune}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitLoreColourImmune(BlowEffectParser.LoreColourImmuneContext ctx);

    /**
     * Visit a parse tree produced by {@link BlowEffectParser#effectType}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitEffectType(BlowEffectParser.EffectTypeContext ctx);

    /**
     * Visit a parse tree produced by {@link BlowEffectParser#resist}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitResist(BlowEffectParser.ResistContext ctx);

    /**
     * Visit a parse tree produced by {@link BlowEffectParser#lashType}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitLashType(BlowEffectParser.LashTypeContext ctx);

    /**
     * Visit a parse tree produced by {@link BlowEffectParser#blowEffect}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitBlowEffect(BlowEffectParser.BlowEffectContext ctx);

    /**
     * Visit a parse tree produced by {@link BlowEffectParser#file}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFile(BlowEffectParser.FileContext ctx);
}