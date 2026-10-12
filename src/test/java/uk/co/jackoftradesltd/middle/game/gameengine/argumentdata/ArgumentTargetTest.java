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

package uk.co.jackoftradesltd.middle.game.gameengine.argumentdata;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.co.jackoftradesltd.middle.cave.enums.DirectionEnum;
import uk.co.jackoftradesltd.middle.game.enums.CommandArgumentType;
import uk.co.jackoftradesltd.middle.game.enums.CommandCode;
import uk.co.jackoftradesltd.middle.game.enums.CommandContext;
import uk.co.jackoftradesltd.middle.game.gameengine.*;
import uk.co.jackoftradesltd.middle.player.Player;
import uk.co.jackoftradesltd.testsupport.SeededPlayerRegistry;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link ArgumentTarget}, the port of C's {@code arg_TARGET} tag, which shares the
 * {@code int direction} member of {@code union cmd_arg_data} (cmd-core.h). Expected values come
 * from {@code cmd_set_arg_target} and {@code cmd_get_arg_target} in cmd-core.c, which store and
 * return the int unchecked under the {@code arg_TARGET} tag, from {@code cmd_copy}, which copies
 * a non-string argument by plain struct assignment, and from {@code cave.h}, where
 * {@code DIR_NONE} and {@code DIR_TARGET} are both 5. The Java side adds one rule C could not
 * express, that a {@code null} is refused, because C's int has no null.
 *
 * <p>Class ArgumentTargetTest coded on 261012, commented in full on 261012.
 */
@ExtendWith(SeededPlayerRegistry.class)
class ArgumentTargetTest {

    private Player realPlayer;
    private CommandQueue realQueue;
    private Command cmd;

    @BeforeEach
    void setUp() {
        Player player = new Player();
        realPlayer = GameState.getPlayer();
        realQueue = GameState.getCommandQueue();
        GameState.setPlayer(player);
        GameState.setCommandQueue(new CommandQueue(player));
        cmd = new Command(CommandContext.CTX_GAME, CommandCode.CMD_NULL, 0, 0, new ArrayList<>());
    }

    @AfterEach
    void tearDown() {
        GameState.setPlayer(realPlayer);
        GameState.setCommandQueue(realQueue);
    }

    @Test
    @DisplayName("value() returns exactly the code the record was built with, for every constant")
    void valueRoundTrips() {
        for (DirectionEnum d : DirectionEnum.values()) {
            assertSame(d, new ArgumentTarget(d).value());
        }
    }

    @Test
    @DisplayName("type() is arg_TARGET for every code, never arg_DIRECTION")
    void typeIsTarget() {
        for (DirectionEnum d : DirectionEnum.values()) {
            assertEquals(CommandArgumentType.arg_TARGET, new ArgumentTarget(d).type(), d.name());
        }
    }

    @Test
    @DisplayName("a null code is refused with IllegalArgumentException: C's int cannot be null")
    void nullIsRefused() {
        assertThrows(IllegalArgumentException.class, () -> new ArgumentTarget(null));
    }

    @Test
    @DisplayName("DIR_UNKNOWN (0) is storable: cmd_set_arg_target checks nothing")
    void unknownIsStorable() {
        assertSame(DirectionEnum.DIR_UNKNOWN, new ArgumentTarget(DirectionEnum.DIR_UNKNOWN).value());
    }

    @Test
    @DisplayName("DIR_NONE and DIR_TARGET share C's value 5 but stay distinct records")
    void noneAndTargetStayDistinct() {
        assertEquals(5, DirectionEnum.DIR_NONE.getKey());
        assertEquals(5, DirectionEnum.DIR_TARGET.getKey());

        assertNotEquals(new ArgumentTarget(DirectionEnum.DIR_NONE), new ArgumentTarget(DirectionEnum.DIR_TARGET));
        assertSame(DirectionEnum.DIR_NONE, new ArgumentTarget(DirectionEnum.DIR_NONE).value());
        assertSame(DirectionEnum.DIR_TARGET, new ArgumentTarget(DirectionEnum.DIR_TARGET).value());
    }

    @Test
    @DisplayName("an ArgumentTarget is not equal to an ArgumentDirection holding the same code")
    void targetIsNotDirection() {
        for (DirectionEnum d : DirectionEnum.values()) {
            assertNotEquals(new ArgumentTarget(d), new ArgumentDirection(d), d.name());
        }
    }

    @Test
    @DisplayName("copy() is an ArgumentTarget with the same code, equal to but not the same object")
    void copyHoldsSameValue() {
        ArgumentTarget original = new ArgumentTarget(DirectionEnum.DIR_TARGET);

        CommandArgumentData copy = original.copy();

        assertInstanceOf(ArgumentTarget.class, copy);
        assertSame(DirectionEnum.DIR_TARGET, ((ArgumentTarget) copy).value());
        assertEquals(original, copy);
        assertNotSame(original, copy);
        assertEquals(CommandArgumentType.arg_TARGET, copy.type());
    }

    @Test
    @DisplayName("records with the same code are equal and share a hash; different codes are not equal")
    void equality() {
        assertEquals(new ArgumentTarget(DirectionEnum.DIR_E), new ArgumentTarget(DirectionEnum.DIR_E));
        assertEquals(new ArgumentTarget(DirectionEnum.DIR_E).hashCode(),
                new ArgumentTarget(DirectionEnum.DIR_E).hashCode());
        assertNotEquals(new ArgumentTarget(DirectionEnum.DIR_E), new ArgumentTarget(DirectionEnum.DIR_W));
    }

    @Test
    @DisplayName("a CommandArgument holding a target copies to an independent argument with the same code")
    void commandArgumentCopyKeepsTarget() {
        CommandArgument original = new CommandArgument(CommandArgumentType.arg_TARGET,
                new ArgumentTarget(DirectionEnum.DIR_TARGET), "target");

        CommandArgument copy = original.copy();

        assertNotSame(original, copy);
        assertNotSame(original.getData(), copy.getData());
        assertEquals(CommandArgumentType.arg_TARGET, copy.getType());
        assertEquals("target", copy.getName());
        assertEquals(new ArgumentTarget(DirectionEnum.DIR_TARGET), copy.getData());
    }

    @Test
    @DisplayName("Command round trip: set then get returns the code, and a cloned command keeps it")
    void commandRoundTripAndClone() {
        cmd.setArgTarget("target", DirectionEnum.DIR_TARGET);

        assertEquals(Optional.of(DirectionEnum.DIR_TARGET), cmd.getArgTarget("target"));
        assertEquals(Optional.of(DirectionEnum.DIR_TARGET), cmd.clone().getArgTarget("target"));
    }

    @Test
    @DisplayName("Command.setArgTarget refuses a null code and stores nothing")
    void commandRefusesNull() {
        assertThrows(IllegalArgumentException.class, () -> cmd.setArgTarget("target", null));
        assertTrue(cmd.getArgTarget("target").isEmpty());
    }

    @Test
    @DisplayName("a target argument is not readable as a direction, choice or number")
    void commandTargetIsNotAnInt() {
        cmd.setArgTarget("t", DirectionEnum.DIR_N);

        assertTrue(cmd.getArgDirection("t").isEmpty());
        assertTrue(cmd.getArgChoice("t").isEmpty());
        assertTrue(cmd.getArgNumber("t").isEmpty());
    }
}
