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
 * A grid of integer "distances"/intensities over the whole level, used for
 * flow-based monster pathfinding (how far each grid is from the player along
 * passable terrain). This is the Java port of the C original's {@code heatmap}
 * struct ({@code src/cave.h}); higher-level flow code fills and reads {@link
 * #grids}.
 *
 * @author ClaudeCode
 */
public class Heatmap {
    /**
     * Per-grid scalar values, indexed {@code [y][x]}.
     *
     * @author ClaudeCode
     */
    private int[][] grids;
}
