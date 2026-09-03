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

/**
 * Tests {@link GameState#setTurn(int)} - the port of the direct assignments C makes to its global
 * {@code turn} ({@code int32_t}, {@code game-world.c}) at every call site, since C keeps no single
 * setter function for the global to diff against. What is asserted is that the write is a bare one:
 * no clamping, no relation to the previous value, and no side effect on the day count or any other
 * field {@link GameState} holds alongside it.
 *
 * <p>{@link GameState#resetTurnForNewPlayer()} and {@link GameState#resetTurnFromSave(int)} write
 * the same field under birth- and load-specific names; this file covers only the general-purpose
 * {@code setTurn}, and does not re-test those siblings.
 *
 * <p>{@code turn} is a static field shared across the JVM, so each test saves the count beforehand
 * and restores it afterwards to avoid leaking state into whichever test runs next.
 *
 * <p>Class GameStateSetTurnTest coded on 260903, commented in full on 260903.
 *
 * @author Rowan Crowther
 */
class GameStateSetTurnTest {

    /**
     * The turn count in effect before the test, restored afterwards.
     */
    private int savedTurn;

    /**
     * Records the turn count so it can be put back.
     */
    @BeforeEach
    void saveTurn() {
        savedTurn = GameState.getTurn();
    }

    /**
     * Restores the turn count the test found on entry.
     */
    @AfterEach
    void restoreTurn() {
        GameState.setTurn(savedTurn);
    }

    /**
     * The ordinary path: a value handed in comes back out of {@link GameState#getTurn()} unchanged.
     */
    @Test
    @DisplayName("stores the given value unchanged")
    void storesValueUnchanged() {
        GameState.setTurn(12345);
        assertEquals(12345, GameState.getTurn());
    }

    /**
     * Zero is not special-cased - {@code resetTurnForNewPlayer}'s value is reachable through the
     * general setter too, since both are the same bare field write in C.
     */
    @Test
    @DisplayName("zero writes like any other value")
    void zeroIsAnOrdinaryWrite() {
        GameState.setTurn(999);
        GameState.setTurn(0);
        assertEquals(0, GameState.getTurn());
    }

    /**
     * A second call replaces the first outright, with no dependency on what was there before - the
     * write does not add to or clamp against the existing count.
     */
    @Test
    @DisplayName("a second write replaces the first, independent of the prior value")
    void secondWriteReplacesIndependentlyOfPrior() {
        GameState.setTurn(100);
        GameState.setTurn(1);
        assertEquals(1, GameState.getTurn());
    }

    /**
     * {@code turn} is C's {@code int32_t}, so the full signed 32-bit range is legal input and must
     * survive without clamping, including the negative end - C never actually assigns a negative
     * turn, but the field write itself has no guard against it.
     */
    @Test
    @DisplayName("the full int32 range survives, including negative values")
    void int32RangeSurvives() {
        GameState.setTurn(Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, GameState.getTurn());

        GameState.setTurn(Integer.MIN_VALUE);
        assertEquals(Integer.MIN_VALUE, GameState.getTurn());
    }

    /**
     * Writing the turn count does not touch the day count sitting next to it in {@link GameState} -
     * C's assignment to the {@code turn} global cannot reach the separate {@code daycount} global,
     * and the port must not couple the two fields either.
     */
    @Test
    @DisplayName("does not disturb the day count")
    void doesNotDisturbDaycount() {
        int dayCountBefore = GameState.getDaycount();

        GameState.setTurn(500);

        assertEquals(dayCountBefore, GameState.getDaycount());
    }
}
