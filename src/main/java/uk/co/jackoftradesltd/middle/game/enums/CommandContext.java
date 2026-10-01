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

package uk.co.jackoftradesltd.middle.game.enums;

/**
 * The situation a command is issued in - the port of C's {@code enum cmd_context} ({@code cmd-core.h}).
 *
 * <p>The same key can mean different things depending on where the player is, so every command
 * carries the context it was issued in, and the queue is drained for one context at a time: the
 * main loop pops {@link #CTX_GAME}, birth executes {@link #CTX_BIRTH}, the store menu pops
 * {@link #CTX_STORE}, and the death-screen spoiler menu executes {@link #CTX_DEATH}.
 * {@link #CTX_INIT} is the placeholder C gives a blank command, not a screen of its own.
 *
 * <p>The five constants match C in name and order, so the ordinals equal C's integer values
 * ({@code CTX_INIT} is 0). Nothing in the port depends on that, but it keeps cross-referencing the
 * C source painless.
 *
 * <p>coded on 2026-10-01 / commented in full on 2026-10-01
 *
 * @author Rowan Crowther
 */
public enum CommandContext {
    /**
     * The default context of a blank command: C's {@code last_command} and the command built by
     * {@code cmdq_push_repeat} both start here. No queue is ever drained for it.
     */
    CTX_INIT,
    /**
     * Character creation: {@code player-birth.c} runs {@code cmdq_execute(CTX_BIRTH)}.
     */
    CTX_BIRTH,
    /** Normal play: {@code game-world.c} pops this context once per game turn. */
    CTX_GAME,
    /** Shopping: {@code ui-store.c} pops this context while the store menu is open. */
    CTX_STORE,
    /** The end-of-character screen: {@code ui-spoil.c} executes this once the player is dead. */
    CTX_DEATH
}
