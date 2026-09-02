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
 * Builds a cavern level — the port of C's {@code cavern_gen}.
 *
 * <p>An open, natural-looking level: the floor is eroded out of solid rock rather than laid out as
 * rooms and corridors, so there are no room boundaries to speak of.
 *
 * <p><b>Stub:</b> not yet implemented; returns {@code null}.
 *
 * @author Rowan Crowther
 */
public class CavernBuilder implements CaveBuilder {
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
