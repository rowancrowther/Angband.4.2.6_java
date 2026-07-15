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

package uk.co.jackoftrades.middle.objects.enums;

/**
 * The damage elements as referenced by object code (resistances, brands, slays).
 * Mirrors the C original's {@code ELEM_*} list; the constants are self-describing
 * and documented collectively here. {@code ELEM_MAX} is the count sentinel.
 *
 * @author Rowan Crowther
 */
public enum ElementEnum {
    ELEM_NONE(false),
    ELEM_ACID(true),
    ELEM_ELEC(true),
    ELEM_FIRE(true),
    ELEM_COLD(true),
    ELEM_POIS(false),
    ELEM_LIGHT(false),
    ELEM_DARK(false),
    ELEM_SOUND(false),
    ELEM_SHARD(false),
    ELEM_NEXUS(false),
    ELEM_NETHER(false),
    ELEM_CHAOS(false),
    ELEM_DISEN(false),
    ELEM_WATER(false),
    ELEM_ICE(false),
    ELEM_GRAVITY(false),
    ELEM_INERTIA(false),
    ELEM_FORCE(false),
    ELEM_TIME(false),
    ELEM_PLASMA(false),
    ELEM_METEOR(false),
    ELEM_MISSILE(false),
    ELEM_MANA(false),
    ELEM_HOLY_ORB(false),
    ELEM_ARROW(false),
    ELEM_MAX(false);

    /**
     * Whether this is a "base" element — the four physical damage types (acid, electricity, fire,
     * cold) that objects can ignore/be affected by as a group. Ports the {@code base} column of C's
     * {@code list-elements.h}; used e.g. when a dungeon spellbook is set to ignore every base element.
     *
     * @author Rowan Crowther
     */
    private final boolean isBase;

    /**
     * @param isBase whether this element is one of the four base (physical) damage types
     * @author Rowan Crowther
     */
    ElementEnum(boolean isBase) {
        this.isBase = isBase;
    }

    /**
     * @return {@code true} if this is a base (physical) element
     * @author Rowan Crowther
     */
    public boolean isBase() {
        return isBase;
    }
}
