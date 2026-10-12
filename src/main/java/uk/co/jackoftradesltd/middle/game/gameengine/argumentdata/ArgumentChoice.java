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
 * A command-argument value holding a menu/selection index - the {@code arg_CHOICE} variant of
 * {@link CommandArgumentData}, from the {@code int choice} member of C's {@code union
 * cmd_arg_data} (cmd-core.h). {@code Command.setArgChoice} and {@code Command.getArgChoice}
 * are the ports of {@code cmd_set_arg_choice} and {@code cmd_get_arg_choice} that wrap and
 * unwrap it.
 *
 * <p>C marks this type with an {@code XXX} comment as a hack: it is a bare integer whose meaning
 * depends entirely on the command consuming it, and C wants each use to have a type of its own so
 * the data can be validated. The uses C lists in cmd-core.c are resting, birth choices, store
 * items, spells, the selection for an {@code EF_SELECT} effect (dragon's breath wands, potions
 * and dragon armour with several breath types), and several debugging commands whose integer or
 * boolean arguments did not suit a number argument. The port keeps the single variant, so none of
 * those is told apart from the others by type.
 *
 * <p>The record is immutable, and the stored int is any value at all: negative values are legal
 * and carry no check here, because C stored the int unchecked and it is the consumer that gives
 * a value its meaning. The difference from {@link ArgumentNumber} is the tag only. A number
 * argument holding 3 and a choice argument holding 3 are different variants, as C's
 * {@code arg_NUMBER} and {@code arg_CHOICE} tags made them different, so reading one as the other
 * is a type mismatch rather than a coincidence of storage.
 *
 * <p>Class ArgumentChoice coded before 261012, commented in full on 261012.
 *
 * @param value the chosen index, stored as given
 * @author Rowan Crowther
 */
public record ArgumentChoice(int value) implements CommandArgumentData {
    /**
     * Reports the {@code arg_CHOICE} tag for this variant, the tag C's {@code cmd_set_arg_choice}
     * stores beside the {@code choice} member and {@code cmd_get_arg_choice} checks before it
     * reads it. Every instance returns the same tag whatever its value.
     *
     * <p>Function type coded before 261012, commented in full on 261012.
     *
     * @return {@link CommandArgumentType#arg_CHOICE}
     */
    public CommandArgumentType type() {
        return CommandArgumentType.arg_CHOICE;
    }

    /**
     * Returns a new record holding the same index, the port of the per-argument step of C's
     * {@code cmd_copy}. C copies the whole command by struct assignment and deep-copies only the
     * {@code arg_STRING} arguments; a choice is a plain int, so the assignment alone gives an
     * independent value. The record is immutable, so the new instance shares no state with this one
     * and equals it.
     *
     * <p>Function copy coded before 261012, commented in full on 261012.
     *
     * @return a new {@code ArgumentChoice} with the same {@link #value()}
     */
    @Override
    public CommandArgumentData copy() {
        return new ArgumentChoice(value);
    }
}
