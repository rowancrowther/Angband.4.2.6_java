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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.middle.monsters.MonsterRace;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests {@link Quest} on its own: the constructor and getters, which carry the six fields of C's
 * {@code struct quest} ({@code player.h}), and {@link Quest#copy()}, the port of the four field
 * assignments in {@code player_quests_reset} ({@code player-quest.c}).
 *
 * <p>{@link PlayerQuestResetTest} already drives {@code copy()} through {@code playerQuestsReset}
 * with a {@link Player}; this suite needs no player and no registry, so it can cover the cases that
 * route cannot reach cheaply: a template whose quest is already complete, a kill count above one,
 * and copying a copy. Expected values come from the C loop body ({@code name}, {@code level},
 * {@code race} and {@code max_num} assigned; {@code index} and {@code cur_num} left at the zero
 * {@code mem_zalloc} gave them) and from the shipped {@code quest.txt} (Sauron, level 99; Morgoth,
 * level 100; one kill each).
 *
 * <p>Class QuestTest coded on 261009, commented in full on 261009.
 *
 * @author Rowan Crowther
 */
@DisplayName("Quest")
class QuestTest {

    /**
     * Every constructor argument comes back from its matching getter, in the right slot - the six
     * arguments are positional and mostly {@code int}s, so a transposed pair would otherwise go
     * unnoticed.
     */
    @Test
    @DisplayName("the constructor stores each field and the getters return it")
    void constructorAndGettersRoundTrip() {
        MonsterRace race = new MonsterRace();

        Quest quest = new Quest(3, "Sauron", 99, race, 2, 5);

        assertEquals(3, quest.getIndex());
        assertEquals("Sauron", quest.getName());
        assertEquals(99, quest.getLevel());
        assertSame(race, quest.getRace());
        assertEquals(2, quest.getCurrentNumber());
        assertEquals(5, quest.getMaxNumber());
    }

    /**
     * The shipped Sauron record: C copies name, level, race and max_num, and leaves index and
     * cur_num at zero. The template here carries index 0 and cur_num 0 as the assembler gives them,
     * so this is the ordinary birth-time path.
     */
    @Test
    @DisplayName("copying a freshly loaded template reproduces name, level and max_num")
    void copyOfFreshTemplate() {
        MonsterRace race = new MonsterRace();
        Quest template = new Quest(0, "Sauron", 99, race, 0, 1);

        Quest copy = template.copy();

        assertEquals("Sauron", copy.getName());
        assertEquals(99, copy.getLevel());
        assertEquals(1, copy.getMaxNumber());
        assertEquals(0, copy.getIndex());
        assertEquals(0, copy.getCurrentNumber());
    }

    /**
     * {@code index} is not in the loop body, so the second shipped quest (index 1 in the shared
     * list) comes back with index 0, not 1. A copy that carried the index across would put every
     * entry of the player's list at its template's position and disagree with C's zeroed array.
     */
    @Test
    @DisplayName("index is not copied: the second template's copy has index 0")
    void indexIsNotCopied() {
        Quest template = new Quest(1, "Morgoth", 100, new MonsterRace(), 0, 1);

        assertEquals(0, template.copy().getIndex());
    }

    /**
     * {@code cur_num} is not in the loop body either, so a template that somehow held progress
     * (here 1 of 3) yields a copy at zero.
     */
    @Test
    @DisplayName("cur_num is not copied: progress on the template does not reach the copy")
    void currentNumberIsNotCopied() {
        Quest template = new Quest(0, "Sauron", 99, new MonsterRace(), 1, 3);

        Quest copy = template.copy();

        assertEquals(0, copy.getCurrentNumber());
        assertEquals(3, copy.getMaxNumber());
    }

    /**
     * {@code max_num} is copied by value whatever its size; the shipped data only ever uses 1, so
     * a larger figure checks the copy is not hard-wiring that.
     */
    @Test
    @DisplayName("max_num above one is carried over")
    void maxNumberAboveOne() {
        Quest template = new Quest(0, "Sauron", 99, new MonsterRace(), 0, 7);

        assertEquals(7, template.copy().getMaxNumber());
    }

    /**
     * {@code level} is a plain assignment, so a template whose level is already 0 (the completed
     * marker) copies as 0 rather than being "repaired" to something else.
     */
    @Test
    @DisplayName("a level of 0 is copied as 0")
    void zeroLevelIsCopied() {
        Quest template = new Quest(0, "Sauron", 0, new MonsterRace(), 0, 1);

        assertEquals(0, template.copy().getLevel());
    }

    /**
     * C assigns {@code race} as a bare pointer, so the copy and the template share the one
     * {@link MonsterRace}. The suite's central identity check, here without a player in the way.
     */
    @Test
    @DisplayName("race is the same object in template and copy")
    void raceIsShared() {
        MonsterRace race = new MonsterRace();
        Quest template = new Quest(0, "Sauron", 99, race, 0, 1);

        assertSame(race, template.copy().getRace());
    }

    /**
     * The copy is a new {@link Quest}, so a later change to the character's entry (zeroing the
     * level on completion, once that is ported) cannot reach the shared template. Each call also
     * gives a distinct object.
     */
    @Test
    @DisplayName("copy returns a new object each call")
    void copyIsAFreshObject() {
        Quest template = new Quest(0, "Sauron", 99, new MonsterRace(), 0, 1);

        Quest first = template.copy();
        Quest second = template.copy();

        assertNotSame(template, first);
        assertNotSame(first, second);
    }

    /**
     * Copying a copy gives the same result as copying the template: after the first copy index and
     * cur_num are already zero and the remaining four fields carry over, so the operation is
     * stable.
     */
    @Test
    @DisplayName("copying a copy yields the same field values")
    void copyOfCopyIsStable() {
        MonsterRace race = new MonsterRace();
        Quest template = new Quest(1, "Morgoth", 100, race, 1, 1);

        Quest twice = template.copy().copy();

        assertEquals(0, twice.getIndex());
        assertEquals("Morgoth", twice.getName());
        assertEquals(100, twice.getLevel());
        assertSame(race, twice.getRace());
        assertEquals(0, twice.getCurrentNumber());
        assertEquals(1, twice.getMaxNumber());
    }
}
