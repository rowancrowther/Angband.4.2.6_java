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

import uk.co.jackoftradesltd.testsupport.SeededPlayerRegistry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * Tests {@link Player#setAUBirth} and {@link Player#getAUBirth} — the quickstart copy of C's
 * {@code p->au_birth} ({@code player.h:586}).
 *
 * <p>Both are storage, so nothing is proved by reading a value back on its own. What is worth
 * pinning is where the figure comes from and how it survives play, and the expected numbers here
 * come from the C, not from the port. The plain roller opens it from the shipped starting gold:
 *
 * <pre>{@code
 * p->au = p->au_birth = z_info->start_gold;   // player-birth.c:393, get_money
 * }</pre>
 *
 * <p>which the shipped {@code constants.txt} sets to 600. The point-based roller writes it a second
 * way while the character is being built, so that unspent stat points show as gold:
 *
 * <pre>{@code
 * player->au_birth = z_info->start_gold + (50 * points_left_local);   // player-birth.c:694, recalculate_stats
 * }</pre>
 *
 * <p>Neither figure ever goes negative in the shipped data — {@code points_left_local} does not run
 * below zero — but nothing in either language stops a caller writing one that does, so the port must
 * accept it unclamped, same as {@link Player#setAU}.
 *
 * <p>C holds the field as {@code int32_t}, the same width the port's {@code int} field and
 * {@link Player#setAUBirth} use. {@link Player#getAUBirth} returns {@code long} rather than
 * {@code int}, widened only at that one accessor so its result can be handed straight to
 * {@link Birther#setAu(long)} at the {@code PlayerBirth.saveRollerData} boundary; the widening is
 * lossless for every {@code int}, so no test here depends on the return type being wider than the
 * field.
 *
 * <p>Class PlayerAUBirthTest coded on 260906, commented in full on 260906.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class PlayerAUBirthTest {

    /**
     * The player under test, fresh for each test since the birth gold is mutable.
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
     * {@code setAUBirth} and {@code getAUBirth}, the two halves of C's {@code p->au_birth}.
     */
    @Nested
    @DisplayName("Player.setAUBirth / getAUBirth")
    class SetAndGetAUBirth {

        /**
         * A new player has birth gold zero: C zeroes the whole player struct before birth, and the
         * port's default {@code int} agrees.
         */
        @Test
        @DisplayName("a new player has birth gold zero")
        void newPlayerIsZero() {
            assertEquals(0L, player.getAUBirth());
        }

        /**
         * The plain roller's path: the shipped starting gold, 600, is stored as given.
         */
        @Test
        @DisplayName("stores the plain roller's starting gold")
        void storesPlainRollerGold() {
            player.setAUBirth(600);
            assertEquals(600L, player.getAUBirth());
        }

        /**
         * The point-based roller's path: {@code start_gold + 50 * points_left}. Five unspent points
         * over the 600 base gives 850, the figure {@code recalculate_stats} would write for a
         * character that spent fifteen of its twenty birth points.
         */
        @Test
        @DisplayName("stores the point roller's inflated gold")
        void storesPointRollerGold() {
            int pointsLeft = 5;
            player.setAUBirth(600 + 50 * pointsLeft);
            assertEquals(850L, player.getAUBirth());
        }

        /**
         * Writing twice keeps the second value. {@code recalculate_stats} overwrites the figure on
         * every stat purchase while the point roller is in use, and accepting the character afterwards
         * runs {@code get_money}, which overwrites it again with the plain starting sum
         * ({@code player-birth.c:1256} calling {@code player-birth.c:393}).
         */
        @Test
        @DisplayName("the last write wins")
        void lastWriteWins() {
            player.setAUBirth(850);
            player.setAUBirth(600);
            assertEquals(600L, player.getAUBirth());
        }

        /**
         * No clamping in either direction. Neither C writer checks the value it stores, so the port
         * must not invent a floor or a ceiling either.
         */
        @Test
        @DisplayName("does not clamp")
        void doesNotClamp() {
            player.setAUBirth(0);
            assertEquals(0L, player.getAUBirth());
            player.setAUBirth(-5);
            assertEquals(-5L, player.getAUBirth());
            player.setAUBirth(1_000_000);
            assertEquals(1_000_000L, player.getAUBirth());
        }

        /**
         * The birth gold and the birth copy are separate storage, exactly as the working and birth
         * height and weight are. Writing one must leave the other alone, or {@link Player#getAU} and
         * {@link Player#getAUBirth} would be one field wearing two names.
         */
        @Test
        @DisplayName("does not touch the working gold")
        void doesNotTouchWorkingGold() {
            player.setAU(200);
            player.setAUBirth(600);
            assertEquals(200, player.getAU());
            assertEquals(600L, player.getAUBirth());
            assertNotEquals(player.getAU(), player.getAUBirth());
        }

        /**
         * The widening to {@code long} loses nothing: a value the field could not hold in C either —
         * {@code Integer.MAX_VALUE}, the ceiling C's own wizard command clamps a typed amount to
         * ({@code cmd-wizard.c:1240}) — round-trips exactly rather than wrapping or truncating.
         */
        @Test
        @DisplayName("widens to long without losing precision")
        void widensWithoutLoss() {
            player.setAUBirth(Integer.MAX_VALUE);
            assertEquals((long) Integer.MAX_VALUE, player.getAUBirth());
        }
    }
}
