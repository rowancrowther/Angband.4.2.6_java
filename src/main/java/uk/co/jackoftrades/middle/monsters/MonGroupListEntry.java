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

package uk.co.jackoftrades.middle.monsters;

/**
 * One node in a monster group's member list, holding the monster index of a
 * group member. This is the Java port of the C original's {@code mon_group_list_entry}
 * ({@code src/monster.h}).
 *
 * @author ClaudeCode
 */
public class MonGroupListEntry {
    /**
     * The monster index of this group member.
     *
     * @author ClaudeCode
     */
    private int mIdx;
}
