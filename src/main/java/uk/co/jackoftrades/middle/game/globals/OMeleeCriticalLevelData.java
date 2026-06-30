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

import uk.co.jackoftrades.middle.enums.MessageType;

/**
 * Level for non-standard (O) melee criticals. These are normally visited in the order they are
 * written in ("It is convenient to put the least likely first"). If no critical level is
 * defined, then no extra damage will occur for a critical
 *
 * @param chance      1/chance that this level of critical occurs
 * @param dice        The number of dice to add for the critical
 * @param messageType The resolved {@link MessageType} emitted
 *                    when a critical at this level occurs
 *
 * @author Rowan Crowther
 */
public record OMeleeCriticalLevelData(int chance, int dice, MessageType messageType) {
}
