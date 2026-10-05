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

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.middle.enums.DamageAspect;
import uk.co.jackoftradesltd.middle.game.globals.GameConstants;
import uk.co.jackoftradesltd.middle.game.globals.data.GameConstantsData;
import uk.co.jackoftradesltd.middle.game.globals.data.WorldData;
import uk.co.jackoftradesltd.middle.game.globals.registry.WorldRegistry;

import java.lang.reflect.Field;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link Random#Zero()} and {@link Random#One()}, the Java stand-ins for C's inline
 * {@code random_value} literal {@code { 0, 0, 0, 0 }} (and, for {@code One()}, a {@code 1d1} that
 * the C source never names).
 *
 * <p>The expected figures are worked out from {@code z-rand.c}, functions {@code randcalc()},
 * {@code damcalc()}, {@code m_bonus_calc()} and {@code damroll()}, not read back from the Java. For
 * {@code { 0, 0, 0, 0 }} every term is zero, so every aspect gives 0 at every level. For
 * {@code 1d1} the dice term is {@code num} when minimised, {@code num * sides} when maximised,
 * {@code num * (sides + 1) / 2} when averaged and {@code damroll(1, 1)} when randomised: all 1. The
 * level bonus is zero throughout.
 *
 * <p>The tests also pin the property C gets for free from struct copy-by-value: a caller that
 * changes the value it was handed must not change the next caller's.
 *
 * <p>Class RandomConstantsTest coded on 261003, commented in full on 261003.
 *
 * @author Rowan Crowther
 */
class RandomConstantsTest {

    /**
     * The world's maximum depth, which the average aspect divides the level bonus by. Any non-zero
     * figure will do; this is C's.
     */
    private static final int MAX_DEPTH = 128;

    /**
     * Every aspect, so each constant is checked under all of them in one loop.
     */
    private static final DamageAspect[] ASPECTS = DamageAspect.values();

    /**
     * Whatever the constants holder held before this class ran, put back afterwards.
     */
    private static Object savedConstants;
    /**
     * Whatever the world list held before this class ran, put back afterwards.
     */
    private static Object savedWorlds;

    /**
     * Seeds the two things evaluation reads: the world's maximum depth for the average aspect, and a
     * world list of that length for the randomised aspect's level bonus. Without them those aspects
     * fail loudly, which suits an unloaded game but not a unit test.
     *
     * @throws Exception if either field cannot be reached
     */
    @BeforeAll
    static void seedGlobals() throws Exception {
        Field data = GameConstants.class.getDeclaredField("data");
        data.setAccessible(true);
        savedConstants = data.get(null);
        data.set(null, new GameConstantsData(
                null, null, null, null,
                new WorldData(MAX_DEPTH, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                null, null, null, null, null, null, null, null, null, null, null, null));

        Field worlds = WorldRegistry.class.getDeclaredField("worlds");
        worlds.setAccessible(true);
        savedWorlds = worlds.get(null);
        worlds.set(null, Collections.nCopies(MAX_DEPTH, null));
    }

    /**
     * Puts both globals back, so a class running after this one sees what it expected.
     *
     * @throws Exception if either field cannot be reached
     */
    @AfterAll
    static void restoreGlobals() throws Exception {
        Field data = GameConstants.class.getDeclaredField("data");
        data.setAccessible(true);
        data.set(null, savedConstants);

        Field worlds = WorldRegistry.class.getDeclaredField("worlds");
        worlds.setAccessible(true);
        worlds.set(null, savedWorlds);
    }

    /**
     * {@code Random.Zero()}, the equivalent of {@code { 0, 0, 0, 0 }}.
     */
    @Nested
    @DisplayName("Zero()")
    class Zero {

        /**
         * All four terms are 0, read back from the accessors.
         */
        @Test
        @DisplayName("has every term zero")
        void termsAreZero() {
            Random zero = Random.Zero();

            assertEquals(0, zero.getBase());
            assertEquals(0, zero.getDice());
            assertEquals(0, zero.getSides());
            assertEquals(0, zero.getMBonus());
        }

        /**
         * Zero dice of zero sides contribute nothing, and a zero bonus multiplier scales nothing, so
         * every aspect at every level gives 0. The randomised aspect is included: {@code damroll}
         * returns 0 for no dice and {@code m_bonus} clamps to its zero maximum.
         */
        @Test
        @DisplayName("rolls 0 under every aspect at every level")
        void rollsZero() {
            Random zero = Random.Zero();

            for (DamageAspect aspect : ASPECTS) {
                for (int level : new int[]{0, 1, 50, MAX_DEPTH - 1, MAX_DEPTH, 500}) {
                    assertEquals(0, zero.randCalc(level, aspect),
                            aspect + " at level " + level);
                }
            }
        }

        /**
         * A single point: only 0 is valid, and it does not vary.
         */
        @Test
        @DisplayName("is valid only for 0 and does not vary")
        void validAndVaries() {
            Random zero = Random.Zero();

            assertTrue(zero.isValid(0));
            assertFalse(zero.isValid(1));
            assertFalse(zero.isValid(-1));
            assertFalse(zero.varies());
        }
    }

    /**
     * {@code Random.One()}, a {@code 1d1} with no base and no level bonus.
     */
    @Nested
    @DisplayName("One()")
    class One {

        /**
         * Base and bonus are 0; one die of one side.
         */
        @Test
        @DisplayName("is 1d1 with no base and no bonus")
        void terms() {
            Random one = Random.One();

            assertEquals(0, one.getBase());
            assertEquals(1, one.getDice());
            assertEquals(1, one.getSides());
            assertEquals(0, one.getMBonus());
        }

        /**
         * Minimised gives {@code num} = 1, maximised {@code num * sides} = 1, averaged
         * {@code num * (sides + 1) / 2} = 1 (the integer division is exact for a 1d1) and randomised
         * {@code damroll(1, 1)} = 1. The extremified aspect compares {@code abs(1)} with
         * {@code abs(1)}, ties, and takes the maximum, which is also 1.
         */
        @Test
        @DisplayName("rolls 1 under every aspect at every level")
        void rollsOne() {
            Random one = Random.One();

            for (DamageAspect aspect : ASPECTS) {
                for (int level : new int[]{0, 1, 50, MAX_DEPTH - 1, MAX_DEPTH, 500}) {
                    assertEquals(1, one.randCalc(level, aspect),
                            aspect + " at level " + level);
                }
            }
        }

        /**
         * A single point: only 1 is valid, and it does not vary.
         */
        @Test
        @DisplayName("is valid only for 1 and does not vary")
        void validAndVaries() {
            Random one = Random.One();

            assertTrue(one.isValid(1));
            assertFalse(one.isValid(0));
            assertFalse(one.isValid(2));
            assertFalse(one.varies());
        }
    }

    /**
     * What C gets from passing a struct by value: the next caller is unaffected by the last one's
     * changes.
     */
    @Nested
    @DisplayName("independence of the returned values")
    class Independence {

        /**
         * Two calls return two objects.
         */
        @Test
        @DisplayName("each call returns a new instance")
        void distinctInstances() {
            assertNotSame(Random.Zero(), Random.Zero());
            assertNotSame(Random.One(), Random.One());
        }

        /**
         * Changing every term of one {@code Zero()} leaves the next one at zero.
         */
        @Test
        @DisplayName("mutating a Zero() does not change the next Zero()")
        void zeroIsolated() {
            Random first = Random.Zero();
            first.setBase(9);
            first.setDice(3);
            first.setSides(6);
            first.setMBonus(4);

            Random second = Random.Zero();

            assertEquals(0, second.getBase());
            assertEquals(0, second.getDice());
            assertEquals(0, second.getSides());
            assertEquals(0, second.getMBonus());
            assertEquals(0, second.randCalc(0, DamageAspect.MAXIMIZE));
        }

        /**
         * Changing every term of one {@code One()} leaves the next one at 1d1.
         */
        @Test
        @DisplayName("mutating a One() does not change the next One()")
        void oneIsolated() {
            Random first = Random.One();
            first.setBase(9);
            first.setDice(3);
            first.setSides(6);
            first.setMBonus(4);

            Random second = Random.One();

            assertEquals(0, second.getBase());
            assertEquals(1, second.getDice());
            assertEquals(1, second.getSides());
            assertEquals(0, second.getMBonus());
            assertEquals(1, second.randCalc(0, DamageAspect.MAXIMIZE));
        }
    }
}
