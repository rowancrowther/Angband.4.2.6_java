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

package uk.co.jackoftradesltd.middle.game.gameengine;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.co.jackoftradesltd.middle.cave.Chunk;
import uk.co.jackoftradesltd.middle.game.GameWorld;
import uk.co.jackoftradesltd.middle.player.Player;
import uk.co.jackoftradesltd.testsupport.SeededPlayerRegistry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Tests the members of {@link GameState} that the single-method test classes beside it do not
 * cover: the turn clock ({@link GameState#getTurn()}, {@link GameState#incrementTurn()},
 * {@link GameState#resetTurnFromSave(int)}), the day-count forwarder, and the player, level and
 * {@link CommandQueue} slots.
 *
 * <p>C has no functions for any of these - they are bare reads and writes of the globals
 * {@code turn} ({@code int32_t}, {@code game-world.c}), {@code player} ({@code player.c}) and
 * {@code cave} ({@code cave.c}), plus the {@code turn++} in {@code run_game_loop()}
 * ({@code game-world.c}) and {@code rd_s32b(&turn)} in {@code rd_misc()} ({@code load.c}). The
 * expected values below are therefore what those C statements produce on the same inputs: a plain
 * store, an add of one, and two's-complement wrap at the 32-bit boundary, with nothing clamped and
 * nothing coupled to a neighbouring global.
 *
 * <p>{@link GameState#targetOkay()} is deliberately not tested: it is a stub answering
 * {@code true}, and C's {@code target_okay()} answers {@code false} when no target is set, so a
 * test pinning the stub would fail the moment targeting is ported. {@code CommandTest} exercises it
 * through {@code getTarget}. The {@code seedFlavour} and {@code characterGenerated} pairs have
 * their own test classes, and {@code seedRandart} has no accessor to test.
 *
 * <p>Every member is static, so each test saves the five fields it can touch beforehand and puts
 * them back afterwards to avoid leaking state into whichever test runs next.
 *
 * <p>Class GameStateTest coded on 261006, commented in full on 261006.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class GameStateTest {

    /**
     * The turn count in effect before the test, restored afterwards.
     */
    private int savedTurn;
    /**
     * The player in effect before the test, restored afterwards.
     */
    private Player savedPlayer;
    /**
     * The level in effect before the test, restored afterwards.
     */
    private Chunk savedCave;
    /**
     * The queue in effect before the test, restored afterwards.
     */
    private CommandQueue savedQueue;

    /**
     * Builds a minimal level, with no player attached, that is distinguishable by identity.
     *
     * @return a new level
     */
    private static Chunk newLevel() {
        return new Chunk("test", 0, 0, 0, 0, 0, false, 1, 1, 0, 0, 1, 0, -1, 0, null);
    }

    /**
     * Records every field a test here may write so it can be put back.
     */
    @BeforeEach
    void saveState() {
        savedTurn = GameState.getTurn();
        savedPlayer = GameState.getPlayer();
        savedCave = GameState.getCave();
        savedQueue = GameState.getCommandQueue();
    }

    /**
     * Restores the fields the test found on entry.
     */
    @AfterEach
    void restoreState() {
        GameState.setTurn(savedTurn);
        GameState.setPlayer(savedPlayer);
        GameState.setCave(savedCave);
        GameState.setCommandQueue(savedQueue);
    }

    /**
     * {@link GameState#incrementTurn()} - C's {@code turn++} in {@code run_game_loop()}.
     */
    @Nested
    @DisplayName("incrementTurn")
    class IncrementTurn {

        /**
         * The ordinary path: one call adds exactly one, from zero and from the birth value of 1
         * that {@code player_init()} ({@code player-birth.c}) writes.
         */
        @Test
        @DisplayName("adds exactly one")
        void addsOne() {
            GameState.setTurn(0);
            GameState.incrementTurn();
            assertEquals(1, GameState.getTurn());

            GameState.setTurn(1);
            GameState.incrementTurn();
            assertEquals(2, GameState.getTurn());
        }

        /**
         * Repeated calls accumulate, one per game turn, as the world loop makes them.
         */
        @Test
        @DisplayName("accumulates across calls")
        void accumulates() {
            GameState.setTurn(1);
            for (int i = 0; i < 1000; i++) {
                GameState.incrementTurn();
            }
            assertEquals(1001, GameState.getTurn());
        }

        /**
         * The boundary: {@code int32_t} {@code INT32_MAX + 1} wraps to {@code INT32_MIN} in
         * practice (signed overflow is formally undefined in C), and Java's {@code int} does the
         * same by definition. There is no saturation or reset.
         */
        @Test
        @DisplayName("wraps at the int32 boundary")
        void wrapsAtInt32Max() {
            GameState.setTurn(Integer.MAX_VALUE - 1);
            GameState.incrementTurn();
            assertEquals(Integer.MAX_VALUE, GameState.getTurn());

            GameState.incrementTurn();
            assertEquals(Integer.MIN_VALUE, GameState.getTurn());
        }

        /**
         * C's {@code turn++} cannot reach the separate {@code daycount} global - the day only
         * advances in {@code process_world()}, which {@link GameWorld} owns - so the clock tick
         * must not move it.
         */
        @Test
        @DisplayName("does not disturb the day count")
        void doesNotDisturbDaycount() {
            int dayCountBefore = GameState.getDaycount();

            GameState.setTurn(9999);
            GameState.incrementTurn();

            assertEquals(dayCountBefore, GameState.getDaycount());
        }
    }

    /**
     * {@link GameState#resetTurnFromSave(int)} - C's {@code rd_s32b(&turn)} in {@code rd_misc()}.
     */
    @Nested
    @DisplayName("resetTurnFromSave")
    class ResetTurnFromSave {

        /**
         * The ordinary path: the saved count becomes the clock, replacing whatever was there.
         */
        @Test
        @DisplayName("stores the saved value, replacing the current one")
        void storesSavedValue() {
            GameState.setTurn(77);
            GameState.resetTurnFromSave(123456);
            assertEquals(123456, GameState.getTurn());
        }

        /**
         * {@code rd_s32b} reads any signed 32-bit value and C stores it with no check, so zero and
         * both extremes must come back unchanged.
         */
        @Test
        @DisplayName("stores zero and both int32 extremes unchanged")
        void storesBoundaryValuesUnchanged() {
            GameState.resetTurnFromSave(0);
            assertEquals(0, GameState.getTurn());

            GameState.resetTurnFromSave(Integer.MAX_VALUE);
            assertEquals(Integer.MAX_VALUE, GameState.getTurn());

            GameState.resetTurnFromSave(Integer.MIN_VALUE);
            assertEquals(Integer.MIN_VALUE, GameState.getTurn());
        }

        /**
         * The clock carries on from the restored value: a load followed by one world turn is the
         * saved count plus one, not a restart.
         */
        @Test
        @DisplayName("the clock continues from the restored value")
        void clockContinuesFromRestoredValue() {
            GameState.resetTurnFromSave(5000);
            GameState.incrementTurn();
            assertEquals(5001, GameState.getTurn());
        }
    }

    /**
     * {@link GameState#getDaycount()} - forwards to {@link GameWorld}, which owns C's
     * {@code daycount}.
     */
    @Nested
    @DisplayName("getDaycount")
    class GetDaycount {

        /**
         * The forwarder reports exactly what {@link GameWorld#getDaycount()} does, and writing the
         * turn does not change it.
         */
        @Test
        @DisplayName("reports the GameWorld day count")
        void reportsGameWorldDaycount() {
            assertEquals(GameWorld.getDaycount(), GameState.getDaycount());

            GameState.setTurn(10000);
            assertEquals(GameWorld.getDaycount(), GameState.getDaycount());
        }
    }

    /**
     * {@link GameState#setPlayer(Player)} and {@link GameState#getPlayer()} - C's {@code player}
     * global, which {@code init_player()} assigns and {@code cleanup_player()} resets to
     * {@code NULL}.
     */
    @Nested
    @DisplayName("player slot")
    class PlayerSlot {

        /**
         * The ordinary path: the very object handed in comes back, not a copy.
         */
        @Test
        @DisplayName("returns the same player that was set")
        void returnsSamePlayer() {
            Player player = new Player();
            GameState.setPlayer(player);
            assertSame(player, GameState.getPlayer());
        }

        /**
         * A second player replaces the first outright, as {@code init_player()} does when a new
         * character is made.
         */
        @Test
        @DisplayName("a second player replaces the first")
        void secondPlayerReplacesFirst() {
            Player first = new Player();
            Player second = new Player();
            GameState.setPlayer(first);
            GameState.setPlayer(second);
            assertSame(second, GameState.getPlayer());
        }

        /**
         * {@code cleanup_player()} leaves the global {@code NULL}, so {@code null} must clear the
         * slot rather than being rejected or ignored.
         */
        @Test
        @DisplayName("null clears the slot, as cleanup_player does")
        void nullClearsSlot() {
            GameState.setPlayer(new Player());
            GameState.setPlayer(null);
            assertNull(GameState.getPlayer());
        }

        /**
         * Replacing the player touches neither the level nor the clock; C's {@code player} global
         * is independent of {@code cave} and {@code turn}.
         */
        @Test
        @DisplayName("does not disturb the level or the turn count")
        void doesNotDisturbNeighbours() {
            Chunk level = newLevel();
            GameState.setCave(level);
            GameState.setTurn(321);

            GameState.setPlayer(new Player());

            assertSame(level, GameState.getCave());
            assertEquals(321, GameState.getTurn());
        }
    }

    /**
     * {@link GameState#setCave(Chunk)} and {@link GameState#getCave()} - C's {@code cave} global,
     * which {@code prepare_next_level()} assigns and sets {@code NULL} while the old level is
     * stored.
     */
    @Nested
    @DisplayName("level slot")
    class LevelSlot {

        /**
         * The ordinary path: the very object handed in comes back, not a copy.
         */
        @Test
        @DisplayName("returns the same level that was set")
        void returnsSameLevel() {
            Chunk level = newLevel();
            GameState.setCave(level);
            assertSame(level, GameState.getCave());
        }

        /**
         * A new level replaces the old one outright, as a fresh {@code cave = cave_generate(...)}
         * does.
         */
        @Test
        @DisplayName("a second level replaces the first")
        void secondLevelReplacesFirst() {
            Chunk first = newLevel();
            Chunk second = newLevel();
            GameState.setCave(first);
            GameState.setCave(second);
            assertSame(second, GameState.getCave());
        }

        /**
         * C sets {@code cave = NULL} between levels, so {@code null} must clear the slot.
         */
        @Test
        @DisplayName("null clears the slot, as cave = NULL does")
        void nullClearsSlot() {
            GameState.setCave(newLevel());
            GameState.setCave(null);
            assertNull(GameState.getCave());
        }

        /**
         * Replacing the level touches neither the player nor the clock; C's {@code cave} global is
         * independent of {@code player} and {@code turn}.
         */
        @Test
        @DisplayName("does not disturb the player or the turn count")
        void doesNotDisturbNeighbours() {
            Player player = new Player();
            GameState.setPlayer(player);
            GameState.setTurn(654);

            GameState.setCave(newLevel());

            assertSame(player, GameState.getPlayer());
            assertEquals(654, GameState.getTurn());
        }
    }

    /**
     * {@link GameState#setCommandQueue(CommandQueue)} and {@link GameState#getCommandQueue()} -
     * port-only, since C's command ring is file-scope statics in {@code cmd-core.c} with no global
     * to mirror. What is pinned is the plain store/return the other slots have.
     */
    @Nested
    @DisplayName("command queue slot")
    class CommandQueueSlot {

        /**
         * The ordinary path, and replacement: the object handed in comes back, and a second one
         * replaces it outright.
         */
        @Test
        @DisplayName("returns the queue that was set, and a second replaces it")
        void storesAndReplaces() {
            CommandQueue first = new CommandQueue(null);
            CommandQueue second = new CommandQueue(null);

            GameState.setCommandQueue(first);
            assertSame(first, GameState.getCommandQueue());

            GameState.setCommandQueue(second);
            assertSame(second, GameState.getCommandQueue());
        }

        /**
         * {@code null} clears the slot rather than being rejected.
         */
        @Test
        @DisplayName("null clears the slot")
        void nullClearsSlot() {
            GameState.setCommandQueue(new CommandQueue(null));
            GameState.setCommandQueue(null);
            assertNull(GameState.getCommandQueue());
        }
    }
}
