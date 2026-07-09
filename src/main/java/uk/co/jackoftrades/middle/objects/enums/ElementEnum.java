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
    ELEM_NONE,
    ELEM_ACID,
    ELEM_ELEC,
    ELEM_FIRE,
    ELEM_COLD,
    ELEM_POIS,
    ELEM_LIGHT,
    ELEM_DARK,
    ELEM_SOUND,
    ELEM_SHARD,
    ELEM_NEXUS,
    ELEM_NETHER,
    ELEM_CHAOS,
    ELEM_DISEN,
    ELEM_WATER,
    ELEM_ICE,
    ELEM_GRAVITY,
    ELEM_INERTIA,
    ELEM_FORCE,
    ELEM_TIME,
    ELEM_PLASMA,
    ELEM_METEOR,
    ELEM_MISSILE,
    ELEM_MANA,
    ELEM_HOLY_ORB,
    ELEM_ARROW,
    ELEM_MAX
}
