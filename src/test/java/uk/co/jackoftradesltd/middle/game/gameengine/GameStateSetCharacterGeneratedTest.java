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

package uk.co.jackoftradesltd.middle.game.gameengine;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link GameState#setCharacterGenerated(boolean)} - the port of the direct assignments C
 * makes to its global {@code character_generated} ({@code game-world.h:36}) at every call site
 * ({@code player-birth.c:1329}, {@code savefile.c:653}, {@code ui-game.c:721}), since C keeps no
 * single setter function for the global to diff against. What is asserted is that the write is a
 * bare one: no derivation from the previous value, and no side effect on the turn count or any
 * other field {@link GameState} holds alongside it.
 *
 * <p>{@code characterGenerated} is a static field shared across the JVM, so each test saves the
 * value beforehand and restores it afterwards to avoid leaking state into whichever test runs
 * next.
 *
 * <p>Class GameStateSetCharacterGeneratedTest coded on 260908, commented in full on 260908.
 *
 * @author Rowan Crowther
 */
class GameStateSetCharacterGeneratedTest {

    /**
     * The value in effect before the test, restored afterwards.
     */
    private boolean savedValue;

    /**
     * Records the field's value so it can be put back.
     */
    @BeforeEach
    void saveValue() {
        savedValue = GameState.getCharacterGenerated();
    }

    /**
     * Restores the value the test found on entry.
     */
    @AfterEach
    void restoreValue() {
        GameState.setCharacterGenerated(savedValue);
    }

    /**
     * The ordinary path: {@code true} handed in comes back out of
     * {@link GameState#getCharacterGenerated()} unchanged, the write birth and save-loading make.
     */
    @Test
    @DisplayName("stores true unchanged")
    void storesTrueUnchanged() {
        GameState.setCharacterGenerated(true);
        assertTrue(GameState.getCharacterGenerated());
    }

    /**
     * The other side: {@code false}, the write a fresh birth after death makes.
     */
    @Test
    @DisplayName("stores false unchanged")
    void storesFalseUnchanged() {
        GameState.setCharacterGenerated(false);
        assertFalse(GameState.getCharacterGenerated());
    }

    /**
     * A second call replaces the first outright, with no dependency on what was there before - the
     * write does not toggle or latch against the existing value.
     */
    @Test
    @DisplayName("a second write replaces the first, independent of the prior value")
    void secondWriteReplacesIndependentlyOfPrior() {
        GameState.setCharacterGenerated(true);
        GameState.setCharacterGenerated(false);
        assertFalse(GameState.getCharacterGenerated());

        GameState.setCharacterGenerated(true);
        assertTrue(GameState.getCharacterGenerated());
    }

    /**
     * Writing the flag does not touch the turn count sitting next to it in {@link GameState} - C's
     * assignment to the {@code character_generated} global cannot reach the separate {@code turn}
     * global, and the port must not couple the two fields either.
     */
    @Test
    @DisplayName("does not disturb the turn count")
    void doesNotDisturbTurn() {
        int turnBefore = GameState.getTurn();

        GameState.setCharacterGenerated(true);

        assertEquals(turnBefore, GameState.getTurn());
    }
}
