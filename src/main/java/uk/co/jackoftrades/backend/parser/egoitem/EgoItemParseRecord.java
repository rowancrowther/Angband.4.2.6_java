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

package uk.co.jackoftrades.backend.parser.egoitem;

import java.util.List;
import java.util.Map;

public record EgoItemParseRecord(String name,
                                 String cost, String rating,
                                 String commonness, String lower, String upper,
                                 String toH, String toD, String toA,
                                 String minToH, String minToD, String minToA,
                                 List<String> tVals,
                                 List<ItemRef> itemRefs,
                                 List<String> flags,
                                 List<String> flagsOff,
                                 Map<String, String> values,
                                 Map<String, String> minValues,
                                 String act,
                                 String timeout,
                                 List<String> brands,
                                 List<String> slays,
                                 Map<String, String> curses,
                                 String desc,
                                 int line) {

    public record ItemRef(String tVal, String sVal) {
    }
}
