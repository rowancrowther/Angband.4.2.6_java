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

package uk.co.jackoftradesltd.middle.game;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link GameWorld#setCharacterDungeon(boolean)} - the port of the direct assignments C makes
 * to its global {@code character_dungeon} ({@code bool}, {@code game-world.c:47}) at every call
 * site ({@code true} at {@code generate.c:1549} and {@code load.c:1541}; {@code false} at
 * {@code generate.c:1123}, {@code player-birth.c:1066}, {@code init.c:4529} and
 * {@code main-stats.c:1707}), since C keeps no single setter function for the global to diff
 * against. What is asserted is that the write is a bare one: no clamping, no relation to the
 * previous value, and readable straight back through {@link GameWorld#hasCharacterDungeon()}.
 *
 * <p>{@code characterDungeon} is a static field shared across the JVM, so each test saves the flag
 * beforehand and restores it afterwards to avoid leaking state into whichever test runs next.
 *
 * <p>Class GameWorldSetCharacterDungeonTest coded on 260907, commented in full on 260907.
 *
 * @author Rowan Crowther
 */
class GameWorldSetCharacterDungeonTest {

    /**
     * The flag's value before the test, restored afterwards.
     */
    private boolean savedCharacterDungeon;

    /**
     * Records the flag so it can be put back.
     */
    @BeforeEach
    void saveCharacterDungeon() {
        savedCharacterDungeon = GameWorld.hasCharacterDungeon();
    }

    /**
     * Restores the flag the test found on entry.
     */
    @AfterEach
    void restoreCharacterDungeon() {
        GameWorld.setCharacterDungeon(savedCharacterDungeon);
    }

    /**
     * The ordinary path: setting {@code true} comes back out of {@link GameWorld#hasCharacterDungeon()}
     * unchanged, matching a level-generation or save-load call site.
     */
    @Test
    @DisplayName("setting true is readable back as true")
    void settingTrueIsReadableBackAsTrue() {
        GameWorld.setCharacterDungeon(true);
        assertTrue(GameWorld.hasCharacterDungeon());
    }

    /**
     * The other branch: setting {@code false} comes back out unchanged, matching a birth-reset or
     * level-teardown call site.
     */
    @Test
    @DisplayName("setting false is readable back as false")
    void settingFalseIsReadableBackAsFalse() {
        GameWorld.setCharacterDungeon(false);
        assertFalse(GameWorld.hasCharacterDungeon());
    }

    /**
     * A second call replaces the first outright, with no dependency on what was there before - the
     * write does not toggle or latch against the existing flag.
     */
    @Test
    @DisplayName("a second write replaces the first, independent of the prior value")
    void secondWriteReplacesIndependentlyOfPrior() {
        GameWorld.setCharacterDungeon(true);
        GameWorld.setCharacterDungeon(false);
        assertFalse(GameWorld.hasCharacterDungeon());

        GameWorld.setCharacterDungeon(false);
        GameWorld.setCharacterDungeon(true);
        assertTrue(GameWorld.hasCharacterDungeon());
    }

    /**
     * Setting the same value twice in a row is not special-cased - the write is unconditional, so a
     * repeat is just as valid as a change.
     */
    @Test
    @DisplayName("repeating the same value is an ordinary write")
    void repeatingSameValueIsOrdinaryWrite() {
        GameWorld.setCharacterDungeon(true);
        GameWorld.setCharacterDungeon(true);
        assertTrue(GameWorld.hasCharacterDungeon());
    }
}
