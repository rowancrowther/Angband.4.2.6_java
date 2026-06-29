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
 * Record for storing data to do with carrying capacity
 *
 * @param packSize         Maximum number of pack slots for carrying items
 * @param quiverSize       Maximum number of quiver slots for carry ing missiles
 * @param quiverSlotSize   Maximum number of items per quiver slot
 * @param thrownQuiverMult Multiplier for non-ammo throw items in quiver slots
 * @param floorSize        Maximum number of objects allowed in a single dungeon grid
 * @author Rowan Crowther
 */
public record CarryCapData(int packSize, int quiverSize, int quiverSlotSize,
                           int thrownQuiverMult, int floorSize) {
}
