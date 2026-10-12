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
import org.junit.jupiter.api.extension.ExtendWith;
import uk.co.jackoftradesltd.middle.cave.Loc;
import uk.co.jackoftradesltd.middle.cave.enums.DirectionEnum;
import uk.co.jackoftradesltd.middle.game.enums.CommandArgumentType;
import uk.co.jackoftradesltd.middle.game.gameengine.argumentdata.*;
import uk.co.jackoftradesltd.middle.objects.ItemObject;
import uk.co.jackoftradesltd.testsupport.SeededPlayerRegistry;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link CommandArgumentData}, the port of C's {@code union cmd_arg_data} and the
 * {@code enum cmd_arg_type} tag that travels with it (cmd-core.h). Expected values come from
 * cmd-core.h, where the union has the members {@code string}, {@code choice}, {@code obj},
 * {@code number}, {@code direction} and {@code point}, and the enum numbers {@code arg_NONE = 0},
 * {@code arg_STRING = 1} and then {@code arg_CHOICE} to {@code arg_POINT} in sequence; from
 * {@code cmd_set_arg_target} and {@code cmd_get_arg_target} in cmd-core.c, which store the target
 * in {@code data.direction}; and from {@code cmd_copy} in cmd-core.c, which copies the command by
 * struct assignment and then deep-copies only {@code arg_STRING} arguments.
 *
 * <p>The per-variant records have their own test classes; the two that had none,
 * {@link ArgumentString} and {@link ArgumentTarget}, are covered here along with the contract the
 * interface itself states.
 *
 * <p>Not covered: {@code cmd_release}, which has no port because Java frees nothing by hand, and
 * the way {@code Command} stores and reads each variant, which {@code CommandTest} covers.
 *
 * <p>Class CommandArgumentDataTest coded on 261012, commented in full on 261012.
 */
@ExtendWith(SeededPlayerRegistry.class)
class CommandArgumentDataTest {

    @Test
    @DisplayName("exactly seven variants are permitted: one per union member plus the target")
    void sevenPermittedVariants() {
        Set<Class<?>> permitted = new HashSet<>(Arrays.asList(CommandArgumentData.class.getPermittedSubclasses()));

        assertEquals(Set.of(ArgumentString.class, ArgumentChoice.class, ArgumentItem.class, ArgumentNumber.class,
                ArgumentDirection.class, ArgumentTarget.class, ArgumentPoint.class), permitted);
    }

    @Test
    @DisplayName("each variant reports the tag C's cmd_set_arg_* stores beside its union member")
    void typeTagPerVariant() {
        assertEquals(CommandArgumentType.arg_STRING, new ArgumentString("x").type());
        assertEquals(CommandArgumentType.arg_CHOICE, new ArgumentChoice(1).type());
        assertEquals(CommandArgumentType.arg_ITEM, new ArgumentItem(new ItemObject()).type());
        assertEquals(CommandArgumentType.arg_NUMBER, new ArgumentNumber(1).type());
        assertEquals(CommandArgumentType.arg_DIRECTION, new ArgumentDirection(DirectionEnum.DIR_N).type());
        assertEquals(CommandArgumentType.arg_TARGET, new ArgumentTarget(DirectionEnum.DIR_TARGET).type());
        assertEquals(CommandArgumentType.arg_POINT, new ArgumentPoint(Loc.row(2).col(1)).type());
    }

    @Test
    @DisplayName("the tag enum is numbered as in cmd-core.h: arg_NONE 0, arg_STRING 1, then in sequence to arg_POINT")
    void typeEnumOrdinalsMatchC() {
        assertEquals(0, CommandArgumentType.arg_NONE.ordinal());
        assertEquals(1, CommandArgumentType.arg_STRING.ordinal());
        assertEquals(2, CommandArgumentType.arg_CHOICE.ordinal());
        assertEquals(3, CommandArgumentType.arg_ITEM.ordinal());
        assertEquals(4, CommandArgumentType.arg_NUMBER.ordinal());
        assertEquals(5, CommandArgumentType.arg_DIRECTION.ordinal());
        assertEquals(6, CommandArgumentType.arg_TARGET.ordinal());
        assertEquals(7, CommandArgumentType.arg_POINT.ordinal());
    }

    @Test
    @DisplayName("no variant reports arg_NONE, and the seven variants report seven distinct tags")
    void tagsAreDistinctAndNeverNone() {
        CommandArgumentData[] all = {
                new ArgumentString("x"), new ArgumentChoice(0), new ArgumentItem(null), new ArgumentNumber(0),
                new ArgumentDirection(DirectionEnum.DIR_UNKNOWN), new ArgumentTarget(DirectionEnum.DIR_UNKNOWN),
                new ArgumentPoint(Loc.zero)
        };
        Set<CommandArgumentType> seen = new HashSet<>();
        for (CommandArgumentData data : all) {
            assertNotEquals(CommandArgumentType.arg_NONE, data.type());
            seen.add(data.type());
        }
        assertEquals(7, seen.size());
    }

    @Test
    @DisplayName("a direction and a target holding the same code are different variants with different tags")
    void directionAndTargetStayDistinct() {
        CommandArgumentData direction = new ArgumentDirection(DirectionEnum.DIR_TARGET);
        CommandArgumentData target = new ArgumentTarget(DirectionEnum.DIR_TARGET);

        assertNotEquals(direction, target);
        assertNotEquals(direction.type(), target.type());
    }

    @Test
    @DisplayName("ArgumentString.copy() equals the original, keeps its tag, and an empty string stays empty")
    void stringCopy() {
        ArgumentString original = new ArgumentString("inscription");
        CommandArgumentData copy = original.copy();

        assertInstanceOf(ArgumentString.class, copy);
        assertEquals(original, copy);
        assertEquals(CommandArgumentType.arg_STRING, copy.type());
        assertEquals("", ((ArgumentString) new ArgumentString("").copy()).value());
    }

    @Test
    @DisplayName("ArgumentString.copy() of a null string is a null string; C's string_make(NULL) is NULL")
    void stringCopyOfNull() {
        CommandArgumentData copy = new ArgumentString(null).copy();

        assertInstanceOf(ArgumentString.class, copy);
        assertNull(((ArgumentString) copy).value());
    }

    @Test
    @DisplayName("ArgumentTarget.copy() equals the original and keeps its code, DIR_TARGET and DIR_UNKNOWN included")
    void targetCopy() {
        for (DirectionEnum code : DirectionEnum.values()) {
            ArgumentTarget original = new ArgumentTarget(code);
            CommandArgumentData copy = original.copy();

            assertInstanceOf(ArgumentTarget.class, copy, code.name());
            assertEquals(original, copy, code.name());
            assertEquals(code, ((ArgumentTarget) copy).value(), code.name());
            assertEquals(CommandArgumentType.arg_TARGET, copy.type(), code.name());
        }
    }

    @Test
    @DisplayName("copy() never changes the variant: the copy of each variant is the same record class with the same tag")
    void copyKeepsVariantAndTag() {
        CommandArgumentData[] all = {
                new ArgumentString("x"), new ArgumentChoice(-2), new ArgumentItem(new ItemObject()),
                new ArgumentNumber(-1), new ArgumentDirection(DirectionEnum.DIR_SE),
                new ArgumentTarget(DirectionEnum.DIR_TARGET), new ArgumentPoint(Loc.row(4).col(3))
        };

        for (CommandArgumentData data : all) {
            CommandArgumentData copy = data.copy();
            assertSame(data.getClass(), copy.getClass());
            assertEquals(data.type(), copy.type());
            assertEquals(data, copy);
            assertNotSame(data, copy);
        }
    }

    @Test
    @DisplayName("copy() of an ArgumentItem shares the item, as cmd_copy copies an obj pointer by assignment")
    void itemCopySharesTheObject() {
        ItemObject item = new ItemObject();
        ArgumentItem copy = (ArgumentItem) new ArgumentItem(item).copy();

        assertSame(item, copy.value());
    }

    @Test
    @DisplayName("copy() of an ArgumentItem holding null is an ArgumentItem holding null")
    void itemCopyOfNull() {
        ArgumentItem copy = (ArgumentItem) new ArgumentItem(null).copy();

        assertNull(copy.value());
    }

    @Test
    @DisplayName("copy() of an ArgumentPoint is a separate Loc with the same coordinates; C copies struct loc by value")
    void pointCopyIsIndependent() {
        Loc loc = Loc.row(9).col(7);
        ArgumentPoint copy = (ArgumentPoint) new ArgumentPoint(loc).copy();

        assertEquals(loc, copy.value());
        assertNotSame(loc, copy.value());
    }
}
