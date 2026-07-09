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

package uk.co.jackoftrades.middle.enums;

/**
 * How a "nourish" (food) effect changes the player's food level — increase by,
 * decrease by, set to, or increase up to a value. Mirrors the nourish-mode
 * parameter of the C original's effect code.
 *
 * @author Rowan Crowther
 */
public enum EffectNourish {
    /**
     * No change. @author Rowan Crowther
     */
    EN_NONE,
    /**
     * Increase food by a given amount. @author Rowan Crowther
     */
    EN_INC_BY,
    /** Decrease food by a given amount. @author Rowan Crowther */
    EN_DEC_BY,
    /** Set food to a given value. @author Rowan Crowther */
    EN_SET_TO,
    /** Increase food up to (but not beyond) a given value. @author Rowan Crowther */
    EN_INC_TO
}
