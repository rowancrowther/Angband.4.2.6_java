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
import uk.co.jackoftradesltd.middle.game.globals.loaders.PlayerDataLoader;
import uk.co.jackoftradesltd.middle.game.globals.registry.PlayerRegistry;
import uk.co.jackoftradesltd.testsupport.CalcBonusesFixture;
import uk.co.jackoftradesltd.testsupport.SeededPlayerRegistry;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link Player#playerExpGain(long)}, the route every experience award in the game arrives
 * by.
 *
 * <p>The expected figures come from C's {@code player_exp_gain} ({@code player.c:269}) and the
 * {@code player_exp[]} thresholds it ends in, not from the port. Three lines is the whole function,
 * and each of them hides something worth pinning.
 *
 * <p>The maximum takes a tenth of the award, and the test that guards it is made <em>after</em> the
 * current total has already been raised. That ordering is the only part of the function a reader
 * can plausibly get wrong, and it is visible only in a narrow band: an award that carries the
 * character from behind their maximum to past it earns no tenth at all, where testing first would
 * have paid one. {@code gainThatOvertakesTheMaximumEarnsNoTenth} is the case that separates them.
 *
 * <p>The tenth is integer division, so awards below ten add nothing to the maximum, and a negative
 * award - which {@code cmd-wizard.c:1208} produces when a wizard lowers a character's experience -
 * truncates towards zero in both languages rather than towards minus infinity.
 *
 * <p>Nothing is clamped in the function itself. {@code adjust_level} is what floors both totals at
 * zero, caps them at {@code PY_MAX_EXP} and drags the maximum up to the current total, so an award
 * past the cap is tested here through its visible result rather than at the point it overflows.
 *
 * <p>Class PlayerExpGainTest coded on 260831, commented in full on 260831.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class PlayerExpGainTest {

    /**
     * The player under test.
     */
    private Player player;

    /**
     * A new player and the real experience table for each test.
     *
     * <p>The character comes from {@link CalcBonusesFixture} because {@code playerExpGain} ends in
     * {@code adjustLevel}, which runs {@code handleStuff} twice; that walks the race, class and
     * stat maps a bare {@code new Player()} does not have. {@code playerHP} is sized for all fifty
     * levels because the hit-point recalculation indexes it by whatever level the arithmetic lands
     * on, and {@code expFact} is 100 so the thresholds are the table's own.
     *
     * @throws Exception if a fixture field cannot be reached
     */
    @BeforeEach
    void newPlayer() throws Exception {
        PlayerDataLoader.initialiseExpLevel();
        player = CalcBonusesFixture.plainCharacter().player();
        set("playerHP", new int[PlayerRegistry.PY_MAX_LEVEL]);
        set("expFact", 100);
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
     * Reads one of the player's private long fields. The two experience totals are {@code long}
     * because C's are 32-bit quantities that do not fit a Java {@code int} comfortably.
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
     * Puts the player's four progression figures in a known state.
     *
     * @param exp      the current experience
     * @param maxExp   the maximum experience
     * @param level    the starting character level
     * @param maxLevel the starting highest level
     * @throws Exception if a field cannot be reached
     */
    private void state(long exp, long maxExp, int level, int maxLevel) throws Exception {
        set("exp", exp);
        set("maxExp", maxExp);
        set("level", level);
        set("maxLevel", maxLevel);
    }

    /**
     * The player's history entries.
     *
     * @return the ledger
     */
    private List<HistoryInfo> history() {
        return player.getPlayerHistory().entries;
    }

    /**
     * The tenth paid to the maximum, which is the whole of the function's arithmetic.
     */
    @Nested
    @DisplayName("the maximum")
    class Maximum {

        /**
         * A character behind their maximum - one who has been drained and is earning it back - has
         * the award added in full to the current total and a tenth of it added to the maximum. The
         * tenth is what makes a drain cost something permanent.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("a character behind their maximum earns a tenth of the award on top")
        void behindTheMaximumEarnsATenth() throws Exception {
            state(100, 1000, 1, 1);

            player.playerExpGain(200);

            assertEquals(300L, longField("exp"), "the current total takes the whole award");
            assertEquals(1020L, longField("maxExp"), "and the maximum takes 200 / 10");
        }

        /**
         * A character already level with their maximum earns no tenth: the guard fails, and the
         * maximum ends up equal to the new current total only because {@code adjustLevel} drags it
         * there afterwards.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("a character level with their maximum earns no tenth")
        void levelWithTheMaximumEarnsNothing() throws Exception {
            state(1000, 1000, 1, 1);

            player.playerExpGain(200);

            assertEquals(1200L, longField("exp"));
            assertEquals(1200L, longField("maxExp"),
                    "the maximum followed the current total rather than gaining a tenth");
        }

        /**
         * The case that pins the ordering. The character starts 100 behind their maximum and is
         * awarded 101, which carries them one point past it - so the guard, which is tested after
         * the award has landed, fails and no tenth is paid.
         *
         * <p>Were the guard tested before the award, as it reads at a glance, the maximum would
         * have taken its 10 first and finished at 1010 rather than at the 1001 that
         * {@code adjustLevel} drags it to.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("an award that overtakes the maximum earns no tenth")
        void gainThatOvertakesTheMaximumEarnsNoTenth() throws Exception {
            state(900, 1000, 1, 1);

            player.playerExpGain(101);

            assertEquals(1001L, longField("exp"));
            assertEquals(1001L, longField("maxExp"),
                    "1010 here would mean the guard had been tested before the award");
        }

        /**
         * The tenth is integer division, so the small awards the game hands out constantly - a
         * point for disarming a trap, a point for a chest - lift the current total and leave the
         * maximum exactly where it was.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("an award below ten adds nothing to the maximum")
        void smallAwardsAddNothingToTheMaximum() throws Exception {
            state(0, 1000, 1, 1);

            player.playerExpGain(9);

            assertEquals(9L, longField("exp"));
            assertEquals(1000L, longField("maxExp"), "9 / 10 is 0");
        }

        /**
         * And the division truncates rather than rounding: nineteen pays one, not two.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("the tenth truncates rather than rounding")
        void theTenthTruncates() throws Exception {
            state(0, 1000, 1, 1);

            player.playerExpGain(19);

            assertEquals(19L, longField("exp"));
            assertEquals(1001L, longField("maxExp"), "19 / 10 is 1");
        }

        /**
         * An award of nothing changes nothing. {@code obj-knowledge.c:1954} can produce this: the
         * rune-learning award is {@code (lev + p->lev / 2) / p->lev}, which is zero for a deep
         * character learning a shallow rune.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("an award of nothing changes neither total")
        void zeroAwardChangesNothing() throws Exception {
            state(100, 1000, 1, 1);

            player.playerExpGain(0);

            assertEquals(100L, longField("exp"));
            assertEquals(1000L, longField("maxExp"));
        }
    }

    /**
     * The routes that are not an ordinary kill: the wizard's downward adjustment and the cap.
     */
    @Nested
    @DisplayName("the edges")
    class Edges {

        /**
         * A negative award is a real caller - {@code cmd-wizard.c:1208} produces one when a wizard
         * sets a character's experience lower than it was - and both languages truncate the tenth
         * towards zero, so -95 takes 9 off the maximum and not 10.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("a negative award truncates its tenth towards zero")
        void negativeAwardTruncatesTowardsZero() throws Exception {
            state(1000, 2000, 1, 1);

            player.playerExpGain(-95);

            assertEquals(905L, longField("exp"));
            assertEquals(1991L, longField("maxExp"), "-95 / 10 is -9 in both C and Java");
        }

        /**
         * Nothing in the function itself stops either total passing {@code PY_MAX_EXP}; the cap is
         * applied by {@code adjustLevel} on the way out, so an award that overshoots ends exactly
         * on the cap.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("an award past the cap ends on the cap")
        void awardPastTheCapIsClamped() throws Exception {
            state(PlayerRegistry.PY_MAX_EXP, PlayerRegistry.PY_MAX_EXP, 50, 50);

            player.playerExpGain(1000);

            assertEquals(PlayerRegistry.PY_MAX_EXP, longField("exp"));
            assertEquals(PlayerRegistry.PY_MAX_EXP, longField("maxExp"));
            assertEquals(50, intField("level"), "and the level is already as high as it goes");
        }
    }

    /**
     * What the award does to the level, which is {@code adjustLevel}'s work but reached only
     * through here.
     */
    @Nested
    @DisplayName("the level")
    class Level {

        /**
         * The award is always verbose, so a level earned by it is announced and written to the
         * character's history. Ten points is C's {@code player_exp[0]}, the cost of level 2.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("an award that earns a level announces it")
        void earnedLevelIsAnnounced() throws Exception {
            state(0, 1000, 1, 1);

            player.playerExpGain(10);

            assertEquals(2, intField("level"), "10 is player_exp[0], the cost of level 2");
            assertEquals(1, history().size());
            assertEquals("Reached level 2", history().get(0).historyText);
        }

        /**
         * The tenth reaches the maximum, and the maximum drives {@code maxLevel} on its own - so an
         * award can lift the highest level reached further than it lifts the working level. Here
         * 300 points of experience is level 9, while the 1020 the maximum has climbed to is level
         * 13.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("maxLevel follows the maximum, which the tenth has moved")
        void maxLevelFollowsTheMaximum() throws Exception {
            state(100, 1000, 1, 1);

            player.playerExpGain(200);

            assertEquals(9, intField("level"), "300 clears player_exp[7], which is 280");
            assertEquals(13, intField("maxLevel"), "1020 clears player_exp[11], which is 850");
        }

        /**
         * An award too small to reach the next threshold leaves the level alone and logs nothing,
         * which is the ordinary case for almost every kill in the game.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("an award short of the next threshold leaves the level alone")
        void awardShortOfTheThresholdLeavesTheLevel() throws Exception {
            state(300, 1000, 9, 13);

            player.playerExpGain(50);

            assertEquals(350L, longField("exp"));
            assertEquals(9, intField("level"), "380 is the cost of level 10, and 350 is short of it");
            assertTrue(history().isEmpty(), "and nothing is written down");
        }
    }
}
