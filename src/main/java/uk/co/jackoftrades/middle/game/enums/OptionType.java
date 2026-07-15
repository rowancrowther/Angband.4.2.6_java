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

package uk.co.jackoftrades.middle.game.enums;

/**
 * The category an {@link Options} constant belongs to — the Java port of the C original's
 * {@code option_type} ({@code option.h}). The category decides where and when an option applies:
 * {@link #OP_INTERFACE} options tune display and controls, {@link #OP_BIRTH} options are fixed at
 * character creation (these are the ones {@code class.txt}'s equipment exclusions reference),
 * {@link #OP_CHEAT}/{@link #OP_SCORE} are the paired debugging/scoring flags, and
 * {@link #OP_SPECIAL} covers internal options with no menu presence. {@link #OP_NONE} and
 * {@link #OP_MAX} are the C sentinel bookends.
 *
 * @author Rowan Crowther
 */
public enum OptionType {
    /**
     * No/unset category (C sentinel).
     */
    OP_NONE,
    /**
     * Display- and control-tuning options shown in the interface menus.
     */
    OP_INTERFACE,
    /**
     * Character-creation options, fixed at birth; the ones class equipment exclusions consult.
     */
    OP_BIRTH,
    /**
     * Debugging "peek" options that reveal hidden game state.
     */
    OP_CHEAT,
    /**
     * The scoring counterparts of the cheat options (each cheat pairs with a score).
     */
    OP_SCORE,
    /**
     * Internal options with no menu presence.
     */
    OP_SPECIAL,
    /**
     * Count/upper-bound sentinel (C: {@code OPT_MAX}).
     */
    OP_MAX
}
