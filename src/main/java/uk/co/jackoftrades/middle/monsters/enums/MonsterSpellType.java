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

package uk.co.jackoftrades.middle.monsters.enums;

import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public enum MonsterSpellType {
    RST_NONE(false),
    RST_BOLT(true),
    RST_BALL(true),
    RST_BREATH(true),
    RST_DIRECT(true),
    RST_ANNOY(false),
    RST_HASTE(false),
    RST_HEAL(false),
    RST_HEAL_OTHER(false),
    RST_TACTIC(false),
    RST_ESCAPE(false),
    RST_SUMMON(false),
    RST_INNATE(false),
    RST_ARCHERY(false);

    private final boolean isDamage;

    @Contract(mutates = "this")
    private MonsterSpellType(boolean isDamage) {
        this.isDamage = isDamage;
    }

    @Contract(pure = true)
    @CheckReturnValue
    @NotNull
    public boolean isDamage() {
        return isDamage;
    }
}
