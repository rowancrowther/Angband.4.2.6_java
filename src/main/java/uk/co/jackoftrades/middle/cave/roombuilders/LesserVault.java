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

package uk.co.jackoftrades.middle.cave.roombuilders;

import uk.co.jackoftrades.middle.cave.Chunk;
import uk.co.jackoftrades.middle.cave.Loc;

/**
 * Builds a lesser vault — the port of C's {@code build_lesser_vault}.
 *
 * <p>The smallest of the three vault sizes, at most 22x22. The layout comes from {@code vault.txt},
 * which also fixes where monsters and objects are placed within it. Ignores the rating.
 *
 * <p><b>Stub:</b> not yet implemented; returns {@code false}.
 *
 * @author Rowan Crowther
 */
public class LesserVault implements RoomBuilder {
    /**
     * {@inheritDoc}
     *
     * <p><b>Stub:</b> not yet implemented; always returns {@code false}.
     */
    @Override
    public boolean build(Chunk cave, Loc centre, int rating) {
        return false;
    }
}
