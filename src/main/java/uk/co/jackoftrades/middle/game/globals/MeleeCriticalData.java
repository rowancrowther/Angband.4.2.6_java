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
 * Constants for normal (non-O) melee critical calculations
 *
 * <p>Generally these calculations compute the chance for a critical based on a
 * linear combination of the to-hit bonus, the players to-hit skill, the
 * players level and the weapon/missile's weight. Level of melee criticals
 * is dealt with in MeleeCriticalLevelData.
 *
 * @param debuffToh           Amount added to to-hit if the target is debuffed
 * @param chanceWeightScale   Scale factor for the weapon/missile's weight
 * @param chanceTohScale      Scale factor for overall to-hit when calculating
 *                            the chance for a melee crtical
 * @param chanceLevelScale    Scale factor for player level
 * @param chanceTohSkillScale Scale factor for to-hit skill
 * @param chanceOffset        Value added to the chance for a melee critical
 * @param chanceRange         Maximum range for melee crticial chance; if the chance
 *                            is N and the maximum range is M, then the probability
 *                            that a critical hit will happen is min(max(0, N), M) / M;
 *                            to avoid extra inaccuracies for the damage shown within
 *                            the object description, this should be a multiple of 100
 * @param powerWeightScale    Scale factor for the weapon's weight
 * @param powerRandom         Maximum of random part of power for a melee critical
 * @author Rowan Crowther
 */
public record MeleeCriticalData(int debuffToh, int chanceWeightScale, int chanceTohScale,
                                int chanceLevelScale, int chanceTohSkillScale, int chanceOffset,
                                int chanceRange, int powerWeightScale, int powerRandom) {
}
