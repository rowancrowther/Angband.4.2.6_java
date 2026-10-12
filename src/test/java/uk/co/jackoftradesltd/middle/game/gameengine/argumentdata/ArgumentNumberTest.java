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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.middle.game.enums.CommandArgumentType;
import uk.co.jackoftradesltd.middle.game.enums.CommandCode;
import uk.co.jackoftradesltd.middle.game.enums.CommandContext;
import uk.co.jackoftradesltd.middle.game.gameengine.Command;
import uk.co.jackoftradesltd.middle.game.gameengine.CommandArgument;
import uk.co.jackoftradesltd.middle.game.gameengine.CommandArgumentData;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link ArgumentNumber}, the port of the {@code int number} member of C's
 * {@code union cmd_arg_data} and its {@code arg_NUMBER} tag (cmd-core.h). Expected values come
 * from {@code cmd_set_arg_number} and {@code cmd_get_arg_number} in cmd-core.c, which store and
 * return the int unchecked under the {@code arg_NUMBER} tag, and from {@code cmd_copy}, which
 * copies a non-string argument by plain struct assignment. {@code cmd_get_arg} refuses an argument
 * stored under another tag, so a number read from a choice slot (or the reverse) must come back
 * empty rather than as a coincidence of storage.
 *
 * <p>Class ArgumentNumberTest coded on 261012, commented in full on 261012.
 */
class ArgumentNumberTest {

    private static Command newCommand() {
        return new Command(CommandContext.CTX_BIRTH, CommandCode.CMD_BUY_STAT, 0, 0, new ArrayList<>());
    }

    @Test
    @DisplayName("value() returns exactly the int the record was built with")
    void valueRoundTrips() {
        assertEquals(0, new ArgumentNumber(0).value());
        assertEquals(1, new ArgumentNumber(1).value());
        assertEquals(40, new ArgumentNumber(40).value());
    }

    @Test
    @DisplayName("negative and extreme ints are stored unchecked, as C's int member stores them")
    void valueIsUnchecked() {
        assertEquals(-1, new ArgumentNumber(-1).value());
        assertEquals(Integer.MAX_VALUE, new ArgumentNumber(Integer.MAX_VALUE).value());
        assertEquals(Integer.MIN_VALUE, new ArgumentNumber(Integer.MIN_VALUE).value());
    }

    @Test
    @DisplayName("type() is arg_NUMBER whatever the value")
    void typeIsNumber() {
        for (int v : new int[]{0, 5, -1, Integer.MAX_VALUE, Integer.MIN_VALUE}) {
            assertEquals(CommandArgumentType.arg_NUMBER, new ArgumentNumber(v).type());
        }
    }

    @Test
    @DisplayName("arg_NUMBER sits at C's enum position 4, after arg_ITEM and before arg_DIRECTION")
    void tagOrdinalMatchesC() {
        assertEquals(4, new ArgumentNumber(0).type().ordinal());
    }

    @Test
    @DisplayName("arg_NUMBER is a different tag from arg_CHOICE even for the same int")
    void numberIsNotChoice() {
        ArgumentNumber number = new ArgumentNumber(3);
        ArgumentChoice choice = new ArgumentChoice(3);

        assertNotEquals(number.type(), choice.type());
        assertNotEquals(number, choice);
    }

    @Test
    @DisplayName("copy() is an ArgumentNumber with the same value, equal to but not the same object")
    void copyHoldsSameValue() {
        ArgumentNumber original = new ArgumentNumber(-7);

        CommandArgumentData copy = original.copy();

        assertInstanceOf(ArgumentNumber.class, copy);
        assertEquals(-7, ((ArgumentNumber) copy).value());
        assertEquals(original, copy);
        assertNotSame(original, copy);
        assertEquals(CommandArgumentType.arg_NUMBER, copy.type());
    }

    @Test
    @DisplayName("records with the same int are equal and share a hash; different ints are not equal")
    void equality() {
        assertEquals(new ArgumentNumber(4), new ArgumentNumber(4));
        assertEquals(new ArgumentNumber(4).hashCode(), new ArgumentNumber(4).hashCode());
        assertNotEquals(new ArgumentNumber(4), new ArgumentNumber(5));
        assertNotEquals(new ArgumentNumber(0), new ArgumentNumber(-1));
    }

    @Test
    @DisplayName("a CommandArgument holding a number copies to an independent argument with the same value")
    void commandArgumentCopyKeepsNumber() {
        CommandArgument original = new CommandArgument(CommandArgumentType.arg_NUMBER,
                new ArgumentNumber(25), "quantity");

        CommandArgument copy = original.copy();

        assertNotSame(original, copy);
        assertNotSame(original.getData(), copy.getData());
        assertEquals(CommandArgumentType.arg_NUMBER, copy.getType());
        assertEquals("quantity", copy.getName());
        assertEquals(new ArgumentNumber(25), copy.getData());
    }

    @Test
    @DisplayName("setArgNumber then getArgNumber round-trips the value, including a negative one")
    void commandRoundTrip() {
        Command cmd = newCommand();

        cmd.setArgNumber("quantity", 12);
        cmd.setArgNumber("range", -3);

        assertEquals(Optional.of(12), cmd.getArgNumber("quantity"));
        assertEquals(Optional.of(-3), cmd.getArgNumber("range"));
    }

    @Test
    @DisplayName("getArgNumber is empty when the name is unset or holds a choice (cmd_get_arg type check)")
    void commandReadRefusesOtherTags() {
        Command cmd = newCommand();
        cmd.setArgChoice("selection", 3);

        assertEquals(Optional.empty(), cmd.getArgNumber("selection"));
        assertEquals(Optional.empty(), cmd.getArgNumber("missing"));
    }

    @Test
    @DisplayName("setting a name again replaces its number rather than adding a second argument")
    void commandOverwrite() {
        Command cmd = newCommand();

        cmd.setArgNumber("quantity", 1);
        cmd.setArgNumber("quantity", 9);

        assertEquals(Optional.of(9), cmd.getArgNumber("quantity"));
    }
}
