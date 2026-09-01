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

package uk.co.jackoftrades.middle.player;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.co.jackoftrades.middle.player.enums.TimedEffect;
import uk.co.jackoftrades.testsupport.SeededPlayerRegistry;

import java.lang.reflect.Field;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the three accessors {@link Player} exposes over its timed-effect table:
 * {@link Player#getTimedEffectOrDefault(TimedEffect, int)},
 * {@link Player#playerTimedContains(TimedEffect)} and {@link Player#playerHasTimed()}.
 *
 * <p>None of the three has a C counterpart. C's {@code p->timed} is an {@code int16_t} array of
 * {@code TMD_MAX} slots embedded in the player struct, so it cannot be absent and no slot can be
 * missing; the original simply subscripts it. The port holds a {@link HashMap} instead, and these
 * three methods are the guards that stand where C's array shape did the work for free. What they
 * have to get right is therefore a Java question, and three things are worth pinning.
 *
 * <p><b>A count of zero is not an absent entry.</b> The constructor seeds the map with a zero for
 * every {@link TimedEffect}, matching C's zeroed array, so a fresh character has every key present
 * and none of the defaults or guards is ever reached. Every test that wants to see the absent branch
 * has to reach in and empty the table, which is the shape a hand-built fixture has rather than
 * anything the game produces. A non-zero default separates the two cases.
 *
 * <p><b>Presence is not running.</b> {@code playerTimedContains} answers {@code true} for an effect
 * sitting at zero, which is why {@link PlayerTimed} pairs it with a {@code != 0} test at every call
 * site rather than treating it as "is this effect active".
 *
 * <p><b>The guards nest.</b> {@code playerHasTimed} is the outermost - table present - and
 * {@code playerTimedContains} the next in; a {@code true} from either says nothing about the layer
 * below it.
 *
 * <p>Class PlayerTimedTableAccessorTest coded on 260901, commented in full on 260901.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class PlayerTimedTableAccessorTest {

    /**
     * A default no live character can ever produce, so a test that reads it back knows the value came
     * from the absent-key branch rather than from a stored zero.
     */
    private static final int MISSING = -999;

    /**
     * The player under test.
     */
    private Player player;

    /**
     * A fresh character, whose constructor fills the table with a zero for every effect.
     */
    @BeforeEach
    void newPlayer() {
        player = new Player();
    }

    /**
     * Replaces the timed table, so the absent-key and absent-table branches can be reached. A live
     * character never gets into either state.
     *
     * @param replacement the table to install, or {@code null} to remove it entirely
     * @throws Exception if the field cannot be reached
     */
    private void setTable(HashMap<TimedEffect, Integer> replacement) throws Exception {
        Field field = Player.class.getDeclaredField("timed");
        field.setAccessible(true);
        field.set(player, replacement);
    }

    /**
     * Reading a count with a caller-chosen fallback.
     */
    @Nested
    @DisplayName("getTimedEffectOrDefault")
    class Reading {

        /**
         * A fresh character has every effect present at zero, so the default is not reached even for an
         * effect nobody has touched - the port of C's zeroed array.
         */
        @Test
        @DisplayName("an effect that is not running reads zero, not the default")
        void notRunningReadsZero() {
            assertEquals(0, player.getTimedEffectOrDefault(TimedEffect.TMD_BLESSED, MISSING));
            assertEquals(0, player.getTimedEffectOrDefault(TimedEffect.TMD_STUN, MISSING));
        }

        /**
         * A running effect reads its turn count back unchanged.
         */
        @Test
        @DisplayName("a running effect reads its turn count")
        void runningReadsItsCount() {
            player.putTimed(TimedEffect.TMD_BLESSED, 27);

            assertEquals(27, player.getTimedEffectOrDefault(TimedEffect.TMD_BLESSED, MISSING));
        }

        /**
         * Nothing is clamped or filtered on the way out: {@code player-birth.c:1021} writes a
         * five-figure food count, and a negative count is what {@code playerDecTimed} hands the setter
         * before the effect finishes.
         */
        @Test
        @DisplayName("large and negative counts read back unchanged")
        void extremeCountsReadBackUnchanged() {
            player.putTimed(TimedEffect.TMD_FOOD, 19999);
            player.putTimed(TimedEffect.TMD_CUT, -4);

            assertEquals(19999, player.getTimedEffectOrDefault(TimedEffect.TMD_FOOD, MISSING));
            assertEquals(-4, player.getTimedEffectOrDefault(TimedEffect.TMD_CUT, MISSING));
        }

        /**
         * Each effect is its own slot: writing one leaves the rest reading zero.
         */
        @Test
        @DisplayName("effects do not read each other's counts")
        void effectsAreIndependent() {
            player.putTimed(TimedEffect.TMD_FAST, 10);
            player.putTimed(TimedEffect.TMD_SLOW, 5);

            assertEquals(10, player.getTimedEffectOrDefault(TimedEffect.TMD_FAST, MISSING));
            assertEquals(5, player.getTimedEffectOrDefault(TimedEffect.TMD_SLOW, MISSING));
            assertEquals(0, player.getTimedEffectOrDefault(TimedEffect.TMD_SPRINT, MISSING));
        }

        /**
         * The default is only reached where the map has no key at all - the case C's array cannot have,
         * and the reason this method exists alongside {@link Player#getTimedEffect}.
         *
         * @throws Exception if the field cannot be reached
         */
        @Test
        @DisplayName("an absent entry reads the supplied default")
        void absentEntryReadsTheDefault() throws Exception {
            setTable(new HashMap<>());

            assertEquals(MISSING, player.getTimedEffectOrDefault(TimedEffect.TMD_BLESSED, MISSING));
            assertEquals(0, player.getTimedEffectOrDefault(TimedEffect.TMD_BLESSED, 0));
        }

        /**
         * The default applies per effect, not per table: an entry written into an otherwise empty table
         * is read, and its neighbours still fall through.
         *
         * @throws Exception if the field cannot be reached
         */
        @Test
        @DisplayName("a present entry is read even when its neighbours are absent")
        void presentEntryBeatsTheDefault() throws Exception {
            setTable(new HashMap<>());
            player.putTimed(TimedEffect.TMD_STUN, 3);

            assertEquals(3, player.getTimedEffectOrDefault(TimedEffect.TMD_STUN, MISSING));
            assertEquals(MISSING, player.getTimedEffectOrDefault(TimedEffect.TMD_CUT, MISSING));
        }

        /**
         * A stored zero is returned as the stored zero, not treated as absent - the distinction the
         * whole table turns on.
         *
         * @throws Exception if the field cannot be reached
         */
        @Test
        @DisplayName("a stored zero beats the default")
        void storedZeroBeatsTheDefault() throws Exception {
            setTable(new HashMap<>());
            player.putTimed(TimedEffect.TMD_AFRAID, 0);

            assertEquals(0, player.getTimedEffectOrDefault(TimedEffect.TMD_AFRAID, MISSING));
        }

        /**
         * With a default of zero the method is indistinguishable from {@link Player#getTimedEffect},
         * which is how {@link PlayerTimed#playerDecTimed} and every {@code calcBonuses} caller use it.
         */
        @Test
        @DisplayName("a default of zero makes this getTimedEffect")
        void zeroDefaultMatchesGetTimedEffect() {
            player.putTimed(TimedEffect.TMD_POISONED, 12);

            for (TimedEffect effect : TimedEffect.values()) {
                assertEquals(player.getTimedEffect(effect), player.getTimedEffectOrDefault(effect, 0),
                        "effect " + effect);
            }
        }

        /**
         * Reading does not create the entry it failed to find, so a later presence check still says the
         * key is absent.
         *
         * @throws Exception if the field cannot be reached
         */
        @Test
        @DisplayName("reading an absent entry does not create it")
        void readingDoesNotCreateTheEntry() throws Exception {
            setTable(new HashMap<>());

            player.getTimedEffectOrDefault(TimedEffect.TMD_BLESSED, MISSING);

            assertFalse(player.playerTimedContains(TimedEffect.TMD_BLESSED));
        }
    }

    /**
     * Asking whether an effect has an entry.
     */
    @Nested
    @DisplayName("playerTimedContains")
    class Containment {

        /**
         * The constructor seeds a key for every effect, so a fresh character answers {@code true}
         * throughout - C's array shape, reproduced.
         */
        @Test
        @DisplayName("a fresh character has an entry for every effect")
        void freshCharacterHasEveryEffect() {
            for (TimedEffect effect : TimedEffect.values()) {
                assertTrue(player.playerTimedContains(effect), "effect " + effect);
            }
        }

        /**
         * Presence is not the same question as running: an effect sitting at zero is still present,
         * which is why the {@link PlayerTimed} call sites add a {@code != 0} test of their own.
         */
        @Test
        @DisplayName("an effect at zero is still present")
        void zeroCountIsStillPresent() {
            assertEquals(0, player.getTimedEffect(TimedEffect.TMD_AFRAID));

            assertTrue(player.playerTimedContains(TimedEffect.TMD_AFRAID));
        }

        /**
         * An emptied table has no entry for anything.
         *
         * @throws Exception if the field cannot be reached
         */
        @Test
        @DisplayName("an emptied table contains nothing")
        void emptiedTableContainsNothing() throws Exception {
            setTable(new HashMap<>());

            assertFalse(player.playerTimedContains(TimedEffect.TMD_BLESSED));
            assertFalse(player.playerTimedContains(TimedEffect.TMD_STUN));
        }

        /**
         * A write puts the key in, and only that key.
         *
         * @throws Exception if the field cannot be reached
         */
        @Test
        @DisplayName("a write adds the entry it wrote, and no other")
        void writeAddsOnlyItsOwnEntry() throws Exception {
            setTable(new HashMap<>());
            player.putTimed(TimedEffect.TMD_FAST, 10);

            assertTrue(player.playerTimedContains(TimedEffect.TMD_FAST));
            assertFalse(player.playerTimedContains(TimedEffect.TMD_SLOW));
        }
    }

    /**
     * Asking whether the table exists at all.
     */
    @Nested
    @DisplayName("playerHasTimed")
    class TablePresence {

        /**
         * The constructor builds the table, so any character the game produces answers {@code true}.
         */
        @Test
        @DisplayName("a fresh character has a table")
        void freshCharacterHasATable() {
            assertTrue(player.playerHasTimed());
        }

        /**
         * The table's existence is a separate question from its contents: emptying it leaves it there.
         *
         * @throws Exception if the field cannot be reached
         */
        @Test
        @DisplayName("an empty table is still a table")
        void emptyTableIsStillATable() throws Exception {
            setTable(new HashMap<>());

            assertTrue(player.playerHasTimed());
            assertFalse(player.playerTimedContains(TimedEffect.TMD_BLESSED));
        }

        /**
         * The only state that answers {@code false} - a character built without the constructor's
         * seeding, which is what the guard at {@code PlayerTimed:227} and {@code PlayerTimed:468}
         * exists to catch before the inner checks dereference the map.
         *
         * @throws Exception if the field cannot be reached
         */
        @Test
        @DisplayName("a character with no table answers false")
        void absentTableAnswersFalse() throws Exception {
            setTable(null);

            assertFalse(player.playerHasTimed());
        }
    }
}
