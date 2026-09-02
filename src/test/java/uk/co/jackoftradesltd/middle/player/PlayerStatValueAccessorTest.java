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
import org.junit.jupiter.api.extension.ExtendWith;
import uk.co.jackoftradesltd.middle.enums.Stats;
import uk.co.jackoftradesltd.testsupport.CalcBonusesFixture;
import uk.co.jackoftradesltd.testsupport.SeededPlayerRegistry;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link Player#getCurStatValue(Stats)} and {@link Player#getMaxStatValue(Stats)}, the two
 * reads that stand in for C's {@code p->stat_cur[stat]} and {@code p->stat_max[stat]}.
 *
 * <p>There is no C function to compare against - the original subscripts the arrays at the point of
 * use - so the expectations come from what the C sites require of those arrays. Three properties
 * carry the whole contract, and each has a test here.
 *
 * <p><b>The two arrays are separate.</b> {@code ui-display.c:158} paints a drained stat differently
 * precisely because {@code stat_cur} can sit below {@code stat_max}, and
 * {@code effect-handler-general.c:783} restores a stat by copying the maximum over the current
 * value. An accessor that read one field for both would pass a test using equal values, so the
 * tests here always separate them.
 *
 * <p><b>Each stat is its own slot.</b> C indexes by {@code stat}, and every caller - the
 * {@code calc_bonuses} loop at {@code player-calcs.c:2072} above all - relies on a write to one stat
 * leaving the other four alone.
 *
 * <p><b>The encoding is C's.</b> Values run 3 to 18, then {@code 18 + percentile} up to
 * {@code 18 + 100}; {@code player.c:158} clamps at {@code 18 + 99} inside the roll and
 * {@code player.c:161} hands over {@code 18 + 100}, so 118 is the ceiling an accessor must return
 * unchanged. Nothing is scaled or clamped on the way out.
 *
 * <p>The values are written through the private maps by reflection because {@link Player} has no
 * stat setters; {@code statDec} is used for the one case that needs the drain to be real rather than
 * arranged.
 *
 * <p>Class PlayerStatValueAccessorTest coded on 260901, commented in full on 260901.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class PlayerStatValueAccessorTest {

    /**
     * The five real stats, in C's order.
     */
    private static final Stats[] REAL_STATS = {Stats.STAT_STR, Stats.STAT_INT, Stats.STAT_WIS,
            Stats.STAT_DEX, Stats.STAT_CON};

    /**
     * The player under test.
     */
    private Player player;

    /**
     * A character with all five stats present, as birth leaves them.
     *
     * @throws Exception if a fixture field cannot be reached
     */
    @BeforeEach
    void newPlayer() throws Exception {
        player = CalcBonusesFixture.plainCharacter().player();
    }

    /**
     * Writes both stat maps outright.
     *
     * @param cur the current values
     * @param max the maximal values
     * @throws Exception if a field cannot be reached
     */
    private void stats(Map<Stats, Integer> cur, Map<Stats, Integer> max) throws Exception {
        set("statCur", new HashMap<>(cur));
        set("statMax", new HashMap<>(max));
    }

    /**
     * Gives every real stat the same pair of values.
     *
     * @param cur the current value for all five
     * @param max the maximal value for all five
     * @throws Exception if a field cannot be reached
     */
    private void allStats(int cur, int max) throws Exception {
        Map<Stats, Integer> curMap = new HashMap<>();
        Map<Stats, Integer> maxMap = new HashMap<>();
        for (Stats stat : REAL_STATS) {
            curMap.put(stat, cur);
            maxMap.put(stat, max);
        }
        stats(curMap, maxMap);
    }

    /**
     * Writes one of the player's private fields.
     *
     * @param name  the field's name
     * @param value the value to store
     * @throws Exception if the field cannot be reached
     */
    private void set(String name, Object value) throws Exception {
        Field field = Player.class.getDeclaredField(name);
        field.setAccessible(true);
        field.set(player, value);
    }

    /**
     * The ordinary read: what was stored for a stat comes back for that stat.
     */
    @Nested
    @DisplayName("reading a stat")
    class Reading {

        /**
         * A character at the birth-shaped state reads the same figure from both accessors.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("an undrained stat reads the same current and maximal value")
        void undrainedStatReadsAlike() throws Exception {
            allStats(16, 16);

            assertEquals(16, player.getCurStatValue(Stats.STAT_STR));
            assertEquals(16, player.getMaxStatValue(Stats.STAT_STR));
        }

        /**
         * The two arrays are distinct storage: a drained character's current value is below its
         * maximum, and each accessor answers from its own map.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("a drained stat reads current below maximal")
        void drainedStatReadsBelowItsMaximum() throws Exception {
            allStats(9, 17);

            assertEquals(9, player.getCurStatValue(Stats.STAT_CON));
            assertEquals(17, player.getMaxStatValue(Stats.STAT_CON));
            assertNotEquals(player.getCurStatValue(Stats.STAT_CON),
                    player.getMaxStatValue(Stats.STAT_CON));
        }

        /**
         * Each of the five stats is read from its own slot, so five different values come back five
         * different ways round.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("every stat is read from its own slot")
        void eachStatIsIndependent() throws Exception {
            Map<Stats, Integer> cur = new HashMap<>();
            Map<Stats, Integer> max = new HashMap<>();
            for (int index = 0; index < REAL_STATS.length; index++) {
                cur.put(REAL_STATS[index], 3 + index);
                max.put(REAL_STATS[index], 30 + index);
            }
            stats(cur, max);

            for (int index = 0; index < REAL_STATS.length; index++) {
                assertEquals(3 + index, player.getCurStatValue(REAL_STATS[index]));
                assertEquals(30 + index, player.getMaxStatValue(REAL_STATS[index]));
            }
        }

        /**
         * Writing one stat leaves the other four where they were - the property the
         * {@code calc_bonuses} loop depends on.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("changing one stat leaves the others alone")
        void changingOneStatLeavesTheOthers() throws Exception {
            allStats(10, 10);
            Map<Stats, Integer> cur = new HashMap<>();
            for (Stats stat : REAL_STATS) cur.put(stat, 10);
            cur.put(Stats.STAT_DEX, 18);
            set("statCur", cur);

            assertEquals(18, player.getCurStatValue(Stats.STAT_DEX));
            for (Stats stat : REAL_STATS) {
                if (stat == Stats.STAT_DEX) continue;
                assertEquals(10, player.getCurStatValue(stat));
                assertEquals(10, player.getMaxStatValue(stat));
            }
            assertEquals(10, player.getMaxStatValue(Stats.STAT_DEX));
        }
    }

    /**
     * The C encoding, returned as stored.
     */
    @Nested
    @DisplayName("the C encoding")
    class Encoding {

        /**
         * The bottom of the ordinary range. {@code player.c:196} refuses to drain below 3, so it is
         * the lowest figure an accessor ever has to return.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("the floor of 3 reads back unchanged")
        void floorReadsBack() throws Exception {
            allStats(3, 3);

            assertEquals(3, player.getCurStatValue(Stats.STAT_STR));
            assertEquals(3, player.getMaxStatValue(Stats.STAT_STR));
        }

        /**
         * 18 is the join between the plain and the percentile part of the scale, and the value
         * {@code player.c:190} drops an exceptional stat to on a drain.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("18, the join in the scale, reads back unchanged")
        void eighteenReadsBack() throws Exception {
            allStats(18, 18);

            assertEquals(18, player.getCurStatValue(Stats.STAT_WIS));
            assertEquals(18, player.getMaxStatValue(Stats.STAT_WIS));
        }

        /**
         * The first exceptional value, 18/01. Nothing splits the percentile off on the way out.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("18/01 reads back as 19, not as a split pair")
        void firstPercentileReadsBack() throws Exception {
            allStats(18 + 1, 18 + 1);

            assertEquals(19, player.getCurStatValue(Stats.STAT_INT));
            assertEquals(19, player.getMaxStatValue(Stats.STAT_INT));
        }

        /**
         * The ceiling of {@code 18 + 100}, which {@code player.c:161} sets directly and
         * {@code ui-player.c:478} recognises as 18/100. It reads back as 118, not clamped to
         * anything smaller.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("the ceiling of 18/100 reads back as 118")
        void ceilingReadsBack() throws Exception {
            allStats(18 + 100, 18 + 100);

            assertEquals(118, player.getCurStatValue(Stats.STAT_STR));
            assertEquals(118, player.getMaxStatValue(Stats.STAT_STR));
        }
    }

    /**
     * The accessors against a drain that really happened, rather than one arranged by reflection.
     */
    @Nested
    @DisplayName("after a real drain")
    class AfterDrain {

        /**
         * A temporary drain moves the current value and leaves the maximum: from 18/50 the current
         * value loses ten points ({@code player.c:186}) and the maximum stays where it was.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("a temporary drain moves only the current value")
        void temporaryDrainMovesOnlyCurrent() throws Exception {
            allStats(18 + 50, 18 + 50);

            assertTrue(player.statDec(Stats.STAT_STR, false));

            assertEquals(18 + 40, player.getCurStatValue(Stats.STAT_STR));
            assertEquals(18 + 50, player.getMaxStatValue(Stats.STAT_STR));
        }

        /**
         * A permanent drain moves both, so the accessors agree again at the lower figure.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("a permanent drain moves both values")
        void permanentDrainMovesBoth() throws Exception {
            allStats(18 + 50, 18 + 50);

            assertTrue(player.statDec(Stats.STAT_STR, true));

            assertEquals(18 + 40, player.getCurStatValue(Stats.STAT_STR));
            assertEquals(18 + 40, player.getMaxStatValue(Stats.STAT_STR));
        }
    }

    /**
     * Where the map port and the C array part company.
     */
    @Nested
    @DisplayName("stats with no slot")
    class NoSlot {

        /**
         * C's arrays are {@code STAT_MAX} long, so the two sentinels have no slot at all; the port
         * has no map entry for them and throws rather than reading whatever lies past the array.
         * This is a difference in failure mode, not in behaviour - no caller passes a sentinel.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("the sentinels have no value to read")
        void sentinelsThrow() throws Exception {
            allStats(10, 10);

            assertThrows(NullPointerException.class,
                    () -> player.getCurStatValue(Stats.STAT_NONE));
            assertThrows(NullPointerException.class,
                    () -> player.getMaxStatValue(Stats.STAT_MAX));
        }
    }
}
