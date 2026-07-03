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

package uk.co.jackoftrades.middle.cave;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A World record used to store the details of each level of the dungeon, including the Town
 *
 * @param levelNumber the number of the level in 0-base from the town downwards
 * @param levelName   the name of the level
 * @param prevLevel   the name of the previous level in the List of worlds or null if one doesn't exist,
 *                    i.e. the current level is the town level
 * @param nextLevel   the name of the next level in the list of worlds or null if one doesn't exist, i.e.
 *                    the current level is the deepest one in the dungeon
 */
public record World(int levelNumber, @NotNull String levelName, @Nullable String prevLevel,
                    @Nullable String nextLevel) {
}
