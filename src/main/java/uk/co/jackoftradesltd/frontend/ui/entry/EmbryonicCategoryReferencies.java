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

import uk.co.jackoftradesltd.frontend.ui.entry.assembler.helperfunctions.UIEntryPriorityScheme;

public class EmbryonicCategoryReferencies {
    boolean prioritySet;
    private String name;
    private UIEntryPriorityScheme pSourceIndex;
    private int priority;

    public EmbryonicCategoryReferencies(String name, UIEntryPriorityScheme pSourceIndex, int priority, boolean prioritySet) {
        this.name = name;
        this.pSourceIndex = pSourceIndex;
        this.priority = priority;
        this.prioritySet = prioritySet;
    }

    public String getName() {
        return name;
    }

    public UIEntryPriorityScheme getpSourceIndex() {
        return pSourceIndex;
    }

    public int getPriority() {
        return priority;
    }

    public boolean isPrioritySet() {
        return prioritySet;
    }
}
