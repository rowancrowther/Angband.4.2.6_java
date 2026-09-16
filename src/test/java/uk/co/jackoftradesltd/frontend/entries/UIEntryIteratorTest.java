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

package uk.co.jackoftradesltd.frontend.entries;

import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.channel.enums.ElementEnum;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.frontend.entries.enums.EntryFlag;
import uk.co.jackoftradesltd.frontend.screen.enums.CombinerName;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link UIEntryIterator}, checked against C's
 * {@code struct ui_entry_iterator} and its free functions
 * ({@code src/ui-entry.c:41-44, 461-524}).
 *
 * <p>Expected values are derived from the C reference implementation directly:
 * {@code advance_ui_entry_iterator} reads {@code entries[i]} then increments
 * {@code i} (so the first call returns index 0, not index 1), and
 * {@code count_ui_entry_iterator} returns {@code n - i}, the count remaining
 * rather than the original total.
 *
 * @author Rowan Crowther
 */
class UIEntryIteratorTest {

    private static UIEntry entry(String name) {
        return new UIEntry(name, ElementEnum.ELEM_ACID, UIEntry.StatElemType.ELEMENT,
                null, CombinerName.ADD, List.of(), 5, new Flag<>(EntryFlag.class),
                "some description", "Label", "Lbl5", "L2", null);
    }

    private static List<UIEntry> entries(String... names) {
        List<UIEntry> list = new ArrayList<>();
        for (String name : names) {
            list.add(entry(name));
        }
        return list;
    }

    // ---- advance(): read-then-increment, matching advance_ui_entry_iterator ------------------

    @Test
    void advanceReturnsEntriesInOrderStartingAtIndexZero() {
        List<UIEntry> list = entries("a", "b", "c");
        UIEntryIterator it = new UIEntryIterator(list);

        assertEquals("a", it.advance().getName());
        assertEquals("b", it.advance().getName());
        assertEquals("c", it.advance().getName());
    }

    @Test
    void advanceIncrementsIndexAfterReading() {
        UIEntryIterator it = new UIEntryIterator(entries("a", "b"));

        assertEquals(0, it.getIndex());
        it.advance();
        assertEquals(1, it.getIndex());
        it.advance();
        assertEquals(2, it.getIndex());
    }

    @Test
    void advancePastTheEndReturnsNullRatherThanThrowing() {
        UIEntryIterator it = new UIEntryIterator(entries("a"));

        it.advance();
        assertNull(it.advance());
    }

    @Test
    void advanceOnAnEmptyIteratorReturnsNullImmediately() {
        UIEntryIterator it = new UIEntryIterator(new ArrayList<>());

        assertNull(it.advance());
    }

    // ---- getNum(): n - i, matching count_ui_entry_iterator ------------------------------------

    @Test
    void getNumAtConstructionEqualsTheEntryCount() {
        UIEntryIterator it = new UIEntryIterator(entries("a", "b", "c"));

        assertEquals(3, it.getNum());
    }

    @Test
    void getNumFallsByOneWithEachAdvanceAndReachesZeroWhenExhausted() {
        UIEntryIterator it = new UIEntryIterator(entries("a", "b", "c"));

        assertEquals(3, it.getNum());
        it.advance();
        assertEquals(2, it.getNum());
        it.advance();
        assertEquals(1, it.getNum());
        it.advance();
        assertEquals(0, it.getNum());
    }

    @Test
    void getNumOnAnEmptyIteratorIsZero() {
        UIEntryIterator it = new UIEntryIterator(new ArrayList<>());

        assertEquals(0, it.getNum());
    }

    // ---- three-arg constructor: num is accepted but not retained -----------------------------

    @Test
    void threeArgConstructorIgnoresTheSuppliedNumInFavourOfEntriesSize() {
        UIEntryIterator it = new UIEntryIterator(entries("a", "b", "c", "d"), 999, 0);

        assertEquals(4, it.getNum());
    }

    @Test
    void threeArgConstructorHonoursAnArbitraryStartingIndex() {
        List<UIEntry> list = entries("a", "b", "c", "d");
        UIEntryIterator it = new UIEntryIterator(list, 4, 2);

        assertEquals(2, it.getIndex());
        assertEquals(2, it.getNum());
        assertEquals("c", it.advance().getName());
        assertEquals("d", it.advance().getName());
        assertNull(it.advance());
    }

    // ---- addEntry()/no-arg constructor: the build-up path used by UIEntryCode ----------------

    @Test
    void noArgConstructorStartsEmptyAndAddEntryAppendsInCallOrder() {
        UIEntryIterator it = new UIEntryIterator();

        assertEquals(0, it.getNum());

        it.addEntry(entry("a"));
        it.addEntry(entry("b"));

        assertEquals(2, it.getNum());
        assertEquals("a", it.advance().getName());
        assertEquals("b", it.advance().getName());
    }

    @Test
    void getEntriesReturnsTheBackingListInEnumerationOrder() {
        List<UIEntry> list = entries("a", "b");
        UIEntryIterator it = new UIEntryIterator(list);

        assertEquals(List.of("a", "b"), it.getEntries().stream().map(UIEntry::getName).toList());
    }
}
