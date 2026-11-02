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
import uk.co.jackoftradesltd.channel.enums.ElementEnum;
import uk.co.jackoftradesltd.channel.enums.StatElemType;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.channel.utils.FlagView;
import uk.co.jackoftradesltd.channel.utils.combiners.CombinerName;

import java.util.*;

/**
 * A single entry in the player status display (one stat or resistance line),
 * ported from the C original's {@code struct ui_entry} ({@code src/ui-entry.c}). Each
 * entry binds a stat or element parameter to a renderer, a value combiner, a
 * priority (for ordering/space competition), one or more labels for different
 * widths, a set of categories and a set of behavioural flags.
 *
 * <p>Field-by-field, the port differs from {@code struct ui_entry} in four ways:
 * <ul>
 *   <li>C's single {@code param_index} is split in two. A stat entry keeps its index in
 *   {@link #statParameter}; an element entry keeps its {@link ElementEnum} constant in
 *   {@link #elementParameter}; {@link #statOrElementParameter} says which applies, and
 *   {@link #setParamIndex(int)} writes the right one. C's {@code -1} for "not parameterized"
 *   becomes {@link StatElemType#NONE} with {@code -1} and {@code ELEM_NONE}.</li>
 *   <li>C's {@code renderer_index} and {@code combiner_index} are held already resolved, as
 *   {@link UIEntryRenderer} and {@link CombinerName}.</li>
 *   <li>C's wide-character label buffers, with their {@code nlabel} and {@code nshortened[]}
 *   lengths, are plain {@link String}s; a string's length is the count.</li>
 *   <li>C's {@code obj_props} and {@code p_abilities} arrays (and their {@code n_*} and
 *   {@code nalloc_*} counters) are not held here. The binding runs the other way round, in
 *   {@code UIEntryValueRegistry}.</li>
 * </ul>
 *
 * <p>Nor does this class store any description text: the data file's {@code desc:} field is
 * validated and discarded at the boundary, matching {@code parse_entry_desc} ({@code ui-entry.c}),
 * which reads the field only to consume it and never writes it onto the struct.
 *
 * <p>C uses one {@code struct ui_entry} for both the {@code ui_entry_base.txt} templates and the
 * entries built from them, telling them apart by the {@code ENTRY_FLAG_TEMPLATE_ONLY} bit. This
 * port does the same: {@code UIEntryBaseAssembler} builds its templates as {@code UIEntry}
 * instances carrying {@link ChannelEntryFlag#ENTRY_FLAG_TEMPLATE_ONLY}, and a real entry
 * records the template it was built from in {@link #template}.
 *
 * <p>Class UIEntry coded before 260916, commented in full on 261102.
 *
 * @author Rowan Crowther
 */
public class UIEntry {
    /**
     * The entry's internal name - C's {@code name}. For an entry expanded from a parameterized
     * record it carries the parameter suffix, e.g. {@code name<ACID>}, as {@code hatch_embryo}
     * ({@code ui-entry.c}) builds it.
     *
     * <p>Field name coded before 260916, commented in full on 261102.
     */
    private String name;

    /**
     * The {@code ui_entry_base.txt} template this entry was built from, or {@code null} if it was
     * not built via a {@code template:} directive. C has no equivalent field on
     * {@code struct ui_entry}: {@code parse_entry_template} ({@code ui-entry.c}) copies the
     * template's renderer, combiner, priority, flags and categories onto the embryo outright and
     * never keeps the template itself, so this reference is a Java-only convenience and is not
     * consulted by anything else on this class.
     *
     * <p>Field template coded before 260916, commented in full on 261102.
     */
    private UIEntry template;
    /**
     * The element this entry tracks, when its parameter is an element
     * ({@link #statOrElementParameter} is {@link StatElemType#ELEMENT}). The element half of C's
     * {@code param_index}; {@link #statParameter} is the stat half. Holds {@code ELEM_NONE}, or
     * {@code null} where a caller passed none to the constructor, for an entry that is not
     * parameterized by element.
     *
     * <p>Field elementParameter coded before 260916, commented in full on 261102.
     */
    private ElementEnum elementParameter;
    /**
     * Which kind of parameter this entry is expanded over: {@link StatElemType#STAT},
     * {@link StatElemType#ELEMENT}, or {@link StatElemType#NONE} when the record is not
     * parameterized. It is the Java form of C's {@code embryo->param_index} choosing a row of
     * {@code name_parameters[]} ({@code ui-entry.c}), and it decides which of
     * {@link #statParameter} and {@link #elementParameter} is meaningful.
     *
     * <p>Field statOrElementParameter coded before 260916, commented in full on 261102.
     */
    private StatElemType statOrElementParameter;

    /**
     * Index into {@code PlayerEventStatusUpdate}'s cached stat-name array, when this entry's
     * parameter is a stat ({@link #getStatOrElement()} is {@link StatElemType#STAT}). The stat half
     * of C's {@code param_index} ({@code struct ui_entry}), the counterpart to
     * {@link #elementParameter} for the element case. {@code -1} means unset, the value C gives an
     * entry that is not parameterized.
     *
     * <p>Field statParameter coded before 260916, commented in full on 261102.
     */
    private int statParameter = -1;
    /**
     * The renderer used to draw this entry's value - the resolved form of C's
     * {@code renderer_index}, set from the {@code renderer:} directive.
     *
     * <p>Field renderer coded before 260916, commented in full on 261102.
     */
    private UIEntryRenderer renderer;
    /**
     * How multiple contributing values for this entry are combined - the resolved form of C's
     * {@code combiner_index}, set from the {@code combine:} directive. C refuses to hatch an
     * entry whose combiner index is still zero.
     *
     * <p>Field combineType coded before 260916, commented in full on 261102.
     */
    private CombinerName combineType;
    /**
     * Numeric display priority (used when space is limited) - C's {@code default_priority}. For a
     * record whose {@code priority:} names a scheme this holds the value the scheme gave for this
     * entry's parameter index; {@link #priorityString} records which scheme.
     *
     * <p>Field priorityNum coded before 260916, commented in full on 261102.
     */
    private int priorityNum;

    /**
     * The raw {@code priority:} text for this entry - {@code "index"}, {@code "negative_index"} or
     * the empty string for a literal number already resolved into {@link #priorityNum} - kept so a
     * later override merge can tell a priority scheme apart from a literal value, the way C keeps
     * {@code embryo->psource_index} alongside {@code embryo->entry->default_priority} while a record
     * is still being parsed ({@code parse_entry_priority}, {@code ui-entry.c}). C drops the scheme
     * once the entry is hatched; this field is Java-only.
     *
     * <p>Field priorityString coded before 260916, commented in full on 261102.
     */
    private String priorityString;

    /**
     * Behavioural flags for this entry - C's {@code flags} bitmask, held as a {@link Flag} set.
     * {@link ChannelEntryFlag#ENTRY_FLAG_TIMED_AS_AUX} comes from a {@code flags:} directive;
     * {@link ChannelEntryFlag#ENTRY_FLAG_TEMPLATE_ONLY} marks a {@code ui_entry_base.txt} template
     * and is cleared on a real entry that copies its flags.
     *
     * <p>Field entryFlag coded before 260916, commented in full on 261102.
     */
    private Flag<ChannelEntryFlag> entryFlag;

    /**
     * Default-width label text - C's {@code label}, with {@code nlabel} being this string's length.
     * C cuts it to {@code MAX_ENTRY_LABEL} characters when parsing; this class does not cut, so the
     * assembler does.
     *
     * <p>Field label coded before 260916, commented in full on 261102.
     */
    private String label;

    /**
     * Shortened-width label variants, keyed by index (label length minus one) rather than the
     * fixed-size {@code MAX_SHORTENED}-element array C uses ({@code entry->shortened_labels[]}).
     * An absent, {@code null} or empty entry at an index means that shortened label is still
     * unset, corresponding to C's {@code nshortened[i] == 0}; {@link #getLabel2()} and
     * {@link #getLabel5()} are the two indices ({@code 1} and {@code 4}) the data files actually use.
     * The constructor seeds exactly those two keys, with {@code null} when the label is not given.
     *
     * <p>Field shortenedLabels coded before 260916, commented in full on 261102.
     */
    private final Map<Integer, String> shortenedLabels;
    /**
     * Categories this entry belongs to (used for grouping on screen) - C's {@code categories}
     * array, with {@code n_category} being this list's size. C keeps the array sorted by name; the
     * list is stored as given, and {@code UIEntryAssembler} sorts it by name when it finishes an
     * entry.
     *
     * <p>Field categories coded before 260916, commented in full on 261102.
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
     * <p>There is no stat-index parameter: {@link #statParameter} starts at {@code -1} and a caller
     * expanding over stats sets it afterwards with {@link #setStatParameter(int)}. The shortened
     * labels are seeded at keys {@code 1} and {@code 4} only, with {@code null} where
     * {@code label2} or {@code label5} is {@code null}. {@code categories} and {@code entryFlag}
     * are stored as the caller's own objects, not copied, unlike {@link #setEntryFlags} and
     * {@link #copy()}, which do copy; C's {@code flags} is an {@code int} copied by value. A caller
     * must therefore not mutate either one after construction.
     *
     * <p>Function UIEntry coded before 260916, commented in full on 261102.
     *
     * @param name           internal entry name
     * @param template       the {@code ui_entry_base.txt} template the entry was built from, or
     *                       {@code null} if it was not built via {@code template:}
     * @param parameter      the element parameter (when applicable)
     * @param parmType       whether the parameter is a stat or an element
     * @param renderer       the renderer for the entry's value
     * @param combineType    the value-combining strategy
     * @param categories     categories the entry belongs to
     * @param priorityNum    numeric display priority
     * @param priorityString the raw {@code priority:} text, {@code "index"}, {@code "negative_index"}
     *                       or the empty string for a literal number
     * @param entryFlag      behavioural flags
     * @param description    the {@code desc:} text; must be non-null but is not stored
     * @param label          default-width label
     * @param label5         five-character label variant
     * @param label2         two-character label variant
     * @throws IllegalArgumentException if {@code description} is {@code null}
     */
    public UIEntry(String name,
                   UIEntry template,
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
     * <p>Function getName coded before 260916, commented in full on 261102.
     *
     * @return this entry's internal name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns this entry's behavioural flags as a read-only view. Corresponds to C's
     * {@code entry->flags} bitmask ({@code struct ui_entry}); the view cannot change the set, so a
     * caller must go through {@link #setEntryFlags} to alter it.
     *
     * <p>Function getEntryFlag coded before 260916, commented in full on 261102.
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
     * checking membership only needs {@link Optional#isPresent()}. The match is exact and
     * case-sensitive, as C's {@code strcmp} is. The index is the position in this entry's category
     * list, which equals C's index only while the list is sorted by name.
     *
     * <p>Function uiEntryHasCategory coded before 260916, commented in full on 261102.
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
     * single match, since a category name appears at most once per entry. Where C reports an
     * absent name as zero with the insertion point in {@code *ind}, this returns an empty
     * {@link Optional} and gives no insertion point, as nothing here inserts in sorted position.
     *
     * <p>Function uiEntrySearchCategories coded before 260916, commented in full on 261102.
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
     * to match C's {@code default_priority} field ({@code struct ui_entry}).
     *
     * <p>Function getDefaultPriority coded before 260916, commented in full on 261102.
     *
     * @return this entry's display priority
     */
    public int getDefaultPriority() {
        return priorityNum;
    }

    /**
     * Setter - directly overwrites this entry's numeric display priority. Used both while resolving
     * a {@code priority:} directive and, for a parameterized entry, while recomputing the priority
     * for each expanded stat/element value, mirroring the assignments to
     * {@code entry->default_priority} throughout {@code hatch_embryo} ({@code ui-entry.c}). It does
     * not touch {@link #priorityString}, so a caller changing the scheme must set that as well.
     *
     * <p>Function setDefaultPriority coded before 260916, commented in full on 261102.
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
     * parameterized entry, where the per-value name (e.g. {@code name<ACID>}) replaces the
     * un-suffixed one, mirroring the {@code entry->name} reassignment in {@code hatch_embryo}
     * ({@code ui-entry.c}).
     *
     * <p>Function setName coded before 260916, commented in full on 261102.
     *
     * @param name the new internal name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Setter - directly overwrites this entry's numeric display priority. Synonym for
     * {@link #setDefaultPriority(int)}; like it, this leaves {@link #priorityString} alone.
     *
     * <p>Function setPriorityNum coded before 260916, commented in full on 261102.
     *
     * @param priority the new display priority
     */
    public void setPriorityNum(int priority) {
        this.priorityNum = priority;
    }

    /**
     * Setter - directly overwrites this entry's default-width label. Corresponds to
     * {@code parse_entry_label}'s unconditional overwrite of {@code entry->label}
     * ({@code ui-entry.c}). Unlike C, which cuts the text to {@code MAX_ENTRY_LABEL} characters
     * as it reads it, this setter stores the text as given; the caller does the cutting.
     *
     * <p>Function setLabel coded before 260916, commented in full on 261102.
     *
     * @param label the new default-width label
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * Setter - directly overwrites one of this entry's shortened-width label variants. Corresponds
     * to {@code parse_entry_shortened_label} writing {@code entry->shortened_labels[n - 1]} and
     * {@code nshortened[n - 1]} ({@code ui-entry.c}), and to {@code fill_out_shortened} filling the
     * slots no directive set. There is no bounds check: C refuses a {@code label} number above
     * {@code MAX_SHORTENED}, whereas this stores any key.
     *
     * <p>Function setShortenedLabel coded before 260916, commented in full on 261102.
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
     * ({@code struct ui_entry}); see {@link #getLabel2()} and {@link #getLabel5()} for the two
     * indices the data files actually populate.
     *
     * <p>Function getShortenedLabel coded before 260916, commented in full on 261102.
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
     * ({@code ui-entry.c}). The lookup that can fail, {@code ui_entry_renderer_lookup}, is the
     * caller's job here; this setter accepts whatever it is given, {@code null} included.
     *
     * <p>Function setRenderer coded before 260916, commented in full on 261102.
     *
     * @param renderer the new renderer
     */
    public void setRenderer(UIEntryRenderer renderer) {
        this.renderer = renderer;
    }

    /**
     * Setter - directly overwrites this entry's value-combining strategy. Corresponds to
     * {@code parse_entry_combine}'s unconditional overwrite of {@code entry->combiner_index}
     * ({@code ui-entry.c}). The lookup that can fail, {@code ui_entry_combiner_lookup}, is the
     * caller's job here; this setter accepts whatever it is given, {@code null} included.
     *
     * <p>Function setCombinerType coded before 260916, commented in full on 261102.
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
     * index {@code i} throughout {@code hatch_embryo}'s expansion ({@code ui-entry.c}).
     *
     * <p>C counts elements from {@code ACID} at zero, while {@link ElementEnum} starts with a
     * {@code ELEM_NONE} placeholder, so element index {@code i} resolves to the constant at
     * ordinal {@code i + 1}; {@code -1} lands on {@code ELEM_NONE}. An index past the last element
     * gives {@code ELEM_MAX} or throws {@link IndexOutOfBoundsException}; C's matching
     * {@code count_func} bounds the loop so it never passes one. When the kind is
     * {@link StatElemType#NONE} the index is ignored and the entry gets C's unparameterized
     * values: {@code param_index = -1}, with {@link #statParameter} {@code -1} and
     * {@link #elementParameter} {@code ELEM_NONE}.
     *
     * <p>Function setParamIndex coded before 260916, commented in full on 261102.
     *
     * @param index the resolved parameter index for the relevant kind
     */
    public void setParamIndex(int index) {
        if (statOrElementParameter == StatElemType.STAT) {
            this.statParameter = index;
        } else if (statOrElementParameter == StatElemType.ELEMENT) {
            this.elementParameter = Arrays.asList(ElementEnum.values()).get(index + 1);
        } else {
            this.statParameter = -1;
            this.elementParameter = ElementEnum.ELEM_NONE;
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
     * {@code entry->n_category} ({@code struct ui_entry}); C keeps it as a separate counter, here
     * it is always the size of the category list.
     *
     * <p>Function getNCategory coded before 260916, commented in full on 261102.
     *
     * @return the number of categories this entry belongs to
     */
    public int getNCategory() {
        return categories.size();
    }

    /**
     * Returns the index into {@code PlayerEventStatusUpdate}'s cached stat-name array, when this
     * entry's parameter is a stat - the stat half of C's {@code param_index}.
     *
     * <p>Function getStatParameter coded before 260916, commented in full on 261102.
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
     * Setter - directly overwrites this entry's element parameter, without regard to
     * {@link #getStatOrElement()} (see {@link #setParamIndex(int)} for the dispatching form). It
     * takes the {@link ElementEnum} constant itself, so the {@code ELEM_NONE} offset that
     * {@link #setParamIndex(int)} applies to a C index does not arise here.
     *
     * <p>Function setElementParameter coded before 260916, commented in full on 261102.
     *
     * @param elementParameter the new element parameter
     */
    public void setElementParameter(ElementEnum elementParameter) {
        this.elementParameter = elementParameter;
    }

    /**
     * Setter - directly overwrites whether this entry's parameter is a stat or an element, the
     * Java form of picking a row of C's {@code name_parameters[]} ({@code ui-entry.c}). It does
     * not reset {@link #statParameter} or {@link #elementParameter}, so both keep whatever they
     * held.
     *
     * <p>Function setStatOrElement coded before 260916, commented in full on 261102.
     *
     * @param statType the new parameter kind
     */
    public void setStatOrElement(StatElemType statType) {
        this.statOrElementParameter = statType;
    }

    /**
     * Setter - directly overwrites this entry's two-character label variant, the
     * {@code shortened_labels[1]} slot C fills from the data file's {@code label2:} directive
     * ({@code parse_entry_shortened_label}, {@code ui-entry.c}). The text is stored as given; C
     * cuts it to two characters as it reads it.
     *
     * <p>Function setLabel2 coded before 260916, commented in full on 261102.
     *
     * @param label2 the new two-character label
     */
    public void setLabel2(String label2) {
        this.shortenedLabels.put(1, label2);
    }

    /**
     * Setter - directly overwrites this entry's five-character label variant, the
     * {@code shortened_labels[4]} slot C fills from the data file's {@code label5:} directive
     * ({@code parse_entry_shortened_label}, {@code ui-entry.c}). The text is stored as given; C
     * cuts it to five characters as it reads it.
     *
     * <p>Function setLabel5 coded before 260916, commented in full on 261102.
     *
     * @param label5 the new five-character label
     */
    public void setLabel5(String label5) {
        this.shortenedLabels.put(4, label5);
    }

    /**
     * Builds an independent copy of this entry - a new {@link UIEntry} with its own category list
     * (a fresh {@link ArrayList} of {@link UIEntryCategory#copy()} copies, so changing a copied
     * category's priority leaves the original alone) and its own {@link Flag} set, with the
     * description forced to the literal {@code "Copy"} since the constructor requires a non-null
     * one and this entry no longer has the original text (see
     * {@link #UIEntry(String, UIEntry, ElementEnum, StatElemType, UIEntryRenderer, CombinerName,
     * List, int, String, Flag, String, String, String, String)}). The {@link #template} reference
     * is shared, not copied. The shortened labels at keys {@code 0} to {@code 9} (C's
     * {@code MAX_SHORTENED} slots) are carried across, as {@code copy_shortened_labels} does in
     * {@code hatch_embryo}'s loop, as are {@link #statParameter} and {@link #elementParameter}.
     * Used by {@code UIEntryBaseAssembler} to stamp out one entry per expanded stat/element value
     * from a common base, the Java form of {@code hatch_embryo} building each expanded
     * {@code struct ui_entry} from the embryo's field values ({@code ui-entry.c}).
     *
     * <p>Function copy coded before 260916, commented in full on 261102.
     *
     * @return an independent copy of this entry
     */
    public UIEntry copy() {
        List<UIEntryCategory> categoriesCopy = new ArrayList<>();
        for (UIEntryCategory category : categories) {
            categoriesCopy.add(category.copy());
        }
        Flag<ChannelEntryFlag> copyFlags = new Flag<>(ChannelEntryFlag.class);
        copyFlags.copyFrom(entryFlag);
        UIEntry copyEntry = new UIEntry(this.name, this.template, this.elementParameter,
                this.statOrElementParameter, this.renderer,
                this.combineType, categoriesCopy, this.priorityNum, this.priorityString,
                copyFlags, "Copy",
                this.label, this.shortenedLabels.get(4), this.shortenedLabels.get(1));
        
        for (int index = 0; index < 10; index++) {
            if (shortenedLabels.containsKey(index)) {
                copyEntry.setShortenedLabel(index, shortenedLabels.get(index));
            }
        }
        copyEntry.setStatParameter(statParameter);
        copyEntry.setElementParameter(elementParameter);
        
        return copyEntry;
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
    public UIEntry getTemplate() {
        return template;
    }

    /**
     * Setter - directly overwrites this entry's template reference. Java-only, like
     * {@link #template}: nothing else on this class reads it, so changing it alters no other
     * field.
     *
     * <p>Function setTemplate coded before 260916, commented in full on 261102.
     *
     * @param template the new template reference
     */
    public void setTemplate(UIEntry template) {
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