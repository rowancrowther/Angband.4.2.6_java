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
 *    Java code copyright (c) Rowan Crowther 2026
 */

package uk.co.jackoftrades.middle.cave;

/**
 * A connection point between dungeon pieces during level generation — the grid
 * to join, the terrain feature to place there, and extra info flags. This is the
 * Java port of the C original's {@code connector} struct ({@code src/generate.h})
 * used when stitching rooms/chunks together.
 *
 * @author ClaudeCode
 */
public class Connector {
    /**
     * The grid where this connection is made.
     *
     * @author ClaudeCode
     */
    private Loc grid;
    /**
     * The terrain feature to place at the connection grid.
     *
     * @author ClaudeCode
     */
    private int feat;
    /**
     * Extra connection info flags (type still to be determined in the port).
     *
     * @author ClaudeCode
     */
    private Object info;   // TODO: Change this to a Flag<bitfield> when we find out what bitflags are being used here
}
