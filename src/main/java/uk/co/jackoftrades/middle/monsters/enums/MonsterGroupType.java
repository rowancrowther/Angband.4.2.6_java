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

/**
 * Distinguishes a monster's primary (originally-generated) group from a group
 * formed by summoning. Mirrors the C original's group-type distinction.
 *
 * @author Rowan Crowther
 */
public enum MonsterGroupType {
    /**
     * The monster's primary/original group. @author Rowan Crowther
     */
    PRIMARY_GROUP,
    /**
     * A group formed by summoning. @author Rowan Crowther
     */
    SUMMON_GROUP,
    /** Count sentinel. @author Rowan Crowther */
    GROUP_MAX
}
