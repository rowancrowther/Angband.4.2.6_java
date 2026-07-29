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

package uk.co.jackoftrades.middle.game.globals.registry;

/**
 * Hard-coded stat-adjustment lookup tables — the Java port of the {@code adj_*}
 * arrays in the C original's {@code src/player-calcs.c}. Each table maps a stat's
 * <em>compressed index</em> (see {@link uk.co.jackoftrades.middle.player.PlayerState#getStatInd},
 * {@code 0..}{@link #STAT_RANGE}{@code -1}, not the raw stat value) to a small
 * adjustment. These are game constants, not data-file driven.
 *
 * @author Rowan Crowther
 */
public class StatTables {
    /**
     * Number of distinct compressed stat rungs — the port of C's {@code STAT_RANGE}.
     */
    private static final int STAT_RANGE = 38;

    /**
     * CON → base regeneration rate (the port of C's {@code adj_con_fix}). Indexed by
     * the CON stat index; drives how fast bleeding, poison and stun recover in
     * {@code decrease_timeouts}. Consumers add one to the looked-up value.
     */
    public static final int[] adjConFix = {
            0    /* 3 */,
            0    /* 4 */,
            0    /* 5 */,
            0    /* 6 */,
            0    /* 7 */,
            0    /* 8 */,
            0    /* 9 */,
            0    /* 10 */,
            0    /* 11 */,
            0    /* 12 */,
            0    /* 13 */,
            1    /* 14 */,
            1    /* 15 */,
            1    /* 16 */,
            1    /* 17 */,
            2    /* 18/00-18/09 */,
            2    /* 18/10-18/19 */,
            2    /* 18/20-18/29 */,
            2    /* 18/30-18/39 */,
            2    /* 18/40-18/49 */,
            3    /* 18/50-18/59 */,
            3    /* 18/60-18/69 */,
            3    /* 18/70-18/79 */,
            3    /* 18/80-18/89 */,
            3    /* 18/90-18/99 */,
            4    /* 18/100-18/109 */,
            4    /* 18/110-18/119 */,
            5    /* 18/120-18/129 */,
            6    /* 18/130-18/139 */,
            6    /* 18/140-18/149 */,
            7    /* 18/150-18/159 */,
            7    /* 18/160-18/169 */,
            8    /* 18/170-18/179 */,
            8    /* 18/180-18/189 */,
            8    /* 18/190-18/199 */,
            9    /* 18/200-18/209 */,
            9    /* 18/210-18/219 */,
            9    /* 18/220+ */
    };
}