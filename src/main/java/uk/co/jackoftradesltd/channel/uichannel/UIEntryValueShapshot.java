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

package uk.co.jackoftradesltd.channel.uichannel;

import uk.co.jackoftradesltd.channel.messages.data.GameEventData;

import java.util.Map;

/**
 * A point-in-time snapshot of every named UI entry's computed {@link UIEntryValue}, carried across
 * the channel as one {@link GameEventData} payload rather than one message per entry. No C
 * counterpart: C computes an entry's value on demand, in-process, by calling
 * {@code compute_ui_entry_values_for_object}/{@code _for_player} ({@code [C] ui-entry.c:672, 875})
 * from the drawing code that needs it; this port's UI thread cannot call across to the core's game
 * state that way, so the core computes every bound entry's value up front and sends the whole map
 * over in one message instead.
 *
 * <p>Record UIEntryValueShapshot coded before 260924, commented in full on 260924.
 *
 * @param byEntryName each named UI entry's computed value, keyed by
 *                    {@link uk.co.jackoftradesltd.frontend.entries.UIEntry#getName()}
 * @author Rowan Crowther
 */
public record UIEntryValueShapshot(Map<String, UIEntryValue> byEntryName) implements GameEventData {
}
