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

import uk.co.jackoftradesltd.channel.enums.ChannelEntryFlag;
import uk.co.jackoftradesltd.channel.enums.StatElemType;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.channel.utils.FlagView;
import uk.co.jackoftradesltd.channel.utils.combiners.CombinerName;
import uk.co.jackoftradesltd.channel.enums.ElementEnum;

import java.util.*;

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
    private ElementEnum elementParameter;
    /**
     * True if the parameter is an element; false if it is a stat.
     *
     * <p>Field statOrElement coded before 260916, commented in full on 260916.
     */
    private StatElemType statOrElementParameter;
    // Stats.getValue() pointer into the Stats enum
    private int statParameter;
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
    private Flag<ChannelEntryFlag> entryFlag;

    /**
     * Default-width label text.
     *
     * <p>Field label coded before 260916, commented in full on 260916.
     */
    private String label;

    private Map<Integer, String> shortenedLabels;
    /**
     * Categories this entry belongs to (used for grouping on screen).
     *
     * <p>Field categories coded before 260916, commented in full on 260916.
     */
    private List<UIEntryCategory> categories;

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
     */
    public UIEntry(String name,
                   ElementEnum parameter,
                   StatElemType parmType,
                   UIEntryRenderer renderer,
                   CombinerName combineType,
                   List<UIEntryCategory> categories,
                   int priorityNum,
                   Flag<ChannelEntryFlag> entryFlag,
                   String description,
                   String label,
                   String label5,
                   String label2) {
        if (description == null) {
            throw new IllegalArgumentException("Description cannot be null for UIEntry " + name);
        }
        
        this.name = name;
        this.elementParameter = parameter;
        this.statOrElementParameter = parmType;
        this.renderer = renderer;
        this.combineType = combineType;
        this.priorityNum = priorityNum;
        this.entryFlag = entryFlag;
        this.label = label;
        this.shortenedLabels = new HashMap<>();
        this.shortenedLabels.put(1, label2);
        this.shortenedLabels.put(4, label5);
        this.categories = categories;
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

    public FlagView<ChannelEntryFlag> getEntryFlag() {
        return entryFlag;
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
                ", renderer=" + renderer +
                ", combineType=" + combineType +
                ", priorityNum=" + priorityNum +
                ", entryFlag=" + entryFlag +
                ", label='" + label + '\'' +
                ", categories=" + categories +
                '}';
    }

    public int getDefaultPriority() {
        return priorityNum;
    }

    public void setDefaultPriority(int priority) {
        this.priorityNum = priority;
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
        return elementParameter;
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
        return statOrElementParameter;
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
    public boolean entryFlagHas(ChannelEntryFlag flag) {
        return entryFlag.has(flag);
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
        return shortenedLabels.get(1);
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
     * Returns the five-character label variant, used by the second character
     * screen. Corresponds to one of C's {@code shortened_labels[]} entries
     * ({@code ui-entry.c}), set from the data file's {@code label5:} field.
     *
     * <p>Function getLabel5 coded before 260916, commented in full on 260916.
     *
     * @return this entry's five-character label, or {@code null} if none was set
     */
    public String getLabel5() {
        return shortenedLabels.get(4);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPriorityNum(int priority) {
        this.priorityNum = priority;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setShortenedLabel(int index, String label) {
        this.shortenedLabels.put(index, label);
    }

    public String getShortenedLabel(int index) {
        return shortenedLabels.get(index);
    }

    public void setRenderer(UIEntryRenderer renderer) {
        this.renderer = renderer;
    }

    public void setCombinerType(CombinerName combineType) {
        this.combineType = combineType;
    }

    public void setParamIndex(int index) {
        if (statOrElementParameter == StatElemType.STAT) {
            this.statParameter = index;
        } else if (statOrElementParameter == StatElemType.ELEMENT) {
            this.elementParameter = Arrays.asList(ElementEnum.values()).get(index);
        }
    }

    public void setEntryFlags(FlagView<ChannelEntryFlag> entryFlag) {
        Flag<ChannelEntryFlag> newFlags = new Flag<>(ChannelEntryFlag.class);
        newFlags.copyFrom(entryFlag);
        this.entryFlag = newFlags;
    }

    public int getNCategory() {
        return categories.size();
    }
}