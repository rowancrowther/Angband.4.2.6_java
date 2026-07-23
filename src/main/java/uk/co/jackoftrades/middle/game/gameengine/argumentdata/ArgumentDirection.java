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

import uk.co.jackoftrades.middle.cave.enums.DirectionEnum;
import uk.co.jackoftrades.middle.game.enums.CommandArgumentType;
import uk.co.jackoftrades.middle.game.gameengine.CommandArgumentData;

/**
 * A command-argument value holding a movement direction - the {@code arg_DIRECTION} variant of
 * {@link CommandArgumentData}, from the union's {@code direction} field in C.
 *
 * <p>Where C stored the direction as a raw {@code int}, the port carries a {@link DirectionEnum},
 * so the argument's meaning is self-describing. Contrast {@link ArgumentTarget}, which C kept in
 * the same union field but which denotes a target rather than a plain direction.
 *
 * @param value the movement direction
 * @author Rowan Crowther
 */
public record ArgumentDirection(DirectionEnum value) implements CommandArgumentData {
    @Override
    public CommandArgumentType type() {
        return CommandArgumentType.arg_DIRECTION;
    }
}
