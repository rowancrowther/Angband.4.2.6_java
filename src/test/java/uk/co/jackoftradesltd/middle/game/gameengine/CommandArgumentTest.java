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

package uk.co.jackoftradesltd.middle.game.gameengine;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.middle.game.enums.CommandArgumentType;
import uk.co.jackoftradesltd.middle.game.gameengine.argumentdata.ArgumentNumber;
import uk.co.jackoftradesltd.middle.game.gameengine.argumentdata.ArgumentString;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link CommandArgument}, the port of C's {@code struct cmd_arg} (cmd-core.h). Expected
 * values come from the C struct's shape (type, data, name) and from {@code cmd_copy} in
 * cmd-core.c, whose copy leaves the source and destination arguments independent.
 *
 * <p>Class CommandArgumentTest coded on 261001, commented in full on 261001.
 */
class CommandArgumentTest {

    private static CommandArgument number(int n, String name) {
        return new CommandArgument(CommandArgumentType.arg_NUMBER, new ArgumentNumber(n), name);
    }

    @Test
    @DisplayName("constructor stores type, data and name, exposed by the getters")
    void constructorStoresFields() {
        ArgumentNumber data = new ArgumentNumber(7);
        CommandArgument arg = new CommandArgument(CommandArgumentType.arg_NUMBER, data, "quantity");

        assertEquals(CommandArgumentType.arg_NUMBER, arg.getType());
        assertSame(data, arg.getData());
        assertEquals("quantity", arg.getName());
        assertEquals(arg.getType(), arg.getData().type());
    }

    @Test
    @DisplayName("update with all three values replaces type, data and name")
    void updateReplacesAll() {
        CommandArgument arg = number(1, "old");
        ArgumentString s = new ArgumentString("hello");

        arg.update(CommandArgumentType.arg_STRING, s, "new");

        assertEquals(CommandArgumentType.arg_STRING, arg.getType());
        assertSame(s, arg.getData());
        assertEquals("new", arg.getName());
    }

    @Test
    @DisplayName("update with nulls leaves the corresponding fields untouched")
    void updateNullsKeepFields() {
        ArgumentNumber data = new ArgumentNumber(1);
        CommandArgument arg = new CommandArgument(CommandArgumentType.arg_NUMBER, data, "keep");

        arg.update(null, null, null);
        assertEquals(CommandArgumentType.arg_NUMBER, arg.getType());
        assertSame(data, arg.getData());
        assertEquals("keep", arg.getName());

        // Payload only: the name and type survive.
        ArgumentNumber replacement = new ArgumentNumber(9);
        arg.update(null, replacement, null);
        assertSame(replacement, arg.getData());
        assertEquals("keep", arg.getName());
        assertEquals(CommandArgumentType.arg_NUMBER, arg.getType());

        // Name only.
        arg.update(null, null, "renamed");
        assertEquals("renamed", arg.getName());
        assertSame(replacement, arg.getData());
    }

    @Test
    @DisplayName("copy yields an equal but independent argument (cmd_copy semantics)")
    void copyIsIndependent() {
        CommandArgument original = number(42, "amount");
        CommandArgument copy = original.copy();

        assertNotSame(original, copy);
        assertEquals(original.getType(), copy.getType());
        assertEquals(original.getName(), copy.getName());
        assertEquals(original.getData(), copy.getData());
        assertNotSame(original.getData(), copy.getData());

        // Mutating the copy must not disturb the original.
        copy.update(CommandArgumentType.arg_STRING, new ArgumentString("x"), "other");
        assertEquals(CommandArgumentType.arg_NUMBER, original.getType());
        assertEquals(new ArgumentNumber(42), original.getData());
        assertEquals("amount", original.getName());
    }

    @Test
    @DisplayName("name is not truncated, unlike C's name[20] (documented divergence)")
    void longNameIsKept() {
        String name = "a_name_longer_than_nineteen_chars";
        assertEquals(name, number(0, name).getName());
    }
}
