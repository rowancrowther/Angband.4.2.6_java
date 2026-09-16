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
import uk.co.jackoftradesltd.frontend.events.UIEntryCategory;
import uk.co.jackoftradesltd.frontend.screen.enums.CombinerName;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link UIEntry}, checked against the C original's UI-entry
 * system ({@code src/ui-entry.c}).
 *
 * <p>Covers the constructor's {@code description} validation, the category
 * search ({@link UIEntry#uiEntryHasCategory(String)}, which ports
 * {@code ui_entry_has_category} / {@code ui_entry_search_categories}), the
 * flag test ({@link UIEntry#entryFlagHas(EntryFlag)}, which ports the
 * {@code entry->flags & ENTRY_FLAG_*} bit test), and
 * {@link UIEntry.StatElemType#fromValue(String)} (which ports the linear scan
 * over C's {@code name_parameters[]} table in {@code parse_entry_parameter}).
 *
 * @author Rowan Crowther
 */
class UIEntryTest {

    private static UIEntry entry(List<UIEntryCategory> categories, Flag<EntryFlag> flags) {
        return new UIEntry("test_entry", ElementEnum.ELEM_ACID, UIEntry.StatElemType.ELEMENT,
                null, CombinerName.ADD, categories, 5, flags, "some description",
                "Label", "Lbl5", "L2", null);
    }

    // ---- constructor: description is validated but not stored --------------------------------

    @Test
    void constructorRejectsANullDescription() {
        assertThrows(IllegalArgumentException.class, () ->
                new UIEntry("x", null, UIEntry.StatElemType.NONE, null, CombinerName.NONE,
                        List.of(), 0, new Flag<>(EntryFlag.class), null, "L", "L5", "L2", null));
    }

    @Test
    void constructorAcceptsANonNullDescriptionAndDoesNotExposeIt() {
        UIEntry e = entry(List.of(), new Flag<>(EntryFlag.class));

        assertEquals("test_entry", e.getName());
        // No getDescription() exists: C's struct ui_entry never carries the desc: field either
        // (parse_entry_desc discards it), so there is nothing further to assert here beyond the
        // constructor not rejecting a legitimate value.
    }

    // ---- uiEntryHasCategory / uiEntrySearchCategories -----------------------------------------

    @Test
    void findsAKnownCategoryAtItsIndex() {
        UIEntry e = entry(List.of(
                        new UIEntryCategory("CHAR_SCREEN1", 0, false),
                        new UIEntryCategory("EQUIPCMP_SCREEN", 0, false)),
                new Flag<>(EntryFlag.class));

        Optional<Integer> found = e.uiEntryHasCategory("CHAR_SCREEN1");
        assertTrue(found.isPresent());
        assertEquals(0, found.get());

        Optional<Integer> foundSecond = e.uiEntryHasCategory("EQUIPCMP_SCREEN");
        assertTrue(foundSecond.isPresent());
        assertEquals(1, foundSecond.get());
    }

    @Test
    void reportsAbsentForAnUnknownCategory() {
        // Mirrors ui_entry_has_category returning false (ui-entry.c) when the name isn't present.
        UIEntry e = entry(List.of(new UIEntryCategory("CHAR_SCREEN1", 0, false)),
                new Flag<>(EntryFlag.class));

        assertTrue(e.uiEntryHasCategory("NO_SUCH_CATEGORY").isEmpty());
    }

    @Test
    void reportsAbsentWhenThereAreNoCategoriesAtAll() {
        UIEntry e = entry(List.of(), new Flag<>(EntryFlag.class));

        assertTrue(e.uiEntryHasCategory("ANYTHING").isEmpty());
    }

    // ---- entryFlagHas --------------------------------------------------------------------------

    @Test
    void entryFlagHasReportsASetFlag() {
        UIEntry e = entry(List.of(), new Flag<>(EntryFlag.class, EntryFlag.ENTRY_FLAG_TIMED_AS_AUX));

        assertTrue(e.entryFlagHas(EntryFlag.ENTRY_FLAG_TIMED_AS_AUX));
        assertFalse(e.entryFlagHas(EntryFlag.ENTRY_FLAG_TEMPLATE_ONLY));
    }

    @Test
    void entryFlagHasReportsFalseWhenNoFlagsAreSet() {
        UIEntry e = entry(List.of(), new Flag<>(EntryFlag.class));

        assertFalse(e.entryFlagHas(EntryFlag.ENTRY_FLAG_TIMED_AS_AUX));
        assertFalse(e.entryFlagHas(EntryFlag.ENTRY_FLAG_TEMPLATE_ONLY));
    }

    // ---- StatElemType.fromValue -----------------------------------------------------------------

    @Test
    void fromValueResolvesTheStatAndElementParameterNames() {
        // Matches the "stat"/"element" entries of C's name_parameters[] (ui-entry.c).
        assertEquals(UIEntry.StatElemType.STAT, UIEntry.StatElemType.fromValue("stat"));
        assertEquals(UIEntry.StatElemType.ELEMENT, UIEntry.StatElemType.fromValue("element"));
    }

    @Test
    void fromValueResolvesTheEmptyStringToNone() {
        // Matches C's dummy "" entry in name_parameters[], used when parameter: is unset.
        assertEquals(UIEntry.StatElemType.NONE, UIEntry.StatElemType.fromValue(""));
    }

    @Test
    void fromValueReturnsNullForAnUnrecognisedName() {
        // Matches parse_entry_parameter (ui-entry.c) falling through to PARSE_ERROR_INVALID_VALUE.
        assertNull(UIEntry.StatElemType.fromValue("bogus"));
    }
}
