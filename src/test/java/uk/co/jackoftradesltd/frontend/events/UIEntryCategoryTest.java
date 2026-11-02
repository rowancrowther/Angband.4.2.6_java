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

package uk.co.jackoftradesltd.frontend.events;

import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.frontend.entries.UIEntryCategory;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link UIEntryCategory}, checked against C's {@code struct
 * category_reference} in {@code ui-entry.c} ({@code name}, {@code priority},
 * {@code priority_set}) and the way {@code parameterize_category_list} fills
 * those fields - the {@code priority_set} branch stores the resolved priority
 * and sets the flag true, the unset branch zeroes the priority and leaves the
 * flag false. {@code finish_parse_ui_entry} then gives every unset category
 * the entry's default priority and sets the flag true.
 *
 * @author Rowan Crowther
 */
class UIEntryCategoryTest {

    // ---- explicit priority: the priority_set branch of parameterize_category_list -----------------

    @Test
    void constructorStoresAnExplicitlySetPriorityVerbatim() {
        UIEntryCategory category = new UIEntryCategory("HP", 5, true);

        assertEquals("HP", category.getName());
        assertEquals(5, category.getPriority());
        assertTrue(category.isPrioritySet());
    }

    // ---- unset priority: the else branch of parameterize_category_list (priority zeroed, flag false)

    @Test
    void constructorStoresAnUnsetPriorityAsZeroWithTheFlagFalse() {
        UIEntryCategory category = new UIEntryCategory("AC", 0, false);

        assertEquals("AC", category.getName());
        assertEquals(0, category.getPriority());
        assertFalse(category.isPrioritySet());
    }

    // ---- negative priority: get_priority_from_negative_index returns -i, so it can be < 0 ---------

    @Test
    void constructorAcceptsANegativePriorityFromTheNegativeIndexScheme() {
        UIEntryCategory category = new UIEntryCategory("SPEED", -3, true);

        assertEquals(-3, category.getPriority());
        assertTrue(category.isPrioritySet());
    }

    // ---- prioritySet is independent of the priority value, unlike inferring it from priority != 0 ---

    @Test
    void anExplicitZeroPriorityIsStillReportedAsSet() {
        UIEntryCategory category = new UIEntryCategory("STAT", 0, true);

        assertEquals(0, category.getPriority());
        assertTrue(category.isPrioritySet());
    }

    // ---- integer extremes: C stores a plain int and the struct does no range checking --------------

    @Test
    void constructorKeepsIntegerExtremesUnchanged() {
        assertEquals(Integer.MAX_VALUE, new UIEntryCategory("A", Integer.MAX_VALUE, true).getPriority());
        assertEquals(Integer.MIN_VALUE, new UIEntryCategory("B", Integer.MIN_VALUE, true).getPriority());
    }

    // ---- the constructor does not enforce the unset => 0 pairing; the caller supplies it ------------

    @Test
    void constructorStoresANonZeroPriorityWithTheFlagFalseWithoutNormalizing() {
        UIEntryCategory category = new UIEntryCategory("X", 7, false);

        assertEquals(7, category.getPriority());
        assertFalse(category.isPrioritySet());
    }

    // ---- setters write exactly one field each, as the C struct fields are assigned independently ----

    @Test
    void setNameChangesOnlyTheName() {
        UIEntryCategory category = new UIEntryCategory("OLD", 4, true);

        category.setName("NEW");

        assertEquals("NEW", category.getName());
        assertEquals(4, category.getPriority());
        assertTrue(category.isPrioritySet());
    }

    @Test
    void setPriorityChangesOnlyThePriorityAndLeavesTheFlagAlone() {
        UIEntryCategory category = new UIEntryCategory("CAT", 0, false);

        category.setPriority(9);

        assertEquals(9, category.getPriority());
        assertFalse(category.isPrioritySet());
        assertEquals("CAT", category.getName());
    }

    @Test
    void setPrioritySetChangesOnlyTheFlagAndLeavesThePriorityAlone() {
        UIEntryCategory category = new UIEntryCategory("CAT", 6, false);

        category.setPrioritySet(true);

        assertTrue(category.isPrioritySet());
        assertEquals(6, category.getPriority());
    }

    // ---- finish_parse_ui_entry: an unset category takes the default priority and becomes set ------

    @Test
    void fillingAnUnsetCategoryWithTheDefaultPriorityMatchesTheFinalParsePass() {
        int defaultPriority = 3;
        UIEntryCategory unset = new UIEntryCategory("CAT", 0, false);
        UIEntryCategory explicit = new UIEntryCategory("CAT2", 8, true);

        for (UIEntryCategory c : new UIEntryCategory[] {unset, explicit}) {
            if (!c.isPrioritySet()) {
                c.setPriority(defaultPriority);
                c.setPrioritySet(true);
            }
        }

        assertEquals(3, unset.getPriority());
        assertTrue(unset.isPrioritySet());
        assertEquals(8, explicit.getPriority());
        assertTrue(explicit.isPrioritySet());
    }

    // ---- copy(): the by-value copy of the C struct; the Java copy must be a distinct object --------

    @Test
    void copyHoldsTheSameThreeFieldsInADistinctObject() {
        UIEntryCategory original = new UIEntryCategory("HP", -2, true);

        UIEntryCategory copy = original.copy();

        assertNotSame(original, copy);
        assertEquals("HP", copy.getName());
        assertEquals(-2, copy.getPriority());
        assertTrue(copy.isPrioritySet());
    }

    @Test
    void copyOfAnUnsetCategoryStaysUnset() {
        UIEntryCategory copy = new UIEntryCategory("AC", 0, false).copy();

        assertEquals(0, copy.getPriority());
        assertFalse(copy.isPrioritySet());
    }

    @Test
    void changingTheCopyDoesNotChangeTheOriginalAndViceVersa() {
        UIEntryCategory original = new UIEntryCategory("HP", 1, false);
        UIEntryCategory copy = original.copy();

        copy.setName("MP");
        copy.setPriority(5);
        copy.setPrioritySet(true);

        assertEquals("HP", original.getName());
        assertEquals(1, original.getPriority());
        assertFalse(original.isPrioritySet());

        original.setPriority(99);
        assertEquals(5, copy.getPriority());
    }
}
