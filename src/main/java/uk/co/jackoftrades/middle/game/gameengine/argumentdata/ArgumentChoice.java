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

package uk.co.jackoftrades.middle.game.gameengine.argumentdata;

import uk.co.jackoftrades.middle.game.enums.CommandArgumentType;
import uk.co.jackoftrades.middle.game.gameengine.CommandArgumentData;

/**
 * A command-argument value holding a menu/selection index - the {@code arg_CHOICE} variant of
 * {@link CommandArgumentData}, from the union's {@code choice} field in C.
 *
 * <p>C flagged this type as a hack used only in a couple of places (resting and birth choices):
 * it is a bare selection number whose meaning depends entirely on the command consuming it.
 *
 * @param value the chosen index
 * @author Rowan Crowther
 */
public record ArgumentChoice(int value) implements CommandArgumentData {
    public CommandArgumentType type() {
        return CommandArgumentType.arg_CHOICE;
    }
}
