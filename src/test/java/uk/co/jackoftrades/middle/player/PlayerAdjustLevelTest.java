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
import uk.co.jackoftrades.middle.game.globals.loaders.PlayerDataLoader;
import uk.co.jackoftrades.middle.game.globals.registry.PlayerRegistry;
import uk.co.jackoftrades.testsupport.CalcBonusesFixture;
import uk.co.jackoftrades.testsupport.SeededPlayerRegistry;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link Player}'s {@code adjustLevel}, the level arithmetic every experience change ends in.
 *
 * <p>The expected figures are taken from C's {@code player_exp[]} table ({@code player.c:48}) and
 * from {@code adjust_level} ({@code player.c:208}) rather than from the Java implementation, so the
 * thresholds are the game's own: 10 points to reach level 2, then 25, 45, 70, 100, 140, 200, 280,
 * 380 and 500 for level 11.
 *
 * <p>Three properties are worth pinning above the rest. The table is indexed from zero, so entry
 * {@code 0} is the cost of level 2 and an off-by-one here would hand a fresh character a free
 * level. The {@code expFact} scaling divides by 100 <em>after</em> multiplying, so the scaled
 * threshold truncates. And {@code maxLevel} is a high-water mark driven by {@code maxExp} alone,
 * which is why a drained character's working level falls while their best level does not.
 *
 * <p>Class PlayerAdjustLevelTest coded on 260831, commented in full on 260831.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class PlayerAdjustLevelTest {

    /**
     * The player under test.
     */
    private Player player;

    /**
     * A new player, and the real experience table, for each test. The table is global static state
     * that a reader may or may not have filled, so it is loaded here rather than assumed.
     *
     * <p>The character comes from {@link CalcBonusesFixture}, which contributes nothing of its own
     * but does supply the race, class and stat maps that {@code adjustLevel}'s two
     * {@code handleStuff} calls walk on their way through {@code calcBonuses}. A bare
     * {@code new Player()} has no class, and the recalculation throws on it.
     *
     * @throws Exception if a fixture field cannot be reached
     */
    @BeforeEach
    void newPlayer() throws Exception {
        PlayerDataLoader.initialiseExpLevel();
        player = CalcBonusesFixture.plainCharacter().player();

        // calcHitpoints reads playerHP[level - 1] for whatever level the arithmetic lands on, so
        // the per-level roll table has to exist for all fifty levels.
        set("playerHP", new int[PlayerRegistry.PY_MAX_LEVEL]);
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
     * Reads one of the player's private int fields.
     *
     * @param name the field's name
     * @return its value
     * @throws Exception if the field cannot be reached
     */
    private int intField(String name) throws Exception {
        Field field = Player.class.getDeclaredField(name);
        field.setAccessible(true);
        return field.getInt(player);
    }

    /**
     * Reads one of the player's private long fields.
     *
     * @param name the field's name
     * @return its value
     * @throws Exception if the field cannot be reached
     */
    private long longField(String name) throws Exception {
        Field field = Player.class.getDeclaredField(name);
        field.setAccessible(true);
        return field.getLong(player);
    }

    /**
     * Puts the player in a known state and runs the level arithmetic on it.
     *
     * @param exp      the current experience
     * @param maxExp   the maximum experience
     * @param level    the starting character level
     * @param maxLevel the starting highest level
     * @param expFact  the character's experience percentage
     * @param verbose  whether a level gain announces itself
     * @throws Exception if a field or the method cannot be reached
     */
    private void adjust(long exp, long maxExp, int level, int maxLevel, int expFact, boolean verbose)
            throws Exception {
        set("exp", exp);
        set("maxExp", maxExp);
        set("level", level);
        set("maxLevel", maxLevel);
        set("expFact", expFact);

        Method method = Player.class.getDeclaredMethod("adjustLevel", boolean.class);
        method.setAccessible(true);
        method.invoke(player, verbose);
    }

    /**
     * The player's history entries.
     *
     * @return the ledger
     * @throws Exception if the field cannot be reached
     */
    private List<HistoryInfo> history() throws Exception {
        Field field = Player.class.getDeclaredField("playerHistory");
        field.setAccessible(true);
        return ((PlayerHistory) field.get(player)).entries;
    }

    /**
     * The thresholds themselves, which are what every other test rests on.
     */
    @Nested
    @DisplayName("the experience table")
    class Table {

        /**
         * The table is keyed as C indexes {@code player_exp[]}: from zero, with entry {@code 0}
         * holding the cost of reaching level 2. Read a level number into it by mistake and every
         * threshold is one step too cheap.
         */
        @Test
        @DisplayName("is keyed from zero, as C's player_exp[] is indexed")
        void keyedFromZero() {
            assertEquals(10L, PlayerRegistry.playerExperience.get(0), "player_exp[0], for level 2");
            assertEquals(25L, PlayerRegistry.playerExperience.get(1), "player_exp[1], for level 3");
            assertEquals(500L, PlayerRegistry.playerExperience.get(9), "player_exp[9], for level 11");
            assertEquals(5000000L, PlayerRegistry.playerExperience.get(49),
                    "player_exp[49], the last entry");
            assertEquals(PlayerRegistry.PY_MAX_LEVEL, PlayerRegistry.playerExperience.size());
        }
    }

    /**
     * The upward walk, which is the loop that announces and restores.
     */
    @Nested
    @DisplayName("gaining levels")
    class Gaining {

        /**
         * A character with no experience stays at level 1. This is the case the table's keying
         * decides: C tests {@code exp >= player_exp[0]}, which is 10, so zero experience is not
         * enough for level 2.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("no experience leaves a fresh character at level 1")
        void freshCharacterStaysAtLevelOne() throws Exception {
            adjust(0, 0, 1, 1, 100, true);

            assertEquals(1, intField("level"));
            assertEquals(1, intField("maxLevel"));
        }

        /**
         * One point short of the threshold is still level 1; the threshold itself is inclusive, so
         * exactly 10 makes level 2.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("the level 2 threshold is inclusive at 10")
        void thresholdIsInclusive() throws Exception {
            adjust(9, 9, 1, 1, 100, true);
            assertEquals(1, intField("level"), "9 is short of player_exp[0]");

            adjust(10, 10, 1, 1, 100, true);
            assertEquals(2, intField("level"), "10 reaches player_exp[0]");
        }

        /**
         * The loop keeps going while the experience covers the next threshold, so a single call can
         * cross several levels at once. 500 points is exactly {@code player_exp[9]}, the cost of
         * level 11, and {@code player_exp[10]} is 650.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("one call can cross several levels at once")
        void climbsSeveralLevels() throws Exception {
            adjust(500, 500, 1, 1, 100, true);

            assertEquals(11, intField("level"));
            assertEquals(11, intField("maxLevel"));
        }

        /**
         * {@code PY_MAX_LEVEL} caps the climb: the cap on experience is far above the cost of the
         * last level, so a character with the maximum experience stops at 50 rather than running
         * off the end of the table.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("the climb stops at PY_MAX_LEVEL")
        void stopsAtMaxLevel() throws Exception {
            adjust(PlayerRegistry.PY_MAX_EXP, PlayerRegistry.PY_MAX_EXP, 1, 1, 100, true);

            assertEquals(PlayerRegistry.PY_MAX_LEVEL, intField("level"));
            assertEquals(PlayerRegistry.PY_MAX_LEVEL, intField("maxLevel"));
        }
    }

    /**
     * The {@code expFact} scaling, which is where C's integer arithmetic shows.
     */
    @Nested
    @DisplayName("expFact scaling")
    class Scaling {

        /**
         * A costly race or class raises every threshold in proportion: at 200 percent, level 2
         * costs 20 rather than 10.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("a doubled expFact doubles the threshold")
        void doubledFactorDoublesTheThreshold() throws Exception {
            adjust(19, 19, 1, 1, 200, true);
            assertEquals(1, intField("level"), "19 is short of 10 * 200 / 100");

            adjust(20, 20, 1, 1, 200, true);
            assertEquals(2, intField("level"));
        }

        /**
         * C multiplies before dividing by 100 and keeps the integer result, so the scaled threshold
         * truncates rather than rounds. At 133 percent level 2 costs {@code 10 * 133 / 100} = 13,
         * not 14, and level 3 costs {@code 25 * 133 / 100} = 33, not 34.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("the scaled threshold truncates, not rounds")
        void scaledThresholdTruncates() throws Exception {
            adjust(13, 13, 1, 1, 133, true);
            assertEquals(2, intField("level"), "13.3 truncates to 13, which 13 reaches");

            adjust(33, 33, 1, 1, 133, true);
            assertEquals(3, intField("level"), "33.25 truncates to 33, which 33 reaches");
        }
    }

    /**
     * The downward walk, and the high-water mark that survives it.
     */
    @Nested
    @DisplayName("losing levels")
    class Losing {

        /**
         * A character stripped of all experience falls back to level 1, one level per turn of the
         * loop, and the loop's {@code level > 1} guard is what stops it there.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("a total drain falls back to level 1")
        void totalDrainFallsToLevelOne() throws Exception {
            adjust(0, 500, 5, 5, 100, true);

            assertEquals(1, intField("level"));
        }

        /**
         * The working level falls but the highest level reached does not: the third loop runs on
         * {@code maxExp}, which a temporary drain leaves alone.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("maxLevel survives a drain of the working experience")
        void maxLevelSurvivesADrain() throws Exception {
            adjust(0, 500, 11, 11, 100, true);

            assertEquals(1, intField("level"));
            assertEquals(11, intField("maxLevel"), "maxExp still covers level 11");
        }

        /**
         * A drain that stops part-way lands on the level the remaining experience pays for: 70
         * points is exactly {@code player_exp[3]}, the cost of level 5, and level 6 costs 100.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("a partial drain lands on the level the remainder pays for")
        void partialDrainLandsOnTheRightLevel() throws Exception {
            adjust(70, 500, 11, 11, 100, true);

            assertEquals(5, intField("level"));
        }
    }

    /**
     * The third loop, which tracks the best level reached from {@code maxExp} alone.
     */
    @Nested
    @DisplayName("maxLevel")
    class HighWaterMark {

        /**
         * {@code maxLevel} climbs on {@code maxExp} even while the working level does not move, so
         * a character who earned the experience keeps the credit for it after a drain.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("climbs on maxExp while the working level stays put")
        void climbsIndependentlyOfLevel() throws Exception {
            adjust(0, 100, 1, 1, 100, true);

            assertEquals(1, intField("level"), "no current experience, so no current level");
            assertEquals(6, intField("maxLevel"), "100 covers player_exp[4], the cost of level 6");
        }
    }

    /**
     * The clamps that run before any level arithmetic.
     */
    @Nested
    @DisplayName("clamping the totals")
    class Clamps {

        /**
         * Negative totals are floored at zero, so nothing downstream has to cope with a negative
         * experience figure.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("negative totals are floored at zero")
        void negativesAreFloored() throws Exception {
            adjust(-500, -500, 3, 3, 100, true);

            assertEquals(0L, longField("exp"));
            assertEquals(0L, longField("maxExp"));
            assertEquals(1, intField("level"));
        }

        /**
         * Both totals are capped at {@code PY_MAX_EXP}.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("both totals are capped at PY_MAX_EXP")
        void totalsAreCapped() throws Exception {
            adjust(PlayerRegistry.PY_MAX_EXP + 5000, PlayerRegistry.PY_MAX_EXP + 9000,
                    1, 1, 100, true);

            assertEquals(PlayerRegistry.PY_MAX_EXP, longField("exp"));
            assertEquals(PlayerRegistry.PY_MAX_EXP, longField("maxExp"));
        }

        /**
         * A current total above the maximum drags the maximum up to meet it, which is how a gain
         * that outruns the recorded maximum is absorbed.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("a current total above the maximum raises the maximum")
        void currentAboveMaximumRaisesIt() throws Exception {
            adjust(1000, 400, 1, 1, 100, true);

            assertEquals(1000L, longField("maxExp"));
        }
    }

    /**
     * What {@code verbose} governs, and what it does not.
     */
    @Nested
    @DisplayName("verbosity")
    class Verbosity {

        /**
         * A verbose gain writes one history entry per level crossed, worded as C words it.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("a verbose gain logs a history entry per level")
        void verboseGainLogsHistory() throws Exception {
            adjust(45, 45, 1, 1, 100, true);

            List<HistoryInfo> entries = history();
            assertEquals(3, entries.size(), "levels 2, 3 and 4 were reached");
            assertEquals("Reached level 2", entries.get(0).historyText);
            assertEquals("Reached level 4", entries.get(2).historyText);
            assertTrue(entries.get(0).type.has(
                            uk.co.jackoftrades.middle.player.enums.PlayerHistoryType.HIST_GAIN_LEVEL),
                    "the entry carries HIST_GAIN_LEVEL");
        }

        /**
         * A silent gain moves the level without writing anything down — the savefile loader and
         * character generation both climb quietly.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("a silent gain logs nothing")
        void silentGainLogsNothing() throws Exception {
            adjust(45, 45, 1, 1, 100, false);

            assertEquals(4, intField("level"), "the level still moves");
            assertTrue(history().isEmpty(), "but nothing is written down");
        }

        /**
         * The downward walk is silent in C whatever {@code verbose} says, so a drain leaves no
         * history behind.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("losing levels logs nothing even when verbose")
        void losingLevelsLogsNothing() throws Exception {
            adjust(0, 500, 11, 11, 100, true);

            assertTrue(history().isEmpty());
        }
    }

    /**
     * The public route in, which is the only way the rest of the game reaches this arithmetic.
     */
    @Nested
    @DisplayName("through playerExpLose")
    class ThroughExpLose {

        /**
         * A permanent drain of everything takes both totals to zero, drops the working level to 1
         * and leaves the highest level reached where it was.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("a permanent total drain drops the level but keeps maxLevel")
        void permanentDrainDropsTheLevel() throws Exception {
            set("exp", 500L);
            set("maxExp", 500L);
            set("level", 11);
            set("maxLevel", 11);
            set("expFact", 100);

            player.playerExpLose(500, true);

            assertEquals(0L, longField("exp"));
            assertEquals(0L, longField("maxExp"));
            assertEquals(1, intField("level"));
            assertEquals(11, intField("maxLevel"), "the best level reached is never taken back");
        }

        /**
         * A temporary drain leaves {@code maxExp} intact, so the level falls while the highest
         * level reached is still supported by the maximum.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("a temporary drain lowers the level alone")
        void temporaryDrainLowersTheLevelAlone() throws Exception {
            set("exp", 500L);
            set("maxExp", 500L);
            set("level", 11);
            set("maxLevel", 11);
            set("expFact", 100);

            player.playerExpLose(430, false);

            assertEquals(70L, longField("exp"));
            assertEquals(500L, longField("maxExp"));
            assertEquals(5, intField("level"), "70 is exactly player_exp[3], the cost of level 5");
            assertEquals(11, intField("maxLevel"));
        }
    }
}
