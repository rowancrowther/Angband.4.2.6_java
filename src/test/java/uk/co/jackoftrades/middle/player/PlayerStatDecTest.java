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
import uk.co.jackoftrades.middle.enums.Stats;
import uk.co.jackoftrades.middle.player.enums.PlayerRedraw;
import uk.co.jackoftrades.middle.player.enums.PlayerUpdateEnum;
import uk.co.jackoftrades.testsupport.SeededPlayerRegistry;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@code Player.statDec}, the port of C's {@code player_stat_dec} ({@code src/player.c:171}).
 *
 * <p>Every expectation is read off that C, and {@link TheCsOwnWalk} reproduces the C's own unit test
 * ({@code src/tests/player/playerstat.c:53}) step for step, including the values it happens to pick.
 *
 * <p><b>Three bands, and they are not the gain's bands.</b> A drain takes ten off the percentile
 * tail, drops to 18 from anywhere in the tail's bottom ten, or takes one off the plain part. So the
 * boundaries are 28 and 18, not the 18 and 108 that {@code player_stat_inc} uses, and each is
 * checked from both sides — 28 against 29, and 18 against 19. Nothing here is random, so every
 * expectation is an exact value.
 *
 * <p><b>The permanent flag replaces the answer.</b> C computes {@code res} from the current value
 * and then, when {@code permanent}, overwrites it with the comparison on the maximum. The
 * consequence is not obvious and is pinned in {@link ThePermanentFlagReplacesTheAnswer}: a permanent
 * drain whose maximum cannot move reports no change and writes nothing back, discarding a current
 * value that would otherwise have fallen. That is the C's behaviour, so it is the port's, and a test
 * that let it pass either way would be no test at all.
 *
 * <p><b>Writes are all-or-nothing.</b> Nothing is stored unless the answer is {@code true}, and the
 * two flags go up in the same block, so an unchanged stat must leave {@code PU_BONUS} and
 * {@code PR_STATS} clear as well as leaving the numbers alone. All three are asserted together
 * wherever the answer is {@code false}.
 *
 * <p>Class PlayerStatDecTest coded on 260831, commented in full on 260831.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class PlayerStatDecTest {

    /**
     * The player under test, fresh for each test since all of this is mutable.
     */
    private Player player;

    /**
     * A new player with every stat present, since the port reads maps where C reads a zeroed array
     * and an absent key would throw rather than read as nothing.
     */
    @BeforeEach
    void newPlayer() throws Exception {
        player = new Player();
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
     * Sets one stat's current and maximum independently, since the two bands are applied separately
     * and a drained character has them apart.
     *
     * @param stat    the stat
     * @param current the current value
     * @param maximum the maximum value
     * @throws Exception if a field cannot be reached
     */
    private void stat(Stats stat, int current, int maximum) throws Exception {
        read("statCur").put(stat, current);
        read("statMax").put(stat, maximum);
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
     * @return whether the change was reported to the rest of the game
     */
    private boolean flagsRaised() {
        return player.getPlayerUpkeep().updateHas(PlayerUpdateEnum.PU_BONUS)
                && player.getPlayerUpkeep().getRedrawFlags().has(PlayerRedraw.PR_STATS);
    }

    /**
     * @return whether neither flag was raised
     */
    private boolean noFlagsRaised() {
        return !player.getPlayerUpkeep().updateHas(PlayerUpdateEnum.PU_BONUS)
                && !player.getPlayerUpkeep().getRedrawFlags().has(PlayerRedraw.PR_STATS);
    }

    /**
     * The C's own unit test, step for step ({@code playerstat.c:53-78}). Kept as one walk rather
     * than split into cases, because the sequence is what it asserts: each step starts from where
     * the last one left the character.
     */
    @Nested
    @DisplayName("the C's own walk")
    class TheCsOwnWalk {

        /**
         * A character already at the floor of 3, drained permanently, reports nothing.
         */
        @Test
        @DisplayName("the floor reports no change")
        void theFloorReportsNoChange() throws Exception {
            stat(Stats.STAT_STR, 3, 3);

            assertFalse(player.statDec(Stats.STAT_STR, true));
            assertEquals(3, cur(Stats.STAT_STR));
            assertEquals(3, max(Stats.STAT_STR));
            assertTrue(noFlagsRaised());
        }

        /**
         * From 15: a temporary drain takes the current to 14 and leaves the maximum, then a
         * permanent one takes both, arriving at 13 and 14 rather than at a matched pair — the two
         * fall independently from where each of them was.
         */
        @Test
        @DisplayName("15 drains temporarily then permanently to 13 over 14")
        void temporaryThenPermanent() throws Exception {
            stat(Stats.STAT_STR, 15, 15);

            player.statDec(Stats.STAT_STR, false);
            assertEquals(14, cur(Stats.STAT_STR));
            assertEquals(15, max(Stats.STAT_STR));

            player.statDec(Stats.STAT_STR, true);
            assertEquals(13, cur(Stats.STAT_STR));
            assertEquals(14, max(Stats.STAT_STR));
        }

        /**
         * From 18/13 a temporary drain takes ten off the tail and leaves the maximum where it was,
         * then a permanent drain from 18/03 drops both out of the tail to a bare 18.
         */
        @Test
        @DisplayName("the tail loses ten, then drops out of the tail entirely")
        void downThroughTheTail() throws Exception {
            stat(Stats.STAT_STR, 18 + 13, 18 + 13);

            player.statDec(Stats.STAT_STR, false);
            assertEquals(18 + 3, cur(Stats.STAT_STR));
            assertEquals(18 + 13, max(Stats.STAT_STR));

            read("statMax").put(Stats.STAT_STR, 18 + 3);
            player.statDec(Stats.STAT_STR, true);
            assertEquals(18, cur(Stats.STAT_STR));
            assertEquals(18, max(Stats.STAT_STR));
        }
    }

    /**
     * The three bands and the two boundaries between them.
     */
    @Nested
    @DisplayName("the bands")
    class TheBands {

        /**
         * 29 is the first value that loses ten and 28 the last that drops to 18, so the two are
         * asserted against each other. Both start from the same distance above 18, which is what
         * makes a boundary written the wrong way round obvious.
         */
        @Test
        @DisplayName("29 loses ten, 28 drops to 18")
        void theBoundaryAtTwentyEight() throws Exception {
            stat(Stats.STAT_STR, 18 + 11, 18 + 11);
            player.statDec(Stats.STAT_STR, false);
            assertEquals(19, cur(Stats.STAT_STR));

            stat(Stats.STAT_INT, 18 + 10, 18 + 10);
            player.statDec(Stats.STAT_INT, false);
            assertEquals(18, cur(Stats.STAT_INT));
        }

        /**
         * 19 is the last value in the drop-to-18 band and 18 the first in the linear one, so 19
         * falls by one to 18 and 18 falls by one to 17 — the same step for different reasons.
         */
        @Test
        @DisplayName("19 drops to 18, and 18 is linear from there")
        void theBoundaryAtEighteen() throws Exception {
            stat(Stats.STAT_STR, 19, 19);
            player.statDec(Stats.STAT_STR, false);
            assertEquals(18, cur(Stats.STAT_STR));

            player.statDec(Stats.STAT_STR, false);
            assertEquals(17, cur(Stats.STAT_STR));
        }

        /**
         * The top of the scale loses ten like any other tail value; there is no special case at the
         * ceiling the way there is on the way up.
         */
        @Test
        @DisplayName("the ceiling loses ten like any other tail value")
        void theCeilingIsNotSpecial() throws Exception {
            stat(Stats.STAT_STR, 18 + 100, 18 + 100);

            assertTrue(player.statDec(Stats.STAT_STR, false));
            assertEquals(18 + 90, cur(Stats.STAT_STR));
        }

        /**
         * 4 is the last value that can fall and 3 the floor, so the two are asserted together: one
         * moves and reports true, the other does neither.
         */
        @Test
        @DisplayName("4 falls to the floor, and the floor holds")
        void theFloorHolds() throws Exception {
            stat(Stats.STAT_STR, 4, 4);

            assertTrue(player.statDec(Stats.STAT_STR, false));
            assertEquals(3, cur(Stats.STAT_STR));

            assertFalse(player.statDec(Stats.STAT_STR, false));
            assertEquals(3, cur(Stats.STAT_STR));
        }
    }

    /**
     * The quirk in how the answer is computed, and what follows from it.
     */
    @Nested
    @DisplayName("the permanent flag replaces the answer")
    class ThePermanentFlagReplacesTheAnswer {

        /**
         * A permanent drain answers about the maximum, not the current value. Set up with a maximum
         * at the floor and a current value above it — a state the game does not otherwise produce,
         * used here because it is the only way to make the two comparisons disagree — the method
         * reports no change and, because nothing is written when the answer is false, leaves the
         * current value where it was even though the band arithmetic had already lowered it.
         */
        @Test
        @DisplayName("a maximum that cannot move discards the current value's fall")
        void anImmovableMaximumDiscardsTheFall() throws Exception {
            stat(Stats.STAT_STR, 15, 3);

            assertFalse(player.statDec(Stats.STAT_STR, true));
            assertEquals(15, cur(Stats.STAT_STR), "the write is skipped entirely when res is false");
            assertEquals(3, max(Stats.STAT_STR));
            assertTrue(noFlagsRaised());
        }

        /**
         * The same character drained temporarily does move, which is what shows the previous case is
         * about the flag and not about the character.
         */
        @Test
        @DisplayName("the same character drained temporarily does fall")
        void temporarilyTheSameCharacterFalls() throws Exception {
            stat(Stats.STAT_STR, 15, 3);

            assertTrue(player.statDec(Stats.STAT_STR, false));
            assertEquals(14, cur(Stats.STAT_STR));
            assertEquals(3, max(Stats.STAT_STR));
        }

        /**
         * The converse: a permanent drain whose maximum moves reports true even where the current
         * value was already at the floor and could not.
         */
        @Test
        @DisplayName("a moving maximum reports true even when the current value is at the floor")
        void aMovingMaximumReportsTrue() throws Exception {
            stat(Stats.STAT_STR, 3, 15);

            assertTrue(player.statDec(Stats.STAT_STR, true));
            assertEquals(3, cur(Stats.STAT_STR));
            assertEquals(14, max(Stats.STAT_STR));
        }

        /**
         * The two values are in different bands and fall by different amounts, which is what makes
         * the independence worth asserting rather than assuming.
         */
        @Test
        @DisplayName("current and maximum fall by their own bands")
        void eachFallsByItsOwnBand() throws Exception {
            stat(Stats.STAT_STR, 17, 18 + 30);

            assertTrue(player.statDec(Stats.STAT_STR, true));
            assertEquals(16, cur(Stats.STAT_STR));
            assertEquals(18 + 20, max(Stats.STAT_STR));
        }
    }

    /**
     * What a change tells the rest of the game, and what an unchanged stat does not.
     */
    @Nested
    @DisplayName("side effects")
    class SideEffects {

        /**
         * Both flags go up in the same block, so a drain raises the recalculation and the repaint
         * together.
         */
        @Test
        @DisplayName("a drain schedules the recalculation and the repaint")
        void aDrainSchedulesBoth() throws Exception {
            stat(Stats.STAT_STR, 15, 15);

            assertTrue(player.statDec(Stats.STAT_STR, false));

            assertTrue(flagsRaised());
        }

        /**
         * A temporary drain leaves the maximum untouched, which is what lets the ground be regained
         * later — and is the difference the flag exists to express.
         */
        @Test
        @DisplayName("a temporary drain leaves the maximum alone")
        void temporaryLeavesTheMaximum() throws Exception {
            stat(Stats.STAT_STR, 18 + 40, 18 + 40);

            player.statDec(Stats.STAT_STR, false);

            assertEquals(18 + 30, cur(Stats.STAT_STR));
            assertEquals(18 + 40, max(Stats.STAT_STR));
        }

        /**
         * C subscripts one stat; nothing else in either array is touched.
         */
        @Test
        @DisplayName("only the named stat moves")
        void onlyTheNamedStatMoves() throws Exception {
            player.statDec(Stats.STAT_STR, true);

            assertEquals(9, cur(Stats.STAT_STR));
            assertEquals(9, max(Stats.STAT_STR));
            for (Stats stat : Stats.values()) {
                if (stat == Stats.STAT_NONE || stat == Stats.STAT_MAX || stat == Stats.STAT_STR) continue;
                assertEquals(10, cur(stat), stat + " moved");
                assertEquals(10, max(stat), stat + " moved");
            }
        }
    }
}
