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
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.magic.ClassMagic;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.player.enums.PlayerFlag;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests {@link PlayerClass#getTitle(int)} and {@link PlayerClass#getNoTitles()}, the port of C's
 * {@code class->title[(lev - 1) / 5]}.
 *
 * <p>C holds ten title slots and indexes them with integer division, so each title covers five
 * levels: levels 1 to 5 read slot 0 and levels 46 to 50 read slot 9. A slot the file left empty is
 * NULL. The expected values come from working that index out by hand, not from the port. Levels
 * above 50 and below 1 are the port's own handling, as C never indexes there: the character sheet
 * returns a winner string first, and a level below 1 does not occur.
 *
 * <p>Class PlayerClassGetTitleTest coded on 261010, commented in full on 261010.
 *
 * @author Rowan Crowther
 */
class PlayerClassGetTitleTest {

    /**
     * A class with the given titles and nothing else of interest.
     *
     * @param titles the class's titles
     * @return the class
     */
    private static PlayerClass playerClass(List<String> titles) {
        return new PlayerClass("Tester", titles, Map.of(), Map.of(), Map.of(), 0, 0,
                new Flag<>(ObjectFlag.class), new Flag<>(PlayerFlag.class), 5, 30, 5,
                List.of(), ClassMagic.NONE);
    }

    /**
     * Ten titles, named for their slot, so that a result says which slot was read.
     *
     * @return the titles T0 to T9
     */
    private static List<String> tenTitles() {
        return List.of("T0", "T1", "T2", "T3", "T4", "T5", "T6", "T7", "T8", "T9");
    }

    /**
     * The edges of each five-level band: (1 - 1) / 5 = 0 and (5 - 1) / 5 = 0, then (6 - 1) / 5 = 1,
     * up to (50 - 1) / 5 = 9.
     */
    @Test
    @DisplayName("each title covers five levels")
    void bandsOfFive() {
        PlayerClass c = playerClass(tenTitles());

        assertAll(
                () -> assertEquals("T0", c.getTitle(1)),
                () -> assertEquals("T0", c.getTitle(5)),
                () -> assertEquals("T1", c.getTitle(6)),
                () -> assertEquals("T1", c.getTitle(10)),
                () -> assertEquals("T2", c.getTitle(11)),
                () -> assertEquals("T8", c.getTitle(45)),
                () -> assertEquals("T9", c.getTitle(46)),
                () -> assertEquals("T9", c.getTitle(50)));
    }

    /**
     * Level 0 gives (0 - 1) / 5 = -1 / 5 = 0 in both C and Java, because integer division truncates
     * toward zero, so it reads slot 0. A level further below 1 has no C meaning and reads slot 0.
     */
    @Test
    @DisplayName("a level below 1 reads the first title")
    void belowLevelOne() {
        PlayerClass c = playerClass(tenTitles());

        assertAll(
                () -> assertEquals("T0", c.getTitle(0)),
                () -> assertEquals("T0", c.getTitle(-4)),
                () -> assertEquals("T0", c.getTitle(-100)));
    }

    /**
     * Level 51 gives slot 10, which a ten-title class does not have, so the answer is null as C's
     * empty slot is; C's callers never reach it for the character sheet.
     */
    @Test
    @DisplayName("a level above 50 has no title")
    void aboveLevelFifty() {
        PlayerClass c = playerClass(tenTitles());

        assertAll(
                () -> assertNull(c.getTitle(51)),
                () -> assertNull(c.getTitle(100)));
    }

    /**
     * Three titles fill slots 0 to 2 and leave the rest NULL. Level 15 is slot 2, the last filled;
     * level 16 is slot 3, which is empty. The earlier levels still answer, as C's would.
     */
    @Test
    @DisplayName("a short list answers up to its last title and then null")
    void shortList() {
        PlayerClass c = playerClass(List.of("A", "B", "C"));

        assertAll(
                () -> assertEquals("A", c.getTitle(1)),
                () -> assertEquals("B", c.getTitle(6)),
                () -> assertEquals("C", c.getTitle(15)),
                () -> assertNull(c.getTitle(16)),
                () -> assertNull(c.getTitle(50)));
    }

    /**
     * A class with no titles has every slot NULL.
     */
    @Test
    @DisplayName("a class with no titles answers null at every level")
    void noTitles() {
        PlayerClass c = playerClass(List.of());

        assertAll(
                () -> assertNull(c.getTitle(1)),
                () -> assertNull(c.getTitle(50)),
                () -> assertEquals(0, c.getNoTitles()));
    }

    /**
     * The count is the number of titles loaded, not C's fixed ten.
     */
    @Test
    @DisplayName("getNoTitles counts the titles loaded")
    void countsLoadedTitles() {
        assertAll(
                () -> assertEquals(10, playerClass(tenTitles()).getNoTitles()),
                () -> assertEquals(3, playerClass(List.of("A", "B", "C")).getNoTitles()));
    }
}
