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

import uk.co.jackoftrades.middle.cave.Loc;
import uk.co.jackoftrades.middle.game.enums.CommandArgumentType;
import uk.co.jackoftrades.middle.game.gameengine.CommandArgumentData;

/**
 * A command-argument value holding a grid location - the {@code arg_POINT} variant of
 * {@link CommandArgumentData}, from the union's {@code point} field in C.
 *
 * <p>This is the one union member C stored by value as a {@code struct loc} rather than as an
 * {@code int} or pointer; the port carries the equivalent {@link Loc}, a coordinate on the level.
 *
 * @param value the grid location
 * @author Rowan Crowther
 */
public record ArgumentPoint(Loc value) implements CommandArgumentData {
    @Override
    public CommandArgumentType type() {
        return CommandArgumentType.arg_POINT;
    }
}
