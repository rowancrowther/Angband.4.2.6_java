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
 * The role a monster plays within its group/pack, mirroring the C original's
 * group-role constants — used by group AI to decide behaviour (e.g. bodyguards
 * stay near the leader).
 *
 * @author Rowan Crowther
 */
public enum MonsterGroupRole {
    /**
     * No special group role. @author Rowan Crowther
     */
    MON_GROUP_NONE,
    /**
     * The group's leader. @author Rowan Crowther
     */
    MON_GROUP_LEADER,
    /** A servant of the leader. @author Rowan Crowther */
    MON_GROUP_SERVANT,
    /** A bodyguard that stays near the leader. @author Rowan Crowther */
    MON_GROUP_BODYGUARD,
    /** An ordinary group member. @author Rowan Crowther */
    MON_GROUP_MEMBER,
    /** A summoned addition to the group. @author Rowan Crowther */
    MON_GROUP_SUMMON
}