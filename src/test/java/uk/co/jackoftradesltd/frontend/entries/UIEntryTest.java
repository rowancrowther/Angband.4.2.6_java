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
import uk.co.jackoftradesltd.channel.enums.ChannelEntryFlag;
import uk.co.jackoftradesltd.channel.enums.ElementEnum;
import uk.co.jackoftradesltd.channel.enums.StatElemType;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.channel.utils.combiners.CombinerName;

import java.util.ArrayList;
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
 * flag test ({@link UIEntry#entryFlagHas(ChannelEntryFlag)}, which ports the
 * {@code entry->flags & ENTRY_FLAG_*} bit test), and
 * {@link StatElemType#fromValue(String)} (which ports the linear scan
 * over C's {@code name_parameters[]} table in {@code parse_entry_parameter}).
 *
 * @author Rowan Crowther
 */
class UIEntryTest {

    private static UIEntry entry(List<UIEntryCategory> categories, Flag<ChannelEntryFlag> flags) {
        return new UIEntry("test_entry", null, ElementEnum.ELEM_ACID, StatElemType.ELEMENT,
                null, CombinerName.ADD, categories, 5, null, flags, "some description",
                "Label", "Lbl5", "L2");
    }

    // ---- constructor: description is validated but not stored --------------------------------

    @Test
    void constructorRejectsANullDescription() {
        assertThrows(IllegalArgumentException.class, () ->
                new UIEntry("x", null, null, StatElemType.NONE, null, CombinerName.NONE,
                        List.of(), 0, null, new Flag<>(ChannelEntryFlag.class), null, "L", "L5", "L2"));
    }

    @Test
    void constructorAcceptsANonNullDescriptionAndDoesNotExposeIt() {
        UIEntry e = entry(List.of(), new Flag<>(ChannelEntryFlag.class));

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
                new Flag<>(ChannelEntryFlag.class));

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
                new Flag<>(ChannelEntryFlag.class));

        assertTrue(e.uiEntryHasCategory("NO_SUCH_CATEGORY").isEmpty());
    }

    @Test
    void reportsAbsentWhenThereAreNoCategoriesAtAll() {
        UIEntry e = entry(List.of(), new Flag<>(ChannelEntryFlag.class));

        assertTrue(e.uiEntryHasCategory("ANYTHING").isEmpty());
    }

    // ---- entryFlagHas --------------------------------------------------------------------------

    @Test
    void entryFlagHasReportsASetFlag() {
        UIEntry e = entry(List.of(), new Flag<>(ChannelEntryFlag.class, ChannelEntryFlag.ENTRY_FLAG_TIMED_AS_AUX));

        assertTrue(e.entryFlagHas(ChannelEntryFlag.ENTRY_FLAG_TIMED_AS_AUX));
        assertFalse(e.entryFlagHas(ChannelEntryFlag.ENTRY_FLAG_TEMPLATE_ONLY));
    }

    @Test
    void entryFlagHasReportsFalseWhenNoFlagsAreSet() {
        UIEntry e = entry(List.of(), new Flag<>(ChannelEntryFlag.class));

        assertFalse(e.entryFlagHas(ChannelEntryFlag.ENTRY_FLAG_TIMED_AS_AUX));
        assertFalse(e.entryFlagHas(ChannelEntryFlag.ENTRY_FLAG_TEMPLATE_ONLY));
    }

    // ---- StatElemType.fromValue -----------------------------------------------------------------

    @Test
    void fromValueResolvesTheStatAndElementParameterNames() {
        // Matches the "stat"/"element" entries of C's name_parameters[] (ui-entry.c).
        assertEquals(StatElemType.STAT, StatElemType.fromValue("stat"));
        assertEquals(StatElemType.ELEMENT, StatElemType.fromValue("element"));
    }

    @Test
    void fromValueResolvesTheEmptyStringToNone() {
        // Matches C's dummy "" entry in name_parameters[], used when parameter: is unset.
        assertEquals(StatElemType.NONE, StatElemType.fromValue(""));
    }

    @Test
    void fromValueReturnsNullForAnUnrecognisedName() {
        // Matches parse_entry_parameter (ui-entry.c) falling through to PARSE_ERROR_INVALID_VALUE.
        assertNull(StatElemType.fromValue("bogus"));
    }

    // ---- uiEntryHasCategory: strcmp semantics -------------------------------------------------

    @Test
    void categoryMatchIsCaseSensitiveAsStrcmpIs() {
        // ui_entry_search_categories compares with strcmp (ui-entry.c): "char_screen1" is not
        // "CHAR_SCREEN1".
        UIEntry e = entry(List.of(new UIEntryCategory("CHAR_SCREEN1", 0, false)),
                new Flag<>(ChannelEntryFlag.class));

        assertTrue(e.uiEntryHasCategory("char_screen1").isEmpty());
    }

    @Test
    void categoryMatchNeedsTheWholeName() {
        // strcmp of a prefix or an extension of the stored name is nonzero.
        UIEntry e = entry(List.of(new UIEntryCategory("CHAR_SCREEN1", 0, false)),
                new Flag<>(ChannelEntryFlag.class));

        assertTrue(e.uiEntryHasCategory("CHAR_SCREEN").isEmpty());
        assertTrue(e.uiEntryHasCategory("CHAR_SCREEN12").isEmpty());
        assertTrue(e.uiEntryHasCategory("").isEmpty());
    }

    @Test
    void categoryMatchFindsTheMiddleAndLastOfSeveral() {
        // Names in strcmp order, as insert_embryo_category keeps them; C's binary search finds each.
        UIEntry e = entry(List.of(
                        new UIEntryCategory("A_CAT", 0, false),
                        new UIEntryCategory("B_CAT", 0, false),
                        new UIEntryCategory("C_CAT", 0, false),
                        new UIEntryCategory("D_CAT", 0, false)),
                new Flag<>(ChannelEntryFlag.class));

        assertEquals(Optional.of(0), e.uiEntryHasCategory("A_CAT"));
        assertEquals(Optional.of(1), e.uiEntryHasCategory("B_CAT"));
        assertEquals(Optional.of(2), e.uiEntryHasCategory("C_CAT"));
        assertEquals(Optional.of(3), e.uiEntryHasCategory("D_CAT"));
        assertTrue(e.uiEntryHasCategory("AA_CAT").isEmpty());
        assertTrue(e.uiEntryHasCategory("E_CAT").isEmpty());
    }

    // ---- labels: shortened_labels[] slots ------------------------------------------------------

    @Test
    void constructorSeedsLabelAndTheTwoShortenedSlotsC() {
        // label2: fills shortened_labels[1], label5: fills shortened_labels[4] (parse_entry_shortened_label).
        UIEntry e = entry(List.of(), new Flag<>(ChannelEntryFlag.class));

        assertEquals("Label", e.getLabel());
        assertEquals("L2", e.getLabel2());
        assertEquals("Lbl5", e.getLabel5());
        assertEquals("L2", e.getShortenedLabel(1));
        assertEquals("Lbl5", e.getShortenedLabel(4));
    }

    @Test
    void unseededShortenedSlotsAreUnset() {
        // nshortened[i] == 0 for every slot no label directive filled.
        UIEntry e = entry(List.of(), new Flag<>(ChannelEntryFlag.class));

        for (int index : new int[]{0, 2, 3, 5, 6, 7, 8, 9}) {
            assertNull(e.getShortenedLabel(index), "slot " + index);
        }
    }

    @Test
    void labelSettersWriteTheSameSlotsTheGettersRead() {
        UIEntry e = entry(List.of(), new Flag<>(ChannelEntryFlag.class));

        e.setLabel("Other");
        e.setLabel2("O2");
        e.setLabel5("Oth5");
        e.setShortenedLabel(7, "Other_7");

        assertEquals("Other", e.getLabel());
        assertEquals("O2", e.getShortenedLabel(1));
        assertEquals("Oth5", e.getShortenedLabel(4));
        assertEquals("Other_7", e.getShortenedLabel(7));
        assertEquals("O2", e.getLabel2());
        assertEquals("Oth5", e.getLabel5());
    }

    @Test
    void settingOneShortenedSlotLeavesTheOthersAlone() {
        UIEntry e = entry(List.of(), new Flag<>(ChannelEntryFlag.class));

        e.setShortenedLabel(2, "Lab");

        assertEquals("Lab", e.getShortenedLabel(2));
        assertEquals("L2", e.getLabel2());
        assertEquals("Lbl5", e.getLabel5());
        assertNull(e.getShortenedLabel(3));
    }

    // ---- priority ------------------------------------------------------------------------------

    @Test
    void priorityAccessorsAreSynonymsForOneField() {
        UIEntry e = entry(List.of(), new Flag<>(ChannelEntryFlag.class));

        assertEquals(5, e.getPriorityNum());
        assertEquals(5, e.getDefaultPriority());

        e.setDefaultPriority(-3);
        assertEquals(-3, e.getPriorityNum());
        assertEquals(-3, e.getDefaultPriority());

        e.setPriorityNum(17);
        assertEquals(17, e.getPriorityNum());
        assertEquals(17, e.getDefaultPriority());
    }

    @Test
    void settingThePriorityLeavesTheSchemeTextAlone() {
        UIEntry e = new UIEntry("e", null, ElementEnum.ELEM_NONE, StatElemType.NONE, null, CombinerName.ADD,
                List.of(), 4, "index", new Flag<>(ChannelEntryFlag.class), "d", "L", "L5", "L2");

        e.setPriorityNum(9);

        assertEquals("index", e.getPriorityString());
        e.setPriorityString("negative_index");
        assertEquals("negative_index", e.getPriorityString());
        assertEquals(9, e.getPriorityNum());
    }

    // ---- setParamIndex: C's param_index -------------------------------------------------------

    @Test
    void setParamIndexOnAStatEntryStoresTheIndexAndLeavesTheElementAlone() {
        UIEntry e = new UIEntry("e", null, ElementEnum.ELEM_NONE, StatElemType.STAT, null, CombinerName.ADD,
                List.of(), 0, "", new Flag<>(ChannelEntryFlag.class), "d", "L", "L5", "L2");

        e.setParamIndex(3);

        assertEquals(3, e.getStatParameter());
        assertEquals(ElementEnum.ELEM_NONE, e.getParameter());
    }

    @Test
    void setParamIndexOnAnElementEntryFollowsCsElementOrder() {
        // list-elements.h: ACID is index 0, ELEC 1, ... ARROW 24 (25 elements).
        UIEntry e = entry(List.of(), new Flag<>(ChannelEntryFlag.class));

        e.setParamIndex(0);
        assertEquals(ElementEnum.ELEM_ACID, e.getParameter());
        e.setParamIndex(1);
        assertEquals(ElementEnum.ELEM_ELEC, e.getParameter());
        e.setParamIndex(4);
        assertEquals(ElementEnum.ELEM_POIS, e.getParameter());
        e.setParamIndex(13);
        assertEquals(ElementEnum.ELEM_WATER, e.getParameter());
        e.setParamIndex(23);
        assertEquals(ElementEnum.ELEM_HOLY_ORB, e.getParameter());
        e.setParamIndex(24);
        assertEquals(ElementEnum.ELEM_ARROW, e.getParameter());
    }

    @Test
    void setParamIndexOnAnElementEntryLeavesTheStatIndexAlone() {
        UIEntry e = entry(List.of(), new Flag<>(ChannelEntryFlag.class));

        e.setParamIndex(2);

        assertEquals(-1, e.getStatParameter());
        assertEquals(ElementEnum.ELEM_FIRE, e.getParameter());
    }

    @Test
    void setParamIndexOnAnUnparameterizedEntryGivesCsMinusOne() {
        // hatch_embryo: an unparameterized entry gets param_index = -1.
        UIEntry e = new UIEntry("e", null, ElementEnum.ELEM_ACID, StatElemType.NONE, null, CombinerName.ADD,
                List.of(), 0, "", new Flag<>(ChannelEntryFlag.class), "d", "L", "L5", "L2");
        e.setStatParameter(6);

        e.setParamIndex(5);

        assertEquals(-1, e.getStatParameter());
        assertEquals(ElementEnum.ELEM_NONE, e.getParameter());
    }

    @Test
    void aFreshEntryHasNoStatIndex() {
        assertEquals(-1, entry(List.of(), new Flag<>(ChannelEntryFlag.class)).getStatParameter());
    }

    // ---- flags ---------------------------------------------------------------------------------

    @Test
    void setEntryFlagsCopiesTheCallersSet() {
        // C copies flags by value (entry->flags = ...), so later changes to the source must not show.
        UIEntry e = entry(List.of(), new Flag<>(ChannelEntryFlag.class));
        Flag<ChannelEntryFlag> source = new Flag<>(ChannelEntryFlag.class, ChannelEntryFlag.ENTRY_FLAG_TIMED_AS_AUX);

        e.setEntryFlags(source);
        source.on(ChannelEntryFlag.ENTRY_FLAG_TEMPLATE_ONLY);
        source.off(ChannelEntryFlag.ENTRY_FLAG_TIMED_AS_AUX);

        assertTrue(e.entryFlagHas(ChannelEntryFlag.ENTRY_FLAG_TIMED_AS_AUX));
        assertFalse(e.entryFlagHas(ChannelEntryFlag.ENTRY_FLAG_TEMPLATE_ONLY));
    }

    @Test
    void setEntryFlagsReplacesRatherThanMerges() {
        UIEntry e = entry(List.of(), new Flag<>(ChannelEntryFlag.class, ChannelEntryFlag.ENTRY_FLAG_TEMPLATE_ONLY));

        e.setEntryFlags(new Flag<>(ChannelEntryFlag.class, ChannelEntryFlag.ENTRY_FLAG_TIMED_AS_AUX));

        assertTrue(e.entryFlagHas(ChannelEntryFlag.ENTRY_FLAG_TIMED_AS_AUX));
        assertFalse(e.entryFlagHas(ChannelEntryFlag.ENTRY_FLAG_TEMPLATE_ONLY));
    }

    @Test
    void getEntryFlagViewReflectsTheEntrysFlags() {
        UIEntry e = entry(List.of(), new Flag<>(ChannelEntryFlag.class, ChannelEntryFlag.ENTRY_FLAG_TIMED_AS_AUX));

        assertTrue(e.getEntryFlag().has(ChannelEntryFlag.ENTRY_FLAG_TIMED_AS_AUX));
        assertFalse(e.getEntryFlag().has(ChannelEntryFlag.ENTRY_FLAG_TEMPLATE_ONLY));
    }

    // ---- categories ----------------------------------------------------------------------------

    @Test
    void getNCategoryIsTheCategoryCount() {
        // n_category in struct ui_entry.
        assertEquals(0, entry(List.of(), new Flag<>(ChannelEntryFlag.class)).getNCategory());
        assertEquals(2, entry(List.of(
                        new UIEntryCategory("A", 0, false),
                        new UIEntryCategory("B", 0, false)),
                new Flag<>(ChannelEntryFlag.class)).getNCategory());
    }

    @Test
    void setCategoresReplacesTheList() {
        UIEntry e = entry(List.of(new UIEntryCategory("A", 0, false)), new Flag<>(ChannelEntryFlag.class));

        e.setCategores(List.of(new UIEntryCategory("B", 0, false), new UIEntryCategory("C", 0, false)));

        assertEquals(2, e.getNCategory());
        assertTrue(e.uiEntryHasCategory("A").isEmpty());
        assertEquals(Optional.of(1), e.uiEntryHasCategory("C"));
    }

    // ---- copy ----------------------------------------------------------------------------------

    @Test
    void copyCarriesEveryScalarField() {
        // hatch_embryo gives each expanded entry the embryo's renderer, combiner, priority, flags
        // and labels.
        UIEntry template = entry(List.of(), new Flag<>(ChannelEntryFlag.class));
        UIEntry original = new UIEntry("orig", template, ElementEnum.ELEM_COLD, StatElemType.ELEMENT, null,
                CombinerName.ADD, new ArrayList<>(List.of(new UIEntryCategory("CAT", 7, true))), 12, "index",
                new Flag<>(ChannelEntryFlag.class, ChannelEntryFlag.ENTRY_FLAG_TIMED_AS_AUX),
                "desc", "Full label", "Lbl5", "L2");
        original.setStatParameter(4);

        UIEntry copy = original.copy();

        assertNotSame(original, copy);
        assertEquals("orig", copy.getName());
        assertSame(template, copy.getTemplate());
        assertEquals(ElementEnum.ELEM_COLD, copy.getParameter());
        assertEquals(StatElemType.ELEMENT, copy.getStatOrElement());
        assertEquals(4, copy.getStatParameter());
        assertEquals(CombinerName.ADD, copy.getCombineType());
        assertEquals(12, copy.getPriorityNum());
        assertEquals("index", copy.getPriorityString());
        assertEquals("Full label", copy.getLabel());
        assertEquals("L2", copy.getLabel2());
        assertEquals("Lbl5", copy.getLabel5());
        assertTrue(copy.entryFlagHas(ChannelEntryFlag.ENTRY_FLAG_TIMED_AS_AUX));
        assertEquals(1, copy.getNCategory());
        assertEquals("CAT", copy.getCategories().get(0).getName());
        assertEquals(7, copy.getCategories().get(0).getPriority());
        assertTrue(copy.getCategories().get(0).isPrioritySet());
    }

    @Test
    void copyCarriesAllTenShortenedSlots() {
        // copy_shortened_labels copies all MAX_SHORTENED (10) slots.
        UIEntry original = entry(List.of(), new Flag<>(ChannelEntryFlag.class));
        for (int index = 0; index < 10; index++) {
            original.setShortenedLabel(index, "s" + index);
        }

        UIEntry copy = original.copy();

        for (int index = 0; index < 10; index++) {
            assertEquals("s" + index, copy.getShortenedLabel(index), "slot " + index);
        }
    }

    @Test
    void copyHasItsOwnCategoryList() {
        UIEntry original = entry(new ArrayList<>(List.of(new UIEntryCategory("CAT", 1, false))),
                new Flag<>(ChannelEntryFlag.class));

        UIEntry copy = original.copy();
        copy.getCategories().get(0).setPriority(99);
        copy.getCategories().add(new UIEntryCategory("EXTRA", 0, false));

        assertEquals(1, original.getNCategory());
        assertEquals(1, original.getCategories().get(0).getPriority());
        assertEquals(2, copy.getNCategory());
    }

    @Test
    void copyHasItsOwnFlagsAndLabels() {
        UIEntry original = entry(List.of(), new Flag<>(ChannelEntryFlag.class, ChannelEntryFlag.ENTRY_FLAG_TIMED_AS_AUX));

        UIEntry copy = original.copy();
        copy.setEntryFlags(new Flag<>(ChannelEntryFlag.class, ChannelEntryFlag.ENTRY_FLAG_TEMPLATE_ONLY));
        copy.setLabel("Changed");
        copy.setLabel2("C2");
        copy.setName("changed<NAME>");
        copy.setPriorityNum(-1);

        assertTrue(original.entryFlagHas(ChannelEntryFlag.ENTRY_FLAG_TIMED_AS_AUX));
        assertFalse(original.entryFlagHas(ChannelEntryFlag.ENTRY_FLAG_TEMPLATE_ONLY));
        assertEquals("Label", original.getLabel());
        assertEquals("L2", original.getLabel2());
        assertEquals("test_entry", original.getName());
        assertEquals(5, original.getPriorityNum());
    }

    @Test
    void copyOfAnUnparameterizedEntryKeepsMinusOneAndNone() {
        UIEntry original = new UIEntry("e", null, ElementEnum.ELEM_NONE, StatElemType.NONE, null, CombinerName.ADD,
                List.of(), 0, "", new Flag<>(ChannelEntryFlag.class), "d", "L", "L5", "L2");

        UIEntry copy = original.copy();

        assertEquals(StatElemType.NONE, copy.getStatOrElement());
        assertEquals(-1, copy.getStatParameter());
        assertEquals(ElementEnum.ELEM_NONE, copy.getParameter());
        assertNull(copy.getTemplate());
    }
}
