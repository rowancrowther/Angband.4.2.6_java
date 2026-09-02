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
 * {@link GameEventData} payload reporting how one tunnel turned out during level generation — the
 * port of C's {@code game_event_data.tunnel} ({@code src/game-event.h}).
 *
 * <p>Diagnostic rather than visible: the tunneller wanders towards a goal and may give up early,
 * and these counts are what tell you whether it behaved. Nothing in normal play consumes them —
 * they exist so that generation can be watched while it is being tuned.
 *
 * <p>Whether the tunnel <em>succeeded</em> is read from {@code dEnd}: zero means it arrived. Read
 * against {@code dStart} it also says how much of the distance was closed, which is the useful
 * measure when {@code early} is set.
 *
 * @param nStep   how many steps the tunneller took in total
 * @param nPierce how many room walls it broke through
 * @param nDug    how many squares it excavated, not counting those piercings
 * @param dStart  the city-block distance from the starting square to the goal, i.e.
 *                {@code abs(start.col - end.col) + abs(start.row - end.row)}
 * @param dEnd    the same distance measured from where the tunnel actually finished; zero means it
 *                reached its goal
 * @param early   whether the tunneller stopped short by its own random early-termination rule
 *                rather than by arriving
 * @author Rowan Crowther
 */
public record EventDataTunnel(int nStep, int nPierce, int nDug, int dStart, int dEnd,
                              boolean early) implements GameEventData {
}