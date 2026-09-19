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
import uk.co.jackoftradesltd.frontend.ui.entry.assembler.UIEntryAssembler;
import uk.co.jackoftradesltd.frontend.ui.entry.assembler.helperfunctions.UIEntryNameParameter;
import uk.co.jackoftradesltd.frontend.ui.entry.assembler.helperfunctions.UIEntryPriorityScheme;

import java.util.List;

public class UIEntryEmbryo {
    private UIEntry uiEntry;
    private List<EmbryonicCategoryReferencies> categories;
    private UIEntryNameParameter parmIndex;
    private UIEntryPriorityScheme pSourceIndex;
    private int lastCategoryIndex;
    private boolean exists;

    public UIEntryEmbryo(UIEntry uiEntry, List<EmbryonicCategoryReferencies> categories, UIEntryNameParameter parmIndex,
                         UIEntryPriorityScheme pSourceIndex, int lastCategoryIndex, boolean exists) {
        this.uiEntry = uiEntry;
        this.categories = categories;
        this.parmIndex = parmIndex;
        this.pSourceIndex = pSourceIndex;
        this.lastCategoryIndex = lastCategoryIndex;
        this.exists = exists;
    }

    public UIEntry getUiEntry() {
        return uiEntry;
    }

    public void setUiEntry(UIEntry uiEntry) {
        this.uiEntry = uiEntry;
    }

    public List<EmbryonicCategoryReferencies> getCategories() {
        return categories;
    }

    public void setCategories(List<EmbryonicCategoryReferencies> categories) {
        this.categories = categories;
    }

    public UIEntryNameParameter getParmIndex() {
        return parmIndex;
    }

    public void setParmIndex(UIEntryNameParameter parmIndex) {
        this.parmIndex = parmIndex;
    }

    public UIEntryPriorityScheme getpSourceIndex() {
        return pSourceIndex;
    }

    public void setpSourceIndex(UIEntryPriorityScheme pSourceIndex) {
        this.pSourceIndex = pSourceIndex;
    }

    public int getLastCategoryIndex() {
        return lastCategoryIndex;
    }

    public void setLastCategoryIndex(int lastCategoryIndex) {
        this.lastCategoryIndex = lastCategoryIndex;
    }

    public boolean exists() {
        return exists;
    }

    public void setExists(boolean b) {
        exists = b;
    }
}
