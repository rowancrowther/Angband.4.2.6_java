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

public enum DirectionEnum {
    DIR_UNKNOWN(0),
    DIR_NW(7),
    DIR_N(8),
    DIR_NE(9),
    DIR_W(4),
    DIR_TARGET(5),
    DIR_NONE(5),
    DIR_E(6),
    DIR_SW(1),
    DIR_S(2),
    DIR_SE(3);

    private final int key;

    DirectionEnum(int key) {
        this.key = key;
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
