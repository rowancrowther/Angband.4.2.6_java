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

package uk.co.jackoftradesltd.middle.game.gameengine.argumentdata;

import uk.co.jackoftradesltd.middle.cave.Loc;
import uk.co.jackoftradesltd.middle.game.enums.CommandArgumentType;
import uk.co.jackoftradesltd.middle.game.gameengine.CommandArgumentData;

import java.util.Objects;

/**
 * A command-argument value holding a grid location - the {@code arg_POINT} variant of
 * {@link CommandArgumentData}, from the {@code struct loc point} member of C's {@code union
 * cmd_arg_data} (cmd-core.h). {@code Command.setArgPoint} and {@code Command.getArgPoint} are the
 * ports of {@code cmd_set_arg_point} and {@code cmd_get_arg_point} (cmd-core.c) that wrap and
 * unwrap it.
 *
 * <p>This is the one union member C stored by value as a {@code struct loc} rather than as an
 * {@code int} or pointer; the port carries the equivalent {@link Loc}, a coordinate on the level.
 * A point is a position on the map, not a movement direction: {@link ArgumentDirection} holds the
 * latter, and an {@code ArgumentPoint} and an {@code ArgumentDirection} are different variants even
 * though both are used to aim a command.
 *
 * <p>The record is immutable, and so is its {@link Loc}, so a value read back with
 * {@code Command.getArgPoint} can be handed on without a defensive copy - where C's
 * {@code cmd_get_arg_point} copied the struct out by value. The stored coordinates are unchecked,
 * as in C: nothing here tests that the grid is inside the level, and {@code (0, 0)} is an ordinary
 * point rather than "no point". The record never holds a {@code null} {@link Loc}, which C's
 * by-value struct could not express: its constructor turns a {@code null} into {@link Loc#zero},
 * so {@link #value()} and {@link #copy()} need no guard for one.
 *
 * <p>Two points are equal when their locations are: the record's {@code equals} and
 * {@code hashCode} defer to {@link Loc#equals(Object)} and {@link Loc#hashCode()}, which compare
 * {@code x} and {@code y} as C's {@code loc_eq} (z-type.c) does. Identity of the {@link Loc}
 * object plays no part.
 *
 * <p>Class ArgumentPoint coded before 261012, commented in full on 261012, {@code null} location
 * stored as {@code Loc.zero} on 261012, equality note added on 261012.
 *
 * @param value the grid location; a {@code null} is stored as {@link Loc#zero}
 * @author Rowan Crowther
 */
public record ArgumentPoint(Loc value) implements CommandArgumentData {
    /**
     * Builds a point argument holding {@code value}. C's {@code cmd_set_arg_point} takes a
     * {@code struct loc} by value, which cannot be null, so the port stores {@link Loc#zero} for a
     * {@code null} argument rather than keeping a {@code null} that {@link #copy()} would later
     * fail on. This is the one place that choice is made: {@code Command.setArgPoint} passes its
     * argument straight in.
     *
     * <p>A stored {@link Loc#zero} is then an ordinary point at the origin, not a marker for "no
     * point" - the caller who passed {@code null} gets the origin back from
     * {@code Command.getArgPoint}.
     *
     * <p>Constructor ArgumentPoint coded on 261012, commented in full on 261012.
     *
     * @param value the grid location, or {@code null} for {@link Loc#zero}
     */
    public ArgumentPoint(Loc value) {
        this.value = Objects.requireNonNullElse(value, Loc.zero);
    }

    /**
     * Reports the {@code arg_POINT} tag for this variant, the tag C's {@code cmd_set_arg_point}
     * stores beside the {@code point} member and {@code cmd_get_arg_point} checks before it reads
     * it. Every instance returns the same tag whatever its location.
     *
     * <p>Function type coded before 261012, commented in full on 261012.
     *
     * @return {@link CommandArgumentType#arg_POINT}
     */
    @Override
    public CommandArgumentType type() {
        return CommandArgumentType.arg_POINT;
    }

    /**
     * Returns a new record holding an equal location, the port of the per-argument step of C's
     * {@code cmd_copy}. C copies the whole command by struct assignment ({@code *dest = *src}) and
     * deep-copies only the {@code arg_STRING} arguments; a {@code struct loc} is two plain ints
     * held by value, so the assignment alone gives an independent point. The port copies the
     * {@link Loc} through {@link Loc#copy()} as well, so the new record shares no reference with
     * this one even though neither can be mutated. The result equals this record. {@link #value()}
     * is never {@code null}, so the copy cannot fail.
     *
     * <p>Function copy coded before 261012, commented in full on 261012, no-{@code null} note added
     * on 261012.
     *
     * @return a new {@code ArgumentPoint} whose {@link #value()} equals this one's
     */
    @Override
    public CommandArgumentData copy() {
        return new ArgumentPoint(value.copy());
    }
}
