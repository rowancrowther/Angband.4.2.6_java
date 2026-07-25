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

import uk.co.jackoftrades.middle.game.enums.CommandContext;

/**
 * The seam through which the game loop asks the user interface to fetch a command - the port of C's
 * {@code cmd_get_hook} function pointer (cmd-core.c). A front-end installs its implementation and the
 * loop calls it once per turn to top the {@link CommandQueue} up before draining it.
 *
 * <p>This is a different seam from {@link uk.co.jackoftrades.middle.gameinput.GameInput}: that one
 * fetches a single <em>argument</em> for a command already being carried out (a direction, an item, a
 * spell); this one fetches a <em>whole command</em>. They fire at different points in the same turn -
 * this first, to obtain the command, then the argument seam as the command's handler runs.
 *
 * <p>The method returns C's {@code errr} status (0 for success, non-zero for "no command obtained"),
 * but that return is largely vestigial - as in C, the loop ignores it and treats an empty queue as
 * the real "nothing to do" signal. The actual work is a side effect: pushing the fetched command onto
 * the {@link CommandQueue}. That queue is passed in rather than read from a global (C kept its
 * {@code cmd_queue} at file scope), matching the choice made in
 * {@link CommandProcessor#processCommand} - it keeps the getter stateless and lets it push onto
 * whichever queue the caller is currently draining, even across a game restart.
 *
 * <p>The live implementation is held by {@link CommandGetterHolder} and can be swapped: a real
 * front-end for play, a fake for tests.
 *
 * @author Rowan Crowther
 */
public interface CommandGetter {

    /**
     * Fetches a command from the UI and pushes it onto {@code queue} - the port of the call through
     * C's {@code cmd_get_hook}.
     *
     * @param context the situation the command is being fetched for (the game loop passes
     *                {@link CommandContext#CTX_GAME})
     * @param queue   the queue to push the fetched command onto
     * @return C's {@code errr} status: 0 if a command was obtained, non-zero if none was
     */
    int getCommand(CommandContext context, CommandQueue queue);
}
