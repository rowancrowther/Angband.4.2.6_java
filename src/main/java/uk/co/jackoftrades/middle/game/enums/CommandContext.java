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

package uk.co.jackoftrades.middle.game.enums;

/**
 * The situation a command is issued in - the port of C's {@code enum cmd_context} (cmd-core.h).
 *
 * <p>The same key can mean different things depending on where the player is, so the input layer
 * tags each command with the context that produced it and the game loop pops commands for the
 * context it is currently servicing (the main loop runs with {@link #CTX_GAME}). The five states
 * span the whole session: {@link #CTX_INIT} for the splash screen, {@link #CTX_BIRTH} for
 * character creation, {@link #CTX_GAME} for normal play, {@link #CTX_STORE} while shopping, and
 * {@link #CTX_DEATH} for the end-of-character screen.
 *
 * @author Rowan Crowther
 */
public enum CommandContext {
    CTX_INIT,
    CTX_BIRTH,
    CTX_GAME,
    CTX_STORE,
    CTX_DEATH
}
