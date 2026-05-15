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

package uk.co.jackoftrades.middle.cave.enums;

import uk.co.jackoftrades.middle.cave.Loc;

public enum DirectionEnum {
    DIR_UNKNOWN(0, 0, 0),
    DIR_NW(7, -1, 1),
    DIR_N(8, 0, 1),
    DIR_NE(9, 1, 1),
    DIR_W(4, -1, 0),
    DIR_TARGET(5, 0, 0),
    DIR_NONE(5, 0, 0),
    DIR_E(6, 1, 0),
    DIR_SW(1, -1, -1),
    DIR_S(2, 0, -1),
    DIR_SE(3, 1, -1),
    ;

    private final int key;
    private final int xOffset;
    private final int yOffset;

    DirectionEnum(int key, int xOffset, int yOffset) {
        this.key = key;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
    }

    public int ddx() {
        return xOffset;
    }

    public int ddy() {
        return yOffset;
    }

    public Loc ddgrid() {
        return new Loc(xOffset, yOffset);
    }

    public int getKey() {
        return key;
    }

    public static DirectionEnum fromKey(int key) {
        for (DirectionEnum d : DirectionEnum.values()) {
            if (d.getKey() == key) {
                return d;
            }
        }

        return DIR_UNKNOWN;
    }
}
