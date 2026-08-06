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
 * A room builder — the port of C's {@code room_builder} function pointer.
 *
 * <p>Each implementation carves one kind of room into a level that is already under construction:
 * a plain rectangle, a monster pit, a greater vault, and so on. {@link RoomType} pairs each one
 * with the name {@code dungeon_profile.txt} refers to it by, so the data file picks which rooms a
 * level may contain.
 *
 * <p>Room builders run inside a
 * {@link uk.co.jackoftrades.middle.cave.chunkbuilders.CaveBuilder}, not instead of one — the level
 * builder decides how many rooms to attempt and where, then calls in here for each.
 *
 * @author Rowan Crowther
 */
public interface RoomBuilder {
    /**
     * Carve one room of this type into the level.
     *
     * <p>The centre is a hint rather than an instruction: in C, a centre that falls outside the
     * chunk is the signal for the builder to call {@code find_space()} and choose its own position.
     *
     * <p>Most room types ignore the rating; {@code build_template} is the exception, using it to
     * choose which template of that rating to lay down.
     *
     * @param cave   the level under construction
     * @param centre where to centre the room; outside the level means "find your own space"
     * @param rating the room rating to select, where the room type cares about it
     * @return {@code true} if the room was built, {@code false} if it could not be placed
     */
    boolean build(Chunk cave, Loc centre, int rating);
}
