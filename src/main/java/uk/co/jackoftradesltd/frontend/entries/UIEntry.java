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
     * The {@code ui_entry_base.txt} template this entry was built from, or {@code null} if it was
     * not built via a {@code template:} directive. C has no equivalent field on
     * {@code struct ui_entry}: the template's fields are copied onto the entry outright by
     * {@code parse_entry_template} ({@code [C] ui-entry.c:1952-1986}) and the template itself is
     * never kept, so this reference is a Java-only convenience and is not consulted by anything else
     * on this class.
     *
     * <p>Field template coded before 260916, commented in full on 260922.
     */
    private UIEntryBase template;
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

    /**
     * Index into {@code PlayerEventStatusUpdate}'s cached stat-name array, when this entry's
     * parameter is a stat ({@link #getStatOrElement()} is {@link StatElemType#STAT}). Corresponds to
     * the resolved form of C's {@code param_index} ({@code [C] ui-entry.c:110}) for the stat case, the
     * counterpart to {@link #elementParameter} for the element case.
     *
     * <p>Field statParameter coded before 260916, commented in full on 260922.
     */
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
     * The raw {@code priority:} text for this entry - {@code "index"}, {@code "negative_index"} or
     * the empty string for a literal number already resolved into {@link #priorityNum} - kept so a
     * later override merge can tell a priority scheme apart from a literal value, the way C keeps
     * {@code embryo->psource_index} alongside {@code embryo->entry->default_priority} while a record
     * is still being parsed ({@code [C] ui-entry.c:2139-2191}).
     *
     * <p>Field priorityString coded before 260916, commented in full on 260922.
     */
    private String priorityString;

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

    /**
     * Shortened-width label variants, keyed by index (label length minus one) rather than the
     * fixed-size {@code MAX_SHORTENED}-element array C uses ({@code entry->shortened_labels[]},
     * {@code [C] ui-entry.c:103}). An absent or empty entry at an index means that shortened label is
     * still unset, corresponding to C's {@code nshortened[i] == 0}; {@link #getLabel2()} and
     * {@link #getLabel5()} are the two indices ({@code 1} and {@code 4}) the data files actually use.
     *
     * <p>Field shortenedLabels coded before 260916, commented in full on 260922.
     */
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
                   UIEntryBase template,
                   ElementEnum parameter,
                   StatElemType parmType,
                   UIEntryRenderer renderer,
                   CombinerName combineType,
                   List<UIEntryCategory> categories,
                   int priorityNum,
                   String priorityString,
                   Flag<ChannelEntryFlag> entryFlag,
                   String description,
                   String label,
                   String label5,
                   String label2) {
        if (description == null) {
            throw new IllegalArgumentException("Description cannot be null for UIEntry " + name);
        }
        
        this.name = name;
        this.template = template;
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
        this.priorityString = priorityString;
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
     * Returns this entry's behavioural flags as a read-only view. Corresponds to C's
     * {@code entry->flags} bitmask ({@code [C] ui-entry.c:111}).
     *
     * <p>Function getEntryFlag coded before 260916, commented in full on 260922.
     *
     * @return this entry's behavioural flags
     */
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

    /**
     * Getter - this entry's numeric display priority. Synonym for {@link #getPriorityNum()}, named
     * to match C's {@code default_priority} field ({@code [C] ui-entry.c:109}).
     *
     * <p>Function getDefaultPriority coded before 260916, commented in full on 260922.
     *
     * @return this entry's display priority
     */
    public int getDefaultPriority() {
        return priorityNum;
    }

    /**
     * Setter - directly overwrites this entry's numeric display priority. Used both while resolving
     * a {@code priority:} directive and, for a parameterised entry, while recomputing the priority
     * for each expanded stat/element value, mirroring the assignments to
     * {@code entry->default_priority} throughout {@code hatch_embryo} ({@code [C]
     * ui-entry.c:1762-1869}).
     *
     * <p>Function setDefaultPriority coded before 260916, commented in full on 260922.
     *
     * @param priority the new display priority
     */
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

    /**
     * Setter - directly overwrites this entry's internal name. Used when expanding a
     * parameterised entry, where the per-value name (e.g. {@code name<ACID>}) replaces the
     * un-suffixed one, mirroring the {@code entry->name} reassignment in {@code hatch_embryo}
     * ({@code [C] ui-entry.c:1838-1842}).
     *
     * <p>Function setName coded before 260916, commented in full on 260922.
     *
     * @param name the new internal name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Setter - directly overwrites this entry's numeric display priority. Synonym for
     * {@link #setDefaultPriority(int)}.
     *
     * <p>Function setPriorityNum coded before 260916, commented in full on 260922.
     *
     * @param priority the new display priority
     */
    public void setPriorityNum(int priority) {
        this.priorityNum = priority;
    }

    /**
     * Setter - directly overwrites this entry's default-width label. Corresponds to
     * {@code parse_entry_label}'s unconditional overwrite of {@code entry->label}
     * ({@code [C] ui-entry.c:2051-2078}).
     *
     * <p>Function setLabel coded before 260916, commented in full on 260922.
     *
     * @param label the new default-width label
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * Setter - directly overwrites one of this entry's shortened-width label variants.
     *
     * <p>Function setShortenedLabel coded before 260916, commented in full on 260922.
     *
     * @param index the shortened-label index (label length minus one)
     * @param label the new shortened label at that index
     */
    public void setShortenedLabel(int index, String label) {
        this.shortenedLabels.put(index, label);
    }

    /**
     * Returns this entry's shortened-width label variant at the given index, or {@code null} if
     * unset at that index. Corresponds to one of C's {@code shortened_labels[]} entries
     * ({@code [C] ui-entry.c:103}); see {@link #getLabel2()} and {@link #getLabel5()} for the two
     * indices the data files actually populate.
     *
     * <p>Function getShortenedLabel coded before 260916, commented in full on 260922.
     *
     * @param index the shortened-label index (label length minus one)
     * @return the shortened label at that index, or {@code null} if unset
     */
    public String getShortenedLabel(int index) {
        return shortenedLabels.get(index);
    }

    /**
     * Setter - directly overwrites this entry's renderer. Corresponds to
     * {@code parse_entry_renderer}'s unconditional overwrite of {@code entry->renderer_index}
     * ({@code [C] ui-entry.c:2021-2033}).
     *
     * <p>Function setRenderer coded before 260916, commented in full on 260922.
     *
     * @param renderer the new renderer
     */
    public void setRenderer(UIEntryRenderer renderer) {
        this.renderer = renderer;
    }

    /**
     * Setter - directly overwrites this entry's value-combining strategy. Corresponds to
     * {@code parse_entry_combine}'s unconditional overwrite of {@code entry->combiner_index}
     * ({@code [C] ui-entry.c:2036-2048}).
     *
     * <p>Function setCombinerType coded before 260916, commented in full on 260922.
     *
     * @param combineType the new value-combining strategy
     */
    public void setCombinerType(CombinerName combineType) {
        this.combineType = combineType;
    }

    /**
     * Sets this entry's resolved parameter index, dispatching on whether {@link #getStatOrElement()}
     * is a stat or an element: a stat index is stored directly in {@link #statParameter}, while an
     * element index is resolved to its {@link ElementEnum} constant and stored in
     * {@link #elementParameter}. The Java form of {@code entry->param_index} being set to the loop
     * index {@code i} throughout {@code hatch_embryo}'s expansion ({@code [C] ui-entry.c:1762-1869}).
     *
     * <p>Function setParamIndex coded before 260916, commented in full on 260922.
     *
     * @param index the resolved parameter index for the relevant kind
     */
    public void setParamIndex(int index) {
        if (statOrElementParameter == StatElemType.STAT) {
            this.statParameter = index;
        } else if (statOrElementParameter == StatElemType.ELEMENT) {
            this.elementParameter = Arrays.asList(ElementEnum.values()).get(index);
        }
    }

    /**
     * Replaces this entry's behavioural flags with a defensive copy of {@code entryFlag}, so the
     * caller's own {@link Flag} instance can keep changing without affecting this entry.
     *
     * <p>Function setEntryFlags coded before 260916, commented in full on 260922.
     *
     * @param entryFlag the flags to copy onto this entry
     */
    public void setEntryFlags(FlagView<ChannelEntryFlag> entryFlag) {
        Flag<ChannelEntryFlag> newFlags = new Flag<>(ChannelEntryFlag.class);
        newFlags.copyFrom(entryFlag);
        this.entryFlag = newFlags;
    }

    /**
     * Returns the number of categories this entry belongs to. Corresponds to C's
     * {@code entry->n_category} ({@code [C] ui-entry.c:112}).
     *
     * <p>Function getNCategory coded before 260916, commented in full on 260922.
     *
     * @return the number of categories this entry belongs to
     */
    public int getNCategory() {
        return categories.size();
    }

    /**
     * Returns the index into {@code PlayerEventStatusUpdate}'s cached stat-name array, when this
     * entry's parameter is a stat.
     *
     * <p>Function getStatParameter coded before 260916, commented in full on 260922.
     *
     * @return the resolved stat parameter index
     */
    public int getStatParameter() {
        return statParameter;
    }

    /**
     * Setter - directly overwrites the resolved stat parameter index, without regard to
     * {@link #getStatOrElement()}. Unlike {@link #setParamIndex(int)}, which dispatches on the
     * parameter kind, this always writes {@link #statParameter}.
     *
     * <p>Function setStatParameter coded before 260916, commented in full on 260922.
     *
     * @param statParameter the new stat parameter index
     */
    public void setStatParameter(int statParameter) {
        this.statParameter = statParameter;
    }

    /**
     * Setter - directly overwrites this entry's element parameter.
     *
     * <p>Function setElementParameter coded before 260916, commented in full on 260922.
     *
     * @param elementParameter the new element parameter
     */
    public void setElementParameter(ElementEnum elementParameter) {
        this.elementParameter = elementParameter;
    }

    /**
     * Setter - directly overwrites whether this entry's parameter is a stat or an element.
     *
     * <p>Function setStatOrElement coded before 260916, commented in full on 260922.
     *
     * @param statType the new parameter kind
     */
    public void setStatOrElement(StatElemType statType) {
        this.statOrElementParameter = statType;
    }

    /**
     * Setter - directly overwrites this entry's two-character label variant.
     *
     * <p>Function setLabel2 coded before 260916, commented in full on 260922.
     *
     * @param label2 the new two-character label
     */
    public void setLabel2(String label2) {
        this.shortenedLabels.put(1, label2);
    }

    /**
     * Setter - directly overwrites this entry's five-character label variant.
     *
     * <p>Function setLabel5 coded before 260916, commented in full on 260922.
     *
     * @param label5 the new five-character label
     */
    public void setLabel5(String label5) {
        this.shortenedLabels.put(4, label5);
    }

    /**
     * Builds a shallow copy of this entry - a new {@link UIEntry} sharing this one's
     * {@link #categories} list contents (via a fresh {@link ArrayList}) and a defensive copy of its
     * flags, but with the description forced to the literal {@code "Copy"} since the constructor
     * requires a non-null one and this entry no longer has the original text (see
     * {@link #UIEntry(String, UIEntryBase, ElementEnum, StatElemType, UIEntryRenderer, CombinerName,
     * List, int, String, Flag, String, String, String, String)}). Used by
     * {@code UIEntryBaseAssembler} to stamp out one entry per expanded stat/element value from a
     * common base, the Java form of C reusing the same field values across each iteration of
     * {@code hatch_embryo}'s expansion loop ({@code [C] ui-entry.c:1779-1828}).
     *
     * <p>Function copy coded before 260916, commented in full on 260922.
     *
     * @return a shallow copy of this entry
     */
    public UIEntry copy() {
        List<UIEntryCategory> categoriesCopy = new ArrayList<>(categories);
        Flag<ChannelEntryFlag> copyFlags = new Flag<>(ChannelEntryFlag.class);
        copyFlags.copyFrom(entryFlag);
        return new UIEntry(this.name, this.template, this.elementParameter,
                this.statOrElementParameter, this.renderer,
                this.combineType, categoriesCopy, this.priorityNum, this.priorityString,
                copyFlags, "Copy",
                this.label, this.shortenedLabels.get(4), this.shortenedLabels.get(1));
    }

    /**
     * Setter - directly overwrites this entry's category list. (The name keeps its original spelling
     * rather than being corrected to {@code setCategories}, since renaming it is a behavioural, not a
     * comment-only, change and so is outside this Javadoc pass.)
     *
     * <p>Function setCategores coded before 260916, commented in full on 260922.
     *
     * @param cats the new category list
     */
    public void setCategores(List<UIEntryCategory> cats) {
        this.categories = cats;
    }

    /**
     * Getter - the {@code ui_entry_base.txt} template this entry was built from, or {@code null} if
     * none. See {@link #template} for why this has no C counterpart.
     *
     * <p>Function getTemplate coded before 260916, commented in full on 260922.
     *
     * @return this entry's template, or {@code null} if it was not built from one
     */
    public UIEntryBase getTemplate() {
        return template;
    }

    /**
     * Setter - directly overwrites this entry's template reference.
     *
     * <p>Function setTemplate coded before 260916, commented in full on 260922.
     *
     * @param template the new template reference
     */
    public void setTemplate(UIEntryBase template) {
        this.template = template;
    }

    /**
     * Getter - the raw {@code priority:} text for this entry (a priority scheme name, or the empty
     * string for a literal number already resolved into {@link #getPriorityNum()}).
     *
     * <p>Function getPriorityString coded before 260916, commented in full on 260922.
     *
     * @return this entry's raw priority-scheme text
     */
    public String getPriorityString() {
        return priorityString;
    }

    /**
     * Setter - directly overwrites this entry's raw priority-scheme text.
     *
     * <p>Function setPriorityString coded before 260916, commented in full on 260922.
     *
     * @param priorityString the new raw priority-scheme text
     */
    public void setPriorityString(String priorityString) {
        this.priorityString = priorityString;
    }
}