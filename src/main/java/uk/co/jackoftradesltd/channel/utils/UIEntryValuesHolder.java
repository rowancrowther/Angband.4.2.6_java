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

package uk.co.jackoftradesltd.channel.utils;

import uk.co.jackoftradesltd.channel.uichannel.UIEntryValue;
import uk.co.jackoftradesltd.channel.uichannel.UIEntryValueShapshot;

import java.util.HashMap;

public class UIEntryValuesHolder {
    private static final UIEntryValueShapshot snapshot = new UIEntryValueShapshot(new HashMap<>());

    public static void addEntry(String name, UIEntryValue value) {
        snapshot.byEntryName().put(name, value);
    }

    public static void removeEntry(String name) {
        if (snapshot.byEntryName() == null || !snapshot.byEntryName().containsKey(name)) {
            return;
        }
        snapshot.byEntryName().remove(name);
    }

    public static UIEntryValue getEntryByName(String name) {
        if (snapshot.byEntryName() == null || !snapshot.byEntryName().containsKey(name))
            return null;
        return snapshot.byEntryName().get(name);
    }

    public static void clear() {
        if (snapshot.byEntryName() == null) {
            return;
        }
        snapshot.byEntryName().clear();
    }
}
