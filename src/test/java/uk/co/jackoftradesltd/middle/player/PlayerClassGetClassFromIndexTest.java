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

package uk.co.jackoftradesltd.middle.player;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.game.globals.registry.PlayerRegistry;
import uk.co.jackoftradesltd.middle.magic.ClassMagic;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.player.enums.PlayerFlag;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Tests {@link PlayerClass#getClassFromIndex(int)}, the port of C's {@code player_id2class}
 * ({@code player-class.c:22-29}).
 *
 * <p>C searches a linked list for the node whose {@code cidx} equals the requested id and falls
 * back to {@code NULL} when the search runs out. The expected values here come from tracing that
 * search directly, not from the port: {@code cidx} is assigned by counting down from the list head
 * once parsing finishes ({@code init.c:4128-4139}), and because the list is built by prepending
 * during parsing, the count-down puts the data file's first class at {@code cidx} 0 and its last at
 * {@code cidx count - 1}. {@link PlayerRegistry#getPlayerClasses()} keeps its list in that same file
 * order, so a fixture list's position doubles as the {@code cidx} it is standing in for.
 *
 * <p>{@code guid} is an unsigned int ({@code guid.h:22}), so C has no negative-id case: a caller's
 * negative intent arrives as a value no {@code cidx} can equal, and the search still ends in
 * {@code NULL}. The negative-index cases below are the port's equivalent of that miss.
 *
 * <p>Each test seeds the registry's private class list directly and restores whatever was there
 * beforehand, so this class carries no dependency on suite ordering or on {@code class.txt} having
 * been parsed.
 *
 * <p>Class PlayerClassGetClassFromIndexTest coded on 260907, commented in full on 260907.
 *
 * @author Rowan Crowther
 */
class PlayerClassGetClassFromIndexTest {

    /**
     * Whatever the registry held before this test seeded it, restored in {@link #restoreRegistry}.
     */
    private List<PlayerClass> previousClasses;

    /**
     * A class whose only interesting property is its name, so a returned instance can be told
     * apart from its neighbours in the fixture list by identity.
     *
     * @param name the class's name
     * @return the class
     */
    private static PlayerClass playerClass(String name) {
        return new PlayerClass(name, List.of(), Map.of(), Map.of(), Map.of(), 0, 0,
                new Flag<>(ObjectFlag.class), new Flag<>(PlayerFlag.class), 5, 30, 5,
                List.of(), ClassMagic.NONE);
    }

    /**
     * Reaches {@code PlayerRegistry.playerClasses}, so a test can both read whatever the suite left
     * behind and put it back afterwards.
     *
     * @return the accessible field
     * @throws ReflectiveOperationException if the field cannot be reached
     */
    private static Field classesField() throws ReflectiveOperationException {
        Field field = PlayerRegistry.class.getDeclaredField("playerClasses");
        field.setAccessible(true);
        return field;
    }

    @BeforeEach
    void seedRegistry() throws Exception {
        previousClasses = (List<PlayerClass>) classesField().get(null);
    }

    @AfterEach
    void restoreRegistry() throws Exception {
        classesField().set(null, previousClasses);
    }

    @Nested
    @DisplayName("an index within the loaded class count")
    class InRangeIndex {

        @Test
        @DisplayName("index 0 answers the first class in file order, C's cidx 0")
        void firstIndexAnswersFirstClass() {
            PlayerClass warrior = playerClass("Warrior");
            PlayerClass mage = playerClass("Mage");
            PlayerRegistry.setPlayerClasses(List.of(warrior, mage));

            assertSame(warrior, PlayerClass.getClassFromIndex(0));
        }

        @Test
        @DisplayName("the last index answers the last class in file order, C's cidx count - 1")
        void lastIndexAnswersLastClass() {
            PlayerClass warrior = playerClass("Warrior");
            PlayerClass mage = playerClass("Mage");
            PlayerClass rogue = playerClass("Rogue");
            PlayerRegistry.setPlayerClasses(List.of(warrior, mage, rogue));

            assertSame(rogue, PlayerClass.getClassFromIndex(2));
        }

        @Test
        @DisplayName("a middle index answers the matching middle class")
        void middleIndexAnswersMiddleClass() {
            PlayerClass warrior = playerClass("Warrior");
            PlayerClass mage = playerClass("Mage");
            PlayerClass rogue = playerClass("Rogue");
            PlayerRegistry.setPlayerClasses(List.of(warrior, mage, rogue));

            assertSame(mage, PlayerClass.getClassFromIndex(1));
        }
    }

    @Nested
    @DisplayName("an index at or past the loaded class count - C's search falling off the list")
    class OutOfRangeHighIndex {

        @Test
        @DisplayName("an index equal to the class count answers null")
        void indexEqualToSizeAnswersNull() {
            PlayerRegistry.setPlayerClasses(List.of(playerClass("Warrior"), playerClass("Mage")));

            assertNull(PlayerClass.getClassFromIndex(2));
        }

        @Test
        @DisplayName("an index well past the class count answers null")
        void indexWellPastSizeAnswersNull() {
            PlayerRegistry.setPlayerClasses(List.of(playerClass("Warrior")));

            assertNull(PlayerClass.getClassFromIndex(50));
        }

        @Test
        @DisplayName("index 0 against an empty class list answers null")
        void indexZeroAgainstEmptyListAnswersNull() {
            PlayerRegistry.setPlayerClasses(List.of());

            assertNull(PlayerClass.getClassFromIndex(0));
        }
    }

    @Nested
    @DisplayName("a negative index - the port's guard for C's unsigned guid never matching")
    class NegativeIndex {

        @Test
        @DisplayName("index -1 answers null rather than throwing")
        void minusOneAnswersNull() {
            PlayerRegistry.setPlayerClasses(List.of(playerClass("Warrior")));

            assertNull(PlayerClass.getClassFromIndex(-1));
        }

        @Test
        @DisplayName("the most negative int answers null rather than throwing")
        void minValueAnswersNull() {
            PlayerRegistry.setPlayerClasses(List.of(playerClass("Warrior")));

            assertNull(PlayerClass.getClassFromIndex(Integer.MIN_VALUE));
        }
    }
}
