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
 * Builds a standard dungeon level — the port of C's {@code modified_gen}.
 *
 * <p>The newer of the two general-purpose level builders, and the one several of the others are
 * layered on top of. What it changes relative to {@link ClassicBuilder}: levels vary in size;
 * block size no longer constrains the layout; rooms find their own space rather than being handed
 * it; a running count of each terrain lets generation stop once there is enough floor; and it adds
 * the huge, chambers and interesting room types along with many new vaults, which between them can
 * place named monsters and objects and restrict monsters across an area.
 *
 * <p><b>Stub:</b> not yet implemented; returns {@code null}.
 *
 * @author Rowan Crowther
 */
public class ModifiedBuilder implements CaveBuilder {
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
