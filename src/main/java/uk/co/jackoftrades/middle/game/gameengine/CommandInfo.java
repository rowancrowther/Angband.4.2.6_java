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

package uk.co.jackoftrades.middle.game.gameengine;

import uk.co.jackoftrades.middle.game.enums.CommandCode;

/**
 * One row of the command dispatch table: a command code wired to the handler that performs it,
 * plus the flags governing how it repeats and spends energy - the port of C's
 * {@code struct command_info} (cmd-core.c).
 *
 * <p>{@code CommandProcessor} holds a table of these keyed by {@link #command()}. When a command
 * is executed the engine finds its row, applies the repeat/energy rules, then invokes
 * {@link #function()}. A {@code null} {@link #function()} means the code is recognised but not
 * dispatched here (handled upstream instead), mirroring the {@code NULL} handlers in C's
 * {@code game_cmds[]}.
 *
 * @param command          the command this row describes
 * @param verb             a short human-readable description of the action (used by the UI)
 * @param function         the handler to run, or {@code null} if the command is dispatched
 *                         elsewhere
 * @param repeatAllowed    whether the command may be repeated
 * @param canUseEnergy     whether performing the command consumes a game turn's energy
 * @param autoRepeatNumber the default repeat count applied when the command auto-repeats
 *                         ({@code 0} for none)
 * @author Rowan Crowther
 */
public record CommandInfo(CommandCode command,
                          String verb,
                          CommandHandlerFunction function,
                          boolean repeatAllowed,
                          boolean canUseEnergy,
                          int autoRepeatNumber) {


}
