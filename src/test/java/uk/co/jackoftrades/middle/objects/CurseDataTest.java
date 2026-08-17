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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
 * <p>Class CurseDataTest coded on 260817, commented in full on 260817.
 *
 * @author Rowan Crowther
 */
class CurseDataTest {

    /**
     * A minimal curse definition, distinct from every other by identity.
     *
     * @param name the curse's name
     * @return a curse with every other field empty
     * @author Rowan Crowther
     */
    private static Curse curse(String name) {
        return new Curse(name, java.util.List.of(), 0, null, java.util.List.of(), Map.of(),
                Map.of(), 0, 0, 0, java.util.List.of(), java.util.List.of(), "", "");
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
         *
         * @author Rowan Crowther
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
         * in different buckets, and the map comparison that {@code cursesAreEqual} relies on stops
         * working for reasons that have nothing to do with curses.
         *
         * @author Rowan Crowther
         */
        @Test
        @DisplayName("the hash follows equality rather than the timeout")
        void hashFollowsEquality() {
            assertEquals(new CurseData(3, 0).hashCode(), new CurseData(3, 99).hashCode());
        }

        /**
         * Not equal to a non-{@link CurseData} and not equal to null — the pattern-matching
         * {@code instanceof} gives both, and a test holds them if it is ever rewritten.
         *
         * @author Rowan Crowther
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
         * The reason equality is defined at all: {@code ItemObject.cursesAreEqual} compares two
         * curse maps with {@code Map.equals}, which defers to this for the values. This asserts the
         * composition rather than the parts, since that is what the stacking code actually does.
         *
         * @author Rowan Crowther
         */
        @Test
        @DisplayName("map comparison reaches C's answer through it")
        void mapComparisonUsesIt() {
            Curse siren = curse("siren");

            Map<Curse, CurseData> left = new HashMap<>();
            left.put(siren, new CurseData(2, 5));
            Map<Curse, CurseData> right = new HashMap<>();
            right.put(siren, new CurseData(2, 31));

            assertTrue(left.equals(right));
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
         *
         * @author Rowan Crowther
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
         *
         * @author Rowan Crowther
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
         *
         * @author Rowan Crowther
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
         *
         * @author Rowan Crowther
         */
        @Test
        @DisplayName("a copy is equal to what it was copied from")
        void copyEqualsOriginal() {
            CurseData original = new CurseData(4, 12);

            assertEquals(original, new CurseData(original));
        }
    }
}
