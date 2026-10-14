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

package uk.co.jackoftradesltd.middle.cave;

import org.jetbrains.annotations.NotNull;

/**
 * A World record used to store the details of each level of the dungeon, including the Town
 * (C: {@code struct level} in {@code game-world.h}, filled by {@code parse_world_level} in {@code init.c}).
 *
 * <p><b>"None" is kept as text.</b> C maps the {@code "None"} that {@code world.txt} writes for a missing
 * neighbour to a {@code NULL} {@code up} or {@code down}. The port does not: {@code WorldAssembler} copies
 * the text through, so the town's {@code prevLevel} and the deepest level's {@code nextLevel} are the
 * string {@code "None"}, never {@code null}, which is why all four components are {@code @NotNull}. Code
 * that asks whether a neighbour exists must compare with {@code "None"}, as {@code WorldDataLoader} does
 * when it checks that every named neighbour is a loaded level.
 *
 * <p>Record World coded before 261014, commented in full on 261014.
 *
 * @param levelNumber the number of the level in 0-base from the town downwards
 * @param levelName   the name of the level
 * @param prevLevel   the name of the previous level in the List of worlds, or {@code "None"} if one doesn't
 *                    exist, i.e. the current level is the town level
 * @param nextLevel   the name of the next level in the list of worlds, or {@code "None"} if one doesn't
 *                    exist, i.e. the current level is the deepest one in the dungeon
 */
public record World(int levelNumber, @NotNull String levelName, @NotNull String prevLevel,
                    @NotNull String nextLevel) {
}
