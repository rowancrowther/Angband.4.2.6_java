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

package uk.co.jackoftrades.middle.cave;

import uk.co.jackoftrades.backend.utils.Flag;
import uk.co.jackoftrades.middle.cave.enums.SquareEnum;
import uk.co.jackoftrades.middle.objects.ItemObject;

import java.util.ArrayList;

public class Square {
    private int feat;
    private Flag<SquareEnum> info;

    private int light;
    private int monster;
    private ArrayList<ItemObject> objects;
    private ArrayList<Trap> traps;

    public boolean isFloor(Loc grid) {
        return false;
    }
}
