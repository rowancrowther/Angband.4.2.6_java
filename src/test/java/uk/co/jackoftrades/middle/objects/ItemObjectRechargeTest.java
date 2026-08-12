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

package uk.co.jackoftrades.middle.objects;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import uk.co.jackoftrades.middle.game.globals.GameConstants;
import uk.co.jackoftrades.middle.game.globals.data.GameConstantsData;
import uk.co.jackoftrades.middle.game.globals.data.WorldData;
import uk.co.jackoftrades.middle.numerics.Random;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link ItemObject#rechargeTimeout()} and its helper
 * {@link ItemObject#numberCharging()} — the ports of C's {@code recharge_timeout} and
 * {@code number_charging} ({@code obj-util.c:1018-1065}).
 *
 * <p>The pair implements Angband's pooled-timeout model for stacks of rods. A stack of rods is a
 * single object with one {@code timeout} counter shared across {@code number} rods, rather than one
 * counter each. {@code numberCharging} reads that pooled counter back as a rod count by dividing it
 * by the per-rod recharge interval and rounding up, clamped to the stack size; {@code
 * rechargeTimeout} then burns one turn of charge <em>per charging rod</em> and reports whether the
 * rod count dropped, which is the signal that one more rod has come ready.
 *
 * <p>Two consequences of that arithmetic are what these tests mostly pin down:
 *
 * <ul>
 *   <li>the return value is a <em>transition</em>, not a state — it is false on every tick that
 *       merely reduces the timeout, and true only on the tick that crosses a multiple of the
 *       recharge interval;</li>
 *   <li>the clamp to stack size makes a stack whose timeout exceeds {@code number * chargeTime}
 *       drain at only {@code number} per turn, so an over-large timeout is not a shortcut to a
 *       faster recharge.</li>
 * </ul>
 *
 * <p>The fields under test ({@code time}, {@code timeout}, {@code number}) have no setters — they
 * are populated by the parsing constructor, whose {@code String time} argument would drag the ANTLR
 * random-dice parser into a test that is not about parsing. The tests therefore build a blank
 * {@link ItemObject} and inject the three fields reflectively via {@link #rod}, which keeps the
 * coupling to field names in one place.
 */
@DisplayName("ItemObject recharge timeout")
class ItemObjectRechargeTest {

    /**
     * The {@code GameConstants.data} in place before this class replaced it.
     */
    private static Object savedConstants;

    /**
     * Seeds {@code GameConstants.data} with just enough for {@code world:max-depth} to resolve.
     *
     * <p>Needed because {@code numberCharging} evaluates its dice through {@code
     * RandomValueUtils.mBonusCalc}, whose AVERAGE branch computes {@code max * level /
     * GameConstants.getWorldMaxDepth()} unconditionally — so the global constants table is read even
     * for a flat interval with no mBonus term, and an unseeded table throws. Running the whole of
     * {@code GameConstants.init()} for one integer would drag in every data file, so the field is
     * set directly and restored afterwards.
     */
    @BeforeAll
    static void seedWorldMaxDepth() {
        GameConstantsData seed = new GameConstantsData(
                null, null, null, null,
                new WorldData(128, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                null, null, null, null, null, null, null, null, null, null, null, null);
        savedConstants = setStatic(GameConstants.class, "data", seed);
    }

    /**
     * Puts back whatever {@code GameConstants.data} held before {@link #seedWorldMaxDepth()}, so a
     * test class running later in the same JVM sees the state it expects.
     */
    @AfterAll
    static void restoreConstants() {
        setStatic(GameConstants.class, "data", savedConstants);
    }

    /**
     * Writes a private static field, returning its previous value.
     *
     * @param owner the declaring class
     * @param name  the declared field name
     * @param value the value to write
     * @return the value the field held beforehand
     */
    private static Object setStatic(Class<?> owner, String name, Object value) {
        try {
            Field field = owner.getDeclaredField(name);
            field.setAccessible(true);
            Object previous = field.get(null);
            field.set(null, value);
            return previous;
        } catch (ReflectiveOperationException e) {
            throw new AssertionError(owner.getSimpleName() + "." + name
                    + " is no longer settable by reflection", e);
        }
    }

    /**
     * Builds an item standing in for a rod (or a stack of them) with the three fields the recharge
     * arithmetic reads.
     *
     * <p>The recharge interval is given as a flat base value rather than dice, because
     * {@code numberCharging} evaluates {@code time} at {@link
     * uk.co.jackoftrades.middle.numerics.DamageAspect#AVERAGE} and level 0 — where the dice term
     * averages out and the mBonus term vanishes — so a flat base is the interval the code will
     * actually see, stated without indirection.
     *
     * @param chargeTime the per-item recharge interval, or a negative value for "no interval at
     *                   all" (a {@code null} {@code time}, which is how a non-rod is stored)
     * @param timeout    the pooled timeout counter
     * @param number     the stack size
     * @return the constructed item
     */
    private static ItemObject rod(int chargeTime, int timeout, int number) {
        ItemObject item = new ItemObject();
        set(item, "time", chargeTime < 0 ? null : new Random(chargeTime, 0, 0, 0, false));
        set(item, "timeout", timeout);
        set(item, "number", number);
        return item;
    }

    /**
     * Writes a private field on an {@link ItemObject} by reflection.
     *
     * @param item  the item to modify
     * @param name  the declared field name
     * @param value the value to write
     */
    private static void set(ItemObject item, String name, Object value) {
        try {
            Field field = ItemObject.class.getDeclaredField(name);
            field.setAccessible(true);
            field.set(item, value);
        } catch (ReflectiveOperationException e) {
            throw new AssertionError("ItemObject." + name + " is no longer settable by reflection", e);
        }
    }

    @Nested
    @DisplayName("nothing is charging")
    class NothingCharging {

        @Test
        @DisplayName("a ready item reports no charge and stays at zero")
        void readyItem() {
            ItemObject item = rod(10, 0, 1);

            assertEquals(0, item.numberCharging());
            assertFalse(item.rechargeTimeout(), "an item already at timeout 0 gains nothing");
            assertEquals(0, item.getTimeout());
        }

        @Test
        @DisplayName("an item with no recharge interval is left alone")
        void noTimeField() {
            // C reads obj->time as a struct and so cannot see null; the Java guard is the port's
            // own, covering every non-rod whose time was never parsed.
            ItemObject item = rod(-1, 25, 3);

            assertEquals(0, item.numberCharging());
            assertFalse(item.rechargeTimeout());
            assertEquals(25, item.getTimeout(), "timeout must not be touched when time is absent");
        }

        @Test
        @DisplayName("a zero recharge interval is left alone")
        void zeroChargeTime() {
            ItemObject item = rod(0, 25, 3);

            assertEquals(0, item.numberCharging());
            assertFalse(item.rechargeTimeout());
            assertEquals(25, item.getTimeout());
        }

        @Test
        @DisplayName("a negative timeout is treated as ready, not as debt")
        void negativeTimeout() {
            ItemObject item = rod(10, -5, 1);

            assertEquals(0, item.numberCharging());
            assertFalse(item.rechargeTimeout());
            assertEquals(-5, item.getTimeout(), "the guard returns before any decrement");
        }
    }

    @Nested
    @DisplayName("a single rod")
    class SingleRod {

        @Test
        @DisplayName("ticks down one turn at a time without reporting a charge")
        void midRecharge() {
            ItemObject item = rod(10, 5, 1);

            assertEquals(1, item.numberCharging());
            assertFalse(item.rechargeTimeout(), "still charging: the count has not fallen");
            assertEquals(4, item.getTimeout());
        }

        @Test
        @DisplayName("reports the charge on the tick that empties the timeout")
        void finalTick() {
            ItemObject item = rod(10, 1, 1);

            assertTrue(item.rechargeTimeout(), "the rod becomes usable on this tick");
            assertEquals(0, item.getTimeout());
            assertEquals(0, item.numberCharging());
        }

        @Test
        @DisplayName("reports exactly once across a full recharge")
        void wholeRecharge() {
            ItemObject item = rod(10, 10, 1);

            int reports = 0;
            for (int turn = 0; turn < 10; turn++)
                if (item.rechargeTimeout()) reports++;

            assertEquals(1, reports, "a lone rod recharges once, so it is announced once");
            assertEquals(0, item.getTimeout());
            assertFalse(item.rechargeTimeout(), "and nothing more happens afterwards");
        }
    }

    @Nested
    @DisplayName("a stack of rods")
    class Stack {

        /**
         * Pins the pooled-counter arithmetic: with three rods on a ten-turn interval, all three are
         * charging while the timeout is above 20, and the stack burns three turns of charge per game
         * turn — so the transitions land on the ticks that take the timeout to 20 and to 10.
         *
         * @param timeout        the timeout before the tick
         * @param expectedBefore the rod count the tick should start from
         * @param expectedAfter  the timeout the tick should leave behind
         * @param recharged      whether the tick should report a rod coming ready
         */
        @ParameterizedTest(name = "timeout {0} -> {2}, recharged={3}")
        @CsvSource({
                "25, 3, 22, false",
                "24, 3, 21, false",
                "23, 3, 20, true",
                "22, 3, 19, true",
                "21, 3, 18, true",
                "13, 2, 11, false",
                "11, 2,  9, true",
                " 3, 1,  2, false",
                " 1, 1,  0, true"
        })
        @DisplayName("burns one turn of charge per charging rod")
        void perRodDrain(int timeout, int expectedBefore, int expectedAfter, boolean recharged) {
            ItemObject item = rod(10, timeout, 3);

            assertEquals(expectedBefore, item.numberCharging());
            assertEquals(recharged, item.rechargeTimeout());
            assertEquals(expectedAfter, item.getTimeout());
        }

        @Test
        @DisplayName("the charging count is clamped to the stack size")
        void clampedToStackSize() {
            // Timeout 45 would imply five charging rods at a ten-turn interval, but only two exist,
            // so the stack drains at two per turn rather than five. This is the port's own trap: an
            // out-of-range timeout does not recharge faster, it recharges slower per rod.
            ItemObject item = rod(10, 45, 2);

            assertEquals(2, item.numberCharging(), "cannot have more rods charging than rods");
            assertFalse(item.rechargeTimeout());
            assertEquals(43, item.getTimeout());
        }

        @Test
        @DisplayName("the timeout floors at zero rather than going negative")
        void doesNotOvershoot() {
            // Five rods charging but only three turns of charge left: the MIN in the decrement is
            // what stops the timeout running past zero into a state numberCharging reads as "ready".
            ItemObject item = rod(1, 3, 5);

            assertEquals(3, item.numberCharging());
            assertTrue(item.rechargeTimeout());
            assertEquals(0, item.getTimeout(), "timeout is clamped at zero, never negative");
        }

        @Test
        @DisplayName("reports once per rod across a full recharge, at a decelerating rate")
        void oneReportPerRod() {
            // Three rods, ten-turn interval, fully drained: 30 turns of pooled charge. The drain
            // rate is the number still charging, so it starts at three per turn and steps down to
            // two and then one as rods come ready — 18 game turns rather than the 10 a constant
            // three-per-turn drain would take, and three separate announcements along the way.
            ItemObject item = rod(10, 30, 3);

            int reports = 0;
            int turns = 0;
            while (item.getTimeout() > 0) {
                if (item.rechargeTimeout()) reports++;
                turns++;
            }

            assertEquals(3, reports, "one announcement per rod that came ready");
            assertEquals(18, turns, "the stack drains more slowly as fewer rods remain charging");
            assertEquals(0, item.getTimeout());
        }
    }
}