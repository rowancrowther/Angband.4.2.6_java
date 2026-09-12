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

import java.util.List;

public class UIEntryIterator {
    private List<UIEntry> entries;
    private int num;
    private int index;

    public UIEntryIterator(List<UIEntry> entries, int num, int index) {
        this.entries = entries;
        this.num = num;
        this.index = index;
    }

    public UIEntryIterator(List<UIEntry> entries) {
        this.entries = entries;
        this.num = 0;
        this.index = 0;
    }

    public List<UIEntry> getEntries() {
        return entries;
    }

    public int getNum() {
        return num;
    }

    public int getIndex() {
        return index;
    }
}
