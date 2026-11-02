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
import uk.co.jackoftradesltd.channel.parser.DataFileParseException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for {@link HelperFunctions}, checked against C's {@code name_parameters[]} and
 * {@code priority_schemes[]} function-pointer tables and the nine lookup functions they name, all in
 * {@code ui-entry.c}. The expected element names come from C's {@code list-elements.h} and the stat
 * names from {@code list-stats.h}, not from {@code ElementEnum}.
 *
 * <p>In the same package deliberately, since every method under test is package-private -
 * {@link UIEntryNameParameter} and {@link UIEntryPriorityScheme} are the only production callers.
 *
 * <p>Test class coded on 260920, rewritten on 261102 for the unshifted 0-based element index.
 *
 * @author Rowan Crowther
 */
class HelperFunctionsTest {

    /** C's {@code element_names[]}: the {@code ELEM(x)} entries of {@code list-elements.h}, in order. */
    private static final String[] C_ELEMENT_NAMES = {
            "ACID", "ELEC", "FIRE", "COLD", "POIS", "LIGHT", "DARK", "SOUND", "SHARD", "NEXUS",
            "NETHER", "CHAOS", "DISEN", "WATER", "ICE", "GRAVITY", "INERTIA", "FORCE", "TIME",
            "PLASMA", "METEOR", "MISSILE", "MANA", "HOLY_ORB", "ARROW"
    };

    // ---- the "none" scheme: get_dummy_param_count/get_dummy_param_name ------------------------------

    @Test
    void dummyParamCountIsOne() {
        assertEquals(1, HelperFunctions.getDummyParamCount());
    }

    @Test
    void dummyParamNameAtIndexZeroIsTheEmptyString() {
        assertEquals("", HelperFunctions.getDummyParamName(0));
    }

    @Test
    void dummyParamNameRejectsNonZeroIndexMatchingCsAssert() {
        assertThrows(DataFileParseException.class, () -> HelperFunctions.getDummyParamName(1));
        assertThrows(DataFileParseException.class, () -> HelperFunctions.getDummyParamName(-1));
    }

    // ---- the "element" scheme: get_element_count/get_element_name. 0-based, no offset ---------------

    @Test
    void elementCountIsTwentyFiveMatchingCsElementNames() {
        assertEquals(25, HelperFunctions.getElementCount());
        assertEquals(C_ELEMENT_NAMES.length, HelperFunctions.getElementCount());
    }

    @Test
    void elementNameAtIndexZeroIsAcidWithoutThePrefix() {
        assertEquals("ACID", HelperFunctions.getElementName(0));
    }

    @Test
    void elementNameAtTheLastValidIndexIsArrow() {
        assertEquals("ARROW", HelperFunctions.getElementName(24));
    }

    @Test
    void elementNameKeepsTheUnderscoreInHolyOrb() {
        assertEquals("HOLY_ORB", HelperFunctions.getElementName(23));
    }

    @Test
    void elementNameMatchesCsListForEveryValidIndex() {
        for (int i = 0; i < C_ELEMENT_NAMES.length; i++) {
            assertEquals(C_ELEMENT_NAMES[i], HelperFunctions.getElementName(i), "index " + i);
        }
    }

    @Test
    void elementNameRejectsNegativeIndex() {
        assertThrows(IndexOutOfBoundsException.class, () -> HelperFunctions.getElementName(-1));
    }

    @Test
    void elementNameRejectsIndexEqualToTheCount() {
        assertThrows(IndexOutOfBoundsException.class,
                () -> HelperFunctions.getElementName(HelperFunctions.getElementCount()));
    }

    @Test
    void elementNameRejectsTheOldShiftedUpperIndex() {
        // 25 was the last valid index before the shift was removed; C's assert rejects it
        assertThrows(IndexOutOfBoundsException.class, () -> HelperFunctions.getElementName(25));
    }

    // ---- the "stat" scheme: get_stat_count/get_stat_name. Plain 0-based -----------------------------

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

    @Test
    void statsFieldHoldsTheSameFiveNamesAsGetStatName() {
        assertEquals(HelperFunctions.getStatCount(), HelperFunctions.stats.length);
        for (int i = 0; i < HelperFunctions.stats.length; i++) {
            assertEquals(HelperFunctions.stats[i], HelperFunctions.getStatName(i));
        }
    }

    // ---- priority schemes: get_dummy_priority/get_priority_from_index/ ------------------------------
    // ---- get_priority_from_negative_index -----------------------------------------------------------

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
        assertEquals(-2, HelperFunctions.getPriorityFromIndex(-2));
    }

    @Test
    void priorityFromNegativeIndexReturnsTheNegatedIndex() {
        assertEquals(0, HelperFunctions.getPriorityFromNegativeIndex(0));
        assertEquals(-4, HelperFunctions.getPriorityFromNegativeIndex(4));
        assertEquals(2, HelperFunctions.getPriorityFromNegativeIndex(-2));
    }
}
