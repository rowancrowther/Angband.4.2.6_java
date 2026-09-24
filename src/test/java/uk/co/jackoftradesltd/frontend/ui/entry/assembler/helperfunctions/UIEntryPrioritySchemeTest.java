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

package uk.co.jackoftradesltd.frontend.ui.entry.assembler.helperfunctions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests {@link UIEntryPriorityScheme}, the port of C's {@code priority_schemes[]} table
 * ({@code [C] ui-entry.c:157-161}).
 *
 * <p>Expected values are derived from the C originals directly: {@code get_dummy_priority} always
 * returns {@code 0}, {@code get_priority_from_index} returns its argument unchanged, and
 * {@code get_priority_from_negative_index} returns its argument negated ({@code [C]
 * ui-entry.c:1643-1668}).
 *
 * <p>Class UIEntryPrioritySchemeTest coded on 260924, commented in full on 260924.
 *
 * @author Rowan Crowther
 */
class UIEntryPrioritySchemeTest {

    @Test
    void namesMatchTheirDataFileDirectiveText() {
        assertEquals("", UIEntryPriorityScheme.PRIORITY_SCHEME_NONE.getName());
        assertEquals("index", UIEntryPriorityScheme.PRIORITY_SCHEME_INDEX.getName());
        assertEquals("negative_index", UIEntryPriorityScheme.PRIORITY_SCHEME_NEGATIVE_INDEX.getName());
    }

    @Test
    void noneAlwaysResolvesToZero() {
        assertEquals(0, UIEntryPriorityScheme.PRIORITY_SCHEME_NONE.getPriority(0));
        assertEquals(0, UIEntryPriorityScheme.PRIORITY_SCHEME_NONE.getPriority(3));
        assertEquals(0, UIEntryPriorityScheme.PRIORITY_SCHEME_NONE.getPriority(-7));
    }

    @Test
    void indexResolvesToTheIndexUnchanged() {
        assertEquals(0, UIEntryPriorityScheme.PRIORITY_SCHEME_INDEX.getPriority(0));
        assertEquals(4, UIEntryPriorityScheme.PRIORITY_SCHEME_INDEX.getPriority(4));
    }

    @Test
    void negativeIndexResolvesToTheNegatedIndex() {
        assertEquals(0, UIEntryPriorityScheme.PRIORITY_SCHEME_NEGATIVE_INDEX.getPriority(0));
        assertEquals(-4, UIEntryPriorityScheme.PRIORITY_SCHEME_NEGATIVE_INDEX.getPriority(4));
    }
}
