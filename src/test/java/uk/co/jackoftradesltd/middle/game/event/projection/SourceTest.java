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
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.middle.cave.Trap;
import uk.co.jackoftradesltd.middle.monsters.Monster;
import uk.co.jackoftradesltd.middle.objects.ChestTrap;
import uk.co.jackoftradesltd.middle.objects.ItemObject;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Tests the six factory methods on {@link Source}, the port of C's {@code source.c}.
 *
 * <p>Each C factory does exactly two things: it stamps the discriminant, and — for the four that
 * take an argument — it stores that argument in the matching arm of the union. The expected values
 * below come from reading those six functions, so every test asserts the discriminant and the
 * payload separately, and the two payload-free factories assert that nothing is carried.
 *
 * <p>C leaves {@code which} uninitialised for {@code SRC_NONE} and {@code SRC_PLAYER}, so there is
 * no C value to compare against for those; what the tests pin down instead is the contract the
 * port chose in its place, that {@code which} is {@code null} and never a stale payload.
 *
 * <p>C stores a monster as an index and resolves it through {@code cave_monster}, which answers
 * {@code NULL} for an index of zero or less. The port stores the monster itself, so those sentinel
 * indices arrive as a {@code null} monster — hence the test that {@code sourceMonster(null)} still
 * builds a properly discriminated {@code SRC_MONSTER} source rather than being rejected.
 *
 * <p>Class SourceTest written on 260829.
 */
@DisplayName("Source factories")
class SourceTest {

    private static Monster monster() {
        return new Monster(null, null, null, 0, 0, null, 0, 0, 0, null,
                null, null, null, null, null, null, null, 0, 0);
    }

    private static ChestTrap chestTrap() {
        return new ChestTrap("gas trap", null, 1, List.of(), false, false, "hiss", "death");
    }

    @Nested
    @DisplayName("the payload-free sources")
    class PayloadFree {

        @Test
        @DisplayName("sourceNone stamps SRC_NONE and carries nothing")
        void sourceNone() {
            Source source = Source.sourceNone();

            assertEquals(SourceWhat.SRC_NONE, source.what());
            assertNull(source.which());
        }

        @Test
        @DisplayName("sourcePlayer stamps SRC_PLAYER and carries nothing")
        void sourcePlayer() {
            Source source = Source.sourcePlayer();

            assertEquals(SourceWhat.SRC_PLAYER, source.what());
            assertNull(source.which());
        }

        @Test
        @DisplayName("the two payload-free sources are not interchangeable")
        void noneIsNotPlayer() {
            assertNotEquals(Source.sourceNone(), Source.sourcePlayer());
        }
    }

    @Nested
    @DisplayName("the sources that carry a payload")
    class WithPayload {

        @Test
        @DisplayName("sourceTrap stamps SRC_TRAP and carries the trap it was given")
        void sourceTrap() {
            Trap trap = new Trap();

            Source source = Source.sourceTrap(trap);

            assertEquals(SourceWhat.SRC_TRAP, source.what());
            SourceWhich.TrapRecord which = assertInstanceOf(SourceWhich.TrapRecord.class, source.which());
            assertSame(trap, which.trap());
        }

        @Test
        @DisplayName("sourceMonster stamps SRC_MONSTER and carries the monster it was given")
        void sourceMonster() {
            Monster monster = monster();

            Source source = Source.sourceMonster(monster);

            assertEquals(SourceWhat.SRC_MONSTER, source.what());
            SourceWhich.MonsterRecord which = assertInstanceOf(SourceWhich.MonsterRecord.class, source.which());
            assertSame(monster, which.monster());
        }

        @Test
        @DisplayName("sourceObject stamps SRC_OBJECT and carries the object it was given")
        void sourceObject() {
            ItemObject object = new ItemObject();

            Source source = Source.sourceObject(object);

            assertEquals(SourceWhat.SRC_OBJECT, source.what());
            SourceWhich.ObjectRecord which = assertInstanceOf(SourceWhich.ObjectRecord.class, source.which());
            assertSame(object, which.object());
        }

        @Test
        @DisplayName("sourceChestTrap stamps SRC_CHEST_TRAP and carries the chest trap it was given")
        void sourceChestTrap() {
            ChestTrap chestTrap = chestTrap();

            Source source = Source.sourceChestTrap(chestTrap);

            assertEquals(SourceWhat.SRC_CHEST_TRAP, source.what());
            SourceWhich.ChestTrapRecord which =
                    assertInstanceOf(SourceWhich.ChestTrapRecord.class, source.which());
            assertSame(chestTrap, which.chestTrap());
        }

        @Test
        @DisplayName("a floor trap and a chest trap are different kinds of source")
        void trapIsNotChestTrap() {
            assertNotEquals(Source.sourceTrap(new Trap()).what(), Source.sourceChestTrap(chestTrap()).what());
        }
    }

    @Nested
    @DisplayName("the sentinel monster indices C resolves to NULL")
    class NullMonster {

        @Test
        @DisplayName("sourceMonster(null) is still an SRC_MONSTER source carrying an empty record")
        void nullMonsterIsStillAMonsterSource() {
            Source source = Source.sourceMonster(null);

            assertEquals(SourceWhat.SRC_MONSTER, source.what());
            SourceWhich.MonsterRecord which = assertInstanceOf(SourceWhich.MonsterRecord.class, source.which());
            assertNull(which.monster());
        }

        @Test
        @DisplayName("a null-monster source is not the same as a source with no payload at all")
        void nullMonsterIsNotSourceNone() {
            assertNotEquals(Source.sourceNone(), Source.sourceMonster(null));
        }
    }

    @Nested
    @DisplayName("the discriminant ordering matches the C enum")
    class Discriminant {

        @Test
        @DisplayName("SourceWhat declares the same six constants in the same order as source.h")
        void ordinalsMatchC() {
            assertEquals(0, SourceWhat.SRC_NONE.ordinal());
            assertEquals(1, SourceWhat.SRC_TRAP.ordinal());
            assertEquals(2, SourceWhat.SRC_PLAYER.ordinal());
            assertEquals(3, SourceWhat.SRC_MONSTER.ordinal());
            assertEquals(4, SourceWhat.SRC_OBJECT.ordinal());
            assertEquals(5, SourceWhat.SRC_CHEST_TRAP.ordinal());
            assertEquals(6, SourceWhat.values().length);
        }
    }
}
