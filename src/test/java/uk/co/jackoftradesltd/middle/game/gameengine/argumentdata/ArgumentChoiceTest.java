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
import uk.co.jackoftradesltd.middle.game.gameengine.CommandArgument;
import uk.co.jackoftradesltd.middle.game.gameengine.CommandArgumentData;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link ArgumentChoice}, the port of the {@code int choice} member of C's
 * {@code union cmd_arg_data} and its {@code arg_CHOICE} tag (cmd-core.h). Expected values come
 * from {@code cmd_set_arg_choice} and {@code cmd_get_arg_choice} in cmd-core.c, which store and
 * return the int unchecked under the {@code arg_CHOICE} tag, and from {@code cmd_copy}, which
 * copies a non-string argument by plain struct assignment. -2 is the value C callers actually
 * store as a sentinel: {@code cmd_get_effect_from_list} accepts a stored selection of -2 to mean
 * "random" when random is allowed, so a negative must survive the round trip.
 *
 * <p>Class ArgumentChoiceTest coded on 261012, commented in full on 261012.
 */
class ArgumentChoiceTest {

    @Test
    @DisplayName("value() returns exactly the int the record was built with")
    void valueRoundTrips() {
        assertEquals(0, new ArgumentChoice(0).value());
        assertEquals(1, new ArgumentChoice(1).value());
        assertEquals(37, new ArgumentChoice(37).value());
    }

    @Test
    @DisplayName("negative and extreme ints are stored unchecked, as C's int member stores them")
    void valueIsUnchecked() {
        assertEquals(-1, new ArgumentChoice(-1).value());
        assertEquals(-2, new ArgumentChoice(-2).value());
        assertEquals(Integer.MAX_VALUE, new ArgumentChoice(Integer.MAX_VALUE).value());
        assertEquals(Integer.MIN_VALUE, new ArgumentChoice(Integer.MIN_VALUE).value());
    }

    @Test
    @DisplayName("type() is arg_CHOICE whatever the value")
    void typeIsChoice() {
        for (int v : new int[]{0, 5, -1, -2, Integer.MAX_VALUE, Integer.MIN_VALUE}) {
            assertEquals(CommandArgumentType.arg_CHOICE, new ArgumentChoice(v).type());
        }
    }

    @Test
    @DisplayName("arg_CHOICE is a different tag from arg_NUMBER even for the same int")
    void choiceIsNotNumber() {
        ArgumentChoice choice = new ArgumentChoice(3);
        ArgumentNumber number = new ArgumentNumber(3);

        assertNotEquals(choice.type(), number.type());
        assertNotEquals(choice, number);
    }

    @Test
    @DisplayName("copy() is an ArgumentChoice with the same value, equal to but not the same object")
    void copyHoldsSameValue() {
        ArgumentChoice original = new ArgumentChoice(-2);

        CommandArgumentData copy = original.copy();

        assertInstanceOf(ArgumentChoice.class, copy);
        assertEquals(-2, ((ArgumentChoice) copy).value());
        assertEquals(original, copy);
        assertNotSame(original, copy);
        assertEquals(CommandArgumentType.arg_CHOICE, copy.type());
    }

    @Test
    @DisplayName("records with the same int are equal and share a hash; different ints are not equal")
    void equality() {
        assertEquals(new ArgumentChoice(4), new ArgumentChoice(4));
        assertEquals(new ArgumentChoice(4).hashCode(), new ArgumentChoice(4).hashCode());
        assertNotEquals(new ArgumentChoice(4), new ArgumentChoice(5));
        assertNotEquals(new ArgumentChoice(0), new ArgumentChoice(-1));
    }

    @Test
    @DisplayName("a CommandArgument holding a choice copies to an independent argument with the same value")
    void commandArgumentCopyKeepsChoice() {
        CommandArgument original = new CommandArgument(CommandArgumentType.arg_CHOICE,
                new ArgumentChoice(-2), "choice");

        CommandArgument copy = original.copy();

        assertNotSame(original, copy);
        assertNotSame(original.getData(), copy.getData());
        assertEquals(CommandArgumentType.arg_CHOICE, copy.getType());
        assertEquals("choice", copy.getName());
        assertEquals(new ArgumentChoice(-2), copy.getData());
    }
}
