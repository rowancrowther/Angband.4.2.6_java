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
import uk.co.jackoftrades.middle.player.enums.PlayerRedraw;
import uk.co.jackoftrades.middle.player.enums.PlayerUpdateEnum;
import uk.co.jackoftrades.middle.player.enums.TimedEffect;
import uk.co.jackoftrades.testsupport.SeededPlayerRegistry;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link Player#getTimedEffectOrDefault(TimedEffect, int)} and
 * {@link Player#putTimed(TimedEffect, int)}, the two raw reaches into the timed-effect table that
 * stand in for C's {@code p->timed[idx]} and {@code p->timed[idx] = value}.
 *
 * <p>There is no C function behind either - the original subscripts the array at the point of use -
 * so the expectations come from what those C sites need. Three things are worth pinning.
 *
 * <p><b>A count of zero is not an absent effect.</b> C's array has a slot for every effect and an
 * effect that is not running holds zero there, which is why every caller tests
 * {@code if (p->timed[TMD_X])} rather than asking whether the effect exists. The port fills the map
 * with a zero for each {@link TimedEffect} at construction, so a fresh character reads zero
 * everywhere and the supplied default is unreachable; the default only shows itself on a map that
 * was built without a key, which is a partially-built test character rather than anything the game
 * produces. Both cases are covered, and they are distinguished by choosing a non-zero default.
 *
 * <p><b>The write is raw.</b> {@code player-calcs.c:2154} and {@code player-calcs.c:2161} clear
 * {@code TMD_FASTCAST} by assigning the slot, not by calling {@code player_set_timed}, precisely so
 * that nothing else fires: no message, no disturb, and no redraw or update flags. {@code calc_bonuses}
 * is already inside an update when it does this. {@code putTimed} has to behave the same way, so the
 * tests here check the upkeep flags stay clear across a write - the property that separates it from
 * {@code setTimed}.
 *
 * <p><b>Nothing is filtered.</b> C stores what it is given: {@code player-birth.c:1021} writes
 * {@code PY_FOOD_FULL - 1}, a five-figure count, and {@code mon-util.c:1287} writes a plain zero.
 * The port must not clamp, floor or drop either.
 *
 * <p>Class PlayerTimedAccessorTest coded on 260901, commented in full on 260901.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class PlayerTimedAccessorTest {

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
     * Empties the timed table, leaving a character in the shape a hand-built fixture has: no key for
     * any effect at all.
     *
     * @throws Exception if the field cannot be reached
     */
    private void emptyTable() throws Exception {
        Field field = Player.class.getDeclaredField("timed");
        field.setAccessible(true);
        field.set(player, new HashMap<TimedEffect, Integer>());
    }

    /**
     * Reads the timed table directly, so a write can be checked at the field rather than through the
     * accessor that is also under test.
     *
     * @return the table
     * @throws Exception if the field cannot be reached
     */
    @SuppressWarnings("unchecked")
    private Map<TimedEffect, Integer> table() throws Exception {
        Field field = Player.class.getDeclaredField("timed");
        field.setAccessible(true);
        return (Map<TimedEffect, Integer>) field.get(player);
    }

    /**
     * Reading the table.
     */
    @Nested
    @DisplayName("getTimedEffectOrDefault")
    class Reading {

        /**
         * A fresh character has every effect present at zero, so the default is not reached even for
         * an effect nobody has touched - the port of C's zeroed array.
         */
        @Test
        @DisplayName("an effect that is not running reads zero, not the default")
        void notRunningReadsZero() {
            assertEquals(0, PlayerTimed.getTimedEffectOrDefault(player, TimedEffect.TMD_BLESSED, MISSING));
            assertEquals(0, PlayerTimed.getTimedEffectOrDefault(player, TimedEffect.TMD_STUN, MISSING));
        }

        /**
         * A running effect reads its turn count back unchanged.
         */
        @Test
        @DisplayName("a running effect reads its turn count")
        void runningReadsItsCount() {
            player.putTimed(TimedEffect.TMD_BLESSED, 27);

            assertEquals(27, PlayerTimed.getTimedEffectOrDefault(player, TimedEffect.TMD_BLESSED, MISSING));
        }

        /**
         * Each effect is its own slot: writing one leaves the rest reading zero.
         */
        @Test
        @DisplayName("effects do not read each other's counts")
        void effectsAreIndependent() {
            player.putTimed(TimedEffect.TMD_FAST, 10);
            player.putTimed(TimedEffect.TMD_SLOW, 5);

            assertEquals(10, PlayerTimed.getTimedEffectOrDefault(player, TimedEffect.TMD_FAST, MISSING));
            assertEquals(5, PlayerTimed.getTimedEffectOrDefault(player, TimedEffect.TMD_SLOW, MISSING));
            assertEquals(0, PlayerTimed.getTimedEffectOrDefault(player, TimedEffect.TMD_SPRINT, MISSING));
        }

        /**
         * The default is only reached where the map has no key at all - the case C's array cannot
         * have, and the reason this method exists alongside {@link Player#getTimedEffect}.
         *
         * @throws Exception if the field cannot be reached
         */
        @Test
        @DisplayName("an absent entry reads the supplied default")
        void absentEntryReadsTheDefault() throws Exception {
            emptyTable();

            assertEquals(MISSING, PlayerTimed.getTimedEffectOrDefault(player, TimedEffect.TMD_BLESSED, MISSING));
            assertEquals(0, PlayerTimed.getTimedEffectOrDefault(player, TimedEffect.TMD_BLESSED, 0));
        }

        /**
         * The default applies per effect, not per table: an entry written into an otherwise empty
         * table is read, and its neighbours still fall through.
         *
         * @throws Exception if the field cannot be reached
         */
        @Test
        @DisplayName("a present entry is read even when its neighbours are absent")
        void presentEntryBeatsTheDefault() throws Exception {
            emptyTable();
            player.putTimed(TimedEffect.TMD_STUN, 3);

            assertEquals(3, PlayerTimed.getTimedEffectOrDefault(player, TimedEffect.TMD_STUN, MISSING));
            assertEquals(MISSING, PlayerTimed.getTimedEffectOrDefault(player, TimedEffect.TMD_CUT, MISSING));
        }

        /**
         * With a default of zero the method is indistinguishable from {@link Player#getTimedEffect},
         * which is how every {@code calcBonuses} caller uses it.
         *
         * @throws Exception if the field cannot be reached
         */
        @Test
        @DisplayName("a default of zero makes this getTimedEffect")
        void zeroDefaultMatchesGetTimedEffect() throws Exception {
            player.putTimed(TimedEffect.TMD_POISONED, 12);
            emptyTable();
            player.putTimed(TimedEffect.TMD_POISONED, 12);

            for (TimedEffect effect : TimedEffect.values()) {
                assertEquals(player.getTimedEffect(effect),
                        PlayerTimed.getTimedEffectOrDefault(player, effect, 0),
                        effect.name() + " should agree with getTimedEffect");
            }
        }

        /**
         * The static wrapper reaches the table by
         * {@link Player#playerTimedContains(TimedEffect)} then
         * {@link Player#getTimedEffect(TimedEffect)}, while {@link Player#getTimedEffectOrDefault} goes
         * straight to the map's own {@code getOrDefault}. Two routes to the same answer, so they are
         * checked against each other across every effect, present and absent.
         *
         * @throws Exception if the field cannot be reached
         */
        @Test
        @DisplayName("the wrapper agrees with Player's own accessor")
        void wrapperAgreesWithPlayerAccessor() throws Exception {
            emptyTable();
            player.putTimed(TimedEffect.TMD_POISONED, 12);
            player.putTimed(TimedEffect.TMD_AFRAID, 0);

            for (TimedEffect effect : TimedEffect.values()) {
                assertEquals(player.getTimedEffectOrDefault(effect, MISSING),
                        PlayerTimed.getTimedEffectOrDefault(player, effect, MISSING),
                        effect.name() + " should agree with Player.getTimedEffectOrDefault");
            }
        }

        /**
         * The wrapper's presence check is {@link Player#playerTimedContains} alone - it has no
         * {@link Player#playerHasTimed} guard in front of it, unlike {@code incCheck} and
         * {@code timedGradeEq}. A character with no table at all therefore throws rather than falling
         * through to the default. That character is the one C's fixed array cannot produce and no
         * caller passes, so this pins the asymmetry rather than endorsing it: should the guard ever be
         * added, this test is the reminder that the Javadoc says otherwise.
         *
         * @throws Exception if the field cannot be reached
         */
        @Test
        @DisplayName("an absent table throws rather than reading the default")
        void absentTableThrows() throws Exception {
            Field field = Player.class.getDeclaredField("timed");
            field.setAccessible(true);
            field.set(player, null);

            assertFalse(player.playerHasTimed());
            assertThrows(NullPointerException.class,
                    () -> PlayerTimed.getTimedEffectOrDefault(player, TimedEffect.TMD_BLESSED, MISSING));
        }
    }

    /**
     * Writing the table.
     */
    @Nested
    @DisplayName("putTimed")
    class Writing {

        /**
         * The count is stored as given and read straight back.
         *
         * @throws Exception if the field cannot be reached
         */
        @Test
        @DisplayName("a count is stored as given")
        void storesTheCount() throws Exception {
            player.putTimed(TimedEffect.TMD_HERO, 15);

            assertEquals(15, table().get(TimedEffect.TMD_HERO));
        }

        /**
         * The clearing write {@code player-calcs.c:2154} makes when a stun cancels fast casting.
         *
         * @throws Exception if the field cannot be reached
         */
        @Test
        @DisplayName("a zero clears a running effect")
        void zeroClearsAnEffect() throws Exception {
            player.putTimed(TimedEffect.TMD_FASTCAST, 40);

            player.putTimed(TimedEffect.TMD_FASTCAST, 0);

            assertEquals(0, table().get(TimedEffect.TMD_FASTCAST));
            assertEquals(0, PlayerTimed.getTimedEffectOrDefault(player, TimedEffect.TMD_FASTCAST, MISSING));
        }

        /**
         * A write replaces rather than accumulates - it is an assignment in C, not an increase.
         */
        @Test
        @DisplayName("a second write replaces the first")
        void writeReplaces() {
            player.putTimed(TimedEffect.TMD_SHIELD, 30);
            player.putTimed(TimedEffect.TMD_SHIELD, 4);

            assertEquals(4, PlayerTimed.getTimedEffectOrDefault(player, TimedEffect.TMD_SHIELD, MISSING));
        }

        /**
         * Nothing is filtered on the way in. {@code player-birth.c:1021} writes a five-figure food
         * count, so a large value has to survive; the port clamps nothing, exactly as C does not.
         */
        @Test
        @DisplayName("a large count is stored unclamped")
        void largeCountSurvives() {
            player.putTimed(TimedEffect.TMD_FOOD, 20000 - 1);

            assertEquals(19999, PlayerTimed.getTimedEffectOrDefault(player, TimedEffect.TMD_FOOD, MISSING));
        }

        /**
         * A write into an emptied table creates the entry rather than failing.
         *
         * @throws Exception if the field cannot be reached
         */
        @Test
        @DisplayName("a write creates an absent entry")
        void writeCreatesAnAbsentEntry() throws Exception {
            emptyTable();

            player.putTimed(TimedEffect.TMD_AFRAID, 8);

            assertEquals(8, PlayerTimed.getTimedEffectOrDefault(player, TimedEffect.TMD_AFRAID, MISSING));
        }

        /**
         * The property that separates this from {@code setTimed}: the raw write announces nothing and
         * asks for no recalculation, which is why {@code calc_bonuses} is allowed to use it from
         * inside an update. Nothing is flagged on the upkeep either way.
         */
        @Test
        @DisplayName("a write raises no update or redraw flags")
        void writeRaisesNoFlags() {
            PlayerUpkeep upkeep = player.getPlayerUpkeep();

            player.putTimed(TimedEffect.TMD_FASTCAST, 0);
            player.putTimed(TimedEffect.TMD_BLESSED, 20);

            assertFalse(upkeep.getUpdate(), "no update should be pending after a raw write");
            assertFalse(upkeep.updateHas(PlayerUpdateEnum.PU_BONUS));
            assertFalse(upkeep.getRedrawFlags().has(PlayerRedraw.PR_STATUS));
        }

        /**
         * A write touches one slot only.
         */
        @Test
        @DisplayName("a write leaves the other effects alone")
        void writeTouchesOneSlot() {
            player.putTimed(TimedEffect.TMD_TERROR, 6);

            assertEquals(6, PlayerTimed.getTimedEffectOrDefault(player, TimedEffect.TMD_TERROR, MISSING));
            assertEquals(0, PlayerTimed.getTimedEffectOrDefault(player, TimedEffect.TMD_AFRAID, MISSING));
            assertEquals(0, PlayerTimed.getTimedEffectOrDefault(player, TimedEffect.TMD_IMAGE, MISSING));
        }
    }

    /**
     * The pair used the way {@code calc_bonuses} uses them.
     */
    @Nested
    @DisplayName("as calcBonuses uses them")
    class AsUsedByCalcBonuses {

        /**
         * The stun branch at {@code player-calcs.c:2149-2156}: a running fast-cast is cleared, and the
         * zero-versus-non-zero test the surrounding code makes then reads false.
         */
        @Test
        @DisplayName("clearing fast casting makes the running test read false")
        void clearingFastcastReadsAsNotRunning() {
            player.putTimed(TimedEffect.TMD_FASTCAST, 25);
            assertTrue(PlayerTimed.getTimedEffectOrDefault(player, TimedEffect.TMD_FASTCAST, 0) != 0);

            player.putTimed(TimedEffect.TMD_FASTCAST, 0);

            assertFalse(PlayerTimed.getTimedEffectOrDefault(player, TimedEffect.TMD_FASTCAST, 0) != 0);
        }
    }
}
