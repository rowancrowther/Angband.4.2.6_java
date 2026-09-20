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
import uk.co.jackoftradesltd.frontend.entries.UIEntryCategory;
import uk.co.jackoftradesltd.frontend.ui.entry.assembler.helperfunctions.UIEntryPriorityScheme;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link EmbryonicCategoryReferencies}, checked against C's
 * {@code struct embryonic_category_reference} ({@code [C] ui-entry.c:163-168}: {@code name},
 * {@code psource_index}, {@code priority}, {@code priority_set}) and the sorted-by-name invariant
 * {@code insert_embryo_category} ({@code [C] ui-entry.c:1447-1561}) maintains over an entry's
 * in-progress category list via binary search.
 *
 * @author Rowan Crowther
 */
class EmbryonicCategoryReferenciesTest {

    // ---- constructor: direct field storage, mirroring insert_embryo_category's field assignments ---

    private static EmbryonicCategoryReferencies withCategoryName(String name) {
        return new EmbryonicCategoryReferencies(
                new UIEntryCategory(name, 0, false), UIEntryPriorityScheme.PRIORITY_SCHEME_NONE, 0, false);
    }

    @Test
    void constructorStoresASchemeDerivedPriorityVerbatim() {
        UIEntryCategory category = new UIEntryCategory("stat_modifiers", 0, false);

        EmbryonicCategoryReferencies ref = new EmbryonicCategoryReferencies(
                category, UIEntryPriorityScheme.PRIORITY_SCHEME_NEGATIVE_INDEX, 0, true);

        assertSame(category, ref.getCategory());
        assertEquals(UIEntryPriorityScheme.PRIORITY_SCHEME_NEGATIVE_INDEX, ref.getpSourceIndex());
        assertEquals(0, ref.getPriority());
        assertTrue(ref.isPrioritySet());
    }

    // ---- unset priority: mirrors insert_embryo_category's call from parse_entry_category, which ----
    // ---- always passes priority_set=false (ui-entry.c:2114-2130) -----------------------------------

    @Test
    void constructorStoresALiteralPriorityWithNoScheme() {
        UIEntryCategory category = new UIEntryCategory("CHAR_SCREEN1", 3, true);

        EmbryonicCategoryReferencies ref = new EmbryonicCategoryReferencies(
                category, UIEntryPriorityScheme.PRIORITY_SCHEME_NONE, 3, true);

        assertEquals(UIEntryPriorityScheme.PRIORITY_SCHEME_NONE, ref.getpSourceIndex());
        assertEquals(3, ref.getPriority());
        assertTrue(ref.isPrioritySet());
    }

    // ---- compareTo: the sorted-by-category-name order insert_embryo_category's binary search -------
    // ---- (ui-entry.c:1447-1561) relies on ------------------------------------------------------------

    @Test
    void constructorStoresAnUnsetPriorityAsPassedRatherThanNormalisingItToZero() {
        UIEntryCategory category = new UIEntryCategory("resistances", 7, false);

        EmbryonicCategoryReferencies ref = new EmbryonicCategoryReferencies(
                category, UIEntryPriorityScheme.PRIORITY_SCHEME_NONE, 7, false);

        assertFalse(ref.isPrioritySet());
        // The embryonic form does not zero an unset priority the way the finished
        // UIEntryCategory's own default construction path does - it just carries
        // through whatever was passed, since finish_parse_ui_entry resolves it later.
        assertEquals(7, ref.getPriority());
    }

    @Test
    void compareToOrdersByCategoryNameAscending() {
        EmbryonicCategoryReferencies a = withCategoryName("abilities");
        EmbryonicCategoryReferencies b = withCategoryName("hindrances");

        assertTrue(a.compareTo(b) < 0);
        assertTrue(b.compareTo(a) > 0);
    }

    @Test
    void compareToReportsEqualCategoryNamesAsZero() {
        EmbryonicCategoryReferencies a = withCategoryName("modifiers");
        EmbryonicCategoryReferencies b = withCategoryName("modifiers");

        assertEquals(0, a.compareTo(b));
    }

    @Test
    void sortingByThisOrderReproducesTheArrayInvariantInsertEmbryoCategoryMaintains() {
        List<EmbryonicCategoryReferencies> refs = new ArrayList<>(List.of(
                withCategoryName("resistances"), withCategoryName("CHAR_SCREEN1"),
                withCategoryName("EQUIPCMP_SCREEN"), withCategoryName("abilities")));

        Collections.sort(refs);

        List<String> names = refs.stream().map(r -> r.getCategory().getName()).toList();
        assertEquals(List.of("CHAR_SCREEN1", "EQUIPCMP_SCREEN", "abilities", "resistances"), names);
    }
}
