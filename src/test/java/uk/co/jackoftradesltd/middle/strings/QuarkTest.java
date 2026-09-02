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

package uk.co.jackoftradesltd.middle.strings;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link Quark}, the port of the C source's string-interning table
 * ({@code src/z-quark.c}).
 *
 * <p>In the C game a quark is an integer handle standing in for a string - inscriptions,
 * mostly - so that saved objects can reference text without each carrying its own copy. The
 * handle is what gets stored, so the contract that actually matters is that a handle keeps
 * pointing at the same string for as long as the table lives, and that handle 0 is never
 * issued (the C original reserves it as "no quark").
 *
 * <p>{@link Quark} is an {@link uk.co.jackoftradesltd.backend.AngbandModule}, which means its
 * storage does not exist until {@code init()} and is deliberately torn down by
 * {@code cleanup()}. The lifecycle is therefore part of the contract rather than incidental
 * setup, and the tests cover use before initialisation and after teardown as well as the
 * ordinary path - a module used out of order should fail loudly rather than quietly
 * mis-store an inscription.
 *
 * @author Rowan Crowther
 */
class QuarkTest {

    /**
     * The table under test, freshly initialised before each test.
     */
    private Quark quark;

    @BeforeEach
    void setUp() {
        quark = new Quark();
        quark.init();
    }

    /**
     * Ordinary interning.
     */
    @Nested
    class Interning {

        @Test
        void aFreshTableIsEmpty() {
            assertEquals(0, quark.size());
        }

        @Test
        void addingAStringReturnsAHandleThatFetchesItBack() {
            int handle = quark.add("of the Magi");

            assertEquals("of the Magi", quark.getQuark(handle));
            assertEquals(1, quark.size());
        }

        @Test
        void handlesStartAtOneSoZeroStaysFreeForNoQuark() {
            // The C original reserves 0 to mean "this object has no inscription", so the
            // very first handle issued must not be 0.
            assertEquals(1, quark.add("first"));
        }

        @Test
        void handlesAreIssuedInSequence() {
            assertEquals(1, quark.add("first"));
            assertEquals(2, quark.add("second"));
            assertEquals(3, quark.add("third"));
        }

        @Test
        void everyHandleKeepsPointingAtItsOwnString() {
            int first = quark.add("first");
            int second = quark.add("second");
            int third = quark.add("third");

            assertEquals("first", quark.getQuark(first));
            assertEquals("second", quark.getQuark(second));
            assertEquals("third", quark.getQuark(third));
        }

        @Test
        void addingTheSameTextTwiceIssuesTwoHandles() {
            // This table does not deduplicate on the way in - worth pinning, because a
            // caller assuming otherwise would compare handles instead of strings.
            int first = quark.add("hello");
            int second = quark.add("hello");

            assertNotEquals(first, second);
            assertEquals(2, quark.size());
            assertEquals("hello", quark.getQuark(first));
            assertEquals("hello", quark.getQuark(second));
        }

        @Test
        void theEmptyStringIsStorableLikeAnyOther() {
            int handle = quark.add("");

            assertEquals("", quark.getQuark(handle));
        }

        @Test
        void anUnissuedHandleFetchesNull() {
            quark.add("only one");

            assertNull(quark.getQuark(0));
            assertNull(quark.getQuark(99));
            assertNull(quark.getQuark(-1));
        }
    }

    /**
     * The content search.
     */
    @Nested
    class Searching {

        @Test
        void containsTextFindsAStoredStringExactly() {
            quark.add("of the Magi");

            assertTrue(quark.containsText("of the Magi"));
        }

        @Test
        void containsTextIsCaseSensitiveAndDoesNotMatchSubstrings() {
            quark.add("of the Magi");

            assertFalse(quark.containsText("of the magi"));
            assertFalse(quark.containsText("Magi"));
        }

        @Test
        void containsTextIsFalseOnAnEmptyTable() {
            assertFalse(quark.containsText("anything"));
        }
    }

    /**
     * Merging one table's contents into another.
     */
    @Nested
    class Merging {

        @Test
        void mergingBringsTheOtherTablesStringsAcross() {
            quark.add("mine");

            Quark other = new Quark();
            other.init();
            other.add("theirs");

            quark.merge(other);

            assertEquals(2, quark.size());
            assertTrue(quark.containsText("mine"));
            assertTrue(quark.containsText("theirs"));
        }

        @Test
        void mergingOffsetsTheIncomingHandlesSoNothingIsOverwritten() {
            // Both tables issue handles from 1, so a naive merge would have the incoming
            // strings land on top of the existing ones.
            int mine = quark.add("mine");

            Quark other = new Quark();
            other.init();
            other.add("theirs");

            quark.merge(other);

            assertEquals("mine", quark.getQuark(mine));
            assertEquals(2, quark.size());
        }

        @Test
        void mergingLeavesTheSourceTableAlone() {
            quark.add("mine");

            Quark other = new Quark();
            other.init();
            int theirs = other.add("theirs");

            quark.merge(other);

            assertEquals(1, other.size());
            assertEquals("theirs", other.getQuark(theirs));
        }

        @Test
        void mergingNullOrAnEmptyTableIsANoOp() {
            quark.add("mine");

            Quark empty = new Quark();
            empty.init();

            quark.merge(null);
            quark.merge(empty);

            assertEquals(1, quark.size());
        }
    }

    /**
     * The module lifecycle - storage exists only between {@code init} and {@code cleanup}.
     */
    @Nested
    class Lifecycle {

        @Test
        void initNamesTheModule() {
            assertEquals("Quark", quark.getName());
        }

        @Test
        void anUninitialisedTableRefusesToStoreOrFetch() {
            // Failing loudly matters here: silently swallowing an add would lose an
            // inscription that a saved object already holds a handle for.
            Quark uninitialised = new Quark();

            assertThrows(NullPointerException.class, () -> uninitialised.add("hello"));
            assertThrows(NullPointerException.class, () -> uninitialised.getQuark(1));
        }

        @Test
        void cleanupReleasesTheStorage() {
            quark.add("hello");

            quark.cleanup();

            assertThrows(NullPointerException.class, () -> quark.add("again"));
            assertThrows(NullPointerException.class, () -> quark.getQuark(1));
        }

        @Test
        void reinitialisingAfterCleanupGivesAnEmptyTable() {
            quark.add("hello");
            quark.cleanup();

            quark.init();

            assertEquals(0, quark.size());
            assertFalse(quark.containsText("hello"));
            assertEquals(1, quark.add("fresh"));
        }
    }
}
