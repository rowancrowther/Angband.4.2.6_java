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

package uk.co.jackoftrades.middle.player;

import uk.co.jackoftrades.channel.utils.Flag;
import uk.co.jackoftrades.middle.cave.Loc;
import uk.co.jackoftrades.middle.cave.store.Store;
import uk.co.jackoftrades.middle.game.gameengine.GameState;
import uk.co.jackoftrades.middle.game.globals.GameConstants;
import uk.co.jackoftrades.middle.player.enums.PlayerOverExertion;
import uk.co.jackoftrades.middle.player.enums.TimedEffect;

/**
 * Free-standing player helper routines — the port of C's {@code player-util.c}.
 *
 * <p>Holds behaviour that operates on a {@link Player} but does not belong to the player's own data
 * model: resting/interrupt handling, terrain interaction and similar utilities the game loop and
 * commands call through. Modelled as static methods over a passed-in {@link Player}, mirroring C's
 * free functions that take {@code struct player *} rather than reading a global.
 *
 * <p><b>Status:</b> a stub landed to unblock the game loop; individual routines are ported as callers
 * need them.
 *
 * @author Rowan Crowther
 */
public class PlayerUtils {
    private static Player player;

    static {
        player = GameState.getPlayer();
    }

    /**
     * Check whether an in-progress rest should end for a special reason — the port of C's
     * {@code player_resting_complete_special}. Called at the top of the player-processing pass so a
     * rest can be interrupted the moment its stopping condition (e.g. HP/SP restored) is met.
     *
     * <p><b>Stub:</b> not yet implemented — takes no action until the resting subsystem is ported.
     */
    public static void restingCompleteSpecial() {
        // Stub class TODO: implement
    }

    /**
     * Apply any damage the terrain under the player inflicts — the port of C's
     * {@code player_take_terrain_damage} ({@code player-util.c}). Some features (lava and similar)
     * hurt whatever stands on them; this works out the damage for the player's current grid, applies
     * it, and reports the cause. Called once per turn as part of the post-command cleanup.
     *
     * <p><b>Stub:</b> not yet implemented.
     *
     * @param grid   the grid to test — normally the player's own location
     */
    public static void takeTerrainDamage(Loc grid) {
        // Stub class TODO: implement
    }

    /**
     * Applies damage to the player from a named source, handling death if it is fatal — the port of
     * C's {@code take_hit} ({@code player-util.c}).
     *
     * <p><b>Stub:</b> not yet implemented.
     *
     * @param damage the amount of damage to inflict
     * @param cause  the death-message cause, used if the hit is fatal
     * @author Rowan Crowther
     */
    public static void takeHit(int damage, String cause) {
        // Stub class TODO: implement
    }

    /**
     * Reduces an incoming damage figure by the player's protections — the port of C's percentage
     * damage-reduction handling. Invulnerability nullifies non-massive hits outright; otherwise the
     * player's percentage reduction is applied, and the result is floored at zero.
     *
     * @param damage the raw incoming damage
     * @return the damage remaining after reduction (never negative)
     * @author Rowan Crowther
     */
    public static int applyDamageReduction(int damage) {
        // Hack - apply invulnerability
        if (player.getTimedEffect(TimedEffect.TMD_INVULN) != 0 && (damage < 9000)) return 0;

        damage -= player.getPlayerState().getPercDamageReduction();

        if (damage > 0 && player.getPlayerState().getPercDamageReduction() != 0) {
            damage -= (damage * player.getPlayerState().getPercDamageReduction() / 100);
        }

        return Math.max(damage, 0);
    }

    /**
     * Applies the cost of over-exerting the player (e.g. casting a spell without enough mana) — the
     * port of C's {@code player_over_exert} ({@code player-util.c}). The {@code flag} selects which
     * penalties (mana loss, stun, HP damage, fainting) may apply, each with the given probability.
     *
     * <p><b>Stub:</b> not yet implemented.
     *
     * @param flag   the set of over-exertion penalties permitted
     * @param chance the percentage chance each penalty triggers
     * @param amount the magnitude of the penalty
     * @author Rowan Crowther
     */
    public static void overExert(Flag<PlayerOverExertion> flag, int chance, int amount) {
        // Stub class TODO: implement
    }

    /**
     * Interrupts the player's current rest/run/repeat and refreshes state — the port of C's
     * {@code disturb} ({@code player-util.c}), called when something demands the player's attention.
     *
     * <p><b>Stub:</b> not yet implemented.
     *
     * @author Rowan Crowther
     */
    public static void disturb() {
        // Stub class TODO: implement
    }

    /**
     * Regenerates the player's hit points for the turn — the port of C's HP regeneration in
     * {@code player-util.c}.
     *
     * <p><b>Stub:</b> not yet implemented.
     *
     * @author Rowan Crowther
     */
    public static void regenHP() {
        // Stub class TODO: implement
    }

    /**
     * Regenerates the player's spell points (mana) for the turn — the port of C's mana regeneration
     * in {@code player-util.c}.
     *
     * <p><b>Stub:</b> not yet implemented.
     *
     * @author Rowan Crowther
     */
    public static void regenMana() {
        // Stub class TODO: implement
    }

    /**
     * Recomputes the player's light radius from equipped light sources and effects — the port of C's
     * light-update handling.
     *
     * <p><b>Stub:</b> not yet implemented.
     *
     * @author Rowan Crowther
     */
    public static void updateLight() {
        // Stub class TODO: Implement this
    }

    /**
     * Moves the player to a new dungeon depth — the port of C's {@code dungeon_change_level}
     * ({@code player-util.c}). Records the new depth, restocks the stores when returning to town
     * after the first day, flags a fresh level for generation, and requests an autosave.
     *
     * @param dungeonLevel the depth to descend/ascend to (0 = town)
     * @author Rowan Crowther
     */
    public static void dungeonChangeLevel(int dungeonLevel) {
        // Record the new depth
        player.setDepth(dungeonLevel);

        // Are we returning to town
        if (dungeonLevel == 0 && GameState.getDaycount() != 0)
            Store.storeUpdate();

        // Make new level
        player.getPlayerUpkeep().setGenerateLevel(true);

        // Save the game when we reach the new level
        player.getPlayerUpkeep().setAutosave(true);
    }

    /**
     * Works out the depth the player would actually arrive at when descending, honouring the
     * force-descend stair-skip, the dungeon's depth bounds, and any intervening quest level — the
     * port of C's {@code dungeon_get_next_level} ({@code player-util.c}). If a quest sits between the
     * current and target depths, the player is stopped at the quest level.
     *
     * @param dungeonLevel the current depth
     * @param added        the number of levels to move (may be scaled by the stair-skip)
     * @return the resulting depth, clamped to the dungeon's limits and to any intervening quest
     * @author Rowan Crowther
     */
    public static int dungeonGetNextLevel(int dungeonLevel, int added) {
        int targetLevel;
        int index;

        // Get Target Level
        targetLevel = dungeonLevel + added * GameConstants.getWorldStairSkip();

        // Don't allow levels below dungeon max
        targetLevel = Math.min(targetLevel, GameConstants.getWorldMaxDepth() - 1);

        // Don't allow levels above the town
        targetLevel = Math.max(targetLevel, 0);

        // Check intermediate levels for quests
        for (index = dungeonLevel; index < targetLevel; index++) {
            if (player.isQuest(index))
                return index;
        }

        return targetLevel;
    }

    public static void search() {
        // Stub function TODO: Implement
    }
}
