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
import uk.co.jackoftrades.channel.utils.Flag;
import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;
import uk.co.jackoftrades.middle.player.enums.PlayerFlag;

import uk.co.jackoftrades.testsupport.SeededPlayerRegistry;

import java.lang.reflect.Field;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link Player#setAge}, {@link PlayerRace#getBaseAge} and {@link PlayerRace#getModAge} — the
 * three pieces C's {@code get_ahw} needs to roll a starting age.
 *
 * <p>All three are storage, so nothing is proved by reading a value back on its own. What is worth
 * pinning is the arithmetic they exist to serve, and the expected numbers here come from the C, not
 * from the port: the birth roll is
 *
 * <pre>{@code
 * p->age = p->race->b_age + randint1(p->race->m_age);   // player-birth.c:356
 * }</pre>
 *
 * <p>and the two race fields are the pair parsed from the {@code age:base:mod} line of
 * {@code p_race.txt} ({@code init.c:2694-2700}, registered at {@code init.c:2808}). The shipped
 * values used below are read from that file: Human {@code 14:6}, Half-Orc {@code 11:4} (the
 * youngest), Elf {@code 75:75} (the widest spread), and High-Elf {@code 100:30} (the oldest).
 *
 * <p>{@code randint1} returns 1..m inclusive, never zero, so the starting age is
 * {@code b_age + 1 .. b_age + m_age}. That inclusive lower bound is the one thing an
 * {@code m_age}-sized roll could plausibly get wrong in the port, so it is stated here as the
 * contract the getters have to feed.
 *
 * <p>{@code setAge} is checked for what it does <em>not</em> do as much as what it does: C keeps no
 * clamp on {@code p->age} anywhere — the debug build writes 1 ({@code wiz-debug.c:31}) and the
 * stats collector writes 14 ({@code main-stats.c:471}) straight over a rolled value — so the port
 * must accept any int, including zero and negatives, without adjusting it.
 *
 * <p>Class PlayerAgeTest coded on 260902, commented in full on 260902.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class PlayerAgeTest {

    /**
     * The player under test, fresh for each test since the age is mutable.
     */
    private Player player;

    /**
     * Builds a race carrying the given age pair and nothing else this test reads.
     *
     * @param name    the race's display name
     * @param baseAge C's {@code b_age}
     * @param modAge  C's {@code m_age}
     * @return the race
     */
    private static PlayerRace race(String name, int baseAge, int modAge) {
        return new PlayerRace(name, 0, 10, 100, baseAge, modAge, 72, 6, 180, 25, 0, null,
                Map.of(), Map.of(), new Flag<>(ObjectFlag.class), new Flag<>(PlayerFlag.class),
                null, Map.of());
    }

    /**
     * A new player, as the constructor leaves one.
     */
    @BeforeEach
    void newPlayer() {
        player = new Player();
    }

    /**
     * Reads the player's private {@code age} field, which has no getter yet.
     *
     * @return the stored age in years
     * @throws Exception if the field cannot be reached
     */
    private int age() throws Exception {
        Field field = Player.class.getDeclaredField("age");
        field.setAccessible(true);
        return field.getInt(player);
    }

    /**
     * {@code setAge}, the write half of C's {@code p->age}.
     */
    @Nested
    @DisplayName("Player.setAge")
    class SetAge {

        /**
         * A new player is age zero: C zeroes the whole player struct before birth, and the port's
         * default {@code int} agrees.
         *
         * @throws Exception if the field cannot be reached
         */
        @Test
        @DisplayName("a new player is age zero")
        void newPlayerIsZero() throws Exception {
            assertEquals(0, age());
        }

        /**
         * The ordinary path: an age rolled at birth is stored as given. Fourteen plus a roll of one
         * is the youngest a Human can be under {@code player-birth.c:356}.
         *
         * @throws Exception if the field cannot be reached
         */
        @Test
        @DisplayName("stores the value it is given")
        void storesTheValue() throws Exception {
            player.setAge(15);
            assertEquals(15, age());
        }

        /**
         * Writing twice keeps the second value. C's birth code overwrites a rolled age whenever the
         * player rerolls, and {@code load_roller_data} ({@code player-birth.c:196}) restores a saved
         * one over whatever is there.
         *
         * @throws Exception if the field cannot be reached
         */
        @Test
        @DisplayName("the last write wins")
        void lastWriteWins() throws Exception {
            player.setAge(130);
            player.setAge(1);
            assertEquals(1, age());
        }

        /**
         * No clamping in either direction. C applies none, and the port must not invent any: the
         * debug build's {@code player->age = 1} would be silently corrected by a lower bound, and a
         * value above the shipped maximum of 130 would be corrected by an upper one.
         *
         * @throws Exception if the field cannot be reached
         */
        @Test
        @DisplayName("does not clamp")
        void doesNotClamp() throws Exception {
            player.setAge(0);
            assertEquals(0, age());
            player.setAge(-5);
            assertEquals(-5, age());
            player.setAge(30000);
            assertEquals(30000, age());
        }
    }

    /**
     * {@code getBaseAge} and {@code getModAge}, the pair the birth roll reads.
     */
    @Nested
    @DisplayName("PlayerRace age fields")
    class RaceAgeFields {

        /**
         * The shipped values come back as the constructor was given them, and the two do not cross —
         * a swapped pair would compile and read plausibly, and for Elf ({@code 75:75}) it would not
         * even show up.
         */
        @Test
        @DisplayName("base and mod are reported separately")
        void baseAndModAreSeparate() {
            PlayerRace human = race("Human", 14, 6);
            assertEquals(14, human.getBaseAge());
            assertEquals(6, human.getModAge());

            PlayerRace highElf = race("High-Elf", 100, 30);
            assertEquals(100, highElf.getBaseAge());
            assertEquals(30, highElf.getModAge());
        }

        /**
         * The Elf's {@code 75:75} is the case where a crossed pair hides, so it is asserted on its
         * own: both fields hold 75, and the race that can be oldest is not the one with the widest
         * spread.
         */
        @Test
        @DisplayName("Elf has an equal base and spread")
        void elfBaseEqualsSpread() {
            PlayerRace elf = race("Elf", 75, 75);
            assertEquals(75, elf.getBaseAge());
            assertEquals(elf.getBaseAge(), elf.getModAge());
        }

        /**
         * The pair drives {@code b_age + randint1(m_age)}, so the reachable ages are
         * {@code b_age + 1 .. b_age + m_age} inclusive at both ends. Every roll {@code randint1} can
         * return is walked here for the Half-Orc ({@code 11:4}), giving 12, 13, 14 and 15 — the base
         * of 11 is itself unreachable, because {@code randint1} never returns zero.
         *
         * @throws Exception if the field cannot be reached
         */
        @Test
        @DisplayName("feeds the birth roll's inclusive range")
        void feedsBirthRollRange() throws Exception {
            PlayerRace halfOrc = race("Half-Orc", 11, 4);

            for (int roll = 1; roll <= halfOrc.getModAge(); roll++) {
                player.setAge(halfOrc.getBaseAge() + roll);
                assertEquals(11 + roll, age());
                assertTrue(age() > halfOrc.getBaseAge());
            }

            player.setAge(halfOrc.getBaseAge() + 1);
            assertEquals(12, age());
            player.setAge(halfOrc.getBaseAge() + halfOrc.getModAge());
            assertEquals(15, age());
        }
    }
}
