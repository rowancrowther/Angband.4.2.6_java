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
import static org.junit.jupiter.api.Assertions.assertNotSame;

/**
 * Tests {@link PlayerUtils#playerRestingCount(Player)}, the port of C's
 * {@code player_resting_count} in {@code player-util.c}.
 *
 * <p>The C function is one line, {@code return p->upkeep->resting}, so what is worth asserting is not
 * arithmetic but that the value survives the trip unchanged. Three classes of value share the one
 * field and mean quite different things: a positive count is turns of rest still to run, zero means
 * not resting, and the negative {@code REST_} sentinels mean "rest until a condition is met". A read
 * that clamped, took an absolute value, or treated zero and the sentinels alike would look harmless
 * and would break resting silently, so each class is checked for its exact value.
 *
 * <p>The sentinel values are taken from C's {@code player-util.h:53-55} rather than from anything on
 * the Java side, which has no equivalent constants yet: {@code REST_ALL_POINTS} is -1,
 * {@code REST_COMPLETE} -2 and {@code REST_SOME_POINTS} -3. They are asserted as literals here
 * deliberately — the point is that the accessor is transparent to them, not that it recognises them.
 *
 * <p>The method now sits on {@link PlayerUtils} as a static taking the player, where C's
 * {@code player-util.c} keeps it, so it is called directly. The counter itself is still reached by
 * reflection: {@link PlayerUpkeep} exposes it for reading only.
 *
 * <p>Class PlayerRestingCountTest reworked on 260901 for the move of the accessor to
 * {@link PlayerUtils}.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class PlayerRestingCountTest {

    /**
     * The player under test, fresh for each test since the counter is mutable.
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
     * Writes the upkeep's resting counter, which has a getter but no setter.
     *
     * @param count the value to store
     * @throws Exception if the field cannot be reached
     */
    private void setResting(int count) throws Exception {
        Field field = PlayerUpkeep.class.getDeclaredField("restingCounter");
        field.setAccessible(true);
        field.setInt(player.getPlayerUpkeep(), count);
    }

    /**
     * Calls the accessor under test.
     *
     * @return whatever the accessor returned
     */
    private int restingCount() {
        return PlayerUtils.playerRestingCount(player);
    }

    /**
     * A fresh player is not resting, and C's zero-initialised {@code upkeep->resting} reads back as
     * zero rather than as anything the accessor invented.
     *
     * @throws Exception if the reflection fails
     */
    @Test
    @DisplayName("a new player's resting count is zero")
    void newPlayerIsNotResting() throws Exception {
        assertEquals(0, restingCount());
    }

    /**
     * The ordinary path: a positive count is turns remaining, and comes back exactly. The values
     * bracket the {@code % 100} test in {@code player-calcs.c:2694}, which is the only caller that
     * does arithmetic on the result — 100 is a multiple, 1 and 99 are not.
     *
     * @throws Exception if the reflection fails
     */
    @Test
    @DisplayName("a positive count is returned unchanged")
    void positiveCountsAreReturnedUnchanged() throws Exception {
        for (int count : new int[]{1, 99, 100, 101, 9999}) {
            setResting(count);

            assertEquals(count, restingCount(), "resting count " + count);
        }
    }

    /**
     * The three conditional-rest sentinels are negative, and must not be clamped to zero or made
     * positive on the way out — {@code player_resting_is_special} distinguishes them by their exact
     * values, so any alteration would turn "rest until healed" into a turn count.
     *
     * @throws Exception if the reflection fails
     */
    @Test
    @DisplayName("the REST_ sentinels come back as their exact negative values")
    void sentinelsAreReturnedUnchanged() throws Exception {
        setResting(-1);
        assertEquals(-1, restingCount(), "REST_ALL_POINTS");

        setResting(-2);
        assertEquals(-2, restingCount(), "REST_COMPLETE");

        setResting(-3);
        assertEquals(-3, restingCount(), "REST_SOME_POINTS");
    }

    /**
     * C's field is an {@code int16_t}, so its range ends at -32768 and 32767. The accessor widens to
     * {@code int}, and the widening must be a plain one: the extremes read back as themselves rather
     * than wrapping.
     *
     * @throws Exception if the reflection fails
     */
    @Test
    @DisplayName("the sixteen-bit extremes survive the widening")
    void sixteenBitExtremesSurvive() throws Exception {
        setResting(Short.MAX_VALUE);
        assertEquals(32767, restingCount());

        setResting(Short.MIN_VALUE);
        assertEquals(-32768, restingCount());
    }

    /**
     * The accessor reads, and does not consume: C returns the field without touching it, so two calls
     * in a row give the same answer and leave the counter where it was. A decrement hidden in the
     * getter would make resting run out at twice the rate.
     *
     * @throws Exception if the reflection fails
     */
    @Test
    @DisplayName("reading the count does not change it")
    void readingDoesNotConsume() throws Exception {
        setResting(50);

        assertEquals(50, restingCount());
        assertEquals(50, restingCount(), "a second read is unchanged");
        assertEquals(50, player.getPlayerUpkeep().getRestingCounter(), "the counter was not disturbed");
    }

    /**
     * The value comes from the player's own upkeep, not from anywhere shared: C dereferences
     * {@code p->upkeep}, so two players rest independently.
     *
     * @throws Exception if the reflection fails
     */
    @Test
    @DisplayName("the count is read from this player's upkeep")
    void countIsPerPlayer() throws Exception {
        Player other = new Player();
        assertNotSame(player.getPlayerUpkeep(), other.getPlayerUpkeep());

        setResting(25);

        assertEquals(25, restingCount());
        assertEquals(0, other.getPlayerUpkeep().getRestingCounter(), "the other player is unaffected");
        assertEquals(0, PlayerUtils.playerRestingCount(other), "and reads back zero of its own");
    }
}
