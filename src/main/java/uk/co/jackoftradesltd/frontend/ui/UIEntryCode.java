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

package uk.co.jackoftradesltd.frontend.ui;

import uk.co.jackoftradesltd.frontend.entries.UIEntry;
import uk.co.jackoftradesltd.frontend.entries.UIEntryIterator;
import uk.co.jackoftradesltd.frontend.entries.enums.EntryFlag;
import uk.co.jackoftradesltd.frontend.events.UIEntryCategory;
import uk.co.jackoftradesltd.frontend.ui.globals.UIRegistry;

import java.util.List;
import java.util.function.BiPredicate;

public class UIEntryCode {
    private static String sortCategoryName = "";

    public static UIEntryIterator initialiseUIEntryIterator(BiPredicate<String[], UIEntry> categoryCheck,
                                                            String[] closure, String sortCategory) {
        List<UIEntry> entries = UIRegistry.getUIEntries();

        UIEntryIterator it = new UIEntryIterator();

        for (UIEntry entry : entries) {
            if (!entry.entryFlagHas(EntryFlag.ENTRY_FLAG_TEMPLATE_ONLY) && categoryCheck.test(closure, entry))
                it.addEntry(entry);
        }

        sortCategoryName = sortCategory;

        it.getEntries().sort(UIEntryCode::sortFunction);

        sortCategoryName = "";

        return it;
    }

    private static int sortFunction(UIEntry entry1, UIEntry entry2) {
        if (sortCategoryName == null || sortCategoryName.isEmpty())
            return 0;

        int value1 = 0;
        String string1 = entry1.getName();
        boolean found1 = false;
        for (UIEntryCategory category : entry1.getCategories()) {
            if (category.getName().equals(sortCategoryName) && category.isPrioritySet()) {
                value1 = category.getPriority();
                found1 = true;
                break;
            } else if (category.getName().equals(sortCategoryName)) {
                value1 = 0;
                found1 = true;
                break;
            }
        }

        int value2 = 0;
        String string2 = entry2.getName();
        boolean found2 = false;
        for (UIEntryCategory category : entry2.getCategories()) {
            if (category.getName().equals(sortCategoryName) && category.isPrioritySet()) {
                value2 = category.getPriority();
                found2 = true;
                break;
            } else if (category.getName().equals(sortCategoryName)) {
                value2 = 0;
                found2 = true;
                break;
            }
        }

        if (!found1 && found2)
            return 1;
        if (found1 && !found2)
            return -1;

        if (!found1 && !found2) {
            return string2.compareTo(string1);
        }

        // Sort descending
        return Integer.compare(value2, value1);
    }
}
