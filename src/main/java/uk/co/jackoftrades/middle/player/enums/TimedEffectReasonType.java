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

package uk.co.jackoftrades.middle.player.enums;

import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

public enum TimedEffectReasonType {
    TYPE_NONE(0),
    TYPE_OBJECT_FLAG(1),
    TYPE_RESIST(2),
    TYPE_VULN(3),
    TYPE_PLAYER_FLAG(4),
    TYPE_TIMED_EFFECT(5);

    private final int value;

    @Contract(mutates = "this")
    private TimedEffectReasonType(int index) {
        this.value = index;
    }

    @Contract(pure = true)
    @Nullable
    @CheckReturnValue
    public static TimedEffectReasonType fromValue(int value) {
        return switch (value) {
            case 0 -> TYPE_NONE;
            case 1 -> TYPE_OBJECT_FLAG;
            case 2 -> TYPE_RESIST;
            case 3 -> TYPE_VULN;
            case 4 -> TYPE_PLAYER_FLAG;
            case 5 -> TYPE_TIMED_EFFECT;
            default -> null;
        };
    }
}