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
 * Tests {@link Player#playerExpLose(long, boolean)}, the route every experience drain in the game
 * arrives by.
 *
 * <p>The expected figures come from C's {@code player_exp_lose} ({@code player.c:278}) and the
 * {@code player_exp[]} thresholds it ends in, not from the port.
 *
 * <p>The cap is the part worth pinning, and it is worth pinning twice. C writes the cap back into
 * {@code amount} before subtracting, so an oversized drain takes the character to zero rather than
 * past it, <em>and</em> a permanent drain reduces the maximum by what was actually taken rather
 * than by what was asked for. A port that capped only the current total would leave the maximum
 * short by the difference, and nothing would notice until the character tried to earn it back.
 *
 * <p>The maximum has no floor here, only the current total does. A permanent drain can carry it
 * below zero, and {@code adjustLevel} is what floors it afterwards - so that case is tested for its
 * end state rather than for the intermediate.
 *
 * <p>A drain is silent. C's downward walk announces nothing whatever {@code verbose} says, so a
 * character who loses levels has nothing written to their history, and their highest level reached
 * stands because {@code maxLevel} is driven by {@code maxExp} alone.
 *
 * <p>Class PlayerExpLoseTest coded on 260831, commented in full on 260831.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class PlayerExpLoseTest {

    /**
     * The player under test.
     */
    private Player player;

    /**
     * A new player and the real experience table for each test.
     *
     * <p>The character comes from {@link CalcBonusesFixture} because {@code playerExpLose} ends in
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
     * What the two flavours of drain take, and from which total.
     */
    @Nested
    @DisplayName("the two totals")
    class Totals {

        /**
         * The ordinary drain - a nether breath, a draining blow - leaves the maximum alone, which
         * is what lets the character earn the same experience back.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("a temporary loss leaves the maximum alone")
        void temporaryLossKeepsTheMaximum() throws Exception {
            state(1000, 1000, 13, 13);

            player.playerExpLose(300, false);

            assertEquals(700L, longField("exp"));
            assertEquals(1000L, longField("maxExp"), "the maximum is what can be earned back");
        }

        /**
         * A permanent drain takes the maximum down with it, so the experience is gone for good.
         * Only {@code effect-handler-general.c:3533} asks for this.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("a permanent loss takes the maximum too")
        void permanentLossTakesTheMaximum() throws Exception {
            state(1000, 1000, 13, 13);

            player.playerExpLose(300, true);

            assertEquals(700L, longField("exp"));
            assertEquals(700L, longField("maxExp"));
        }

        /**
         * A drain of nothing changes neither total. The black-breath upkeep in
         * {@code game-world.c:755} divides by ten before calling, so a small enough hit arrives
         * here as zero.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("a drain of nothing changes neither total")
        void zeroLossChangesNothing() throws Exception {
            state(1000, 1000, 13, 13);

            player.playerExpLose(0, true);

            assertEquals(1000L, longField("exp"));
            assertEquals(1000L, longField("maxExp"));
        }
    }

    /**
     * The cap, which is the whole of the function's arithmetic.
     */
    @Nested
    @DisplayName("the cap")
    class Cap {

        /**
         * A drain larger than the character has takes everything and stops. Experience never goes
         * negative however hard the drain.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("an oversized loss takes everything and stops at zero")
        void oversizedLossStopsAtZero() throws Exception {
            state(400, 1000, 8, 13);

            player.playerExpLose(9999, false);

            assertEquals(0L, longField("exp"));
            assertEquals(1000L, longField("maxExp"), "and a temporary drain still spares the maximum");
        }

        /**
         * The half a port can lose. C writes the cap back into {@code amount}, so the maximum falls
         * by the 400 actually taken and not by the 9999 asked for - a maximum of 1000 ends at 600,
         * not at zero and not below.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("the cap applies to the maximum on a permanent loss")
        void capAppliesToTheMaximum() throws Exception {
            state(400, 1000, 8, 13);

            player.playerExpLose(9999, true);

            assertEquals(0L, longField("exp"));
            assertEquals(600L, longField("maxExp"),
                    "the maximum lost the 400 actually taken, not the 9999 asked for");
        }

        /**
         * A drain of exactly what the character has is not capped - the test is {@code <}, not
         * {@code <=} - and lands on zero by subtraction rather than by the cap. The two routes are
         * indistinguishable from outside, which is the point: an off-by-one in the comparison would
         * change nothing here, so this pins the boundary rather than the branch.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("a drain of exactly the total lands on zero")
        void exactDrainLandsOnZero() throws Exception {
            state(400, 1000, 8, 13);

            player.playerExpLose(400, true);

            assertEquals(0L, longField("exp"));
            assertEquals(600L, longField("maxExp"));
        }

        /**
         * Only the current total is capped. A permanent drain of a character whose maximum is the
         * lower of the two figures drives that maximum below zero inside the function, and
         * {@code adjustLevel} is what floors it - so the character ends at zero rather than in
         * debt.
         *
         * <p>The starting state is unusual but reachable: {@code adjustLevel} raises the maximum to
         * the current total, so it can only arise if the current total was written past the maximum
         * without going through it. It is tested because the floor it relies on lives in another
         * method, and that is exactly the kind of dependency a later change breaks quietly.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("a maximum driven below zero is floored, not left negative")
        void negativeMaximumIsFloored() throws Exception {
            state(400, 300, 8, 8);

            player.playerExpLose(400, true);

            assertEquals(0L, longField("exp"));
            assertEquals(0L, longField("maxExp"), "adjustLevel floors what this method drove to -100");
        }

        /**
         * The cap is a one-sided test, so a negative amount passes straight through it and the
         * subtraction becomes an addition - a permanent "loss" of -50 raises both totals by 50.
         *
         * <p>No caller in 4.2.6 does this: every drain is a damage figure or a division of one. It
         * is recorded because it is C's behaviour rather than because it is reachable, and because a
         * port that added a floor of zero to {@code amount} would be a divergence that no other test
         * here would catch.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("a negative amount is not capped, and adds")
        void negativeAmountAdds() throws Exception {
            state(100, 1000, 6, 13);

            player.playerExpLose(-50, true);

            assertEquals(150L, longField("exp"));
            assertEquals(1050L, longField("maxExp"));
        }
    }

    /**
     * What the drain does to the levels, which is {@code adjustLevel}'s work but reached only
     * through here.
     */
    @Nested
    @DisplayName("the levels")
    class Levels {

        /**
         * A temporary drain lowers the working level and leaves the highest level reached standing,
         * because {@code maxLevel} is driven by {@code maxExp} and this drain did not touch it.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("a temporary drain lowers the level and keeps maxLevel")
        void temporaryDrainKeepsMaxLevel() throws Exception {
            state(1000, 1000, 13, 13);

            player.playerExpLose(900, false);

            assertEquals(100L, longField("exp"));
            assertEquals(6, intField("level"), "100 is exactly player_exp[4], the cost of level 6");
            assertEquals(13, intField("maxLevel"), "the best level reached is never taken back");
        }

        /**
         * And it is silent. C's downward walk writes no history entry and prints no message
         * whatever {@code verbose} says, so a character can lose seven levels without a line in
         * their log.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("losing levels is logged nowhere")
        void losingLevelsIsSilent() throws Exception {
            state(1000, 1000, 13, 13);

            player.playerExpLose(900, false);

            assertTrue(history().isEmpty());
        }

        /**
         * A permanent drain of everything takes both totals to zero and the working level to 1, and
         * {@code maxLevel} still stands: the third loop only ever walks it up.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("a permanent total drain drops the level to 1 but keeps maxLevel")
        void permanentTotalDrainDropsToLevelOne() throws Exception {
            state(500, 500, 11, 11);

            player.playerExpLose(500, true);

            assertEquals(0L, longField("exp"));
            assertEquals(0L, longField("maxExp"));
            assertEquals(1, intField("level"));
            assertEquals(11, intField("maxLevel"));
        }
    }
}
