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

import uk.co.jackoftradesltd.testsupport.SeededPlayerRegistry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests {@link Player#setStatBirth} and {@link Player#getStatBirth} — the quickstart copy of C's
 * {@code p->stat_birth[stat]}.
 *
 * <p>Both are storage, so nothing is proved by reading a value back on its own. What is worth
 * pinning is the shape the C array gives the pair: five independent slots, written together at
 * birth and never touched again by play. The plain roller seeds it from the same roll that opens
 * the working and maximal values, in one statement per stat:
 *
 * <pre>{@code
 * player->stat_birth[i] = player->stat_max[i];   // player-birth.c:274, roll_player_stats
 * }</pre>
 *
 * <p>and the point-based roller seeds it from the points spent instead, again alongside the working
 * and maximal values:
 *
 * <pre>{@code
 * player->stat_cur[i] = player->stat_max[i] = player->stat_birth[i] = stats_local_local[i];   // player-birth.c:274, recalculate_stats
 * }</pre>
 *
 * <p>The value uses the same 3-to-18-then-percentile encoding as {@link Player#getMaxStatValue}. A
 * fresh {@link Player} reads every real stat back as {@code 0} rather than throwing: {@link
 * Player#wipe} and the constructor both zero-fill {@code statsBirth} for the five real stats,
 * matching C's blanket {@code memset(p, 0, sizeof(struct player))} ({@code player-birth.c:406}),
 * which zeroes {@code stat_birth[]} along with every other field in the struct. The two sentinels,
 * {@code STAT_NONE} and {@code STAT_MAX}, are outside that fill and still throw on the auto-unboxing
 * — the same failure mode {@link Player#getStatMax} and {@link Player#getCurStatValue} give for
 * those same two, so this getter still matches its siblings there.
 *
 * <p>Class PlayerStatBirthTest coded on 260906, commented in full on 260906.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class PlayerStatBirthTest {

    /**
     * The five real stats, in C's order.
     */
    private static final Stats[] REAL_STATS = {Stats.STAT_STR, Stats.STAT_INT, Stats.STAT_WIS,
            Stats.STAT_DEX, Stats.STAT_CON};

    /**
     * The player under test, fresh for each test since the birth stats are mutable.
     */
    private Player player;

    /**
     * A new player, as the constructor leaves one.
     */
    @BeforeEach
    void newPlayer() {
        player = new Player();
    }

    /**
     * {@code setStatBirth} and {@code getStatBirth}, the two halves of C's
     * {@code p->stat_birth[stat]}.
     */
    @Nested
    @DisplayName("Player.setStatBirth / getStatBirth")
    class SetAndGetStatBirth {

        /**
         * The ordinary path: a stat rolled at birth is stored and read back for that stat. Sixteen is
         * a reachable {@code 5 + 1d3 + 1d4 + 1d5} roll under the plain roller
         * ({@code player-birth.c:256}).
         */
        @Test
        @DisplayName("stores the value it is given")
        void storesTheValue() {
            player.setStatBirth(Stats.STAT_STR, 16);
            assertEquals(16, player.getStatBirth(Stats.STAT_STR));
        }

        /**
         * Each of the five stats is its own slot, exactly as C's array indexes by stat. A write to
         * one must leave the other four unread rather than aliasing a single value across all five.
         */
        @Test
        @DisplayName("each stat is read from its own slot")
        void statsAreIndependent() {
            player.setStatBirth(Stats.STAT_STR, 18);
            player.setStatBirth(Stats.STAT_INT, 10);
            player.setStatBirth(Stats.STAT_WIS, 9);
            player.setStatBirth(Stats.STAT_DEX, 14);
            player.setStatBirth(Stats.STAT_CON, 17);

            assertEquals(18, player.getStatBirth(Stats.STAT_STR));
            assertEquals(10, player.getStatBirth(Stats.STAT_INT));
            assertEquals(9, player.getStatBirth(Stats.STAT_WIS));
            assertEquals(14, player.getStatBirth(Stats.STAT_DEX));
            assertEquals(17, player.getStatBirth(Stats.STAT_CON));
        }

        /**
         * Writing twice keeps the second value. Re-rolling under the plain roller and every stat
         * purchase under the point roller overwrite a previously rolled birth value in exactly this
         * way.
         */
        @Test
        @DisplayName("the last write wins")
        void lastWriteWins() {
            player.setStatBirth(Stats.STAT_CON, 18);
            player.setStatBirth(Stats.STAT_CON, 8);
            assertEquals(8, player.getStatBirth(Stats.STAT_CON));
        }

        /**
         * No clamping in either direction. C's array write does not clamp, and nor does the port: the
         * shipped ceiling is {@code 18 + 100} ({@code player.c:161}), but nothing here enforces it, so
         * a value above or below the shipped range must come back unchanged.
         */
        @Test
        @DisplayName("does not clamp")
        void doesNotClamp() {
            player.setStatBirth(Stats.STAT_DEX, 3);
            assertEquals(3, player.getStatBirth(Stats.STAT_DEX));
            player.setStatBirth(Stats.STAT_DEX, 118);
            assertEquals(118, player.getStatBirth(Stats.STAT_DEX));
            player.setStatBirth(Stats.STAT_DEX, 0);
            assertEquals(0, player.getStatBirth(Stats.STAT_DEX));
        }

        /**
         * The birth copy and the maximal value are separate storage, even though both start equal at
         * birth: draining a stat in play moves {@link Player#getMaxStatValue} but must leave
         * {@link Player#getStatBirth} exactly where it started, or the port would lose the roller's
         * undo record the moment a character stepped on a trap.
         */
        @Test
        @DisplayName("does not move when the maximal value is drained")
        void survivesMaxStatDrain() {
            player.setStatBirth(Stats.STAT_STR, 17);
            player.setStatMax(Stats.STAT_STR, 17);

            player.setStatMax(Stats.STAT_STR, 15);

            assertEquals(17, player.getStatBirth(Stats.STAT_STR));
            assertNotEquals(player.getStatBirth(Stats.STAT_STR), player.getMaxStatValue(Stats.STAT_STR));
        }

        /**
         * A stat never written reads back {@code 0}, matching C's {@code memset}-zeroed
         * {@code stat_birth[]} ({@code player-birth.c:406}) rather than throwing on a missing map
         * entry.
         */
        @Test
        @DisplayName("an unset stat reads back 0, matching C's zeroed struct")
        void unsetStatReadsZero() {
            assertEquals(0, player.getStatBirth(Stats.STAT_STR));
        }

        /**
         * The two sentinels sit outside the zero-fill loop, the same as every other real-stats-only
         * map on {@link Player}, so they still throw on the auto-unboxing rather than reading 0.
         */
        @Test
        @DisplayName("the sentinels have no slot to read")
        void sentinelsThrow() {
            assertThrows(NullPointerException.class, () -> player.getStatBirth(Stats.STAT_NONE));
            assertThrows(NullPointerException.class, () -> player.getStatBirth(Stats.STAT_MAX));
        }
    }
}
