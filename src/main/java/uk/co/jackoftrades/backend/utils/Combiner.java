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

package uk.co.jackoftrades.backend.utils;

import java.util.List;

public interface Combiner extends Cloneable {
    int UI_ENTRY_UNKNOWN_VALUE = Integer.MAX_VALUE;
    int UI_ENTRY_VALUE_NOT_PRESENT = Integer.MAX_VALUE - 1;
    int UI_ENTRY_RESIST0_RES_VUL = Integer.MAX_VALUE - 2;

    void init(int v, int a);

    void accum(int v, int a);

    UIEntryCombinerState finish();

    UIEntryCombinerState vec(int n, List<Integer> values, List<Integer> auxs);

    Object clone() throws CloneNotSupportedException;
}
