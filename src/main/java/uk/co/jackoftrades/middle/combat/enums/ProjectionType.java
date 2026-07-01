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

package uk.co.jackoftrades.middle.combat.enums;

/**
 * The broad categories a {@link ProjectionEnum} projection falls into, mirroring
 * the C original's projection-type grouping. This determines how a projection's
 * effect is resolved.
 *
 * @author ClaudeCode
 */
public enum ProjectionType {
    /**
     * Used to signify a blank/null enum value
     */
    PT_NONE,
    /**
     * Acts as a damage element against creatures. @author ClaudeCode
     */
    PT_ELEMENT,
    /**
     * Affects the environment/terrain rather than a target. @author ClaudeCode
     */
    PT_ENVIRONS,
    /**
     * Specifically targets/affects monsters. @author ClaudeCode
     */
    PT_MONSTER
}
