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
 * Record to hold game world constants
 *
 * @param maxDepth     Maximum dungeon depth; must be at least 100
 *                     Setting it below 128 may prevent some items generating
 * @param dayLength    Number of turns from dawn to dawn
 * @param dungeonHgt   Maximum number of vertical grids per dungeon level
 * @param dungeonWid   Maximum number of horizontal grids per dungeon level
 * @param townHgt      Maximum number of vertical grids in each town level
 * @param townWid      Maximum number of horizontal grids in each town level
 * @param feelingTotal Total number of feeling squares per level
 * @param feelingNeed  Squares needed to see to get first feeling
 * @param stairSkip    Number of levels for each staircase
 * @param moveEnergy   Energy needed by monsters or player to move
 * @author Rowan Crowther
 */
public record WorldData(int maxDepth, int dayLength, int dungeonHgt, int dungeonWid,
                        int townHgt, int townWid, int feelingTotal, int feelingNeed,
                        int stairSkip, int moveEnergy) {
}
