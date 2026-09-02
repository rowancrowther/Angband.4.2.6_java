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
import uk.co.jackoftradesltd.middle.numerics.RandomValueUtils;
import uk.co.jackoftradesltd.middle.player.enums.PlayerUpdateEnum;
import uk.co.jackoftradesltd.testsupport.SeededPlayerRegistry;
import uk.co.jackoftradesltd.middle.enums.Stats;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@code Player.playerStatInc}, the port of C's {@code player_stat_inc}
 * ({@code src/player.c:145}).
 *
 * <p>Every expectation below is read off that C, and where the C's own unit test
 * ({@code src/tests/player/playerstat.c:33}) pins a value, that value is used here too.
 *
 * <p><b>Three bands and two boundaries.</b> The function splits on the current value: at or above
 * {@code 18 + 100} it refuses, below 18 it adds exactly one, below {@code 18 + 90} it rolls, and
 * otherwise it jumps straight to the ceiling. The two boundaries are where a port goes wrong, so
 * each is tested from both sides — 17 against 18, and 107 against 108. Both of the outer bands are
 * deterministic, which is what makes them assertable as exact values; the middle band is not, and is
 * asserted as a range computed from the C's own formula.
 *
 * <p><b>The ceiling test is inclusive.</b> C is {@code if (v >= 18 + 100) return false}, so 118
 * itself is a refusal and not a no-op write. The difference is visible in three places at once: the
 * answer, the stat, and whether {@code PU_BONUS} was raised. All three are asserted, because a port
 * using {@code >} would leave the stat looking correct while still claiming a gain and still
 * scheduling a recalculation.
 *
 * <p><b>The clamp is defensive.</b> C clamps the rolled result to {@code 18 + 99}, but with 4.2.6's
 * constants the roll cannot reach it — the largest result anywhere in the band is 113. That is not
 * an assumption to leave implicit, so {@link RolledBand#theBandNeverReachesTheClampLetAloneTheCeiling}
 * sweeps the whole band and pins it. The clamp is ported because it is a branch in the C, and
 * because it is what would hold if those constants moved.
 *
 * <p>The random draws are seeded so a failure is reproducible, and every assertion about a rolled
 * value is a range rather than a number, since the port's generator is not C's and does not produce
 * the same stream for the same seed.
 *
 * <p>Class PlayerStatIncTest coded on 260831, commented in full on 260831.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class PlayerStatIncTest {

    /**
     * The player under test, fresh for each test since all of this is mutable.
     */
    private Player player;

    /**
     * C's {@code gain} for a current value in the rolled band:
     * {@code (((18 + 100) - v) / 2 + 3) / 2}, floored at 1 ({@code player.c:153-155}).
     *
     * @param value the current stat value
     * @return the gain the C would compute
     */
    private static int cGain(int value) {
        int gain = (((18 + 100) - value) / 2 + 3) / 2;
        return Math.max(gain, 1);
    }

    /**
     * A new player with every stat present, since the port reads maps where C reads a zeroed array
     * and an absent key would throw rather than read as nothing.
     */
    @BeforeEach
    void newPlayer() throws Exception {
        player = new Player();
        RandomValueUtils.stateInit(20260831L);
        Map<Stats, Integer> cur = new HashMap<>();
        Map<Stats, Integer> max = new HashMap<>();
        for (Stats stat : Stats.values()) {
            if (stat == Stats.STAT_NONE || stat == Stats.STAT_MAX) continue;
            cur.put(stat, 10);
            max.put(stat, 10);
        }
        set("statCur", cur);
        set("statMax", max);
    }

    /**
     * Writes one of the player's private fields, for the state with no setter.
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
     * Reads one of the player's stat maps.
     *
     * @param name the field's name
     * @return the map
     * @throws Exception if the field cannot be reached
     */
    @SuppressWarnings("unchecked")
    private Map<Stats, Integer> read(String name) throws Exception {
        Field field = Player.class.getDeclaredField(name);
        field.setAccessible(true);
        return (Map<Stats, Integer>) field.get(player);
    }

    /**
     * Sets one stat's current and maximum together, which is what an undrained character has.
     *
     * @param stat  the stat
     * @param value the value
     * @throws Exception if a field cannot be reached
     */
    private void stat(Stats stat, int value) throws Exception {
        read("statCur").put(stat, value);
        read("statMax").put(stat, value);
    }

    /**
     * @param stat the stat
     * @return the stat's current value
     * @throws Exception if the field cannot be reached
     */
    private int cur(Stats stat) throws Exception {
        return read("statCur").get(stat);
    }

    /**
     * @param stat the stat
     * @return the stat's maximum value
     * @throws Exception if the field cannot be reached
     */
    private int max(Stats stat) throws Exception {
        return read("statMax").get(stat);
    }

    /**
     * The ceiling, where the function does nothing at all.
     */
    @Nested
    @DisplayName("at the ceiling")
    class AtTheCeiling {

        /**
         * C is {@code if (v >= 18 + 100) return false}, so the ceiling itself is a refusal. The
         * whole point of the inclusive test is that nothing happens, so the stat and the update
         * flag are checked as well as the answer.
         */
        @Test
        @DisplayName("118 is a refusal, not a no-op write")
        void theCeilingItselfRefuses() throws Exception {
            stat(Stats.STAT_STR, 18 + 100);

            assertFalse(player.playerStatInc(Stats.STAT_STR));
            assertEquals(18 + 100, cur(Stats.STAT_STR));
            assertFalse(player.getPlayerUpkeep().updateHas(PlayerUpdateEnum.PU_BONUS));
        }

        /**
         * The value the C's own unit test probes ({@code playerstat.c:37-39}).
         */
        @Test
        @DisplayName("above the ceiling refuses too")
        void aboveTheCeilingRefuses() throws Exception {
            stat(Stats.STAT_STR, 18 + 101);

            assertFalse(player.playerStatInc(Stats.STAT_STR));
            assertEquals(18 + 101, cur(Stats.STAT_STR));
            assertFalse(player.getPlayerUpkeep().updateHas(PlayerUpdateEnum.PU_BONUS));
        }

        /**
         * One below the ceiling is still a gain, and it is the last one — 117 is in the top band,
         * so it goes to 118 exactly.
         */
        @Test
        @DisplayName("117 is the last gain, and it lands on the ceiling")
        void oneBelowTheCeilingStillGains() throws Exception {
            stat(Stats.STAT_STR, 18 + 99);

            assertTrue(player.playerStatInc(Stats.STAT_STR));
            assertEquals(18 + 100, cur(Stats.STAT_STR));
        }
    }

    /**
     * The plain part of the scale, below 18, where the gain is exactly one.
     */
    @Nested
    @DisplayName("below 18")
    class BelowEighteen {

        /**
         * The C's own walk ({@code playerstat.c:40-47}): 15 to 16 to 17 to 18, one point each time.
         * Written as a walk rather than three separate cases because the bug this catches — a
         * post-increment that stores the old value — passes any single-call test that only checks
         * the answer.
         */
        @Test
        @DisplayName("the C test's walk: 15 to 16 to 17 to 18")
        void oneAtATime() throws Exception {
            stat(Stats.STAT_STR, 15);

            assertTrue(player.playerStatInc(Stats.STAT_STR));
            assertEquals(16, cur(Stats.STAT_STR));
            player.playerStatInc(Stats.STAT_STR);
            assertEquals(17, cur(Stats.STAT_STR));
            player.playerStatInc(Stats.STAT_STR);
            assertEquals(18, cur(Stats.STAT_STR));
        }

        /**
         * The bottom of the scale behaves like the rest of the band.
         */
        @Test
        @DisplayName("the bottom of the scale gains one like any other")
        void theBottomOfTheScale() throws Exception {
            stat(Stats.STAT_STR, 3);

            assertTrue(player.playerStatInc(Stats.STAT_STR));
            assertEquals(4, cur(Stats.STAT_STR));
        }

        /**
         * 17 is the last value in the linear band and 18 the first in the rolled one, so the two
         * are asserted against each other: 17 moves by exactly one, 18 by more than one. The C's
         * own test makes the same pairing ({@code playerstat.c:46-48}).
         */
        @Test
        @DisplayName("17 is linear, 18 is rolled")
        void theBoundaryAtEighteen() throws Exception {
            stat(Stats.STAT_STR, 17);
            player.playerStatInc(Stats.STAT_STR);
            assertEquals(18, cur(Stats.STAT_STR));

            stat(Stats.STAT_INT, 18);
            player.playerStatInc(Stats.STAT_INT);
            assertTrue(cur(Stats.STAT_INT) > 18 + 1,
                    "18 is in the rolled band, so it must gain more than the linear one point");
        }
    }

    /**
     * The rolled band, from 18 up to but not including {@code 18 + 90}.
     */
    @Nested
    @DisplayName("the rolled band")
    class RolledBand {

        /**
         * The roll is {@code randint1(gain) + gain / 2}, so the result is bounded below by
         * {@code v + 1 + gain / 2} and above by {@code v + gain + gain / 2}. Both ends are computed
         * from the C's formula rather than from the port.
         */
        @Test
        @DisplayName("every value in the band lands inside the C's bounds")
        void withinTheCsBounds() throws Exception {
            for (int value = 18; value < 18 + 90; value++) {
                for (int attempt = 0; attempt < 20; attempt++) {
                    stat(Stats.STAT_STR, value);
                    assertTrue(player.playerStatInc(Stats.STAT_STR));

                    int gain = cGain(value);
                    int result = cur(Stats.STAT_STR);
                    assertTrue(result >= value + 1 + gain / 2 && result <= value + gain + gain / 2,
                            "from " + value + " expected " + (value + 1 + gain / 2) + ".."
                                    + (value + gain + gain / 2) + " but got " + result);
                }
            }
        }

        /**
         * The rolled result always moves, which is what separates this band from the ceiling's
         * refusal: {@code randint1} is one-based, so the smallest possible roll is still a point.
         */
        @Test
        @DisplayName("a roll always moves the stat")
        void aRollAlwaysMoves() throws Exception {
            for (int value = 18; value < 18 + 90; value++) {
                stat(Stats.STAT_STR, value);
                player.playerStatInc(Stats.STAT_STR);
                assertTrue(cur(Stats.STAT_STR) > value, "no movement from " + value);
            }
        }

        /**
         * The gain shrinks as the stat climbs, which is the band's whole design. Compared at the
         * two ends rather than everywhere, since the formula's integer division makes it flat in
         * places.
         */
        @Test
        @DisplayName("the gain shrinks as the stat climbs")
        void theGainShrinks() {
            assertEquals(26, cGain(18));
            assertEquals(4, cGain(18 + 89));
            assertTrue(cGain(18) > cGain(60) && cGain(60) > cGain(18 + 89));
        }

        /**
         * With 4.2.6's constants the roll cannot reach the {@code 18 + 99} clamp, let alone the
         * ceiling: the largest result anywhere in the band is 113, from a current value of 107. The
         * sweep pins that, so the clamp is documented as defensive rather than assumed to be.
         */
        @Test
        @DisplayName("the band never reaches the clamp, let alone the ceiling")
        void theBandNeverReachesTheClampLetAloneTheCeiling() throws Exception {
            for (int value = 18; value < 18 + 90; value++) {
                for (int attempt = 0; attempt < 20; attempt++) {
                    stat(Stats.STAT_STR, value);
                    player.playerStatInc(Stats.STAT_STR);
                    assertTrue(cur(Stats.STAT_STR) <= 113,
                            "from " + value + " got " + cur(Stats.STAT_STR));
                }
            }
        }
    }

    /**
     * The top band, from {@code 18 + 90} to the ceiling, where the last points are given rather
     * than rolled.
     */
    @Nested
    @DisplayName("the top band")
    class TopBand {

        /**
         * 107 is the last rolled value and 108 the first given one, so the two are asserted against
         * each other. 108 is deterministic — straight to 118 — which is the clearest evidence the
         * boundary is the right way round.
         */
        @Test
        @DisplayName("107 is rolled, 108 goes straight to the ceiling")
        void theBoundaryAtNinety() throws Exception {
            stat(Stats.STAT_STR, 18 + 89);
            player.playerStatInc(Stats.STAT_STR);
            assertTrue(cur(Stats.STAT_STR) >= 110 && cur(Stats.STAT_STR) <= 113,
                    "107 is rolled, so it must land in 110..113 but got " + cur(Stats.STAT_STR));

            stat(Stats.STAT_INT, 18 + 90);
            assertTrue(player.playerStatInc(Stats.STAT_INT));
            assertEquals(18 + 100, cur(Stats.STAT_INT));
        }

        /**
         * Every value in the band goes to the ceiling in one step, with no roll and no partial
         * climb.
         */
        @Test
        @DisplayName("the whole band jumps to the ceiling in one step")
        void oneStepToTheCeiling() throws Exception {
            for (int value = 18 + 90; value < 18 + 100; value++) {
                stat(Stats.STAT_STR, value);
                assertTrue(player.playerStatInc(Stats.STAT_STR));
                assertEquals(18 + 100, cur(Stats.STAT_STR), "from " + value);
            }
        }
    }

    /**
     * The two side effects every successful gain has.
     */
    @Nested
    @DisplayName("side effects")
    class SideEffects {

        /**
         * The maximum is dragged up behind the current, which is what makes a gained point survive
         * later drain.
         */
        @Test
        @DisplayName("the maximum follows the current up")
        void theMaximumFollows() throws Exception {
            stat(Stats.STAT_STR, 15);

            player.playerStatInc(Stats.STAT_STR);

            assertEquals(16, cur(Stats.STAT_STR));
            assertEquals(16, max(Stats.STAT_STR));
        }

        /**
         * C's test is {@code >}, so a drained character regaining ground below their old maximum
         * leaves that maximum alone rather than rewriting it downward.
         */
        @Test
        @DisplayName("a drained character's maximum is not pulled down")
        void aDrainedMaximumIsLeftAlone() throws Exception {
            read("statCur").put(Stats.STAT_STR, 15);
            read("statMax").put(Stats.STAT_STR, 40);

            player.playerStatInc(Stats.STAT_STR);

            assertEquals(16, cur(Stats.STAT_STR));
            assertEquals(40, max(Stats.STAT_STR));
        }

        /**
         * {@code PU_BONUS} is what makes everything derived from the stat recompute, so a gain that
         * did not raise it would be invisible until something else did.
         */
        @Test
        @DisplayName("a gain schedules the bonus recalculation")
        void aGainSchedulesTheRecalculation() throws Exception {
            stat(Stats.STAT_STR, 15);

            player.playerStatInc(Stats.STAT_STR);

            assertTrue(player.getPlayerUpkeep().updateHas(PlayerUpdateEnum.PU_BONUS));
        }

        /**
         * C subscripts one stat; nothing else in either array is touched.
         */
        @Test
        @DisplayName("only the named stat moves")
        void onlyTheNamedStatMoves() throws Exception {
            player.playerStatInc(Stats.STAT_STR);

            assertEquals(11, cur(Stats.STAT_STR));
            for (Stats stat : Stats.values()) {
                if (stat == Stats.STAT_NONE || stat == Stats.STAT_MAX || stat == Stats.STAT_STR) continue;
                assertEquals(10, cur(stat), stat + " moved");
                assertEquals(10, max(stat), stat + " moved");
            }
        }
    }
}
