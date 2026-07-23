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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.jackoftrades.middle.game.enums.CommandCode;
import uk.co.jackoftrades.middle.game.enums.CommandContext;
import uk.co.jackoftrades.middle.player.Player;
import uk.co.jackoftrades.middle.player.PlayerUpkeep;
import uk.co.jackoftrades.middle.player.enums.TimedEffect;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests that {@link CommandProcessor}'s static command table is actually populated, and that a
 * representative sample of its rows carry the flags the C {@code game_cmds[]} table assigns them.
 *
 * <p>The reason this test exists at all is a bug that is invisible to the compiler:
 * {@code buildTable()} was populated through a bare {@code put(map, info)} call which - absent a
 * matching helper - silently resolved to {@code javax.swing.UIManager.put(Object, Object)}. That
 * compiles cleanly, returns without complaint, and writes every entry into Swing's global
 * defaults table instead of the {@link java.util.EnumMap}, leaving {@code gameCommands} empty and
 * {@link CommandProcessor#process} a permanent no-op. A type checker cannot catch this because
 * both arguments widen to {@code Object}; only asserting on the table's <em>contents</em> can.
 * So the first assertion here - that the table is non-empty - is the real guard, and the rest
 * pin the transcription so a future edit cannot quietly drop or mis-flag a command.
 *
 * <p>The table is a {@code private static} field, so it is read reflectively rather than through
 * an accessor added purely for testing; this keeps the production surface unchanged.
 *
 * @author Rowan Crowther
 */
class CommandProcessorTest {

    /**
     * Reads the private static {@code gameCommands} map out of {@link CommandProcessor} by
     * reflection. Class initialisation runs {@code buildTable()} as a side effect, so the map is
     * fully populated (or, under the bug, empty) by the time the field is read.
     *
     * @return the live command table
     * @throws ReflectiveOperationException if the field is renamed or its access is tightened,
     *                                      which should fail the test loudly rather than be swallowed
     */
    @SuppressWarnings("unchecked")
    private static Map<CommandCode, CommandInfo> table() throws ReflectiveOperationException {
        Field field = CommandProcessor.class.getDeclaredField("gameCommands");
        field.setAccessible(true);
        return (Map<CommandCode, CommandInfo>) field.get(null);
    }

    /**
     * The headline guard: the table must contain entries. Under the {@code UIManager.put} bug it
     * is empty, so this assertion is what turns the silent no-op into a visible failure.
     */
    @Test
    void tableIsPopulated() throws ReflectiveOperationException {
        assertFalse(table().isEmpty(), "command table is empty - buildTable() populated the wrong map");
    }

    /**
     * A spread of commands from every band of the enum (game, store, spoiler, wizard) must be
     * present, so the failure mode of populating only part of the table also gets caught.
     */
    @Test
    void representativeCommandsPresent() throws ReflectiveOperationException {
        Map<CommandCode, CommandInfo> table = table();
        for (CommandCode code : new CommandCode[]{
                CommandCode.CMD_WALK, CommandCode.CMD_OPEN, CommandCode.CMD_QUAFF,
                CommandCode.CMD_SELL, CommandCode.CMD_SPOIL_OBJ,
                CommandCode.CMD_WIZ_WIZARD_LIGHT, CommandCode.CMD_COMMAND_MONSTER}) {
            assertNotNull(table.get(code), code + " missing from command table");
        }
    }

    /**
     * Every row's key must match the {@link CommandInfo#command()} it stores. This catches the
     * class of bug where an entry is filed under the wrong code (e.g. a copy-paste slip), which a
     * simple {@code containsKey} check would miss.
     */
    @Test
    void keysMatchTheirCommandInfo() throws ReflectiveOperationException {
        table().forEach((code, info) ->
                assertEquals(code, info.command(),
                        "entry keyed by " + code + " holds CommandInfo for " + info.command()));
    }

    /**
     * Spot-checks the flags against the C {@code game_cmds[]} rows: {@code CMD_WALK} repeats and
     * uses energy with no auto-repeat; {@code CMD_OPEN} auto-repeats 99 times; and the pure debug
     * action {@code CMD_WIZ_WIZARD_LIGHT} does none of these. Verbs are checked too, since they
     * feed the UI's command descriptions.
     */
    @Test
    void flagsMatchCTable() throws ReflectiveOperationException {
        Map<CommandCode, CommandInfo> table = table();

        CommandInfo walk = table.get(CommandCode.CMD_WALK);
        assertEquals("walk", walk.verb());
        assertTrue(walk.repeatAllowed());
        assertTrue(walk.canUseEnergy());
        assertEquals(0, walk.autoRepeatNumber());

        CommandInfo open = table.get(CommandCode.CMD_OPEN);
        assertTrue(open.repeatAllowed());
        assertTrue(open.canUseEnergy());
        assertEquals(99, open.autoRepeatNumber());

        CommandInfo wizLight = table.get(CommandCode.CMD_WIZ_WIZARD_LIGHT);
        assertEquals("wizard light the level", wizLight.verb());
        assertFalse(wizLight.repeatAllowed());
        assertFalse(wizLight.canUseEnergy());
        assertEquals(0, wizLight.autoRepeatNumber());
    }

    /**
     * Codes that exist in the enum but deliberately have no {@code game_cmds[]} row in C must not
     * appear in the table. {@code CMD_NULL} is the "no command" sentinel; {@code CMD_BROWSE_SPELL}
     * and {@code CMD_IGNORE} are handled outside this dispatch table upstream. Pinning their
     * absence documents that the omission is intentional, not a gap to be "fixed" later.
     */
    @Test
    void enumOnlyCodesAreAbsent() throws ReflectiveOperationException {
        Map<CommandCode, CommandInfo> table = table();
        assertNull(table.get(CommandCode.CMD_NULL), "CMD_NULL should not be a dispatchable command");
        assertNull(table.get(CommandCode.CMD_BROWSE_SPELL), "CMD_BROWSE_SPELL has no game_cmds[] row in C");
        assertNull(table.get(CommandCode.CMD_IGNORE), "CMD_IGNORE has no game_cmds[] row in C");
    }

    // ------------------------------------------------------------------ processCommand

    /**
     * A player built fresh per test and registered as the current {@link GameState} player.
     */
    private Player player;

    /**
     * Registers a usable player as the current {@link GameState} player before each test.
     * {@code processCommand} reads {@link GameState#getPlayer()} and touches its timed-effect map
     * and upkeep - both {@code null} on a bare {@link Player}, so they are reflectively initialised
     * to empty here. See the matching helper in {@code CommandQueueTest}.
     */
    @BeforeEach
    void primePlayer() throws ReflectiveOperationException {
        player = new Player();

        Field timed = Player.class.getDeclaredField("timed");
        timed.setAccessible(true);
        timed.set(player, new HashMap<TimedEffect, Integer>());

        Field upkeep = Player.class.getDeclaredField("playerUpkeep");
        upkeep.setAccessible(true);
        upkeep.set(player, new PlayerUpkeep());

        player.setSkipCmdCoercion(0);

        GameState.setPlayer(player);
    }

    /**
     * A non-repeatable command must have its {@code nrepeats} cleared - this is the regression guard
     * for the misplaced {@code else}. In C the {@code nrepeats = 0; repeating = false} clearing hangs
     * off the outer {@code if (repeat_allowed)}, so it fires for a command that may <em>not</em>
     * repeat. An earlier port attached that {@code else} to the inner auto-repeat test instead, which
     * inverted the logic: a non-repeatable command would keep its count (and the final "count this
     * execution" step would leave it at 4 here, not 0). Asserting 0 pins the corrected structure.
     */
    @Test
    void nonRepeatableCommandClearsNrepeats() {
        // CMD_HELP is repeatAllowed=false in the table.
        Command help = new Command(CommandContext.CTX_INIT, CommandCode.CMD_HELP, 5, 0, new ArrayList<>());
        CommandProcessor.processCommand(CommandContext.CTX_GAME, help, new CommandQueue(player));
        assertEquals(0, help.getNrepeats(), "a non-repeatable command must have its repeat count cleared");
    }

    /**
     * {@code processCommand} stamps the execution context onto the command, mirroring C's
     * {@code cmd->context = ctx}.
     */
    @Test
    void processCommandSetsTheContext() {
        Command help = new Command(CommandContext.CTX_INIT, CommandCode.CMD_HELP, 0, 0, new ArrayList<>());
        CommandProcessor.processCommand(CommandContext.CTX_GAME, help, new CommandQueue(player));
        assertEquals(CommandContext.CTX_GAME, help.getContext());
    }
}
