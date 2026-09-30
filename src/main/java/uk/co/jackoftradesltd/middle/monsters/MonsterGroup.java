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

package uk.co.jackoftradesltd.middle.monsters;

import uk.co.jackoftradesltd.middle.cave.Chunk;

import java.util.ArrayList;

/**
 * A live group/pack of monsters on the level — its identifier, the monster index
 * of its leader, and the list of members. Group AI uses this so packs move and
 * fight cohesively. This is the Java port of the C original's
 * {@code struct monster_group} ({@code src/mon-group.h}).
 *
 * @author Rowan Crowther
 */
public class MonsterGroup {
    /**
     * This group's identifier within the level, C's {@code index}.
     */
    private int index;
    /**
     * The monster index of the group's leader, C's {@code leader}. It has to follow the leader if
     * {@link #monsterGroupChangeIndex} renumbers it.
     */
    private int leader;
    /**
     * The group's members, C's {@code member_list}. Each entry holds a monster index, so these too
     * must follow a renumbering.
     */
    private ArrayList<MonGroupListEntry> memberList;

    /**
     * Repoints a pack at a monster that has moved from one slot of the level's monster list to
     * another, the port of C's {@code monster_group_change_index} ({@code mon-group.c}). It is called
     * by {@code Chunk.monsterIndexMove} while the level's monster list is being compacted, and a
     * {@code false} result there is treated as fatal ("Bad monster group info!").
     *
     * <p>When ported it has to find every group that refers to {@code fromIndex}, whether as its
     * leader or as a member, and rewrite the reference to {@code toIndex}, reporting {@code false}
     * if the group information is inconsistent.
     *
     * <p><b>Stub:</b> waiting on Chapter 6 (monsters and combat), where monster groups are ported.
     * It does nothing and always reports success, so packs are not renumbered when the monster list
     * is compacted, and the consistency check in the caller cannot fire.
     *
     * <p>Function monsterGroupChangeIndex stubbed, commented in full on 260930.
     *
     * @param c         the level whose groups are being updated
     * @param toIndex   the monster's new index
     * @param fromIndex the monster's old index
     * @return {@code true} if the groups were updated (always, until this is ported)
     */
    public static boolean monsterGroupChangeIndex(Chunk c, int toIndex, int fromIndex) {
        // Stub function
        // TODO Implement
        return true;
    }
}
