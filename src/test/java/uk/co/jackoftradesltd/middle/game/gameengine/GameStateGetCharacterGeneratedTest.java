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

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link GameState#getCharacterGenerated()} - the port of reading C's global
 * {@code character_generated} ({@code game-world.h:36}, {@code game-world.c:46}), which C reads
 * directly at every call site (e.g. {@code player-calcs.c:2620}) since it has no accessor function
 * of its own to diff against.
 *
 * <p>What is worth pinning is that the getter is a bare, side-effect-free read of the backing field
 * - it neither clamps nor derives anything, and touching it cannot disturb any other field
 * {@link GameState} holds alongside it. {@link GameState} exposes no setter for the field, so this
 * suite reaches it through reflection, the same route the port's other tests now use since the
 * field moved here from its earlier, mistaken second home on {@link
 * uk.co.jackoftradesltd.middle.game.GameWorld}.
 *
 * <p>{@code characterGenerated} is a static field shared across the JVM, so each test saves the
 * value beforehand and restores it afterwards to avoid leaking state into whichever test runs next.
 *
 * <p>Class GameStateGetCharacterGeneratedTest coded on 260907, commented in full on 260907.
 *
 * @author Rowan Crowther
 */
class GameStateGetCharacterGeneratedTest {

    /**
     * The value in effect before the test, restored afterwards.
     */
    private boolean savedValue;

    /**
     * Writes {@link GameState}'s private {@code characterGenerated} field directly, since
     * {@link GameState} exposes no setter for it.
     *
     * @param value the value to force the field to
     * @throws ReflectiveOperationException if the field cannot be reached
     */
    private static void setCharacterGenerated(boolean value) throws ReflectiveOperationException {
        Field field = GameState.class.getDeclaredField("characterGenerated");
        field.setAccessible(true);
        field.set(null, value);
    }

    /**
     * Records the field's value so it can be put back.
     *
     * @throws ReflectiveOperationException if the field cannot be reached
     */
    @BeforeEach
    void saveValue() throws ReflectiveOperationException {
        savedValue = GameState.getCharacterGenerated();
    }

    /**
     * Restores the value the test found on entry.
     *
     * @throws ReflectiveOperationException if the field cannot be reached
     */
    @AfterEach
    void restoreValue() throws ReflectiveOperationException {
        setCharacterGenerated(savedValue);
    }

    /**
     * The ordinary path: whatever the backing field holds comes straight back out, matching C's
     * bare read of the global at, for example, {@code player-calcs.c:2620}.
     *
     * @throws ReflectiveOperationException if the field cannot be reached
     */
    @Test
    @DisplayName("reports true once the field is set true")
    void reportsTrueWhenFieldIsTrue() throws ReflectiveOperationException {
        setCharacterGenerated(true);
        assertTrue(GameState.getCharacterGenerated());
    }

    /**
     * The other side of the same bare read - nothing is inverted or defaulted.
     *
     * @throws ReflectiveOperationException if the field cannot be reached
     */
    @Test
    @DisplayName("reports false once the field is set false")
    void reportsFalseWhenFieldIsFalse() throws ReflectiveOperationException {
        setCharacterGenerated(false);
        assertFalse(GameState.getCharacterGenerated());
    }

    /**
     * A second write replaces the first outright - the getter has no memory of what it returned
     * last time, only what the field currently holds.
     *
     * @throws ReflectiveOperationException if the field cannot be reached
     */
    @Test
    @DisplayName("a second write replaces the first")
    void secondWriteReplacesFirst() throws ReflectiveOperationException {
        setCharacterGenerated(true);
        setCharacterGenerated(false);
        assertFalse(GameState.getCharacterGenerated());

        setCharacterGenerated(true);
        assertTrue(GameState.getCharacterGenerated());
    }

    /**
     * Reading the flag does not touch the turn count sitting next to it in {@link GameState} - C's
     * read of the {@code character_generated} global cannot reach the separate {@code turn} global,
     * and the port must not couple the two fields either.
     *
     * @throws ReflectiveOperationException if the field cannot be reached
     */
    @Test
    @DisplayName("does not disturb the turn count")
    void doesNotDisturbTurn() throws ReflectiveOperationException {
        int turnBefore = GameState.getTurn();

        setCharacterGenerated(true);
        GameState.getCharacterGenerated();

        assertEquals(turnBefore, GameState.getTurn());
    }
}
