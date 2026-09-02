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
 * {@link GameEventData} payload carrying a single piece of text — the port of the {@code string}
 * arm of C's {@code game_event_data} union ({@code src/game-event.h}), signalled by
 * {@code event_signal_string}. Used where an event names something: a generation profile, a room
 * type, or the progress notes shown while the game loads.
 *
 * <p>Distinct from {@link EventDataMessage}, which carries text meant for the player's message
 * log and travels with a category that colours it. This one is a label, and the display decides
 * what to do with it from the event type alone.
 *
 * @param string the text this event is reporting
 * @author Rowan Crowther
 */
public record EventDataString(String string) implements GameEventData {
}
