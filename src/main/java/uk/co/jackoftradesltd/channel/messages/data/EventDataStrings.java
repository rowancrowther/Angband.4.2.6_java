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
 * {@link GameEventData} payload carrying several pieces of text at once — the multi-value sibling
 * of {@link EventDataString}, used where an event needs more than one label in the same signal, such
 * as a race name paired with a class name.
 *
 * <p>Has no C counterpart: C's {@code game_event_data} union ({@code src/game-event.h}) has no
 * "several strings" arm, because C's {@code redraw_events} table ({@code player-calcs.c}) fires
 * {@code EVENT_RACE_CLASS} as a bare {@code event_signal()} with no payload at all — {@code prt_race}
 * and {@code prt_class} ({@code ui-display.c}) each read {@code player->race->name}/
 * {@code class->name} straight off the shared global instead. The front end here has no such global,
 * so the values have to travel with the signal, and both belong to the one event rather than two.
 *
 * <p><b>Two instances built from equal-content arrays are not {@code equals()}.</b> A record's
 * generated {@code equals()} compares each component with {@link Object#equals}, and a
 * {@code String[]} does not override that method — it falls back to reference identity, the same
 * as any other array. So {@code new EventDataStrings("a", "b").equals(new EventDataStrings("a",
 * "b"))} is {@code false}. A test or comparison that needs to check content has to read
 * {@link #strings()} back and compare it with {@link java.util.Arrays#equals(Object[], Object[])},
 * not with this record's own {@code equals}.
 *
 * @param strings the pieces of text this event is reporting, in a fixed, caller-defined order
 * @author Rowan Crowther
 */
public record EventDataStrings(String... strings) implements GameEventData {
}
