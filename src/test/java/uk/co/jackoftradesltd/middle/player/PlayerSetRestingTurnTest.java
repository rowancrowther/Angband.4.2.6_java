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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.co.jackoftradesltd.testsupport.SeededPlayerRegistry;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests {@link Player#setRestingTurn(int)} - the port of the plain field write C makes to
 * {@code p->resting_turn} ({@code player.h:554}, {@code uint32_t}) both at birth,
 * {@code player_generate}'s {@code p->resting_turn = 0} ({@code player-birth.c:450}), and on load,
 * {@code rd_u32b(&player->resting_turn)} ({@code load.c:834}). Neither call site validates the value,
 * so this is a direct-write check rather than a behavioural one, following
 * {@link PlayerSetOptionsTest}'s pattern for the sibling {@code setOptions} setter.
 *
 * <p>{@code restingTurn} has no getter, so the field is read back by reflection, as
 * {@link PlayerRestingCountTest} does for the unrelated {@code PlayerUpkeep.restingCounter} field -
 * the two are easily confused by name but port different C fields ({@code p->resting_turn}, a
 * lifetime total, versus {@code p->upkeep->resting}, the live countdown).
 *
 * <p>Class PlayerSetRestingTurnTest coded on 260903, commented in full on 260903.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class PlayerSetRestingTurnTest {

    /**
     * The character whose resting-turn field is written, fresh for each test.
     */
    private Player player;

    /**
     * Builds a new character.
     */
    @BeforeEach
    void build() {
        player = new Player();
    }

    /**
     * Reads the private field back, since there is no getter to call.
     *
     * @return the value currently stored in {@code restingTurn}
     * @throws Exception if the field cannot be reached
     */
    private int restingTurn() throws Exception {
        Field field = Player.class.getDeclaredField("restingTurn");
        field.setAccessible(true);
        return field.getInt(player);
    }

    /**
     * A fresh player's resting-turn count is zero, matching the field's default and
     * {@code player_generate}'s explicit reset.
     *
     * @throws Exception if the reflection fails
     */
    @Test
    @DisplayName("a new player's resting-turn count is zero")
    void newPlayerIsZero() throws Exception {
        assertEquals(0, restingTurn());
    }

    /**
     * The ordinary path: a value handed in comes back out unchanged, as C's bare assignment gives
     * no room for anything else.
     *
     * @throws Exception if the reflection fails
     */
    @Test
    @DisplayName("stores the given value unchanged")
    void storesValueUnchanged() throws Exception {
        player.setRestingTurn(42);
        assertEquals(42, restingTurn());
    }

    /**
     * Birth's own reset - {@code p->resting_turn = 0} - is the same call as any other write, so
     * zero is not special-cased.
     *
     * @throws Exception if the reflection fails
     */
    @Test
    @DisplayName("zero writes like any other value")
    void zeroIsAnOrdinaryWrite() throws Exception {
        player.setRestingTurn(7);
        player.setRestingTurn(0);
        assertEquals(0, restingTurn());
    }

    /**
     * A second call replaces the first outright, matching the load path overwriting whatever the
     * freshly constructed player held.
     *
     * @throws Exception if the reflection fails
     */
    @Test
    @DisplayName("a second write replaces the first")
    void secondWriteReplaces() throws Exception {
        player.setRestingTurn(5);
        player.setRestingTurn(9);
        assertEquals(9, restingTurn());
    }

    /**
     * C's field is {@code uint32_t}; the port widens to a signed {@code int}. A value at the top of
     * the unsigned 32-bit range does not fit in a signed int and is read back by
     * {@code rd_u32b} into storage that {@link Player#setRestingTurn(int)} then takes bit-for-bit, so
     * it must survive as the equivalent negative {@code int} rather than being clamped or throwing.
     *
     * @throws Exception if the reflection fails
     */
    @Test
    @DisplayName("a uint32_t value beyond Integer.MAX_VALUE survives as its bit-for-bit int")
    void unsignedOverflowSurvivesBitForBit() throws Exception {
        long unsigned = 3_000_000_000L;
        player.setRestingTurn((int) unsigned);
        assertEquals((int) unsigned, restingTurn());
        assertEquals(-1_294_967_296, restingTurn());
    }
}
