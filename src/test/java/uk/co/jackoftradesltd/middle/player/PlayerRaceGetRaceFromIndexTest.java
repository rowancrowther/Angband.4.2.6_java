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
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.player.enums.PlayerFlag;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Tests {@link PlayerRace#getRaceFromIndex(int)}, the port of C's {@code player_id2race}
 * ({@code player-race.c:21-28}).
 *
 * <p>C searches a linked list for the node whose {@code ridx} equals the requested id and falls
 * back to {@code NULL} when the search runs out. The expected values here come from tracing that
 * search directly, not from the port: {@code ridx} is assigned by counting down from the list head
 * once parsing finishes ({@code init.c:2821-2832}), and because the list is built by prepending
 * during parsing, the count-down puts the data file's first race at {@code ridx} 0 and its last at
 * {@code ridx count - 1}. {@link PlayerRegistry#getPlayerRaces()} keeps its list in that same file
 * order, so a fixture list's position doubles as the {@code ridx} it is standing in for.
 *
 * <p>{@code guid} is an unsigned int ({@code guid.h:22}), so C has no negative-id case: a caller's
 * negative intent arrives as a value no {@code ridx} can equal, and the search still ends in
 * {@code NULL}. The negative-index cases below are the port's equivalent of that miss.
 *
 * <p>Each test seeds the registry's private race list directly and restores whatever was there
 * beforehand, so this class carries no dependency on suite ordering or on {@code p_race.txt} having
 * been parsed.
 *
 * <p>Class PlayerRaceGetRaceFromIndexTest coded on 260907, commented in full on 260907.
 *
 * @author Rowan Crowther
 */
class PlayerRaceGetRaceFromIndexTest {

    /**
     * Whatever the registry held before this test seeded it, restored in {@link #restoreRegistry}.
     */
    private List<PlayerRace> previousRaces;

    /**
     * A race whose only interesting property is its name, so a returned instance can be told apart
     * from its neighbours in the fixture list by identity.
     *
     * @param name the race's name
     * @return the race
     */
    private static PlayerRace playerRace(String name) {
        return new PlayerRace(name, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, null,
                Map.of(), Map.of(), new Flag<>(ObjectFlag.class), new Flag<>(PlayerFlag.class),
                null, Map.of());
    }

    /**
     * Reaches {@code PlayerRegistry.playerRaces}, so a test can both read whatever the suite left
     * behind and put it back afterwards.
     *
     * @return the accessible field
     * @throws ReflectiveOperationException if the field cannot be reached
     */
    private static Field racesField() throws ReflectiveOperationException {
        Field field = PlayerRegistry.class.getDeclaredField("playerRaces");
        field.setAccessible(true);
        return field;
    }

    @BeforeEach
    void seedRegistry() throws Exception {
        previousRaces = (List<PlayerRace>) racesField().get(null);
    }

    @AfterEach
    void restoreRegistry() throws Exception {
        racesField().set(null, previousRaces);
    }

    @Nested
    @DisplayName("an index within the loaded race count")
    class InRangeIndex {

        @Test
        @DisplayName("index 0 answers the first race in file order, C's ridx 0")
        void firstIndexAnswersFirstRace() {
            PlayerRace human = playerRace("Human");
            PlayerRace elf = playerRace("Elf");
            PlayerRegistry.setPlayerRaces(List.of(human, elf));

            assertSame(human, PlayerRace.getRaceFromIndex(0));
        }

        @Test
        @DisplayName("the last index answers the last race in file order, C's ridx count - 1")
        void lastIndexAnswersLastRace() {
            PlayerRace human = playerRace("Human");
            PlayerRace elf = playerRace("Elf");
            PlayerRace dwarf = playerRace("Dwarf");
            PlayerRegistry.setPlayerRaces(List.of(human, elf, dwarf));

            assertSame(dwarf, PlayerRace.getRaceFromIndex(2));
        }

        @Test
        @DisplayName("a middle index answers the matching middle race")
        void middleIndexAnswersMiddleRace() {
            PlayerRace human = playerRace("Human");
            PlayerRace elf = playerRace("Elf");
            PlayerRace dwarf = playerRace("Dwarf");
            PlayerRegistry.setPlayerRaces(List.of(human, elf, dwarf));

            assertSame(elf, PlayerRace.getRaceFromIndex(1));
        }
    }

    @Nested
    @DisplayName("an index at or past the loaded race count - C's search falling off the list")
    class OutOfRangeHighIndex {

        @Test
        @DisplayName("an index equal to the race count answers null")
        void indexEqualToSizeAnswersNull() {
            PlayerRegistry.setPlayerRaces(List.of(playerRace("Human"), playerRace("Elf")));

            assertNull(PlayerRace.getRaceFromIndex(2));
        }

        @Test
        @DisplayName("an index well past the race count answers null")
        void indexWellPastSizeAnswersNull() {
            PlayerRegistry.setPlayerRaces(List.of(playerRace("Human")));

            assertNull(PlayerRace.getRaceFromIndex(50));
        }

        @Test
        @DisplayName("index 0 against an empty race list answers null")
        void indexZeroAgainstEmptyListAnswersNull() {
            PlayerRegistry.setPlayerRaces(List.of());

            assertNull(PlayerRace.getRaceFromIndex(0));
        }
    }

    @Nested
    @DisplayName("a negative index - the port's guard for C's unsigned guid never matching")
    class NegativeIndex {

        @Test
        @DisplayName("index -1 answers null rather than throwing")
        void minusOneAnswersNull() {
            PlayerRegistry.setPlayerRaces(List.of(playerRace("Human")));

            assertNull(PlayerRace.getRaceFromIndex(-1));
        }

        @Test
        @DisplayName("the most negative int answers null rather than throwing")
        void minValueAnswersNull() {
            PlayerRegistry.setPlayerRaces(List.of(playerRace("Human")));

            assertNull(PlayerRace.getRaceFromIndex(Integer.MIN_VALUE));
        }
    }
}
