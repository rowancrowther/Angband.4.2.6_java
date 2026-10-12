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
import uk.co.jackoftradesltd.middle.cave.Loc;
import uk.co.jackoftradesltd.middle.cave.enums.DirectionEnum;
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
 * Tests for {@link ArgumentPoint}, the port of the {@code struct loc point} member of C's
 * {@code union cmd_arg_data} and its {@code arg_POINT} tag (cmd-core.h). Expected values come from
 * {@code cmd_set_arg_point} and {@code cmd_get_arg_point} in cmd-core.c, which store and return the
 * grid by value, unchecked, under the {@code arg_POINT} tag; from {@code cmd_get_arg}, which
 * refuses an argument stored under another tag; and from {@code cmd_copy}, which copies a
 * non-string argument by plain struct assignment.
 *
 * <p>Class ArgumentPointTest coded on 261012, commented in full on 261012.
 */
class ArgumentPointTest {

    private static Command newCommand() {
        return new Command(CommandContext.CTX_BIRTH, CommandCode.CMD_BUY_STAT, 0, 0, new ArrayList<>());
    }

    private static Loc at(int x, int y) {
        return Loc.row(y).col(x);
    }

    @Test
    @DisplayName("value() returns exactly the location the record was built with")
    void valueRoundTrips() {
        assertEquals(at(3, 4), new ArgumentPoint(at(3, 4)).value());
        assertEquals(4, new ArgumentPoint(at(3, 4)).value().getY());
        assertEquals(3, new ArgumentPoint(at(3, 4)).value().getX());
    }

    @Test
    @DisplayName("x and y are not transposed on the way in or out")
    void axesAreNotTransposed() {
        Loc stored = new ArgumentPoint(at(10, 2)).value();

        assertEquals(10, stored.getX());
        assertEquals(2, stored.getY());
        assertNotEquals(new ArgumentPoint(at(10, 2)), new ArgumentPoint(at(2, 10)));
    }

    @Test
    @DisplayName("coordinates are stored unchecked: origin, negative and extreme ints all round-trip")
    void valueIsUnchecked() {
        assertEquals(Loc.zero, new ArgumentPoint(Loc.zero).value());
        assertEquals(at(-1, -1), new ArgumentPoint(at(-1, -1)).value());
        assertEquals(at(Integer.MAX_VALUE, Integer.MIN_VALUE),
                new ArgumentPoint(at(Integer.MAX_VALUE, Integer.MIN_VALUE)).value());
    }

    @Test
    @DisplayName("type() is arg_POINT whatever the location")
    void typeIsPoint() {
        for (Loc loc : new Loc[]{Loc.zero, at(1, 1), at(-5, 7), at(Integer.MAX_VALUE, Integer.MAX_VALUE)}) {
            assertEquals(CommandArgumentType.arg_POINT, new ArgumentPoint(loc).type());
        }
    }

    @Test
    @DisplayName("arg_POINT sits at C's enum position 7, the last tag after arg_TARGET")
    void tagOrdinalMatchesC() {
        assertEquals(7, new ArgumentPoint(Loc.zero).type().ordinal());
    }

    @Test
    @DisplayName("a point is a different variant from a direction")
    void pointIsNotDirection() {
        ArgumentPoint point = new ArgumentPoint(at(1, 1));
        ArgumentDirection direction = new ArgumentDirection(DirectionEnum.DIR_N);

        assertNotEquals(point.type(), direction.type());
        assertNotEquals(point, direction);
    }

    @Test
    @DisplayName("copy() is an ArgumentPoint with an equal location, equal to but not the same object")
    void copyHoldsSameLocation() {
        ArgumentPoint original = new ArgumentPoint(at(-7, 12));

        CommandArgumentData copy = original.copy();

        assertInstanceOf(ArgumentPoint.class, copy);
        assertEquals(at(-7, 12), ((ArgumentPoint) copy).value());
        assertEquals(original, copy);
        assertNotSame(original, copy);
        assertEquals(CommandArgumentType.arg_POINT, copy.type());
    }

    @Test
    @DisplayName("copy() shares no Loc reference with the original, though the locations are equal")
    void copyDoesNotShareLoc() {
        ArgumentPoint original = new ArgumentPoint(at(8, 9));

        ArgumentPoint copy = (ArgumentPoint) original.copy();

        assertNotSame(original.value(), copy.value());
        assertEquals(original.value(), copy.value());
    }

    @Test
    @DisplayName("equality is by coordinates (loc_eq), not by Loc identity")
    void equalityIgnoresLocIdentity() {
        Loc first = at(14, 6);
        Loc second = at(14, 6);

        assertNotSame(first, second);
        assertEquals(new ArgumentPoint(first), new ArgumentPoint(second));
        assertEquals(new ArgumentPoint(first).hashCode(), new ArgumentPoint(second).hashCode());
    }

    @Test
    @DisplayName("copy() of the origin is still the origin: (0, 0) is a point, not 'no point'")
    void copyOfOriginKeepsOrigin() {
        CommandArgumentData copy = new ArgumentPoint(Loc.zero).copy();

        assertEquals(new ArgumentPoint(Loc.zero), copy);
        assertTrue(((ArgumentPoint) copy).value().isZero());
    }

    @Test
    @DisplayName("a null location is stored as Loc.zero, so copy() has nothing to fail on")
    void nullLocationBecomesOrigin() {
        ArgumentPoint point = new ArgumentPoint(null);

        assertSame(Loc.zero, point.value());
        assertEquals(new ArgumentPoint(Loc.zero), point.copy());
    }

    @Test
    @DisplayName("setArgPoint with null stores the origin, which getArgPoint reads back as a point")
    void commandNullPointIsOrigin() {
        Command cmd = newCommand();

        cmd.setArgPoint("grid", null);

        assertEquals(Optional.of(Loc.zero), cmd.getArgPoint("grid"));
    }

    @Test
    @DisplayName("records with the same location are equal and share a hash; different locations are not equal")
    void equality() {
        assertEquals(new ArgumentPoint(at(4, 5)), new ArgumentPoint(at(4, 5)));
        assertEquals(new ArgumentPoint(at(4, 5)).hashCode(), new ArgumentPoint(at(4, 5)).hashCode());
        assertNotEquals(new ArgumentPoint(at(4, 5)), new ArgumentPoint(at(4, 6)));
        assertNotEquals(new ArgumentPoint(at(4, 5)), new ArgumentPoint(at(5, 5)));
        assertNotEquals(new ArgumentPoint(Loc.zero), new ArgumentPoint(at(-1, 0)));
    }

    @Test
    @DisplayName("a CommandArgument holding a point copies to an independent argument with an equal location")
    void commandArgumentCopyKeepsPoint() {
        CommandArgument original = new CommandArgument(CommandArgumentType.arg_POINT,
                new ArgumentPoint(at(25, 9)), "point");

        CommandArgument copy = original.copy();

        assertNotSame(original, copy);
        assertNotSame(original.getData(), copy.getData());
        assertEquals(CommandArgumentType.arg_POINT, copy.getType());
        assertEquals("point", copy.getName());
        assertEquals(new ArgumentPoint(at(25, 9)), copy.getData());
    }

    @Test
    @DisplayName("setArgPoint then getArgPoint round-trips the grid, including the origin and a negative one")
    void commandRoundTrip() {
        Command cmd = newCommand();

        cmd.setArgPoint("grid", at(12, 3));
        cmd.setArgPoint("origin", Loc.zero);
        cmd.setArgPoint("offset", at(-3, -4));

        assertEquals(Optional.of(at(12, 3)), cmd.getArgPoint("grid"));
        assertEquals(Optional.of(Loc.zero), cmd.getArgPoint("origin"));
        assertEquals(Optional.of(at(-3, -4)), cmd.getArgPoint("offset"));
    }

    @Test
    @DisplayName("getArgPoint is empty when the name is unset or holds another tag (cmd_get_arg type check)")
    void commandReadRefusesOtherTags() {
        Command cmd = newCommand();
        cmd.setArgDirection("direction", DirectionEnum.DIR_N);
        cmd.setArgNumber("quantity", 3);

        assertEquals(Optional.empty(), cmd.getArgPoint("direction"));
        assertEquals(Optional.empty(), cmd.getArgPoint("quantity"));
        assertEquals(Optional.empty(), cmd.getArgPoint("missing"));
    }

    @Test
    @DisplayName("the other getters refuse a name that holds a point")
    void otherGettersRefusePoint() {
        Command cmd = newCommand();
        cmd.setArgPoint("grid", at(2, 2));

        assertEquals(Optional.empty(), cmd.getArgNumber("grid"));
        assertEquals(Optional.empty(), cmd.getArgDirection("grid"));
    }

    @Test
    @DisplayName("setting a name again replaces its point rather than adding a second argument")
    void commandOverwrite() {
        Command cmd = newCommand();

        cmd.setArgPoint("grid", at(1, 1));
        cmd.setArgPoint("grid", at(9, 8));

        assertEquals(Optional.of(at(9, 8)), cmd.getArgPoint("grid"));
    }

    @Test
    @DisplayName("overwriting a point with another tag makes it unreadable as a point, and the reverse")
    void commandOverwriteChangesTag() {
        Command cmd = newCommand();

        cmd.setArgPoint("target", at(1, 1));
        cmd.setArgNumber("target", 5);
        assertEquals(Optional.empty(), cmd.getArgPoint("target"));
        assertEquals(Optional.of(5), cmd.getArgNumber("target"));

        cmd.setArgPoint("target", at(6, 7));
        assertEquals(Optional.of(at(6, 7)), cmd.getArgPoint("target"));
        assertEquals(Optional.empty(), cmd.getArgNumber("target"));
    }

    @Test
    @DisplayName("a cloned command keeps its point, and re-setting the original does not change the clone (cmd_copy)")
    void commandCloneKeepsPoint() {
        Command original = newCommand();
        original.setArgPoint("grid", at(30, 15));

        Command clone = original.clone();
        original.setArgPoint("grid", at(1, 2));

        assertEquals(Optional.of(at(30, 15)), clone.getArgPoint("grid"));
        assertEquals(Optional.of(at(1, 2)), original.getArgPoint("grid"));
    }
}
