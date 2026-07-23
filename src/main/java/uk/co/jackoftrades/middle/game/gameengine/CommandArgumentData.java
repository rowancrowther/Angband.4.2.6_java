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

package uk.co.jackoftrades.middle.game.gameengine;

import uk.co.jackoftrades.middle.game.enums.CommandArgumentType;
import uk.co.jackoftrades.middle.game.gameengine.argumentdata.*;

/**
 * The value carried by a command argument - the port of C's {@code union cmd_arg_data}
 * (cmd-core.h), reworked from an untagged union into a type-safe tagged one.
 *
 * <p>In C the argument value was a union of a string, an int (used for choice / number /
 * direction / target), an object pointer, and a grid location, with a separate {@code cmd_arg_type}
 * saying which member was live; nothing but discipline stopped code reading the wrong member. Here
 * that becomes a sealed interface whose permitted records - one per meaningful value kind - make
 * the variant part of the type. Reading the value is a pattern switch the compiler checks for
 * exhaustiveness, and mismatches are impossible to express rather than merely discouraged.
 *
 * <p>Each variant reports its own {@link #type()} so a {@link CommandArgumentData} can be checked
 * against a requested {@link CommandArgumentType} without unwrapping it. Note that
 * {@link ArgumentTarget} and {@link ArgumentDirection} are separate variants even though C stored
 * both in the union's single {@code int}: they mean different things (a target code versus a
 * movement direction), and the split keeps that distinction honest.
 *
 * @author Rowan Crowther
 */
public sealed interface CommandArgumentData permits ArgumentString, ArgumentChoice, ArgumentItem, ArgumentNumber,
        ArgumentDirection, ArgumentTarget, ArgumentPoint {
    /**
     * The argument-type tag corresponding to this variant, so a value can be matched against a
     * requested type without a downcast.
     *
     * @return this variant's {@link CommandArgumentType}
     */
    CommandArgumentType type();
}
