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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.jackoftradesltd.middle.cave.enums.DirectionEnum;
import uk.co.jackoftradesltd.middle.game.enums.CommandArgumentType;
import uk.co.jackoftradesltd.middle.game.gameengine.CommandArgumentData;

/**
 * A command-argument value holding a target - the {@code arg_TARGET} variant of
 * {@link CommandArgumentData}. {@code Command.setArgTarget} and {@code Command.getArgTarget} are
 * the ports of {@code cmd_set_arg_target} and {@code cmd_get_arg_target} (cmd-core.c) that wrap
 * and unwrap it, and {@code Command.getTarget} is the port of {@code cmd_get_target}, the one C
 * reader that goes on to judge the stored value.
 *
 * <p>C had no dedicated union member for this: {@code cmd_set_arg_target} wrote the target into the
 * union's {@code direction} field, so a target and a direction physically shared one {@code int}.
 * The value is a target <em>code</em>, not coordinates - it can be a genuine direction, or a
 * sentinel meaning "use the current health-bar target" - which is why it stays a distinct variant
 * from {@link ArgumentDirection} despite the shared storage in C. C's own comment asks whether the
 * two should be unified and whether {@code DIR_TARGET} should be abolished in favour of a struct
 * carrying the target's details; the port answers the first by keeping them apart.
 *
 * <p>Where C stored the code as a raw {@code int}, the port carries a {@link DirectionEnum}. The
 * record is immutable and its only check is that the value is not {@code null}, because C's int
 * has no such state; the constructor refuses it with an {@link IllegalArgumentException}, and unset
 * is expressed by the argument being absent from the command. Every constant is storable, as
 * {@code cmd_set_arg_target} stored the int unchecked. {@code cmd_get_target} is the judge: it
 * rejects a stored {@link DirectionEnum#DIR_UNKNOWN} and prompts again, and accepts a stored 5
 * ({@link DirectionEnum#DIR_TARGET} or {@link DirectionEnum#DIR_NONE}, the same value in C) only
 * while {@code target_okay()} holds. Contrast {@code cmd_get_direction}, which treats a stored 5 as
 * "no direction given".
 *
 * <p>Class ArgumentTarget coded before 261012, commented in full on 261012.
 *
 * @param value the target code, never {@code null}
 * @author Rowan Crowther
 */
public record ArgumentTarget(DirectionEnum value) implements CommandArgumentData {
    /**
     * Logs the refusal of a {@code null} target code before the constructor throws.
     *
     * <p>Field logger coded before 261012, commented in full on 261012.
     */
    private static final Logger logger = LogManager.getLogger(ArgumentTarget.class);

    /**
     * Reports the {@code arg_TARGET} tag for this variant, the tag C's {@code cmd_set_arg_target}
     * stores beside the {@code direction} member and {@code cmd_get_arg_target} checks before it
     * reads it. Every instance returns the same tag whatever its value, and never
     * {@link CommandArgumentType#arg_DIRECTION}, which shares the union member in C.
     *
     * <p>Function type coded before 261012, commented in full on 261012.
     *
     * @return {@link CommandArgumentType#arg_TARGET}
     */
    @Override
    public CommandArgumentType type() {
        return CommandArgumentType.arg_TARGET;
    }

    /**
     * Returns a new record holding the same target code, the port of the per-argument step of C's
     * {@code cmd_copy}. C copies the whole command by struct assignment and deep-copies only the
     * {@code arg_STRING} arguments; a target is a plain int, so the assignment alone gives an
     * independent value. The record is immutable and an enum constant is a singleton, so the new
     * instance shares no mutable state with this one and equals it. It goes through the
     * constructor and cannot fail, as {@link #value()} is never {@code null}.
     *
     * <p>Function copy coded before 261012, commented in full on 261012.
     *
     * @return a new {@code ArgumentTarget} with the same {@link #value()}
     */
    @Override
    public CommandArgumentData copy() {
        return new ArgumentTarget(value);
    }

    /**
     * Builds a target argument holding {@code value}. C's {@code cmd_set_arg_target} stores an
     * {@code int} in {@code data.direction}, which cannot be null, so the port refuses a
     * {@code null} {@link DirectionEnum} rather than keeping a value that
     * {@code Command.getArgTarget} could not hand back: the refusal is logged at fatal level and
     * thrown, before anything is stored. Every constant is accepted unchecked, as C accepts any
     * int, including {@link DirectionEnum#DIR_UNKNOWN}, which {@code cmd_get_target} later treats
     * as "ask again", and the two 5s ({@code DIR_TARGET}, {@code DIR_NONE}), which it checks
     * against {@code target_okay()}. {@link #copy()} goes through this constructor and so cannot
     * fail, as {@link #value()} is never {@code null}.
     *
     * <p>Constructor ArgumentTarget coded on 261012, commented in full on 261012.
     *
     * @param value the target code, never {@code null}
     * @throws IllegalArgumentException if {@code value} is {@code null}
     */
    public ArgumentTarget(DirectionEnum value) {
        if (value != null)
            this.value = value;
        else {
            String msg = "Illegal argument value. Expected a DirectionEnum, obtained null";
            logger.fatal(msg);
            throw new IllegalArgumentException(msg);
        }
    }
}
