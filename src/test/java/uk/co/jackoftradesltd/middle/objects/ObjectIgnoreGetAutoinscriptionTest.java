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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static uk.co.jackoftradesltd.testsupport.ItemFixture.set;

/**
 * Tests {@link ObjectIgnore#getAutoinscription}, the port of C's {@code get_autoinscription}
 * ({@code obj-ignore.c:229}).
 *
 * <p>C reads the answer through {@code quark_str}, whose null-quark case returns {@code NULL}; the
 * port's {@link ObjectKind#getNoteAware()} and {@link ObjectKind#getNoteUnaware()} are plain
 * {@code String} fields that are {@code null} until set, so the two sides answer the same way
 * without the port doing anything extra for it. Private, so reached by reflection rather than
 * through its only caller, {@code applyAutoinscription}, which is not itself ported yet — there is
 * no setter for either note field either, so a kind under test has its note fields written directly.
 *
 * @author Rowan Crowther
 */
class ObjectIgnoreGetAutoinscriptionTest {

    /**
     * Runs the method under test.
     *
     * @param kind  the kind to look up, or {@code null}
     * @param aware which of the two notes to ask for
     * @return the autoinscription {@code getAutoinscription} answers
     */
    private static String getAutoinscription(ObjectKind kind, boolean aware) {
        try {
            Method method = ObjectIgnore.class.getDeclaredMethod("getAutoinscription", ObjectKind.class,
                    boolean.class);
            method.setAccessible(true);
            return (String) method.invoke(null, kind, aware);
        } catch (InvocationTargetException e) {
            throw new IllegalStateException("getAutoinscription threw", e.getCause());
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("getAutoinscription is no longer reachable", e);
        }
    }

    /**
     * A {@code null} kind answers {@code null} before either branch is tried, matching C's
     * {@code !kind} guard — for both values of {@code aware}, since the guard runs before the flag
     * is even read.
     */
    @Nested
    @DisplayName("a null kind")
    class NullKind {

        @Test
        @DisplayName("answers null when aware")
        void nullKindAwareIsNull() {
            assertNull(getAutoinscription(null, true));
        }

        @Test
        @DisplayName("answers null when unaware")
        void nullKindUnawareIsNull() {
            assertNull(getAutoinscription(null, false));
        }
    }

    /**
     * With a real kind, the {@code aware} flag alone picks which of the two notes comes back — never
     * the other one, and never both.
     */
    @Nested
    @DisplayName("a real kind")
    class RealKind {

        /**
         * Aware reads {@code note_aware} and nothing else.
         */
        @Test
        @DisplayName("aware returns the aware note")
        void awareReturnsAwareNote() {
            ObjectKind kind = new ObjectKind();
            set(kind, "noteAware", "{ blessed}");

            assertSame("{ blessed}", getAutoinscription(kind, true));
        }

        /**
         * Unaware reads {@code note_unaware} and nothing else.
         */
        @Test
        @DisplayName("unaware returns the unaware note")
        void unawareReturnsUnawareNote() {
            ObjectKind kind = new ObjectKind();
            set(kind, "noteUnaware", "{ tried}");

            assertSame("{ tried}", getAutoinscription(kind, false));
        }

        /**
         * Asking aware of a kind that only carries an unaware note gets nothing — the two notes are
         * not a fallback pair, matching C's branch reading one quark field or the other, never both.
         */
        @Test
        @DisplayName("aware does not fall back to the unaware note")
        void awareDoesNotFallBackToUnaware() {
            ObjectKind kind = new ObjectKind();
            set(kind, "noteUnaware", "{ tried}");

            assertNull(getAutoinscription(kind, true));
        }

        /**
         * And the same the other way round.
         */
        @Test
        @DisplayName("unaware does not fall back to the aware note")
        void unawareDoesNotFallBackToAware() {
            ObjectKind kind = new ObjectKind();
            set(kind, "noteAware", "{ blessed}");

            assertNull(getAutoinscription(kind, false));
        }

        /**
         * A kind with neither note set answers {@code null} either way — the fields default to
         * {@code null} the way C's quark fields default to the null quark.
         */
        @Test
        @DisplayName("a kind with no notes set answers null either way")
        void bareKindAnswersNullBothWays() {
            ObjectKind kind = new ObjectKind();

            assertNull(getAutoinscription(kind, true));
            assertNull(getAutoinscription(kind, false));
        }
    }
}
