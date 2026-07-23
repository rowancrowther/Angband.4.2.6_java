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
 * A command-argument value holding a plain number - the {@code arg_NUMBER} variant of
 * {@link CommandArgumentData}, from the union's {@code number} field in C.
 *
 * <p>A general-purpose numeric argument such as a quantity or count. It differs from
 * {@link ArgumentChoice} only in intent: a number is a magnitude, whereas a choice is a selection
 * index into some set of options.
 *
 * @param value the numeric value
 * @author Rowan Crowther
 */
public record ArgumentNumber(int value) implements CommandArgumentData {
    public CommandArgumentType type() {
        return CommandArgumentType.arg_NUMBER;
    }
}
