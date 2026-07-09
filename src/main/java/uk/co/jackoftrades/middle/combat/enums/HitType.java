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
 * The escalating quality tiers of a critical hit, used to choose the
 * "good hit"/"great hit"/… message and damage multiplier. Ordered from least to
 * most powerful, mirroring the critical message tiers of the C original.
 *
 * @author Rowan Crowther
 */
public enum HitType {
    /**
     * A good critical hit. @author Rowan Crowther
     */
    HIT_GOOD,
    /**
     * A great critical hit. @author Rowan Crowther
     */
    HIT_GREAT,
    /** A superb critical hit. @author Rowan Crowther */
    HIT_SUPERB,
    /** A very high "great" critical hit. @author Rowan Crowther */
    HIT_HI_GREAT,
    /** The highest "superb" critical hit. @author Rowan Crowther */
    HIT_HI_SUPERB
}
