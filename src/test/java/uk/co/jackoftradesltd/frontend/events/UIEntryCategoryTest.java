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
 * category_reference} ({@code [C] ui-entry.c:55-59}: {@code name}, {@code
 * priority}, {@code priority_set}) and the way {@code finish_ui_entry}
 * ({@code [C] ui-entry.c:1660-1680}) fills those fields - the {@code
 * priority_set} branch copies the resolved priority across and sets the flag
 * true, the unset branch zeroes the priority and leaves the flag false.
 *
 * @author Rowan Crowther
 */
class UIEntryCategoryTest {

    // ---- explicit priority: mirrors the priority_set branch, ui-entry.c:1670-1675 ------------------

    @Test
    void constructorStoresAnExplicitlySetPriorityVerbatim() {
        UIEntryCategory category = new UIEntryCategory("HP", 5, true);

        assertEquals("HP", category.getName());
        assertEquals(5, category.getPriority());
        assertTrue(category.isPrioritySet());
    }

    // ---- unset priority: mirrors the else branch, ui-entry.c:1676-1678 (priority zeroed, flag false)

    @Test
    void constructorStoresAnUnsetPriorityAsZeroWithTheFlagFalse() {
        UIEntryCategory category = new UIEntryCategory("AC", 0, false);

        assertEquals("AC", category.getName());
        assertEquals(0, category.getPriority());
        assertFalse(category.isPrioritySet());
    }

    // ---- negative priority: get_priority_from_negative_index (ui-entry.c:1651-1656) can yield < 0 ---

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
}
