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
 * A command-argument value holding a target - the {@code arg_TARGET} variant of
 * {@link CommandArgumentData}.
 *
 * <p>C had no dedicated union member for this: {@code cmd_set_arg_target} wrote the target into the
 * union's {@code direction} field, so a target and a direction physically shared one {@code int}.
 * The value is a target <em>code</em>, not coordinates - it can be a genuine direction, or a
 * sentinel meaning "use the current health-bar target" - which is why it stays a distinct variant
 * from {@link ArgumentDirection} despite the shared storage in C.
 *
 * @param value the target code
 * @author Rowan Crowther
 */
public record ArgumentTarget(DirectionEnum value) implements CommandArgumentData {
    @Override
    public CommandArgumentType type() {
        return CommandArgumentType.arg_TARGET;
    }
}
