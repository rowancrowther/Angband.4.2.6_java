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

package uk.co.jackoftrades.middle.game.globals;

/**
 * Holder for data dealing with dungeon generation
 *
 * @param centMax Number of possible room centres (and hence rooms) on a normal level
 * @param doorMax Number of possible door locations
 * @param wallMax Number of possible places to pierce room walls with tunnels
 * @param tunnMax Number of tunnel grids
 * @param amtRoom Average number of objects to place in a room (TODO: Work out whether this is per room or per level)
 * @param amtItem Average number of objects to place in rooms/corridors
 * @param amtGold Average amount of treasure to place in rooms/corridors
 * @param pitMax  Maximum number of pits or nests per level
 * @author Rowan Crowther
 */
public record DunGenData(int centMax, int doorMax, int wallMax, int tunnMax,
                         int amtRoom, int amtItem, int amtGold, int pitMax) {
}
