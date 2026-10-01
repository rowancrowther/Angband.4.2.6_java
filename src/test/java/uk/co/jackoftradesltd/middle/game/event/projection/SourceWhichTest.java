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

package uk.co.jackoftradesltd.middle.game.event.projection;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.middle.cave.Trap;
import uk.co.jackoftradesltd.middle.objects.ItemObject;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the {@link SourceWhich} hierarchy, the port of the anonymous {@code which} union inside
 * {@code struct source} in C's {@code source.h}.
 *
 * <p>The expected values come from the C union, which has exactly four members: {@code trap},
 * {@code monster}, {@code object} and {@code chest_trap}. There is no member for {@code SRC_NONE}
 * or {@code SRC_PLAYER}, so the hierarchy must have four permitted records and no more.
 *
 * <p>Class SourceWhichTest written on 261001.
 */
@DisplayName("SourceWhich")
class SourceWhichTest {

    @Test
    @DisplayName("permits exactly four records, one per member of the C union")
    void fourPermittedRecords() {
        Set<String> names = Arrays.stream(SourceWhich.class.getPermittedSubclasses())
                .map(Class::getSimpleName)
                .collect(Collectors.toSet());

        assertEquals(Set.of("TrapRecord", "MonsterRecord", "ObjectRecord", "ChestTrapRecord"), names);
    }

    @Test
    @DisplayName("every permitted type is a record")
    void allRecords() {
        assertTrue(Arrays.stream(SourceWhich.class.getPermittedSubclasses()).allMatch(Class::isRecord));
    }

    @Test
    @DisplayName("a record keeps the very reference it was given")
    void keepsReference() {
        Trap trap = new Trap();
        ItemObject object = new ItemObject();

        assertSame(trap, new SourceWhich.TrapRecord(trap).trap());
        assertSame(object, new SourceWhich.ObjectRecord(object).object());
    }

    @Test
    @DisplayName("a null payload is accepted, as C's NULL pointer and non-positive monster index are")
    void nullPayloads() {
        assertNull(new SourceWhich.TrapRecord(null).trap());
        assertNull(new SourceWhich.MonsterRecord(null).monster());
        assertNull(new SourceWhich.ObjectRecord(null).object());
        assertNull(new SourceWhich.ChestTrapRecord(null).chestTrap());
    }

    @Test
    @DisplayName("records of different kinds are never equal, even when both are empty")
    void differentKindsNotEqual() {
        assertNotEquals(new SourceWhich.TrapRecord(null), new SourceWhich.ObjectRecord(null));
        assertNotEquals(new SourceWhich.MonsterRecord(null), new SourceWhich.ChestTrapRecord(null));
    }

    @Test
    @DisplayName("records of the same kind are equal when they hold the same reference")
    void sameKindEqual() {
        Trap trap = new Trap();

        assertEquals(new SourceWhich.TrapRecord(trap), new SourceWhich.TrapRecord(trap));
        assertNotEquals(new SourceWhich.TrapRecord(trap), new SourceWhich.TrapRecord(new Trap()));
    }

    @Test
    @DisplayName("a switch over SourceWhich is exhaustive with four cases and no default")
    void exhaustiveSwitch() {
        SourceWhich which = new SourceWhich.ObjectRecord(new ItemObject());

        String kind = switch (which) {
            case SourceWhich.TrapRecord t -> "trap";
            case SourceWhich.MonsterRecord m -> "monster";
            case SourceWhich.ObjectRecord o -> "object";
            case SourceWhich.ChestTrapRecord c -> "chest_trap";
        };

        assertEquals("object", kind);
    }
}
