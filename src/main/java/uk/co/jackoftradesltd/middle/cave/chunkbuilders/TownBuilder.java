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
 * Builds the town — the port of C's {@code town_gen}.
 *
 * <p>Starts from a level of plain floor and places the store entrances in it. This is the physical
 * layout only: who owns the stores and what they have in stock is settled elsewhere. The requested
 * minimum height and width are ignored, as the town is always the same size.
 *
 * <p><b>Stub:</b> not yet implemented; returns {@code null}.
 *
 * @author Rowan Crowther
 */
public class TownBuilder implements CaveBuilder {
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
