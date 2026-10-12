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

package uk.co.jackoftradesltd.middle.game.gameengine;

import uk.co.jackoftradesltd.middle.game.enums.CommandArgumentType;
import uk.co.jackoftradesltd.middle.game.gameengine.argumentdata.*;

/**
 * The value carried by a command argument - the port of C's {@code union cmd_arg_data}
 * (cmd-core.h), reworked from an untagged union into a type-safe tagged one.
 *
 * <p>In C the argument value was a union of a string, an int (used for choice / number /
 * direction / target), an object pointer, and a grid location, with a separate {@code cmd_arg_type}
 * saying which member was live; nothing but discipline stopped code reading the wrong member. Here
 * that becomes a sealed interface whose permitted records - one per meaningful value kind - make
 * the variant part of the type. {@code Command}'s {@code getArg*} methods read the value with a
 * record pattern ({@code instanceof ArgumentChoice(int value)} and the like), so asking for the
 * wrong kind simply fails to match and yields an empty result, the port of C's
 * {@code CMD_ARG_WRONG_TYPE}, instead of reinterpreting the bits of another member.
 *
 * <p>Each variant reports its own {@link #type()} so a {@link CommandArgumentData} can be checked
 * against a requested {@link CommandArgumentType} without unwrapping it. Note that
 * {@link ArgumentTarget} and {@link ArgumentDirection} are separate variants even though C stored
 * both in the union's single {@code int}: they mean different things (a target code versus a
 * movement direction), and the split keeps that distinction honest. Both hold a
 * {@code DirectionEnum}, whose {@code DIR_TARGET} and {@code DIR_NONE} are two constants for the
 * one C value 5.
 *
 * <p>The mapping from C is one record per union member, with the target added:
 * <ul>
 *   <li>{@code string} - {@link ArgumentString}</li>
 *   <li>{@code choice} - {@link ArgumentChoice}</li>
 *   <li>{@code obj} - {@link ArgumentItem}</li>
 *   <li>{@code number} - {@link ArgumentNumber}</li>
 *   <li>{@code direction} - {@link ArgumentDirection}, and also {@link ArgumentTarget}</li>
 *   <li>{@code point} - {@link ArgumentPoint}</li>
 * </ul>
 * C's {@code arg_NONE}, an unused slot, has no variant: an absent argument is expressed by the
 * argument not being in the {@code Command}'s list at all. C's {@code cmd_release}, which frees
 * string arguments, has no counterpart either, since a {@link String} is left to garbage
 * collection.
 *
 * <p>Interface CommandArgumentData coded before 261012, commented in full on 261012.
 *
 * @author Rowan Crowther
 */
public sealed interface CommandArgumentData permits ArgumentString, ArgumentChoice, ArgumentItem, ArgumentNumber,
        ArgumentDirection, ArgumentTarget, ArgumentPoint {
    /**
     * The argument-type tag corresponding to this variant, so a value can be matched against a
     * requested type without a downcast. It is the tag C's {@code cmd_set_arg_*} functions pass to
     * {@code cmd_set_arg} beside the union, and the one {@code cmd_get_arg} compares before
     * reading it. Each variant returns one fixed constant, whatever value it holds; no variant
     * reports {@link CommandArgumentType#arg_NONE}.
     *
     * <p>Function type coded before 261012, commented in full on 261012.
     *
     * @return this variant's {@link CommandArgumentType}
     */
    CommandArgumentType type();

    /**
     * Returns an independent copy of this value, the per-argument step of C's {@code cmd_copy}. C
     * copies the whole command by struct assignment and then deep-copies only the
     * {@code arg_STRING} arguments with {@code string_make}, so every other kind is copied by value
     * and an {@code arg_ITEM} pointer is shared between the copy and the original. The variants
     * follow that: a {@link String} is immutable so needs no duplicating, and
     * {@link ArgumentItem} deliberately keeps the same {@code ItemObject} reference rather than
     * cloning it. The result equals this value and has the same {@link #type()}.
     *
     * <p>Function copy coded before 261012, commented in full on 261012.
     *
     * @return a new value of the same variant that equals this one
     */
    CommandArgumentData copy();
}
