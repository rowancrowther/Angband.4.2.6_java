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

import java.util.ArrayList;

/**
 * A live group/pack of monsters on the level — its identifier, the monster index
 * of its leader, and the list of members. Group AI uses this so packs move and
 * fight cohesively. This is the Java port of the C original's
 * {@code struct monster_group} ({@code src/monster.h}).
 *
 * @author ClaudeCode
 */
public class MonsterGroup {
    /**
     * This group's identifier within the level.
     *
     * @author ClaudeCode
     */
    private int index;
    /**
     * The monster index of the group's leader.
     *
     * @author ClaudeCode
     */
    private int leader;
    /**
     * The group's members.
     *
     * @author ClaudeCode
     */
    private ArrayList<MonGroupListEntry> memberList;
}
