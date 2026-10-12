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

import static org.junit.jupiter.api.Assertions.*;

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
     * The table under test, freshly initialized before each test.
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
        void addingTheSameTextTwiceReturnsTheSameHandle() {
            // quark_add() scans for a streq match before allocating, so equal text never
            // holds two handles and callers may compare handles instead of strings.
            int first = quark.add("hello");
            int second = quark.add("hello");

            assertEquals(first, second);
            assertEquals(1, quark.size());
            assertEquals("hello", quark.getQuark(first));
        }

        @Test
        void aRepeatedAddDoesNotConsumeAHandle() {
            // Walk of quark_add(): "a" -> 1, "b" -> 2, "a" again -> 1 (nr_quarks stays 3 in C),
            // then "c" -> 3, not 4.
            assertEquals(1, quark.add("a"));
            assertEquals(2, quark.add("b"));
            assertEquals(1, quark.add("a"));
            assertEquals(3, quark.add("c"));
            assertEquals(3, quark.size());
        }

        @Test
        void theMatchIsCaseSensitiveAndWholeString() {
            // streq is an exact comparison, so near-misses get their own handles.
            int lower = quark.add("!d");
            int upper = quark.add("!D");
            int longer = quark.add("!d ");
            int prefix = quark.add("!");

            assertEquals(4, quark.size());
            assertNotEquals(lower, upper);
            assertNotEquals(lower, longer);
            assertNotEquals(lower, prefix);
        }

        @Test
        void theEmptyStringDeduplicatesToo() {
            int first = quark.add("");
            int second = quark.add("");

            assertEquals(first, second);
            assertEquals(1, quark.size());
        }

        @Test
        void aMatchIsFoundAmongManyEntries() {
            // The C scan runs from q = 1 to nr_quarks - 1; make sure the first and last
            // entries are both reachable, not just the middle.
            for (int i = 1; i <= 20; i++) quark.add("note" + i);

            assertEquals(1, quark.add("note1"));
            assertEquals(20, quark.add("note20"));
            assertEquals(20, quark.size());
        }

        @Test
        void aRepeatedAddAfterAMergeStillFindsTheMergedText() {
            quark.add("mine");
            Quark other = new Quark();
            other.init();
            other.add("theirs");
            quark.merge(other);

            int found = quark.add("theirs");

            assertEquals("theirs", quark.getQuark(found));
            assertEquals(2, quark.size());
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
