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
 * Data record for monster generation
 *
 * @param chance    The 1/per turn chance of a new monster spawning
 * @param levelMin  The minimum number of monsters generated on a new level
 * @param townDay   The number of townsfolk generated during the day
 * @param townNight The number of townsfolk generated during the night
 * @param reproMax  Maximum number of breeding monsters allowed on a level
 * @param oodChance The chance of a generated monster's level being inflated
 *                  as 1/oodChance
 * @param oodAmount The maximum out of depth amount for monster generation
 * @param groupMax  The maximum number of monsters in a group
 * @param groupDist The maximum distance for a group of monsters from a
 *                  related group
 * @author Rowan Crowther
 */
public record MonGenData(int chance, int levelMin, int townDay, int townNight,
                         int reproMax, int oodChance, int oodAmount, int groupMax,
                         int groupDist) {
}
