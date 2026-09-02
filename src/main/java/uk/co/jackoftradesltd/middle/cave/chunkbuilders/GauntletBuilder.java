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
 * Builds a gauntlet level — the port of C's {@code gauntlet_gen}.
 *
 * <p>Two separate caverns with an unmappable labyrinth between them. The level is deliberately
 * hard to leave: teleport is disabled, and the side the player starts on has only up staircases,
 * so the labyrinth has to be crossed to go deeper. Ignores the requested minimum height and width.
 *
 * <p><b>Stub:</b> not yet implemented; returns {@code null}.
 *
 * @author Rowan Crowther
 */
public class GauntletBuilder implements CaveBuilder {
    /**
     * {@inheritDoc}
     *
     * <p><b>Stub:</b> not yet implemented; always returns {@code null}.
     */
    @Override
    public Chunk build(Player player, int minHeight, int minWidth) {
        return null;
    }
}
