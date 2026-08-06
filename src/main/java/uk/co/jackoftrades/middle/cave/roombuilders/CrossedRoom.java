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
 * Builds a cross-shaped room — the port of C's {@code build_crossed}.
 *
 * <p>Two rectangles crossing at right angles, one running north/south and the other east/west.
 * Depending on how they overlap the result may have a central pillar, or an inner chamber where
 * the two meet. The centre is currently always 3x3, though C notes the code should cope with
 * larger and unsymmetrical sizes. Ignores the rating.
 *
 * <p><b>Stub:</b> not yet implemented; returns {@code false}.
 *
 * @author Rowan Crowther
 */
public class CrossedRoom implements RoomBuilder {
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
