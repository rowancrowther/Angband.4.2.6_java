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
 * {@code struct category_reference} in {@code ui-entry.c} ({@code name},
 * {@code priority}, {@code priority_set}). C keeps a separate {@code struct
 * embryonic_category_reference} while parsing {@code ui_entry.txt}, carrying
 * an extra {@code psource_index} used to look up an automatic priority (by
 * parameter index or negative index) before the priority is resolved to a
 * plain {@code int} and copied across ({@code parameterize_category_list} in
 * {@code ui-entry.c}); this class models only that resolved form, matching
 * how {@code UIEntryAssembler} builds instances with the priority already
 * known.
 *
 * <p>{@code prioritySet} distinguishes an explicit priority from one not yet
 * configured. C's last parse pass ({@code finish_parse_ui_entry}) gives every
 * category whose {@code priority_set} is false the entry's {@code
 * default_priority} and sets the flag true; the ordering comparison {@code
 * cmp_desc_prio} reads only {@code priority}, never the flag. Before that
 * pass an unset category holds priority 0.
 *
 * <p>Unlike the C struct, which {@code UIEntry} copies by value, this class
 * is a mutable object, so {@link #copy()} exists to give each entry its own
 * instance.
 *
 * <p>Class UIEntryCategory coded before 260916, commented in full on 261102.
 *
 * @author Rowan Crowther
 */
public class UIEntryCategory {
    /**
     * The category's name. The Java form of C's {@code
     * category_reference.name} ({@code ui-entry.c}). C's {@code const char *}
     * points into its global category-name table; here it is an ordinary
     * string.
     *
     * <p>Field name coded before 260916, commented in full on 261102.
     */
    private String name;
    /**
     * The category's priority; only meaningful when {@link #prioritySet} is
     * {@code true}, and 0 for a category not yet configured. The Java form of
     * C's {@code category_reference.priority} ({@code ui-entry.c}), read by
     * the descending-priority ordering {@code cmp_desc_prio}: the higher
     * priority sorts first, and equal priorities fall back to the entry
     * name.
     *
     * <p>Field priority coded before 260916, commented in full on 261102.
     */
    private int priority;
    /**
     * Whether {@link #priority} has been configured for this category, as
     * opposed to left at its unresolved default of 0. The Java form of C's
     * {@code category_reference.priority_set} ({@code ui-entry.c}). Set true
     * by an explicit {@code priority:} line, and by the final pass that
     * fills in the entry's default priority.
     *
     * <p>Field prioritySet coded before 260916, commented in full on 261102.
     */
    private boolean prioritySet;

    /**
     * Build a resolved category reference from its name, priority and
     * whether that priority was explicitly set. The Java form of the
     * field-by-field assignment C performs once a category's priority is
     * resolved ({@code parameterize_category_list} in {@code ui-entry.c}):
     * a set priority is stored with the flag true, an unset one is stored
     * as 0 with the flag false. The constructor stores what it is given and
     * does not enforce that pairing, so a caller must pass 0 with {@code
     * false} for an unset category.
     *
     * @param name        the category's name
     * @param priority    the category's priority
     * @param prioritySet whether {@code priority} was explicitly set
     *
     * <p>Function UIEntryCategory(String, int, boolean) coded before 260916,
     * commented in full on 261102.
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
     * 261102.
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
     * 261102.
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
     * 261102.
     */
    public boolean isPrioritySet() {
        return prioritySet;
    }

    /**
     * Setter - this category's name. C assigns {@code category_reference.name}
     * directly; there is no validation, so a {@code null} is stored as given.
     *
     * @param name the new category name
     *
     * <p>Function setName(String) coded before 260916, commented in full on
     * 261102.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Setter - this category's priority. Changes only {@link #priority}; the
     * flag is left alone, so a caller giving an unset category a real
     * priority must also call {@link #setPrioritySet(boolean)}, as C's
     * {@code finish_parse_ui_entry} does when it fills in the entry's default
     * priority.
     *
     * @param priority the new priority
     *
     * <p>Function setPriority(int) coded before 260916, commented in full on
     * 261102.
     */
    public void setPriority(int priority) {
        this.priority = priority;
    }

    /**
     * Setter - whether this category's priority has been configured. Changes
     * only {@link #prioritySet}; the priority value is left alone. Mirrors C
     * writing {@code category_reference.priority_set}.
     *
     * @param set {@code true} if the priority is now configured
     *
     * <p>Function setPrioritySet(boolean) coded before 260916, commented in
     * full on 261102.
     */
    public void setPrioritySet(boolean set) {
        this.prioritySet = set;
    }

    /**
     * Returns an independent copy holding the same name, priority and flag.
     * C copies a {@code struct category_reference} by value (the {@code
     * memcpy} calls that grow and merge an entry's category array in {@code
     * ui-entry.c}); a Java object is a reference, so each entry that takes a
     * category from a template or base entry needs its own instance, or a
     * later {@link #setPriority(int)} on one entry would change the other.
     *
     * @return a new {@code UIEntryCategory} equal in all three fields
     *
     * <p>Function copy() coded before 260916, commented in full on 261102.
     */
    public UIEntryCategory copy() {
        return new UIEntryCategory(name, priority, prioritySet);
    }
}
