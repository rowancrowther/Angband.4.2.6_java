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

package uk.co.jackoftradesltd.channel.messages.data;

/**
 * {@link GameEventData} payload carrying a single number too wide for {@link EventDataStat}'s
 * {@code int} pair — today, the figure {@code EVENT_EXPERIENCE} sends alongside its
 * {@link EventDataLongStat} pair: the experience needed to reach the next level, or the current
 * total at level fifty. See {@code PlayerCalcs.redrawStuff}'s {@code PR_EXP} arm, the port of C's
 * local {@code xp} in {@code prt_exp} ({@code [C] ui-display.c}), which holds the same value for
 * the same reason - {@code long} because {@code Player.exp}/{@code maxExp} are held that wide, per
 * those fields' own Javadoc.
 *
 * <p>No C counterpart, for the same reason as {@link EventDataStat}: C's {@code prt_exp} reads
 * {@code player->exp}, {@code player->lev} and {@code player_exp[]} straight off the shared
 * globals at draw time, with nothing to carry across a boundary C does not have.
 *
 * <p>Class EventDataLong coded on 260927, commented in full on 260928.
 *
 * @param value the number this event is reporting
 * @author Rowan Crowther
 */
public record EventDataLong(long value) implements GameEventData {
}
