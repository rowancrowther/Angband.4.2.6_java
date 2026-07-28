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

package uk.co.jackoftrades.middle.monsters;

/**
 * Free-standing helper routines for the monster subsystem — a port landing spot for the utility
 * corners of C's {@code mon-util.c} and friends.
 *
 * <p>Static methods that act on monsters without belonging to any one monster's data model.
 * Populated as callers need them.
 *
 * <p><b>Status:</b> a stub landed to unblock the game loop.
 *
 * @author Rowan Crowther
 */
public class MonsterUtils {
    /**
     * Remove the arena's combatant monster once the arena bout ends — the port of C's
     * {@code kill_arena_monster}, called from the game loop after an arena level is left behind.
     *
     * <p><b>Stub:</b> not yet implemented.
     *
     * @param monster the arena monster to remove (C passes the health-bar trackee)
     */
    public static void killArenaMonster(Monster monster) {
        // Stub class TODO: implement
    }
}
