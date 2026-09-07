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
 * Tests {@link PlayerBirth#doCmdChooseName}, the port of C's {@code do_cmd_choose_name}
 * ({@code player-birth.c:1234-1241}).
 *
 * <p>The C is short:
 *
 * <pre>{@code
 * void do_cmd_choose_name(struct command *cmd)
 * {
 *         const char *str;
 *         cmd_get_arg_string(cmd, "name", &str);
 *
 *         /* Set player name *\/
 *         my_strcpy(player->full_name, str, sizeof(player->full_name));
 * }
 * }</pre>
 *
 * <p><b>The deliberate divergence.</b> C never checks {@code cmd_get_arg_string}'s return value, so
 * on the arg-missing path {@code str} is left uninitialised and {@code my_strcpy} would read through
 * a garbage pointer - undefined behaviour, not a designed fallback. Every real producer of
 * {@code CMD_NAME_CHOICE} in the C tree ({@code ui-birth.c:1321}) sets the arg before the command
 * runs, and the port's own caller, {@link PlayerBirth#playerMakeSimple}, does the same
 * ({@code PlayerBirth.java:748}), so this path is unreachable in practice on either side. The port
 * still guards it explicitly with {@code if (name.isEmpty()) return;} rather than following C's
 * fallthrough. {@link #argMissingDoesNothing} pins that guard down.
 *
 * <p><b>Truncation.</b> {@link Player#setFullName} already has its own suite covering the
 * 31-character {@code my_strcpy} cutover; this suite only confirms {@code doCmdChooseName} hands the
 * argument straight through to it, not that truncation itself is correct.
 *
 * <p>Class PlayerBirthDoCmdChooseNameTest coded on 260907, commented in full on 260907.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class PlayerBirthDoCmdChooseNameTest {

    private Player player;
    private Player realPlayer;

    private static Command chooseNameCommand(String name) {
        Command cmd = new Command(CommandContext.CTX_BIRTH, CommandCode.CMD_NAME_CHOICE, 0, 0,
                new ArrayList<>());
        if (name != null) cmd.setArgString("name", name);
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
     * The ordinary path: a short name, well under the 31-character cutover, is stored unchanged.
     */
    @Test
    @DisplayName("sets the player's full name from the command argument")
    void ordinaryNameIsSet() {
        PlayerBirth.doCmdChooseName(chooseNameCommand("Frodo"));

        assertEquals("Frodo", player.getFullName());
    }

    /**
     * {@code doCmdChooseName} hands the raw argument straight to {@link Player#setFullName}, so a
     * 32-character name comes out truncated to 31 - the same cutover {@code my_strcpy} applies against
     * {@code PLAYER_NAME_LEN} (32), confirming the wiring rather than re-proving the cutover itself.
     */
    @Test
    @DisplayName("delegates truncation to Player.setFullName for an over-length name")
    void overLengthNameIsTruncatedByDelegation() {
        String thirtyTwoChars = "a".repeat(32);

        PlayerBirth.doCmdChooseName(chooseNameCommand(thirtyTwoChars));

        assertEquals(thirtyTwoChars.substring(0, 31), player.getFullName());
    }

    /**
     * The port's deliberate divergence from C: with the {@code "name"} arg unset,
     * {@code doCmdChooseName} returns before touching the player at all, unlike C, which would fall
     * through to {@code my_strcpy} with an uninitialised source pointer. This path is otherwise
     * unreachable in practice, since every producer of {@code CMD_NAME_CHOICE} sets the arg first;
     * this test exists to pin down what the port does about it on paper.
     */
    @Test
    @DisplayName("with the name arg unset, does nothing rather than following C's fallthrough")
    void argMissingDoesNothing() {
        PlayerBirth.doCmdChooseName(chooseNameCommand(null));

        assertNull(player.getFullName(), "full name must be untouched");
    }
}
