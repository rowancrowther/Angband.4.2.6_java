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


package uk.co.jackoftradesltd.middle.numerics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link RandomChance}, the port of {@code random_chance},
 * {@code random_chance_check} and {@code random_chance_scaled} ({@code z-rand.c}).
 *
 * <p>The expected values come from the C source, not from the Java: {@code scale} is plain
 * {@code int32} arithmetic ({@code scale * numerator / denominator}) and {@code check} is
 * {@code randint0(d) >= d - n}, so every always/never case below follows from that inequality
 * with {@code randint0(0) == randint0(1) == 0}. The two worked examples (7 in 13 at scales
 * 100 and 1000) are the ones in C's own comment. Randomness is seeded, so the one statistical
 * test is reproducible rather than a flake.
 *
 * <p>Class RandomChanceTest coded on 261014, commented in full on 261014.
 */
class RandomChanceTest {
    private static final long SEED = 20261014L;

    @BeforeEach
    void seedTheGenerator() {
        RandomValueUtils.stateInit(SEED);
    }

    @Test
    void scaleReproducesTheWorkedExamplesInCsComment() {
        RandomChance c = new RandomChance(7, 13);
        assertEquals(53, c.scale(100));
        assertEquals(538, c.scale(1000));
    }

    @Test
    void scaleOfEightInOneHundredAndTwentyFiveIsSixtyFourAtOneThousand() {
        // 8 in 125 is the 6.4% example in z-rand.h
        assertEquals(64, new RandomChance(8, 125).scale(1000));
        assertEquals(6, new RandomChance(8, 125).scale(100));
    }

    @Test
    void scaleTruncatesRatherThanRounds() {
        // 2/3 of 10 is 6.67; 1/3 of 10 is 3.33
        assertEquals(6, new RandomChance(2, 3).scale(10));
        assertEquals(3, new RandomChance(1, 3).scale(10));
    }

    @Test
    void scaleTruncatesTowardsZeroForNegatives() {
        // C integer division truncates toward zero: -700 / 13 == -53, not -54
        assertEquals(-53, new RandomChance(-7, 13).scale(100));
        assertEquals(-53, new RandomChance(7, 13).scale(-100));
        assertEquals(53, new RandomChance(-7, -13).scale(100));
    }

    @Test
    void scaleOfZeroOrOfAWholeChance() {
        assertEquals(0, new RandomChance(7, 13).scale(0));
        assertEquals(0, new RandomChance(0, 13).scale(100));
        assertEquals(100, new RandomChance(13, 13).scale(100));
        assertEquals(200, new RandomChance(26, 13).scale(100));
    }

    @Test
    void scaleProductWrapsAs32BitIntegerLikeC() {
        // 100000 * 100000 = 10^10, which wraps to 1410065408 in int32; / 7 = 201437915
        assertEquals(201437915, new RandomChance(100000, 7).scale(100000));
    }

    @Test
    void scaleWithZeroDenominatorThrows() {
        assertThrows(ArithmeticException.class, () -> new RandomChance(1, 0).scale(100));
    }

    @Test
    void scaleDoesNotRoll() {
        RandomChance c = new RandomChance(7, 13);
        for (int i = 0; i < 50; i++) assertEquals(53, c.scale(100));
    }

    @Test
    void aNumeratorEqualToTheDenominatorAlwaysSucceeds() {
        RandomChance c = new RandomChance(13, 13);
        for (int i = 0; i < 500; i++) assertTrue(c.check());
    }

    @Test
    void aNumeratorAboveTheDenominatorAlwaysSucceeds() {
        RandomChance c = new RandomChance(20, 13);
        for (int i = 0; i < 500; i++) assertTrue(c.check());
    }

    @Test
    void aZeroNumeratorNeverSucceedsWhenTheDenominatorExceedsOne() {
        RandomChance c = new RandomChance(0, 13);
        for (int i = 0; i < 500; i++) assertFalse(c.check());
    }

    @Test
    void aNegativeNumeratorNeverSucceedsWhenTheDenominatorExceedsOne() {
        RandomChance c = new RandomChance(-3, 13);
        for (int i = 0; i < 500; i++) assertFalse(c.check());
    }

    @Test
    void denominatorOfOneRollsAFixedZero() {
        // randint0(1) == 0, so 0 >= 1 - n holds only for n >= 1
        for (int i = 0; i < 50; i++) {
            assertTrue(new RandomChance(1, 1).check());
            assertFalse(new RandomChance(0, 1).check());
        }
    }

    @Test
    void denominatorOfZeroRollsAFixedZero() {
        // randint0(0) == 0, so 0 >= 0 - n holds only for n >= 0
        for (int i = 0; i < 50; i++) {
            assertTrue(new RandomChance(0, 0).check());
            assertTrue(new RandomChance(5, 0).check());
            assertFalse(new RandomChance(-1, 0).check());
        }
    }

    @Test
    void aNegativeDenominatorIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new RandomChance(1, -5).check());
    }

    @Test
    void aDenominatorAboveTheRollLimitIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new RandomChance(1, 0x10000001).check());
    }

    @Test
    void oneInTwoReachesBothOutcomes() {
        RandomChance c = new RandomChance(1, 2);
        boolean sawTrue = false, sawFalse = false;
        for (int i = 0; i < 200; i++) {
            if (c.check()) sawTrue = true; else sawFalse = true;
        }
        assertTrue(sawTrue && sawFalse, "a 1 in 2 chance should both succeed and fail");
    }

    @Test
    void successRateMatchesTheStatedChance() {
        // 3 in 10 over 20000 rolls: mean 6000, standard deviation about 65; allow five of them
        RandomChance c = new RandomChance(3, 10);
        int hits = 0;
        for (int i = 0; i < 20000; i++) if (c.check()) hits++;
        assertTrue(Math.abs(hits - 6000) < 325, "3 in 10 succeeded " + hits + " times in 20000");
    }
}
