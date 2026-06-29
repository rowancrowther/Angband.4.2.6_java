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
 * Record to hold player constants
 *
 * @param maxSight  Maximum line of sight for player
 * @param maxRange  Maximum missile and spell range
 * @param startGold Amount of starting gold or value of starting
 *                  equipment
 * @param foodValue Number of turns 1% of player food capacity
 *                  feeds them for
 * @author Rowan Crowther
 */
public record PlayerData(int maxSight, int maxRange, int startGold, int foodValue) {
}
