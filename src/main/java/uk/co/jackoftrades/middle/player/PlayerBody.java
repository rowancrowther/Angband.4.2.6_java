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
 *    Java code copyright (c) Rowan Crowther 2026
 */

package uk.co.jackoftrades.middle.player;

import uk.co.jackoftrades.middle.objects.ItemObject;

import java.util.HashMap;

public class PlayerBody {
    private String name;
    private int count;
    private HashMap<Integer, ItemObject> equipmentSlots;

    public boolean isEquipped(ItemObject item) {
        return equipmentSlots.containsValue(item);
    }
}
