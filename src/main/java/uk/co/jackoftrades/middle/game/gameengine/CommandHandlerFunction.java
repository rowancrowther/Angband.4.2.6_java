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

/**
 * The code that performs a command - the port of C's {@code cmd_handler_fn} function-pointer type
 * (cmd-core.h, {@code typedef void (*cmd_handler_fn)(struct command *cmd)}).
 *
 * <p>Where C stored a bare function pointer in each dispatch-table row, the port stores an
 * implementation of this functional interface, supplied as a lambda or method reference. Each
 * handler receives the {@link Command} being executed and pulls whatever arguments it needs from
 * it (some handlers ignore it entirely and act on global state). The dispatch table references
 * handlers it does not own - they live with their respective subsystems.
 *
 * @author Rowan Crowther
 */
@FunctionalInterface
public interface CommandHandlerFunction {
    /**
     * Performs the command.
     *
     * @param command the command being executed, carrying its context and arguments
     */
    void handle(Command command);
}
