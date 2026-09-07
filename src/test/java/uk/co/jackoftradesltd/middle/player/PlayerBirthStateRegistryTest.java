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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.middle.enums.Stats;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link PlayerBirthStateRegistry}, the port of the block of C statics at
 * {@code player-birth.c:118-138} - {@code stats}, {@code points_spent}, {@code points_inc},
 * {@code points_left}, {@code quickstart_allowed}, {@code rolled_stats}, {@code prev} and
 * {@code quickstart_prev}.
 *
 * <p>There is no C function to derive expected values from here: the class holds data, not control
 * flow, so what each suite pins down is a field's C-derived default (C's implicit zero/false, or its
 * one explicit {@code = false}) and that a write is read back unchanged. The port adds behaviour C
 * has none of in two places, both pinned down as the port's own documented choice rather than
 * derived from a C read: the {@code -1} answered for a stat, points-spent or points-inc entry that
 * has never been written, and the fresh, never-{@code null} {@link Birther} {@link
 * PlayerBirthStateRegistry#getPrev()}/{@link PlayerBirthStateRegistry#getQuickstartPrev()} backfill
 * before anything has been set. C's arrays and structs cannot observe either default themselves -
 * every C caller of the arrays fills the whole thing before the birth screen ever reads it, and C's
 * {@code prev}/{@code quickstart_prev} are never absent to begin with, only ever zero-valued.
 *
 * <p>All eight fields are static, so every test resets them directly through reflection before
 * running, rather than through {@link PlayerBirthStateRegistry#initPlayerBirthStateRegistry()} - that
 * method is itself under test, and reflection gives a state no public call reaches: the three maps
 * and the two {@link Birther} fields genuinely {@code null}, and {@code inited} clear, as they are
 * before anything in the class has ever run.
 *
 * <p>Class PlayerBirthStateRegistryTest coded on 260907, commented in full on 260907.
 *
 * @author Rowan Crowther
 */
class PlayerBirthStateRegistryTest {

    /**
     * Reaches one of the registry's private static fields.
     *
     * @param name the field's name
     * @return the field, made accessible
     * @throws ReflectiveOperationException if no such field exists
     */
    private static Field field(String name) throws ReflectiveOperationException {
        Field field = PlayerBirthStateRegistry.class.getDeclaredField(name);
        field.setAccessible(true);
        return field;
    }

    /**
     * Puts every field back to the state C's loader would give it before anything has run - the three
     * maps and the two {@link Birther} snapshots genuinely {@code null}, {@code inited} clear so the
     * maps are eligible to be (re)created, {@code pointsLeft} at {@code 0}, and the two flags
     * {@code false} - so no test inherits another's writes.
     *
     * @throws ReflectiveOperationException if a field cannot be reached
     */
    @BeforeEach
    void resetRegistry() throws ReflectiveOperationException {
        field("inited").setBoolean(null, false);
        field("stats").set(null, null);
        field("pointsSpent").set(null, null);
        field("pointsInc").set(null, null);
        field("pointsLeft").setInt(null, 0);
        field("quickstartAllowed").setBoolean(null, false);
        field("rolledStats").setBoolean(null, false);
        field("prev").set(null, null);
        field("quickstartPrev").set(null, null);
    }

    /**
     * {@link PlayerBirthStateRegistry#getStat(Stats)} / {@link
     * PlayerBirthStateRegistry#setStat(Stats, int)} - the port of C's {@code stats[stat]}.
     */
    @Nested
    @DisplayName("the stats map")
    class StatsMap {

        /**
         * A stat never written to answers {@code -1}, and reaching it with the backing map still
         * {@code null} does not throw - the lazy {@code if (stats == null) initPlayerBirthStateRegistry()}
         * guard in {@link PlayerBirthStateRegistry#getStat(Stats)} covers a call before the map has
         * ever been created.
         */
        @Test
        @DisplayName("answers -1 for a stat never set, without throwing on a null backing map")
        void defaultsToMinusOne() {
            assertEquals(-1, PlayerBirthStateRegistry.getStat(Stats.STAT_STR));
        }

        /**
         * A write is read back unchanged, and reaching {@link
         * PlayerBirthStateRegistry#setStat(Stats, int)} with the backing map still {@code null} does
         * not throw either - the same lazy-init guard on the write side.
         */
        @Test
        @DisplayName("round-trips a value written through setStat, from a null backing map")
        void roundTripsFromNull() {
            PlayerBirthStateRegistry.setStat(Stats.STAT_CON, 17);

            assertEquals(17, PlayerBirthStateRegistry.getStat(Stats.STAT_CON));
        }

        /**
         * Writing one stat does not disturb another's stored value or default.
         */
        @Test
        @DisplayName("keeps each stat's value independent of the others")
        void keepsStatsIndependent() {
            PlayerBirthStateRegistry.setStat(Stats.STAT_STR, 15);
            PlayerBirthStateRegistry.setStat(Stats.STAT_INT, 9);

            assertEquals(15, PlayerBirthStateRegistry.getStat(Stats.STAT_STR));
            assertEquals(9, PlayerBirthStateRegistry.getStat(Stats.STAT_INT));
            assertEquals(-1, PlayerBirthStateRegistry.getStat(Stats.STAT_WIS));
        }
    }

    /**
     * {@link PlayerBirthStateRegistry#getStats()} / {@link PlayerBirthStateRegistry#setStats(Map)}
     * - the whole-array form of C's {@code stats}, the shape {@code reset_stats}, {@code
     * generate_stats} and {@code get_stats} use when they take the array by reference rather than
     * one stat at a time ({@code player-birth.c:715, 829, 1181}).
     */
    @Nested
    @DisplayName("the stats whole map")
    class StatsWholeMap {

        /**
         * A read before anything has been set lazily creates the map fresh and empty, rather than
         * answering {@code null} - the same guard the single-stat accessors rely on.
         */
        @Test
        @DisplayName("lazily creates an empty, non-null map before anything is set")
        void lazilyCreatesEmptyMap() {
            Map<Stats, Integer> stats = PlayerBirthStateRegistry.getStats();

            assertNotNull(stats);
            assertTrue(stats.isEmpty());
        }

        /**
         * Every call answers the same map instance, not a defensive copy - matching C's array
         * being one fixed piece of storage for the life of the process.
         */
        @Test
        @DisplayName("returns the same live map instance across calls")
        void returnsTheSameInstance() {
            assertSame(PlayerBirthStateRegistry.getStats(), PlayerBirthStateRegistry.getStats());
        }

        /**
         * A map written through {@link PlayerBirthStateRegistry#setStats(Map)} is read back as the
         * very same reference.
         */
        @Test
        @DisplayName("round-trips the same reference written through setStats")
        void roundTrips() {
            Map<Stats, Integer> written = new HashMap<>();
            written.put(Stats.STAT_STR, 5);

            PlayerBirthStateRegistry.setStats(written);

            assertSame(written, PlayerBirthStateRegistry.getStats());
        }

        /**
         * A write through the single-stat {@link PlayerBirthStateRegistry#setStat(Stats, int)} is
         * visible through the whole map {@link PlayerBirthStateRegistry#getStats()} returns - both
         * read and write the one backing map, the same way every C caller sees its writes to
         * {@code stats[i]} reflected in the array it also holds a reference to.
         */
        @Test
        @DisplayName("reflects a write made through setStat")
        void reflectsSetStat() {
            PlayerBirthStateRegistry.setStat(Stats.STAT_DEX, 14);

            assertEquals(14, PlayerBirthStateRegistry.getStats().get(Stats.STAT_DEX));
        }

        /**
         * Mutating the map handed back by {@link PlayerBirthStateRegistry#getStats()} directly is
         * visible through {@link PlayerBirthStateRegistry#getStat(Stats)} - the shape a ported
         * {@code generate_stats} or {@code reset_stats} would use, filling the array in place
         * through the reference it was handed rather than replacing it.
         */
        @Test
        @DisplayName("a direct mutation of the returned map is visible through getStat")
        void directMutationIsVisible() {
            PlayerBirthStateRegistry.getStats().put(Stats.STAT_CON, 12);

            assertEquals(12, PlayerBirthStateRegistry.getStat(Stats.STAT_CON));
        }
    }

    /**
     * {@link PlayerBirthStateRegistry#getPointsSpent(Stats)} / {@link
     * PlayerBirthStateRegistry#setPointsSpent(Stats, int)} - the port of C's {@code points_spent[stat]}.
     */
    @Nested
    @DisplayName("the pointsSpent map")
    class PointsSpentMap {

        /**
         * A stat with nothing recorded answers {@code -1}, from a null backing map.
         */
        @Test
        @DisplayName("answers -1 for a stat never set, without throwing on a null backing map")
        void defaultsToMinusOne() {
            assertEquals(-1, PlayerBirthStateRegistry.getPointsSpent(Stats.STAT_DEX));
        }

        /**
         * A write is read back unchanged, from a null backing map.
         */
        @Test
        @DisplayName("round-trips a value written through setPointsSpent, from a null backing map")
        void roundTripsFromNull() {
            PlayerBirthStateRegistry.setPointsSpent(Stats.STAT_WIS, 8);

            assertEquals(8, PlayerBirthStateRegistry.getPointsSpent(Stats.STAT_WIS));
        }

        /**
         * Writing one stat's spend does not disturb another's.
         */
        @Test
        @DisplayName("keeps each stat's value independent of the others")
        void keepsStatsIndependent() {
            PlayerBirthStateRegistry.setPointsSpent(Stats.STAT_STR, 5);
            PlayerBirthStateRegistry.setPointsSpent(Stats.STAT_CON, 3);

            assertEquals(5, PlayerBirthStateRegistry.getPointsSpent(Stats.STAT_STR));
            assertEquals(3, PlayerBirthStateRegistry.getPointsSpent(Stats.STAT_CON));
            assertEquals(-1, PlayerBirthStateRegistry.getPointsSpent(Stats.STAT_INT));
        }
    }

    /**
     * {@link PlayerBirthStateRegistry#getPointsSpent()} / {@link
     * PlayerBirthStateRegistry#setPointsSpent(Map)} - the whole-array form of C's {@code
     * points_spent}, the shape {@code reset_stats}, {@code generate_stats}, {@code buy_stat} and
     * {@code sell_stat} use when they take the array by reference ({@code player-birth.c:715,
     * 829, 1137, 1148}).
     */
    @Nested
    @DisplayName("the pointsSpent whole map")
    class PointsSpentWholeMap {

        /**
         * A read before anything has been set lazily creates the map fresh and empty.
         */
        @Test
        @DisplayName("lazily creates an empty, non-null map before anything is set")
        void lazilyCreatesEmptyMap() {
            Map<Stats, Integer> pointsSpent = PlayerBirthStateRegistry.getPointsSpent();

            assertNotNull(pointsSpent);
            assertTrue(pointsSpent.isEmpty());
        }

        /**
         * A map written through {@link PlayerBirthStateRegistry#setPointsSpent(Map)} is read back
         * as the very same reference.
         */
        @Test
        @DisplayName("round-trips the same reference written through setPointsSpent")
        void roundTrips() {
            Map<Stats, Integer> written = new HashMap<>();
            written.put(Stats.STAT_WIS, 6);

            PlayerBirthStateRegistry.setPointsSpent(written);

            assertSame(written, PlayerBirthStateRegistry.getPointsSpent());
        }

        /**
         * A write through the single-stat {@link PlayerBirthStateRegistry#setPointsSpent(Stats,
         * int)} is visible through the whole map.
         */
        @Test
        @DisplayName("reflects a write made through setPointsSpent(Stats, int)")
        void reflectsSingleStatWrite() {
            PlayerBirthStateRegistry.setPointsSpent(Stats.STAT_INT, 3);

            assertEquals(3, PlayerBirthStateRegistry.getPointsSpent().get(Stats.STAT_INT));
        }
    }

    /**
     * {@link PlayerBirthStateRegistry#getPointsInc(Stats)} / {@link
     * PlayerBirthStateRegistry#setPointsInc(Stats, int)} - the port of C's {@code points_inc[stat]}.
     */
    @Nested
    @DisplayName("the pointsInc map")
    class PointsIncMap {

        /**
         * A stat with no cost recorded answers {@code -1}, from a null backing map.
         */
        @Test
        @DisplayName("answers -1 for a stat never set, without throwing on a null backing map")
        void defaultsToMinusOne() {
            assertEquals(-1, PlayerBirthStateRegistry.getPointsInc(Stats.STAT_CON));
        }

        /**
         * A write is read back unchanged, from a null backing map.
         */
        @Test
        @DisplayName("round-trips a value written through setPointsInc, from a null backing map")
        void roundTripsFromNull() {
            PlayerBirthStateRegistry.setPointsInc(Stats.STAT_STR, 4);

            assertEquals(4, PlayerBirthStateRegistry.getPointsInc(Stats.STAT_STR));
        }

        /**
         * Writing one stat's increment cost does not disturb another's.
         */
        @Test
        @DisplayName("keeps each stat's value independent of the others")
        void keepsStatsIndependent() {
            PlayerBirthStateRegistry.setPointsInc(Stats.STAT_INT, 1);
            PlayerBirthStateRegistry.setPointsInc(Stats.STAT_DEX, 2);

            assertEquals(1, PlayerBirthStateRegistry.getPointsInc(Stats.STAT_INT));
            assertEquals(2, PlayerBirthStateRegistry.getPointsInc(Stats.STAT_DEX));
            assertEquals(-1, PlayerBirthStateRegistry.getPointsInc(Stats.STAT_WIS));
        }
    }

    /**
     * {@link PlayerBirthStateRegistry#getPointsInc()} / {@link
     * PlayerBirthStateRegistry#setPointsInc(Map)} - the whole-array form of C's {@code
     * points_inc}, the shape {@code reset_stats}, {@code generate_stats}, {@code buy_stat} and
     * {@code sell_stat} use when they take the array by reference ({@code player-birth.c:715,
     * 829, 1137, 1148}).
     */
    @Nested
    @DisplayName("the pointsInc whole map")
    class PointsIncWholeMap {

        /**
         * A read before anything has been set lazily creates the map fresh and empty.
         */
        @Test
        @DisplayName("lazily creates an empty, non-null map before anything is set")
        void lazilyCreatesEmptyMap() {
            Map<Stats, Integer> pointsInc = PlayerBirthStateRegistry.getPointsInc();

            assertNotNull(pointsInc);
            assertTrue(pointsInc.isEmpty());
        }

        /**
         * A map written through {@link PlayerBirthStateRegistry#setPointsInc(Map)} is read back as
         * the very same reference.
         */
        @Test
        @DisplayName("round-trips the same reference written through setPointsInc")
        void roundTrips() {
            Map<Stats, Integer> written = new HashMap<>();
            written.put(Stats.STAT_DEX, 2);

            PlayerBirthStateRegistry.setPointsInc(written);

            assertSame(written, PlayerBirthStateRegistry.getPointsInc());
        }

        /**
         * A write through the single-stat {@link PlayerBirthStateRegistry#setPointsInc(Stats, int)}
         * is visible through the whole map.
         */
        @Test
        @DisplayName("reflects a write made through setPointsInc(Stats, int)")
        void reflectsSingleStatWrite() {
            PlayerBirthStateRegistry.setPointsInc(Stats.STAT_CON, 1);

            assertEquals(1, PlayerBirthStateRegistry.getPointsInc().get(Stats.STAT_CON));
        }
    }

    /**
     * The three whole-map setters against the {@code inited} guard they share with {@link
     * PlayerBirthStateRegistry#initPlayerBirthStateRegistry()} - a regression suite for a bug
     * caught in review before these tests were written: the setters originally assigned their
     * field directly without touching {@code inited}, so calling one before anything else had
     * touched the registry left {@code inited} still {@code false}; the next call to any of the
     * three whole-map getters would then see {@code inited == false}, run {@code
     * initPlayerBirthStateRegistry()}, and silently overwrite all three maps with fresh empty
     * ones - discarding whatever had just been stored. Each test below sets one map first, from
     * the registry's cold state, then reads a different map through its getter, and checks the
     * first write survived.
     */
    @Nested
    @DisplayName("whole-map setters guard inited the same way the getters do")
    class WholeMapSetterInitOrdering {

        /**
         * {@link PlayerBirthStateRegistry#setStats(Map)} called first must survive a later {@link
         * PlayerBirthStateRegistry#getPointsInc()}, and the untouched {@code pointsSpent} map must
         * still have been created fresh and empty by that same {@code inited} guard.
         */
        @Test
        @DisplayName("setStats survives a later getPointsInc call")
        void setStatsSurvivesLaterGetPointsInc() {
            Map<Stats, Integer> written = new HashMap<>();
            written.put(Stats.STAT_STR, 5);
            PlayerBirthStateRegistry.setStats(written);

            PlayerBirthStateRegistry.getPointsInc();

            assertSame(written, PlayerBirthStateRegistry.getStats());
            assertEquals(5, PlayerBirthStateRegistry.getStat(Stats.STAT_STR));
            assertNotNull(PlayerBirthStateRegistry.getPointsSpent());
            assertTrue(PlayerBirthStateRegistry.getPointsSpent().isEmpty());
        }

        /**
         * {@link PlayerBirthStateRegistry#setPointsSpent(Map)} called first must survive a later
         * {@link PlayerBirthStateRegistry#getStats()}.
         */
        @Test
        @DisplayName("setPointsSpent survives a later getStats call")
        void setPointsSpentSurvivesLaterGetStats() {
            Map<Stats, Integer> written = new HashMap<>();
            written.put(Stats.STAT_WIS, 6);
            PlayerBirthStateRegistry.setPointsSpent(written);

            PlayerBirthStateRegistry.getStats();

            assertSame(written, PlayerBirthStateRegistry.getPointsSpent());
            assertEquals(6, PlayerBirthStateRegistry.getPointsSpent(Stats.STAT_WIS));
        }

        /**
         * {@link PlayerBirthStateRegistry#setPointsInc(Map)} called first must survive a later
         * {@link PlayerBirthStateRegistry#getStats()}.
         */
        @Test
        @DisplayName("setPointsInc survives a later getStats call")
        void setPointsIncSurvivesLaterGetStats() {
            Map<Stats, Integer> written = new HashMap<>();
            written.put(Stats.STAT_DEX, 2);
            PlayerBirthStateRegistry.setPointsInc(written);

            PlayerBirthStateRegistry.getStats();

            assertSame(written, PlayerBirthStateRegistry.getPointsInc());
            assertEquals(2, PlayerBirthStateRegistry.getPointsInc(Stats.STAT_DEX));
        }
    }

    /**
     * {@link PlayerBirthStateRegistry#getPointsLeft()} / {@link
     * PlayerBirthStateRegistry#setPointsLeft(int)} - the port of C's {@code points_left}, a plain
     * {@code int} with no lazy creation to guard.
     */
    @Nested
    @DisplayName("pointsLeft")
    class PointsLeft {

        /**
         * Defaults to {@code 0}, matching C's implicit zero-initialised static {@code int}.
         */
        @Test
        @DisplayName("defaults to 0")
        void defaultsToZero() {
            assertEquals(0, PlayerBirthStateRegistry.getPointsLeft());
        }

        /**
         * A write is read back unchanged.
         */
        @Test
        @DisplayName("round-trips a value written through setPointsLeft")
        void roundTrips() {
            PlayerBirthStateRegistry.setPointsLeft(20);

            assertEquals(20, PlayerBirthStateRegistry.getPointsLeft());
        }
    }

    /**
     * {@link PlayerBirthStateRegistry#isQuickstartAllowed()} / {@link
     * PlayerBirthStateRegistry#setQuickstartAllowed(boolean)} - the port of C's
     * {@code quickstart_allowed}.
     */
    @Nested
    @DisplayName("quickstartAllowed")
    class QuickstartAllowed {

        /**
         * Defaults to {@code false}, matching C's implicit zero-initialised static {@code bool}.
         */
        @Test
        @DisplayName("defaults to false")
        void defaultsToFalse() {
            assertFalse(PlayerBirthStateRegistry.isQuickstartAllowed());
        }

        /**
         * A write of {@code true} is read back unchanged.
         */
        @Test
        @DisplayName("round-trips true written through setQuickstartAllowed")
        void roundTripsTrue() {
            PlayerBirthStateRegistry.setQuickstartAllowed(true);

            assertTrue(PlayerBirthStateRegistry.isQuickstartAllowed());
        }
    }

    /**
     * {@link PlayerBirthStateRegistry#isRolledStats()} / {@link
     * PlayerBirthStateRegistry#setRolledStats(boolean)} - the port of C's {@code rolled_stats}, the
     * one field of the six C initialises explicitly ({@code static bool rolled_stats = false}) rather
     * than leaving to implicit zero.
     */
    @Nested
    @DisplayName("rolledStats")
    class RolledStats {

        /**
         * Defaults to {@code false}, matching C's explicit initialiser.
         */
        @Test
        @DisplayName("defaults to false")
        void defaultsToFalse() {
            assertFalse(PlayerBirthStateRegistry.isRolledStats());
        }

        /**
         * A write of {@code true} is read back unchanged.
         */
        @Test
        @DisplayName("round-trips true written through setRolledStats")
        void roundTripsTrue() {
            PlayerBirthStateRegistry.setRolledStats(true);

            assertTrue(PlayerBirthStateRegistry.isRolledStats());
        }
    }

    /**
     * {@link PlayerBirthStateRegistry#initPlayerBirthStateRegistry()}, which has no C equivalent: C's
     * {@code stats}, {@code points_spent} and {@code points_inc} are zeroed once by the loader and
     * never need recreating. The port's {@code inited} guard gives it the matching one-shot shape:
     * the three maps are created once, ever, and a later call is a no-op rather than a reset.
     */
    @Nested
    @DisplayName("initPlayerBirthStateRegistry")
    class Init {

        /**
         * The first call - the state every test in this class starts from, with the three maps
         * genuinely {@code null} - creates all three fresh and empty, so every real stat still
         * answers the unset sentinel afterwards.
         */
        @Test
        @DisplayName("creates all three maps fresh and empty on the first call")
        void createsTheMapsFreshOnFirstCall() {
            PlayerBirthStateRegistry.initPlayerBirthStateRegistry();

            assertEquals(-1, PlayerBirthStateRegistry.getStat(Stats.STAT_STR));
            assertEquals(-1, PlayerBirthStateRegistry.getPointsSpent(Stats.STAT_STR));
            assertEquals(-1, PlayerBirthStateRegistry.getPointsInc(Stats.STAT_STR));
        }

        /**
         * A second call, once the maps already hold data, leaves that data alone rather than
         * wiping it - the {@code inited} guard's whole point, and the regression this test exists
         * to catch: an earlier version of the guard was missing, so a second call here would
         * silently reset every stat back to the unset sentinel.
         */
        @Test
        @DisplayName("a second call leaves already-written stats untouched rather than clearing them")
        void secondCallDoesNotClearExistingData() {
            PlayerBirthStateRegistry.setStat(Stats.STAT_STR, 18);
            PlayerBirthStateRegistry.setPointsSpent(Stats.STAT_STR, 4);
            PlayerBirthStateRegistry.setPointsInc(Stats.STAT_STR, 2);

            PlayerBirthStateRegistry.initPlayerBirthStateRegistry();

            assertEquals(18, PlayerBirthStateRegistry.getStat(Stats.STAT_STR));
            assertEquals(4, PlayerBirthStateRegistry.getPointsSpent(Stats.STAT_STR));
            assertEquals(2, PlayerBirthStateRegistry.getPointsInc(Stats.STAT_STR));
        }

        /**
         * {@code pointsLeft}, {@code quickstartAllowed}, {@code rolledStats}, {@code prev} and
         * {@code quickstartPrev} are not among the fields it (re)creates - only the three maps at
         * {@code player-birth.c:118-120} are - so calling it leaves all five exactly as they were.
         */
        @Test
        @DisplayName("leaves pointsLeft, quickstartAllowed, rolledStats, prev and quickstartPrev untouched")
        void leavesTheOtherFieldsUntouched() {
            PlayerBirthStateRegistry.setPointsLeft(20);
            PlayerBirthStateRegistry.setQuickstartAllowed(true);
            PlayerBirthStateRegistry.setRolledStats(true);
            Birther prev = new Birther();
            Birther quickstartPrev = new Birther();
            PlayerBirthStateRegistry.setPrev(prev);
            PlayerBirthStateRegistry.setQuickstartPrev(quickstartPrev);

            PlayerBirthStateRegistry.initPlayerBirthStateRegistry();

            assertEquals(20, PlayerBirthStateRegistry.getPointsLeft());
            assertTrue(PlayerBirthStateRegistry.isQuickstartAllowed());
            assertTrue(PlayerBirthStateRegistry.isRolledStats());
            assertSame(prev, PlayerBirthStateRegistry.getPrev());
            assertSame(quickstartPrev, PlayerBirthStateRegistry.getQuickstartPrev());
        }

        /**
         * Calling it directly, with every map already {@code null}, does not throw - the state every
         * test in this class starts from.
         */
        @Test
        @DisplayName("does not throw when the maps are already null")
        void doesNotThrowFromNull() {
            assertDoesNotThrow(PlayerBirthStateRegistry::initPlayerBirthStateRegistry);
        }
    }

    /**
     * {@link PlayerBirthStateRegistry#getPrev()} / {@link PlayerBirthStateRegistry#setPrev(Birther)}
     * - the port of C's {@code prev} ({@code player-birth.c:131}), the birth screen's "flick between
     * two rolls" undo snapshot.
     */
    @Nested
    @DisplayName("prev")
    class Prev {

        /**
         * A read before anything has been set answers a fresh, non-{@code null} {@link Birther}
         * rather than {@code null} - C's own {@code prev} is a zero-initialised struct, never
         * absent, and its {@code age} field is exactly zero on a snapshot nothing has written to
         * yet, matching the default a bare {@code new Birther()} carries.
         */
        @Test
        @DisplayName("answers a fresh, non-null snapshot with age 0 before anything is set")
        void defaultsToAFreshZeroAgeSnapshot() {
            Birther prev = PlayerBirthStateRegistry.getPrev();

            assertNotNull(prev);
            assertEquals(0, prev.getAge());
        }

        /**
         * A write is read back as the very same reference - matching C's raw struct assignment,
         * which a caller reading the same static back would also see reflected in place.
         */
        @Test
        @DisplayName("round-trips the same reference written through setPrev")
        void roundTrips() {
            Birther saved = new Birther();
            saved.setAge(42);

            PlayerBirthStateRegistry.setPrev(saved);

            assertSame(saved, PlayerBirthStateRegistry.getPrev());
        }
    }

    /**
     * {@link PlayerBirthStateRegistry#getQuickstartPrev()} / {@link
     * PlayerBirthStateRegistry#setQuickstartPrev(Birther)} - the port of C's {@code quickstart_prev}
     * ({@code player-birth.c:138}), the quickstart restore snapshot.
     */
    @Nested
    @DisplayName("quickstartPrev")
    class QuickstartPrev {

        /**
         * The same never-{@code null}, zero-age default {@link Prev#defaultsToAFreshZeroAgeSnapshot}
         * checks for {@code prev} - {@link PlayerBirth#doCmdBirthInit} depends on this directly,
         * handing the result straight to {@link PlayerBirth#saveRollerData}, which writes every
         * field of whatever it is given with no null check of its own; without this backfill, the
         * first quickstart birth of a run would hand it {@code null} and throw.
         */
        @Test
        @DisplayName("answers a fresh, non-null snapshot with age 0 before anything is set")
        void defaultsToAFreshZeroAgeSnapshot() {
            Birther quickstartPrev = PlayerBirthStateRegistry.getQuickstartPrev();

            assertNotNull(quickstartPrev);
            assertEquals(0, quickstartPrev.getAge());
        }

        /**
         * A write is read back as the very same reference.
         */
        @Test
        @DisplayName("round-trips the same reference written through setQuickstartPrev")
        void roundTrips() {
            Birther saved = new Birther();
            saved.setAge(17);

            PlayerBirthStateRegistry.setQuickstartPrev(saved);

            assertSame(saved, PlayerBirthStateRegistry.getQuickstartPrev());
        }

        /**
         * {@code prev} and {@code quickstartPrev} are independent fields; writing one must not be
         * observable through the other.
         */
        @Test
        @DisplayName("stays independent of prev")
        void independentOfPrev() {
            Birther prevSnapshot = new Birther();
            Birther quickstartSnapshot = new Birther();

            PlayerBirthStateRegistry.setPrev(prevSnapshot);
            PlayerBirthStateRegistry.setQuickstartPrev(quickstartSnapshot);

            assertSame(prevSnapshot, PlayerBirthStateRegistry.getPrev());
            assertSame(quickstartSnapshot, PlayerBirthStateRegistry.getQuickstartPrev());
        }
    }
}
