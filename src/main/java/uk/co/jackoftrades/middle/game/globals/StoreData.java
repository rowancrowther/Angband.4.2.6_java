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
 * Data record for storing 'store' generation data
 *
 * @param invenMax   Maximum number of discrete objects in store inventory
 * @param turns      Number of turns between store inventory turnover
 * @param shuffle    1/chance per day of owner changing
 * @param magicLevel Dungeon level to apply magic to objects for normal stores
 * @author Rowan Crowther
 */
public record StoreData(int invenMax, int turns, int shuffle, int magicLevel) {
}
