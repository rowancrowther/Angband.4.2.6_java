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
 * Tests for {@link ArgumentDirection}, the port of the {@code int direction} member of C's
 * {@code union cmd_arg_data} and its {@code arg_DIRECTION} tag (cmd-core.h). Expected values come
 * from {@code cmd_set_arg_direction} and {@code cmd_get_arg_direction} in cmd-core.c, which store
 * and return the int unchecked under the {@code arg_DIRECTION} tag, from {@code cmd_copy}, which
 * copies a non-string argument by plain struct assignment, and from {@code cave.h}, where
 * {@code DIR_NONE} and {@code DIR_TARGET} are both 5. {@code cmd_set_arg_target} and
 * {@code cmd_get_arg_target} use the same union member under the {@code arg_TARGET} tag, so a
 * direction argument must not be readable as a target and the other way round.
 *
 * <p>Class ArgumentDirectionTest coded on 261012, commented in full on 261012.
 */
@ExtendWith(SeededPlayerRegistry.class)
class ArgumentDirectionTest {

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
    @DisplayName("value() returns exactly the direction the record was built with, for every constant")
    void valueRoundTrips() {
        for (DirectionEnum d : DirectionEnum.values()) {
            assertSame(d, new ArgumentDirection(d).value());
        }
    }

    @Test
    @DisplayName("type() is arg_DIRECTION for every direction, including DIR_TARGET and DIR_NONE")
    void typeIsDirection() {
        for (DirectionEnum d : DirectionEnum.values()) {
            assertEquals(CommandArgumentType.arg_DIRECTION, new ArgumentDirection(d).type());
        }
    }

    @Test
    @DisplayName("DIR_NONE and DIR_TARGET share C's value 5 but stay distinct records")
    void noneAndTargetStayDistinct() {
        assertEquals(DirectionEnum.DIR_NONE.getKey(), DirectionEnum.DIR_TARGET.getKey());
        assertEquals(5, DirectionEnum.DIR_NONE.getKey());

        assertNotEquals(new ArgumentDirection(DirectionEnum.DIR_NONE),
                new ArgumentDirection(DirectionEnum.DIR_TARGET));
        assertSame(DirectionEnum.DIR_NONE, new ArgumentDirection(DirectionEnum.DIR_NONE).value());
        assertSame(DirectionEnum.DIR_TARGET, new ArgumentDirection(DirectionEnum.DIR_TARGET).value());
    }

    @Test
    @DisplayName("DIR_UNKNOWN (0) is storable: the set function checks nothing")
    void unknownIsStorable() {
        assertSame(DirectionEnum.DIR_UNKNOWN, new ArgumentDirection(DirectionEnum.DIR_UNKNOWN).value());
    }

    @Test
    @DisplayName("arg_DIRECTION is a different variant from arg_TARGET even for the same direction")
    void directionIsNotTarget() {
        ArgumentDirection direction = new ArgumentDirection(DirectionEnum.DIR_N);
        ArgumentTarget target = new ArgumentTarget(DirectionEnum.DIR_N);

        assertNotEquals(direction.type(), target.type());
        assertNotEquals(direction, target);
    }

    @Test
    @DisplayName("copy() is an ArgumentDirection with the same value, equal to but not the same object")
    void copyHoldsSameValue() {
        ArgumentDirection original = new ArgumentDirection(DirectionEnum.DIR_SW);

        CommandArgumentData copy = original.copy();

        assertInstanceOf(ArgumentDirection.class, copy);
        assertSame(DirectionEnum.DIR_SW, ((ArgumentDirection) copy).value());
        assertEquals(original, copy);
        assertNotSame(original, copy);
        assertEquals(CommandArgumentType.arg_DIRECTION, copy.type());
    }

    @Test
    @DisplayName("records with the same direction are equal and share a hash; different directions are not equal")
    void equality() {
        assertEquals(new ArgumentDirection(DirectionEnum.DIR_E), new ArgumentDirection(DirectionEnum.DIR_E));
        assertEquals(new ArgumentDirection(DirectionEnum.DIR_E).hashCode(),
                new ArgumentDirection(DirectionEnum.DIR_E).hashCode());
        assertNotEquals(new ArgumentDirection(DirectionEnum.DIR_E), new ArgumentDirection(DirectionEnum.DIR_W));
    }

    @Test
    @DisplayName("a CommandArgument holding a direction copies to an independent argument with the same value")
    void commandArgumentCopyKeepsDirection() {
        CommandArgument original = new CommandArgument(CommandArgumentType.arg_DIRECTION,
                new ArgumentDirection(DirectionEnum.DIR_NW), "direction");

        CommandArgument copy = original.copy();

        assertNotSame(original, copy);
        assertNotSame(original.getData(), copy.getData());
        assertEquals(CommandArgumentType.arg_DIRECTION, copy.getType());
        assertEquals("direction", copy.getName());
        assertEquals(new ArgumentDirection(DirectionEnum.DIR_NW), copy.getData());
    }

    @Test
    @DisplayName("Command round trip: set then get returns the direction, and a cloned command keeps it")
    void commandRoundTripAndClone() {
        cmd.setArgDirection("direction", DirectionEnum.DIR_SE);

        assertEquals(Optional.of(DirectionEnum.DIR_SE), cmd.getArgDirection("direction"));
        assertEquals(Optional.of(DirectionEnum.DIR_SE), cmd.clone().getArgDirection("direction"));
    }

    @Test
    @DisplayName("a direction argument is not readable as a target, and a target is not readable as a direction")
    void commandDirectionAndTargetDoNotCross() {
        cmd.setArgDirection("d", DirectionEnum.DIR_N);
        cmd.setArgTarget("t", DirectionEnum.DIR_N);

        assertTrue(cmd.getArgTarget("d").isEmpty());
        assertTrue(cmd.getArgDirection("t").isEmpty());
        assertEquals(Optional.of(DirectionEnum.DIR_N), cmd.getArgDirection("d"));
        assertEquals(Optional.of(DirectionEnum.DIR_N), cmd.getArgTarget("t"));
    }

    @Test
    @DisplayName("a direction argument is not readable as a choice or a number")
    void commandDirectionIsNotAnInt() {
        cmd.setArgDirection("d", DirectionEnum.DIR_N);

        assertTrue(cmd.getArgChoice("d").isEmpty());
        assertTrue(cmd.getArgNumber("d").isEmpty());
    }
}
