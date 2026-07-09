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

package uk.co.jackoftrades.middle.cave.enums;

/**
 * The lighting states a dungeon grid can be drawn in, mirroring the
 * {@code LIGHTING_*} constants of the C original. These select which colour
 * variant a feature uses when displayed (see {@code ColourTranslation}).
 *
 * @author Rowan Crowther
 */
public enum GridLightLevel {
    /**
     * In the player's line of sight (ambient daylight/level light). @author Rowan Crowther
     */
    LIGHTING_LOS,
    /**
     * Lit by the player's torch. @author Rowan Crowther
     */
    LIGHTING_TORCH,
    /** Permanently lit (e.g. a lit room). @author Rowan Crowther */
    LIGHTING_LIT,
    /** Dark / unlit. @author Rowan Crowther */
    LIGHTING_DARK,
    /** Count sentinel (number of lighting states). @author Rowan Crowther */
    LIGHTING_MAX
}
