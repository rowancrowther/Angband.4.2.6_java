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

import uk.co.jackoftradesltd.frontend.entries.UIEntry;
import uk.co.jackoftradesltd.frontend.entries.UIEntryRenderer;
import uk.co.jackoftradesltd.frontend.ui.entry.assembler.UIEntryAssembler;
import uk.co.jackoftradesltd.frontend.ui.entry.assembler.helperfunctions.UIEntryNameParameter;
import uk.co.jackoftradesltd.frontend.ui.entry.assembler.helperfunctions.UIEntryPriorityScheme;

import java.util.List;

/**
 * A {@code ui_entry.txt}/{@code ui_entry_base.txt} record still being assembled, the Java form of
 * C's {@code struct embryonic_ui_entry} ({@code [C] ui-entry.c:170-177}: {@code entry},
 * {@code categories}, {@code param_index}, {@code psource_index}, {@code last_category_index},
 * {@code exists}). C builds one of these per record as its directives are parsed one at a time,
 * carried across parser calls via {@code parser_priv}, then converts it into a finished
 * {@link UIEntry} in {@code hatch_embryo} ({@code [C] ui-entry.c:1756-1863}) once the next
 * {@code name:} directive (or end of file) closes the record off.
 *
 * <p>Three fields hold resolved objects here where C still holds table offsets at this stage:
 * {@link #parmIndex} is the {@link UIEntryNameParameter} constant itself rather than C's
 * {@code param_index} into {@code name_parameters[]}, {@link #pSourceIndex} is the
 * {@link UIEntryPriorityScheme} constant rather than C's {@code psource_index} into
 * {@code priority_schemes[]}, and {@link #lastCategoryIndex} holds the
 * {@link EmbryonicCategoryReferencies} object most recently inserted rather than C's
 * {@code last_category_index} position within the category array - a plain index has nothing to
 * point at here since {@link #categories} is a {@link List} rather than a hand-managed C array.
 *
 * <p>Live, but not in the shape the paragraph above describes: {@link UIEntryAssembler}'s create
 * path ({@code parseEachEntry}) does build one of these per record, but in a single call from an
 * already-fully-parsed {@code UIEntryParseRecord} rather than by accumulating it field-by-field
 * across successive parser calls the way C's {@code parser_priv}-carried struct does - the object
 * is constructed, filled and read back to {@link #getUiEntry()} within that one method call, and
 * nothing outside it ever holds onto one across separate invocations.
 *
 * <p>Class UIEntryEmbryo coded before 260920, commented in full on 260922.
 *
 * @author Rowan Crowther
 */
public class UIEntryEmbryo {
    /**
     * The entry being assembled. The Java form of C's {@code embryonic_ui_entry.entry}
     * ({@code [C] ui-entry.c:171}).
     *
     * <p>Field uiEntry coded before 260920, commented in full on 260920.
     */
    private UIEntry uiEntry;
    /**
     * The categories accumulated for this entry so far, in the same insertion order C's
     * {@code insert_embryo_category} ({@code [C] ui-entry.c:1447-1561}) keeps its array in. The Java
     * form of C's {@code embryonic_ui_entry.categories} ({@code [C] ui-entry.c:172}).
     *
     * <p>Field categories coded before 260920, commented in full on 260920.
     */
    private List<EmbryonicCategoryReferencies> categories;
    /**
     * Which name-parameterisation scheme (none, {@code element} or {@code stat}) this record's
     * {@code parameter:} directive selected, resolved to the {@link UIEntryNameParameter} constant
     * itself. The Java form of C's {@code embryonic_ui_entry.param_index}
     * ({@code [C] ui-entry.c:173}), an index into {@code name_parameters[]}.
     *
     * <p>Field parmIndex coded before 260920, commented in full on 260920.
     */
    private UIEntryNameParameter parmIndex;
    /**
     * Which priority scheme this record's {@code priority:} directive selected, resolved to the
     * {@link UIEntryPriorityScheme} constant itself, for use as the default for categories declared
     * after it. The Java form of C's {@code embryonic_ui_entry.psource_index}
     * ({@code [C] ui-entry.c:174}), an index into {@code priority_schemes[]}.
     *
     * <p>Field pSourceIndex coded before 260920, commented in full on 260920.
     */
    private UIEntryPriorityScheme pSourceIndex;
    /**
     * The category most recently inserted by a {@code category:} directive, so that a following
     * {@code priority:} directive with a category already open knows which one to attach its
     * priority to. The Java form of C's {@code embryonic_ui_entry.last_category_index}
     * ({@code [C] ui-entry.c:175}), which holds the category's array position rather than the
     * category itself, since {@link #categories} needs no such index here.
     *
     * <p>Field lastCategoryIndex coded before 260920, commented in full on 260920.
     */
    private EmbryonicCategoryReferencies lastCategoryIndex;
    /**
     * Whether this embryo was reopened from an already-inserted {@link UIEntry} (a repeated
     * {@code name:}) rather than built fresh. The Java form of C's {@code embryonic_ui_entry.exists}
     * ({@code [C] ui-entry.c:176}), which {@code hatch_embryo} uses to skip re-inserting an entry
     * that is only being edited.
     *
     * <p>Field exists coded before 260920, commented in full on 260920.
     */
    private boolean exists;

    /**
     * Build an embryo around an entry, its accumulated categories, its name and priority schemes,
     * its most recently inserted category, and whether it is editing an existing entry. The Java
     * form of the field assignments C performs when {@code parse_entry_name}
     * ({@code [C] ui-entry.c:1882-1943}) opens a new record.
     *
     * <p>Function UIEntryEmbryo coded before 260920, commented in full on 260920.
     *
     * @param uiEntry           the entry being assembled
     * @param categories        the categories accumulated for this entry so far
     * @param parmIndex         the name-parameterisation scheme selected for this record
     * @param pSourceIndex      the priority scheme selected for this record
     * @param lastCategoryIndex the category most recently inserted, or {@code null} if none has been
     *                          yet
     * @param exists            whether this embryo is editing an already-inserted entry
     */
    public UIEntryEmbryo(UIEntry uiEntry, List<EmbryonicCategoryReferencies> categories, UIEntryNameParameter parmIndex,
                         UIEntryPriorityScheme pSourceIndex, EmbryonicCategoryReferencies lastCategoryIndex, boolean exists) {
        this.uiEntry = uiEntry;
        this.categories = categories;
        this.parmIndex = parmIndex;
        this.pSourceIndex = pSourceIndex;
        this.lastCategoryIndex = lastCategoryIndex;
        this.exists = exists;
    }

    /**
     * Returns the entry being assembled.
     *
     * <p>Function getUiEntry coded before 260920, commented in full on 260920.
     *
     * @return this embryo's entry
     */
    public UIEntry getUiEntry() {
        return uiEntry;
    }

    /**
     * Sets the entry being assembled.
     *
     * <p>Function setUiEntry coded before 260920, commented in full on 260920.
     *
     * @param uiEntry the entry to assemble
     */
    public void setUiEntry(UIEntry uiEntry) {
        this.uiEntry = uiEntry;
    }

    /**
     * Returns the categories accumulated for this entry so far.
     *
     * <p>Function getCategories coded before 260920, commented in full on 260920.
     *
     * @return this embryo's accumulated categories
     */
    public List<EmbryonicCategoryReferencies> getCategories() {
        return categories;
    }

    /**
     * Replaces the categories accumulated for this entry.
     *
     * <p>Function setCategories coded before 260920, commented in full on 260920.
     *
     * @param categories the categories to accumulate
     */
    public void setCategories(List<EmbryonicCategoryReferencies> categories) {
        this.categories = categories;
    }

    /**
     * Appends one category to those accumulated for this entry, the Java form of the array-growing
     * half of C's {@code insert_embryo_category} ({@code [C] ui-entry.c:1447-1561}), simplified since
     * {@link #categories} is a {@link List} rather than a hand-managed C array needing its own
     * reallocation.
     *
     * <p>Function addCategories coded before 260920, commented in full on 260920.
     *
     * @param category the category to append
     */
    public void addCategories(EmbryonicCategoryReferencies category) {
        categories.add(category);
    }

    /**
     * Returns which name-parameterisation scheme this record's {@code parameter:} directive
     * selected.
     *
     * <p>Function getParmIndex coded before 260920, commented in full on 260920.
     *
     * @return this embryo's name-parameterisation scheme
     */
    public UIEntryNameParameter getParmIndex() {
        return parmIndex;
    }

    /**
     * Sets which name-parameterisation scheme this record's {@code parameter:} directive selected.
     *
     * <p>Function setParmIndex coded before 260920, commented in full on 260920.
     *
     * @param parmIndex the name-parameterisation scheme to select
     */
    public void setParmIndex(UIEntryNameParameter parmIndex) {
        this.parmIndex = parmIndex;
    }

    /**
     * Returns which priority scheme this record's {@code priority:} directive selected.
     *
     * <p>Function getPSourceIndex coded before 260920, commented in full on 260920.
     *
     * @return this embryo's priority scheme
     */
    public UIEntryPriorityScheme getPSourceIndex() {
        return pSourceIndex;
    }

    /**
     * Sets which priority scheme this record's {@code priority:} directive selected.
     *
     * <p>Function setpSourceIndex coded before 260920, commented in full on 260920.
     *
     * @param pSourceIndex the priority scheme to select
     */
    public void setpSourceIndex(UIEntryPriorityScheme pSourceIndex) {
        this.pSourceIndex = pSourceIndex;
    }

    /**
     * Returns the category most recently inserted by a {@code category:} directive.
     *
     * <p>Function getLastCategoryIndex coded before 260920, commented in full on 260920.
     *
     * @return the most recently inserted category, or {@code null} if none has been yet
     */
    public EmbryonicCategoryReferencies getLastCategoryIndex() {
        return lastCategoryIndex;
    }

    /**
     * Sets the category most recently inserted by a {@code category:} directive.
     *
     * <p>Function setLastCategoryIndex coded before 260920, commented in full on 260920.
     *
     * @param lastCategoryIndex the category to record as most recently inserted
     */
    public void setLastCategoryIndex(EmbryonicCategoryReferencies lastCategoryIndex) {
        this.lastCategoryIndex = lastCategoryIndex;
    }

    /**
     * Returns whether this embryo is editing an already-inserted entry rather than building a fresh
     * one.
     *
     * <p>Function exists coded before 260920, commented in full on 260920.
     *
     * @return {@code true} if this embryo was reopened from an existing entry
     */
    public boolean exists() {
        return exists;
    }

    /**
     * Sets whether this embryo is editing an already-inserted entry.
     *
     * <p>Function setExists coded before 260920, commented in full on 260920.
     *
     * @param b {@code true} if this embryo is editing an existing entry
     */
    public void setExists(boolean b) {
        exists = b;
    }
}
