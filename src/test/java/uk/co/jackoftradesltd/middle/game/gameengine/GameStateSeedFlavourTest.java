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
 * Tests {@link GameState#setSeedFlavour(long)} and {@link GameState#getSeedFlavour()} - the port of
 * the direct reads and writes C makes to its global {@code seed_flavor} ({@code uint32_t},
 * {@code game-world.c}), since C keeps no accessor functions for the global to diff against. What is
 * asserted is that the pair is a bare store/return: no clamping, no relation to the previous value,
 * and no side effect on any other field {@link GameState} holds alongside it.
 *
 * <p>{@code seed_flavor} is assigned {@code randint0(0x10000000)} at birth
 * ({@code player-birth.c:1315}), so the legal C range is {@code [0, 0x10000000)}; the port widens the
 * field to {@code long} to stay unsigned across the full {@code uint32_t} range, so this also checks
 * that the wider range above {@code 0x10000000} survives the round trip without truncation.
 *
 * <p>{@code seedFlavour} is a static field shared across the JVM, so each test saves the seed
 * beforehand and restores it afterwards to avoid leaking state into whichever test runs next.
 *
 * <p>Class GameStateSeedFlavourTest coded on 260908, commented in full on 260908.
 *
 * @author Rowan Crowther
 */
class GameStateSeedFlavourTest {

    /**
     * The object-flavour seed in effect before the test, restored afterwards.
     */
    private long savedSeedFlavour;

    /**
     * Records the seed so it can be put back.
     */
    @BeforeEach
    void saveSeedFlavour() {
        savedSeedFlavour = GameState.getSeedFlavour();
    }

    /**
     * Restores the seed the test found on entry.
     */
    @AfterEach
    void restoreSeedFlavour() {
        GameState.setSeedFlavour(savedSeedFlavour);
    }

    /**
     * The ordinary path: a value handed in comes back out of {@link GameState#getSeedFlavour()}
     * unchanged.
     */
    @Test
    @DisplayName("stores the given value unchanged")
    void storesValueUnchanged() {
        GameState.setSeedFlavour(12345L);
        assertEquals(12345L, GameState.getSeedFlavour());
    }

    /**
     * Zero is not special-cased - it is a legal result of C's {@code randint0(0x10000000)} and must
     * round-trip like any other value.
     */
    @Test
    @DisplayName("zero writes like any other value")
    void zeroIsAnOrdinaryWrite() {
        GameState.setSeedFlavour(999L);
        GameState.setSeedFlavour(0L);
        assertEquals(0L, GameState.getSeedFlavour());
    }

    /**
     * A second call replaces the first outright, with no dependency on what was there before - the
     * write does not add to or clamp against the existing seed.
     */
    @Test
    @DisplayName("a second write replaces the first, independent of the prior value")
    void secondWriteReplacesIndependentlyOfPrior() {
        GameState.setSeedFlavour(100L);
        GameState.setSeedFlavour(1L);
        assertEquals(1L, GameState.getSeedFlavour());
    }

    /**
     * The top of C's birth-time range, {@code randint0(0x10000000)}'s exclusive upper bound minus
     * one, survives the round trip.
     */
    @Test
    @DisplayName("the top of the birth-time randint0(0x10000000) range survives")
    void topOfBirthRangeSurvives() {
        long topOfBirthRange = 0x10000000L - 1;

        GameState.setSeedFlavour(topOfBirthRange);

        assertEquals(topOfBirthRange, GameState.getSeedFlavour());
    }

    /**
     * {@code seed_flavor} is C's {@code uint32_t}, so the full unsigned 32-bit range is legal input
     * even though {@code randint0} never actually produces values above {@code 0x10000000} - the
     * field write itself has no guard against it, and the port's {@code long} field must carry the
     * full range without truncating or going negative the way a Java {@code int} would.
     */
    @Test
    @DisplayName("the full uint32 range survives without truncation")
    void uint32RangeSurvivesWithoutTruncation() {
        long maxUnsigned32 = 0xFFFFFFFFL;

        GameState.setSeedFlavour(maxUnsigned32);

        assertEquals(maxUnsigned32, GameState.getSeedFlavour());
    }

    /**
     * Writing the object-flavour seed does not touch the turn counter sitting next to it in
     * {@link GameState} - C's assignment to the {@code seed_flavor} global cannot reach the separate
     * {@code turn} global, and the port must not couple the two fields either.
     */
    @Test
    @DisplayName("does not disturb the turn counter")
    void doesNotDisturbTurn() {
        int turnBefore = GameState.getTurn();

        GameState.setSeedFlavour(500L);

        assertEquals(turnBefore, GameState.getTurn());
    }
}
