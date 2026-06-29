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
 * Data structure for storing monster gameplay data
 *
 * @param breakGlyph Rune of protection resistance to monster breaking
 * @param multRate   Multiplication constant - high value slows
 *                   multiplication
 * @param lifeDrain  Percent of player exp drained per hit
 * @param fleeRange  Number of grids out of sight monsters will flee
 * @param turnRange  Terrified monsters who are slower than the player
 *                   will turn to fight if they are closer than this
 *                   distance.
 * @author Rowan Crowther
 */
public record MonPlayData(int breakGlyph, int multRate, int lifeDrain, int fleeRange,
                          int turnRange) {
}