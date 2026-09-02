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
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.player.enums.PlayerFlag;

import uk.co.jackoftradesltd.testsupport.SeededPlayerRegistry;

import java.lang.reflect.Field;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link Player#setHeight}, {@link Player#setHeightBirth}, {@link PlayerRace#getBaseHeight}
 * and {@link PlayerRace#getModHeight} — the four pieces C's {@code get_ahw} needs to roll a
 * starting height.
 *
 * <p>All four are storage, so reading a value back proves nothing on its own. What is worth pinning
 * is the arithmetic they exist to serve, and every expected number below is derived from the C
 * rather than from the port. The birth roll is a single statement assigning to both fields:
 *
 * <pre>{@code
 * p->ht = p->ht_birth = Rand_normal(p->race->base_hgt, p->race->mod_hgt);   // player-birth.c:359
 * }</pre>
 *
 * <p>and the two race fields are the pair parsed from the {@code height:base_hgt:mod_hgt} line of
 * {@code p_race.txt} ({@code init.c:2707-2708}, registered at {@code init.c:2809}). The shipped
 * values used below are read from that file: Human {@code 69:10}, Hobbit {@code 34:4} (the
 * shortest), Half-Orc {@code 64:2} (the narrowest spread) and Half-Troll {@code 90:16} (the tallest
 * and the widest).
 *
 * <p>The unit is inches. The character sheet is the only reader, and it splits the value there with
 * {@code player->ht / 12} and {@code player->ht % 12} ({@code ui-player.c:829}), so the
 * feet-and-inches split is asserted here as the contract the stored value has to satisfy.
 *
 * <p>Unlike the age roll, {@code base_hgt} is a <em>mean</em> and not a floor:
 * {@code Rand_normal(mean, stand)} returns {@code mean} unchanged when {@code stand < 1}
 * ({@code z-rand.c:296}) and otherwise {@code mean ± stand * low / RANDNOR_STD}, with {@code low}
 * in {@code 0 .. 256} and {@code RANDNOR_STD} 64 ({@code z-rand.c:314}). The reachable heights are
 * therefore {@code base_hgt - 4 * mod_hgt .. base_hgt + 4 * mod_hgt}, the base itself is reachable,
 * and roughly half of a race's characters are shorter than it — the opposite of the age pair's
 * strictly-above-base range.
 *
 * <p>{@code setHeight} is checked for what it does <em>not</em> do as much as for what it does: C
 * keeps no clamp on {@code p->ht} anywhere — the stats collector writes 66 straight over a rolled
 * value ({@code main-stats.c:469}) — so the port must accept any int, including zero and negatives,
 * without adjusting it.
 *
 * <p>Class PlayerHeightTest coded on 260902, commented in full on 260902.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class PlayerHeightTest {

    /**
     * The player under test, fresh for each test since both heights are mutable.
     */
    private Player player;

    /**
     * Builds a race carrying the given height pair and nothing else this test reads.
     *
     * @param name       the race's display name
     * @param baseHeight C's {@code base_hgt}
     * @param modHeight  C's {@code mod_hgt}
     * @return the race
     */
    private static PlayerRace race(String name, int baseHeight, int modHeight) {
        return new PlayerRace(name, 0, 10, 100, 14, 6, baseHeight, modHeight, 180, 25, 0, null,
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
     * Reads the player's private {@code htBirth} field, which has no getter yet.
     *
     * @return the stored birth height in inches
     * @throws Exception if the field cannot be reached
     */
    private int htBirth() throws Exception {
        Field field = Player.class.getDeclaredField("htBirth");
        field.setAccessible(true);
        return field.getInt(player);
    }

    /**
     * {@code setHeight}, the write half of C's {@code p->ht}.
     */
    @Nested
    @DisplayName("Player.setHeight")
    class SetHeight {

        /**
         * A new player has height zero: C zeroes the whole player struct before birth, and the
         * port's default {@code int} agrees.
         */
        @Test
        @DisplayName("a new player has height zero")
        void newPlayerIsZero() {
            assertEquals(0, player.getHeight());
        }

        /**
         * The ordinary path: a height rolled at birth is stored as given. Sixty-nine inches is the
         * Human mean, the single most likely value {@code Rand_normal} can return for that race.
         */
        @Test
        @DisplayName("stores the value it is given")
        void storesTheValue() {
            player.setHeight(69);
            assertEquals(69, player.getHeight());
        }

        /**
         * Writing twice keeps the second value. Quickstart overwrites a rolled height with the saved
         * one ({@code player-birth.c:198}), and the save loader writes over it again
         * ({@code load.c:719}).
         */
        @Test
        @DisplayName("the last write wins")
        void lastWriteWins() {
            player.setHeight(90);
            player.setHeight(34);
            assertEquals(34, player.getHeight());
        }

        /**
         * No clamping in either direction. C applies none, and the port must not invent any: a lower
         * bound would silently correct the zero a struct starts at, and an upper bound would correct
         * anything past the shipped maximum.
         */
        @Test
        @DisplayName("does not clamp")
        void doesNotClamp() {
            player.setHeight(0);
            assertEquals(0, player.getHeight());
            player.setHeight(-5);
            assertEquals(-5, player.getHeight());
            player.setHeight(30000);
            assertEquals(30000, player.getHeight());
        }

        /**
         * The stored value is inches, which the character sheet splits into feet and inches with
         * {@code ht / 12} and {@code ht % 12} ({@code ui-player.c:829}). Seventy-two must read as
         * 6'0" and not 6'12" or 5'12", so the boundary either side of a whole foot is walked.
         */
        @Test
        @DisplayName("holds inches, which split into feet and inches")
        void holdsInches() {
            player.setHeight(71);
            assertEquals(5, player.getHeight() / 12);
            assertEquals(11, player.getHeight() % 12);

            player.setHeight(72);
            assertEquals(6, player.getHeight() / 12);
            assertEquals(0, player.getHeight() % 12);

            player.setHeight(34);
            assertEquals(2, player.getHeight() / 12);
            assertEquals(10, player.getHeight() % 12);
        }
    }

    /**
     * {@code setHeightBirth}, the write half of C's {@code p->ht_birth}.
     */
    @Nested
    @DisplayName("Player.setHeightBirth")
    class SetHeightBirth {

        /**
         * A new player has a birth height of zero, and C leans on that: a zero {@code ht_birth} is
         * how it decides no previous character exists to quickstart from
         * ({@code player-birth.c:1061}).
         *
         * @throws Exception if the field cannot be reached
         */
        @Test
        @DisplayName("a new player has a birth height of zero")
        void newPlayerIsZero() throws Exception {
            assertEquals(0, htBirth());
        }

        /**
         * The two heights are separate storage. Writing the birth copy must leave the working height
         * alone, or the quickstart record and the live value would be one field wearing two names.
         *
         * @throws Exception if the field cannot be reached
         */
        @Test
        @DisplayName("does not touch the working height")
        void doesNotTouchWorkingHeight() throws Exception {
            player.setHeight(69);
            player.setHeightBirth(90);
            assertEquals(69, player.getHeight());
            assertEquals(90, htBirth());
            assertNotEquals(player.getHeight(), htBirth());
        }

        /**
         * The reverse direction: writing the working height leaves the birth copy where it was. This
         * is the case that matters in play — the birth height is the height the character was born
         * with, however the working value is later reloaded.
         *
         * @throws Exception if the field cannot be reached
         */
        @Test
        @DisplayName("survives a later write to the working height")
        void survivesLaterWorkingWrite() throws Exception {
            player.setHeightBirth(69);
            player.setHeight(34);
            assertEquals(69, htBirth());
            assertEquals(34, player.getHeight());
        }

        /**
         * The birth idiom itself: C's chained assignment puts one roll into both fields
         * ({@code player-birth.c:359}), which the port spells as two calls sharing a single rolled
         * value. Both must end up holding it.
         *
         * @throws Exception if the field cannot be reached
         */
        @Test
        @DisplayName("the birth assignment leaves both fields equal")
        void birthAssignmentSetsBoth() throws Exception {
            int rolled = 87;
            player.setHeight(rolled);
            player.setHeightBirth(player.getHeight());
            assertEquals(rolled, player.getHeight());
            assertEquals(rolled, htBirth());
        }

        /**
         * No clamping here either — the same reasoning as the working height, and a zero has to
         * stay a zero because C reads it as a flag.
         *
         * @throws Exception if the field cannot be reached
         */
        @Test
        @DisplayName("does not clamp")
        void doesNotClamp() throws Exception {
            player.setHeightBirth(0);
            assertEquals(0, htBirth());
            player.setHeightBirth(-5);
            assertEquals(-5, htBirth());
            player.setHeightBirth(30000);
            assertEquals(30000, htBirth());
        }
    }

    /**
     * {@code getBaseHeight} and {@code getModHeight}, the pair the birth roll reads.
     */
    @Nested
    @DisplayName("PlayerRace height fields")
    class RaceHeightFields {

        /**
         * The shipped values come back as the constructor was given them, and the two do not cross —
         * a swapped pair would compile and read plausibly.
         */
        @Test
        @DisplayName("base and mod are reported separately")
        void baseAndModAreSeparate() {
            PlayerRace human = race("Human", 69, 10);
            assertEquals(69, human.getBaseHeight());
            assertEquals(10, human.getModHeight());

            PlayerRace halfTroll = race("Half-Troll", 90, 16);
            assertEquals(90, halfTroll.getBaseHeight());
            assertEquals(16, halfTroll.getModHeight());
        }

        /**
         * The height pair is not the age pair. A race carrying both must report each from its own
         * field, which is the mistake a copy-paste getter would make invisibly for a race whose
         * numbers happened to be close.
         */
        @Test
        @DisplayName("height does not read the age fields")
        void heightIsNotAge() {
            PlayerRace hobbit = race("Hobbit", 34, 4);
            assertEquals(34, hobbit.getBaseHeight());
            assertEquals(4, hobbit.getModHeight());
            assertEquals(14, hobbit.getBaseAge());
            assertEquals(6, hobbit.getModAge());
        }

        /**
         * The base is the mean of {@code Rand_normal}, so unlike the age base it is itself a
         * reachable height — the offset is zero whenever the binary search lands at index zero
         * ({@code z-rand.c:314}) — and half the distribution lies below it. The Hobbit's 34 inches
         * is asserted as a height a character can actually be born at.
         */
        @Test
        @DisplayName("the base is a reachable height, not a floor")
        void baseIsReachable() {
            PlayerRace hobbit = race("Hobbit", 34, 4);
            player.setHeight(hobbit.getBaseHeight());
            assertEquals(34, player.getHeight());

            player.setHeight(hobbit.getBaseHeight() - 1);
            assertTrue(player.getHeight() < hobbit.getBaseHeight());
        }

        /**
         * The spread is a standard deviation scaled by {@code RANDNOR_STD} of 64, and the search
         * index tops out at 256, so the offset reaches four times {@code mod_hgt} either side of the
         * base ({@code z-rand.c:314}). Walking the extremes gives the Half-Troll {@code 26 .. 154}
         * and the Half-Orc, whose spread of 2 is the narrowest shipped, {@code 56 .. 72}.
         */
        @Test
        @DisplayName("the spread reaches four deviations either side")
        void spreadReachesFourDeviations() {
            PlayerRace halfTroll = race("Half-Troll", 90, 16);
            int maxOffset = halfTroll.getModHeight() * 256 / 64;
            assertEquals(64, maxOffset);
            player.setHeight(halfTroll.getBaseHeight() - maxOffset);
            assertEquals(26, player.getHeight());
            player.setHeight(halfTroll.getBaseHeight() + maxOffset);
            assertEquals(154, player.getHeight());

            PlayerRace halfOrc = race("Half-Orc", 64, 2);
            int orcOffset = halfOrc.getModHeight() * 256 / 64;
            assertEquals(8, orcOffset);
            player.setHeight(halfOrc.getBaseHeight() - orcOffset);
            assertEquals(56, player.getHeight());
            player.setHeight(halfOrc.getBaseHeight() + orcOffset);
            assertEquals(72, player.getHeight());
        }

        /**
         * Every offset the table can produce for the Half-Orc, walked one search index at a time.
         * The integer division in {@code stand * low / RANDNOR_STD} truncates, so a spread of 2
         * yields whole inches only in steps of 32 index positions — the point being that the port's
         * fields feed that arithmetic unscaled, in the same units C uses.
         */
        @Test
        @DisplayName("feeds the offset arithmetic in C's units")
        void feedsOffsetArithmetic() {
            PlayerRace halfOrc = race("Half-Orc", 64, 2);

            for (int low = 0; low <= 256; low += 32) {
                int offset = halfOrc.getModHeight() * low / 64;
                assertEquals(low / 32, offset);

                player.setHeight(halfOrc.getBaseHeight() + offset);
                assertEquals(64 + low / 32, player.getHeight());
            }
        }

        /**
         * A spread below one short-circuits the roll entirely — {@code Rand_normal} returns the mean
         * untouched ({@code z-rand.c:296}). No shipped race does this, but the getter has to be able
         * to report it, so a zero spread is checked to come back as zero rather than being defaulted
         * to something usable.
         */
        @Test
        @DisplayName("reports a spread below one unchanged")
        void reportsZeroSpread() {
            PlayerRace fixed = race("Fixed", 70, 0);
            assertEquals(0, fixed.getModHeight());
            assertEquals(70, fixed.getBaseHeight());

            player.setHeight(fixed.getModHeight() < 1 ? fixed.getBaseHeight() : -1);
            assertEquals(70, player.getHeight());
        }
    }
}
