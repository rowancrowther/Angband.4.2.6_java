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

package uk.co.jackoftradesltd.backend.parser;

import uk.co.jackoftradesltd.middle.effect.Effect;

import java.lang.reflect.Field;

/**
 * Reads the private {@code y}, {@code x} and {@code diceString} fields of an {@link Effect}, so
 * the reader tests can show that {@code effect-yx:} and {@code dice:} both reached the same
 * effect. Shared by the suites whose grammars import {@code EffectBlock.g4}.
 *
 * <p>The {@code dice} field itself stays null at load: {@code EffectAssembler} keeps the raw
 * string until the roll engine exists, so {@code diceString} is where a {@code dice:} line lands.
 */
final class EffectProbe {

    private EffectProbe() {
    }

    /**
     * The effect's {@code y}, {@code x} and raw dice string, as {@code "y,x,diceString"}. An
     * effect with no {@code dice:} line shows {@code null} for the last part.
     *
     * @param effect an {@link Effect}, passed as {@link Object} so callers can hand over the raw
     *               element of a reflected list
     */
    static String yxBase(Object effect) throws Exception {
        Effect e = (Effect) effect;
        return read(e, "y") + "," + read(e, "x") + "," + read(e, "diceString");
    }

    /**
     * The effect's bound expressions in order, each as {@code "letter:BASE:operations"}, joined by
     * {@code "|"}. An effect that bound none gives the empty string.
     *
     * @param effect an {@link Effect}, passed as {@link Object} so callers can hand over the raw
     *               element of a reflected list
     */
    static String expressions(Object effect) throws Exception {
        java.util.List<?> list = (java.util.List<?>) read((Effect) effect, "expression");
        StringBuilder sb = new StringBuilder();
        for (Object x : list) {
            if (!sb.isEmpty()) sb.append('|');
            sb.append(readExpr(x, "codeLetter")).append(':')
              .append(readExpr(x, "baseType")).append(':')
              .append(readExpr(x, "operations"));
        }
        return sb.toString();
    }

    /**
     * The effect's {@code msg}, which is C's {@code effect->msg}: {@code null} when no
     * {@code effect-msg:} line reached the effect.
     *
     * @param effect an {@link Effect}, passed as {@link Object} so callers can hand over the raw
     *               element of a reflected list
     */
    static String message(Object effect) throws Exception {
        return (String) read((Effect) effect, "msg");
    }

    private static Object readExpr(Object x, String name) throws Exception {
        Field f = x.getClass().getDeclaredField(name);
        f.setAccessible(true);
        return f.get(x);
    }

    private static Object read(Effect e, String name) throws Exception {
        Field f = Effect.class.getDeclaredField(name);
        f.setAccessible(true);
        return f.get(e);
    }
}
