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
 * {@link GameEventData} payload carrying a single {@code int}-wide number — the
 * {@code int}-valued sibling of {@link EventDataLong}, for a redraw whose figure fits an
 * {@code int} and so does not need the wider type. Used today only for {@code EVENT_AC}, sent by
 * {@code PlayerCalcs.redrawStuff}'s {@code PR_ARMOR} arm carrying the player's armour class,
 * {@code known_state.ac + known_state.to_a}.
 *
 * <p>No C counterpart, for the same reason as {@link EventDataStat}: C's {@code prt_ac}
 * ({@code [C] ui-display.c}, function {@code prt_ac}) reads {@code player->known_state.ac} and
 * {@code player->known_state.to_a} straight off the shared global at draw time, with nothing to
 * carry across a boundary C does not have.
 *
 * <p>Class EventDataInt coded on 260927, commented in full on 260928.
 *
 * @param data the number this event is reporting — C's {@code known_state.ac + known_state.to_a}
 * @author Rowan Crowther
 */
public record EventDataInt(int data) implements GameEventData {
}
