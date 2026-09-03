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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.co.jackoftradesltd.testsupport.SeededPlayerRegistry;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Tests {@link Player#setQuests(ArrayList)} and {@link Player#setUpkeep(PlayerUpkeep)} — the ports
 * of the plain field writes C makes to {@code p->quests} and {@code p->upkeep}. Neither C assignment
 * validates or copies what it is given; both simply replace whatever the field held, and these tests
 * check that the port does the same: the exact reference handed in is what the paired getter hands
 * back, and a second write discards the first without merging into it.
 *
 * <p>Class PlayerQuestsAndUpkeepAccessorTest coded on 260903, commented in full on 260903.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class PlayerQuestsAndUpkeepAccessorTest {

    /**
     * The character whose fields are written, fresh for each test.
     */
    private Player player;

    /**
     * Builds a new character.
     */
    @BeforeEach
    void build() {
        player = new Player();
    }

    /**
     * {@link Player#setQuests(ArrayList)}, the port of {@code p->quests = ...}
     * ({@code player-quest.c:164}, among other assignment sites).
     */
    @Nested
    @DisplayName("setQuests")
    class SetQuests {

        /**
         * The list handed in is the list {@link Player#getQuests()} hands back — same reference,
         * same elements, in order. C's assignment is a pointer swap, not a copy.
         */
        @Test
        @DisplayName("stores the given list by identity")
        void storesByIdentity() {
            Quest quest = new Quest(0, "Sauron, the Sorcerer", 100, null, 0, 1);
            ArrayList<Quest> quests = new ArrayList<>(List.of(quest));

            player.setQuests(quests);

            List<Quest> result = player.getQuests();
            assertEquals(1, result.size());
            assertSame(quest, result.get(0));
        }

        /**
         * A second call replaces the first list outright. C's {@code player_quests_reset} frees the
         * old array before allocating a new one ({@code player-quest.c:161-164}); the port has
         * nothing to free, but the observable effect is the same — the old entries are gone, not
         * merged with the new ones.
         */
        @Test
        @DisplayName("a second write replaces the first")
        void secondWriteReplaces() {
            Quest first = new Quest(0, "Sauron, the Sorcerer", 100, null, 0, 1);
            Quest second = new Quest(1, "The Cloning Pits", 40, null, 0, 1);

            player.setQuests(new ArrayList<>(List.of(first)));
            player.setQuests(new ArrayList<>(List.of(second)));

            List<Quest> result = player.getQuests();
            assertEquals(1, result.size());
            assertSame(second, result.get(0));
        }

        /**
         * An empty list is a legal replacement, not an error - C's array can be
         * {@code z_info->quest_max} long with every slot zeroed, which is what an empty
         * {@code ArrayList} models on the port's side.
         */
        @Test
        @DisplayName("accepts an empty list")
        void acceptsEmptyList() {
            player.setQuests(new ArrayList<>());
            assertEquals(0, player.getQuests().size());
        }
    }

    /**
     * {@link Player#setUpkeep(PlayerUpkeep)}, the port of {@code p->upkeep = ...}
     * ({@code player.c:494}, {@code player-birth.c:432}).
     */
    @Nested
    @DisplayName("setUpkeep")
    class SetUpkeep {

        /**
         * The struct handed in is the struct {@link Player#getPlayerUpkeep()} hands back - the same
         * reference, not a copy. C assigns the pointer C's {@code mem_zalloc} returned; nothing
         * about the assignment itself inspects or duplicates the struct's contents.
         */
        @Test
        @DisplayName("stores the given struct by identity")
        void storesByIdentity() {
            PlayerUpkeep upkeep = new PlayerUpkeep();
            player.setUpkeep(upkeep);
            assertSame(upkeep, player.getPlayerUpkeep());
        }

        /**
         * A second call replaces the first struct outright, discarding it rather than merging
         * fields into it - {@code init_player} and {@code player_birth} both {@code mem_zalloc} a
         * fresh struct rather than reusing one, so C never has two live upkeep structs for the
         * same character to merge between.
         */
        @Test
        @DisplayName("a second write replaces the first")
        void secondWriteReplaces() {
            PlayerUpkeep first = new PlayerUpkeep();
            PlayerUpkeep second = new PlayerUpkeep();

            player.setUpkeep(first);
            player.setUpkeep(second);

            assertSame(second, player.getPlayerUpkeep());
            assertNotSame(first, player.getPlayerUpkeep());
        }
    }
}
