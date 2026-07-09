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

package uk.co.jackoftrades.middle.monsters.enums;

/**
 * How a monster timed effect stacks when re-applied — refuse to stack, or
 * increase the duration.
 *
 * @author Rowan Crowther
 */
public enum MonTimedStack {
    /**
     * MonTimedStack not set to any value. @author Rowan Crowther
     */
    MTS_NONE,
    /**
     * Re-application does not stack. @author Rowan Crowther
     */
    MTS_NO,
    /**
     * Re-application increases the remaining duration. @author Rowan Crowther
     */
    MTS_INCR,
    /** Count sentinel. @author Rowan Crowther */
    MTS_MAX
}
