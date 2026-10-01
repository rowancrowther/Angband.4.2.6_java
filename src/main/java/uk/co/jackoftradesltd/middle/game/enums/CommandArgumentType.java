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

package uk.co.jackoftradesltd.middle.game.enums;

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
 * tags preserves that distinction of intent even where the underlying storage coincides. C's own
 * source carries an {@code XXX} asking whether {@code arg_TARGET} should be unified with
 * {@code arg_DIRECTION}; the port keeps them apart.
 *
 * <p>The declaration order matches C exactly ({@code arg_NONE} is 0, {@code arg_STRING} is 1, and
 * so on up to {@code arg_POINT} at 7), so {@link #ordinal()} agrees with the C integer values.
 * Nothing in the port depends on that, but it keeps cross-referencing the C source painless.
 *
 * <p>commented in full on 2026-10-01
 *
 * @author Rowan Crowther
 */
public enum CommandArgumentType {
    /**
     * No argument present. C's {@code arg_NONE = 0}: the state of an unused argument slot, and the
     * type {@code cmd_release()} resets a string argument to after freeing it. It carries no
     * payload, so no {@code CommandArgumentData} variant reports this tag.
     */
    arg_NONE,
    /**
     * A text value - C's {@code arg_STRING = 1}, held in the union's {@code const char *string}.
     * Paired with {@code ArgumentString}.
     */
    arg_STRING,
    /**
     * An index chosen from a menu or list - C's {@code arg_CHOICE}, held in the union's {@code int
     * choice}. Paired with {@code ArgumentChoice}.
     */
    arg_CHOICE,
    /**
     * A game object - C's {@code arg_ITEM}, held in the union's {@code struct object *obj}. Paired
     * with {@code ArgumentItem}.
     */
    arg_ITEM,
    /**
     * A plain count or quantity - C's {@code arg_NUMBER}, held in the union's {@code int number}.
     * Paired with {@code ArgumentNumber}.
     */
    arg_NUMBER,
    /**
     * A movement or aiming direction - C's {@code arg_DIRECTION}, held in the union's {@code int
     * direction}. Paired with {@code ArgumentDirection}.
     */
    arg_DIRECTION,
    /**
     * A target code - C's {@code arg_TARGET}. C has no union member of its own for it:
     * {@code cmd_set_arg_target()} and {@code cmd_get_arg_target()} both go through
     * {@code data.direction}. Paired with {@code ArgumentTarget}.
     */
    arg_TARGET,
    /**
     * A grid location - C's {@code arg_POINT}, held in the union's {@code struct loc point}.
     * Paired with {@code ArgumentPoint}.
     */
    arg_POINT
}
