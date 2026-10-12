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

package uk.co.jackoftradesltd.middle.objects;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.testsupport.CurseFixture;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests {@link CurseData}, the port of C's {@code struct curse_data} ({@code src/object.h}) — the
 * power a curse has on one object and the countdown to its next effect.
 *
 * <p><b>Two fields and four setters would not be worth a test class.</b> What is worth one is that
 * the type is deliberately half-mutable and half value-like, and the two halves have to be exactly
 * the right halves. Equality reads only the power, because that is what
 * {@code curses_are_equal} compares in C and what decides whether two cursed items stack; the
 * timeout is excluded because two identical swords will always be at different points in their
 * countdowns. Mutability is the other half: the curse tick decrements a timeout in place, so the
 * instances handed around have to be shared rather than copied — which is precisely what makes the
 * copy constructor necessary wherever a template's data would otherwise be handed to the objects
 * made from it.
 *
 * <p>Get either half wrong and the failure is quiet. Equality that included the timeout would stop
 * cursed items stacking for a reason the player cannot see; a copy constructor that copied by
 * reference would let one cursed sword count down the template every other sword is made from.
 *
 * <p>Class CurseDataTest coded on 260817, commented in full on 260817, extended on 261012.
 *
 * @author Rowan Crowther
 */
class CurseDataTest {

    /**
     * A minimal curse definition, distinct from every other by identity.
     *
     * @param name the curse's name
     * @return a curse with every other field empty
     */
    private static Curse curse(String name) {
        return CurseFixture.curse(name, java.util.List.of(), 0, null, new Flag<>(ObjectFlag.class), Map.of(),
                Map.of(), 0, 0, 0, java.util.List.of(), new Flag<>(ObjectFlag.class), "", "", 0);
    }

    /**
     * The value half: what {@code equals} and {@code hashCode} read, and what they must not.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("equality")
    class Equality {

        /**
         * The rule in one line: same power, equal, whatever the timeouts. C's
         * {@code curses_are_equal} ({@code obj-curse.c}) compares nothing but {@code power} across
         * the two curse arrays, and this is the port of that comparison.
         */
        @Test
        @DisplayName("power alone decides equality")
        void powerAloneDecides() {
            assertAll(
                    () -> assertEquals(new CurseData(3, 0), new CurseData(3, 40)),
                    () -> assertFalse(new CurseData(3, 0).equals(new CurseData(4, 0))));
        }

        /**
         * The hash has to follow equality or two curses of the same power at different timeouts land
         * in different buckets, and any hashed comparison of curse data stops working for reasons
         * that have nothing to do with curses.
         */
        @Test
        @DisplayName("the hash follows equality rather than the timeout")
        void hashFollowsEquality() {
            assertEquals(new CurseData(3, 0).hashCode(), new CurseData(3, 99).hashCode());
        }

        /**
         * Not equal to a non-{@link CurseData} and not equal to null — the pattern-matching
         * {@code instanceof} gives both, and a test holds them if it is ever rewritten.
         */
        @Test
        @DisplayName("nothing else is equal to curse data")
        void otherTypesAreNotEqual() {
            CurseData data = new CurseData(1, 0);

            assertAll(
                    () -> assertFalse(data.equals(null)),
                    () -> assertFalse(data.equals("1")));
        }

        /**
         * What a plain {@code Map.equals} makes of this: it defers to {@code equals} for the
         * values, so two maps holding the same curse at the same power are equal whatever the
         * timeouts. {@code ItemObject.cursesAreEqual} does not use {@code Map.equals} (it walks the
         * curse registry, as C does), so this is the composition for any other caller, not the
         * stacking rule.
         */
        @Test
        @DisplayName("map comparison defers to it for the values")
        void mapComparisonUsesIt() {
            Curse siren = curse("siren");

            Map<Curse, CurseData> left = new HashMap<>();
            left.put(siren, new CurseData(2, 5));
            Map<Curse, CurseData> right = new HashMap<>();
            right.put(siren, new CurseData(2, 31));

            assertTrue(left.equals(right));
        }

        /**
         * Where {@code Map.equals} and C part company. C's {@code curses_are_equal} indexes both
         * arrays by curse, so a curse absent from one object (power zero) matches a zero-power
         * entry on the other. A map has no such slot: an entry at power zero is a key the other
         * map lacks, so the maps are unequal. This pins that gap so nobody swaps
         * {@code cursesAreEqual} for {@code Map.equals} believing the two are the same test.
         */
        @Test
        @DisplayName("a zero-power entry is not equal to an absent one under Map.equals")
        void zeroPowerEntryDiffersFromAbsentUnderMapEquals() {
            Curse siren = curse("siren");

            Map<Curse, CurseData> carries = new HashMap<>();
            carries.put(siren, new CurseData(0, 7));

            assertFalse(carries.equals(new HashMap<Curse, CurseData>()));
        }

        /**
         * Hash follows power and stays put when only the timeout moves, which is the half of the
         * contract {@link #hashFollowsEquality()} does not reach: the same instance, ticked.
         */
        @Test
        @DisplayName("ticking the timeout does not move the hash")
        void tickingDoesNotMoveHash() {
            CurseData data = new CurseData(3, 10);
            int before = data.hashCode();

            data.decrementTimeout();
            data.setTimeout(99);

            assertEquals(before, data.hashCode());
        }
    }

    /**
     * The mutable half, and the copy constructor that exists because of it.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("instance state")
    class State {

        /**
         * C's curse tick is {@code obj->curses[i].timeout--}, one turn at a time, and this is that
         * decrement. It is a separate operation from {@link CurseData#setTimeout} because the
         * re-arming after an effect fires is an assignment of a freshly rolled interval, not a step.
         */
        @Test
        @DisplayName("the timeout steps down by one and is re-armed by assignment")
        void timeoutStepsAndRearms() {
            CurseData data = new CurseData(2, 3);

            data.decrementTimeout();
            assertEquals(2, data.getTimeout());

            data.setTimeout(17);
            assertEquals(17, data.getTimeout());
        }

        /**
         * Nothing clamps the countdown at zero. C does not clamp it either — the tick tests for zero
         * on the way past and re-arms there, so the value is never left to run on. Pinning it means a
         * future clamp has to be a deliberate change rather than an accident, and says where the
         * responsibility for the boundary lies.
         */
        @Test
        @DisplayName("the timeout is not clamped at zero")
        void timeoutIsNotClamped() {
            CurseData data = new CurseData(2, 0);

            data.decrementTimeout();

            assertEquals(-1, data.getTimeout());
        }

        /**
         * The copy constructor takes both fields and shares nothing. The independence is the point:
         * mutating the copy must leave the original alone, since the whole reason for copying is that
         * one of the two is a template that outlives the object made from it.
         */
        @Test
        @DisplayName("a copy carries both fields and is independent")
        void copyIsIndependent() {
            CurseData original = new CurseData(4, 12);
            CurseData copy = new CurseData(original);

            assertAll(
                    () -> assertEquals(4, copy.getPower()),
                    () -> assertEquals(12, copy.getTimeout()),
                    () -> assertNotSame(original, copy));

            copy.decrementTimeout();
            copy.setPower(9);

            assertAll(
                    () -> assertEquals(12, original.getTimeout()),
                    () -> assertEquals(4, original.getPower()));
        }

        /**
         * A copy is equal to its original, the copy constructor and {@code equals} agreeing that the
         * power is what identifies the data. Worth stating because the two were written for opposite
         * reasons — one to keep instances apart, the other to treat them as interchangeable.
         */
        @Test
        @DisplayName("a copy is equal to what it was copied from")
        void copyEqualsOriginal() {
            CurseData original = new CurseData(4, 12);

            assertEquals(original, new CurseData(original));
        }

        /**
         * C's {@code struct curse_data} is two bare {@code int}s with no validation, so the
         * constructor stores whatever it is given: zero, a permanent power of 100 or more (the
         * {@code power >= 100} test in {@code uncurse_object}, {@code effect-handler-general.c}),
         * and a negative timeout.
         */
        @Test
        @DisplayName("the constructor stores both fields unchecked")
        void constructorStoresUnchecked() {
            CurseData zero = new CurseData(0, 0);
            CurseData permanent = new CurseData(100, 5);
            CurseData negative = new CurseData(2, -3);

            assertAll(
                    () -> assertEquals(0, zero.getPower()),
                    () -> assertEquals(0, zero.getTimeout()),
                    () -> assertEquals(100, permanent.getPower()),
                    () -> assertEquals(5, permanent.getTimeout()),
                    () -> assertEquals(-3, negative.getTimeout()));
        }

        /**
         * Setting the power is a plain field write, C's {@code obj->curses[pick].power = power} in
         * {@code append_object_curse}. It is not {@code remove_object_curse}, which zeroes the
         * timeout alongside the power: here the timeout survives a power of zero, and the
         * timeout does not move when the power is raised.
         */
        @Test
        @DisplayName("setting the power leaves the timeout alone")
        void setPowerLeavesTimeout() {
            CurseData data = new CurseData(2, 8);

            data.setPower(5);
            assertAll(
                    () -> assertEquals(5, data.getPower()),
                    () -> assertEquals(8, data.getTimeout()));

            data.setPower(0);
            assertAll(
                    () -> assertEquals(0, data.getPower()),
                    () -> assertEquals(8, data.getTimeout()));
        }

        /**
         * Setting the timeout does not touch the power, and the stored value is exactly the one
         * given — the dice roll ({@code randcalc(c->obj->time, 0, RANDOMISE)}) is the caller's.
         */
        @Test
        @DisplayName("setting the timeout leaves the power alone")
        void setTimeoutLeavesPower() {
            CurseData data = new CurseData(7, 1);

            data.setTimeout(0);

            assertAll(
                    () -> assertEquals(7, data.getPower()),
                    () -> assertEquals(0, data.getTimeout()));
        }

        /**
         * One cursed item through C's {@code decrease_timeouts}: with power set, the timeout is
         * decremented and the effect fires when it lands on exactly zero, after which the timeout
         * is re-armed. From 2 that is a quiet turn at 1, then the firing turn at 0. The loop here
         * is the caller's, written out from C; the test pins that the decrement alone produces the
         * values the loop tests.
         */
        @Test
        @DisplayName("a countdown from 2 reaches zero on the second tick")
        void countdownFiresOnZero() {
            CurseData data = new CurseData(3, 2);

            data.decrementTimeout();
            assertEquals(1, data.getTimeout());

            data.decrementTimeout();
            assertEquals(0, data.getTimeout());

            data.setTimeout(30);
            assertEquals(30, data.getTimeout());
        }

        /**
         * The unclamped step again, from the far end. C's {@code !curse[j].timeout} test is true
         * only at exactly zero, so a timeout that starts negative (never re-armed) ticks further
         * down without firing: -1 becomes -2, and none of the values on the way is zero.
         */
        @Test
        @DisplayName("a negative timeout keeps falling and never reads as zero")
        void negativeTimeoutKeepsFalling() {
            CurseData data = new CurseData(3, -1);

            data.decrementTimeout();

            assertEquals(-2, data.getTimeout());
        }
    }
}
