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

import uk.co.jackoftrades.middle.cave.Loc;

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
    /**
     * Check whether an in-progress rest should end for a special reason — the port of C's
     * {@code player_resting_complete_special}. Called at the top of the player-processing pass so a
     * rest can be interrupted the moment its stopping condition (e.g. HP/SP restored) is met.
     *
     * <p><b>Stub:</b> not yet implemented — takes no action until the resting subsystem is ported.
     *
     * @param player the player whose rest is being checked
     */
    public static void restingCompleteSpecial(Player player) {
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
     * @param player the player who may be standing on damaging terrain
     * @param grid   the grid to test — normally the player's own location
     */
    public static void takeTerrainDamage(Player player, Loc grid) {
        // Stub class TODO: implement
    }
}
