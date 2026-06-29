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
 * Constants for normal (non-O) ranged critical calculations
 *
 * <p>Generally these calculations compute the chance for a critical based on a
 * linear combination of the to-hit bonus, the players to-hit skill, the
 * players level and the weapon/missile's weight. Level of melee criticals
 * is dealt with in RangedCriticalLevelData.
 *
 * @param debuffToh                   Amount added to the to-hit value for
 *                                    calculating the ranged critical chance when
 *                                    the target is debuffed
 * @param chanceWeightScale           Scale factor for the missile's weight in
 *                                    ranged critical chance
 * @param chanceTohScale              Scale factor for the overall to-hit value when
 *                                    calculating the chance for a ranged critical
 * @param chanceLevelScale            Scale factor for the player level when
 *                                    calculating the chance for a ranged critical
 * @param chanceLaunchedTohSkillScale Scale factor for the launched to-hit skill when
 *                                    calculating the chance for a ranged critical
 *                                    when using a launcher (i.e. bow)
 * @param chanceThrownTohSkillScale   Scale factor for the thrown to-hit skill when
 *                                    calculating the chance for a ranged critical
 *                                    when throwing an object
 * @param chanceOffset                Value added to the offset for a ranged critical
 * @param chanceRange                 Maximum range for a ranged critical chance. If
 *                                    the chance is N, and the maximum range is M, then
 *                                    the probability of a critical occurring is
 *                                    min(max(0, N), M) / M; to avoid extra inaccuracies
 *                                    within object descriptions this should be a
 *                                    multiple of 100
 * @param powerWeightScale            Scale factor for missile weight
 * @param powerRandom                 Maximum random part of the power in the ranged
 *                                    critical calculation
 * @author Rowan Crowther
 */
public record RangedCriticalData(int debuffToh, int chanceWeightScale, int chanceTohScale,
                                 int chanceLevelScale, int chanceLaunchedTohSkillScale,
                                 int chanceThrownTohSkillScale, int chanceOffset,
                                 int chanceRange, int powerWeightScale, int powerRandom) {
}
