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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.co.jackoftradesltd.middle.game.globals.registry.WorldRegistry;
import uk.co.jackoftradesltd.middle.monsters.MonsterRace;
import uk.co.jackoftradesltd.testsupport.SeededPlayerRegistry;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Tests {@link PlayerQuest#playerQuestsReset} and {@link Quest#copy()} together, the ports of C's
 * {@code player_quests_reset} and the four field assignments in its copy loop
 * ({@code player-quest.c:158-172}).
 *
 * <p>The values every expectation is read off {@code struct quest}
 * ({@code name, level, race, cur_num, max_num, index}) and the loop that fills a fresh, freshly
 * {@code mem_zalloc}'d array from the shared {@code quests[]} table: {@code name}, {@code level},
 * {@code race} and {@code max_num} are assigned; {@code index} and {@code cur_num} are not, so they
 * come back at the zero the allocation gave them regardless of what the source held.
 *
 * <p><b>{@code race} is the one field the copy deliberately does not deepen.</b> C copies the
 * pointer, not the struct it points to, so a player's quest and the shared template - and every
 * monster later built from the same {@code r_info} entry - are the same object. The still-unported
 * {@code quest_check} depends on that identity ({@code m->race == p->quests[i].race},
 * {@code player-quest.c:229}), so {@link #raceIsSharedNotCopied()} is the test that matters most in
 * this suite: it is the one a plausible-looking {@code race.copy()} would silently fail.
 *
 * <p>{@link Player}'s constructor needs a loaded race and body to succeed at all, which is what
 * {@link SeededPlayerRegistry} is for; {@link WorldRegistry}'s quest list is global static state
 * shared with the parser suites, so it is saved and restored around every test the way
 * {@code PlayerClearTimedTest} treats the timed-effect registry.
 *
 * <p>Class PlayerQuestResetTest coded on 260903, commented in full on 260903.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
@DisplayName("PlayerQuest.playerQuestsReset / Quest.copy")
class PlayerQuestResetTest {

    /**
     * The player under test.
     */
    private Player player;

    /**
     * Whatever {@link WorldRegistry} held before this test, put back afterwards - the registry is
     * global static state shared with the quest-parser suites.
     */
    private List<Quest> savedQuests;

    /**
     * @return {@link WorldRegistry}'s private quest list, made accessible
     * @throws ReflectiveOperationException if the field cannot be reached
     */
    private static Field questsField() throws ReflectiveOperationException {
        Field f = WorldRegistry.class.getDeclaredField("quests");
        f.setAccessible(true);
        return f;
    }

    /**
     * A convenience constructor call - {@link Quest} has no builder, so this just names the
     * positional arguments at each call site.
     */
    private static Quest quest(int index, String name, int level, MonsterRace race,
                               int currentNumber, int maxNumber) {
        return new Quest(index, name, level, race, currentNumber, maxNumber);
    }

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() throws Exception {
        savedQuests = (List<Quest>) questsField().get(null);
        player = new Player();
    }

    @AfterEach
    void tearDown() throws Exception {
        questsField().set(null, savedQuests);
    }

    /**
     * The player's list comes out the same length as the standard set, one entry per quest -
     * {@code player_quests_reset}'s loop bound is {@code z_info->quest_max}, the size of the shared
     * array.
     */
    @Test
    @DisplayName("the player's quest list has one entry per standard quest")
    void copiesEveryStandardQuest() {
        WorldRegistry.setQuests(List.of(
                quest(0, "Sauron, the Sorcerer", 100, new MonsterRace(), 0, 1),
                quest(1, "Morgoth, Lord of Darkness", 127, new MonsterRace(), 0, 1)));

        PlayerQuest.playerQuestsReset(player);

        assertEquals(2, player.getQuests().size());
    }

    /**
     * {@code name}, {@code level} and {@code max_num} are copied by value, matching C's
     * {@code string_make}, plain assignment and plain assignment respectively ({@code player-quest.c:167-170}).
     */
    @Test
    @DisplayName("name, level and max_num are carried over unchanged")
    void carriesTheDescriptiveFieldsOver() {
        WorldRegistry.setQuests(List.of(
                quest(1, "Morgoth, Lord of Darkness", 127, new MonsterRace(), 0, 1)));

        PlayerQuest.playerQuestsReset(player);

        Quest copy = player.getQuests().get(0);
        assertEquals("Morgoth, Lord of Darkness", copy.getName());
        assertEquals(127, copy.getLevel());
        assertEquals(1, copy.getMaxNumber());
    }

    /**
     * C copies {@code race} as a bare pointer ({@code p->quests[i].race = quests[i].race;},
     * {@code player-quest.c:169}), so the player's quest and the shared template point at the same
     * {@code struct monster_race}. The port must do the same rather than deep-copy: the still-unported
     * {@code quest_check} matches a kill with {@code m->race == p->quests[i].race}
     * ({@code player-quest.c:229}), an identity test that a copied race object could never satisfy.
     */
    @Test
    @DisplayName("race is the same object as the world's, not a copy")
    void raceIsSharedNotCopied() {
        MonsterRace morgothRace = new MonsterRace();
        WorldRegistry.setQuests(List.of(
                quest(1, "Morgoth, Lord of Darkness", 127, morgothRace, 0, 1)));

        PlayerQuest.playerQuestsReset(player);

        assertSame(morgothRace, player.getQuests().get(0).getRace(),
                "race must be the exact object a killed monster's race will be compared against");
    }

    /**
     * C's array is {@code mem_zalloc}'d before the copy loop runs, and the loop only ever assigns
     * {@code name}, {@code level}, {@code race} and {@code max_num} - {@code index} and {@code cur_num}
     * are left at the zero the allocation gave them, whatever the source quest held. The template here
     * is deliberately built with a non-zero index and a non-zero {@code currentNumber} so the test
     * would fail if either one leaked through the copy.
     */
    @Test
    @DisplayName("index and cur_num come back zeroed, matching C's mem_zalloc'd array")
    void indexAndCurrentNumberAreZeroed() {
        WorldRegistry.setQuests(List.of(
                quest(1, "Morgoth, Lord of Darkness", 127, new MonsterRace(), 1, 1)));

        PlayerQuest.playerQuestsReset(player);

        Quest copy = player.getQuests().get(0);
        assertEquals(0, copy.getIndex());
        assertEquals(0, copy.getCurrentNumber());
    }

    /**
     * The player's quest objects are their own, not the shared template's - the whole point of
     * porting a per-field copy instead of handing back the world's own {@link Quest} references.
     */
    @Test
    @DisplayName("the player's quests are independent objects, not the world's own")
    void producesIndependentQuestObjects() {
        Quest template = quest(1, "Morgoth, Lord of Darkness", 127, new MonsterRace(), 0, 1);
        WorldRegistry.setQuests(List.of(template));

        PlayerQuest.playerQuestsReset(player);

        assertNotSame(template, player.getQuests().get(0));
    }

    /**
     * Resetting twice replaces the list rather than appending to it, matching C's
     * {@code if (p->quests) player_quests_free(p);} guard before the fresh allocation - the second
     * reset does not double up on entries.
     */
    @Test
    @DisplayName("resetting twice replaces the list rather than appending to it")
    void resettingTwiceReplacesRatherThanAppends() {
        WorldRegistry.setQuests(List.of(quest(0, "Sauron, the Sorcerer", 100, new MonsterRace(), 0, 1)));

        PlayerQuest.playerQuestsReset(player);
        PlayerQuest.playerQuestsReset(player);

        assertEquals(1, player.getQuests().size());
    }
}
