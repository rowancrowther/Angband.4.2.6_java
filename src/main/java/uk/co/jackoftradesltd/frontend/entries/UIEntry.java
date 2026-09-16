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

import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.frontend.entries.enums.EntryFlag;
import uk.co.jackoftradesltd.frontend.screen.enums.CombinerName;
import uk.co.jackoftradesltd.channel.enums.ElementEnum;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * A single entry in the player status display (one stat or resistance line),
 * ported from the C original's UI-entry system ({@code src/ui-entry.c}). Each
 * entry binds a stat or element parameter to a renderer, a value combiner, a
 * priority (for ordering/space competition), one or more labels for different
 * widths, and an optional {@link UIEntryBase} template it inherits defaults from.
 * Unlike the C {@code struct ui_entry}, this class stores no description text:
 * the data file's {@code desc:} field is validated and discarded at the boundary,
 * matching {@code parse_entry_desc} ({@code ui-entry.c}), which reads the field
 * only to consume it and never writes it onto the struct.
 *
 * <p>Class UIEntry coded before 260916, commented in full on 260916.
 *
 * @author Rowan Crowther
 */
public class UIEntry {
    /**
     * The entry's internal name.
     *
     * <p>Field name coded before 260916, commented in full on 260916.
     */
    private String name;
    /**
     * The element this entry tracks, when its parameter is an element.
     *
     * <p>Field parameter coded before 260916, commented in full on 260916.
     */
    private ElementEnum parameter;
    /**
     * True if the parameter is an element; false if it is a stat.
     *
     * <p>Field statOrElement coded before 260916, commented in full on 260916.
     */
    private StatElemType statOrElement;
    /**
     * The renderer used to draw this entry's value.
     *
     * <p>Field renderer coded before 260916, commented in full on 260916.
     */
    private UIEntryRenderer renderer;
    /**
     * How multiple contributing values for this entry are combined.
     *
     * <p>Field combineType coded before 260916, commented in full on 260916.
     */
    private CombinerName combineType;
    /**
     * Numeric display priority (used when space is limited).
     *
     * <p>Field priorityNum coded before 260916, commented in full on 260916.
     */
    private int priorityNum;
    /**
     * Behavioural flag for this entry.
     *
     * <p>Field entryFlag coded before 260916, commented in full on 260916.
     */
    private Flag<EntryFlag> entryFlag;
    /**
     * Default-width label text.
     *
     * <p>Field label coded before 260916, commented in full on 260916.
     */
    private String label;
    /**
     * Two-character label variant.
     *
     * <p>Field label2 coded before 260916, commented in full on 260916.
     */
    private String label2;
    /**
     * Five-character label variant.
     *
     * <p>Field label5 coded before 260916, commented in full on 260916.
     */
    private String label5;
    /**
     * Categories this entry belongs to (used for grouping on screen).
     *
     * <p>Field categories coded before 260916, commented in full on 260916.
     */
    private List<UIEntryCategory> categories;
    /**
     * The template this entry inherits defaults from, if any.
     *
     * <p>Field template coded before 260916, commented in full on 260916.
     */
    private UIEntryBase template;

    /**
     * Build a UI status entry from its parsed data-file fields. Note the
     * constructor parameter order places {@code label5} before {@code label2},
     * matching the data-file column order.
     *
     * <p>The {@code description} parameter is validated (must be non-null) but
     * not stored: C's {@code parse_entry_desc} ({@code ui-entry.c}) reads and
     * discards the data file's {@code desc:} field the same way, and
     * {@code struct ui_entry} has no field for it to end up on. Keeping the
     * null check here is deliberate — it still catches a malformed data-file
     * record at the boundary, it just doesn't keep what it validates.
     *
     * <p>Function UIEntry coded before 260916, commented in full on 260916.
     *
     * @param name        internal entry name
     * @param parameter   the element parameter (when applicable)
     * @param parmType    whether the parameter is a stat or an element
     * @param renderer    the renderer for the entry's value
     * @param combineType the value-combining strategy
     * @param categories  categories the entry belongs to
     * @param priorityNum numeric display priority
     * @param entryFlag   behavioural flag
     * @param description human-readable description
     * @param label       default-width label
     * @param label5      five-character label variant
     * @param label2      two-character label variant
     * @param template    optional template supplying defaults
     */
    public UIEntry(String name,
                   ElementEnum parameter,
                   StatElemType parmType,
                   UIEntryRenderer renderer,
                   CombinerName combineType,
                   List<UIEntryCategory> categories,
                   int priorityNum,
                   Flag<EntryFlag> entryFlag,
                   String description,
                   String label,
                   String label5,
                   String label2,
                   UIEntryBase template) {
        if (description == null) {
            throw new IllegalArgumentException("Description cannot be null for UIEntry " + name);
        }
        
        this.name = name;
        this.parameter = parameter;
        this.statOrElement = parmType;
        this.renderer = renderer;
        this.combineType = combineType;
        this.priorityNum = priorityNum;
        this.entryFlag = entryFlag;
        this.label = label;
        this.label2 = label2;
        this.label5 = label5;
        this.categories = categories;
        this.template = template;
    }

    /**
     * Returns this entry's internal name, the value the data file's
     * {@code name:} field set and other records reference through
     * {@code template:} fields or bindui fields in {@code player_property.txt}
     * / {@code object_property.txt}.
     *
     * <p>Function getName coded before 260916, commented in full on 260916.
     *
     * @return this entry's internal name
     */
    public String getName() {
        return name;
    }

    /**
     * Reports whether this entry belongs to the named category, and if so, at
     * what index. Ports {@code ui_entry_has_category} ({@code ui-entry.c}),
     * which only reports a boolean; the index is exposed here too since the
     * private search this delegates to already produces one, but a caller
     * checking membership only needs {@link Optional#isPresent()}.
     *
     * <p>Function uiEntryHasCategory coded before 260916, commented in full on 260916.
     *
     * @param name the category name to look for
     * @return the category's index if this entry has it, empty otherwise
     */
    public Optional<Integer> uiEntryHasCategory(String name) {
        return uiEntrySearchCategories(name);
    }

    /**
     * Finds the named category among this entry's categories. Ports
     * {@code ui_entry_search_categories} ({@code ui-entry.c}), which binary
     * searches because C keeps {@code entry->categories} sorted by name on
     * insert ({@code insert_embryo_category}, {@code ui-entry.c}); this scans
     * linearly instead, which needs no sortedness invariant and finds the same
     * single match, since a category name appears at most once per entry.
     *
     * <p>Function uiEntrySearchCategories coded before 260916, commented in full on 260916.
     *
     * @param name the category name to search for
     * @return the category's index if found, empty otherwise
     */
    private Optional<Integer> uiEntrySearchCategories(String name) {
        for (UIEntryCategory category : categories) {
            if (category.getName().equals(name)) {
                return Optional.of(categories.indexOf(category));
            }
        }

        return Optional.empty();
    }

    /**
     * Builds a debug string listing this entry's fields, in declaration order.
     * Has no C counterpart — {@code struct ui_entry} has no analogous dump
     * routine — so this is a Java-side convenience for logging and debugging
     * only.
     *
     * <p>Function toString coded before 260916, commented in full on 260916.
     *
     * @return a debug string listing this entry's fields
     */
    @Override
    public String toString() {
        return "UIEntry{" +
                "name='" + name + '\'' +
                ", parameter=" + parameter +
                ", statOrElement=" + statOrElement +
                ", renderer=" + renderer +
                ", combineType=" + combineType +
                ", priorityNum=" + priorityNum +
                ", entryFlag=" + entryFlag +
                ", label='" + label + '\'' +
                ", label2='" + label2 + '\'' +
                ", label5='" + label5 + '\'' +
                ", categories=" + categories +
                ", template='" + template + '\'' +
                '}';
    }

    /**
     * Returns the element this entry tracks, when its parameter is an element
     * ({@link #getStatOrElement()} is {@link StatElemType#ELEMENT}). Corresponds
     * to the resolved form of C's {@code param_index} ({@code ui-entry.c}) for
     * the element case.
     *
     * <p>Function getParameter coded before 260916, commented in full on 260916.
     *
     * @return the element parameter, or {@code null} when this entry's parameter is not an element
     */
    public ElementEnum getParameter() {
        return parameter;
    }

    /**
     * Returns whether this entry's parameter is a stat or an element.
     * Corresponds to which of C's {@code name_parameters[]} entries
     * ({@code ui-entry.c}) the entry's {@code param_index} was resolved
     * against.
     *
     * <p>Function getStatOrElement coded before 260916, commented in full on 260916.
     *
     * @return the parameter kind
     */
    public StatElemType getStatOrElement() {
        return statOrElement;
    }

    /**
     * Returns the renderer used to draw this entry's value. Corresponds to
     * the resolved form of C's {@code renderer_index} ({@code ui-entry.c}),
     * set from the data file's {@code renderer:} field via
     * {@code ui_entry_renderer_lookup}.
     *
     * <p>Function getRenderer coded before 260916, commented in full on 260916.
     *
     * @return this entry's renderer
     */
    public UIEntryRenderer getRenderer() {
        return renderer;
    }

    /**
     * Returns how multiple contributing values for this entry are combined
     * for display. Corresponds to the resolved form of C's
     * {@code combiner_index} ({@code ui-entry.c}), set from the data file's
     * {@code combine:} field via {@code ui_entry_combiner_lookup}.
     *
     * <p>Function getCombineType coded before 260916, commented in full on 260916.
     *
     * @return this entry's value-combining strategy
     */
    public CombinerName getCombineType() {
        return combineType;
    }

    /**
     * Returns this entry's numeric display priority, used to decide what to
     * show first and what to omit or move when space is tight. Corresponds to
     * C's {@code default_priority} ({@code ui-entry.c}), set from the data
     * file's {@code priority:} field.
     *
     * <p>Function getPriorityNum coded before 260916, commented in full on 260916.
     *
     * @return this entry's display priority
     */
    public int getPriorityNum() {
        return priorityNum;
    }

    /**
     * Reports whether the given behavioural flag is set on this entry. Ports
     * the bit test C performs directly on {@code entry->flags}
     * ({@code ui-entry.c}), e.g. {@code entry->flags & ENTRY_FLAG_TIMED_AUX},
     * as an {@link java.util.EnumSet} membership test via {@link Flag#has}.
     *
     * <p>Function entryFlagHas coded before 260916, commented in full on 260916.
     *
     * @param flag the flag to test
     * @return true if the flag is set, false otherwise
     */
    public boolean entryFlagHas(EntryFlag flag) {
        return entryFlag.has(flag);
    }

    /**
     * Returns the default-width label text for this entry. Corresponds to
     * C's {@code label} ({@code ui-entry.c}), set from the data file's
     * {@code label:} field.
     *
     * <p>Function getLabel coded before 260916, commented in full on 260916.
     *
     * @return this entry's default-width label
     */
    public String getLabel() {
        return label;
    }

    /**
     * Returns the two-character label variant, used by the equipment
     * comparison screen. Corresponds to one of C's
     * {@code shortened_labels[]} entries ({@code ui-entry.c}), set from the
     * data file's {@code label2:} field.
     *
     * <p>Function getLabel2 coded before 260916, commented in full on 260916.
     *
     * @return this entry's two-character label, or {@code null} if none was set
     */
    public String getLabel2() {
        return label2;
    }

    /**
     * Returns the five-character label variant, used by the second character
     * screen. Corresponds to one of C's {@code shortened_labels[]} entries
     * ({@code ui-entry.c}), set from the data file's {@code label5:} field.
     *
     * <p>Function getLabel5 coded before 260916, commented in full on 260916.
     *
     * @return this entry's five-character label, or {@code null} if none was set
     */
    public String getLabel5() {
        return label5;
    }

    /**
     * Returns the categories this entry belongs to, used to decide which
     * screens it appears on. Corresponds to C's {@code categories} array
     * ({@code ui-entry.c}), set from the data file's {@code category:}
     * field(s).
     *
     * <p>Function getCategories coded before 260916, commented in full on 260916.
     *
     * @return this entry's categories
     */
    public List<UIEntryCategory> getCategories() {
        return categories;
    }

    /**
     * Returns the template this entry inherits defaults from, if any, as set
     * from the data file's {@code template:} field.
     *
     * <p>Function getTemplate coded before 260916, commented in full on 260916.
     *
     * @return this entry's template, or {@code null} if it has none
     */
    public UIEntryBase getTemplate() {
        return template;
    }

    /**
     * Whether a UI entry's parameter refers to a player stat or a damage
     * element. Mirrors the {@code "stat"} / {@code "element"} strings the data
     * file's {@code parameter:} field accepts, matched against C's
     * {@code name_parameters} table ({@code ui-entry.c}).
     *
     * <p>Class StatElemType coded before 260916, commented in full on 260916.
     *
     * @author Rowan Crowther
     */
    public enum StatElemType {
        /**
         * No parameter is set; the entry's value does not vary by stat or
         * element. Has no direct C counterpart of its own — C's dummy
         * {@code ""} entry in {@code name_parameters} exists to make an unset
         * {@code parameter:} field resolve to a valid index rather than to
         * carry a distinct meaning.
         *
         * <p>Constant NONE coded before 260916, commented in full on 260916.
         *
         * @author Rowan Crowther
         */
        NONE(""),
        /**
         * The parameter is a player stat (STR, INT, …).
         *
         * <p>Constant STAT coded before 260916, commented in full on 260916.
         *
         * @author Rowan Crowther
         */
        STAT("stat"),
        /**
         * The parameter is a damage element (fire, cold, …).
         *
         * <p>Constant ELEMENT coded before 260916, commented in full on 260916.
         *
         * @author Rowan Crowther
         */
        ELEMENT("element");

        private final String value;

        StatElemType(String value) {
            this.value = value;
        }

        /**
         * Resolves the data file's {@code parameter:} field text to a
         * {@link StatElemType} constant. Ports the linear scan in C's
         * {@code parse_entry_parameter} ({@code ui-entry.c}), which walks
         * {@code name_parameters[]} until the name matches; this returns
         * {@code null} where C reports {@code PARSE_ERROR_INVALID_VALUE}.
         *
         * <p>Function fromValue coded before 260916, commented in full on 260916.
         *
         * @param value the data-file parameter name ({@code "stat"}, {@code "element"}, or {@code ""})
         * @return the matching constant, or {@code null} if none matches
         */
        public static StatElemType fromValue(String value) {
            return Arrays.stream(StatElemType.values()).filter(s -> s.value.equals(value))
                    .findFirst().orElse(null);
        }
    }
}