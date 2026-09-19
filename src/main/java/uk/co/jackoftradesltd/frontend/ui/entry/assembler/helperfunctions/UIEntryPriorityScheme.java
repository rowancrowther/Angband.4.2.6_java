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

package uk.co.jackoftradesltd.frontend.ui.entry.assembler.helperfunctions;

import java.util.function.Function;

public enum UIEntryPriorityScheme {
    PRIORITY_SCHEME_NONE("", HelperFunctions::getDummyPriority),
    PRIORITY_SCHEME_INDEX("index", HelperFunctions::getPriorityFromIndex),
    PRIORITY_SCHEME_NEGATIVE_INDEX("negative index", HelperFunctions::getPriorityFromNegativeIndex);

    private final String name;
    private final Function<Integer, Integer> priorityResolver;

    UIEntryPriorityScheme(String name, Function<Integer, Integer> priorityResolver) {
        this.name = name;
        this.priorityResolver = priorityResolver;
    }

    public String getName() {
        return name;
    }

    public int getPriority(int index) {
        return priorityResolver.apply(index);
    }
}
