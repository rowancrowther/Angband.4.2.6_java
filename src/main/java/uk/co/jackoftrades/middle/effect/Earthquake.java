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

package uk.co.jackoftrades.middle.effect;

/**
 * Targeting mode for an earthquake effect: centred on the caster, or centred on a
 * chosen target.
 *
 * @author Rowan Crowther
 */
public enum Earthquake {
    /**
     * No special targeting (centred on the caster). @author Rowan Crowther
     */
    QUAKE_NONE,
    /**
     * Centred on a chosen target grid. @author Rowan Crowther
     */
    QUAKE_TARGETED
}
