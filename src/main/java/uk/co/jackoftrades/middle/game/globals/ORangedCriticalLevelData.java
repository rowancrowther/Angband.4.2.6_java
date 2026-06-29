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
 * Each of the levels of a ranged critical. They are considered in the order they appear, so the Java
 * port pust the least likely first canonically. If no critical levels are defined, no extra damage
 * due to critical hits occurs.
 *
 * @param chance      1/chance of this level of critical occurring. Must be positive.
 * @param dice        The number of dice to add to the critical damage roll
 * @param messageType The name of the message from list-messages.h to display on this level of critical
 *                    hit
 * @author Rowan Crowther
 */
public record ORangedCriticalLevelData(int chance, int dice, String messageType) {
}
