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

package uk.co.jackoftrades.middle.monsters.enums;

import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * The categories of monster spell, mirroring the C original's {@code RST_*}
 * spell types ({@code src/list-mon-spells.h}) — bolt/ball/breath/direct (which
 * deal damage), plus annoyance, haste, heal, tactic, escape, summon, innate and
 * archery types. Each constant records whether spells of that type deal damage,
 * used by monster AI to weigh offensive versus utility casting.
 *
 * @author Rowan Crowther
 */
public enum MonsterSpellType {
    /**
     * No spell type. @author Rowan Crowther
     */
    RST_NONE(false),
    /**
     * A damaging bolt. @author Rowan Crowther
     */
    RST_BOLT(true),
    /** A damaging ball. @author Rowan Crowther */
    RST_BALL(true),
    /** A damaging breath. @author Rowan Crowther */
    RST_BREATH(true),
    /** A damaging direct attack. @author Rowan Crowther */
    RST_DIRECT(true),
    /** A non-damaging annoyance spell. @author Rowan Crowther */
    RST_ANNOY(false),
    /** A self-haste spell. @author Rowan Crowther */
    RST_HASTE(false),
    /** A self-heal spell. @author Rowan Crowther */
    RST_HEAL(false),
    /** A heal-other spell. @author Rowan Crowther */
    RST_HEAL_OTHER(false),
    /** A tactical (repositioning) spell. @author Rowan Crowther */
    RST_TACTIC(false),
    /** An escape spell. @author Rowan Crowther */
    RST_ESCAPE(false),
    /** A summoning spell. @author Rowan Crowther */
    RST_SUMMON(false),
    /** An innate (non-magical) ability. @author Rowan Crowther */
    RST_INNATE(false),
    /** A ranged archery attack. @author Rowan Crowther */
    RST_ARCHERY(false);

    /**
     * Whether spells of this type deal damage.
     *
     * @author Rowan Crowther
     */
    private final boolean isDamage;

    /**
     * Bind a spell type to whether it is damaging.
     *
     * @param isDamage whether the type deals damage
     * @author Rowan Crowther
     */
    @Contract(mutates = "this")
    private MonsterSpellType(boolean isDamage) {
        this.isDamage = isDamage;
    }

    /**
     * @return whether spells of this type deal damage
     * @author Rowan Crowther
     */
    @Contract(pure = true)
    @CheckReturnValue
    @NotNull
    public boolean isDamage() {
        return isDamage;
    }
}
