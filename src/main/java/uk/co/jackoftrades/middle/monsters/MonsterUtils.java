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

import uk.co.jackoftrades.middle.cave.Chunk;
import uk.co.jackoftrades.middle.game.gameengine.GameState;
import uk.co.jackoftrades.middle.monsters.enums.MonTimed;

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

    /**
     * Recalculate what the player currently knows about a monster — the port of C's
     * {@code update_mon} ({@code mon-util.c}). Works out whether the monster is visible (by sight,
     * telepathy, or detection), updates its per-monster visibility flags accordingly, and triggers any
     * disturbance or redraw the change implies. Called whenever something that could affect a
     * monster's visibility has changed.
     *
     * <p><b>Stub:</b> not yet implemented.
     *
     * @param monster the monster whose visibility is being reassessed
     * @param cave    the level the monster lives on
     * @param full    when {@code true}, also recompute the monster's distance from the player before
     *                reassessing visibility; when {@code false}, reuse the stored distance
     */
    public static void updateMonster(Monster monster, Chunk cave, boolean full) {
        // Stub class TODO: implement
    }

    /**
     * Find the monster the player is currently commanding, i.e. the one carrying the
     * {@code MON_TMD_COMMAND} timed effect. The port of C's {@code get_commanded_monster}.
     * Scans the current level's monster list and returns the first match.
     *
     * @return the commanded monster, or {@code null} if none is under command
     */
    public static Monster getCommandMonster() {
        Chunk currentCave = GameState.getCave();
        for (Monster monster : currentCave.getMonsters()) {
            if (monster.getMonsterRace() == null)
                continue;

            if (monster.getMonTimed(MonTimed.MON_TMD_COMMAND) != 0)
                return monster;
        }

        return null;
    }

    public static void showMonsterMessages() {
        // STUB class: TODO: implement
    }
}
