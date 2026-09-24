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

import uk.co.jackoftradesltd.channel.enums.ChannelEntryFlag;
import uk.co.jackoftradesltd.frontend.entries.UIEntry;
import uk.co.jackoftradesltd.frontend.entries.UIEntryIterator;
import uk.co.jackoftradesltd.frontend.entries.UIEntryCategory;
import uk.co.jackoftradesltd.frontend.ui.globals.UIRegistry;

import java.util.List;
import java.util.function.BiPredicate;

/**
 * Builds {@link UIEntryIterator}s over {@link UIRegistry}'s entries - the port of C's
 * {@code initialize_ui_entry_iterator} and its private sort comparator {@code cmp_desc_prio}
 * ({@code [C] ui-entry.c:392-483}). C's {@code sort()}/{@code qsort}-based comparator has no closure
 * argument, so the category it sorts by is threaded through a file-scope static,
 * {@code category_for_cmp_desc_prio} ({@code [C] ui-entry.c:392}); {@link #sortCategoryName} is that
 * same shape carried into Java, since {@link List#sort} does not pass extra state to its comparator
 * either.
 *
 * <p>Class UIEntryCode coded before 260924, commented in full on 260924.
 *
 * @author Rowan Crowther
 */
public class UIEntryCode {
    /**
     * The category whose priority the in-progress sort orders entries by, or the empty string when
     * sorting by name alone. The Java form of C's file-scope {@code category_for_cmp_desc_prio}
     * ({@code [C] ui-entry.c:392}), set for the duration of one {@link #initialiseUIEntryIterator}
     * call and cleared to {@code ""} immediately after, since {@link #sortFunction} has no other way
     * to reach it.
     *
     * <p>Field sortCategoryName coded before 260924, commented in full on 260924.
     */
    private static String sortCategoryName = "";

    /**
     * Builds an iterator over every {@link UIRegistry} entry for which {@code categoryCheck} returns
     * true, excluding template-only entries, ordered by descending priority within
     * {@code sortCategory} - the port of C's {@code initialize_ui_entry_iterator}
     * ({@code [C] ui-entry.c:463-483}). Where C tests each candidate with a predicate taking the entry
     * and a {@code void *closure}, this takes the closure array first and the entry second, since
     * {@link BiPredicate} fixes the argument order but not which side is which; the effect - one
     * predicate call per entry, same true/false outcome - is the same either way.
     *
     * <p>{@link ChannelEntryFlag#ENTRY_FLAG_TEMPLATE_ONLY} is filtered here rather than left to
     * {@code categoryCheck}, matching C's {@code entries[i]->flags & ENTRY_FLAG_TEMPLATE_ONLY} check
     * ({@code [C] ui-entry.c:473}) sitting alongside the predicate call rather than inside it - a
     * {@code ui_entry_base.txt}-derived entry is never a candidate for any iterator, regardless of
     * what the predicate would have said.
     *
     * <p>Function initialiseUIEntryIterator coded before 260924, commented in full on 260924.
     *
     * @param categoryCheck the predicate an entry must satisfy to be included, called with
     *                      {@code closure} first and the candidate entry second
     * @param closure       the value passed as {@code categoryCheck}'s first argument for every entry
     * @param sortCategory  the category whose priority sets the enumeration order, or {@code null}/
     *                      empty to sort by name alone
     * @return an iterator over the matching entries, in descending {@code sortCategory} priority order
     */
    public static UIEntryIterator initialiseUIEntryIterator(BiPredicate<String[], UIEntry> categoryCheck,
                                                            String[] closure, String sortCategory) {
        List<UIEntry> entries = UIRegistry.getUIEntries();

        UIEntryIterator it = new UIEntryIterator();

        for (UIEntry entry : entries) {
            if (!entry.entryFlagHas(ChannelEntryFlag.ENTRY_FLAG_TEMPLATE_ONLY) && categoryCheck.test(closure, entry))
                it.addEntry(entry);
        }

        sortCategoryName = sortCategory;

        it.getEntries().sort(UIEntryCode::sortFunction);

        sortCategoryName = "";

        return it;
    }

    /**
     * Compares two entries for {@link #initialiseUIEntryIterator}'s sort - the port of C's
     * {@code cmp_desc_prio} ({@code [C] ui-entry.c:393-442}). When {@link #sortCategoryName} is unset,
     * both entries sort by name alone, matching C falling through to {@code strcmp} when neither side
     * is found in the sort category (C reaches the same {@code strcmp} branch whenever
     * {@code category_for_cmp_desc_prio} is {@code NULL}, since the category search is never even
     * attempted). Otherwise: an entry carrying {@code sortCategoryName} sorts before one that does
     * not; between two entries that both carry it, the higher {@link UIEntryCategory#getPriority()}
     * sorts first, with name as the tiebreak; between two that carry neither, name alone decides.
     *
     * <p>Function sortFunction coded before 260924, commented in full on 260924.
     *
     * @param entry1 the first entry to compare
     * @param entry2 the second entry to compare
     * @return a negative number if {@code entry1} sorts first, a positive number if {@code entry2}
     * sorts first, or {@code 0} if they are equivalent for sorting purposes
     */
    private static int sortFunction(UIEntry entry1, UIEntry entry2) {
        if (sortCategoryName == null || sortCategoryName.isEmpty())
            return entry1.getName().compareTo(entry2.getName());

        int value1 = 0;
        String string1 = entry1.getName();
        boolean found1 = false;
        for (UIEntryCategory category : entry1.getCategories()) {
            if (category.getName().equals(sortCategoryName)) {
                value1 = category.getPriority();
                found1 = true;
                break;
            }
        }

        int value2 = 0;
        String string2 = entry2.getName();
        boolean found2 = false;
        for (UIEntryCategory category : entry2.getCategories()) {
            if (category.getName().equals(sortCategoryName)) {
                value2 = category.getPriority();
                found2 = true;
                break;
            }
        }

        if (!found1 && found2)
            return 1;
        if (found1 && !found2)
            return -1;

        if (!found1 && !found2) {
            return string1.compareTo(string2);
        }

        // Sort descending
        if (value1 == value2)
            return string1.compareTo(string2);

        return Integer.compare(value2, value1);
    }
}
