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
import uk.co.jackoftradesltd.channel.enums.ElementEnum;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link HelperFunctions}, checked against C's {@code name_parameters[]} and
 * {@code priority_schemes[]} function-pointer tables ({@code [C] ui-entry.c:129-161}) and the nine
 * lookup functions they name ({@code [C] ui-entry.c:1564-1653}).
 *
 * <p>In the same package deliberately, since every method under test is package-private -
 * {@link UIEntryNameParameter} and {@link UIEntryPriorityScheme} are the only production callers.
 *
 * @author Rowan Crowther
 */
class HelperFunctionsTest {

    // ---- the "none" scheme: get_dummy_param_count/get_dummy_param_name (ui-entry.c:1565-1576) ------

    @Test
    void dummyParamCountIsOne() {
        assertEquals(1, HelperFunctions.getDummyParamCount());
    }

    @Test
    void dummyParamNameIsAlwaysEmptyRegardlessOfIndex() {
        assertEquals("", HelperFunctions.getDummyParamName(0));
        assertEquals("", HelperFunctions.getDummyParamName(1));
        assertEquals("", HelperFunctions.getDummyParamName(-5));
    }

    // ---- the "element" scheme: get_element_count/get_element_name (ui-entry.c:1587-1599). C's ------
    // ---- element_names[] runs ACID..ARROW, 25 entries, 0-indexed; ElementEnum carries two -----------
    // ---- placeholders C has no counterpart for, ELEM_NONE and ELEM_MAX, so the count stays the ------
    // ---- same 25 (values().length - 2) while only the valid index range is shifted up by one to -----
    // ---- skip ELEM_NONE. --------------------------------------------------------------------------

    @Test
    void elementCountMatchesTheCCountUnshifted() {
        assertEquals(25, HelperFunctions.getElementCount());
    }

    @Test
    void elementNameAtTheFirstValidIndexIsAcidMatchingCsElementNames0() {
        assertEquals("ELEM_ACID", HelperFunctions.getElementName(1));
    }

    @Test
    void elementNameAtTheLastValidIndexIsArrowMatchingCsElementNames24() {
        assertEquals("ELEM_ARROW", HelperFunctions.getElementName(25));
    }

    @Test
    void elementNameRejectsIndexZeroWhichWouldBeTheNonePlaceholder() {
        assertThrows(IndexOutOfBoundsException.class, () -> HelperFunctions.getElementName(0));
    }

    @Test
    void elementNameRejectsIndexTwentySixWhichWouldBeElemMax() {
        assertThrows(IndexOutOfBoundsException.class, () -> HelperFunctions.getElementName(26));
    }

    @Test
    void elementNameCoversEveryResistableElementInEnumDeclarationOrder() {
        // Cross-checks the whole valid range against ElementEnum itself, rather than
        // hard-coding all 25 names, so this also pins the offset-by-one relationship.
        for (int ord = 1; ord < ElementEnum.values().length - 1; ord++) {
            assertEquals(ElementEnum.values()[ord].name(), HelperFunctions.getElementName(ord));
        }
    }

    // ---- the "stat" scheme: get_stat_count/get_stat_name (ui-entry.c:1610-1623). Plain 0-based, -----
    // ---- no placeholder to offset for, unlike the element scheme above. ----------------------------

    @Test
    void statCountIsFiveMatchingCsStatNames() {
        assertEquals(5, HelperFunctions.getStatCount());
    }

    @Test
    void statNameReturnsTheFiveStatsInCsListStatsOrder() {
        assertEquals("STR", HelperFunctions.getStatName(0));
        assertEquals("INT", HelperFunctions.getStatName(1));
        assertEquals("WIS", HelperFunctions.getStatName(2));
        assertEquals("DEX", HelperFunctions.getStatName(3));
        assertEquals("CON", HelperFunctions.getStatName(4));
    }

    @Test
    void statNameRejectsIndexBelowZero() {
        assertThrows(IndexOutOfBoundsException.class, () -> HelperFunctions.getStatName(-1));
    }

    @Test
    void statNameRejectsIndexFiveWhichIsOneBeyondCon() {
        assertThrows(IndexOutOfBoundsException.class, () -> HelperFunctions.getStatName(5));
    }

    // ---- priority schemes: get_dummy_priority/get_priority_from_index/ -----------------------------
    // ---- get_priority_from_negative_index (ui-entry.c:1626-1653) -----------------------------------

    @Test
    void dummyPriorityIsAlwaysZeroRegardlessOfIndex() {
        assertEquals(0, HelperFunctions.getDummyPriority(0));
        assertEquals(0, HelperFunctions.getDummyPriority(7));
        assertEquals(0, HelperFunctions.getDummyPriority(-3));
    }

    @Test
    void priorityFromIndexReturnsTheIndexUnchanged() {
        assertEquals(0, HelperFunctions.getPriorityFromIndex(0));
        assertEquals(4, HelperFunctions.getPriorityFromIndex(4));
    }

    @Test
    void priorityFromNegativeIndexReturnsTheNegatedIndex() {
        assertEquals(0, HelperFunctions.getPriorityFromNegativeIndex(0));
        assertEquals(-4, HelperFunctions.getPriorityFromNegativeIndex(4));
    }
}
