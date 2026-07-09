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

package uk.co.jackoftrades.frontend.entries;

import uk.co.jackoftrades.frontend.entries.enums.EntryFlag;
import uk.co.jackoftrades.frontend.screen.enums.CombinerName;
import uk.co.jackoftrades.middle.objects.enums.ElementEnum;

import java.util.List;

/**
 * A single entry in the player status display (one stat or resistance line),
 * ported from the C original's UI-entry system ({@code src/ui-entry.c}). Each
 * entry binds a stat or element parameter to a renderer, a value combiner, a
 * priority (for ordering/space competition), one or more labels for different
 * widths, and an optional {@link UIEntryBase} template it inherits defaults from.
 *
 * @author Rowan Crowther
 */
public class UIEntry {
    /**
     * The entry's internal name.
     *
     * @author Rowan Crowther
     */
    private String name;
    /**
     * The element this entry tracks, when its parameter is an element.
     *
     * @author Rowan Crowther
     */
    private ElementEnum parameter;
    /**
     * True if the parameter is an element; false if it is a stat.
     *
     * @author Rowan Crowther
     */
    private StatElemType statOrElement;
    /**
     * The renderer used to draw this entry's value.
     *
     * @author Rowan Crowther
     */
    private UIEntryRenderer renderer;
    /**
     * How multiple contributing values for this entry are combined.
     *
     * @author Rowan Crowther
     */
    private CombinerName combineType;
    /**
     * Numeric display priority (used when space is limited).
     *
     * @author Rowan Crowther
     */
    private int priorityNum;
    /**
     * Behavioural flag for this entry.
     *
     * @author Rowan Crowther
     */
    private EntryFlag entryFlag;
    /**
     * Human-readable description.
     *
     * @author Rowan Crowther
     */
    private String description;
    /**
     * Default-width label text.
     *
     * @author Rowan Crowther
     */
    private String label;
    /**
     * Two-character label variant.
     *
     * @author Rowan Crowther
     */
    private String label2;
    /**
     * Five-character label variant.
     *
     * @author Rowan Crowther
     */
    private String label5;
    /**
     * Categories this entry belongs to (used for grouping on screen).
     *
     * @author Rowan Crowther
     */
    private List<String> categories;
    /**
     * The template this entry inherits defaults from, if any.
     *
     * @author Rowan Crowther
     */
    private UIEntryBase template;

    /**
     * Build a UI status entry from its parsed data-file fields. Note the
     * constructor parameter order places {@code label5} before {@code label2},
     * matching the data-file column order.
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
     * @author Rowan Crowther
     */
    public UIEntry(String name,
                   ElementEnum parameter,
                   StatElemType parmType,
                   UIEntryRenderer renderer,
                   CombinerName combineType,
                   List<String> categories,
                   int priorityNum,
                   EntryFlag entryFlag,
                   String description,
                   String label,
                   String label5,
                   String label2,
                   UIEntryBase template) {
        this.name = name;
        this.parameter = parameter;
        this.statOrElement = parmType;
        this.renderer = renderer;
        this.combineType = combineType;
        this.priorityNum = priorityNum;
        this.entryFlag = entryFlag;
        this.description = description;
        this.label = label;
        this.label2 = label2;
        this.label5 = label5;
        this.categories = categories;
        this.template = template;
    }

    /**
     * @return this entry's internal name
     * @author Rowan Crowther
     */
    public String getName() {
        return name;
    }

    /**
     * Whether a UI entry's parameter refers to a player stat or a damage element.
     *
     * @author Rowan Crowther
     */
    public enum StatElemType {
        NONE,
        /**
         * The parameter is a player stat (STR, INT, …). @author Rowan Crowther
         */
        STAT,
        /**
         * The parameter is a damage element (fire, cold, …). @author Rowan Crowther
         */
        ELEMENT
    }

    /**
     * @return a debug string listing this entry's fields
     * @author Rowan Crowther
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
                ", description='" + description + '\'' +
                ", label='" + label + '\'' +
                ", label2='" + label2 + '\'' +
                ", label5='" + label5 + '\'' +
                ", categories=" + categories +
                ", template='" + template + '\'' +
                '}';
    }

    public ElementEnum getParameter() {
        return parameter;
    }

    public StatElemType getStatOrElement() {
        return statOrElement;
    }

    public UIEntryRenderer getRenderer() {
        return renderer;
    }

    public CombinerName getCombineType() {
        return combineType;
    }

    public int getPriorityNum() {
        return priorityNum;
    }

    public EntryFlag getEntryFlag() {
        return entryFlag;
    }

    public String getDescription() {
        return description;
    }

    public String getLabel() {
        return label;
    }

    public String getLabel2() {
        return label2;
    }

    public String getLabel5() {
        return label5;
    }

    public List<String> getCategories() {
        return categories;
    }

    public UIEntryBase getTemplate() {
        return template;
    }
}