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
import java.util.function.Supplier;

public enum UIEntryNameParameter {
    ENTRY_NAME_PARAMETER_NONE("", HelperFunctions::getDummyParamCount,
            HelperFunctions::getDummyParamName),
    ENTRY_NAME_PARAMETER_ELEMENT("element", HelperFunctions::getElementCount,
            HelperFunctions::getElementName),
    ENTRY_NAME_PARAMETER_STAT("stat", HelperFunctions::getStatCount,
            HelperFunctions::getStatName);

    private final String name;
    private final Function<Integer, String> nameResolver;
    private final Supplier<Integer> countResolver;

    UIEntryNameParameter(String s, Supplier<Integer> dummyParamCount, Function<Integer, String> dummyParamName) {
        this.name = s;
        this.nameResolver = dummyParamName;
        this.countResolver = dummyParamCount;
    }

    public String getName() {
        return name;
    }

    public int getCount() {
        return countResolver.get();
    }

    public String getParamName(int index) {
        return nameResolver.apply(index);
    }
}