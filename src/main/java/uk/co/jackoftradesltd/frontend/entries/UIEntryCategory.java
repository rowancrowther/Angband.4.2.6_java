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

/**
 * One resolved (category name, priority) pairing for a {@link
 * uk.co.jackoftradesltd.frontend.entries.UIEntry}, the Java form of C's
 * {@code struct category_reference} ({@code [C] ui-entry.c:55-59}: {@code
 * name}, {@code priority}, {@code priority_set}). C keeps a separate {@code
 * struct embryonic_category_reference} ({@code [C] ui-entry.c:163-168}) while
 * parsing {@code ui_entry.txt}, carrying an extra {@code psource_index} used
 * to look up an automatic priority (by parameter index or negative index)
 * before the priority is resolved to a plain {@code int} and copied across
 * ({@code finish_ui_entry}, {@code [C] ui-entry.c:1660-1680}); this class
 * models only that resolved, post-parse form, matching how {@code
 * UIEntryAssembler} builds instances with the priority already known.
 * {@code prioritySet} distinguishes an explicit priority from one left at its
 * category default, mirroring the same distinction the ordering comparison
 * in C's {@code ui_entry_combine_category_order} relies on ({@code [C]
 * ui-entry.c:414-418}).
 *
 * <p>Class UIEntryCategory coded before 260916, commented in full on 260916.
 *
 * @author Rowan Crowther
 */
public class UIEntryCategory {
    /**
     * The category's name. The Java form of C's {@code
     * category_reference.name} ({@code [C] ui-entry.c:56}).
     *
     * <p>Field name coded before 260916, commented in full on 260916.
     */
    private String name;
    /**
     * The category's priority; only meaningful when {@link #prioritySet} is
     * {@code true}. The Java form of C's {@code category_reference.priority}
     * ({@code [C] ui-entry.c:57}), used by the descending-priority ordering
     * in {@code ui_entry_combine_category_order} ({@code [C]
     * ui-entry.c:414-418}).
     *
     * <p>Field priority coded before 260916, commented in full on 260916.
     */
    private int priority;
    /**
     * Whether {@link #priority} was explicitly set for this category, as
     * opposed to left at its unresolved default. The Java form of C's
     * {@code category_reference.priority_set} ({@code [C] ui-entry.c:58}).
     *
     * <p>Field prioritySet coded before 260916, commented in full on 260916.
     */
    private boolean prioritySet;

    /**
     * Build a resolved category reference from its name, priority and
     * whether that priority was explicitly set. The Java form of the direct
     * field assignment C performs once a category's priority is resolved
     * ({@code finish_ui_entry}, {@code [C] ui-entry.c:1670-1678}).
     *
     * @param name        the category's name
     * @param priority    the category's priority
     * @param prioritySet whether {@code priority} was explicitly set
     *
     *                    <p>Function UIEntryCategory(String, int, boolean) coded before 260916,
     *                    commented in full on 260916.
     */
    public UIEntryCategory(String name, int priority, boolean prioritySet) {
        this.name = name;
        this.priority = priority;
        this.prioritySet = prioritySet;
    }

    /**
     * Getter - this category's name.
     *
     * @return the category's name
     *
     * <p>Function getName() coded before 260916, commented in full on
     * 260916.
     */
    public String getName() {
        return name;
    }

    /**
     * Getter - this category's priority. Only meaningful when {@link
     * #isPrioritySet()} is {@code true}.
     *
     * @return the category's priority
     *
     * <p>Function getPriority() coded before 260916, commented in full on
     * 260916.
     */
    public int getPriority() {
        return priority;
    }

    /**
     * Getter - whether this category's priority was explicitly set.
     *
     * @return {@code true} if the priority was explicitly set
     *
     * <p>Function isPrioritySet() coded before 260916, commented in full on
     * 260916.
     */
    public boolean isPrioritySet() {
        return prioritySet;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public void setPrioritySet(boolean set) {
        this.prioritySet = set;
    }
}
