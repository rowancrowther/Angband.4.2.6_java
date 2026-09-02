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

package uk.co.jackoftradesltd.middle.cave.chunkbuilders;

import uk.co.jackoftradesltd.middle.cave.Chunk;
import uk.co.jackoftradesltd.middle.player.Player;

/**
 * A level builder — the port of C's {@code cave_builder} function pointer.
 *
 * <p>Each implementation lays out one whole dungeon level in its own style (town, cavern,
 * labyrinth, and so on) and hands back the finished {@link Chunk}. {@link BuilderType} pairs each
 * one with the name {@code dungeon_profile.txt} refers to it by, so the data file picks the
 * algorithm.
 *
 * <p>C passes a fourth argument, {@code const char **p_error}, which the builder points at a
 * constant string explaining why it gave up. That out-parameter has no place in Java, so the
 * failure reason is not currently reported anywhere — see the note on {@link #build}.
 *
 * @author Rowan Crowther
 */
public interface CaveBuilder {
    /**
     * Build a complete dungeon level for the player.
     *
     * <p>The minimum sizes are a request, not a guarantee: several C builders
     * ({@code town_gen}, {@code classic_gen}, {@code hard_centre_gen}, {@code gauntlet_gen})
     * document that they ignore them outright and produce a level of their own preferred size.
     *
     * <p>Returning {@code null} means the attempt failed and the caller should try again —
     * C's {@code cave_generate} retries up to 100 times before giving up. The reason for the
     * failure is lost on the way out, where C recovers it from {@code p_error}; something will
     * have to carry it once {@code cave_generate} is ported.
     *
     * @param player    the player the level is being built for
     * @param minHeight the smallest acceptable level height, in grids
     * @param minWidth  the smallest acceptable level width, in grids
     * @return the newly built level, or {@code null} if this attempt failed
     */
    Chunk build(Player player, int minHeight, int minWidth);
}
