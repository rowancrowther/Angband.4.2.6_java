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

import uk.co.jackoftradesltd.middle.game.enums.CommandArgumentType;
import uk.co.jackoftradesltd.middle.game.gameengine.CommandArgumentData;

/**
 * A command-argument value holding a plain number - the {@code arg_NUMBER} variant of
 * {@link CommandArgumentData}, from the {@code int number} member of C's {@code union
 * cmd_arg_data} (cmd-core.h). {@code Command.setArgNumber} and {@code Command.getArgNumber} are
 * the ports of {@code cmd_set_arg_number} and {@code cmd_get_arg_number} that wrap and unwrap it,
 * and {@code Command.getQuantity} is the port of {@code cmd_get_quantity}, which reads it and
 * prompts when it is absent.
 *
 * <p>A general-purpose numeric argument. The values C stores under this tag are quantities
 * ({@code "quantity"} for drops, wizard-mode item creation and simulations), levels and depths,
 * ranges, tvals, and indices into a kind, monster or curse list. It differs from
 * {@link ArgumentChoice} only in the tag: C's own comment on {@code arg_CHOICE} calls that type a
 * hack that is used where an integer or boolean argument "did not seem to be a good match for
 * 'number' arguments", so a number is the argument that is a magnitude or an index in its own
 * right. An {@code ArgumentNumber(3)} and an {@code ArgumentChoice(3)} are different variants, so
 * reading one as the other is a type mismatch.
 *
 * <p>The record is immutable, and the stored int is any value at all. C stores it unchecked, so
 * negative values and the extremes of the int range round-trip, and any positivity rule belongs to
 * the consumer (in C, {@code cmd_get_quantity} only accepts a prompted amount above zero, and does
 * not check a value already stored).
 *
 * <p>Class ArgumentNumber coded before 261012, commented in full on 261012.
 *
 * @param value the numeric value, stored as given
 * @author Rowan Crowther
 */
public record ArgumentNumber(int value) implements CommandArgumentData {
    /**
     * Reports the {@code arg_NUMBER} tag for this variant, the tag C's {@code cmd_set_arg_number}
     * stores beside the {@code number} member and {@code cmd_get_arg_number} checks before it
     * reads it. Every instance returns the same tag whatever its value.
     *
     * <p>Function type coded before 261012, commented in full on 261012.
     *
     * @return {@link CommandArgumentType#arg_NUMBER}
     */
    @Override
    public CommandArgumentType type() {
        return CommandArgumentType.arg_NUMBER;
    }

    /**
     * Returns a new record holding the same number, the port of the per-argument step of C's
     * {@code cmd_copy}. C copies the whole command by struct assignment and deep-copies only the
     * {@code arg_STRING} arguments; a number is a plain int, so the assignment alone gives an
     * independent value. The record is immutable, so the new instance shares no state with this one
     * and equals it.
     *
     * <p>Function copy coded before 261012, commented in full on 261012.
     *
     * @return a new {@code ArgumentNumber} with the same {@link #value()}
     */
    @Override
    public CommandArgumentData copy() {
        return new ArgumentNumber(value);
    }
}
