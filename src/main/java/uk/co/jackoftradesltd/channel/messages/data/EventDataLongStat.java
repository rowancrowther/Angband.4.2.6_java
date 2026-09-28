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
 * {@link GameEventData} payload carrying a pair of numbers too wide for {@link EventDataStat}'s
 * {@code int} pair — the {@code long}-valued sibling of that record, and today the (current,
 * maximum) experience pair {@code EVENT_EXPERIENCE} sends: C's {@code player->exp} and
 * {@code player->max_exp}, both held as {@code long} here because {@code Player.exp}/
 * {@code maxExp} are, per those fields' own Javadoc.
 *
 * <p>No C counterpart, for the same reason {@link EventDataStat} has none: C's {@code prt_exp}
 * ({@code [C] ui-display.c}) reads {@code player->exp} and {@code player->max_exp} straight off
 * the shared global at draw time to decide whether the row is drawn drained or full, rather than
 * receiving them as an argument.
 *
 * <p>Class EventDataLongStat coded on 260927, commented in full on 260928.
 *
 * @param value the value the redraw is about — C's {@code player->exp}
 * @param other whatever the event pairs {@code value} with — C's {@code player->max_exp}
 * @author Rowan Crowther
 */
public record EventDataLongStat(long value, long other) implements GameEventData {
}
