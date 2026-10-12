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
 * Tests for {@link ArgumentString}, the port of the {@code const char *string} member of C's
 * {@code union cmd_arg_data} and its {@code arg_STRING} tag (cmd-core.h). Expected values come
 * from {@code cmd_set_arg_string} and {@code cmd_get_arg_string} in cmd-core.c, which store a
 * {@code string_make} duplicate and return it under the {@code arg_STRING} tag, from
 * {@code cmd_copy}, which deep-copies the string arguments (and only those), and from
 * {@code string_make} in z-virt.c, which returns {@code NULL} for a {@code NULL} input.
 *
 * <p>Class ArgumentStringTest coded on 261012, commented in full on 261012.
 */
@ExtendWith(SeededPlayerRegistry.class)
class ArgumentStringTest {

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
    @DisplayName("value() returns exactly the string the record was built with")
    void valueRoundTrips() {
        assertEquals("inscription", new ArgumentString("inscription").value());
        assertEquals("", new ArgumentString("").value());
    }

    @Test
    @DisplayName("type() is arg_STRING for every string, an empty one and a null one included")
    void typeIsString() {
        assertEquals(CommandArgumentType.arg_STRING, new ArgumentString("x").type());
        assertEquals(CommandArgumentType.arg_STRING, new ArgumentString("").type());
        assertEquals(CommandArgumentType.arg_STRING, new ArgumentString(null).type());
    }

    @Test
    @DisplayName("a null string is legal: string_make(NULL) is NULL and cmd_set_arg_string stores it")
    void nullIsStorable() {
        assertNull(new ArgumentString(null).value());
    }

    @Test
    @DisplayName("null and empty are different values")
    void nullIsNotEmpty() {
        assertNotEquals(new ArgumentString(null), new ArgumentString(""));
    }

    @Test
    @DisplayName("strings are compared by content, not identity, and case and whitespace matter")
    void equalityIsByContent() {
        assertEquals(new ArgumentString(new String("abc")), new ArgumentString(new String("abc")));
        assertEquals(new ArgumentString("abc").hashCode(), new ArgumentString("abc").hashCode());
        assertNotEquals(new ArgumentString("abc"), new ArgumentString("ABC"));
        assertNotEquals(new ArgumentString("abc"), new ArgumentString("abc "));
        assertEquals(new ArgumentString(null), new ArgumentString(null));
    }

    @Test
    @DisplayName("copy() is an ArgumentString with the same text, equal to but not the same object")
    void copyHoldsSameValue() {
        ArgumentString original = new ArgumentString("inscription");

        CommandArgumentData copy = original.copy();

        assertInstanceOf(ArgumentString.class, copy);
        assertEquals("inscription", ((ArgumentString) copy).value());
        assertEquals(original, copy);
        assertNotSame(original, copy);
        assertEquals(CommandArgumentType.arg_STRING, copy.type());
    }

    @Test
    @DisplayName("copy() of an empty string stays empty and of a null string stays null")
    void copyKeepsEmptyAndNull() {
        assertEquals("", ((ArgumentString) new ArgumentString("").copy()).value());
        assertNull(((ArgumentString) new ArgumentString(null).copy()).value());
    }

    @Test
    @DisplayName("a CommandArgument holding a string copies to an independent argument with the same text")
    void commandArgumentCopyKeepsString() {
        CommandArgument original = new CommandArgument(CommandArgumentType.arg_STRING,
                new ArgumentString("hello"), "name");

        CommandArgument copy = original.copy();

        assertNotSame(original, copy);
        assertNotSame(original.getData(), copy.getData());
        assertEquals(CommandArgumentType.arg_STRING, copy.getType());
        assertEquals("name", copy.getName());
        assertEquals(new ArgumentString("hello"), copy.getData());
    }

    @Test
    @DisplayName("Command round trip: set then get returns the string, and a cloned command keeps it")
    void commandRoundTripAndClone() {
        cmd.setArgString("name", "Grip");

        assertEquals(Optional.of("Grip"), cmd.getArgString("name"));
        assertEquals(Optional.of("Grip"), cmd.clone().getArgString("name"));
    }

    @Test
    @DisplayName("an empty string reads back as present, a null string reads back as empty")
    void commandEmptyVersusNull() {
        cmd.setArgString("empty", "");
        cmd.setArgString("none", null);

        assertEquals(Optional.of(""), cmd.getArgString("empty"));
        assertTrue(cmd.getArgString("none").isEmpty());
    }

    @Test
    @DisplayName("overwriting a string argument replaces the text under the same name")
    void commandOverwrite() {
        cmd.setArgString("name", "first");
        cmd.setArgString("name", "second");

        assertEquals(Optional.of("second"), cmd.getArgString("name"));
    }

    @Test
    @DisplayName("a string argument is not readable as a choice, number, item, direction, target or point")
    void commandStringIsNotAnyOtherKind() {
        cmd.setArgString("s", "5");

        assertTrue(cmd.getArgChoice("s").isEmpty());
        assertTrue(cmd.getArgNumber("s").isEmpty());
        assertTrue(cmd.getArgItem("s").isEmpty());
        assertTrue(cmd.getArgDirection("s").isEmpty());
        assertTrue(cmd.getArgTarget("s").isEmpty());
        assertTrue(cmd.getArgPoint("s").isEmpty());
    }

    @Test
    @DisplayName("a number argument is not readable as a string, and an absent name is empty")
    void commandOtherKindsAreNotStrings() {
        cmd.setArgNumber("n", 5);

        assertTrue(cmd.getArgString("n").isEmpty());
        assertTrue(cmd.getArgString("missing").isEmpty());
    }
}
