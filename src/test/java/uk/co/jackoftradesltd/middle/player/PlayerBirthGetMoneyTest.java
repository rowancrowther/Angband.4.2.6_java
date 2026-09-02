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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.co.jackoftradesltd.middle.game.globals.GameConstants;
import uk.co.jackoftradesltd.middle.game.globals.data.CarryCapData;
import uk.co.jackoftradesltd.middle.game.globals.data.GameConstantsData;
import uk.co.jackoftradesltd.middle.game.globals.data.PlayerData;

import uk.co.jackoftradesltd.testsupport.SeededPlayerRegistry;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link PlayerBirth#getMoney}, the port of C's {@code get_money}
 * ({@code player-birth.c:391-394}), together with the three accessors it drives —
 * {@link Player#getAU()}, {@link Player#setAU(int)} and {@link Player#setAUBirth(int)}.
 *
 * <p>The whole of the C is one chained assignment:
 *
 * <pre>{@code
 * static void get_money(struct player *p)
 * {
 *     p->au = p->au_birth = z_info->start_gold;
 * }
 * }</pre>
 *
 * <p>so the properties worth pinning are that <em>both</em> fields are written, that they are
 * written with the same figure, and that the figure is the data file's rather than a constant baked
 * into the port. Every expected number below is derived from the C and its data: the shipped
 * {@code constants.txt} carries {@code player:start-gold:600} (line 201), and {@code z_info} is
 * filled from that file at {@code init.c:693}. The tests seed the constants table by hand for that
 * reason — a test reading the port's own value back would prove only that a field exists.
 *
 * <p>C's chain assigns right to left, writing the birth copy first, where the port writes the
 * working purse first and reads it back for the birth copy. Neither field is read while the other
 * is being written, so the two orders are indistinguishable; what a test <em>can</em> tell apart is
 * a port that writes only one field, or that rolls the two apart, and both of those are checked.
 *
 * <p>The gold is not final at this point. {@code get_money} runs from
 * {@code do_cmd_accept_character} ({@code player-birth.c:1256}) and {@code player_outfit} runs
 * later in the same function ({@code player-birth.c:1298}), subtracting each starting item's
 * {@code object_value_real} from the working purse and flooring the result at zero
 * ({@code player-birth.c:655}, {@code player-birth.c:663}). That floor belongs to the outfitting,
 * not to the assignment, so {@code setAU} is checked for accepting a negative rather than
 * correcting one — the port must not move the clamp into the accessor.
 *
 * <p>The two fields part company afterwards, and that is the point of there being two. Play moves
 * the working purse only: gold picked up ({@code cmd-pickup.c:117}), a sale
 * ({@code store.c:1919}), a purchase ({@code store.c:1700}), a thieving monster taking
 * {@code au / 10 + randint1(25)} ({@code mon-blows.c:797}). The birth copy is written by birth and
 * by the save loader ({@code load.c:737}) and by nothing else, so it still reads 600 after a game.
 *
 * <p>Class PlayerBirthGetMoneyTest coded on 260902, commented in full on 260902.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class PlayerBirthGetMoneyTest {

    /**
     * The shipped {@code player:start-gold} figure ({@code constants.txt:201}).
     */
    private static final int START_GOLD = 600;

    /**
     * The birth code under test. C's {@code get_money} is a free function; the port hangs it off an
     * instance, which the test has to supply.
     */
    private final PlayerBirth birth = new PlayerBirth();

    /**
     * The character being born, fresh for each test.
     */
    private Player player;

    /**
     * The constants table as it was before the test replaced it.
     */
    private Object savedConstants;

    /**
     * The constants holder, made accessible.
     *
     * @return the field
     * @throws Exception if it cannot be reached
     */
    private static Field constantsField() throws Exception {
        Field field = GameConstants.class.getDeclaredField("data");
        field.setAccessible(true);
        return field;
    }

    /**
     * Puts a constants table in place carrying the given starting gold, plus the carry-cap figures
     * a {@code Player} needs to be constructed at all.
     *
     * @param startGold the {@code player:start-gold} figure to publish
     * @throws Exception if the field cannot be reached
     */
    private static void seedConstants(int startGold) throws Exception {
        constantsField().set(null, new GameConstantsData(
                null, null, null, null, null,
                new CarryCapData(23, 10, 40, 5, 16),
                null, null,
                new PlayerData(20, 20, startGold, 5000),
                null, null, null, null, null, null, null, null));
    }

    /**
     * Reads the player's private {@code auBirth} field, which has no getter.
     *
     * @return the stored birth gold
     * @throws Exception if the field cannot be reached
     */
    private int auBirth() throws Exception {
        Field field = Player.class.getDeclaredField("auBirth");
        field.setAccessible(true);
        return field.getInt(player);
    }

    /**
     * Publishes the shipped starting gold and builds a new character.
     *
     * @throws Exception if a fixture field cannot be reached
     */
    @BeforeEach
    void seedAndBuild() throws Exception {
        savedConstants = constantsField().get(null);
        seedConstants(START_GOLD);
        player = new Player();
    }

    /**
     * Puts the constants table back, so a class running after this one finds what it expects.
     *
     * @throws Exception if the field cannot be reached
     */
    @AfterEach
    void restoreConstants() throws Exception {
        constantsField().set(null, savedConstants);
    }

    /**
     * {@code getMoney}, the port of C's {@code get_money}.
     */
    @Nested
    @DisplayName("PlayerBirth.getMoney")
    class GetMoney {

        /**
         * The ordinary path: a character born under the shipped data has 600 gold, and 600 recorded
         * as the sum they were born with.
         *
         * @throws Exception if the birth field cannot be reached
         */
        @Test
        @DisplayName("gives the shipped starting gold to both purses")
        void givesShippedStartGold() throws Exception {
            birth.getMoney(player);
            assertEquals(600, player.getAU());
            assertEquals(600, auBirth());
        }

        /**
         * A new character starts at zero — C zeroes the player struct before birth — so the call is
         * doing the work rather than agreeing with a default. Without this, a port that wrote
         * nothing at all would pass a test asserting only the post-condition on a struct that
         * happened to be pre-loaded.
         *
         * @throws Exception if the birth field cannot be reached
         */
        @Test
        @DisplayName("moves both fields off their zero default")
        void movesBothOffZero() throws Exception {
            assertEquals(0, player.getAU());
            assertEquals(0, auBirth());

            birth.getMoney(player);
            assertNotEquals(0, player.getAU());
            assertNotEquals(0, auBirth());
        }

        /**
         * One figure lands in both fields, which is what C's chained assignment guarantees. Asserting
         * them equal to each other as well as to the constant catches a port that read the data file
         * twice, or that filled the birth copy from somewhere else.
         *
         * @throws Exception if the birth field cannot be reached
         */
        @Test
        @DisplayName("writes the same figure to both fields")
        void writesOneFigureToBoth() throws Exception {
            birth.getMoney(player);
            assertEquals(player.getAU(), auBirth());
        }

        /**
         * The figure comes from {@code player:start-gold} and not from a literal in the port. A
         * different data file gives a different starting purse, which is the whole reason
         * {@code z_info} is consulted rather than a {@code #define} used.
         *
         * @throws Exception if a fixture field cannot be reached
         */
        @Test
        @DisplayName("reads the figure from the constants table")
        void readsFromTheConstantsTable() throws Exception {
            seedConstants(1234);
            birth.getMoney(player);
            assertEquals(1234, player.getAU());
            assertEquals(1234, auBirth());
        }

        /**
         * A data file may legally say zero — the constant is C's {@code uint16_t} and nothing in
         * {@code init.c} imposes a floor — and a character born with no money is a playable, if
         * unkind, start. The port must publish the zero rather than substituting a default.
         *
         * @throws Exception if a fixture field cannot be reached
         */
        @Test
        @DisplayName("passes a zero starting gold through unchanged")
        void passesZeroThrough() throws Exception {
            seedConstants(0);
            birth.getMoney(player);
            assertEquals(0, player.getAU());
            assertEquals(0, auBirth());
        }

        /**
         * The upper boundary the C types set: {@code start_gold} is {@code uint16_t}
         * ({@code init.h:144}), so 65535 is the largest figure a data file can express, and both
         * fields are {@code int32_t} and hold it comfortably. This is the case that would break a
         * port holding either field as a {@code short}.
         *
         * @throws Exception if a fixture field cannot be reached
         */
        @Test
        @DisplayName("carries the largest figure the C constant can hold")
        void carriesUint16Max() throws Exception {
            seedConstants(65535);
            birth.getMoney(player);
            assertEquals(65535, player.getAU());
            assertEquals(65535, auBirth());
            assertTrue(player.getAU() > Short.MAX_VALUE);
        }

        /**
         * Called twice, it produces the same result — there is no roll here and no accumulation. The
         * point-based roller writes the birth copy itself while the character is being built
         * ({@code player-birth.c:694}), and accepting the character calls this afterwards, so this
         * has to overwrite rather than add to whatever it finds.
         *
         * @throws Exception if the birth field cannot be reached
         */
        @Test
        @DisplayName("overwrites whatever the fields already held")
        void overwritesRatherThanAccumulates() throws Exception {
            player.setAU(50);
            player.setAUBirth(900);

            birth.getMoney(player);
            assertEquals(600, player.getAU());
            assertEquals(600, auBirth());
        }

        /**
         * Two characters born from the same table get the same purse, and one being born does not
         * disturb the other. The gold lives on the player, not in a static.
         *
         * @throws Exception if a fixture field cannot be reached
         */
        @Test
        @DisplayName("gives each character their own purse")
        void eachCharacterHasTheirOwnPurse() throws Exception {
            Player other = new Player();
            birth.getMoney(player);
            assertEquals(0, other.getAU());

            birth.getMoney(other);
            assertEquals(600, other.getAU());
            assertEquals(600, player.getAU());

            other.setAU(0);
            assertEquals(600, player.getAU());
        }
    }

    /**
     * {@code setAU} and {@code getAU}, the two halves of C's {@code p->au}.
     */
    @Nested
    @DisplayName("Player.setAU / getAU")
    class WorkingPurse {

        /**
         * A new character is penniless, matching the zeroed struct C starts from.
         */
        @Test
        @DisplayName("a new player has no gold")
        void newPlayerIsZero() {
            assertEquals(0, player.getAU());
        }

        /**
         * The ordinary path: the value stored is the value read back.
         */
        @Test
        @DisplayName("stores the value it is given")
        void storesTheValue() {
            player.setAU(600);
            assertEquals(600, player.getAU());
        }

        /**
         * No clamping in either direction. {@code player_outfit} can drive the purse below zero
         * before its own sanity check pulls it back up ({@code player-birth.c:655},
         * {@code player-birth.c:663}), so an accessor that refused a negative would floor the total
         * at the wrong moment and leave the character with gold the C would have taken away.
         */
        @Test
        @DisplayName("does not clamp")
        void doesNotClamp() {
            player.setAU(0);
            assertEquals(0, player.getAU());
            player.setAU(-40);
            assertEquals(-40, player.getAU());
            player.setAU(10000000);
            assertEquals(10000000, player.getAU());
        }

        /**
         * The field is as wide as C's {@code int32_t}, which the wizard command says out loud when it
         * clamps a typed amount to {@code (1 << 31) - 1} ({@code cmd-wizard.c:1240}). The wizard
         * "get rich" command's own million ({@code cmd-wizard.c:425}) and the debug ten million
         * ({@code player-util.c:295}) both have to fit.
         */
        @Test
        @DisplayName("holds the whole int32 range C allows")
        void holdsTheInt32Range() {
            player.setAU(1000000);
            assertEquals(1000000, player.getAU());
            player.setAU(Integer.MAX_VALUE);
            assertEquals(Integer.MAX_VALUE, player.getAU());
        }

        /**
         * The arithmetic play does to the purse, in C's units. A thieving monster takes
         * {@code au / 10 + randint1(25)}, capped at what is there ({@code mon-blows.c:797-800}); the
         * division truncates, so a purse of 600 loses 60 plus the roll and a purse of 9 loses none of
         * the tenth. A field held in anything but whole gold pieces would pass every read-back test
         * above and get this wrong.
         */
        @Test
        @DisplayName("feeds the theft arithmetic in whole gold pieces")
        void feedsTheftArithmetic() {
            player.setAU(600);
            assertEquals(60, player.getAU() / 10);

            player.setAU(9);
            assertEquals(0, player.getAU() / 10);

            player.setAU(50000);
            assertEquals(5000, player.getAU() / 10);
            assertEquals(2500, player.getAU() / 20);
        }

        /**
         * Spending, as the stores do it: the price is compared against the purse and then subtracted
         * ({@code store.c:1693}, {@code store.c:1700}). Buying everything affordable leaves exactly
         * the remainder, which for 600 gold and a 250-gold item is 100.
         */
        @Test
        @DisplayName("supports the store's compare-then-subtract")
        void supportsStoreSpending() {
            player.setAU(600);
            int price = 250;
            assertTrue(player.getAU() >= price);
            player.setAU(player.getAU() - price);
            player.setAU(player.getAU() - price);
            assertEquals(100, player.getAU());
            assertTrue(player.getAU() < price);
        }
    }

    /**
     * {@code setAUBirth}, the write half of C's {@code p->au_birth}.
     */
    @Nested
    @DisplayName("Player.setAUBirth")
    class BirthPurse {

        /**
         * A new character's birth copy is zero, matching the zeroed struct.
         *
         * @throws Exception if the field cannot be reached
         */
        @Test
        @DisplayName("a new player has no birth gold")
        void newPlayerIsZero() throws Exception {
            assertEquals(0, auBirth());
        }

        /**
         * The two purses are separate storage. Writing the birth copy must leave the working total
         * alone, or the quickstart record and the live purse would be one field wearing two names.
         *
         * @throws Exception if the field cannot be reached
         */
        @Test
        @DisplayName("does not touch the working purse")
        void doesNotTouchWorkingPurse() throws Exception {
            player.setAU(600);
            player.setAUBirth(1200);
            assertEquals(600, player.getAU());
            assertEquals(1200, auBirth());
            assertNotEquals(player.getAU(), auBirth());
        }

        /**
         * The reverse direction, and the case the field exists for: a whole game's worth of spending
         * and looting moves the working purse and leaves the birth copy where birth put it, so it
         * can still be read out to a saved character afterwards ({@code player-birth.c:156}).
         *
         * @throws Exception if the field cannot be reached
         */
        @Test
        @DisplayName("survives a game's worth of writes to the working purse")
        void survivesLaterWorkingWrites() throws Exception {
            birth.getMoney(player);

            player.setAU(player.getAU() - 250);
            player.setAU(player.getAU() + 4000);
            player.setAU(0);

            assertEquals(0, player.getAU());
            assertEquals(600, auBirth());
        }

        /**
         * Quickstart's round trip, which is where the two fields visibly diverge. C reads the birth
         * copy out to the saved character and, coming back in, restores only the birth copy from it
         * while re-opening the working purse from the data file —
         * {@code player->au_birth = saved->au; player->au = z_info->start_gold}
         * ({@code player-birth.c:199-200}). So a character quickstarted from one whose birth copy
         * was 1200 keeps that 1200 on record and still starts play with the shipped 600; this is
         * the opposite of the height and weight, which quickstart restores to both fields.
         *
         * @throws Exception if a fixture field cannot be reached
         */
        @Test
        @DisplayName("quickstart restores the birth copy but re-reads the working purse")
        void quickstartRestoresBirthCopyOnly() throws Exception {
            player.setAUBirth(1200);
            int saved = auBirth();

            Player next = new Player();
            next.setAUBirth(saved);
            next.setAU(GameConstants.getPlayerStartGold());

            assertEquals(600, next.getAU());
            assertNotEquals(next.getAU(), 1200);

            Field field = Player.class.getDeclaredField("auBirth");
            field.setAccessible(true);
            assertEquals(1200, field.getInt(next));
        }

        /**
         * The point-based roller's figure, {@code z_info->start_gold + (50 * points_left)}
         * ({@code player-birth.c:694}), is arithmetic the field has to be able to hold: with the
         * shipped 600 and the twenty points the roller starts with, that is 1600.
         *
         * @throws Exception if the field cannot be reached
         */
        @Test
        @DisplayName("holds the point-based roller's figure")
        void holdsPointBasedFigure() throws Exception {
            int pointsLeft = 20;
            player.setAUBirth(GameConstants.getPlayerStartGold() + (50 * pointsLeft));
            assertEquals(1600, auBirth());

            player.setAUBirth(GameConstants.getPlayerStartGold() + (50 * 0));
            assertEquals(600, auBirth());
        }

        /**
         * No clamping here either — the same reasoning as the working purse, and the save loader
         * writes whatever the file holds ({@code load.c:737}).
         *
         * @throws Exception if the field cannot be reached
         */
        @Test
        @DisplayName("does not clamp")
        void doesNotClamp() throws Exception {
            player.setAUBirth(0);
            assertEquals(0, auBirth());
            player.setAUBirth(-40);
            assertEquals(-40, auBirth());
            player.setAUBirth(Integer.MAX_VALUE);
            assertEquals(Integer.MAX_VALUE, auBirth());
        }
    }
}
