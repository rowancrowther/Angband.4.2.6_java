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
import uk.co.jackoftrades.middle.player.Player;
import uk.co.jackoftrades.middle.player.PlayerCalcs;

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

    /**
     * Flushes the queued monster messages to the player - the port of C's
     * {@code show_monster_messages} ({@code mon-msg.c}). The messages accumulate over a turn so
     * that several monsters doing the same thing are reported once, in one line, rather than
     * separately.
     *
     * <p><b>Stub:</b> not yet implemented, awaiting chapter 6.
     * {@code Player.noticeStuff} clears {@code PN_MON_MESSAGE} before calling this, so with the
     * body empty the queued messages are discarded rather than held over to the next pass.
     *
     * <p>Function showMonsterMessages stubbed on 260822, commented in full on 260824.
     */
    public static void showMonsterMessages() {
        // STUB class: TODO: implement as part of chapter 6
    }

    /**
     * Refreshes every living monster's view of the player - the port of C's
     * {@code update_monsters} ({@code mon-util.c:481}).
     *
     * <p>C walks the current level's monster array from index 1 to
     * {@code cave_monster_max(cave)} and calls {@code update_mon} on each entry that still has a
     * race, a dead monster being one whose race has been cleared. All the work is in
     * {@code update_mon} ({@code mon-util.c:291}), which touches only three things per monster: its
     * distance from the player, whether the player can currently see it, and the
     * {@code MFLAG_VIEW} flag that records line of sight.
     *
     * <p>The {@code full} flag is passed straight through: set, it also recomputes each monster's
     * cached distance from the player, which is needed only when the player or the monster has
     * moved. A visibility-only pass - the player going blind, gaining telepathy or see-invisible, a
     * grid changing its lighting - leaves the distances alone and passes {@code false}. That is why
     * {@link PlayerCalcs#updateStuff(Player)} lets {@code PU_DISTANCE} subsume {@code PU_MONSTERS}: the full pass
     * does everything the cheap one would.
     *
     * <p>C notes that this runs once per monster on every player move, and is one of the main
     * bottlenecks while running, alongside view recalculation - so the eventual implementation
     * should stay allocation-free in the loop.
     *
     * <p><b>Outstanding:</b> this is a stub and does nothing. It is scheduled for chapter 6 with
     * the rest of the monster work, and the loop is over the level's monsters rather than over anything
     * the player owns.
     *
     * <p>Function updateMonsters coded before 260828, commented in full on 260828.
     *
     * @param full {@code true} to recompute each monster's distance from the player as well as its
     *             visibility; {@code false} for a visibility-only pass.
     * @see PlayerCalcs#updateStuff(Player)
     */
    public static void updateMonsters(boolean full) {
        // STUB function: TODO: Implement in chapter 6
    }
}
