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

import org.jspecify.annotations.NonNull;
import uk.co.jackoftradesltd.frontend.entries.UIEntryCategory;
import uk.co.jackoftradesltd.frontend.ui.entry.assembler.helperfunctions.UIEntryPriorityScheme;

/**
 * One in-progress (category, priority) pairing for a {@code ui_entry.txt}/{@code ui_entry_base.txt}
 * record that has not yet finished parsing, the Java form of C's
 * {@code struct embryonic_category_reference} ({@code [C] ui-entry.c:163-168}: {@code name},
 * {@code psource_index}, {@code priority}, {@code priority_set}). Where the finished
 * {@link UIEntryCategory} ({@code struct category_reference}, {@code [C] ui-entry.c:55-59}) holds
 * only a plain resolved {@code priority} int, this embryonic form additionally carries the
 * {@code psource_index} bookkeeping C uses to defer that resolution: a category seen before its
 * record's {@code priority:} directive is inserted with a priority scheme but no fixed number yet,
 * and only becomes a plain {@code priority} once {@code parameterize_category_list}
 * ({@code [C] ui-entry.c:1657-1680}) or {@code finish_parse_ui_entry} ({@code [C]
 * ui-entry.c:2283-2330}) turns the whole embryo into an entry's real category array.
 *
 * <p>Two of this class's fields already hold the resolved objects C only reaches at the end of that
 * process: {@link #category} is a full {@link UIEntryCategory} rather than C's raw {@code name}
 * string, and {@link #pSourceIndex} is the {@link UIEntryPriorityScheme} constant itself rather than
 * C's {@code psource_index} table offset. Holding the resolved objects directly here is what lets
 * {@link #getpSourceIndex()} call {@link UIEntryPriorityScheme#getPriority(int)} without a second
 * table lookup.
 *
 * <p>Class EmbryonicCategoryReferencies coded before 260920, commented in full on 260920.
 *
 * @author Rowan Crowther
 */
public class EmbryonicCategoryReferencies implements Comparable<EmbryonicCategoryReferencies> {
    /**
     * Whether {@link #priority} has been explicitly set for this category, as opposed to left at its
     * unresolved default pending a later {@code priority:} directive or the record's finishing pass.
     * The Java form of C's {@code embryonic_category_reference.priority_set}
     * ({@code [C] ui-entry.c:167}).
     *
     * <p>Field prioritySet coded before 260920, commented in full on 260920.
     */
    boolean prioritySet;
    /**
     * The category this priority is being assembled for. The Java form of C's
     * {@code embryonic_category_reference.name} ({@code [C] ui-entry.c:164}), except that this holds
     * the resolved {@link UIEntryCategory} rather than the raw name C carries at this stage.
     *
     * <p>Field category coded before 260920, commented in full on 260920.
     */
    private UIEntryCategory category;
    /**
     * The priority scheme this category's priority is drawn from, when {@link #prioritySet} is
     * {@code true} and the priority was set via a scheme (such as {@code index} or
     * {@code negative_index}) rather than a literal number. The Java form of C's
     * {@code embryonic_category_reference.psource_index} ({@code [C] ui-entry.c:165}), except that
     * this holds the resolved {@link UIEntryPriorityScheme} constant rather than C's table index.
     *
     * <p>Field pSourceIndex coded before 260920, commented in full on 260920.
     */
    private UIEntryPriorityScheme pSourceIndex;
    /**
     * The category's literal priority, meaningful only when {@link #prioritySet} is {@code true} and
     * {@link #pSourceIndex} is {@link UIEntryPriorityScheme#PRIORITY_SCHEME_NONE}. The Java form of
     * C's {@code embryonic_category_reference.priority} ({@code [C] ui-entry.c:166}).
     *
     * <p>Field priority coded before 260920, commented in full on 260920.
     */
    private int priority;
    /**
     * Build an embryonic category reference from its category, priority scheme, literal priority and
     * whether a priority has been set at all. The Java form of the field assignments C performs
     * inline in {@code insert_embryo_category} ({@code [C] ui-entry.c:1447-1561}) when a new category
     * is inserted into a record's in-progress category list.
     *
     * <p>Function EmbryonicCategoryReferencies coded before 260920, commented in full on 260920.
     *
     * @param category     the category this priority is being assembled for
     * @param pSourceIndex the priority scheme to draw the priority from, or
     *                     {@link UIEntryPriorityScheme#PRIORITY_SCHEME_NONE} for a literal priority
     * @param priority     the literal priority, used when {@code pSourceIndex} is
     *                     {@code PRIORITY_SCHEME_NONE}
     * @param prioritySet  whether a priority has been explicitly set for this category yet
     */
    public EmbryonicCategoryReferencies(UIEntryCategory category, UIEntryPriorityScheme pSourceIndex, int priority, boolean prioritySet) {
        this.category = category;
        this.pSourceIndex = pSourceIndex;
        this.priority = priority;
        this.prioritySet = prioritySet;
    }

    /**
     * Orders two embryonic category references by their category's name, matching the way C keeps
     * both {@code categories} (the global list of category names seen so far, {@code [C]
     * ui-entry.c:1417-1440}) and each entry's own {@code categories} array sorted by name for binary
     * search. Has no direct C counterpart as a named function - C never sorts
     * {@code embryonic_category_reference}s with a comparator, since {@code insert_embryo_category}
     * ({@code [C] ui-entry.c:1447-1561}) keeps the array sorted by inserting at the position a binary
     * search already found - but this is the ordering that invariant depends on.
     *
     * <p>Function compareTo coded before 260920, commented in full on 260920.
     *
     * @param o the embryonic category reference to compare against
     * @return the result of comparing the two categories' names, as {@link String#compareTo(String)}
     */
    @Override
    public int compareTo(@NonNull EmbryonicCategoryReferencies o) {
        return this.getCategory().getName().compareTo(o.getCategory().getName());
    }

    /**
     * Returns the category this priority is being assembled for.
     *
     * <p>Function getCategory coded before 260920, commented in full on 260920.
     *
     * @return this reference's category
     */
    public UIEntryCategory getCategory() {
        return category;
    }

    /**
     * Returns the priority scheme this category's priority is drawn from.
     *
     * <p>Function getpSourceIndex coded before 260920, commented in full on 260920.
     *
     * @return this reference's priority scheme, or {@link UIEntryPriorityScheme#PRIORITY_SCHEME_NONE}
     * if the priority is a literal number rather than scheme-derived
     */
    public UIEntryPriorityScheme getpSourceIndex() {
        return pSourceIndex;
    }

    /**
     * Returns this category's literal priority. Only meaningful when {@link #isPrioritySet()} is
     * {@code true} and {@link #getpSourceIndex()} is {@link UIEntryPriorityScheme#PRIORITY_SCHEME_NONE}.
     *
     * <p>Function getPriority coded before 260920, commented in full on 260920.
     *
     * @return this reference's literal priority
     */
    public int getPriority() {
        return priority;
    }

    /**
     * Returns whether this category's priority has been explicitly set yet.
     *
     * <p>Function isPrioritySet coded before 260920, commented in full on 260920.
     *
     * @return {@code true} if the priority has been explicitly set
     */
    public boolean isPrioritySet() {
        return prioritySet;
    }
}
