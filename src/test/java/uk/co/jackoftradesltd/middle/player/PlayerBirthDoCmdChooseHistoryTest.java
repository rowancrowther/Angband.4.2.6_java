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

package uk.co.jackoftradesltd.middle.player;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.co.jackoftradesltd.middle.game.enums.CommandCode;
import uk.co.jackoftradesltd.middle.game.enums.CommandContext;
import uk.co.jackoftradesltd.middle.game.gameengine.Command;
import uk.co.jackoftradesltd.middle.game.gameengine.GameState;
import uk.co.jackoftradesltd.testsupport.SeededPlayerRegistry;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Tests {@link PlayerBirth#doCmdChooseHistory}, the port of C's {@code do_cmd_choose_history}
 * ({@code player-birth.c:1244-1255}).
 *
 * <p>The C is short:
 *
 * <pre>{@code
 * void do_cmd_choose_history(struct command *cmd)
 * {
 *         const char *str;
 *
 *         /* Forget the old history *\/
 *         if (player->history)
 *                 string_free(player->history);
 *
 *         /* Get the new history *\/
 *         cmd_get_arg_string(cmd, "history", &str);
 *         player->history = string_make(str);
 * }
 * }</pre>
 *
 * <p><b>The deliberate divergence.</b> C never checks {@code cmd_get_arg_string}'s return value, so
 * on the arg-missing path {@code str} is left uninitialised and {@code string_make} would read
 * through a garbage pointer - undefined behaviour, not a designed fallback. The port guards it
 * explicitly with {@code if (history.isEmpty()) return;} rather than following C's fallthrough, the
 * same shape used by the sibling command {@link PlayerBirth#doCmdChooseName}.
 * {@link #argMissingDoesNothing} pins that guard down.
 *
 * <p><b>The free guard.</b> C frees the old string first, guarded by {@code if (player->history)},
 * before allocating the replacement. {@link Player#setHistoryBirth} already documents absorbing that
 * guard - the port simply replaces the field reference - so this suite only confirms the value comes
 * through unchanged, both from empty and from a previously-set history.
 *
 * <p>Class PlayerBirthDoCmdChooseHistoryTest coded on 260907, commented in full on 260907.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class PlayerBirthDoCmdChooseHistoryTest {

    private Player player;
    private Player realPlayer;

    private static Command chooseHistoryCommand(String history) {
        Command cmd = new Command(CommandContext.CTX_BIRTH, CommandCode.CMD_HISTORY_CHOICE, 0, 0,
                new ArrayList<>());
        if (history != null) cmd.setArgString("history", history);
        return cmd;
    }

    @BeforeEach
    void seedFixture() {
        player = new Player();
        realPlayer = GameState.getPlayer();
        GameState.setPlayer(player);
    }

    @AfterEach
    void restoreFixture() {
        GameState.setPlayer(realPlayer);
    }

    /**
     * The ordinary path: a history string is stored unchanged, with no truncation - unlike
     * {@code full_name}, C's {@code player->history} is a heap pointer, not a fixed-size buffer.
     */
    @Test
    @DisplayName("sets the player's history from the command argument")
    void ordinaryHistoryIsSet() {
        PlayerBirth.doCmdChooseHistory(chooseHistoryCommand("Born in a small village."));

        assertEquals("Born in a small village.", player.getHistoryBirth());
    }

    /**
     * C frees the old {@code player->history} before allocating the new one; the port replaces the
     * field reference instead. Either way, a fresh command argument overwrites whatever history was
     * already set.
     */
    @Test
    @DisplayName("overwrites a previously-set history rather than appending or leaking it")
    void previousHistoryIsReplaced() {
        player.setHistoryBirth("Old text.");

        PlayerBirth.doCmdChooseHistory(chooseHistoryCommand("New text."));

        assertEquals("New text.", player.getHistoryBirth());
    }

    /**
     * The port's deliberate divergence from C: with the {@code "history"} arg unset,
     * {@code doCmdChooseHistory} returns before touching the player at all, unlike C, which would fall
     * through to {@code string_make} with an uninitialised source pointer. This test exists to pin
     * down what the port does about it on paper.
     */
    @Test
    @DisplayName("with the history arg unset, does nothing rather than following C's fallthrough")
    void argMissingDoesNothing() {
        PlayerBirth.doCmdChooseHistory(chooseHistoryCommand(null));

        assertNull(player.getHistoryBirth(), "history must be untouched");
    }
}
