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

package uk.co.jackoftradesltd.middle.cave;

import uk.co.jackoftradesltd.middle.player.Player;

/**
 * Dungeon-level generation — the port of C's {@code generate.c}.
 *
 * <p>Builds the level the player is about to enter: picks a generation profile, lays out the cave,
 * and populates it. The game loop calls in here whenever a new level is requested. Static methods,
 * matching C's globals-driven generator.
 *
 * <p><b>Status:</b> a stub landed to unblock the game loop; the generator itself is a later, large
 * piece of work ({@code gen-cave.c} / {@code gen-room.c}).
 *
 * @author Rowan Crowther
 */
public class Generate {
    /**
     * The player the level is being generated for, held while a generation pass runs.
     */
    private static Player player;

    /**
     * Generate and install the next dungeon level for the player — the port of C's
     * {@code prepare_next_level}. Called from the game loop when
     * {@code upkeep.generateLevel} is set, replacing the current level with a freshly built one.
     *
     * <p><b>Stub:</b> not yet implemented — records the player but builds no level yet.
     *
     * @param player the player the new level is being prepared for
     */
    public static void prepareNextLevel(Player player) {
        Generate.player = player;

        // Stub class TODO: implement
    }
}
