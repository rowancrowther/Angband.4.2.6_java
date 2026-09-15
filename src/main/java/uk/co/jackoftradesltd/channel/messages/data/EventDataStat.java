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
 * {@link GameEventData} payload carrying a pair of numbers — the value a redraw is really about,
 * and whatever second number the event pairs it with for display. Reused across several
 * {@code PR_*} redraws of the same shape: {@code EVENT_HP} ({@code chp}, {@code mhp}),
 * {@code EVENT_MANA} ({@code csp}, {@code msp}) and {@code EVENT_PLAYERLEVEL} ({@code lev},
 * {@code max_lev}) all fit it. C's {@code prt_hp}, {@code prt_sp} and {@code prt_level}
 * ({@code src/ui-display.c:207,314,332}) each read their pair straight off the {@code player}
 * global rather than receiving it as an argument — there is no union arm or
 * {@code event_signal_*} function behind this shape in C, only the port's need to carry across a
 * boundary what C reaches for directly.
 *
 * <p>{@code other} rather than {@code max}: the second number is whatever the event pairs
 * {@code current} with, and this record is not in a position to say more than that.
 * {@code EVENT_PLAYERLEVEL} pairs {@code lev} with {@code max_lev} to decide whether the level
 * label is drawn drained or full — the same shape as an HP/mana current/maximum pair, but not a
 * maximum in its own right. Naming the field for one reading of it would mislead the first event
 * that pairs the current value with something else.
 *
 * <p>What the payload does not send: a colour, or a drained/full verdict. {@code prt_hp} picks
 * its colour from the ratio and {@code prt_level} from the comparison, and both of those stay the
 * front end's business — the core sends the two numbers and nothing else.
 *
 * <p>Class EventDataStat coded before 260915, commented in full on 260915.
 *
 * @param current the value the redraw is about — C's {@code chp}, {@code csp} or {@code lev}
 * @param other   whatever the event pairs {@code current} with — C's {@code mhp}, {@code msp} or
 *                {@code max_lev}
 * @author Rowan Crowther
 */
public record EventDataStat(int current, int other) implements GameEventData {
}
