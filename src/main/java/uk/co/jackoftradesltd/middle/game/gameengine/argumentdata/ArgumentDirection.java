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
 * A command-argument value holding a movement direction - the {@code arg_DIRECTION} variant of
 * {@link CommandArgumentData}, from the {@code int direction} member of C's {@code union
 * cmd_arg_data} (cmd-core.h). {@code Command.setArgDirection} and {@code Command.getArgDirection}
 * are the ports of {@code cmd_set_arg_direction} and {@code cmd_get_arg_direction} that wrap and
 * unwrap it, and {@code Command.getDirection} is the port of {@code cmd_get_direction}, the one C
 * reader that goes on to judge the stored value.
 *
 * <p>Where C stored the direction as a raw {@code int}, the port carries a {@link DirectionEnum},
 * so the argument's meaning is self-describing. The record is immutable and its only check is
 * that the direction is not {@code null}: C's {@code cmd_set_arg_direction} stored the int
 * unchecked, so every {@link DirectionEnum} constant is storable, and it is the consumer that
 * decides whether a value is usable. {@code cmd_get_direction} treats a stored 5 as "no direction
 * given" and prompts instead, so {@link DirectionEnum#DIR_NONE} and {@link DirectionEnum#DIR_TARGET}
 * are values this record can hold but that reader will not accept. A {@code null} is not a legal
 * value - C's int has no such state - so the constructor refuses it with an
 * {@link IllegalArgumentException}, and unset is expressed by the argument being absent from the
 * command, not by a {@code null} here.
 *
 * <p>C gives {@code DIR_NONE} and {@code DIR_TARGET} the same value 5, so an int read back from a
 * stored direction could not say which of the two was meant. {@link DirectionEnum} keeps them as
 * separate constants, and this record preserves whichever was stored.
 *
 * <p>Contrast {@link ArgumentTarget}, which C kept in the same union field ({@code
 * cmd_set_arg_target} writes {@code data.direction}) but which denotes a target code rather than a
 * plain direction. The two are different variants with different tags, so reading one as the other
 * is a type mismatch rather than a coincidence of storage, even though both hold a {@link
 * DirectionEnum}.
 *
 * <p>Class ArgumentDirection coded before 261012, commented in full on 261012.
 *
 * @param value the movement direction, stored as given
 * @author Rowan Crowther
 */
public record ArgumentDirection(DirectionEnum value) implements CommandArgumentData {
    /**
     * Logs the refusal of a {@code null} direction before the constructor throws.
     *
     * <p>Field logger coded before 261012, commented in full on 261012.
     */
    private static final Logger logger = LogManager.getLogger(ArgumentDirection.class);

    /**
     * Reports the {@code arg_DIRECTION} tag for this variant, the tag C's {@code
     * cmd_set_arg_direction} stores beside the {@code direction} member and {@code
     * cmd_get_arg_direction} checks before it reads it. Every instance returns the same tag
     * whatever its value, and never {@link CommandArgumentType#arg_TARGET}, which shares the union
     * member in C.
     *
     * <p>Function type coded before 261012, commented in full on 261012.
     *
     * @return {@link CommandArgumentType#arg_DIRECTION}
     */
    @Override
    public CommandArgumentType type() {
        return CommandArgumentType.arg_DIRECTION;
    }

    /**
     * Returns a new record holding the same direction, the port of the per-argument step of C's
     * {@code cmd_copy}. C copies the whole command by struct assignment and deep-copies only the
     * {@code arg_STRING} arguments; a direction is a plain int, so the assignment alone gives an
     * independent value. The record is immutable and an enum constant is a singleton, so the new
     * instance shares no mutable state with this one and equals it.
     *
     * <p>Function copy coded before 261012, commented in full on 261012.
     *
     * @return a new {@code ArgumentDirection} with the same {@link #value()}
     */
    @Override
    public CommandArgumentData copy() {
        return new ArgumentDirection(value);
    }

    /**
     * Builds a direction argument holding {@code value}. C's {@code cmd_set_arg_direction} stores
     * an {@code int}, which cannot be null, so the port refuses a {@code null} {@link DirectionEnum}
     * rather than keeping a value that {@code Command.getArgDirection} could not hand back: the
     * refusal is logged at fatal level and thrown, before anything is stored. Every constant,
     * {@link DirectionEnum#DIR_UNKNOWN} and the two 5s ({@code DIR_NONE}, {@code DIR_TARGET})
     * included, is accepted unchecked, as C accepts any int. {@link #copy()} goes through this
     * constructor and so cannot fail, as {@link #value()} is never {@code null}.
     *
     * <p>Constructor ArgumentDirection coded on 261012, commented in full on 261012.
     *
     * @param value the movement direction, never {@code null}
     * @throws IllegalArgumentException if {@code value} is {@code null}
     */
    public ArgumentDirection(DirectionEnum value) {
        if (value != null) {
            this.value = value;
        } else {
            String msg = "Illegal argument value. Expected DirectionEnum, obtained null";
            logger.fatal(msg);
            throw new IllegalArgumentException(msg);
        }
    }
}
