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

package uk.co.jackoftrades.channel.messages.data;

/**
 * {@link GameEventData} payload carrying a pair of dimensions — the port of C's
 * {@code game_event_data.size} ({@code src/game-event.h}), signalled by {@code event_signal_size}.
 * Used where an event reports how big something turned out to be, such as a room's extent during
 * level generation.
 *
 * <p><b>Height first.</b> C declares the pair as {@code struct { int h, w; }} and passes it as
 * {@code event_signal_size(type, h, w)}, and this record follows. Two bare {@code int}s make the
 * opposite order compile perfectly and be wrong, so the order is worth checking against the
 * original rather than against intuition — "width and height" is the more natural English phrase
 * and the wrong one here.
 *
 * @param height the vertical extent, in map squares — C's {@code h}
 * @param width  the horizontal extent, in map squares — C's {@code w}
 * @author Rowan Crowther
 */
public record EventDataSize(int height, int width) implements GameEventData {
}

