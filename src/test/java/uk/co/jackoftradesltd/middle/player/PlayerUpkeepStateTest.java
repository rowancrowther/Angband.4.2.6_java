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

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.game.globals.GameConstants;
import uk.co.jackoftradesltd.middle.game.globals.data.CarryCapData;
import uk.co.jackoftradesltd.middle.game.globals.data.GameConstantsData;
import uk.co.jackoftradesltd.middle.objects.ItemObject;
import uk.co.jackoftradesltd.middle.player.enums.PlayerNotice;
import uk.co.jackoftradesltd.middle.player.enums.PlayerRedraw;
import uk.co.jackoftradesltd.middle.player.enums.PlayerUpdateEnum;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link PlayerUpkeep}'s per-turn bookkeeping — the port of C's {@code player_upkeep}
 * ({@code player.h}).
 *
 * <p>Almost every field here is transient state the turn loop reads and clears, so the tests are
 * about two things rather than arithmetic: that a fresh instance starts in the state C's
 * {@code mem_zalloc} leaves it in, and that the paired predicate-and-value accessors agree with each
 * other. Several fields have both — {@code energyUse()} asks whether any energy was spent while
 * {@code getEnergyUse()} says how much — and the two answering differently is exactly the sort of
 * thing that shows up as a free turn nobody meant to grant.
 *
 * @author Rowan Crowther
 */
class PlayerUpkeepStateTest {

    /**
     * The pack size the constructor sizes its arrays from.
     */
    private static final int PACK_SIZE = 23;

    /**
     * The quiver size, likewise.
     */
    private static final int QUIVER_SIZE = 10;

    /**
     * Whatever was in the constants holder before this class ran.
     */
    private static Object savedConstants;

    /**
     * The instance under test, fresh for each test.
     */
    private PlayerUpkeep upkeep;

    /**
     * Seeds the two carry-capacity figures the constructor needs to size the pack and quiver.
     *
     * @throws Exception if the constants field cannot be reached
     */
    @BeforeAll
    static void seedConstants() throws Exception {
        Field data = GameConstants.class.getDeclaredField("data");
        data.setAccessible(true);
        savedConstants = data.get(null);
        data.set(null, new GameConstantsData(
                null, null, null, null, null,
                new CarryCapData(PACK_SIZE, QUIVER_SIZE, 40, 5, 16),
                null, null, null, null, null, null, null, null, null, null, null));
    }

    /**
     * Puts the constants holder back.
     *
     * @throws Exception if the constants field cannot be reached
     */
    @AfterAll
    static void restoreConstants() throws Exception {
        Field data = GameConstants.class.getDeclaredField("data");
        data.setAccessible(true);
        data.set(null, savedConstants);
    }

    /**
     * A fresh upkeep for each test, since every one of these fields is mutable.
     */
    @BeforeEach
    void newUpkeep() {
        upkeep = new PlayerUpkeep();
    }

    /**
     * The state a fresh instance starts in, which is C's zeroed struct.
     */
    @Nested
    @DisplayName("a fresh upkeep")
    class Fresh {

        /**
         * Every boolean starts false and every counter at zero — the turn loop assumes it, and a
         * constructor that forgot one would grant a free turn or regenerate a level unasked.
         */
        @Test
        @DisplayName("starts with nothing set")
        void startsEmpty() {
            assertFalse(upkeep.isPlaying());
            assertFalse(upkeep.energyUse());
            assertFalse(upkeep.getDropping());
            assertFalse(upkeep.generateLevel());
            assertFalse(upkeep.isArenaLevel());
            assertFalse(upkeep.isOnlyPartial());

            assertEquals(0, upkeep.getEnergyUse());
            assertEquals(0, upkeep.getInventoryCount());
            assertEquals(0, upkeep.getQuiverCount());
            assertEquals(0, upkeep.getRestingCounter());
            assertEquals(0, upkeep.getTotalWeight());
            assertEquals(0, upkeep.getCommand_wrk());
        }

        /**
         * Nothing is being tracked, and no housekeeping is pending.
         */
        @Test
        @DisplayName("tracks nothing and notices nothing")
        void tracksNothing() {
            assertNull(upkeep.getObject());
            assertNull(upkeep.getHealthWho());
            assertNull(upkeep.getMonsterRace());
            assertFalse(upkeep.healthWho());
            assertFalse(upkeep.isNotice());
            assertTrue(upkeep.getNoticeFlags().isEmpty());
        }

        /**
         * The floor pile starts as nothing, not as an empty pile. C's {@code upkeep->object_pile}
         * is a pointer left {@code NULL} by {@code mem_zalloc}, and the constructor here says so
         * explicitly rather than allocating — the pile is set when the player steps onto a square
         * that has one, and a caller reading it has to cope with there being none.
         *
         * <p>That makes it unlike the pack and quiver two fields away, which the same constructor
         * <em>does</em> allocate because C allocates them too.
         */
        @Test
        @DisplayName("the floor pile starts as nothing")
        void floorPileStartsNull() {
            assertNull(upkeep.getPile());
        }
    }

    /**
     * The fields that have both a predicate and a value accessor.
     */
    @Nested
    @DisplayName("paired accessors")
    class Paired {

        /**
         * The energy pair. The predicate asks whether the turn cost anything; the value says how
         * much. A command that spends nothing is a free turn, which is the case the predicate
         * exists to name.
         */
        @Test
        @DisplayName("energy: the predicate asks whether, the getter asks how much")
        void energyPair() {
            assertFalse(upkeep.energyUse(), "a fresh turn has spent nothing");

            upkeep.setEnergyUse(100);
            assertTrue(upkeep.energyUse());
            assertEquals(100, upkeep.getEnergyUse());

            upkeep.setEnergyUse(0);
            assertFalse(upkeep.energyUse(), "zero energy is a free turn, not a spent one");
            assertEquals(0, upkeep.getEnergyUse());
        }

        /**
         * The health-bar pair. The predicate asks whether a monster is on the bar; the getter says
         * which. Tracking also raises the health redraw, because the bar has to be repainted.
         */
        @Test
        @DisplayName("health: tracking a monster raises the redraw with it")
        void healthPair() {
            assertFalse(upkeep.healthWho());

            upkeep.healthTrack(null);
            assertFalse(upkeep.healthWho(), "tracking nothing clears the bar");
            assertTrue(upkeep.getRedrawFlags().has(PlayerRedraw.PR_HEALTH),
                    "the bar still needs repainting, to clear it");
        }
    }

    /**
     * The plain flags and counters the turn loop sets and reads.
     */
    @Nested
    @DisplayName("turn state")
    class TurnState {

        /**
         * Each boolean round-trips through its own pair of accessors, and setting one does not
         * disturb another. They are adjacent booleans in the class, which is where confusion would
         * live.
         */
        @Test
        @DisplayName("each flag is independent of the others")
        void flagsAreIndependent() {
            upkeep.setDropping(true);

            assertTrue(upkeep.getDropping());
            assertFalse(upkeep.generateLevel());
            assertFalse(upkeep.isArenaLevel());

            upkeep.setGenerateLevel(true);
            upkeep.setArenaLevel(true);

            assertTrue(upkeep.generateLevel());
            assertTrue(upkeep.isArenaLevel());
            assertTrue(upkeep.getDropping(), "setting the others left this one alone");
        }

        /**
         * The counters the inventory rebuild writes.
         */
        @Test
        @DisplayName("the pack and quiver counts round-trip")
        void countsRoundTrip() {
            upkeep.setInventoryCount(7);
            upkeep.setQuiverCount(3);

            assertEquals(7, upkeep.getInventoryCount());
            assertEquals(3, upkeep.getQuiverCount());
        }

        /**
         * The UI's listing preference, which C keeps as an integer rather than an enum.
         */
        @Test
        @DisplayName("the command working mode round-trips")
        void commandWorkRoundTrips() {
            upkeep.setCommand_wrk(2);

            assertEquals(2, upkeep.getCommand_wrk());
        }

        /**
         * The autosave request has no getter — it is read by the save code through the field — so
         * what can be asserted is that setting it disturbs nothing else.
         */
        @Test
        @DisplayName("requesting an autosave changes nothing else")
        void autosaveIsIndependent() {
            upkeep.setAutosave(true);

            assertFalse(upkeep.generateLevel());
            assertFalse(upkeep.isPlaying());
        }
    }

    /**
     * The object trackee, which the deletion code clears so that nothing points at an object that no
     * longer exists.
     */
    @Nested
    @DisplayName("object tracking")
    class ObjectTracking {

        /**
         * Set and read back, by identity — the trackee is <em>that</em> object, not one like it.
         */
        @Test
        @DisplayName("the tracked object round-trips by identity")
        void trackedObjectRoundTrips() {
            ItemObject potion = new ItemObject();
            upkeep.setObject(potion);

            assertSame(potion, upkeep.getObject());
        }

        /**
         * And clearing it is how the deletion code stops the bar pointing at a deleted object.
         */
        @Test
        @DisplayName("tracking can be cleared")
        void trackingCanBeCleared() {
            upkeep.setObject(new ItemObject());
            upkeep.setObject(null);

            assertNull(upkeep.getObject());
        }
    }

    /**
     * The two flag sets, which are how the turn loop is told what needs doing.
     */
    @Nested
    @DisplayName("notice and update flags")
    class Flags {

        /**
         * Raising a notice flag makes the whole set non-empty, which is the test
         * {@code noticeStuff} returns early on.
         */
        @Test
        @DisplayName("raising a notice flag makes the set non-empty")
        void noticeRaised() {
            assertFalse(upkeep.isNotice());

            upkeep.setNoticeFlagOn(PlayerNotice.PN_COMBINE);

            assertTrue(upkeep.isNotice());
            assertTrue(upkeep.getNoticeFlags().has(PlayerNotice.PN_COMBINE));
        }

        /**
         * {@code noticeFlagOn} is a second spelling of the same operation, and must behave the same
         * — two names for one thing is worth pinning so they cannot drift apart.
         */
        @Test
        @DisplayName("both ways of raising a notice flag agree")
        void bothSpellingsAgree() {
            upkeep.noticeFlagOn(PlayerNotice.PN_IGNORE);

            assertTrue(upkeep.getNoticeFlags().has(PlayerNotice.PN_IGNORE));
            assertTrue(upkeep.isNotice());
        }

        /**
         * Clearing the last raised flag empties the set, so the next pass returns early rather than
         * doing nothing slowly.
         */
        @Test
        @DisplayName("clearing the last flag empties the set")
        void clearingEmptiesTheSet() {
            upkeep.setNoticeFlagOn(PlayerNotice.PN_COMBINE);
            upkeep.setNoticeFlagOff(PlayerNotice.PN_COMBINE);

            assertFalse(upkeep.isNotice());
            assertFalse(upkeep.getNoticeFlags().has(PlayerNotice.PN_COMBINE));
        }

        /**
         * Clearing one flag leaves the others, which is what lets an action queue another pass while
         * the current one is being cleared.
         */
        @Test
        @DisplayName("clearing one flag leaves the rest")
        void clearingOneLeavesTheRest() {
            upkeep.setNoticeFlagOn(PlayerNotice.PN_COMBINE);
            upkeep.setNoticeFlagOn(PlayerNotice.PN_IGNORE);

            upkeep.setNoticeFlagOff(PlayerNotice.PN_IGNORE);

            assertTrue(upkeep.isNotice());
            assertTrue(upkeep.getNoticeFlags().has(PlayerNotice.PN_COMBINE));
        }

        /**
         * The update flags take one at a time or several at once. There is no accessor for the set
         * — the turn loop reads the field directly — so the tests reach it by reflection, which is
         * the only way to show that raising several does not clear the one already up.
         *
         * @throws Exception if the flag field cannot be reached
         */
        @Test
        @DisplayName("update flags can be raised singly or together")
        void updateFlagsSinglyOrTogether() throws Exception {
            upkeep.setUpdateFlagOn(PlayerUpdateEnum.PU_INVEN);
            assertTrue(updateFlags().has(PlayerUpdateEnum.PU_INVEN));

            upkeep.setUpdateFlagsOn(PlayerUpdateEnum.PU_BONUS, PlayerUpdateEnum.PU_HP);

            assertTrue(updateFlags().has(PlayerUpdateEnum.PU_BONUS));
            assertTrue(updateFlags().has(PlayerUpdateEnum.PU_HP));
            assertTrue(updateFlags().has(PlayerUpdateEnum.PU_INVEN),
                    "raising several did not clear the one already up");
        }

        /**
         * Reads the update-flag set, which has no accessor.
         *
         * @return the live set
         * @throws Exception if the field cannot be reached
         */
        @SuppressWarnings("unchecked")
        private Flag<PlayerUpdateEnum> updateFlags() throws Exception {
            Field field = PlayerUpkeep.class.getDeclaredField("updateFlags");
            field.setAccessible(true);
            return (Flag<PlayerUpdateEnum>) field.get(upkeep);
        }

    }
}
