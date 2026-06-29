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
 * Melee critical level, used to determine the level of the critical, and extra damage
 * it does. Each record has a power cutoff, and whether that level is triggered or not
 * depends on what the power of the melee attack is
 *
 * @param powerCutoff      Cutoff for power for this level
 * @param damageMultiplier Damage multiplier for this level
 * @param damageAddition   Amount added to damage for this level
 * @param messageType      Name of the messages enum to use when a critical from this level
 *                         occurs
 * @author Rowan Crowther
 */
public record MeleeCriticalLevelData(Integer powerCutoff, Integer damageMultiplier, Integer damageAddition,
                                     String messageType) {
}
