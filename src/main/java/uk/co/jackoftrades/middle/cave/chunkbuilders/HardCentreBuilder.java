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

package uk.co.jackoftrades.middle.cave.chunkbuilders;

import uk.co.jackoftrades.middle.cave.Chunk;
import uk.co.jackoftrades.middle.player.Player;

/**
 * Builds a hard centre level — the port of C's {@code hard_centre_gen}.
 *
 * <p>A greater vault dropped in the middle of the level, with caverns filling the space around it.
 * Ignores the requested minimum height and width.
 *
 * <p><b>Stub:</b> not yet implemented; returns {@code null}.
 *
 * @author Rowan Crowther
 */
public class HardCentreBuilder implements CaveBuilder {
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
