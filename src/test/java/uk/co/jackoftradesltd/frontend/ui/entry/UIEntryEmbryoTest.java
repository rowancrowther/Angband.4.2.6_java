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

package uk.co.jackoftradesltd.frontend.ui.entry;

import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.channel.enums.ChannelEntryFlag;
import uk.co.jackoftradesltd.channel.enums.ElementEnum;
import uk.co.jackoftradesltd.channel.enums.StatElemType;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.channel.utils.combiners.CombinerName;
import uk.co.jackoftradesltd.frontend.entries.UIEntry;
import uk.co.jackoftradesltd.frontend.entries.UIEntryCategory;
import uk.co.jackoftradesltd.frontend.ui.entry.assembler.helperfunctions.UIEntryNameParameter;
import uk.co.jackoftradesltd.frontend.ui.entry.assembler.helperfunctions.UIEntryPriorityScheme;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link UIEntryEmbryo}, checked against C's {@code struct embryonic_ui_entry}
 * ({@code [C] ui-entry.c:170-177}: {@code entry}, {@code categories}, {@code param_index},
 * {@code psource_index}, {@code last_category_index}, {@code exists}) and the field assignments
 * {@code parse_entry_name} ({@code [C] ui-entry.c:1882-1943}) makes when it opens a new record.
 *
 * @author Rowan Crowther
 */
class UIEntryEmbryoTest {

    private static UIEntry freshEntry(String name) {
        return new UIEntry(name, ElementEnum.ELEM_NONE, StatElemType.NONE, null, CombinerName.NONE,
                new ArrayList<>(), 0, new Flag<>(ChannelEntryFlag.class), "desc", "", "", "");
    }

    // ---- constructor: direct field storage, mirroring parse_entry_name's assignments ---------------

    @Test
    void constructorStoresAllSixFieldsVerbatim() {
        UIEntry entry = freshEntry("t");
        List<EmbryonicCategoryReferencies> categories = new ArrayList<>();
        EmbryonicCategoryReferencies lastCategory = new EmbryonicCategoryReferencies(
                new UIEntryCategory("CHAR_SCREEN1", 0, false), UIEntryPriorityScheme.PRIORITY_SCHEME_NONE, 0, false);

        UIEntryEmbryo embryo = new UIEntryEmbryo(entry, categories,
                UIEntryNameParameter.ENTRY_NAME_PARAMETER_STAT, UIEntryPriorityScheme.PRIORITY_SCHEME_INDEX,
                lastCategory, true);

        assertSame(entry, embryo.getUiEntry());
        assertSame(categories, embryo.getCategories());
        assertEquals(UIEntryNameParameter.ENTRY_NAME_PARAMETER_STAT, embryo.getParmIndex());
        assertEquals(UIEntryPriorityScheme.PRIORITY_SCHEME_INDEX, embryo.getPSourceIndex());
        assertSame(lastCategory, embryo.getLastCategoryIndex());
        assertTrue(embryo.exists());
    }

    // ---- a fresh (not-yet-existing) embryo, mirroring parse_entry_name's "make a completely new -----
    // ---- entry" branch (ui-entry.c:1916-1936): param_index=0, psource_index=0, ----------------------
    // ---- last_category_index=-1, exists=false ------------------------------------------------------

    @Test
    void aFreshEmbryoMatchesCsNewEntryDefaults() {
        UIEntryEmbryo embryo = new UIEntryEmbryo(freshEntry("fresh"), new ArrayList<>(),
                UIEntryNameParameter.ENTRY_NAME_PARAMETER_NONE, UIEntryPriorityScheme.PRIORITY_SCHEME_NONE,
                null, false);

        assertEquals(UIEntryNameParameter.ENTRY_NAME_PARAMETER_NONE, embryo.getParmIndex());
        assertEquals(UIEntryPriorityScheme.PRIORITY_SCHEME_NONE, embryo.getPSourceIndex());
        assertNull(embryo.getLastCategoryIndex(), "C's last_category_index starts at -1: no category yet");
        assertFalse(embryo.exists());
    }

    // ---- setters: round-trip, mirroring later directives overwriting the same embryo fields ---------

    @Test
    void settersOverwriteEachFieldIndependently() {
        UIEntryEmbryo embryo = new UIEntryEmbryo(freshEntry("a"), new ArrayList<>(),
                UIEntryNameParameter.ENTRY_NAME_PARAMETER_NONE, UIEntryPriorityScheme.PRIORITY_SCHEME_NONE,
                null, false);

        UIEntry replacement = freshEntry("b");
        List<EmbryonicCategoryReferencies> replacementCategories = new ArrayList<>();
        EmbryonicCategoryReferencies newLast = new EmbryonicCategoryReferencies(
                new UIEntryCategory("hindrances", 0, false), UIEntryPriorityScheme.PRIORITY_SCHEME_NONE, 0, false);

        embryo.setUiEntry(replacement);
        embryo.setCategories(replacementCategories);
        embryo.setParmIndex(UIEntryNameParameter.ENTRY_NAME_PARAMETER_ELEMENT);
        embryo.setpSourceIndex(UIEntryPriorityScheme.PRIORITY_SCHEME_NEGATIVE_INDEX);
        embryo.setLastCategoryIndex(newLast);
        embryo.setExists(true);

        assertSame(replacement, embryo.getUiEntry());
        assertSame(replacementCategories, embryo.getCategories());
        assertEquals(UIEntryNameParameter.ENTRY_NAME_PARAMETER_ELEMENT, embryo.getParmIndex());
        assertEquals(UIEntryPriorityScheme.PRIORITY_SCHEME_NEGATIVE_INDEX, embryo.getPSourceIndex());
        assertSame(newLast, embryo.getLastCategoryIndex());
        assertTrue(embryo.exists());
    }

    // ---- addCategories: the growable-list half of insert_embryo_category (ui-entry.c:1447-1561), ----
    // ---- simplified from C's manual array reallocation ----------------------------------------------

    @Test
    void addCategoriesAppendsToTheAccumulatedList() {
        List<EmbryonicCategoryReferencies> categories = new ArrayList<>();
        UIEntryEmbryo embryo = new UIEntryEmbryo(freshEntry("t"), categories,
                UIEntryNameParameter.ENTRY_NAME_PARAMETER_NONE, UIEntryPriorityScheme.PRIORITY_SCHEME_NONE,
                null, false);

        EmbryonicCategoryReferencies first = new EmbryonicCategoryReferencies(
                new UIEntryCategory("CHAR_SCREEN1", 0, false), UIEntryPriorityScheme.PRIORITY_SCHEME_NONE, 0, false);
        EmbryonicCategoryReferencies second = new EmbryonicCategoryReferencies(
                new UIEntryCategory("EQUIPCMP_SCREEN", 0, false), UIEntryPriorityScheme.PRIORITY_SCHEME_NONE, 0, false);

        embryo.addCategories(first);
        embryo.addCategories(second);

        assertEquals(List.of(first, second), embryo.getCategories());
    }
}
