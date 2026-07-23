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

package uk.co.jackoftrades.middle.game.enums;

/**
 * The kind of value a command argument carries - the port of C's {@code enum cmd_arg_type}
 * (cmd-core.h). It is the discriminator for a command argument: it says which flavour of data the
 * argument holds and therefore how it may be read back.
 *
 * <p>In C this tag sat beside a {@code union cmd_arg_data}, and reading the wrong union member for
 * the tag was undefined behaviour guarded only by a runtime type check. The port replaces the
 * union with a sealed {@code CommandArgumentData} hierarchy, so each constant here corresponds to
 * one permitted variant and the compiler enforces the pairing.
 *
 * <p>Note the set is not one-to-one with the data variants that existed in C's union: there are
 * eight tags but the union had six fields. {@link #arg_NONE} carries no payload at all, and
 * {@link #arg_TARGET} and {@link #arg_DIRECTION} are distinct <em>meanings</em> that C stored in
 * the same {@code int} field - a target code versus a movement direction. Keeping them as separate
 * tags preserves that distinction of intent even where the underlying storage coincides.
 *
 * @author Rowan Crowther
 */
public enum CommandArgumentType {
    arg_NONE,
    arg_STRING,
    arg_CHOICE,
    arg_ITEM,
    arg_NUMBER,
    arg_DIRECTION,
    arg_TARGET,
    arg_POINT
}
