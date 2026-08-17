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

package uk.co.jackoftrades.middle.cave;

public class ChunkUtils {
    /**
     * Test whether an unobstructed line of sight runs between two grids. The port of
     * C's {@code los}. Symmetric in its two endpoints and used, for example, to break
     * a player's command over a monster once the monster passes out of sight.
     *
     * <p><b>Stub:</b> not yet implemented, awaiting the projection/line-of-sight
     * runtime; currently always reports {@code false}.</p>
     *
     * @param cave  the level the grids belong to
     * @param grid1 one endpoint
     * @param grid2 the other endpoint
     * @return {@code true} if the two grids can see each other
     */
    public static boolean los(Chunk cave, Loc grid1, Loc grid2) {
        // Stub function TODO: Implement
        return false;
    }
}
