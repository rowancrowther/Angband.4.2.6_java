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

package uk.co.jackoftradesltd.middle.cave.roombuilders;

import uk.co.jackoftradesltd.middle.cave.Chunk;
import uk.co.jackoftradesltd.middle.cave.Loc;

/**
 * Builds a monster pit — the port of C's {@code build_pit}.
 *
 * <p>Laid out much like a {@link NestRoom}, but the occupants are arranged by strength rather than
 * scattered: 16 suitable monsters are requested, sorted by level, and the even entries of that list
 * are placed in concentric bands, weakest at the moat and strongest at the centre. Which monsters
 * are eligible comes from {@code pit.txt}.
 *
 * <p>Monster selection can fail, in which case the pit is empty and does not raise the level
 * rating. Pits never contain uniques. Ignores the rating parameter.
 *
 * <p><b>Stub:</b> not yet implemented; returns {@code false}.
 *
 * @author Rowan Crowther
 */
public class PitRoom implements RoomBuilder {
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
