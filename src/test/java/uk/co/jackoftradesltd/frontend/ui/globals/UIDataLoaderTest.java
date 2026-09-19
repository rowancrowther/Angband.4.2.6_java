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

package uk.co.jackoftradesltd.frontend.ui.globals;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.channel.enums.ChannelEntryFlag;
import uk.co.jackoftradesltd.channel.utils.combiners.CombinerName;
import uk.co.jackoftradesltd.frontend.entries.UIEntry;
import uk.co.jackoftradesltd.frontend.entries.UIEntryBase;
import uk.co.jackoftradesltd.frontend.entries.UIEntryCategory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link UIDataLoader#portUIEntryBasesToUIEntries()}, checked against the C
 * original's {@code run_parse_ui_entry} ({@code [C] src/ui-entry.c:2263-2276}).
 *
 * <p>The case this pins is the {@code entries[i]->flags |= ENTRY_FLAG_TEMPLATE_ONLY} union
 * ({@code [C] ui-entry.c:2274}): an earlier version of the port replaced a base's own resolved
 * flags outright with a fresh {@code Flag} containing only {@code ENTRY_FLAG_TEMPLATE_ONLY},
 * silently dropping {@code ENTRY_FLAG_TIMED_AS_AUX} - the flag all three of the bases the shipped
 * {@code ui_entry_base.txt} carries actually set. Every test resets {@link UIRegistry}'s bases and
 * entries afterwards so this static state does not leak between tests.
 *
 * @author Rowan Crowther
 */
class UIDataLoaderTest {

    private static UIEntryBase base(String name, String flags, List<String> categories) {
        return new UIEntryBase(name, null, CombinerName.LOGICAL_OR, categories, flags, "d");
    }

    @AfterEach
    void resetRegistry() {
        UIRegistry.setUIEntryBases(List.of());
        UIRegistry.setUIEntries(List.of());
    }

    @Test
    void aBaseWithNoOwnFlagBeyondTemplateOnlyKeepsExactlyTemplateOnly() {
        UIRegistry.setUIEntryBases(List.of(base("t", "TEMPLATE_ONLY", List.of("CHAR_SCREEN1"))));

        UIDataLoader.portUIEntryBasesToUIEntries();

        UIEntry entry = UIRegistry.getUIEntry("t");
        assertTrue(entry.entryFlagHas(ChannelEntryFlag.ENTRY_FLAG_TEMPLATE_ONLY));
        assertFalse(entry.entryFlagHas(ChannelEntryFlag.ENTRY_FLAG_TIMED_AS_AUX));
    }

    /**
     * The regression case: matches C's {@code entries[i]->flags |= ENTRY_FLAG_TEMPLATE_ONLY}
     * ({@code [C] ui-entry.c:2274}), which ORs the new bit onto whatever the base's own
     * {@code flags:} line already set, rather than replacing the set outright.
     */
    @Test
    void aBasesOwnFlagSurvivesAlongsideTemplateOnly() {
        UIRegistry.setUIEntryBases(List.of(base("t", "TIMED_AS_AUX", List.of("CHAR_SCREEN1"))));

        UIDataLoader.portUIEntryBasesToUIEntries();

        UIEntry entry = UIRegistry.getUIEntry("t");
        assertTrue(entry.entryFlagHas(ChannelEntryFlag.ENTRY_FLAG_TIMED_AS_AUX),
                "the base's own flag must survive the TEMPLATE_ONLY union, matching C's |=");
        assertTrue(entry.entryFlagHas(ChannelEntryFlag.ENTRY_FLAG_TEMPLATE_ONLY));
    }

    @Test
    void categoriesCarryOverByNameWithAnUnsetPriority() {
        UIRegistry.setUIEntryBases(List.of(
                base("t", "TIMED_AS_AUX", List.of("CHAR_SCREEN1", "abilities"))));

        UIDataLoader.portUIEntryBasesToUIEntries();

        List<UIEntryCategory> categories = UIRegistry.getUIEntry("t").getCategories();
        assertEquals(2, categories.size());
        assertEquals("CHAR_SCREEN1", categories.get(0).getName());
        assertFalse(categories.get(0).isPrioritySet(),
                "ui_entry_base.txt never sets a category priority, matching C's default");
        assertEquals("abilities", categories.get(1).getName());
        assertFalse(categories.get(1).isPrioritySet());
    }

    @Test
    void defaultPriorityIsZeroMatchingCsUnsetDefaultPriority() {
        UIRegistry.setUIEntryBases(List.of(base("t", "TIMED_AS_AUX", List.of("CHAR_SCREEN1"))));

        UIDataLoader.portUIEntryBasesToUIEntries();

        assertEquals(0, UIRegistry.getUIEntry("t").getPriorityNum());
    }

    @Test
    void multipleBasesEachConvertIndependently() {
        UIRegistry.setUIEntryBases(List.of(
                base("first", "TIMED_AS_AUX", List.of("CHAR_SCREEN1")),
                base("second", "TIMED_AS_AUX", List.of("hindrances"))));

        UIDataLoader.portUIEntryBasesToUIEntries();

        assertEquals(2, UIRegistry.getUIEntries().size());
        assertNotNull(UIRegistry.getUIEntry("first"));
        assertNotNull(UIRegistry.getUIEntry("second"));
    }

    @Test
    void anEmptyBaseListProducesAnEmptyEntryList() {
        UIRegistry.setUIEntryBases(List.of());

        UIDataLoader.portUIEntryBasesToUIEntries();

        assertTrue(UIRegistry.getUIEntries().isEmpty());
    }

    /**
     * {@link UIRegistry#setUIEntries} replaces the list wholesale, matching every other loader in
     * {@link UIDataLoader}; this method has no append path, so entries already registered (e.g. by
     * a prior {@link UIDataLoader#loadUIEntries()} call) are lost if this runs after it - ordering
     * that is {@link UIDataLoader}'s own job to get right, not something this method guards
     * against.
     */
    @Test
    void reRunningReplacesThePreviousEntryListRatherThanAppending() {
        UIRegistry.setUIEntryBases(List.of(base("first", "TIMED_AS_AUX", List.of("CHAR_SCREEN1"))));
        UIDataLoader.portUIEntryBasesToUIEntries();
        assertEquals(1, UIRegistry.getUIEntries().size());

        UIRegistry.setUIEntryBases(List.of(base("second", "TIMED_AS_AUX", List.of("hindrances"))));
        UIDataLoader.portUIEntryBasesToUIEntries();

        assertEquals(1, UIRegistry.getUIEntries().size());
        assertNull(UIRegistry.getUIEntry("first"));
        assertNotNull(UIRegistry.getUIEntry("second"));
    }
}
