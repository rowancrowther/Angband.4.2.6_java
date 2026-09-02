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
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.enums.Stats;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.player.enums.PlayerFlag;
import uk.co.jackoftradesltd.middle.player.enums.PlayerSkill;
import uk.co.jackoftradesltd.testsupport.SeededPlayerRegistry;

import java.lang.reflect.Field;
import java.time.Duration;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link PlayerBirth#getStats(Player, Map)}, the port of C's {@code get_stats}
 * ({@code player-birth.c:231-275}), together with the five accessors it writes the character
 * through — {@link Player#setStatMax(Stats, int)}, {@link Player#setCurrStatValue(Stats, int)},
 * {@link Player#setCurrStatMap(Stats, Stats)}, {@link Player#setStatBirth(Stats, int)} and
 * {@link Player#getStatMax(Stats)}.
 *
 * <p>The C:
 *
 * <pre>{@code
 * int dice[3 * STAT_MAX];
 * while (true) {
 *     for (j = i = 0; i < 3 * STAT_MAX; i++) {
 *         dice[i] = randint1(3 + i % 3);
 *         j += dice[i];
 *     }
 *     if ((j > 7 * STAT_MAX) && (j < 9 * STAT_MAX)) break;
 * }
 * for (i = 0; i < STAT_MAX; i++) {
 *     j = 5 + dice[3 * i] + dice[3 * i + 1] + dice[3 * i + 2];
 *     player->stat_max[i] = j;
 *     bonus = player->race->r_adj[i] + player->class->c_adj[i];
 *     player->stat_cur[i] = player->stat_max[i];
 *     player->stat_map[i] = i;
 *     stat_use[i] = modify_stat_value(player->stat_max[i], bonus);
 *     player->stat_birth[i] = player->stat_max[i];
 * }
 * }</pre>
 *
 * <p><b>Testing something that rolls dice.</b> The shared {@code Random} behind
 * {@code RandomValueUtils} cannot be swapped out by a test, so the expected values here are the
 * properties the C guarantees rather than single numbers. Three of those properties are strong
 * enough to pin the arithmetic on their own.
 *
 * <p><b>The stride.</b> Each stat takes three consecutive dice, {@code dice[3i]},
 * {@code dice[3i + 1]}, {@code dice[3i + 2]}, so between them the five stats consume all fifteen
 * dice exactly once. That makes the five rolled values sum to the accepted dice total plus the five
 * bases: {@code sum(stat_max) - 25} is precisely the {@code j} the window admitted, and so must lie
 * in 36 to 44 on every single birth. A port reading the dice at any other stride re-uses some and
 * skips others, and the identity breaks — which is what makes this a test of the subscripts and not
 * merely of the range.
 *
 * <p><b>The window.</b> {@code 7 * STAT_MAX} and {@code 9 * STAT_MAX} with {@code STAT_MAX} of five
 * give 35 and 45, and both comparisons are strict, so the accepted totals are 36 to 44 inclusive
 * against a possible 15 to 60. The bounds themselves have to be reachable: a port that had written
 * {@code >=} and {@code <=} would accept 35 and 45 as well, and one that had narrowed the window
 * would never produce 36 or 44. Both are checked by sampling, since a d3, a d4 and a d5 average 7.5
 * between them and fifteen dice average 37.5, putting both ends of the window a couple of standard
 * deviations out at most.
 *
 * <p><b>Termination.</b> C resets its accumulator in the {@code for} initialiser,
 * {@code for (j = i = 0; ...)}, so every attempt is judged on its own dice. An accumulator carried
 * between attempts only grows, so once a first roll misses low the running total sails past 44 and
 * the loop never breaks. That failure is a hang rather than a wrong answer, which is why the
 * sampling tests here run under a timeout — a regression should fail the suite, not stop it.
 *
 * <p><b>The bonus goes to the caller's map alone.</b> The race and class adjustments reach
 * {@code stat_use} and nothing else; the value stored on the player is the bare roll. The fixture
 * gives every stat a different adjustment, so a port that had applied the wrong stat's bonus, or
 * written the adjusted figure back to the player, shows up rather than cancelling out.
 *
 * <p>Class PlayerBirthGetStatsTest coded on 260902, commented in full on 260902.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class PlayerBirthGetStatsTest {

    /**
     * C's {@code STAT_MAX} ({@code player.h}), repeated here rather than read from the port so that
     * a test does not agree with the constant it is checking.
     */
    private static final int STAT_MAX = 5;

    /**
     * The base added to each stat's three dice ({@code player-birth.c:256}).
     */
    private static final int BASE = 5;

    /**
     * The lowest dice total the window admits, from {@code j > 7 * STAT_MAX}.
     */
    private static final int LOWEST_TOTAL = 7 * STAT_MAX + 1;

    /**
     * The highest dice total the window admits, from {@code j < 9 * STAT_MAX}.
     */
    private static final int HIGHEST_TOTAL = 9 * STAT_MAX - 1;

    /**
     * The smallest value a stat can be rolled at, {@code 5 + 1 + 1 + 1}.
     */
    private static final int LOWEST_STAT = BASE + 3;

    /**
     * The largest value a stat can be rolled at, {@code 5 + 3 + 4 + 5}.
     */
    private static final int HIGHEST_STAT = BASE + 12;

    /**
     * Births per sampling test — enough for both ends of the window, each a few per cent of
     * accepted rolls, to appear many times over.
     */
    private static final int SAMPLES = 5000;

    /**
     * The ceiling on any one sampling test, so that a non-terminating roller fails rather than
     * hanging the suite.
     */
    private static final Duration TIMEOUT = Duration.ofSeconds(30);

    /**
     * The five real stats in C's index order, so a test can talk about "the stat at index two".
     */
    private static final List<Stats> REAL_STATS = List.of(Stats.STAT_STR, Stats.STAT_INT,
            Stats.STAT_WIS, Stats.STAT_DEX, Stats.STAT_CON);

    /**
     * The race's adjustment to each stat, all different and of both signs, so that a bonus applied
     * to the wrong stat cannot go unnoticed.
     */
    private static final Map<Stats, Integer> RACE_ADJUST = Map.of(
            Stats.STAT_STR, 2,
            Stats.STAT_INT, -1,
            Stats.STAT_WIS, 0,
            Stats.STAT_DEX, 3,
            Stats.STAT_CON, -2);

    /**
     * The class's adjustment to each stat, again all different, and chosen so that no two stats end
     * up with the same total adjustment once the race's is added.
     */
    private static final Map<Stats, Integer> CLASS_ADJUST = Map.of(
            Stats.STAT_STR, 1,
            Stats.STAT_INT, 4,
            Stats.STAT_WIS, -3,
            Stats.STAT_DEX, -1,
            Stats.STAT_CON, 5);

    /**
     * The character being born, fresh for each test.
     */
    private Player player;

    /**
     * The map {@code getStats} fills, the port's stand-in for C's {@code stat_use[]} argument.
     */
    private Map<Stats, Integer> statUse;

    /**
     * A race carrying the given stat adjustments and contributing nothing else.
     *
     * @param adjust the adjustment per stat
     * @return the race
     */
    private static PlayerRace race(Map<Stats, Integer> adjust) {
        Map<PlayerSkill, Integer> skills = new HashMap<>();
        for (PlayerSkill skill : PlayerSkill.values()) {
            if (skill == PlayerSkill.SKILL_NONE || skill == PlayerSkill.SKILL_MAX) continue;
            skills.put(skill, 0);
        }
        return new PlayerRace("Test Race", 0, 10, 100, 14, 6, 72, 6, 180, 25, 0, null,
                new HashMap<>(adjust), skills, new Flag<>(ObjectFlag.class),
                new Flag<>(PlayerFlag.class), null, new HashMap<>());
    }

    /**
     * A class carrying the given stat adjustments and contributing nothing else.
     *
     * @param adjust the adjustment per stat
     * @return the class
     */
    private static PlayerClass playerClass(Map<Stats, Integer> adjust) {
        Map<PlayerSkill, Integer> skills = new HashMap<>();
        Map<PlayerSkill, Integer> extra = new HashMap<>();
        for (PlayerSkill skill : PlayerSkill.values()) {
            if (skill == PlayerSkill.SKILL_NONE || skill == PlayerSkill.SKILL_MAX) continue;
            skills.put(skill, 0);
            extra.put(skill, 0);
        }
        return new PlayerClass("Test Class", List.of(), new HashMap<>(adjust), skills, extra, 0, 0,
                new Flag<>(ObjectFlag.class), new Flag<>(PlayerFlag.class), 5, 30, 5, List.of(),
                null);
    }

    /**
     * The port's rendering of C's {@code modify_stat_value}, which the expected {@code stat_use}
     * values are built from. Re-derived here from the C rather than called through
     * {@link PlayerUtils}, so that this class does not agree with the port it is checking:
     * a point is worth one below 18 and ten at or above it, and a penalty is worth ten down to 18
     * and one below that, with a floor of 3 ({@code player-util.c:342-372}).
     *
     * @param value  the rolled value
     * @param amount the adjustment in points
     * @return the adjusted value
     */
    private static int expectedUse(int value, int amount) {
        for (int index = 0; index < amount; index++) {
            if (value < 18) value++;
            else value += 10;
        }
        for (int index = 0; index < -amount; index++) {
            if (value >= 18 + 10) value -= 10;
            else if (value > 18) value = 18;
            else if (value > 3) value--;
        }
        return value;
    }

    /**
     * Builds a character with a race and a class whose adjustments are all different.
     *
     * @throws Exception if a field cannot be reached
     */
    @BeforeEach
    void newCharacter() throws Exception {
        player = new Player();
        set("race", race(RACE_ADJUST));
        set("playerClass", playerClass(CLASS_ADJUST));
        statUse = new EnumMap<>(Stats.class);
    }

    /**
     * Writes one of {@link Player}'s private fields. Race and class have no setters — in C they are
     * chosen on the birth screen, which the port has not reached — so the fixture reaches them
     * directly, as the other birth tests do.
     *
     * @param fieldName the field to write
     * @param value     the value to store
     * @throws Exception if the field cannot be reached
     */
    private void set(String fieldName, Object value) throws Exception {
        Field field = Player.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(player, value);
    }

    /**
     * Reads one of {@link Player}'s private stat maps. The scramble map and the birth record have
     * getters in neither the port nor a shape the test could use, so they are read directly.
     *
     * @param fieldName the map to read
     * @return that map
     * @throws Exception if the field cannot be reached
     */
    @SuppressWarnings("unchecked")
    private Map<Stats, ?> map(String fieldName) throws Exception {
        Field field = Player.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        return (Map<Stats, ?>) field.get(player);
    }

    /**
     * The dice total the roller accepted, recovered from the character: the five stats hold the
     * fifteen dice plus five bases between them.
     *
     * @return the accepted total
     */
    private int acceptedTotal() {
        int total = 0;
        for (Stats stat : REAL_STATS) total += player.getMaxStatValue(stat) - BASE;
        return total;
    }

    /**
     * The five accessors {@code get_stats} writes the character through, checked as accessors before
     * they are trusted inside the roller.
     */
    @Nested
    @DisplayName("the stat accessors")
    class Accessors {

        /**
         * {@code setStatMax} writes the slot both maximum getters read, and it stores rather than
         * accumulates — C's {@code p->stat_max[i] = j} is a plain assignment.
         */
        @Test
        @DisplayName("setStatMax writes the value both maximum getters read")
        void setStatMaxWritesTheMaximum() {
            player.setStatMax(Stats.STAT_STR, 16);
            assertEquals(16, player.getStatMax(Stats.STAT_STR));
            assertEquals(16, player.getMaxStatValue(Stats.STAT_STR));
            player.setStatMax(Stats.STAT_STR, 16);
            assertEquals(16, player.getStatMax(Stats.STAT_STR),
                    "the setter accumulated rather than storing");
        }

        /**
         * The stats are separate slots, not one shared value; a write to one leaves the rest alone.
         */
        @Test
        @DisplayName("each stat is its own slot")
        void statsAreSeparate() {
            for (int index = 0; index < STAT_MAX; index++) {
                player.setStatMax(REAL_STATS.get(index), 10 + index);
            }
            for (int index = 0; index < STAT_MAX; index++) {
                assertEquals(10 + index, player.getStatMax(REAL_STATS.get(index)),
                        REAL_STATS.get(index).name());
            }
        }

        /**
         * {@code setCurrStatValue} writes the drained value, which {@code getCurStatValue} reads,
         * and leaves the maximum where it was — C's {@code stat_cur} and {@code stat_max} are
         * separate arrays, and only {@code player_stat_inc} ties them together.
         */
        @Test
        @DisplayName("setCurrStatValue writes the current value and not the maximum")
        void setCurrStatValueWritesTheCurrent() {
            player.setStatMax(Stats.STAT_CON, 17);
            player.setCurrStatValue(Stats.STAT_CON, 9);
            assertEquals(9, player.getCurStatValue(Stats.STAT_CON));
            assertEquals(17, player.getStatMax(Stats.STAT_CON));
        }

        /**
         * {@code setCurrStatMap} stores the stat the slot should read through. The identity entry is
         * what birth writes; a non-identity one is what a scramble would write, and both have to
         * round-trip.
         *
         * @throws Exception if the map cannot be reached
         */
        @Test
        @DisplayName("setCurrStatMap stores the stat the slot points at")
        void setCurrStatMapStoresTheTarget() throws Exception {
            player.setCurrStatMap(Stats.STAT_STR, Stats.STAT_STR);
            player.setCurrStatMap(Stats.STAT_INT, Stats.STAT_WIS);
            assertEquals(Stats.STAT_STR, map("statMap").get(Stats.STAT_STR));
            assertEquals(Stats.STAT_WIS, map("statMap").get(Stats.STAT_INT));
        }

        /**
         * {@code setStatBirth} writes its own record, touching neither the maximum nor the current
         * value — in C the three are distinct arrays, and only this one survives play unchanged.
         *
         * @throws Exception if the map cannot be reached
         */
        @Test
        @DisplayName("setStatBirth writes its own record")
        void setStatBirthIsSeparate() throws Exception {
            player.setStatMax(Stats.STAT_DEX, 14);
            player.setCurrStatValue(Stats.STAT_DEX, 12);
            player.setStatBirth(Stats.STAT_DEX, 16);
            assertEquals(16, map("statsBirth").get(Stats.STAT_DEX));
            assertEquals(14, player.getStatMax(Stats.STAT_DEX));
            assertEquals(12, player.getCurStatValue(Stats.STAT_DEX));
        }
    }

    /**
     * {@code getStats}, the port of C's {@code get_stats}.
     */
    @Nested
    @DisplayName("PlayerBirth.getStats")
    class GetStats {

        /**
         * Every rolled stat is {@code 5 + 1d3 + 1d4 + 1d5}, so 8 to 17, and both extremes are
         * reachable. A port that had given a stat the wrong die size would show up at the ends: the
         * smallest and largest values only come from the exact triple {@code 1,1,1} and
         * {@code 3,4,5}.
         */
        @Test
        @DisplayName("every stat is rolled in 8 to 17, both ends reachable")
        void statsAreRolledInRange() {
            assertTimeoutPreemptively(TIMEOUT, () -> {
                int lowest = Integer.MAX_VALUE;
                int highest = Integer.MIN_VALUE;
                for (int birth = 0; birth < SAMPLES; birth++) {
                    PlayerBirth.getStats(player, statUse);
                    for (Stats stat : REAL_STATS) {
                        int rolled = player.getMaxStatValue(stat);
                        assertTrue(rolled >= LOWEST_STAT && rolled <= HIGHEST_STAT,
                                stat.name() + " was rolled at " + rolled);
                        lowest = Math.min(lowest, rolled);
                        highest = Math.max(highest, rolled);
                    }
                }
                assertEquals(LOWEST_STAT, lowest, "no stat ever rolled the minimum triple");
                assertEquals(HIGHEST_STAT, highest, "no stat ever rolled the maximum triple");
            });
        }

        /**
         * The five stats between them account for all fifteen dice exactly once, so their total
         * less the five bases is the {@code j} the window accepted, and lies in 36 to 44 on every
         * birth. This is the subscript test: a stride other than three re-uses some dice and skips
         * others, and the total stops matching what was verified.
         */
        @Test
        @DisplayName("the five stats hold the fifteen dice the window accepted")
        void statsHoldTheAcceptedDice() {
            assertTimeoutPreemptively(TIMEOUT, () -> {
                for (int birth = 0; birth < SAMPLES; birth++) {
                    PlayerBirth.getStats(player, statUse);
                    int total = acceptedTotal();
                    assertTrue(total >= LOWEST_TOTAL && total <= HIGHEST_TOTAL,
                            "the rolled stats total " + total
                                    + " dice, which the window would have rejected");
                }
            });
        }

        /**
         * Both bounds of the window are inclusive of their nearest admitted value and exclusive of
         * the value itself: C breaks on {@code j > 35 && j < 45}, so 36 and 44 are accepted and 35
         * and 45 are not. Sampling has to see both ends, or the window in the port is narrower than
         * C's.
         */
        @Test
        @DisplayName("accepts 36 and 44 and nothing outside them")
        void windowBoundsAreReached() {
            assertTimeoutPreemptively(TIMEOUT, () -> {
                int lowest = Integer.MAX_VALUE;
                int highest = Integer.MIN_VALUE;
                for (int birth = 0; birth < SAMPLES; birth++) {
                    PlayerBirth.getStats(player, statUse);
                    lowest = Math.min(lowest, acceptedTotal());
                    highest = Math.max(highest, acceptedTotal());
                }
                assertEquals(LOWEST_TOTAL, lowest,
                        "the lowest accepted total was " + lowest + ", so the window is not 36 to 44");
                assertEquals(HIGHEST_TOTAL, highest,
                        "the highest accepted total was " + highest + ", so the window is not 36 to 44");
            });
        }

        /**
         * The rejection loop starts each attempt from zero. A port carrying its total between
         * attempts can never fall back inside the window once a roll has missed, so it does not
         * produce a wrong answer — it produces none at all. A few hundred births are far more than
         * the handful of attempts a working roller needs, and a broken one does not finish the
         * first.
         */
        @Test
        @DisplayName("terminates on every birth, so the total is reset per attempt")
        void rerollTotalIsResetEachAttempt() {
            assertTimeoutPreemptively(TIMEOUT, () -> {
                for (int birth = 0; birth < 500; birth++) {
                    PlayerBirth.getStats(player, statUse);
                }
            });
        }

        /**
         * "Start fully healed": the current value is seeded equal to the maximum for every stat
         * ({@code player-birth.c:265}), whatever the character held before.
         */
        @Test
        @DisplayName("seeds every current value from the maximum")
        void currentValuesStartAtTheMaximum() {
            for (Stats stat : REAL_STATS) player.setCurrStatValue(stat, 3);
            PlayerBirth.getStats(player, statUse);
            for (Stats stat : REAL_STATS) {
                assertEquals(player.getMaxStatValue(stat), player.getCurStatValue(stat),
                        stat.name());
            }
        }

        /**
         * "Start with unscrambled stats": every slot points at itself, C's
         * {@code p->stat_map[i] = i} ({@code player-birth.c:268}). A scrambled map left over from a
         * previous character is overwritten rather than kept.
         *
         * @throws Exception if the map cannot be reached
         */
        @Test
        @DisplayName("resets the scramble map to the identity")
        void scrambleMapIsTheIdentity() throws Exception {
            player.setCurrStatMap(Stats.STAT_STR, Stats.STAT_CON);
            PlayerBirth.getStats(player, statUse);
            for (Stats stat : REAL_STATS) {
                assertEquals(stat, map("statMap").get(stat), stat.name());
            }
        }

        /**
         * The birth record is the rolled maximum, taken after it was stored
         * ({@code player-birth.c:274}) and so before any adjustment — the two must agree stat by
         * stat.
         *
         * @throws Exception if the map cannot be reached
         */
        @Test
        @DisplayName("records the rolled maximum as the birth value")
        void birthRecordMatchesTheRoll() throws Exception {
            PlayerBirth.getStats(player, statUse);
            for (Stats stat : REAL_STATS) {
                assertEquals(player.getMaxStatValue(stat), map("statsBirth").get(stat),
                        stat.name());
            }
        }

        /**
         * The racial and class adjustments are summed and applied through
         * {@code modify_stat_value}, and the result reaches {@code stat_use} only. With every stat
         * given a different pair of adjustments, a bonus applied to the wrong stat cannot cancel
         * out.
         */
        @Test
        @DisplayName("applies the summed race and class bonus to the caller's map")
        void bonusReachesStatUse() {
            for (int birth = 0; birth < 200; birth++) {
                PlayerBirth.getStats(player, statUse);
                for (Stats stat : REAL_STATS) {
                    int bonus = RACE_ADJUST.get(stat) + CLASS_ADJUST.get(stat);
                    assertEquals(expectedUse(player.getMaxStatValue(stat), bonus),
                            statUse.get(stat), stat.name());
                }
            }
        }

        /**
         * The bonus reaches nothing else. The value stored on the player is the bare roll in the 8
         * to 17 range, while the working map carries it adjusted — three points up for strength
         * (+2 race, +1 class) and three down for wisdom (0 race, -3 class). Below 18 a point is
         * worth exactly one either way, and the floor of 3 is out of reach from a roll of 8, so the
         * two differences are exact rather than bounded. C never writes the adjusted figure back.
         */
        @Test
        @DisplayName("leaves the player's own stats unadjusted")
        void playerKeepsTheBareRoll() {
            PlayerBirth.getStats(player, statUse);

            int strength = player.getMaxStatValue(Stats.STAT_STR);
            int wisdom = player.getMaxStatValue(Stats.STAT_WIS);
            assertTrue(strength >= LOWEST_STAT && strength <= HIGHEST_STAT,
                    "the player's strength was adjusted, reading " + strength);
            assertTrue(wisdom >= LOWEST_STAT && wisdom <= HIGHEST_STAT,
                    "the player's wisdom was adjusted, reading " + wisdom);

            assertEquals(strength + 3, statUse.get(Stats.STAT_STR),
                    "strength is adjusted by +2 race and +1 class");
            assertEquals(wisdom - 3, statUse.get(Stats.STAT_WIS),
                    "wisdom is adjusted by 0 race and -3 class");
        }

        /**
         * Exactly the five real stats are written. {@code STAT_NONE} and {@code STAT_MAX} have no
         * slot in C's arrays, and the enum walk skips them rather than giving them one.
         */
        @Test
        @DisplayName("writes the five real stats and neither sentinel")
        void sentinelsAreSkipped() {
            PlayerBirth.getStats(player, statUse);
            assertEquals(STAT_MAX, statUse.size());
            assertFalse(statUse.containsKey(Stats.STAT_NONE), "STAT_NONE was given a value");
            assertFalse(statUse.containsKey(Stats.STAT_MAX), "STAT_MAX was given a value");
        }

        /**
         * A second call re-rolls rather than adding to what is there — {@code do_cmd_roll_stats}
         * calls it once per re-roll ({@code player-birth.c:1167}), so the previous character's
         * figures must be gone. Fifteen dice agreeing twice over is possible; twenty births
         * agreeing every time is not.
         */
        @Test
        @DisplayName("re-rolls on a second call")
        void secondCallRerolls() {
            PlayerBirth.getStats(player, statUse);
            Map<Stats, Integer> first = new EnumMap<>(Stats.class);
            for (Stats stat : REAL_STATS) first.put(stat, player.getMaxStatValue(stat));

            boolean differs = false;
            for (int attempt = 0; attempt < 20 && !differs; attempt++) {
                PlayerBirth.getStats(player, statUse);
                for (Stats stat : REAL_STATS) {
                    if (player.getMaxStatValue(stat) != first.get(stat)) {
                        differs = true;
                        break;
                    }
                }
            }
            assertTrue(differs, "twenty births produced the same stats every time");
        }

        /**
         * The caller's map is filled, not replaced, and a stale entry for a real stat is
         * overwritten — C writes into an array the caller owns.
         */
        @Test
        @DisplayName("fills the caller's map in place")
        void fillsTheCallersMap() {
            Map<Stats, Integer> supplied = new EnumMap<>(Stats.class);
            supplied.put(Stats.STAT_STR, 999);
            PlayerBirth.getStats(player, supplied);
            assertTrue(supplied.get(Stats.STAT_STR) < 999, "the stale entry survived");
        }

        /**
         * The port's {@code STAT_MAX} is C's. Every bound above is derived from five, so a port that
         * had changed it would make the rest of this class meaningless rather than fail.
         */
        @Test
        @DisplayName("STAT_MAX is five")
        void statMaxIsFive() {
            assertEquals(STAT_MAX, Stats.STAT_MAX.getValue());
        }
    }
}
